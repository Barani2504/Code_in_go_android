package com.simats.codeingo.ui.visualizer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.data.model.DSAVisualizerType
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DsaRed
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.InputBorder
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.visualizer.engine.HmacPasswordEngine
import com.simats.codeingo.ui.visualizer.model.CNSLabMode
import kotlin.math.roundToInt

@Composable
fun HMACCollisionVisualizer(mode: CNSLabMode = CNSLabMode.EXPLORE) {
    when (mode) {
        CNSLabMode.CHALLENGE -> {
            CNSChallengeCardView(labType = DSAVisualizerType.SORTING)
        }
        CNSLabMode.CODE_TEST -> {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                CNSTestVectorCardView(labType = DSAVisualizerType.SORTING)
                CNSLanguageImplementationCardView(structureName = DSAVisualizerType.SORTING.title)
            }
        }
        CNSLabMode.ATTACK -> {
            HMACCollisionAttackView()
        }
        CNSLabMode.EXPLORE -> {
            HMACCollisionExploreView()
        }
    }
}

@Composable
private fun HMACCollisionAttackView() {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    val amberColor = if (isDark) AmberGold else com.simats.codeingo.ui.theme.DsaOrange
    val redColor = if (isDark) DsaRed else androidx.compose.ui.graphics.Color(0xFFDC2626)
    val greenColor = if (isDark) DuolingoGreen else androidx.compose.ui.graphics.Color(0xFF15803D)

    var collisionBits by remember { mutableIntStateOf(12) }
    val collisionResult = remember(collisionBits) {
        HmacPasswordEngine.findTruncatedCollision(collisionBits)
    }

    val pbkdf2Result = remember {
        HmacPasswordEngine.benchmarkPbkdf2("UserPassword123!", ByteArray(16) { 0x11.toByte() }, 10000)
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                .background(DsaRed.copy(alpha = 0.15f)).border(1.dp, DsaRed.copy(alpha = 0.5f), RoundedCornerShape(10.dp)).padding(12.dp)
        ) {
            Text(
                text = "🔓 ATTACK SUITE: Birthday Collision Simulation & PBKDF2 Cracking Cost Analysis",
                fontSize = 11.sp, fontWeight = FontWeight.Black, color = redColor
            )
        }

        // Birthday Paradox Collision Finder
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, redColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "🎂 BIRTHDAY BOUND COLLISION SEARCH ($collisionBits BITS)", fontSize = 11.5.sp, fontWeight = FontWeight.Black, color = redColor)
                Slider(
                    value = collisionBits.toFloat(),
                    onValueChange = { collisionBits = it.roundToInt().coerceIn(8, 16) },
                    valueRange = 8f..16f,
                    steps = 7,
                    colors = SliderDefaults.colors(thumbColor = DsaRed, activeTrackColor = DsaRed)
                )

                Text(text = "Theoretical Bound ≈ 1.25 × 2^(k/2) = ${collisionResult.expectedSteps} trials", fontSize = 10.5.sp, color = SubtextGray)
                Text(text = "Actual steps until collision: ${collisionResult.actualSteps}", fontSize = 13.sp, fontWeight = FontWeight.Black, color = amberColor)
                Text(text = "Collision found between \"${collisionResult.input1}\" and \"${collisionResult.input2}\"", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = dynamicColors.textPrimary)
                Text(text = "Matched Truncated Hash: 0x${collisionResult.matchedHashTruncatedHex}", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = greenColor)
            }
        }

        // PBKDF2 On-Device Benchmark
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, InputBorder, RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "⏱️ REAL DEVICE PBKDF2 BENCHMARK", fontSize = 11.5.sp, fontWeight = FontWeight.Black, color = amberColor)
                Text(text = "Iterations: 10,000  ->  Elapsed: ${pbkdf2Result.elapsedMs} ms", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = greenColor)
                Text(text = "Estimated Time to Brute Force 10M guesses: ${"%.1f".format(pbkdf2Result.crackingCostEstimateSec)} seconds", fontSize = 10.sp, color = SubtextGray)
            }
        }
    }
}

@Composable
private fun HMACCollisionExploreView() {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    val amberColor = if (isDark) AmberGold else com.simats.codeingo.ui.theme.DsaOrange
    val redColor = if (isDark) DsaRed else androidx.compose.ui.graphics.Color(0xFFDC2626)

    val keyInput = "SecretPasswordKey"
    val msgInput = "Transfer $500 to Bob"
    val hmacTrace = remember {
        HmacPasswordEngine.computeHmacTrace(keyInput.toByteArray(Charsets.UTF_8), msgInput.toByteArray(Charsets.UTF_8))
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
            }
        }
    }
}
