package com.simats.codeingo.ui.visualizer.engine

import java.math.BigInteger

/**
 * 🤝 Diffie-Hellman Key Exchange Math Engine
 *
 * Exposes:
 * 1. Safe primes & primitive root generators
 * 2. Honest protocol execution (Alice & Bob shared secret derivation)
 * 3. Mallory Man-in-the-Middle (MITM) interception state machine
 * 4. Discrete Logarithm problem brute-force work counter
 */
object DhEngine {

    data class DhDomainParameters(
        val p: Long,
        val g: Long,
        val name: String
    )

    val PRESET_DOMAINS = listOf(
        DhDomainParameters(23, 5, "Toy Prime 23 (g=5)"),
        DhDomainParameters(47, 5, "Safe Prime 47 (g=5)"),
        DhDomainParameters(107, 2, "Safe Prime 107 (g=2)"),
        DhDomainParameters(227, 2, "Safe Prime 227 (g=2)")
    )

    data class HonestDhState(
        val p: Long,
        val g: Long,
        val alicePrivate: Long,
        val alicePublic: Long,
        val bobPrivate: Long,
        val bobPublic: Long,
        val aliceSharedSecret: Long,
        val bobSharedSecret: Long,
        val secretsMatch: Boolean
    )

    data class MitmDhState(
        val p: Long,
        val g: Long,
        val alicePrivate: Long,
        val alicePublic: Long,
        val bobPrivate: Long,
        val bobPublic: Long,
        val malloryPrivate: Long,
        val malloryPublic: Long,
        val aliceComputedSecret: Long, // M^a mod p
        val malloryAliceSecret: Long,  // A^m mod p
        val bobComputedSecret: Long,   // M^b mod p
        val malloryBobSecret: Long,    // B^m mod p
        val aliceBobMatch: Boolean,    // false!
        val malloryKnowsBoth: Boolean  // true!
    )

    data class DiscreteLogStep(
        val guessedX: Long,
        val value: Long,
        val isMatch: Boolean
    )

    data class DiscreteLogResult(
        val p: Long,
        val g: Long,
        val targetPublic: Long,
        val actualSecret: Long,
        val totalSteps: Int,
        val steps: List<DiscreteLogStep>
    )

    /** Modular exponentiation */
    fun modPow(base: Long, exp: Long, mod: Long): Long {
        return BigInteger.valueOf(base).modPow(BigInteger.valueOf(exp), BigInteger.valueOf(mod)).toLong()
    }

    /** Verifies whether g is a primitive root modulo p */
    fun isPrimitiveRoot(g: Long, p: Long): Boolean {
        if (p <= 1L) return false
        val seen = BooleanArray(p.toInt())
        var cur = 1L
        for (i in 1 until p) {
            cur = (cur * g) % p
            if (cur == 0L || seen[cur.toInt()]) return false
            seen[cur.toInt()] = true
        }
        return true
    }

    /** Runs honest Diffie-Hellman key exchange */
    fun computeHonestExchange(p: Long, g: Long, alicePriv: Long, bobPriv: Long): HonestDhState {
        val alicePub = modPow(g, alicePriv, p)
        val bobPub = modPow(g, bobPriv, p)

        val sAlice = modPow(bobPub, alicePriv, p)
        val sBob = modPow(alicePub, bobPriv, p)

        return HonestDhState(
            p = p,
            g = g,
            alicePrivate = alicePriv,
            alicePublic = alicePub,
            bobPrivate = bobPriv,
            bobPublic = bobPub,
            aliceSharedSecret = sAlice,
            bobSharedSecret = sBob,
            secretsMatch = (sAlice == sBob)
        )
    }

    /** Runs Man-in-the-Middle attack where Mallory substitutes her own public key */
    fun computeMitmExchange(
        p: Long,
        g: Long,
        alicePriv: Long,
        bobPriv: Long,
        malloryPriv: Long
    ): MitmDhState {
        val alicePub = modPow(g, alicePriv, p)
        val bobPub = modPow(g, bobPriv, p)
        val malloryPub = modPow(g, malloryPriv, p)

        // Alice receives malloryPub thinking it's Bob
        val aliceSecret = modPow(malloryPub, alicePriv, p)
        // Mallory computes shared secret with Alice
        val malloryAliceSecret = modPow(alicePub, malloryPriv, p)

        // Bob receives malloryPub thinking it's Alice
        val bobSecret = modPow(malloryPub, bobPriv, p)
        // Mallory computes shared secret with Bob
        val malloryBobSecret = modPow(bobPub, malloryPriv, p)

        return MitmDhState(
            p = p,
            g = g,
            alicePrivate = alicePriv,
            alicePublic = alicePub,
            bobPrivate = bobPriv,
            bobPublic = bobPub,
            malloryPrivate = malloryPriv,
            malloryPublic = malloryPub,
            aliceComputedSecret = aliceSecret,
            malloryAliceSecret = malloryAliceSecret,
            bobComputedSecret = bobSecret,
            malloryBobSecret = malloryBobSecret,
            aliceBobMatch = (aliceSecret == bobSecret),
            malloryKnowsBoth = (aliceSecret == malloryAliceSecret && bobSecret == malloryBobSecret)
        )
    }

    /** Simulates brute-force discrete log search: finds x such that g^x = targetPublic mod p */
    fun bruteForceDiscreteLog(p: Long, g: Long, targetPublic: Long): DiscreteLogResult {
        val steps = mutableListOf<DiscreteLogStep>()
        var foundSecret = -1L

        for (x in 1 until p) {
            val v = modPow(g, x, p)
            val match = (v == targetPublic)
            steps.add(DiscreteLogStep(x, v, match))
            if (match) {
                foundSecret = x
                break
            }
        }

        return DiscreteLogResult(
            p = p,
            g = g,
            targetPublic = targetPublic,
            actualSecret = foundSecret,
            totalSteps = steps.size,
            steps = steps
        )
    }
}
