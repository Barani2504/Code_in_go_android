package com.simats.codeingo.di

import com.simats.codeingo.BuildConfig
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
        authHeaderInterceptor: AuthHeaderInterceptor
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(6, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(authHeaderInterceptor)

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor { message ->
                // Ensure passwords and tokens are never printed in cleartext in logcat
                val sanitized = message
                    .replace(Regex("(?i)(\"password\"\\s*:\\s*\")[^\"]+(\")"), "$1[REDACTED]$2")
                    .replace(Regex("(?i)(\"token\"\\s*:\\s*\")[^\"]+(\")"), "$1[REDACTED]$2")
                android.util.Log.d("CodeingoHttp", sanitized)
            }.apply {
                level = HttpLoggingInterceptor.Level.BODY
                redactHeader("Authorization")
                redactHeader("Cookie")
            }
            builder.addInterceptor(loggingInterceptor)
        }

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        val contentType = "application/json".toMediaType()
        val baseUrl = BuildConfig.API_BASE_URL.let { if (it.endsWith("/")) it else "$it/" }
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
