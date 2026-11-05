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
 * Centralized Network Configuration & Environment Coordinator.
 *
 * Direct parity with iOS `NetworkConfig.swift`.
 * Controls dynamic base URL resolution, timeout intervals, and environment switching
 * across Cloud Dev Tunnel, Localhost Emulator, Production, and Custom hosts.
 */
enum class NetworkEnvironment(val displayName: String) {
    DEV_TUNNEL("Dev Tunnel (Cloud)"),
    LOCAL_EMULATOR("Localhost (Android Emulator 10.0.2.2)"),
    PRODUCTION("Production Cloud"),
    CUSTOM("Custom Server")
}

@Singleton
class NetworkConfig @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: Context
) {

    companion object {
        const val DEFAULT_DEV_TUNNEL_URL = "https://qkc9q70m-8080.inc1.devtunnels.ms/"
        const val DEFAULT_PROD_URL = "https://api.codeingo.com/"
        const val DEFAULT_EMULATOR_URL = "http://10.0.2.2:8080/"

        private const val PREFS_NAME = "codeingo_network_prefs"
        private const val KEY_ENVIRONMENT = "codeingo_network_environment"
        private const val KEY_DEV_TUNNEL_URL = "codeingo_network_dev_tunnel_url"
        private const val KEY_CUSTOM_URL = "codeingo_network_custom_url"

        @Volatile
        private var INSTANCE: NetworkConfig? = null

        fun getInstance(context: Context): NetworkConfig {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: NetworkConfig(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _currentEnvironment = MutableStateFlow(loadInitialEnvironment())
    val currentEnvironment: StateFlow<NetworkEnvironment> = _currentEnvironment.asStateFlow()

    private val _currentBaseUrl = MutableStateFlow(resolveBaseUrl(_currentEnvironment.value))
    val currentBaseUrl: StateFlow<String> = _currentBaseUrl.asStateFlow()

    private fun loadInitialEnvironment(): NetworkEnvironment {
        val saved = prefs.getString(KEY_ENVIRONMENT, null)
        return if (saved != null) {
            try {
                NetworkEnvironment.valueOf(saved)
            } catch (_: Exception) {
                NetworkEnvironment.DEV_TUNNEL
            }
        } else {
            NetworkEnvironment.DEV_TUNNEL
        }
    }

    fun setEnvironment(environment: NetworkEnvironment) {
        _currentEnvironment.value = environment
        prefs.edit().putString(KEY_ENVIRONMENT, environment.name).apply()
        val newUrl = resolveBaseUrl(environment)
        _currentBaseUrl.value = newUrl
    }

    fun setCustomUrl(url: String) {
        val cleaned = cleanUrl(url)
        prefs.edit().putString(KEY_CUSTOM_URL, cleaned).apply()
        if (_currentEnvironment.value == NetworkEnvironment.CUSTOM) {
            _currentBaseUrl.value = cleaned
        }
    }

    fun setDevTunnelUrl(url: String) {
        val cleaned = cleanUrl(url)
        prefs.edit().putString(KEY_DEV_TUNNEL_URL, cleaned).apply()
        if (_currentEnvironment.value == NetworkEnvironment.DEV_TUNNEL) {
            _currentBaseUrl.value = cleaned
        }
    }

    fun getBaseUrl(): String = _currentBaseUrl.value

    fun getHttpUrl(): HttpUrl {
        val url = _currentBaseUrl.value
        return url.toHttpUrlOrNull() ?: DEFAULT_DEV_TUNNEL_URL.toHttpUrlOrNull()!!
    }

    private fun resolveBaseUrl(env: NetworkEnvironment): String {
        val url = when (env) {
            NetworkEnvironment.DEV_TUNNEL -> {
                prefs.getString(KEY_DEV_TUNNEL_URL, null)?.takeIf { it.isNotBlank() }
                    ?: DEFAULT_DEV_TUNNEL_URL
            }
            NetworkEnvironment.LOCAL_EMULATOR -> DEFAULT_EMULATOR_URL
            NetworkEnvironment.PRODUCTION -> DEFAULT_PROD_URL
            NetworkEnvironment.CUSTOM -> {
                prefs.getString(KEY_CUSTOM_URL, null)?.takeIf { it.isNotBlank() }
                    ?: DEFAULT_DEV_TUNNEL_URL
            }
        }
        return cleanUrl(url)
    }

    private fun cleanUrl(raw: String): String {
        val trimmed = raw.trim()
        return if (trimmed.endsWith("/")) trimmed else "$trimmed/"
    }
}
