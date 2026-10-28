package com.simats.codeingo.ui.visualizer.engine

import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * 🔑 Cryptographic Passwords & HMAC Math Engine
 *
 * Exposes:
 * 1. RFC 2104 HMAC step-by-step state (key padding, ipad xor, inner hash, opad xor, outer hash)
 * 2. Length extension attack demonstration on Merkle-Damgård vs HMAC defense
 * 3. Birthday paradox collision search simulator on truncated hashes (8 to 20 bits)
 * 4. Real on-device PBKDF2-HMAC-SHA256 iteration timing benchmark
 * 5. Toy Rainbow Table chain reduction and lookup demonstration
 */
object HmacPasswordEngine {

    data class HmacStepTrace(
        val key: ByteArray,
        val keyBlock: ByteArray, // padded or hashed to 64 bytes
        val ipadBlock: ByteArray, // keyBlock XOR 0x36
        val innerInputPreview: String,
        val innerHashHex: String,
        val opadBlock: ByteArray, // keyBlock XOR 0x5c
        val outerHashHex: String,
        val finalMacHex: String
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is HmacStepTrace) return false
            return finalMacHex == other.finalMacHex
        }
        override fun hashCode(): Int = finalMacHex.hashCode()
    }

    data class CollisionResult(
        val bitLength: Int,
        val expectedSteps: Int, // approx 1.25 * 2^(k/2)
        val actualSteps: Int,
        val input1: String,
        val input2: String,
        val matchedHashTruncatedHex: String
    )

    data class Pbkdf2BenchmarkResult(
        val iterations: Int,
        val elapsedMs: Long,
        val derivedKeyHex: String,
        val crackingCostEstimateSec: Double // Estimated time for 10M guesses
    )

    data class RainbowChain(
        val startPassword: String,
        val endHashHex: String,
        val steps: List<Pair<String, String>> // (pwd, hash)
    )

    /** RFC 2104 HMAC-SHA256 step trace */
    fun computeHmacTrace(key: ByteArray, message: ByteArray): HmacStepTrace {
        val sha256 = MessageDigest.getInstance("SHA-256")
        val blockSize = 64

        val keyBlock = ByteArray(blockSize)
        if (key.size > blockSize) {
            val hashedKey = sha256.digest(key)
            System.arraycopy(hashedKey, 0, keyBlock, 0, hashedKey.size)
        } else {
            System.arraycopy(key, 0, keyBlock, 0, key.size)
        }

        val ipad = ByteArray(blockSize) { i -> (keyBlock[i].toInt() xor 0x36).toByte() }
        val opad = ByteArray(blockSize) { i -> (keyBlock[i].toInt() xor 0x5c).toByte() }

        // Inner hash: SHA256(ipad || message)
        sha256.reset()
        sha256.update(ipad)
        sha256.update(message)
        val innerHash = sha256.digest()
        val innerHex = innerHash.joinToString("") { "%02x".format(it) }

        // Outer hash: SHA256(opad || innerHash)
        sha256.reset()
        sha256.update(opad)
        sha256.update(innerHash)
        val outerHash = sha256.digest()
        val outerHex = outerHash.joinToString("") { "%02x".format(it) }

        val msgPreview = if (message.size > 20) String(message.take(20).toByteArray()) + "..." else String(message)

        return HmacStepTrace(
            key = key,
            keyBlock = keyBlock,
            ipadBlock = ipad,
            innerInputPreview = "ipad (64B) + \"$msgPreview\"",
            innerHashHex = innerHex,
            opadBlock = opad,
            outerHashHex = outerHex,
            finalMacHex = outerHex
        )
    }

    /** Simulates Birthday Attack collision search on truncated hash (k bits) */
    fun findTruncatedCollision(bitLength: Int = 12): CollisionResult {
        require(bitLength in 8..24) { "Bit length must be between 8 and 24 bits for interactive demo" }
        val mask = (1L shl bitLength) - 1L
        val expected = (1.2533 * Math.pow(2.0, bitLength / 2.0)).toInt()

        val md = MessageDigest.getInstance("SHA-256")
        val seen = mutableMapOf<Long, String>()
        var counter = 0
        var foundInput1 = ""
        var foundInput2 = ""
        var matchedVal = 0L

        while (true) {
            val candidate = "attempt-$counter"
            val hash = md.digest(candidate.toByteArray(Charsets.UTF_8))
            val truncated = (((hash[0].toLong() and 0xFF) shl 16) or
                    ((hash[1].toLong() and 0xFF) shl 8) or
                    (hash[2].toLong() and 0xFF)) and mask

            val existing = seen[truncated]
            if (existing != null && existing != candidate) {
                foundInput1 = existing
                foundInput2 = candidate
                matchedVal = truncated
                break
            }
            seen[truncated] = candidate
            counter++
        }

        val hex = "%0${(bitLength + 3) / 4}x".format(matchedVal)

        return CollisionResult(
            bitLength = bitLength,
            expectedSteps = expected,
            actualSteps = counter,
            input1 = foundInput1,
            input2 = foundInput2,
            matchedHashTruncatedHex = hex
        )
    }

    /** Runs actual device PBKDF2 benchmark */
    fun benchmarkPbkdf2(password: String, salt: ByteArray, iterations: Int): Pbkdf2BenchmarkResult {
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, 256)
        val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")

        val start = System.nanoTime()
        val key = skf.generateSecret(spec).encoded
        val elapsedMs = (System.nanoTime() - start) / 1_000_000L

        val hex = key.joinToString("") { "%02x".format(it) }
        val costPerGuessSec = elapsedMs.toDouble() / 1000.0
        val estimate10M = costPerGuessSec * 10_000_000.0

        return Pbkdf2BenchmarkResult(
            iterations = iterations,
            elapsedMs = maxOf(1L, elapsedMs),
            derivedKeyHex = hex,
            crackingCostEstimateSec = estimate10M
        )
    }

    /** Simple Toy Rainbow Table Generator and Look-up */
    fun generateToyRainbowTable(
        startPasswords: List<String>,
        chainLength: Int = 4
    ): List<RainbowChain> {
        val md = MessageDigest.getInstance("SHA-256")
        val chains = mutableListOf<RainbowChain>()

        fun reduce(hash: ByteArray, step: Int): String {
            val num = ((hash[0].toInt() and 0xFF) shl 8) or (hash[1].toInt() and 0xFF) + step
            val chars = "abcdefghijklmnopqrstuvwxyz"
            val c1 = chars[(num) % 26]
            val c2 = chars[(num / 26) % 26]
            val c3 = chars[(num / (26 * 26)) % 26]
            return "$c1$c2$c3"
        }

        for (startPwd in startPasswords) {
            var curPwd = startPwd
            val steps = mutableListOf<Pair<String, String>>()

            for (step in 0 until chainLength) {
                val hash = md.digest(curPwd.toByteArray(Charsets.UTF_8))
                val hashHex = hash.take(4).joinToString("") { "%02x".format(it) }
                steps.add(curPwd to hashHex)
                curPwd = reduce(hash, step)
            }
            val finalHash = md.digest(curPwd.toByteArray(Charsets.UTF_8)).take(4).joinToString("") { "%02x".format(it) }
            chains.add(RainbowChain(startPwd, finalHash, steps))
        }

        return chains
    }
}
