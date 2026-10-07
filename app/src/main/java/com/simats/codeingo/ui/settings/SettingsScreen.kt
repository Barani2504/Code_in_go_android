package com.simats.codeingo.ui.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.domain.AppTheme
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.domain.LocalizationManager
import com.simats.codeingo.domain.PhoenixEmotionManager
import com.simats.codeingo.domain.ThemeManager
import com.simats.codeingo.ui.phoenix.PhoenixAtmosphericBackgroundView
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DuolingoBlue
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ══════════════════════════════════════════════════════════════════
// ⚙️  SettingsScreen — Full 4-Tab parity with iOS SettingsView.swift
// Tabs: Preferences | Notifications | Courses | Account
// Seamlessly adapts to Light, Dark, and System Theme modes
// ══════════════════════════════════════════════════════════════════

private val tabTitles = listOf("Preferences", "Notifications", "Courses", "Account")

@Composable
fun SettingsScreen(
    onDismiss: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    // ── Singletons ──────────────────────────────────────────────
    val themeManager = ThemeManager.instance
    val emotionManager = PhoenixEmotionManager.instance
    val locManager = LocalizationManager.instance
    val gameManager = GameManager.instance

    // ── Collected state ─────────────────────────────────────────
    val currentTheme by themeManager.currentTheme.collectAsState()
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (currentTheme) {
        AppTheme.DARK -> true
        AppTheme.LIGHT -> false
        AppTheme.SYSTEM -> isSystemDark
    }

    val dynamicColors = LocalDynamicThemeColors.current

    val isAutoEmotion by emotionManager.isAutoEmotionEnabled.collectAsState()
    val userName by locManager.userName.collectAsState()
    val userHandle by locManager.userHandle.collectAsState()
    val selectedLang by locManager.selectedLanguage.collectAsState()
    val streakDays by gameManager.streakDays.collectAsState()

    // ── Tab state ────────────────────────────────────────────────
    var selectedTab by remember { mutableIntStateOf(0) }

    // ── Lesson Experience toggles ─────────────────────────────────
    var soundEffects by remember { mutableStateOf(true) }
    var animations by remember { mutableStateOf(true) }
    var motivationalMessages by remember { mutableStateOf(true) }
    var listeningExercises by remember { mutableStateOf(true) }

    // ── Notification toggles (General) ───────────────────────────
    var notifLessons by remember { mutableStateOf(true) }
    var notifStreakAlerts by remember { mutableStateOf(true) }
    var notifAchievements by remember { mutableStateOf(false) }
    var notifLeaderboard by remember { mutableStateOf(false) }
    var notifTips by remember { mutableStateOf(true) }
    var notifUpdates by remember { mutableStateOf(false) }

    // ── Daily reminder ────────────────────────────────────────────
    var dailyReminderEnabled by remember { mutableStateOf(true) }
    var practiceTime by remember { mutableStateOf("9:00 AM") }
    var showTimePicker by remember { mutableStateOf(false) }

    // ── Reset alert / toast ───────────────────────────────────────
    var showResetAlert by remember { mutableStateOf(false) }
    var showResetToast by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // ── Dynamic Theme adaptive colors ────────────────────────────
    val textPrimary = dynamicColors.textPrimary
    val textSecondary = dynamicColors.textSecondary
    val cardBg = dynamicColors.cardBackground
    val cardBorder = dynamicColors.inputBorder

    // ── Reset progress alert dialog ──────────────────────────────
    if (showResetAlert) {
        AlertDialog(
            onDismissRequest = { showResetAlert = false },
            title = {
                Text(
                    "Reset Progress",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = textPrimary
                )
            },
            text = {
                Text(
                    "Are you sure you want to reset all your progress in this course? This cannot be undone.",
                    fontSize = 14.sp,
                    color = textSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showResetAlert = false
                    gameManager.resetAllProgress()
                    scope.launch {
                        showResetToast = true
                        delay(2500)
                        showResetToast = false
                    }
                }) {
                    Text("RESET", color = DuolingoRed, fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetAlert = false }) {
                    Text("CANCEL", color = DuolingoBlue, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = cardBg
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Living Phoenix Atmospheric Background with Light/Dark transition
        PhoenixAtmosphericBackgroundView()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // ── Top nav bar ───────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = textSecondary
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "SETTINGS",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = textPrimary
                )
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = onDismiss) {
                    Text(
                        text = "Done",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = DuolingoBlue
                    )
                }
            }

            // ── Tab switcher ──────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(cardBg)
                    .border(1.dp, cardBorder, RoundedCornerShape(14.dp)),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                tabTitles.forEachIndexed { index, label ->
                    val isSelected = selectedTab == index
                    val tabBg by animateColorAsState(
                        targetValue = if (isSelected) DuolingoBlue else Color.Transparent,
                        animationSpec = tween(200), label = "tabBg"
                    )
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                        color = if (isSelected) Color.White else textSecondary,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(tabBg)
                            .clickable { selectedTab = index }
                            .padding(vertical = 10.dp, horizontal = 4.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Tab content ───────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                when (selectedTab) {
                    0 -> PreferencesTab(
                        currentTheme = currentTheme,
                        isDark = isDark,
                        onThemeChange = { themeManager.setTheme(it) },
                        isAutoEmotion = isAutoEmotion,
                        onAutoEmotionChange = { emotionManager.setAutoEmotionEnabled(it) },
                        soundEffects = soundEffects,
                        onSoundEffectsChange = { soundEffects = it },
                        animations = animations,
                        onAnimationsChange = { animations = it },
                        motivationalMessages = motivationalMessages,
                        onMotivationalChange = { motivationalMessages = it },
                        listeningExercises = listeningExercises,
                        onListeningChange = { listeningExercises = it },
                        streakDays = streakDays,
                        onAddDay = { gameManager.completeLessonAndExtendStreak() },
                        onResetStreak = { gameManager.debugResetStreak() },
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        cardBg = cardBg,
                        cardBorder = cardBorder
                    )
                    1 -> NotificationsTab(
                        notifLessons = notifLessons, onNotifLessons = { notifLessons = it },
                        notifStreakAlerts = notifStreakAlerts, onNotifStreakAlerts = { notifStreakAlerts = it },
                        notifAchievements = notifAchievements, onNotifAchievements = { notifAchievements = it },
                        notifLeaderboard = notifLeaderboard, onNotifLeaderboard = { notifLeaderboard = it },
                        notifTips = notifTips, onNotifTips = { notifTips = it },
                        notifUpdates = notifUpdates, onNotifUpdates = { notifUpdates = it },
                        dailyReminderEnabled = dailyReminderEnabled, onDailyReminderChange = { dailyReminderEnabled = it },
                        practiceTime = practiceTime,
                        showTimePicker = showTimePicker,
                        onShowTimePicker = { showTimePicker = it },
                        onTimeSelected = { practiceTime = it; showTimePicker = false },
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        cardBg = cardBg,
                        cardBorder = cardBorder
                    )
                    2 -> CoursesTab(
                        flagEmoji = selectedLang?.flagEmoji ?: "🇺🇸",
                        langName = selectedLang?.name ?: "English",
                        onResetClick = { showResetAlert = true },
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        cardBg = cardBg,
                        cardBorder = cardBorder
                    )
                    3 -> AccountTab(
                        userName = userName.ifEmpty { "Learner" },
                        userHandle = userHandle.ifEmpty { "learner" },
                        onLogout = onLogout,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        cardBg = cardBg,
                        cardBorder = cardBorder
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        // ── Toast overlay ─────────────────────────────────────────
        if (showResetToast) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF1C2733))
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Done, contentDescription = null, tint = DuolingoGreen, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Progress reset successfully!", color = LocalDynamicThemeColors.current.textPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// Tab 1 — PREFERENCES
// ══════════════════════════════════════════════════════════════════
@Composable
private fun PreferencesTab(
    currentTheme: AppTheme,
    isDark: Boolean,
    onThemeChange: (AppTheme) -> Unit,
    isAutoEmotion: Boolean,
    onAutoEmotionChange: (Boolean) -> Unit,
    soundEffects: Boolean,
    onSoundEffectsChange: (Boolean) -> Unit,
    animations: Boolean,
    onAnimationsChange: (Boolean) -> Unit,
    motivationalMessages: Boolean,
    onMotivationalChange: (Boolean) -> Unit,
    listeningExercises: Boolean,
    onListeningChange: (Boolean) -> Unit,
    streakDays: Int,
    onAddDay: () -> Unit,
    onResetStreak: () -> Unit,
    textPrimary: Color,
    textSecondary: Color,
    cardBg: Color,
    cardBorder: Color
) {
    // Appearance section
    SectionHeader("Appearance", textSecondary)
    Spacer(modifier = Modifier.height(10.dp))
    AppearanceSection(
        currentTheme = currentTheme,
        isDark = isDark,
        onThemeChange = onThemeChange,
        textPrimary = textPrimary,
        textSecondary = textSecondary,
        cardBg = cardBg,
        cardBorder = cardBorder
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Live Emotion Icon
    SectionHeader("Live Emotion Icon", textSecondary)
    Spacer(modifier = Modifier.height(10.dp))
    SettingsCard(cardBg = cardBg, cardBorder = cardBorder) {
        ToggleRow(
            title = "Auto Emotion",
            subtitle = if (isAutoEmotion) "Changes automatically based on app activity" else "Static icon active",
            checked = isAutoEmotion,
            onCheckedChange = onAutoEmotionChange,
            textPrimary = textPrimary,
            textSecondary = textSecondary
        )
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Lesson Experience
    SectionHeader("Lesson Experience", textSecondary)
    Spacer(modifier = Modifier.height(10.dp))
    SettingsCard(cardBg = cardBg, cardBorder = cardBorder) {
        ToggleRow(title = "Sound Effects", checked = soundEffects, onCheckedChange = onSoundEffectsChange, textPrimary = textPrimary, textSecondary = textSecondary)
        SettingsDivider(cardBorder)
        ToggleRow(title = "Animations", checked = animations, onCheckedChange = onAnimationsChange, textPrimary = textPrimary, textSecondary = textSecondary)
        SettingsDivider(cardBorder)
        ToggleRow(title = "Motivational Messages", checked = motivationalMessages, onCheckedChange = onMotivationalChange, textPrimary = textPrimary, textSecondary = textSecondary)
        SettingsDivider(cardBorder)
        ToggleRow(title = "Listening Exercises", checked = listeningExercises, onCheckedChange = onListeningChange, textPrimary = textPrimary, textSecondary = textSecondary)
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Daily Fire Streak
    SectionHeader("Daily Fire Streak", textSecondary)
    Spacer(modifier = Modifier.height(10.dp))
    SettingsCard(cardBg = cardBg, cardBorder = cardBorder) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🔥 Streak Days", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textPrimary, modifier = Modifier.weight(1f))
            Text("$streakDays", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color(0xFFFF6B35))
        }
        SettingsDivider(cardBorder)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TextButton(onClick = onAddDay) {
                Text("+1 Day", color = DuolingoGreen, fontWeight = FontWeight.Black)
            }
            TextButton(onClick = onResetStreak) {
                Text("Reset", color = DuolingoRed, fontWeight = FontWeight.Black)
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Account navigation rows
    SectionHeader("Account", textSecondary)
    Spacer(modifier = Modifier.height(10.dp))
    SettingsCard(cardBg = cardBg, cardBorder = cardBorder) {
        NavigationRow(title = "Account", detail = "", textPrimary = textPrimary, textSecondary = textSecondary)
        SettingsDivider(cardBorder)
        NavigationRow(title = "Preferences", detail = "", textPrimary = textPrimary, textSecondary = textSecondary)
        SettingsDivider(cardBorder)
        NavigationRow(title = "Profile", detail = "", textPrimary = textPrimary, textSecondary = textSecondary)
        SettingsDivider(cardBorder)
        NavigationRow(title = "Notifications", detail = "", textPrimary = textPrimary, textSecondary = textSecondary)
        SettingsDivider(cardBorder)
        NavigationRow(title = "Courses", detail = "", textPrimary = textPrimary, textSecondary = textSecondary)
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Support row
    SectionHeader("Support", textSecondary)
    Spacer(modifier = Modifier.height(10.dp))
    SettingsCard(cardBg = cardBg, cardBorder = cardBorder) {
        NavigationRow(title = "Help & Support", detail = "", textPrimary = textPrimary, textSecondary = textSecondary)
    }
}

// ── Appearance section (dark/light toggle + 3-way theme picker) ──
@Composable
private fun AppearanceSection(
    currentTheme: AppTheme,
    isDark: Boolean,
    onThemeChange: (AppTheme) -> Unit,
    textPrimary: Color,
    textSecondary: Color,
    cardBg: Color,
    cardBorder: Color
) {
    SettingsCard(cardBg = cardBg, cardBorder = cardBorder) {
        // Dark Mode quick toggle (with iOS-style status and icons)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isDark) Brush.linearGradient(listOf(Color(0xFF3F51B5), Color(0xFF1A237E)))
                        else Brush.linearGradient(listOf(AmberGold, Color(0xFFFF9800)))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isDark) "🌙" else "☀️",
                    fontSize = 22.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Dark Mode",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
                Text(
                    text = if (isDark) "Midnight obsidian theme active" else "Luminous daytime theme active",
                    fontSize = 12.sp,
                    color = if (isDark) AmberGold else textSecondary
                )
            }

            Switch(
                checked = isDark,
                onCheckedChange = { toDark ->
                    ThemeManager.instance.toggleDarkMode(toDark)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbcolor = LocalDynamicThemeColors.current.textPrimary,
                    checkedTrackColor = AmberGold,
                    uncheckedThumbcolor = LocalDynamicThemeColors.current.textPrimary,
                    uncheckedTrackColor = cardBorder
                )
            )
        }

        SettingsDivider(cardBorder)

        // 3-way theme picker
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Text(
                "Theme Mode",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AppTheme.entries.forEach { theme ->
                    ThemeModeCard(
                        theme = theme,
                        isSelected = currentTheme == theme,
                        onSelect = { onThemeChange(theme) },
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        cardBg = cardBg,
                        cardBorder = cardBorder,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeModeCard(
    theme: AppTheme,
    isSelected: Boolean,
    onSelect: () -> Unit,
    textPrimary: Color,
    textSecondary: Color,
    cardBg: Color,
    cardBorder: Color,
    modifier: Modifier = Modifier
) {
    val emoji = when (theme) {
        AppTheme.DARK -> "🌙"
        AppTheme.LIGHT -> "☀️"
        AppTheme.SYSTEM -> "⚙️"
    }

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) AmberGold else cardBorder,
        animationSpec = tween(200), label = "themeBorder"
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) AmberGold.copy(alpha = 0.12f) else cardBg)
            .border(if (isSelected) 2.dp else 1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable { onSelect() }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(emoji, fontSize = 24.sp)
        Text(
            text = theme.title,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
            color = if (isSelected) AmberGold else textSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(AmberGold),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Color(0xFF1A0F00),
                    modifier = Modifier.size(10.dp)
                )
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// Tab 2 — NOTIFICATIONS
// ══════════════════════════════════════════════════════════════════
@Composable
private fun NotificationsTab(
    notifLessons: Boolean, onNotifLessons: (Boolean) -> Unit,
    notifStreakAlerts: Boolean, onNotifStreakAlerts: (Boolean) -> Unit,
    notifAchievements: Boolean, onNotifAchievements: (Boolean) -> Unit,
    notifLeaderboard: Boolean, onNotifLeaderboard: (Boolean) -> Unit,
    notifTips: Boolean, onNotifTips: (Boolean) -> Unit,
    notifUpdates: Boolean, onNotifUpdates: (Boolean) -> Unit,
    dailyReminderEnabled: Boolean, onDailyReminderChange: (Boolean) -> Unit,
    practiceTime: String,
    showTimePicker: Boolean,
    onShowTimePicker: (Boolean) -> Unit,
    onTimeSelected: (String) -> Unit,
    textPrimary: Color,
    textSecondary: Color,
    cardBg: Color,
    cardBorder: Color
) {
    Text(
        text = "Notifications",
        fontSize = 24.sp,
        fontWeight = FontWeight.Black,
        color = textPrimary
    )
    Spacer(modifier = Modifier.height(20.dp))

    // General section
    SectionHeader("General", textSecondary)
    Spacer(modifier = Modifier.height(10.dp))
    SettingsCard(cardBg = cardBg, cardBorder = cardBorder) {
        CheckboxRow(title = "Lessons & XP Updates", checked = notifLessons, onCheckedChange = onNotifLessons, textPrimary = textPrimary, textSecondary = textSecondary)
        SettingsDivider(cardBorder)
        CheckboxRow(title = "Streak Alerts", checked = notifStreakAlerts, onCheckedChange = onNotifStreakAlerts, textPrimary = textPrimary, textSecondary = textSecondary)
        SettingsDivider(cardBorder)
        CheckboxRow(title = "Achievement Unlocked", checked = notifAchievements, onCheckedChange = onNotifAchievements, textPrimary = textPrimary, textSecondary = textSecondary)
        SettingsDivider(cardBorder)
        CheckboxRow(title = "Leaderboard Changes", checked = notifLeaderboard, onCheckedChange = onNotifLeaderboard, textPrimary = textPrimary, textSecondary = textSecondary)
        SettingsDivider(cardBorder)
        CheckboxRow(title = "Tips & Suggestions", checked = notifTips, onCheckedChange = onNotifTips, textPrimary = textPrimary, textSecondary = textSecondary)
        SettingsDivider(cardBorder)
        CheckboxRow(title = "App Updates & News", checked = notifUpdates, onCheckedChange = onNotifUpdates, textPrimary = textPrimary, textSecondary = textSecondary)
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Daily Reminders
    SectionHeader("Daily Reminders", textSecondary)
    Spacer(modifier = Modifier.height(10.dp))
    SettingsCard(cardBg = cardBg, cardBorder = cardBorder) {
        CheckboxRow(
            title = "Daily Practice Reminder",
            checked = dailyReminderEnabled,
            onCheckedChange = onDailyReminderChange,
            textPrimary = textPrimary,
            textSecondary = textSecondary
        )
        if (dailyReminderEnabled) {
            SettingsDivider(cardBorder)
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                Column {
                    Text(
                        "Reminder Time",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(cardBg)
                            .border(1.5.dp, cardBorder, RoundedCornerShape(12.dp))
                            .clickable { onShowTimePicker(true) }
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                practiceTime,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    DropdownMenu(
                        expanded = showTimePicker,
                        onDismissRequest = { onShowTimePicker(false) },
                        modifier = Modifier.background(cardBg)
                    ) {
                        listOf("9:00 AM", "12:00 PM", "5:00 PM", "7:00 PM", "9:00 PM").forEach { time ->
                            DropdownMenuItem(
                                text = { Text(time, fontWeight = FontWeight.Bold, color = textPrimary) },
                                onClick = { onTimeSelected(time) }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// Tab 3 — COURSES
// ══════════════════════════════════════════════════════════════════
@Composable
private fun CoursesTab(
    flagEmoji: String,
    langName: String,
    onResetClick: () -> Unit,
    textPrimary: Color,
    textSecondary: Color,
    cardBg: Color,
    cardBorder: Color
) {
    Text(
        text = "Courses",
        fontSize = 24.sp,
        fontWeight = FontWeight.Black,
        color = textPrimary
    )
    Spacer(modifier = Modifier.height(20.dp))
    SettingsCard(cardBg = cardBg, cardBorder = cardBorder) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(flagEmoji, fontSize = 32.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = langName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onResetClick) {
                Text(
                    text = "RESET",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = textSecondary
                )
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// Tab 4 — ACCOUNT
// ══════════════════════════════════════════════════════════════════
@Composable
private fun AccountTab(
    userName: String,
    userHandle: String,
    onLogout: () -> Unit,
    textPrimary: Color,
    textSecondary: Color,
    cardBg: Color,
    cardBorder: Color
) {
    Text(
        text = "Account Details",
        fontSize = 24.sp,
        fontWeight = FontWeight.Black,
        color = textPrimary
    )
    Spacer(modifier = Modifier.height(20.dp))

    // Profile info card
    SettingsCard(cardBg = cardBg, cardBorder = cardBorder) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Name", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textPrimary, modifier = Modifier.weight(1f))
            Text(userName, fontSize = 15.sp, color = textSecondary)
        }
        SettingsDivider(cardBorder)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Username", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textPrimary, modifier = Modifier.weight(1f))
            Text(
                text = if (userHandle.startsWith("@")) userHandle else "@$userHandle",
                fontSize = 15.sp,
                color = textSecondary
            )
        }
    }

    Spacer(modifier = Modifier.height(32.dp))

    // Log out button
    Button(
        onClick = onLogout,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = DuolingoRed.copy(alpha = 0.12f),
            contentColor = DuolingoRed
        )
    ) {
        Text(
            text = "LOG OUT",
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(vertical = 6.dp)
        )
    }
}

// ══════════════════════════════════════════════════════════════════
// Shared helper composables
// ══════════════════════════════════════════════════════════════════

@Composable
private fun SectionHeader(title: String, textSecondary: Color) {
    Text(
        text = title.uppercase(),
        fontSize = 12.sp,
        fontWeight = FontWeight.Black,
        color = textSecondary,
        letterSpacing = 1.sp
    )
}

@Composable
private fun SettingsCard(
    cardBg: Color,
    cardBorder: Color,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(cardBg)
            .border(1.2.dp, cardBorder, shape)
    ) {
        content()
    }
}

@Composable
private fun SettingsDivider(color: Color) {
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        color = color.copy(alpha = 0.6f),
        thickness = 1.dp
    )
}

@Composable
private fun ToggleRow(
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    textPrimary: Color,
    textSecondary: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = textSecondary
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbcolor = LocalDynamicThemeColors.current.textPrimary,
                checkedTrackColor = DuolingoBlue,
                uncheckedThumbcolor = LocalDynamicThemeColors.current.textPrimary,
                uncheckedTrackColor = textSecondary.copy(alpha = 0.35f)
            )
        )
    }
}

@Composable
private fun CheckboxRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    textPrimary: Color,
    textSecondary: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimary,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (checked) DuolingoBlue else Color.Transparent)
                .border(2.dp, if (checked) DuolingoBlue else textSecondary, RoundedCornerShape(6.dp))
                .clickable { onCheckedChange(!checked) },
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun NavigationRow(
    title: String,
    detail: String,
    textPrimary: Color,
    textSecondary: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* navigate */ }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimary,
            modifier = Modifier.weight(1f)
        )
        if (detail.isNotEmpty()) {
            Text(text = detail, fontSize = 14.sp, color = textSecondary)
            Spacer(modifier = Modifier.width(6.dp))
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = textSecondary,
            modifier = Modifier.size(18.dp)
        )
    }
}
