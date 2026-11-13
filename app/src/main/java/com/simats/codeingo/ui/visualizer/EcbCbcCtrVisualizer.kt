package com.simats.codeingo.ui.visualizer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
import kotlin.random.Random

enum class BlockCipherMode(val title: String, val subtitle: String, val isSecure: Boolean) {
    ECB("Electronic Codebook (ECB)", "Deterministically encrypts identical blocks to identical ciphertext", false),
    CBC("Cipher Block Chaining (CBC)", "Chains prior ciphertext block with IV via XOR before encryption", true),
    CTR("Counter Mode (CTR)", "Encrypts successive counters to generate a stream cipher keystream", true)
}

/**
 * 🐧 ECB vs CBC vs CTR Block Cipher Mode Visualizer
 *
 * Implements Phase 4.1:
 * - Visualizes the famous Penguin image pattern leakage problem in ECB mode.
 * - Compares deterministic ECB encryption with randomized CBC (IV chaining) and CTR (keystream counter).
 * - Interactive block inspection showing why ECB is fundamentally insecure for structured payloads.
 */
@Composable
fun EcbCbcCtrVisualizer(modifier: Modifier = Modifier) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    var selectedMode by remember { mutableStateOf(BlockCipherMode.ECB) }
    var currentStep by remember { mutableIntStateOf(1) }
    var isPlaying by remember { mutableStateOf(false) }
    var playbackSpeed by remember { mutableStateOf(1.0f) }

    val totalSteps = 4 // 1: Plaintext Image, 2: IV/Counter Prep, 3: Block XOR/Encryption, 4: Ciphertext Image

    val cardBg = if (isDark) Color(0xFF111827) else Color.White
    val borderColor = if (isDark) Color(0xFF1F2937) else Color(0xFFE2E8F0)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Mode Selector Tabs (ECB, CBC, CTR)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BlockCipherMode.entries.forEach { mode ->
                val isSelected = selectedMode == mode
                val modeColor = if (mode.isSecure) PhoenixGreen else PhoenixCrimson

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .pressSquish {
                            FeedbackManager.tap()
                            selectedMode = mode
                            currentStep = 1
                        }
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) modeColor.copy(alpha = 0.18f)
                            else if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)
                        )
                        .border(
                            1.2.dp,
                            if (isSelected) modeColor else if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(vertical = 10.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = mode.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isSelected) modeColor else dynamicColors.textPrimary
                        )
                        Text(
                            text = if (mode.isSecure) "SECURE" else "VULNERABLE",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (mode.isSecure) PhoenixGreen else PhoenixCrimson
                        )
                    }
                }
            }
        }

        // Mode Explanation Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (selectedMode.isSecure) PhoenixGreen.copy(alpha = 0.12f) else PhoenixCrimson.copy(alpha = 0.12f))
                .border(1.dp, if (selectedMode.isSecure) PhoenixGreen.copy(alpha = 0.5f) else PhoenixCrimson.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = selectedMode.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = if (selectedMode.isSecure) PhoenixGreen else PhoenixCrimson
                )
                Text(
                    text = selectedMode.subtitle,
                    fontSize = 11.sp,
                    color = dynamicColors.textSecondary,
                    lineHeight = 14.sp
                )
            }
        }

        // Visual Bitmap Simulation Grid (The Penguin Image Analogy)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(cardBg)
                .border(1.2.dp, borderColor, RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "8x8 BLOCK BITMAP VISUALIZATION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = dynamicColors.textPrimary
                )

                // 8x8 Canvas Rendering Penguin Silhouette vs Scrambled Noise
                Canvas(
                    modifier = Modifier
                        .size(192.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                ) {
                    val gridSize = 8
                    val cellSize = size.width / gridSize

                    // 8x8 pattern representing a penguin silhouette: 1 = dark penguin body, 0 = white background
                    val penguinPattern = listOf(
                        listOf(0, 0, 1, 1, 1, 1, 0, 0),
                        listOf(0, 1, 1, 0, 0, 1, 1, 0),
                        listOf(1, 1, 1, 1, 1, 1, 1, 1),
                        listOf(1, 0, 1, 1, 1, 1, 0, 1),
                        listOf(1, 0, 1, 1, 1, 1, 0, 1),
                        listOf(1, 1, 1, 1, 1, 1, 1, 1),
                        listOf(0, 1, 1, 1, 1, 1, 1, 0),
                        listOf(0, 0, 1, 0, 0, 1, 0, 0)
                    )

                    // Seeded random noise for CBC/CTR mode diffusion
                    val noiseRandom = Random(selectedMode.ordinal * 100 + currentStep)

                    for (row in 0 until gridSize) {
                        for (col in 0 until gridSize) {
                            val isBody = penguinPattern[row][col] == 1
                            val cellColor = when {
                                currentStep == 1 -> {
                                    // Original Plaintext Image
                                    if (isBody) Color(0xFF1E293B) else Color(0xFFF1F5F9)
                                }
                                selectedMode == BlockCipherMode.ECB -> {
                                    // ECB: Identical plaintext blocks produce IDENTICAL ciphertext blocks!
                                    // Silhouette pattern remains clearly visible!
                                    if (isBody) Color(0xFFB45309) else Color(0xFFFEF3C7)
                                }
                                else -> {
                                    // CBC or CTR: Diffusion across entire block matrix produces pure pseudorandom noise!
                                    val shade = noiseRandom.nextFloat()
                                    Color(red = shade, green = shade * 0.9f, blue = shade)
                                }
                            }

                            drawRect(
                                color = cellColor,
                                topLeft = Offset(col * cellSize, row * cellSize),
                                size = Size(cellSize - 1f, cellSize - 1f)
                            )
                        }
                    }
                }

                // Inspection Result Text
                val observationText = when {
                    currentStep == 1 -> "Original Plaintext: Structured bitmap with high spatial redundancy."
                    selectedMode == BlockCipherMode.ECB -> "⚠️ CRITICAL FLAW: ECB does not hide pixel patterns! Identical white blocks encrypt to the same ciphertext, leaking the outline."
                    selectedMode == BlockCipherMode.CBC -> "✅ SECURE DIFFUSION: CBC XORs each block with prior ciphertext. The entire image dissolves into uniform pseudorandom noise."
                    selectedMode == BlockCipherMode.CTR -> "✅ SECURE STREAM: CTR turns the block cipher into a stream cipher via counter keystream. Complete pattern destruction."
                    else -> "Transformed ciphertext block output."
                }

                Text(
                    text = observationText,
                    fontSize = 11.5.sp,
                    color = if (selectedMode == BlockCipherMode.ECB && currentStep > 1) PhoenixCrimson else dynamicColors.textSecondary,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 15.sp
                )
            }
        }

        // Visualizer 2.0 Playback Controller
        VisualizerPlaybackController(
            currentStep = currentStep,
            totalSteps = totalSteps,
            onStepChange = { currentStep = it },
            isPlaying = isPlaying,
            onPlayPauseToggle = { isPlaying = !isPlaying },
            playbackSpeed = playbackSpeed,
            onSpeedChange = { playbackSpeed = it },
            stepLabel = "STAGE",
            accentColor = if (selectedMode.isSecure) PhoenixGreen else AmberGold
        )
    }
}
