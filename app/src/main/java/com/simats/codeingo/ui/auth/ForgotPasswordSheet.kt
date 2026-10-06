package com.simats.codeingo.ui.auth

import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.components.CustomSecureField
import com.simats.codeingo.ui.components.CustomTextField
import com.simats.codeingo.ui.components.DuolingoButton
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DuolingoBlue
import com.simats.codeingo.ui.theme.DuolingoBlueDark
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoGreenDark
import com.simats.codeingo.ui.theme.SubtextGray
import kotlinx.coroutines.delay

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
                        tint = SubtextGray
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "RESET PASSWORD",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (step) {
                ForgotPasswordStep.ENTER_EMAIL -> {
                    Text(
                        text = "Forgot your password?",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Enter your email address and we'll send you a 6-digit recovery code.",
                        fontSize = 14.sp,
                        color = SubtextGray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                    CustomTextField(
                        value = email,
                        onValueChange = { email = it; errorMessage = null },
                        placeholder = "Email address",
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = errorMessage!!, color = Color.Red, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(28.dp))
                    DuolingoButton(
                        text = "SEND RECOVERY CODE",
                        faceColor = DuolingoGreen,
                        shadowColor = DuolingoGreenDark,
                        onClick = {
                            if (email.contains("@")) {
                                step = ForgotPasswordStep.SENDING_EMAIL
                            } else {
                                errorMessage = "Please enter a valid email address."
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                ForgotPasswordStep.SENDING_EMAIL -> {
                    Spacer(modifier = Modifier.height(48.dp))
                    CircularProgressIndicator(color = DuolingoGreen, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Sending recovery code...",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                ForgotPasswordStep.VERIFY_CODE -> {
                    Text(
                        text = "Enter Verification Code",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "We sent a 6-digit code to $email",
                        fontSize = 14.sp,
                        color = SubtextGray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                    CustomTextField(
                        value = verificationCode,
                        onValueChange = { verificationCode = it; errorMessage = null },
                        placeholder = "6-digit code (e.g. 123456)",
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = errorMessage!!, color = Color.Red, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(28.dp))
                    DuolingoButton(
                        text = "VERIFY CODE",
                        faceColor = DuolingoGreen,
                        shadowColor = DuolingoGreenDark,
                        onClick = {
                            if (verificationCode.trim().length >= 4) {
                                step = ForgotPasswordStep.RESET_PASSWORD
                            } else {
                                errorMessage = "Code must be at least 4 digits."
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                ForgotPasswordStep.RESET_PASSWORD -> {
                    Text(
                        text = "Create New Password",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(24.dp))
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
                        Text(text = errorMessage!!, color = Color.Red, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(28.dp))
                    DuolingoButton(
                        text = "UPDATE PASSWORD",
                        faceColor = DuolingoGreen,
                        shadowColor = DuolingoGreenDark,
                        onClick = {
                            if (newPassword.length < 6) {
                                errorMessage = "Password must be at least 6 characters."
                            } else if (newPassword != confirmPassword) {
                                errorMessage = "Passwords do not match."
                            } else {
                                step = ForgotPasswordStep.SUCCESS
                                onPasswordResetSuccess(email)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                ForgotPasswordStep.SUCCESS -> {
                    Spacer(modifier = Modifier.height(32.dp))
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = DuolingoGreen,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Password Reset Complete!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You can now log in with your updated password.",
                        fontSize = 14.sp,
                        color = SubtextGray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    DuolingoButton(
                        text = "RETURN TO LOGIN",
                        faceColor = DuolingoBlue,
                        shadowColor = DuolingoBlueDark,
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
