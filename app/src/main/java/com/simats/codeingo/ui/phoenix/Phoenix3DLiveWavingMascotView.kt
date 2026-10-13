package com.simats.codeingo.ui.phoenix

import android.media.MediaPlayer
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.simats.codeingo.R
import com.simats.codeingo.ui.theme.AmberGold
import kotlinx.coroutines.launch

/**
 * Phoenix3DLiveWavingMascotView — Jetpack Compose equivalent of iOS Phoenix3DLiveWavingMascotView.swift.
 * 100% transparent, true-alpha 3D animated Phoenix mascot:
 * - ZERO background pixels, ZERO cards, ZERO black box artifacts
 * - 24-frame full-body 3D waving sequence with expressive smiling beak and fluttering feathers
 * - Perspective ground contact shadow beneath feet for realistic depth
 * - Soft studio key light backlight aura matching Phoenix atmospheric theme
 * - Synchronized voice audio playback with clean lifecycle management (R.raw.phoenix_greeting)
 * - Interactive tap bounce with haptic feedback
 */
@Composable
fun Phoenix3DLiveWavingMascotView(
    size: Dp = 260.dp,
    playAudioOnAppear: Boolean = true,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    // Living Breathing & Aura Motion
    val infiniteTransition = rememberInfiniteTransition(label = "wavingLivingMotion")
    val breathOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathOffset"
    )

    val glowBreath by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowBreath"
    )

    val tapScale = remember { Animatable(1f) }

    // Audio Player State & Lifecycle Management
    var mediaPlayer = remember { mutableListOf<MediaPlayer>() }

    fun playGreetingVoice() {
        try {
            val player = MediaPlayer.create(context, R.raw.phoenix_greeting)
            if (player != null) {
                player.setVolume(0.85f, 0.85f)
                player.setOnCompletionListener { mp ->
                    try {
                        mp.release()
                    } catch (_: Exception) {}
                    mediaPlayer.remove(mp)
                }
                mediaPlayer.add(player)
                player.start()
            }
        } catch (_: Exception) {
            // Audio play notice handled gracefully
        }
    }

    LaunchedEffect(Unit) {
        if (playAudioOnAppear) {
            playGreetingVoice()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.forEach { player ->
                try {
                    if (player.isPlaying) player.stop()
                    player.release()
                } catch (_: Exception) {}
            }
            mediaPlayer.clear()
        }
    }

    fun triggerTap() {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        coroutineScope.launch {
            tapScale.animateTo(1.07f, animationSpec = spring(dampingRatio = 0.52f, stiffness = 600f))
            tapScale.animateTo(1.0f, animationSpec = spring(dampingRatio = 0.62f, stiffness = 500f))
        }
        playGreetingVoice()
    }

    Box(
        modifier = modifier
            .size(size)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { triggerTap() }
            ),
        contentAlignment = Alignment.Center
    ) {
        // MARK: - 1. Soft Dynamic Backlight Aura (Zero Box Boundary Artifacts)
        Canvas(
            modifier = Modifier
                .size(size * 1.35f)
                .scale(tapScale.value)
        ) {
            val auraRadius = this.size.minDimension * 0.44f
            drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.0f to AmberGold.copy(alpha = (0.28f * glowBreath).coerceIn(0f, 1f)),
                        0.35f to Color(0xFFFF9500).copy(alpha = (0.15f * glowBreath).coerceIn(0f, 1f)),
                        0.70f to Color(0xFFFF7A00).copy(alpha = (0.04f * glowBreath).coerceIn(0f, 1f)),
                        1.0f to Color.Transparent
                    ),
                    center = center,
                    radius = auraRadius
                ),
                radius = auraRadius,
                center = center
            )
        }

        // MARK: - 2. Ground Contact Shadow (3D Perspective Depth, Zero Clipping Bounds)
        val shadowWidth = size * 0.68f
        val shadowHeight = size * 0.14f
        val shadowOffsetY = (size * 0.44f) + (breathOffset * 0.3f).dp
        Canvas(
            modifier = Modifier
                .size(width = shadowWidth, height = shadowHeight)
                .offset(y = shadowOffsetY)
        ) {
            val shadowRadiusX = this.size.width * 0.46f
            drawOval(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.0f to Color.Black.copy(alpha = 0.45f),
                        0.40f to Color.Black.copy(alpha = 0.22f),
                        0.75f to Color.Black.copy(alpha = 0.05f),
                        1.0f to Color.Transparent
                    ),
                    center = center,
                    radius = shadowRadiusX
                ),
                topLeft = Offset.Zero,
                size = this.size
            )
        }

        // MARK: - 3. Pure Transparent 3D Waving Character (Zero Background)
        AnimatedGIFView(
            resourceName = "phoenix_waving",
            size = size,
            speed = 1.0,
            modifier = Modifier
                .offset(y = breathOffset.dp)
                .scale(tapScale.value)
        )
    }
}
