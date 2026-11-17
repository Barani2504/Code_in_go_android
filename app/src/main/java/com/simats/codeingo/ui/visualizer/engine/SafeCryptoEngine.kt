package com.simats.codeingo.ui.visualizer.engine

import java.math.BigInteger

/**
 * 🛡️ SafeCryptoEngine — Uniform Safety Net Layer for all Visualizers (Phase 0 & Phase 4).
 *
 * Guarantees:
 * 1. Every engine call returns kotlin.Result<T>.
 * 2. All inputs are clamped to mathematically safe boundaries.
 * 3. Never throws uncaught exceptions; invalid inputs yield friendly Ash messages.
 * 4. Step-back operations always return mathematically reversible identical state.
 */
object SafeCryptoEngine {

    data class CaesarResult(
        val plaintext: String,
        val shift: Int,
        val ciphertext: String,
        val explanation: String
    )

    data class ModuloResult(
        val a: Long,
        val m: Long,
        val remainder: Long,
        val gcd: Long
    )

    data class RsaTinyResult(
        val p: Long,
        val q: Long,
        val n: Long,
        val phi: Long,
        val e: Long,
        val d: Long,
        val message: Long,
        val ciphertext: Long,
        val decrypted: Long
    )

    data class DhResult(
        val p: Long,
        val g: Long,
        val alicePrivate: Long,
        val bobPrivate: Long,
        val alicePublic: Long,
        val bobPublic: Long,
        val sharedSecret: Long
    )

    // ── 1. CAESAR CIPHER ─────────────────────────────────────────
    fun safeCaesar(text: String, shift: Int): Result<CaesarResult> = runCatching {
        val safeShift = ((shift % 26) + 26) % 26
        val cleanText = text.take(500)
        val sb = StringBuilder()
        for (ch in cleanText) {
            when {
                ch in 'a'..'z' -> sb.append('a' + (ch - 'a' + safeShift) % 26)
                ch in 'A'..'Z' -> sb.append('A' + (ch - 'A' + safeShift) % 26)
                else -> sb.append(ch)
            }
        }
        val cipher = sb.toString()
        CaesarResult(
            plaintext = cleanText,
            shift = safeShift,
            ciphertext = cipher,
            explanation = "Shifted each letter forward by $safeShift positions along the 26-letter ring."
        )
    }

    // ── 2. MODULO & GCD ──────────────────────────────────────────
    fun safeModulo(a: Long, m: Long): Result<ModuloResult> = runCatching {
        val safeM = if (m <= 0) 1L else m.coerceAtMost(10_000_000L)
        val rem = ((a % safeM) + safeM) % safeM
        val gcdVal = BigInteger.valueOf(a).gcd(BigInteger.valueOf(safeM)).toLong()
        ModuloResult(a = a, m = safeM, remainder = rem, gcd = gcdVal)
    }

    // ── 3. SHA-256 AVALANCHE ─────────────────────────────────────
    fun safeSha256(text: String): Result<String> = runCatching {
        val trace = Sha256Engine.hashWithTrace(text.toByteArray(Charsets.UTF_8))
        trace.finalHex
    }

    fun safeAvalanche(textA: String, textB: String): Result<Sha256Engine.AvalancheResult> = runCatching {
        val hashA = Sha256Engine.hashWithTrace(textA.toByteArray(Charsets.UTF_8)).finalHash
        val hashB = Sha256Engine.hashWithTrace(textB.toByteArray(Charsets.UTF_8)).finalHash
        Sha256Engine.computeAvalanche(hashA, hashB)
    }

    // ── 4. RSA WITH TINY PRIMES ──────────────────────────────────
    fun safeRsaTiny(pInput: Long, qInput: Long, eInput: Long, messageInput: Long): Result<RsaTinyResult> = runCatching {
        // Clamp primes to small prime candidates
        val safeP = clampToPrime(pInput.coerceIn(2, 97))
        val rawQ = clampToPrime(qInput.coerceIn(2, 97))
        val safeQ = if (rawQ == safeP) getNextPrime(rawQ) else rawQ

        val n = safeP * safeQ
        val phi = (safeP - 1) * (safeQ - 1)

        val safeE = findValidE(eInput.coerceIn(3, phi - 1), phi)
        val d = BigInteger.valueOf(safeE).modInverse(BigInteger.valueOf(phi)).toLong()

        val safeM = messageInput.coerceIn(0, n - 1)
        val c = BigInteger.valueOf(safeM).modPow(BigInteger.valueOf(safeE), BigInteger.valueOf(n)).toLong()
        val dec = BigInteger.valueOf(c).modPow(BigInteger.valueOf(d), BigInteger.valueOf(n)).toLong()

        RsaTinyResult(
            p = safeP,
            q = safeQ,
            n = n,
            phi = phi,
            e = safeE,
            d = d,
            message = safeM,
            ciphertext = c,
            decrypted = dec
        )
    }

    // ── 5. DIFFIE-HELLMAN KEY EXCHANGE ───────────────────────────
    fun safeDiffieHellman(pInput: Long, gInput: Long, aInput: Long, bInput: Long): Result<DhResult> = runCatching {
        val p = clampToPrime(pInput.coerceIn(5, 997))
        val g = gInput.coerceIn(2, p - 2)
        val a = aInput.coerceIn(1, p - 2)
        val b = bInput.coerceIn(1, p - 2)

        val bigP = BigInteger.valueOf(p)
        val bigG = BigInteger.valueOf(g)

        val bigA = bigG.modPow(BigInteger.valueOf(a), bigP)
        val bigB = bigG.modPow(BigInteger.valueOf(b), bigP)

        val secretAlice = bigB.modPow(BigInteger.valueOf(a), bigP).toLong()
        val secretBob = bigA.modPow(BigInteger.valueOf(b), bigP).toLong()

        require(secretAlice == secretBob) { "Math anomaly in DH computation" }

        DhResult(
            p = p,
            g = g,
            alicePrivate = a,
            bobPrivate = b,
            alicePublic = bigA.toLong(),
            bobPublic = bigB.toLong(),
            sharedSecret = secretAlice
        )
    }

    // ── HELPERS ──────────────────────────────────────────────────
    private val smallPrimes = listOf(
        2L, 3L, 5L, 7L, 11L, 13L, 17L, 19L, 23L, 29L, 31L, 37L, 41L, 43L, 47L,
        53L, 59L, 61L, 67L, 71L, 73L, 79L, 83L, 89L, 97L, 101L, 103L, 107L, 109L
    )

    private fun clampToPrime(value: Long): Long {
        return smallPrimes.minByOrNull { kotlin.math.abs(it - value) } ?: 3L
    }

    private fun getNextPrime(value: Long): Long {
        return smallPrimes.firstOrNull { it > value } ?: 101L
    }

    private fun findValidE(candidate: Long, phi: Long): Long {
        val phiBig = BigInteger.valueOf(phi)
        for (candidateE in listOf(candidate, 3L, 5L, 7L, 17L, 65537L)) {
            if (candidateE in 2 until phi && BigInteger.valueOf(candidateE).gcd(phiBig) == BigInteger.ONE) {
                return candidateE
            }
        }
        for (i in 3 until phi step 2) {
            if (BigInteger.valueOf(i).gcd(phiBig) == BigInteger.ONE) return i
        }
        return 3L
    }
}
