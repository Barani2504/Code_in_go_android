package com.simats.codeingo.ui.visualizer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.data.model.DSAVisualizerType
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DsaGreen
import com.simats.codeingo.ui.theme.InputBorder
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.visualizer.engine.KasiskiEngine
import com.simats.codeingo.ui.visualizer.model.CNSLabMode
import kotlin.math.roundToInt

@Composable
fun CaesarCipherVisualizer(mode: CNSLabMode = CNSLabMode.EXPLORE) {
    when (mode) {
        CNSLabMode.CHALLENGE -> {
            CNSChallengeCardView(labType = DSAVisualizerType.ARRAY)
        }
        CNSLabMode.CODE_TEST -> {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                CNSTestVectorCardView(labType = DSAVisualizerType.ARRAY)
                CNSLanguageImplementationCardView(structureName = DSAVisualizerType.ARRAY.title)
            }
        }
        CNSLabMode.ATTACK -> {
            CaesarCipherAttackView()
        }
        CNSLabMode.EXPLORE -> {
            CaesarCipherExploreView()
        }
    }
}

@Composable
private fun CaesarCipherAttackView() {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    val amberColor = if (isDark) AmberGold else Color(0xFFB45309)
    val greenColor = if (isDark) DsaGreen else Color(0xFF15803D)

    var sampleCipher by remember { mutableStateOf("WKH TXLFN EURZQ IRA MXPSV RYHU WKH ODCB GRJ") }
    var shiftToInspect by remember { mutableIntStateOf(3) }

    val chiSquaredScores = remember(sampleCipher) {
        KasiskiEngine.analyzeCaesarShifts(sampleCipher)
    }

    val letterFrequencies = remember(sampleCipher) {
        KasiskiEngine.computeLetterFrequencies(sampleCipher)
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                .background(AmberGold.copy(alpha = 0.15f)).border(1.dp, AmberGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp)).padding(12.dp)
        ) {
            Text(
                text = "🔓 ATTACK SUITE: Automated Chi-Squared (χ²) Goodness of Fit & 26-Letter Exhaustive Search",
                fontSize = 11.sp, fontWeight = FontWeight.Black, color = amberColor
            )
        }

        // Chi-Squared Automatic Solver Card
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, AmberGold.copy(alpha = 0.6f), RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "⚡ AUTOMATED χ² FREQUENCY ATTACK", fontSize = 12.sp, fontWeight = FontWeight.Black, color = amberColor)
                val best = chiSquaredScores.firstOrNull()
                if (best != null) {
                    Text(
                        text = "🏆 Optimal Decryption Found: Shift K = ${best.shift}  (χ² = ${"%.1f".format(best.chiSquared)})",
                        fontSize = 13.sp, fontWeight = FontWeight.Black, color = greenColor
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    chiSquaredScores.take(5).forEachIndexed { rank, candidate ->
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (rank == 0) DsaGreen.copy(alpha = 0.15f) else dynamicColors.inputBackground)
                                .clickable { shiftToInspect = candidate.shift }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "#${rank + 1} Shift K=${candidate.shift}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (rank == 0) greenColor else dynamicColors.textPrimary)
                            Text(text = candidate.decryptedPreview.take(18) + "...", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = SubtextGray)
                            Text(text = "χ²=${"%.1f".format(candidate.chiSquared)}", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = amberColor)
                        }
                    }
                }
            }
        }

        // Full 26-Letter Frequency Distribution vs English Profile
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, InputBorder, RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "📊 26-LETTER MONOGRAM PROFILE", fontSize = 11.sp, fontWeight = FontWeight.Black, color = SubtextGray)
                    Text(text = "Green: Actual | Gray: English Expected", fontSize = 9.sp, color = SubtextGray)
                }

                Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    letterFrequencies.forEach { item ->
                        val actualHeight = (item.percentage * 2.8f).coerceIn(2f, 42f)
                        val expectedHeight = (item.expectedPercentage * 2.8f).coerceIn(2f, 42f)

                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                                Box(modifier = Modifier.width(6.dp).height(actualHeight.dp).clip(RoundedCornerShape(2.dp)).background(DsaGreen))
                                Box(modifier = Modifier.width(4.dp).height(expectedHeight.dp).clip(RoundedCornerShape(2.dp)).background(Color.Gray.copy(alpha = 0.4f)))
                            }
                            Text(text = "${item.char}", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary)
                        }
                    }
                }
                Text(text = "Notice that the entire frequency shape is shifted intact without changing height, proving Caesar is vulnerable to frequency correlation.", fontSize = 10.sp, color = SubtextGray)
            }
        }
    }
}

@Composable
private fun CaesarCipherExploreView() {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    val amberColor = if (isDark) AmberGold else Color(0xFFB45309)
    val greenColor = if (isDark) DsaGreen else Color(0xFF15803D)

    var textInput by remember { mutableStateOf("THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG") }
    var shift by remember { mutableIntStateOf(3) }
    var isDecryptMode by remember { mutableStateOf(false) }
    val clipboard = LocalClipboardManager.current
    var isCopied by remember { mutableStateOf(false) }

    val effectiveShift = if (isDecryptMode) (26 - (shift % 26)) % 26 else shift % 26

    val outputText = remember(textInput, effectiveShift) {
        textInput.map { char ->
            if (char.isLetter()) {
                val base = if (char.isUpperCase()) 'A' else 'a'
                val shifted = (char.code - base.code + effectiveShift).mod(26)
                (base.code + shifted).toChar()
            } else char
        }.joinToString("")
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Direction Toggle
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                    .background(if (!isDecryptMode) DsaGreen else (if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.05f)))
                    .border(1.dp, if (!isDecryptMode) DsaGreen else dynamicColors.inputBorder, RoundedCornerShape(10.dp))
                    .clickable { isDecryptMode = false }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "ENCRYPT (P + K mod 26)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = if (!isDecryptMode) Color.Black else SubtextGray)
            }
            Box(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                    .background(if (isDecryptMode) AmberGold else (if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.05f)))
                    .border(1.dp, if (isDecryptMode) AmberGold else dynamicColors.inputBorder, RoundedCornerShape(10.dp))
                    .clickable { isDecryptMode = true }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "DECRYPT (C - K mod 26)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = if (isDecryptMode) Color.Black else SubtextGray)
            }
        }

        // Input Card
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, InputBorder, RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (isDecryptMode) "INPUT CIPHERTEXT" else "INPUT PLAINTEXT",
                    fontSize = 11.sp, fontWeight = FontWeight.Black,
                    color = if (isDecryptMode) amberColor else greenColor
                )
                BasicTextField(
                    value = textInput,
                    onValueChange = { if (it.length <= 60) textInput = it },
                    textStyle = TextStyle(
                        color = dynamicColors.textPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    ),
                    cursorBrush = SolidColor(if (isDecryptMode) AmberGold else DsaGreen),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Key Shift Slider
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, InputBorder, RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "MODULAR SHIFT KEY (K)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = SubtextGray)
                    Text(text = "K = $shift (${('A'.code + shift).toChar()})", fontSize = 14.sp, fontWeight = FontWeight.Black, color = greenColor, fontFamily = FontFamily.Monospace)
                }
                Slider(
                    value = shift.toFloat(),
                    onValueChange = { shift = it.roundToInt() },
                    valueRange = 0f..25f,
                    steps = 24,
                    colors = SliderDefaults.colors(thumbColor = DsaGreen, activeTrackColor = DsaGreen)
                )
            }
        }

        // Output Display Card
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, if (isDecryptMode) AmberGold.copy(alpha = 0.6f) else DsaGreen.copy(alpha = 0.6f), RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isDecryptMode) "RECOVERED PLAINTEXT" else "GENERATED CIPHERTEXT",
                        fontSize = 11.sp, fontWeight = FontWeight.Black,
                        color = if (isDecryptMode) amberColor else greenColor
                    )
                    Row(
                        modifier = Modifier.clickable {
                            clipboard.setText(AnnotatedString(outputText))
                            isCopied = true
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = if (isDecryptMode) amberColor else greenColor, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (isCopied) "Copied!" else "Copy", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isDecryptMode) amberColor else greenColor)
                    }
                }

                Text(
                    text = outputText.ifEmpty { "(empty)" },
                    fontSize = 16.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace,
                    color = if (isDecryptMode) amberColor else greenColor, letterSpacing = 1.sp
                )
            }
        }
    }
}
