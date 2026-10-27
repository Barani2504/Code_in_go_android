package com.simats.codeingo.ui.arenas.block_cipher_forge.levels

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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.arenas.crypto.ArenaCryptoEngine
import com.simats.codeingo.ui.components.Tactile3DButton
import com.simats.codeingo.ui.components.Tactile3DButtonColor
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.ArenaSuccessGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

@Composable
fun AvalancheSprintLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current

    // Compute dynamic AES diffusion rounds from ArenaCryptoEngine between two 16-byte blocks differing by 1 bit
    val block1 = remember { ByteArray(16) { 0x00 } }
    val block2 = remember {
        val b = ByteArray(16) { 0x00 }
        b[0] = 0x01 // 1-bit flip at index 0
        b
    }
    val diffusionRounds = remember {
        ArenaCryptoEngine.computeAesDiffusionRounds(block1, block2)
    }

    var currentRoundIndex by remember { mutableIntStateOf(0) }
    val currentRound = diffusionRounds.getOrElse(currentRoundIndex) { diffusionRounds.last() }
    val diffusionPercentage = (currentRound.totalDifferingBits / 128f) * 100f

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "AES AVALANCHE DIFFUSION MATRIX",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "AES Round ${currentRound.round} of 3 • Flipped Bits: ${currentRound.totalDifferingBits} / 128",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = dynamicColors.textPrimary
            )
            Text(
                text = "Strict Avalanche: ${String.format("%.1f", diffusionPercentage)}% (Standard: ~50% on average)",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = if (diffusionPercentage >= 40.0) ArenaSuccessGreen else AmberGold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 4x4 AES Byte State Heatmap (16 bytes = 128 bits)
        Column(
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(dynamicColors.cardBorder.copy(alpha = 0.25f))
                .padding(10.dp),
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
                                    if (isFlipped) Color(0xFFFF2A6D)
                                    else dynamicColors.cardBorder.copy(alpha = 0.35f)
                                )
                                .border(
                                    1.dp,
                                    if (isFlipped) Color(0xFFFF2A6D) else dynamicColors.cardBorder,
                                    RoundedCornerShape(6.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "B$byteIdx ($bitsFlippedInByte)",
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = if (isFlipped) Color.White else dynamicColors.textSecondary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Round Advance Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Tactile3DButton(
                title = "1-Bit Input Flip",
                style = Tactile3DButtonColor.BLUE,
                onClick = { currentRoundIndex = 0 },
                modifier = Modifier.weight(1f)
            )
            Tactile3DButton(
                title = if (currentRoundIndex < diffusionRounds.size - 1) "Next Round >>" else "✓ Confirm Diffusion",
                style = if (currentRoundIndex >= diffusionRounds.size - 1) Tactile3DButtonColor.GREEN else Tactile3DButtonColor.AMBER,
                onClick = {
                    if (currentRoundIndex < diffusionRounds.size - 1) {
                        currentRoundIndex++
                        onActionSuccess()
                    } else {
                        onComplete()
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
