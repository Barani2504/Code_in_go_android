package com.simats.codeingo.ui.visualizer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.arenas.crypto.ArenaCryptoEngine
import com.simats.codeingo.ui.components.DuolingoButton
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DsaBlue
import com.simats.codeingo.ui.theme.DsaGreen
import com.simats.codeingo.ui.theme.DsaOrange
import com.simats.codeingo.ui.theme.DsaPurple
import com.simats.codeingo.ui.theme.DsaRed
import com.simats.codeingo.ui.theme.DsaTeal
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.InputBorder
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.visualizer.engine.AesEngine
import com.simats.codeingo.ui.visualizer.engine.DesEngine
import com.simats.codeingo.ui.visualizer.engine.DhEngine
import com.simats.codeingo.ui.visualizer.engine.HmacPasswordEngine
import com.simats.codeingo.ui.visualizer.engine.KasiskiEngine
import com.simats.codeingo.ui.visualizer.engine.RsaEngine
import com.simats.codeingo.ui.visualizer.engine.Sha256Engine
import com.simats.codeingo.ui.visualizer.engine.TlsEngine
import java.math.BigInteger
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.math.roundToInt

// ══════════════════════════════════════════════════════════════════
// 🛡️ CNS VISUALIZER ENGINES (10 Flagship Cryptography & NetSec Labs)
// Powered by Rigorous NIST / RFC Mathematical Engines
// ══════════════════════════════════════════════════════════════════

// ──────────────────────────────────────────────
// 1. Caesar & Shift Cipher Visualizer (ARRAY)
// ──────────────────────────────────────────────
@Composable
fun CaesarCipherVisualizer() {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val amberColor = if (isDark) AmberGold else Color(0xFFB45309)
    val greenColor = if (isDark) DsaGreen else Color(0xFF15803D)
    val cyanColor = if (isDark) Color.Cyan else Color(0xFF0284C7)

    var textInput by remember { mutableStateOf("THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG") }
    var shift by remember { mutableIntStateOf(3) }
    var isDecryptMode by remember { mutableStateOf(false) }
    var activeSubTab by remember { mutableIntStateOf(0) } // 0: Live Wheel, 1: 26-Letter Frequency, 2: Chi-Squared Solver
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

    val letterFrequencies = remember(outputText) {
        KasiskiEngine.computeLetterFrequencies(outputText)
    }

    val chiSquaredScores = remember(outputText) {
        KasiskiEngine.analyzeCaesarShifts(outputText)
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Educational Disclaimer Badge
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                .background(AmberGold.copy(alpha = if (isDark) 0.15f else 0.10f))
                .border(1.dp, AmberGold.copy(alpha = if (isDark) 0.5f else 0.35f), RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "⚠️ MONOALPHABETIC TOY CIPHER — Keyspace = 26 shifts (4.7 bits). Trivial to break via frequency analysis or exhaustive search in <1ms.",
                fontSize = 10.5.sp, color = amberColor, lineHeight = 14.sp, fontWeight = FontWeight.Bold
            )
        }

        // SubTab Selector
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("SHIFT WHEEL", "26-LETTER FREQUENCY", "CHI-SQUARED SOLVER").forEachIndexed { index, label ->
                val selected = activeSubTab == index
                Box(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                        .background(if (selected) DsaGreen else (if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.05f)))
                        .border(1.dp, if (selected) DsaGreen else dynamicColors.inputBorder, RoundedCornerShape(10.dp))
                        .clickable { activeSubTab = index }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = label, fontSize = 9.5.sp, fontWeight = FontWeight.Black, color = if (selected) Color.Black else SubtextGray)
                }
            }
        }

        // Direction Toggle
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                    .background(if (!isDecryptMode) DsaGreen else (if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.05f)))
                    .border(1.dp, if (!isDecryptMode) DsaGreen else dynamicColors.inputBorder, RoundedCornerShape(10.dp))
                    .clickable { isDecryptMode = false }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "ENCRYPT (P + K mod 26)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = if (!isDecryptMode) Color.Black else SubtextGray)
            }
            Box(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                    .background(if (isDecryptMode) AmberGold else (if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.05f)))
                    .border(1.dp, if (isDecryptMode) AmberGold else dynamicColors.inputBorder, RoundedCornerShape(10.dp))
                    .clickable { isDecryptMode = true }
                    .padding(vertical = 8.dp),
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

        // Conditional Tab Contents
        when (activeSubTab) {
            1 -> {
                // Full 26-Letter Frequency Histogram with English overlay
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(dynamicColors.inputBackground)
                        .border(1.dp, dynamicColors.inputBorder, RoundedCornerShape(14.dp)).padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "📊 FULL 26-LETTER FREQUENCY DISTRIBUTION", fontSize = 10.sp, fontWeight = FontWeight.Black, color = SubtextGray)
                            Text(text = "Green: Actual | Gray: English Expected", fontSize = 9.sp, color = SubtextGray)
                        }

                        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            letterFrequencies.forEach { item ->
                                val actualHeight = (item.percentage * 2.5f).coerceIn(2f, 40f)
                                val expectedHeight = (item.expectedPercentage * 2.5f).coerceIn(2f, 40f)

                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                                        Box(modifier = Modifier.width(6.dp).height(actualHeight.dp).clip(RoundedCornerShape(2.dp)).background(DsaGreen))
                                        Box(modifier = Modifier.width(4.dp).height(expectedHeight.dp).clip(RoundedCornerShape(2.dp)).background(Color.Gray.copy(alpha = 0.4f)))
                                    }
                                    Text(text = "${item.char}", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary)
                                }
                            }
                        }
                        Text(text = "Notice how the entire frequency profile shifts rigidly by K positions without flattening peaks (E, T, A).", fontSize = 10.sp, color = SubtextGray)
                    }
                }
            }
            2 -> {
                // Chi-Squared Automatic Solver
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(dynamicColors.inputBackground)
                        .border(1.dp, dynamicColors.inputBorder, RoundedCornerShape(14.dp)).padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = "⚡ CHI-SQUARED AUTOMATIC CRACKER (χ² GOODNESS OF FIT)", fontSize = 10.sp, fontWeight = FontWeight.Black, color = amberColor)
                        val best = chiSquaredScores.firstOrNull()
                        if (best != null) {
                            Text(
                                text = "🏆 Most Likely Shift: K = ${best.shift} (χ² = ${"%.1f".format(best.chiSquared)})",
                                fontSize = 12.sp, fontWeight = FontWeight.Black, color = greenColor
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            chiSquaredScores.take(5).forEachIndexed { rank, candidate ->
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (rank == 0) DsaGreen.copy(alpha = 0.15f) else Color.Transparent)
                                        .clickable { shift = candidate.shift }
                                        .padding(horizontal = 6.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "#${rank + 1} Shift K=${candidate.shift}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (rank == 0) greenColor else dynamicColors.textPrimary)
                                    Text(text = candidate.decryptedPreview.take(18) + "...", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = SubtextGray)
                                    Text(text = "χ²=${"%.1f".format(candidate.chiSquared)}", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = amberColor)
                                }
                            }
                        }
                    }
                }
            }
            else -> {
                // Shift Wheel Summary
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(dynamicColors.inputBackground).padding(10.dp)
                ) {
                    Text(
                        text = "Formula: C = (P + K) mod 26  |  Decryption: P = (C - K + 26) mod 26. Try dragging the shift slider above or check the Chi-Squared Solver tab to see cryptanalysis in action.",
                        fontSize = 10.5.sp, color = SubtextGray, lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

// ──────────────────────────────────────────────
// 2. DES Feistel Network Visualizer (STACK)
// ──────────────────────────────────────────────
@Composable
fun DESFeistelVisualizer() {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val amberColor = if (isDark) AmberGold else Color(0xFFB45309)
    val redColor = if (isDark) DsaRed else Color(0xFFDC2626)
    val tealColor = if (isDark) DsaTeal else Color(0xFF0D9488)
    val greenColor = if (isDark) DuolingoGreen else Color(0xFF15803D)

    // NIST FIPS 46-3 Verified Test Vector default:
    // Key: 0x133457799BBCDFF1, Plaintext: 0x0123456789ABCDEF -> Ciphertext: 0x85E813540F0AB405
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
        // Deprecation Warning Banner
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                .background(DsaRed.copy(alpha = if (isDark) 0.15f else 0.08f))
                .border(1.dp, DsaRed.copy(alpha = if (isDark) 0.5f else 0.35f), RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "⚠️ DEPRECATED NIST STANDARD (FIPS 46-3) — Effective key length is only 56 bits. Exhaustively broken in 1998 by EFF Deep Crack in 56 hours. Replaced globally by AES.",
                fontSize = 10.5.sp, color = redColor, lineHeight = 14.sp, fontWeight = FontWeight.Bold
            )
        }

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
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp))
                            .background(dynamicColors.inputBackground)
                            .border(1.dp, dynamicColors.inputBorder, RoundedCornerShape(8.dp))
                            .clickable { if (roundIndex > 1) roundIndex-- }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "◀ STEP BACK", fontSize = 10.sp, fontWeight = FontWeight.Black, color = dynamicColors.textPrimary)
                    }
                    Box(
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp))
                            .background(DsaBlue)
                            .clickable { if (roundIndex < 16) roundIndex++ }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "STEP FORWARD ▶", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.White)
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
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                        .background(dynamicColors.inputBackground).border(1.dp, dynamicColors.inputBorder, RoundedCornerShape(10.dp)).padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
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

// ──────────────────────────────────────────────
// 3. AES 128-Bit State Matrix & Modes Visualizer (QUEUE)
// ──────────────────────────────────────────────
@Composable
fun AESMatrixVisualizer() {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val amberColor = if (isDark) AmberGold else Color(0xFFB45309)
    val redColor = if (isDark) DsaRed else Color(0xFFDC2626)
    val greenColor = if (isDark) DuolingoGreen else Color(0xFF15803D)

    var mainTab by remember { mutableIntStateOf(0) } // 0: State Matrix Rounds, 1: Modes & Attacks
    var selectedRound by remember { mutableIntStateOf(1) } // 1..10
    var selectedStep by remember { mutableStateOf("SubBytes") } // "Start", "SubBytes", "ShiftRows", "MixColumns", "AddRoundKey"
    var selectedMode by remember { mutableStateOf("ECB") }

    // FIPS-197 Appendix B Test Vector
    val fipsKey = remember { AesEngine.hexToBytes("2b7e151628aed2a6abf7158809cf4f3c") }
    val fipsPt = remember { AesEngine.hexToBytes("3243f6a8885a308d313198a2e0370734") }

    val trace = remember(fipsPt, fipsKey) {
        AesEngine.encryptBlockWithTrace(fipsPt, fipsKey)
    }

    val currentRoundState = remember(trace, selectedRound) {
        trace.rounds[selectedRound - 1]
    }

    val currentMatrix = remember(currentRoundState, selectedStep) {
        when (selectedStep) {
            "Start" -> currentRoundState.startState
            "SubBytes" -> currentRoundState.afterSubBytes
            "ShiftRows" -> currentRoundState.afterShiftRows
            "MixColumns" -> currentRoundState.afterMixColumns ?: currentRoundState.afterShiftRows
            else -> currentRoundState.afterAddRoundKey
        }
    }

    // Attacks & Mode states
    var ctrMsg1 by remember { mutableStateOf("Transfer $500") }
    var ctrMsg2 by remember { mutableStateOf("Attack At Dawn") }
    val ctrResult = remember(ctrMsg1, ctrMsg2) {
        AesEngine.simulateCtrNonceReuse(ctrMsg1, ctrMsg2)
    }

    var gcmTamperIdx by remember { mutableIntStateOf(2) }
    val gcmResult = remember(gcmTamperIdx) {
        AesEngine.testGcmTamper("Confidential Payloads", gcmTamperIdx)
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Mode Tabs Header
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                    .background(if (mainTab == 0) DsaPurple else (if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.05f)))
                    .border(1.dp, if (mainTab == 0) DsaPurple else dynamicColors.inputBorder, RoundedCornerShape(10.dp))
                    .clickable { mainTab = 0 }.padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "10-ROUND STATE TRACER", fontSize = 10.sp, fontWeight = FontWeight.Black, color = if (mainTab == 0) Color.White else SubtextGray)
            }
            Box(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                    .background(if (mainTab == 1) DsaPurple else (if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.05f)))
                    .border(1.dp, if (mainTab == 1) DsaPurple else dynamicColors.inputBorder, RoundedCornerShape(10.dp))
                    .clickable { mainTab = 1 }.padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "MODES & ATTACK DEMOS", fontSize = 10.sp, fontWeight = FontWeight.Black, color = if (mainTab == 1) Color.White else SubtextGray)
            }
        }

        if (mainTab == 0) {
            // State Matrix Tracer
            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                    .border(1.5.dp, DsaPurple.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "FIPS-197 AES-128 STATE (ROUND $selectedRound/10)", fontSize = 11.5.sp, fontWeight = FontWeight.Black, color = DsaPurple)
                        Text(text = selectedStep, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = amberColor)
                    }

                    // Round Slider (1..10)
                    Slider(
                        value = selectedRound.toFloat(),
                        onValueChange = { selectedRound = it.roundToInt().coerceIn(1, 10) },
                        valueRange = 1f..10f,
                        steps = 8,
                        colors = SliderDefaults.colors(thumbColor = DsaPurple, activeTrackColor = DsaPurple)
                    )

                    // Step Pills
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("Start", "SubBytes", "ShiftRows", "MixColumns", "AddRoundKey").forEach { stepName ->
                            val isChosen = selectedStep == stepName
                            val isOmitted = selectedRound == 10 && stepName == "MixColumns"
                            Box(
                                modifier = Modifier.weight(1f).clip(RoundedCornerShape(6.dp))
                                    .background(if (isChosen) DsaPurple else dynamicColors.inputBackground)
                                    .clickable(enabled = !isOmitted) { selectedStep = stepName }
                                    .padding(vertical = 5.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isOmitted) "(Skip)" else stepName.take(5),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isOmitted) Color.Gray else if (isChosen) Color.White else SubtextGray
                                )
                            }
                        }
                    }

                    // 4x4 Byte State Matrix Grid
                    Column(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(dynamicColors.inputBackground).padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp), horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        for (r in 0..3) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                for (c in 0..3) {
                                    val byteVal = currentMatrix[r][c]
                                    Box(
                                        modifier = Modifier.size(48.dp, 36.dp).clip(RoundedCornerShape(6.dp))
                                            .background(DsaPurple.copy(alpha = if (isDark) 0.25f else 0.12f))
                                            .border(1.dp, DsaPurple.copy(alpha = 0.5f), RoundedCornerShape(6.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "%02X".format(byteVal),
                                            fontSize = 13.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace,
                                            color = dynamicColors.textPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Text(
                        text = "FIPS-197 Appendix B Test Vector. Final Ciphertext: ${AesEngine.bytesToHex(trace.ciphertext)}",
                        fontSize = 9.5.sp, color = SubtextGray, fontFamily = FontFamily.Monospace
                    )
                }
            }
        } else {
            // Modes & Attacks Tab
            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                    .border(1.5.dp, DsaPurple.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "BLOCK CIPHER SECURITY EXPERIMENTS", fontSize = 12.sp, fontWeight = FontWeight.Black, color = DsaPurple)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("ECB", "CBC", "CTR", "GCM").forEach { mode ->
                            val isSel = selectedMode == mode
                            Box(
                                modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) AmberGold else dynamicColors.inputBackground)
                                    .clickable { selectedMode = mode }.padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = mode, fontSize = 11.sp, fontWeight = FontWeight.Black, color = if (isSel) Color.Black else dynamicColors.textPrimary)
                            }
                        }
                    }

                    when (selectedMode) {
                        "ECB" -> {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(text = "⚠️ ECB (Electronic Codebook) Flaw — Pattern Leakage", fontSize = 11.sp, fontWeight = FontWeight.Black, color = redColor)
                                Text(text = "Identical 16-byte plaintext blocks encrypt to identical ciphertext blocks. The visual silhouette of the input data remains fully visible in the ciphertext output.", fontSize = 10.5.sp, color = dynamicColors.textPrimary)
                            }
                        }
                        "CTR" -> {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(text = "⚡ CTR Nonce Reuse Attack Demo", fontSize = 11.sp, fontWeight = FontWeight.Black, color = redColor)
                                Text(text = "C1 ⊕ C2 = P1 ⊕ P2. When a nonce is reused, the keystream completely cancels out!", fontSize = 10.sp, color = SubtextGray)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "C1 ⊕ C2 Hex:", fontSize = 10.sp, color = SubtextGray)
                                    Text(text = AesEngine.bytesToHex(ctrResult.c1XorC2).take(18) + "...", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = amberColor)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "P1 ⊕ P2 Hex:", fontSize = 10.sp, color = SubtextGray)
                                    Text(text = AesEngine.bytesToHex(ctrResult.p1XorP2).take(18) + "...", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = greenColor)
                                }
                                Text(text = "Verified Match: Keystream cancelled out!", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = greenColor)
                            }
                        }
                        "GCM" -> {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(text = "🛡️ GCM Tamper & Authentication Verification", fontSize = 11.sp, fontWeight = FontWeight.Black, color = greenColor)
                                Text(text = "Tampering byte at index $gcmTamperIdx in transit triggers AEADBadTagException:", fontSize = 10.sp, color = SubtextGray)
                                Slider(
                                    value = gcmTamperIdx.toFloat(),
                                    onValueChange = { gcmTamperIdx = it.roundToInt() },
                                    valueRange = 0f..10f,
                                    colors = SliderDefaults.colors(thumbColor = greenColor, activeTrackColor = greenColor)
                                )
                                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(DsaRed.copy(alpha = 0.15f)).padding(8.dp)) {
                                    Text(text = gcmResult.errorMessage ?: "Valid", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = redColor)
                                }
                            }
                        }
                        else -> {
                            Text(text = "CBC: Cipher Block Chaining uses random IV. Vulnerable to padding oracle if MAC is not verified first.", fontSize = 10.5.sp, color = SubtextGray)
                        }
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────────────
// 4. Vigenère Polyalphabetic Cipher Visualizer (LINKED_LIST)
// ──────────────────────────────────────────────
@Composable
fun VigenereCipherVisualizer() {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val amberColor = if (isDark) AmberGold else Color(0xFFB45309)
    val tealColor = if (isDark) DsaTeal else Color(0xFF0D9488)
    val greenColor = if (isDark) DuolingoGreen else Color(0xFF15803D)

    var plaintext by remember { mutableStateOf("DEFEND THE EAST WALL OF THE CASTLE FROM THE ENEMY") }
    var key by remember { mutableStateOf("FORT") }
    var isDecryptMode by remember { mutableStateOf(false) }
    var activeSubTab by remember { mutableIntStateOf(0) } // 0: Alignment, 1: Kasiski Cracker

    val cleanPlain = plaintext.uppercase().filter { it in 'A'..'Z' }
    val cleanKey = key.uppercase().filter { it in 'A'..'Z' }.ifEmpty { "A" }

    val resultText = remember(cleanPlain, cleanKey, isDecryptMode) {
        cleanPlain.mapIndexed { i, pChar ->
            val kChar = cleanKey[i % cleanKey.length]
            val pVal = pChar - 'A'
            val kVal = kChar - 'A'
            val shifted = if (isDecryptMode) (pVal - kVal).mod(26) else (pVal + kVal).mod(26)
            ('A'.code + shifted).toChar()
        }.joinToString("")
    }

    val kasiskiResult = remember(resultText) {
        KasiskiEngine.kasiskiExamination(resultText, ngramLen = 3)
    }

    val ioc = remember(resultText) {
        KasiskiEngine.indexOfCoincidence(resultText)
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Mode Switch
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                    .background(if (activeSubTab == 0) DsaTeal else dynamicColors.inputBackground)
                    .clickable { activeSubTab = 0 }.padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "KEYSTREAM ALIGNMENT", fontSize = 10.5.sp, fontWeight = FontWeight.Black, color = if (activeSubTab == 0) Color.White else SubtextGray)
            }
            Box(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                    .background(if (activeSubTab == 1) DsaTeal else dynamicColors.inputBackground)
                    .clickable { activeSubTab = 1 }.padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "KASISKI & IOC CRACKER", fontSize = 10.5.sp, fontWeight = FontWeight.Black, color = if (activeSubTab == 1) Color.White else SubtextGray)
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, DsaTeal.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "PLAINTEXT", fontSize = 11.sp, fontWeight = FontWeight.Black, color = tealColor)
                BasicTextField(
                    value = plaintext, onValueChange = { if (it.length <= 60) plaintext = it },
                    textStyle = TextStyle(color = dynamicColors.textPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                    cursorBrush = SolidColor(DsaTeal), modifier = Modifier.fillMaxWidth()
                )
                Text(text = "SECRET KEYWORD", fontSize = 11.sp, fontWeight = FontWeight.Black, color = amberColor)
                BasicTextField(
                    value = key, onValueChange = { if (it.length <= 12) key = it },
                    textStyle = TextStyle(color = amberColor, fontSize = 15.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace),
                    cursorBrush = SolidColor(AmberGold), modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (activeSubTab == 0) {
            // Tabula Recta Keystream Mapping Strip
            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(dynamicColors.inputBackground)
                    .border(1.dp, dynamicColors.inputBorder, RoundedCornerShape(14.dp)).padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "REPEATED KEYSTREAM ALIGNMENT", fontSize = 10.sp, fontWeight = FontWeight.Black, color = SubtextGray)
                    Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        cleanPlain.take(25).forEachIndexed { i, pChar ->
                            val kChar = cleanKey[i % cleanKey.length]
                            val rChar = resultText.getOrElse(i) { '?' }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(dynamicColors.cardBackground).padding(horizontal = 7.dp, vertical = 5.dp)
                            ) {
                                Text(text = "$pChar", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary)
                                Text(text = "⊕", fontSize = 9.sp, color = SubtextGray)
                                Text(text = "$kChar", fontSize = 12.sp, fontWeight = FontWeight.Black, color = amberColor)
                                Text(text = "=", fontSize = 9.sp, color = SubtextGray)
                                Text(text = "$rChar", fontSize = 13.sp, fontWeight = FontWeight.Black, color = tealColor)
                            }
                        }
                    }
                }
            }
        } else {
            // Kasiski Examination Card
            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(dynamicColors.inputBackground)
                    .border(1.dp, dynamicColors.inputBorder, RoundedCornerShape(14.dp)).padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "🔍 KASISKI EXAMINATION & INDEX OF COINCIDENCE", fontSize = 11.sp, fontWeight = FontWeight.Black, color = amberColor)
                    Text(text = "Measured IoC: ${"%.4f".format(ioc)} (English ≈ 0.0667, Random ≈ 0.0385)", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = dynamicColors.textPrimary)

                    Text(text = "Repeated 3-grams found: ${kasiskiResult.repeatedNgrams.size}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SubtextGray)
                    kasiskiResult.repeatedNgrams.take(3).forEach { ng ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "3-gram \"${ng.ngram}\"", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = tealColor)
                            Text(text = "Distances: ${ng.distances.joinToString(", ")}", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = SubtextGray)
                        }
                    }

                    val candidateLen = kasiskiResult.mostLikelyKeyLength
                    Text(text = "Most likely keyword length by distance GCD factors: $candidateLen", fontSize = 10.5.sp, fontWeight = FontWeight.Black, color = greenColor)
                }
            }
        }
    }
}

// ──────────────────────────────────────────────
// 5. Diffie-Hellman Key Agreement Visualizer (BINARY_TREE)
// ──────────────────────────────────────────────
@Composable
fun DiffieHellmanVisualizer() {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val amberColor = if (isDark) AmberGold else Color(0xFFB45309)
    val tealColor = if (isDark) Color(0xFF00CD9C) else Color(0xFF047857)
    val greenColor = if (isDark) DuolingoGreen else Color(0xFF15803D)
    val redColor = if (isDark) DsaRed else Color(0xFFDC2626)

    var selectedDomainIdx by remember { mutableIntStateOf(0) }
    val domain = DhEngine.PRESET_DOMAINS[selectedDomainIdx]
    val p = domain.p
    val g = domain.g

    var aSecret by remember { mutableIntStateOf(6) }
    var bSecret by remember { mutableIntStateOf(15) }
    var mitmEnabled by remember { mutableStateOf(false) }
    var mallorySecret by remember { mutableIntStateOf(9) }

    val honestState = remember(p, g, aSecret, bSecret) {
        DhEngine.computeHonestExchange(p, g, aSecret.toLong(), bSecret.toLong())
    }

    val mitmState = remember(p, g, aSecret, bSecret, mallorySecret) {
        DhEngine.computeMitmExchange(p, g, aSecret.toLong(), bSecret.toLong(), mallorySecret.toLong())
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                .background(DsaOrange.copy(alpha = if (isDark) 0.15f else 0.08f))
                .border(1.dp, DsaOrange.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(text = "⚠️ TOY FINITE FIELD PARAMETERS — Prime p=$p, Base g=$g (Production uses 2048+ bit FFDH or Curve25519)", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = if (isDark) DsaOrange else Color(0xFFC2410C))
        }

        // Domain Selector & MITM Toggle
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                    .background(dynamicColors.inputBackground)
                    .border(1.dp, dynamicColors.inputBorder, RoundedCornerShape(10.dp))
                    .clickable { selectedDomainIdx = (selectedDomainIdx + 1) % DhEngine.PRESET_DOMAINS.size }
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = domain.name, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary)
            }

            Box(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                    .background(if (mitmEnabled) DsaRed else dynamicColors.inputBackground)
                    .clickable { mitmEnabled = !mitmEnabled }
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = if (mitmEnabled) "😈 MITM ACTIVE" else "🛡️ HONEST CHANNEL", fontSize = 10.sp, fontWeight = FontWeight.Black, color = if (mitmEnabled) Color.White else SubtextGray)
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, Color(0xFF00CD9C).copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Alice Slider
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "👩 Alice private a", fontSize = 11.sp, color = tealColor, fontWeight = FontWeight.Bold)
                    Text(text = "a = $aSecret", fontSize = 12.sp, fontWeight = FontWeight.Black, color = tealColor, fontFamily = FontFamily.Monospace)
                }
                Slider(
                    value = aSecret.toFloat(), onValueChange = { aSecret = it.roundToInt().coerceIn(2, (p - 2).toInt()) }, valueRange = 2f..(p - 2).toFloat(),
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF00CD9C), activeTrackColor = Color(0xFF00CD9C))
                )

                // Bob Slider
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "👨 Bob private b", fontSize = 11.sp, color = amberColor, fontWeight = FontWeight.Bold)
                    Text(text = "b = $bSecret", fontSize = 12.sp, fontWeight = FontWeight.Black, color = amberColor, fontFamily = FontFamily.Monospace)
                }
                Slider(
                    value = bSecret.toFloat(), onValueChange = { bSecret = it.roundToInt().coerceIn(2, (p - 2).toInt()) }, valueRange = 2f..(p - 2).toFloat(),
                    colors = SliderDefaults.colors(thumbColor = AmberGold, activeTrackColor = AmberGold)
                )

                if (mitmEnabled) {
                    // Mallory Injection
                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(DsaRed.copy(alpha = 0.15f)).padding(10.dp)) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "😈 Mallory Injects Public Key M = g^m mod p = ${mitmState.malloryPublic}", fontSize = 11.sp, fontWeight = FontWeight.Black, color = redColor)
                            Text(text = "Alice Secret (with Mallory): ${mitmState.aliceComputedSecret}  |  Bob Secret (with Mallory): ${mitmState.bobComputedSecret}", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = dynamicColors.textPrimary)
                            Text(text = "Notice Alice & Bob have completely different secrets! Mallory reads and alters all traffic.", fontSize = 9.5.sp, color = redColor)
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                            .background(DuolingoGreen.copy(alpha = 0.15f)).border(1.5.dp, DuolingoGreen, RoundedCornerShape(12.dp)).padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(text = "✨ MATCHED SHARED SECRET", fontSize = 11.sp, fontWeight = FontWeight.Black, color = greenColor)
                            Text(text = "K = ${honestState.aliceSharedSecret}", fontSize = 22.sp, fontWeight = FontWeight.Black, color = dynamicColors.textPrimary, fontFamily = FontFamily.Monospace)
                            Text(text = "Alice: B^a mod p = ${honestState.aliceSharedSecret}  |  Bob: A^b mod p = ${honestState.bobSharedSecret}", fontSize = 10.sp, color = SubtextGray, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────────────
// 6. RSA Public-Key & Digital Signature Visualizer (AVL_TREE)
// ──────────────────────────────────────────────
@Composable
fun RSAPublicKeyVisualizer() {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val amberColor = if (isDark) AmberGold else Color(0xFFB45309)
    val tealColor = if (isDark) DsaTeal else Color(0xFF0D9488)
    val greenColor = if (isDark) DuolingoGreen else Color(0xFF15803D)
    val redColor = if (isDark) DsaRed else Color(0xFFDC2626)

    var pIndex by remember { mutableIntStateOf(10) } // 41
    var qIndex by remember { mutableIntStateOf(13) } // 53
    val pVal = RsaEngine.RECOMMENDED_PRIMES[pIndex].toLong()
    val qVal = RsaEngine.RECOMMENDED_PRIMES[qIndex].toLong()

    val rsaKey = remember(pVal, qVal) {
        RsaEngine.generateKeys(pVal, qVal, preferredE = 17)
    }

    var messageNum by remember { mutableIntStateOf(65) } // 'A'
    var activeTab by remember { mutableIntStateOf(0) } // 0: Encrypt/Decrypt, 1: ModExp Trace, 2: Malleability Attack

    val modExpTrace = remember(messageNum, rsaKey) {
        RsaEngine.traceModExp(BigInteger.valueOf(messageNum.toLong()), rsaKey.e, rsaKey.n)
    }

    val malleability = remember(rsaKey) {
        RsaEngine.testMalleability(15, 20, rsaKey.e, rsaKey.d, rsaKey.n)
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                .background(DsaOrange.copy(alpha = if (isDark) 0.15f else 0.08f))
                .border(1.dp, DsaOrange.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(text = "⚠️ TOY RSA KEYGEN (p=$pVal, q=$qVal, n=${rsaKey.n}) — For educational inspection. Production requires ≥2048-bit primes.", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = if (isDark) DsaOrange else Color(0xFFC2410C))
        }

        // Tab Selector
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("ENCRYPT/DECRYPT", "MODEXP TRACE", "HOMOMORPHIC ATTACK").forEachIndexed { idx, label ->
                val sel = activeTab == idx
                Box(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                        .background(if (sel) DsaTeal else dynamicColors.inputBackground)
                        .clickable { activeTab = idx }.padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.Black, color = if (sel) Color.White else SubtextGray)
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, DsaTeal.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "RSA KEYPAIR PARAMETERS", fontSize = 12.sp, fontWeight = FontWeight.Black, color = tealColor)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Modulus n = p×q: ${rsaKey.n}", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = dynamicColors.textPrimary)
                    Text(text = "φ(n): ${rsaKey.phi}", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = SubtextGray)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Public Exponent e: ${rsaKey.e}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = amberColor, fontFamily = FontFamily.Monospace)
                    Text(text = "Private Exponent d: ${rsaKey.d}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = greenColor, fontFamily = FontFamily.Monospace)
                }
            }
        }

        when (activeTab) {
            0 -> {
                // Encryption & Decryption
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                        .border(1.5.dp, InputBorder, RoundedCornerShape(16.dp)).padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "MESSAGE SYMBOL (m)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = tealColor)
                            Text(text = "$messageNum ('${messageNum.toChar()}')", fontSize = 13.sp, fontWeight = FontWeight.Black, color = dynamicColors.textPrimary, fontFamily = FontFamily.Monospace)
                        }
                        Slider(
                            value = messageNum.toFloat(), onValueChange = { messageNum = it.roundToInt() }, valueRange = 2f..100f,
                            colors = SliderDefaults.colors(thumbColor = DsaTeal, activeTrackColor = DsaTeal)
                        )

                        val ciphertext = modExpTrace.result
                        val decrypted = BigInteger.valueOf(ciphertext.toLong()).modPow(rsaKey.d, rsaKey.n)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(AmberGold.copy(alpha = 0.15f)).padding(10.dp)) {
                                Column {
                                    Text(text = "CIPHERTEXT (c)", fontSize = 10.sp, color = amberColor, fontWeight = FontWeight.Bold)
                                    Text(text = "$ciphertext", fontSize = 15.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = dynamicColors.textPrimary)
                                    Text(text = "c = m^e mod n", fontSize = 9.sp, color = SubtextGray)
                                }
                            }
                            Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(DuolingoGreen.copy(alpha = 0.15f)).padding(10.dp)) {
                                Column {
                                    Text(text = "DECRYPTED (m)", fontSize = 10.sp, color = greenColor, fontWeight = FontWeight.Bold)
                                    Text(text = "$decrypted", fontSize = 15.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = dynamicColors.textPrimary)
                                    Text(text = "m = c^d mod n", fontSize = 9.sp, color = SubtextGray)
                                }
                            }
                        }
                    }
                }
            }
            1 -> {
                // Square-and-Multiply Trace
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(dynamicColors.inputBackground)
                        .border(1.dp, dynamicColors.inputBorder, RoundedCornerShape(14.dp)).padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = "⚡ SQUARE-AND-MULTIPLY MODEXP TRACE (${rsaKey.e.toString(2)}₂)", fontSize = 10.5.sp, fontWeight = FontWeight.Black, color = amberColor)
                        modExpTrace.steps.take(6).forEach { st ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Bit ${st.bitIndex} (${st.bitValue}):", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = SubtextGray)
                                Text(text = "Square -> ${st.afterSquare} ${if (st.afterMultiply != null) "| Mult -> ${st.afterMultiply}" else ""}", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = dynamicColors.textPrimary)
                            }
                        }
                    }
                }
            }
            else -> {
                // Textbook RSA Malleability Demonstration
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(DsaRed.copy(alpha = 0.12f))
                        .border(1.dp, DsaRed.copy(alpha = 0.5f), RoundedCornerShape(14.dp)).padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = "⚠️ HOMOMORPHIC MALLEABILITY ATTACK", fontSize = 11.sp, fontWeight = FontWeight.Black, color = redColor)
                        Text(text = "In textbook RSA without OAEP padding, multiplying two ciphertexts (c1 * c2 mod n) produces a ciphertext that decrypts directly to (m1 * m2 mod n)!", fontSize = 10.sp, color = dynamicColors.textPrimary)
                        Text(text = "c1=${malleability.c1}, c2=${malleability.c2}  =>  cProduct=${malleability.cProductModN}", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = amberColor)
                        Text(text = "Decrypted Product: ${malleability.decryptedProduct} == Expected (m1*m2): ${malleability.expectedProductModN}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = greenColor)
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────────────
// 7. ECC Elliptic Curve Visualizer (TRIE)
// ──────────────────────────────────────────────
@Composable
fun ECCPointVisualizer() {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val cyanColor = if (isDark) Color.Cyan else Color(0xFF0284C7)
    val amberColor = if (isDark) AmberGold else Color(0xFFB45309)

    var kScalar by remember { mutableIntStateOf(3) }

    // Connect real ArenaCryptoEngine.ToyF17Curve: y² ≡ x³ + 2x + 2 (mod 17), G = (5, 1)
    val G = ArenaCryptoEngine.ToyF17Curve.G
    val computedPoint = remember(kScalar) {
        ArenaCryptoEngine.ToyF17Curve.multiply(kScalar, G)
    }

    // Step-by-step additions from 1G to kG
    val stepPoints = remember(kScalar) {
        val list = mutableListOf<Pair<Int, String>>()
        for (i in 1..kScalar) {
            val pt = ArenaCryptoEngine.ToyF17Curve.multiply(i, G)
            list.add(i to pt.toString())
        }
        list
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                .background(cyanColor.copy(alpha = if (isDark) 0.10f else 0.08f)).border(1.dp, cyanColor.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "📐 RIGOROUS FINITE FIELD F₁₇ — Curve: y² ≡ x³ + 2x + 2 (mod 17). Base generator G = (5, 1). Order = 19 points.",
                fontSize = 10.5.sp, color = cyanColor, lineHeight = 14.sp, fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, cyanColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "SCALAR MULTIPLIER (k)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = cyanColor)
                    Text(text = "k = $kScalar  (Q = $kScalar · G)", fontSize = 14.sp, fontWeight = FontWeight.Black, color = cyanColor, fontFamily = FontFamily.Monospace)
                }

                Slider(
                    value = kScalar.toFloat(),
                    onValueChange = { kScalar = it.roundToInt().coerceIn(1, 19) },
                    valueRange = 1f..19f,
                    steps = 17,
                    colors = SliderDefaults.colors(thumbColor = cyanColor, activeTrackColor = cyanColor)
                )

                // Result Box
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .background(cyanColor.copy(alpha = 0.15f)).border(1.dp, cyanColor, RoundedCornerShape(12.dp)).padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(text = "POINT ON CURVE Q = $kScalar · (5, 1)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = cyanColor)
                        Text(text = "$computedPoint", fontSize = 24.sp, fontWeight = FontWeight.Black, color = dynamicColors.textPrimary, fontFamily = FontFamily.Monospace)
                        Text(text = if (kScalar == 19) "Point at Infinity 𝒪 (Identity Element)" else "Group addition via chord-and-tangent slope m in F₁₇", fontSize = 10.sp, color = SubtextGray)
                    }
                }

                // Intermediate Scalar Multiplications
                Text(text = "GENERATED SUBGROUP SEQUENCE:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SubtextGray)
                Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    stepPoints.forEach { (stepK, ptStr) ->
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(6.dp))
                                .background(if (stepK == kScalar) cyanColor else dynamicColors.inputBackground)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${stepK}G: $ptStr",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (stepK == kScalar) Color.Black else dynamicColors.textPrimary,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────────────
// 8. SHA-256 Hash Construction & Avalanche Visualizer (GRAPH)
// ──────────────────────────────────────────────
@Composable
fun SHA256Visualizer() {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val amberColor = if (isDark) AmberGold else Color(0xFFB45309)
    val purpleColor = if (isDark) Color(0xFFA05AFF) else Color(0xFF7E22CE)
    val greenColor = if (isDark) DuolingoGreen else Color(0xFF15803D)

    var messageA by remember { mutableStateOf("Integrity Check A") }
    var messageB by remember { mutableStateOf("Integrity Check B") }
    var selectedRoundStep by remember { mutableIntStateOf(0) } // 0..63

    val traceA = remember(messageA) {
        Sha256Engine.hashWithTrace(messageA.toByteArray(Charsets.UTF_8))
    }

    val traceB = remember(messageB) {
        Sha256Engine.hashWithTrace(messageB.toByteArray(Charsets.UTF_8))
    }

    val avalanche = remember(traceA.finalHash, traceB.finalHash) {
        Sha256Engine.computeAvalanche(traceA.finalHash, traceB.finalHash)
    }

    val currentBlockTrace = traceA.blocks.firstOrNull()
    val activeRoundState = currentBlockTrace?.steps?.getOrElse(selectedRoundStep) { currentBlockTrace.steps.first() }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, purpleColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "INPUT MESSAGE", fontSize = 11.sp, fontWeight = FontWeight.Black, color = purpleColor)
                BasicTextField(
                    value = messageA, onValueChange = { messageA = it },
                    textStyle = TextStyle(color = dynamicColors.textPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                    cursorBrush = SolidColor(purpleColor), modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Padded 512-bit block length: ${traceA.paddedData.size} bytes (${traceA.blocks.size} blocks)",
                    fontSize = 10.sp, color = SubtextGray
                )
            }
        }

        // 64-Round Intermediate Register Inspector
        if (activeRoundState != null) {
            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                    .border(1.5.dp, purpleColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "COMPRESSION ROUND STEP: ${activeRoundState.step}/63", fontSize = 11.sp, fontWeight = FontWeight.Black, color = purpleColor)
                        Text(text = "W[t]: 0x%08X".format(activeRoundState.w), fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = amberColor)
                    }

                    Slider(
                        value = selectedRoundStep.toFloat(),
                        onValueChange = { selectedRoundStep = it.roundToInt().coerceIn(0, 63) },
                        valueRange = 0f..63f,
                        steps = 62,
                        colors = SliderDefaults.colors(thumbColor = purpleColor, activeTrackColor = purpleColor)
                    )

                    // Real Working Registers A..H
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        listOf(
                            "A" to activeRoundState.a, "B" to activeRoundState.b,
                            "C" to activeRoundState.c, "D" to activeRoundState.d,
                            "E" to activeRoundState.e, "F" to activeRoundState.f,
                            "G" to activeRoundState.g, "H" to activeRoundState.h
                        ).forEach { (reg, valInt) ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = reg, fontSize = 10.sp, fontWeight = FontWeight.Black, color = purpleColor)
                                Text(text = "%04X".format((valInt ushr 16) and 0xFFFF), fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = dynamicColors.textPrimary)
                            }
                        }
                    }

                    Text(
                        text = "Final 256-bit Digest: ${traceA.finalHex}",
                        fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = purpleColor
                    )
                }
            }
        }

        // Bit-Level Avalanche Analysis Card
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(dynamicColors.inputBackground)
                .border(1.dp, dynamicColors.inputBorder, RoundedCornerShape(14.dp)).padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "⚡ BIT-LEVEL HAMMING AVALANCHE ANALYSIS", fontSize = 11.sp, fontWeight = FontWeight.Black, color = amberColor)
                Text(
                    text = "Single Character Difference: '${messageA.lastOrNull()}' vs '${messageB.lastOrNull()}'",
                    fontSize = 10.sp, color = SubtextGray
                )
                Text(
                    text = "Bits Flipped: ${avalanche.flippedBitsCount} / 256 (${"%.1f".format(avalanche.percentage)}%)",
                    fontSize = 14.sp, fontWeight = FontWeight.Black, color = if (avalanche.percentage in 40f..60f) greenColor else amberColor
                )
                Text(
                    text = "Strict Avalanche Criterion (SAC): High cryptographic quality requires flipping ~50% of output bits.",
                    fontSize = 10.sp, color = SubtextGray
                )
            }
        }
    }
}

// ──────────────────────────────────────────────
// 9. Real HMAC & Password Salting Visualizer (SORTING)
// ──────────────────────────────────────────────
@Composable
fun HMACCollisionVisualizer() {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val amberColor = if (isDark) AmberGold else Color(0xFFB45309)
    val redColor = if (isDark) DsaRed else Color(0xFFDC2626)
    val greenColor = if (isDark) DuolingoGreen else Color(0xFF15803D)

    var keyInput by remember { mutableStateOf("SecretPasswordKey") }
    var msgInput by remember { mutableStateOf("Transfer $500 to Bob") }
    var activeTab by remember { mutableIntStateOf(0) } // 0: HMAC Step Trace, 1: Birthday Collision, 2: PBKDF2 Benchmark

    val hmacTrace = remember(keyInput, msgInput) {
        HmacPasswordEngine.computeHmacTrace(keyInput.toByteArray(Charsets.UTF_8), msgInput.toByteArray(Charsets.UTF_8))
    }

    var collisionBits by remember { mutableIntStateOf(12) }
    val collisionResult = remember(collisionBits) {
        HmacPasswordEngine.findTruncatedCollision(collisionBits)
    }

    var pbkdf2Iterations by remember { mutableIntStateOf(10000) }
    val pbkdf2Result = remember(pbkdf2Iterations) {
        HmacPasswordEngine.benchmarkPbkdf2("UserPassword123!", ByteArray(16) { 0x11.toByte() }, pbkdf2Iterations)
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Tab Selector
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("HMAC PAD TRACE", "BIRTHDAY COLLISION", "PBKDF2 BENCHMARK").forEachIndexed { idx, label ->
                val sel = activeTab == idx
                Box(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                        .background(if (sel) DsaRed else dynamicColors.inputBackground)
                        .clickable { activeTab = idx }.padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = label, fontSize = 8.5.sp, fontWeight = FontWeight.Black, color = if (sel) Color.White else SubtextGray)
                }
            }
        }

        when (activeTab) {
            0 -> {
                // RFC 2104 HMAC Step Trace
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                        .border(1.5.dp, redColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "RFC 2104 HMAC-SHA256 NESTED HASH", fontSize = 12.sp, fontWeight = FontWeight.Black, color = redColor)
                        Text(text = "H((K ⊕ opad) ∥ H((K ⊕ ipad) ∥ m))", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = amberColor)

                        Text(text = "Inner Pad (K ⊕ 0x36): 64 bytes", fontSize = 10.sp, color = SubtextGray)
                        Text(text = "Inner Hash: ${hmacTrace.innerHashHex.take(24)}...", fontSize = 10.5.sp, fontFamily = FontFamily.Monospace, color = dynamicColors.textPrimary)

                        Text(text = "Outer Pad (K ⊕ 0x5C): 64 bytes", fontSize = 10.sp, color = SubtextGray)
                        Text(text = "Final MAC Tag: ${hmacTrace.finalMacHex}", fontSize = 11.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = redColor)

                        Text(text = "🛡️ Prevents Length Extension Attacks: Even if an attacker knows H(m), they cannot append data without knowing K.", fontSize = 9.5.sp, color = greenColor)
                    }
                }
            }
            1 -> {
                // Birthday Collision Simulator
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                        .border(1.5.dp, redColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "BIRTHDAY BOUND COLLISION SEARCH ($collisionBits BITS)", fontSize = 11.5.sp, fontWeight = FontWeight.Black, color = redColor)
                        Slider(
                            value = collisionBits.toFloat(),
                            onValueChange = { collisionBits = it.roundToInt().coerceIn(8, 16) },
                            valueRange = 8f..16f,
                            steps = 7,
                            colors = SliderDefaults.colors(thumbColor = DsaRed, activeTrackColor = DsaRed)
                        )

                        Text(text = "Theoretical Bound ≈ 1.25 × 2^(k/2) = ${collisionResult.expectedSteps} trials", fontSize = 10.5.sp, color = SubtextGray)
                        Text(text = "Actual steps until collision: ${collisionResult.actualSteps}", fontSize = 13.sp, fontWeight = FontWeight.Black, color = amberColor)
                        Text(text = "Input 1: \"${collisionResult.input1}\"  !=  Input 2: \"${collisionResult.input2}\"", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = dynamicColors.textPrimary)
                        Text(text = "Matched Truncated Hash: 0x${collisionResult.matchedHashTruncatedHex}", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = greenColor)
                    }
                }
            }
            else -> {
                // PBKDF2 On-Device Benchmark
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                        .border(1.5.dp, redColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "PBKDF2-HMAC-SHA256 BENCHMARK", fontSize = 11.5.sp, fontWeight = FontWeight.Black, color = redColor)
                        Text(text = "Iterations: $pbkdf2Iterations", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = amberColor)
                        Slider(
                            value = pbkdf2Iterations.toFloat(),
                            onValueChange = { pbkdf2Iterations = it.roundToInt() },
                            valueRange = 1000f..50000f,
                            steps = 4,
                            colors = SliderDefaults.colors(thumbColor = DsaRed, activeTrackColor = DsaRed)
                        )

                        Text(text = "Device Computation Time: ${pbkdf2Result.elapsedMs} ms", fontSize = 15.sp, fontWeight = FontWeight.Black, color = greenColor)
                        Text(text = "Estimated Cost for 10M offline guesses: ${"%.1f".format(pbkdf2Result.crackingCostEstimateSec)} seconds", fontSize = 10.sp, color = SubtextGray)
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────────────
// 10. TLS 1.3 Handshake & Protocol Lab (STEP_RECORDER)
// ──────────────────────────────────────────────
@Composable
fun TLS13HandshakeVisualizer() {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val amberColor = if (isDark) AmberGold else Color(0xFFB45309)
    val tealColor = if (isDark) DsaTeal else Color(0xFF0D9488)
    val orangeColor = if (isDark) Color(0xFFFF9600) else Color(0xFFEA580C)
    val greenColor = if (isDark) DuolingoGreen else Color(0xFF15803D)
    val redColor = if (isDark) DsaRed else Color(0xFFDC2626)

    var flightIndex by remember { mutableIntStateOf(0) }
    var activeLabTab by remember { mutableIntStateOf(0) } // 0: Swim-Lane & Inspector, 1: Record Tamper, 2: X.509 Chain
    val packets = remember { TlsEngine.getHandshakePackets() }
    val currentPacket = packets.getOrElse(flightIndex) { packets.first() }

    var tamperByte by remember { mutableIntStateOf(5) }
    val tamperResult = remember(tamperByte) {
        TlsEngine.testRecordTamper(tamperByteIndex = tamperByte)
    }

    val chain = remember {
        listOf(
            TlsEngine.X509Cert("CN=codeingo.simats.com", "CN=Simats Secure CA G2", false, 2024, 2028, true),
            TlsEngine.X509Cert("CN=Simats Secure CA G2", "CN=GlobalTrust Root CA", false, 2020, 2030, true),
            TlsEngine.X509Cert("CN=GlobalTrust Root CA", "CN=GlobalTrust Root CA", true, 2015, 2035, true)
        )
    }
    val chainValidation = remember {
        TlsEngine.validateCertificateChain(chain, "codeingo.simats.com", 2026)
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Tab Selector
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("PACKET INSPECTOR", "RECORD TAMPER", "X.509 CERT CHAIN").forEachIndexed { idx, label ->
                val sel = activeLabTab == idx
                Box(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                        .background(if (sel) orangeColor else dynamicColors.inputBackground)
                        .clickable { activeLabTab = idx }.padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = label, fontSize = 8.5.sp, fontWeight = FontWeight.Black, color = if (sel) Color.White else SubtextGray)
                }
            }
        }

        when (activeLabTab) {
            0 -> {
                // Swim Lane & Packet Inspector
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                        .border(1.5.dp, orangeColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "PACKET ${flightIndex + 1} / ${packets.size}: ${currentPacket.messageType}", fontSize = 12.sp, fontWeight = FontWeight.Black, color = orangeColor)
                            Text(text = currentPacket.sender, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = tealColor)
                        }

                        Text(text = currentPacket.description, fontSize = 10.5.sp, color = SubtextGray)

                        // Packet Headers & Fields Table
                        Column(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(dynamicColors.inputBackground).padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            currentPacket.fields.forEach { (k, v) ->
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "$k:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SubtextGray)
                                    Text(text = v, fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = dynamicColors.textPrimary)
                                }
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(dynamicColors.inputBackground)
                                    .clickable { if (flightIndex > 0) flightIndex-- }.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "◀ PREV PACKET", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary)
                            }
                            Box(
                                modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(orangeColor)
                                    .clickable { if (flightIndex < packets.size - 1) flightIndex++ else flightIndex = 0 }.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "NEXT PACKET ▶", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
            1 -> {
                // Interactive Record Tamper Demo
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                        .border(1.5.dp, redColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "INTERACTIVE RECORD TAMPER DEMO", fontSize = 11.5.sp, fontWeight = FontWeight.Black, color = redColor)
                        Text(text = "Slide to flip 1 bit in the encrypted TLS 1.3 record body:", fontSize = 10.sp, color = SubtextGray)
                        Slider(
                            value = tamperByte.toFloat(),
                            onValueChange = { tamperByte = it.roundToInt() },
                            valueRange = 0f..10f,
                            colors = SliderDefaults.colors(thumbColor = DsaRed, activeTrackColor = DsaRed)
                        )

                        Text(text = "Original Record: ${tamperResult.originalRecordHex.take(24)}...", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = SubtextGray)
                        Text(text = "Tampered Record: ${tamperResult.tamperedRecordHex.take(24)}...", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = redColor)

                        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(DsaRed.copy(alpha = 0.15f)).padding(8.dp)) {
                            Text(text = tamperResult.errorDetail ?: "Verified", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = redColor)
                        }
                    }
                }
            }
            else -> {
                // X.509 Certificate Chain Validator
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                        .border(1.5.dp, greenColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "X.509 PKI TRUST CHAIN VALIDATOR", fontSize = 11.5.sp, fontWeight = FontWeight.Black, color = greenColor)
                        chain.forEachIndexed { i, cert ->
                            Column(
                                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(dynamicColors.inputBackground).padding(8.dp)
                            ) {
                                Text(text = "${i + 1}. ${cert.subject}", fontSize = 11.sp, fontWeight = FontWeight.Black, color = dynamicColors.textPrimary)
                                Text(text = "Issuer: ${cert.issuer}  |  Validity: ${cert.validFromYear}-${cert.validToYear}", fontSize = 9.5.sp, color = SubtextGray)
                            }
                        }

                        Text(
                            text = if (chainValidation.isValid) "✅ Certificate Chain Validated to Trusted Root Anchor" else "❌ Chain Rejected: ${chainValidation.failureReason}",
                            fontSize = 11.sp, fontWeight = FontWeight.Black, color = if (chainValidation.isValid) greenColor else redColor
                        )
                    }
                }
            }
        }
    }
}
