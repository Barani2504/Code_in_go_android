package com.simats.codeingo.data.network.repository

import android.content.Context
import android.util.Log
import com.simats.codeingo.data.network.api.CodeingoApiService
import com.simats.codeingo.data.network.model.ApiResult
import com.simats.codeingo.data.network.model.EmotionEventRequest
import com.simats.codeingo.data.network.model.GenericApiResponse
import com.simats.codeingo.data.network.security.TokenStorage
import com.simats.codeingo.domain.GameManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository handling Phoenix Celestial Mascot stage equipping and
 * explicit-consent-gated emotion telemetry.
 *
 * Compliance & Privacy Invariants:
 * 1. Telemetry is strictly opt-in ("sec_telemetry" in preferences). If false (default),
 *    zero network requests are sent.
 * 2. Unauthenticated sessions never leak telemetry signals.
 * 3. Equipping is optimistic and local-first: local state advances immediately;
 *    network sync occurs asynchronously in the background.
 */
@Singleton
class MascotRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: CodeingoApiService,
    private val tokenStorage: TokenStorage,
    private val gameManager: GameManager
) {
    companion object {
        private const val TAG = "MascotRepository"
        const val PREFS_SETTINGS = "codeingo_settings"
        const val KEY_TELEMETRY_CONSENT = "sec_telemetry"
    }

    /**
     * Checks if the user has explicitly consented to anonymous telemetry in Settings.
     */
    fun isTelemetryConsentGranted(): Boolean {
        val prefs = context.getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_TELEMETRY_CONSENT, false)
    }

    /**
     * Equips a Phoenix Celestial evolution stage (1–18).
     * Updates local GameManager state immediately, then asynchronously informs the remote backend.
     */
    suspend fun equipStage(stageNumber: Int): ApiResult<GenericApiResponse> = withContext(Dispatchers.IO) {
        val clampedStage = stageNumber.coerceIn(1, 18)
        gameManager.setActivePhoenixStage(clampedStage)

        val token = tokenStorage.getAccessToken()
        if (token.isNullOrEmpty()) {
            Log.d(TAG, "User unauthenticated: stage $clampedStage equipped locally only")
            return@withContext ApiResult.Success(
                GenericApiResponse(success = true, message = "Equipped locally (offline)")
            )
        }

        try {
            val response = apiService.equipMascotStage(clampedStage)
            if (response.isSuccessful) {
                Log.d(TAG, "Successfully synced mascot stage $clampedStage to backend")
                ApiResult.Success(response.body() ?: GenericApiResponse(success = true, message = "Stage equipped"))
            } else {
                Log.w(TAG, "Backend returned HTTP ${response.code()} for mascot equip $clampedStage")
                ApiResult.HttpError(response.code(), response.message())
            }
        } catch (e: IOException) {
            Log.w(TAG, "Network unavailable during mascot equip: ${e.message}")
            ApiResult.NetworkError(e)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error equipping mascot stage", e)
            ApiResult.HttpError(-1, e.message ?: "Unknown error")
        }
    }

    /**
     * Transmits emotion reaction telemetry to the remote backend ONLY IF:
     * 1. The user has explicitly granted consent via Settings ("sec_telemetry" == true).
     * 2. The user has an active authenticated session.
     *
     * Returns true if telemetry was sent, false if skipped or failed.
     */
    suspend fun recordEmotionTelemetry(
        emotion: String,
        reason: String,
        contextInfo: String? = null
    ): Boolean = withContext(Dispatchers.IO) {
        // Privacy Gate: Explicit opt-in required
        if (!isTelemetryConsentGranted()) {
            Log.d(TAG, "Emotion telemetry skipped: User has not opted in (consent=false)")
            return@withContext false
        }

        // Auth Gate: Session required
        val token = tokenStorage.getAccessToken()
        if (token.isNullOrEmpty()) {
            Log.d(TAG, "Emotion telemetry skipped: Unauthenticated session")
            return@withContext false
        }

        try {
            val request = EmotionEventRequest(
                emotion = emotion,
                reason = reason,
                context = contextInfo
            )
            val response = apiService.recordEmotion(request)
            if (response.isSuccessful) {
                Log.d(TAG, "Emotion telemetry sent: $emotion ($reason)")
                true
            } else {
                Log.w(TAG, "Backend returned HTTP ${response.code()} for emotion telemetry")
                false
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to transmit emotion telemetry: ${e.message}")
            false
        }
    }
}
