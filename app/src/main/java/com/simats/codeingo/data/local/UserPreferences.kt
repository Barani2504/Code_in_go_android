package com.simats.codeingo.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ashcrypt_prefs")

data class GameStateSnapshot(
    val totalXP: Int,
    val gemsCount: Int,
    val heartsCount: Int,
    val streakDays: Int,
    val activePhoenixStage: Int,
    val highestCombo: Int,
    val eggCrackLevel: Int,
    val lastPracticedTimestampMs: Long,
    val lastPlayedDateMs: Long,
    val completedLessons: Set<String>,
    val completedBosses: Set<String>,
    val unlockedLevels: Set<Int>,
    val maxUnlockedChapter: Int,
    val completedChapters: Set<Int>,
    val completedLevelIndices: Set<Int>,
    val activeLevelIndex: Int,
    val basecampCompletedLevels: Set<String>,
    val diagnosticTaken: Boolean,
    val diagnosticRecommendedChapter: Int,
    val lastSyncedServerGems: Int = 0
)

class UserPreferences(private val context: Context) : UserProgressSource {

    companion object {
        val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val KEY_USER_NAME = stringPreferencesKey("user_name")
        val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        val KEY_SELECTED_LANGUAGE = stringPreferencesKey("selected_language")

        val KEY_TOTAL_XP = intPreferencesKey("dsa_total_xp")
        val KEY_GEMS = intPreferencesKey("dsa_gems")
        val KEY_HEARTS = intPreferencesKey("dsa_hearts")
        val KEY_STREAK = intPreferencesKey("dsa_streak_days")
        val KEY_PHOENIX_STAGE = intPreferencesKey("active_phoenix_stage")
        val KEY_HIGHEST_COMBO = intPreferencesKey("dsa_highest_combo")
        val KEY_EGG_CRACK = intPreferencesKey("dsa_egg_crack_level")

        val KEY_COMPLETED_LESSONS = stringSetPreferencesKey("completed_lessons")
        val KEY_COMPLETED_BOSSES = stringSetPreferencesKey("completed_bosses")
        val KEY_UNLOCKED_LEVELS = stringSetPreferencesKey("unlocked_levels")
        val KEY_MAX_UNLOCKED_CHAPTER = intPreferencesKey("dsa_shared_max_unlocked_chapter")
        val KEY_COMPLETED_CHAPTERS = stringSetPreferencesKey("dsa_shared_completed_chapters")
        val KEY_COMPLETED_LEVEL_INDICES = stringSetPreferencesKey("home_completed_levels")
        val KEY_ACTIVE_LEVEL_INDEX = intPreferencesKey("home_active_level_index")
        val KEY_LAST_PRACTICED_MS = longPreferencesKey("dsa_last_practiced_timestamp_ms")
        val KEY_LAST_PLAYED_MS = longPreferencesKey("dsa_last_played_date_ms")

        // ── Chapter 0 Basecamp & Diagnostic Placement Keys ─────────
        val KEY_BASECAMP_COMPLETED_LEVELS = stringSetPreferencesKey("basecamp_completed_levels")
        val KEY_DIAGNOSTIC_TAKEN = booleanPreferencesKey("diagnostic_taken")
        val KEY_DIAGNOSTIC_RECOMMENDED_CHAPTER = intPreferencesKey("diagnostic_recommended_chapter")

        // ── Currency Reconciliation Key ───────────────────────────
        val KEY_LAST_SYNCED_SERVER_GEMS = intPreferencesKey("last_synced_server_gems")
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { it[KEY_IS_LOGGED_IN] ?: false }
    val userName: Flow<String> = context.dataStore.data.map { it[KEY_USER_NAME] ?: "Guest" }
    val userEmail: Flow<String> = context.dataStore.data.map { it[KEY_USER_EMAIL] ?: "" }
    val selectedLanguage: Flow<String> = context.dataStore.data.map { it[KEY_SELECTED_LANGUAGE] ?: "python" }

    val totalXP: Flow<Int> = context.dataStore.data.map { it[KEY_TOTAL_XP] ?: 120 }
    val gemsCount: Flow<Int> = context.dataStore.data.map { it[KEY_GEMS] ?: 450 }
    val heartsCount: Flow<Int> = context.dataStore.data.map { it[KEY_HEARTS] ?: 10 }
    val streakDays: Flow<Int> = context.dataStore.data.map { it[KEY_STREAK] ?: 3 }
    val activePhoenixStage: Flow<Int> = context.dataStore.data.map { it[KEY_PHOENIX_STAGE] ?: 1 }
    val highestCombo: Flow<Int> = context.dataStore.data.map { it[KEY_HIGHEST_COMBO] ?: 5 }
    val eggCrackLevel: Flow<Int> = context.dataStore.data.map { it[KEY_EGG_CRACK] ?: 0 }

    val lastPracticedTimestampMs: Flow<Long> = context.dataStore.data.map { it[KEY_LAST_PRACTICED_MS] ?: 0L }
    val lastPlayedDateMs: Flow<Long> = context.dataStore.data.map { it[KEY_LAST_PLAYED_MS] ?: 0L }

    val completedLessons: Flow<Set<String>> = context.dataStore.data.map {
        it[KEY_COMPLETED_LESSONS] ?: setOf("u1_l1_what_is_ds")
    }
    val completedBosses: Flow<Set<String>> = context.dataStore.data.map {
        it[KEY_COMPLETED_BOSSES] ?: emptySet()
    }
    val unlockedLevels: Flow<Set<String>> = context.dataStore.data.map {
        it[KEY_UNLOCKED_LEVELS] ?: setOf("1")
    }
    val maxUnlockedChapter: Flow<Int> = context.dataStore.data.map {
        it[KEY_MAX_UNLOCKED_CHAPTER] ?: 1
    }
    val completedChapters: Flow<Set<String>> = context.dataStore.data.map {
        it[KEY_COMPLETED_CHAPTERS] ?: emptySet()
    }
    override val completedLevelIndices: Flow<Set<String>> = context.dataStore.data.map {
        it[KEY_COMPLETED_LEVEL_INDICES] ?: emptySet()
    }
    val activeLevelIndex: Flow<Int> = context.dataStore.data.map {
        it[KEY_ACTIVE_LEVEL_INDEX] ?: 1
    }

    override val basecampCompletedLevels: Flow<Set<String>> = context.dataStore.data.map {
        it[KEY_BASECAMP_COMPLETED_LEVELS] ?: emptySet()
    }
    val diagnosticTaken: Flow<Boolean> = context.dataStore.data.map {
        it[KEY_DIAGNOSTIC_TAKEN] ?: false
    }
    val diagnosticRecommendedChapter: Flow<Int> = context.dataStore.data.map {
        it[KEY_DIAGNOSTIC_RECOMMENDED_CHAPTER] ?: 1
    }
    val lastSyncedServerGems: Flow<Int> = context.dataStore.data.map {
        it[KEY_LAST_SYNCED_SERVER_GEMS] ?: 0
    }

    val gameStateSnapshot: Flow<GameStateSnapshot> = context.dataStore.data.map { prefs ->
        val completedLevels = (prefs[KEY_COMPLETED_LEVEL_INDICES] ?: emptySet()).mapNotNull { it.toIntOrNull() }.toSet()
        val completedChaps = (prefs[KEY_COMPLETED_CHAPTERS] ?: emptySet()).mapNotNull { it.toIntOrNull() }.toSet()
        val storedMaxChap = prefs[KEY_MAX_UNLOCKED_CHAPTER] ?: 1

        val legitimateMaxChapter = when {
            completedLevels.contains(25) || completedChaps.contains(5) -> maxOf(6, storedMaxChap)
            completedLevels.contains(20) || completedChaps.contains(4) -> maxOf(5, storedMaxChap.coerceAtMost(5))
            completedLevels.contains(15) || completedChaps.contains(3) -> maxOf(4, storedMaxChap.coerceAtMost(4))
            completedLevels.contains(10) || completedChaps.contains(2) -> maxOf(3, storedMaxChap.coerceAtMost(3))
            completedLevels.contains(5) || completedChaps.contains(1) -> maxOf(2, storedMaxChap.coerceAtMost(2))
            else -> 1
        }

        val savedActive = prefs[KEY_ACTIVE_LEVEL_INDEX] ?: 1
        val resolvedActive = if (legitimateMaxChapter == 1 && savedActive !in 1..5) 1 else savedActive

        GameStateSnapshot(
            totalXP = prefs[KEY_TOTAL_XP] ?: 120,
            gemsCount = prefs[KEY_GEMS] ?: 450,
            heartsCount = prefs[KEY_HEARTS] ?: 10,
            streakDays = prefs[KEY_STREAK] ?: 3,
            activePhoenixStage = prefs[KEY_PHOENIX_STAGE] ?: 1,
            highestCombo = prefs[KEY_HIGHEST_COMBO] ?: 5,
            eggCrackLevel = prefs[KEY_EGG_CRACK] ?: 0,
            lastPracticedTimestampMs = prefs[KEY_LAST_PRACTICED_MS] ?: 0L,
            lastPlayedDateMs = prefs[KEY_LAST_PLAYED_MS] ?: 0L,
            completedLessons = prefs[KEY_COMPLETED_LESSONS] ?: setOf("u1_l1_what_is_ds"),
            completedBosses = prefs[KEY_COMPLETED_BOSSES] ?: emptySet(),
            unlockedLevels = (prefs[KEY_UNLOCKED_LEVELS] ?: setOf("1")).mapNotNull { it.toIntOrNull() }.toSet().ifEmpty { setOf(1) },
            maxUnlockedChapter = legitimateMaxChapter,
            completedChapters = completedChaps,
            completedLevelIndices = completedLevels,
            activeLevelIndex = resolvedActive,
            basecampCompletedLevels = prefs[KEY_BASECAMP_COMPLETED_LEVELS] ?: emptySet(),
            diagnosticTaken = prefs[KEY_DIAGNOSTIC_TAKEN] ?: false,
            diagnosticRecommendedChapter = prefs[KEY_DIAGNOSTIC_RECOMMENDED_CHAPTER] ?: 1,
            lastSyncedServerGems = prefs[KEY_LAST_SYNCED_SERVER_GEMS] ?: 0
        )
    }

    suspend fun setLastSyncedServerGems(gems: Int) {
        context.dataStore.edit {
            it[KEY_LAST_SYNCED_SERVER_GEMS] = gems
        }
    }

    suspend fun markBasecampLevelCompleted(levelId: String) {
        context.dataStore.edit {
            val current = (it[KEY_BASECAMP_COMPLETED_LEVELS] ?: emptySet()).toMutableSet()
            current.add(levelId)
            it[KEY_BASECAMP_COMPLETED_LEVELS] = current
        }
    }

    suspend fun setDiagnosticResult(recommendedChapter: Int) {
        context.dataStore.edit {
            it[KEY_DIAGNOSTIC_TAKEN] = true
            it[KEY_DIAGNOSTIC_RECOMMENDED_CHAPTER] = recommendedChapter
        }
    }

    suspend fun setLoggedIn(loggedIn: Boolean, name: String = "", email: String = "") {
        context.dataStore.edit {
            it[KEY_IS_LOGGED_IN] = loggedIn
            if (name.isNotEmpty()) it[KEY_USER_NAME] = name
            if (email.isNotEmpty()) it[KEY_USER_EMAIL] = email
        }
    }

    suspend fun setSelectedLanguage(code: String) {
        context.dataStore.edit { it[KEY_SELECTED_LANGUAGE] = code }
    }

    suspend fun batchUpdateGameState(
        xp: Int,
        gems: Int,
        hearts: Int,
        streak: Int,
        phoenixStage: Int,
        highestCombo: Int,
        eggCrack: Int,
        lastPracticedMs: Long,
        lastPlayedMs: Long
    ) {
        context.dataStore.edit {
            it[KEY_TOTAL_XP] = xp
            it[KEY_GEMS] = gems
            it[KEY_HEARTS] = hearts
            it[KEY_STREAK] = streak
            it[KEY_PHOENIX_STAGE] = phoenixStage
            it[KEY_HIGHEST_COMBO] = highestCombo
            it[KEY_EGG_CRACK] = eggCrack
            if (lastPracticedMs > 0L) it[KEY_LAST_PRACTICED_MS] = lastPracticedMs
            if (lastPlayedMs > 0L) it[KEY_LAST_PLAYED_MS] = lastPlayedMs
        }
    }

    suspend fun updateGameState(
        xp: Int? = null,
        gems: Int? = null,
        hearts: Int? = null,
        streak: Int? = null,
        phoenixStage: Int? = null,
        highestCombo: Int? = null,
        eggCrack: Int? = null
    ) {
        context.dataStore.edit {
            xp?.let { v -> it[KEY_TOTAL_XP] = v }
            gems?.let { v -> it[KEY_GEMS] = v }
            hearts?.let { v -> it[KEY_HEARTS] = v }
            streak?.let { v -> it[KEY_STREAK] = v }
            phoenixStage?.let { v -> it[KEY_PHOENIX_STAGE] = v }
            highestCombo?.let { v -> it[KEY_HIGHEST_COMBO] = v }
            eggCrack?.let { v -> it[KEY_EGG_CRACK] = v }
        }
    }

    suspend fun completeLesson(lessonId: String, nextLevelIndex: Int? = null) {
        context.dataStore.edit {
            val lessons = (it[KEY_COMPLETED_LESSONS] ?: setOf("u1_l1_what_is_ds")).toMutableSet()
            lessons.add(lessonId)
            it[KEY_COMPLETED_LESSONS] = lessons

            if (nextLevelIndex != null) {
                val levels = (it[KEY_UNLOCKED_LEVELS] ?: setOf("1")).toMutableSet()
                levels.add(nextLevelIndex.toString())
                it[KEY_UNLOCKED_LEVELS] = levels
            }
        }
    }

    suspend fun completeBoss(bossId: String) {
        context.dataStore.edit {
            val bosses = (it[KEY_COMPLETED_BOSSES] ?: emptySet()).toMutableSet()
            bosses.add(bossId)
            it[KEY_COMPLETED_BOSSES] = bosses
        }
    }

    suspend fun setMaxUnlockedChapter(chapterId: Int) {
        context.dataStore.edit {
            val current = it[KEY_MAX_UNLOCKED_CHAPTER] ?: 1
            it[KEY_MAX_UNLOCKED_CHAPTER] = maxOf(current, chapterId)
        }
    }

    suspend fun markChapterCompleted(chapterId: Int) {
        context.dataStore.edit {
            val current = (it[KEY_COMPLETED_CHAPTERS] ?: emptySet()).toMutableSet()
            current.add(chapterId.toString())
            it[KEY_COMPLETED_CHAPTERS] = current
        }
    }

    suspend fun setActiveLevelIndex(level: Int) {
        context.dataStore.edit {
            it[KEY_ACTIVE_LEVEL_INDEX] = level
        }
    }

    suspend fun markLevelCompleted(level: Int) {
        context.dataStore.edit {
            val current = (it[KEY_COMPLETED_LEVEL_INDICES] ?: emptySet()).toMutableSet()
            current.add(level.toString())
            it[KEY_COMPLETED_LEVEL_INDICES] = current
        }
    }

    suspend fun resetChapterProgression() {
        context.dataStore.edit {
            it[KEY_MAX_UNLOCKED_CHAPTER] = 1
            it[KEY_COMPLETED_CHAPTERS] = emptySet()
            it[KEY_COMPLETED_LEVEL_INDICES] = emptySet()
            it[KEY_ACTIVE_LEVEL_INDEX] = 1
            it[KEY_UNLOCKED_LEVELS] = setOf("1")
            it[KEY_LAST_SYNCED_SERVER_GEMS] = 0
        }
    }

    suspend fun updatePracticeTimestamps(lastPracticedMs: Long, lastPlayedMs: Long) {
        context.dataStore.edit {
            it[KEY_LAST_PRACTICED_MS] = lastPracticedMs
            it[KEY_LAST_PLAYED_MS] = lastPlayedMs
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit {
            it[KEY_IS_LOGGED_IN] = false
            it[KEY_USER_NAME] = "Guest"
            it[KEY_USER_EMAIL] = ""
        }
    }

    suspend fun clearAllUserData() {
        context.dataStore.edit {
            it.clear()
        }
    }
}
