package com.simats.codeingo

import android.content.SharedPreferences
import com.simats.codeingo.domain.AuthService
import com.simats.codeingo.security.KeystoreCryptoHelper
import com.simats.codeingo.security.PasswordHasher
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.security.GeneralSecurityException

class AuthSecurityTest {

    // ─────────────────────────────────────────────────────────────────────────
    // 1. Password Hasher Verification Tests
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun testPasswordHasher_correctPasswordVerifies() {
        val password = "SuperSecretPassword123!".toCharArray()
        val hash = PasswordHasher.hashPassword(password)

        assertTrue("Valid password must verify successfully", PasswordHasher.verifyPassword(password, hash))
    }

    @Test
    fun testPasswordHasher_wrongPasswordFails() {
        val password = "SuperSecretPassword123!".toCharArray()
        val wrongPassword = "WrongPassword999!".toCharArray()
        val hash = PasswordHasher.hashPassword(password)

        assertFalse("Wrong password must fail verification", PasswordHasher.verifyPassword(wrongPassword, hash))
    }

    @Test
    fun testPasswordHasher_uniqueSaltsProduceDifferentHashes() {
        val password = "IdenticalPassword".toCharArray()
        val hash1 = PasswordHasher.hashPassword(password)
        val hash2 = PasswordHasher.hashPassword(password)

        assertNotEquals("Identical passwords must have distinct hashes due to random salting", hash1, hash2)
        assertTrue(PasswordHasher.verifyPassword(password, hash1))
        assertTrue(PasswordHasher.verifyPassword(password, hash2))
    }

    @Test
    fun testPasswordHasher_malformedHashReturnsFalse() {
        val password = "AnyPassword".toCharArray()
        assertFalse(PasswordHasher.verifyPassword(password, "invalid_hash_string"))
        assertFalse(PasswordHasher.verifyPassword(password, "pbkdf2\$notanumber\$salt\$hash"))
        assertFalse(PasswordHasher.verifyPassword(password, ""))
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. Legacy Preferences Migration Tests
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun testLegacyPasswordMigration_convertsToHashAndDeletesAllLegacyKeys() {
        val fakePrefs = FakeSharedPreferences().apply {
            edit()
                .putString("dsaUserPasswordKey", "LegacyGlobalPass1")
                .putString("dsaUserPassword_alice@test.com", "AliceSecretPass!")
                .putString("dsaUserPassword_bob@crypto.io", "BobMasterKey#")
                .putString("ashcrypt_auth_user_profile_v2", "{\"name\":\"Alice\"}")
                .apply()
        }

        // Execute migration
        AuthService.shared.migrateLegacyPasswords(fakePrefs)

        // 1. Assert NO legacy keys remain in preferences
        assertNull("Global legacy password key must be deleted", fakePrefs.getString("dsaUserPasswordKey", null))
        assertNull("Alice legacy password key must be deleted", fakePrefs.getString("dsaUserPassword_alice@test.com", null))
        assertNull("Bob legacy password key must be deleted", fakePrefs.getString("dsaUserPassword_bob@crypto.io", null))

        val remainingKeys = fakePrefs.all.keys
        val lingeringLegacyKeys = remainingKeys.filter { it.startsWith("dsaUserPassword") }
        assertTrue("No legacy dsaUserPassword keys must linger in storage: $lingeringLegacyKeys", lingeringLegacyKeys.isEmpty())

        // 2. Assert converted hashes exist and verify against original passwords
        val globalHash = fakePrefs.getString("auth_pwd_hash_global", null)
        assertNotNull("Migrated global hash must exist", globalHash)
        assertTrue(PasswordHasher.verifyPassword("LegacyGlobalPass1".toCharArray(), globalHash!!))

        val aliceHash = fakePrefs.getString("auth_pwd_hash_alice@test.com", null)
        assertNotNull("Migrated Alice hash must exist", aliceHash)
        assertTrue(PasswordHasher.verifyPassword("AliceSecretPass!".toCharArray(), aliceHash!!))

        val bobHash = fakePrefs.getString("auth_pwd_hash_bob@crypto.io", null)
        assertNotNull("Migrated Bob hash must exist", bobHash)
        assertTrue(PasswordHasher.verifyPassword("BobMasterKey#".toCharArray(), bobHash!!))

        // 3. Assert non-password keys were untouched
        assertEquals("{\"name\":\"Alice\"}", fakePrefs.getString("ashcrypt_auth_user_profile_v2", null))
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3. Keystore AES-256-GCM Round-Trip Tests
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun testKeystoreCryptoHelper_encryptionRoundTripSucceeds() {
        val helper = KeystoreCryptoHelper()
        val originalMessage = "Sensitive Session Auth Token: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9".toByteArray(Charsets.UTF_8)

        val encryptedPayload = helper.encrypt(originalMessage)
        val decryptedBytes = helper.decrypt(encryptedPayload)

        assertArrayEquals("Decrypted bytes must match original plaintext exactly", originalMessage, decryptedBytes)
        assertEquals(String(originalMessage, Charsets.UTF_8), String(decryptedBytes, Charsets.UTF_8))
    }

    @Test
    fun testKeystoreCryptoHelper_uniqueIvPerEncryption() {
        val helper = KeystoreCryptoHelper()
        val message = "Constant Secret Data".toByteArray(Charsets.UTF_8)

        val payload1 = helper.encrypt(message)
        val payload2 = helper.encrypt(message)

        assertFalse("Subsequent encryptions must have different IVs", payload1.iv.contentEquals(payload2.iv))
        assertFalse("Subsequent encryptions must have different ciphertexts", payload1.ciphertext.contentEquals(payload2.ciphertext))
    }

    @Test(expected = GeneralSecurityException::class)
    fun testKeystoreCryptoHelper_tamperedCiphertextThrowsSecurityException() {
        val helper = KeystoreCryptoHelper()
        val originalMessage = "Authenticity Protected Content".toByteArray(Charsets.UTF_8)

        val payload = helper.encrypt(originalMessage)
        // Flip one bit in the ciphertext
        val tamperedCiphertext = payload.ciphertext.clone()
        tamperedCiphertext[0] = (tamperedCiphertext[0].toInt() xor 0x01).toByte()

        helper.decrypt(KeystoreCryptoHelper.EncryptedPayload(tamperedCiphertext, payload.iv))
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Lightweight In-Memory SharedPreferences Fixture for Unit Tests
    // ─────────────────────────────────────────────────────────────────────────

    private class FakeSharedPreferences : SharedPreferences {
        private val data = mutableMapOf<String, Any?>()

        override fun getAll(): MutableMap<String, *> = HashMap(data)
        override fun getString(key: String?, defValue: String?): String? = data[key] as? String ?: defValue
        override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String>? =
            @Suppress("UNCHECKED_CAST") (data[key] as? MutableSet<String> ?: defValues)
        override fun getInt(key: String?, defValue: Int): Int = data[key] as? Int ?: defValue
        override fun getLong(key: String?, defValue: Long): Long = data[key] as? Long ?: defValue
        override fun getFloat(key: String?, defValue: Float): Float = data[key] as? Float ?: defValue
        override fun getBoolean(key: String?, defValue: Boolean): Boolean = data[key] as? Boolean ?: defValue
        override fun contains(key: String?): Boolean = data.containsKey(key)
        override fun edit(): SharedPreferences.Editor = FakeEditor(data)
        override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}
        override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}

        private class FakeEditor(private val target: MutableMap<String, Any?>) : SharedPreferences.Editor {
            private val pending = mutableMapOf<String, Any?>()
            private val removes = mutableSetOf<String>()
            private var clearPending = false

            override fun putString(key: String?, value: String?): SharedPreferences.Editor {
                if (key != null) { pending[key] = value; removes.remove(key) }
                return this
            }
            override fun putStringSet(key: String?, values: MutableSet<String>?): SharedPreferences.Editor {
                if (key != null) { pending[key] = values; removes.remove(key) }
                return this
            }
            override fun putInt(key: String?, value: Int): SharedPreferences.Editor {
                if (key != null) { pending[key] = value; removes.remove(key) }
                return this
            }
            override fun putLong(key: String?, value: Long): SharedPreferences.Editor {
                if (key != null) { pending[key] = value; removes.remove(key) }
                return this
            }
            override fun putFloat(key: String?, value: Float): SharedPreferences.Editor {
                if (key != null) { pending[key] = value; removes.remove(key) }
                return this
            }
            override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor {
                if (key != null) { pending[key] = value; removes.remove(key) }
                return this
            }
            override fun remove(key: String?): SharedPreferences.Editor {
                if (key != null) { removes.add(key); pending.remove(key) }
                return this
            }
            override fun clear(): SharedPreferences.Editor {
                clearPending = true
                return this
            }
            override fun commit(): Boolean {
                apply()
                return true
            }
            override fun apply() {
                if (clearPending) target.clear()
                removes.forEach { target.remove(it) }
                target.putAll(pending)
            }
        }
    }
}
