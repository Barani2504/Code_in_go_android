package com.simats.codeingo.ui.auth

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.KeyboardType
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

@Composable
fun ProfileCreationScreen(
    onProfileCreated: (name: String, email: String) -> Unit,
    onLoginClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableStateOf(1) } // 1: Age, 2: Account details
    var age by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var toastMessage by remember { mutableStateOf<String?>(null) }
    var isSuccessToast by remember { mutableStateOf(false) }

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
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (step == 2) step = 1 else onDismiss()
                    }
                ) {
                    Icon(
                        imageVector = if (step == 2) Icons.AutoMirrored.Filled.ArrowBack else Icons.Default.Close,
                        contentDescription = "Back",
                        tint = SubtextGray
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "LOG IN",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = DuolingoBlue,
                    modifier = Modifier
                        .clickable { onLoginClick() }
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (step == 1) {
                // Step 1: Age
                Text(
                    text = "How old are you?",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Providing your age ensures you get the right learning experience.",
                    fontSize = 14.sp,
                    color = SubtextGray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(36.dp))

                CustomTextField(
                    value = age,
                    onValueChange = { age = it.filter { ch -> ch.isDigit() } },
                    placeholder = "Age",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(36.dp))

                DuolingoButton(
                    text = "CONTINUE",
                    faceColor = DuolingoGreen,
                    shadowColor = DuolingoGreenDark,
                    onClick = {
                        if (age.isNotEmpty() && (age.toIntOrNull() ?: 0) > 0) {
                            step = 2
                        } else {
                            toastMessage = "Please enter a valid age."
                            isSuccessToast = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                // Step 2: Name, Email, Password
                Text(
                    text = "Create your profile",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(24.dp))

                CustomTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Name (optional)",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                CustomTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Email",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                CustomSecureField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Password",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(28.dp))

                DuolingoButton(
                    text = "CREATE ACCOUNT",
                    faceColor = DuolingoGreen,
                    shadowColor = DuolingoGreenDark,
                    onClick = {
                        if (email.contains("@") && password.length >= 6) {
                            isSuccessToast = true
                            toastMessage = "Account created!"
                            onProfileCreated(if (name.isNotEmpty()) name else "User", email)
                        } else {
                            isSuccessToast = false
                            toastMessage = "Please provide a valid email and 6+ char password."
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Social Logins
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f).height(1.dp).background(InputBorder))
                    Text(
                        text = "OR",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SubtextGray,
                        modifier = Modifier.padding(horizontal = 14.dp)
                    )
                    Box(modifier = Modifier.weight(1f).height(1.dp).background(InputBorder))
                }

                Spacer(modifier = Modifier.height(20.dp))

                val socialShape = RoundedCornerShape(16.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(socialShape)
                        .background(CardBackground)
                        .border(1.5.dp, InputBorder, socialShape)
                        .clickable {
                            onProfileCreated("Google User", "user@gmail.com")
                        }
                        .padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GoogleLogoView(size = 20.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "SIGN UP WITH GOOGLE",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(socialShape)
                        .background(CardBackground)
                        .border(1.5.dp, InputBorder, socialShape)
                        .clickable {
                            onProfileCreated("Facebook User", "user@facebook.com")
                        }
                        .padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FacebookLogoView(size = 20.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "SIGN UP WITH FACEBOOK",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }

        ToastBanner(
            message = toastMessage,
            isSuccess = isSuccessToast,
            onDismiss = { toastMessage = null }
        )
    }
}
