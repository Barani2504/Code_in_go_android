package com.simats.codeingo.data.network.api

import com.simats.codeingo.data.network.model.AchievementDTO
import com.simats.codeingo.data.network.model.AnswerRequest
import com.simats.codeingo.data.network.model.AuthRequest
import com.simats.codeingo.data.network.model.AuthResponse
import com.simats.codeingo.data.network.model.BackendLessonCompleteRequest
import com.simats.codeingo.data.network.model.BackendLessonCompleteResponse
import com.simats.codeingo.data.network.model.CodingGoalsResponseDTO
import com.simats.codeingo.data.network.model.CompleteSessionRequest
import com.simats.codeingo.data.network.model.CompleteSessionResponse
import com.simats.codeingo.data.network.model.CosmeticPurchaseRequest
import com.simats.codeingo.data.network.model.DailyQuestItem
import com.simats.codeingo.data.network.model.EmotionEventRequest
import com.simats.codeingo.data.network.model.ForgotPasswordRequest
import com.simats.codeingo.data.network.model.GenericApiResponse
import com.simats.codeingo.data.network.model.GuestStartRequest
import com.simats.codeingo.data.network.model.LeaderboardEntryDTO
import com.simats.codeingo.data.network.model.MascotConfigResponse
import com.simats.codeingo.data.network.model.MascotStageDTO
import com.simats.codeingo.data.network.model.MistakeReviewDTO
import com.simats.codeingo.data.network.model.ProgressStatusDTO
import com.simats.codeingo.data.network.model.UserProgressItemDTO
import com.simats.codeingo.data.network.model.RefillHeartsResponse
import com.simats.codeingo.data.network.model.RegisterRequest
import com.simats.codeingo.data.network.model.ReportIssueRequest
import com.simats.codeingo.data.network.model.ResetPasswordRequest
import com.simats.codeingo.data.network.model.SocialEventDTO
import com.simats.codeingo.data.network.model.StartSessionResponse
import com.simats.codeingo.data.network.model.SubmitAnswerResponse
import com.simats.codeingo.data.network.model.SystemAnnouncementDTO
import com.simats.codeingo.data.network.model.UpdateProfileRequest
import com.simats.codeingo.data.network.model.UserDTO
import com.simats.codeingo.data.network.model.VerifyOtpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.Query

interface CodeingoApiService {

    // ── 1. Authentication Endpoints ──────────────────────────────────────────

    @POST("api/auth/authenticate")
    suspend fun authenticate(
        @Body request: AuthRequest
    ): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    @POST("api/auth/guest-start")
    suspend fun guestStart(
        @Body request: GuestStartRequest
    ): Response<AuthResponse>

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): Response<GenericApiResponse>

    @POST("api/auth/verify-otp")
    suspend fun verifyOtp(
        @Body request: VerifyOtpRequest
    ): Response<GenericApiResponse>

    @POST("api/auth/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<GenericApiResponse>

    // ── 2. User Profile & Preferences ────────────────────────────────────────

    @GET("api/users/me")
    suspend fun getCurrentUser(): Response<UserDTO>

    @PATCH("api/users/me/update")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): Response<UserDTO>

    @POST("api/users/me/claim-reward")
    suspend fun claimDailyReward(
        @Query("xp") xp: Int = 20,
        @Query("gems") gems: Int = 15
    ): Response<UserDTO>

    @POST("api/users/me/refill-hearts")
    suspend fun refillHearts(): Response<RefillHeartsResponse>

    @POST("api/users/me/earn-heart")
    suspend fun earnPracticeHeart(): Response<UserDTO>

    @POST("api/users/me/lose-heart")
    suspend fun loseHeart(): Response<UserDTO>

    // ── 3. Shop & Power-Ups ──────────────────────────────────────────────────

    @POST("api/users/shop/buy/streak-freeze")
    suspend fun buyStreakFreeze(
        @Query("price") price: Int = 50
    ): Response<UserDTO>

    @POST("api/users/shop/buy/refill-hearts")
    suspend fun buyHeartRefill(
        @Query("price") price: Int = 35,
        @Query("hearts") hearts: Int = 10
    ): Response<UserDTO>

    @POST("api/users/shop/buy/xp-boost")
    suspend fun buyXpBoost(): Response<UserDTO>

    @POST("api/users/shop/buy/streak-repair")
    suspend fun buyStreakRepair(): Response<UserDTO>

    @POST("api/users/shop/buy/cosmetic")
    suspend fun buyCosmetic(
        @Body request: CosmeticPurchaseRequest
    ): Response<UserDTO>

    // ── 4. Quests & Learning Goals ───────────────────────────────────────────

    @GET("api/users/me/quests")
    suspend fun getDailyQuests(): Response<List<DailyQuestItem>>

    @POST("api/users/me/quests/{questId}/claim")
    suspend fun claimQuest(
        @Path("questId") questId: String
    ): Response<UserDTO>

    @GET("api/courses/goals")
    suspend fun getCodingGoals(): Response<CodingGoalsResponseDTO>

    // ── 5. Game Sessions & Mistake Practice ──────────────────────────────────

    @POST("api/game/sessions/start")
    suspend fun startSession(
        @Query("lessonId") lessonId: String,
        @Query("learningPhase") learningPhase: Boolean = false
    ): Response<StartSessionResponse>

    @POST("api/game/sessions/{sessionId}/submit")
    suspend fun submitAnswer(
        @Path("sessionId") sessionId: Int,
        @Body request: AnswerRequest
    ): Response<SubmitAnswerResponse>

    @POST("api/game/sessions/{sessionId}/complete")
    suspend fun completeSession(
        @Path("sessionId") sessionId: Int,
        @Body request: CompleteSessionRequest
    ): Response<CompleteSessionResponse>

    @GET("api/game/practice/mistakes")
    suspend fun getMistakeReviewQuestions(
        @Query("limit") limit: Int = 10
    ): Response<List<MistakeReviewDTO>>

    // ── 6. Progress Tracking Endpoints ───────────────────────────────────────

    @GET("api/progress/status")
    suspend fun getProgressStatus(
        @Query("courseId") courseId: String? = "cns_defense"
    ): Response<ProgressStatusDTO>

    @GET("api/progress/me")
    suspend fun getMyProgress(): Response<List<UserProgressItemDTO>>

    @POST("api/progress/lesson/complete")
    suspend fun completeLesson(
        @Body request: BackendLessonCompleteRequest
    ): Response<BackendLessonCompleteResponse>

    @POST("api/progress/sync-batch")
    suspend fun syncBatchProgress(
        @Body requests: List<BackendLessonCompleteRequest>
    ): Response<BackendLessonCompleteResponse>

    // ── 7. Leaderboard Endpoints ─────────────────────────────────────────────

    @GET("api/users/leaderboard")
    suspend fun getLeaderboard(
        @Query("league") league: String = "BRONZE",
        @Query("courseId") courseId: String? = "cns_defense"
    ): Response<List<LeaderboardEntryDTO>>

    // ── 8. Mascot & Emotion Endpoints ────────────────────────────────────────

    @GET("api/mascot/config")
    suspend fun getMascotConfig(): Response<MascotConfigResponse>

    @GET("api/mascot/stages")
    suspend fun getMascotStages(): Response<List<MascotStageDTO>>

    @POST("api/mascot/equip/{stageNumber}")
    suspend fun equipMascotStage(
        @Path("stageNumber") stageNumber: Int
    ): Response<GenericApiResponse>

    @POST("api/mascot/emotion")
    suspend fun recordEmotion(
        @Body request: EmotionEventRequest
    ): Response<GenericApiResponse>

    // ── 9. Reporting ─────────────────────────────────────────────────────────

    @POST("api/reports")
    suspend fun submitReport(
        @Body request: ReportIssueRequest
    ): Response<GenericApiResponse>

    // ── 10. System Announcement & Broadcast Endpoints ─────────────────────────

    @GET("api/courses/announcement")
    suspend fun getSystemAnnouncement(): Response<SystemAnnouncementDTO>

    // ── 11. Persistent Achievements Endpoints ─────────────────────────────────

    @GET("api/users/me/achievements")
    suspend fun getAchievements(): Response<List<AchievementDTO>>

    @POST("api/users/me/achievements/{id}/claim")
    suspend fun claimAchievement(
        @Path("id") id: String
    ): Response<UserDTO>

    // ── 12. Community Social Activity Feed ───────────────────────────────────

    @GET("api/users/social/feed")
    suspend fun getSocialFeed(): Response<List<SocialEventDTO>>

    @POST("api/users/social/events/{id}/cheer")
    suspend fun cheerSocialEvent(
        @Path("id") id: Int
    ): Response<SocialEventDTO>

    // ── 13. Social Following & Coder Profile Lookups ─────────────────────────

    @GET("api/users/{id}")
    suspend fun getUserProfile(
        @Path("id") userId: Int
    ): Response<UserDTO>

    @GET("api/users/by-username/{username}")
    suspend fun getUserByUsername(
        @Path("username") username: String
    ): Response<UserDTO>

    @GET("api/users/me/following")
    suspend fun getFollowing(): Response<List<UserDTO>>

    @GET("api/users/me/followers")
    suspend fun getFollowers(): Response<List<UserDTO>>

    @POST("api/users/{id}/follow")
    suspend fun followUser(
        @Path("id") userId: Int
    ): Response<GenericApiResponse>

    @POST("api/users/{id}/unfollow")
    suspend fun unfollowUser(
        @Path("id") userId: Int
    ): Response<GenericApiResponse>

    @GET("api/users/search")
    suspend fun searchUsers(
        @Query("q") query: String
    ): Response<List<UserDTO>>
}
