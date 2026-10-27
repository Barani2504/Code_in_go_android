package com.simats.codeingo.ui.arenas.cipher_sanctum.levels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.simats.codeingo.ui.components.Tactile3DButton
import com.simats.codeingo.ui.components.Tactile3DButtonColor
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.ArenaSuccessGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray

@Composable
fun RailFenceGridLevel(onComplete: () -> Unit) {
    val dynamicColors = LocalDynamicThemeColors.current

    // Plaintext: "DEFENDTHEWALLX" (14 chars)
    // 3-Rail Zigzag Transposition:
    // Rail 0: D . . . N . . . E . . . L       -> "DNEL"
    // Rail 1: . E . E . D . H . W . L . X     -> "EEDHWLX"
    // Rail 2: . . F . . . T . . . A . . .     -> "FTA"
    // Ciphertext: "DNELEEDHWLXFTA" (verified by ArenaCryptoEngine.railFenceEncrypt)
    val correctRailRows = listOf("DNEL", "EEDHWLX", "FTA")
    val initialScrambled = remember { mutableStateListOf("FTA", "DNEL", "EEDHWLX") }
    var selectedIdx by remember { mutableStateOf<Int?>(null) }
    var decodedResult by remember { mutableStateOf<String?>(null) }

    val isAligned = initialScrambled.toList() == correctRailRows

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Mission Briefing
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(if (dynamicColors.isDark) Color(0xFF0F1A2E) else dynamicColors.cardBackground)
                .border(1.2.dp, AmberGold.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "RAIL FENCE 3-RAIL ZIGZAG TRANSPOSITION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = AmberGold
                )
                Text(
                    text = "A transposition cipher writes plaintext letters in a continuous zigzag across 3 horizontal rails, reading them off row-by-row. Tap two rails to swap and align them from top to bottom (Rail 0 ➔ 1 ➔ 2)!",
                    fontSize = 12.sp,
                    color = dynamicColors.textPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }

        // Ciphertext Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (dynamicColors.isDark) Color(0xFF14243C) else dynamicColors.inputBackground)
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "CIPHERTEXT: DNELEEDHWLXFTA",
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                color = AmberGold
            )
        }

        // 3 Interactive Rails (min height 48dp each for touch compliance)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            initialScrambled.forEachIndexed { i, railChunk ->
                val isSelected = selectedIdx == i
                val isRowCorrect = railChunk == correctRailRows[i]

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (isSelected) AmberGold
                            else if (isAligned) ArenaSuccessGreen.copy(alpha = 0.2f)
                            else if (dynamicColors.isDark) Color(0xFF14243C)
                            else dynamicColors.inputBackground
                        )
                        .border(
                            1.5.dp,
                            if (isSelected) Color.White
                            else if (isAligned) ArenaSuccessGreen
                            else AmberGold.copy(alpha = 0.5f),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable {
                            if (selectedIdx == null) {
                                selectedIdx = i
                            } else {
                                val first = selectedIdx!!
                                val temp = initialScrambled[first]
                                initialScrambled[first] = initialScrambled[i]
                                initialScrambled[i] = temp
                                selectedIdx = null

                                if (initialScrambled.toList() == correctRailRows) {
                                    // Verified reconstruction using ArenaCryptoEngine
                                    val cipher = initialScrambled.joinToString("")
                                    decodedResult = ArenaCryptoEngine.railFenceDecrypt(cipher, 3)
                                }
                            }
                        }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "RAIL $i:  $railChunk",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = if (isSelected) Color.Black else dynamicColors.textPrimary
                        )
                        Text(
                            text = if (isRowCorrect) "✓ POSITION" else "TAP TO SWAP ⇄",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.Black else if (isRowCorrect) ArenaSuccessGreen else SubtextGray
                        )
                    }
                }
            }
        }

        // Zigzag Visualization & Plaintext Outcome
        if (isAligned) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(ArenaSuccessGreen.copy(alpha = 0.15f))
                    .border(1.dp, ArenaSuccessGreen, RoundedCornerShape(14.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "DECODED PLAINTEXT (ZIGZAG RECOVERED):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = ArenaSuccessGreen
                    )
                    Text(
                        text = decodedResult ?: "DEFENDTHEWALLX",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = dynamicColors.textPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Tactile3DButton(
            title = if (isAligned) "Clear Transposition Gate →" else "Align 3 Rail Positions",
            style = if (isAligned) Tactile3DButtonColor.GREEN else Tactile3DButtonColor.AMBER,
            isEnabled = isAligned,
            onClick = onComplete,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
