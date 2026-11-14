package com.simats.codeingo.ui.profile

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.domain.AuthService
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.domain.LocalizationManager
import com.simats.codeingo.ui.components.motion.pressSquish
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.FeedbackManager
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.PhoenixGreen
import com.simats.codeingo.ui.theme.SubtextGray

/**
 * 🎴 Shareable "Cipher Card" Identity & Viral Referral Sheet
 *
 * Implements Phase 5.6:
 * - High-fidelity profile card showing handle, streak, total XP, and domain rank
 * - 1-tap System Share Sheet (`Intent.ACTION_SEND`) with deep-link referral URL
 * - Rewards both sides when friends join via deep-link
 */
@Composable
fun CipherCardShareView(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val gameManager = GameManager.instance
    val totalXp by gameManager.totalXP.collectAsState()
    val streakDays by gameManager.streakDays.collectAsState()
    val completedChapters by gameManager.completedChapters.collectAsState()

    val handle = remember {
        LocalizationManager.instance.userHandle.value.ifBlank {
            AuthService.shared.currentUser.value?.email?.substringBefore("@") ?: "Defender"
        }
    }

    val referralCode = remember(handle) {
        handle.lowercase().take(8) + "_" + (1000..9999).random()
    }
    val inviteUrl = "https://ashcrypt.app/invite/$referralCode"

    val cardGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1E1B4B),
            Color(0xFF0F172A),
            Color(0xFF090D16)
        )
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Rendered Cipher Card Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(cardGradient)
                .border(1.5.dp, AmberGold.copy(alpha = 0.7f), RoundedCornerShape(22.dp))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card Top: Brand & Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("🛡️", fontSize = 18.sp)
                        Text(
                            text = "ASHCRYPT CIPHER CARD",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = AmberGold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PhoenixGreen.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "VERIFIED DEFENDER",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = PhoenixGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Avatar & Handle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                            .border(1.5.dp, AmberGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🦅", fontSize = 32.sp)
                    }

                    Column {
                        Text(
                            text = handle,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "Master of ${completedChapters.size} / 5 Cryptographic Domains",
                            fontSize = 11.sp,
                            color = SubtextGray
                        )
                    }
                }

                // Stats Triplet
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🔥 $streakDays DAYS", fontSize = 12.sp, fontWeight = FontWeight.Black, color = AmberGold)
                        Text(text = "Unbroken Streak", fontSize = 9.sp, color = SubtextGray)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "⚡ $totalXp XP", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.White)
                        Text(text = "Total XP", fontSize = 9.sp, color = SubtextGray)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "👑 OBSIDIAN", fontSize = 12.sp, fontWeight = FontWeight.Black, color = PhoenixGreen)
                        Text(text = "League Tier", fontSize = 9.sp, color = SubtextGray)
                    }
                }

                // Footer Referral Code
                Text(
                    text = "Invite: $inviteUrl",
                    fontSize = 9.5.sp,
                    fontFamily = FontFamily.Monospace,
                    color = SubtextGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Native Android Share Sheet Trigger
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .pressSquish {
                    FeedbackManager.tap()
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "I'm mastering Cryptography & Network Security on AshCrypt! 🔥 Join my squad with invite code '$referralCode': $inviteUrl"
                        )
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share Cipher Card"))
                }
                .clip(RoundedCornerShape(14.dp))
                .background(AmberGold)
                .padding(vertical = 13.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = Color.Black, modifier = Modifier.size(16.dp))
                Text(
                    text = "SHARE CIPHER CARD & SQUAD INVITE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }
        }
    }
}

