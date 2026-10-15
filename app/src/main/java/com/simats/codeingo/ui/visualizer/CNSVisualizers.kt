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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
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
import com.simats.codeingo.ui.theme.pressScale
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin

// ══════════════════════════════════════════════════════════════════
// 🛡️ CNS VISUALIZER ENGINES (10 Flagship Cryptography & NetSec Labs)
// Full Light / Dark Theme Adaptive Compatibility
// ══════════════════════════════════════════════════════════════════

// ──────────────────────────────────────────────
// 1. Caesar & Shift Cipher Visualizer
// ──────────────────────────────────────────────
@Composable
fun CaesarCipherVisualizer() {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val amberColor = if (isDark) AmberGold else Color(0xFFB45309)
    val greenColor = if (isDark) DsaGreen else Color(0xFF15803D)
    val cyanColor = if (isDark) Color.Cyan else Color(0xFF0284C7)

    var textInput by remember { mutableStateOf("CRYPTO LAB") }
    var shift by remember { mutableIntStateOf(3) }
    var isDecryptMode by remember { mutableStateOf(false) }
    var showBruteForce by remember { mutableStateOf(false) }
    val clipboard = LocalClipboardManager.current
    var isCopied by remember { mutableStateOf(false) }

    val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
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

    val letterCounts = remember(outputText) {
        val upper = outputText.uppercase()
        val counts = mutableMapOf<Char, Int>()
        for (c in alphabet) counts[c] = 0
        for (c in upper) {
            if (c in 'A'..'Z') counts[c] = (counts[c] ?: 0) + 1
        }
        counts
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
                text = "⚠️ HISTORICAL TOY CIPHER — Key space = 26. Insecure: trivial to break via exhaustive search or single-letter frequency analysis.",
                fontSize = 10.5.sp, color = amberColor, lineHeight = 14.sp, fontWeight = FontWeight.Bold
            )
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
                Text(text = "ENCRYPT (P + K)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = if (!isDecryptMode) Color.Black else SubtextGray)
            }
            Box(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                    .background(if (isDecryptMode) AmberGold else (if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.05f)))
                    .border(1.dp, if (isDecryptMode) AmberGold else dynamicColors.inputBorder, RoundedCornerShape(10.dp))
                    .clickable { isDecryptMode = true }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "DECRYPT (C - K)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = if (isDecryptMode) Color.Black else SubtextGray)
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
                    onValueChange = { if (it.length <= 25) textInput = it },
                    textStyle = TextStyle(
                        color = dynamicColors.textPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    ),
                    cursorBrush = SolidColor(if (isDecryptMode) AmberGold else DsaGreen),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = if (isDecryptMode) "Formula: P = (C - K) mod 26" else "Formula: C = (P + K) mod 26",
                    fontSize = 11.sp, color = SubtextGray
                )
            }
        }

        // Shift Key Slider Card
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, InputBorder, RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "SHIFT KEY (K)", fontSize = 12.sp, fontWeight = FontWeight.Black, color = amberColor)
                    Text(text = "+$shift MOD 26", fontSize = 14.sp, fontWeight = FontWeight.Black, color = amberColor, fontFamily = FontFamily.Monospace)
                }

                Slider(
                    value = shift.toFloat(),
                    onValueChange = { shift = it.roundToInt() },
                    valueRange = 0f..25f,
                    steps = 24,
                    colors = SliderDefaults.colors(thumbColor = AmberGold, activeTrackColor = AmberGold, inactiveTrackColor = AmberGold.copy(alpha = 0.2f))
                )

                // Alphabet Shift Ribbon
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    alphabet.forEachIndexed { i, ch ->
                        val targetChar = alphabet[(i + effectiveShift) % 26]
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clip(RoundedCornerShape(8.dp))
                                .background(if (i == shift) AmberGold.copy(alpha = if (isDark) 0.25f else 0.18f) else (if (isDark) Color.White.copy(alpha = 0.04f) else Color.Black.copy(alpha = 0.04f)))
                                .border(1.dp, if (i == shift) AmberGold else Color.Transparent, RoundedCornerShape(8.dp))
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            Text(text = "$ch", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary)
                            Text(text = "↓", fontSize = 9.sp, color = amberColor)
                            Text(text = "$targetChar", fontSize = 11.sp, fontWeight = FontWeight.Black, color = if (isDecryptMode) amberColor else greenColor)
                        }
                    }
                }
            }
        }

        // Output Card
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, (if (isDecryptMode) AmberGold else DsaGreen).copy(alpha = 0.6f), RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = if (isDecryptMode) "DECRYPTED PLAINTEXT" else "CIPHERTEXT (OUTPUT)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = if (isDecryptMode) amberColor else greenColor)
                    Row(
                        modifier = Modifier.clip(RoundedCornerShape(8.dp)).background((if (isDecryptMode) AmberGold else DsaGreen).copy(alpha = if (isDark) 0.15f else 0.10f))
                            .clickable {
                                clipboard.setText(AnnotatedString(outputText))
                                isCopied = true
                            }.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy, contentDescription = null, tint = if (isDecryptMode) amberColor else greenColor, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (isCopied) "Copied!" else "Copy", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isDecryptMode) amberColor else greenColor)
                    }
                }

                Text(
                    text = outputText.ifEmpty { "(empty)" },
                    fontSize = 20.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace,
                    color = if (isDecryptMode) amberColor else greenColor, letterSpacing = 2.sp
                )
            }
        }

        // Frequency Bar Chart Card
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(dynamicColors.inputBackground)
                .border(1.dp, dynamicColors.inputBorder, RoundedCornerShape(14.dp)).padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "📊 OUTPUT FREQUENCY ANALYSIS", fontSize = 10.sp, fontWeight = FontWeight.Black, color = SubtextGray)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    val maxVal = letterCounts.values.maxOrNull()?.coerceAtLeast(1) ?: 1
                    listOf('E', 'T', 'A', 'O', 'I', 'N', 'S', 'H', 'R').forEach { letter ->
                        val count = letterCounts[letter] ?: 0
                        val barHeight = (count.toFloat() / maxVal * 32).coerceIn(4f, 32f)
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Box(modifier = Modifier.width(16.dp).height(barHeight.dp).clip(RoundedCornerShape(4.dp)).background(if (count > 0) DsaGreen else SubtextGray.copy(alpha = 0.2f)))
                            Text(text = "$letter", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary)
                        }
                    }
                }
                Text(text = "Frequency distributions are shifted without alteration in monoalphabetic ciphers.", fontSize = 10.sp, color = SubtextGray)
            }
        }

        // Brute Force Tab
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(dynamicColors.inputBackground)
                .border(1.dp, dynamicColors.inputBorder, RoundedCornerShape(12.dp)).clickable { showBruteForce = !showBruteForce }.padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "🔓 BRUTE-FORCE ALL 26 SHIFTS", fontSize = 11.sp, fontWeight = FontWeight.Black, color = amberColor)
                    Text(text = if (showBruteForce) "Hide ▲" else "View All 26 ▼", fontSize = 11.sp, color = SubtextGray)
                }
                if (showBruteForce) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        (0..25).take(10).forEach { k ->
                            val shifted = textInput.map { c ->
                                if (c.isLetter()) {
                                    val base = if (c.isUpperCase()) 'A' else 'a'
                                    ((c.code - base.code - k).mod(26) + base.code).toChar()
                                } else c
                            }.joinToString("")
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "K = %02d:".format(k), fontSize = 10.sp, color = amberColor, fontFamily = FontFamily.Monospace)
                                Text(text = shifted.take(20), fontSize = 10.sp, color = if (k == shift) cyanColor else dynamicColors.textPrimary, fontFamily = FontFamily.Monospace)
                            }
                        }
                        Text(text = "...and 16 more shifts. Exhaustive search takes milliseconds.", fontSize = 9.5.sp, color = SubtextGray)
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────────────
// 2. DES Feistel Network Visualizer
// ──────────────────────────────────────────────
@Composable
fun DESFeistelVisualizer() {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val amberColor = if (isDark) AmberGold else Color(0xFFB45309)
    val redColor = if (isDark) DsaRed else Color(0xFFDC2626)
    val tealColor = if (isDark) DsaTeal else Color(0xFF0D9488)

    var round by remember { mutableIntStateOf(1) }
    var leftHalf by remember { mutableStateOf("0x3554474B") }
    var rightHalf by remember { mutableStateOf("0x6E616C69") }

    fun stepRound() {
        if (round < 16) {
            val oldLeft = leftHalf
            val oldRight = rightHalf
            val subkey = (round * 0x1F2E3D4C).toInt()
            val fOut = "0x" + ((oldRight.removePrefix("0x").toLong(16) * 31 xor subkey.toLong()) and 0xFFFFFFFFL).toString(16).uppercase().padStart(8, '0')
            val newRight = "0x" + (oldLeft.removePrefix("0x").toLong(16) xor fOut.removePrefix("0x").toLong(16)).toString(16).uppercase().padStart(8, '0')
            leftHalf = oldRight
            rightHalf = newRight
            round += 1
        }
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
                text = "⚠️ DEPRECATED — 56-bit DES key space (2^56) cracked in 1998 by EFF Deep Crack ($250k, 56 hours). Replaced by AES-128/256.",
                fontSize = 10.5.sp, color = redColor, lineHeight = 14.sp, fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, DsaBlue.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "FEISTEL ROUND $round / 16", fontSize = 12.sp, fontWeight = FontWeight.Black, color = DsaBlue)
                    Text(text = "64-BIT BLOCK", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SubtextGray)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(DsaBlue.copy(alpha = if (isDark) 0.15f else 0.08f)).border(1.5.dp, DsaBlue, RoundedCornerShape(12.dp)).padding(12.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "L[$round] (32-bit)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = DsaBlue)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = leftHalf, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary, fontFamily = FontFamily.Monospace)
                        }
                    }

                    Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(DsaPurple.copy(alpha = if (isDark) 0.15f else 0.08f)).border(1.5.dp, DsaPurple, RoundedCornerShape(12.dp)).padding(12.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "R[$round] (32-bit)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = DsaPurple)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = rightHalf, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary, fontFamily = FontFamily.Monospace)
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(dynamicColors.inputBackground).border(1.dp, dynamicColors.inputBorder, RoundedCornerShape(12.dp)).padding(12.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "🔑 Round Subkey K[$round]:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SubtextGray)
                            Text(text = "0x" + ((round * 0x1F2E3D4C).toLong() and 0xFFFFFFFFFFL).toString(16).uppercase().take(12), fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = amberColor)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "⚙️ Non-linear F(R, K):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SubtextGray)
                            Text(text = "Expansion E(32➔48) ➔ 8 S-Boxes ➔ P-Permutation", fontSize = 10.sp, color = tealColor, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(dynamicColors.inputBackground.copy(alpha = 0.7f)).padding(10.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = "L[i+1] = R[i]   |   R[i+1] = L[i] ⊕ F(R[i], K[i])",
                        fontSize = 11.sp, fontWeight = FontWeight.Bold, color = amberColor, fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DuolingoButton(
                text = if (round < 16) "STEP ROUND ($round/16)" else "16 ROUNDS COMPLETED",
                faceColor = DsaBlue, shadowColor = Color(0xFF1899D6),
                onClick = { stepRound() }, modifier = Modifier.weight(1f)
            )
            DuolingoButton(
                text = "RESET",
                faceColor = DsaRed, shadowColor = Color(0xFFCC3B3B),
                onClick = {
                    round = 1
                    leftHalf = "0x3554474B"
                    rightHalf = "0x6E616C69"
                }, modifier = Modifier.weight(0.7f)
            )
        }
    }
}

// ──────────────────────────────────────────────
// 3. AES 128-Bit State Matrix & Modes Visualizer
// ──────────────────────────────────────────────
@Composable
fun AESMatrixVisualizer() {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val amberColor = if (isDark) AmberGold else Color(0xFFB45309)
    val redColor = if (isDark) DsaRed else Color(0xFFDC2626)
    val greenColor = if (isDark) DuolingoGreen else Color(0xFF15803D)

    var mainTab by remember { mutableIntStateOf(0) } // 0: State Matrix Rounds, 1: Block Cipher Modes
    var stageIndex by remember { mutableIntStateOf(0) }
    var selectedMode by remember { mutableStateOf("ECB") }
    val stages = listOf("Plaintext", "SubBytes", "ShiftRows", "MixColumns", "AddRoundKey")

    val stateMatrices = listOf(
        listOf(listOf("32", "88", "31", "E0"), listOf("43", "5A", "31", "37"), listOf("F6", "30", "98", "07"), listOf("A8", "8D", "A2", "34")),
        listOf(listOf("23", "C4", "C7", "E1"), listOf("1A", "BE", "C7", "9A"), listOf("42", "04", "46", "C5"), listOf("C2", "5D", "3A", "18")),
        listOf(listOf("23", "C4", "C7", "E1"), listOf("BE", "C7", "9A", "1A"), listOf("46", "C5", "42", "04"), listOf("18", "C2", "5D", "3A")),
        listOf(listOf("BA", "84", "E8", "1B"), listOf("75", "A4", "8D", "40"), listOf("F4", "8D", "06", "7D"), listOf("7A", "32", "0E", "5D")),
        listOf(listOf("D4", "E0", "B8", "1E"), listOf("27", "BF", "B4", "41"), listOf("11", "98", "5D", "52"), listOf("AE", "F1", "E5", "30"))
    )

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
                Text(text = "STATE MATRIX ROUNDS", fontSize = 10.5.sp, fontWeight = FontWeight.Black, color = if (mainTab == 0) Color.White else SubtextGray)
            }
            Box(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                    .background(if (mainTab == 1) DsaPurple else (if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.05f)))
                    .border(1.dp, if (mainTab == 1) DsaPurple else dynamicColors.inputBorder, RoundedCornerShape(10.dp))
                    .clickable { mainTab = 1 }.padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "BLOCK CIPHER MODES", fontSize = 10.5.sp, fontWeight = FontWeight.Black, color = if (mainTab == 1) Color.White else SubtextGray)
            }
        }

        if (mainTab == 0) {
            val currentMatrix = stateMatrices[stageIndex]
            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                    .border(1.5.dp, DsaPurple.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "AES 4×4 BYTE STATE MATRIX", fontSize = 12.sp, fontWeight = FontWeight.Black, color = DsaPurple)
                        Text(text = "STAGE: ${stages[stageIndex]}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = amberColor)
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(dynamicColors.inputBackground).padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        currentMatrix.forEachIndexed { r, row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                row.forEachIndexed { c, byteStr ->
                                    val isShifted = stageIndex == 2 && r > 0
                                    Box(
                                        modifier = Modifier.size(54.dp, 42.dp).clip(RoundedCornerShape(8.dp))
                                            .background(if (isShifted) DsaPurple.copy(alpha = if (isDark) 0.35f else 0.18f) else DsaBlue.copy(alpha = if (isDark) 0.15f else 0.08f))
                                            .border(1.dp, if (isShifted) DsaPurple else dynamicColors.inputBorder, RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = byteStr, fontSize = 14.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = if (isShifted) (if (isDark) Color(0xFFD8B4FE) else Color(0xFF6B21A8)) else dynamicColors.textPrimary)
                                    }
                                }
                            }
                        }
                    }

                    Text(
                        text = when (stageIndex) {
                            0 -> "Initial 16-byte input block arranged into a 4x4 column-major matrix."
                            1 -> "SubBytes: Each byte replaced with multiplicative inverse in GF(2^8) S-box + affine transformation."
                            2 -> "ShiftRows: Row 0 shifted 0, Row 1 shifted 1, Row 2 shifted 2, Row 3 shifted 3 bytes left."
                            3 -> "MixColumns: Columns multiplied by fixed polynomial matrix c(x) modulo x^4 + 1 for diffusion."
                            4 -> "AddRoundKey: State matrix XORed with expanded 128-bit round key."
                            else -> ""
                        },
                        fontSize = 11.5.sp, color = SubtextGray, lineHeight = 16.sp
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                stages.forEachIndexed { idx, name ->
                    Box(
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                            .background(if (stageIndex == idx) DsaPurple else (if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.05f)))
                            .border(1.dp, if (stageIndex == idx) DsaPurple else dynamicColors.inputBorder, RoundedCornerShape(10.dp))
                            .clickable { stageIndex = idx }.padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = name.take(5), fontSize = 10.sp, fontWeight = FontWeight.Black, color = if (stageIndex == idx) Color.White else SubtextGray)
                    }
                }
            }
        } else {
            // Block Cipher Modes Comparison (ECB, CBC, CTR, GCM)
            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                    .border(1.5.dp, DsaPurple.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "BLOCK CIPHER OPERATING MODES", fontSize = 12.sp, fontWeight = FontWeight.Black, color = DsaPurple)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("ECB", "CBC", "CTR", "GCM").forEach { mode ->
                            Box(
                                modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp))
                                    .background(if (selectedMode == mode) AmberGold else (if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f)))
                                    .border(1.dp, if (selectedMode == mode) AmberGold else dynamicColors.inputBorder, RoundedCornerShape(8.dp))
                                    .clickable { selectedMode = mode }.padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = mode, fontSize = 11.sp, fontWeight = FontWeight.Black, color = if (selectedMode == mode) Color.Black else dynamicColors.textPrimary)
                            }
                        }
                    }

                    when (selectedMode) {
                        "ECB" -> {
                            Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(DsaRed.copy(alpha = if (isDark) 0.15f else 0.08f)).border(1.dp, DsaRed.copy(alpha = 0.5f), RoundedCornerShape(10.dp)).padding(10.dp)) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(text = "⚠️ INSECURE: ECB (ELECTRONIC CODEBOOK)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = redColor)
                                    Text(text = "Each 128-bit block is encrypted independently with key K: C_i = E_K(P_i). Identical plaintext blocks produce identical ciphertext blocks!", fontSize = 10.5.sp, color = dynamicColors.textPrimary)
                                    Text(text = "🐧 ECB Penguin Effect: Pattern leaks through encryption. NEVER use ECB for messages > 16 bytes.", fontSize = 10.5.sp, color = amberColor, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        "CBC" -> {
                            Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(DsaBlue.copy(alpha = if (isDark) 0.15f else 0.08f)).border(1.dp, DsaBlue.copy(alpha = 0.5f), RoundedCornerShape(10.dp)).padding(10.dp)) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(text = "🔒 CBC (CIPHER BLOCK CHAINING)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = DsaBlue)
                                    Text(text = "C_0 = E_K(P_0 ⊕ IV), C_i = E_K(P_i ⊕ C_{i-1}). Random Initial Vector (IV) ensures distinct ciphertexts for repeated plaintexts.", fontSize = 10.5.sp, color = dynamicColors.textPrimary)
                                    Text(text = "⚠️ Vulnerable to padding oracle attacks if not combined with HMAC (Encrypt-then-MAC).", fontSize = 10.5.sp, color = amberColor, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        "CTR" -> {
                            Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Color(0xFF00CD9C).copy(alpha = if (isDark) 0.15f else 0.08f)).border(1.dp, Color(0xFF00CD9C).copy(alpha = 0.5f), RoundedCornerShape(10.dp)).padding(10.dp)) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(text = "⚡ CTR (COUNTER MODE)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = if (isDark) Color(0xFF00CD9C) else Color(0xFF047857))
                                    Text(text = "Converts block cipher into a stream cipher: C_i = P_i ⊕ E_K(Nonce ∥ Counter). Fully parallelizable encryption & decryption.", fontSize = 10.5.sp, color = dynamicColors.textPrimary)
                                    Text(text = "⚠️ FATAL IF NONCE REUSED: C1 ⊕ C2 = P1 ⊕ P2 (two-time pad leakage).", fontSize = 10.5.sp, color = redColor, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        "GCM" -> {
                            Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(DuolingoGreen.copy(alpha = if (isDark) 0.15f else 0.08f)).border(1.dp, DuolingoGreen.copy(alpha = 0.5f), RoundedCornerShape(10.dp)).padding(10.dp)) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(text = "🛡️ GCM (GALOIS/COUNTER MODE) — GOLD STANDARD", fontSize = 11.sp, fontWeight = FontWeight.Black, color = greenColor)
                                    Text(text = "Modern AEAD: combines CTR mode encryption with GHASH polynomial MAC over GF(2^128). Provides both Confidentiality and Authenticity in one pass.", fontSize = 10.5.sp, color = dynamicColors.textPrimary)
                                    Text(text = "Mandatory cipher mode for TLS 1.3 and modern secure communications.", fontSize = 10.5.sp, color = greenColor, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────────────
// 4. Vigenère Polyalphabetic Cipher Visualizer
// ──────────────────────────────────────────────
@Composable
fun VigenereCipherVisualizer() {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val amberColor = if (isDark) AmberGold else Color(0xFFB45309)
    val tealColor = if (isDark) DsaTeal else Color(0xFF0D9488)

    var plaintext by remember { mutableStateOf("DEFEND THE EAST WALL") }
    var key by remember { mutableStateOf("FORT") }
    var isDecryptMode by remember { mutableStateOf(false) }

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

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Mode Switch
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                    .background(if (!isDecryptMode) DsaTeal else (if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.05f)))
                    .border(1.dp, if (!isDecryptMode) DsaTeal else dynamicColors.inputBorder, RoundedCornerShape(10.dp))
                    .clickable { isDecryptMode = false }.padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "ENCRYPT (P + K mod 26)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = if (!isDecryptMode) Color.Black else SubtextGray)
            }
            Box(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                    .background(if (isDecryptMode) AmberGold else (if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.05f)))
                    .border(1.dp, if (isDecryptMode) AmberGold else dynamicColors.inputBorder, RoundedCornerShape(10.dp))
                    .clickable { isDecryptMode = true }.padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "DECRYPT (C - K mod 26)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = if (isDecryptMode) Color.Black else SubtextGray)
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, DsaTeal.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = if (isDecryptMode) "CIPHERTEXT" else "PLAINTEXT", fontSize = 11.sp, fontWeight = FontWeight.Black, color = tealColor)
                BasicTextField(
                    value = plaintext, onValueChange = { if (it.length <= 25) plaintext = it },
                    textStyle = TextStyle(color = dynamicColors.textPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                    cursorBrush = SolidColor(DsaTeal), modifier = Modifier.fillMaxWidth()
                )
                Text(text = "SECRET KEYWORD", fontSize = 11.sp, fontWeight = FontWeight.Black, color = amberColor)
                BasicTextField(
                    value = key, onValueChange = { if (it.length <= 10) key = it },
                    textStyle = TextStyle(color = amberColor, fontSize = 16.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace),
                    cursorBrush = SolidColor(AmberGold), modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Tabula Recta Keystream Mapping Strip
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(dynamicColors.inputBackground)
                .border(1.dp, dynamicColors.inputBorder, RoundedCornerShape(14.dp)).padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "REPEATED KEYSTREAM ALIGNMENT", fontSize = 10.sp, fontWeight = FontWeight.Black, color = SubtextGray)
                Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    cleanPlain.forEachIndexed { i, pChar ->
                        val kChar = cleanKey[i % cleanKey.length]
                        val rChar = resultText.getOrElse(i) { '?' }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(dynamicColors.cardBackground).border(1.dp, dynamicColors.inputBorder, RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(text = "$pChar", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary)
                            Text(text = "⊕", fontSize = 10.sp, color = SubtextGray)
                            Text(text = "$kChar", fontSize = 12.sp, fontWeight = FontWeight.Black, color = amberColor)
                            Text(text = "=", fontSize = 10.sp, color = SubtextGray)
                            Text(text = "$rChar", fontSize = 13.sp, fontWeight = FontWeight.Black, color = tealColor)
                        }
                    }
                }
            }
        }

        // Cryptanalysis Note
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(dynamicColors.inputBackground).border(1.dp, dynamicColors.inputBorder, RoundedCornerShape(12.dp)).padding(12.dp)
        ) {
            Text(
                text = "💡 Cryptanalysis: Vigenère flattens monoalphabetic frequency spikes, but repeated key patterns allow Kasiski Examination and Index of Coincidence (IC) analysis to deduce key length and break each Caesar slice.",
                fontSize = 11.sp, color = SubtextGray, lineHeight = 15.sp
            )
        }
    }
}

// ──────────────────────────────────────────────
// 5. Diffie-Hellman Key Agreement Visualizer
// ──────────────────────────────────────────────
@Composable
fun DiffieHellmanVisualizer() {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val amberColor = if (isDark) AmberGold else Color(0xFFB45309)
    val tealColor = if (isDark) Color(0xFF00CD9C) else Color(0xFF047857)
    val greenColor = if (isDark) DuolingoGreen else Color(0xFF15803D)

    val p = 23
    val g = 5

    var aSecret by remember { mutableIntStateOf(6) }
    var bSecret by remember { mutableIntStateOf(15) }

    val alicePub = remember(aSecret) {
        var res = 1
        repeat(aSecret) { res = (res * g) % p }
        res
    }

    val bobPub = remember(bSecret) {
        var res = 1
        repeat(bSecret) { res = (res * g) % p }
        res
    }

    val aliceShared = remember(alicePub, bobPub, aSecret) {
        var res = 1
        repeat(aSecret) { res = (res * bobPub) % p }
        res
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                .background(DsaOrange.copy(alpha = if (isDark) 0.15f else 0.08f))
                .border(1.dp, DsaOrange.copy(alpha = if (isDark) 0.5f else 0.35f), RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(text = "⚠️ TOY PARAMETERS — p=23, g=5 (educational toy; real DH uses 2048+ bit primes or Curve25519)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isDark) DsaOrange else Color(0xFFC2410C))
        }

        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, Color(0xFF00CD9C).copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "PUBLIC PARAMETERS: Prime p = $p  |  Base g = $g", fontSize = 12.sp, fontWeight = FontWeight.Black, color = tealColor)

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "👩 Alice private secret a", fontSize = 11.sp, color = tealColor, fontWeight = FontWeight.Bold)
                        Text(text = "a = $aSecret", fontSize = 12.sp, fontWeight = FontWeight.Black, color = tealColor, fontFamily = FontFamily.Monospace)
                    }
                    Slider(
                        value = aSecret.toFloat(), onValueChange = { aSecret = it.roundToInt() }, valueRange = 2f..20f,
                        colors = SliderDefaults.colors(thumbColor = Color(0xFF00CD9C), activeTrackColor = Color(0xFF00CD9C), inactiveTrackColor = Color(0xFF00CD9C).copy(alpha = 0.2f))
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "👨 Bob private secret b", fontSize = 11.sp, color = amberColor, fontWeight = FontWeight.Bold)
                        Text(text = "b = $bSecret", fontSize = 12.sp, fontWeight = FontWeight.Black, color = amberColor, fontFamily = FontFamily.Monospace)
                    }
                    Slider(
                        value = bSecret.toFloat(), onValueChange = { bSecret = it.roundToInt() }, valueRange = 2f..20f,
                        colors = SliderDefaults.colors(thumbColor = AmberGold, activeTrackColor = AmberGold, inactiveTrackColor = AmberGold.copy(alpha = 0.2f))
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(Color(0xFF00CD9C).copy(alpha = if (isDark) 0.12f else 0.08f)).border(1.dp, Color(0xFF00CD9C), RoundedCornerShape(12.dp)).padding(12.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(text = "👩 ALICE", fontSize = 12.sp, fontWeight = FontWeight.Black, color = tealColor)
                            Text(text = "A = g^a mod p", fontSize = 10.sp, color = SubtextGray, fontFamily = FontFamily.Monospace)
                            Text(text = "Sends A = $alicePub", fontSize = 14.sp, fontWeight = FontWeight.Black, color = dynamicColors.textPrimary, fontFamily = FontFamily.Monospace)
                        }
                    }

                    Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(AmberGold.copy(alpha = if (isDark) 0.12f else 0.08f)).border(1.dp, AmberGold, RoundedCornerShape(12.dp)).padding(12.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(text = "👨 BOB", fontSize = 12.sp, fontWeight = FontWeight.Black, color = amberColor)
                            Text(text = "B = g^b mod p", fontSize = 10.sp, color = SubtextGray, fontFamily = FontFamily.Monospace)
                            Text(text = "Sends B = $bobPub", fontSize = 14.sp, fontWeight = FontWeight.Black, color = dynamicColors.textPrimary, fontFamily = FontFamily.Monospace)
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(dynamicColors.inputBackground).padding(8.dp), contentAlignment = Alignment.Center) {
                    Text(text = "⚠️ Eve sees p=$p, g=$g, A=$alicePub, B=$bobPub — solving DLOG to find a or b is hard", fontSize = 10.sp, color = SubtextGray)
                }

                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .background(DuolingoGreen.copy(alpha = if (isDark) 0.18f else 0.10f)).border(1.5.dp, DuolingoGreen, RoundedCornerShape(12.dp)).padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(text = "✨ MATCHED SHARED SECRET", fontSize = 11.sp, fontWeight = FontWeight.Black, color = greenColor)
                        Text(text = "K = $aliceShared", fontSize = 22.sp, fontWeight = FontWeight.Black, color = dynamicColors.textPrimary, fontFamily = FontFamily.Monospace)
                        Text(text = "Alice: B^a mod p  |  Bob: A^b mod p  =  $aliceShared", fontSize = 10.sp, color = SubtextGray, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────────────
// 6. RSA Public-Key & Digital Signature Visualizer
// ──────────────────────────────────────────────
@Composable
fun RSAPublicKeyVisualizer() {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val amberColor = if (isDark) AmberGold else Color(0xFFB45309)
    val tealColor = if (isDark) DsaTeal else Color(0xFF0D9488)
    val greenColor = if (isDark) DuolingoGreen else Color(0xFF15803D)

    val p = 61
    val q = 53
    val n = p * q // 3233
    val phi = (p - 1) * (q - 1) // 3120
    val e = 17
    val d = 2753 // d * e = 1 mod phi

    var rsaMode by remember { mutableIntStateOf(0) } // 0: Encryption/Decryption, 1: Digital Signature
    var messageNum by remember { mutableIntStateOf(65) } // 'A'

    // c = m^e mod n
    val ciphertextNum = remember(messageNum) {
        var res = 1L
        repeat(e) { res = (res * messageNum) % n }
        res.toInt()
    }

    // Decrypt: m = c^d mod n
    val decryptedNum = remember(ciphertextNum) {
        var res = 1L
        var base = ciphertextNum.toLong()
        var exp = d
        while (exp > 0) {
            if (exp % 2 == 1) res = (res * base) % n
            base = (base * base) % n
            exp /= 2
        }
        res.toInt()
    }

    // Signature: s = m^d mod n
    val signatureNum = remember(messageNum) {
        var res = 1L
        var base = messageNum.toLong()
        var exp = d
        while (exp > 0) {
            if (exp % 2 == 1) res = (res * base) % n
            base = (base * base) % n
            exp /= 2
        }
        res.toInt()
    }

    // Verify signature: m' = s^e mod n
    val verifiedNum = remember(signatureNum) {
        var res = 1L
        repeat(e) { res = (res * signatureNum) % n }
        res.toInt()
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                .background(DsaOrange.copy(alpha = if (isDark) 0.15f else 0.08f))
                .border(1.dp, DsaOrange.copy(alpha = if (isDark) 0.5f else 0.35f), RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(text = "⚠️ TOY RSA (p=61, q=53, n=3233) — For learning only. Real RSA uses ≥2048-bit primes.", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isDark) DsaOrange else Color(0xFFC2410C))
        }

        // Mode Selector: Encrypt vs Sign
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                    .background(if (rsaMode == 0) DsaTeal else (if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.05f)))
                    .border(1.dp, if (rsaMode == 0) DsaTeal else dynamicColors.inputBorder, RoundedCornerShape(10.dp))
                    .clickable { rsaMode = 0 }.padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "ENCRYPTION (m^e mod n)", fontSize = 10.5.sp, fontWeight = FontWeight.Black, color = if (rsaMode == 0) Color.Black else SubtextGray)
            }
            Box(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                    .background(if (rsaMode == 1) AmberGold else (if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.05f)))
                    .border(1.dp, if (rsaMode == 1) AmberGold else dynamicColors.inputBorder, RoundedCornerShape(10.dp))
                    .clickable { rsaMode = 1 }.padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "SIGNATURE (s = m^d mod n)", fontSize = 10.5.sp, fontWeight = FontWeight.Black, color = if (rsaMode == 1) Color.Black else SubtextGray)
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, DsaTeal.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "RSA KEY PAIR MATH", fontSize = 12.sp, fontWeight = FontWeight.Black, color = tealColor)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Primes: p=$p, q=$q", fontSize = 11.sp, color = SubtextGray)
                    Text(text = "Modulus n = p×q = $n", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary, fontFamily = FontFamily.Monospace)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Euler Totient φ(n) = $phi", fontSize = 11.sp, color = SubtextGray)
                    Text(text = "Public e=$e | Private d=$d", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = amberColor, fontFamily = FontFamily.Monospace)
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, InputBorder, RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "MESSAGE SYMBOL (m)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = tealColor)
                    Text(text = "$messageNum ('${messageNum.toChar()}')", fontSize = 14.sp, fontWeight = FontWeight.Black, color = dynamicColors.textPrimary, fontFamily = FontFamily.Monospace)
                }

                Slider(
                    value = messageNum.toFloat(), onValueChange = { messageNum = it.roundToInt() }, valueRange = 2f..100f,
                    colors = SliderDefaults.colors(thumbColor = DsaTeal, activeTrackColor = DsaTeal)
                )

                if (rsaMode == 0) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(AmberGold.copy(alpha = if (isDark) 0.15f else 0.08f)).border(1.dp, AmberGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp)).padding(10.dp)) {
                            Column {
                                Text(text = "CIPHERTEXT (c)", fontSize = 10.sp, color = amberColor, fontWeight = FontWeight.Bold)
                                Text(text = "$ciphertextNum", fontSize = 16.sp, fontWeight = FontWeight.Black, color = dynamicColors.textPrimary, fontFamily = FontFamily.Monospace)
                                Text(text = "c = m^e mod n", fontSize = 9.sp, color = SubtextGray)
                            }
                        }

                        Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(DuolingoGreen.copy(alpha = if (isDark) 0.15f else 0.08f)).border(1.dp, DuolingoGreen.copy(alpha = 0.4f), RoundedCornerShape(10.dp)).padding(10.dp)) {
                            Column {
                                Text(text = "DECRYPTED (m)", fontSize = 10.sp, color = greenColor, fontWeight = FontWeight.Bold)
                                Text(text = "$decryptedNum", fontSize = 16.sp, fontWeight = FontWeight.Black, color = dynamicColors.textPrimary, fontFamily = FontFamily.Monospace)
                                Text(text = "m = c^d mod n", fontSize = 9.sp, color = SubtextGray)
                            }
                        }
                    }
                } else {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(AmberGold.copy(alpha = if (isDark) 0.15f else 0.08f)).border(1.dp, AmberGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp)).padding(10.dp)) {
                            Column {
                                Text(text = "SIGNATURE (s)", fontSize = 10.sp, color = amberColor, fontWeight = FontWeight.Bold)
                                Text(text = "$signatureNum", fontSize = 16.sp, fontWeight = FontWeight.Black, color = dynamicColors.textPrimary, fontFamily = FontFamily.Monospace)
                                Text(text = "s = m^d mod n (private key)", fontSize = 9.sp, color = SubtextGray)
                            }
                        }

                        Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(DuolingoGreen.copy(alpha = if (isDark) 0.15f else 0.08f)).border(1.dp, DuolingoGreen.copy(alpha = 0.4f), RoundedCornerShape(10.dp)).padding(10.dp)) {
                            Column {
                                Text(text = "VERIFIED DIGEST", fontSize = 10.sp, color = greenColor, fontWeight = FontWeight.Bold)
                                Text(text = "$verifiedNum", fontSize = 16.sp, fontWeight = FontWeight.Black, color = dynamicColors.textPrimary, fontFamily = FontFamily.Monospace)
                                Text(text = "m' = s^e mod n (public key)", fontSize = 9.sp, color = SubtextGray)
                            }
                        }
                    }
                }

                Text(
                    text = "⚠️ Real-world RSA requires randomized padding (RSA-OAEP for encryption, RSA-PSS for signatures) to eliminate padding oracle attacks (Bleichenbacher attack).",
                    fontSize = 10.sp, color = SubtextGray, lineHeight = 14.sp
                )
            }
        }
    }
}

// ──────────────────────────────────────────────
// 7. ECC Elliptic Curve Visualizer
// ──────────────────────────────────────────────
@Composable
fun ECCPointVisualizer() {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val cyanColor = if (isDark) Color.Cyan else Color(0xFF0284C7)

    var kScalar by remember { mutableIntStateOf(3) }

    // Toy finite field F17: y^2 = x^3 + x + 6 mod 17
    val toyPoints = remember {
        val pts = mutableListOf<Pair<Int, Int>>()
        for (x in 0..16) {
            val rhs = ((x.toLong() * x * x + x + 6) % 17).toInt()
            for (y in 0..16) { if ((y * y) % 17 == rhs) pts.add(Pair(x, y)) }
        }
        pts
    }
    val kPoint = remember(kScalar) {
        if (toyPoints.isEmpty()) "—" else {
            val pt = toyPoints[(kScalar - 1) % toyPoints.size]
            "(${pt.first}, ${pt.second})"
        }
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                .background(cyanColor.copy(alpha = if (isDark) 0.10f else 0.08f)).border(1.dp, cyanColor.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "📐 ILLUSTRATION ON ℝ — This canvas shows the real-number curve shape only. Real ECC uses points over a prime finite field F_p. Educational toy only.",
                fontSize = 10.5.sp, color = cyanColor, lineHeight = 14.sp, fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, cyanColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "ELLIPTIC CURVE: y² = x³ + ax + b (ℝ illustration)", fontSize = 12.sp, fontWeight = FontWeight.Black, color = cyanColor)
                Text(text = "Chord-and-Tangent Point Addition & Scalar Multiplication kP", fontSize = 11.sp, color = SubtextGray)

                // Visual Canvas Curve (Theme Adaptive)
                Canvas(
                    modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(12.dp)).background(dynamicColors.inputBackground)
                        .border(1.dp, dynamicColors.inputBorder, RoundedCornerShape(12.dp))
                ) {
                    val w = size.width
                    val h = size.height
                    val originX = w / 2f
                    val originY = h / 2f

                    val axisColor = if (isDark) Color.White.copy(alpha = 0.18f) else Color.Black.copy(alpha = 0.15f)
                    drawLine(color = axisColor, start = Offset(0f, originY), end = Offset(w, originY), strokeWidth = 1.dp.toPx())
                    drawLine(color = axisColor, start = Offset(originX, 0f), end = Offset(originX, h), strokeWidth = 1.dp.toPx())

                    val upperPath = Path()
                    val lowerPath = Path()
                    var first = true

                    val scale = 25f
                    for (pixelX in 0..w.toInt()) {
                        val mathX = (pixelX - originX) / scale
                        val rhs = mathX * mathX * mathX - 3 * mathX + 3
                        if (rhs >= 0) {
                            val mathY = kotlin.math.sqrt(rhs)
                            val canvasY1 = originY - mathY * scale
                            val canvasY2 = originY + mathY * scale
                            if (first) {
                                upperPath.moveTo(pixelX.toFloat(), canvasY1)
                                lowerPath.moveTo(pixelX.toFloat(), canvasY2)
                                first = false
                            } else {
                                upperPath.lineTo(pixelX.toFloat(), canvasY1)
                                lowerPath.lineTo(pixelX.toFloat(), canvasY2)
                            }
                        }
                    }

                    drawPath(upperPath, color = cyanColor, style = Stroke(width = 2.dp.toPx()))
                    drawPath(lowerPath, color = cyanColor, style = Stroke(width = 2.dp.toPx()))

                    val pCanvasX = originX + 1.2f * scale
                    val pCanvasY = originY - 1.5f * scale
                    drawCircle(color = AmberGold, radius = 5.dp.toPx(), center = Offset(pCanvasX, pCanvasY))

                    val qCanvasX = originX + 2.5f * scale
                    val qCanvasY = originY - 3.2f * scale
                    drawCircle(color = DsaGreen, radius = 5.dp.toPx(), center = Offset(qCanvasX, qCanvasY))

                    val chordColor = if (isDark) Color.White.copy(alpha = 0.45f) else Color.Black.copy(alpha = 0.35f)
                    drawLine(
                        color = chordColor,
                        start = Offset(pCanvasX - 30f, pCanvasY + 35f),
                        end = Offset(qCanvasX + 30f, qCanvasY - 35f),
                        strokeWidth = 1.5.dp.toPx()
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "SCALAR MULTIPLIER (k)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SubtextGray)
                    Text(text = "$kScalar × P", fontSize = 14.sp, fontWeight = FontWeight.Black, color = cyanColor, fontFamily = FontFamily.Monospace)
                }

                Slider(
                    value = kScalar.toFloat(), onValueChange = { kScalar = it.roundToInt() }, valueRange = 1f..10f,
                    colors = SliderDefaults.colors(thumbColor = cyanColor, activeTrackColor = cyanColor)
                )

                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                        .background(dynamicColors.inputBackground).border(1.dp, dynamicColors.inputBorder, RoundedCornerShape(10.dp)).padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "🔢 TOY FINITE FIELD F₁₇: y² = x³ + x + 6 mod 17", fontSize = 10.sp, fontWeight = FontWeight.Black, color = cyanColor)
                        Text(text = "k×P → index point $kPoint  (${toyPoints.size} points on curve)", fontSize = 12.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = dynamicColors.textPrimary)
                        Text(text = "ECDLP Hardness: Given P and Q = kP, finding k is computationally intractable in real F_p with p ≈ 2²⁵⁶.", fontSize = 10.5.sp, color = SubtextGray, lineHeight = 14.sp)
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────────────
// 8. SHA-256 Hash Construction & Avalanche Visualizer
// ──────────────────────────────────────────────
@Composable
fun SHA256Visualizer() {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val amberColor = if (isDark) AmberGold else Color(0xFFB45309)
    val purpleColor = if (isDark) Color(0xFFA05AFF) else Color(0xFF7E22CE)
    val digestColor = if (isDark) Color(0xFFD8B4FE) else Color(0xFF6B21A8)
    val greenColor = if (isDark) DuolingoGreen else Color(0xFF15803D)

    var message by remember { mutableStateOf("Integrity Check") }
    var compareMode by remember { mutableStateOf(false) }
    var messageB by remember { mutableStateOf("integrity check") }

    val hashA = remember(message) {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(message.toByteArray(Charsets.UTF_8))
        bytes.joinToString("") { "%02x".format(it) }
    }

    val hashB = remember(messageB) {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(messageB.toByteArray(Charsets.UTF_8))
        bytes.joinToString("") { "%02x".format(it) }
    }

    val differingNibbles = remember(hashA, hashB) {
        var diff = 0
        for (i in 0 until minOf(hashA.length, hashB.length)) {
            if (hashA[i] != hashB[i]) diff++
        }
        diff
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, purpleColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "INPUT MESSAGE A", fontSize = 11.sp, fontWeight = FontWeight.Black, color = purpleColor)
                BasicTextField(
                    value = message, onValueChange = { message = it },
                    textStyle = TextStyle(color = dynamicColors.textPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                    cursorBrush = SolidColor(purpleColor), modifier = Modifier.fillMaxWidth()
                )
                Text(text = "Padded to 512-bit blocks (message + 1-bit + zeros + 64-bit length)", fontSize = 10.5.sp, color = SubtextGray)
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, purpleColor.copy(alpha = 0.6f), RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "SHA-256 HASH DIGEST A (256-BIT)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = purpleColor)
                Text(
                    text = hashA.chunked(8).joinToString(" "),
                    fontSize = 12.5.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace,
                    color = digestColor, lineHeight = 18.sp
                )

                // 8 Working Registers
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    listOf("A", "B", "C", "D", "E", "F", "G", "H").forEachIndexed { i, reg ->
                        val chunk = hashA.chunked(4).getOrElse(i) { "0000" }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = reg, fontSize = 10.sp, fontWeight = FontWeight.Black, color = purpleColor)
                            Text(text = chunk, fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = dynamicColors.textPrimary)
                        }
                    }
                }
            }
        }

        // Avalanche Effect Demo Card
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(dynamicColors.inputBackground)
                .border(1.dp, dynamicColors.inputBorder, RoundedCornerShape(14.dp)).padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⚡ AVALANCHE EFFECT COMPARISON", fontSize = 11.sp, fontWeight = FontWeight.Black, color = amberColor)
                    Text(
                        text = if (compareMode) "Close ▲" else "Compare 2 Inputs ▼",
                        fontSize = 11.sp, color = purpleColor, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { compareMode = !compareMode }
                    )
                }

                if (compareMode) {
                    Text(text = "INPUT MESSAGE B (single character difference):", fontSize = 10.sp, color = SubtextGray)
                    BasicTextField(
                        value = messageB, onValueChange = { messageB = it },
                        textStyle = TextStyle(color = amberColor, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                        cursorBrush = SolidColor(AmberGold), modifier = Modifier.fillMaxWidth()
                    )
                    Text(text = "HASH B: " + hashB.chunked(8).joinToString(" "), fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = amberColor)

                    val pctDiff = (differingNibbles * 100f / 64f).roundToInt()
                    Text(
                        text = "Diff: $differingNibbles / 64 hex nibbles changed (~$pctDiff% output change). Strict Avalanche Criterion satisfied!",
                        fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = greenColor
                    )
                } else {
                    Text(text = "One-way & Pre-image resistance: Flipping a single bit in the input flips ~50% of all output bits in a pseudo-random avalanche.", fontSize = 10.5.sp, color = SubtextGray)
                }
            }
        }
    }
}

// ──────────────────────────────────────────────
// 9. Real HMAC & Password Salting Visualizer
// ──────────────────────────────────────────────
@Composable
fun HMACCollisionVisualizer() {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val amberColor = if (isDark) AmberGold else Color(0xFFB45309)
    val redColor = if (isDark) DsaRed else Color(0xFFDC2626)

    var key by remember { mutableStateOf("secretKey123") }
    var msg by remember { mutableStateOf("Transfer $500 to Alice") }

    // Real Java HmacSHA256
    val realHmac = remember(key, msg) {
        try {
            val mac = Mac.getInstance("HmacSHA256")
            val keySpec = SecretKeySpec(key.toByteArray(Charsets.UTF_8), "HmacSHA256")
            mac.init(keySpec)
            val bytes = mac.doFinal(msg.toByteArray(Charsets.UTF_8))
            bytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            "Error computing HMAC"
        }
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, redColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "RFC 2104 HMAC-SHA256 (GENUINE MAC)", fontSize = 12.sp, fontWeight = FontWeight.Black, color = redColor)
                Text(text = "HMAC(K, m) = H((K ⊕ opad) ∥ H((K ⊕ ipad) ∥ m))", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = amberColor, fontFamily = FontFamily.Monospace)

                Text(text = "SECRET KEY (K)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SubtextGray)
                BasicTextField(
                    value = key, onValueChange = { key = it },
                    textStyle = TextStyle(color = amberColor, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                    cursorBrush = SolidColor(AmberGold), modifier = Modifier.fillMaxWidth()
                )

                Text(text = "AUTHENTICATED MESSAGE (m)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SubtextGray)
                BasicTextField(
                    value = msg, onValueChange = { msg = it },
                    textStyle = TextStyle(color = dynamicColors.textPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                    cursorBrush = SolidColor(DsaRed), modifier = Modifier.fillMaxWidth()
                )

                // Visual Pads
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(dynamicColors.inputBackground).border(1.dp, dynamicColors.inputBorder, RoundedCornerShape(8.dp)).padding(8.dp)) {
                        Column {
                            Text(text = "Inner Pad (0x36)", fontSize = 10.sp, color = amberColor, fontWeight = FontWeight.Bold)
                            Text(text = "512-bit K ⊕ 0x36 ∥ m", fontSize = 10.sp, color = dynamicColors.textPrimary, fontFamily = FontFamily.Monospace)
                        }
                    }
                    Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(dynamicColors.inputBackground).border(1.dp, dynamicColors.inputBorder, RoundedCornerShape(8.dp)).padding(8.dp)) {
                        Column {
                            Text(text = "Outer Pad (0x5C)", fontSize = 10.sp, color = redColor, fontWeight = FontWeight.Bold)
                            Text(text = "512-bit K ⊕ 0x5C ∥ inner", fontSize = 10.sp, color = dynamicColors.textPrimary, fontFamily = FontFamily.Monospace)
                        }
                    }
                }

                Text(text = "AUTHENTICATION TAG (256-BIT):", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SubtextGray)
                Text(text = realHmac.chunked(8).joinToString(" "), fontSize = 11.sp, fontWeight = FontWeight.Black, color = redColor, fontFamily = FontFamily.Monospace, lineHeight = 16.sp)
            }
        }

        // Password Hashing & Salting Education Card
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(dynamicColors.inputBackground)
                .border(1.dp, dynamicColors.inputBorder, RoundedCornerShape(14.dp)).padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "🧂 PASSWORD HASHING & SALTING (KDFs)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = amberColor)
                Text(
                    text = "• Raw MD5/SHA-256 for passwords is DEAD (GPUs test billions/sec; vulnerable to Rainbow Tables).\n" +
                           "• Cryptographic Salt: Unique random value per user stops precomputed rainbow table lookups.\n" +
                           "• Memory-Hard KDFs: Argon2id (PHC winner), bcrypt, scrypt defeat ASIC/GPU parallel cracking.",
                    fontSize = 10.5.sp, color = SubtextGray, lineHeight = 15.sp
                )
            }
        }
    }
}

// ──────────────────────────────────────────────
// 10. TLS 1.3 Handshake Swim-Lane Visualizer
// ──────────────────────────────────────────────
@Composable
fun TLS13HandshakeVisualizer() {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val amberColor = if (isDark) AmberGold else Color(0xFFB45309)
    val tealColor = if (isDark) DsaTeal else Color(0xFF0D9488)
    val orangeColor = if (isDark) Color(0xFFFF9600) else Color(0xFFEA580C)
    val greenColor = if (isDark) DuolingoGreen else Color(0xFF15803D)

    var step by remember { mutableIntStateOf(0) }
    var viewComparison by remember { mutableStateOf(false) }

    data class TLSPacket(val title: String, val sender: String, val detail: String, val keyDerived: String?)

    val packets = listOf(
        TLSPacket("ClientHello + KeyShare", "CLIENT ➔ SERVER", "Supported ciphers (e.g. TLS_AES_256_GCM_SHA384) + Client ECDH ephemeral share g^a", null),
        TLSPacket("ServerHello + KeyShare", "SERVER ➔ CLIENT", "Chosen cipher + Server ECDH share g^b.", "⚡ Ephemeral Handshake Keys Derived!"),
        TLSPacket("EncryptedExtensions & Cert", "SERVER ➔ CLIENT", "Server X.509 certificate chain transmitted encrypted under Handshake Key.", null),
        TLSPacket("CertificateVerify & Finished", "SERVER ➔ CLIENT", "Server signs handshake transcript with private key; HMAC Finished tag sent.", null),
        TLSPacket("Client Finished & AEAD", "CLIENT ➔ SERVER", "Client validates cert chain; sends Finished tag. Symmetric application traffic begins!", "🔒 1-RTT Application Keys Active!")
    )

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, orangeColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "TLS 1.3 1-RTT HANDSHAKE SWIM-LANE", fontSize = 12.sp, fontWeight = FontWeight.Black, color = orangeColor)
                    Text(text = "STEP ${step + 1} / 5", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = amberColor)
                }

                // Swim Lane Headers
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(6.dp)).background(AmberGold.copy(alpha = if (isDark) 0.18f else 0.10f)).border(1.dp, AmberGold.copy(alpha = 0.3f), RoundedCornerShape(6.dp)).padding(6.dp), contentAlignment = Alignment.Center) {
                        Text(text = "💻 CLIENT (BROWSER)", fontSize = 10.sp, fontWeight = FontWeight.Black, color = amberColor)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(6.dp)).background(DsaTeal.copy(alpha = if (isDark) 0.18f else 0.10f)).border(1.dp, DsaTeal.copy(alpha = 0.3f), RoundedCornerShape(6.dp)).padding(6.dp), contentAlignment = Alignment.Center) {
                        Text(text = "🖥️ SERVER (NGINX/EDGE)", fontSize = 10.sp, fontWeight = FontWeight.Black, color = tealColor)
                    }
                }

                // Swim lane packet rows
                packets.forEachIndexed { idx, p ->
                    val isPast = idx < step
                    val isCurrent = idx == step
                    val isClientSender = p.sender.startsWith("CLIENT")

                    Column(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                            .background(if (isCurrent) orangeColor.copy(alpha = if (isDark) 0.15f else 0.10f) else dynamicColors.inputBackground.copy(alpha = 0.5f))
                            .border(1.dp, if (isCurrent) orangeColor else dynamicColors.inputBorder, RoundedCornerShape(10.dp))
                            .padding(8.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "${idx + 1}. ${p.title}", fontSize = 11.5.sp, fontWeight = FontWeight.Black, color = if (isCurrent) dynamicColors.textPrimary else SubtextGray)
                            Text(text = if (isClientSender) "➔ [SEND]" else "⬅ [RECV]", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isClientSender) amberColor else tealColor)
                        }
                        Text(text = p.detail, fontSize = 10.sp, color = SubtextGray, lineHeight = 13.sp)
                        if (p.keyDerived != null && (isCurrent || isPast)) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = p.keyDerived, fontSize = 10.sp, fontWeight = FontWeight.Black, color = greenColor)
                        }
                    }
                }
            }
        }

        // TLS 1.2 vs 1.3 Comparison Sheet
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(dynamicColors.inputBackground)
                .border(1.dp, dynamicColors.inputBorder, RoundedCornerShape(12.dp)).clickable { viewComparison = !viewComparison }.padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "⚡ TLS 1.2 VS TLS 1.3 ARCHITECTURE", fontSize = 11.sp, fontWeight = FontWeight.Black, color = amberColor)
                    Text(text = if (viewComparison) "Hide ▲" else "Compare ▼", fontSize = 11.sp, color = SubtextGray)
                }
                if (viewComparison) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Latency: TLS 1.3 requires only 1-RTT (down from 2-RTT in TLS 1.2). Supports 0-RTT PSK resumption.\n" +
                               "• Perfect Forward Secrecy: Static RSA key transport completely removed. If server private key leaks in the future, past recorded traffic remains secure.\n" +
                               "• Pruned Obsolete Ciphers: Removed RC4, 3DES, MD5, SHA-1, and CBC-mode ciphers vulnerable to POODLE and Lucky13.",
                        fontSize = 10.5.sp, color = SubtextGray, lineHeight = 15.sp
                    )
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DuolingoButton(
                text = if (step < 4) "NEXT PACKET ➔" else "RESTART HANDSHAKE",
                faceColor = Color(0xFFFF9600), shadowColor = Color(0xFFD27800),
                onClick = { step = (step + 1) % 5 }, modifier = Modifier.weight(1f)
            )
        }
    }
}
