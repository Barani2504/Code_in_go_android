package com.simats.codeingo.ui.arenas.block_cipher_forge.levels

import androidx.compose.foundation.background
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.simats.codeingo.ui.components.Duolingo3DButton
import com.simats.codeingo.ui.components.Duolingo3DButtonColor
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

@Composable
fun AvalancheSprintLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current

    var currentRound by remember { mutableIntStateOf(0) }
    val flippedBitsByRound = listOf(1, 5, 26, 64) // Strict diffusion: 64 of 128 = 50%

    // 16 nodes representing byte states
    val activeNodes = remember { mutableStateListOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0) }

    LaunchedEffect(currentRound) {
        val count = when (currentRound) {
            0 -> 1
            1 -> 3
            2 -> 8
            else -> 16
        }
        repeat(16) { i -> activeNodes[i] = if (i < count) 1 else 0 }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "AVALANCHE DIFFUSION MATRIX",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "AES Round $currentRound of 3 • Flipped bits: ${flippedBitsByRound[currentRound]} / 128",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = dynamicColors.textPrimary
            )
            Text(
                text = "Standard: about 50% of output bits flip on average",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = DuolingoGreen
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 4x4 Diffusion Nodes
        Column(
            modifier = Modifier
                .size(160.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(dynamicColors.cardBorder.copy(alpha = 0.25f))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(4) { r ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(4) { c ->
                        val idx = r * 4 + c
                        val isActive = activeNodes[idx] == 1
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(30.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isActive) Color(0xFFFF2A6D) else dynamicColors.cardBorder.copy(alpha = 0.3f))
                                .clickable {
                                    activeNodes[idx] = 1 - activeNodes[idx]
                                    onActionSuccess()
                                }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Round Advance Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Duolingo3DButton(
                title = "Trigger 1-Bit Flip",
                style = Duolingo3DButtonColor.BLUE,
                onClick = { currentRound = 0 },
                modifier = Modifier.weight(1f).padding(end = 4.dp)
            )
            Duolingo3DButton(
                title = if (currentRound < 3) "Next Round >>" else "✓ 50% Diffusion",
                style = if (currentRound == 3) Duolingo3DButtonColor.GREEN else Duolingo3DButtonColor.AMBER,
                onClick = {
                    if (currentRound < 3) {
                        currentRound++
                        onActionSuccess()
                    } else {
                        onComplete()
                    }
                },
                modifier = Modifier.weight(1f).padding(start = 4.dp)
            )
        }
    }
}
