package com.simats.codeingo.network

import com.simats.codeingo.domain.GameManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GameManagerSnapshotReconciliationTest {

    private val gameManager = GameManager.instance

    @Before
    fun setUp() {
        gameManager.resetAllProgress()
        gameManager.deductGems(gameManager.gemsCount.value)
    }

    @Test
    fun testApplyRemoteSnapshot_advancesStateNonDestructively() {
        assertEquals(0, gameManager.totalXP.value)
        assertEquals(1, gameManager.maxUnlockedChapter.value)

        // Remote snapshot arrives with higher stats and completed lessons
        val remoteIndices = setOf(101, 102, 1, 2, 6) // Basecamp + Chapter 1 + Chapter 2 Level 6
        gameManager.applyRemoteSnapshot(
            remoteTotalXP = 200,
            remoteGems = 350,
            remoteStreak = 5,
            remoteCompletedIndices = remoteIndices
        )

        // Verifies stats updated cleanly
        assertEquals(200, gameManager.totalXP.value)
        assertEquals(350, gameManager.gemsCount.value)
        assertEquals(5, gameManager.streakDays.value)

        // Verifies completed indices contains the union
        assertTrue(gameManager.completedLevelIndices.value.containsAll(remoteIndices))

        // Level 6 is in Chapter 2, so maxUnlockedChapter should advance to at least 2
        assertTrue(gameManager.maxUnlockedChapter.value >= 2)
    }

    @Test
    fun testApplyRemoteSnapshot_neverAppliesNegativeDelta() {
        // Set local state higher than remote
        gameManager.addXP(500)
        gameManager.addGems(600)
        assertEquals(500, gameManager.totalXP.value)
        assertEquals(600, gameManager.gemsCount.value)

        // Stale or smaller remote snapshot arrives (e.g. 300 XP, 400 gems)
        gameManager.applyRemoteSnapshot(
            remoteTotalXP = 300,
            remoteGems = 400,
            remoteStreak = 1,
            remoteCompletedIndices = emptySet()
        )

        // Local state MUST remain 500 XP and 600 gems (never truncated!)
        assertEquals(500, gameManager.totalXP.value)
        assertEquals(600, gameManager.gemsCount.value)
    }

    @Test
    fun testApplyRemoteSnapshot_echoLoopPrevention_neverTriggersOutboxEnqueue() {
        var outboxInserted = false
        val trackingDao = object : com.simats.codeingo.data.db.OfflineSyncDao {
            override suspend fun findByUserAndLesson(userId: String, lessonId: String): com.simats.codeingo.data.db.OfflineSyncEntity? = null
            override suspend fun insertOrUpdate(entity: com.simats.codeingo.data.db.OfflineSyncEntity): Long {
                outboxInserted = true
                return 1L
            }
            override suspend fun getPendingItems(): List<com.simats.codeingo.data.db.OfflineSyncEntity> = emptyList()
            override suspend fun getPendingItemsForUser(userId: String): List<com.simats.codeingo.data.db.OfflineSyncEntity> = emptyList()
            override suspend fun updateSyncState(id: Long, state: com.simats.codeingo.data.db.SyncState, error: String?) {}
            override suspend fun delete(entity: com.simats.codeingo.data.db.OfflineSyncEntity) {}
            override suspend fun deleteByIds(ids: List<Long>) {}
            override suspend fun clearForUser(userId: String) {}
            override suspend fun clearAll() {}
            override fun getPendingCountFlow(): kotlinx.coroutines.flow.Flow<Int> = kotlinx.coroutines.flow.flowOf(0)
        }

        val trackingScheduler = com.simats.codeingo.data.network.worker.SyncScheduler {
            outboxInserted = true
        }

        val fakeProgressSource = object : com.simats.codeingo.data.local.UserProgressSource {
            override val completedLevelIndices: kotlinx.coroutines.flow.Flow<Set<String>> = kotlinx.coroutines.flow.flowOf(emptySet())
            override val basecampCompletedLevels: kotlinx.coroutines.flow.Flow<Set<String>> = kotlinx.coroutines.flow.flowOf(emptySet())
        }

        val fakeTokenStorage = object : com.simats.codeingo.data.network.security.TokenStorage {
            override val hasValidToken: kotlinx.coroutines.flow.StateFlow<Boolean> = kotlinx.coroutines.flow.MutableStateFlow(true)
            override fun saveAccessToken(token: String) {}
            override fun getAccessToken(): String? = "test_token"
            override fun saveRefreshToken(token: String) {}
            override fun getRefreshToken(): String? = null
            override fun clearTokens() {}
        }

        val mockApi = java.lang.reflect.Proxy.newProxyInstance(
            com.simats.codeingo.data.network.api.CodeingoApiService::class.java.classLoader,
            arrayOf(com.simats.codeingo.data.network.api.CodeingoApiService::class.java)
        ) { _, _, _ -> null } as com.simats.codeingo.data.network.api.CodeingoApiService

        val outboxRepo = com.simats.codeingo.data.network.repository.ProgressOutboxRepository(
            syncScheduler = trackingScheduler,
            offlineSyncDao = trackingDao,
            apiService = mockApi,
            tokenStorage = fakeTokenStorage,
            userProgressSource = fakeProgressSource
        )

        gameManager.setProgressOutboxRepository(outboxRepo)

        // Apply remote snapshot with multiple completed levels
        gameManager.applyRemoteSnapshot(
            remoteTotalXP = 999,
            remoteGems = 888,
            remoteStreak = 10,
            remoteCompletedIndices = setOf(101, 102, 1, 2, 3, 26, 27)
        )

        // REGRESSION INVARIANT: applyRemoteSnapshot MUST NEVER insert into outbox
        org.junit.Assert.assertFalse("Echo loop detected! applyRemoteSnapshot must never enqueue items to outbox", outboxInserted)
    }
}
