package com.simats.codeingo.ui.arenas.integrity_foundry.levels

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.components.Duolingo3DButton
import com.simats.codeingo.ui.components.Duolingo3DButtonColor
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

@Composable
fun SpongeChamberLevel(
    onComplete: () -> Unit,
    onActionSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    var step by remember { mutableIntStateOf(0) }
    val phases = listOf(
        "1. Absorb: XOR 1088-bit rate r into state",
        "2. Permute: Apply Keccak-f[1600] 24 rounds",
        "3. Squeeze: Extract 256-bit hash digest"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "SHA-3 KECCAK SPONGE CHAMBER",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Rate (r) and Capacity (c) in 5×5 64-bit lane state",
                fontSize = 11.sp,
                color = dynamicColors.textSecondary,
                textAlign = TextAlign.Center
            )
        }

        // 5x5 Keccak State Matrix preview
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(dynamicColors.cardBorder.copy(alpha = 0.25f))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(5) { r ->
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(5) { c ->
                        val isRate = (r * 5 + c) < 17
                        Box(
                            modifier = Modifier
                                .size(34.dp, 24.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isRate) Color(0xFFB946FA).copy(alpha = 0.4f) else AmberGold.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isRate) "r" else "c",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = dynamicColors.textPrimary
                            )
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(10.dp))
                .background(dynamicColors.cardBorder.copy(alpha = 0.3f))
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = phases[step.coerceIn(0, 2)],
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center
            )
        }

        Duolingo3DButton(
            title = if (step < 2) "🌊 Sponge Step (${step + 1}/3)" else "Sponge Complete ✓",
            style = if (step >= 2) Duolingo3DButtonColor.GREEN else Duolingo3DButtonColor.BLUE,
            onClick = {
                step++
                onActionSuccess()
                if (step >= 3) onComplete()
            },
            modifier = Modifier.fillMaxWidth(0.85f)
        )
    }
}
