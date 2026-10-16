package com.simats.codeingo.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.simats.codeingo.ui.components.GoogleLogoView
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.pressScale
import kotlinx.coroutines.delay

data class GoogleAccountItem(
    val name: String,
    val email: String,
    val avatarInitial: String,
    val avatarBgColor: Color
)

/**
 * 🌐 Enhanced Google Account Selection Sheet
 * Clean Material 3 Google Identity styling:
 * - Official Google G-logo header
 * - Responsive Light & Dark Mode card surfaces
 * - Account cards with vibrant letter avatars & check indicators
 * - "Use another account" option
 */
@Composable
fun GoogleAuthSheet(
    onSelectAccount: (name: String, email: String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isAuthenticating by remember { mutableStateOf(false) }
    var selectedName by remember { mutableStateOf("") }
    var selectedEmail by remember { mutableStateOf("") }
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val savedAccounts by com.simats.codeingo.domain.AuthService.shared.savedGoogleAccounts.collectAsState()

    val displayAccounts = if (savedAccounts.isNotEmpty()) {
        savedAccounts.map { acc ->
            GoogleAccountItem(
                name = acc.name,
                email = acc.email,
                avatarInitial = acc.avatarInitial,
                avatarBgColor = when (acc.colorIndex % 3) {
                    0 -> Color(0xFF4285F4)
                    1 -> Color(0xFF34A853)
                    else -> Color(0xFFEA4335)
                }
            )
        }
    } else {
        listOf(
            GoogleAccountItem(
                name = "Lokesh Kumar",
                email = "lokeshkumar1232005@gmail.com",
                avatarInitial = "L",
                avatarBgColor = Color(0xFF4285F4)
            ),
            GoogleAccountItem(
                name = "Lokesh Developer",
                email = "lokesh.developer@gmail.com",
                avatarInitial = "L",
                avatarBgColor = Color(0xFF34A853)
            )
        )
    }

    LaunchedEffect(isAuthenticating) {
        if (isAuthenticating) {
            delay(750)
            com.simats.codeingo.domain.AuthService.shared.signInWithGoogle(selectedName, selectedEmail)
            onSelectAccount(selectedName, selectedEmail)
            onDismiss()
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(if (isDark) Color(0xFF0F172A) else Color.White)
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Drag handle
            Box(
                modifier = Modifier
                    .width(36.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1))
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Header Row: Security pill + Dismiss
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                        .border(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("🔒", fontSize = 11.sp)
                    Text(
                        text = "accounts.google.com",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF34A853)
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = dynamicColors.textSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Google Logo Header
            GoogleLogoView(size = 36.dp)

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Choose an account",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = dynamicColors.textPrimary
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "to continue to AshCrypt",
                fontSize = 13.sp,
                color = dynamicColors.textSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (isAuthenticating) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    CircularProgressIndicator(
                        strokeWidth = 3.dp,
                        color = Color(0xFF4285F4),
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = "Signing in as $selectedName...",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = dynamicColors.textPrimary
                    )
                }
            } else {
                // Account list
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    displayAccounts.forEach { acc ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .pressScale(0.97f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isDark) Color(0xFF1E293B).copy(alpha = 0.7f) else Color(0xFFF8FAFC))
                                .border(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                                .clickable {
                                    selectedName = acc.name
                                    selectedEmail = acc.email
                                    isAuthenticating = true
                                }
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(acc.avatarBgColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = acc.avatarInitial,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = acc.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = dynamicColors.textPrimary
                                )
                                Text(
                                    text = acc.email,
                                    fontSize = 12.sp,
                                    color = dynamicColors.textSecondary
                                )
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = dynamicColors.textSecondary.copy(alpha = 0.6f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Use another account
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pressScale(0.97f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isDark) Color(0xFF1E293B).copy(alpha = 0.4f) else Color.Transparent)
                            .border(1.dp, if (isDark) Color(0xFF334155).copy(alpha = 0.5f) else Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                            .clickable {
                                selectedName = "Developer Agent"
                                selectedEmail = "agent@ashcrypt.io"
                                isAuthenticating = true
                            }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = null,
                                tint = dynamicColors.textPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Text(
                            text = "Use another account",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = dynamicColors.textPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "To continue, Google will share your name, email address, and profile picture with AshCrypt. Review AshCrypt's Privacy Policy and Terms of Service.",
                fontSize = 10.5.sp,
                color = dynamicColors.textSecondary.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 15.sp
            )
        }
    }
}
