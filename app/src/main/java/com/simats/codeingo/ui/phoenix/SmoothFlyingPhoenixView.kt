package com.simats.codeingo.ui.phoenix

import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.simats.codeingo.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

/**
 * High-fidelity, living Phoenix mascot view with smooth 16-frame continuous wing flapping
 * and organic aerodynamic flight physics (floating heave, bank roll, breathing lift, and thermal glow).
 * Faithfully mirrors iOS SmoothFlyingPhoenixView & AnimatedGIFView (commit 86e6fcf).
 */
@Composable
fun SmoothFlyingPhoenixView(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    speed: Double = 1.0
) {
    val frames = remember {
        listOf(
            R.drawable.fly_0,
            R.drawable.fly_1,
            R.drawable.fly_2,
            R.drawable.fly_3,
            R.drawable.fly_4,
            R.drawable.fly_5,
            R.drawable.fly_6,
            R.drawable.fly_7,
            R.drawable.fly_8,
            R.drawable.fly_9,
            R.drawable.fly_10,
            R.drawable.fly_11,
            R.drawable.fly_12,
            R.drawable.fly_13,
            R.drawable.fly_14,
            R.drawable.fly_15
        )
    }

    var currentFrameIndex by remember { mutableIntStateOf(0) }

    val baseDurationMs = 1600.0 // 1.6s loop matching iOS baseDuration
    val effectiveDurationMs = maxOf(100.0, baseDurationMs / maxOf(0.01, speed))
    val frameDelayMs = (effectiveDurationMs / frames.size).toLong()

    LaunchedEffect(speed) {
        while (isActive) {
            delay(frameDelayMs)
            currentFrameIndex = (currentFrameIndex + 1) % frames.size
        }
    }

    val oscillationDurationMs = (1800.0 / maxOf(0.2, speed)).toInt()
    val infiniteTransition = rememberInfiniteTransition(label = "flightKinematics")

    val hoverOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -7f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = oscillationDurationMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hoverY"
    )

    val bankAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = oscillationDurationMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bankAngle"
    )

    val breathScaleX by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.025f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = oscillationDurationMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathX"
    )

    val breathScaleY by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 0.985f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = oscillationDurationMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathY"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        // Soft radiant ambient thermal glow under the soaring bird
        Box(
            modifier = Modifier
                .size(size * 1.1f)
                .scale(breathScaleX)
                .offset(y = (hoverOffset * 0.5f).dp)
                .blur(8.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFF9800).copy(alpha = 0.28f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Living aerodynamic 16-frame Phoenix
        Image(
            painter = painterResource(id = frames[currentFrameIndex]),
            contentDescription = "Smooth Flying Phoenix",
            modifier = Modifier
                .size(size)
                .offset(y = hoverOffset.dp)
                .rotate(bankAngle)
                .scale(scaleX = breathScaleX, scaleY = breathScaleY)
        )
    }
}
