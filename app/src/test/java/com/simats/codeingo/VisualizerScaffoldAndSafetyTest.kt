package com.simats.codeingo

import com.simats.codeingo.ui.theme.AshTypography
import com.simats.codeingo.ui.visualizer.engine.SafeCryptoEngine
import com.simats.codeingo.ui.visualizer.scaffold.CnsVisualizerRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class VisualizerScaffoldAndSafetyTest {

    @Test
    fun testAll25VisualizerSpecsMeetFourBeatAndWordCountRules() {
        val specs = CnsVisualizerRegistry.allSpecs
        assertEquals("Must have 25 visualizer specs in registry", 25, specs.size)

        for (spec in specs) {
            // Story Beat check: One-sentence hook, clear real-life analogy
            assertTrue("One-sentence hook must not be blank for ${spec.id}", spec.story.oneSentenceHook.isNotBlank())
            assertTrue("Real life analogy must not be blank for ${spec.id}", spec.story.realLifeAnalogy.isNotBlank())
            assertTrue("Promise must not be blank for ${spec.id}", spec.oneSentencePromise.isNotBlank())

            // Watch Beat check: <= 5 steps, each narration <= 15 words
            assertTrue("Watch steps must be between 1 and 5 for ${spec.id}, was ${spec.watchSteps.size}", spec.watchSteps.size in 1..5)
            for (step in spec.watchSteps) {
                val words = step.narration.split(Regex("\\s+")).filter { it.isNotBlank() }
                assertTrue(
                    "Watch narration must be <= 15 words for ${spec.id} step ${step.stepIndex}, was ${words.size}: '${step.narration}'",
                    words.size <= 15
                )
                assertTrue("What changed description must not be blank", step.whatChanged.isNotBlank())
            }

            // Check Beat check: 1 question, >= 2 options, valid correctIndex
            assertTrue("Check question prompt must not be blank", spec.checkQuestion.prompt.isNotBlank())
            assertTrue("Check question options must be >= 2", spec.checkQuestion.options.size >= 2)
            assertTrue("correctIndex must be in range", spec.checkQuestion.correctIndex in spec.checkQuestion.options.indices)
            assertTrue("Check question explanation must not be blank", spec.checkQuestion.explanation.isNotBlank())

            // "I'm lost" alternate analogy check
            assertTrue("Lost help headline must not be blank for ${spec.id}", spec.lostHelp.simpleHeadline.isNotBlank())
            assertTrue("Lost help alternate analogy must not be blank for ${spec.id}", spec.lostHelp.simpleAnalogy.isNotBlank())
            assertTrue("Lost help takeaway must not be blank for ${spec.id}", spec.lostHelp.takeawayBullet.isNotBlank())
        }
    }

    @Test
    fun testTypographyConstraints_bodyAtLeast15sp_captionsAtLeast12sp() {
        val typo = AshTypography()

        // Body sizes must be >= 15 sp
        assertTrue("bodyLarge must be >= 15sp", typo.bodyLarge.fontSize.value >= 15f)
        assertTrue("bodyMedium must be >= 15sp", typo.bodyMedium.fontSize.value >= 15f)

        // Labels & captions must be >= 12 sp
        assertTrue("labelLarge must be >= 12sp", typo.labelLarge.fontSize.value >= 12f)
        assertTrue("labelMedium must be >= 12sp", typo.labelMedium.fontSize.value >= 12f)
        assertTrue("labelSmall must be >= 12sp", typo.labelSmall.fontSize.value >= 12f)
    }

    @Test
    fun testSafeCryptoEngine_handlesInvalidAndRandomInputsWithoutCrashing() {
        // 1. Caesar random & boundary fuzzing
        val random = Random(42)
        val weirdStrings = listOf("", "   ", "!@#\$%^&*()_+{}[]:;\"'<>?,./~`", "1234567890", "🔥🎉🚀🔐", "a".repeat(1000))
        val weirdShifts = listOf(0, -1, -26, -9999, 26, 27, 10000, Int.MAX_VALUE, Int.MIN_VALUE)

        for (s in weirdStrings) {
            for (shift in weirdShifts) {
                val res = SafeCryptoEngine.safeCaesar(s, shift)
                assertTrue("Caesar must always succeed on input: '$s' shift: $shift", res.isSuccess)
            }
        }

        // 2. Modulo zero-division safety
        val zeroMod = SafeCryptoEngine.safeModulo(10, 0)
        assertTrue("Modulo with 0 must succeed with safe fallback without throwing ArithmeticException", zeroMod.isSuccess)

        val negMod = SafeCryptoEngine.safeModulo(-17, -5)
        assertTrue("Modulo with negative values must succeed", negMod.isSuccess)

        // 3. Tiny RSA with invalid inputs
        val rsa1 = SafeCryptoEngine.safeRsaTiny(pInput = 0, qInput = 0, eInput = 0, messageInput = -1)
        assertTrue("RSA must clamp invalid inputs safely", rsa1.isSuccess)
        val rsaResult = rsa1.getOrThrow()
        assertEquals("Decrypted message must equal original clamped message", rsaResult.message, rsaResult.decrypted)

        // 4. Diffie-Hellman with random inputs
        for (i in 1..20) {
            val p = random.nextLong(1, 200)
            val g = random.nextLong(1, 200)
            val a = random.nextLong(1, 200)
            val b = random.nextLong(1, 200)
            val dhRes = SafeCryptoEngine.safeDiffieHellman(p, g, a, b)
            assertTrue("DH must succeed for p=$p g=$g a=$a b=$b", dhRes.isSuccess)
            val dh = dhRes.getOrThrow()
            assertTrue("Shared secret must be positive", dh.sharedSecret >= 0)
        }

        // 5. SHA-256 with empty and large strings
        val shaEmpty = SafeCryptoEngine.safeSha256("")
        assertTrue(shaEmpty.isSuccess)
        assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", shaEmpty.getOrThrow())

        val shaLarge = SafeCryptoEngine.safeSha256("A".repeat(5000))
        assertTrue(shaLarge.isSuccess)
        assertEquals(64, shaLarge.getOrThrow().length)
    }

    @Test
    fun testReversibleStepForwardThenBackReturnsIdenticalState() {
        // Caesar: Step forward K=7, step back K=-7
        val originalText = "THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG"
        val encrypted = SafeCryptoEngine.safeCaesar(originalText, 7).getOrThrow().ciphertext
        val decrypted = SafeCryptoEngine.safeCaesar(encrypted, -7).getOrThrow().ciphertext
        assertEquals("Step forward then step back must return identical plaintext", originalText, decrypted)

        // RSA: Encrypt M=15 with public key, decrypt with private key
        val rsa = SafeCryptoEngine.safeRsaTiny(pInput = 5, qInput = 11, eInput = 3, messageInput = 15).getOrThrow()
        assertEquals("RSA forward then backward must restore identical state", 15L, rsa.decrypted)

        // Modulo: Step forward +3 mod 7, step back -3 mod 7
        val valStart = 4L
        val mod = 7L
        val stepForward = SafeCryptoEngine.safeModulo(valStart + 3, mod).getOrThrow().remainder
        val stepBack = SafeCryptoEngine.safeModulo(stepForward - 3, mod).getOrThrow().remainder
        assertEquals("Modulo step forward and backward must return original remainder", valStart, stepBack)
    }
}
