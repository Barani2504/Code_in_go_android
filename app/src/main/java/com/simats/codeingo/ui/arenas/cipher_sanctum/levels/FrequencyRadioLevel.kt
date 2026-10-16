package com.simats.codeingo.ui.arenas.cipher_sanctum.levels

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.theme.pressScale
import kotlin.math.abs

@Composable
fun FrequencyRadioLevel(onComplete: () -> Unit) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    // Frequencies of top letters in English
    val frequencyTable = listOf(
        'E' to 12.7f,
        'T' to 9.1f,
        'A' to 8.2f,
        'O' to 7.5f,
        'I' to 7.0f,
        'N' to 6.7f,
        'S' to 6.3f,
        'H' to 6.1f,
        'R' to 6.0f,
        'D' to 4.3f
    )

    // Target is tuning into letter 'E' (peak at 12.7 MHz)
    var tunedFrequency by remember { mutableFloatStateOf(4.0f) }
    val targetFreq = 12.7f
    val delta = abs(tunedFrequency - targetFreq)
    val isLocked = delta < 0.6f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
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
                        text = "RADIO SPECTRUM CRYPTANALYZER",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = AmberGold
                    )
                }
                Text(
                    text = "Sweep the radio tuner to identify the highest natural English letter frequency (~12.7%). Lock onto the carrier spike to defeat substitution ciphers!",
                    fontSize = 12.5.sp,
                    color = dynamicColors.textPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp
                )
            }
        }

        // Oscilloscope / Spectrum Visualizer Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF090E17))
                .border(1.5.dp, if (isLocked) DuolingoGreen else AmberGold.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
                .padding(12.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxWidth().height(100.dp)) {
                val barWidth = size.width / (frequencyTable.size * 1.5f)
                val maxHeight = size.height

                frequencyTable.forEachIndexed { i, (letter, freq) ->
                    val x = i * (barWidth * 1.5f) + 12f
                    val barHeight = (freq / 14f) * (maxHeight * 0.85f)
                    val y = maxHeight - barHeight

                    val isTargetBar = letter == 'E'
                    val barColor = if (isTargetBar && isLocked) DuolingoGreen
                    else if (isTargetBar) AmberGold
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
                        if (isLocked) DuolingoGreen.copy(alpha = 0.2f)
                        else Color.White.copy(alpha = 0.08f)
                    )
                    .border(
                        1.dp,
                        if (isLocked) DuolingoGreen else SubtextGray.copy(alpha = 0.3f),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (isLocked) "CARRIER LOCKED: 'E' (~12.7%) ✓" else "TUNING SPECTRUM...",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = if (isLocked) DuolingoGreen else AmberGold
                )
            }
        }

        // Tuner Dial Control
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(if (isDark) Color(0xFF10192A) else Color(0xFFF8FAFC))
                .border(1.2.dp, AmberGold.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SPECTRUM DEMODULATOR",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = SubtextGray
                    )
                    Text(
                        text = "${String.format("%.1f", tunedFrequency)}% FREQ",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (isLocked) DuolingoGreen else AmberGold
                    )
                }

                Slider(
                    value = tunedFrequency,
                    onValueChange = { tunedFrequency = it },
                    valueRange = 1.0f..14.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = if (isLocked) DuolingoGreen else AmberGold,
                        activeTrackColor = if (isLocked) DuolingoGreen else AmberGold,
                        inactiveTrackColor = SubtextGray.copy(alpha = 0.3f)
                    )
                )

                // Frequency Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Low Freq (Z, Q)", fontSize = 10.sp, color = SubtextGray)
                    Text(text = "Target: Letter 'E' (12.7%)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AmberGold)
                    Text(text = "14.0% Max", fontSize = 10.sp, color = SubtextGray)
                }
            }
        }

        // Demodulation Success Button
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (isLocked) Brush.horizontalGradient(listOf(DuolingoGreen, Color(0xFF059669)))
                    else Brush.horizontalGradient(listOf(Color(0xFF64748B), Color(0xFF475569)))
                )
                .pressScale(targetScale = 0.96f)
                .clickable(enabled = isLocked) {
                    onComplete()
                },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = if (isLocked) "DEMODULATE & DECRYPT →" else "TUNE CARRIER TO 12.7%",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = Color.White,
                    letterSpacing = 0.6.sp
                )
            }
        }
    }
}
