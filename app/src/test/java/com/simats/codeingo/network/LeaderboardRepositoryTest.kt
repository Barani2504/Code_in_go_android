package com.simats.codeingo.network

import com.simats.codeingo.data.network.api.CodeingoApiService
import com.simats.codeingo.data.network.model.LeaderboardEntryDTO
import com.simats.codeingo.data.network.repository.LeaderboardRepository
import com.simats.codeingo.domain.GameManager
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

class LeaderboardRepositoryTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: CodeingoApiService
    private lateinit var repository: LeaderboardRepository
    private val gameManager = GameManager.instance

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
        repository = LeaderboardRepository(apiService, gameManager)

        gameManager.resetAllProgress()
        gameManager.deductGems(gameManager.gemsCount.value)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testFetchLeaderboard_sendsLeagueAndCourseId() = runBlocking {
        val mockJson = """
            [
                {
                    "username": "Alice_Crypto",
                    "totalXp": 600,
                    "league": "BRONZE",
                    "courseId": "cns_defense"
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(mockJson)
        )

        val results = repository.getRankings(league = "BRONZE", courseId = "cns_defense", forceRefresh = true)
        val recordedRequest = mockWebServer.takeRequest(2, TimeUnit.SECONDS)

        assertNotNull(recordedRequest)
        assertEquals("/api/users/leaderboard?league=BRONZE&courseId=cns_defense", recordedRequest?.path)
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun testCourseScoping_discardsForeignDsaLearners() = runBlocking {
        // Backend returns a mixed list of DSA and CNS learners
        val candidateEntries = listOf(
            LeaderboardEntryDTO(username = "Alice_CNS", totalXp = 700, courseId = "cns_defense"),
            LeaderboardEntryDTO(username = "Bob_DSA", totalXp = 800, courseId = "dsa"),
            LeaderboardEntryDTO(username = "Charlie_General", totalXp = 500, courseId = null)
        )

        val mockJson = """
            [
                {"username": "Alice_CNS", "totalXp": 700, "courseId": "cns_defense"},
                {"username": "Bob_DSA", "totalXp": 800, "courseId": "dsa"},
                {"username": "Charlie_General", "totalXp": 500, "courseId": null}
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(mockJson)
        )

        val results = repository.getRankings(league = "BRONZE", courseId = "cns_defense", forceRefresh = true)

        // Bob_DSA must be discarded; Alice_CNS and Charlie_General preserved
        assertTrue("Must contain Alice_CNS", results.any { it.name == "Alice_CNS" })
        assertTrue("Must NOT contain Bob_DSA", results.none { it.name == "Bob_DSA" })
    }

    @Test
    fun testLiveUserInjection_whenUserNotInList() = runBlocking {
        // Set local user XP to 650
        gameManager.addXP(650)

        val entries = listOf(
            LeaderboardEntryDTO(username = "UserA", totalXp = 800, currentStreak = 5),
            LeaderboardEntryDTO(username = "UserB", totalXp = 500, currentStreak = 3)
        )

        val ranked = repository.processAndRankEntries(entries)

        // 3 entries: UserA (800), User (650), UserB (500)
        assertEquals(3, ranked.size)

        val userEntry = ranked.firstOrNull { it.isUser }
        assertNotNull("User entry must be live-injected", userEntry)
        assertEquals(2, userEntry?.rank) // Rank 2 because 800 > 650 > 500
        assertEquals(650, userEntry?.xp)
    }

    @Test
    fun testReconcileExistingUser_takesMaxXp() = runBlocking {
        gameManager.addXP(400)

        // Backend says operative has 250 XP
        val entries = listOf(
            LeaderboardEntryDTO(username = "operative", totalXp = 250, currentStreak = 2)
        )

        val ranked = repository.processAndRankEntries(entries)
        val userEntry = ranked.first { it.isUser }

        // Must adopt max(250, 400) = 400 XP
        assertEquals(400, userEntry.xp)
    }

    @Test
    fun testSortingAndDynamicRanks_highestXpIsRankOne() {
        val entries = listOf(
            LeaderboardEntryDTO(username = "Low", totalXp = 100),
            LeaderboardEntryDTO(username = "High", totalXp = 900),
            LeaderboardEntryDTO(username = "Mid", totalXp = 500)
        )

        val ranked = repository.processAndRankEntries(entries)

        assertEquals("High", ranked[0].name)
        assertEquals(1, ranked[0].rank)

        assertEquals("Mid", ranked[1].name)
        assertEquals(2, ranked[1].rank)

        assertEquals("Low", ranked[2].name)
        assertEquals(3, ranked[2].rank)
    }

    @Test
    fun testOfflineFallback_returnsBenchmarkRosterWhenServerError() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("""{"error": "Internal Server Error"}""")
        )

        val results = repository.getRankings(league = "BRONZE", courseId = "cns_defense", forceRefresh = true)

        // Must return default benchmark roster with live user injected and not empty!
        assertTrue("Fallback must provide non-empty roster", results.isNotEmpty())
        assertTrue("Must include user entry in fallback", results.any { it.isUser })
    }

    @Test
    fun testUnauthorizedFallback_returnsBenchmarkRosterWhen401() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(401)
                .setBody("""{"error": "Unauthorized"}""")
        )

        val results = repository.getRankings(league = "BRONZE", courseId = "cns_defense", forceRefresh = true)

        assertTrue("401 must fallback to benchmark roster", results.isNotEmpty())
        assertTrue("Must include user entry in fallback", results.any { it.isUser })
    }

    @Test
    fun testEmptyListFallback_returnsBenchmarkRosterWhenRemoteIsEmpty() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("[]")
        )

        val results = repository.getRankings(league = "BRONZE", courseId = "cns_defense", forceRefresh = true)

        assertTrue("Empty list response must fallback to benchmark roster", results.isNotEmpty())
        assertTrue("Must include user entry in fallback", results.any { it.isUser })
    }

    @Test
    fun testMalformedJsonFallback_returnsBenchmarkRosterWithoutCrash() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("""{ "corrupted": true }""")
        )

        val results = repository.getRankings(league = "BRONZE", courseId = "cns_defense", forceRefresh = true)

        assertTrue("Malformed JSON must gracefully fallback to benchmark roster", results.isNotEmpty())
        assertTrue("Must include user entry in fallback", results.any { it.isUser })
    }
}
