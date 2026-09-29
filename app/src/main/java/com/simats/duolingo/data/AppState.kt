package com.simats.duolingo.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Singleton app state – replaces iOS LocalizationManager.shared.
 * Holds the selected language, logged-in user, streak, etc.
 */
object AppState {
    var selectedLanguage: Language? by mutableStateOf(null)
    var isLoggedIn: Boolean        by mutableStateOf(false)
    var userName: String           by mutableStateOf("Vishal Rao")
    var userHandle: String         by mutableStateOf("vishalrao3454")
    var dayStreak: Int             by mutableStateOf(0)
}
