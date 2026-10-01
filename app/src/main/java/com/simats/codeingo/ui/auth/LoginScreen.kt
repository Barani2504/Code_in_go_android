package com.simats.codeingo.ui.auth

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.components.CustomSecureField
import com.simats.codeingo.ui.components.CustomTextField
import com.simats.codeingo.ui.components.DuolingoButton
import com.simats.codeingo.ui.components.FacebookLogoView
import com.simats.codeingo.ui.components.GoogleLogoView
import com.simats.codeingo.ui.components.ToastBanner
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DuolingoBlue
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoGreenDark
import com.simats.codeingo.ui.theme.InputBorder
import com.simats.codeingo.ui.theme.SubtextGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (name: String, email: String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSignUpMode by remember { mutableStateOf(false) }
    var emailOrUsername by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var showEmailFields by remember { mutableStateOf(false) }

    var showGoogleAuthSheet by remember { mutableStateOf(false) }
    var showForgotPasswordSheet by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf<String?>(null) }
    var isSuccessToast by remember { mutableStateOf(false) }

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar: Close X + Sign Up / Log In Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
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

                val toggleShape = RoundedCornerShape(12.dp)
                Box(
                    modifier = Modifier
                        .clip(toggleShape)
                        .background(CardBackground.copy(alpha = 0.6f))
                        .border(1.5.dp, InputBorder, toggleShape)
                        .clickable { isSignUpMode = !isSignUpMode }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = if (isSignUpMode) "LOG IN" else "SIGN UP",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = SubtextGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = if (isSignUpMode) "Create your profile" else "Log in",
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            // User Avatar Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFA560E8)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (name.isNotEmpty()) name.first().uppercase() else "V",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (name.isNotEmpty()) name else "Vishal Rao",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = if (showEmailFields) "Hide credentials" else "Use another account",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = DuolingoBlue,
                    modifier = Modifier
                        .clickable { showEmailFields = !showEmailFields }
                        .padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Social Logins
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Google Button
                val socialShape = RoundedCornerShape(16.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(socialShape)
                        .background(CardBackground)
                        .border(1.5.dp, InputBorder, socialShape)
                        .clickable { showGoogleAuthSheet = true }
                        .padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GoogleLogoView(size = 20.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "CONTINUE WITH GOOGLE",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Facebook Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(socialShape)
                        .background(CardBackground)
                        .border(1.5.dp, InputBorder, socialShape)
                        .clickable {
                            onLoginSuccess("Facebook User", "user@facebook.com")
                        }
                        .padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FacebookLogoView(size = 20.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "CONTINUE WITH FACEBOOK",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Divider OR
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(InputBorder)
                )
                Text(
                    text = "OR",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SubtextGray,
                    modifier = Modifier.padding(horizontal = 14.dp)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(InputBorder)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Email & Password Fields
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isSignUpMode) {
                    CustomTextField(
                        value = age,
                        onValueChange = { age = it },
                        placeholder = "Age",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CustomTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = "Name (optional)",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                CustomTextField(
                    value = emailOrUsername,
                    onValueChange = { emailOrUsername = it },
                    placeholder = "Email or username",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                CustomSecureField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Password",
                    modifier = Modifier.fillMaxWidth()
                )

                if (!isSignUpMode) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "FORGOT PASSWORD?",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DuolingoBlue,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable { showForgotPasswordSheet = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Action Button
            DuolingoButton(
                text = if (isSignUpMode) "CREATE ACCOUNT" else "LOG IN",
                faceColor = DuolingoGreen,
                shadowColor = DuolingoGreenDark,
                onClick = {
                    if (emailOrUsername.isNotEmpty() && password.isNotEmpty()) {
                        isSuccessToast = true
                        toastMessage = if (isSignUpMode) "Profile created successfully!" else "Welcome back!"
                        onLoginSuccess(
                            if (name.isNotEmpty()) name else emailOrUsername.substringBefore("@"),
                            emailOrUsername
                        )
                    } else {
                        isSuccessToast = false
                        toastMessage = "Please enter both email and password."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "By signing in, you agree to our Terms and Privacy Policy.",
                fontSize = 12.sp,
                color = SubtextGray,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(36.dp))
        }

        // Toast overlay
        ToastBanner(
            message = toastMessage,
            isSuccess = isSuccessToast,
            onDismiss = { toastMessage = null }
        )

        // Google Auth Sheet
        if (showGoogleAuthSheet) {
            ModalBottomSheet(
                onDismissRequest = { showGoogleAuthSheet = false },
                sheetState = bottomSheetState,
                containerColor = DarkBackground
            ) {
                GoogleAuthSheet(
                    onSelectAccount = { accName, accEmail ->
                        onLoginSuccess(accName, accEmail)
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
                containerColor = DarkBackground
            ) {
                ForgotPasswordSheet(
                    initialEmail = emailOrUsername,
                    onPasswordResetSuccess = { resetEmail ->
                        toastMessage = "Password reset code sent to $resetEmail"
                        isSuccessToast = true
                    },
                    onDismiss = { showForgotPasswordSheet = false }
                )
            }
        }
    }
}
