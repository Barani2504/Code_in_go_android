package com.simats.codeingo.data.network.repository

import android.util.Log
import com.simats.codeingo.data.network.api.CodeingoApiService
import com.simats.codeingo.data.network.model.AchievementDTO
import com.simats.codeingo.data.network.model.AnswerRequest
import com.simats.codeingo.data.network.model.ApiResult
import com.simats.codeingo.data.network.model.BackendLessonCompleteRequest
import com.simats.codeingo.data.network.model.CompleteSessionRequest
import com.simats.codeingo.data.network.model.CompleteSessionResponse
import com.simats.codeingo.data.network.model.DailyQuestItem
import com.simats.codeingo.data.network.model.GenericApiResponse
import com.simats.codeingo.data.network.model.MistakeReviewDTO
import com.simats.codeingo.data.network.model.ProgressStatusDTO
import com.simats.codeingo.data.network.model.RefillHeartsResponse
import com.simats.codeingo.data.network.model.SocialEventDTO
import com.simats.codeingo.data.network.model.StartSessionResponse
import com.simats.codeingo.data.network.model.SubmitAnswerResponse
import com.simats.codeingo.data.network.model.SystemAnnouncementDTO
import com.simats.codeingo.data.network.model.UpdateProfileRequest
import com.simats.codeingo.data.network.model.UserDTO
import com.simats.codeingo.domain.GameManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Unified Backend Repository and Synchronization Coordinator.
 *
 * Implements complete parity with iOS CodeingoAPI.swift, UserAPIService.swift,
 * and GameAPIService.swift. Provides direct, type-safe GET and POST methods
 * connecting the Android application to the common live backend:
 * - User Profile and Stats reconciliation (GET /api/users/me, PATCH /api/users/me/update)
 * - Economy and Shop actions (POST /api/users/me/claim-reward, POST /api/users/shop/buy/...)
 * - Heart lifecycle (POST /api/users/me/refill-hearts, lose-heart, earn-heart)
 * - Daily Quests and Missions (GET /api/users/me/quests, POST quests/{id}/claim)
 * - Game Sessions and Mistake Practice (POST /api/game/sessions/..., GET practice/mistakes)
 * - Progress Tracking (POST /api/progress/lesson/complete, GET /api/progress/status)
 */
@Singleton
class SyncRepository @Inject constructor(
    private val apiService: CodeingoApiService,
    private val gameManager: GameManager
) {
    companion object {
        private const val TAG = "SyncRepository"
        const val CNS_COURSE_ID = "cns_defense"
    }

    // -------------------------------------------------------------------------
    // 1. User Profile and Stats (GET / PATCH)
    // -------------------------------------------------------------------------

    suspend fun fetchUserProfile(): ApiResult<UserDTO> = safeApiCall {
        mapResponse(apiService.getCurrentUser())
    }

    suspend fun updateProfile(request: UpdateProfileRequest): ApiResult<UserDTO> = safeApiCall {
        val res = apiService.updateProfile(request)
        if (res.isSuccessful && res.body() != null) {
            val user = res.body()!!
            withContext(Dispatchers.Main) {
                gameManager.applyRemoteSnapshot(
                    remoteTotalXP = user.totalXp,
                    remoteGems = user.gems,
                    remoteStreak = user.currentStreak,
                    remotePhoenixStage = user.activePhoenixStage,
                    isLessonUploadResponse = false
                )
            }
        }
        mapResponse(res)
    }

    suspend fun syncUserProfileToLocal(): ApiResult<UserDTO> {
        val result = fetchUserProfile()
        if (result is ApiResult.Success) {
            val user = result.data
            val localIndices = LessonIdAdapter.extractLocalIndicesFromRemoteList(user.completedLessonIds)
            withContext(Dispatchers.Main) {
                gameManager.applyRemoteSnapshot(
                    remoteTotalXP = user.totalXp,
                    remoteGems = user.gems,
                    remoteStreak = user.currentStreak,
                    remotePhoenixStage = user.activePhoenixStage,
                    remoteCompletedIndices = localIndices,
                    isLessonUploadResponse = false
                )
            }
        }
        return result
    }

    // -------------------------------------------------------------------------
    // 2. Progress Tracking (GET / POST)
    // -------------------------------------------------------------------------

    suspend fun fetchProgressStatus(courseId: String? = CNS_COURSE_ID): ApiResult<ProgressStatusDTO> = safeApiCall {
        mapResponse(apiService.getProgressStatus(courseId = courseId))
    }

    suspend fun syncRemoteProgressToLocal(courseId: String? = CNS_COURSE_ID): ApiResult<ProgressStatusDTO> {
        val result = fetchProgressStatus(courseId)
        if (result is ApiResult.Success) {
            val status = result.data
            val localIndices = LessonIdAdapter.extractLocalIndicesFromRemoteList(status.completedLessons)
            withContext(Dispatchers.Main) {
                gameManager.applyRemoteSnapshot(
                    remoteTotalXP = status.totalXp,
                    remoteGems = status.gems,
                    remoteStreak = status.streakDays,
                    remoteCompletedIndices = localIndices,
                    isLessonUploadResponse = false
                )
            }
        }
        return result
    }

    // -------------------------------------------------------------------------
    // 3. Economy and Heart Lifecycle (POST)
    // -------------------------------------------------------------------------

    suspend fun claimDailyReward(xp: Int = 20, gems: Int = 15): ApiResult<UserDTO> = safeApiCall {
        val res = apiService.claimDailyReward(xp = xp, gems = gems)
        if (res.isSuccessful && res.body() != null) {
            val user = res.body()!!
            withContext(Dispatchers.Main) {
                gameManager.applyRemoteSnapshot(
                    remoteTotalXP = user.totalXp,
                    remoteGems = user.gems,
                    remoteStreak = user.currentStreak,
                    isLessonUploadResponse = false
                )
            }
        }
        mapResponse(res)
    }

    suspend fun refillHearts(): ApiResult<RefillHeartsResponse> = safeApiCall {
        val res = apiService.refillHearts()
        if (res.isSuccessful && res.body() != null) {
            val body = res.body()!!
            withContext(Dispatchers.Main) {
                body.hearts?.let { gameManager.replenishHearts(it) }
                body.gemsRemaining?.let { gameManager.applyRemoteSnapshot(null, it, null) }
            }
        }
        mapResponse(res)
    }

    suspend fun buyStreakFreeze(price: Int = 50): ApiResult<UserDTO> = safeApiCall {
        val res = apiService.buyStreakFreeze(price = price)
        if (res.isSuccessful && res.body() != null) {
            val user = res.body()!!
            withContext(Dispatchers.Main) {
                gameManager.applyRemoteSnapshot(user.totalXp, user.gems, user.currentStreak)
            }
        }
        mapResponse(res)
    }

    suspend fun buyHeartRefill(price: Int = 35, hearts: Int = 10): ApiResult<UserDTO> = safeApiCall {
        val res = apiService.buyHeartRefill(price = price, hearts = hearts)
        if (res.isSuccessful && res.body() != null) {
            val user = res.body()!!
            withContext(Dispatchers.Main) {
                user.hearts?.let { gameManager.replenishHearts(it) }
                user.gems?.let { gameManager.applyRemoteSnapshot(null, it, null) }
            }
        }
        mapResponse(res)
    }

    suspend fun earnPracticeHeart(): ApiResult<UserDTO> = safeApiCall {
        val res = apiService.earnPracticeHeart()
        if (res.isSuccessful && res.body() != null) {
            val user = res.body()!!
            withContext(Dispatchers.Main) {
                user.hearts?.let { gameManager.replenishHearts(it) }
            }
        }
        mapResponse(res)
    }

    suspend fun loseHeart(): ApiResult<UserDTO> = safeApiCall {
        val res = apiService.loseHeart()
        if (res.isSuccessful && res.body() != null) {
            val user = res.body()!!
            withContext(Dispatchers.Main) {
                user.hearts?.let { gameManager.setHeartsCount(it) }
            }
        }
        mapResponse(res)
    }

    // -------------------------------------------------------------------------
    // 4. Daily Quests and Missions (GET / POST)
    // -------------------------------------------------------------------------

    suspend fun fetchDailyQuests(): ApiResult<List<DailyQuestItem>> = safeApiCall {
        mapResponse(apiService.getDailyQuests())
    }

    suspend fun claimQuest(questId: String): ApiResult<UserDTO> = safeApiCall {
        val res = apiService.claimQuest(questId)
        if (res.isSuccessful && res.body() != null) {
            val user = res.body()!!
            withContext(Dispatchers.Main) {
                gameManager.applyRemoteSnapshot(user.totalXp, user.gems, user.currentStreak)
            }
        }
        mapResponse(res)
    }

    // -------------------------------------------------------------------------
    // 5. Game Sessions and Mistake Practice (POST / GET)
    // -------------------------------------------------------------------------

    suspend fun startSession(lessonId: String, learningPhase: Boolean = false): ApiResult<StartSessionResponse> = safeApiCall {
        mapResponse(apiService.startSession(lessonId = lessonId, learningPhase = learningPhase))
    }

    suspend fun submitAnswer(
        sessionId: Int,
        questionId: Int,
        answer: String,
        baseXp: Int = 10,
        timeTakenMs: Int = 1000
    ): ApiResult<SubmitAnswerResponse> = safeApiCall {
        val req = AnswerRequest(
            questionId = questionId,
            userAnswer = answer,
            baseXp = baseXp,
            timeTakenMs = timeTakenMs
        )
        mapResponse(apiService.submitAnswer(sessionId, req))
    }

    suspend fun completeSession(
        sessionId: Int,
        accuracy: Double,
        totalTimeSeconds: Int,
        heartsRemaining: Int
    ): ApiResult<CompleteSessionResponse> = safeApiCall {
        val req = CompleteSessionRequest(
            accuracy = accuracy,
            totalTimeSeconds = totalTimeSeconds,
            heartsRemaining = heartsRemaining
        )
        mapResponse(apiService.completeSession(sessionId, req))
    }

    suspend fun fetchMistakeReviewQuestions(limit: Int = 10): ApiResult<List<MistakeReviewDTO>> = safeApiCall {
        mapResponse(apiService.getMistakeReviewQuestions(limit = limit))
    }

    // -------------------------------------------------------------------------
    // 6. System Announcement & Broadcast (GET)
    // -------------------------------------------------------------------------

    suspend fun fetchSystemAnnouncement(): ApiResult<SystemAnnouncementDTO> = safeApiCall {
        mapResponse(apiService.getSystemAnnouncement())
    }

    // -------------------------------------------------------------------------
    // 7. Persistent Achievements System (GET / POST)
    // -------------------------------------------------------------------------

    suspend fun fetchAchievements(): ApiResult<List<AchievementDTO>> = safeApiCall {
        mapResponse(apiService.getAchievements())
    }

    suspend fun claimAchievement(id: String): ApiResult<UserDTO> = safeApiCall {
        val res = apiService.claimAchievement(id)
        if (res.isSuccessful && res.body() != null) {
            val user = res.body()!!
            withContext(Dispatchers.Main) {
                gameManager.applyRemoteSnapshot(
                    remoteTotalXP = user.totalXp,
                    remoteGems = user.gems,
                    remoteStreak = user.currentStreak,
                    isLessonUploadResponse = false
                )
            }
        }
        mapResponse(res)
    }

    // -------------------------------------------------------------------------
    // 8. Community Social Activity Feed (GET / POST)
    // -------------------------------------------------------------------------

    suspend fun fetchSocialFeed(): ApiResult<List<SocialEventDTO>> = safeApiCall {
        mapResponse(apiService.getSocialFeed())
    }

    suspend fun cheerSocialEvent(id: Int): ApiResult<SocialEventDTO> = safeApiCall {
        mapResponse(apiService.cheerSocialEvent(id))
    }

    // -------------------------------------------------------------------------
    // 9. Coder Profiles & Social Following (GET / POST)
    // -------------------------------------------------------------------------

    suspend fun fetchUser(id: Int): ApiResult<UserDTO> = safeApiCall {
        mapResponse(apiService.getUserProfile(id))
    }

    suspend fun fetchUserByUsername(username: String): ApiResult<UserDTO> = safeApiCall {
        mapResponse(apiService.getUserByUsername(username))
    }

    suspend fun fetchFollowing(): ApiResult<List<UserDTO>> = safeApiCall {
        mapResponse(apiService.getFollowing())
    }

    suspend fun fetchFollowers(): ApiResult<List<UserDTO>> = safeApiCall {
        mapResponse(apiService.getFollowers())
    }

    suspend fun followUser(id: Int): ApiResult<GenericApiResponse> = safeApiCall {
        mapResponse(apiService.followUser(id))
    }

    suspend fun unfollowUser(id: Int): ApiResult<GenericApiResponse> = safeApiCall {
        mapResponse(apiService.unfollowUser(id))
    }

    suspend fun searchUsers(query: String): ApiResult<List<UserDTO>> = safeApiCall {
        mapResponse(apiService.searchUsers(query))
    }

    // -------------------------------------------------------------------------
    // Response and Error Helpers
    // -------------------------------------------------------------------------

    private fun <T> mapResponse(response: Response<T>): ApiResult<T> {
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                return ApiResult.Success(body)
            }
        }
        val code = response.code()
        val errorBody = response.errorBody()?.string()
        return if (code == 401) {
            ApiResult.Unauthorized()
        } else {
            ApiResult.HttpError(code = code, message = errorBody ?: "HTTP $code", errorBody = errorBody)
        }
    }

    private suspend fun <T> safeApiCall(block: suspend () -> ApiResult<T>): ApiResult<T> {
        return withContext(Dispatchers.IO) {
            try {
                block()
            } catch (e: IOException) {
                Log.w(TAG, "Network I/O error: ${e.message}")
                ApiResult.NetworkError(e)
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected API error", e)
                ApiResult.HttpError(code = -1, message = e.localizedMessage ?: "Network request failed")
            }
        }
    }
}
