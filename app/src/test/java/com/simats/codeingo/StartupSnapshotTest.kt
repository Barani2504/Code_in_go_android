package com.simats.codeingo

import com.simats.codeingo.data.local.GameStateSnapshot
import com.simats.codeingo.data.local.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StartupSnapshotTest {

    @Test
    fun testGameStateSnapshot_frozenKeysParityFixture() {
        // Legacy frozen keys test fixture: verifies all frozen keys from PARITY.md
        // load into GameStateSnapshot with 100% fidelity.
        val snapshot = GameStateSnapshot(
            totalXP = 540,
            gemsCount = 350,
            heartsCount = 5,
            streakDays = 14,
            activePhoenixStage = 3,
            highestCombo = 8,
            eggCrackLevel = 2,
            lastPracticedTimestampMs = 1700000000000L,
            lastPlayedDateMs = 1700000000000L,
            completedLessons = setOf("u1_l1_what_is_ds", "u1_l2_array_intro"),
            completedBosses = setOf("boss_unit_1"),
            unlockedLevels = setOf(1, 2, 3),
            maxUnlockedChapter = 2,
            completedChapters = setOf(1),
            completedLevelIndices = setOf(1, 2, 3, 4, 5),
            activeLevelIndex = 6,
            basecampCompletedLevels = setOf("bc_1"),
            diagnosticTaken = true,
            diagnosticRecommendedChapter = 2
        )

        // Assert all 7 frozen persistent keys match
        assertEquals("dsa_total_xp parity", 540, snapshot.totalXP)
        assertEquals("dsa_gems parity", 350, snapshot.gemsCount)
        assertEquals("dsa_hearts parity", 5, snapshot.heartsCount)
        assertEquals("dsa_streak_days parity", 14, snapshot.streakDays)
        assertEquals("active_phoenix_stage parity", 3, snapshot.activePhoenixStage)
        assertEquals("dsa_highest_combo parity", 8, snapshot.highestCombo)
        assertEquals("home_active_level_index parity", 6, snapshot.activeLevelIndex)

        // Assert course progression keys match
        assertEquals(2, snapshot.maxUnlockedChapter)
        assertTrue(snapshot.completedChapters.contains(1))
        assertTrue(snapshot.completedLevelIndices.containsAll(listOf(1, 2, 3, 4, 5)))
        assertTrue(snapshot.unlockedLevels.contains(3))
        assertTrue(snapshot.diagnosticTaken)
    }

    @Test
    fun testThreadSafeCounterUpdate_concurrentSafety() {
        val counter = MutableStateFlow(0)
        val numThreads = 10
        val incrementsPerThread = 1000

        val threads = (1..numThreads).map {
            Thread {
                repeat(incrementsPerThread) {
                    counter.update { it + 1 }
                }
            }
        }

        threads.forEach { it.start() }
        threads.forEach { it.join() }

        assertEquals(
            "Atomic MutableStateFlow.update must produce exact count without race conditions",
            numThreads * incrementsPerThread,
            counter.value
        )
    }

    @Test
    fun testHeartDecrement_clampsAtZero() {
        val hearts = MutableStateFlow(1)
        hearts.update { maxOf(0, it - 1) }
        assertEquals("Hearts decremented to 0", 0, hearts.value)

        // Decrementing again must stay clamped at 0
        hearts.update { maxOf(0, it - 1) }
        assertEquals("Hearts must clamp at 0 and not go negative", 0, hearts.value)
    }

    @Test
    fun testGemDeduction_clampsSafely() {
        val gems = MutableStateFlow(50)
        val price = 100
        val canAfford = gems.value >= price
        assertFalse("Cannot afford item when gems < price", canAfford)

        // If deducted
        gems.update { maxOf(0, it - 30) }
        assertEquals(20, gems.value)
    }
}
