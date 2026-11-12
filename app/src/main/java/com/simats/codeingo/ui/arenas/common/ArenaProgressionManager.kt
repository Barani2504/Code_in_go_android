package com.simats.codeingo.ui.arenas.common

import android.content.Context
import android.content.SharedPreferences
import com.simats.codeingo.domain.ArenaCompletionUseCase
import com.simats.codeingo.domain.AuthService
import com.simats.codeingo.domain.LocalizationManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ArenaEntryPoint {
    fun arenaCompletionUseCase(): ArenaCompletionUseCase
}

object ArenaProgressionManager {
    private var legacyChecked = false

    fun getPrefs(context: Context): SharedPreferences {
        if (!legacyChecked) {
            try {
                // Wipe any old global legacy unisolated preferences
                context.getSharedPreferences("arena_progression_prefs", Context.MODE_PRIVATE).edit().clear().apply()
                context.getSharedPreferences("arena_prefs", Context.MODE_PRIVATE).edit().clear().apply()
            } catch (_: Exception) {}
            legacyChecked = true
        }

        val activeUserId = AuthService.shared.currentUserId
            ?: AuthService.shared.currentUser.value?.email
            ?: LocalizationManager.instance.userHandle.value
        val userKey = if (!activeUserId.isNullOrBlank()) {
            activeUserId.lowercase().replace(Regex("[^a-z0-9_]"), "_")
        } else {
            "guest"
        }
        return context.getSharedPreferences("cns_arena_prefs_$userKey", Context.MODE_PRIVATE)
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

