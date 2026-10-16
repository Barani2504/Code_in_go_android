package com.simats.codeingo.ui.arenas.block_cipher_forge.levels

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import com.simats.codeingo.ui.arenas.crypto.ArenaCryptoEngine
import com.simats.codeingo.ui.components.Duolingo3DButton
import com.simats.codeingo.ui.components.Duolingo3DButtonColor
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import kotlinx.coroutines.delay

@Composable
fun MixColumnsMixerLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current

    // NIST FIPS-197 known column vector: [0xdb, 0x13, 0x53, 0x45] -> [0x8e, 0x4d, 0xa1, 0xbc]
    val inputCol = remember { intArrayOf(0xdb, 0x13, 0x53, 0x45) }
    val expectedOutputCol = remember { ArenaCryptoEngine.mixColumn(inputCol).toList() }

    // User chosen coefficient offsets for Row 1: [2, 3, 1, 1]
    var c0 by remember { mutableIntStateOf(2) }
    var c1 by remember { mutableIntStateOf(3) }
    var c2 by remember { mutableIntStateOf(1) }
    var c3 by remember { mutableIntStateOf(1) }

    val isCoefficientsAligned = (c0 == 2 && c1 == 3 && c2 == 1 && c3 == 1)
    var reactorRunning by remember { mutableStateOf(false) }

    LaunchedEffect(reactorRunning) {
        if (reactorRunning) {
            delay(500L)
            reactorRunning = false
            if (isCoefficientsAligned) {
                onActionSuccess()
                onComplete()
            } else {
                onDeductHeart()
            }
        }
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
                text = "GALOIS FIELD GF(2⁸) MIXER",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Reduction polynomial: P(x) = x⁸ + x⁴ + x³ + x + 1 (0x11B)",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = dynamicColors.textSecondary,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Vessel Chamber: Input Column vs Output Column
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(dynamicColors.cardBorder.copy(alpha = 0.2f))
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Input Column
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "INPUT COL", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                inputCol.forEach { b ->
                    Text(
                        text = "0x" + b.toString(16).uppercase().padStart(2, '0'),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = dynamicColors.textPrimary
                    )
                }
            }

            Text(text = "⊗", fontSize = 20.sp, color = AmberGold)

            // MDS Matrix coefficients row 1
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "MDS ROW 1", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AmberGold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "[ $c0 , $c1 , $c2 , $c3 ]", fontSize = 13.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Black, color = AmberGold)
            }

            Text(text = "=", fontSize = 20.sp, color = AmberGold)

            // Output Column
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "OUTPUT COL", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = DuolingoGreen)
                Spacer(modifier = Modifier.height(4.dp))
                expectedOutputCol.forEach { b ->
                    Text(
                        text = if (isCoefficientsAligned) "0x" + b.toString(16).uppercase().padStart(2, '0') else "0x??",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (isCoefficientsAligned) DuolingoGreen else dynamicColors.textSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // MDS Matrix Row Dial
        Text(text = "Adjust MDS Mixing Coefficients for Row 1:", fontSize = 11.sp, color = dynamicColors.textPrimary)
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(
                Triple("c0", c0) { c0 = if (c0 == 2) 1 else 2 },
                Triple("c1", c1) { c1 = if (c1 == 3) 1 else 3 },
                Triple("c2", c2) { c2 = if (c2 == 1) 2 else 1 },
                Triple("c3", c3) { c3 = if (c3 == 1) 3 else 1 }
            ).forEach { item ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(dynamicColors.cardBorder.copy(alpha = 0.25f))
                        .clickable { item.third() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(text = item.first, fontSize = 9.sp, color = dynamicColors.textSecondary)
                    Text(
                        text = "${item.second}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF00C3FF)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Reactor Lever Button
        Duolingo3DButton(
            title = if (reactorRunning) "Diffusing Columns..." else "Engage Galois Reactor",
            style = if (isCoefficientsAligned) Duolingo3DButtonColor.GREEN else Duolingo3DButtonColor.AMBER,
            onClick = { reactorRunning = true },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
