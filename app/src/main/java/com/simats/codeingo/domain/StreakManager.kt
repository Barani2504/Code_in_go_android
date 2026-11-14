package com.simats.codeingo.domain

import android.content.Context
import com.simats.codeingo.domain.AuthService
import java.util.Calendar
import java.util.TimeZone

/**
 * 🔥 StreakManager: Ethical Streak Mechanics & Retention Loop
 *
 * Implements Phase 5.1:
 * - Weekend Amulet protection (skipping Saturday/Sunday without penalty)
 * - 24-Hour Streak Repair window for accidental missed days
 * - Countdown timer to local midnight deadline
 * - Zero-guilt Quiet Mode setting
 * - Per-user preference isolation
 */
class StreakManager(private val context: Context? = null) {

    private fun getUserKey(): String {
        return (AuthService.shared.currentUser.value?.email ?: "guest")
            .lowercase()
            .replace(Regex("[^a-z0-9_]"), "_")
    }

    private fun getPrefs(context: Context) =
        context.getSharedPreferences("cns_streak_prefs_${getUserKey()}", Context.MODE_PRIVATE)

    fun isWeekend(timestamp: Long = System.currentTimeMillis()): Boolean {
        val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
        val day = cal.get(Calendar.DAY_OF_WEEK)
        return day == Calendar.SATURDAY || day == Calendar.SUNDAY
    }

    fun isWeekendAmuletEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean("weekend_amulet_equipped", false)
    }

    fun setWeekendAmuletEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean("weekend_amulet_equipped", enabled).apply()
    }

    fun isQuietModeEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean("quiet_mode_enabled", false)
    }

    fun setQuietModeEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean("quiet_mode_enabled", enabled).apply()
    }

    fun shouldIncrementStreak(lastActiveTimestamp: Long, currentTimestamp: Long = System.currentTimeMillis()): Boolean {
        if (lastActiveTimestamp <= 0L) return true

        val lastCal = Calendar.getInstance().apply { timeInMillis = lastActiveTimestamp }
        val currCal = Calendar.getInstance().apply { timeInMillis = currentTimestamp }

        val diffDays = currCal.get(Calendar.DAY_OF_YEAR) - lastCal.get(Calendar.DAY_OF_YEAR)
        val diffYears = currCal.get(Calendar.YEAR) - lastCal.get(Calendar.YEAR)

        return (diffYears == 0 && diffDays == 1) || (diffYears == 1 && diffDays < 0)
    }

    fun isStreakLost(
        lastActiveTimestamp: Long,
        currentTimestamp: Long = System.currentTimeMillis(),
        weekendAmuletActive: Boolean = false
    ): Boolean {
        if (lastActiveTimestamp <= 0L) return false

        // If Weekend Amulet is active and today is Saturday or Sunday, streak is protected!
        if (weekendAmuletActive && isWeekend(currentTimestamp)) {
            return false
        }

        val lastCal = Calendar.getInstance().apply { timeInMillis = lastActiveTimestamp }
        val currCal = Calendar.getInstance().apply { timeInMillis = currentTimestamp }

        val diffDays = currCal.get(Calendar.DAY_OF_YEAR) - lastCal.get(Calendar.DAY_OF_YEAR)
        val diffYears = currCal.get(Calendar.YEAR) - lastCal.get(Calendar.YEAR)

        return (diffYears == 0 && diffDays > 1) || (diffYears > 0 && !(diffYears == 1 && diffDays < 0))
    }

    /**
     * Checks if a lost streak can be repaired (within 24 hours of deadline expiration).
     */
    fun canRepairStreak(missedTimestamp: Long, currentTimestamp: Long = System.currentTimeMillis()): Boolean {
        if (missedTimestamp <= 0L) return false
        val window24h = 24L * 60L * 60L * 1000L
        return (currentTimestamp - missedTimestamp) in 0L..window24h
    }

    /**
     * Calculates time remaining until 23:59:59 in the user's local timezone.
     * Returns a pair of (hours, minutes).
     */
    fun getTimeUntilMidnight(currentTimestamp: Long = System.currentTimeMillis()): Pair<Int, Int> {
        val now = Calendar.getInstance().apply { timeInMillis = currentTimestamp }
        val midnight = Calendar.getInstance().apply {
            timeInMillis = currentTimestamp
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }

        val diffMs = (midnight.timeInMillis - now.timeInMillis).coerceAtLeast(0L)
        val hours = (diffMs / (1000L * 60L * 60L)).toInt()
        val minutes = ((diffMs % (1000L * 60L * 60L)) / (1000L * 60L)).toInt()
        return Pair(hours, minutes)
    }

    companion object {
        val instance by lazy { StreakManager() }
    }
}
