package com.simats.codeingo.ui.visualizer.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.components.motion.pressSquish
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.FeedbackManager
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

/**
 * 🎛️ Visualizer 2.0 Timeline Scrubber & Playback Controller
 *
 * Implements Phase 4.1:
 * - Timeline scrubber slider with tick marks
 * - Play / Pause automatic stepped playback
 * - Granular Step-back (◀) and Step-forward (▶)
 * - Playback speed selector: 0.5x, 1x, 2x
 */
@Composable
fun VisualizerPlaybackController(
    currentStep: Int,
    totalSteps: Int,
    onStepChange: (Int) -> Unit,
    isPlaying: Boolean,
    onPlayPauseToggle: () -> Unit,
    playbackSpeed: Float = 1.0f,
    onSpeedChange: (Float) -> Unit = {},
    stepLabel: String = "STEP",
    accentColor: Color = AmberGold,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    val safeTotalSteps = totalSteps.coerceAtLeast(1)
    val safeCurrentStep = currentStep.coerceIn(1, safeTotalSteps)

    LaunchedEffect(safeTotalSteps, currentStep) {
        if (currentStep != safeCurrentStep) {
            onStepChange(safeCurrentStep)
        }
    }

    // Auto-advance loop when playing
    LaunchedEffect(isPlaying, safeCurrentStep, playbackSpeed, safeTotalSteps) {
        if (isPlaying && safeTotalSteps > 1) {
            val safeSpeed = playbackSpeed.coerceAtLeast(0.25f)
            val delayMs = (1000L / safeSpeed).toLong().coerceIn(250L, 2000L)
            delay(delayMs)
            if (safeCurrentStep < safeTotalSteps) {
                onStepChange(safeCurrentStep + 1)
            } else {
                // Loop back to start or pause
                onStepChange(1)
            }
        }
    }

    val cardBg = if (isDark) Color(0xFF111827) else Color.White
    val borderColor = if (isDark) Color(0xFF1F2937) else Color(0xFFE2E8F0)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(cardBg)
            .border(1.2.dp, borderColor, RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header: Step Indicator & Speed Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(accentColor.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "$stepLabel $safeCurrentStep / $safeTotalSteps",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = accentColor
                        )
                    }
                    if (isPlaying) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        )
                        Text(
                            text = "PLAYING",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                    }
                }

                // Speed Selector Pills (0.5x, 1x, 2x)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(0.5f, 1.0f, 2.0f).forEach { speed ->
                        val isSelected = playbackSpeed == speed
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (isSelected) accentColor else if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f)
                                )
                                .clickable {
                                    FeedbackManager.tap()
                                    onSpeedChange(speed)
                                }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${speed}x",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.Black else if (isDark) SubtextGray else dynamicColors.textSecondary
                            )
                        }
                    }
                    if (safeCurrentStep > 1) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f))
                                .clickable {
                                    FeedbackManager.tap()
                                    if (isPlaying) onPlayPauseToggle()
                                    onStepChange(1)
                                }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "RESET",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) SubtextGray else dynamicColors.textSecondary
                            )
                        }
                    }
                }
            }

            // Timeline Scrubber Slider
            if (safeTotalSteps > 1) {
                Slider(
                    value = safeCurrentStep.toFloat(),
                    onValueChange = { newVal ->
                        val next = newVal.roundToInt().coerceIn(1, safeTotalSteps)
                        if (next != safeCurrentStep) {
                            FeedbackManager.tap()
                            onStepChange(next)
                        }
                    },
                    valueRange = 1f..safeTotalSteps.toFloat(),
                    steps = (safeTotalSteps - 2).coerceAtLeast(0),
                    colors = SliderDefaults.colors(
                        thumbColor = accentColor,
                        activeTrackColor = accentColor,
                        inactiveTrackColor = if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.08f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Transport Control Buttons (Step Back | Play/Pause | Step Forward)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Step Back Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .pressSquish {
                            if (safeCurrentStep > 1) {
                                FeedbackManager.tap()
                                onStepChange(safeCurrentStep - 1)
                            }
                        }
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                        .border(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Step Back",
                            tint = if (safeCurrentStep > 1) dynamicColors.textPrimary else dynamicColors.textSecondary.copy(alpha = 0.4f),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "BACK",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (safeCurrentStep > 1) dynamicColors.textPrimary else dynamicColors.textSecondary.copy(alpha = 0.4f)
                        )
                    }
                }

                // Play / Pause Main CTA
                Box(
                    modifier = Modifier
                        .weight(1.3f)
                        .height(42.dp)
                        .pressSquish {
                            FeedbackManager.tap()
                            onPlayPauseToggle()
                        }
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (isPlaying) "PAUSE" else "PLAY",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }
                }

                // Step Forward Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .pressSquish {
                            if (safeCurrentStep < safeTotalSteps) {
                                FeedbackManager.tap()
                                onStepChange(safeCurrentStep + 1)
                            }
                        }
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                        .border(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "NEXT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (safeCurrentStep < safeTotalSteps) dynamicColors.textPrimary else dynamicColors.textSecondary.copy(alpha = 0.4f)
                        )
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Step Forward",
                            tint = if (safeCurrentStep < safeTotalSteps) dynamicColors.textPrimary else dynamicColors.textSecondary.copy(alpha = 0.4f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

