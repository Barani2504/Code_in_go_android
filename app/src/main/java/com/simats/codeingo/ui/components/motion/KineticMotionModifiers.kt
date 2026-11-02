package com.simats.codeingo.ui.components.motion

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Animmaster & Unlumen UI Motion Library for Codeingo Jetpack Compose.
 *
 * Implements:
 * 1. [kineticBouncyClick]: Physics-based spring rebound squash-and-stretch with haptics.
 * 2. [unlumenNeonGlow]: Animated breathing specular neon glow for high-priority targets.
 * 3. [shimmerGlossSweep]: Shimmering specular gloss shine sweeping across unlocked nodes.
 * 4. [dangerHeartbeatShake]: Agitated horizontal wobble when critical resources (hearts) are low.
 */

/**
 * Adds an interactive physical squash-and-stretch spring rebound to any Composable.
 * Surpasses standard mobile buttons by applying dynamic scaleX and scaleY physics on tap.
 */
fun Modifier.kineticBouncyClick(
    squashFactor: Float = 0.92f,
    stretchFactor: Float = 1.05f,
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    if (!enabled) return@composed this

    val view = LocalView.current
    val coroutineScope = rememberCoroutineScope()
    val scaleX = remember { Animatable(1f) }
    val scaleY = remember { Animatable(1f) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    LaunchedEffect(isPressed) {
        if (isPressed) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            coroutineScope.launch {
                scaleX.animateTo(stretchFactor, spring(stiffness = Spring.StiffnessMediumLow))
            }
            coroutineScope.launch {
                scaleY.animateTo(squashFactor, spring(stiffness = Spring.StiffnessMediumLow))
            }
        } else {
            coroutineScope.launch {
                scaleX.animateTo(
                    1f,
                    spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
            coroutineScope.launch {
                scaleY.animateTo(
                    1f,
                    spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
        }
    }

    this
        .graphicsLayer {
            this.scaleX = scaleX.value
            this.scaleY = scaleY.value
        }
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                onClick()
            }
        )
}

/**
 * Unlumen UI Specular Neon Glow:
 * Renders a rotating, breathing ambient aura around a component.
 */
fun Modifier.unlumenNeonGlow(
    glowColor: Color = Color(0xFFFFB800),
    secondaryColor: Color = Color(0xFFFF4500),
    pulseDurationMs: Int = 2200,
    cornerRadius: androidx.compose.ui.unit.Dp = 16.dp,
    blurRadius: androidx.compose.ui.unit.Dp = 12.dp,
    enabled: Boolean = true
): Modifier = composed {
    if (!enabled) return@composed this

    val infiniteTransition = rememberInfiniteTransition(label = "unlumenGlow")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(pulseDurationMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(pulseDurationMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    this.graphicsLayer {
        this.scaleX = scale
        this.scaleY = scale
        this.alpha = alpha
    }
}

/**
 * Shimmer Gloss Sweep:
 * An authentic Duolingo / Unlumen specular light reflection passing across unlocked nodes.
 */
fun Modifier.shimmerGlossSweep(
    durationMs: Int = 2400,
    shineColor: Color = Color.White.copy(alpha = 0.45f),
    enabled: Boolean = true
): Modifier = composed {
    if (!enabled) return@composed this

    val transition = rememberInfiniteTransition(label = "glossSweep")
    val progress by transition.animateFloat(
        initialValue = -1.2f,
        targetValue = 2.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shineProgress"
    )

    this.drawWithContent {
        drawContent()
        val width = size.width
        val height = size.height

        val xOffset = width * progress
        val brush = Brush.linearGradient(
            colors = listOf(
                Color.Transparent,
                shineColor,
                Color.Transparent
            ),
            start = Offset(xOffset, 0f),
            end = Offset(xOffset + width * 0.45f, height)
        )

        drawRect(brush = brush, blendMode = BlendMode.SrcAtop)
    }
}

/**
 * Danger Heartbeat Shake:
 * Creates an alert shake effect used when hearts are critical (0 or 1).
 */
fun Modifier.dangerHeartbeatShake(enabled: Boolean = true): Modifier = composed {
    if (!enabled) return@composed this

    val transition = rememberInfiniteTransition(label = "dangerHeartbeat")
    val rotation by transition.animateFloat(
        initialValue = -3.5f,
        targetValue = 3.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(120, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shakeRotation"
    )

    this.graphicsLayer {
        this.rotationZ = rotation
    }
}

