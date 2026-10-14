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
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin

// ══════════════════════════════════════════════════════════════════
// 🛡️ CNS VISUALIZER ENGINES (10 Flagship Cryptography & NetSec Labs)
// ══════════════════════════════════════════════════════════════════

// ──────────────────────────────────────────────
// 1. Caesar & Shift Cipher Visualizer
// ──────────────────────────────────────────────
@Composable
fun CaesarCipherVisualizer() {
    var plaintext by remember { mutableStateOf("CRYPTO LAB") }
    var shift by remember { mutableIntStateOf(3) }
    val clipboard = LocalClipboardManager.current
    var isCopied by remember { mutableStateOf(false) }

    val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val ciphertext = remember(plaintext, shift) {
        plaintext.map { char ->
            if (char.isLetter()) {
                val base = if (char.isUpperCase()) 'A' else 'a'
                val shifted = (char.code - base.code + shift).mod(26)
                (base.code + shifted).toChar()
            } else char
        }.joinToString("")
    }

    val letterCounts = remember(ciphertext) {
        val upper = ciphertext.uppercase()
        val counts = mutableMapOf<Char, Int>()
        for (c in alphabet) counts[c] = 0
        for (c in upper) {
            if (c in 'A'..'Z') counts[c] = (counts[c] ?: 0) + 1
        }
        counts
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Plaintext Input Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackground)
                .border(1.5.dp, InputBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "INPUT PLAINTEXT", fontSize = 11.sp, fontWeight = FontWeight.Black, color = DsaGreen)
                BasicTextField(
                    value = plaintext,
                    onValueChange = { if (it.length <= 25) plaintext = it },
                    textStyle = TextStyle(
                        color = LocalDynamicThemeColors.current.textPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    ),
                    cursorBrush = SolidColor(DsaGreen),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(text = "Modular formula: C = (P + K) mod 26", fontSize = 11.sp, color = SubtextGray)
            }
        }

        // Shift Key Slider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackground)
                .border(1.5.dp, InputBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "SHIFT KEY (K)", fontSize = 12.sp, fontWeight = FontWeight.Black, color = AmberGold)
                    Text(text = "+$shift MOD 26", fontSize = 14.sp, fontWeight = FontWeight.Black, color = AmberGold, fontFamily = FontFamily.Monospace)
                }

                Slider(
                    value = shift.toFloat(),
                    onValueChange = { shift = it.roundToInt() },
                    valueRange = 0f..25f,
                    steps = 24,
                    colors = SliderDefaults.colors(
                        thumbColor = AmberGold,
                        activeTrackColor = AmberGold,
                        inactiveTrackColor = AmberGold.copy(alpha = 0.2f)
                    )
                )

                // Alphabet Shift Ribbon
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    alphabet.forEachIndexed { i, ch ->
                        val shiftedChar = alphabet[(i + shift) % 26]
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (i == shift) AmberGold.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.04f))
                                .border(1.dp, if (i == shift) AmberGold else Color.Transparent, RoundedCornerShape(8.dp))
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            Text(text = "$ch", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LocalDynamicThemeColors.current.textPrimary)
                            Text(text = "↓", fontSize = 9.sp, color = AmberGold)
                            Text(text = "$shiftedChar", fontSize = 11.sp, fontWeight = FontWeight.Black, color = DsaGreen)
                        }
                    }
                }
            }
        }

        // Ciphertext Output Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackground)
                .border(1.5.dp, DsaGreen.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "CIPHERTEXT (OUTPUT)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = DsaGreen)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DsaGreen.copy(alpha = 0.15f))
                            .clickable {
                                clipboard.setText(AnnotatedString(ciphertext))
                                isCopied = true
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy, contentDescription = null, tint = DsaGreen, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (isCopied) "Copied!" else "Copy", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DsaGreen)
                    }
                }

                Text(
                    text = ciphertext.ifEmpty { "(empty)" },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = DsaGreen,
                    letterSpacing = 2.sp
                )
            }
        }

        // Letter Frequency Distribution Preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF0F1726))
                .border(1.dp, InputBorder, RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "📊 CIPHERTEXT FREQUENCY ANALYSIS", fontSize = 10.sp, fontWeight = FontWeight.Black, color = SubtextGray)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val maxVal = letterCounts.values.maxOrNull()?.coerceAtLeast(1) ?: 1
                    listOf('E', 'T', 'A', 'O', 'I', 'N', 'S', 'H', 'R').forEach { letter ->
                        val count = letterCounts[letter] ?: 0
                        val barHeight = (count.toFloat() / maxVal * 32).coerceIn(4f, 32f)
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Box(
                                modifier = Modifier
                                    .width(16.dp)
                                    .height(barHeight.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (count > 0) DsaGreen else SubtextGray.copy(alpha = 0.2f))
                            )
                            Text(text = "$letter", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = LocalDynamicThemeColors.current.textPrimary)
                        }
                    }
                }
                Text(text = "Single-letter frequency patterns remain unchanged in monoalphabetic ciphers, leaving them vulnerable to frequency analysis.", fontSize = 10.5.sp, color = SubtextGray)
            }
        }
    }
}

// ──────────────────────────────────────────────
// 2. DES Feistel Network Visualizer
// ──────────────────────────────────────────────
@Composable
fun DESFeistelVisualizer() {
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

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackground)
                .border(1.5.dp, DsaBlue.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "FEISTEL ROUND $round / 16", fontSize = 12.sp, fontWeight = FontWeight.Black, color = DsaBlue)
                    Text(text = "64-BIT BLOCK", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SubtextGray)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Left 32-bit Half
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DsaBlue.copy(alpha = 0.15f))
                            .border(1.5.dp, DsaBlue, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "L[$round] (32-bit)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = DsaBlue)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = leftHalf, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = LocalDynamicThemeColors.current.textPrimary, fontFamily = FontFamily.Monospace)
                        }
                    }

                    // Right 32-bit Half
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DsaPurple.copy(alpha = 0.15f))
                            .border(1.5.dp, DsaPurple, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "R[$round] (32-bit)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = DsaPurple)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = rightHalf, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = LocalDynamicThemeColors.current.textPrimary, fontFamily = FontFamily.Monospace)
                        }
                    }
                }

                // Subkey and Round Function Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF10192A))
                        .border(1.dp, InputBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "🔑 Round Subkey K[$round]:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SubtextGray)
                            Text(text = "0x" + ((round * 0x1F2E3D4C).toLong() and 0xFFFFFFFFFFL).toString(16).uppercase().take(12), fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = AmberGold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "⚙️ Non-linear F(R, K):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SubtextGray)
                            Text(text = "Expansion E(R) ➔ 8 S-Boxes ➔ P-Permutation", fontSize = 10.5.sp, color = DsaTeal)
                        }
                    }
                }

                // Feistel Equation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF131B2E))
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "L[i+1] = R[i]   |   R[i+1] = L[i] ⊕ F(R[i], K[i])",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberGold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DuolingoButton(
                text = if (round < 16) "STEP ROUND ($round/16)" else "16 ROUNDS COMPLETED",
                faceColor = DsaBlue,
                shadowColor = Color(0xFF1899D6),
                onClick = { stepRound() },
                modifier = Modifier.weight(1f)
            )
            DuolingoButton(
                text = "RESET",
                faceColor = DsaRed,
                shadowColor = Color(0xFFCC3B3B),
                onClick = {
                    round = 1
                    leftHalf = "0x3554474B"
                    rightHalf = "0x6E616C69"
                },
                modifier = Modifier.weight(0.7f)
            )
        }
    }
}

// ──────────────────────────────────────────────
// 3. AES 128-Bit State Matrix Visualizer
// ──────────────────────────────────────────────
@Composable
fun AESMatrixVisualizer() {
    var stageIndex by remember { mutableIntStateOf(0) }
    val stages = listOf("Plaintext", "SubBytes", "ShiftRows", "MixColumns", "AddRoundKey")

    val stateMatrices = listOf(
        listOf(
            listOf("32", "88", "31", "E0"),
            listOf("43", "5A", "31", "37"),
            listOf("F6", "30", "98", "07"),
            listOf("A8", "8D", "A2", "34")
        ),
        listOf(
            listOf("23", "C4", "C7", "E1"),
            listOf("1A", "BE", "C7", "9A"),
            listOf("42", "04", "46", "C5"),
            listOf("C2", "5D", "3A", "18")
        ),
        listOf(
            listOf("23", "C4", "C7", "E1"),
            listOf("BE", "C7", "9A", "1A"),
            listOf("46", "C5", "42", "04"),
            listOf("18", "C2", "5D", "3A")
        ),
        listOf(
            listOf("BA", "84", "E8", "1B"),
            listOf("75", "A4", "8D", "40"),
            listOf("F4", "8D", "06", "7D"),
            listOf("7A", "32", "0E", "5D")
        ),
        listOf(
            listOf("D4", "E0", "B8", "1E"),
            listOf("27", "BF", "B4", "41"),
            listOf("11", "98", "5D", "52"),
            listOf("AE", "F1", "E5", "30")
        )
    )

    val currentMatrix = stateMatrices[stageIndex]

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackground)
                .border(1.5.dp, DsaPurple.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "AES 4×4 BYTE STATE MATRIX", fontSize = 12.sp, fontWeight = FontWeight.Black, color = DsaPurple)
                    Text(text = "STAGE: ${stages[stageIndex]}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AmberGold)
                }

                // 4x4 Grid
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F1524))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    currentMatrix.forEachIndexed { r, row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            row.forEachIndexed { c, byteStr ->
                                val isShifted = stageIndex == 2 && r > 0
                                Box(
                                    modifier = Modifier
                                        .size(54.dp, 44.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isShifted) DsaPurple.copy(alpha = 0.35f) else DsaBlue.copy(alpha = 0.15f))
                                        .border(1.dp, if (isShifted) DsaPurple else Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = byteStr,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace,
                                        color = if (isShifted) Color(0xFFD8B4FE) else Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                // Stage Description
                Text(
                    text = when (stageIndex) {
                        0 -> "Initial 16-byte input block arranged into a 4x4 column-major matrix."
                        1 -> "SubBytes: Each byte replaced with its non-linear multiplicative inverse in GF(2^8) S-box."
                        2 -> "ShiftRows: Row 0 shifted 0, Row 1 shifted 1, Row 2 shifted 2, Row 3 shifted 3 bytes left."
                        3 -> "MixColumns: Columns multiplied by fixed polynomial matrix c(x) modulo x^4 + 1 for diffusion."
                        4 -> "AddRoundKey: State matrix XORed with expanded 128-bit round key."
                        else -> ""
                    },
                    fontSize = 11.5.sp,
                    color = SubtextGray,
                    lineHeight = 16.sp
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            stages.forEachIndexed { idx, name ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (stageIndex == idx) DsaPurple else Color.White.copy(alpha = 0.06f))
                        .clickable { stageIndex = idx }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name.take(5),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = if (stageIndex == idx) Color.White else SubtextGray
                    )
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
    var plaintext by remember { mutableStateOf("DEFEND THE EAST WALL") }
    var key by remember { mutableStateOf("FORT") }

    val cleanPlain = plaintext.filter { it.isLetter() }.uppercase()
    val cleanKey = key.filter { it.isLetter() }.uppercase().ifEmpty { "A" }

    val ciphertext = remember(cleanPlain, cleanKey) {
        cleanPlain.mapIndexed { idx, pChar ->
            val kChar = cleanKey[idx % cleanKey.length]
            val shift = kChar - 'A'
            val c = ((pChar - 'A' + shift) % 26 + 'A'.code).toChar()
            c
        }.joinToString("")
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackground)
                .border(1.5.dp, DsaOrange.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "SECRET KEYWORD", fontSize = 11.sp, fontWeight = FontWeight.Black, color = DsaOrange)
                BasicTextField(
                    value = key,
                    onValueChange = { if (it.length <= 10) key = it },
                    textStyle = TextStyle(
                        color = AmberGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    ),
                    cursorBrush = SolidColor(AmberGold),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(text = "KEYSTREAM ALIGNMENT (TABULA RECTA)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = SubtextGray)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    cleanPlain.take(15).forEachIndexed { idx, pChar ->
                        val kChar = cleanKey[idx % cleanKey.length]
                        val cChar = if (idx < ciphertext.length) ciphertext[idx] else '?'
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF141F30))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(text = "$pChar", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LocalDynamicThemeColors.current.textPrimary)
                            Text(text = "+$kChar", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AmberGold)
                            Text(text = "↓", fontSize = 8.sp, color = SubtextGray)
                            Text(text = "$cChar", fontSize = 13.sp, fontWeight = FontWeight.Black, color = DsaOrange)
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackground)
                .border(1.5.dp, InputBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "POLYALPHABETIC CIPHERTEXT", fontSize = 11.sp, fontWeight = FontWeight.Black, color = DsaOrange)
                Text(
                    text = ciphertext.chunked(4).joinToString(" "),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = DsaOrange,
                    letterSpacing = 2.sp
                )
                Text(text = "Repeated keystream flattens monoalphabetic frequency spikes across Tabula Recta.", fontSize = 11.sp, color = SubtextGray)
            }
        }
    }
}

// ──────────────────────────────────────────────
// 5. Diffie-Hellman Key Exchange Visualizer
// ──────────────────────────────────────────────
@Composable
fun DiffieHellmanVisualizer() {
    val p = 23 // Shared prime modulus
    val g = 5  // Shared base generator
    var aSecret by remember { mutableIntStateOf(6) } // Alice private key
    var bSecret by remember { mutableIntStateOf(15) } // Bob private key

    // A = g^a mod p
    val alicePub = remember(aSecret) {
        var res = 1
        repeat(aSecret) { res = (res * g) % p }
        res
    }

    // B = g^b mod p
    val bobPub = remember(bSecret) {
        var res = 1
        repeat(bSecret) { res = (res * g) % p }
        res
    }

    // Alice shared secret = B^a mod p
    val aliceShared = remember(bobPub, aSecret) {
        var res = 1
        repeat(aSecret) { res = (res * bobPub) % p }
        res
    }

    // Bob shared secret = A^b mod p
    val bobShared = remember(alicePub, bSecret) {
        var res = 1
        repeat(bSecret) { res = (res * alicePub) % p }
        res
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackground)
                .border(1.5.dp, Color(0xFF00CD9C).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "PUBLIC PARAMETERS", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(0xFF00CD9C))
                    Text(text = "Prime p = $p  |  Base g = $g", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LocalDynamicThemeColors.current.textPrimary, fontFamily = FontFamily.Monospace)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Alice
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0D2520))
                            .border(1.dp, Color(0xFF00CD9C), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "👩 ALICE", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(0xFF00CD9C))
                            Text(text = "Secret a = $aSecret", fontSize = 11.sp, color = AmberGold, fontFamily = FontFamily.Monospace)
                            Text(text = "Sends A = $alicePub", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color.White, fontFamily = FontFamily.Monospace)
                        }
                    }

                    // Bob
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF251A0D))
                            .border(1.dp, AmberGold, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "👨 BOB", fontSize = 12.sp, fontWeight = FontWeight.Black, color = AmberGold)
                            Text(text = "Secret b = $bSecret", fontSize = 11.sp, color = Color(0xFF00CD9C), fontFamily = FontFamily.Monospace)
                            Text(text = "Sends B = $bobPub", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color.White, fontFamily = FontFamily.Monospace)
                        }
                    }
                }

                // Insecure Channel
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1B1B1B))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "⚠️ Insecure Channel: Eavesdropper Eve sees p=$p, g=$g, A=$alicePub, B=$bobPub", fontSize = 10.sp, color = SubtextGray)
                }

                // Computed Shared Secret
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DuolingoGreen.copy(alpha = 0.2f))
                        .border(1.5.dp, DuolingoGreen, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "✨ MATCHED SHARED SECRET", fontSize = 11.sp, fontWeight = FontWeight.Black, color = DuolingoGreen)
                        Text(text = "K = g^(ab) mod p = $aliceShared", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────────────
// 6. RSA Public Key Encryption Visualizer
// ──────────────────────────────────────────────
@Composable
fun RSAPublicKeyVisualizer() {
    val p = 61
    val q = 53
    val n = p * q // 3233
    val phi = (p - 1) * (q - 1) // 3120
    val e = 17
    val d = 2753 // d * e = 1 mod phi

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

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackground)
                .border(1.5.dp, DsaTeal.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "RSA KEY GENERATION (PRIME FACTORIZATION)", fontSize = 12.sp, fontWeight = FontWeight.Black, color = DsaTeal)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Primes: p=$p, q=$q", fontSize = 11.sp, color = SubtextGray)
                    Text(text = "Modulus n = $n", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White, fontFamily = FontFamily.Monospace)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Totient φ(n) = $phi", fontSize = 11.sp, color = SubtextGray)
                    Text(text = "Public e = $e, Private d = $d", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AmberGold, fontFamily = FontFamily.Monospace)
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackground)
                .border(1.5.dp, InputBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "PLAINTEXT VALUE (m)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = DsaTeal)
                    Text(text = "$messageNum ('${messageNum.toChar()}')", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White, fontFamily = FontFamily.Monospace)
                }

                Slider(
                    value = messageNum.toFloat(),
                    onValueChange = { messageNum = it.roundToInt() },
                    valueRange = 2f..100f,
                    colors = SliderDefaults.colors(thumbColor = DsaTeal, activeTrackColor = DsaTeal)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF16232E))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(text = "CIPHERTEXT (c)", fontSize = 10.sp, color = AmberGold)
                            Text(text = "$ciphertextNum", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.White, fontFamily = FontFamily.Monospace)
                            Text(text = "c = m^e mod n", fontSize = 9.sp, color = SubtextGray)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF10281F))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(text = "DECRYPTED (m)", fontSize = 10.sp, color = DuolingoGreen)
                            Text(text = "$decryptedNum", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.White, fontFamily = FontFamily.Monospace)
                            Text(text = "m = c^d mod n", fontSize = 9.sp, color = SubtextGray)
                        }
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────────────
// 7. ECC Elliptic Curve Visualizer
// ──────────────────────────────────────────────
@Composable
fun ECCPointVisualizer() {
    var kScalar by remember { mutableIntStateOf(3) }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackground)
                .border(1.5.dp, Color.Cyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "ELLIPTIC CURVE: y² = x³ + ax + b", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.Cyan)
                Text(text = "Chord-and-Tangent Point Addition & Scalar Multiplication kP", fontSize = 11.sp, color = SubtextGray)

                // Visual Canvas Curve
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF09141F))
                ) {
                    val w = size.width
                    val h = size.height
                    val cx = w / 2f
                    val cy = h / 2f

                    // Axes
                    drawLine(Color.White.copy(alpha = 0.15f), Offset(0f, cy), Offset(w, cy), strokeWidth = 1f)
                    drawLine(Color.White.copy(alpha = 0.15f), Offset(cx, 0f), Offset(cx, h), strokeWidth = 1f)

                    // Draw curve points
                    val pathUpper = Path()
                    val pathLower = Path()
                    var first = true

                    for (xPix in 0..w.toInt()) {
                        val xVal = (xPix - cx) / 25f
                        val ySq = xVal * xVal * xVal - 3 * xVal + 3 // a=-3, b=3
                        if (ySq >= 0) {
                            val yVal = kotlin.math.sqrt(ySq)
                            val yPix1 = cy - yVal * 25f
                            val yPix2 = cy + yVal * 25f
                            if (first) {
                                pathUpper.moveTo(xPix.toFloat(), yPix1)
                                pathLower.moveTo(xPix.toFloat(), yPix2)
                                first = false
                            } else {
                                pathUpper.lineTo(xPix.toFloat(), yPix1)
                                pathLower.lineTo(xPix.toFloat(), yPix2)
                            }
                        }
                    }
                    drawPath(pathUpper, Color.Cyan, style = Stroke(width = 2.5f))
                    drawPath(pathLower, Color.Cyan, style = Stroke(width = 2.5f))

                    // Draw Point P and Q
                    drawCircle(Color(0xFFFF9600), radius = 6f, center = Offset(cx - 20f, cy - 35f))
                    drawCircle(Color(0xFF00CD9C), radius = 6f, center = Offset(cx + 35f, cy - 45f))
                    // Secant Line
                    drawLine(Color.White.copy(alpha = 0.6f), Offset(cx - 60f, cy - 27f), Offset(cx + 70f, cy - 53f), strokeWidth = 1.5f)
                    // Result R = P + Q
                    drawCircle(Color(0xFFE91E63), radius = 6f, center = Offset(cx + 55f, cy + 50f))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "SCALAR MULTIPLIER (k)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SubtextGray)
                    Text(text = "$kScalar × P", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.Cyan, fontFamily = FontFamily.Monospace)
                }

                Slider(
                    value = kScalar.toFloat(),
                    onValueChange = { kScalar = it.roundToInt() },
                    valueRange = 1f..10f,
                    colors = SliderDefaults.colors(thumbColor = Color.Cyan, activeTrackColor = Color.Cyan)
                )

                Text(
                    text = "ECDLP Hardness: Given P and Q = kP, it is computationally intractable to find scalar k.",
                    fontSize = 11.sp,
                    color = SubtextGray
                )
            }
        }
    }
}

// ──────────────────────────────────────────────
// 8. SHA-256 Hash Construction Visualizer
// ──────────────────────────────────────────────
@Composable
fun SHA256Visualizer() {
    var message by remember { mutableStateOf("Integrity Check") }

    val hashDigest = remember(message) {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(message.toByteArray(Charsets.UTF_8))
        bytes.joinToString("") { "%02x".format(it) }
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackground)
                .border(1.5.dp, Color(0xFFA05AFF).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "INPUT MESSAGE", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFFA05AFF))
                BasicTextField(
                    value = message,
                    onValueChange = { message = it },
                    textStyle = TextStyle(
                        color = LocalDynamicThemeColors.current.textPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    ),
                    cursorBrush = SolidColor(Color(0xFFA05AFF)),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(text = "Padded to 512-bit blocks (message + 1-bit + zeros + 64-bit length)", fontSize = 10.5.sp, color = SubtextGray)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackground)
                .border(1.5.dp, Color(0xFFA05AFF).copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "SHA-256 HASH DIGEST (256-BIT / 32 BYTES)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFFA05AFF))
                Text(
                    text = hashDigest.chunked(8).joinToString(" "),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFFD8B4FE),
                    lineHeight = 18.sp
                )

                // 8 Working Registers
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    listOf("A", "B", "C", "D", "E", "F", "G", "H").forEachIndexed { i, reg ->
                        val chunk = hashDigest.chunked(4).getOrElse(i) { "0000" }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = reg, fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFFA05AFF))
                            Text(text = chunk, fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = Color.White)
                        }
                    }
                }
                Text(text = "One-way property: Changing a single input bit flips ~50% of the output digest (avalanche effect).", fontSize = 11.sp, color = SubtextGray)
            }
        }
    }
}

// ──────────────────────────────────────────────
// 9. HMAC & Collision Bound Visualizer
// ──────────────────────────────────────────────
@Composable
fun HMACCollisionVisualizer() {
    var key by remember { mutableStateOf("secretKey") }
    var msg by remember { mutableStateOf("Transfer $500") }

    val hmacPreview = remember(key, msg) {
        val md = MessageDigest.getInstance("SHA-256")
        val inner = md.digest((key + "ipad" + msg).toByteArray(Charsets.UTF_8))
        val outer = md.digest((key + "opad" + inner.joinToString("") { "%02x".format(it) }).toByteArray(Charsets.UTF_8))
        outer.take(16).joinToString("") { "%02x".format(it) }
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackground)
                .border(1.5.dp, DsaRed.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "HMAC CONSTRUCTION (NESTED HASHING)", fontSize = 12.sp, fontWeight = FontWeight.Black, color = DsaRed)
                Text(text = "HMAC(K, m) = H((K ⊕ opad) ∥ H((K ⊕ ipad) ∥ m))", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AmberGold, fontFamily = FontFamily.Monospace)

                // Visual Pads
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(Color(0xFF201318)).padding(8.dp)) {
                        Column {
                            Text(text = "Inner Pad (0x36)", fontSize = 10.sp, color = AmberGold)
                            Text(text = "K ⊕ ipad ∥ msg", fontSize = 11.sp, color = Color.White, fontFamily = FontFamily.Monospace)
                        }
                    }
                    Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(Color(0xFF201318)).padding(8.dp)) {
                        Column {
                            Text(text = "Outer Pad (0x5C)", fontSize = 10.sp, color = DsaRed)
                            Text(text = "K ⊕ opad ∥ inner", fontSize = 11.sp, color = Color.White, fontFamily = FontFamily.Monospace)
                        }
                    }
                }

                Text(text = "AUTHENTICATION TAG: $hmacPreview", fontSize = 12.sp, fontWeight = FontWeight.Black, color = DsaRed, fontFamily = FontFamily.Monospace)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF16101D))
                .border(1.dp, InputBorder, RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "🎯 BIRTHDAY PARADOX BOUND: 2^(n/2)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = AmberGold)
                Text(text = "Finding an identical hash collision for an n-bit digest requires only 2^(n/2) attempts, rather than 2^n brute-force iterations.", fontSize = 11.sp, color = SubtextGray)
            }
        }
    }
}

// ──────────────────────────────────────────────
// 10. TLS 1.3 Handshake Tracer Visualizer
// ──────────────────────────────────────────────
@Composable
fun TLS13HandshakeVisualizer() {
    var step by remember { mutableIntStateOf(0) }

    val handshakeSteps = listOf(
        Pair("1. ClientHello", "Client initiates with supported ciphers (e.g. TLS_AES_256_GCM_SHA384) + ephemeral KeyShare."),
        Pair("2. ServerHello", "Server responds with chosen cipher + Server KeyShare. Handshake keys derived!"),
        Pair("3. EncryptedExtensions & Cert", "Server sends encrypted certificate chain & validation parameters."),
        Pair("4. CertificateVerify & Finished", "Server signs handshake context with private key; sends Finished tag."),
        Pair("5. Client Finished & AEAD Record", "Client verifies cert, sends Finished; symmetric application traffic begins!")
    )

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackground)
                .border(1.5.dp, Color(0xFFFF9600).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "TLS 1.3 1-RTT HANDSHAKE TRACER", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(0xFFFF9600))
                    Text(text = "STEP ${step + 1} / 5", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AmberGold)
                }

                handshakeSteps.forEachIndexed { idx, (title, desc) ->
                    val isPast = idx < step
                    val isCurrent = idx == step
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isCurrent) Color(0xFFFF9600).copy(alpha = 0.15f) else Color.Transparent)
                            .border(1.dp, if (isCurrent) Color(0xFFFF9600) else Color.Transparent, RoundedCornerShape(10.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (isPast || isCurrent) Color(0xFFFF9600) else SubtextGray.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "${idx + 1}", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.Black)
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Black, color = if (isCurrent) Color.White else SubtextGray)
                            Text(text = desc, fontSize = 10.5.sp, color = SubtextGray, lineHeight = 14.sp)
                        }
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DuolingoButton(
                text = if (step < 4) "NEXT PACKET ➔" else "RESTART HANDSHAKE",
                faceColor = Color(0xFFFF9600),
                shadowColor = Color(0xFFD27800),
                onClick = { step = (step + 1) % 5 },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
