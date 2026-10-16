package com.simats.codeingo.ui.arenas.key_exchange_citadel.levels

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.theme.pressScale

@Composable
fun PaintMixHandshakeLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    // Interactive Stage: 0=Initial base, 1=Alice mixed green, 2=Bob mixed purple, 3=Exchanged over wire, 4=Shared key achieved
    var stage by remember { mutableIntStateOf(0) }

    // Colors
    val baseBlue = Color(0xFF2563EB)
    val aliceYellow = Color(0xFFFACC15)
    val bobRed = Color(0xFFEF4444)
    val alicePublicGreen = Color(0xFF10B981)
    val bobPublicPurple = Color(0xFF8B5CF6)
    val sharedSecretAmber = Color(0xFFD97706)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mission Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(if (isDark) Color(0xFF10192A) else dynamicColors.cardBackground)
                .border(1.2.dp, AmberGold.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = null,
                        tint = AmberGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "DIFFIE-HELLMAN COLOR MIXER LAB",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = AmberGold
                    )
                }
                Text(
                    text = when (stage) {
                        0 -> "Step 1: Alice & Bob agree on Public Base Paint (Blue). Alice adds her Secret Yellow (a=6) into her flask."
                        1 -> "Step 2: Bob adds his Secret Red (b=15) into his Blue base flask to produce Public Purple."
                        2 -> "Step 3: Transmit Public Green & Public Purple over the insecure wire. Eavesdropper Eve can see both, but cannot separate the colors!"
                        3 -> "Step 4: Alice mixes her Yellow into Bob's Purple, and Bob mixes his Red into Alice's Green."
                        else -> "🎉 Perfect Key Agreement! Both arrived at the identical Shared Secret (Amber Gold) without exposing private keys!"
                    },
                    fontSize = 12.5.sp,
                    color = dynamicColors.textPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp
                )
            }
        }

        // Flask Simulation Board
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Alice Column
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "👩 ALICE", fontSize = 12.sp, fontWeight = FontWeight.Black, color = dynamicColors.textPrimary)
                Text(text = "Secret: Yellow (a=6)", fontSize = 10.sp, color = aliceYellow, fontWeight = FontWeight.Bold)

                // Alice Flask
                val aliceFlaskColor = when {
                    stage >= 4 -> sharedSecretAmber
                    stage >= 1 -> alicePublicGreen
                    else -> baseBlue
                }

                FlaskView(
                    color = aliceFlaskColor,
                    label = when {
                        stage >= 4 -> "Shared Key K"
                        stage >= 1 -> "Public Mix A (Green)"
                        else -> "Base (Blue)"
                    }
                )
            }

            // Insecure Public Network Wire (Middle)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "PUBLIC WIRE",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = SubtextGray
                )

                Box(
                    modifier = Modifier
                        .size(60.dp, 38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (stage >= 3) Color(0xFFEF4444).copy(alpha = 0.15f) else Color.Transparent)
                        .border(1.dp, if (stage >= 3) Color(0xFFEF4444) else SubtextGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (stage >= 3) "EVE 🕵️\nSEEING" else "WIRE 🌐\nIDLE",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = if (stage >= 3) Color(0xFFEF4444) else SubtextGray
                    )
                }

                Text(
                    text = "g = 5, p = 23",
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    color = AmberGold
                )
            }

            // Bob Column
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "👨 BOB", fontSize = 12.sp, fontWeight = FontWeight.Black, color = dynamicColors.textPrimary)
                Text(text = "Secret: Red (b=15)", fontSize = 10.sp, color = bobRed, fontWeight = FontWeight.Bold)

                // Bob Flask
                val bobFlaskColor = when {
                    stage >= 4 -> sharedSecretAmber
                    stage >= 2 -> bobPublicPurple
                    else -> baseBlue
                }

                FlaskView(
                    color = bobFlaskColor,
                    label = when {
                        stage >= 4 -> "Shared Key K"
                        stage >= 2 -> "Public Mix B (Purple)"
                        else -> "Base (Blue)"
                    }
                )
            }
        }

        // Mathematical Equivalence Banner
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (isDark) Color(0xFF131D33) else Color(0xFFF8FAFC))
                .border(1.dp, AmberGold.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text(
                text = when (stage) {
                    0 -> "Common Base g = 5 mod 23"
                    1 -> "Alice: A = gᵃ mod p = 5⁶ mod 23 = 8 (Green)"
                    2 -> "Bob: B = gᵇ mod p = 5¹⁵ mod 23 = 19 (Purple)"
                    3 -> "Public Exchange: Wire carries A=8 & B=19"
                    else -> "K = Bᵃ mod p = Aᵇ mod p = 19⁶ mod 23 = 2 ✓"
                },
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = if (stage >= 4) DuolingoGreen else AmberGold,
                textAlign = TextAlign.Center
            )
        }

        // Action Step Button
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (stage >= 4) Brush.horizontalGradient(listOf(DuolingoGreen, Color(0xFF059669)))
                    else Brush.horizontalGradient(listOf(AmberGold, Color(0xFFD97706)))
                )
                .pressScale(targetScale = 0.96f)
                .clickable {
                    if (stage < 4) {
                        stage++
                        onActionSuccess()
                    } else {
                        onComplete()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (stage >= 4) Icons.Default.Check else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = when (stage) {
                        0 -> "MIX ALICE'S SECRET YELLOW →"
                        1 -> "MIX BOB'S SECRET RED →"
                        2 -> "TRANSMIT ACROSS WIRE →"
                        3 -> "SYNTHESIZE SHARED SECRET KEY →"
                        else -> "KEY ESTABLISHED! ADVANCE CITADEL →"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Black,
                    letterSpacing = 0.4.sp
                )
            }
        }
    }
}

@Composable
private fun FlaskView(color: Color, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(68.dp, 76.dp)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 24.dp, bottomEnd = 24.dp))
                .background(color)
                .border(2.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 24.dp, bottomEnd = 24.dp))
                .shadow(12.dp, spotColor = color),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🧪", fontSize = 24.sp)
        }
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}
