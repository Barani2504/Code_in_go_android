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
}
