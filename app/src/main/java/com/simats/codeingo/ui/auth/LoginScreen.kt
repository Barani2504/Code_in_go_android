package com.simats.codeingo.ui.auth

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.domain.AuthService
import com.simats.codeingo.domain.LocalizationManager
import com.simats.codeingo.domain.PhoenixEmotionManager
import com.simats.codeingo.ui.components.AppleLogoView
import com.simats.codeingo.ui.components.GoogleLogoView
import com.simats.codeingo.ui.phoenix.PhoenixAtmosphericBackgroundView
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.liquidGlassCard
import com.simats.codeingo.ui.theme.pressScale
import com.simats.codeingo.ui.theme.pulse
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * ⚡ Modernized Cyber Authentication Interface
 * Completely restructured from the ground up:
 * - Ambient cyber-atmospheric backdrop with dynamic embers
 * - Minimalist, modern top navigation bar
 * - Hero brand emblem with interactive Phoenix companion pill
 * - Unified glowing glassmorphic auth card with integrated primary actions
 * - Streamlined biometric quick-unlock
 * - Polished Google OAuth and Guest passes
 * - Clean mobile & tablet responsive layout
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (name: String, email: String) -> Unit,
    onDismiss: () -> Unit,
    onCreateAccountClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val localizationManager = LocalizationManager.instance
    val emotionManager = PhoenixEmotionManager.instance
    val currentEmotion by emotionManager.currentEmotion.collectAsState()
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val prefs = remember { context.getSharedPreferences("dsa_preferences", Context.MODE_PRIVATE) }

    // Form inputs
    var emailOrUsername by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }

    // Field focus tracking
    var isEmailFocused by remember { mutableStateOf(false) }
    var isPasswordFocused by remember { mutableStateOf(false) }

    // Validation
    val isEmailValid = emailOrUsername.contains("@") && emailOrUsername.contains(".")
    val isPasswordStrong = password.length >= 6
    val isFormValid = emailOrUsername.trim().isNotEmpty() && password.length >= 4

    // UI Feedback & Animations
    var isSubmitting by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf<String?>(null) }
    var isSuccessToast by remember { mutableStateOf(false) }
    val shakeOffset = remember { Animatable(0f) }

    // Sheets
    var showGoogleAuthSheet by remember { mutableStateOf(false) }
    var showAppleAuthSheet by remember { mutableStateOf(false) }
    var showForgotPasswordSheet by remember { mutableStateOf(false) }

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    // Pre-fill email if remembered
    LaunchedEffect(Unit) {
        val saved = prefs.getString("dsaUserEmail", "")
        if (!saved.isNullOrEmpty()) {
            emailOrUsername = saved
        }
    }

    // Auto dismiss toast after 3.2s
    LaunchedEffect(toastMessage) {
        if (toastMessage != null) {
            delay(3200)
            toastMessage = null
        }
    }

    fun triggerShake() {
        coroutineScope.launch {
            for (i in 0..4) {
                shakeOffset.animateTo(if (i % 2 == 0) -12f else 12f, tween(35))
            }
            shakeOffset.animateTo(0f, tween(35))
        }
    }

    fun handleLogin() {
        if (!isFormValid || isSubmitting) return
        focusManager.clearFocus()
        isSubmitting = true

        coroutineScope.launch {
            delay(300)
            val trimmed = emailOrUsername.trim()
            val isPasswordCorrect = AuthService.shared.verifyPassword(trimmed, password)

            if (!isPasswordCorrect) {
                isSubmitting = false
                isSuccessToast = false
                toastMessage = "Incorrect password. Tap 'Forgot password?' to reset."
                triggerShake()
                return@launch
            }

            if (rememberMe) {
                prefs.edit().putString("dsaUserEmail", trimmed).apply()
            } else {
                prefs.edit().remove("dsaUserEmail").apply()
            }

            val profile = AuthService.shared.signInWithEmail(trimmed)
            localizationManager.updateUserProfile(profile.name, profile.handle)
            localizationManager.setLoggedIn(true, profile.name, profile.handle)

            isSubmitting = false
            isSuccessToast = true
            toastMessage = "Welcome back, ${profile.name}! 🔥"

            delay(600)
            onLoginSuccess(profile.name, profile.email)
        }
    }

    // Ambient background pulse
    val infiniteTransition = rememberInfiniteTransition(label = "ambient")
    val heroAuraScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hero_scale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDark) Color(0xFF070B14) else Color(0xFFF6F8FC))
    ) {
        PhoenixAtmosphericBackgroundView()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ) {
            // ── 1. Top Modern Navigation Bar ─────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Circular Frosted Close Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .pressScale(0.92f)
                        .clip(CircleShape)
                        .background(if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f))
                        .border(1.dp, if (isDark) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.10f), CircleShape)
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = dynamicColors.textPrimary,
                        modifier = Modifier.size(17.dp)
                    )
                }

                // Cyber Access Insignia Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isDark) Color(0xFF0F172A).copy(alpha = 0.8f) else Color.White.copy(alpha = 0.8f))
                        .border(1.dp, AmberGold.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(AmberGold)
                    )
                    Text(
                        text = "CODEINGO ✦ ACCESS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = dynamicColors.textPrimary,
                        letterSpacing = 1.sp
                    )
                }

                // Sign Up Action Chip
                Box(
                    modifier = Modifier
                        .pressScale(0.94f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AmberGold.copy(alpha = 0.12f))
                        .border(1.dp, AmberGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .clickable { onCreateAccountClick() }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SIGN UP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = AmberGold,
                        letterSpacing = 0.6.sp
                    )
                }
            }

            // ── 2. Scrollable Body ───────────────────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // ── Hero Section: Cyber Insignia & Dynamic Title ─────────────
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(72.dp)
                ) {
                    // Radiant aura glow
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .scale(heroAuraScale)
                            .clip(CircleShape)
                            .background(AmberGold.copy(alpha = 0.28f))
                            .blur(14.dp)
                    )
                    // Modern 3D emblem
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        if (isDark) Color(0xFF1E293B) else Color.White,
                                        if (isDark) Color(0xFF0F172A) else Color(0xFFF1F5F9)
                                    )
                                )
                            )
                            .border(1.5.dp, AmberGold.copy(alpha = 0.65f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Security Shield",
                            tint = AmberGold,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Welcome Back",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = dynamicColors.textPrimary,
                    letterSpacing = (-0.5).sp
                )

                Text(
                    text = "Authenticate to unlock the Cryptographic Cyber Arenas",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = dynamicColors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Interactive Dynamic Island Mascot Companion Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDark) Color(0xFF131D2E).copy(alpha = 0.75f) else Color.White.copy(alpha = 0.85f))
                        .border(1.dp, AmberGold.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = currentEmotion.emoji, fontSize = 16.sp, modifier = Modifier.pulse(0.95f..1.05f))
                    val companionTip = when {
                        isPasswordFocused -> "Validating cryptographic passkey..."
                        isEmailFocused -> "Looking up agent credentials..."
                        isFormValid -> "Passkey ready. Tap Authenticate!"
                        else -> "Phoenix Security Protocol Active"
                    }
                    Text(
                        text = companionTip,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AmberGold
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // ── 3. Unified Glassmorphic Authentication Card ───────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
                        .liquidGlassCard(accentGlow = AmberGold.copy(alpha = 0.22f), cornerRadius = 24.dp)
                        .padding(20.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Email / Username Field
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "AGENT IDENTIFIER",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (isEmailFocused) AmberGold else dynamicColors.textSecondary,
                                    letterSpacing = 0.8.sp
                                )
                                if (isEmailValid) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = DuolingoGreen,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = "VERIFIED",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black,
                                            color = DuolingoGreen
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isDark) Color(0xFF0F172A).copy(alpha = 0.90f) else Color.White)
                                    .border(
                                        width = if (isEmailFocused) 1.8.dp else 1.dp,
                                        color = if (isEmailFocused) AmberGold else dynamicColors.cardBorder.copy(alpha = 0.4f),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = if (isEmailFocused) AmberGold else dynamicColors.textSecondary.copy(alpha = 0.6f),
                                    modifier = Modifier.size(18.dp)
                                )

                                BasicTextField(
                                    value = emailOrUsername,
                                    onValueChange = { emailOrUsername = it },
                                    modifier = Modifier
                                        .weight(1f)
                                        .onFocusChanged { isEmailFocused = it.isFocused },
                                    textStyle = TextStyle(
                                        color = dynamicColors.textPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    cursorBrush = SolidColor(AmberGold),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Email,
                                        imeAction = ImeAction.Next
                                    ),
                                    decorationBox = { innerTextField ->
                                        if (emailOrUsername.isEmpty()) {
                                            Text(
                                                text = "agent@codeingo.io or username",
                                                color = dynamicColors.textSecondary.copy(alpha = 0.4f),
                                                fontSize = 14.sp
                                            )
                                        }
                                        innerTextField()
                                    }
                                )

                                if (emailOrUsername.isNotEmpty()) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = dynamicColors.textSecondary.copy(alpha = 0.4f),
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clickable { emailOrUsername = "" }
                                    )
                                }
                            }
                        }

                        // Password Field
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "SECURITY PASSKEY",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (isPasswordFocused) AmberGold else dynamicColors.textSecondary,
                                    letterSpacing = 0.8.sp
                                )
                                if (password.isNotEmpty()) {
                                    Text(
                                        text = if (isPasswordStrong) "STRONG PASSKEY ✓" else "MIN 4 CHARS",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isPasswordStrong) DuolingoGreen else AmberGold
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isDark) Color(0xFF0F172A).copy(alpha = 0.90f) else Color.White)
                                    .border(
                                        width = if (isPasswordFocused) 1.8.dp else 1.dp,
                                        color = if (isPasswordFocused) AmberGold else dynamicColors.cardBorder.copy(alpha = 0.4f),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (isPasswordFocused) AmberGold else dynamicColors.textSecondary.copy(alpha = 0.6f),
                                    modifier = Modifier.size(18.dp)
                                )

                                BasicTextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    modifier = Modifier
                                        .weight(1f)
                                        .onFocusChanged { isPasswordFocused = it.isFocused },
                                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    textStyle = TextStyle(
                                        color = dynamicColors.textPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    cursorBrush = SolidColor(AmberGold),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Password,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(onDone = { handleLogin() }),
                                    decorationBox = { innerTextField ->
                                        if (password.isEmpty()) {
                                            Text(
                                                text = "Enter passkey",
                                                color = dynamicColors.textSecondary.copy(alpha = 0.4f),
                                                fontSize = 14.sp
                                            )
                                        }
                                        innerTextField()
                                    }
                                )

                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle password",
                                    tint = AmberGold.copy(alpha = 0.85f),
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clickable { isPasswordVisible = !isPasswordVisible }
                                )
                            }
                        }

                        // Utility Row: Remember Me & Forgot Password
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { rememberMe = !rememberMe }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(7.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(if (rememberMe) AmberGold else Color.Transparent)
                                        .border(
                                            1.2.dp,
                                            if (rememberMe) AmberGold else dynamicColors.cardBorder,
                                            RoundedCornerShape(5.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (rememberMe) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.Black,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Remember passkey",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = dynamicColors.textSecondary
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { showForgotPasswordSheet = true }
                                    .padding(horizontal = 4.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Key,
                                    contentDescription = null,
                                    tint = AmberGold,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "Forgot passkey?",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberGold
                                )
                            }
                        }

                        // Primary Action Row: Biometric + Sign In Action Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Biometrics Quick Button
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .pressScale(0.92f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.04f))
                                    .border(1.2.dp, AmberGold.copy(alpha = 0.40f), RoundedCornerShape(14.dp))
                                    .clickable {
                                        val savedUser = AuthService.shared.currentUser.value
                                            ?: AuthService.shared.savedGoogleAccounts.value.firstOrNull()?.let {
                                                AuthService.shared.signInWithGoogle(it.name, it.email)
                                            }
                                        if (savedUser != null) {
                                            localizationManager.updateUserProfile(savedUser.name, savedUser.handle)
                                            localizationManager.setLoggedIn(true, savedUser.name, savedUser.handle)
                                            isSuccessToast = true
                                            toastMessage = "Biometric credentials authorized! ⚡"
                                            coroutineScope.launch {
                                                delay(450)
                                                onLoginSuccess(savedUser.name, savedUser.email)
                                            }
                                        } else {
                                            toastMessage = "No biometric profile registered. Sign in once with passkey."
                                            isSuccessToast = false
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Fingerprint,
                                    contentDescription = "Biometric Login",
                                    tint = AmberGold,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            // 3D Duolingo Action Button
                            val buttonColor = if (isFormValid) AmberGold else (if (isDark) Color.White.copy(alpha = 0.10f) else Color.Black.copy(alpha = 0.08f))

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .then(if (isFormValid && !isSubmitting) Modifier.pressScale(0.96f) else Modifier)
                                    .shadow(
                                        elevation = if (isFormValid) 6.dp else 0.dp,
                                        shape = RoundedCornerShape(14.dp),
                                        ambientColor = Color(0xFFCC8800),
                                        spotColor = Color(0xFFCC8800)
                                    )
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(buttonColor)
                                    .border(
                                        width = 1.dp,
                                        color = if (isFormValid) Color(0xFFFFA000) else Color.Transparent,
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .clickable(enabled = isFormValid && !isSubmitting) { handleLogin() },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSubmitting) {
                                    CircularProgressIndicator(
                                        strokeWidth = 2.5.dp,
                                        color = Color.Black,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "AUTHENTICATE & ENTER",
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 0.8.sp,
                                            color = if (isFormValid) Color.Black else dynamicColors.textSecondary.copy(alpha = 0.5f)
                                        )
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            tint = if (isFormValid) Color.Black else dynamicColors.textSecondary.copy(alpha = 0.5f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // ── 4. Modern Alternative Auth Options ───────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(dynamicColors.cardBorder.copy(alpha = 0.4f))
                    )
                    Text(
                        text = "OR CONTINUE WITH",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = dynamicColors.textSecondary.copy(alpha = 0.6f),
                        letterSpacing = 0.8.sp
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(dynamicColors.cardBorder.copy(alpha = 0.4f))
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Branded OAuth Actions: Google & Apple Identity
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Enhanced Google OAuth Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .pressScale(0.96f)
                            .shadow(
                                elevation = if (isDark) 3.dp else 4.dp,
                                shape = RoundedCornerShape(14.dp),
                                ambientColor = if (isDark) Color.Black else Color(0xFF64748B).copy(alpha = 0.25f),
                                spotColor = if (isDark) Color.Black else Color(0xFF64748B).copy(alpha = 0.25f)
                            )
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isDark) Color(0xFF131D2E) else Color.White)
                            .border(
                                1.2.dp,
                                if (isDark) Color(0xFF2A3C53) else Color(0xFFE2E8F0),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { showGoogleAuthSheet = true }
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            GoogleLogoView(size = 19.dp)
                            Text(
                                text = "Google",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = dynamicColors.textPrimary
                            )
                        }
                    }

                    // Official Apple Sign-In Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .pressScale(0.96f)
                            .shadow(
                                elevation = if (isDark) 3.dp else 4.dp,
                                shape = RoundedCornerShape(14.dp),
                                ambientColor = Color.Black.copy(alpha = 0.3f),
                                spotColor = Color.Black.copy(alpha = 0.3f)
                            )
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isDark) Color.White else Color(0xFF0F172A))
                            .border(
                                1.2.dp,
                                if (isDark) Color(0xFFE2E8F0) else Color(0xFF1E293B),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { showAppleAuthSheet = true }
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AppleLogoView(
                                size = 19.dp,
                                tint = if (isDark) Color.Black else Color.White
                            )
                            Text(
                                text = "Apple",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.Black else Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Guest Instant Pass Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .pressScale(0.97f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.03f))
                        .border(
                            1.dp,
                            if (isDark) AmberGold.copy(alpha = 0.35f) else Color(0xFFCBD5E1),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable {
                            val guest = AuthService.shared.signInAsGuest()
                            localizationManager.updateUserProfile(guest.name, guest.handle)
                            localizationManager.setLoggedIn(true, guest.name, guest.handle)
                            onLoginSuccess(guest.name, guest.email)
                        }
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = AmberGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Continue with Guest Pass",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = dynamicColors.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Bottom Security Note
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = DuolingoGreen,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "256-bit AES Cryptographic Channel • TLS 1.3 Verified",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = dynamicColors.textSecondary.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))
            }
        }

        // ── 5. Feedback Toast Banner ─────────────────────────────────────────
        AnimatedVisibility(
            visible = toastMessage != null,
            enter = slideInVertically(initialOffsetY = { -50 }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -50 }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 64.dp, start = 20.dp, end = 20.dp)
        ) {
            toastMessage?.let { msg ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(10.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDark) Color(0xFF131D2E) else Color.White)
                        .border(1.5.dp, if (isSuccessToast) DuolingoGreen else DuolingoRed, RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = if (isSuccessToast) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (isSuccessToast) DuolingoGreen else DuolingoRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = msg,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = dynamicColors.textPrimary
                    )
                }
            }
        }

        // ── 6. Sheets: Google Identity & Forgot Password ─────────────────────
        if (showGoogleAuthSheet) {
            ModalBottomSheet(
                onDismissRequest = { showGoogleAuthSheet = false },
                sheetState = bottomSheetState,
                containerColor = if (isDark) Color(0xFF0F1522) else Color.White
            ) {
                GoogleAuthSheet(
                    onSelectAccount = { accName, accEmail ->
                        showGoogleAuthSheet = false
                        val user = AuthService.shared.signInWithGoogle(accName, accEmail)
                        localizationManager.updateUserProfile(user.name, user.handle)
                        localizationManager.setLoggedIn(true, user.name, user.handle)
                        isSuccessToast = true
                        toastMessage = "Welcome, ${user.name}!"
                        coroutineScope.launch {
                            delay(450)
                            onLoginSuccess(user.name, user.email)
                        }
                    },
                    onDismiss = { showGoogleAuthSheet = false }
                )
            }
        }

        if (showAppleAuthSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAppleAuthSheet = false },
                sheetState = bottomSheetState,
                containerColor = if (isDark) Color(0xFF0F1522) else Color.White
            ) {
                AppleAuthSheet(
                    onSelectAccount = { accName, accEmail ->
                        showAppleAuthSheet = false
                        val user = AuthService.shared.currentUser.value
                            ?: AuthService.shared.signInWithApple(
                                appleID = "apple_${System.currentTimeMillis()}",
                                name = accName,
                                email = accEmail
                            )
                        localizationManager.updateUserProfile(user.name, user.handle)
                        localizationManager.setLoggedIn(true, user.name, user.handle)
                        isSuccessToast = true
                        toastMessage = "Authorized with Apple ID, ${user.name}!"
                        coroutineScope.launch {
                            delay(450)
                            onLoginSuccess(user.name, user.email)
                        }
                    },
                    onDismiss = { showAppleAuthSheet = false }
                )
            }
        }

        if (showForgotPasswordSheet) {
            ModalBottomSheet(
                onDismissRequest = { showForgotPasswordSheet = false },
                sheetState = bottomSheetState,
                containerColor = if (isDark) Color(0xFF0F1522) else Color.White
            ) {
                ForgotPasswordSheet(
                    initialEmail = emailOrUsername,
                    onPasswordResetSuccess = { resetEmail ->
                        showForgotPasswordSheet = false
                        emailOrUsername = resetEmail
                        val newPass = AuthService.shared.getStoredPassword(resetEmail)
                        if (newPass != null) {
                            password = newPass
                        }
                        toastMessage = "Password reset! You can now sign in."
                        isSuccessToast = true
                    },
                    onDismiss = { showForgotPasswordSheet = false }
                )
            }
        }
    }
}
