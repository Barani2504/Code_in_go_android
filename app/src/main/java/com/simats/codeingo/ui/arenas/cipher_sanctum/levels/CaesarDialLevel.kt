package com.simats.codeingo.ui.arenas.cipher_sanctum.levels

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
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
fun CaesarDialLevel(onComplete: () -> Unit) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    // Target encrypted intercept: "DWWDFN" (ATTACK shifted by +3)
    val targetCipher = "DWWDFN"
    val targetPlaintext = "ATTACK"
    val targetKey = 3

    var currentShift by remember { mutableIntStateOf(0) }

    // Live decoded text based on currentShift: P = (C - shift) mod 26
    val currentDecoded = remember(currentShift) {
        targetCipher.map { c ->
            val shifted = (c - 'A' - currentShift + 26) % 26
            ('A' + shifted)
        }.joinToString("")
    }

    val isSolved = currentShift == targetKey

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
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = AmberGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "INTERCEPTED ROMAN MILITARY CIPHER",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = AmberGold
                    )
                }
                Text(
                    text = "Rotate the Caesar Dial wheel to calibrate shift key K. Decrypt ciphertext 'DWWDFN' back into the clear military command 'ATTACK'.",
                    fontSize = 12.5.sp,
                    color = dynamicColors.textPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp
                )
            }
        }

        // Intercept Display Board
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            targetCipher.forEachIndexed { idx, cChar ->
                val pChar = currentDecoded[idx]
                val isCharCorrect = pChar == targetPlaintext[idx]

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    // Cipher character (fixed)
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFEF4444).copy(alpha = 0.15f))
                            .border(1.2.dp, Color(0xFFEF4444).copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$cChar",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFFEF4444)
                        )
                    }

                    Text(text = "↓", fontSize = 11.sp, color = SubtextGray)

                    // Decrypted character (live)
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isCharCorrect) DuolingoGreen.copy(alpha = 0.25f)
                                else if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)
                            )
                            .border(
                                1.5.dp,
                                if (isCharCorrect) DuolingoGreen else dynamicColors.cardBorder,
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$pChar",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = if (isCharCorrect) DuolingoGreen else dynamicColors.textPrimary
                        )
                    }
                }
            }
        }

        // Live Modular Math Formula Ribbon
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (isDark) Color(0xFF131D33) else Color(0xFFF8FAFC))
                .border(1.dp, AmberGold.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = "C = (P + $currentShift) mod 26  |  P = (C - $currentShift) mod 26",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = AmberGold
            )
        }

        // Interactive Dial Control Panel
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    if (isDark) Color(0xFF10192A) else Color(0xFFF8FAFC)
                )
                .border(1.5.dp, AmberGold.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ROTATIONAL SHIFT DIAL",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = SubtextGray
                    )
                    Text(
                        text = "KEY K = $currentShift",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (isSolved) DuolingoGreen else AmberGold
                    )
                }

                // Interactive Stepper Buttons + Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Decrement Button
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(AmberGold.copy(alpha = 0.15f))
                            .border(1.dp, AmberGold, CircleShape)
                            .pressScale()
                            .clickable {
                                currentShift = (currentShift - 1 + 26) % 26
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Minus", tint = AmberGold)
                    }

                    // Slider
                    Slider(
                        value = currentShift.toFloat(),
                        onValueChange = { currentShift = it.toInt() },
                        valueRange = 0f..25f,
                        steps = 24,
                        colors = SliderDefaults.colors(
                            thumbColor = if (isSolved) DuolingoGreen else AmberGold,
                            activeTrackColor = if (isSolved) DuolingoGreen else AmberGold,
                            inactiveTrackColor = SubtextGray.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    // Increment Button
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(AmberGold.copy(alpha = 0.15f))
                            .border(1.dp, AmberGold, CircleShape)
                            .pressScale()
                            .clickable {
                                currentShift = (currentShift + 1) % 26
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Plus", tint = AmberGold)
                    }
                }

                // Alphabet Wheel Strip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ('A'..'Z').forEach { letter ->
                        val shifted = ('A' + (letter - 'A' + currentShift) % 26)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (isSolved && (letter == 'A' || letter == 'T' || letter == 'C' || letter == 'K'))
                                        DuolingoGreen.copy(alpha = 0.2f)
                                    else Color.Transparent
                                )
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(text = "$letter", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary)
                            Text(text = "$shifted", fontSize = 9.sp, fontWeight = FontWeight.Black, color = AmberGold)
                        }
                    }
                }
            }
        }

        // Completion Action Button
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
                    text = if (isSolved) "CIPHERTEXT CRACKED! ADVANCE →" else "ROTATE DIAL TO DECRYPT (K=$targetKey)",
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
