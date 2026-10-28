package com.simats.codeingo.ui.arenas.block_cipher_forge.levels

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
import com.simats.codeingo.ui.arenas.crypto.ArenaCryptoEngine
import com.simats.codeingo.ui.components.Tactile3DButton
import com.simats.codeingo.ui.components.Tactile3DButtonColor
import com.simats.codeingo.ui.theme.ArenaDangerRed
import com.simats.codeingo.ui.theme.ArenaSuccessGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.LocalReduceMotion

@Composable
fun AvalancheSprintLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val view = LocalView.current
    val reduceMotion = LocalReduceMotion.current
    val isDark = dynamicColors.isDark

    val primary = ArenaPalettes.Arena2Primary
    val accent = ArenaPalettes.Arena2Accent
    val cardBg = if (isDark) ArenaPalettes.Arena2CardDark else dynamicColors.cardBackground

    // Byte injection index for 1-bit flip
    var flippedByteIndex by remember { mutableIntStateOf(0) }

    val block1 = remember { ByteArray(16) { 0x00 } }
    val block2 = remember(flippedByteIndex) {
        val b = ByteArray(16) { 0x00 }
        b[flippedByteIndex] = 0x01 // 1-bit flip at selected byte
        b
    }

    val diffusionRounds = remember(block2) {
        ArenaCryptoEngine.computeAesDiffusionRounds(block1, block2)
    }

    var currentRoundIndex by remember { mutableIntStateOf(0) }
    val currentRound = diffusionRounds.getOrElse(currentRoundIndex) { diffusionRounds.last() }
    val diffusionPercentage = (currentRound.totalDifferingBits / 128f) * 100f

    // Interactive Strict Avalanche Criterion (SAC) Challenge
    var selectedSacOption by remember { mutableStateOf<Int?>(null) }
    var sacSolved by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Mission Header with Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "AES AVALANCHE DIFFUSION MATRIX",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = accent,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Strict Avalanche Criterion (SAC)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = dynamicColors.textPrimary
                )
            }
            ArenaStatusBadge(
                type = if (sacSolved) ArenaBadgeType.SUCCESS else ArenaBadgeType.WARNING,
                label = if (sacSolved) "SAC CERTIFIED" else "ROUND ${currentRound.round}/3"
            )
        }

        // Stats Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(cardBg)
                .border(1.dp, dynamicColors.cardBorder, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "FLIPPED BITS IN ROUND ${currentRound.round}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = dynamicColors.textSecondary
                    )
                    Text(
                        text = "${currentRound.totalDifferingBits} / 128 bits",
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        color = primary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "DIFFUSION PERCENTAGE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = dynamicColors.textSecondary
                    )
                    Text(
                        text = "${String.format("%.1f", diffusionPercentage)}% (SAC ~50%)",
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        color = if (diffusionPercentage >= 40.0) ArenaSuccessGreen else accent
                    )
                }
            }
        }

        // 4x4 AES Byte State Heatmap (16 bytes = 128 bits)
        Column(
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(cardBg)
                .border(1.dp, dynamicColors.cardBorder, RoundedCornerShape(14.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(4) { r ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    repeat(4) { c ->
                        val byteIdx = r * 4 + c
                        val bitsFlippedInByte = currentRound.byteDiffs.getOrElse(byteIdx) { 0 }
                        val isFlipped = bitsFlippedInByte > 0
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (isFlipped) primary
                                    else dynamicColors.cardBorder.copy(alpha = 0.35f)
                                )
                                .border(
                                    1.dp,
                                    if (isFlipped) primary else dynamicColors.cardBorder,
                                    RoundedCornerShape(6.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "B$byteIdx ($bitsFlippedInByte)",
                                fontSize = 8.5.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = if (isFlipped) Color.White else dynamicColors.textSecondary
                            )
                        }
                    }
                }
            }
        }

        // Interactive Byte Injection Selector & Round Stepper
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Tactile3DButton(
                title = "Inject Flip (Byte $flippedByteIndex)",
                style = Tactile3DButtonColor.BLUE,
                onClick = {
                    ArenaHaptics.tap(view, reduceMotion)
                    flippedByteIndex = (flippedByteIndex + 5) % 16
                    currentRoundIndex = 0
                },
                modifier = Modifier.weight(1f)
            )

            Tactile3DButton(
                title = if (currentRoundIndex < diffusionRounds.size - 1) "Advance Round >>" else "Reset to Round 1",
                style = if (currentRoundIndex < diffusionRounds.size - 1) Tactile3DButtonColor.AMBER else Tactile3DButtonColor.BLUE,
                onClick = {
                    ArenaHaptics.tap(view, reduceMotion)
                    currentRoundIndex = (currentRoundIndex + 1) % diffusionRounds.size
                },
                modifier = Modifier.weight(1f)
            )
        }

        // Pedagogical SAC Knowledge Challenge (Eliminates passive progression)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(cardBg)
                .border(1.dp, dynamicColors.cardBorder, RoundedCornerShape(12.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "SAC Verification Challenge:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = primary
            )
            Text(
                text = "Under the Strict Avalanche Criterion, when 1 input bit is inverted, what proportion of output ciphertext bits must flip with equal probability?",
                fontSize = 11.sp,
                color = dynamicColors.textSecondary,
                lineHeight = 15.sp
            )

            val options = listOf(
                "A) Approximately 50% (~64 of 128 bits)", // Index 0 (CORRECT)
                "B) Exactly 100% (All 128 bits must invert)",
                "C) Fewer than 10% (Minimal disturbance)",
                "D) Only bits in the injected column"
            )

            options.forEachIndexed { index, optionText ->
                val isSelected = selectedSacOption == index
                val isCorrect = index == 0

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .minTouchTarget48()
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSelected && isCorrect) ArenaSuccessGreen.copy(alpha = 0.2f)
                            else if (isSelected) ArenaDangerRed.copy(alpha = 0.2f)
                            else dynamicColors.cardBorder.copy(alpha = 0.2f)
                        )
                        .border(
                            1.dp,
                            if (isSelected && isCorrect) ArenaSuccessGreen
                            else if (isSelected) ArenaDangerRed
                            else dynamicColors.cardBorder,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            selectedSacOption = index
                            if (isCorrect) {
                                ArenaHaptics.success(view, reduceMotion)
                                sacSolved = true
                                onActionSuccess()
                            } else {
                                ArenaHaptics.error(view, reduceMotion)
                                sacSolved = false
                                onDeductHeart()
                            }
                        }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = optionText,
                        fontSize = 11.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected && isCorrect) ArenaSuccessGreen
                                else if (isSelected) ArenaDangerRed
                                else dynamicColors.textPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f, fill = false))

        Tactile3DButton(
            title = if (sacSolved) "Certify Avalanche Criterion ✓" else "Verify SAC Answer",
            style = if (sacSolved) Tactile3DButtonColor.GREEN else Tactile3DButtonColor.AMBER,
            onClick = {
                if (sacSolved) {
                    ArenaHaptics.success(view, reduceMotion)
                    onActionSuccess()
                    onComplete()
                } else {
                    ArenaHaptics.error(view, reduceMotion)
                    onDeductHeart()
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
