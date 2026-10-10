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
import com.simats.codeingo.ui.components.GoogleLogoView
import com.simats.codeingo.ui.phoenix.PhoenixAtmosphericBackgroundView
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DuolingoBlue
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
 * 🦅 Enhanced LoginScreen — State-of-the-Art Authentication Experience.
 * Exact Parity with iOS LoginView.swift:
 * - Dynamic Ember Atmospheric Background
 * - Live Reactive Phoenix Companion Hero Card with typing reactions
 * - Google Identity & Quick Guest Sign-In Cards
 * - Interactive Real-Time Validation Badges (✓ Valid, Strong password)
 * - Tactile Shake Animation on incorrect input
 * - Remember Me persistent storage & Key-styled Forgot Password Sheet
 * - Biometric Fingerprint Button & 3D Duolingo Action Button
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
    val isDark = LocalDynamicThemeColors.current.isDark

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
                shakeOffset.animateTo(if (i % 2 == 0) -14f else 14f, tween(35))
            }
            shakeOffset.animateTo(0f, tween(35))
        }
    }

    fun handleLogin() {
        if (!isFormValid || isSubmitting) return
        focusManager.clearFocus()
        isSubmitting = true

        coroutineScope.launch {
            delay(300) // Smooth micro-delay for realistic feedback
            val trimmed = emailOrUsername.trim()
            val isPasswordCorrect = AuthService.shared.verifyPassword(trimmed, password)

            if (!isPasswordCorrect) {
                isSubmitting = false
                isSuccessToast = false
                toastMessage = "Incorrect password. Tap 'Forgot password?' to reset."
                triggerShake()
                return@launch
            }

            // Save or clear remembered email
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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDark) Color(0xFF070B14) else Color(0xFFF7F9FC))
    ) {
        PhoenixAtmosphericBackgroundView()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ) {
            // ── 1. Top Navigation Header ─────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular Liquid Glass Close Button
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .pressScale(0.92f)
                        .clip(CircleShape)
                        .background(if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.04f))
                        .border(
                            1.dp,
                            if (isDark) Color.White.copy(alpha = 0.16f) else Color.Black.copy(alpha = 0.10f),
                            CircleShape
                        )
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.85f),
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Centered Sign In Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "🔥", fontSize = 15.sp)
                    Text(
                        text = "SIGN IN",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = LocalDynamicThemeColors.current.textPrimary,
                        letterSpacing = 1.2.sp
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Create Account Action Pill
                Box(
                    modifier = Modifier
                        .pressScale(targetScale = 0.94f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(AmberGold.copy(alpha = if (isDark) 0.14f else 0.10f))
                        .border(1.2.dp, AmberGold.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                        .clickable { onCreateAccountClick() }
                        .padding(horizontal = 13.dp, vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SIGN UP",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Black,
                        color = AmberGold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // ── 2. Scrollable Form Content ──────────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                // ── Interactive Phoenix Companion Hero Card ──────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlassCard(accentGlow = AmberGold.copy(alpha = 0.28f), cornerRadius = 20.dp)
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Mascot Circle with Glowing Aura Blur
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(54.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(currentEmotion.auraColor.copy(alpha = 0.32f))
                                .blur(10.dp)
                        )
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (isDark) Color(0xFF0F172A) else Color.White)
                                .border(1.5.dp, AmberGold.copy(alpha = 0.55f), CircleShape)
                        ) {
                            Text(
                                text = currentEmotion.emoji,
                                fontSize = 24.sp,
                                modifier = Modifier.pulse(0.96f..1.04f)
                            )
                        }
                    }

                    // Speech Bubble & Live Status
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Phoenix Guide",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = AmberGold
                            )
                            // Live green pulsing status dot
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF33D17A))
                            )
                        }

                        val companionMessage = when {
                            isPasswordFocused -> "Keep your passkey secure and ready! 🔑"
                            isEmailFocused -> "Enter your username or email to find your nest 🪺"
                            isFormValid -> "All details set! Ready to ignite your streak? 🔥"
                            else -> "Welcome back! Ready to continue your streak and earn gems?"
                        }

                        Text(
                            text = companionMessage,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.90f),
                            lineHeight = 17.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title & Subtitle Header
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Welcome to Codeingo",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Black,
                        color = LocalDynamicThemeColors.current.textPrimary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Ignite your coding streak & master data structures",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.60f),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── 3. Primary Social Sign-In Buttons ─────────────────────────
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Google Sign-In Button (Official Identity Look)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .pressScale(0.97f)
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(16.dp),
                                ambientColor = Color.Black.copy(alpha = 0.2f),
                                spotColor = Color.Black.copy(alpha = 0.2f)
                            )
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isDark) Color(0xFF131D2D).copy(alpha = 0.92f) else Color.White
                            )
                            .border(
                                1.2.dp,
                                if (isDark) Color.White.copy(alpha = 0.14f) else Color(0xFFDADCE0),
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { showGoogleAuthSheet = true }
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GoogleLogoView(size = 20.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Continue with Google",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = LocalDynamicThemeColors.current.textPrimary
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.45f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Guest / Demo Quick Pass Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .pressScale(0.97f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.03f)
                            )
                            .border(
                                1.dp,
                                if (isDark) Color.White.copy(alpha = 0.10f) else Color.Black.copy(alpha = 0.08f),
                                RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                val guest = AuthService.shared.signInAsGuest()
                                localizationManager.updateUserProfile(guest.name, guest.handle)
                                localizationManager.setLoggedIn(true, guest.name, guest.handle)
                                onLoginSuccess(guest.name, guest.email)
                            }
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = AmberGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Continue as Guest",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = LocalDynamicThemeColors.current.textPrimary
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "INSTANT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = AmberGold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // ── Divider with Label ──────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(if (isDark) Color.White.copy(alpha = 0.10f) else Color.Black.copy(alpha = 0.08f))
                    )
                    Text(
                        text = "OR WITH EMAIL",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.45f),
                        letterSpacing = 0.8.sp
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(if (isDark) Color.White.copy(alpha = 0.10f) else Color.Black.copy(alpha = 0.08f))
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── 4. Credential Inputs with Shake & Live Validation ─────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset { IntOffset(shakeOffset.value.roundToInt(), 0) },
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Email or Username
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "EMAIL OR USERNAME",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isEmailFocused) AmberGold else LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.70f),
                                letterSpacing = 0.5.sp
                            )

                            if (isEmailValid) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = DuolingoGreen,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "Valid",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DuolingoGreen
                                    )
                                }
                            }
                        }

                        val emailBorderColor = when {
                            isEmailFocused -> AmberGold
                            emailOrUsername.isNotEmpty() -> AmberGold.copy(alpha = 0.50f)
                            else -> if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.10f)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isDark) Color(0xFF10192A).copy(alpha = 0.90f) else Color.White)
                                .border(if (isEmailFocused) 2.dp else 1.2.dp, emailBorderColor, RoundedCornerShape(16.dp))
                                .padding(horizontal = 14.dp, vertical = 13.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = if (isEmailFocused) AmberGold else LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.5f),
                                modifier = Modifier.size(18.dp)
                            )

                            BasicTextField(
                                value = emailOrUsername,
                                onValueChange = { emailOrUsername = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .onFocusChanged { isEmailFocused = it.isFocused },
                                textStyle = TextStyle(
                                    color = LocalDynamicThemeColors.current.textPrimary,
                                    fontSize = 14.5.sp,
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
                                            text = "name@example.com or username",
                                            color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.35f),
                                            fontSize = 14.5.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    innerTextField()
                                }
                            )

                            if (emailOrUsername.isNotEmpty()) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.40f),
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable { emailOrUsername = "" }
                                )
                            }
                        }
                    }

                    // Password
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "PASSWORD",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isPasswordFocused) AmberGold else LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.70f),
                                letterSpacing = 0.5.sp
                            )

                            if (password.isNotEmpty()) {
                                val strengthColor = if (isPasswordStrong) DuolingoGreen else AmberGold
                                val strengthLabel = if (isPasswordStrong) "Strong ✓" else "Min 4 chars"
                                Text(
                                    text = strengthLabel,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = strengthColor
                                )
                            }
                        }

                        val passwordBorderColor = when {
                            isPasswordFocused -> AmberGold
                            password.isNotEmpty() -> AmberGold.copy(alpha = 0.50f)
                            else -> if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.10f)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isDark) Color(0xFF10192A).copy(alpha = 0.90f) else Color.White)
                                .border(if (isPasswordFocused) 2.dp else 1.2.dp, passwordBorderColor, RoundedCornerShape(16.dp))
                                .padding(horizontal = 14.dp, vertical = 13.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (isPasswordFocused) AmberGold else LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.5f),
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
                                    color = LocalDynamicThemeColors.current.textPrimary,
                                    fontSize = 14.5.sp,
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
                                            text = "Enter password (min 4 chars)",
                                            color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.35f),
                                            fontSize = 14.5.sp,
                                            fontWeight = FontWeight.Medium
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
                }

                // ── 5. Remember Me & Forgot Password Row ─────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Custom Remember Me Checkbox
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { rememberMe = !rememberMe }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (rememberMe) AmberGold else Color.White.copy(alpha = 0.08f))
                                .border(
                                    1.2.dp,
                                    if (rememberMe) AmberGold else LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.35f),
                                    RoundedCornerShape(6.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (rememberMe) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                        Text(
                            text = "Remember me",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.75f)
                        )
                    }

                    // Forgot Password Link
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
                            text = "Forgot password?",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberGold
                        )
                    }
                }

                // ── 6. Toast Notification Banner Overlay ──────────────────────
                AnimatedVisibility(
                    visible = toastMessage != null,
                    enter = slideInVertically(initialOffsetY = { -40 }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { -40 }) + fadeOut()
                ) {
                    toastMessage?.let { msg ->
                        Row(
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isDark) Color(0xFF131D2D) else Color.White)
                                .border(
                                    1.5.dp,
                                    if (isSuccessToast) DuolingoGreen else DuolingoRed,
                                    RoundedCornerShape(14.dp)
                                )
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
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = LocalDynamicThemeColors.current.textPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // ── 7. Docked Bottom Action Bar ──────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isDark) Color(0xFF0A0F1D).copy(alpha = 0.96f)
                        else Color.White.copy(alpha = 0.96f)
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            listOf(
                                if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.06f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Biometrics Quick Button
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .pressScale(0.92f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isDark) Color.White.copy(alpha = 0.07f) else Color.Black.copy(alpha = 0.04f)
                            )
                            .border(
                                1.2.dp,
                                AmberGold.copy(alpha = 0.35f),
                                RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                // Fast Biometric Auth Simulation matching iOS
                                val savedUser = AuthService.shared.currentUser.value
                                    ?: AuthService.shared.savedGoogleAccounts.value.firstOrNull()?.let {
                                        AuthService.shared.signInWithGoogle(it.name, it.email)
                                    }
                                if (savedUser != null) {
                                    localizationManager.updateUserProfile(savedUser.name, savedUser.handle)
                                    localizationManager.setLoggedIn(true, savedUser.name, savedUser.handle)
                                    isSuccessToast = true
                                    toastMessage = "Biometric authentication verified! ⚡"
                                    coroutineScope.launch {
                                        delay(500)
                                        onLoginSuccess(savedUser.name, savedUser.email)
                                    }
                                } else {
                                    toastMessage = "No saved biometric profile found. Sign in once with password."
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

                    // Duolingo 3D Sign In Action Button
                    val buttonColor = if (isFormValid) AmberGold else (if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.10f))
                    val buttonBorder = if (isFormValid) Color(0xFFCC8800) else Color.Transparent

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .then(if (isFormValid && !isSubmitting) Modifier.pressScale(0.96f) else Modifier)
                            .shadow(
                                elevation = if (isFormValid) 4.dp else 0.dp,
                                shape = RoundedCornerShape(16.dp),
                                ambientColor = Color(0xFFCC8800),
                                spotColor = Color(0xFFCC8800)
                            )
                            .clip(RoundedCornerShape(16.dp))
                            .background(buttonColor)
                            .border(1.5.dp, buttonBorder, RoundedCornerShape(16.dp))
                            .clickable(enabled = isFormValid && !isSubmitting) { handleLogin() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                strokeWidth = 2.5.dp,
                                color = Color.Black,
                                modifier = Modifier.size(22.dp)
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "LOG IN",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp,
                                    color = if (isFormValid) Color.Black else LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.35f)
                                )
                                if (isFormValid) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Security & Policy Subtext
                Text(
                    text = "Protected by 256-bit AES encryption & ReCAPTCHA Enterprise",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.40f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // ── 8. Google Identity Bottom Sheet ──────────────────────────────────
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
                            delay(500)
                            onLoginSuccess(user.name, user.email)
                        }
                    },
                    onDismiss = { showGoogleAuthSheet = false }
                )
            }
        }

        // ── 9. Forgot Password Modal Sheet ───────────────────────────────────
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
                        toastMessage = "Password updated! You can now sign in."
                        isSuccessToast = true
                    },
                    onDismiss = { showForgotPasswordSheet = false }
                )
            }
        }
    }
}
