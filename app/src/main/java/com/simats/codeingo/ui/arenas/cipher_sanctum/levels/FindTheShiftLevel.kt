package com.simats.codeingo.ui.arenas.cipher_sanctum.levels

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Search
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
fun FindTheShiftLevel(
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

    // Target shift key to discover is K = 3
    val targetShift = 3
    var shiftKey by remember { mutableIntStateOf(0) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var lastCheckSuccess by remember { mutableStateOf<Boolean?>(null) }

    val testPairs = remember {
        listOf('H' to 'K', 'E' to 'H', 'L' to 'O', 'P' to 'S')
    }
    val isSolved = shiftKey == targetShift

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
                    text = "KNOWN-PLAINTEXT CALIBRATOR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = primary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Recover Shift Key K from Known Pairs",
                    fontSize = 10.sp,
                    color = dynamicColors.textSecondary
                )
            }
            ArenaStatusBadge(
                type = if (isSolved) ArenaBadgeType.SUCCESS else ArenaBadgeType.INFO,
                label = if (isSolved) "PAIRS MATCHED (K=$targetShift)" else "CALIBRATING..."
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
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Known-Plaintext Cryptanalysis:",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = primary
                    )
                }
                Text(
                    text = "In a known-plaintext attack, the cryptanalyst has access to corresponding pairs of plaintext and ciphertext. Calibrate the shift key K so all 4 pairs align simultaneously: (H➔K, E➔H, L➔O, P➔S).",
                    fontSize = 11.sp,
                    color = dynamicColors.textPrimary,
                    lineHeight = 15.sp
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
                            .background(cardBg)
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
                                if (isPairAligned) ArenaSuccessGreen.copy(alpha = 0.2f)
                                else ArenaDangerRed.copy(alpha = 0.15f)
                            )
                            .border(
                                1.5.dp,
                                if (isPairAligned) ArenaSuccessGreen else ArenaDangerRed.copy(alpha = 0.6f),
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$currentShifted",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = if (isPairAligned) ArenaSuccessGreen else ArenaDangerRed
                            )
                            Text(
                                text = "exp: $targetCChar",
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace,
                                color = if (isPairAligned) ArenaSuccessGreen else SubtextGray
                            )
                        }
                    }
                }
            }
        }

        // Interactive Calibration Control Panel
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
                        text = "TEST KEY CANDIDATE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = SubtextGray
                    )
                    Text(
                        text = "K = $shiftKey",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (isSolved) ArenaSuccessGreen else primary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .minTouchTarget48()
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(primary.copy(alpha = 0.15f))
                            .border(1.dp, primary, CircleShape)
                            .clickable {
                                ArenaHaptics.tap(view, reduceMotion)
                                shiftKey = (shiftKey - 1 + 26) % 26
                                feedbackMessage = null
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Minus", tint = primary)
                    }

                    Slider(
                        value = shiftKey.toFloat(),
                        onValueChange = {
                            shiftKey = it.toInt()
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

                    Box(
                        modifier = Modifier
                            .minTouchTarget48()
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(primary.copy(alpha = 0.15f))
                            .border(1.dp, primary, CircleShape)
                            .clickable {
                                ArenaHaptics.tap(view, reduceMotion)
                                shiftKey = (shiftKey + 1) % 26
                                feedbackMessage = null
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Plus", tint = primary)
                    }
                }
            }
        }

        // Live Feedback Message
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

        // Active Submission Button
        Tactile3DButton(
            title = if (isSolved) "All 4 Pairs Aligned (K=3) ✓" else "Verify Key Calibration (K=$shiftKey)",
            style = if (isSolved) Tactile3DButtonColor.GREEN else Tactile3DButtonColor.AMBER,
            onClick = {
                if (isSolved) {
                    ArenaHaptics.success(view, reduceMotion)
                    lastCheckSuccess = true
                    feedbackMessage = "✓ Verified: Shift K=3 satisfies all 4 known plaintext-ciphertext constraints!"
                    onActionSuccess()
                    onComplete()
                } else {
                    ArenaHaptics.error(view, reduceMotion)
                    lastCheckSuccess = false
                    val firstMismatch = testPairs.firstOrNull { (p, target) ->
                        ((p - 'A' + shiftKey) % 26 + 'A'.code).toChar() != target
                    }
                    val mismatchDetail = if (firstMismatch != null) {
                        val actual = ((firstMismatch.first - 'A' + shiftKey) % 26 + 'A'.code).toChar()
                        "Shift K=$shiftKey maps '${firstMismatch.first}' to '$actual', expected '${firstMismatch.second}'!"
                    } else "Mismatch detected."
                    feedbackMessage = "❌ Incorrect Calibration: $mismatchDetail"
                    onDeductHeart()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .minTouchTarget48()
        )
    }
}
