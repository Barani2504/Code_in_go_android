package com.simats.codeingo.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.R
import com.simats.codeingo.domain.LocalizationManager
import com.simats.codeingo.domain.PhoenixEmotionManager
import com.simats.codeingo.ui.components.Duolingo3DButton
import com.simats.codeingo.ui.components.Duolingo3DButtonStyle
import com.simats.codeingo.ui.components.GoogleLogoView
import com.simats.codeingo.ui.phoenix.PhoenixAtmosphericBackgroundView
import com.simats.codeingo.ui.theme.AmberGold
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * LoginScreen faithfully synchronized with iOS LoginView.swift.
 * Matches exact Duolingo Login Flow & Phoenix Bird Life Theme:
 * - PhoenixAtmosphericBackgroundView
 * - Top header with Close and CREATE ACCOUNT pill
 * - Phoenix Guide mascot with speech bubble
 * - Google Auth liquid glass button
 * - Animated form fields with focus strokes and eye password toggle
 * - Docked bottom bar with Duolingo3DButton
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (name: String, email: String) -> Unit,
    onDismiss: () -> Unit,
    onCreateAccountClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val localizationManager = LocalizationManager.instance
    val emotionManager = PhoenixEmotionManager.instance
    val currentEmotion by emotionManager.currentEmotion.collectAsState()

    var emailOrUsername by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    var isEmailFocused by remember { mutableStateOf(false) }
    var isPasswordFocused by remember { mutableStateOf(false) }

    var toastMessage by remember { mutableStateOf<String?>(null) }
    var isSuccessToast by remember { mutableStateOf(false) }

    var showGoogleAuthSheet by remember { mutableStateOf(false) }
    var showForgotPasswordSheet by remember { mutableStateOf(false) }

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    val isFormValid = emailOrUsername.trim().isNotEmpty() && password.length >= 4

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A0914))
    ) {
        PhoenixAtmosphericBackgroundView()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ) {
            // Top Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular Close Button with glass effect
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape)
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "SIGN IN",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                // CREATE ACCOUNT Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(AmberGold.copy(alpha = 0.14f))
                        .border(1.2.dp, AmberGold.copy(alpha = 0.40f), RoundedCornerShape(12.dp))
                        .clickable { onCreateAccountClick() }
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "CREATE ACCOUNT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = AmberGold
                    )
                }
            }

            // Scrollable Form Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // Phoenix Mascot with Speech Bubble (Exact Duolingo Style)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Mascot Icon with Aura
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(AmberGold.copy(alpha = 0.22f))
                                .blur(8.dp)
                        )
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF141F36))
                                .border(1.5.dp, AmberGold.copy(alpha = 0.45f), CircleShape)
                        ) {
                            Text(
                                text = currentEmotion.emoji,
                                fontSize = 24.sp
                            )
                        }
                    }

                    // Speech Bubble
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF141E34).copy(alpha = 0.90f))
                            .border(1.dp, AmberGold.copy(alpha = 0.30f), RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Phoenix Guide",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberGold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Welcome back! Enter your details to ignite your daily streak.",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Screen Title
                Text(
                    text = localizationManager.string("login_title"),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Google Auth Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(1.2.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(16.dp))
                        .clickable { showGoogleAuthSheet = true },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        GoogleLogoView(size = 20.dp)
                        Text(
                            text = localizationManager.string("google_auth"),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Divider Line with "OR"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(Color.White.copy(alpha = 0.12f))
                    )
                    Text(
                        text = "OR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White.copy(alpha = 0.40f)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(Color.White.copy(alpha = 0.12f))
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 1. Email or Username Field
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "EMAIL OR USERNAME",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = AmberGold.copy(alpha = 0.90f),
                        letterSpacing = 0.5.sp
                    )

                    val emailBorderColor = when {
                        isEmailFocused -> AmberGold
                        emailOrUsername.isNotEmpty() -> AmberGold.copy(alpha = 0.60f)
                        else -> Color.White.copy(alpha = 0.18f)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF141F36).copy(alpha = 0.85f))
                            .border(if (isEmailFocused) 2.dp else 1.2.dp, emailBorderColor, RoundedCornerShape(14.dp))
                            .padding(horizontal = 14.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = AmberGold.copy(alpha = 0.80f),
                            modifier = Modifier.size(18.dp)
                        )

                        BasicTextField(
                            value = emailOrUsername,
                            onValueChange = { emailOrUsername = it },
                            modifier = Modifier
                                .weight(1f)
                                .onFocusChanged { isEmailFocused = it.isFocused },
                            textStyle = TextStyle(
                                color = Color.White,
                                fontSize = 15.sp,
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
                                        text = localizationManager.string("email_or_username"),
                                        color = Color.White.copy(alpha = 0.35f),
                                        fontSize = 15.sp,
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
                                tint = Color.White.copy(alpha = 0.40f),
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { emailOrUsername = "" }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Password Field
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "PASSWORD",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = AmberGold.copy(alpha = 0.90f),
                        letterSpacing = 0.5.sp
                    )

                    val passwordBorderColor = when {
                        isPasswordFocused -> AmberGold
                        password.isNotEmpty() -> AmberGold.copy(alpha = 0.60f)
                        else -> Color.White.copy(alpha = 0.18f)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF141F36).copy(alpha = 0.85f))
                            .border(if (isPasswordFocused) 2.dp else 1.2.dp, passwordBorderColor, RoundedCornerShape(14.dp))
                            .padding(horizontal = 14.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = AmberGold.copy(alpha = 0.80f),
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
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            cursorBrush = SolidColor(AmberGold),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            decorationBox = { innerTextField ->
                                if (password.isEmpty()) {
                                    Text(
                                        text = localizationManager.string("password"),
                                        color = Color.White.copy(alpha = 0.35f),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                innerTextField()
                            }
                        )

                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle password",
                            tint = AmberGold.copy(alpha = 0.80f),
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { isPasswordVisible = !isPasswordVisible }
                        )
                    }
                }

                // Forgot Password Link
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = null,
                        tint = AmberGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Forgot password?",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberGold,
                        modifier = Modifier.clickable { showForgotPasswordSheet = true }
                    )
                }

                // Toast Notification Overlay
                AnimatedVisibility(
                    visible = toastMessage != null,
                    enter = slideInVertically() + fadeIn(),
                    exit = slideOutVertically() + fadeOut()
                ) {
                    toastMessage?.let { msg ->
                        Row(
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF141F36))
                                .border(1.5.dp, if (isSuccessToast) Color(0xFF33D17A) else Color(0xFFFF9500), RoundedCornerShape(12.dp))
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (isSuccessToast) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (isSuccessToast) Color(0xFF33D17A) else Color(0xFFFF9500),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = msg,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Docked Bottom Action Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F0E17).copy(alpha = 0.95f))
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Duolingo3DButton(
                    title = "LOG IN",
                    style = if (isFormValid) Duolingo3DButtonStyle.Amber else Duolingo3DButtonStyle.Disabled,
                    isEnabled = isFormValid,
                    onClick = {
                        val trimmed = emailOrUsername.trim()
                        val resolvedName = if (trimmed.contains("@")) {
                            trimmed.substringBefore("@").replaceFirstChar { it.uppercase() }
                        } else {
                            trimmed.replaceFirstChar { it.uppercase() }
                        }
                        val resolvedHandle = trimmed.substringBefore("@").lowercase() + "28"

                        localizationManager.updateUserProfile(resolvedName, resolvedHandle)
                        localizationManager.setLoggedIn(true)

                        isSuccessToast = true
                        toastMessage = "Signed in successfully as $resolvedName!"

                        coroutineScope.launch {
                            delay(600)
                            onLoginSuccess(resolvedName, emailOrUsername)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = localizationManager.string("terms_privacy"),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.40f),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = localizationManager.string("recaptcha"),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.30f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Google Auth Sheet
        if (showGoogleAuthSheet) {
            ModalBottomSheet(
                onDismissRequest = { showGoogleAuthSheet = false },
                sheetState = bottomSheetState,
                containerColor = Color(0xFF0F0E17)
            ) {
                GoogleAuthSheet(
                    onSelectAccount = { accName, accEmail ->
                        showGoogleAuthSheet = false
                        val handlePart = accEmail.substringBefore("@")
                        localizationManager.updateUserProfile(accName, handlePart.lowercase() + "2026")
                        localizationManager.setLoggedIn(true)
                        isSuccessToast = true
                        toastMessage = "Welcome, $accName!"
                        coroutineScope.launch {
                            delay(500)
                            onLoginSuccess(accName, accEmail)
                        }
                    },
                    onDismiss = { showGoogleAuthSheet = false }
                )
            }
        }

        // Forgot Password Sheet
        if (showForgotPasswordSheet) {
            ModalBottomSheet(
                onDismissRequest = { showForgotPasswordSheet = false },
                sheetState = bottomSheetState,
                containerColor = Color(0xFF0F0E17)
            ) {
                ForgotPasswordSheet(
                    initialEmail = emailOrUsername,
                    onPasswordResetSuccess = { resetEmail ->
                        showForgotPasswordSheet = false
                        toastMessage = "Password reset code sent to $resetEmail"
                        isSuccessToast = true
                    },
                    onDismiss = { showForgotPasswordSheet = false }
                )
            }
        }
    }
}
