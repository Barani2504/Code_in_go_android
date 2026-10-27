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
import androidx.compose.foundation.layout.size
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
fun SpongeChamberLevel(
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

    // Keccak-f[1600] / SHA3-256:
    // Rate r = 1088 bits (17 lanes of 64 bits)
    // Capacity c = 512 bits (8 lanes of 64 bits)
    // 5x5 grid = 25 lanes (25 * 64 = 1600 bits)
    // Stage 0: Capacity Question
    // Stage 1: Absorb Phase (XOR into Rate lanes)
    // Stage 2: Squeeze Question
    // Stage 3: Complete
    var stage by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
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
                        text = "SHA-3 KECCAK SPONGE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = primary
                    )

                    ArenaStatusBadge(
                        type = when (stage) {
                            3 -> ArenaBadgeType.SUCCESS
                            1, 2 -> ArenaBadgeType.WARNING
                            else -> ArenaBadgeType.INFO
                        },
                        label = when (stage) {
                            0 -> "PHASE 1: CAPACITY"
                            1 -> "PHASE 2: ABSORB"
                            2 -> "PHASE 3: SQUEEZE"
                            else -> "SPONGE SEALED"
                        }
                    )
                }

                Text(
                    text = "Unlike Merkle-Damgård (SHA-2), SHA-3 uses a 1600-bit permutation sponge with Rate (r=1088b) and Capacity (c=512b), rendering length extension mathematically impossible.",
                    fontSize = 12.sp,
                    color = dynamicColors.textPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }

        // 5x5 Keccak State Matrix (Rate vs Capacity visualizer)
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(16.dp))
                .background(if (isDark) ArenaPalettes.Arena4BackgroundDark else Color(0xFFF8FAFC))
                .border(1.5.dp, primary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "5×5 KECCAK-f[1600] STATE LANES", fontSize = 10.sp, fontWeight = FontWeight.Black, color = SubtextGray)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "■ Rate (r): 17 lanes", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = primary)
                        Text(text = "■ Capacity (c): 8 lanes", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = accent)
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(5) { r ->
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            repeat(5) { c ->
                                val laneIdx = r * 5 + c
                                val isRate = laneIdx < 17
                                val isAbsorbed = stage >= 2 && isRate

                                Box(
                                    modifier = Modifier
                                        .size(46.dp, 28.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            if (isRate) {
                                                if (isAbsorbed) ArenaSuccessGreen.copy(alpha = 0.35f)
                                                else primary.copy(alpha = 0.35f)
                                            } else accent.copy(alpha = 0.35f)
                                        )
                                        .border(
                                            1.dp,
                                            if (isRate) (if (isAbsorbed) ArenaSuccessGreen else primary) else accent,
                                            RoundedCornerShape(6.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isRate) "r$laneIdx" else "c${laneIdx - 17}",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace,
                                        color = dynamicColors.textPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Interactive Stage Decision
        when (stage) {
            0 -> {
                // Capacity Question
                Column(modifier = Modifier.fillMaxWidth(0.95f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "WHAT IS THE PURPOSE OF THE 512-BIT CAPACITY c?",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    listOf(
                        "Internal security margin (never directly read or modified)" to true,
                        "Storage buffer for large files" to false,
                        "Stores the output signature key" to false
                    ).forEach { (opt, isCorrect) ->
                        val isSelected = selectedOption == opt
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
                                    selectedOption = opt
                                    if (isCorrect) {
                                        ArenaHaptics.success(view, reduceMotion)
                                        stage = 1
                                        selectedOption = null
                                        feedbackText = "✓ Correct! Capacity is isolated to prevent state recovery."
                                        onActionSuccess()
                                    } else {
                                        ArenaHaptics.error(view, reduceMotion)
                                        onDeductHeart()
                                        feedbackText = "❌ Capacity c is the unexposed security margin (c = 2 × security level)."
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
            }
            1 -> {
                // Absorb Action
                Tactile3DButton(
                    title = "ABSORB MESSAGE CHUNK INTO RATE LANES (XOR 1088b) ➔",
                    style = Tactile3DButtonColor.AMBER,
                    onClick = {
                        ArenaHaptics.success(view, reduceMotion)
                        stage = 2
                        feedbackText = "Message absorbed! Now 24 Keccak-f rounds permute all 1600 bits."
                        onActionSuccess()
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .minTouchTarget48()
                )
            }
            2 -> {
                // Squeeze Question
                Column(modifier = Modifier.fillMaxWidth(0.95f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "HOW IS THE 256-BIT DIGEST SQUEEZED OUT OF THE SPONGE?",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    listOf(
                        "Read directly from the Rate r lanes (never touching Capacity c)" to true,
                        "Read equally from both Rate and Capacity lanes" to false,
                        "Decrypted with RSA private key" to false
                    ).forEach { (opt, isCorrect) ->
                        val isSelected = selectedOption == opt
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
                                    selectedOption = opt
                                    if (isCorrect) {
                                        ArenaHaptics.criticalStrike(view, reduceMotion)
                                        stage = 3
                                        selectedOption = null
                                        feedbackText = "🎉 Correct! Output bits are squeezed exclusively from the Rate lanes."
                                        onActionSuccess()
                                    } else {
                                        ArenaHaptics.error(view, reduceMotion)
                                        onDeductHeart()
                                        feedbackText = "❌ Output is only squeezed from Rate r; Capacity remains hidden."
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
            }
            else -> {
                // Advance
                Tactile3DButton(
                    title = "SHA-3 SPONGE MASTERED! ADVANCE FOUNDRY ➔",
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
