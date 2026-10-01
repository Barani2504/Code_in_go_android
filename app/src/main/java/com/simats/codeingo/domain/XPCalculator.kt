package com.simats.codeingo.domain

object XPCalculator {
    fun calculateLessonXP(
        baseXP: Int,
        accuracyPercentage: Double,
        speedSeconds: Int,
        currentCombo: Int
    ): Int {
        var earned = baseXP
        if (accuracyPercentage >= 1.0) {
            earned += 10
        } else if (accuracyPercentage >= 0.8) {
            earned += 5
        }

        if (speedSeconds < 45) {
            earned += 5
        } else if (speedSeconds < 90) {
            earned += 2
        }

        if (currentCombo >= 5) {
            earned += 5
        }

        return earned
    }
}
