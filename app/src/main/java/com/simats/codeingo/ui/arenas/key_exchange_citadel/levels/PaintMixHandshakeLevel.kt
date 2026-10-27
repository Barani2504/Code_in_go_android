package com.simats.codeingo.ui.arenas.key_exchange_citadel.levels

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.arenas.common.ArenaBadgeType
import com.simats.codeingo.ui.arenas.common.ArenaHaptics
import com.simats.codeingo.ui.arenas.common.ArenaPalettes
import com.simats.codeingo.ui.arenas.common.ArenaStatusBadge
import com.simats.codeingo.ui.arenas.common.minTouchTarget48
import com.simats.codeingo.ui.components.Tactile3DButton
import com.simats.codeingo.ui.components.Tactile3DButtonColor
import com.simats.codeingo.ui.theme.ArenaDangerRed
import com.simats.codeingo.ui.theme.ArenaSuccessGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.LocalReduceMotion
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
    val reduceMotion = LocalReduceMotion.current
    val view = LocalView.current

    val primary = ArenaPalettes.Arena3Primary
    val accent = ArenaPalettes.Arena3Accent

    // Stage 0: Alice selects private secret a (Target: a=6)
    // Stage 1: Bob selects private secret b (Target: b=15)
    // Stage 2: Wire transmission of public shares A=8, B=19
    // Stage 3: Shared key K computation test (User must calculate / select K=2)
    // Stage 4: Key agreement verified!
    var stage by remember { mutableIntStateOf(0) }
    var selectedAliceSecret by remember { mutableStateOf<Int?>(null) }
    var selectedBobSecret by remember { mutableStateOf<Int?>(null) }
    var selectedSharedKey by remember { mutableStateOf<Int?>(null) }
    var feedbackText by remember { mutableStateOf<String?>(null) }

    // Colors
    val baseBlue = Color(0xFF2563EB)
    val aliceYellow = Color(0xFFFACC15)
    val bobRed = Color(0xFFEF4444)
    val alicePublicGreen = Color(0xFF10B981)
    val bobPublicPurple = Color(0xFF8B5CF6)
    val sharedSecretAmber = Color(0xFFD97706)

    val currentAliceFlaskColor by animateColorAsState(
        targetValue = when {
            stage >= 4 -> sharedSecretAmber
            stage >= 2 -> alicePublicGreen
            stage >= 1 -> aliceYellow
            else -> baseBlue
        },
        animationSpec = tween(if (reduceMotion) 100 else 400),
        label = "aliceColor"
    )

    val currentBobFlaskColor by animateColorAsState(
        targetValue = when {
            stage >= 4 -> sharedSecretAmber
            stage >= 2 -> bobPublicPurple
            stage >= 2 -> bobRed
            else -> baseBlue
        },
        animationSpec = tween(if (reduceMotion) 100 else 400),
        label = "bobColor"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Mission Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(if (isDark) ArenaPalettes.Arena3CardDark else dynamicColors.cardBackground)
                .border(1.2.dp, primary.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "DIFFIE-HELLMAN COLOR MIXER",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = primary
                        )
                    }

                    ArenaStatusBadge(
                        type = when (stage) {
                            4 -> ArenaBadgeType.SUCCESS
                            2, 3 -> ArenaBadgeType.WARNING
                            else -> ArenaBadgeType.ENCRYPTED
                        },
                        label = when (stage) {
                            0 -> "PHASE 1: ALICE"
                            1 -> "PHASE 2: BOB"
                            2 -> "PHASE 3: WIRE"
                            3 -> "PHASE 4: DERIVE K"
                            else -> "KEY ESTABLISHED"
                        }
                    )
                }

                Text(
                    text = when (stage) {
                        0 -> "Step 1: Alice & Bob agree on Public Base (Blue, g=5, p=23). Alice must inject her private secret exponent into the flask."
                        1 -> "Step 2: Now Bob injects his private secret exponent into his Blue base flask to compute his public mix."
                        2 -> "Step 3: Transmit Public Mixes (A=8, B=19) across the wire. Eavesdropper Eve intercepts them, but cannot extract the secrets!"
                        3 -> "Step 4: Cryptographic Synthesis! Alice computes K = B^a mod p = 19^6 mod 23. What is the negotiated Shared Secret K?"
                        else -> "🎉 Perfect Key Agreement! Both arrived at the identical Shared Secret (K=2) without exposing private exponents!"
                    },
                    fontSize = 12.sp,
                    color = dynamicColors.textPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
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
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = "👩 ALICE", fontSize = 12.sp, fontWeight = FontWeight.Black, color = dynamicColors.textPrimary)
                Text(
                    text = if (stage >= 1) "Secret: a=6" else "Secret: [Choose]",
                    fontSize = 10.sp,
                    color = aliceYellow,
                    fontWeight = FontWeight.Bold
                )

                FlaskView(
                    color = currentAliceFlaskColor,
                    label = when {
                        stage >= 4 -> "Shared Key K"
                        stage >= 2 -> "A = 5⁶ mod 23 = 8"
                        stage >= 1 -> "Secret Injected"
                        else -> "Base (Blue)"
                    }
                )
            }

            // Insecure Public Network Wire (Middle)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
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
                        .size(72.dp, 44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (stage >= 2) ArenaDangerRed.copy(alpha = 0.15f) else Color.Transparent)
                        .border(1.dp, if (stage >= 2) ArenaDangerRed else SubtextGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (stage >= 2) "EVE 🕵️\nSEES A & B" else "WIRE 🌐\nIDLE",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = if (stage >= 2) ArenaDangerRed else SubtextGray
                    )
                }

                Text(
                    text = "g = 5, p = 23",
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    color = accent
                )
            }

            // Bob Column
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = "👨 BOB", fontSize = 12.sp, fontWeight = FontWeight.Black, color = dynamicColors.textPrimary)
                Text(
                    text = if (stage >= 2) "Secret: b=15" else "Secret: [Choose]",
                    fontSize = 10.sp,
                    color = bobRed,
                    fontWeight = FontWeight.Bold
                )

                FlaskView(
                    color = currentBobFlaskColor,
                    label = when {
                        stage >= 4 -> "Shared Key K"
                        stage >= 2 -> "B = 5¹⁵ mod 23 = 19"
                        stage >= 2 -> "Secret Injected"
                        else -> "Base (Blue)"
                    }
                )
            }
        }

        // Interactive Decision Section
        when (stage) {
            0 -> {
                // Alice chooses secret a
                Column(
                    modifier = Modifier.fillMaxWidth(0.95f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "SELECT ALICE'S PRIVATE EXPONENT a:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = accent
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(6 to "a = 6 (Target)", 9 to "a = 9", 14 to "a = 14").forEach { (candidate, label) ->
                            val isSelected = selectedAliceSecret == candidate
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .minTouchTarget48()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) primary.copy(alpha = 0.3f)
                                        else if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
                                    )
                                    .border(1.dp, if (isSelected) accent else Color.Transparent, RoundedCornerShape(12.dp))
                                    .pressScale()
                                    .clickable {
                                        selectedAliceSecret = candidate
                                        if (candidate == 6) {
                                            ArenaHaptics.success(view, reduceMotion)
                                            stage = 1
                                            feedbackText = "✓ Alice selected a = 6! Next: Bob's secret."
                                            onActionSuccess()
                                        } else {
                                            ArenaHaptics.error(view, reduceMotion)
                                            onDeductHeart()
                                            feedbackText = "❌ Choose target exponent a = 6 for the textbook protocol."
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) accent else dynamicColors.textPrimary
                                )
                            }
                        }
                    }
                }
            }
            1 -> {
                // Bob chooses secret b
                Column(
                    modifier = Modifier.fillMaxWidth(0.95f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "SELECT BOB'S PRIVATE EXPONENT b:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = accent
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(15 to "b = 15 (Target)", 4 to "b = 4", 18 to "b = 18").forEach { (candidate, label) ->
                            val isSelected = selectedBobSecret == candidate
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .minTouchTarget48()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) primary.copy(alpha = 0.3f)
                                        else if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
                                    )
                                    .border(1.dp, if (isSelected) accent else Color.Transparent, RoundedCornerShape(12.dp))
                                    .pressScale()
                                    .clickable {
                                        selectedBobSecret = candidate
                                        if (candidate == 15) {
                                            ArenaHaptics.success(view, reduceMotion)
                                            stage = 2
                                            feedbackText = "✓ Bob selected b = 15! Public shares synthesized."
                                            onActionSuccess()
                                        } else {
                                            ArenaHaptics.error(view, reduceMotion)
                                            onDeductHeart()
                                            feedbackText = "❌ Choose target exponent b = 15 for Bob."
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) accent else dynamicColors.textPrimary
                                )
                            }
                        }
                    }
                }
            }
            2 -> {
                // Wire Transmission Action
                Tactile3DButton(
                    title = "TRANSMIT PUBLIC SHARES OVER WIRE ➔",
                    style = Tactile3DButtonColor.AMBER,
                    onClick = {
                        ArenaHaptics.success(view, reduceMotion)
                        stage = 3
                        feedbackText = "Shares exchanged: Alice received B=19, Bob received A=8."
                        onActionSuccess()
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .minTouchTarget48()
                )
            }
            3 -> {
                // Calculate K
                Column(
                    modifier = Modifier.fillMaxWidth(0.95f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "SOLVE SHARED KEY K = 19⁶ mod 23 = 8¹⁵ mod 23:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = accent
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(2, 7, 14, 21).forEach { candidate ->
                            val isSelected = selectedSharedKey == candidate
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .minTouchTarget48()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) {
                                            if (candidate == 2) ArenaSuccessGreen else ArenaDangerRed
                                        } else if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
                                    )
                                    .border(1.dp, if (isSelected) Color.White else Color.Transparent, RoundedCornerShape(12.dp))
                                    .pressScale()
                                    .clickable {
                                        selectedSharedKey = candidate
                                        if (candidate == 2) {
                                            ArenaHaptics.criticalStrike(view, reduceMotion)
                                            stage = 4
                                            feedbackText = "🎉 Correct! K = 19⁶ mod 23 = 2! Key agreement verified."
                                            onActionSuccess()
                                        } else {
                                            ArenaHaptics.error(view, reduceMotion)
                                            onDeductHeart()
                                            feedbackText = "❌ Incorrect residue. Remember 19² = 361 ≡ 16 mod 23..."
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "K = $candidate",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (isSelected && candidate == 2) Color.Black else dynamicColors.textPrimary
                                )
                            }
                        }
                    }
                }
            }
            else -> {
                // Stage 4: Advance
                Tactile3DButton(
                    title = "KEY ESTABLISHED (K = 2)! ADVANCE CITADEL ➔",
                    style = Tactile3DButtonColor.GREEN,
                    onClick = {
                        ArenaHaptics.success(view, reduceMotion)
                        onComplete()
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .minTouchTarget48()
                )
            }
        }

        if (feedbackText != null) {
            Text(
                text = feedbackText!!,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (feedbackText!!.startsWith("✓") || feedbackText!!.startsWith("🎉")) ArenaSuccessGreen else ArenaDangerRed,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
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
