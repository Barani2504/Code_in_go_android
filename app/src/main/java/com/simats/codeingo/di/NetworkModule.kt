package com.simats.codeingo.di

import com.simats.codeingo.BuildConfig
import com.simats.codeingo.data.network.NetworkConfig
import com.simats.codeingo.data.network.api.CodeingoApiService
import com.simats.codeingo.data.network.security.AuthHeaderInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        explicitNulls = false
        isLenient = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authHeaderInterceptor: AuthHeaderInterceptor,
        networkConfig: NetworkConfig
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            // Dynamic host redirection: routes all requests to active NetworkConfig target (Dev Tunnel / Prod / Local)
            .addInterceptor { chain ->
                val original = chain.request()
                val targetUrl = networkConfig.getHttpUrl()
                val newUrl = original.url.newBuilder()
                    .scheme(targetUrl.scheme)
                    .host(targetUrl.host)
                    .port(targetUrl.port)
                    .build()
                val newRequest = original.newBuilder().url(newUrl).build()
                chain.proceed(newRequest)
            }
            .addInterceptor(authHeaderInterceptor)

        if (BuildConfig.DEBUG) {
            val bodyLoggingInterceptor = HttpLoggingInterceptor { message ->
                val sanitized = message
                    .replace(Regex("(?i)(\"password\"\\s*:\\s*\")[^\"]+(\")"), "$1[REDACTED]$2")
                    .replace(Regex("(?i)(\"token\"\\s*:\\s*\")[^\"]+(\")"), "$1[REDACTED]$2")
                    .replace(Regex("(?i)(\"refreshToken\"\\s*:\\s*\")[^\"]+(\")"), "$1[REDACTED]$2")
                android.util.Log.d("CodeingoHttp", sanitized)
            }.apply {
                level = HttpLoggingInterceptor.Level.BODY
                redactHeader("Authorization")
                redactHeader("Cookie")
            }

            val headersLoggingInterceptor = HttpLoggingInterceptor { message ->
                android.util.Log.d("CodeingoHttp", message)
            }.apply {
                level = HttpLoggingInterceptor.Level.HEADERS
                redactHeader("Authorization")
                redactHeader("Cookie")
            }

            builder.addInterceptor { chain ->
                val path = chain.request().url.encodedPath
                if (path.startsWith("/api/auth/")) {
                    headersLoggingInterceptor.intercept(chain)
                } else {
                    bodyLoggingInterceptor.intercept(chain)
                }
            }
        }

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json,
        networkConfig: NetworkConfig
    ): Retrofit {
        val contentType = "application/json".toMediaType()
        val baseUrl = networkConfig.getBaseUrl()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideCodeingoApiService(
        retrofit: Retrofit
    ): CodeingoApiService = retrofit.create(CodeingoApiService::class.java)

    @Provides
    @Singleton
    fun provideTokenStorage(tokenManager: com.simats.codeingo.data.network.security.TokenManager): com.simats.codeingo.data.network.security.TokenStorage = tokenManager

    @Provides
    @Singleton
    fun provideSyncScheduler(scheduler: com.simats.codeingo.data.network.worker.WorkManagerSyncScheduler): com.simats.codeingo.data.network.worker.SyncScheduler = scheduler
}
