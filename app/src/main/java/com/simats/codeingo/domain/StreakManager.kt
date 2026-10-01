package com.simats.codeingo.domain

import java.util.Calendar

class StreakManager {
    fun shouldIncrementStreak(lastActiveTimestamp: Long, currentTimestamp: Long = System.currentTimeMillis()): Boolean {
        if (lastActiveTimestamp <= 0L) return true

        val lastCal = Calendar.getInstance().apply { timeInMillis = lastActiveTimestamp }
        val currCal = Calendar.getInstance().apply { timeInMillis = currentTimestamp }

        val diffDays = currCal.get(Calendar.DAY_OF_YEAR) - lastCal.get(Calendar.DAY_OF_YEAR)
        val diffYears = currCal.get(Calendar.YEAR) - lastCal.get(Calendar.YEAR)

        return (diffYears == 0 && diffDays == 1) || (diffYears == 1 && diffDays < 0)
    }

    fun isStreakLost(lastActiveTimestamp: Long, currentTimestamp: Long = System.currentTimeMillis()): Boolean {
        if (lastActiveTimestamp <= 0L) return false

        val lastCal = Calendar.getInstance().apply { timeInMillis = lastActiveTimestamp }
        val currCal = Calendar.getInstance().apply { timeInMillis = currentTimestamp }

        val diffDays = currCal.get(Calendar.DAY_OF_YEAR) - lastCal.get(Calendar.DAY_OF_YEAR)
        val diffYears = currCal.get(Calendar.YEAR) - lastCal.get(Calendar.YEAR)

        return (diffYears == 0 && diffDays > 1) || (diffYears > 0 && !(diffYears == 1 && diffDays < 0))
    }
}
