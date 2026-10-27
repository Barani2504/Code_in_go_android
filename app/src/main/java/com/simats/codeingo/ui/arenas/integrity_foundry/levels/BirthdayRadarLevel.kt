package com.simats.codeingo.ui.arenas.integrity_foundry.levels

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
fun BirthdayRadarLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    val reduceMotion = LocalReduceMotion.current
    val view = LocalView.current

    val primary = ArenaPalettes.Arena4Primary
    val accent = ArenaPalettes.Arena4Accent

    // Birthday Bound Radar: O(2^(n/2)) collision search complexity
    val bitLengths = listOf(16, 32, 64, 128, 256)
    var selectedBitIndex by remember { mutableIntStateOf(3) } // 128b default
    val currentBits = bitLengths[selectedBitIndex]
    val collisionExponent = currentBits / 2

    // Interactive Challenge
    var challengeCompleted by remember { mutableStateOf(false) }
    var selectedChallengeOption by remember { mutableStateOf<String?>(null) }
    var feedbackText by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Mission Briefing & Status Badge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(if (isDark) ArenaPalettes.Arena4CardDark else dynamicColors.cardBackground)
                .border(1.2.dp, primary.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "BIRTHDAY BOUND COLLISION RADAR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = primary
                    )

                    ArenaStatusBadge(
                        type = if (challengeCompleted) ArenaBadgeType.SUCCESS else ArenaBadgeType.WARNING,
                        label = "RADAR: O(2^(n/2))"
                    )
                }

                Text(
                    text = "Square-Root Collision Search Complexity: By the Birthday Paradox, finding ANY collision H(m₁) = H(m₂) requires only ~√(2ⁿ) = 2^(n/2) hash computations with 50% probability, halving nominal security!",
                    fontSize = 12.sp,
                    color = dynamicColors.textPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }

        // Bit Length Tabs (Enforced 48dp+ height)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth(0.95f)) {
            Text(
                text = "EXPLORE HASH DIGEST SIZES (n BITS):",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = SubtextGray
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                bitLengths.forEachIndexed { idx, bits ->
                    val isSelected = selectedBitIndex == idx
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .minTouchTarget48()
                            .height(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) primary.copy(alpha = 0.25f)
                                else if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
                            )
                            .border(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) primary else Color.Transparent,
                                RoundedCornerShape(10.dp)
                            )
                            .pressScale()
                            .clickable {
                                selectedBitIndex = idx
                                ArenaHaptics.tap(view, reduceMotion)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${bits}b",
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (isSelected) primary else dynamicColors.textPrimary
                        )
                    }
                }
            }
        }

        // Radar Computation Card
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(14.dp))
                .background(if (isDark) ArenaPalettes.Arena4BackgroundDark else Color(0xFFF8FAFC))
                .border(1.5.dp, primary.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "NOMINAL PREIMAGE RESISTANCE:", fontSize = 9.sp, fontWeight = FontWeight.Black, color = SubtextGray)
                        Text(text = "O(2^$currentBits) operations", fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = dynamicColors.textPrimary)
                    }
                    Text(text = "2^$currentBits", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = SubtextGray)
                }

                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(SubtextGray.copy(alpha = 0.2f)))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "BIRTHDAY COLLISION RESISTANCE:", fontSize = 9.sp, fontWeight = FontWeight.Black, color = accent)
                        Text(text = "O(2^$collisionExponent) operations (~50% chance)", fontSize = 13.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = accent)
                    }
                    Text(text = "2^($currentBits/2)", fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = accent)
                }
            }
        }

        // Interactive Collision Challenge
        if (!challengeCompleted) {
            Column(modifier = Modifier.fillMaxWidth(0.95f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "TO ACHIEVE 128 BITS OF COLLISION RESISTANCE, WHAT MUST n BE?",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                listOf(
                    "256 bits (since 2^(256/2) = 2^128)" to true,
                    "128 bits (gives only 2^64 collision resistance!)" to false,
                    "64 bits" to false
                ).forEach { (opt, isCorrect) ->
                    val isSelected = selectedChallengeOption == opt
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .minTouchTarget48()
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) (if (isCorrect) ArenaSuccessGreen else ArenaDangerRed)
                                else if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
                            )
                            .border(1.dp, if (isSelected) Color.White else Color.Transparent, RoundedCornerShape(12.dp))
                            .pressScale()
                            .clickable {
                                selectedChallengeOption = opt
                                if (isCorrect) {
                                    ArenaHaptics.criticalStrike(view, reduceMotion)
                                    challengeCompleted = true
                                    feedbackText = "🎉 Correct! SHA-256's 256-bit digest provides exactly 128-bit collision resistance."
                                    onActionSuccess()
                                } else {
                                    ArenaHaptics.error(view, reduceMotion)
                                    onDeductHeart()
                                    feedbackText = "❌ Remember the square-root bound: n-bit hash offers n/2 bits of collision resistance."
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = opt,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected && isCorrect) Color.Black else dynamicColors.textPrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )
                    }
                }
            }
        } else {
            Tactile3DButton(
                title = "PROCEED TO COLLISION BEAST CHAMBER ➔",
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
