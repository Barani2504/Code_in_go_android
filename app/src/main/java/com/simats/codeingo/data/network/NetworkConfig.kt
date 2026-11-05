

package com.simats.codeingo.data.network

import android.content.Context
import android.content.SharedPreferences
import com.simats.codeingo.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Centralized Network Configuration & Live Backend Coordinator.
 *
 * Locked strictly to the live cloud dev tunnel backend:
 * https://qkc9q70m-8080.inc1.devtunnels.ms/
 * Localhost and emulator fallbacks are removed as requested.
 */
enum class NetworkEnvironment(val displayName: String) {
    DEV_TUNNEL("Live Cloud (Dev Tunnel)")
}

@Singleton
class NetworkConfig @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: Context
) {

    companion object {
        const val DEFAULT_DEV_TUNNEL_URL = "https://qkc9q70m-8080.inc1.devtunnels.ms/"
        const val LIVE_URL = DEFAULT_DEV_TUNNEL_URL

        private const val PREFS_NAME = "codeingo_network_prefs"
        private const val KEY_ENVIRONMENT = "codeingo_network_environment"

        @Volatile
        private var INSTANCE: NetworkConfig? = null

        fun getInstance(context: Context): NetworkConfig {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: NetworkConfig(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private val _currentEnvironment = MutableStateFlow(NetworkEnvironment.DEV_TUNNEL)
    val currentEnvironment: StateFlow<NetworkEnvironment> = _currentEnvironment.asStateFlow()

    private val _currentBaseUrl = MutableStateFlow(LIVE_URL)
    val currentBaseUrl: StateFlow<String> = _currentBaseUrl.asStateFlow()

    fun setEnvironment(environment: NetworkEnvironment) {
        _currentEnvironment.value = NetworkEnvironment.DEV_TUNNEL
        _currentBaseUrl.value = LIVE_URL
    }

    fun setDevTunnelUrl(url: String) {
        // Maintained for backward compatibility; always preserves live dev tunnel
        _currentBaseUrl.value = LIVE_URL
    }

    fun getBaseUrl(): String = LIVE_URL

    fun getHttpUrl(): HttpUrl {
        return LIVE_URL.toHttpUrlOrNull()!!
    }
}
