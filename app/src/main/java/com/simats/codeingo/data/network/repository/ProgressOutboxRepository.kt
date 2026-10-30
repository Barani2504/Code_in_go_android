package com.simats.codeingo.data.network.repository

import android.content.Context
import android.util.Log
import com.simats.codeingo.data.db.OfflineSyncDao
import com.simats.codeingo.data.db.OfflineSyncEntity
import com.simats.codeingo.data.db.SyncState
import com.simats.codeingo.data.local.UserPreferences
import com.simats.codeingo.data.network.api.CodeingoApiService
import com.simats.codeingo.data.network.model.BackendLessonCompleteRequest
import com.simats.codeingo.data.network.security.TokenManager
import com.simats.codeingo.data.network.worker.ProgressSyncWorker
import com.simats.codeingo.domain.AuthService
import com.simats.codeingo.domain.GameManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository responsible for the offline outbox pattern.
 *
 * Guarantees zero UI latency by enqueueing lesson completions immediately to Room.
 * The outbox items are drained in the background via WorkManager with exponential backoff.
 */
@Singleton
class ProgressOutboxRepository @Inject constructor(
    private val syncScheduler: com.simats.codeingo.data.network.worker.SyncScheduler,
    private val offlineSyncDao: OfflineSyncDao,
    private val apiService: CodeingoApiService,
    private val tokenStorage: com.simats.codeingo.data.network.security.TokenStorage,
    private val userProgressSource: com.simats.codeingo.data.local.UserProgressSource
) {

    companion object {
        private const val TAG = "ProgressOutboxRepo"
    }

    /**
     * Enqueues a local lesson completion into the Room outbox.
     * Retains the highest accuracy if an entry for the same user and lesson already exists.
     */
    suspend fun enqueueLessonCompletion(levelIndex: Int, accuracy: Int = 100): Boolean {
        val backendLessonId = LessonIdAdapter.toBackendLessonId(levelIndex)
        if (LessonIdAdapter.toLocalLevelIndex(backendLessonId) == null) {
            Log.w(TAG, "Cannot map level index $levelIndex to valid backend ID ($backendLessonId). Skipping outbox queue.")
            return false
        }

        val safeAccuracy = accuracy.coerceIn(0, 100)
        val userId = AuthService.shared.currentUser.value?.id ?: "local_user"

        val existing = offlineSyncDao.findByUserAndLesson(userId, backendLessonId)
        if (existing != null) {
            val updated = existing.copy(
                accuracyPercentage = maxOf(existing.accuracyPercentage, safeAccuracy),
                completedAtEpochMs = System.currentTimeMillis(),
                attemptCount = 0,
                syncState = SyncState.PENDING,
                lastError = null
            )
            offlineSyncDao.insertOrUpdate(updated)
            Log.d(TAG, "Updated existing outbox item for $backendLessonId (accuracy=${updated.accuracyPercentage})")
        } else {
            val newEntity = OfflineSyncEntity(
                userId = userId,
                lessonId = backendLessonId,
                accuracyPercentage = safeAccuracy,
                completedAtEpochMs = System.currentTimeMillis(),
                attemptCount = 0,
                syncState = SyncState.PENDING,
                lastError = null
            )
            offlineSyncDao.insertOrUpdate(newEntity)
            Log.d(TAG, "Inserted new outbox item for $backendLessonId (accuracy=$safeAccuracy)")
        }

        syncScheduler.scheduleSync()
        return true
    }

    /**
     * Drains the pending outbox items by uploading them to the backend.
     * Called primarily by [ProgressSyncWorker].
     */
    suspend fun uploadPendingCompletions(): Boolean {
        val token = tokenStorage.getAccessToken()
        if (token.isNullOrBlank()) {
            Log.d(TAG, "No valid access token available; skipping outbox drain for now.")
            return true
        }

        val pendingItems = offlineSyncDao.getPendingItems()
        if (pendingItems.isEmpty()) {
            return true
        }

        for (item in pendingItems) {
            offlineSyncDao.updateSyncState(item.id, SyncState.IN_FLIGHT)

            val request = BackendLessonCompleteRequest(
                lessonId = item.lessonId,
                accuracyPercentage = item.accuracyPercentage,
                completedAtEpochMs = item.completedAtEpochMs,
                idempotencyKey = item.idempotencyKey
            )

            val response = try {
                apiService.completeLesson(request)
            } catch (e: IOException) {
                offlineSyncDao.updateSyncState(item.id, SyncState.PENDING, e.message)
                throw e // Triggers WorkManager exponential backoff retry
            } catch (e: Exception) {
                offlineSyncDao.updateSyncState(item.id, SyncState.PENDING, e.message)
                return false
            }

            if (response.isSuccessful) {
                offlineSyncDao.delete(item)
                Log.d(TAG, "Successfully synced outbox item for ${item.lessonId}")

                // Reconcile non-destructively with GameManager without echo loops
                response.body()?.let { res ->
                    GameManager.instance.applyRemoteSnapshot(
                        remoteTotalXP = res.totalXp,
                        remoteGems = res.totalGems,
                        remoteStreak = res.currentStreak,
                        isLessonUploadResponse = true
                    )
                }
            } else {
                val code = response.code()
                when (code) {
                    401 -> {
                        offlineSyncDao.updateSyncState(item.id, SyncState.PENDING, "Unauthorized (401)")
                        Log.w(TAG, "Authentication expired (401) while uploading ${item.lessonId}")
                        return false
                    }
                    429 -> {
                        offlineSyncDao.updateSyncState(item.id, SyncState.PENDING, "Rate limited (429)")
                        throw IOException("Rate limited (429) on ${item.lessonId}")
                    }
                    in 400..499 -> {
                        // Permanent client error (e.g., malformed ID rejected by backend)
                        val errorBody = response.errorBody()?.string()
                        offlineSyncDao.updateSyncState(
                            item.id,
                            SyncState.FAILED_PERMANENT,
                            "Client error $code: $errorBody"
                        )
                        Log.e(TAG, "Permanent client error $code for ${item.lessonId}: $errorBody")
                    }
                    else -> {
                        // 5xx Server error
                        offlineSyncDao.updateSyncState(item.id, SyncState.PENDING, "Server error $code")
                        throw IOException("Server error $code on ${item.lessonId}")
                    }
                }
            }
        }

        return true
    }

    /**
     * Seeds initial local progress into the outbox on first user login/link.
     */
    suspend fun uploadInitialLocalProgress(userId: String) {
        val completedSet = userProgressSource.completedLevelIndices.first()
        val basecampSet = userProgressSource.basecampCompletedLevels.first()

        for (levelStr in completedSet) {
            val levelIndex = levelStr.toIntOrNull() ?: continue
            val backendId = LessonIdAdapter.toBackendLessonId(levelIndex)
            if (LessonIdAdapter.toLocalLevelIndex(backendId) == null) continue
            if (offlineSyncDao.findByUserAndLesson(userId, backendId) == null) {
                offlineSyncDao.insertOrUpdate(
                    OfflineSyncEntity(
                        userId = userId,
                        lessonId = backendId,
                        accuracyPercentage = 100,
                        completedAtEpochMs = System.currentTimeMillis(),
                        syncState = SyncState.PENDING
                    )
                )
            }
        }

        for (basecampStr in basecampSet) {
            val stageNum = basecampStr.removePrefix("c0_l").toIntOrNull() ?: continue
            val levelIndex = 100 + stageNum
            val backendId = LessonIdAdapter.toBackendLessonId(levelIndex)
            if (LessonIdAdapter.toLocalLevelIndex(backendId) == null) continue
            if (offlineSyncDao.findByUserAndLesson(userId, backendId) == null) {
                offlineSyncDao.insertOrUpdate(
                    OfflineSyncEntity(
                        userId = userId,
                        lessonId = backendId,
                        accuracyPercentage = 100,
                        completedAtEpochMs = System.currentTimeMillis(),
                        syncState = SyncState.PENDING
                    )
                )
            }
        }

        syncScheduler.scheduleSync()
    }

    /**
     * Exposes a reactive count of pending items waiting to be synced.
     */
    fun getPendingCountFlow(): Flow<Int> = offlineSyncDao.getPendingCountFlow()
}
