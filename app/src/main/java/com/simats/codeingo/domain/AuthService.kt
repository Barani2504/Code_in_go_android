package com.simats.codeingo.domain

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

// ══════════════════════════════════════════════════════════════════
// 🔐 Authentication & Session Service
// Exact parity with iOS AuthService.swift (Multi-provider, Session,
// Password persistence, Google Accounts management)
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

        private const val PREFS_NAME = "ashnode_auth_prefs"
        private const val KEY_USER_PROFILE = "ashnode_auth_user_profile_v2"
        private const val KEY_GOOGLE_ACCOUNTS = "ashnode_saved_google_accounts_v2"
        private const val KEY_PASSWORD_GLOBAL = "dsaUserPasswordKey"
        private const val KEY_PASSWORD_PREFIX = "dsaUserPassword_"
    }

    private var prefs: SharedPreferences? = null

    private val _currentUser = MutableStateFlow<UserAuthProfile?>(null)
    val currentUser: StateFlow<UserAuthProfile?> = _currentUser.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _savedGoogleAccounts = MutableStateFlow<List<SavedGoogleAccount>>(emptyList())
    val savedGoogleAccounts: StateFlow<List<SavedGoogleAccount>> = _savedGoogleAccounts.asStateFlow()

    init {
        // Fallback default profile matching iOS
        val defaultProfile = UserAuthProfile(
            name = "Lokesh Kumar",
            email = "lokeshkumar1232005@gmail.com",
            handle = "lokesh_dev",
            avatarInitial = "L",
            avatarColorHex = "#4285F4",
            provider = AuthProvider.GOOGLE,
            isEmailVerified = true,
            lastLoginDate = System.currentTimeMillis(),
            streakDays = 1
        )
        _currentUser.value = defaultProfile
        _isAuthenticated.value = true

        _savedGoogleAccounts.value = listOf(
            SavedGoogleAccount(name = "Lokesh Kumar", email = "lokeshkumar1232005@gmail.com", colorIndex = 0),
            SavedGoogleAccount(name = "Lokesh Developer", email = "lokesh.developer@gmail.com", colorIndex = 1)
        )
    }

    fun initialize(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
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
                    name = json.optString("name", "Lokesh Kumar"),
                    email = json.optString("email", "lokeshkumar1232005@gmail.com"),
                    handle = json.optString("handle", "lokesh_dev"),
                    avatarInitial = json.optString("avatarInitial", "L"),
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

        // Use LocalizationManager state if available
        val existingName = LocalizationManager.shared.userName.value.ifEmpty { "Lokesh Kumar" }
        val existingHandle = LocalizationManager.shared.userHandle.value.ifEmpty { "lokesh_dev" }
        val fallback = UserAuthProfile(
            name = existingName,
            email = "${existingName.lowercase().replace(" ", ".")}@gmail.com",
            handle = existingHandle,
            avatarInitial = existingName.take(1).uppercase(),
            avatarColorHex = "#4285F4",
            provider = AuthProvider.GOOGLE
        )
        _currentUser.value = fallback
        _isAuthenticated.value = LocalizationManager.shared.isLoggedIn.value
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

    fun signOut() {
        saveSession(null)
    }

    // ── Password Management & Storage ──────────────────────────────────────────

    fun updatePassword(email: String, newPassword: String) {
        val cleanEmail = email.trim().lowercase()
        prefs?.edit()?.putString(KEY_PASSWORD_GLOBAL, newPassword)?.apply()
        if (cleanEmail.isNotEmpty()) {
            prefs?.edit()?.putString("$KEY_PASSWORD_PREFIX$cleanEmail", newPassword)?.apply()
        }
    }

    fun getStoredPassword(emailOrUsername: String): String? {
        val clean = emailOrUsername.trim().lowercase()
        return prefs?.getString("$KEY_PASSWORD_PREFIX$clean", null)
            ?: prefs?.getString(KEY_PASSWORD_GLOBAL, null)
    }

    fun verifyPassword(emailOrUsername: String, inputPassword: String): Boolean {
        val stored = getStoredPassword(emailOrUsername)
        if (stored != null) {
            return stored == inputPassword
        }
        return inputPassword.length >= 4
    }
}
