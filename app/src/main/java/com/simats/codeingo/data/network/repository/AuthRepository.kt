package com.simats.codeingo.data.network.repository

import com.simats.codeingo.data.network.api.CodeingoApiService
import com.simats.codeingo.data.network.model.ApiResult
import com.simats.codeingo.data.network.model.AuthRequest
import com.simats.codeingo.data.network.model.AuthResponse
import com.simats.codeingo.data.network.model.ForgotPasswordRequest
import com.simats.codeingo.data.network.model.GenericApiResponse
import com.simats.codeingo.data.network.model.GuestStartRequest
import com.simats.codeingo.data.network.model.RegisterRequest
import com.simats.codeingo.data.network.model.ResetPasswordRequest
import com.simats.codeingo.data.network.model.UserDTO
import com.simats.codeingo.data.network.model.VerifyOtpRequest
import com.simats.codeingo.data.network.security.TokenManager
import com.simats.codeingo.domain.AuthService
import com.simats.codeingo.domain.GameManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: CodeingoApiService,
    private val tokenManager: TokenManager
) {
    suspend fun authenticate(email: String, password: String): ApiResult<UserDTO> {
        return safeApiCall {
            val response = apiService.authenticate(
                AuthRequest(
                    email = email.trim().lowercase(),
                    password = password
                )
            )
            handleAuthResponse(response)
        }
    }

    suspend fun register(
        email: String,
        password: String,
        username: String? = null,
        name: String? = null,
        age: Int? = null
    ): ApiResult<UserDTO> {
        return safeApiCall {
            val response = apiService.register(
                RegisterRequest(
                    email = email.trim().lowercase(),
                    password = password,
                    username = username?.trim()?.ifEmpty { null },
                    name = name?.trim()?.ifEmpty { null },
                    age = age,
                    codingGoal = "cns",
                    activeCourseId = "cns_defense"
                )
            )
            handleAuthResponse(response)
        }
    }

    suspend fun guestStart(): ApiResult<UserDTO> {
        return safeApiCall {
            val response = apiService.guestStart(
                GuestStartRequest(activeCourseId = "cns_defense")
            )
            handleAuthResponse(response)
        }
    }

    suspend fun forgotPassword(email: String): ApiResult<GenericApiResponse> {
        return safeApiCall {
            val response = apiService.forgotPassword(
                ForgotPasswordRequest(email = email.trim().lowercase())
            )
            mapResponse(response)
        }
    }

    suspend fun verifyOtp(email: String, otp: String): ApiResult<GenericApiResponse> {
        return safeApiCall {
            val response = apiService.verifyOtp(
                VerifyOtpRequest(
                    email = email.trim().lowercase(),
                    otp = otp.trim()
                )
            )
            mapResponse(response)
        }
    }

    suspend fun resetPassword(email: String, otp: String, newPassword: String): ApiResult<GenericApiResponse> {
        return safeApiCall {
            val response = apiService.resetPassword(
                ResetPasswordRequest(
                    email = email.trim().lowercase(),
                    otp = otp.trim(),
                    newPassword = newPassword
                )
            )
            mapResponse(response)
        }
    }

    fun logout() {
        tokenManager.clearTokens()
        AuthService.shared.signOut()
    }

    private fun handleAuthResponse(response: Response<AuthResponse>): ApiResult<UserDTO> {
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null && body.token.isNotEmpty()) {
                tokenManager.saveAccessToken(body.token)
                body.refreshToken?.let { tokenManager.saveRefreshToken(it) }

                val user = body.user ?: UserDTO(
                    username = "operative",
                    email = "",
                    activeCourseId = "cns_defense"
                )

                // Sync with local AuthService session
                AuthService.shared.signInWithEmail(user.email, user.name ?: user.username)

                // Reconcile user profile stats into GameManager
                val localIndices = LessonIdAdapter.extractLocalIndicesFromRemoteList(user.completedLessonIds)
                GameManager.instance.applyRemoteSnapshot(
                    remoteTotalXP = user.totalXp,
                    remoteGems = user.gems,
                    remoteStreak = user.currentStreak,
                    remotePhoenixStage = user.activePhoenixStage,
                    remoteCompletedIndices = localIndices,
                    isLessonUploadResponse = false
                )
                return ApiResult.Success(user)
            }
        }
        return mapErrorResponse(response)
    }

    private fun <T> mapResponse(response: Response<T>): ApiResult<T> {
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                return ApiResult.Success(body)
            }
        }
        return mapErrorResponse(response)
    }

    private fun <T> mapErrorResponse(response: Response<*>): ApiResult<T> {
        val code = response.code()
        val errorBody = response.errorBody()?.string()
        return when (code) {
            401 -> ApiResult.Unauthorized()
            else -> ApiResult.HttpError(
                code = code,
                message = errorBody ?: "HTTP error $code",
                errorBody = errorBody
            )
        }
    }

    private suspend fun <T> safeApiCall(block: suspend () -> ApiResult<T>): ApiResult<T> {
        return withContext(Dispatchers.IO) {
            try {
                block()
            } catch (e: IOException) {
                ApiResult.NetworkError(e)
            } catch (e: Exception) {
                ApiResult.HttpError(
                    code = -1,
                    message = e.localizedMessage ?: "Unexpected error occurred"
                )
            }
        }
    }
}
