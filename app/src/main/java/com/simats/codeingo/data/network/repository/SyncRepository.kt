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
import com.simats.codeingo.data.network.model.GuestStartRequest
import com.simats.codeingo.data.network.model.MistakeReviewDTO
import com.simats.codeingo.data.network.model.ProgressStatusDTO
import com.simats.codeingo.data.network.model.RefillHeartsResponse
import com.simats.codeingo.data.network.model.SocialEventDTO
import com.simats.codeingo.data.network.model.StartSessionResponse
import com.simats.codeingo.data.network.model.SubmitAnswerResponse
import com.simats.codeingo.data.network.model.SystemAnnouncementDTO
import com.simats.codeingo.data.network.model.UpdateProfileRequest
import com.simats.codeingo.data.network.model.UserDTO
import com.simats.codeingo.data.network.model.UserProgressItemDTO
import com.simats.codeingo.data.network.model.CourseUnitDTO
import com.simats.codeingo.data.network.model.QuestionDTO
import com.simats.codeingo.data.network.model.MascotPetResponse
import com.simats.codeingo.data.network.model.OnboardingFlowDTO
import com.simats.codeingo.data.network.model.PointerChallengeDTO
import com.simats.codeingo.data.network.model.ConceptChallengeDTO
import com.simats.codeingo.data.network.security.TokenManager
import com.simats.codeingo.domain.AuthService
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
    private val gameManager: GameManager,
    private val tokenManager: TokenManager
) {
    companion object {
        private const val TAG = "SyncRepository"
        const val CNS_COURSE_ID = "cns_defense"
    }

    suspend fun ensureAuthenticated(): Boolean {
        val existing = tokenManager.getAccessToken()
        if (!existing.isNullOrBlank()) return true
        return try {
            val res = apiService.guestStart(GuestStartRequest(activeCourseId = CNS_COURSE_ID))
            if (res.isSuccessful && res.body() != null) {
                val body = res.body()!!
                tokenManager.saveAccessToken(body.token)
                body.refreshToken?.let { tokenManager.saveRefreshToken(it) }
                val user = body.resolvedUser
                withContext(Dispatchers.Main) {
                    AuthService.shared.signInWithEmail(user.email, user.name ?: user.username)
                    gameManager.applyRemoteSnapshot(
                        remoteTotalXP = user.totalXp,
                        remoteGems = user.gems,
                        remoteStreak = user.currentStreak,
                        remotePhoenixStage = user.activePhoenixStage,
                        isLessonUploadResponse = false
                    )
                }
                Log.i(TAG, "Successfully acquired guest session for ${user.username}")
                true
            } else {
                Log.w(TAG, "guestStart failed with HTTP ${res.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "guestStart error during ensureAuthenticated", e)
            false
        }
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

    suspend fun fetchMyProgress(): ApiResult<List<UserProgressItemDTO>> = safeApiCall {
        mapResponse(apiService.getMyProgress())
    }

    suspend fun syncRemoteProgressToLocal(courseId: String? = CNS_COURSE_ID): ApiResult<ProgressStatusDTO> {
        val statusResult = fetchProgressStatus(courseId)
        val meResult = fetchMyProgress()

        val allCompletedLessonIds = mutableSetOf<String>()

        var remoteTotalXp: Int? = null
        var remoteGems: Int? = null
        var remoteStreak: Int? = null
        var remoteCurrentUnitIndex: Int? = null
        val remoteCompletedUnits = mutableSetOf<Int>()

        if (statusResult is ApiResult.Success) {
            val status = statusResult.data
            status.completedLessons?.let { allCompletedLessonIds.addAll(it) }
            remoteTotalXp = status.totalXp
            remoteGems = status.gems
            remoteStreak = status.streakDays
            remoteCurrentUnitIndex = status.currentUnitIndex
            status.completedUnits?.forEach { uStr ->
                LessonIdAdapter.parseUnitNumber(uStr)?.let { remoteCompletedUnits.add(it) }
            }
        }

        if (meResult is ApiResult.Success) {
            val items = meResult.data
            allCompletedLessonIds.addAll(items.filter { it.isCompleted == true }.map { it.lessonId })
        }

        val localIndices = LessonIdAdapter.extractLocalIndicesFromRemoteList(allCompletedLessonIds.toList())
        withContext(Dispatchers.Main) {
            gameManager.applyRemoteSnapshot(
                remoteTotalXP = remoteTotalXp,
                remoteGems = remoteGems,
                remoteStreak = remoteStreak,
                remoteCompletedIndices = localIndices,
                remoteCurrentUnitIndex = remoteCurrentUnitIndex,
                remoteCompletedUnits = remoteCompletedUnits,
                isLessonUploadResponse = false
            )
        }

        return if (statusResult is ApiResult.Success) {
            ApiResult.Success(
                statusResult.data.copy(completedLessons = allCompletedLessonIds.toList())
            )
        } else if (meResult is ApiResult.Success) {
            ApiResult.Success(
                ProgressStatusDTO(
                    completedLessons = allCompletedLessonIds.toList(),
                    totalXp = gameManager.totalXP.value,
                    hearts = gameManager.heartsCount.value,
                    gems = gameManager.gemsCount.value,
                    streakDays = gameManager.streakDays.value
                )
            )
        } else {
            statusResult
        }
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
                user.hearts?.let { gameManager.setHeartsCount(it) }
                user.gems?.let { gameManager.applyRemoteSnapshot(null, it, null) }
            }
        }
        mapResponse(res)
    }

    suspend fun buyCosmetic(cosmeticId: String, price: Int, category: String = "hats"): ApiResult<UserDTO> = safeApiCall {
        val res = apiService.buyCosmetic(
            com.simats.codeingo.data.network.model.CosmeticPurchaseRequest(
                cosmeticId = cosmeticId,
                price = price,
                category = category
            )
        )
        if (res.isSuccessful && res.body() != null) {
            val user = res.body()!!
            withContext(Dispatchers.Main) {
                user.gems?.let { gameManager.applyRemoteSnapshot(null, it, null) }
            }
        }
        mapResponse(res)
    }

    suspend fun fetchCurrentUser(): ApiResult<UserDTO> = safeApiCall {
        val res = apiService.getCurrentUser()
        if (res.isSuccessful && res.body() != null) {
            val user = res.body()!!
            withContext(Dispatchers.Main) {
                user.hearts?.let { gameManager.setHeartsCount(it) }
                gameManager.applyRemoteSnapshot(
                    remoteTotalXP = user.totalXp,
                    remoteGems = user.gems,
                    remoteStreak = user.currentStreak,
                    remotePhoenixStage = user.activePhoenixStage
                )
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
    // -------------------------------------------------------------------------
    // 10. Live Curriculum & Dynamic Questions
    // -------------------------------------------------------------------------

    suspend fun fetchCourseUnits(courseId: String = CNS_COURSE_ID): ApiResult<List<CourseUnitDTO>> = safeApiCall {
        val res = apiService.getCourseUnits(courseId)
        if (res.isSuccessful) mapResponse(res)
        else {
            val dsaRes = apiService.getDsaUnits()
            if (dsaRes.isSuccessful) mapResponse(dsaRes)
            else mapResponse(apiService.getGameCurriculum())
        }
    }

    suspend fun fetchQuestions(
        unitId: String? = null,
        levelNumber: Int? = null,
        goal: String? = "cns",
        track: String? = CNS_COURSE_ID
    ): ApiResult<List<QuestionDTO>> = safeApiCall {
        mapResponse(apiService.getCourseQuestions(unitId = unitId, levelNumber = levelNumber, goal = goal, track = track))
    }

    suspend fun fetchLessonQuestions(lessonId: String): ApiResult<List<QuestionDTO>> = safeApiCall {
        mapResponse(apiService.getLessonQuestions(lessonId = lessonId))
    }

    suspend fun turnGoalOn(id: String): ApiResult<GenericApiResponse> = safeApiCall {
        mapResponse(apiService.turnGoalOn(id))
    }

    suspend fun turnGoalOff(id: String): ApiResult<GenericApiResponse> = safeApiCall {
        mapResponse(apiService.turnGoalOff(id))
    }

    suspend fun toggleGoal(id: String): ApiResult<GenericApiResponse> = safeApiCall {
        mapResponse(apiService.toggleGoal(id))
    }

    // -------------------------------------------------------------------------
    // 11. Procedural Onboarding & Live Challenges
    // -------------------------------------------------------------------------

    suspend fun fetchOnboardingFlow(): ApiResult<OnboardingFlowDTO> = safeApiCall {
        mapResponse(apiService.getOnboardingFlow())
    }

    suspend fun fetchRandomPointerChallenge(): ApiResult<PointerChallengeDTO> = safeApiCall {
        mapResponse(apiService.getRandomPointerChallenge())
    }

    suspend fun fetchRandomConceptChallenge(): ApiResult<ConceptChallengeDTO> = safeApiCall {
        mapResponse(apiService.getRandomConceptChallenge())
    }

    // -------------------------------------------------------------------------
    // 12. User Course Lifecycle & Pet Mascot
    // -------------------------------------------------------------------------

    suspend fun petMascot(): ApiResult<MascotPetResponse> = safeApiCall {
        mapResponse(apiService.petMascot())
    }

    suspend fun resetCourse(courseId: String = CNS_COURSE_ID): ApiResult<GenericApiResponse> = safeApiCall {
        mapResponse(apiService.resetCourse(courseId))
    }

    suspend fun setPhoenixStage(stage: Int): ApiResult<UserDTO> = safeApiCall {
        val res = apiService.setPhoenixStage(stage)
        if (res.isSuccessful && res.body() != null) {
            withContext(Dispatchers.Main) {
                gameManager.setActivePhoenixStage(stage)
            }
        }
        mapResponse(res)
    }

    suspend fun activatePremium(): ApiResult<UserDTO> = safeApiCall {
        mapResponse(apiService.activatePremium())
    }

    suspend fun deactivateAccount(): ApiResult<GenericApiResponse> = safeApiCall {
        mapResponse(apiService.deactivateAccount())
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
                ensureAuthenticated()
                var result = block()
                if (result is ApiResult.Unauthorized || (result is ApiResult.HttpError && (result.code == 401 || result.code == 403))) {
                    Log.d(TAG, "Received 401/403, attempting token refresh...")
                    var refreshed = false
                    val refreshToken = tokenManager.getRefreshToken()
                    if (!refreshToken.isNullOrEmpty()) {
                        try {
                            val refreshRes = apiService.refreshToken("Bearer $refreshToken")
                            if (refreshRes.isSuccessful && refreshRes.body() != null) {
                                val body = refreshRes.body()!!
                                tokenManager.saveAccessToken(body.token)
                                body.refreshToken?.let { tokenManager.saveRefreshToken(it) }
                                refreshed = true
                                result = block()
                            }
                        } catch (e: Exception) {
                            Log.w(TAG, "Refresh token exchange failed: ${e.message}")
                        }
                    }

                    if (!refreshed) {
                        Log.d(TAG, "Token refresh unavailable or failed; clearing tokens and performing fresh guest auth...")
                        tokenManager.clearTokens()
                        val reAuthed = ensureAuthenticated()
                        if (reAuthed) {
                            result = block()
                        }
                    }
                }
                result
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
