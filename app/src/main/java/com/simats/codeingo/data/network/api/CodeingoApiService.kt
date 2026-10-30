package com.simats.codeingo.data.network.api

import com.simats.codeingo.data.network.model.AuthRequest
import com.simats.codeingo.data.network.model.AuthResponse
import com.simats.codeingo.data.network.model.BackendLessonCompleteRequest
import com.simats.codeingo.data.network.model.BackendLessonCompleteResponse
import com.simats.codeingo.data.network.model.ForgotPasswordRequest
import com.simats.codeingo.data.network.model.GenericApiResponse
import com.simats.codeingo.data.network.model.GuestStartRequest
import com.simats.codeingo.data.network.model.ProgressStatusDTO
import com.simats.codeingo.data.network.model.RegisterRequest
import com.simats.codeingo.data.network.model.ResetPasswordRequest
import com.simats.codeingo.data.network.model.UpdateProfileRequest
import com.simats.codeingo.data.network.model.UserDTO
import com.simats.codeingo.data.network.model.VerifyOtpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
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

    // ── 2. User Profile Endpoints ────────────────────────────────────────────

    @GET("api/users/me")
    suspend fun getCurrentUser(): Response<UserDTO>

    @PATCH("api/users/me/update")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): Response<UserDTO>

    // ── 3. Progress Tracking Endpoints ───────────────────────────────────────

    @GET("api/progress/status")
    suspend fun getProgressStatus(
        @Query("courseId") courseId: String? = "cns_defense"
    ): Response<ProgressStatusDTO>

    @POST("api/progress/lesson/complete")
    suspend fun completeLesson(
        @Body request: BackendLessonCompleteRequest
    ): Response<BackendLessonCompleteResponse>

    @POST("api/progress/sync-batch")
    suspend fun syncBatchProgress(
        @Body requests: List<BackendLessonCompleteRequest>
    ): Response<BackendLessonCompleteResponse>
}
