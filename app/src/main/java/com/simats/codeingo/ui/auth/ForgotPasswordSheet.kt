package com.simats.codeingo.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.components.AppButton
import com.simats.codeingo.ui.components.AppButtonStyle
import com.simats.codeingo.ui.components.AppCard
import com.simats.codeingo.ui.components.CustomSecureField
import com.simats.codeingo.ui.components.CustomTextField
import com.simats.codeingo.ui.phoenix.PhoenixAtmosphericBackgroundView
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.PhoenixGreen
import com.simats.codeingo.ui.theme.shake
import com.simats.codeingo.ui.theme.staggeredAppear
import kotlinx.coroutines.delay

// ══════════════════════════════════════════════════════════════════
// 🔑 ForgotPasswordSheet — Fluid Glassmorphic Password Recovery
// ══════════════════════════════════════════════════════════════════

enum class ForgotPasswordStep {
    ENTER_EMAIL,
    SENDING_EMAIL,
    VERIFY_CODE,
    RESET_PASSWORD,
    SUCCESS
}

@Composable
fun ForgotPasswordSheet(
    initialEmail: String = "",
    onPasswordResetSuccess: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableStateOf(ForgotPasswordStep.ENTER_EMAIL) }
    var email by remember { mutableStateOf(initialEmail) }
    var verificationCode by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val isDark = LocalDynamicThemeColors.current.isDark

    LaunchedEffect(step) {
        if (step == ForgotPasswordStep.SENDING_EMAIL) {
            delay(1200)
            step = ForgotPasswordStep.VERIFY_CODE
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        PhoenixAtmosphericBackgroundView()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = if (isDark) Color.White.copy(alpha = 0.85f) else Color(0xFF12_18_26)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "RESET PASSWORD",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif,
                    color = if (isDark) Color.White else Color(0xFF12_18_26),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            when (step) {
                ForgotPasswordStep.ENTER_EMAIL -> {
                    AppCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shake(errorMessage)
                            .staggeredAppear(0),
                        cornerRadius = 24.dp,
                        accentGlow = AmberGold.copy(alpha = 0.20f)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Forgot your password?",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.SansSerif,
                                color = if (isDark) Color.White else Color(0xFF12_18_26),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Enter your registered email address to receive a 6-digit recovery code.",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = FontFamily.SansSerif,
                                color = if (isDark) Color.White.copy(alpha = 0.65f) else Color(0xFF64_74_8B),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(22.dp))
                            CustomTextField(
                                value = email,
                                onValueChange = { email = it; errorMessage = null },
                                placeholder = "Email address",
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (errorMessage != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = errorMessage!!,
                                    color = Color(0xFFE8_24_10),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            AppButton(
                                title = "SEND RECOVERY CODE",
                                style = AppButtonStyle.PRIMARY_AMBER,
                                onClick = {
                                    if (email.contains("@")) {
                                        step = ForgotPasswordStep.SENDING_EMAIL
                                    } else {
                                        errorMessage = "Please enter a valid email address."
                                    }
                                }
                            )
                        }
                    }
                }

                ForgotPasswordStep.SENDING_EMAIL -> {
                    Spacer(modifier = Modifier.height(60.dp))
                    CircularProgressIndicator(color = AmberGold, modifier = Modifier.size(52.dp))
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Sending recovery code...",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        color = if (isDark) Color.White else Color(0xFF12_18_26)
                    )
                }

                ForgotPasswordStep.VERIFY_CODE -> {
                    AppCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shake(errorMessage)
                            .staggeredAppear(0),
                        cornerRadius = 24.dp,
                        accentGlow = AmberGold.copy(alpha = 0.20f)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Enter Verification Code",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.SansSerif,
                                color = if (isDark) Color.White else Color(0xFF12_18_26),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "We sent a 6-digit code to $email",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = FontFamily.SansSerif,
                                color = if (isDark) Color.White.copy(alpha = 0.65f) else Color(0xFF64_74_8B),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(22.dp))
                            CustomTextField(
                                value = verificationCode,
                                onValueChange = { verificationCode = it; errorMessage = null },
                                placeholder = "6-digit code (e.g. 123456)",
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (errorMessage != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = errorMessage!!,
                                    color = Color(0xFFE8_24_10),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            AppButton(
                                title = "VERIFY CODE",
                                style = AppButtonStyle.PRIMARY_AMBER,
                                onClick = {
                                    if (verificationCode.trim().length >= 4) {
                                        step = ForgotPasswordStep.RESET_PASSWORD
                                    } else {
                                        errorMessage = "Code must be at least 4 digits."
                                    }
                                }
                            )
                        }
                    }
                }

                ForgotPasswordStep.RESET_PASSWORD -> {
                    AppCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shake(errorMessage)
                            .staggeredAppear(0),
                        cornerRadius = 24.dp,
                        accentGlow = AmberGold.copy(alpha = 0.20f)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Create New Password",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.SansSerif,
                                color = if (isDark) Color.White else Color(0xFF12_18_26),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            CustomSecureField(
                                value = newPassword,
                                onValueChange = { newPassword = it; errorMessage = null },
                                placeholder = "New password",
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            CustomSecureField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it; errorMessage = null },
                                placeholder = "Confirm password",
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (errorMessage != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = errorMessage!!,
                                    color = Color(0xFFE8_24_10),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            AppButton(
                                title = "UPDATE PASSWORD",
                                style = AppButtonStyle.SUCCESS_GREEN,
                                onClick = {
                                    if (newPassword.length < 6) {
                                        errorMessage = "Password must be at least 6 characters."
                                    } else if (newPassword != confirmPassword) {
                                        errorMessage = "Passwords do not match."
                                    } else {
                                        errorMessage = null
                                        com.simats.codeingo.domain.AuthService.shared.updatePassword(email, newPassword)
                                        step = ForgotPasswordStep.SUCCESS
                                    }
                                }
                            )
                        }
                    }
                }

                ForgotPasswordStep.SUCCESS -> {
                    AppCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .staggeredAppear(0),
                        cornerRadius = 24.dp,
                        accentGlow = PhoenixGreen.copy(alpha = 0.25f)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(PhoenixGreen.copy(alpha = 0.20f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Success",
                                    tint = PhoenixGreen,
                                    modifier = Modifier.size(44.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Password Updated!",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.SansSerif,
                                color = if (isDark) Color.White else Color(0xFF12_18_26)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Your password has been successfully updated. You can now sign in with your new credentials.",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = FontFamily.SansSerif,
                                color = if (isDark) Color.White.copy(alpha = 0.65f) else Color(0xFF64_74_8B),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            AppButton(
                                title = "CONTINUE TO SIGN IN",
                                style = AppButtonStyle.PRIMARY_AMBER,
                                onClick = { onPasswordResetSuccess(email) }
                            )
                        }
                    }
                }
            }
        }
    }
}
