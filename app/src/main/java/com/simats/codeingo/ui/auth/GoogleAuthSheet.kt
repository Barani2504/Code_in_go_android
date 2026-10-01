package com.simats.codeingo.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.components.GoogleLogoView
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DuolingoBlue
import com.simats.codeingo.ui.theme.InputBorder
import com.simats.codeingo.ui.theme.SubtextGray
import kotlinx.coroutines.delay

data class GoogleAccountItem(
    val name: String,
    val email: String,
    val avatarInitial: String,
    val avatarBgColor: Color
)

@Composable
fun GoogleAuthSheet(
    onSelectAccount: (name: String, email: String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isAuthenticating by remember { mutableStateOf(false) }
    var selectedName by remember { mutableStateOf("") }
    var selectedEmail by remember { mutableStateOf("") }

    val defaultAccounts = listOf(
        GoogleAccountItem(
            name = "Vishal Rao",
            email = "vishal.rao@gmail.com",
            avatarInitial = "V",
            avatarBgColor = Color(0xFFA560E8)
        ),
        GoogleAccountItem(
            name = "Sail User",
            email = "sail.dev@gmail.com",
            avatarInitial = "S",
            avatarBgColor = Color(0xFF1CB0F6)
        )
    )

    LaunchedEffect(isAuthenticating) {
        if (isAuthenticating) {
            delay(1200)
            onSelectAccount(selectedName, selectedEmail)
            onDismiss()
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
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                GoogleLogoView(size = 28.dp)
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Sign in with Google",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Choose an account to continue to Code in Go",
                fontSize = 14.sp,
                color = SubtextGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (isAuthenticating) {
                Spacer(modifier = Modifier.height(40.dp))
                CircularProgressIndicator(color = DuolingoBlue, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Signing in as $selectedName...",
                    fontSize = 15.sp,
                    color = Color.White
                )
            } else {
                // List of Google Accounts
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    defaultAccounts.forEach { account ->
                        val shape = RoundedCornerShape(16.dp)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clip(shape)
                                .background(CardBackground)
                                .border(1.dp, InputBorder, shape)
                                .clickable {
                                    selectedName = account.name
                                    selectedEmail = account.email
                                    isAuthenticating = true
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(account.avatarBgColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = account.avatarInitial,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = account.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = account.email,
                                    fontSize = 13.sp,
                                    color = SubtextGray
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "To continue, Google will share your name, email address, and profile picture with Code in Go.",
                fontSize = 11.sp,
                color = SubtextGray.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)
            )
        }
    }
}
