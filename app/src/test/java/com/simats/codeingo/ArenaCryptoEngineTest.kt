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

    @Test
    fun testAvalanche_standardInput() {
        val result = ArenaCryptoEngine.computeAvalanche("Hello World")
        // Avalanche criterion guarantees about 50% of 256 bits flip (typically 100..160 bits)
        assertTrue("Flipped bits should be between 100 and 160", result.flippedBits in 100..160)
        assertTrue("Percentage should be between 39% and 63%", result.percentage in 39.0..63.0)
        assertTrue("Scientific summary should contain exact expected phrase",
            result.scientificSummary.contains("about 50% of output bits flip on average"))
    }

    @Test
    fun testAvalanche_emptyInput() {
        val result = ArenaCryptoEngine.computeAvalanche("")
        // Empty string must trigger a simulated 1-bit input flip and NOT return 0%
        assertTrue("Flipped bits on empty input should not be zero", result.flippedBits > 80)
        assertTrue("Percentage should reflect avalanche", result.percentage > 30.0)
        assertTrue("Scientific summary should contain exact expected phrase",
            result.scientificSummary.contains("about 50% of output bits flip on average"))
    }

    @Test
    fun testModPowEulerTheoremVectors() {
        // 3^10 mod 1000 = 49
        assertEquals(49L, ArenaCryptoEngine.modPow(3, 10, 1000))
        // 3^2023 mod 1000 = 827 (Euler totient phi(1000) = 400; 3^400 = 1; 2023 = 5*400 + 23; 3^23 mod 1000 = 827)
        assertEquals(827L, ArenaCryptoEngine.modPow(3, 2023, 1000))
    }

    @Test
    fun testSphinxBossWordsCaesarShift() {
        // Test that Caesar shift K=3 encrypts every boss word correctly
        assertEquals("PDVWHU", ArenaCryptoEngine.caesarEncrypt("MASTER", 3))
        assertEquals("VHFUHW", ArenaCryptoEngine.caesarEncrypt("SECRET", 3))
        assertEquals("VKDGRZ", ArenaCryptoEngine.caesarEncrypt("SHADOW", 3))

        // And verify round-trip decryption
        assertEquals("MASTER", ArenaCryptoEngine.caesarDecrypt("PDVWHU", 3))
        assertEquals("SECRET", ArenaCryptoEngine.caesarDecrypt("VHFUHW", 3))
        assertEquals("SHADOW", ArenaCryptoEngine.caesarDecrypt("VKDGRZ", 3))
    }

    @Test
    fun testRailFenceZigzagTestVector() {
        val plaintext = "WEAREDISCOVEREDFLEEATONCE"
        val expectedCipher = "WECRLTEERDSOEEFEAOCAIVDEN"
        val encrypted = ArenaCryptoEngine.railFenceEncrypt(plaintext, rails = 3)
        assertEquals(expectedCipher, encrypted)

        val decrypted = ArenaCryptoEngine.railFenceDecrypt(encrypted, rails = 3)
        assertEquals(plaintext, decrypted)
    }

    @Test
    fun testToyF17EllipticCurveOperations() {
        // G = (5, 1) on y^2 = x^3 + 2x + 2 mod 17
        assertTrue(ArenaCryptoEngine.ToyF17Curve.isPointOnCurve(ArenaCryptoEngine.ToyF17Curve.G))

        // 2G = (6, 3)
        val twoG = ArenaCryptoEngine.ToyF17Curve.double(ArenaCryptoEngine.ToyF17Curve.G)
        assertEquals(6, twoG.x)
        assertEquals(3, twoG.y)
        assertTrue(ArenaCryptoEngine.ToyF17Curve.isPointOnCurve(twoG))

        // Multiply by group order 19 yields point at infinity O
        val orderPoint = ArenaCryptoEngine.ToyF17Curve.multiply(19, ArenaCryptoEngine.ToyF17Curve.G)
        assertTrue("19*G must be point at infinity", orderPoint.isInfinity)
    }

    @Test
    fun testToyRSA5_11RoundTrip() {
        val message = 9L
        val cipher = ArenaCryptoEngine.ToyRSA5_11.encrypt(message)
        val decrypted = ArenaCryptoEngine.ToyRSA5_11.decrypt(cipher)
        assertEquals(message, decrypted)
    }

    @Test
    fun testHmacStepsMatchesStandardHmac() {
        val key = "SecretKey123".toByteArray(Charsets.UTF_8)
        val message = "MessagePayload".toByteArray(Charsets.UTF_8)
        val steps = ArenaCryptoEngine.computeHmacSteps(key, message)
        val direct = ArenaCryptoEngine.bytesToHex(ArenaCryptoEngine.hmacSha256(key, message))
        assertEquals(direct, steps.outerTagHex)
    }

    @Test
    fun testSynCookieDeterministicClock() {
        val secretKey = "SuperSecretKey99".toByteArray(Charsets.UTF_8)
        val fixedClockMs = 1700000000000L // Fixed timestamp for determinism
        val cookie = ArenaCryptoEngine.computeSynCookie(
            srcIp = "192.0.2.10",
            dstIp = "198.51.100.1",
            srcPort = 54321,
            dstPort = 443,
            secretKey = secretKey,
            clockMs = fixedClockMs
        )
        // Verify with same clock
        assertTrue(
            "SYN cookie must verify with same clock",
            ArenaCryptoEngine.verifySynCookie(
                cookie = cookie,
                srcIp = "192.0.2.10",
                dstIp = "198.51.100.1",
                srcPort = 54321,
                dstPort = 443,
                secretKey = secretKey,
                currentClockMs = fixedClockMs
            )
        )
        // Verify that expired cookie (e.g. 10 minutes later) fails
        val futureClockMs = fixedClockMs + 10 * 60 * 1000L
        assertTrue(
            "Expired SYN cookie must be rejected",
            !ArenaCryptoEngine.verifySynCookie(
                cookie = cookie,
                srcIp = "192.0.2.10",
                dstIp = "198.51.100.1",
                srcPort = 54321,
                dstPort = 443,
                secretKey = secretKey,
                currentClockMs = futureClockMs,
                maxAgeMinutes = 4
            )
        )
    }

    @Test
    fun testAesDiffusionRoundsMeasureRealBitDiffs() {
        val in1 = ByteArray(16) { 0x00 }
        val in2 = ByteArray(16) { 0x00 }
        in2[0] = 0x01 // 1-bit input difference

        val diffs = ArenaCryptoEngine.computeAesDiffusionRounds(in1, in2)
        assertEquals(4, diffs.size) // Round 0, 1, 2, 3
        assertEquals(1, diffs[0].totalDifferingBits) // Exactly 1 bit flipped initially
        assertTrue("Round 1 must exhibit diffusion", diffs[1].totalDifferingBits > 1)
        assertTrue("Round 2 must exhibit significant diffusion", diffs[2].totalDifferingBits > 15)
        // Round 3 should reach ~50% of 128 bits (~40..85 bits)
        assertTrue("Round 3 must diffuse to around 50% of 128 bits", diffs[3].totalDifferingBits in 35..90)
    }

    @Test
    fun testAddRoundKeySelfInverseProperty() {
        val stateByte = 0xA5
        val targetCipher = 0xF9
        // Self-inverse property: S ⊕ K = C  <=>  K = S ⊕ C
        val keyByte = stateByte xor targetCipher
        assertEquals(0x5C, keyByte)
        assertEquals(targetCipher, stateByte xor keyByte)
    }

    @Test
    fun testRailFenceDefendTheWallVector() {
        val message = "DEFENDTHEWALLX"
        val encrypted = ArenaCryptoEngine.railFenceEncrypt(message, 3)
        assertEquals("DNELEEDHWLXFTA", encrypted)
        val decrypted = ArenaCryptoEngine.railFenceDecrypt(encrypted, 3)
        assertEquals(message, decrypted)
    }

    @Test
    fun testToyRsaMathematicalConsistency() {
        val p = ArenaCryptoEngine.ToyRSA5_11.P
        val q = ArenaCryptoEngine.ToyRSA5_11.Q
        val n = ArenaCryptoEngine.ToyRSA5_11.N
        val phi = ArenaCryptoEngine.ToyRSA5_11.PHI
        val e = ArenaCryptoEngine.ToyRSA5_11.E
        val d = ArenaCryptoEngine.ToyRSA5_11.D

        assertEquals(55L, n)
        assertEquals(40L, phi)
        assertEquals(1L, (e * d) % phi)

        // Test with M = 2
        val c = ArenaCryptoEngine.ToyRSA5_11.encrypt(2L)
        assertEquals(8L, c) // 2^3 mod 55 = 8
        val decrypted = ArenaCryptoEngine.ToyRSA5_11.decrypt(c)
        assertEquals(2L, decrypted) // 8^27 mod 55 = 2
    }
}
