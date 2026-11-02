package com.simats.codeingo.network

import com.simats.codeingo.domain.GameManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CurrencyReconciliationTest {

    private val gameManager = GameManager.instance

    @Before
    fun setUp() {
        gameManager.resetAllProgress()
        gameManager.deductGems(gameManager.gemsCount.value)
        gameManager.setLastSyncedServerGemsForTesting(0)
    }

    @Test
    fun testHeartsSovereignty_remoteFiveHeartsNeverOverwritesLocalTenHearts() {
        // Android local capacity is 10 hearts
        gameManager.refillHearts()
        assertEquals(10, gameManager.heartsCount.value)

        // Backend snapshot arrives with remote hearts = 5 (DSA backend max)
        gameManager.applyRemoteSnapshot(
            remoteTotalXP = 100,
            remoteGems = 200,
            remoteStreak = 2,
            remoteCompletedIndices = emptySet(),
            remotePhoenixStage = 1,
            isLessonUploadResponse = false
        )

        // Local hearts MUST remain 10/10
        assertEquals("Local 10-heart capacity must never be overwritten by remote 5 hearts", 10, gameManager.heartsCount.value)
    }

    @Test
    fun testInitialSync_adoptsRemoteGemsWhenHigher() {
        // Initially local gems is 0, lastSyncedServerGems is 0
        assertEquals(0, gameManager.gemsCount.value)
        assertEquals(0, gameManager.lastSyncedServerGems.value)

        // Initial sync from server arrives with 450 gems
        gameManager.applyRemoteSnapshot(
            remoteTotalXP = 150,
            remoteGems = 450,
            remoteStreak = 3,
            isLessonUploadResponse = false
        )

        // Local gems and watermark adopt remote
        assertEquals(450, gameManager.gemsCount.value)
        assertEquals(450, gameManager.lastSyncedServerGems.value)
    }

    @Test
    fun testLocalSpendPreservation_localDeductionsNeverRevertedByRemoteSnapshot() {
        // Establish baseline of 500 gems synced
        gameManager.applyRemoteSnapshot(
            remoteTotalXP = 100,
            remoteGems = 500,
            remoteStreak = 1,
            isLessonUploadResponse = false
        )
        assertEquals(500, gameManager.gemsCount.value)
        assertEquals(500, gameManager.lastSyncedServerGems.value)

        // User spends 200 gems locally in GemShopSheet (e.g. cosmetic item)
        val deducted = gameManager.deductGems(200)
        assertTrue(deducted)
        assertEquals(300, gameManager.gemsCount.value)

        // Stale or equal remote snapshot arrives from server (still says 500)
        gameManager.applyRemoteSnapshot(
            remoteTotalXP = 100,
            remoteGems = 500,
            remoteStreak = 1,
            isLessonUploadResponse = false
        )

        // Local gems MUST remain 300; spend must NOT be wiped out!
        assertEquals("Local gem purchase must be preserved against stale remote snapshot", 300, gameManager.gemsCount.value)
        assertEquals(500, gameManager.lastSyncedServerGems.value)
    }

    @Test
    fun testPositiveServerDelta_addsEarnedGemsToLocalBalance() {
        // Establish baseline of 500 gems synced
        gameManager.applyRemoteSnapshot(
            remoteTotalXP = 100,
            remoteGems = 500,
            remoteStreak = 1,
            isLessonUploadResponse = false
        )
        assertEquals(500, gameManager.gemsCount.value)

        // Local spend reduces balance to 300
        gameManager.deductGems(200)
        assertEquals(300, gameManager.gemsCount.value)

        // Remote user earned 50 gems on another device (remoteGems moves from 500 -> 550)
        gameManager.applyRemoteSnapshot(
            remoteTotalXP = 150,
            remoteGems = 550,
            remoteStreak = 2,
            isLessonUploadResponse = false
        )

        // Local balance should be 300 + (550 - 500) = 350
        assertEquals("Earned delta from another device should be added to local balance", 350, gameManager.gemsCount.value)
        assertEquals(550, gameManager.lastSyncedServerGems.value)
    }

    @Test
    fun testLessonCompletionOutboxDrain_advancesWatermarkWithoutDoubleCounting() {
        // Baseline 400 gems
        gameManager.applyRemoteSnapshot(
            remoteTotalXP = 100,
            remoteGems = 400,
            remoteStreak = 1,
            isLessonUploadResponse = false
        )
        assertEquals(400, gameManager.gemsCount.value)
        assertEquals(400, gameManager.lastSyncedServerGems.value)

        // Local lesson completion in UI awards +5 gems immediately
        gameManager.addGems(5)
        assertEquals(405, gameManager.gemsCount.value)

        // Outbox worker completes upload; server returns response with totalGems = 405
        gameManager.applyRemoteSnapshot(
            remoteTotalXP = 120,
            remoteGems = 405,
            remoteStreak = 1,
            isLessonUploadResponse = true
        )

        // Outbox response must NOT add 5 gems again! Local remains 405, watermark updates to 405
        assertEquals("Outbox lesson upload must not double count local gems", 405, gameManager.gemsCount.value)
        assertEquals(405, gameManager.lastSyncedServerGems.value)
    }

    @Test
    fun testStaleLowerSnapshot_neverDecreasesLocalStats() {
        gameManager.addXP(600)
        gameManager.addGems(700)
        gameManager.setLastSyncedServerGemsForTesting(700)

        // Server returns lower stats (e.g. network glitch or older cache)
        gameManager.applyRemoteSnapshot(
            remoteTotalXP = 200,
            remoteGems = 300,
            remoteStreak = 1,
            isLessonUploadResponse = false
        )

        assertEquals(600, gameManager.totalXP.value)
        assertEquals(700, gameManager.gemsCount.value)
        assertEquals(700, gameManager.lastSyncedServerGems.value)
    }

    @Test
    fun testPhoenixStageReconciliation_advancesWithoutRegression() {
        assertEquals(1, gameManager.activePhoenixStage.value)

        // Remote snapshot arrives with stage 5
        gameManager.applyRemoteSnapshot(
            remoteTotalXP = 100,
            remoteGems = 100,
            remoteStreak = 1,
            remotePhoenixStage = 5
        )
        assertEquals(5, gameManager.activePhoenixStage.value)

        // Stale remote snapshot arrives with stage 3
        gameManager.applyRemoteSnapshot(
            remoteTotalXP = 100,
            remoteGems = 100,
            remoteStreak = 1,
            remotePhoenixStage = 3
        )
        assertEquals("Phoenix stage must never regress on stale remote snapshot", 5, gameManager.activePhoenixStage.value)
    }

    @Test
    fun testOfflineXpPreservation_serverLowerThanLocalNeverDecreasesXp() {
        // User plays offline and accumulates 450 XP locally
        gameManager.addXP(450)
        assertEquals(450, gameManager.totalXP.value)

        // Device reconnects, server returns stale snapshot with 120 XP
        gameManager.applyRemoteSnapshot(
            remoteTotalXP = 120,
            remoteGems = 100,
            remoteStreak = 1
        )

        // Offline XP must be preserved
        assertEquals("Offline accumulated XP must never be decreased by lower server snapshot", 450, gameManager.totalXP.value)
    }

    @Test
    fun testClockSkew_reconcilesCompletionsRegardlessOfTimestampDifferences() {
        // Device clock skewed into the past or future relative to server
        val localIndices = setOf(1, 2, 3)
        localIndices.forEach { gameManager.markLevelCompleted(it) }

        // Server has level 4 completed with different clock time
        val remoteIndices = setOf(4)
        gameManager.applyRemoteSnapshot(
            remoteTotalXP = 300,
            remoteGems = 200,
            remoteStreak = 3,
            remoteCompletedIndices = remoteIndices
        )

        // Result must be union of all completed levels
        val expectedUnion = setOf(1, 2, 3, 4)
        assertTrue(gameManager.completedLevelIndices.value.containsAll(expectedUnion))
    }

    @Test
    fun testFirstLogin_localCompletionsUnionWithRemoteCompletions() {
        // Local guest has completed Basecamp 101, 102 and Chapter 1 levels 1, 2
        gameManager.markBasecampLevelCompleted("c0_l1")
        gameManager.markBasecampLevelCompleted("c0_l2")
        gameManager.markLevelCompleted(1)
        gameManager.markLevelCompleted(2)

        // Server account already has Core level 3 and 4 completed
        val serverRemoteIndices = setOf(3, 4)
        gameManager.applyRemoteSnapshot(
            remoteTotalXP = 200,
            remoteGems = 150,
            remoteStreak = 2,
            remoteCompletedIndices = serverRemoteIndices
        )

        // Assert union: local levels 1, 2 and server levels 3, 4 are present
        val union = gameManager.completedLevelIndices.value
        assertTrue("Local level 1 preserved", union.contains(1))
        assertTrue("Local level 2 preserved", union.contains(2))
        assertTrue("Server level 3 merged", union.contains(3))
        assertTrue("Server level 4 merged", union.contains(4))
    }
}
