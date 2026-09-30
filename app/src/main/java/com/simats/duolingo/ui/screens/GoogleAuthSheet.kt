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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.duolingo.ui.components.DuoSecureField
import com.simats.duolingo.ui.components.DuoTextField
import com.simats.duolingo.ui.components.DuolingoButton
import com.simats.duolingo.ui.components.GoogleLogoView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class GoogleAccountItem(
    val name: String,
    val email: String,
    val avatarInitial: String,
    val avatarBgColor: Color
)

@Composable
fun GoogleAuthSheet(
    onDismiss: () -> Unit,
    onSelectAccount: (String, String) -> Unit,
) {
    var isAuthenticating by remember { mutableStateOf(false) }
    var selectedName by remember { mutableStateOf("") }
    var isCustomAccountMode by remember { mutableStateOf(false) }
    var customEmail by remember { mutableStateOf("vishal.rao@gmail.com") }
    var customPassword by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    val accounts = listOf(
        GoogleAccountItem("Vishal Rao", "vishal.rao@gmail.com", "V", Color(165, 96, 232)),
        GoogleAccountItem("Sail User", "sail.dev@gmail.com", "S", Color(28, 176, 246))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Simulated accounts.google.com URL Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Text("✕", color = Color.Gray, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(241, 243, 244))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("🔒", fontSize = 11.sp)
                    Text("accounts.google.com", color = Color(95, 99, 104), fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.size(32.dp))
            }

            HorizontalDivider(color = Color(220, 220, 220))

            if (isAuthenticating) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = Color(66, 133, 244), modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Signing in as $selectedName...",
                        color = Color(32, 33, 36),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else if (!isCustomAccountMode) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    GoogleLogoView(size = 32.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Choose an account",
                        color = Color(32, 33, 36),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Text(
                        text = "to continue to Code in Go",
                        color = Color(95, 99, 104),
                        fontSize = 15.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Account List
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(218, 220, 224), RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        accounts.forEachIndexed { index, account ->
                            if (index > 0) {
                                HorizontalDivider(color = Color(240, 240, 240))
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedName = account.name
                                        isAuthenticating = true
                                        coroutineScope.launch {
                                            delay(1000)
                                            onSelectAccount(account.name, account.email)
                                            onDismiss()
                                        }
                                    }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(account.avatarBgColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = account.avatarInitial,
                                        color = Color.White,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Column {
                                    Text(
                                        text = account.name,
                                        color = Color(32, 33, 36),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = account.email,
                                        color = Color(95, 99, 104),
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = Color(240, 240, 240))

                        // Use another account row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isCustomAccountMode = true }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(241, 243, 244)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("👤", fontSize = 16.sp)
                            }
                            Text(
                                text = "Use another account",
                                color = Color(32, 33, 36),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "To continue, Google will share your name, email address, language preference, and profile picture with Code in Go.",
                        color = Color(95, 99, 104),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            } else {
                // Custom account sign in
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    GoogleLogoView(size = 32.dp)
                    Text(
                        text = "Sign in",
                        color = Color(32, 33, 36),
                        fontSize = 24.sp
                    )
                    Text(
                        text = "with your Google Account",
                        color = Color(95, 99, 104),
                        fontSize = 15.sp
                    )

                    OutlinedTextField(
                        value = customEmail,
                        onValueChange = { customEmail = it },
                        label = { Text("Email or phone") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = customPassword,
                        onValueChange = { customPassword = it },
                        label = { Text("Enter your password") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = { isCustomAccountMode = false }) {
                            Text("Back", color = Color(26, 115, 232))
                        }
                        Button(
                            onClick = {
                                val namePart = customEmail.substringBefore("@").replace(".", " ").capitalize()
                                selectedName = namePart
                                isAuthenticating = true
                                coroutineScope.launch {
                                    delay(1000)
                                    onSelectAccount(namePart, customEmail)
                                    onDismiss()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(26, 115, 232))
                        ) {
                            Text("Next")
                        }
                    }
                }
            }
        }
    }
}
