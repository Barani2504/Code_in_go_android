package com.simats.codeingo.domain

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// ══════════════════════════════════════════════════════════════════
// 🌓 ThemeManager — Dark Mode, Light Mode & System Default
// Exact parity with iOS ThemeManager.swift
// ══════════════════════════════════════════════════════════════════

enum class AppTheme(val rawValue: String, val title: String, val iconName: String) {
    LIGHT("Light", "Light Mode", "sun.max.fill"),
    DARK("Dark", "Dark Mode", "moon.fill"),
    SYSTEM("System", "System Default", "circle.righthalf.filled");

    companion object {
        fun fromRaw(raw: String?): AppTheme {
            return entries.find { it.rawValue.equals(raw, ignoreCase = true) } ?: DARK
        }
    }
}

class ThemeManager private constructor() {

    companion object {
        val instance: ThemeManager by lazy { ThemeManager() }
        val shared: ThemeManager get() = instance
        private const val PREFS_NAME = "codeingo_theme_prefs"
        private const val KEY_THEME = "app_theme_mode_preference_key"
    }

    private var prefs: SharedPreferences? = null

    private val _currentTheme = MutableStateFlow(AppTheme.DARK)
    val currentTheme: StateFlow<AppTheme> = _currentTheme.asStateFlow()

    fun initialize(context: Context) {
        val appContext = context.applicationContext
        prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedRaw = prefs?.getString(KEY_THEME, AppTheme.DARK.rawValue)
        _currentTheme.value = AppTheme.fromRaw(savedRaw)
    }

    fun setTheme(theme: AppTheme) {
        _currentTheme.value = theme
        prefs?.edit()?.putString(KEY_THEME, theme.rawValue)?.apply()
    }

    fun toggleTheme(isCurrentlyDark: Boolean) {
        setTheme(if (isCurrentlyDark) AppTheme.LIGHT else AppTheme.DARK)
    }

    fun toggleDarkMode(toDark: Boolean) {
        setTheme(if (toDark) AppTheme.DARK else AppTheme.LIGHT)
    }
}
