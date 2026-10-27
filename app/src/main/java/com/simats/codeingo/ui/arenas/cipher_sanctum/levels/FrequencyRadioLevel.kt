package com.simats.codeingo.ui.arenas.cipher_sanctum.levels

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.arenas.crypto.ArenaCryptoEngine
import com.simats.codeingo.ui.components.Tactile3DButton
import com.simats.codeingo.ui.components.Tactile3DButtonColor
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.ArenaSuccessGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray
import kotlin.math.roundToInt

@Composable
fun FrequencyRadioLevel(onComplete: () -> Unit) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    // Seeded English text (97 characters):
    // E appears 14 times (~14.4%), T appears 11 times (~11.3%), A appears 7 times (~7.2%)
    val originalPlaintext = "THE ANCIENT PHOENIX RISES FROM THE ASHES TO RESTORE THE CRYPTOGRAPHIC BALANCE AND PROTECT ALL REALMS"
    val actualShift = 3
    val ciphertext = remember { ArenaCryptoEngine.caesarEncrypt(originalPlaintext, actualShift) }

    // Slider controls candidate decryption shift k (0..25)
    var sliderShift by remember { mutableFloatStateOf(10f) }
    val currentKey = sliderShift.roundToInt().coerceIn(0, 25)

    // Decrypt ciphertext with current key
    val candidatePlaintext = remember(currentKey) {
        ArenaCryptoEngine.caesarDecrypt(ciphertext, currentKey)
    }

    // Top expected English letters and natural distribution percentages
    val referenceEnglishFreq = listOf(
        'E' to 12.7f,
        'T' to 9.1f,
        'A' to 8.2f,
        'O' to 7.5f,
        'I' to 7.0f,
        'N' to 6.7f
    )

    // Compute frequency of letters in candidate plaintext
    val candidateFrequencies = remember(candidatePlaintext) {
        val letterOnly = candidatePlaintext.filter { it.isLetter() }
        val total = letterOnly.length.toFloat().coerceAtLeast(1f)
        val counts = letterOnly.groupingBy { it }.eachCount()
        referenceEnglishFreq.map { (char, _) ->
            val count = counts[char] ?: 0
            char to (count / total) * 100f
        }
    }

    val isLocked = currentKey == actualShift

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Mission Briefing
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(if (isDark) Color(0xFF0F1A2E) else dynamicColors.cardBackground)
                .border(1.2.dp, AmberGold.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = AmberGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "FREQUENCY ANALYSIS SPECTROGRAPH",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = AmberGold
                    )
                }
                Text(
                    text = "Substitution ciphers preserve letter frequency signatures. In English, 'E' accounts for ~12.7% of characters, 'T' ~9.1%. Shift the decryption key until the highest ciphertext frequency aligns with 'E'!",
                    fontSize = 12.sp,
                    color = dynamicColors.textPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }

        // Letter Histogram Spectrum Visualizer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF090E17))
                .border(1.5.dp, if (isLocked) ArenaSuccessGreen else AmberGold.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
                .padding(12.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxWidth().height(100.dp)) {
                val count = candidateFrequencies.size
                val barWidth = size.width / (count * 2f)
                val maxHeight = size.height

                candidateFrequencies.forEachIndexed { i, (letter, freq) ->
                    val x = i * (barWidth * 2f) + barWidth * 0.5f
                    val barHeight = ((freq / 16f) * (maxHeight * 0.85f)).coerceIn(4f, maxHeight)
                    val y = maxHeight - barHeight

                    val isTargetE = letter == 'E'
                    val barColor = if (isTargetE && isLocked) ArenaSuccessGreen
                    else if (isTargetE) AmberGold
                    else Color(0xFF334155)

                    drawRoundRect(
                        color = barColor,
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(6f, 6f)
                    )
                }
            }

            // Target Overlay Banner
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (isLocked) ArenaSuccessGreen.copy(alpha = 0.2f)
                        else Color.White.copy(alpha = 0.08f)
                    )
                    .border(
                        1.dp,
                        if (isLocked) ArenaSuccessGreen else SubtextGray.copy(alpha = 0.3f),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (isLocked) "ALIGNED: 'E' PEAK IDENTIFIED ✓" else "ALIGNING CIPHERTEXT HISTOGRAM...",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = if (isLocked) ArenaSuccessGreen else AmberGold
                )
            }
        }

        // Shift Key Tuner Control
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(if (isDark) Color(0xFF10192A) else Color(0xFFF8FAFC))
                .border(1.2.dp, AmberGold.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DECRYPTION SHIFT KEY (K)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = SubtextGray
                    )
                    Text(
                        text = "K = $currentKey",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (isLocked) ArenaSuccessGreen else AmberGold
                    )
                }

                Slider(
                    value = sliderShift,
                    onValueChange = { sliderShift = it },
                    valueRange = 0f..25f,
                    steps = 24,
                    colors = SliderDefaults.colors(
                        thumbColor = if (isLocked) ArenaSuccessGreen else AmberGold,
                        activeTrackColor = if (isLocked) ArenaSuccessGreen else AmberGold,
                        inactiveTrackColor = SubtextGray.copy(alpha = 0.3f)
                    )
                )

                // Decrypted Preview
                Text(
                    text = "DECRYPTED PREVIEW: \"${candidatePlaintext.take(38)}...\"",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = if (isLocked) ArenaSuccessGreen else dynamicColors.textPrimary
                )
            }
        }

        Tactile3DButton(
            title = if (isLocked) "Break Substitution Ward →" else "Align 'E' Peak (K=3)",
            style = if (isLocked) Tactile3DButtonColor.GREEN else Tactile3DButtonColor.AMBER,
            isEnabled = isLocked,
            onClick = onComplete,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
