package com.simats.duolingo.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.*
import com.simats.duolingo.data.AppState
import com.simats.duolingo.ui.components.DuoSecureField
import com.simats.duolingo.ui.components.DuoTextField
import com.simats.duolingo.ui.components.DuolingoButton
import com.simats.duolingo.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class AuthMode { LOGIN, SIGN_UP }

@Composable
fun LoginSheet(onDismiss: () -> Unit) {
    var authMode by remember { mutableStateOf(AuthMode.LOGIN) }
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name     by remember { mutableStateOf("") }
    var age      by remember { mutableStateOf("") }
    var showEmailFields by remember { mutableStateOf(false) }

    var toastMsg     by remember { mutableStateOf("") }
    var isToastShown by remember { mutableStateOf(false) }
    var isSuccess    by remember { mutableStateOf(false) }

    suspend fun showToast(msg: String, success: Boolean) {
        toastMsg = msg
        isSuccess = success
        isToastShown = true
        delay(2000)
        isToastShown = false
        if (success) {
            delay(300)
            onDismiss()
        }
    }

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.95f)
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(DuolingoDarkBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ── Top bar ───────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(DuolingoCardBg)
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✕", color = DuolingoSubtext, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(DuolingoCardBg.copy(alpha = 0.6f))
                        .border(1.5.dp, DuolingoInputBorder, RoundedCornerShape(12.dp))
                        .clickable {
                            authMode = if (authMode == AuthMode.LOGIN) AuthMode.SIGN_UP else AuthMode.LOGIN
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        if (authMode == AuthMode.LOGIN) "SIGN UP" else "LOG IN",
                        color = DuolingoSubtext,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
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
                Spacer(Modifier.height(4.dp))

                // Title
                Text(
                    text = if (authMode == AuthMode.LOGIN) "Log in" else "Create your profile",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )

                // Avatar card
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFA560E8)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            AppState.userName.firstOrNull()?.uppercase() ?: "V",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Text(AppState.userName, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "Use another account",
                        color = DuolingoBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { showEmailFields = !showEmailFields }
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(DuolingoCardBg.copy(alpha = 0.6f))
                        .border(1.5.dp, DuolingoInputBorder, RoundedCornerShape(16.dp))
                        .clickable {
                            // simulate Google auth success
                            AppState.isLoggedIn = true
                            scope.launch {
                                showToast("Signed in with Google!", true)
                            }
                        }
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("G", color = DuolingoBlue, fontSize = 18.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.width(10.dp))
                    Text("Continue with Google", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
                }

                // Email/password fields
                AnimatedVisibility(
                    visible = showEmailFields || authMode == AuthMode.SIGN_UP,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        if (authMode == AuthMode.SIGN_UP) {
                            DuoTextField(name, { name = it }, "Name (optional)")
                            DuoTextField(age, { age = it }, "Age", keyboardType = KeyboardType.Number)
                        }
                        DuoTextField(email, { email = it }, "Email or username", keyboardType = KeyboardType.Email)
                        DuoSecureField(password, { password = it }, "Password")

                        DuolingoButton(
                            text = if (authMode == AuthMode.LOGIN) "LOG IN" else "CREATE ACCOUNT",
                            backgroundColor = DuolingoBlue,
                            shadowColor = DuolingoBlueDark,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                if (email.isBlank()) {
                                    scope.launch {
                                        showToast("Please enter your email", false)
                                    }
                                    return@DuolingoButton
                                }
                                if (password.length < 4) {
                                    scope.launch {
                                        showToast("Password too short", false)
                                    }
                                    return@DuolingoButton
                                }
                                AppState.isLoggedIn = true
                                if (name.isNotEmpty()) AppState.userName = name
                                scope.launch {
                                    showToast(
                                        if (authMode == AuthMode.LOGIN) "Welcome back!" else "Account created!",
                                        true
                                    )
                                }
                            }
                        )
                    }
                }

                // Toast
                AnimatedVisibility(isToastShown, enter = fadeIn(), exit = fadeOut()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DuolingoCardBg)
                            .border(1.dp, if (isSuccess) DuolingoGreen else DuolingoOrange, RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(if (isSuccess) "✅" else "⚠️", fontSize = 18.sp)
                        Text(toastMsg, color = Color.White, fontSize = 13.sp)
                    }
                }

                // Footer
                Text(
                    text = "By continuing, you agree to our Terms of Service and Privacy Policy.",
                    color = DuolingoSubtext,
                    fontSize = 12.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
