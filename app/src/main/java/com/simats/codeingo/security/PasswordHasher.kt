package com.simats.codeingo.security

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * PBKDF2-HMAC-SHA256 password hashing utility.
 * Adheres to OWASP recommendations for password storage.
 *
 * Guarantees:
 * - One-way irreversible cryptographic hash.
 * - Per-password cryptographically secure 16-byte random salt.
 * - 100,000 iterations of HMAC-SHA256.
 * - Constant-time verification to prevent timing side-channel attacks.
 * - Self-contained portable serialized format: "pbkdf2$<iterations>$<saltB64>$<hashB64>".
 */
object PasswordHasher {
    private const val ALGORITHM = "PBKDF2WithHmacSHA256"
    const val DEFAULT_ITERATIONS = 100_000
    private const val KEY_LENGTH_BITS = 256
    private const val SALT_LENGTH_BYTES = 16

    private val secureRandom = SecureRandom()

    /**
     * Generates a cryptographically strong 16-byte random salt.
     */
    fun generateSalt(): ByteArray {
        val salt = ByteArray(SALT_LENGTH_BYTES)
        secureRandom.nextBytes(salt)
        return salt
    }

    /**
     * Hashes a password using PBKDF2-HMAC-SHA256 and returns a serialized string representation.
     */
    fun hashPassword(
        password: CharArray,
        salt: ByteArray = generateSalt(),
        iterations: Int = DEFAULT_ITERATIONS
    ): String {
        val spec = PBEKeySpec(password, salt, iterations, KEY_LENGTH_BITS)
        try {
            val skf = SecretKeyFactory.getInstance(ALGORITHM)
            val hash = skf.generateSecret(spec).encoded
            val encoder = Base64.getEncoder()
            val saltB64 = encoder.encodeToString(salt)
            val hashB64 = encoder.encodeToString(hash)
            return listOf("pbkdf2", iterations.toString(), saltB64, hashB64).joinToString("$")
        } finally {
            spec.clearPassword()
        }
    }

    /**
     * Verifies an input password against a stored PBKDF2 serialized hash in constant time.
     */
    fun verifyPassword(password: CharArray, serializedHash: String): Boolean {
        val parts = serializedHash.split("$")
        if (parts.size != 4 || parts[0] != "pbkdf2") {
            return false
        }

        val iterations = parts[1].toIntOrNull() ?: return false
        val decoder = Base64.getDecoder()
        val salt = try {
            decoder.decode(parts[2])
        } catch (_: Exception) {
            return false
        }
        val expectedHash = try {
            decoder.decode(parts[3])
        } catch (_: Exception) {
            return false
        }

        val spec = PBEKeySpec(password, salt, iterations, expectedHash.size * 8)
        try {
            val skf = SecretKeyFactory.getInstance(ALGORITHM)
            val computedHash = skf.generateSecret(spec).encoded
            return MessageDigest.isEqual(computedHash, expectedHash)
        } catch (_: Exception) {
            return false
        } finally {
            spec.clearPassword()
        }
    }
}
