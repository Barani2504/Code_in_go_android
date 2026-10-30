package com.simats.codeingo.data.network.repository

import com.simats.codeingo.data.network.api.CodeingoApiService
import com.simats.codeingo.data.network.model.ApiResult
import com.simats.codeingo.data.network.model.ProgressStatusDTO
import com.simats.codeingo.data.network.model.UserDTO
import com.simats.codeingo.domain.GameManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepository @Inject constructor(
    private val apiService: CodeingoApiService,
    private val gameManager: GameManager
) {
    suspend fun fetchUserProfile(): ApiResult<UserDTO> {
        return safeApiCall {
            val response = apiService.getCurrentUser()
            mapResponse(response)
        }
    }

    suspend fun fetchProgressStatus(courseId: String? = "cns_defense"): ApiResult<ProgressStatusDTO> {
        return safeApiCall {
            val response = apiService.getProgressStatus(courseId = courseId)
            mapResponse(response)
        }
    }

    /**
     * Pulls remote progress from backend, filters strictly for CNS lessons,
     * and non-destructively reconciles state with GameManager.
     */
    suspend fun syncRemoteProgressToLocal(courseId: String? = "cns_defense"): ApiResult<ProgressStatusDTO> {
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

    /**
     * Pulls current user profile from /api/users/me, maps completed lessons,
     * and reconciles XP, gems, streak, phoenix stage, and completed levels into GameManager.
     */
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
                ApiResult.NetworkError(e)
            } catch (e: Exception) {
                ApiResult.HttpError(code = -1, message = e.localizedMessage ?: "Network request failed")
            }
        }
    }
}
