package com.simats.codeingo.domain.scoring

/**
 * Pure, centralized arena scoring engine.
 * Computes combo progression, multipliers, point increments, and star evaluations.
 */
object ArenaScoring {

    fun calculateMultiplier(comboCount: Int): Float = when {
        comboCount >= 8 -> 3.0f
        comboCount >= 5 -> 2.0f
        comboCount >= 3 -> 1.5f
        else -> 1.0f
    }

    fun calculateEarnedPoints(basePoints: Int, comboCount: Int): Int {
        val multiplier = calculateMultiplier(comboCount)
        return (basePoints * multiplier).toInt()
    }

    fun evaluateStars(comboCount: Int, failedAttempts: Int = 0): Int {
        return when {
            failedAttempts == 0 && comboCount >= 3 -> 3
            failedAttempts <= 1 -> 2
            else -> 1
        }
    }
}
