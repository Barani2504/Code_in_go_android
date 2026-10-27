package com.simats.codeingo.ui.arenas.key_exchange_citadel.levels

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
import androidx.compose.material.icons.filled.Shuffle
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
fun TrapdoorLabLevel(
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

    // Unified RSA Parameters: p = 5, q = 11, n = 55, phi = 40, e = 3, d = 27
    // Stage 0: Forward Encryption (M=2 -> C = 2^3 mod 55 = 8)
    // Stage 1: Totient Trapdoor (Calculate phi(n) = (5-1)*(11-1) = 40)
    // Stage 2: Trapdoor Decryption Verification (8^27 mod 55 = 2)
    var stage by remember { mutableIntStateOf(0) }
    var selectedCiphertext by remember { mutableStateOf<Int?>(null) }
    var selectedTotient by remember { mutableStateOf<Int?>(null) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Mission Briefing & Badge
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
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "TRAPDOOR ONE-WAY LAB",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = primary
                        )
                    }

                    ArenaStatusBadge(
                        type = when (stage) {
                            2 -> ArenaBadgeType.SUCCESS
                            1 -> ArenaBadgeType.WARNING
                            else -> ArenaBadgeType.ENCRYPTED
                        },
                        label = when (stage) {
                            0 -> "STAGE 1: FORWARD"
                            1 -> "STAGE 2: TOTIENT"
                            else -> "TRAPDOOR OPEN"
                        }
                    )
                }

                Text(
                    text = when (stage) {
                        0 -> "Trapdoor functions are easy to compute in the forward direction (C ≡ Mᵉ mod n), but intractable to invert without the mathematical secret trapdoor."
                        1 -> "Adversary dilemma: Inverting C without factors requires trial division (O(√n)). But you know p=5 and q=11! Compute Euler's Totient φ(n)."
                        else -> "🎉 Trapdoor Activated! Knowing φ(n)=40 grants inverse d ≡ 3⁻¹ mod 40 = 27, inverting C=8 back to M=2 instantly!"
                    },
                    fontSize = 12.sp,
                    color = dynamicColors.textPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }

        // Forward vs Reverse Interactive Comparison Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(if (isDark) ArenaPalettes.Arena3BackgroundDark else Color(0xFFF8FAFC))
                .border(1.5.dp, if (stage >= 2) ArenaSuccessGreen else primary.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Forward Lane
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "FORWARD DIRECTION (ENCRYPT)", fontSize = 10.sp, fontWeight = FontWeight.Black, color = ArenaSuccessGreen)
                        Text(
                            text = if (stage >= 1) "C = 2³ mod 55 = 8" else "C = Mᵉ mod n (M=2, e=3, n=55)",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = dynamicColors.textPrimary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ArenaSuccessGreen.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = "EASY: O(log e)", fontSize = 9.5.sp, fontWeight = FontWeight.Black, color = ArenaSuccessGreen)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(SubtextGray.copy(alpha = 0.25f))
                )

                // Reverse Lane
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "REVERSE DIRECTION (DECRYPT)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = if (stage >= 2) ArenaSuccessGreen else ArenaDangerRed
                        )
                        Text(
                            text = if (stage >= 2) "d = 27 ➔ 8²⁷ mod 55 = 2" else "Adversary: Factoring n=55 (O(√n))",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = dynamicColors.textPrimary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (stage >= 2) ArenaSuccessGreen.copy(alpha = 0.2f)
                                else ArenaDangerRed.copy(alpha = 0.2f)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (stage >= 2) "TRAPDOOR UNLOCKED" else "HARD: O(√n)",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Black,
                            color = if (stage >= 2) ArenaSuccessGreen else ArenaDangerRed
                        )
                    }
                }
            }
        }

        // Interactive Stage Controls
        when (stage) {
            0 -> {
                // Forward encrypt choice
                Column(
                    modifier = Modifier.fillMaxWidth(0.95f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "COMPUTE CIPHERTEXT C = 2³ mod 55:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = accent
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(6, 8, 16, 24).forEach { candidate ->
                            val isSelected = selectedCiphertext == candidate
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .minTouchTarget48()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) {
                                            if (candidate == 8) ArenaSuccessGreen else ArenaDangerRed
                                        } else if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
                                    )
                                    .border(1.dp, if (isSelected) Color.White else Color.Transparent, RoundedCornerShape(12.dp))
                                    .pressScale()
                                    .clickable {
                                        selectedCiphertext = candidate
                                        if (candidate == 8) {
                                            ArenaHaptics.success(view, reduceMotion)
                                            stage = 1
                                            feedbackMessage = "✓ C = 2³ mod 55 = 8! Easy forward encryption complete."
                                            onActionSuccess()
                                        } else {
                                            ArenaHaptics.error(view, reduceMotion)
                                            onDeductHeart()
                                            feedbackMessage = "❌ 2³ = 8, and 8 mod 55 = 8."
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "C = $candidate",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (isSelected && candidate == 8) Color.Black else dynamicColors.textPrimary
                                )
                            }
                        }
                    }
                }
            }
            1 -> {
                // Totient calculation choice
                Column(
                    modifier = Modifier.fillMaxWidth(0.95f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "SOLVE φ(55) = (p - 1)(q - 1) = (5 - 1)(11 - 1):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = accent
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(36, 40, 50, 54).forEach { candidate ->
                            val isSelected = selectedTotient == candidate
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .minTouchTarget48()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) {
                                            if (candidate == 40) ArenaSuccessGreen else ArenaDangerRed
                                        } else if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
                                    )
                                    .border(1.dp, if (isSelected) Color.White else Color.Transparent, RoundedCornerShape(12.dp))
                                    .pressScale()
                                    .clickable {
                                        selectedTotient = candidate
                                        if (candidate == 40) {
                                            ArenaHaptics.criticalStrike(view, reduceMotion)
                                            stage = 2
                                            feedbackMessage = "🎉 Correct! φ(55) = 4 × 10 = 40! Trapdoor inverse d=27 unlocked."
                                            onActionSuccess()
                                        } else {
                                            ArenaHaptics.error(view, reduceMotion)
                                            onDeductHeart()
                                            feedbackMessage = "❌ Compute (5-1) × (11-1) = 4 × 10 = 40."
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "φ = $candidate",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (isSelected && candidate == 40) Color.Black else dynamicColors.textPrimary
                                )
                            }
                        }
                    }
                }
            }
            else -> {
                // Stage 2: Advance
                Tactile3DButton(
                    title = "TRAPDOOR MASTERED! ADVANCE CITADEL ➔",
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

        if (feedbackMessage != null) {
            Text(
                text = feedbackMessage!!,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (feedbackMessage!!.startsWith("✓") || feedbackMessage!!.startsWith("🎉")) ArenaSuccessGreen else ArenaDangerRed,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}
