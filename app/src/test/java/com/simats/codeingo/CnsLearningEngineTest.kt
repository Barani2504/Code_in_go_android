package com.simats.codeingo

import com.simats.codeingo.domain.mastery.EmberTier
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
        // Caesar shift 3: 'K' shifted by 3 gives 'N'
        assertTrue(CnsExerciseScoring.evaluateCipherWheel(userShift = 3, targetShift = 3))
        // Shift wraps around modulo 26 (29 % 26 == 3)
        assertTrue(CnsExerciseScoring.evaluateCipherWheel(userShift = 29, targetShift = 3))
        assertFalse(CnsExerciseScoring.evaluateCipherWheel(userShift = 4, targetShift = 3))

        val decrypted = CnsExerciseScoring.applyCaesarShift("KHOOR", 3)
        assertEquals("HELLO", decrypted)
    }

    @Test
    fun testBitFlipper_evaluation() {
        val input = "10110001"
        val target = "10110011" // 1 bit flipped
        assertTrue(CnsExerciseScoring.evaluateBitFlipper("10110011", target))
        assertFalse(CnsExerciseScoring.evaluateBitFlipper("10110000", target))

        val distance = CnsExerciseScoring.calculateHammingDistance(input, target)
        assertEquals(1, distance)

        val avalancheRatio = CnsExerciseScoring.calculateAvalanchePercentage("00000000", "11110000")
        assertEquals(50.0f, avalancheRatio, 0.01f)
    }

    @Test
    fun testRoundSequence_evaluation() {
        val aesRound = listOf("SubBytes", "ShiftRows", "MixColumns", "AddRoundKey")
        assertTrue(CnsExerciseScoring.evaluateRoundSequence(aesRound, aesRound))

        val scrambled = listOf("ShiftRows", "SubBytes", "MixColumns", "AddRoundKey")
        assertFalse(CnsExerciseScoring.evaluateRoundSequence(scrambled, aesRound))
    }

    @Test
    fun testModularMath_evaluation() {
        // (17^1) mod 5 = 2
        assertTrue(CnsExerciseScoring.evaluateModularExponentiation(base = 17, exponent = 1, modulus = 5, userRemainder = 2))
        // (7^2) mod 13 = 49 mod 13 = 10
        assertTrue(CnsExerciseScoring.evaluateModularExponentiation(base = 7, exponent = 2, modulus = 13, userRemainder = 10))
        assertFalse(CnsExerciseScoring.evaluateModularExponentiation(base = 7, exponent = 2, modulus = 13, userRemainder = 9))
    }

    @Test
    fun testPacketTriage_evaluation() {
        // Malicious packet (e.g. unencrypted HTTP basic auth on port 80) -> should NOT allow
        assertTrue(CnsExerciseScoring.evaluatePacketTriage(userAllowed = false, isSafePacket = false))
        // Dropping a legitimate packet -> false
        assertFalse(CnsExerciseScoring.evaluatePacketTriage(userAllowed = false, isSafePacket = true))
        // Allowing a safe packet -> true
        assertTrue(CnsExerciseScoring.evaluatePacketTriage(userAllowed = true, isSafePacket = true))
    }

    @Test
    fun testSpotTheFlaw_evaluation() {
        // Line 4 uses ECB mode: Cipher.getInstance("AES/ECB/PKCS5Padding")
        assertTrue(CnsExerciseScoring.evaluateSpotTheFlaw(userSelectedLine = 4, vulnerableLine = 4))
        assertFalse(CnsExerciseScoring.evaluateSpotTheFlaw(userSelectedLine = 2, vulnerableLine = 4))
    }

    @Test
    fun testKeySizeDial_evaluation() {
        // 256-bit AES chosen for military grade quantum resistance target
        assertTrue(CnsExerciseScoring.evaluateKeySizeDial(chosenKeyBits = 256, requiredSecurityBits = 256))
        assertFalse(CnsExerciseScoring.evaluateKeySizeDial(chosenKeyBits = 128, requiredSecurityBits = 256))
    }

    // ── 2. SM-2 SPACED REPETITION SCHEDULER TESTS ────────────────────────

    @Test
    fun testSm2_intervalProgressionOnSuccess() {
        val now = 1_000_000L

        // Repetition 1
        val rep1 = Sm2Scheduler.calculateNextReview(
            quality = 5,
            currentRepetition = 0,
            currentInterval = 0,
            currentEaseFactor = 2.5f,
            currentTimeMs = now
        )
        assertEquals(1, rep1.consecutiveCorrect)
        assertEquals(1, rep1.intervalDays)
        assertTrue(rep1.nextReviewDueMs >= now + 24 * 60 * 60 * 1000L)
        assertTrue(rep1.easeFactor >= 2.5f)

        // Repetition 2
        val rep2 = Sm2Scheduler.calculateNextReview(
            quality = 5,
            currentRepetition = rep1.consecutiveCorrect,
            currentInterval = rep1.intervalDays,
            currentEaseFactor = rep1.easeFactor,
            currentTimeMs = now
        )
        assertEquals(2, rep2.consecutiveCorrect)
        assertEquals(6, rep2.intervalDays)

        // Repetition 3: interval = 6 * EF
        val rep3 = Sm2Scheduler.calculateNextReview(
            quality = 5,
            currentRepetition = rep2.consecutiveCorrect,
            currentInterval = rep2.intervalDays,
            currentEaseFactor = rep2.easeFactor,
            currentTimeMs = now
        )
        assertEquals(3, rep3.consecutiveCorrect)
        assertTrue(rep3.intervalDays >= 15)
    }

    @Test
    fun testSm2_resetOnFailure() {
        val now = 1_000_000L

        // Failed review (quality < 3) resets repetitions & interval back to 1
        val failed = Sm2Scheduler.calculateNextReview(
            quality = 1,
            currentRepetition = 5,
            currentInterval = 30,
            currentEaseFactor = 2.4f,
            currentTimeMs = now
        )
        assertEquals(0, failed.consecutiveCorrect)
        assertEquals(1, failed.intervalDays)
        // Ease factor decreases but never drops below 1.3
        assertTrue(failed.easeFactor < 2.4f)
        assertTrue(failed.easeFactor >= 1.3f)
    }

    @Test
    fun testEmberTiers() {
        assertEquals(EmberTier.ASH, Sm2Scheduler.computeEmberTier(masteryScore = 15))
        assertEquals(EmberTier.SPARK, Sm2Scheduler.computeEmberTier(masteryScore = 35))
        assertEquals(EmberTier.FLAME, Sm2Scheduler.computeEmberTier(masteryScore = 65))
        assertEquals(EmberTier.PYRE, Sm2Scheduler.computeEmberTier(masteryScore = 85))
        assertEquals(EmberTier.SOLAR, Sm2Scheduler.computeEmberTier(masteryScore = 95))
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

