package com.simats.codeingo.data.network.security

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Secure Token Storage backed by Android Keystore and Google Tink AEAD (AES-256-GCM).
 * Replaces deprecated androidx.security.crypto without using any alpha libraries.
 * Excluded from Android Auto Backup and device-to-device transfers.
 */
@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) : TokenStorage {

    companion object {
        private const val PREFS_NAME = "codeingo_secure_tokens"
        private const val KEYSET_NAME = "codeingo_tink_keyset"
        private const val MASTER_KEY_URI = "android-keystore://codeingo_master_key"
        private const val KEY_ACCESS_TOKEN = "enc_access_token"
        private const val KEY_REFRESH_TOKEN = "enc_refresh_token"
        private val ASSOCIATED_DATA = "codeingo_auth_ad".toByteArray(Charsets.UTF_8)
    }

    private val prefs: SharedPreferences? = runCatching { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }.getOrNull()
    private val aead: Aead?

    // In-memory cached flows for instant UI reactivity
    private val _hasValidToken = MutableStateFlow(false)
    override val hasValidToken: StateFlow<Boolean> = _hasValidToken.asStateFlow()

    init {
        aead = runCatching {
            AeadConfig.register()
            val keysetHandle = AndroidKeysetManager.Builder()
                .withSharedPref(context, KEYSET_NAME, PREFS_NAME)
                .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
                .withMasterKeyUri(MASTER_KEY_URI)
                .build()
                .keysetHandle
            keysetHandle.getPrimitive(Aead::class.java)
        }.getOrNull()

        _hasValidToken.value = !getAccessToken().isNullOrEmpty()
    }

    @Synchronized
    override fun saveAccessToken(token: String) {
        val encrypted = encrypt(token) ?: return
        prefs?.edit()?.putString(KEY_ACCESS_TOKEN, encrypted)?.apply()
        _hasValidToken.value = true
    }

    @Synchronized
    override fun getAccessToken(): String? {
        val raw = prefs?.getString(KEY_ACCESS_TOKEN, null) ?: return null
        return decrypt(raw)
    }

    @Synchronized
    override fun saveRefreshToken(token: String) {
        val encrypted = encrypt(token) ?: return
        prefs?.edit()?.putString(KEY_REFRESH_TOKEN, encrypted)?.apply()
    }

    @Synchronized
    override fun getRefreshToken(): String? {
        val raw = prefs?.getString(KEY_REFRESH_TOKEN, null) ?: return null
        return decrypt(raw)
    }

    @Synchronized
    override fun clearTokens() {
        prefs?.edit()?.remove(KEY_ACCESS_TOKEN)?.remove(KEY_REFRESH_TOKEN)?.apply()
        _hasValidToken.value = false
    }

    private fun encrypt(plaintext: String): String? {
        val cipher = aead ?: return null
        return runCatching {
            val ciphertextBytes = cipher.encrypt(plaintext.toByteArray(Charsets.UTF_8), ASSOCIATED_DATA)
            Base64.encodeToString(ciphertextBytes, Base64.NO_WRAP)
        }.getOrNull()
    }

    private fun decrypt(base64Ciphertext: String): String? {
        val cipher = aead ?: return null
        return runCatching {
            val decodedBytes = Base64.decode(base64Ciphertext, Base64.NO_WRAP)
            val decryptedBytes = cipher.decrypt(decodedBytes, ASSOCIATED_DATA)
            String(decryptedBytes, Charsets.UTF_8)
        }.getOrNull()
    }
}
