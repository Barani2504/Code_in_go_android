package com.simats.codeingo.ui.arenas.cipher_sanctum.levels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

@Composable
fun FindTheShiftLevel(onComplete: () -> Unit, onDeductHeart: () -> Unit) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    // Target shift key to discover is K = 3
    val targetShift = 3
    var shiftKey by remember { mutableIntStateOf(0) }

    val testPairs = listOf('H' to 'K', 'E' to 'H', 'L' to 'O', 'P' to 'S')
    val isSolved = shiftKey == targetShift

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
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = AmberGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "CIPHER KEY CALIBRATOR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = AmberGold
                    )
                }
                Text(
                    text = "Calibrate the shift slider until the plaintext test pairs align with their intercepted ciphertext equivalents (H➔K, E➔H, L➔O).",
                    fontSize = 12.5.sp,
                    color = dynamicColors.textPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp
                )
            }
        }

        // Live Alignment Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            testPairs.forEach { (pChar, targetCChar) ->
                val currentShifted = ((pChar - 'A' + shiftKey) % 26 + 'A'.code).toChar()
                val isPairAligned = currentShifted == targetCChar

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(horizontal = 6.dp)
                ) {
                    // Plaintext letter
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                            .border(1.dp, dynamicColors.cardBorder, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$pChar",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = dynamicColors.textPrimary
                        )
                    }

                    Text(text = "↓", fontSize = 11.sp, color = SubtextGray)

                    // Shifted cipher letter
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isPairAligned) DuolingoGreen.copy(alpha = 0.25f)
                                else Color(0xFFEF4444).copy(alpha = 0.15f)
                            )
                            .border(
                                1.5.dp,
                                if (isPairAligned) DuolingoGreen else Color(0xFFEF4444).copy(alpha = 0.6f),
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$currentShifted",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = if (isPairAligned) DuolingoGreen else Color(0xFFEF4444)
                        )
                    }

                    Text(
                        text = "Target: $targetCChar",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPairAligned) DuolingoGreen else SubtextGray
                    )
                }
            }
        }

        // Interactive Calibration Slider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(if (isDark) Color(0xFF10192A) else Color(0xFFF8FAFC))
                .border(1.2.dp, AmberGold.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SHIFT MODULO STEPPER",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = SubtextGray
                    )
                    Text(
                        text = "SHIFT K = +$shiftKey",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (isSolved) DuolingoGreen else AmberGold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(AmberGold.copy(alpha = 0.15f))
                            .border(1.dp, AmberGold, CircleShape)
                            .pressScale()
                            .clickable { shiftKey = (shiftKey - 1 + 26) % 26 },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = null, tint = AmberGold)
                    }

                    Slider(
                        value = shiftKey.toFloat(),
                        onValueChange = { shiftKey = it.toInt() },
                        valueRange = 0f..25f,
                        steps = 24,
                        colors = SliderDefaults.colors(
                            thumbColor = if (isSolved) DuolingoGreen else AmberGold,
                            activeTrackColor = if (isSolved) DuolingoGreen else AmberGold,
                            inactiveTrackColor = SubtextGray.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(AmberGold.copy(alpha = 0.15f))
                            .border(1.dp, AmberGold, CircleShape)
                            .pressScale()
                            .clickable { shiftKey = (shiftKey + 1) % 26 },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = AmberGold)
                    }
                }
            }
        }

        // Action Confirm Button
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (isSolved) Brush.horizontalGradient(listOf(DuolingoGreen, Color(0xFF059669)))
                    else Brush.horizontalGradient(listOf(Color(0xFF64748B), Color(0xFF475569)))
                )
                .pressScale(targetScale = 0.96f)
                .clickable(enabled = isSolved) {
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
                    text = if (isSolved) "PAIRS ALIGNED (K=$targetShift)! ADVANCE →" else "ALIGN ALL 4 TEST PAIRS",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
