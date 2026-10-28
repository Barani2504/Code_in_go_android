package com.simats.codeingo.ui.visualizer.engine

import java.math.BigInteger

/**
 * 🗝️ RSA Cryptosystem Mathematical Engine
 *
 * Exposes:
 * 1. Step-by-step key pair generation from small primes (p, q, n, phi(n), e, d)
 * 2. Extended Euclidean Algorithm (EEA) tabular trace calculating modular inverse d
 * 3. Square-and-Multiply modular exponentiation trace
 * 4. Textbook RSA homomorphic malleability demo (c1 * c2 = (m1 * m2)^e mod n)
 * 5. Hash-then-sign workflow vs raw message signing
 */
object RsaEngine {

    val RECOMMENDED_PRIMES = listOf(
        11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113
    )

    data class EeaStep(
        val step: Int,
        val quotient: BigInteger,
        val remainder: BigInteger,
        val x: BigInteger,
        val y: BigInteger
    )

    data class RsaKeyGenResult(
        val p: BigInteger,
        val q: BigInteger,
        val n: BigInteger,
        val phi: BigInteger,
        val e: BigInteger,
        val d: BigInteger,
        val eeaSteps: List<EeaStep>,
        val isValid: Boolean,
        val errorMessage: String? = null
    )

    data class SquareAndMultiplyStep(
        val bitIndex: Int,
        val bitValue: Int,
        val accBefore: BigInteger,
        val afterSquare: BigInteger,
        val afterMultiply: BigInteger?,
        val accAfter: BigInteger
    )

    data class ModExpTrace(
        val base: BigInteger,
        val exponent: BigInteger,
        val modulus: BigInteger,
        val binaryExponent: String,
        val steps: List<SquareAndMultiplyStep>,
        val result: BigInteger
    )

    /** Greatest Common Divisor */
    fun gcd(a: BigInteger, b: BigInteger): BigInteger = a.gcd(b)

    /** Extended Euclidean Algorithm producing trace steps for a * x + b * y = gcd(a, b) */
    fun extendedEuclidean(e: BigInteger, phi: BigInteger): Pair<BigInteger, List<EeaStep>> {
        var r0 = phi
        var r1 = e
        var x0 = BigInteger.ZERO
        var x1 = BigInteger.ONE
        var y0 = BigInteger.ONE
        var y1 = BigInteger.ZERO

        val steps = mutableListOf<EeaStep>()
        var stepCount = 0

        steps.add(EeaStep(stepCount++, BigInteger.ZERO, r0, x0, y0))
        steps.add(EeaStep(stepCount++, BigInteger.ZERO, r1, x1, y1))

        while (r1 != BigInteger.ZERO) {
            val q = r0 / r1
            val r2 = r0 % r1
            val x2 = x0 - q * x1
            val y2 = y0 - q * y1

            steps.add(EeaStep(stepCount++, q, r2, x2, y2))

            r0 = r1
            r1 = r2
            x0 = x1
            x1 = x2
            y0 = y1
            y1 = y2
        }

        // x0 is the modular inverse of e modulo phi
        var d = x0 % phi
        if (d < BigInteger.ZERO) {
            d += phi
        }

        return Pair(d, steps)
    }

    /** Generates RSA key parameters from primes p and q */
    fun generateKeys(pVal: Long, qVal: Long, preferredE: Long = 65537L): RsaKeyGenResult {
        val p = BigInteger.valueOf(pVal)
        val q = BigInteger.valueOf(qVal)

        if (p == q) {
            return RsaKeyGenResult(p, q, p * q, BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO, emptyList(), false, "Primes p and q must be distinct")
        }

        val n = p * q
        val phi = (p - BigInteger.ONE) * (q - BigInteger.ONE)

        // Find coprime e
        var e = BigInteger.valueOf(preferredE)
        if (e >= phi || gcd(e, phi) != BigInteger.ONE) {
            // Find smallest suitable odd e > 2
            var candidate = BigInteger.valueOf(3)
            var found = false
            while (candidate < phi) {
                if (gcd(candidate, phi) == BigInteger.ONE) {
                    e = candidate
                    found = true
                    break
                }
                candidate += BigInteger.valueOf(2)
            }
            if (!found) {
                return RsaKeyGenResult(p, q, n, phi, e, BigInteger.ZERO, emptyList(), false, "Could not find coprime exponent e")
            }
        }

        val (d, steps) = extendedEuclidean(e, phi)
        return RsaKeyGenResult(p, q, n, phi, e, d, steps, true)
    }

    /** Traces square-and-multiply modular exponentiation: base^exponent mod modulus */
    fun traceModExp(base: BigInteger, exponent: BigInteger, modulus: BigInteger): ModExpTrace {
        val bin = exponent.toString(2)
        var acc = BigInteger.ONE
        val steps = mutableListOf<SquareAndMultiplyStep>()

        for (i in bin.indices) {
            val bit = bin[i].toString().toInt()
            val before = acc
            val squared = (acc * acc) % modulus
            var multiplied: BigInteger? = null
            var after = squared

            if (bit == 1) {
                multiplied = (squared * base) % modulus
                after = multiplied
            }

            steps.add(
                SquareAndMultiplyStep(
                    bitIndex = i,
                    bitValue = bit,
                    accBefore = before,
                    afterSquare = squared,
                    afterMultiply = multiplied,
                    accAfter = after
                )
            )
            acc = after
        }

        return ModExpTrace(base, exponent, modulus, bin, steps, acc)
    }

    /**
     * Textbook RSA Homomorphic Malleability Demo:
     * Encrypts m1 and m2 separately.
     * Shows that c1 * c2 mod n decrypts exactly to (m1 * m2) mod n!
     */
    data class MalleabilityDemo(
        val m1: BigInteger,
        val m2: BigInteger,
        val c1: BigInteger,
        val c2: BigInteger,
        val cProductModN: BigInteger,
        val decryptedProduct: BigInteger,
        val expectedProductModN: BigInteger,
        val isMalleableMatch: Boolean
    )

    fun testMalleability(m1Val: Long, m2Val: Long, e: BigInteger, d: BigInteger, n: BigInteger): MalleabilityDemo {
        val m1 = BigInteger.valueOf(m1Val) % n
        val m2 = BigInteger.valueOf(m2Val) % n

        val c1 = m1.modPow(e, n)
        val c2 = m2.modPow(e, n)
        val cProduct = (c1 * c2) % n

        val decrypted = cProduct.modPow(d, n)
        val expected = (m1 * m2) % n

        return MalleabilityDemo(
            m1 = m1,
            m2 = m2,
            c1 = c1,
            c2 = c2,
            cProductModN = cProduct,
            decryptedProduct = decrypted,
            expectedProductModN = expected,
            isMalleableMatch = (decrypted == expected)
        )
    }

    /**
     * Hash-then-Sign demonstration:
     * Sign: s = H(m)^d mod n
     * Verify: s^e mod n == H(m)
     */
    data class SignatureDemo(
        val message: String,
        val digestModN: BigInteger,
        val signature: BigInteger,
        val verifiedDigest: BigInteger,
        val isValid: Boolean,
        val tamperedMessage: String,
        val tamperedDigestModN: BigInteger,
        val tamperedValid: Boolean
    )

    fun demonstrateSignature(message: String, e: BigInteger, d: BigInteger, n: BigInteger): SignatureDemo {
        fun simpleDigest(msg: String): BigInteger {
            var h = 0L
            for (c in msg) {
                h = (h * 31L + c.code.toLong()) and 0x7FFFFFFF_FFFFFFFFL
            }
            return (BigInteger.valueOf(h) % (n - BigInteger.ONE)) + BigInteger.ONE
        }

        val digest = simpleDigest(message)
        val signature = digest.modPow(d, n)
        val recovered = signature.modPow(e, n)
        val valid = (recovered == digest)

        val tamperedMsg = if (message.endsWith("!")) message.dropLast(1) else "$message!"
        val tamperedDigest = simpleDigest(tamperedMsg)
        val tamperedValid = (recovered == tamperedDigest)

        return SignatureDemo(
            message = message,
            digestModN = digest,
            signature = signature,
            verifiedDigest = recovered,
            isValid = valid,
            tamperedMessage = tamperedMsg,
            tamperedDigestModN = tamperedDigest,
            tamperedValid = tamperedValid
        )
    }
}
