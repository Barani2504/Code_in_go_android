package com.simats.codeingo.domain

import com.simats.codeingo.data.model.Language
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// ══════════════════════════════════════════════════════════════════
// 🌐 LocalizationManager — Language & User Session Profile State
// Exact parity with iOS LocalizationManager.swift
// ══════════════════════════════════════════════════════════════════
class LocalizationManager private constructor() {

    companion object {
        val instance: LocalizationManager by lazy { LocalizationManager() }
        val shared: LocalizationManager get() = instance
    }

    private val _selectedLanguage = MutableStateFlow<Language?>(null)
    val selectedLanguage: StateFlow<Language?> = _selectedLanguage.asStateFlow()

    private val _selectedLanguageCode = MutableStateFlow("en")
    val selectedLanguageCode: StateFlow<String> = _selectedLanguageCode.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userName = MutableStateFlow("Vishal Rao")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userHandle = MutableStateFlow("VishalRao3454")
    val userHandle: StateFlow<String> = _userHandle.asStateFlow()

    private val _dayStreak = MutableStateFlow(1)
    val dayStreak: StateFlow<Int> = _dayStreak.asStateFlow()

    private var savedLastOpenDateStr: String? = null
    private var savedStreakCount: Int = 1

    val currentLanguageCode: String
        get() = _selectedLanguageCode.value

    init {
        checkAndUpdateStreak()
    }

    fun setSelectedLanguage(language: Language?) {
        _selectedLanguage.value = language
        if (language != null) {
            _selectedLanguageCode.value = language.code
        }
    }

    fun selectLanguage(code: String) {
        _selectedLanguageCode.value = code
        val match = Language.defaultLanguages.find { it.code == code }
        _selectedLanguage.value = match
    }

    fun updateUserProfile(name: String, handle: String) {
        if (name.isNotEmpty()) _userName.value = name
        if (handle.isNotEmpty()) _userHandle.value = handle
    }

    fun setLoggedIn(loggedIn: Boolean, name: String = "", handle: String = "") {
        _isLoggedIn.value = loggedIn
        if (name.isNotEmpty()) _userName.value = name
        if (handle.isNotEmpty()) _userHandle.value = handle
    }

    fun resetProfileData() {
        _isLoggedIn.value = false
        _userName.value = ""
        _userHandle.value = ""
    }

    fun checkAndUpdateStreak() {
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val todayStr = formatter.format(Date())

        if (savedLastOpenDateStr != null) {
            try {
                val savedDate = formatter.parse(savedLastOpenDateStr!!)
                val savedCal = Calendar.getInstance().apply { time = savedDate!! }

                val nowDay = calendar.get(Calendar.DAY_OF_YEAR)
                val nowYear = calendar.get(Calendar.YEAR)
                val lastDay = savedCal.get(Calendar.DAY_OF_YEAR)
                val lastYear = savedCal.get(Calendar.YEAR)

                val daysDiff = if (nowYear == lastYear) {
                    nowDay - lastDay
                } else if (nowYear == lastYear + 1 && lastDay >= 365) {
                    nowDay
                } else {
                    2
                }

                if (daysDiff == 0) {
                    _dayStreak.value = maxOf(savedStreakCount, 1)
                } else if (daysDiff == 1) {
                    val newStreak = (if (savedStreakCount > 0) savedStreakCount else 1) + 1
                    _dayStreak.value = newStreak
                    savedStreakCount = newStreak
                    savedLastOpenDateStr = todayStr
                } else {
                    _dayStreak.value = 1
                    savedStreakCount = 1
                    savedLastOpenDateStr = todayStr
                }
            } catch (e: Exception) {
                _dayStreak.value = 1
                savedStreakCount = 1
                savedLastOpenDateStr = todayStr
            }
        } else {
            _dayStreak.value = 1
            savedStreakCount = 1
            savedLastOpenDateStr = todayStr
        }
    }

    fun simulateNextDayOpening() {
        val newStreak = _dayStreak.value + 1
        _dayStreak.value = newStreak
        savedStreakCount = newStreak
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        savedLastOpenDateStr = formatter.format(Date())
    }

    fun simulateMissedDaysReset() {
        _dayStreak.value = 0
        savedStreakCount = 0
        savedLastOpenDateStr = null
    }

    // Dynamic localization translation lookup dictionary matching iOS
    private val translations: Map<String, Map<String, String>> = mapOf(
        "en" to mapOf(
            "site_language" to "SITE LANGUAGE:",
            "choose_language" to "CHOOSE LANGUAGE",
            "what_language_do_you_speak" to "What language do you speak?",
            "choose_interface_lang" to "Choose your site and interface language",
            "login_caps" to "LOGIN",
            "signup_caps" to "SIGN UP",
            "get_started_caps" to "GET STARTED",
            "already_have_account_caps" to "I ALREADY HAVE AN ACCOUNT",
            "duolingo_on_the_go" to "Duolingo on the go",
            "hero_subtitle" to "The free, fun, and effective way to learn a language!",
            "why_learn" to "Why learn with Duolingo?",
            "effective_fun" to "Effective & Fun",
            "effective_subtitle" to "Gamified lessons keep you motivated.",
            "personalized" to "Personalized",
            "personalized_subtitle" to "AI tailored to your learning pace.",
            "login_title" to "Log in",
            "create_profile" to "Create profile",
            "email_or_username" to "Email or username",
            "password" to "Password",
            "forgot" to "FORGOT?",
            "name_optional" to "Name (optional)",
            "age" to "Age",
            "create_account" to "CREATE ACCOUNT",
            "terms_privacy" to "By signing in to Duolingo, you agree to our Terms and Privacy Policy.",
            "recaptcha" to "This site is protected by reCAPTCHA Enterprise and the Google Privacy Policy and Terms of Service apply.",
            "site_language_modal" to "Site Language",
            "done" to "Done",
            "google_auth" to "GOOGLE",
            "facebook_auth" to "FACEBOOK",
            "use_another_account" to "USE ANOTHER ACCOUNT",
            "signed_in_google" to "Signed in with Google as %s",
            "welcome_back" to "Welcome back! Logging in...",
            "account_created" to "Account created successfully!",
            "enter_email" to "Please enter your email or username",
            "password_short" to "Password must be at least 4 characters",
            "loading" to "LOADING...",
            "how_much_know" to "How much %s do you know?",
            "new_to_lang" to "I'm new to %s",
            "know_common_words" to "I know some common words",
            "basic_conversations" to "I can have basic conversations",
            "various_topics" to "I can talk about various topics",
            "topics_in_detail" to "I can discuss most topics in detail",
            "continue_caps" to "CONTINUE",
            "section_1_unit_1" to "SECTION 1, UNIT 1",
            "section_1_unit_2" to "SECTION 1, UNIT 2",
            "section_1_unit_3" to "SECTION 1, UNIT 3",
            "section_1_unit_4" to "SECTION 1, UNIT 4",
            "section_1_unit_5" to "SECTION 1, UNIT 5",
            "guidebook" to "DOCS",
            "start" to "RUN",
            "learn" to "CODE",
            "letters" to "VIDEOS",
            "leaderboards" to "RANKINGS",
            "quests" to "ISSUES",
            "shop" to "MARKETPLACE",
            "profile" to "DEVELOPER",
            "more" to "CONSOLE",
            "unlock_leaderboards" to "Unlock Rankings!"
        )
    )

    fun t(key: String, vararg args: Any): String {
        val langMap = translations[currentLanguageCode] ?: translations["en"] ?: emptyMap()
        val template = langMap[key] ?: key
        return if (args.isNotEmpty()) {
            try {
                String.format(template, *args)
            } catch (e: Exception) {
                template
            }
        } else {
            template
        }
    }

    fun string(key: String, vararg args: Any): String = t(key, *args)
}
