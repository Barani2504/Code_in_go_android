package com.simats.codeingo.ui.onboarding

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.components.AppButton
import com.simats.codeingo.ui.components.AppButtonStyle
import com.simats.codeingo.ui.components.AppCard
import com.simats.codeingo.ui.components.AppChip
import com.simats.codeingo.ui.phoenix.PhoenixAnimatedMascotView
import com.simats.codeingo.ui.phoenix.PhoenixMascotPose
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.PhoenixGreen
import com.simats.codeingo.ui.theme.PhoenixMotion
import com.simats.codeingo.ui.theme.pressScale
import com.simats.codeingo.ui.theme.shake
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

// ══════════════════════════════════════════════════════════════════
// 🌡️ CreativeTemperatureGaugeView — Interactive Memory Pointer Game
// Exact Parity with iOS CreativeTemperatureGaugeView.swift
// Array: nums = [14, 32, 58, 77, 91] -> Find nums[3] == 77 in O(1)
// ══════════════════════════════════════════════════════════════════

@Composable
fun CreativeTemperatureGaugeView(
    modifier: Modifier = Modifier,
    onCorrectAnswer: () -> Unit
) {
    val arrayElements = remember { listOf(14, 32, 58, 77, 91) }
    val targetIndex = 3

    var selectedIndex by remember { mutableIntStateOf(1) }
    var isChecked by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    var shakeTrigger by remember { mutableStateOf<Any?>(null) }

    val scope = rememberCoroutineScope()
    val view = LocalView.current
    val isDark = LocalDynamicThemeColors.current.isDark

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val laserPulse by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laserPulse"
    )

    val isCurrentCorrect = selectedIndex == targetIndex

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Header: DSA Challenge & Array Declaration
        AppCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 18.dp,
            accentGlow = AmberGold.copy(alpha = 0.20f),
            contentPadding = 14.dp
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = AmberGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "DSA ARENA CHALLENGE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.SansSerif,
                            color = AmberGold,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF00_CD_9C).copy(alpha = 0.18f))
                            .border(1.dp, Color(0xFF00_CD_9C).copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "O(1) ACCESS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF00_CD_9C)
                        )
                    }
                }

                Text(
                    text = "nums = [14, 32, 58, 77, 91]",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = if (isDark) Color.White else Color(0xFF12_18_26)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Align Memory Pointer to: ",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        color = if (isDark) Color.White.copy(alpha = 0.70f) else Color(0xFF64_74_8B)
                    )
                    Text(
                        text = "nums[ ? ] == 77",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = AmberGold
                    )
                }
            }
        }

        // 2. Memory Rack Stage with Animated Phoenix Mascot
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Contiguous Memory Rack Gauge
            Column(
                modifier = Modifier
                    .weight(1.1f)
                    .shake(shakeTrigger),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Active Memory Readout Pill
                val activeColor = if (isChecked) {
                    if (isCorrect) PhoenixGreen else Color(0xFFE8_24_10)
                } else if (isCurrentCorrect) AmberGold else Color(0xFF1C_B0_F6)

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(activeColor.copy(alpha = 0.18f))
                        .border(1.2.dp, activeColor.copy(alpha = 0.50f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "nums[$selectedIndex] = ${arrayElements[selectedIndex]}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = activeColor
                    )
                }

                // 5 Stacked Array Slots
                arrayElements.forEachIndexed { idx, value ->
                    val isSlotSelected = idx == selectedIndex
                    val isTarget = idx == targetIndex
                    val slotBorderColor = if (isSlotSelected) {
                        if (isChecked) (if (isCorrect) PhoenixGreen else Color(0xFFE8_24_10)) else AmberGold
                    } else if (isDark) Color.White.copy(alpha = 0.10f) else Color(0xFFD7_DE_EB)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pressScale(targetScale = 0.96f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSlotSelected) activeColor.copy(alpha = if (isDark) 0.25f else 0.18f)
                                else (if (isDark) Color(0xFF14_1F_36).copy(alpha = 0.75f) else Color(0xFFF1_F4_FA))
                            )
                            .border(if (isSlotSelected) 2.dp else 1.dp, slotBorderColor, RoundedCornerShape(12.dp))
                            .clickable(enabled = !isChecked) {
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                selectedIndex = idx
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "[$idx]",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (isSlotSelected) activeColor else (if (isDark) Color.White.copy(alpha = 0.5f) else Color(0xFF71_80_96))
                        )
                        Text(
                            text = "$value",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = if (isSlotSelected) activeColor else (if (isDark) Color.White else Color(0xFF12_18_26))
                        )
                        if (isSlotSelected) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = activeColor,
                                modifier = Modifier
                                    .size(16.dp)
                                    .scale(laserPulse)
                            )
                        } else {
                            Spacer(modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Mascot Feedback Stage with Pose Reaction
            Column(
                modifier = Modifier.weight(0.9f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val currentPose = when {
                    isChecked && isCorrect -> PhoenixMascotPose.Noting(isWriting = false, showTick = true)
                    isChecked && !isCorrect -> PhoenixMascotPose.TemperatureReacting(temp = 0.0, isSuccess = false, isError = true)
                    selectedIndex == targetIndex -> PhoenixMascotPose.Noting(isWriting = false, showTick = false)
                    else -> PhoenixMascotPose.Welcoming
                }

                PhoenixAnimatedMascotView(
                    pose = currentPose,
                    size = 135.dp
                )

                // Dynamic Speech Reaction Bubble
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDark) Color(0xFF14_1E_37).copy(alpha = 0.90f) else Color.White)
                        .border(1.dp, AmberGold.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = when {
                            isChecked && isCorrect -> "🎯 O(1) Instant Match!"
                            isChecked && !isCorrect -> "⚠️ Look at Index [3]!"
                            selectedIndex == targetIndex -> "🔥 Target 77 locked!"
                            else -> "Pointer at Index [$selectedIndex]"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        color = if (isDark) Color.White else Color(0xFF12_18_26),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // 3. Stepper & Quick Select Options
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .pressScale(targetScale = 0.90f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDark) Color(0xFF14_1F_36) else Color(0xFFE2_E8_F0))
                    .clickable(enabled = !isChecked && selectedIndex > 0) {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        selectedIndex--
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Prev",
                    tint = if (selectedIndex > 0) AmberGold else Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Quick Select Index Pills
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                (0..4).forEach { i ->
                    AppChip(
                        text = "[$i]",
                        isSelected = selectedIndex == i,
                        onClick = {
                            if (!isChecked) {
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                selectedIndex = i
                            }
                        }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .pressScale(targetScale = 0.90f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDark) Color(0xFF14_1F_36) else Color(0xFFE2_E8_F0))
                    .clickable(enabled = !isChecked && selectedIndex < arrayElements.size - 1) {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        selectedIndex++
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = "Next",
                    tint = if (selectedIndex < arrayElements.size - 1) AmberGold else Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 4. Check / Continue Button & Result Banner
        if (!isChecked) {
            AppButton(
                title = "CHECK ALIGNMENT",
                style = AppButtonStyle.PRIMARY_AMBER,
                onClick = {
                    isChecked = true
                    if (selectedIndex == targetIndex) {
                        isCorrect = true
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    } else {
                        isCorrect = false
                        shakeTrigger = Any()
                    }
                }
            )
        } else {
            if (isCorrect) {
                AppButton(
                    title = "CONTINUE JOURNEY",
                    style = AppButtonStyle.SUCCESS_GREEN,
                    icon = Icons.Default.CheckCircle,
                    onClick = onCorrectAnswer
                )
            } else {
                AppButton(
                    title = "TRY AGAIN",
                    style = AppButtonStyle.DANGER_CRIMSON,
                    onClick = {
                        isChecked = false
                        isCorrect = false
                    }
                )
            }
        }
    }
}
