package com.simats.codeingo.ui.phoenix

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.simats.codeingo.R
import kotlinx.coroutines.delay
import kotlin.math.sin

// ══════════════════════════════════════════════════════════════════
// 📝 LiveActionPhoenixNotingView — Live Mascot with Clipboard & Pen
// Exact Parity with iOS LiveActionPhoenixNotingView.swift
// ══════════════════════════════════════════════════════════════════

@Composable
fun LiveActionPhoenixNotingView(
    modifier: Modifier = Modifier,
    isWriting: Boolean = false,
    showTick: Boolean = false,
    size: Dp = 180.dp
) {
    val scaleFactor = size / 194.dp

    // Breathing & Kinematics Transitions
    val infiniteTransition = rememberInfiniteTransition(label = "kinematics")
    val animTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "animTime"
    )

    var isBlinking by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        while (true) {
            delay((2500..4500).random().toLong())
            isBlinking = true
            delay(140)
            isBlinking = false
        }
    }

    val tickTrim = remember { Animatable(0f) }
    val tickScale = remember { Animatable(0.5f) }
    val tickGlowAlpha = remember { Animatable(0f) }

    LaunchedEffect(showTick) {
        if (showTick) {
            tickTrim.snapTo(0f)
            tickScale.snapTo(0.5f)
            tickGlowAlpha.snapTo(0.6f)
            tickTrim.animateTo(1f, animationSpec = tween(350, easing = LinearEasing))
            tickScale.animateTo(1f, animationSpec = tween(300))
            tickGlowAlpha.animateTo(0f, animationSpec = tween(400))
        } else {
            tickTrim.snapTo(0f)
        }
    }

    val breathY = (sin(animTime.toDouble()) * 4.0).toFloat() * scaleFactor
    val squashX = 1.0f + (sin(animTime.toDouble()) * 0.02).toFloat()
    val squashY = 1.0f - (sin(animTime.toDouble()) * 0.02).toFloat()
    val tilt = (sin((animTime * 0.8f).toDouble()) * 2.5).toFloat()

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        // 1. Ambient Thermal Glow
        Box(
            modifier = Modifier
                .size(size * 1.3f)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFF8200).copy(alpha = 0.35f),
                            Color(0xFFFF5000).copy(alpha = 0.12f),
                            Color.Transparent
                        )
                    )
                )
        )

        // 2. Base Character Image (PhoenixNoting)
        Image(
            painter = painterResource(id = R.drawable.phoenix_noting),
            contentDescription = "Phoenix Noting",
            modifier = Modifier
                .size(size)
                .scale(squashX, squashY)
                .rotate(tilt)
                .offset(y = breathY.dp)
                .shadow(10.dp * scaleFactor, CircleShape, spotColor = Color(0xFFFF9800))
        )

        // 3. Live Blinking Eyelids
        if (isBlinking) {
            Row(
                modifier = Modifier
                    .offset(x = 20.dp * scaleFactor, y = (12.dp * scaleFactor) + breathY.dp)
                    .rotate(tilt)
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 24.dp * scaleFactor, height = 7.dp * scaleFactor)
                        .clip(RoundedCornerShape(100.dp))
                        .background(Color(0xFFFF6E00))
                )
                Spacer(modifier = Modifier.width(24.dp * scaleFactor))
                Box(
                    modifier = Modifier
                        .size(width = 24.dp * scaleFactor, height = 7.dp * scaleFactor)
                        .clip(RoundedCornerShape(100.dp))
                        .background(Color(0xFFFF6E00))
                )
            }
        }

        // 4. Live Checkmark on Clipboard
        if (showTick) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .offset(x = (-38).dp * scaleFactor, y = (14.dp * scaleFactor) + breathY.dp)
                    .rotate(-6f + tilt)
                    .size(48.dp * scaleFactor)
            ) {
                // Pulsing glow halo
                Box(
                    modifier = Modifier
                        .size(48.dp * scaleFactor)
                        .scale(tickScale.value * 1.25f)
                        .clip(CircleShape)
                        .background(Color(0xFF58CC02).copy(alpha = tickGlowAlpha.value))
                )

                // Drawn Checkmark
                Canvas(modifier = Modifier.size(32.dp * scaleFactor)) {
                    val canvasW = drawContext.size.width
                    val canvasH = drawContext.size.height
                    val path = Path().apply {
                        moveTo(0f, canvasH * 0.5f)
                        lineTo(canvasW * 0.35f, canvasH * 0.85f)
                        lineTo(canvasW, canvasH * 0.15f)
                    }
                    drawPath(
                        path = path,
                        color = Color(0xFF58CC02),
                        style = Stroke(
                            width = 3.5f * density,
                            cap = StrokeCap.Round
                        )
                    )
                }
            }
        }
    }
}
