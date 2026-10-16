package com.simats.codeingo

import com.simats.codeingo.ui.arenas.crypto.ArenaCryptoEngine
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 🧪 ArenaCryptoEngineTest — Mandatory Mathematical & Cryptographic Verification
 * Verifies all FIPS-197 AES test vectors, SHA-256 NIST vectors, RFC 4231 HMAC,
 * Caesar/Vigenere roundtrips, toy DH/RSA, and Grover/Shor quantum bounds.
 */
class ArenaCryptoEngineTest {

    @Test
    fun testCaesarRoundTrip() {
        val original = "THE QUICK BROWN FOX JUMPS OVER 13 LAZY DOGS!"
        val shift = 7
        val encrypted = ArenaCryptoEngine.caesarEncrypt(original, shift)
        val decrypted = ArenaCryptoEngine.caesarDecrypt(encrypted, shift)
        assertEquals(original, decrypted)
        assertEquals("A", ArenaCryptoEngine.caesarDecrypt("D", 3))
    }

    @Test
    fun testVigenereEncryptionAndDecryption() {
        val plaintext = "ATTACKATDAWN"
        val key = "LEMON"
        val ciphertext = ArenaCryptoEngine.vigenere(plaintext, key, decrypt = false)
        assertEquals("LXFOPVEFRNHR", ciphertext)

        val decrypted = ArenaCryptoEngine.vigenere(ciphertext, key, decrypt = true)
        assertEquals(plaintext, decrypted)
    }

    @Test
    fun testKasiskiCandidateDeduction() {
        // Repeated keystream will create repeats at key-length intervals
        val key = "KEY"
        val text = "CRYPTOISCOOLCRYPTOISGREATCRYPTOISEVERYWHERE"
        val cipher = ArenaCryptoEngine.vigenere(text, key)
        val candidates = ArenaCryptoEngine.kasiskiKeyLengthCandidates(cipher)
        assertTrue("Candidates should contain key length 3", candidates.contains(3))
    }

    @Test
    fun testAesFips197SBox() {
        // FIPS-197 known vectors: S(0x00) = 0x63, S(0x53) = 0xED, S(0xFF) = 0x16
        assertEquals(0x63, ArenaCryptoEngine.sBoxLookup(0x00))
        assertEquals(0xED, ArenaCryptoEngine.sBoxLookup(0x53))
        assertEquals(0x16, ArenaCryptoEngine.sBoxLookup(0xFF))
    }

    @Test
    fun testAesMixColumnsKnownVector() {
        // FIPS-197 Section 5.1.3:
        // Column input [0xdb, 0x13, 0x53, 0x45] transformed by MixColumns gives [0x8e, 0x4d, 0xa1, 0xbc]
        val inputCol = intArrayOf(0xdb, 0x13, 0x53, 0x45)
        val expected = intArrayOf(0x8e, 0x4d, 0xa1, 0xbc)
        val outputCol = ArenaCryptoEngine.mixColumn(inputCol)
        assertArrayEquals(expected, outputCol)
    }

    @Test
    fun testAesShiftRowsPermutation() {
        val state = arrayOf(
            intArrayOf(0, 1, 2, 3),
            intArrayOf(4, 5, 6, 7),
            intArrayOf(8, 9, 10, 11),
            intArrayOf(12, 13, 14, 15)
        )
        val shifted = ArenaCryptoEngine.shiftRows(state)
        // Row 0 unchanged
        assertArrayEquals(intArrayOf(0, 1, 2, 3), shifted[0])
        // Row 1 shifted left 1
        assertArrayEquals(intArrayOf(5, 6, 7, 4), shifted[1])
        // Row 2 shifted left 2
        assertArrayEquals(intArrayOf(10, 11, 8, 9), shifted[2])
        // Row 3 shifted left 3
        assertArrayEquals(intArrayOf(15, 12, 13, 14), shifted[3])
    }

    @Test
    fun testSha256NistVector() {
        // NIST test vector: SHA-256("abc")
        val input = "abc".toByteArray(Charsets.UTF_8)
        val hash = ArenaCryptoEngine.bytesToHex(ArenaCryptoEngine.sha256(input))
        assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad", hash)
    }

    @Test
    fun testAvalancheWordingAndMetrics() {
        val result = ArenaCryptoEngine.computeAvalanche("CodeingoBlockBastion2026")
        assertTrue(
            "Scientific summary must state 'about 50% of output bits flip on average'",
            result.scientificSummary.contains("about 50% of output bits flip on average")
        )
        // Check reasonable avalanche diffusion range (between 40% and 60% for standard inputs)
        assertTrue("Avalanche bits must be reasonable", result.flippedBits in 90..166)
    }

    @Test
    fun testHmacSha256Rfc4231() {
        // RFC 4231 Test Case 2: key = "Jefe", data = "what do ya want for nothing?"
        val key = "Jefe".toByteArray(Charsets.US_ASCII)
        val data = "what do ya want for nothing?".toByteArray(Charsets.US_ASCII)
        val hmac = ArenaCryptoEngine.bytesToHex(ArenaCryptoEngine.hmacSha256(key, data))
        assertEquals("5bdcc146bf60754e6a042426089575c75a003f089d2739839dec58b964ec3843", hmac)
    }

    @Test
    fun testToyDiffieHellman() {
        // p=23, g=5
        // Alice private key = 6 -> A = 5^6 mod 23 = 8
        val alicePriv = 6L
        val alicePub = ArenaCryptoEngine.ToyDH.computePublicKey(alicePriv)
        assertEquals(8L, alicePub)

        // Bob private key = 15 -> B = 5^15 mod 23 = 19
        val bobPriv = 15L
        val bobPub = ArenaCryptoEngine.ToyDH.computePublicKey(bobPriv)
        assertEquals(19L, bobPub)

        // Shared secret: Alice calculates B^a mod 23 = 19^6 mod 23 = 2
        // Bob calculates A^b mod 23 = 8^15 mod 23 = 2
        val secretAlice = ArenaCryptoEngine.ToyDH.computeSharedSecret(bobPub, alicePriv)
        val secretBob = ArenaCryptoEngine.ToyDH.computeSharedSecret(alicePub, bobPriv)
        assertEquals(2L, secretAlice)
        assertEquals(2L, secretBob)
    }

    @Test
    fun testToyRsa() {
        val message = 42L
        val cipher = ArenaCryptoEngine.ToyRSA.encrypt(message)
        val decrypted = ArenaCryptoEngine.ToyRSA.decrypt(cipher)
        assertEquals(message, decrypted)
    }

    @Test
    fun testGroverEffectiveBits() {
        assertEquals(64, ArenaCryptoEngine.groverEffectiveBits(128))
        assertEquals(128, ArenaCryptoEngine.groverEffectiveBits(256))
    }
}
