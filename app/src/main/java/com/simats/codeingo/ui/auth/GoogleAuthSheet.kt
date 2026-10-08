package com.simats.codeingo.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.PersonAdd
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.components.AppCard
import com.simats.codeingo.ui.components.GoogleLogoView
import com.simats.codeingo.ui.phoenix.PhoenixAtmosphericBackgroundView
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.pressScale
import com.simats.codeingo.ui.theme.staggeredAppear
import kotlinx.coroutines.delay

// ══════════════════════════════════════════════════════════════════
// 🌐 GoogleAuthSheet — Fluid Google Account Selection Sheet
// ══════════════════════════════════════════════════════════════════

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
    val isDark = LocalDynamicThemeColors.current.isDark

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
            delay(1000)
            onSelectAccount(selectedName, selectedEmail)
            onDismiss()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        PhoenixAtmosphericBackgroundView()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = if (isDark) Color.White.copy(alpha = 0.85f) else Color(0xFF12_18_26)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "SIGN IN WITH GOOGLE",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif,
                    color = if (isDark) Color.White else Color(0xFF12_18_26),
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Google Logo Header Card
            AppCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .staggeredAppear(0),
                cornerRadius = 24.dp,
                accentGlow = AmberGold.copy(alpha = 0.15f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.10f)),
                        contentAlignment = Alignment.Center
                    ) {
                        GoogleLogoView(size = 32.dp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Choose an account",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif,
                        color = if (isDark) Color.White else Color(0xFF12_18_26)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "to continue to Ashnode DSA",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.SansSerif,
                        color = if (isDark) Color.White.copy(alpha = 0.60f) else Color(0xFF64_74_8B)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    if (isAuthenticating) {
                        CircularProgressIndicator(color = AmberGold, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Signing in as $selectedName...",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberGold
                        )
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            defaultAccounts.forEachIndexed { index, account ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .pressScale(targetScale = 0.96f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            if (isDark) Color(0xFF14_1F_36).copy(alpha = 0.70f)
                                            else Color(0xFFF1_F4_FA)
                                        )
                                        .border(
                                            1.dp,
                                            if (isDark) Color.White.copy(alpha = 0.10f) else Color(0xFFD7_DE_EB),
                                            RoundedCornerShape(16.dp)
                                        )
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            selectedName = account.name
                                            selectedEmail = account.email
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
                                            .background(account.avatarBgColor),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = account.avatarInitial,
                                            color = Color.White,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 16.sp
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = account.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            fontFamily = FontFamily.SansSerif,
                                            color = if (isDark) Color.White else Color(0xFF12_18_26)
                                        )
                                        Text(
                                            text = account.email,
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.SansSerif,
                                            color = if (isDark) Color.White.copy(alpha = 0.60f) else Color(0xFF64_74_8B)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
