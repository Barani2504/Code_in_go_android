package com.simats.codeingo

import com.simats.codeingo.data.model.ModernCryptoRegistry
import com.simats.codeingo.ui.visualizer.components.calculateHammingDistance
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests verifying Phase 4: CNS-Specific Superpowers
 */
class Phase4CnsSuperpowersTest {

    @Test
    fun testHammingDistance_identicalHex() {
        val hexA = "0x12345678"
        val hexB = "0x12345678"
        val distance = calculateHammingDistance(hexA, hexB)
        assertEquals(0, distance)
    }

    @Test
    fun testHammingDistance_singleBitFlip() {
        // 0x00 = 0000 0000, 0x01 = 0000 0001 (1 bit difference)
        val hexA = "0x00"
        val hexB = "0x01"
        val distance = calculateHammingDistance(hexA, hexB)
        assertEquals(1, distance)
    }

    @Test
    fun testHammingDistance_fullByteFlip() {
        // 0x00 vs 0xFF (8 bit difference)
        val hexA = "0x00"
        val hexB = "0xFF"
        val distance = calculateHammingDistance(hexA, hexB)
        assertEquals(8, distance)
    }

    @Test
    fun testHammingDistance_avalancheEffectApproximation() {
        // 64-bit DES output simulation: ~50% bit flip is 32 bits
        val hexA = "0x123456789ABCDEF0"
        val hexB = "0xEDCBA9876543210F"
        val distance = calculateHammingDistance(hexA, hexB)
        val percentage = (distance.toFloat() / 64f) * 100f
        assertTrue("Avalanche effect should produce high diffusion (> 40%)", percentage > 40f)
    }

    @Test
    fun testModernCryptoRegistry_containsPqcAndZkp() {
        val topics = ModernCryptoRegistry.topics
        assertTrue(topics.size >= 4)

        val kyber = topics.firstOrNull { it.id == "pqc_crystals_kyber" }
        assertTrue("Crystals-Kyber should be registered", kyber != null)
        assertTrue("Kyber should be tagged Post-Quantum", kyber?.tag == "Post-Quantum")

        val dilithium = topics.firstOrNull { it.id == "pqc_crystals_dilithium" }
        assertTrue("Crystals-Dilithium should be registered", dilithium != null)

        val zkp = topics.firstOrNull { it.id == "zero_knowledge_proofs" }
        assertTrue("ZKP should be registered", zkp != null)

        val passkeys = topics.firstOrNull { it.id == "passkeys_webauthn" }
        assertTrue("Passkeys should be registered", passkeys != null)
    }

    @Test
    fun testDailyCipher_wordleFeedbackLogic() {
        val target = "PHOENIX"
        val guess = "PEANUTS"

        val feedback = guess.mapIndexed { i, c ->
            if (c == target[i]) 'G' // Green
            else if (target.contains(c)) 'Y' // Yellow
            else 'B' // Black/Gray
        }

        assertEquals('G', feedback[0]) // 'P' is in position 0
        assertEquals('Y', feedback[1]) // 'E' is in target at position 3
        assertEquals('B', feedback[2]) // 'A' is not in target
        assertEquals('Y', feedback[3]) // 'N' is in target at position 4
        assertEquals('B', feedback[4]) // 'U' is not in target
        assertEquals('B', feedback[5]) // 'T' is not in target
        assertEquals('B', feedback[6]) // 'S' is not in target
    }

    @Test
    fun testCaesarShift_wrappingModulo26() {
        val input = "XYZ"
        val shift = 3
        val output = input.map { char ->
            val shifted = (char.code - 'A'.code + shift).mod(26)
            ('A'.code + shifted).toChar()
        }.joinToString("")

        assertEquals("ABC", output)
    }
}

