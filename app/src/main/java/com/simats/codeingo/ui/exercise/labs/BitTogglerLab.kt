package com.simats.codeingo.ui.exercise.labs

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import kotlin.random.Random

/**
 * 🏕️ BitTogglerLab — Chapter 0 Micro-Lab
 * Interactive 8-bit switchboard demonstrating:
 *  - Individual bit toggling and weights (128, 64, 32, 16, 8, 4, 2, 1)
 *  - 4-bit Nibble grouping and Hexadecimal correspondence
 *  - Decimal summation (0..255)
 *  - ASCII character mapping (e.g. 65 -> 'A')
 *  - Optional Target Value challenge mode
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BitTogglerLab(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {}
) {
    val dynamicColors = LocalDynamicThemeColors.current

    // 8 bits: index 0 is bit 7 (MSB, weight 128), index 7 is bit 0 (LSB, weight 1)
    var bits by remember { mutableStateOf(IntArray(8) { 0 }) }
    var targetValue by remember { mutableIntStateOf(65) } // 'A' by default
    var challengeMode by remember { mutableStateOf(false) }

    // Computed values
    val decimalValue = bits.fold(0) { acc, bit -> (acc shl 1) or bit }
    val hexHigh = (decimalValue shr 4).toString(16).uppercase()
    val hexLow = (decimalValue and 0x0F).toString(16).uppercase()
    val hexString = "0x$hexHigh$hexLow"

    val asciiChar = if (decimalValue in 32..126) {
        decimalValue.toChar().toString()
    } else if (decimalValue == 0) {
        "NUL (0)"
    } else if (decimalValue == 10) {
        "\\n (LF)"
    } else if (decimalValue == 13) {
        "\\r (CR)"
    } else {
        "• Unprintable •"
    }

    val isTargetMatched = challengeMode && decimalValue == targetValue

    val slateAccent = Color(0xFF64748B)
    val cyanGlow = Color(0xFF06B6D4)
    val amberGlow = Color(0xFFF59E0B)
    val successGreen = Color(0xFF10B981)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(dynamicColors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // ── TOP NAV HEADER ──────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = dynamicColors.textSecondary)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🏕️", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "BIT TOGGLER LAB",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = dynamicColors.textPrimary
                    )
                }
                Text(
                    "Chapter 0: Foundations Primer",
                    fontSize = 11.sp,
                    color = slateAccent,
                    fontWeight = FontWeight.Bold
                )
            }

            // Challenge Mode Toggle
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (challengeMode) amberGlow.copy(alpha = 0.2f) else dynamicColors.inputBackground)
                    .border(
                        1.dp,
                        if (challengeMode) amberGlow else slateAccent.copy(alpha = 0.3f),
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { challengeMode = !challengeMode }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (challengeMode) "🎯 Challenge" else "🕹️ Sandbox",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (challengeMode) amberGlow else dynamicColors.textSecondary
                )
            }
        }

        // ── SCROLLABLE LAB WORKSPACE ────────────────────────────────
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Target Challenge Banner (if challenge mode enabled)
            if (challengeMode) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isTargetMatched) successGreen.copy(alpha = 0.15f) else amberGlow.copy(alpha = 0.12f))
                        .border(
                            1.5.dp,
                            if (isTargetMatched) successGreen else amberGlow.copy(alpha = 0.5f),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (isTargetMatched) "🎉 TARGET MATCHED!" else "🎯 TARGET GOAL",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isTargetMatched) successGreen else amberGlow
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Build decimal $targetValue (Hex 0x${targetValue.toString(16).uppercase().padStart(2, '0')}${if (targetValue in 32..126) " / '${targetValue.toChar()}'" else ""})",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = dynamicColors.textPrimary
                            )
                        }
                        IconButton(
                            onClick = {
                                targetValue = Random.nextInt(1, 256)
                            }
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "New Target", tint = amberGlow)
                        }
                    }
                }
            }

            // ── LIVE VALUES HUD CARD ────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(dynamicColors.cardBackground)
                    .border(1.5.dp, slateAccent.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "LIVE BYTE READOUT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = slateAccent,
                        letterSpacing = 1.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Decimal Value
                        Column {
                            Text("DECIMAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textSecondary)
                            Text(
                                text = "$decimalValue",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = cyanGlow,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Hex Value (with nibble coloring)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("HEXADECIMAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textSecondary)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("0x", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = slateAccent)
                                Text(hexHigh, fontSize = 32.sp, fontWeight = FontWeight.Black, color = cyanGlow, fontFamily = FontFamily.Monospace)
                                Text(hexLow, fontSize = 32.sp, fontWeight = FontWeight.Black, color = amberGlow, fontFamily = FontFamily.Monospace)
                            }
                        }

                        // ASCII Value
                        Column(horizontalAlignment = Alignment.End) {
                            Text("ASCII CHAR", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textSecondary)
                            Text(
                                text = asciiChar,
                                fontSize = if (asciiChar.length > 2) 16.sp else 32.sp,
                                fontWeight = FontWeight.Black,
                                color = dynamicColors.textPrimary,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // Nibble explanation indicator
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(dynamicColors.inputBackground)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(cyanGlow))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("High Nibble (bits 7..4) = $hexHigh", fontSize = 11.sp, color = dynamicColors.textSecondary)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(amberGlow))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Low Nibble (bits 3..0) = $hexLow", fontSize = 11.sp, color = dynamicColors.textSecondary)
                        }
                    }
                }
            }

            // ── 8-BIT SWITCHBOARD ────────────────────────────────────
            Text(
                "TAP SWITCHES TO FLIP BITS (1 BYTE = 8 BITS)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = slateAccent,
                letterSpacing = 0.5.sp
            )

            // High Nibble (bits 7 down to 4)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "High Nibble (Weight 128 → 16) • Maps to Hex '$hexHigh'",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = cyanGlow
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (i in 0..3) {
                        val bitIndex = 7 - i
                        val weight = 1 shl bitIndex
                        val isBitOn = bits[i] == 1
                        BitSwitchTile(
                            bitIndex = bitIndex,
                            weight = weight,
                            isOn = isBitOn,
                            accentColor = cyanGlow,
                            modifier = Modifier.weight(1f),
                            onToggle = {
                                val next = bits.clone()
                                next[i] = if (next[i] == 1) 0 else 1
                                bits = next
                            }
                        )
                    }
                }
            }

            // Low Nibble (bits 3 down to 0)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "Low Nibble (Weight 8 → 1) • Maps to Hex '$hexLow'",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = amberGlow
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (i in 4..7) {
                        val bitIndex = 7 - i
                        val weight = 1 shl bitIndex
                        val isBitOn = bits[i] == 1
                        BitSwitchTile(
                            bitIndex = bitIndex,
                            weight = weight,
                            isOn = isBitOn,
                            accentColor = amberGlow,
                            modifier = Modifier.weight(1f),
                            onToggle = {
                                val next = bits.clone()
                                next[i] = if (next[i] == 1) 0 else 1
                                bits = next
                            }
                        )
                    }
                }
            }

            // ── PRESET VALUES CHIPS ──────────────────────────────────
            Text(
                "QUICK PRESETS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = slateAccent,
                letterSpacing = 0.5.sp
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PresetChip(label = "0 (All 0s)") {
                    bits = IntArray(8) { 0 }
                }
                PresetChip(label = "65 ('A' / 0x41)") {
                    bits = intArrayOf(0, 1, 0, 0, 0, 0, 0, 1)
                }
                PresetChip(label = "90 ('Z' / 0x5A)") {
                    bits = intArrayOf(0, 1, 0, 1, 1, 0, 1, 0)
                }
                PresetChip(label = "127 (0x7F / Max 7-bit)") {
                    bits = intArrayOf(0, 1, 1, 1, 1, 1, 1, 1)
                }
                PresetChip(label = "128 (0x80 / MSB Only)") {
                    bits = intArrayOf(1, 0, 0, 0, 0, 0, 0, 0)
                }
                PresetChip(label = "255 (0xFF / All 1s)") {
                    bits = IntArray(8) { 1 }
                }
                PresetChip(label = "🎲 Random Byte") {
                    val r = Random.nextInt(0, 256)
                    val next = IntArray(8)
                    for (b in 0..7) {
                        next[b] = (r shr (7 - b)) and 1
                    }
                    bits = next
                }
            }

            // ── PEDAGOGICAL INSIGHT CARDS ─────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(dynamicColors.cardBackground)
                    .border(1.dp, slateAccent.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💡", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Why Cryptography Uses Hex",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = dynamicColors.textPrimary
                        )
                    }
                    Text(
                        text = "1. Binary (0s and 1s) is hard for humans to read: 256-bit AES keys would take 256 characters of 0s and 1s!\n\n" +
                                "2. Every 4 bits (a 'nibble') equals exactly one hexadecimal digit (0 to F):\n" +
                                "   • 0000 = 0x0   • 1010 = 0xA (10)\n" +
                                "   • 0101 = 0x5   • 1111 = 0xF (15)\n\n" +
                                "3. Therefore, 1 byte = 8 bits = exactly 2 hex characters. A 128-bit key is simply 32 hex digits, and a 256-bit key is 64 hex digits.",
                        fontSize = 12.sp,
                        color = dynamicColors.textSecondary,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun BitSwitchTile(
    bitIndex: Int,
    weight: Int,
    isOn: Boolean,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onToggle: () -> Unit
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val tileBg by animateColorAsState(
        targetValue = if (isOn) accentColor.copy(alpha = 0.22f) else dynamicColors.inputBackground,
        animationSpec = tween(180),
        label = "bitBg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isOn) accentColor else dynamicColors.inputBorder,
        animationSpec = tween(180),
        label = "bitBorder"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(tileBg)
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onToggle)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "b$bitIndex",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = dynamicColors.textSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isOn) "1" else "0",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = if (isOn) accentColor else dynamicColors.textSecondary,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$weight",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isOn) dynamicColors.textPrimary else dynamicColors.textSecondary.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun PresetChip(
    label: String,
    onClick: () -> Unit
) {
    val dynamicColors = LocalDynamicThemeColors.current
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(dynamicColors.inputBackground)
            .border(1.dp, dynamicColors.inputBorder, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = dynamicColors.textPrimary,
            fontFamily = FontFamily.Monospace
        )
    }
}
