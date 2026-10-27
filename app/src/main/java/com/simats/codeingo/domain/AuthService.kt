package com.simats.codeingo.domain

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.simats.codeingo.security.PasswordHasher
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

// ══════════════════════════════════════════════════════════════════
// 🔐 Authentication & Session Service
// Hardened with PBKDF2-HMAC-SHA256 salted password hashing & migration
// ══════════════════════════════════════════════════════════════════

enum class AuthProvider(val id: String, val displayName: String) {
    GOOGLE("google", "Google"),
    APPLE("apple", "Apple"),
    EMAIL("email", "Email & Password"),
    GUEST("guest", "Guest")
}

data class UserAuthProfile(
    val id: String = UUID.randomUUID().toString(),
    var name: String,
    var email: String,
    var handle: String = email.substringBefore("@").lowercase().ifEmpty { "user" },
    var avatarInitial: String = name.take(1).uppercase().ifEmpty { "U" },
    var avatarColorHex: String = "#4285F4",
    var provider: AuthProvider = AuthProvider.EMAIL,
    var isEmailVerified: Boolean = true,
    var lastLoginDate: Long = System.currentTimeMillis(),
    var streakDays: Int = 1
)

data class SavedGoogleAccount(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val email: String,
    val avatarInitial: String = name.take(1).uppercase().ifEmpty { "G" },
    val colorIndex: Int = 0
)

class AuthService private constructor() {

    companion object {
        val instance: AuthService by lazy { AuthService() }
        val shared: AuthService get() = instance

        private const val PREFS_NAME = "ashcrypt_auth_prefs"
        private const val KEY_USER_PROFILE = "ashcrypt_auth_user_profile_v2"
        private const val KEY_GOOGLE_ACCOUNTS = "ashcrypt_saved_google_accounts_v2"

        // Legacy plaintext keys (migrated and deleted permanently)
        const val LEGACY_KEY_PASSWORD_GLOBAL = "dsaUserPasswordKey"
        const val LEGACY_KEY_PASSWORD_PREFIX = "dsaUserPassword_"

        // Secure PBKDF2-HMAC-SHA256 hashed keys
        const val KEY_PASSWORD_HASH_GLOBAL = "auth_pwd_hash_global"
        const val KEY_PASSWORD_HASH_PREFIX = "auth_pwd_hash_"
    }

    private var prefs: SharedPreferences? = null

    private val _currentUser = MutableStateFlow<UserAuthProfile?>(null)
    val currentUser: StateFlow<UserAuthProfile?> = _currentUser.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _savedGoogleAccounts = MutableStateFlow<List<SavedGoogleAccount>>(emptyList())
    val savedGoogleAccounts: StateFlow<List<SavedGoogleAccount>> = _savedGoogleAccounts.asStateFlow()

    init {
        _currentUser.value = null
        _isAuthenticated.value = false

        _savedGoogleAccounts.value = listOf(
            SavedGoogleAccount(name = "Lokesh Kumar", email = "lokeshkumar1232005@gmail.com", colorIndex = 0),
            SavedGoogleAccount(name = "Lokesh Developer", email = "lokesh.developer@gmail.com", colorIndex = 1)
        )
    }

    fun initialize(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        migrateLegacyPasswords()
        loadSavedAccounts()
        loadSavedSession()
    }

    // ── Session Loading & Persistence ──────────────────────────────────────────

    private fun loadSavedSession() {
        val jsonString = prefs?.getString(KEY_USER_PROFILE, null)
        if (jsonString != null) {
            try {
                val json = JSONObject(jsonString)
                val profile = UserAuthProfile(
                    id = json.optString("id", UUID.randomUUID().toString()),
                    name = json.optString("name", "Operative"),
                    email = json.optString("email", ""),
                    handle = json.optString("handle", "operative"),
                    avatarInitial = json.optString("avatarInitial", "O"),
                    avatarColorHex = json.optString("avatarColorHex", "#4285F4"),
                    provider = runCatching { AuthProvider.valueOf(json.optString("provider", "EMAIL")) }.getOrDefault(AuthProvider.EMAIL),
                    isEmailVerified = json.optBoolean("isEmailVerified", true),
                    lastLoginDate = json.optLong("lastLoginDate", System.currentTimeMillis()),
                    streakDays = json.optInt("streakDays", 1)
                )
                _currentUser.value = profile
                _isAuthenticated.value = true
                syncGlobalAppState(profile)
                return
            } catch (_: Exception) { }
        }

        // If no saved user session in prefs, user is unauthenticated
        _currentUser.value = null
        _isAuthenticated.value = false
    }

    private fun saveSession(user: UserAuthProfile?) {
        _currentUser.value = user
        _isAuthenticated.value = (user != null)

        if (user != null) {
            val json = JSONObject().apply {
                put("id", user.id)
                put("name", user.name)
                put("email", user.email)
                put("handle", user.handle)
                put("avatarInitial", user.avatarInitial)
                put("avatarColorHex", user.avatarColorHex)
                put("provider", user.provider.name)
                put("isEmailVerified", user.isEmailVerified)
                put("lastLoginDate", user.lastLoginDate)
                put("streakDays", user.streakDays)
            }
            prefs?.edit()?.putString(KEY_USER_PROFILE, json.toString())?.apply()
            syncGlobalAppState(user)
        } else {
            prefs?.edit()?.remove(KEY_USER_PROFILE)?.apply()
            LocalizationManager.shared.setLoggedIn(false)
        }
    }

    private fun syncGlobalAppState(user: UserAuthProfile) {
        LocalizationManager.shared.updateUserProfile(user.name, user.handle)
        LocalizationManager.shared.setLoggedIn(true, user.name, user.handle)
    }

    // ── Google Accounts Management ─────────────────────────────────────────────

    private fun loadSavedAccounts() {
        val raw = prefs?.getString(KEY_GOOGLE_ACCOUNTS, null)
        if (!raw.isNullOrEmpty()) {
            try {
                val array = JSONArray(raw)
                val list = mutableListOf<SavedGoogleAccount>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        SavedGoogleAccount(
                            id = obj.optString("id", UUID.randomUUID().toString()),
                            name = obj.getString("name"),
                            email = obj.getString("email"),
                            avatarInitial = obj.optString("avatarInitial", "G"),
                            colorIndex = obj.optInt("colorIndex", 0)
                        )
                    )
                }
                if (list.isNotEmpty()) {
                    _savedGoogleAccounts.value = list
                    return
                }
            } catch (_: Exception) { }
        }

        val defaultList = listOf(
            SavedGoogleAccount(name = "Lokesh Kumar", email = "lokeshkumar1232005@gmail.com", colorIndex = 0),
            SavedGoogleAccount(name = "Lokesh Developer", email = "lokesh.developer@gmail.com", colorIndex = 1)
        )
        _savedGoogleAccounts.value = defaultList
        saveGoogleAccountsList(defaultList)
    }

    fun saveGoogleAccountsList(list: List<SavedGoogleAccount>) {
        _savedGoogleAccounts.value = list
        val array = JSONArray()
        list.forEach { item ->
            val obj = JSONObject().apply {
                put("id", item.id)
                put("name", item.name)
                put("email", item.email)
                put("avatarInitial", item.avatarInitial)
                put("colorIndex", item.colorIndex)
            }
            array.put(obj)
        }
        prefs?.edit()?.putString(KEY_GOOGLE_ACCOUNTS, array.toString())?.apply()
    }

    fun addGoogleAccount(name: String, email: String) {
        val cleanEmail = email.trim().lowercase()
        val cleanName = name.trim()
        if (cleanEmail.isEmpty()) return

        val current = _savedGoogleAccounts.value.toMutableList()
        current.removeAll { it.email.lowercase() == cleanEmail }

        val resolvedName = cleanName.ifEmpty { cleanEmail.substringBefore("@").replaceFirstChar { it.uppercase() } }
        val newAccount = SavedGoogleAccount(
            name = resolvedName,
            email = cleanEmail,
            avatarInitial = resolvedName.take(1).uppercase(),
            colorIndex = current.size % 5
        )
        current.add(0, newAccount)
        saveGoogleAccountsList(current)
    }

    fun removeGoogleAccount(id: String) {
        val current = _savedGoogleAccounts.value.toMutableList()
        current.removeAll { it.id == id }
        saveGoogleAccountsList(current)
    }

    // ── Authentication Actions ─────────────────────────────────────────────────

    fun signInWithGoogle(name: String, email: String): UserAuthProfile {
        val cleanEmail = email.trim().lowercase()
        val rawName = name.trim()
        val resolvedName = rawName.ifEmpty { cleanEmail.substringBefore("@").replaceFirstChar { it.uppercase() } }
        val handlePart = cleanEmail.substringBefore("@").lowercase().ifEmpty { "google_user" }

        addGoogleAccount(resolvedName, cleanEmail)

        val userProfile = UserAuthProfile(
            name = resolvedName,
            email = cleanEmail,
            handle = handlePart,
            avatarInitial = resolvedName.take(1).uppercase(),
            avatarColorHex = "#4285F4",
            provider = AuthProvider.GOOGLE,
            isEmailVerified = true,
            lastLoginDate = System.currentTimeMillis(),
            streakDays = maxOf(LocalizationManager.shared.dayStreak.value, 1)
        )

        saveSession(userProfile)
        return userProfile
    }

    fun signInWithEmail(email: String, name: String? = null): UserAuthProfile {
        val cleanInput = email.trim()
        val resolvedName: String
        val resolvedEmail: String
        val resolvedHandle: String

        if (cleanInput.contains("@")) {
            resolvedEmail = cleanInput.lowercase()
            val part = cleanInput.substringBefore("@")
            resolvedName = name ?: part.replaceFirstChar { it.uppercase() }
            resolvedHandle = part.lowercase()
        } else {
            resolvedName = cleanInput.replaceFirstChar { it.uppercase() }
            resolvedEmail = "${cleanInput.lowercase()}@gmail.com"
            resolvedHandle = cleanInput.lowercase()
        }

        val profile = UserAuthProfile(
            name = resolvedName,
            email = resolvedEmail,
            handle = resolvedHandle,
            avatarInitial = resolvedName.take(1).uppercase(),
            avatarColorHex = "#FF6B00",
            provider = AuthProvider.EMAIL,
            isEmailVerified = true
        )

        saveSession(profile)
        return profile
    }

    fun signInWithApple(appleID: String, name: String? = null, email: String? = null): UserAuthProfile {
        val resolvedName = name ?: "Apple User"
        val resolvedEmail = email ?: "apple.${appleID.take(6)}@privaterelay.appleid.com"
        val handle = "user_${appleID.take(6)}"

        val profile = UserAuthProfile(
            id = appleID,
            name = resolvedName,
            email = resolvedEmail,
            handle = handle,
            avatarInitial = "A",
            avatarColorHex = "#000000",
            provider = AuthProvider.APPLE,
            isEmailVerified = true
        )

        saveSession(profile)
        return profile
    }

    fun signInAsGuest(): UserAuthProfile {
        val guestId = UUID.randomUUID().toString().take(6)
        val profile = UserAuthProfile(
            name = "Guest Adventurer",
            email = "guest_$guestId@ashcrypt.app",
            handle = "guest_$guestId",
            avatarInitial = "G",
            avatarColorHex = "#F5A623",
            provider = AuthProvider.GUEST,
            isEmailVerified = false
        )
        saveSession(profile)
        return profile
    }

    fun signOut() {
        saveSession(null)
    }

    /** Permanently deletes the user account and purges local storage (Google Play User Data Deletion Policy) */
    fun deleteAccount() {
        signOut()
        prefs?.edit()?.clear()?.apply()
        LocalizationManager.shared.resetProfileData()
        GameManager.instance.deleteAccountAndClearAllData()
    }

    // ── Password Management, Hashing & Migration ───────────────────────────────

    /**
     * One-time migration: reads legacy plaintext passwords (dsaUserPasswordKey, dsaUserPassword_*),
     * converts them to salted PBKDF2-HMAC-SHA256 hashes, writes to new secure keys,
     * and securely purges legacy keys permanently.
     */
    fun migrateLegacyPasswords(targetPrefs: SharedPreferences? = prefs) {
        val p = targetPrefs ?: return
        val allEntries = p.all
        val editor = p.edit()
        var modified = false

        // 1. Check global legacy key
        val legacyGlobal = p.getString(LEGACY_KEY_PASSWORD_GLOBAL, null)
        if (!legacyGlobal.isNullOrEmpty()) {
            val hash = PasswordHasher.hashPassword(legacyGlobal.toCharArray())
            editor.putString(KEY_PASSWORD_HASH_GLOBAL, hash)
            editor.remove(LEGACY_KEY_PASSWORD_GLOBAL)
            modified = true
        }

        // 2. Check per-user legacy keys
        for ((key, value) in allEntries) {
            if (key.startsWith(LEGACY_KEY_PASSWORD_PREFIX) && value is String && value.isNotEmpty()) {
                val emailOrUser = key.removePrefix(LEGACY_KEY_PASSWORD_PREFIX)
                val hash = PasswordHasher.hashPassword(value.toCharArray())
                editor.putString("$KEY_PASSWORD_HASH_PREFIX$emailOrUser", hash)
                editor.remove(key)
                modified = true
            }
        }

        if (modified) {
            editor.apply()
        }
    }

    /**
     * Hashes the password with PBKDF2-HMAC-SHA256 using a fresh cryptographically random salt.
     * Legacy plaintext keys are explicitly removed and never written again.
     */
    fun updatePassword(email: String, newPassword: String) {
        val cleanEmail = email.trim().lowercase()
        val hash = PasswordHasher.hashPassword(newPassword.toCharArray())
        val editor = prefs?.edit() ?: return

        editor.putString(KEY_PASSWORD_HASH_GLOBAL, hash)
        editor.remove(LEGACY_KEY_PASSWORD_GLOBAL)

        if (cleanEmail.isNotEmpty()) {
            editor.putString("$KEY_PASSWORD_HASH_PREFIX$cleanEmail", hash)
            editor.remove("$LEGACY_KEY_PASSWORD_PREFIX$cleanEmail")
        }
        editor.apply()
    }

    /**
     * Deprecated: Passwords are irreversibly hashed and cannot be retrieved in plaintext.
     * Always returns null to satisfy zero-plaintext-retrieval guarantees.
     */
    @Deprecated("Passwords are now irreversibly hashed with PBKDF2-HMAC-SHA256 and cannot be retrieved in plaintext.")
    fun getStoredPassword(emailOrUsername: String): String? = null

    /**
     * Verifies the input password against the stored PBKDF2 salt & hash in constant time.
     */
    fun verifyPassword(emailOrUsername: String, inputPassword: String): Boolean {
        val clean = emailOrUsername.trim().lowercase()
        val storedHash = prefs?.getString("$KEY_PASSWORD_HASH_PREFIX$clean", null)
            ?: prefs?.getString(KEY_PASSWORD_HASH_GLOBAL, null)

        if (storedHash != null) {
            return PasswordHasher.verifyPassword(inputPassword.toCharArray(), storedHash)
        }
        // Fallback for fresh test/guest accounts without an explicitly created password
        return inputPassword.length >= 4
    }
}
