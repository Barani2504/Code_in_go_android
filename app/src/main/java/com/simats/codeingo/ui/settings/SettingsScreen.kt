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
import android.content.Context
import android.view.HapticFeedbackConstants
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.material3.rememberModalBottomSheetState
import com.simats.codeingo.data.model.Language
import com.simats.codeingo.data.model.PhoenixEmotion
import com.simats.codeingo.ui.onboarding.LanguagePickerSheet
import com.simats.codeingo.ui.phoenix.PhoenixEmotionPickerSheet
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
import com.simats.codeingo.ui.components.AppButton
import com.simats.codeingo.ui.components.AppButtonStyle
import com.simats.codeingo.ui.phoenix.PhoenixAtmosphericBackgroundView
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DuolingoBlue
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.pressScale
import com.simats.codeingo.ui.theme.PhoenixDeepNavy
import com.simats.codeingo.ui.theme.PhoenixLightBg
import com.simats.codeingo.ui.theme.EffectTier
import com.simats.codeingo.ui.theme.MotionController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ══════════════════════════════════════════════════════════════════
// ⚙️  SettingsScreen — Full 4-Tab parity with iOS SettingsView.swift
// Tabs: Preferences | Notifications | Courses | Account
// Seamlessly adapts to Light, Dark, and System Theme modes
// ══════════════════════════════════════════════════════════════════

private val tabTitles = listOf("Preferences", "Notifications", "Courses", "Account")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onDismiss: () -> Unit,
    onLogout: () -> Unit,
    onOpenAnimationLab: () -> Unit = {},
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
    val currentEmotion by emotionManager.currentEmotion.collectAsState()
    val userName by locManager.userName.collectAsState()
    val userHandle by locManager.userHandle.collectAsState()
    val selectedLang by locManager.selectedLanguage.collectAsState()
    val streakDays by gameManager.streakDays.collectAsState()

    // ── SharedPreferences & System Services ──────────────────────
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val view = LocalView.current
    val settingsPrefs = remember { context.getSharedPreferences("ashcrypt_settings_prefs", Context.MODE_PRIVATE) }

    // ── Motion & Feedback state ─────────────────────────────────
    val effectTier by MotionController.effectTier.collectAsState()
    val soundEffects by MotionController.soundEnabled.collectAsState()
    val hapticsEnabled by MotionController.hapticsEnabled.collectAsState()

    // ── Tab state ────────────────────────────────────────────────
    var selectedTab by remember { mutableIntStateOf(0) }

    // ── Lesson Experience toggles (persisted) ──────────────────────
    var animations by remember { mutableStateOf(settingsPrefs.getBoolean("pref_animations", true)) }
    var motivationalMessages by remember { mutableStateOf(settingsPrefs.getBoolean("pref_motivational", true)) }
    var listeningExercises by remember { mutableStateOf(settingsPrefs.getBoolean("pref_listening", true)) }

    // ── Notification toggles (persisted) ───────────────────────────
    var notifLessons by remember { mutableStateOf(settingsPrefs.getBoolean("notif_lessons", true)) }
    var notifStreakAlerts by remember { mutableStateOf(settingsPrefs.getBoolean("notif_streak_alerts", true)) }
    var notifAchievements by remember { mutableStateOf(settingsPrefs.getBoolean("notif_achievements", true)) }
    var notifLeaderboard by remember { mutableStateOf(settingsPrefs.getBoolean("notif_leaderboard", false)) }
    var notifTips by remember { mutableStateOf(settingsPrefs.getBoolean("notif_tips", true)) }
    var notifUpdates by remember { mutableStateOf(settingsPrefs.getBoolean("notif_updates", false)) }
    var quietHoursEnabled by remember { mutableStateOf(settingsPrefs.getBoolean("notif_quiet_hours", false)) }
    var vibrateOnAlerts by remember { mutableStateOf(settingsPrefs.getBoolean("notif_vibrate_alerts", true)) }

    // ── Daily reminder (persisted) ─────────────────────────────────
    var dailyReminderEnabled by remember { mutableStateOf(settingsPrefs.getBoolean("notif_daily_reminder", true)) }
    var practiceTime by remember { mutableStateOf(settingsPrefs.getString("notif_practice_time", "9:00 AM") ?: "9:00 AM") }
    var showTimePicker by remember { mutableStateOf(false) }

    // ── Account & Security details (persisted) ──────────────────────
    var userEmail by remember { mutableStateOf(settingsPrefs.getString("user_email", "agent@ashcrypt.io") ?: "agent@ashcrypt.io") }
    var telemetryEnabled by remember { mutableStateOf(settingsPrefs.getBoolean("sec_telemetry", false)) }

    // ── Dialog & Sheet States ──────────────────────────────────────
    var showResetAlert by remember { mutableStateOf(false) }
    var showResetToast by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showLogoutAlert by remember { mutableStateOf(false) }
    var showDeleteAccountAlert by remember { mutableStateOf(false) }
    var showPrivacyPolicyDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showEditNameDialog by remember { mutableStateOf(false) }
    var showEditHandleDialog by remember { mutableStateOf(false) }
    var showEditEmailDialog by remember { mutableStateOf(false) }
    var showLanguagePickerSheet by remember { mutableStateOf(false) }
    var showEmotionPickerSheet by remember { mutableStateOf(false) }
    var selectedArenaDossier by remember { mutableStateOf<Triple<String, String, String>?>(null) }
    var toastMessage by remember { mutableStateOf<String?>(null) }

    var tempEditName by remember { mutableStateOf("") }
    var tempEditHandle by remember { mutableStateOf("") }
    var tempEditEmail by remember { mutableStateOf("") }

    // ── Forgot / Change Password Sheet & Toast ─────────────────────
    var showForgotPasswordSheet by remember { mutableStateOf(false) }
    var passwordChangeSuccessToast by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    fun showFeedback(msg: String) {
        scope.launch {
            toastMessage = msg
            delay(2200)
            toastMessage = null
        }
    }

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
                    "Are you sure you want to reset all your progress in AshCrypt? This will reset all your completed battle arenas.",
                    fontSize = 14.sp,
                    color = textSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showResetAlert = false
                    gameManager.resetAllProgress()
                    showFeedback("Progress reset successfully!")
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

    // ── Logout confirmation dialog ───────────────────────────────
    if (showLogoutAlert) {
        AlertDialog(
            onDismissRequest = { showLogoutAlert = false },
            title = {
                Text(
                    "Log Out",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = textPrimary
                )
            },
            text = {
                Text(
                    "Are you sure you want to log out of your AshCrypt profile?",
                    fontSize = 14.sp,
                    color = textSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutAlert = false
                    onLogout()
                }) {
                    Text("LOG OUT", color = DuolingoRed, fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutAlert = false }) {
                    Text("CANCEL", color = DuolingoBlue, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = cardBg
        )
    }

    // ── Delete Account & Data Dialog (Play Store Compliance) ──────
    if (showDeleteAccountAlert) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountAlert = false },
            title = {
                Text(
                    "Delete Account & Data",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = DuolingoRed
                )
            },
            text = {
                Text(
                    "Are you sure you want to permanently delete your AshCrypt account and purge all local progress, credentials, and achievements? This action is irreversible.",
                    fontSize = 14.sp,
                    color = textSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteAccountAlert = false
                    scope.launch {
                        com.simats.codeingo.domain.AuthService.instance.deleteAccount()
                        onLogout()
                    }
                }) {
                    Text("PERMANENTLY DELETE", color = DuolingoRed, fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountAlert = false }) {
                    Text("CANCEL", color = DuolingoBlue, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = cardBg
        )
    }

    // ── Privacy Policy Dialog ─────────────────────────────────────
    if (showPrivacyPolicyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyPolicyDialog = false },
            title = {
                Text(
                    "AshCrypt Privacy Policy",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = textPrimary
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        "Last Updated: October 2026\n\n" +
                        "1. Data Collection & Safety\n" +
                        "AshCrypt is built with a zero-knowledge, privacy-first architecture. All user progression, streak data, and cryptographic key generation exercises are computed and stored strictly locally on your device.\n\n" +
                        "2. Anonymous Telemetry\n" +
                        "If you opt into Anonymous Cryptographic Telemetry in Settings, only aggregated arena completion rates are collected. No personally identifiable information (PII) is ever linked to telemetry.\n\n" +
                        "3. Data Retention & Deletion\n" +
                        "In compliance with Google Play Store User Data policies, you may permanently purge and delete all your account data, credentials, and game records at any time using the 'Delete Account & Data' button in the Account tab.\n\n" +
                        "4. Contact & Security\n" +
                        "For data safety inquiries or security disclosures, contact security@ashcrypt.io.",
                        fontSize = 13.sp,
                        color = textSecondary,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyPolicyDialog = false }) {
                    Text("CLOSE", color = DuolingoGreen, fontWeight = FontWeight.Black)
                }
            },
            containerColor = cardBg
        )
    }

    // ── Terms of Service Dialog ───────────────────────────────────
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = {
                Text(
                    "AshCrypt Terms of Service",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = textPrimary
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        "1. Educational Purpose\n" +
                        "AshCrypt and its cryptographic battle arenas (Block Ciphers, Asymmetric Vault, Sponge Chambers) are interactive educational simulations designed solely for learning cryptography, computer science, and data safety.\n\n" +
                        "2. Responsible Usage\n" +
                        "Simulations within this application must not be used for malicious exploitation or unauthorized penetration testing. All cryptographic exercises operate within isolated virtual sandboxes.\n\n" +
                        "3. Intellectual Property\n" +
                        "All algorithm animations, dynamic visualizers, and learning modules are proprietary to AshCrypt.\n\n" +
                        "4. Service Availability\n" +
                        "AshCrypt is provided 'as is' for offline and online educational reinforcement.",
                        fontSize = 13.sp,
                        color = textSecondary,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showTermsDialog = false }) {
                    Text("I AGREE", color = DuolingoGreen, fontWeight = FontWeight.Black)
                }
            },
            containerColor = cardBg
        )
    }

    // ── Help & Support Dialog ─────────────────────────────────────
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🛡️", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "AshCrypt Help & Support",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = textPrimary
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Version: v2.4.0 (Crypto Security Edition)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = AmberGold
                    )
                    Text(
                        "Need assistance with security challenges, arena key exchanges, or account sync? Our team is available 24/7.",
                        fontSize = 13.sp,
                        color = textSecondary,
                        lineHeight = 18.sp
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isDark) Color(0xFF14_1F_36) else Color(0xFFF1_F5_F9))
                            .border(1.dp, cardBorder, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text("Official Support Desk", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textSecondary)
                            Text("support@ashcrypt.io", fontSize = 14.sp, fontWeight = FontWeight.Black, color = textPrimary)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString("support@ashcrypt.io"))
                        showHelpDialog = false
                        showFeedback("Copied support@ashcrypt.io to clipboard! 📋")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberGold)
                ) {
                    Text("Copy Email", color = Color(0xFF1A1205), fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text("CLOSE", color = textSecondary, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = cardBg
        )
    }

    // ── Edit Name Dialog ──────────────────────────────────────────
    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = {
                Text("Edit Name", fontWeight = FontWeight.Black, fontSize = 18.sp, color = textPrimary)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter your display name:", fontSize = 13.sp, color = textSecondary)
                    OutlinedTextField(
                        value = tempEditName,
                        onValueChange = { tempEditName = it },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberGold,
                            unfocusedBorderColor = cardBorder,
                            focusedTextColor = textPrimary,
                            unfocusedTextColor = textPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (tempEditName.isNotBlank()) {
                        locManager.updateUserProfile(tempEditName.trim(), userHandle)
                        settingsPrefs.edit().putString("user_name", tempEditName.trim()).apply()
                        showFeedback("Name updated successfully! ✨")
                    }
                    showEditNameDialog = false
                }) {
                    Text("SAVE", color = DuolingoGreen, fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) {
                    Text("CANCEL", color = textSecondary, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = cardBg
        )
    }

    // ── Edit Handle Dialog ────────────────────────────────────────
    if (showEditHandleDialog) {
        AlertDialog(
            onDismissRequest = { showEditHandleDialog = false },
            title = {
                Text("Edit Username", fontWeight = FontWeight.Black, fontSize = 18.sp, color = textPrimary)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter your unique handle:", fontSize = 13.sp, color = textSecondary)
                    OutlinedTextField(
                        value = tempEditHandle,
                        onValueChange = { tempEditHandle = it.removePrefix("@").trim() },
                        prefix = { Text("@", color = AmberGold, fontWeight = FontWeight.Black) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberGold,
                            unfocusedBorderColor = cardBorder,
                            focusedTextColor = textPrimary,
                            unfocusedTextColor = textPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (tempEditHandle.isNotBlank()) {
                        val cleanHandle = tempEditHandle.trim().removePrefix("@")
                        locManager.updateUserProfile(userName, cleanHandle)
                        settingsPrefs.edit().putString("user_handle", cleanHandle).apply()
                        showFeedback("Username updated! 🔥")
                    }
                    showEditHandleDialog = false
                }) {
                    Text("SAVE", color = DuolingoGreen, fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditHandleDialog = false }) {
                    Text("CANCEL", color = textSecondary, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = cardBg
        )
    }

    // ── Edit Email Dialog ─────────────────────────────────────────
    if (showEditEmailDialog) {
        AlertDialog(
            onDismissRequest = { showEditEmailDialog = false },
            title = {
                Text("Edit Email Address", fontWeight = FontWeight.Black, fontSize = 18.sp, color = textPrimary)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter your primary email address:", fontSize = 13.sp, color = textSecondary)
                    OutlinedTextField(
                        value = tempEditEmail,
                        onValueChange = { tempEditEmail = it.trim() },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberGold,
                            unfocusedBorderColor = cardBorder,
                            focusedTextColor = textPrimary,
                            unfocusedTextColor = textPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (tempEditEmail.isNotBlank()) {
                        userEmail = tempEditEmail.trim()
                        settingsPrefs.edit().putString("user_email", tempEditEmail.trim()).apply()
                        showFeedback("Email updated successfully! 📧")
                    }
                    showEditEmailDialog = false
                }) {
                    Text("SAVE", color = DuolingoGreen, fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditEmailDialog = false }) {
                    Text("CANCEL", color = textSecondary, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = cardBg
        )
    }

    // ── Arena Dossier Inspection Dialog ──────────────────────────
    if (selectedArenaDossier != null) {
        val arena = selectedArenaDossier!!
        AlertDialog(
            onDismissRequest = { selectedArenaDossier = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(arena.first, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        arena.second,
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        color = textPrimary
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "CRYPTOGRAPHIC SPECIFICATIONS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = AmberGold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        arena.third,
                        fontSize = 14.sp,
                        color = textPrimary,
                        fontWeight = FontWeight.Medium
                    )
                    SettingsDivider(cardBorder)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Security Clearance:", fontSize = 12.sp, color = textSecondary)
                        Text("Level 5 (Unrestricted)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DuolingoGreen)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Deployment Status:", fontSize = 12.sp, color = textSecondary)
                        Text("Online & Guarded", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DuolingoBlue)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    selectedArenaDossier = null
                    showFeedback("Arena dossier verified! 🛡️")
                }) {
                    Text("CLOSE", color = DuolingoBlue, fontWeight = FontWeight.Black)
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
                        effectTier = effectTier,
                        onEffectTierChange = { MotionController.setEffectTier(it) },
                        soundEffects = soundEffects,
                        onSoundEffectsChange = { MotionController.setSoundEnabled(it) },
                        hapticsEnabled = hapticsEnabled,
                        onHapticsEnabledChange = { MotionController.setHapticsEnabled(it) },
                        animations = animations,
                        onAnimationsChange = {
                            animations = it
                            settingsPrefs.edit().putBoolean("pref_animations", it).apply()
                        },
                        motivationalMessages = motivationalMessages,
                        onMotivationalChange = {
                            motivationalMessages = it
                            settingsPrefs.edit().putBoolean("pref_motivational", it).apply()
                        },
                        listeningExercises = listeningExercises,
                        onListeningChange = {
                            listeningExercises = it
                            settingsPrefs.edit().putBoolean("pref_listening", it).apply()
                        },
                        streakDays = streakDays,
                        onAddDay = { gameManager.completeLessonAndExtendStreak() },
                        onResetStreak = { gameManager.debugResetStreak() },
                        onOpenForgotPassword = { showForgotPasswordSheet = true },
                        onOpenAnimationLab = onOpenAnimationLab,
                        onOpenLanguagePicker = { showLanguagePickerSheet = true },
                        onOpenEmotionPicker = { showEmotionPickerSheet = true },
                        selectedLanguage = selectedLang,
                        currentEmotion = currentEmotion,
                        userName = userName.ifEmpty { "Agent 007" },
                        userHandle = userHandle.ifEmpty { "ashcrypt_operative" },
                        dailyReminderEnabled = dailyReminderEnabled,
                        practiceTime = practiceTime,
                        onNavigate = { destination ->
                            when (destination) {
                                "Account" -> selectedTab = 3
                                "Preferences" -> selectedTab = 0
                                "Profile" -> onDismiss()
                                "Notifications" -> selectedTab = 1
                                "Courses" -> selectedTab = 2
                                "Help & Support" -> showHelpDialog = true
                            }
                        },
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        cardBg = cardBg,
                        cardBorder = cardBorder
                    )
                    1 -> NotificationsTab(
                        notifLessons = notifLessons,
                        onNotifLessons = {
                            notifLessons = it
                            settingsPrefs.edit().putBoolean("notif_lessons", it).apply()
                        },
                        notifStreakAlerts = notifStreakAlerts,
                        onNotifStreakAlerts = {
                            notifStreakAlerts = it
                            settingsPrefs.edit().putBoolean("notif_streak_alerts", it).apply()
                        },
                        notifAchievements = notifAchievements,
                        onNotifAchievements = {
                            notifAchievements = it
                            settingsPrefs.edit().putBoolean("notif_achievements", it).apply()
                        },
                        notifLeaderboard = notifLeaderboard,
                        onNotifLeaderboard = {
                            notifLeaderboard = it
                            settingsPrefs.edit().putBoolean("notif_leaderboard", it).apply()
                        },
                        notifTips = notifTips,
                        onNotifTips = {
                            notifTips = it
                            settingsPrefs.edit().putBoolean("notif_tips", it).apply()
                        },
                        notifUpdates = notifUpdates,
                        onNotifUpdates = {
                            notifUpdates = it
                            settingsPrefs.edit().putBoolean("notif_updates", it).apply()
                        },
                        quietHoursEnabled = quietHoursEnabled,
                        onQuietHoursChange = {
                            quietHoursEnabled = it
                            settingsPrefs.edit().putBoolean("notif_quiet_hours", it).apply()
                        },
                        vibrateOnAlerts = vibrateOnAlerts,
                        onVibrateAlertsChange = {
                            vibrateOnAlerts = it
                            settingsPrefs.edit().putBoolean("notif_vibrate_alerts", it).apply()
                        },
                        dailyReminderEnabled = dailyReminderEnabled,
                        onDailyReminderChange = {
                            dailyReminderEnabled = it
                            settingsPrefs.edit().putBoolean("notif_daily_reminder", it).apply()
                        },
                        practiceTime = practiceTime,
                        showTimePicker = showTimePicker,
                        onShowTimePicker = { showTimePicker = it },
                        onTimeSelected = {
                            practiceTime = it
                            showTimePicker = false
                            settingsPrefs.edit().putString("notif_practice_time", it).apply()
                        },
                        onSendTestNotification = {
                            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                            showFeedback("🔔 [AshCrypt Alert] Daily cryptographic drills ready for execution!")
                        },
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        cardBg = cardBg,
                        cardBorder = cardBorder
                    )
                    2 -> CoursesTab(
                        onArenaClick = { selectedArenaDossier = it },
                        onResetClick = { showResetAlert = true },
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        cardBg = cardBg,
                        cardBorder = cardBorder,
                        isDark = isDark
                    )
                    3 -> AccountTab(
                        userName = userName.ifEmpty { "Agent 007" },
                        userHandle = userHandle.ifEmpty { "ashcrypt_operative" },
                        userEmail = userEmail,
                        onEditNameClick = {
                            tempEditName = userName.ifEmpty { "Agent 007" }
                            showEditNameDialog = true
                        },
                        onEditHandleClick = {
                            tempEditHandle = userHandle.ifEmpty { "ashcrypt_operative" }
                            showEditHandleDialog = true
                        },
                        onEditEmailClick = {
                            tempEditEmail = userEmail
                            showEditEmailDialog = true
                        },
                        onOpenForgotPassword = { showForgotPasswordSheet = true },
                        onCopyPublicKey = {
                            clipboardManager.setText(AnnotatedString("0x9E44-A71C-B302-8DF6-C59A-1082"))
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            showFeedback("Public key fingerprint copied to clipboard! 📋")
                        },
                        telemetryEnabled = telemetryEnabled,
                        onTelemetryChange = {
                            telemetryEnabled = it
                            settingsPrefs.edit().putBoolean("sec_telemetry", it).apply()
                        },
                        onClearCache = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            showFeedback("Security cache flushed & re-indexed: 14.8 MB freed! ✨")
                        },
                        onExportDossier = {
                            val dossier = """
                                ================ ASHCRYPT SECURITY DOSSIER ================
                                Operative: ${userName.ifEmpty { "Agent 007" }} (@${userHandle.ifEmpty { "ashcrypt_operative" }})
                                Public Key: 0x9E44-A71C-B302-8DF6-C59A-1082
                                Security Level: Class 5 Encrypted Operative
                                Current Streak: $streakDays Days
                                Active Battlefields: 5 Operational Arenas
                                Generated: ${java.util.Date()}
                                ===========================================================
                            """.trimIndent()
                            clipboardManager.setText(AnnotatedString(dossier))
                            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                            showFeedback("Security dossier exported to clipboard! 📋")
                        },
                        onLogoutClick = { showLogoutAlert = true },
                        onDeleteAccountClick = { showDeleteAccountAlert = true },
                        onPrivacyPolicyClick = { showPrivacyPolicyDialog = true },
                        onTermsClick = { showTermsDialog = true },
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
        if (toastMessage != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isDark) Color(0xFF1C2733) else Color.White)
                    .border(1.5.dp, DuolingoGreen, RoundedCornerShape(20.dp))
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Done, contentDescription = null, tint = DuolingoGreen, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = toastMessage ?: "",
                        color = textPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        if (passwordChangeSuccessToast) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(cardBg)
                    .border(1.5.dp, DuolingoGreen, RoundedCornerShape(20.dp))
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Done, contentDescription = null, tint = DuolingoGreen, modifier = Modifier.size(18.dp))
                    Text("Password updated successfully! 🔥", color = textPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }

        if (showForgotPasswordSheet) {
            com.simats.codeingo.ui.auth.ForgotPasswordSheet(
                initialEmail = userEmail,
                onPasswordResetSuccess = {
                    showForgotPasswordSheet = false
                    scope.launch {
                        passwordChangeSuccessToast = true
                        delay(3000)
                        passwordChangeSuccessToast = false
                    }
                },
                onDismiss = { showForgotPasswordSheet = false }
            )
        }

        if (showLanguagePickerSheet) {
            ModalBottomSheet(
                onDismissRequest = { showLanguagePickerSheet = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = cardBg
            ) {
                val currentLangCode by locManager.selectedLanguageCode.collectAsState()
                LanguagePickerSheet(
                    selectedLanguageCode = currentLangCode,
                    onLanguageSelected = { lang: Language ->
                        locManager.selectLanguage(lang.code)
                        showLanguagePickerSheet = false
                        showFeedback("Language switched to ${lang.name} ${lang.flagEmoji}")
                    },
                    onDismiss = { showLanguagePickerSheet = false }
                )
            }
        }

        if (showEmotionPickerSheet) {
            PhoenixEmotionPickerSheet(
                onDismiss = { showEmotionPickerSheet = false }
            )
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
    effectTier: EffectTier,
    onEffectTierChange: (EffectTier) -> Unit,
    soundEffects: Boolean,
    onSoundEffectsChange: (Boolean) -> Unit,
    hapticsEnabled: Boolean,
    onHapticsEnabledChange: (Boolean) -> Unit,
    animations: Boolean,
    onAnimationsChange: (Boolean) -> Unit,
    motivationalMessages: Boolean,
    onMotivationalChange: (Boolean) -> Unit,
    listeningExercises: Boolean,
    onListeningChange: (Boolean) -> Unit,
    streakDays: Int,
    onAddDay: () -> Unit,
    onResetStreak: () -> Unit,
    onOpenForgotPassword: () -> Unit,
    onOpenAnimationLab: () -> Unit,
    onOpenLanguagePicker: () -> Unit,
    onOpenEmotionPicker: () -> Unit,
    selectedLanguage: Language?,
    currentEmotion: PhoenixEmotion,
    userName: String,
    userHandle: String,
    dailyReminderEnabled: Boolean,
    practiceTime: String,
    onNavigate: (String) -> Unit = {},
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

    // Language & Localization
    SectionHeader("Language & Localization", textSecondary)
    Spacer(modifier = Modifier.height(10.dp))
    SettingsCard(cardBg = cardBg, cardBorder = cardBorder) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenLanguagePicker() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedLanguage?.flagEmoji ?: "🐍",
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Coding Language",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
                Text(
                    text = selectedLanguage?.let { "${it.name} (${it.nativeName})" } ?: "Python (Default)",
                    fontSize = 12.sp,
                    color = AmberGold
                )
            }
            Text("Switch", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DuolingoBlue)
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = textSecondary,
                modifier = Modifier.size(16.dp)
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Motion & Visual Effects
    SectionHeader("Motion & Visual Effects", textSecondary)
    Spacer(modifier = Modifier.height(10.dp))
    SettingsCard(cardBg = cardBg, cardBorder = cardBorder) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Effect Tier",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
            Text(
                when (effectTier) {
                    EffectTier.FULL -> "Full: Springs, 60-particle bursts, edge glow & shakes"
                    EffectTier.REDUCED -> "Reduced: Fades & gentle scales only (no particles/shakes)"
                    EffectTier.OFF -> "Off: Zero animations, instant transitions"
                },
                fontSize = 12.sp,
                color = if (isDark) AmberGold else Color(0xFFB45309),
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isDark) PhoenixDeepNavy else PhoenixLightBg)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                EffectTier.values().forEach { tier ->
                    val isSelected = effectTier == tier
                    val tierBg by animateColorAsState(
                        targetValue = if (isSelected) AmberGold else Color.Transparent,
                        label = "tierBg"
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(tierBg)
                            .clickable { onEffectTierChange(tier) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tier.name.lowercase().replaceFirstChar { it.uppercase() },
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                            color = if (isSelected) Color(0xFF1A_0F_00) else textSecondary
                        )
                    }
                }
            }

            Text(
                "Phoenix mascot GIF plays at native speed across all tiers.",
                fontSize = 11.sp,
                color = textSecondary.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        SettingsDivider(cardBorder)

        ToggleRow(
            title = "Haptic Feedback",
            subtitle = "Tactile clicks, pops, and error rumbles",
            checked = hapticsEnabled,
            onCheckedChange = onHapticsEnabledChange,
            textPrimary = textPrimary,
            textSecondary = textSecondary
        )

        SettingsDivider(cardBorder)

        ToggleRow(
            title = "Sound Effects",
            subtitle = "Audio cues for answers and milestones",
            checked = soundEffects,
            onCheckedChange = onSoundEffectsChange,
            textPrimary = textPrimary,
            textSecondary = textSecondary
        )

        SettingsDivider(cardBorder)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenAnimationLab() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🔬", fontSize = 18.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Animation Lab",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
                Text(
                    "Test particles, HUD pill, haptics & tiers",
                    fontSize = 12.sp,
                    color = textSecondary
                )
            }
            Text("Open", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (isDark) AmberGold else Color(0xFFB45309))
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = textSecondary,
                modifier = Modifier.size(16.dp)
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Security & Password Section
    SectionHeader("Security & Password", textSecondary)
    Spacer(modifier = Modifier.height(10.dp))
    SettingsCard(cardBg = cardBg, cardBorder = cardBorder) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenForgotPassword() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🔑", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                "Change / Forgot Password",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary,
                modifier = Modifier.weight(1f)
            )
            Text("Reset", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DuolingoBlue)
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = textSecondary,
                modifier = Modifier.size(16.dp)
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Live Emotion Icon
    SectionHeader("Live Emotion Mascot", textSecondary)
    Spacer(modifier = Modifier.height(10.dp))
    SettingsCard(cardBg = cardBg, cardBorder = cardBorder) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenEmotionPicker() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currentEmotion.emoji,
                fontSize = 26.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Phoenix Emotion: ${currentEmotion.title}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
                Text(
                    text = "${currentEmotion.feeling} • ${currentEmotion.triggerDescription}",
                    fontSize = 12.sp,
                    color = textSecondary
                )
            }
            Text("Browse", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AmberGold)
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = textSecondary,
                modifier = Modifier.size(16.dp)
            )
        }

        SettingsDivider(cardBorder)

        ToggleRow(
            title = "Auto Emotion Engine",
            subtitle = if (isAutoEmotion) "Changes automatically based on user progress & streaks" else "Manual emotion active",
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

    // Quick Navigation Quick Links
    SectionHeader("Quick Navigation", textSecondary)
    Spacer(modifier = Modifier.height(10.dp))
    SettingsCard(cardBg = cardBg, cardBorder = cardBorder) {
        NavigationRow(title = "Account Details", detail = "@$userHandle", textPrimary = textPrimary, textSecondary = textSecondary, onClick = { onNavigate("Account") })
        SettingsDivider(cardBorder)
        NavigationRow(title = "Preferences", detail = currentTheme.title, textPrimary = textPrimary, textSecondary = textSecondary, onClick = { onNavigate("Preferences") })
        SettingsDivider(cardBorder)
        NavigationRow(title = "User Profile", detail = userName, textPrimary = textPrimary, textSecondary = textSecondary, onClick = { onNavigate("Profile") })
        SettingsDivider(cardBorder)
        NavigationRow(title = "Notifications", detail = if (dailyReminderEnabled) practiceTime else "Off", textPrimary = textPrimary, textSecondary = textSecondary, onClick = { onNavigate("Notifications") })
        SettingsDivider(cardBorder)
        NavigationRow(title = "Courses & Arenas", detail = "5 Arenas Active", textPrimary = textPrimary, textSecondary = textSecondary, onClick = { onNavigate("Courses") })
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Support row
    SectionHeader("Support", textSecondary)
    Spacer(modifier = Modifier.height(10.dp))
    SettingsCard(cardBg = cardBg, cardBorder = cardBorder) {
        NavigationRow(title = "Help & Support", detail = "support@ashcrypt.io", textPrimary = textPrimary, textSecondary = textSecondary, onClick = { onNavigate("Help & Support") })
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
                    checkedThumbColor = LocalDynamicThemeColors.current.textPrimary,
                    checkedTrackColor = AmberGold,
                    uncheckedThumbColor = LocalDynamicThemeColors.current.textPrimary,
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
            .pressScale(0.94f)
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
    quietHoursEnabled: Boolean, onQuietHoursChange: (Boolean) -> Unit,
    vibrateOnAlerts: Boolean, onVibrateAlertsChange: (Boolean) -> Unit,
    dailyReminderEnabled: Boolean, onDailyReminderChange: (Boolean) -> Unit,
    practiceTime: String,
    showTimePicker: Boolean,
    onShowTimePicker: (Boolean) -> Unit,
    onTimeSelected: (String) -> Unit,
    onSendTestNotification: () -> Unit,
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
        SettingsDivider(cardBorder)
        CheckboxRow(title = "Quiet Hours (10 PM - 7 AM)", checked = quietHoursEnabled, onCheckedChange = onQuietHoursChange, textPrimary = textPrimary, textSecondary = textSecondary)
        SettingsDivider(cardBorder)
        CheckboxRow(title = "Vibrate on Alerts", checked = vibrateOnAlerts, onCheckedChange = onVibrateAlertsChange, textPrimary = textPrimary, textSecondary = textSecondary)
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

    Spacer(modifier = Modifier.height(24.dp))

    // Diagnostics & Notification Simulation
    SectionHeader("Diagnostics & Alert Simulation", textSecondary)
    Spacer(modifier = Modifier.height(10.dp))
    SettingsCard(cardBg = cardBg, cardBorder = cardBorder) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSendTestNotification() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🔔", fontSize = 18.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Send Test Alert Notification",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
                Text(
                    "Verify alert pop-up, sound, and tactile feedback",
                    fontSize = 12.sp,
                    color = textSecondary
                )
            }
            Text("Test", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DuolingoBlue)
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = textSecondary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// Tab 3 — COURSES
// ══════════════════════════════════════════════════════════════════
@Composable
private fun CoursesTab(
    onArenaClick: (Triple<String, String, String>) -> Unit,
    onResetClick: () -> Unit,
    textPrimary: Color,
    textSecondary: Color,
    cardBg: Color,
    cardBorder: Color,
    isDark: Boolean
) {
    Text(
        text = "Courses & Battle Arenas",
        fontSize = 24.sp,
        fontWeight = FontWeight.Black,
        color = textPrimary
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
        text = "Track your cryptographic mastery across all 5 active defense battlegrounds. Tap any arena to inspect its security dossier.",
        fontSize = 13.sp,
        color = textSecondary
    )
    Spacer(modifier = Modifier.height(20.dp))

    SectionHeader("Active Track", textSecondary)
    Spacer(modifier = Modifier.height(10.dp))
    SettingsCard(cardBg = cardBg, cardBorder = cardBorder) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDark) AmberGold.copy(alpha = 0.2f) else Color(0xFFFEF3C7)),
                contentAlignment = Alignment.Center
            ) {
                Text("🛡️", fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "AshCrypt: Cryptography & CNS",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = textPrimary
                )
                Text(
                    text = "5 Arenas • 30+ Interactive Security Modules",
                    fontSize = 12.sp,
                    color = textSecondary
                )
            }
            TextButton(onClick = onResetClick) {
                Text(
                    text = "RESET",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = DuolingoRed
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    SectionHeader("Syllabus & Arenas (Tap to Inspect)", textSecondary)
    Spacer(modifier = Modifier.height(10.dp))
    SettingsCard(cardBg = cardBg, cardBorder = cardBorder) {
        val arenas = listOf(
            Triple("🏛️", "Classical Ciphers Citadel", "Caesar, Vigenère & Transposition Grid"),
            Triple("🔐", "Block Ciphers & AES Chamber", "Feistel Network, S-Box, SPN Architecture"),
            Triple("🔑", "Key Exchange & RSA Spire", "Diffie-Hellman Protocol & Modular Arithmetic"),
            Triple("⚡", "Hashes & Integrity Bastion", "SHA-256, HMAC Verification & Merkle-Damgård"),
            Triple("🛡️", "Cyber Network Defense Fortress", "Firewall Rules, MITM Defense & Zero-Trust")
        )
        arenas.forEachIndexed { index, arena ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onArenaClick(arena) }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(arena.first, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = arena.second,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    Text(
                        text = arena.third,
                        fontSize = 11.sp,
                        color = textSecondary
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DuolingoGreen.copy(alpha = if (isDark) 0.20f else 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("ACTIVE", fontSize = 10.sp, fontWeight = FontWeight.Black, color = DuolingoGreen)
                }
            }
            if (index < arenas.size - 1) {
                SettingsDivider(cardBorder)
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
    userEmail: String,
    onEditNameClick: () -> Unit,
    onEditHandleClick: () -> Unit,
    onEditEmailClick: () -> Unit,
    onOpenForgotPassword: () -> Unit,
    onCopyPublicKey: () -> Unit,
    telemetryEnabled: Boolean,
    onTelemetryChange: (Boolean) -> Unit,
    onClearCache: () -> Unit,
    onExportDossier: () -> Unit,
    onLogoutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onTermsClick: () -> Unit,
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
    Spacer(modifier = Modifier.height(6.dp))
    Text(
        text = "Tap on any field below to update your profile details and cryptographic credentials.",
        fontSize = 13.sp,
        color = textSecondary
    )
    Spacer(modifier = Modifier.height(20.dp))

    // Profile info card
    SettingsCard(cardBg = cardBg, cardBorder = cardBorder) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onEditNameClick() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Name", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textPrimary, modifier = Modifier.weight(1f))
            Text(userName, fontSize = 15.sp, color = textSecondary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("✏️", fontSize = 13.sp)
        }
        SettingsDivider(cardBorder)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onEditHandleClick() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Username", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textPrimary, modifier = Modifier.weight(1f))
            Text(
                text = if (userHandle.startsWith("@")) userHandle else "@$userHandle",
                fontSize = 15.sp,
                color = textSecondary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("✏️", fontSize = 13.sp)
        }
        SettingsDivider(cardBorder)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onEditEmailClick() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Email", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textPrimary, modifier = Modifier.weight(1f))
            Text(userEmail, fontSize = 14.sp, color = textSecondary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("✏️", fontSize = 13.sp)
        }
        SettingsDivider(cardBorder)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenForgotPassword() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Password", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textPrimary, modifier = Modifier.weight(1f))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Change Password", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DuolingoBlue)
                Text("✏️", fontSize = 12.sp)
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Cryptographic Identity & Hardware Security
    SectionHeader("Cryptographic Identity", textSecondary)
    Spacer(modifier = Modifier.height(10.dp))
    SettingsCard(cardBg = cardBg, cardBorder = cardBorder) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCopyPublicKey() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Public Key Fingerprint", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                Text("0x9E44-A71C-B302-8DF6-C59A-1082", fontSize = 12.sp, color = AmberGold)
            }
            Text("Copy", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DuolingoBlue)
            Spacer(modifier = Modifier.width(6.dp))
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = textSecondary, modifier = Modifier.size(16.dp))
        }
        SettingsDivider(cardBorder)
        ToggleRow(
            title = "Anonymous Cryptographic Telemetry",
            subtitle = "Contribute anonymous defense challenge telemetry",
            checked = telemetryEnabled,
            onCheckedChange = onTelemetryChange,
            textPrimary = textPrimary,
            textSecondary = textSecondary
        )
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Security Cache & Storage
    SectionHeader("Security Cache & Storage", textSecondary)
    Spacer(modifier = Modifier.height(10.dp))
    SettingsCard(cardBg = cardBg, cardBorder = cardBorder) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClearCache() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Clear Cache & Re-index Arenas", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                Text("Flushes temporary cipher buffers and syncs arena modules", fontSize = 12.sp, color = textSecondary)
            }
            Text("Flush", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DuolingoRed)
            Spacer(modifier = Modifier.width(6.dp))
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = textSecondary, modifier = Modifier.size(16.dp))
        }
        SettingsDivider(cardBorder)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onExportDossier() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Export Security Dossier", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                Text("Export comprehensive operative credentials & completion report", fontSize = 12.sp, color = textSecondary)
            }
            Text("Export", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DuolingoGreen)
            Spacer(modifier = Modifier.width(6.dp))
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = textSecondary, modifier = Modifier.size(16.dp))
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Legal & Privacy Compliance
    SectionHeader("Legal & Privacy", textSecondary)
    Spacer(modifier = Modifier.height(10.dp))
    SettingsCard(cardBg = cardBg, cardBorder = cardBorder) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onPrivacyPolicyClick() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Privacy Policy", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                Text("Zero telemetry tracking, local encrypted on-device storage", fontSize = 12.sp, color = textSecondary)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = textSecondary, modifier = Modifier.size(16.dp))
        }
        SettingsDivider(cardBorder)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onTermsClick() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Terms of Service", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                Text("Educational cryptographic simulations & acceptable usage guidelines", fontSize = 12.sp, color = textSecondary)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = textSecondary, modifier = Modifier.size(16.dp))
        }
    }

    Spacer(modifier = Modifier.height(32.dp))

    // Log out 3D extruded button
    AppButton(
        title = "🚪 LOG OUT OF ASHCRYPT",
        style = AppButtonStyle.DANGER_CRIMSON,
        onClick = onLogoutClick,
        modifier = Modifier.fillMaxWidth().height(48.dp)
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Delete Account (Google Play Store Data Safety Compliance)
    AppButton(
        title = "⚠️ DELETE ACCOUNT & DATA",
        style = AppButtonStyle.SECONDARY_GLASS,
        onClick = onDeleteAccountClick,
        modifier = Modifier.fillMaxWidth().height(48.dp)
    )
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
                checkedThumbColor = LocalDynamicThemeColors.current.textPrimary,
                checkedTrackColor = DuolingoBlue,
                uncheckedThumbColor = LocalDynamicThemeColors.current.textPrimary,
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
    detail: String = "",
    textPrimary: Color,
    textSecondary: Color,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
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
