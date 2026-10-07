package com.simats.codeingo.ui.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.domain.AppTheme
import com.simats.codeingo.domain.LocalizationManager
import com.simats.codeingo.domain.PhoenixEmotionManager
import com.simats.codeingo.domain.ThemeManager
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DuolingoBlue
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.InputBorder
import com.simats.codeingo.ui.theme.SubtextGray
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ══════════════════════════════════════════════════════════════════
// ⚙️  SettingsScreen — Full 4-Tab parity with iOS SettingsView.swift
// Tabs: Preferences | Notifications | Courses | Account
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
    val isAutoEmotion by emotionManager.isAutoEmotionEnabled.collectAsState()
    val userName by locManager.userName.collectAsState()
    val userHandle by locManager.userHandle.collectAsState()
    val selectedLang by locManager.selectedLanguage.collectAsState()
    val streakDays by gameManager.streakDays.collectAsState()

    // ── Tab state ────────────────────────────────────────────────
    var selectedTab by remember { mutableStateOf(0) }

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

    // ── Background colour driven by theme ────────────────────────
    val bgColor = if (currentTheme == AppTheme.LIGHT) Color(0xFFF5F5F5) else DarkBackground
    val textPrimary = if (currentTheme == AppTheme.LIGHT) Color(0xFF111111) else Color.White

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
                    color = SubtextGray
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showResetAlert = false
                    // Reset: unlock-level progress reset
                    GameManager.instance.resetAllProgress()
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
            containerColor = CardBackground
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
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
                        tint = SubtextGray
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "SETTINGS",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = textPrimary
                )
                Spacer(modifier = Modifier.weight(1f))
                // Done button (mirrors iOS)
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
                    .background(CardBackground),
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
                        color = if (isSelected) Color.White else SubtextGray,
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
                        textPrimary = textPrimary
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
                        textPrimary = textPrimary
                    )
                    2 -> CoursesTab(
                        flagEmoji = selectedLang?.flagEmoji ?: "🇺🇸",
                        langName = selectedLang?.name ?: "English",
                        onResetClick = { showResetAlert = true },
                        textPrimary = textPrimary
                    )
                    3 -> AccountTab(
                        userName = userName.ifEmpty { "Learner" },
                        userHandle = userHandle.ifEmpty { "learner" },
                        onLogout = onLogout,
                        textPrimary = textPrimary
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
                    Text("Progress reset successfully!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
    textPrimary: Color
) {
    // Appearance section
    SectionHeader("Appearance", textPrimary)
    Spacer(modifier = Modifier.height(10.dp))
    AppearanceSection(currentTheme = currentTheme, onThemeChange = onThemeChange, textPrimary = textPrimary)

    Spacer(modifier = Modifier.height(24.dp))

    // Live Emotion Icon
    SectionHeader("Live Emotion Icon", textPrimary)
    Spacer(modifier = Modifier.height(10.dp))
    SettingsCard {
        ToggleRow(title = "Auto Emotion", checked = isAutoEmotion, onCheckedChange = onAutoEmotionChange, textPrimary = textPrimary)
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Lesson Experience
    SectionHeader("Lesson Experience", textPrimary)
    Spacer(modifier = Modifier.height(10.dp))
    SettingsCard {
        ToggleRow(title = "Sound Effects", checked = soundEffects, onCheckedChange = onSoundEffectsChange, textPrimary = textPrimary)
        SettingsDivider()
        ToggleRow(title = "Animations", checked = animations, onCheckedChange = onAnimationsChange, textPrimary = textPrimary)
        SettingsDivider()
        ToggleRow(title = "Motivational Messages", checked = motivationalMessages, onCheckedChange = onMotivationalChange, textPrimary = textPrimary)
        SettingsDivider()
        ToggleRow(title = "Listening Exercises", checked = listeningExercises, onCheckedChange = onListeningChange, textPrimary = textPrimary)
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Daily Fire Streak
    SectionHeader("Daily Fire Streak", textPrimary)
    Spacer(modifier = Modifier.height(10.dp))
    SettingsCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🔥 Streak Days", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textPrimary, modifier = Modifier.weight(1f))
            Text("$streakDays", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color(0xFFFF6B35))
        }
        SettingsDivider()
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
    SectionHeader("Account", textPrimary)
    Spacer(modifier = Modifier.height(10.dp))
    SettingsCard {
        NavigationRow(title = "Account", detail = "", textPrimary = textPrimary)
        SettingsDivider()
        NavigationRow(title = "Preferences", detail = "", textPrimary = textPrimary)
        SettingsDivider()
        NavigationRow(title = "Profile", detail = "", textPrimary = textPrimary)
        SettingsDivider()
        NavigationRow(title = "Notifications", detail = "", textPrimary = textPrimary)
        SettingsDivider()
        NavigationRow(title = "Courses", detail = "", textPrimary = textPrimary)
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Support row
    SectionHeader("Support", textPrimary)
    Spacer(modifier = Modifier.height(10.dp))
    SettingsCard {
        NavigationRow(title = "Help & Support", detail = "", textPrimary = textPrimary)
    }
}

// ── Appearance section (dark/light toggle + 3-way theme picker) ──
@Composable
private fun AppearanceSection(
    currentTheme: AppTheme,
    onThemeChange: (AppTheme) -> Unit,
    textPrimary: Color
) {
    val isDark = currentTheme == AppTheme.DARK

    SettingsCard {
        // Dark Mode quick toggle
        ToggleRow(
            title = "Dark Mode",
            checked = isDark,
            onCheckedChange = { ThemeManager.instance.toggleDarkMode(it) },
            textPrimary = textPrimary
        )
        SettingsDivider()
        // 3-way theme picker
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Text("Theme", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textPrimary)
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
    modifier: Modifier = Modifier
) {
    val emoji = when (theme) {
        AppTheme.DARK -> "🌙"
        AppTheme.LIGHT -> "☀️"
        AppTheme.SYSTEM -> "⚙️"
    }
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) DuolingoBlue else InputBorder,
        animationSpec = tween(200), label = "themeBorder"
    )
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) DuolingoBlue.copy(alpha = 0.12f) else Color.Transparent)
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onSelect() }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(emoji, fontSize = 22.sp)
        Text(
            text = theme.title,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
            color = if (isSelected) DuolingoBlue else SubtextGray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(DuolingoBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
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
    textPrimary: Color
) {
    Text(
        text = "Notifications",
        fontSize = 24.sp,
        fontWeight = FontWeight.Black,
        color = textPrimary
    )
    Spacer(modifier = Modifier.height(20.dp))

    // General section
    SectionHeader("General", textPrimary)
    Spacer(modifier = Modifier.height(10.dp))
    SettingsCard {
        CheckboxRow(title = "Lessons & XP Updates", checked = notifLessons, onCheckedChange = onNotifLessons, textPrimary = textPrimary)
        SettingsDivider()
        CheckboxRow(title = "Streak Alerts", checked = notifStreakAlerts, onCheckedChange = onNotifStreakAlerts, textPrimary = textPrimary)
        SettingsDivider()
        CheckboxRow(title = "Achievement Unlocked", checked = notifAchievements, onCheckedChange = onNotifAchievements, textPrimary = textPrimary)
        SettingsDivider()
        CheckboxRow(title = "Leaderboard Changes", checked = notifLeaderboard, onCheckedChange = onNotifLeaderboard, textPrimary = textPrimary)
        SettingsDivider()
        CheckboxRow(title = "Tips & Suggestions", checked = notifTips, onCheckedChange = onNotifTips, textPrimary = textPrimary)
        SettingsDivider()
        CheckboxRow(title = "App Updates & News", checked = notifUpdates, onCheckedChange = onNotifUpdates, textPrimary = textPrimary)
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Daily Reminders
    SectionHeader("Daily Reminders", textPrimary)
    Spacer(modifier = Modifier.height(10.dp))
    SettingsCard {
        CheckboxRow(
            title = "Daily Practice Reminder",
            checked = dailyReminderEnabled,
            onCheckedChange = onDailyReminderChange,
            textPrimary = textPrimary
        )
        if (dailyReminderEnabled) {
            SettingsDivider()
            // Time picker dropdown
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
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
                            .background(CardBackground)
                            .border(1.5.dp, InputBorder, RoundedCornerShape(12.dp))
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
                                tint = SubtextGray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    DropdownMenu(
                        expanded = showTimePicker,
                        onDismissRequest = { onShowTimePicker(false) }
                    ) {
                        listOf("9:00 AM", "12:00 PM", "5:00 PM", "7:00 PM", "9:00 PM").forEach { time ->
                            DropdownMenuItem(
                                text = { Text(time, fontWeight = FontWeight.Bold) },
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
    textPrimary: Color
) {
    Text(
        text = "Courses",
        fontSize = 24.sp,
        fontWeight = FontWeight.Black,
        color = textPrimary
    )
    Spacer(modifier = Modifier.height(20.dp))
    SettingsCard {
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
                    color = SubtextGray
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
    textPrimary: Color
) {
    Text(
        text = "Account Details",
        fontSize = 24.sp,
        fontWeight = FontWeight.Black,
        color = textPrimary
    )
    Spacer(modifier = Modifier.height(20.dp))

    // Profile info card
    SettingsCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Name", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textPrimary, modifier = Modifier.weight(1f))
            Text(userName, fontSize = 15.sp, color = SubtextGray)
        }
        HorizontalDivider(color = InputBorder.copy(alpha = 0.6f))
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
                color = SubtextGray
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
private fun SectionHeader(title: String, textPrimary: Color) {
    Text(
        text = title.uppercase(),
        fontSize = 12.sp,
        fontWeight = FontWeight.Black,
        color = SubtextGray,
        letterSpacing = 1.sp
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(CardBackground)
            .border(1.5.dp, InputBorder, shape)
    ) {
        content()
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        color = InputBorder.copy(alpha = 0.6f),
        thickness = 1.dp
    )
}

@Composable
private fun ToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    textPrimary: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimary,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = DuolingoBlue,
                uncheckedThumbColor = SubtextGray,
                uncheckedTrackColor = Color(0xFF1B2631)
            )
        )
    }
}

@Composable
private fun CheckboxRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    textPrimary: Color
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
                .border(2.dp, if (checked) DuolingoBlue else SubtextGray, RoundedCornerShape(6.dp))
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
    textPrimary: Color
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
            Text(text = detail, fontSize = 14.sp, color = SubtextGray)
            Spacer(modifier = Modifier.width(6.dp))
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = SubtextGray,
            modifier = Modifier.size(18.dp)
        )
    }
}
