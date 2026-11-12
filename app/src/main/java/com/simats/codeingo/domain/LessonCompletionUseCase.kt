package com.simats.codeingo.domain

import com.simats.codeingo.data.db.OfflineSyncDao
import com.simats.codeingo.data.db.OfflineSyncEntity
import com.simats.codeingo.data.db.SyncState
import com.simats.codeingo.data.network.model.ApiResult
import com.simats.codeingo.data.network.repository.SyncRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

data class LessonCompletionResult(
    val xpEarned: Int,
    val gemsEarned: Int,
    val starsEarned: Int,
    val isSyncedOnline: Boolean
)

@Singleton
class LessonCompletionUseCase @Inject constructor(
    private val gameManager: GameManager,
    private val syncRepository: SyncRepository,
    private val offlineSyncDao: OfflineSyncDao,
    private val authService: AuthService
) {

    suspend fun execute(
        unitId: Int,
        levelNumber: Int,
        totalLevelsInUnit: Int = 6,
        isBoss: Boolean = false,
        correctCount: Int,
        totalQuestions: Int,
        timeRemainingSec: Int = 45
    ): LessonCompletionResult = withContext(Dispatchers.IO) {
        val starsEarned = when (correctCount) {
            totalQuestions -> 5
            in (totalQuestions * 8 / 10)..totalQuestions -> 4
            in (totalQuestions * 6 / 10)..(totalQuestions * 8 / 10) -> 3
            in (totalQuestions * 4 / 10)..(totalQuestions * 6 / 10) -> 2
            in 1..(totalQuestions * 4 / 10) -> 1
            else -> 0
        }

        val accuracyPercentage = if (totalQuestions > 0) {
            ((correctCount.toFloat() / totalQuestions) * 100).toInt()
        } else {
            100
        }

        val optimisticXP = (unitId * 10) + (starsEarned * 5)
        val optimisticGems = (starsEarned * 2).coerceAtLeast(5)
        val lessonId = "cns_unit_${unitId}_lvl_${levelNumber}"
        val userId = authService.currentUserId ?: "guest"

        // 1. Local Optimistic Update
        withContext(Dispatchers.Main) {
            gameManager.addStars(starsEarned)
            gameManager.awardLessonXP(unitId, accuracyPercentage.toDouble() / 100.0, 45 - timeRemainingSec)
            gameManager.completeLessonAndExtendStreak()
            gameManager.unlockNextLevel(
                currentLevelIndex = levelNumber,
                isBoss = isBoss,
                accuracy = accuracyPercentage
            )
        }

        // 2. Queue for Offline Sync in Room with idempotency key
        val idempotencyKey = UUID.randomUUID().toString()
        val syncEntity = OfflineSyncEntity(
            idempotencyKey = idempotencyKey,
            userId = userId,
            lessonId = lessonId,
            accuracyPercentage = accuracyPercentage,
            syncState = SyncState.PENDING
        )
        val entityId = try {
            offlineSyncDao.insertOrUpdate(syncEntity)
        } catch (_: Exception) {
            0L
        }

        // 3. Attempt Authoritative Remote Sync
        var isSynced = false
        var finalXpEarned = optimisticXP
        var finalGemsEarned = optimisticGems

        try {
            when (val apiResult = syncRepository.completeLesson(lessonId, accuracyPercentage)) {
                is ApiResult.Success -> {
                    isSynced = true
                    val remoteResponse = apiResult.data
                    if (entityId > 0) {
                        offlineSyncDao.deleteByIds(listOf(entityId))
                    }
                    finalXpEarned = remoteResponse.xpEarned ?: optimisticXP
                    finalGemsEarned = remoteResponse.gemsEarned ?: optimisticGems
                }
                is ApiResult.HttpError, is ApiResult.NetworkError, is ApiResult.Unauthorized -> {
                    // Stays in Room queue for background flush
                    if (entityId > 0) {
                        offlineSyncDao.updateSyncState(entityId, SyncState.PENDING, "Pending network sync")
                    }
                }
            }
        } catch (_: Exception) {
            if (entityId > 0) {
                offlineSyncDao.updateSyncState(entityId, SyncState.PENDING, "Offline exception")
            }
        }

        LessonCompletionResult(
            xpEarned = finalXpEarned,
            gemsEarned = finalGemsEarned,
            starsEarned = starsEarned,
            isSyncedOnline = isSynced
        )
    }
}

