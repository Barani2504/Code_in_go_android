package com.simats.codeingo.data.network.security

import kotlinx.coroutines.flow.StateFlow

/**
 * Abstraction for token storage, allowing decoupled testing and clean security boundaries.
 */
interface TokenStorage {
    val hasValidToken: StateFlow<Boolean>
    fun saveAccessToken(token: String)
    fun getAccessToken(): String?
    fun saveRefreshToken(token: String)
    fun getRefreshToken(): String?
    fun clearTokens()
}
