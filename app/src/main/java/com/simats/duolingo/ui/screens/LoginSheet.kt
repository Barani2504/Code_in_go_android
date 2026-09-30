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
import com.simats.duolingo.ui.components.GoogleLogoView
import com.simats.duolingo.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class AuthMode { LOGIN, SIGN_UP }

@Composable
fun LoginSheet(
    onDismiss: () -> Unit,
) {
    var authMode by remember { mutableStateOf(AuthMode.LOGIN) }
    var emailOrUsername by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var showEmailFields by remember { mutableStateOf(false) }

    var isShowingToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    var isSuccessToast by remember { mutableStateOf(false) }

    var showGoogleAuthSheet by remember { mutableStateOf(false) }
    var showForgotPasswordSheet by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    fun showToast(msg: String, success: Boolean) {
        toastMessage = msg
        isSuccessToast = success
        isShowingToast = true
        coroutineScope.launch {
            delay(2500)
            isShowingToast = false
        }
    }

    val displayAccountName = AppState.userName.ifEmpty { "Vishal Rao" }
    val avatarInitial = displayAccountName.firstOrNull()?.uppercase() ?: "V"

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
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                    Text("✕", color = DuolingoSubtext, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(DuolingoCardBg.copy(alpha = 0.6f))
                        .border(1.5.dp, DuolingoInputBorder, RoundedCornerShape(12.dp))
                        .clickable {
                            authMode = if (authMode == AuthMode.LOGIN) AuthMode.SIGN_UP else AuthMode.LOGIN
                            isShowingToast = false
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = if (authMode == AuthMode.LOGIN) "SIGN UP" else "LOG IN",
                        color = DuolingoSubtext,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(modifier = Modifier.height(6.dp))

                // Title
                Text(
                    text = if (authMode == AuthMode.LOGIN) "Log in" else "Create your profile",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                // Avatar & Profile Header
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(165, 96, 232)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = avatarInitial,
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = displayAccountName,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Use another account",
                        color = DuolingoBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { showEmailFields = !showEmailFields }
                    )
                }

                // Google Sign In button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(DuolingoCardBg.copy(alpha = 0.6f))
                        .border(1.5.dp, DuolingoInputBorder, RoundedCornerShape(16.dp))
                        .clickable { showGoogleAuthSheet = true },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    GoogleLogoView(size = 20.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Google", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
                }

                // Email & Password Fields
                if (showEmailFields || authMode == AuthMode.SIGN_UP) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        if (authMode == AuthMode.SIGN_UP) {
                            DuoTextField(
                                value = name,
                                onValueChange = { name = it },
                                placeholder = "Name (optional)"
                            )

                            DuoTextField(
                                value = age,
                                onValueChange = { if (it.all { c -> c.isDigit() }) age = it },
                                placeholder = "Age",
                                keyboardType = KeyboardType.Number
                            )
                        }

                        DuoTextField(
                            value = emailOrUsername,
                            onValueChange = { emailOrUsername = it },
                            placeholder = "Email or username",
                            keyboardType = KeyboardType.Email
                        )

                        // Password with inline FORGOT? button
                        Box(contentAlignment = Alignment.CenterEnd) {
                            DuoSecureField(
                                value = password,
                                onValueChange = { password = it },
                                placeholder = "Password"
                            )

                            if (authMode == AuthMode.LOGIN) {
                                Text(
                                    text = "FORGOT?",
                                    color = DuolingoSubtext,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .padding(end = 40.dp)
                                        .clickable { showForgotPasswordSheet = true }
                                )
                            }
                        }

                        DuolingoButton(
                            text = if (authMode == AuthMode.LOGIN) "LOG IN" else "CREATE ACCOUNT",
                            backgroundColor = DuolingoBlue,
                            shadowColor = DuolingoBlueDark,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                        ) {
                            if (emailOrUsername.trim().isEmpty()) {
                                showToast("Please enter your email or username", false)
                                return@DuolingoButton
                            }
                            if (password.length < 4) {
                                showToast("Password must be at least 4 characters", false)
                                return@DuolingoButton
                            }

                            if (authMode == AuthMode.SIGN_UP && name.isNotEmpty()) {
                                AppState.userName = name
                                AppState.userHandle = name.replace(" ", "").lowercase() + "3454"
                            } else {
                                val namePart = emailOrUsername.substringBefore("@").replace(".", " ").capitalize()
                                AppState.userName = namePart
                                AppState.userHandle = emailOrUsername.substringBefore("@") + "3454"
                            }
                            AppState.isLoggedIn = true
                            showToast(if (authMode == AuthMode.LOGIN) "Logged in successfully!" else "Account created!", true)
                            coroutineScope.launch {
                                delay(1200)
                                onDismiss()
                            }
                        }
                    }
                }

                // Toast Notification
                AnimatedVisibility(
                    visible = isShowingToast,
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DuolingoCardBg)
                            .border(1.dp, if (isSuccessToast) DuolingoGreen else Color(0xFFFF9600), RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(if (isSuccessToast) "✓" else "⚠", color = if (isSuccessToast) DuolingoGreen else Color(0xFFFF9600), fontWeight = FontWeight.Bold)
                        Text(toastMessage, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }

                // Terms & Privacy Footer Disclaimer
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "By signing in to Duolingo, you agree to our Terms and Privacy Policy.",
                        color = DuolingoSubtext,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "This site is protected by reCAPTCHA Enterprise and the Google Privacy Policy and Terms of Service apply.",
                        color = DuolingoSubtext.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Sub-sheets
        if (showGoogleAuthSheet) {
            GoogleAuthSheet(
                onDismiss = { showGoogleAuthSheet = false },
                onSelectAccount = { selectedName, selectedEmail ->
                    AppState.userName = selectedName
                    AppState.userHandle = selectedEmail.substringBefore("@") + "3454"
                    AppState.isLoggedIn = true
                    showToast("Signed in as $selectedName", true)
                    coroutineScope.launch {
                        delay(1200)
                        onDismiss()
                    }
                }
            )
        }

        if (showForgotPasswordSheet) {
            ForgotPasswordSheet(
                initialEmail = emailOrUsername,
                onDismiss = { showForgotPasswordSheet = false },
                onPasswordResetSuccess = {
                    showToast("Password updated! Log in with your new password.", true)
                }
            )
        }
    }
}
