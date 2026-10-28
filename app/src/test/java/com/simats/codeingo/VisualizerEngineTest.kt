package com.simats.codeingo

import com.simats.codeingo.ui.visualizer.engine.*
import org.junit.Assert.*
import org.junit.Test
import java.math.BigInteger

class VisualizerEngineTest {

    @Test
    fun testDesEngineNistVector() {
        val key = 0x133457799BBCDFF1L
        val plaintext = 0x0123456789ABCDEFL
        val expectedCiphertext = 0x85E813540F0AB405UL.toLong()

        val trace = DesEngine.encrypt(plaintext, key)
        assertEquals(16, trace.rounds.size)
        assertEquals(expectedCiphertext, trace.ciphertext)
    }

    @Test
    fun testAesEngineFips197Vector() {
        val key = AesEngine.hexToBytes("2b7e151628aed2a6abf7158809cf4f3c")
        val plaintext = AesEngine.hexToBytes("3243f6a8885a308d313198a2e0370734")
        val expectedHex = "3925841d02dc09fbdc118597196a0b32"

        val trace = AesEngine.encryptBlockWithTrace(plaintext, key)
        assertEquals(10, trace.rounds.size)
        assertEquals(expectedHex, AesEngine.bytesToHex(trace.ciphertext))
    }

    @Test
    fun testAesCtrNonceReuseKeystreamCancellation() {
        val msg1 = "SECRET_CREDENTIALS"
        val msg2 = "ATTACK_AT_DAWN_12"
        val result = AesEngine.simulateCtrNonceReuse(msg1, msg2)

        // Verify C1 xor C2 == P1 xor P2
        assertArrayEquals(result.p1XorP2, result.c1XorC2)
    }

    @Test
    fun testAesGcmTamperDetection() {
        val tamperResult = AesEngine.testGcmTamper("Transfer $1000 to Bob", tamperByteIndex = 2)
        assertFalse(tamperResult.tagValid)
        assertNotNull(tamperResult.errorMessage)
    }

    @Test
    fun testSha256EngineNistVector() {
        val input = "abc".toByteArray(Charsets.UTF_8)
        val expectedHex = "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad"

        val trace = Sha256Engine.hashWithTrace(input)
        assertEquals(expectedHex, trace.finalHex)
        assertEquals(1, trace.blocks.size)
        assertEquals(64, trace.blocks[0].steps.size)
    }

    @Test
    fun testSha256AvalancheBitHammingDistance() {
        val trace1 = Sha256Engine.hashWithTrace("Codeingo_Visualizer_A".toByteArray(Charsets.UTF_8))
        val trace2 = Sha256Engine.hashWithTrace("Codeingo_Visualizer_B".toByteArray(Charsets.UTF_8))

        val avalanche = Sha256Engine.computeAvalanche(trace1.finalHash, trace2.finalHash)
        assertEquals(256, avalanche.totalBits)
        // A good cryptographic hash has avalanche around 50% (35% to 65% for single character flip)
        assertTrue("Avalanche was ${avalanche.percentage}%", avalanche.percentage in 35.0f..65.0f)
        assertTrue(avalanche.flippedBitsCount in 90..166)
    }

    @Test
    fun testRsaKeyGenAndMalleability() {
        // Safe primes p=61, q=53
        val keyGen = RsaEngine.generateKeys(61, 53, preferredE = 17)
        assertTrue(keyGen.isValid)
        assertEquals(BigInteger.valueOf(3233), keyGen.n)
        assertEquals(BigInteger.valueOf(3120), keyGen.phi)
        assertEquals(BigInteger.valueOf(17), keyGen.e)

        // Verify e * d == 1 mod phi
        val edModPhi = (keyGen.e * keyGen.d) % keyGen.phi
        assertEquals(BigInteger.ONE, edModPhi)

        // ModExp trace test
        val modExp = RsaEngine.traceModExp(BigInteger.valueOf(65), keyGen.e, keyGen.n)
        assertEquals(BigInteger.valueOf(65).modPow(keyGen.e, keyGen.n), modExp.result)

        // Malleability test
        val malDemo = RsaEngine.testMalleability(15, 20, keyGen.e, keyGen.d, keyGen.n)
        assertTrue(malDemo.isMalleableMatch)
    }

    @Test
    fun testDhEngineHonestAndMitm() {
        val honest = DhEngine.computeHonestExchange(p = 23, g = 5, alicePriv = 6, bobPriv = 15)
        assertTrue(honest.secretsMatch)
        assertEquals(2L, honest.aliceSharedSecret)
        assertEquals(2L, honest.bobSharedSecret)

        val mitm = DhEngine.computeMitmExchange(p = 23, g = 5, alicePriv = 6, bobPriv = 15, malloryPriv = 9)
        assertFalse(mitm.aliceBobMatch)
        assertTrue(mitm.malloryKnowsBoth)
    }

    @Test
    fun testKasiskiEngineCaesarAndVigenere() {
        // Encrypt with Caesar shift 3: "THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG"
        val sample = "WKH TXLFN EURZQ IRA MXPSV RYHU WKH ODCB GRJ"
        val caesarScores = KasiskiEngine.analyzeCaesarShifts(sample)
        // Best shift should be 3
        assertEquals(3, caesarScores.first().shift)

        // Vigenere crack
        val ciphertext = "LXFOPV EF RNHR" // "ATTACK AT DAWN" with key "LEMON"
        val ioc = KasiskiEngine.indexOfCoincidence(ciphertext)
        assertTrue(ioc in 0.0..1.0)
    }

    @Test
    fun testHmacPasswordEngine() {
        val key = "secretkey".toByteArray(Charsets.UTF_8)
        val msg = "transfer $500".toByteArray(Charsets.UTF_8)
        val trace = HmacPasswordEngine.computeHmacTrace(key, msg)

        assertNotNull(trace.finalMacHex)
        assertEquals(64, trace.finalMacHex.length) // 256 bits = 64 hex chars
        assertEquals(trace.innerHashHex.length, 64)

        // Birthday collision test on 12 bits
        val collision = HmacPasswordEngine.findTruncatedCollision(bitLength = 12)
        assertNotEquals(collision.input1, collision.input2)
        assertTrue(collision.actualSteps > 0)
    }

    @Test
    fun testTlsEngineHandshakeAndRecordTamper() {
        val packets = TlsEngine.getHandshakePackets()
        assertEquals(8, packets.size)

        val tamper = TlsEngine.testRecordTamper(tamperByteIndex = 3)
        assertFalse(tamper.tagVerified)
        assertNotNull(tamper.errorDetail)

        // Certificate chain check
        val validChain = listOf(
            TlsEngine.X509Cert("CN=codeingo.simats.com", "CN=Simats CA", false, 2024, 2027, true),
            TlsEngine.X509Cert("CN=Simats CA", "CN=Root CA", false, 2020, 2030, true),
            TlsEngine.X509Cert("CN=Root CA", "CN=Root CA", true, 2015, 2035, true)
        )
        val validation = TlsEngine.validateCertificateChain(validChain, "codeingo.simats.com", 2026)
        assertTrue(validation.isValid)

        // Expired check
        val expiredValidation = TlsEngine.validateCertificateChain(validChain, "codeingo.simats.com", 2036)
        assertFalse(expiredValidation.isValid)
    }
}
