package com.simats.codeingo.network

import com.simats.codeingo.data.network.api.CodeingoApiService
import com.simats.codeingo.data.network.model.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

class NetworkContractTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: CodeingoApiService
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        explicitNulls = false
        isLenient = true
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
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testAuthenticate_200Success() = runBlocking {
        val jsonBody = """
            {
                "token": "mock_jwt_token_123",
                "refreshToken": "mock_refresh_token_456",
                "user": {
                    "id": 42,
                    "username": "operative_cns",
                    "email": "agent@cns.org",
                    "totalXp": 150,
                    "hearts": 5,
                    "gems": 400,
                    "currentStreak": 3
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonBody))

        val response = apiService.authenticate(AuthRequest("agent@cns.org", "pwd123"))
        assertTrue(response.isSuccessful)

        val auth = response.body()
        assertNotNull(auth)
        assertEquals("mock_jwt_token_123", auth?.token)
        assertEquals("mock_refresh_token_456", auth?.refreshToken)
        assertEquals("operative_cns", auth?.user?.username)
        assertEquals(150, auth?.user?.totalXp)
    }

    @Test
    fun testAuthenticate_401Unauthorized() = runBlocking {
        val errorJson = """{"message": "Invalid email or password"}"""
        mockWebServer.enqueue(MockResponse().setResponseCode(401).setBody(errorJson))

        val response = apiService.authenticate(AuthRequest("bad@user.com", "wrong"))
        assertFalse(response.isSuccessful)
        assertEquals(401, response.code())
    }

    @Test
    fun testRegister_409Conflict() = runBlocking {
        val conflictJson = """{"error": "Account already exists with this email", "status": 409}"""
        mockWebServer.enqueue(MockResponse().setResponseCode(409).setBody(conflictJson))

        val response = apiService.register(RegisterRequest(email = "existing@cns.org", password = "pwd"))
        assertFalse(response.isSuccessful)
        assertEquals(409, response.code())
    }

    @Test
    fun testProgressStatus_parsingWithGemsAndNullables() = runBlocking {
        val progressJson = """
            {
                "completedLessons": ["cns.c0.l1", "cns.c1.l1", "u1_l1_foreign_dsa"],
                "totalXp": 250,
                "hearts": 5,
                "gems": 520,
                "streakDays": 7,
                "currentLessonId": null
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(progressJson))

        val response = apiService.getProgressStatus("cns_defense")
        assertTrue(response.isSuccessful)

        val status = response.body()
        assertNotNull(status)
        assertEquals(250, status?.totalXp)
        assertEquals(520, status?.gems) // Verified gems field
        assertEquals(7, status?.streakDays)
        assertEquals(3, status?.completedLessons?.size)
    }

    @Test
    fun testGenericApiResponse_lenientStatusField_intAndString() {
        // Heterogeneous test: server returns status as Integer 200
        val jsonWithInt = """{"message": "OTP verified", "success": true, "status": 200}"""
        val parsedInt = json.decodeFromString<GenericApiResponse>(jsonWithInt)
        assertEquals("200", parsedInt.status)
        assertTrue(parsedInt.success == true)

        // Heterogeneous test: server returns status as String "200"
        val jsonWithString = """{"message": "OTP verified", "success": true, "status": "200"}"""
        val parsedString = json.decodeFromString<GenericApiResponse>(jsonWithString)
        assertEquals("200", parsedString.status)
        assertTrue(parsedString.success == true)

        // Heterogeneous test: server omits status
        val jsonWithNull = """{"message": "OTP verified", "success": true}"""
        val parsedNull = json.decodeFromString<GenericApiResponse>(jsonWithNull)
        assertNull(parsedNull.status)
    }
}
