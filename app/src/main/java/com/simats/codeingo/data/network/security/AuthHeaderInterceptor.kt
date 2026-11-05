package com.simats.codeingo.data.network.security

import com.simats.codeingo.data.network.NetworkConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthHeaderInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
    private val networkConfig: NetworkConfig
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
        val targetHost = networkConfig.getHttpUrl().host
        val isTargetHost = requestUrl.host.equals(targetHost, ignoreCase = true)

        if (isTargetHost && !isAuthEndpoint && original.header("Authorization") == null) {
            val token = tokenManager.getAccessToken()
            if (!token.isNullOrEmpty()) {
                builder.header("Authorization", "Bearer $token")
            }
        }

        return chain.proceed(builder.build())
    }
}
