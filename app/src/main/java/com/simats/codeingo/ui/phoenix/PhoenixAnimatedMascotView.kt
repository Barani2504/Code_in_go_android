package com.simats.codeingo.ui.phoenix

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.simats.codeingo.R
import com.simats.codeingo.data.model.phoenixEmotion
import com.simats.codeingo.ui.theme.AmberGold
import kotlin.math.sin

// ══════════════════════════════════════════════════════════════════
// 🕊️ PhoenixAnimatedMascotView — Live Multi-Pose Character Engine
// Exact Parity with iOS PhoenixAnimatedMascotView.swift
// ══════════════════════════════════════════════════════════════════

sealed interface PhoenixMascotPose {
    object Walking : PhoenixMascotPose
    object Welcoming : PhoenixMascotPose
    object StarryEyes : PhoenixMascotPose
    data class Noting(val isWriting: Boolean = false, val showTick: Boolean = false) : PhoenixMascotPose
    data class TemperatureReacting(val temp: Double, val isSuccess: Boolean = false, val isError: Boolean = false) : PhoenixMascotPose
    object Peeking : PhoenixMascotPose
    object Flying : PhoenixMascotPose
    object StreakFlame : PhoenixMascotPose
    object Singing : PhoenixMascotPose
    object CalendarCheer : PhoenixMascotPose
    object WidgetFlameEyes : PhoenixMascotPose
}

@Composable
fun PhoenixAnimatedMascotView(
    pose: PhoenixMascotPose = PhoenixMascotPose.Welcoming,
    modifier: Modifier = Modifier,
    size: Dp = 160.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mascotKinematics")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    val scaleFactor = size / 160.dp
    val breathY = (sin(time.toDouble()) * 3.5).toFloat() * scaleFactor
    val hoverY = (sin((time * 1.5f).toDouble()) * 6.0).toFloat() * scaleFactor
    val waddleTilt = (sin(time.toDouble()) * 4.0).toFloat()

    val auraColor = when (pose) {
        is PhoenixMascotPose.StreakFlame -> Color(0xFFFF3B30)
        is PhoenixMascotPose.StarryEyes -> Color(0xFF9664FF)
        is PhoenixMascotPose.Singing -> Color(0xFF00C8FF)
        else -> AmberGold
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        // 1. Ambient Thermal Reactive Glow
        Box(
            modifier = Modifier
                .size(size * 1.25f)
                .scale(1.0f + (sin((time * 2f).toDouble()) * 0.05).toFloat())
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            auraColor.copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    )
                )
        )

        // 2. Pose Specific Character Rendering
        when (pose) {
            is PhoenixMascotPose.Noting -> {
                LiveActionPhoenixNotingView(
                    isWriting = pose.isWriting,
                    showTick = pose.showTick,
                    size = size
                )
            }
            is PhoenixMascotPose.Walking -> {
                Image(
                    painter = painterResource(id = R.drawable.phoenix_stage_1),
                    contentDescription = "Walking Phoenix",
                    modifier = Modifier
                        .size(size)
                        .offset(y = breathY.dp)
                        .rotate(waddleTilt)
                )
            }
            is PhoenixMascotPose.StarryEyes -> {
                PhoenixMascotImage(
                    emotion = phoenixEmotion(6), // Starry Prodigy 🤩
                    size = size,
                    modifier = Modifier
                        .offset(y = breathY.dp)
                        .scale(1.04f)
                )
            }
            is PhoenixMascotPose.StreakFlame -> {
                PhoenixMascotImage(
                    emotion = phoenixEmotion(23), // Furious Warning 💢
                    size = size,
                    modifier = Modifier
                        .offset(y = (hoverY * 0.5f).dp)
                        .scale(1.05f)
                )
            }
            is PhoenixMascotPose.Singing -> {
                PhoenixMascotImage(
                    emotion = phoenixEmotion(8), // Laughing / Singing
                    size = size,
                    modifier = Modifier
                        .offset(y = hoverY.dp)
                        .rotate(waddleTilt * 0.5f)
                )
            }
            is PhoenixMascotPose.Flying -> {
                SmoothFlyingPhoenixView(
                    size = size,
                    modifier = Modifier.scale(1.08f)
                )
            }
            else -> {
                // Welcoming / Default Pose
                PhoenixMascotImage(
                    emotion = phoenixEmotion(0),
                    size = size,
                    modifier = Modifier.offset(y = breathY.dp)
                )
            }
        }
    }
}
