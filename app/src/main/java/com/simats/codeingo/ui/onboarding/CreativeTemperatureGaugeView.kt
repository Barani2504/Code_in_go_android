package com.simats.codeingo.ui.onboarding

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.draw.rotate
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
import com.simats.codeingo.ui.components.Duolingo3DButton
import com.simats.codeingo.ui.components.Duolingo3DButtonColor
import com.simats.codeingo.ui.phoenix.PhoenixAnimatedMascotView
import com.simats.codeingo.ui.phoenix.PhoenixMascotPose
import com.simats.codeingo.ui.theme.AmberGold
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

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

    val shakeOffset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val view = LocalView.current

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val laserPulse by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val isCurrentCorrect = selectedIndex == targetIndex
    val activeColor = if (isChecked) {
        if (isCorrect) Color(0xFF58CC02) else Color(0xFFFF4B4B)
    } else {
        if (isCurrentCorrect) AmberGold else Color(0xFF00C8FF)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Question Header Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF141F38).copy(alpha = 0.90f))
                .border(
                    1.dp,
                    Brush.horizontalGradient(listOf(AmberGold.copy(alpha = 0.4f), Color(0xFF00C8FF).copy(alpha = 0.2f))),
                    RoundedCornerShape(14.dp)
                )
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = AmberGold,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "DSA ARENA CHALLENGE",
                        color = AmberGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif,
                        letterSpacing = 1.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF00C8FF).copy(alpha = 0.18f))
                        .border(1.dp, Color(0xFF00C8FF).copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "O(1) ACCESS",
                        color = Color(0xFF00C8FF),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "nums = [14, 32, 58, 77, 91]",
                color = LocalDynamicThemeColors.current.textPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Align Memory Pointer to: ",
                    color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "nums[ ? ] == 77",
                    color = AmberGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // 2. Memory Rack Stage with Mascot
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
        ) {
            // Contiguous Memory Slots
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Address badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(activeColor.copy(alpha = 0.18f))
                        .border(1.2.dp, activeColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "nums[$selectedIndex] = ${arrayElements[selectedIndex]}",
                        color = activeColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // 5 vertical slots
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0B1224).copy(alpha = 0.85f))
                        .border(1.2.dp, LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (idx in arrayElements.indices) {
                        val isSlotSelected = idx == selectedIndex
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .width(110.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSlotSelected) activeColor.copy(alpha = 0.25f) else Color(0xFF141F38).copy(alpha = 0.75f))
                                .border(
                                    width = if (isSlotSelected) 1.5.dp else 1.dp,
                                    color = if (isSlotSelected) activeColor else LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable(enabled = !isChecked) {
                                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                    selectedIndex = idx
                                }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "[$idx]",
                                color = if (isSlotSelected) Color.White else LocalDynamicThemeColors.current.textSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.width(24.dp)
                            )
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .width(32.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSlotSelected) activeColor else Color.White.copy(alpha = 0.08f))
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${arrayElements[idx]}",
                                    color = if (isSlotSelected) Color(0xFF1A1205) else LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.85f),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            if (isSlotSelected) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = activeColor,
                                    modifier = Modifier
                                        .size(12.dp)
                                        .rotate(180f)
                                        .scale(laserPulse)
                                )
                            }
                        }
                    }
                }
            }

            // Mascot & Speech Bubble Stage
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                val pose = if (isChecked) {
                    if (isCorrect) PhoenixMascotPose.Noting(isWriting = false, showTick = true)
                    else PhoenixMascotPose.TemperatureReacting(temp = 0.0, isSuccess = false, isError = true)
                } else {
                    if (selectedIndex == targetIndex) PhoenixMascotPose.Noting(isWriting = false, showTick = false)
                    else PhoenixMascotPose.Welcoming
                }

                PhoenixAnimatedMascotView(
                    pose = pose,
                    size = 135.dp
                )

                // Speech reaction text
                val speechText = if (isChecked) {
                    if (isCorrect) "Target Found! nums[3] == 77 in O(1) time!"
                    else "nums[$selectedIndex] is ${arrayElements[selectedIndex]}! Target is 77!"
                } else {
                    if (selectedIndex < targetIndex) "Index [$selectedIndex] holds ${arrayElements[selectedIndex]} (< 77). Step higher!"
                    else if (selectedIndex > targetIndex) "Index [$selectedIndex] holds ${arrayElements[selectedIndex]} (> 77). Step down!"
                    else "Target Locked! nums[3] == 77! Hit Check!"
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF202C38))
                        .border(1.2.dp, LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = speechText,
                        color = LocalDynamicThemeColors.current.textPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // 3. Memory Pointer Controls (Stepper & Quick Select)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Stepper Buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF141F38))
                        .border(1.5.dp, AmberGold.copy(alpha = if (selectedIndex > 0 && !isChecked) 0.35f else 0.15f), CircleShape)
                        .clickable(enabled = !isChecked && selectedIndex > 0) {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            selectedIndex -= 1
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Previous",
                        tint = if (selectedIndex > 0 && !isChecked) Color.White else LocalDynamicThemeColors.current.placeholder,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "INDEX",
                        color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.6f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "[ $selectedIndex ]",
                        color = AmberGold,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF141F38))
                        .border(1.5.dp, AmberGold.copy(alpha = if (selectedIndex < arrayElements.size - 1 && !isChecked) 0.35f else 0.15f), CircleShape)
                        .clickable(enabled = !isChecked && selectedIndex < arrayElements.size - 1) {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            selectedIndex += 1
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForwardIos,
                        contentDescription = "Next",
                        tint = if (selectedIndex < arrayElements.size - 1 && !isChecked) Color.White else LocalDynamicThemeColors.current.placeholder,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Select Pills
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (idx in arrayElements.indices) {
                    val isSelected = selectedIndex == idx
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) AmberGold else Color(0xFF141F38))
                            .border(1.2.dp, if (isSelected) AmberGold else LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                            .clickable(enabled = !isChecked) {
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                selectedIndex = idx
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "[$idx]",
                            color = if (isSelected) Color(0xFF1A1205) else LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.85f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "${arrayElements[idx]}",
                            color = if (isSelected) Color(0xFF1A1205) else LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.65f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 4. Result Banner & Main Button
        if (isChecked) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isCorrect) AmberGold.copy(alpha = 0.18f) else Color.Red.copy(alpha = 0.18f))
                    .border(1.5.dp, if (isCorrect) AmberGold else Color.Red, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Icon(
                    imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (isCorrect) AmberGold else Color.Red,
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = if (isCorrect) "Target Locked!" else "Not Quite!",
                        color = if (isCorrect) AmberGold else Color.Red,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = if (isCorrect) "nums[3] = 77! Array access by index is instant O(1) time." else "nums[$selectedIndex] = ${arrayElements[selectedIndex]}. We need 77 at index 3!",
                        color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Duolingo3DButton(
            title = if (isChecked) (if (isCorrect) "CONTINUE" else "TRY AGAIN") else "CHECK",
            style = if (isChecked) (if (isCorrect) Duolingo3DButtonColor.AMBER else Duolingo3DButtonColor.WHITE) else Duolingo3DButtonColor.AMBER,
            onClick = {
                if (!isChecked) {
                    val correct = selectedIndex == targetIndex
                    isChecked = true
                    isCorrect = correct
                    if (correct) {
                        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                    } else {
                        view.performHapticFeedback(HapticFeedbackConstants.REJECT)
                        scope.launch {
                            shakeOffset.animateTo(-12f, tween(60))
                            shakeOffset.animateTo(12f, tween(60))
                            shakeOffset.animateTo(-6f, tween(60))
                            shakeOffset.animateTo(0f, tween(60))
                        }
                    }
                } else if (isCorrect) {
                    onCorrectAnswer()
                } else {
                    isChecked = false
                    isCorrect = false
                }
            }
        )
    }
}
