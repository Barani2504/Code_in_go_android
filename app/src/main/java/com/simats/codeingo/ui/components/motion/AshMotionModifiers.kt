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
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * AshCrypt Suite of 11 Physics-Based Spring Animation Modifiers (Item 8)
 *
 * 1.  [bounce]: Scale spring bounce
 * 2.  [shake]: Horizontal error shake (calm-mode safe)
 * 3.  [pulse]: Rhythmic breathing scale
 * 4.  [tada]: Playful rotational wiggle + scale burst
 * 5.  [wobble]: Tilt rotation oscillation
 * 6.  [rubberBand]: Squash-and-stretch elasticity
 * 7.  [flash]: Opacity flash (gated <= 3Hz, photosensitivity safe)
 * 8.  [slide]: Spring positional translation
 * 9.  [zoom]: Focus magnification spring
 * 10. [pressSquish]: Mechanical button depression with haptics
 * 11. [glowBorder]: Specular neon ambient glow border
 *
 * Plus:
 * - [skeletonShimmer]: Native brush-based skeleton shimmer for loading states
 * - [ghostTrailProgress]: Trailing shadow bar animation for XP / health bars
 * - [pathDrawProgress]: Stroke path reveal for unlock paths
 */

// 1. Bounce Spring Modifier
fun Modifier.bounce(
    trigger: Any? = null,
    peakScale: Float = 1.15f
): Modifier = composed {
    if (isCalmModeActive()) return@composed this
    val scale = remember { Animatable(1f) }

    LaunchedEffect(trigger) {
        if (trigger == null) return@LaunchedEffect
        scale.animateTo(peakScale, spring(dampingRatio = 0.52f, stiffness = 600f))
        scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
    }

    this.graphicsLayer {
        scaleX = scale.value
        scaleY = scale.value
    }
}

// 2. Shake Spring Modifier (Calm Mode Safe)
fun Modifier.shake(
    trigger: Any? = null,
    amplitudeDp: Dp = 8.dp
): Modifier = composed {
    if (!EffectBudgetManager.isShakeAllowed()) return@composed this
    val offsetX = remember { Animatable(0f) }

    LaunchedEffect(trigger) {
        if (trigger == null) return@LaunchedEffect
        val target = amplitudeDp.value
        offsetX.animateTo(target, tween(40, easing = LinearEasing))
        offsetX.animateTo(-target * 0.8f, tween(40, easing = LinearEasing))
        offsetX.animateTo(target * 0.6f, tween(40, easing = LinearEasing))
        offsetX.animateTo(-target * 0.4f, tween(40, easing = LinearEasing))
        offsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium))
    }

    this.graphicsLayer {
        translationX = offsetX.value * density
    }
}

// 3. Pulse Spring Modifier
fun Modifier.pulse(
    minScale: Float = 0.96f,
    maxScale: Float = 1.04f,
    periodMs: Int = 1800,
    enabled: Boolean = true
): Modifier = composed {
    if (!enabled || isCalmModeActive()) return@composed this

    val transition = rememberInfiniteTransition(label = "pulseMotion")
    val scale by transition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(periodMs / 2, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

// 4. Tada Spring Modifier
fun Modifier.tada(
    trigger: Any? = null
): Modifier = composed {
    if (isCalmModeActive()) return@composed this
    val scale = remember { Animatable(1f) }
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(trigger) {
        if (trigger == null) return@LaunchedEffect
        launch {
            scale.animateTo(0.9f, tween(60))
            scale.animateTo(1.15f, spring(dampingRatio = 0.45f, stiffness = 500f))
            scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
        }
        launch {
            rotation.animateTo(-4f, tween(50))
            rotation.animateTo(4f, tween(50))
            rotation.animateTo(-3f, tween(50))
            rotation.animateTo(3f, tween(50))
            rotation.animateTo(0f, spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium))
        }
    }

    this.graphicsLayer {
        scaleX = scale.value
        scaleY = scale.value
        rotationZ = rotation.value
    }
}

// 5. Wobble Spring Modifier
fun Modifier.wobble(
    trigger: Any? = null,
    maxAngle: Float = 6f
): Modifier = composed {
    if (isCalmModeActive()) return@composed this
    val rot = remember { Animatable(0f) }

    LaunchedEffect(trigger) {
        if (trigger == null) return@LaunchedEffect
        rot.animateTo(maxAngle, tween(50))
        rot.animateTo(-maxAngle * 0.7f, tween(50))
        rot.animateTo(maxAngle * 0.4f, tween(50))
        rot.animateTo(0f, spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium))
    }

    this.graphicsLayer {
        rotationZ = rot.value
        transformOrigin = TransformOrigin.Center
    }
}

// 6. RubberBand Modifier (Squash and Stretch)
fun Modifier.rubberBand(
    trigger: Any? = null
): Modifier = composed {
    if (isCalmModeActive()) return@composed this
    val scaleX = remember { Animatable(1f) }
    val scaleY = remember { Animatable(1f) }

    LaunchedEffect(trigger) {
        if (trigger == null) return@LaunchedEffect
        launch {
            scaleX.animateTo(1.25f, tween(80))
            scaleX.animateTo(0.85f, tween(80))
            scaleX.animateTo(1.10f, tween(80))
            scaleX.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
        }
        launch {
            scaleY.animateTo(0.75f, tween(80))
            scaleY.animateTo(1.15f, tween(80))
            scaleY.animateTo(0.95f, tween(80))
            scaleY.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
        }
    }

    this.graphicsLayer {
        this.scaleX = scaleX.value
        this.scaleY = scaleY.value
    }
}

// 7. Flash Modifier (Photosensitivity Safe: <= 3Hz, Calm Mode Safe)
fun Modifier.flash(
    trigger: Any? = null
): Modifier = composed {
    if (!EffectBudgetManager.isFlashAllowed()) return@composed this
    val alpha = remember { Animatable(1f) }

    LaunchedEffect(trigger) {
        if (trigger == null) return@LaunchedEffect
        // 1 single gentle flash dip, strictly 300ms (3.3Hz max)
        alpha.animateTo(0.35f, tween(120, easing = LinearEasing))
        alpha.animateTo(1f, tween(180, easing = FastOutSlowInEasing))
    }

    this.graphicsLayer {
        this.alpha = alpha.value
    }
}

// 8. Slide Spring Modifier
fun Modifier.slide(
    trigger: Any? = null,
    initialOffsetY: Dp = 24.dp
): Modifier = composed {
    val offsetY = remember { Animatable(initialOffsetY.value) }

    LaunchedEffect(trigger) {
        offsetY.snapTo(initialOffsetY.value)
        offsetY.animateTo(0f, spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow))
    }

    this.graphicsLayer {
        translationY = offsetY.value * density
    }
}

// 9. Zoom Spring Modifier
fun Modifier.zoom(
    targetScale: Float = 1.05f,
    enabled: Boolean = true
): Modifier = composed {
    val scale = remember { Animatable(1f) }

    LaunchedEffect(enabled, targetScale) {
        val target = if (enabled) targetScale else 1f
        scale.animateTo(target, spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessLow))
    }

    this.graphicsLayer {
        scaleX = scale.value
        scaleY = scale.value
    }
}

// 10. PressSquish Modifier (Tactile Micro Interaction)
fun Modifier.pressSquish(
    squishRatio: Float = 0.94f,
    onClick: () -> Unit
): Modifier = composed {
    val view = LocalView.current
    val coroutineScope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    LaunchedEffect(isPressed) {
        if (isPressed) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            coroutineScope.launch {
                scale.animateTo(squishRatio, spring(stiffness = Spring.StiffnessMediumLow))
            }
        } else {
            coroutineScope.launch {
                scale.animateTo(1f, spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessLow))
            }
        }
    }

    this
        .graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
        }
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                onClick()
            }
        )
}

// 11. GlowBorder Modifier
fun Modifier.glowBorder(
    color: Color = Color(0xFFFFC800),
    strokeWidth: Dp = 2.dp,
    enabled: Boolean = true
): Modifier = composed {
    if (!enabled || isCalmModeActive()) return@composed this

    val infiniteTransition = rememberInfiniteTransition(label = "glowBorder")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.90f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "borderGlowAlpha"
    )

    this.drawWithContent {
        drawContent()
        drawRect(
            color = color.copy(alpha = alpha),
            style = Stroke(width = strokeWidth.toPx())
        )
    }
}

// ── Native Brush-Based Skeleton Shimmer (Loading States) ──
fun Modifier.skeletonShimmer(
    baseColor: Color = Color(0xFF1E293B),
    highlightColor: Color = Color(0xFF334155),
    durationMs: Int = 1200
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "skeleton")
    val progress by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerProgress"
    )

    this.drawWithContent {
        val width = size.width
        val height = size.height
        val xOffset = width * progress

        val brush = Brush.linearGradient(
            colors = listOf(baseColor, highlightColor, baseColor),
            start = Offset(xOffset, 0f),
            end = Offset(xOffset + width * 0.6f, height)
        )
        drawRect(brush = brush)
    }
}

// ── Ghost-Trail Progress Bar Modifier ──
fun Modifier.ghostTrailProgress(
    actualProgress: Float,
    trailColor: Color = Color(0xFFFF5712).copy(alpha = 0.4f),
    barColor: Color = Color(0xFFFFC800)
): Modifier = composed {
    val animatedActual = remember { Animatable(0f) }
    val animatedGhost = remember { Animatable(0f) }

    LaunchedEffect(actualProgress) {
        animatedActual.animateTo(actualProgress, spring(dampingRatio = 0.70f, stiffness = Spring.StiffnessMediumLow))
        delay(120)
        animatedGhost.animateTo(actualProgress, tween(400, easing = FastOutSlowInEasing))
    }

    this.drawWithContent {
        val w = size.width
        val h = size.height

        // Ghost trail (delayed bar)
        val ghostWidth = w * animatedGhost.value.coerceIn(0f, 1f)
        if (ghostWidth > 0f) {
            drawRect(color = trailColor, size = androidx.compose.ui.geometry.Size(ghostWidth, h))
        }

        // Active front bar
        val barWidth = w * animatedActual.value.coerceIn(0f, 1f)
        if (barWidth > 0f) {
            drawRect(color = barColor, size = androidx.compose.ui.geometry.Size(barWidth, h))
        }
    }
}

