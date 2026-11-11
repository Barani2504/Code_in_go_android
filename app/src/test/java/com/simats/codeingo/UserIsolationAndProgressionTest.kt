package com.simats.codeingo

import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.ui.arenas.common.ArenaProgressionManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UserIsolationAndProgressionTest {

    private val gameManager = GameManager.instance

    @Before
    fun setUp() {
        gameManager.resetAllProgress()
    }

    @Test
    fun testNewUser_hasIsolatedZeroState() {
        assertEquals("New user must start with 0 XP", 0, gameManager.totalXP.value)
        assertEquals("New user must start with 0 gems", 0, gameManager.gemsCount.value)
        assertEquals("New user must start with 0 streak days", 0, gameManager.streakDays.value)
        assertEquals("New user must start at Chapter 1", 1, gameManager.maxUnlockedChapter.value)
        assertEquals("New user must have only level 1 unlocked", setOf(1), gameManager.unlockedLevelIndices.value)
        assertEquals("New user must start with active level 1", 1, gameManager.activeLevelIndex.value)
        assertTrue("New user must have no completed chapters", gameManager.completedChapters.value.isEmpty())
        assertTrue("New user must have no completed levels", gameManager.completedLevelIndices.value.isEmpty())
        assertTrue("New user must have no completed lesson IDs", gameManager.completedLessonIds.value.isEmpty())
    }

    @Test
    fun testResetAllProgress_cleansCrossUserState() {
        // Simulate previous user progress
        gameManager.applyRemoteSnapshot(
            remoteTotalXP = 500,
            remoteGems = 250,
            remoteStreak = 5,
            remoteCompletedIndices = setOf(1, 2, 3, 4, 5),
            remoteCurrentUnitIndex = 2
        )

        assertEquals(500, gameManager.totalXP.value)
        assertEquals(2, gameManager.maxUnlockedChapter.value)

        // Reset on logout
        gameManager.resetAllProgress()

        assertEquals("XP must be reset to 0 on logout", 0, gameManager.totalXP.value)
        assertEquals("Gems must be reset to 0 on logout", 0, gameManager.gemsCount.value)
        assertEquals("Streak must be reset to 0 on logout", 0, gameManager.streakDays.value)
        assertEquals("Max chapter must be reset to 1 on logout", 1, gameManager.maxUnlockedChapter.value)
        assertEquals("Unlocked levels must reset to strictly level 1", setOf(1), gameManager.unlockedLevelIndices.value)
        assertTrue("Completed levels must be empty", gameManager.completedLevelIndices.value.isEmpty())
        assertTrue("Completed lessons must be empty", gameManager.completedLessonIds.value.isEmpty())
    }

    @Test
    fun testArenaUnlocking_initialStateIsLevelOneOnly() {
        // When SharedPreferences has no saved progression for a new user,
        // ArenaProgressionManager must enforce that only Level 1 is unlocked.
        val mockEmptyPrefs = object : android.content.SharedPreferences {
            override fun getAll(): MutableMap<String, *> = mutableMapOf<String, Any>()
            override fun getString(key: String?, defValue: String?): String? = defValue
            override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String>? = null
            override fun getInt(key: String?, defValue: Int): Int = defValue
            override fun getLong(key: String?, defValue: Long): Long = defValue
            override fun getFloat(key: String?, defValue: Float): Float = defValue
            override fun getBoolean(key: String?, defValue: Boolean): Boolean = defValue
            override fun contains(key: String?): Boolean = false
            override fun edit(): android.content.SharedPreferences.Editor = throw UnsupportedOperationException()
            override fun registerOnSharedPreferenceChangeListener(listener: android.content.SharedPreferences.OnSharedPreferenceChangeListener?) {}
            override fun unregisterOnSharedPreferenceChangeListener(listener: android.content.SharedPreferences.OnSharedPreferenceChangeListener?) {}
        }

        val unlocked = ArenaProgressionManager.getUnlockedLevels(mockEmptyPrefs, "cipher_sanctum_unlocked")
        assertEquals("Only Level 1 must be unlocked for a fresh user in any arena", setOf(1), unlocked)
    }
}

