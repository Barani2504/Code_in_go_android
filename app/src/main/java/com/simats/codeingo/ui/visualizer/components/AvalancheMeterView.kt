package com.simats.codeingo.ui.visualizer.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.components.motion.pressSquish
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.FeedbackManager
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.PhoenixCrimson
import com.simats.codeingo.ui.theme.PhoenixGreen
import com.simats.codeingo.ui.theme.SubtextGray

/**
 * 🌊 Avalanche Effect Meter & "What If?" Diff Gauge
 *
 * Implements Phase 4.1:
 * - Live avalanche effect calculation across DES, AES and SHA-256
 * - Measures Hamming Distance between original output and perturbed (1-bit flipped) output
 * - Visual target gauge aiming for the ideal 50% strict avalanche criterion (SAC)
 */
@Composable
fun AvalancheMeterView(
    originalHex: String,
    perturbedHex: String,
    totalBits: Int,
    isBitFlipped: Boolean,
    onToggleBitFlip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    // Calculate Hamming Distance between originalHex and perturbedHex
    val hammingDistance = calculateHammingDistance(originalHex, perturbedHex)
    val avalanchePercentage = if (totalBits > 0) {
        (hammingDistance.toFloat() / totalBits.toFloat()) * 100f
    } else 0f

    val animatedPercent by animateFloatAsState(
        targetValue = avalanchePercentage,
        label = "avalancheProgress"
    )

    // Avalanche Quality Assessment
    // Strict Avalanche Criterion states each output bit changes with probability p = 0.5 (45% - 55% is ideal)
    val isIdeal = avalanchePercentage in 45f..55f
    val statusColor = when {
        isIdeal -> PhoenixGreen
        avalanchePercentage in 35f..65f -> AmberGold
        else -> PhoenixCrimson
    }

    val statusText = when {
        !isBitFlipped -> "BASELINE • No bit flip applied"
        isIdeal -> "EXCELLENT AVALANCHE (Strict Avalanche Criterion Met 50% ± 5%)"
        avalanchePercentage in 35f..65f -> "GOOD DIFFUSION ($hammingDistance / $totalBits bits changed)"
        else -> "WEAK DIFFUSION • Structural pattern detectable"
    }

    val cardBg = if (isDark) Color(0xFF111827) else Color.White
    val borderColor = if (isDark) Color(0xFF1F2937) else Color(0xFFE2E8F0)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(cardBg)
            .border(1.2.dp, borderColor, RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header: Title & Interactive 1-Bit Flip Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "🌊", fontSize = 16.sp)
                    Text(
                        text = "AVALANCHE EFFECT METER",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = dynamicColors.textPrimary
                    )
                }

                // Interactive "What-If" Bit Flipper Button
                Box(
                    modifier = Modifier
                        .pressSquish {
                            FeedbackManager.tap()
                            onToggleBitFlip()
                        }
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isBitFlipped) AmberGold else if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)
                        )
                        .border(
                            1.dp,
                            if (isBitFlipped) AmberGold else if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = if (isBitFlipped) "⚡ BIT 0 FLIPPED" else "FLIP 1 BIT",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isBitFlipped) Color.Black else dynamicColors.textPrimary
                    )
                }
            }

            // Avalanche Gauge Progress Bar
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Bit Diff: $hammingDistance / $totalBits bits",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = statusColor
                    )
                    Text(
                        text = "${"%.1f".format(animatedPercent)}% (Target: 50%)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = statusColor
                    )
                }

                LinearProgressIndicator(
                    progress = { (animatedPercent / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = statusColor,
                    trackColor = if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.06f),
                    strokeCap = StrokeCap.Round
                )
            }

            // Output Comparison Diff
            if (isBitFlipped) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Original:", fontSize = 10.sp, color = SubtextGray)
                        Text(
                            text = originalHex.take(32) + if (originalHex.length > 32) "…" else "",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = dynamicColors.textPrimary
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Perturbed:", fontSize = 10.sp, color = AmberGold)
                        Text(
                            text = perturbedHex.take(32) + if (perturbedHex.length > 32) "…" else "",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = AmberGold
                        )
                    }
                }
            }

            // Status message
            Text(
                text = statusText,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = statusColor
            )
        }
    }
}

/**
 * Computes bitwise Hamming distance between two hex strings.
 */
fun calculateHammingDistance(hexA: String, hexB: String): Int {
    val cleanA = hexA.trim().removePrefix("0x").uppercase()
    val cleanB = hexB.trim().removePrefix("0x").uppercase()
    val minLen = minOf(cleanA.length, cleanB.length)
    var diffBits = 0

    for (i in 0 until minLen) {
        val valA = cleanA[i].digitToIntOrNull(16) ?: 0
        val valB = cleanB[i].digitToIntOrNull(16) ?: 0
        diffBits += Integer.bitCount(valA xor valB)
    }

    // Account for length mismatches in bits
    diffBits += kotlin.math.abs(cleanA.length - cleanB.length) * 4
    return diffBits
}

