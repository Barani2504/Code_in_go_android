package com.simats.codeingo.network

import android.content.Context
import android.content.SharedPreferences
import com.simats.codeingo.data.network.api.CodeingoApiService
import com.simats.codeingo.data.network.repository.MascotRepository
import com.simats.codeingo.data.network.security.TokenStorage
import com.simats.codeingo.domain.GameManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.lang.reflect.Proxy
import java.util.concurrent.TimeUnit

class MascotRepositoryTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: CodeingoApiService
    private lateinit var fakeTokenStorage: FakeTokenStorage
    private lateinit var fakeContext: Context
    private val preferencesMap = mutableMapOf<String, Any>()
    private val gameManager = GameManager.instance
    private lateinit var repository: MascotRepository

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        explicitNulls = false
        isLenient = true
    }

    class FakeTokenStorage : TokenStorage {
        var token: String? = "mock_jwt_token"
        private val _hasToken = MutableStateFlow(true)
        override val hasValidToken: StateFlow<Boolean> = _hasToken

        override fun getAccessToken(): String? = token
        override fun saveAccessToken(token: String) {
            this.token = token
            _hasToken.value = true
        }
        override fun saveRefreshToken(token: String) {}
        override fun getRefreshToken(): String? = null
        override fun clearTokens() {
            this.token = null
            _hasToken.value = false
        }
    }

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.SECONDS)
            .readTimeout(2, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        apiService = retrofit.create(CodeingoApiService::class.java)
        fakeTokenStorage = FakeTokenStorage()
        preferencesMap.clear()

        // Dynamic Proxy for SharedPreferences
        val sharedPrefsProxy = Proxy.newProxyInstance(
            SharedPreferences::class.java.classLoader,
            arrayOf(SharedPreferences::class.java)
        ) { _, method, args ->
            when (method.name) {
                "getBoolean" -> {
                    val key = args[0] as String
                    val defVal = args[1] as Boolean
                    preferencesMap[key] as? Boolean ?: defVal
                }
                "edit" -> {
                    Proxy.newProxyInstance(
                        SharedPreferences.Editor::class.java.classLoader,
                        arrayOf(SharedPreferences.Editor::class.java)
                    ) { proxy, editorMethod, editorArgs ->
                        when (editorMethod.name) {
                            "putBoolean" -> {
                                preferencesMap[editorArgs[0] as String] = editorArgs[1] as Boolean
                                proxy
                            }
                            "apply", "commit" -> null
                            else -> proxy
                        }
                    }
                }
                else -> null
            }
        } as SharedPreferences

        // Fake Context using ContextWrapper
        fakeContext = object : android.content.ContextWrapper(null) {
            override fun getSharedPreferences(name: String?, mode: Int): SharedPreferences {
                return sharedPrefsProxy
            }
            override fun getApplicationContext(): Context {
                return this
            }
        }

        repository = MascotRepository(
            context = fakeContext,
            apiService = apiService,
            tokenStorage = fakeTokenStorage,
            gameManager = gameManager
        )
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testEquipStage_sendsCorrectEndpointAndPayload() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("""{"success": true, "message": "Equipped stage 3"}""")
        )

        val result = repository.equipStage(3)
        val recordedRequest = mockWebServer.takeRequest(2, TimeUnit.SECONDS)

        assertNotNull(recordedRequest)
        assertEquals("/api/mascot/equip/3", recordedRequest?.path)
        assertEquals("POST", recordedRequest?.method)
        assertEquals(3, gameManager.activePhoenixStage.value)
        assertTrue(result is com.simats.codeingo.data.network.model.ApiResult.Success)
    }

    @Test
    fun testEquipStage_unauthenticatedEquipsLocallyOnly() = runBlocking {
        fakeTokenStorage.token = null

        val result = repository.equipStage(5)

        assertEquals(0, mockWebServer.requestCount)
        assertEquals(5, gameManager.activePhoenixStage.value)
        assertTrue(result is com.simats.codeingo.data.network.model.ApiResult.Success)
    }

    @Test
    fun testEquipStage_networkErrorFallbackDoesNotCrash() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("""{"error": "Internal Server Error"}""")
        )

        val result = repository.equipStage(7)

        assertEquals(7, gameManager.activePhoenixStage.value)
        assertTrue(result is com.simats.codeingo.data.network.model.ApiResult.HttpError)
    }

    @Test
    fun testEmotionTelemetry_blockedWhenConsentIsFalse() = runBlocking {
        // Explicit opt-in is false by default
        preferencesMap[MascotRepository.KEY_TELEMETRY_CONSENT] = false
        fakeTokenStorage.token = "valid_token"

        val sent = repository.recordEmotionTelemetry(
            emotion = "EUREKA_SPARK",
            reason = "5x Eureka Combo",
            contextInfo = "CipherSanctum"
        )

        assertFalse("Telemetry must not be sent when consent is false", sent)
        assertEquals("No network requests should be made", 0, mockWebServer.requestCount)
    }

    @Test
    fun testEmotionTelemetry_sentWhenConsentIsTrueAndAuthenticated() = runBlocking {
        // Explicit opt-in granted
        preferencesMap[MascotRepository.KEY_TELEMETRY_CONSENT] = true
        fakeTokenStorage.token = "valid_token"

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("""{"success": true, "message": "Telemetry logged"}""")
        )

        val sent = repository.recordEmotionTelemetry(
            emotion = "STARRY_PRODIGY",
            reason = "Perfect Quiz Score",
            contextInfo = "QuizAssessment"
        )

        assertTrue("Telemetry must be sent when consent is true and authenticated", sent)
        val request = mockWebServer.takeRequest(2, TimeUnit.SECONDS)
        assertNotNull(request)
        assertEquals("/api/mascot/emotion", request?.path)
        assertEquals("POST", request?.method)

        val body = request?.body?.readUtf8().orEmpty()
        assertTrue("Body must include emotion", body.contains("STARRY_PRODIGY"))
        assertTrue("Body must include reason", body.contains("Perfect Quiz Score"))
    }

    @Test
    fun testEmotionTelemetry_blockedWhenUnauthenticatedEvenIfConsentIsTrue() = runBlocking {
        preferencesMap[MascotRepository.KEY_TELEMETRY_CONSENT] = true
        fakeTokenStorage.token = null // Unauthenticated

        val sent = repository.recordEmotionTelemetry(
            emotion = "WORRIED_EMBER",
            reason = "Streak Urgency"
        )

        assertFalse("Telemetry must not be sent when unauthenticated", sent)
        assertEquals("No network requests should be made", 0, mockWebServer.requestCount)
    }
}
