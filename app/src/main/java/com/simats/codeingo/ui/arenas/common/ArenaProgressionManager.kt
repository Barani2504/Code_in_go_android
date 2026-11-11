package com.simats.codeingo.ui.arenas.common

import android.content.Context
import android.content.SharedPreferences
import com.simats.codeingo.domain.AuthService
import com.simats.codeingo.domain.LocalizationManager

object ArenaProgressionManager {
    fun getPrefs(context: Context): SharedPreferences {
        val activeEmail = AuthService.shared.currentUser.value?.email
            ?: LocalizationManager.instance.userHandle.value
        val userKey = if (activeEmail.isNotBlank()) {
            activeEmail.lowercase().replace(Regex("[^a-z0-9_]"), "_")
        } else {
            "guest"
        }
        return context.getSharedPreferences("arena_progression_prefs_$userKey", Context.MODE_PRIVATE)
    }

    fun getUnlockedLevels(prefs: SharedPreferences, arenaKey: String): Set<Int> {
        val saved = prefs.getStringSet(arenaKey, null)
            ?.mapNotNull { it.toIntOrNull() }?.toSet() ?: setOf(1)
        return if (saved.isEmpty()) setOf(1) else saved
    }

    fun saveUnlockedLevels(prefs: SharedPreferences, arenaKey: String, levels: Set<Int>) {
        prefs.edit().putStringSet(arenaKey, levels.map { it.toString() }.toSet()).apply()
    }
}

