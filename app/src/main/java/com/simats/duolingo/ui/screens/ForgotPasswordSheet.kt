package com.simats.duolingo.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.duolingo.data.AppState
import com.simats.duolingo.ui.components.DuoSecureField
import com.simats.duolingo.ui.components.DuoTextField
import com.simats.duolingo.ui.components.DuolingoButton
import com.simats.duolingo.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class ForgotPasswordStep {
    ENTER_EMAIL,
    SENDING_EMAIL,
    VERIFY_CODE,
    RESET_PASSWORD,
    SUCCESS
}

@Composable
fun ForgotPasswordSheet(
    initialEmail: String = "",
    onDismiss: () -> Unit,
    onPasswordResetSuccess: (String) -> Unit = {}
) {
    var step by remember { mutableStateOf(ForgotPasswordStep.ENTER_EMAIL) }
    var email by remember { mutableStateOf(initialEmail.ifEmpty { "vishal.rao@gmail.com" }) }
    var verificationCode by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var resendTimer by remember { mutableIntStateOf(30) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(step) {
        if (step == ForgotPasswordStep.VERIFY_CODE) {
            resendTimer = 30
            while (resendTimer > 0) {
                delay(1000)
                resendTimer -= 1
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DuolingoDarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                    Text("✕", color = DuolingoSubtext, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                Text(
                    text = "RESET PASSWORD",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.size(36.dp))
            }

            HorizontalDivider(color = DuolingoInputBorder)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                when (step) {
                    ForgotPasswordStep.ENTER_EMAIL -> {
                        Text("✉️", fontSize = 54.sp)
                        Text(
                            text = "Forgot your password?",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Enter your email address and we'll send you a 6-digit verification code to reset your password.",
                            color = DuolingoSubtext,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("EMAIL ADDRESS", color = DuolingoSubtext, fontSize = 11.sp, fontWeight = FontWeight.Black)
                            DuoTextField(
                                value = email,
                                onValueChange = { email = it; errorMessage = null },
                                placeholder = "name@example.com",
                                keyboardType = KeyboardType.Email
                            )
                        }

                        errorMessage?.let {
                            Text(it, color = Color(0xFFFF9600), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }

                        DuolingoButton(
                            text = "SEND VERIFICATION CODE",
                            backgroundColor = DuolingoBlue,
                            shadowColor = DuolingoBlueDark,
                            enabled = email.isNotEmpty(),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (email.contains("@")) {
                                step = ForgotPasswordStep.SENDING_EMAIL
                                coroutineScope.launch {
                                    delay(1200)
                                    step = ForgotPasswordStep.VERIFY_CODE
                                }
                            } else {
                                errorMessage = "Please enter a valid email address"
                            }
                        }
                    }

                    ForgotPasswordStep.SENDING_EMAIL -> {
                        Spacer(modifier = Modifier.height(60.dp))
                        CircularProgressIndicator(color = DuolingoBlue, modifier = Modifier.size(50.dp))
                        Text(
                            text = "Sending security code...",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    ForgotPasswordStep.VERIFY_CODE -> {
                        Text("🔑", fontSize = 54.sp)
                        Text(
                            text = "Enter 6-digit code",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "We sent a 6-digit verification code to $email",
                            color = DuolingoSubtext,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )

                        DuoTextField(
                            value = verificationCode,
                            onValueChange = { if (it.length <= 6) verificationCode = it; errorMessage = null },
                            placeholder = "123456",
                            keyboardType = KeyboardType.Number
                        )

                        errorMessage?.let {
                            Text(it, color = Color(0xFFFF9600), fontSize = 13.sp)
                        }

                        DuolingoButton(
                            text = "VERIFY CODE",
                            backgroundColor = DuolingoGreen,
                            shadowColor = DuolingoGreenDark,
                            enabled = verificationCode.length >= 4,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            step = ForgotPasswordStep.RESET_PASSWORD
                        }

                        Text(
                            text = if (resendTimer > 0) "Resend code in ${resendTimer}s" else "Resend Code",
                            color = if (resendTimer > 0) DuolingoSubtext else DuolingoBlue,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable(enabled = resendTimer == 0) {
                                resendTimer = 30
                            }
                        )
                    }

                    ForgotPasswordStep.RESET_PASSWORD -> {
                        Text("🔒", fontSize = 54.sp)
                        Text(
                            text = "Set new password",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Choose a strong password with at least 6 characters.",
                            color = DuolingoSubtext,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("NEW PASSWORD", color = DuolingoSubtext, fontSize = 11.sp, fontWeight = FontWeight.Black)
                            DuoSecureField(
                                value = newPassword,
                                onValueChange = { newPassword = it; errorMessage = null },
                                placeholder = "Enter new password"
                            )
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("CONFIRM PASSWORD", color = DuolingoSubtext, fontSize = 11.sp, fontWeight = FontWeight.Black)
                            DuoSecureField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it; errorMessage = null },
                                placeholder = "Re-enter new password"
                            )
                        }

                        errorMessage?.let {
                            Text(it, color = Color(0xFFFF9600), fontSize = 13.sp)
                        }

                        DuolingoButton(
                            text = "SAVE PASSWORD",
                            backgroundColor = DuolingoGreen,
                            shadowColor = DuolingoGreenDark,
                            enabled = newPassword.length >= 4 && newPassword == confirmPassword,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (newPassword.length < 4) {
                                errorMessage = "Password must be at least 4 characters"
                            } else if (newPassword != confirmPassword) {
                                errorMessage = "Passwords do not match"
                            } else {
                                onPasswordResetSuccess(newPassword)
                                step = ForgotPasswordStep.SUCCESS
                            }
                        }
                    }

                    ForgotPasswordStep.SUCCESS -> {
                        Text("🎉", fontSize = 60.sp)
                        Text(
                            text = "Password Reset!",
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Your password has been successfully updated. You can now log in with your new credentials.",
                            color = DuolingoSubtext,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )

                        DuolingoButton(
                            text = "BACK TO LOGIN",
                            backgroundColor = DuolingoGreen,
                            shadowColor = DuolingoGreenDark,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            onDismiss()
                        }
                    }
                }
            }
        }
    }
}
