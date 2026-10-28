package com.simats.codeingo.ui.arenas.cipher_sanctum.levels

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.arenas.common.ArenaBadgeType
import com.simats.codeingo.ui.arenas.common.ArenaHaptics
import com.simats.codeingo.ui.arenas.common.ArenaPalettes
import com.simats.codeingo.ui.arenas.common.ArenaStatusBadge
import com.simats.codeingo.ui.arenas.common.minTouchTarget48
import com.simats.codeingo.ui.components.Tactile3DButton
import com.simats.codeingo.ui.components.Tactile3DButtonColor
import com.simats.codeingo.ui.theme.ArenaDangerRed
import com.simats.codeingo.ui.theme.ArenaSuccessGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.LocalReduceMotion
import com.simats.codeingo.ui.theme.SubtextGray

@Composable
fun CaesarDialLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val view = LocalView.current
    val reduceMotion = LocalReduceMotion.current
    val isDark = dynamicColors.isDark

    val primary = ArenaPalettes.Arena1Primary
    val accent = ArenaPalettes.Arena1Accent
    val cardBg = if (isDark) ArenaPalettes.Arena1CardDark else dynamicColors.cardBackground

    // Target encrypted intercept: "DWWDFN" (ATTACK shifted by +3)
    val targetCipher = "DWWDFN"
    val targetPlaintext = "ATTACK"
    val targetKey = 3

    var currentShift by remember { mutableIntStateOf(0) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var lastCheckSuccess by remember { mutableStateOf<Boolean?>(null) }

    // Live decoded text: P = (C - shift + 26) mod 26
    val currentDecoded = remember(currentShift) {
        targetCipher.map { c ->
            val shifted = (c - 'A' - currentShift + 26) % 26
            ('A' + shifted)
        }.joinToString("")
    }

    val isSolved = currentShift == targetKey

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Mission Header with Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "CAESAR ROTATIONAL DIAL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = primary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Roman Military Modular Shift K",
                    fontSize = 10.sp,
                    color = dynamicColors.textSecondary
                )
            }
            ArenaStatusBadge(
                type = if (isSolved) ArenaBadgeType.SUCCESS else ArenaBadgeType.ENCRYPTED,
                label = if (isSolved) "SHIFT LOCKED (K=$targetKey)" else "KEY UNCALIBRATED"
            )
        }

        // Mission Briefing Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(cardBg)
                .border(1.dp, primary.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Intercepted Ciphertext: DWWDFN",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = primary
                    )
                }
                Text(
                    text = "Rotate the Caesar Dial wheel to calibrate shift key K. Decrypt ciphertext 'DWWDFN' back into the clear military command 'ATTACK' via P = (C - K) mod 26.",
                    fontSize = 11.sp,
                    color = dynamicColors.textPrimary,
                    lineHeight = 15.sp
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
                            .background(ArenaDangerRed.copy(alpha = 0.15f))
                            .border(1.2.dp, ArenaDangerRed.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$cChar",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = ArenaDangerRed
                        )
                    }

                    Text(text = "↓", fontSize = 11.sp, color = SubtextGray)

                    // Decrypted character (live)
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isCharCorrect) ArenaSuccessGreen.copy(alpha = 0.25f)
                                else cardBg
                            )
                            .border(
                                1.5.dp,
                                if (isCharCorrect) ArenaSuccessGreen else dynamicColors.cardBorder,
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$pChar",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = if (isCharCorrect) ArenaSuccessGreen else dynamicColors.textPrimary
                        )
                    }
                }
            }
        }

        // Live Modular Math Formula Ribbon
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(cardBg)
                .border(1.dp, primary.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "C = (P + $currentShift) mod 26  |  P = (C - $currentShift) mod 26",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = primary
            )
        }

        // Interactive Dial Control Panel
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(cardBg)
                .border(1.2.dp, primary.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ROTATIONAL SHIFT DIAL",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = SubtextGray
                    )
                    Text(
                        text = "KEY K = $currentShift",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (isSolved) ArenaSuccessGreen else primary
                    )
                }

                // Interactive Stepper Buttons + Slider (minTouchTarget48)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Decrement Button
                    Box(
                        modifier = Modifier
                            .minTouchTarget48()
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(primary.copy(alpha = 0.15f))
                            .border(1.dp, primary, CircleShape)
                            .clickable {
                                ArenaHaptics.tap(view, reduceMotion)
                                currentShift = (currentShift - 1 + 26) % 26
                                feedbackMessage = null
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Minus", tint = primary)
                    }

                    // Slider
                    Slider(
                        value = currentShift.toFloat(),
                        onValueChange = {
                            currentShift = it.toInt()
                            feedbackMessage = null
                        },
                        valueRange = 0f..25f,
                        steps = 24,
                        colors = SliderDefaults.colors(
                            thumbColor = if (isSolved) ArenaSuccessGreen else primary,
                            activeTrackColor = if (isSolved) ArenaSuccessGreen else primary,
                            inactiveTrackColor = SubtextGray.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    // Increment Button
                    Box(
                        modifier = Modifier
                            .minTouchTarget48()
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(primary.copy(alpha = 0.15f))
                            .border(1.dp, primary, CircleShape)
                            .clickable {
                                ArenaHaptics.tap(view, reduceMotion)
                                currentShift = (currentShift + 1) % 26
                                feedbackMessage = null
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Plus", tint = primary)
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
                                        ArenaSuccessGreen.copy(alpha = 0.2f)
                                    else Color.Transparent
                                )
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(text = "$letter", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary)
                            Text(text = "$shifted", fontSize = 9.sp, fontWeight = FontWeight.Black, color = primary)
                        }
                    }
                }
            }
        }

        // Live Feedback Banner
        AnimatedVisibility(visible = feedbackMessage != null, enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (lastCheckSuccess == true) ArenaSuccessGreen.copy(alpha = 0.12f)
                        else ArenaDangerRed.copy(alpha = 0.12f)
                    )
                    .border(
                        1.dp,
                        if (lastCheckSuccess == true) ArenaSuccessGreen else ArenaDangerRed,
                        RoundedCornerShape(10.dp)
                    )
                    .padding(10.dp)
            ) {
                Text(
                    text = feedbackMessage ?: "",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (lastCheckSuccess == true) ArenaSuccessGreen else ArenaDangerRed,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Verification & Submission Button
        Tactile3DButton(
            title = if (isSolved) "Caesar Ward Shattered (K=3) ✓" else "Verify Shift Calibration (K=$currentShift)",
            style = if (isSolved) Tactile3DButtonColor.GREEN else Tactile3DButtonColor.AMBER,
            onClick = {
                if (isSolved) {
                    ArenaHaptics.success(view, reduceMotion)
                    lastCheckSuccess = true
                    feedbackMessage = "✓ Decrypted: DWWDFN with shift K=3 reveals military command 'ATTACK'!"
                    onActionSuccess()
                    onComplete()
                } else {
                    ArenaHaptics.error(view, reduceMotion)
                    lastCheckSuccess = false
                    feedbackMessage = "❌ Incorrect Shift K=$currentShift: Decrypts to '$currentDecoded', not 'ATTACK'!"
                    onDeductHeart()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .minTouchTarget48()
        )
    }
}
