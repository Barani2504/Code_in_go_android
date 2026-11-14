package com.simats.codeingo

import com.simats.codeingo.domain.StreakManager
import com.simats.codeingo.ui.social.FeatureFlag
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

/**
 * 🧪 Phase 5 Unit Tests: Retention, Social & Ethical Growth
 */
class Phase5RetentionAndSocialTest {

    private val streakManager = StreakManager()

    @Test
    fun testStreakCountdown_timeUntilMidnight() {
        val (hours, minutes) = streakManager.getTimeUntilMidnight()
        assertTrue("Hours until midnight must be in [0, 23]", hours in 0..23)
        assertTrue("Minutes until midnight must be in [0, 59]", minutes in 0..59)
    }

    @Test
    fun testStreakRepair_within24Hours() {
        val now = System.currentTimeMillis()
        val missedRecent = now - (6L * 60L * 60L * 1000L) // 6 hours ago
        val missedOld = now - (26L * 60L * 60L * 1000L) // 26 hours ago

        assertTrue("Should be able to repair within 24 hours", streakManager.canRepairStreak(missedRecent, now))
        assertFalse("Should NOT be able to repair after 24 hours", streakManager.canRepairStreak(missedOld, now))
    }

    @Test
    fun testWeekendAmulet_weekendProtection() {
        // Saturday timestamp
        val satCal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
        }
        val satTime = satCal.timeInMillis
        val fridayTime = satTime - (24L * 60L * 60L * 1000L)

        // Streak should NOT be lost on Saturday if Weekend Amulet is active
        val isLostWithAmulet = streakManager.isStreakLost(
            lastActiveTimestamp = fridayTime,
            currentTimestamp = satTime,
            weekendAmuletActive = true
        )
        assertFalse("Streak should be protected by Weekend Amulet on Saturday", isLostWithAmulet)
    }

    @Test
    fun testNudgeCooldown_24HourCooldown() {
        val now = System.currentTimeMillis()
        val recentNudge = now - (2L * 60L * 60L * 1000L) // 2 hours ago
        val oldNudge = now - (25L * 60L * 60L * 1000L) // 25 hours ago

        val canNudgeRecent = (now - recentNudge) >= 24L * 60L * 60L * 1000L
        val canNudgeOld = (now - oldNudge) >= 24L * 60L * 60L * 1000L

        assertFalse("Cannot nudge again within 24 hours", canNudgeRecent)
        assertTrue("Can nudge after 24 hours cooldown", canNudgeOld)
    }

    @Test
    fun testFeatureFlags_defaultStatus() {
        assertTrue("Friends layer should be enabled by default", FeatureFlag.FRIENDS_ENABLED)
        assertTrue("Plus tier should be enabled by default", FeatureFlag.PLUS_TIER_ENABLED)
    }
}

