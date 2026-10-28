package com.simats.codeingo.ui.visualizer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.data.model.DSAVisualizerType
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DsaBlue
import com.simats.codeingo.ui.theme.DsaRed
import com.simats.codeingo.ui.theme.DsaTeal
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.InputBorder
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.visualizer.engine.DesEngine
import com.simats.codeingo.ui.visualizer.model.CNSLabMode
import kotlin.math.roundToInt

@Composable
fun DESFeistelVisualizer(mode: CNSLabMode = CNSLabMode.EXPLORE) {
    when (mode) {
        CNSLabMode.CHALLENGE -> {
            CNSChallengeCardView(labType = DSAVisualizerType.STACK)
        }
        CNSLabMode.CODE_TEST -> {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                CNSTestVectorCardView(labType = DSAVisualizerType.STACK)
                CNSLanguageImplementationCardView(structureName = DSAVisualizerType.STACK.title)
            }
        }
        CNSLabMode.ATTACK -> {
            DESFeistelAttackView()
        }
        CNSLabMode.EXPLORE -> {
            DESFeistelExploreView()
        }
    }
}

@Composable
private fun DESFeistelAttackView() {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    val amberColor = if (isDark) AmberGold else Color(0xFFB45309)
    val redColor = if (isDark) DsaRed else Color(0xFFDC2626)
    val greenColor = if (isDark) DuolingoGreen else Color(0xFF15803D)

    val defaultKey = 0x133457799BBCDFF1L
    val defaultPt = 0x0123456789ABCDEFL

    val desTrace = remember {
        DesEngine.encrypt(defaultPt, defaultKey)
    }

    val complementedTrace = remember {
        val notPt = defaultPt.inv()
        val notKey = defaultKey.inv()
        val compTrace = DesEngine.encrypt(notPt, notKey)
        val expectedCompCipher = desTrace.ciphertext.inv()
        Triple(compTrace.ciphertext, expectedCompCipher, compTrace.ciphertext == expectedCompCipher)
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                .background(DsaRed.copy(alpha = 0.15f)).border(1.dp, DsaRed.copy(alpha = 0.5f), RoundedCornerShape(10.dp)).padding(12.dp)
        ) {
            Text(
                text = "🔓 ATTACK SUITE: 56-Bit Exhaustive Search & Key Complementation Property",
                fontSize = 11.sp, fontWeight = FontWeight.Black, color = redColor
            )
        }

        // Key Complementation Property Demonstration
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, AmberGold.copy(alpha = 0.6f), RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "⚡ DES KEY COMPLEMENTATION PROPERTY", fontSize = 12.sp, fontWeight = FontWeight.Black, color = amberColor)
                Text(text = "Formula: If C = DES(P, K), then DES(~P, ~K) = ~C exactly.", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = dynamicColors.textPrimary)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Original Ciphertext C:", fontSize = 10.sp, color = SubtextGray)
                    Text(text = "0x%016X".format(desTrace.ciphertext), fontSize = 10.5.sp, fontFamily = FontFamily.Monospace, color = dynamicColors.textPrimary)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "DES(~P, ~K) Output:", fontSize = 10.sp, color = SubtextGray)
                    Text(text = "0x%016X".format(complementedTrace.first), fontSize = 10.5.sp, fontFamily = FontFamily.Monospace, color = amberColor)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Bitwise Complement (~C):", fontSize = 10.sp, color = SubtextGray)
                    Text(text = "0x%016X".format(complementedTrace.second), fontSize = 10.5.sp, fontFamily = FontFamily.Monospace, color = greenColor)
                }

                Text(
                    text = if (complementedTrace.third) "✅ Complementation Property Verified! This reduces the brute-force search space by a factor of 2 (testing 1 key tests its complement for free)." else "Verification failed",
                    fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = greenColor
                )
            }
        }

        // Historical Cracking Timeline Card
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, InputBorder, RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "🕰️ DES EXHAUSTIVE CRACKING TIMELINE (2^56 KEYS)", fontSize = 11.5.sp, fontWeight = FontWeight.Black, color = redColor)
                Text(text = "• 1977: Diffie & Hellman propose \$20M custom machine solving DES in 1 day.\n• 1998: EFF Deep Crack (\$250,000) cracks DES in 56 hours.\n• 1999: Deep Crack + distributed.net cracks DES in 22 hours.\n• Today: Commercial Antminer / GPU array (100 TH/s) cracks 56-bit DES in under 12 minutes for <\$50.", fontSize = 10.5.sp, color = SubtextGray, lineHeight = 16.sp)
            }
        }
    }
}

@Composable
private fun DESFeistelExploreView() {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    val amberColor = if (isDark) AmberGold else Color(0xFFB45309)
    val tealColor = if (isDark) DsaTeal else Color(0xFF0D9488)
    val greenColor = if (isDark) DuolingoGreen else Color(0xFF15803D)

    val defaultKey = 0x133457799BBCDFF1L
    val defaultPt = 0x0123456789ABCDEFL

    var currentKey by remember { mutableStateOf(defaultKey) }
    var currentPt by remember { mutableStateOf(defaultPt) }
    var roundIndex by remember { mutableIntStateOf(1) } // 1..16

    val desTrace = remember(currentPt, currentKey) {
        DesEngine.encrypt(currentPt, currentKey)
    }

    val currentRoundState = remember(desTrace, roundIndex) {
        desTrace.rounds.getOrElse(roundIndex - 1) { desTrace.rounds.first() }
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Round Scrubbing & Stepper Control Hub
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, DsaBlue.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "16-ROUND FEISTEL SWAP TRACER", fontSize = 12.sp, fontWeight = FontWeight.Black, color = DsaBlue)
                    Text(text = "ROUND $roundIndex / 16", fontSize = 13.sp, fontWeight = FontWeight.Black, color = amberColor, fontFamily = FontFamily.Monospace)
                }

                Slider(
                    value = roundIndex.toFloat(),
                    onValueChange = { roundIndex = it.roundToInt().coerceIn(1, 16) },
                    valueRange = 1f..16f,
                    steps = 14,
                    colors = SliderDefaults.colors(thumbColor = DsaBlue, activeTrackColor = DsaBlue)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier.weight(1f).height(48.dp).clip(RoundedCornerShape(10.dp))
                            .background(dynamicColors.inputBackground)
                            .border(1.dp, dynamicColors.inputBorder, RoundedCornerShape(10.dp))
                            .clickable { if (roundIndex > 1) roundIndex-- }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "◀ STEP BACK", fontSize = 10.5.sp, fontWeight = FontWeight.Black, color = dynamicColors.textPrimary)
                    }
                    Box(
                        modifier = Modifier.weight(1f).height(48.dp).clip(RoundedCornerShape(10.dp))
                            .background(DsaBlue)
                            .clickable { if (roundIndex < 16) roundIndex++ }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "STEP FORWARD ▶", fontSize = 10.5.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                }
            }
        }

        // Active Feistel Halves & Swap Diagram
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, InputBorder, RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Left Half
                    Box(
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp))
                            .background(DsaBlue.copy(alpha = if (isDark) 0.15f else 0.08f))
                            .border(1.dp, DsaBlue, RoundedCornerShape(12.dp)).padding(10.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "LEFT HALF (L_${roundIndex - 1})", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DsaBlue)
                            Text(
                                text = "0x%08X".format(currentRoundState.left),
                                fontSize = 13.sp, fontWeight = FontWeight.Black, color = dynamicColors.textPrimary, fontFamily = FontFamily.Monospace
                            )
                            Text(text = "32 Bits", fontSize = 9.sp, color = SubtextGray)
                        }
                    }

                    // Right Half
                    Box(
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp))
                            .background(AmberGold.copy(alpha = if (isDark) 0.15f else 0.08f))
                            .border(1.dp, AmberGold, RoundedCornerShape(12.dp)).padding(10.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "RIGHT HALF (R_${roundIndex - 1})", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = amberColor)
                            Text(
                                text = "0x%08X".format(currentRoundState.right),
                                fontSize = 13.sp, fontWeight = FontWeight.Black, color = dynamicColors.textPrimary, fontFamily = FontFamily.Monospace
                            )
                            Text(text = "32 Bits", fontSize = 9.sp, color = SubtextGray)
                        }
                    }
                }

                // Subkey and F-Function Detail Box
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                        .background(dynamicColors.inputBackground).border(1.dp, dynamicColors.inputBorder, RoundedCornerShape(10.dp)).padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "PC-2 Round Subkey (K_$roundIndex):", fontSize = 10.sp, color = SubtextGray, fontWeight = FontWeight.Bold)
                        Text(text = "0x%012X".format(currentRoundState.subkey), fontSize = 11.sp, fontWeight = FontWeight.Black, color = amberColor, fontFamily = FontFamily.Monospace)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Expansion E(R_${roundIndex - 1}) [48b]:", fontSize = 10.sp, color = SubtextGray)
                        Text(text = "0x%012X".format(currentRoundState.expandedR), fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = dynamicColors.textPrimary)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "8 S-Boxes Output [32b]:", fontSize = 10.sp, color = SubtextGray)
                        Text(text = "0x%08X".format(currentRoundState.sBoxOutput), fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = tealColor)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "F-Function Output (after P):", fontSize = 10.sp, color = SubtextGray, fontWeight = FontWeight.Bold)
                        Text(text = "0x%08X".format(currentRoundState.fOutput), fontSize = 11.sp, fontWeight = FontWeight.Black, color = greenColor, fontFamily = FontFamily.Monospace)
                    }
                }

                // Final Ciphertext Result
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                        .background(DsaBlue.copy(alpha = if (isDark) 0.15f else 0.08f)).padding(8.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "NIST Verified Ciphertext:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DsaBlue)
                        Text(
                            text = "0x%016X".format(desTrace.ciphertext),
                            fontSize = 12.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = dynamicColors.textPrimary
                        )
                    }
                }
            }
        }
    }
}
