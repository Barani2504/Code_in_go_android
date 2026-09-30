package com.simats.duolingo.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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

private enum class ProfileFlowStep { AGE, ACCOUNT }

@Composable
fun ProfileCreationSheet(
    onDismiss: () -> Unit,
    onOpenLogin: () -> Unit,
) {
    var currentStep by remember { mutableStateOf(ProfileFlowStep.AGE) }
    var ageText by remember { mutableStateOf("") }
    var nameText by remember { mutableStateOf("") }
    var emailText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var showGoogleAuthSheet by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

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
                if (currentStep == ProfileFlowStep.AGE) {
                    IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                        Text("✕", color = DuolingoSubtext, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    IconButton(onClick = { currentStep = ProfileFlowStep.AGE }, modifier = Modifier.size(36.dp)) {
                        Text("←", color = DuolingoSubtext, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Top right LOGIN button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(DuolingoDarkBg)
                        .border(1.5.dp, DuolingoBlue, RoundedCornerShape(12.dp))
                        .clickable(onClick = onOpenLogin)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text("LOGIN", color = DuolingoBlue, fontSize = 13.sp, fontWeight = FontWeight.Black)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                if (currentStep == ProfileFlowStep.AGE) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "How old are you?",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    DuoTextField(
                        value = ageText,
                        onValueChange = { if (it.all { char -> char.isDigit() }) ageText = it },
                        placeholder = "Age",
                        keyboardType = KeyboardType.Number,
                        trailingIcon = {
                            if (ageText.isNotEmpty()) {
                                Text(
                                    "✕",
                                    color = DuolingoSubtext,
                                    modifier = Modifier
                                        .clickable { ageText = "" }
                                        .padding(8.dp)
                                )
                            }
                        }
                    )

                    Text(
                        text = "Providing your age ensures you get the right learning experience. For more details, please visit our Privacy Policy.",
                        color = DuolingoSubtext,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    DuolingoButton(
                        text = "NEXT",
                        backgroundColor = DuolingoGreen,
                        shadowColor = DuolingoGreenDark,
                        enabled = ageText.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        currentStep = ProfileFlowStep.ACCOUNT
                    }

                    TermsNoticeView()
                } else {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Create your profile",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Google Sign-in button
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

                    DuoTextField(
                        value = nameText,
                        onValueChange = { nameText = it },
                        placeholder = "Name (optional)"
                    )

                    DuoTextField(
                        value = emailText,
                        onValueChange = { emailText = it },
                        placeholder = "Email or username",
                        keyboardType = KeyboardType.Email
                    )

                    DuoSecureField(
                        value = passwordText,
                        onValueChange = { passwordText = it },
                        placeholder = "Password"
                    )

                    val isFormValid = emailText.isNotEmpty() && passwordText.length >= 4

                    DuolingoButton(
                        text = "CREATE ACCOUNT",
                        backgroundColor = DuolingoBlue,
                        shadowColor = DuolingoBlueDark,
                        enabled = isFormValid,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (nameText.isNotEmpty()) {
                            AppState.userName = nameText
                            AppState.userHandle = nameText.replace(" ", "").lowercase() + "3454"
                        } else {
                            val namePart = emailText.substringBefore("@").replace(".", " ").capitalize()
                            AppState.userName = namePart
                            AppState.userHandle = emailText.substringBefore("@") + "3454"
                        }
                        AppState.isLoggedIn = true
                        toastMessage = "Account created successfully!"
                        coroutineScope.launch {
                            delay(1200)
                            onDismiss()
                        }
                    }

                    TermsNoticeView()
                }
            }
        }

        // Google Auth Modal
        if (showGoogleAuthSheet) {
            GoogleAuthSheet(
                onDismiss = { showGoogleAuthSheet = false },
                onSelectAccount = { selectedName, selectedEmail ->
                    AppState.userName = selectedName
                    AppState.userHandle = selectedEmail.substringBefore("@") + "3454"
                    AppState.isLoggedIn = true
                    toastMessage = "Signed in with Google as $selectedName"
                    coroutineScope.launch {
                        delay(1200)
                        onDismiss()
                    }
                }
            )
        }

        // Toast Message
        AnimatedVisibility(
            visible = toastMessage != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        ) {
            toastMessage?.let { msg ->
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(DuolingoGreen)
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(msg, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun TermsNoticeView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "By signing up, you agree to our Terms and Privacy Policy.",
            color = DuolingoSubtext,
            fontSize = 11.sp,
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
