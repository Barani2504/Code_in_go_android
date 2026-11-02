package com.simats.codeingo.data.network.security

import com.simats.codeingo.BuildConfig
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthHeaderInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestUrl = original.url
        val path = requestUrl.encodedPath

        val builder = original.newBuilder()
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")

        // Exclude authentication endpoints from Bearer injection
        val isAuthEndpoint = path.startsWith("/api/auth/")

        // Attach Bearer token ONLY to API host, never to external hosts
        val configuredHost = BuildConfig.API_BASE_URL.toHttpUrlOrNull()?.host
        val isTargetHost = configuredHost == null || requestUrl.host.equals(configuredHost, ignoreCase = true)

        if (isTargetHost && !isAuthEndpoint && original.header("Authorization") == null) {
            val token = tokenManager.getAccessToken()
            if (!token.isNullOrEmpty()) {
                builder.header("Authorization", "Bearer $token")
            }
        }

        return chain.proceed(builder.build())
    }
}
