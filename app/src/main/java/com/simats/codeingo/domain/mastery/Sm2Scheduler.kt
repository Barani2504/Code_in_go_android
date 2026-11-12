package com.simats.codeingo.domain.mastery

import kotlin.math.max
import kotlin.math.roundToInt

data class Sm2ScheduleResult(
    val nextRepetition: Int,
    val nextIntervalDays: Int,
    val nextEasinessFactor: Float,
    val newMasteryScore: Float,
    val nextReviewEpochMs: Long
)

object Sm2Scheduler {

    private const val ONE_DAY_MS = 24 * 60 * 60 * 1000L

    /**
     * Calculates the next SuperMemo-2 spaced-repetition parameters.
     * @param quality Response quality rating from 0 (complete blackout) to 5 (perfect response).
     * @param currentRepetition Current consecutive successful repetitions count.
     * @param currentInterval Current interval in days.
     * @param currentEf Current easiness factor (minimum 1.3).
     * @param currentMastery Continuous mastery score from 0.0 to 1.0.
     */
    fun calculateNextSchedule(
        quality: Int,
        currentRepetition: Int,
        currentInterval: Int,
        currentEf: Float,
        currentMastery: Float,
        currentTimeMs: Long = System.currentTimeMillis()
    ): Sm2ScheduleResult {
        val q = quality.coerceIn(0, 5)

        // 1. Calculate new Easiness Factor (EF)
        // EF' = EF + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))
        val efDelta = 0.1f - (5 - q) * (0.08f + (5 - q) * 0.02f)
        val newEf = max(1.3f, currentEf + efDelta)

        // 2. Calculate interval and repetition
        val (newRepetition, newInterval) = if (q >= 3) {
            when (currentRepetition) {
                0 -> Pair(1, 1)
                1 -> Pair(2, 6)
                else -> {
                    val nextDays = (currentInterval * newEf).roundToInt().coerceAtLeast(currentInterval + 1)
                    Pair(currentRepetition + 1, nextDays)
                }
            }
        } else {
            // Failure reset: restart intervals
            Pair(0, 1)
        }

        // 3. Elo/Bayesian continuous mastery update
        val targetMastery = if (q >= 3) 1.0f else 0.0f
        val kFactor = if (q >= 3) 0.15f else 0.25f
        val newMastery = (currentMastery + kFactor * (targetMastery - currentMastery)).coerceIn(0.0f, 1.0f)

        val nextReviewMs = currentTimeMs + (newInterval * ONE_DAY_MS)

        return Sm2ScheduleResult(
            nextRepetition = newRepetition,
            nextIntervalDays = newInterval,
            nextEasinessFactor = newEf,
            newMasteryScore = newMastery,
            nextReviewEpochMs = nextReviewMs
        )
    }

    /**
     * Maps continuous mastery score (0.0..1.0) to Phoenix Ember tier.
     */
    fun getMasteryTier(masteryScore: Float): String {
        return when {
            masteryScore >= 0.85f -> "Inferno"
            masteryScore >= 0.60f -> "Blaze"
            masteryScore >= 0.35f -> "Flame"
            else -> "Ember"
        }
    }
}

