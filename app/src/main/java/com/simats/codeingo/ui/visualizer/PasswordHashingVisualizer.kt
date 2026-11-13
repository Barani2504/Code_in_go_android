package com.simats.codeingo.ui.visualizer

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.simats.codeingo.ui.components.motion.pressSquish
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.FeedbackManager
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.PhoenixCrimson
import com.simats.codeingo.ui.theme.PhoenixGreen
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.visualizer.components.VisualizerPlaybackController
import kotlin.math.roundToInt

/**
 * 🔒 Password Hashing, Salt & Memory-Hard KDF Visualizer
 *
 * Implements Phase 4.1:
 * - Compares fast hashes (MD5, SHA-256) vs memory-hard key derivation functions (bcrypt, Argon2id)
 * - Interactive salt generator and work factor (iterations / memory cost) slider
 * - Live cracking time estimator showing defense against GPU cluster brute-force attacks
 */
@Composable
fun PasswordHashingVisualizer(modifier: Modifier = Modifier) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    var password by remember { mutableStateOf("CorrectHorseBatteryStaple") }
    var saltValue by remember { mutableStateOf("s@lt_9f8e7d6c") }
    var workCostExponent by remember { mutableFloatStateOf(12f) } // 2^12 = 4,096 iterations

    val iterations = 1 shl workCostExponent.roundToInt()

    val cardBg = if (isDark) Color(0xFF111827) else Color.White
    val borderColor = if (isDark) Color(0xFF1F2937) else Color(0xFFE2E8F0)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Master Parameter Hub
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(cardBg)
                .border(1.2.dp, borderColor, RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("🔒", fontSize = 16.sp)
                        Text(
                            text = "PASSWORD STORAGE WORK BENCH",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = dynamicColors.textPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .pressSquish {
                                FeedbackManager.tap()
                                val randomHex = (1..8).map { "0123456789abcdef".random() }.joinToString("")
                                saltValue = "s@lt_$randomHex"
                            }
                            .clip(RoundedCornerShape(8.dp))
                            .background(AmberGold.copy(alpha = 0.15f))
                            .border(1.dp, AmberGold, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "🎲 RE-SALT",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Black,
                            color = AmberGold
                        )
                    }
                }

                // Plaintext & Salt Preview
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Plaintext Password:", fontSize = 10.sp, color = SubtextGray)
                        Text(text = password, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Cryptographic Salt (128-bit):", fontSize = 10.sp, color = SubtextGray)
                        Text(text = saltValue, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AmberGold, fontFamily = FontFamily.Monospace)
                    }
                }

                // Work Factor Slider
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Argon2 / bcrypt Work Factor (2^cost):", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary)
                        Text(text = "Cost = ${workCostExponent.toInt()} ($iterations rounds)", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = AmberGold)
                    }
                    Slider(
                        value = workCostExponent,
                        onValueChange = { workCostExponent = it },
                        valueRange = 8f..16f,
                        steps = 7,
                        colors = SliderDefaults.colors(thumbColor = AmberGold, activeTrackColor = AmberGold)
                    )
                }
            }
        }

        // Cracking Resistance Benchmarking Table
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(cardBg)
                .border(1.2.dp, borderColor, RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "ESTIMATED OFFLINE GPU BRUTE FORCE RESISTANCE",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Black,
                    color = AmberGold
                )

                // MD5 Row
                AlgorithmCrackRow(
                    name = "MD5 (Unsalted)",
                    speed = "180 Billion / sec",
                    crackTime = "< 0.001 seconds",
                    isVulnerable = true
                )

                // SHA-256 Row
                AlgorithmCrackRow(
                    name = "SHA-256 (Salted)",
                    speed = "8.5 Billion / sec",
                    crackTime = "~ 0.28 seconds",
                    isVulnerable = true
                )

                // bcrypt Row
                val bcryptTime = when (workCostExponent.toInt()) {
                    in 8..10 -> "~ 1.5 hours"
                    in 11..13 -> "~ 3.8 days"
                    else -> "~ 1.2 years"
                }
                AlgorithmCrackRow(
                    name = "bcrypt (Cost ${workCostExponent.toInt()})",
                    speed = "2,400 / sec",
                    crackTime = bcryptTime,
                    isVulnerable = false
                )

                // Argon2id Row
                val argonTime = when (workCostExponent.toInt()) {
                    in 8..10 -> "~ 4.2 days"
                    in 11..13 -> "~ 2.1 years"
                    else -> "Century+ (Quantum-Hard)"
                }
                AlgorithmCrackRow(
                    name = "Argon2id (64MB, t=3)",
                    speed = "120 / sec (Memory-Bound)",
                    crackTime = argonTime,
                    isVulnerable = false
                )
            }
        }
    }
}

@Composable
private fun AlgorithmCrackRow(
    name: String,
    speed: String,
    crackTime: String,
    isVulnerable: Boolean
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    val tagCol = if (isVulnerable) PhoenixCrimson else PhoenixGreen

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC))
            .border(1.dp, tagCol.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, fontSize = 11.5.sp, fontWeight = FontWeight.Black, color = dynamicColors.textPrimary)
                Text(text = "Speed: $speed", fontSize = 9.5.sp, color = SubtextGray)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(tagCol.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(text = crackTime, fontSize = 10.sp, fontWeight = FontWeight.Black, color = tagCol)
            }
        }
    }
}

