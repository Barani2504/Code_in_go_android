package com.simats.codeingo

import com.simats.codeingo.data.model.allPhoenixEmotions
import com.simats.codeingo.data.model.phoenixEmotion
import com.simats.codeingo.domain.scoring.ArenaScoring
import com.simats.codeingo.ui.arenas.crypto.ArenaCryptoEngine
import com.simats.codeingo.ui.visualizer.engine.DesEngine
import com.simats.codeingo.ui.visualizer.engine.RsaEngine
import com.simats.codeingo.ui.visualizer.engine.Sha256Engine
import com.simats.codeingo.ui.visualizer.engine.TlsEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigInteger

class CrashRecoveryAndStabilityTest {

    @Test
    fun testPhoenixEmotionSafeLookupWithInvalidId() {
        // ID -999 does not exist, must safely fall back without throwing NoSuchElementException
        val fallback = phoenixEmotion(-999)
        assertNotNull(fallback)
        assertEquals(allPhoenixEmotions.first().id, fallback.id)
    }

    @Test
    fun testDiffieHellmanCoerceInBoundarySafety() {
        // For small primes p = 2 or p = 3, coerceAtLeast(2) prevents IllegalArgumentException
        val p = 3L
        val maxSecret = (p - 2).coerceAtLeast(2).toInt()
        assertEquals(2, maxSecret)

        val coerced = 5.coerceIn(2, maxSecret)
        assertEquals(2, coerced)
    }

    @Test
    fun testRsaEngineInvalidModulusAndExponents() {
        // Zero or negative modulus should not throw ArithmeticException in testMalleability
        val result = RsaEngine.testMalleability(
            m1Val = 5L,
            m2Val = 7L,
            e = BigInteger.ZERO,
            d = BigInteger.valueOf(-1),
            n = BigInteger.ZERO
        )
        assertNotNull(result)
        assertFalse(result.homomorphicPropertyHolds)

        // Invalid signature parameters should not throw
        val sigResult = RsaEngine.demonstrateSignature(
            message = "Test",
            e = BigInteger.ZERO,
            d = BigInteger.valueOf(-1),
            n = BigInteger.ONE
        )
        assertNotNull(sigResult)
        assertFalse(sigResult.isValid)
    }

    @Test
    fun testTlsEngineEmptyCertificateChain() {
        val validation = TlsEngine.validateCertificateChain(emptyList(), "example.com")
        assertNotNull(validation)
        assertFalse(validation.isValid)
        assertTrue(validation.failureReason.contains("Empty certificate chain"))
    }

    @Test
    fun testDesEngineAndSha256TraceRobustness() {
        val desTrace = DesEngine.encrypt(0L, 0L)
        assertNotNull(desTrace)
        assertTrue(desTrace.rounds.isNotEmpty())
        val firstRound = desTrace.rounds.firstOrNull()
        assertNotNull(firstRound)

        val shaTrace = Sha256Engine.hashWithTrace(byteArrayOf())
        assertNotNull(shaTrace)
        assertTrue(shaTrace.blocks.isNotEmpty())
        val firstBlock = shaTrace.blocks.firstOrNull()
        assertNotNull(firstBlock)
        assertTrue(firstBlock!!.steps.isNotEmpty())
    }

    @Test
    fun testArenaScoringMultiplierProgression() {
        assertEquals(1.0f, ArenaScoring.calculateMultiplier(0), 0.001f)
        assertEquals(1.0f, ArenaScoring.calculateMultiplier(2), 0.001f)
        assertEquals(1.5f, ArenaScoring.calculateMultiplier(3), 0.001f)
        assertEquals(2.0f, ArenaScoring.calculateMultiplier(5), 0.001f)
        assertEquals(3.0f, ArenaScoring.calculateMultiplier(8), 0.001f)

        // Point calculation
        assertEquals(100, ArenaScoring.calculateEarnedPoints(100, 1))
        assertEquals(150, ArenaScoring.calculateEarnedPoints(100, 3))
        assertEquals(200, ArenaScoring.calculateEarnedPoints(100, 5))
        assertEquals(300, ArenaScoring.calculateEarnedPoints(100, 8))

        // Star evaluation
        assertEquals(3, ArenaScoring.evaluateStars(comboCount = 5, failedAttempts = 0))
        assertEquals(2, ArenaScoring.evaluateStars(comboCount = 1, failedAttempts = 0))
        assertEquals(1, ArenaScoring.evaluateStars(comboCount = 5, failedAttempts = 3))
    }

    @Test
    fun testHammingDistanceEdgeCases() {
        val zeroArray = ByteArray(32) { 0 }
        val allOnes = ByteArray(32) { 0xFF.toByte() }
        val distance = ArenaCryptoEngine.hammingBitDistance(zeroArray, allOnes)
        assertEquals(256, distance)

        val zeroDistance = ArenaCryptoEngine.hammingBitDistance(zeroArray, zeroArray)
        assertEquals(0, zeroDistance)
    }
}
