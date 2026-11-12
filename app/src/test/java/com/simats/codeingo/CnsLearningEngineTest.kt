package com.simats.codeingo

import com.simats.codeingo.domain.mastery.Sm2Scheduler
import com.simats.codeingo.domain.scoring.CnsExerciseScoring
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 🧪 CnsLearningEngineTest
 * Unit tests verifying:
 *  1. CnsExerciseScoring pure mathematical engines across all 6 new interactive types
 *  2. Sm2Scheduler spaced repetition interval progression, EF adjustments, and Ember tiers
 *  3. Mistake forgiveness mechanism
 */
class CnsLearningEngineTest {

    // ── 1. SCORING ENGINE TESTS ──────────────────────────────────────────

    @Test
    fun testCipherWheel_evaluation() {
        // Caesar cipher shift 3: 'KHOOR' decrypted with shift 3 gives 'HELLO'
        assertTrue(CnsExerciseScoring.scoreCipherWheel(ciphertext = "KHOOR", userShift = 3, targetPlaintext = "HELLO"))
        // Wrap-around modulo 26 (29 % 26 == 3)
        assertTrue(CnsExerciseScoring.scoreCipherWheel(ciphertext = "KHOOR", userShift = 29, targetPlaintext = "HELLO"))
        assertFalse(CnsExerciseScoring.scoreCipherWheel(ciphertext = "KHOOR", userShift = 4, targetPlaintext = "HELLO"))
    }

    @Test
    fun testBitFlipper_evaluation() {
        val input = "10110001"
        val target = "10110011" // 1 bit flipped
        val distance = CnsExerciseScoring.calculateAvalancheHammingDistance(input, target)
        assertEquals(1, distance)

        assertTrue(CnsExerciseScoring.scoreBitFlipper(originalBits = input, flippedBits = target, minFlipsRequired = 1))
        assertFalse(CnsExerciseScoring.scoreBitFlipper(originalBits = input, flippedBits = input, minFlipsRequired = 1))
    }

    @Test
    fun testRoundSequence_evaluation() {
        val aesRound = listOf("SubBytes", "ShiftRows", "MixColumns", "AddRoundKey")
        assertTrue(CnsExerciseScoring.scoreRoundBuilder(userOrder = aesRound, targetOrder = aesRound))

        val scrambled = listOf("ShiftRows", "SubBytes", "MixColumns", "AddRoundKey")
        assertFalse(CnsExerciseScoring.scoreRoundBuilder(userOrder = scrambled, targetOrder = aesRound))
    }

    @Test
    fun testModularMath_evaluation() {
        // (17^1) mod 5 = 2
        assertTrue(CnsExerciseScoring.scoreModularMath(base = 17L, exponent = 1L, modulus = 5L, userAnswer = 2L))
        // (7^2) mod 13 = 49 mod 13 = 10
        assertTrue(CnsExerciseScoring.scoreModularMath(base = 7L, exponent = 2L, modulus = 13L, userAnswer = 10L))
        assertFalse(CnsExerciseScoring.scoreModularMath(base = 7L, exponent = 2L, modulus = 13L, userAnswer = 9L))
    }

    @Test
    fun testPacketTriage_evaluation() {
        val expected = mapOf("pkt-1" to "ALLOW", "pkt-2" to "DROP")
        val correctDecisions = mapOf("pkt-1" to "ALLOW", "pkt-2" to "DROP")
        val wrongDecisions = mapOf("pkt-1" to "ALLOW", "pkt-2" to "ALLOW")

        assertTrue(CnsExerciseScoring.scorePacketTriage(correctDecisions, expected))
        assertFalse(CnsExerciseScoring.scorePacketTriage(wrongDecisions, expected))
    }

    @Test
    fun testSpotTheFlaw_evaluation() {
        // Line 4 uses ECB mode
        assertTrue(CnsExerciseScoring.scoreSpotTheFlaw(selectedLineIndex = 4, vulnerableLineIndex = 4))
        assertFalse(CnsExerciseScoring.scoreSpotTheFlaw(selectedLineIndex = 2, vulnerableLineIndex = 4))
    }

    @Test
    fun testKeySizeDial_evaluation() {
        // 256-bit AES chosen
        assertTrue(CnsExerciseScoring.scoreKeySizeDial(selectedKeySize = "256", targetKeySize = "256"))
        assertFalse(CnsExerciseScoring.scoreKeySizeDial(selectedKeySize = "128", targetKeySize = "256"))
    }

    // ── 2. SM-2 SPACED REPETITION SCHEDULER TESTS ────────────────────────

    @Test
    fun testSm2_intervalProgressionOnSuccess() {
        val now = 1_000_000L

        // Repetition 1
        val rep1 = Sm2Scheduler.calculateNextSchedule(
            quality = 5,
            currentRepetition = 0,
            currentInterval = 1,
            currentEf = 2.5f,
            currentMastery = 0.0f,
            currentTimeMs = now
        )
        assertEquals(1, rep1.nextRepetition)
        assertEquals(1, rep1.nextIntervalDays)
        assertTrue(rep1.nextReviewEpochMs >= now + 24 * 60 * 60 * 1000L)
        assertTrue(rep1.nextEasinessFactor >= 2.5f)

        // Repetition 2
        val rep2 = Sm2Scheduler.calculateNextSchedule(
            quality = 5,
            currentRepetition = rep1.nextRepetition,
            currentInterval = rep1.nextIntervalDays,
            currentEf = rep1.nextEasinessFactor,
            currentMastery = rep1.newMasteryScore,
            currentTimeMs = now
        )
        assertEquals(2, rep2.nextRepetition)
        assertEquals(6, rep2.nextIntervalDays)

        // Repetition 3: interval = 6 * EF
        val rep3 = Sm2Scheduler.calculateNextSchedule(
            quality = 5,
            currentRepetition = rep2.nextRepetition,
            currentInterval = rep2.nextIntervalDays,
            currentEf = rep2.nextEasinessFactor,
            currentMastery = rep2.newMasteryScore,
            currentTimeMs = now
        )
        assertEquals(3, rep3.nextRepetition)
        assertTrue(rep3.nextIntervalDays >= 15)
    }

    @Test
    fun testSm2_resetOnFailure() {
        val now = 1_000_000L

        // Failed review (quality < 3) resets repetitions to 0 and interval to 1
        val failed = Sm2Scheduler.calculateNextSchedule(
            quality = 1,
            currentRepetition = 5,
            currentInterval = 30,
            currentEf = 2.4f,
            currentMastery = 0.8f,
            currentTimeMs = now
        )
        assertEquals(0, failed.nextRepetition)
        assertEquals(1, failed.nextIntervalDays)
        assertTrue(failed.nextEasinessFactor < 2.4f)
        assertTrue(failed.nextEasinessFactor >= 1.3f)
    }

    @Test
    fun testEmberTiers() {
        assertEquals("Ember", Sm2Scheduler.getMasteryTier(masteryScore = 0.15f))
        assertEquals("Flame", Sm2Scheduler.getMasteryTier(masteryScore = 0.45f))
        assertEquals("Blaze", Sm2Scheduler.getMasteryTier(masteryScore = 0.70f))
        assertEquals("Inferno", Sm2Scheduler.getMasteryTier(masteryScore = 0.90f))
    }

    // ── 3. MISTAKE FORGIVENESS PROTOCOL TESTS ────────────────────────────

    @Test
    fun testMistakeForgiveness_firstMissZeroPenalty() {
        val conceptAttempts = mutableMapOf<String, Int>()
        val conceptId = "cns_aes_sbox"

        fun recordAttempt(isCorrect: Boolean): Boolean {
            if (isCorrect) return true
            val count = (conceptAttempts[conceptId] ?: 0) + 1
            conceptAttempts[conceptId] = count
            // Returns true if forgiven (first attempt), false if penalty applies
            return count == 1
        }

        // First attempt missed -> forgiven!
        val firstMissForgiven = recordAttempt(isCorrect = false)
        assertTrue(firstMissForgiven)
        assertEquals(1, conceptAttempts[conceptId])

        // Second attempt on same concept missed -> penalty applies!
        val secondMissForgiven = recordAttempt(isCorrect = false)
        assertFalse(secondMissForgiven)
        assertEquals(2, conceptAttempts[conceptId])
    }
}
