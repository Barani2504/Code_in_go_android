package com.simats.codeingo.network

import com.simats.codeingo.data.db.OfflineSyncDao
import com.simats.codeingo.data.db.OfflineSyncEntity
import com.simats.codeingo.data.db.SyncState
import com.simats.codeingo.data.local.UserProgressSource
import com.simats.codeingo.data.network.api.CodeingoApiService
import com.simats.codeingo.data.network.repository.ProgressOutboxRepository
import com.simats.codeingo.data.network.security.TokenStorage
import com.simats.codeingo.data.network.worker.SyncScheduler
import com.simats.codeingo.domain.GameManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
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
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.IOException

class ProgressOutboxRepositoryTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: CodeingoApiService
    private lateinit var fakeDao: FakeOfflineSyncDao
    private lateinit var fakeTokenStorage: FakeTokenStorage
    private lateinit var fakeSyncScheduler: FakeSyncScheduler
    private lateinit var fakeProgressSource: FakeUserProgressSource
    private lateinit var repository: ProgressOutboxRepository

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    class FakeOfflineSyncDao : OfflineSyncDao {
        val items = mutableListOf<OfflineSyncEntity>()
        private var nextId = 1L

        override suspend fun findByUserAndLesson(userId: String, lessonId: String): OfflineSyncEntity? {
            return items.firstOrNull { it.userId == userId && it.lessonId == lessonId }
        }

        override suspend fun insertOrUpdate(entity: OfflineSyncEntity): Long {
            val index = items.indexOfFirst { it.userId == entity.userId && it.lessonId == entity.lessonId }
            val toSave = if (entity.id == 0L) entity.copy(id = nextId++) else entity
            if (index != -1) {
                items[index] = toSave
            } else {
                items.add(toSave)
            }
            return toSave.id
        }

        override suspend fun getPendingItems(): List<OfflineSyncEntity> {
            return items.filter { it.syncState != SyncState.FAILED_PERMANENT }
        }

        override suspend fun getPendingItemsForUser(userId: String): List<OfflineSyncEntity> {
            return items.filter { it.userId == userId && it.syncState != SyncState.FAILED_PERMANENT }
        }

        override suspend fun updateSyncState(id: Long, state: SyncState, error: String?) {
            val index = items.indexOfFirst { it.id == id }
            if (index != -1) {
                val cur = items[index]
                items[index] = cur.copy(syncState = state, attemptCount = cur.attemptCount + 1, lastError = error)
            }
        }

        override suspend fun delete(entity: OfflineSyncEntity) {
            items.removeAll { it.id == entity.id }
        }

        override suspend fun deleteByIds(ids: List<Long>) {
            items.removeAll { it.id in ids }
        }

        override suspend fun clearForUser(userId: String) {
            items.removeAll { it.userId == userId }
        }

        override suspend fun clearAll() {
            items.clear()
        }

        override fun getPendingCountFlow(): Flow<Int> = flowOf(items.count { it.syncState != SyncState.FAILED_PERMANENT })
    }

    class FakeTokenStorage : TokenStorage {
        var token: String? = "test_valid_jwt_token"
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

    class FakeSyncScheduler : SyncScheduler {
        var scheduleCount = 0
        override fun scheduleSync() {
            scheduleCount++
        }
    }

    class FakeUserProgressSource(
        val completedLevels: Set<String> = emptySet(),
        val basecampLevels: Set<String> = emptySet()
    ) : UserProgressSource {
        override val completedLevelIndices: Flow<Set<String>> = flowOf(completedLevels)
        override val basecampCompletedLevels: Flow<Set<String>> = flowOf(basecampLevels)
    }

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val contentType = "application/json".toMediaType()
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(json.asConverterFactory(contentType))
            .client(OkHttpClient.Builder().build())
            .build()

        apiService = retrofit.create(CodeingoApiService::class.java)
        fakeDao = FakeOfflineSyncDao()
        fakeTokenStorage = FakeTokenStorage()
        fakeSyncScheduler = FakeSyncScheduler()
        fakeProgressSource = FakeUserProgressSource()

        repository = ProgressOutboxRepository(
            syncScheduler = fakeSyncScheduler,
            offlineSyncDao = fakeDao,
            apiService = apiService,
            tokenStorage = fakeTokenStorage,
            userProgressSource = fakeProgressSource
        )

        // Reset GameManager to known clean state
        GameManager.instance.resetAllProgress()
        GameManager.instance.deductGems(GameManager.instance.gemsCount.value)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testEnqueueLessonCompletion_insertsNewPendingRecord() = runBlocking {
        val success = repository.enqueueLessonCompletion(levelIndex = 1, accuracy = 85)
        assertTrue(success)

        assertEquals(1, fakeDao.items.size)
        val item = fakeDao.items[0]
        assertEquals("cns.c1.l1", item.lessonId)
        assertEquals(85, item.accuracyPercentage)
        assertEquals(SyncState.PENDING, item.syncState)
        assertNotNull(item.idempotencyKey)
        assertEquals(1, fakeSyncScheduler.scheduleCount)
    }

    @Test
    fun testEnqueueLessonCompletion_updatesWithHigherAccuracy() = runBlocking {
        repository.enqueueLessonCompletion(levelIndex = 1, accuracy = 70)
        assertEquals(70, fakeDao.items[0].accuracyPercentage)

        repository.enqueueLessonCompletion(levelIndex = 1, accuracy = 95)
        assertEquals(1, fakeDao.items.size)
        assertEquals(95, fakeDao.items[0].accuracyPercentage)
        assertEquals(2, fakeSyncScheduler.scheduleCount)
    }

    @Test
    fun testEnqueueLessonCompletion_retainsHigherAccuracyOnLowerAttempt() = runBlocking {
        repository.enqueueLessonCompletion(levelIndex = 1, accuracy = 95)
        assertEquals(95, fakeDao.items[0].accuracyPercentage)

        repository.enqueueLessonCompletion(levelIndex = 1, accuracy = 60)
        assertEquals(1, fakeDao.items.size)
        assertEquals(95, fakeDao.items[0].accuracyPercentage)
    }

    @Test
    fun testEnqueueLessonCompletion_handlesBasecampLevels() = runBlocking {
        repository.enqueueLessonCompletion(levelIndex = 101, accuracy = 100)
        assertEquals(1, fakeDao.items.size)
        assertEquals("cns.c0.l1", fakeDao.items[0].lessonId)
    }

    @Test
    fun testUploadPendingCompletions_whenNoToken_skipsUpload() = runBlocking {
        fakeTokenStorage.token = null
        repository.enqueueLessonCompletion(levelIndex = 1, accuracy = 100)

        val result = repository.uploadPendingCompletions()
        assertTrue(result)
        assertEquals(1, fakeDao.items.size)
        assertEquals(SyncState.PENDING, fakeDao.items[0].syncState)
        assertEquals(0, mockWebServer.requestCount)
    }

    @Test
    fun testUploadPendingCompletions_successfulUpload_deletesItemAndReconcilesGameManager() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    """
                    {
                        "xpEarned": 20,
                        "totalXp": 800,
                        "accuracyPercentage": 100,
                        "currentStreak": 7,
                        "gemsEarned": 10,
                        "totalGems": 500
                    }
                    """.trimIndent()
                )
        )

        repository.enqueueLessonCompletion(levelIndex = 1, accuracy = 100)
        assertEquals(1, fakeDao.items.size)

        val result = repository.uploadPendingCompletions()
        assertTrue(result)
        assertEquals(0, fakeDao.items.size) // Deleted upon success

        // Verify GameManager reconciled non-destructively
        assertEquals(800, GameManager.instance.totalXP.value)
        assertEquals(500, GameManager.instance.gemsCount.value)
        assertEquals(7, GameManager.instance.streakDays.value)
    }

    @Test
    fun testUploadPendingCompletions_clientError_marksPermanentFailure() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(400)
                .setBody("""{"error": "Invalid lesson id"}""")
        )

        repository.enqueueLessonCompletion(levelIndex = 1, accuracy = 100)
        assertEquals(1, fakeDao.items.size)

        val result = repository.uploadPendingCompletions()
        assertTrue(result)
        assertEquals(1, fakeDao.items.size)
        assertEquals(SyncState.FAILED_PERMANENT, fakeDao.items[0].syncState)
        assertTrue(fakeDao.items[0].lastError?.contains("Client error 400") == true)
    }

    @Test
    fun testUploadPendingCompletions_unauthorized_keepsPendingAndHalts() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(401)
                .setBody("""{"error": "Unauthorized"}""")
        )

        repository.enqueueLessonCompletion(levelIndex = 1, accuracy = 100)
        val result = repository.uploadPendingCompletions()

        assertEquals(false, result)
        assertEquals(1, fakeDao.items.size)
        assertEquals(SyncState.PENDING, fakeDao.items[0].syncState)
        assertTrue(fakeDao.items[0].lastError?.contains("Unauthorized (401)") == true)
    }

    @Test
    fun testUploadPendingCompletions_serverError_throwsIOExceptionForWorkManagerRetry() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("""{"error": "Internal Server Error"}""")
        )

        repository.enqueueLessonCompletion(levelIndex = 1, accuracy = 100)

        try {
            repository.uploadPendingCompletions()
            fail("Expected IOException on 500 server error")
        } catch (e: IOException) {
            assertTrue(e.message?.contains("Server error 500") == true)
        }

        assertEquals(1, fakeDao.items.size)
        assertEquals(SyncState.PENDING, fakeDao.items[0].syncState)
    }

    @Test
    fun testUploadInitialLocalProgress_seedsUnsyncedLevels() = runBlocking {
        val seededProgressSource = FakeUserProgressSource(
            completedLevels = setOf("1", "2"),
            basecampLevels = setOf("c0_l1")
        )

        val repoWithSeed = ProgressOutboxRepository(
            syncScheduler = fakeSyncScheduler,
            offlineSyncDao = fakeDao,
            apiService = apiService,
            tokenStorage = fakeTokenStorage,
            userProgressSource = seededProgressSource
        )

        repoWithSeed.uploadInitialLocalProgress("user_42")

        assertEquals(3, fakeDao.items.size)
        val lessonIds = fakeDao.items.map { it.lessonId }.toSet()
        assertTrue(lessonIds.contains("cns.c1.l1"))
        assertTrue(lessonIds.contains("cns.c1.l2"))
        assertTrue(lessonIds.contains("cns.c0.l1"))
        assertEquals(1, fakeSyncScheduler.scheduleCount)
    }
}
