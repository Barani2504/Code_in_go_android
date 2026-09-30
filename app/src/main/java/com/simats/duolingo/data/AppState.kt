package com.simats.duolingo.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Singleton app state – replicates iOS LocalizationManager and AppStorage state.
 * Holds selected language, auth state, streak, hearts timer, phoenix companion stage, unlocked levels, etc.
 */
object AppState {
    var selectedLanguage: Language? by mutableStateOf(null)
    var isLoggedIn: Boolean by mutableStateOf(false)
    var userName: String by mutableStateOf("Vishal Rao")
    var userHandle: String by mutableStateOf("vishalrao3454")
    var dayStreak: Int by mutableIntStateOf(1)

    // Phoenix Sanctuary Stage (1 to 18)
    var activePhoenixStage: Int by mutableIntStateOf(1)

    // Heart Lives & 5-Minute Regeneration Timer State
    var heartsCount: Int by mutableIntStateOf(5)
    var heartTimerRemainingSeconds: Int by mutableIntStateOf(300)
    var isHeartTimerActive: Boolean by mutableStateOf(false)

    // Unlocked Level Progression State (Units 1-5 start levels unlocked: 1, 7, 13, 18, 23)
    var unlockedLevelIndices: Set<Int> by mutableStateOf(setOf(1, 7, 13, 18, 23))
    var activeLevelIndex: Int by mutableIntStateOf(1)

    // Logout and General Toast message
    var toastMessage: String? by mutableStateOf(null)

    // Active Phoenix Stage Data helper
    val activePhoenix: PhoenixStageData
        get() = allPhoenixStages.firstOrNull { it.id == activePhoenixStage } ?: allPhoenixStages.first()

    fun resetProfileData() {
        isLoggedIn = false
        userName = ""
        userHandle = ""
    }

    fun simulateNextDayOpening() {
        dayStreak += 1
    }

    fun simulateMissedDaysReset() {
        dayStreak = 0
    }

    fun loseHeart() {
        if (heartsCount > 0) {
            heartsCount -= 1
        }
        if (heartsCount < 5 && !isHeartTimerActive) {
            isHeartTimerActive = true
            heartTimerRemainingSeconds = 300
        }
    }

    fun tickHeartTimer() {
        if (heartsCount < 5) {
            if (!isHeartTimerActive) {
                isHeartTimerActive = true
                if (heartTimerRemainingSeconds <= 0) {
                    heartTimerRemainingSeconds = 300
                }
            }
            if (isHeartTimerActive) {
                if (heartTimerRemainingSeconds > 0) {
                    heartTimerRemainingSeconds -= 1
                } else {
                    heartsCount = 5
                    isHeartTimerActive = false
                    heartTimerRemainingSeconds = 300
                }
            }
        } else {
            isHeartTimerActive = false
            heartTimerRemainingSeconds = 300
        }
    }

    fun unlockNextLevel() {
        val nextLevel = activeLevelIndex + 1
        unlockedLevelIndices = unlockedLevelIndices + nextLevel
        activeLevelIndex = nextLevel
    }
}
