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
import com.simats.codeingo.ui.theme.DsaBlue
import com.simats.codeingo.ui.theme.DsaGreen
import com.simats.codeingo.ui.theme.DsaOrange
import com.simats.codeingo.ui.theme.DsaPurple
import com.simats.codeingo.ui.theme.DsaRed
import com.simats.codeingo.ui.theme.DsaTeal
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
    var plaintext by remember { mutableStateOf("HELLO CRYPTO") }
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
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "PLAINTEXT INPUT", fontSize = 11.sp, fontWeight = FontWeight.Black, color = SubtextGray)
                BasicTextField(
                    value = plaintext,
                    onValueChange = { plaintext = it.uppercase() },
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = LocalDynamicThemeColors.current.textPrimary,
                        fontFamily = FontFamily.Monospace
                    ),
                    cursorBrush = SolidColor(DsaGreen),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Shift Slider Controls
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackground)
                .border(1.5.dp, DsaGreen.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "MODULO SHIFT (K)", fontSize = 12.sp, fontWeight = FontWeight.Black, color = DsaGreen)
                    Text(
                        text = "K = $shift  |  (C = (P + $shift) mod 26)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = LocalDynamicThemeColors.current.textPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Slider(
                    value = shift.toFloat(),
                    onValueChange = { shift = it.roundToInt() },
                    valueRange = 0f..25f,
                    steps = 24,
                    colors = SliderDefaults.colors(thumbColor = DsaGreen, activeTrackColor = DsaGreen)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DuolingoButton(
                        text = "ROT-13",
                        faceColor = DsaGreen,
                        shadowColor = Color(0xFF45A001),
                        onClick = { shift = 13 },
                        modifier = Modifier.weight(1f)
                    )
                    DuolingoButton(
                        text = "SHIFT +1",
                        faceColor = DsaBlue,
                        shadowColor = Color(0xFF1899D6),
                        onClick = { shift = (shift + 1) % 26 },
                        modifier = Modifier.weight(1f)
                    )
                    DuolingoButton(
                        text = "RESET",
                        faceColor = DsaRed,
                        shadowColor = Color(0xFFCC3B3B),
                        onClick = { shift = 0 },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Cipher Alphabet Mapping Ring
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackground)
                .border(1.5.dp, InputBorder, RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "ALPHABET RING MAPPING", fontSize = 11.sp, fontWeight = FontWeight.Black, color = SubtextGray)
                Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                    alphabet.forEach { char ->
                        val target = alphabet[(char.code - 'A'.code + shift) % 26]
                        Column(
                            modifier = Modifier.padding(horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "$char", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SubtextGray)
                            Text(text = "↓", fontSize = 10.sp, color = DsaGreen)
                            Text(text = "$target", fontSize = 14.sp, fontWeight = FontWeight.Black, color = DsaGreen)
                        }
                    }
                }
            }
        }

        // Ciphertext Result Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DsaGreen.copy(alpha = 0.12f))
                .border(1.5.dp, DsaGreen, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "ENCRYPTED CIPHERTEXT", fontSize = 11.sp, fontWeight = FontWeight.Black, color = DsaGreen)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = ciphertext,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = LocalDynamicThemeColors.current.textPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                }

                DuolingoButton(
                    text = if (isCopied) "COPIED!" else "COPY",
                    faceColor = if (isCopied) DsaBlue else DsaGreen,
                    shadowColor = Color(0xFF45A001),
                    onClick = {
                        clipboard.setText(AnnotatedString(ciphertext))
                        isCopied = true
                    },
                    modifier = Modifier.width(90.dp)
                )
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
    var isRunning by remember { mutableStateOf(false) }

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
                text = "STEP ROUND",
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
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// ──────────────────────────────────────────────
// 3. AES 128-Bit State Matrix Visualizer
// ──────────────────────────────────────────────
@Composable
fun AESMatrixVisualizer() {
    var activePhase by remember { mutableStateOf("SubBytes") }
    var roundNum by remember { mutableIntStateOf(1) }

    val matrixState = remember(activePhase, roundNum) {
        listOf(
            listOf("32", "88", "31", "E0"),
            listOf("43", "5A", "31", "37"),
            listOf("F6", "30", "98", "07"),
            listOf("A8", "8D", "A2", "34")
        ).mapIndexed { r, row ->
            row.mapIndexed { c, byte ->
                when (activePhase) {
                    "SubBytes" -> ((byte.toInt(16) + 0x1F) % 256).toString(16).uppercase().padStart(2, '0')
                    "ShiftRows" -> {
                        val shiftedCol = (c - r).mod(4)
                        row[shiftedCol]
                    }
                    "MixColumns" -> ((byte.toInt(16) xor 0x3B) % 256).toString(16).uppercase().padStart(2, '0')
                    else -> ((byte.toInt(16) xor (roundNum * 17)) % 256).toString(16).uppercase().padStart(2, '0')
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Transformation Selector Tabs
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("SubBytes", "ShiftRows", "MixColumns", "AddRoundKey").forEach { phase ->
                val isSelected = activePhase == phase
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) DsaPurple else CardBackground)
                        .border(1.dp, if (isSelected) DsaPurple else InputBorder, RoundedCornerShape(10.dp))
                        .clickable { activePhase = phase }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = phase,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else SubtextGray
                    )
                }
            }
        }

        // 4x4 State Grid
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackground)
                .border(1.5.dp, DsaPurple.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "4x4 STATE MATRIX (128 BITS)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = DsaPurple)
                    Text(text = "ROUND $roundNum / 10", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SubtextGray)
                }

                matrixState.forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { byteVal ->
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(DsaPurple.copy(alpha = 0.15f))
                                    .border(1.5.dp, DsaPurple.copy(alpha = 0.6f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = byteVal,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = LocalDynamicThemeColors.current.textPrimary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DuolingoButton(
                text = "NEXT ROUND",
                faceColor = DsaPurple,
                shadowColor = Color(0xFF7A3ABF),
                onClick = { if (roundNum < 10) roundNum += 1 },
                modifier = Modifier.weight(1f)
            )
            DuolingoButton(
                text = "RESET ROUND 1",
                faceColor = DsaRed,
                shadowColor = Color(0xFFCC3B3B),
                onClick = {
                    roundNum = 1
                    activePhase = "SubBytes"
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// ──────────────────────────────────────────────
// 4. Vigenère Polyalphabetic Cipher Visualizer
// ──────────────────────────────────────────────
@Composable
fun VigenereCipherVisualizer() {
    var text by remember { mutableStateOf("ATTACK AT DAWN") }
    var key by remember { mutableStateOf("LEMON") }

    val cleanText = text.filter { it.isLetter() }.uppercase()
    val cleanKey = key.filter { it.isLetter() }.uppercase().ifEmpty { "A" }

    val alignedChars = cleanText.mapIndexed { idx, char ->
        val kChar = cleanKey[idx % cleanKey.length]
        val cipherChar = ((char.code - 'A'.code + (kChar.code - 'A'.code)) % 26 + 'A'.code).toChar()
        Triple(char, kChar, cipherChar)
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
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "KEYWORD ALIGNMENT", fontSize = 11.sp, fontWeight = FontWeight.Black, color = DsaOrange)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Plaintext", fontSize = 11.sp, color = SubtextGray)
                        BasicTextField(
                            value = text,
                            onValueChange = { text = it.uppercase() },
                            textStyle = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold, color = LocalDynamicThemeColors.current.textPrimary),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Keyword", fontSize = 11.sp, color = SubtextGray)
                        BasicTextField(
                            value = key,
                            onValueChange = { key = it.uppercase() },
                            textStyle = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DsaOrange),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        // Keystream Alignment Stream
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackground)
                .border(1.5.dp, InputBorder, RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "STREAM ENCRYPTION STEPS", fontSize = 11.sp, fontWeight = FontWeight.Black, color = SubtextGray)
                Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                    alignedChars.forEach { (p, k, c) ->
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 6.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF131D2A))
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "$p", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = LocalDynamicThemeColors.current.textPrimary)
                            Text(text = "+", fontSize = 10.sp, color = SubtextGray)
                            Text(text = "$k", fontSize = 13.sp, fontWeight = FontWeight.Black, color = DsaOrange)
                            Text(text = "↓", fontSize = 10.sp, color = DsaGreen)
                            Text(text = "$c", fontSize = 16.sp, fontWeight = FontWeight.Black, color = DsaGreen)
                        }
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────────────
// 5. Diffie-Hellman Key Exchange Visualizer
// ──────────────────────────────────────────────
@Composable
fun DiffieHellmanVisualizer() {
    val p = 23 // Shared Prime Modulus
    val g = 5  // Shared Generator Base
    var aSecret by remember { mutableIntStateOf(6) }
    var bSecret by remember { mutableIntStateOf(15) }

    fun modExp(base: Int, exp: Int, mod: Int): Int {
        var res = 1
        var b = base % mod
        var e = exp
        while (e > 0) {
            if (e % 2 == 1) res = (res * b) % mod
            b = (b * b) % mod
            e /= 2
        }
        return res
    }

    val aPublic = remember(aSecret) { modExp(g, aSecret, p) }
    val bPublic = remember(bSecret) { modExp(g, bSecret, p) }
    val aShared = remember(bPublic, aSecret) { modExp(bPublic, aSecret, p) }
    val bShared = remember(aPublic, bSecret) { modExp(aPublic, bSecret, p) }

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
                Text(text = "PUBLIC PARAMETERS (INSECURE NETWORK)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFF00CD9C))
                Text(
                    text = "Shared Prime P = $p   |   Shared Base G = $g",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = LocalDynamicThemeColors.current.textPrimary,
                    fontFamily = FontFamily.Monospace
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Alice
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DsaBlue.copy(alpha = 0.15f))
                            .border(1.dp, DsaBlue, RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        Text(text = "ALICE (Private a = $aSecret)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DsaBlue)
                        Text(text = "Sends A = $aPublic", fontSize = 13.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
                    }

                    // Bob
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DsaOrange.copy(alpha = 0.15f))
                            .border(1.dp, DsaOrange, RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        Text(text = "BOB (Private b = $bSecret)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DsaOrange)
                        Text(text = "Sends B = $bPublic", fontSize = 13.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
                    }
                }
            }
        }

        // Shared Secret Match
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DsaGreen.copy(alpha = 0.15f))
                .border(1.5.dp, DsaGreen, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "SHARED KEY ESTABLISHED", fontSize = 12.sp, fontWeight = FontWeight.Black, color = DsaGreen)
                    Text(text = "MATCH ✅", fontSize = 12.sp, fontWeight = FontWeight.Black, color = DsaGreen)
                }
                Text(
                    text = "Alice Secret: $aShared   ==   Bob Secret: $bShared",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = LocalDynamicThemeColors.current.textPrimary,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

// ──────────────────────────────────────────────
// 6. RSA Public Key Visualizer
// ──────────────────────────────────────────────
@Composable
fun RSAPublicKeyVisualizer() {
    val p = 61
    val q = 53
    val n = p * q
    val phi = (p - 1) * (q - 1)
    val e = 17
    val d = 2753 // Modular inverse of e mod phi

    var msgVal by remember { mutableIntStateOf(65) }

    fun modPow(base: Long, exp: Long, mod: Long): Long {
        var res = 1L
        var b = base % mod
        var ep = exp
        while (ep > 0) {
            if (ep % 2 == 1L) res = (res * b) % mod
            b = (b * b) % mod
            ep /= 2
        }
        return res
    }

    val ciphertext = remember(msgVal) { modPow(msgVal.toLong(), e.toLong(), n.toLong()) }
    val decrypted = remember(ciphertext) { modPow(ciphertext, d.toLong(), n.toLong()) }

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
                Text(text = "RSA KEY PAIR MATHEMATICS", fontSize = 11.sp, fontWeight = FontWeight.Black, color = DsaTeal)
                Text(
                    text = "Primes: p=$p, q=$q  ➔  Modulus n = p*q = $n\nTotient φ(n) = (p-1)(q-1) = $phi\nPublic Key: (e=$e, n=$n)\nPrivate Key: (d=$d, n=$n)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = LocalDynamicThemeColors.current.textPrimary,
                    fontFamily = FontFamily.Monospace
                )
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
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "MESSAGE ENCRYPTION DEMO", fontSize = 11.sp, fontWeight = FontWeight.Black, color = SubtextGray)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Plaintext M = $msgVal ('${msgVal.toChar()}')", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = LocalDynamicThemeColors.current.textPrimary)
                    Text(text = "➔", fontSize = 14.sp, color = DsaTeal)
                    Text(text = "Ciphertext C = $ciphertext", fontSize = 14.sp, fontWeight = FontWeight.Black, color = DsaTeal)
                }
                Text(text = "Decrypted M' = c^d mod n = $decrypted ('${decrypted.toInt().toChar()}')", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DsaGreen)
            }
        }
    }
}

// ──────────────────────────────────────────────
// 7. Elliptic Curve Cryptography (ECC) Visualizer
// ──────────────────────────────────────────────
@Composable
fun ECCPointVisualizer() {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F1523))
                .border(1.5.dp, Color.Cyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val midX = size.width / 2f
                val midY = size.height / 2f
                // Axes
                drawLine(Color.White.copy(alpha = 0.15f), Offset(0f, midY), Offset(size.width, midY), 1.5f)
                drawLine(Color.White.copy(alpha = 0.15f), Offset(midX, 0f), Offset(midX, size.height), 1.5f)

                // Curve y^2 = x^3 - 3x + 3
                val path = Path()
                var first = true
                for (xPixel in 0..size.width.toInt()) {
                    val x = (xPixel - midX) / 30f
                    val ySq = x * x * x - 3 * x + 3
                    if (ySq >= 0) {
                        val y = kotlin.math.sqrt(ySq) * 30f
                        if (first) {
                            path.moveTo(xPixel.toFloat(), midY - y)
                            first = false
                        } else {
                            path.lineTo(xPixel.toFloat(), midY - y)
                        }
                    }
                }
                drawPath(path, Color.Cyan, style = Stroke(width = 3f))

                // Points P and Q
                drawCircle(Color.Green, 6.dp.toPx(), Offset(midX + 20f, midY - 45f))
                drawCircle(AmberGold, 6.dp.toPx(), Offset(midX + 70f, midY - 65f))
                drawLine(Color.Yellow, Offset(midX + 20f, midY - 45f), Offset(midX + 110f, midY - 80f), 2f)
            }

            Text(
                text = "y² = x³ + ax + b  (secp256k1 Point Addition P + Q = R)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Cyan,
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }
    }
}

// ──────────────────────────────────────────────
// 8. SHA-256 Hash Engine Visualizer
// ──────────────────────────────────────────────
@Composable
fun SHA256Visualizer() {
    var textInput by remember { mutableStateOf("Codeingo") }
    val digest = remember(textInput) {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(textInput.toByteArray())
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
                Text(text = "INPUT PRE-IMAGE STRING", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFFA05AFF))
                BasicTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    textStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = LocalDynamicThemeColors.current.textPrimary),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF131828))
                .border(1.5.dp, Color(0xFFA05AFF), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "256-BIT ONE-WAY AVALANCHE DIGEST", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFFA05AFF))
                Text(
                    text = digest,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = AmberGold,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

// ──────────────────────────────────────────────
// 9. HMAC & Collision Bound Visualizer
// ──────────────────────────────────────────────
@Composable
fun HMACCollisionVisualizer() {
    var bitLength by remember { mutableFloatStateOf(32f) }
    val trialsFor50 = remember(bitLength) { 2.0.pow(bitLength / 2.0).roundToInt() }

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
                Text(text = "BIRTHDAY PARADOX COLLISION BOUND", fontSize = 11.sp, fontWeight = FontWeight.Black, color = DsaRed)
                Text(
                    text = "Hash Output: ${bitLength.toInt()} bits\nCollision Search Effort: 2^(n/2) = 2^${(bitLength/2).toInt()}\n~ $trialsFor50 trials needed for 50% collision",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = LocalDynamicThemeColors.current.textPrimary,
                    fontFamily = FontFamily.Monospace
                )

                Slider(
                    value = bitLength,
                    onValueChange = { bitLength = it },
                    valueRange = 8f..64f,
                    steps = 6,
                    colors = SliderDefaults.colors(thumbColor = DsaRed, activeTrackColor = DsaRed)
                )
            }
        }
    }
}

// ──────────────────────────────────────────────
// 10. TLS 1.3 Handshake Tracer Visualizer
// ──────────────────────────────────────────────
@Composable
fun TLS13HandshakeVisualizer() {
    var step by remember { mutableIntStateOf(1) }

    val steps = listOf(
        "ClientHello: Supported Groups + X25519 KeyShare",
        "ServerHello: Selected Group + X25519 KeyShare",
        "EncryptedExtensions + Server Certificate (X.509)",
        "CertificateVerify: Ed25519 Signature Verification",
        "Server Finished (HMAC confirmation)",
        "Client Finished ➔ 1-RTT Handshake Complete! 🎉",
        "Encrypted Application Traffic Streaming (AES-256-GCM)"
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
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "TLS 1.3 1-RTT HANDSHAKE SEQUENCE", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFFFF9600))
                steps.take(step).forEachIndexed { idx, desc ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "${idx + 1}.", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(0xFFFF9600))
                        Text(text = desc, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LocalDynamicThemeColors.current.textPrimary)
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DuolingoButton(
                text = "NEXT HANDSHAKE STEP",
                faceColor = Color(0xFFFF9600),
                shadowColor = Color(0xFFC66900),
                onClick = { if (step < steps.size) step += 1 },
                modifier = Modifier.weight(1f)
            )
            DuolingoButton(
                text = "RESET",
                faceColor = DsaRed,
                shadowColor = Color(0xFFCC3B3B),
                onClick = { step = 1 },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
