package com.simats.codeingo.ui.theme

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

// ══════════════════════════════════════════════════════════════════
// ⚡ PHOENIX MOTION SYSTEM — SwiftUI ➔ Jetpack Compose Physics
// Conversion Formulas:
//   stiffness (k) = (2 * PI / response)^2
//   dampingRatio (zeta) = dampingFraction
// ══════════════════════════════════════════════════════════════════

object PhoenixMotion {
    /**
     * Tactile Press: SwiftUI response: 0.20s, dampingFraction: 0.60
     * k = (2π / 0.2)^2 = 986f, ζ = 0.60f
     */
    val PressSpring: AnimationSpec<Float> = spring(
        dampingRatio = 0.60f,
        stiffness = 986f
    )
    val PressSpringDp: AnimationSpec<Dp> = spring(
        dampingRatio = 0.60f,
        stiffness = 986f
    )

    val BounceSpring: AnimationSpec<Float> = spring(
        dampingRatio = 0.50f,
        stiffness = 322f
    )
    val BounceSpringDp: AnimationSpec<Dp> = spring(
        dampingRatio = 0.50f,
        stiffness = 322f
    )

    val SnappySpring: AnimationSpec<Float> = spring(
        dampingRatio = 0.75f,
        stiffness = 438f
    )
    val SnappySpringDp: AnimationSpec<Dp> = spring(
        dampingRatio = 0.75f,
        stiffness = 438f
    )

    val GentleSpring: AnimationSpec<Float> = spring(
        dampingRatio = 0.80f,
        stiffness = 158f
    )
    val GentleSpringDp: AnimationSpec<Dp> = spring(
        dampingRatio = 0.80f,
        stiffness = 158f
    )

    /**
     * Floating & Hover Physics: SwiftUI response: 0.80s, dampingFraction: 0.85
     * k = (2π / 0.80)^2 = 61.6f, ζ = 0.85f
     */
    val FloatSpring: AnimationSpec<Float> = spring(
        dampingRatio = 0.85f,
        stiffness = 62f
    )

    // Standard Durations
    const val DurationFastMs = 150
    const val DurationMediumMs = 300
    const val DurationSlowMs = 500
}

/** Accessibility toggle for system "Remove animations" */
val LocalReduceMotion = compositionLocalOf { false }

// ──────────────────────────────────────────────────────────────────
// 🎯 INTERACTIVE & ENTRANCE MODIFIERS
// ──────────────────────────────────────────────────────────────────

/**
 * 3D Press Scale with automatic haptic feedback.
 * Depresses component with authentic spring physics.
 */
fun Modifier.pressScale(
    targetScale: Float = 0.96f,
    onClick: (() -> Unit)? = null
): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }
    val reduceMotion = LocalReduceMotion.current
    val view = LocalView.current

    val scale by animateFloatAsState(
        targetValue = if (isPressed && !reduceMotion) targetScale else 1.0f,
        animationSpec = PhoenixMotion.PressSpring,
        label = "pressScale"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(Unit) {
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false)
                isPressed = true
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)

                val up = waitForUpOrCancellation()
                isPressed = false
                if (up != null && onClick != null) {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    onClick()
                }
            }
        }
}

/**
 * Bouncy pop-in on composable appearance.
 */
fun Modifier.bounceOnAppear(
    delayMs: Int = 0,
    initialScale: Float = 0.65f
): Modifier = composed {
    val scale = remember { Animatable(initialScale) }
    val alpha = remember { Animatable(0f) }
    val reduceMotion = LocalReduceMotion.current

    LaunchedEffect(Unit) {
        if (reduceMotion) {
            scale.snapTo(1f)
            alpha.snapTo(1f)
            return@LaunchedEffect
        }
        if (delayMs > 0) delay(delayMs.toLong())
        scale.animateTo(1f, animationSpec = PhoenixMotion.BounceSpring)
    }

    LaunchedEffect(Unit) {
        if (reduceMotion) return@LaunchedEffect
        if (delayMs > 0) delay(delayMs.toLong())
        alpha.animateTo(1f, animationSpec = tween(180, easing = FastOutSlowInEasing))
    }

    this.graphicsLayer {
        scaleX = scale.value
        scaleY = scale.value
        this.alpha = alpha.value
    }
}

/**
 * Staggered cascade reveal for lazy lists and card grids.
 */
fun Modifier.staggeredAppear(
    index: Int,
    baseDelayMs: Int = 45,
    slideDistanceDp: Dp = 24.dp
): Modifier = composed {
    val alpha = remember { Animatable(0f) }
    val offsetY = remember { Animatable(slideDistanceDp.value) }
    val reduceMotion = LocalReduceMotion.current

    LaunchedEffect(Unit) {
        if (reduceMotion) {
            alpha.snapTo(1f)
            offsetY.snapTo(0f)
            return@LaunchedEffect
        }
        val delay = (index * baseDelayMs).toLong()
        delay(delay)
        alpha.animateTo(1f, animationSpec = tween(220, easing = FastOutSlowInEasing))
    }

    LaunchedEffect(Unit) {
        if (reduceMotion) return@LaunchedEffect
        val delay = (index * baseDelayMs).toLong()
        delay(delay)
        offsetY.animateTo(0f, animationSpec = PhoenixMotion.SnappySpring)
    }

    this.graphicsLayer {
        this.alpha = alpha.value
        translationY = offsetY.value * density
    }
}

/**
 * Continuous floating hover animation for mascots, badges, and eggs.
 */
fun Modifier.floating(
    distanceDp: Dp = 6.dp,
    durationMs: Int = 2200,
    rotateDeg: Float = 2.5f
): Modifier = composed {
    val reduceMotion = LocalReduceMotion.current
    if (reduceMotion) return@composed this

    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "floatPhase"
    )

    val yOffset = sin(phase) * distanceDp.value
    val rotation = cos(phase) * rotateDeg

    this.graphicsLayer {
        translationY = yOffset * density
        rotationZ = rotation
    }
}

/**
 * Shimmer sheen gradient effect across cards, buttons, or progress bars.
 */
fun Modifier.shimmer(
    shimmerColor: Color = Color.White.copy(alpha = 0.28f),
    durationMs: Int = 1600
): Modifier = composed {
    val reduceMotion = LocalReduceMotion.current
    if (reduceMotion) return@composed this

    val transition = rememberInfiniteTransition(label = "shimmer")
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
        drawContent()
        val width = size.width
        val height = size.height
        val startX = progress * width
        val brush = Brush.linearGradient(
            colors = listOf(
                Color.Transparent,
                shimmerColor,
                Color.Transparent
            ),
            start = Offset(startX - width * 0.4f, 0f),
            end = Offset(startX + width * 0.4f, height)
        )
        drawRect(brush = brush, blendMode = BlendMode.SrcAtop)
    }
}

/**
 * Pulsing glow / scale indicator for active levels and boss nodes.
 */
fun Modifier.pulse(
    scaleRange: ClosedFloatingPointRange<Float> = 1.0f..1.08f,
    durationMs: Int = 1200
): Modifier = composed {
    val reduceMotion = LocalReduceMotion.current
    if (reduceMotion) return@composed this

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = scaleRange.start,
        targetValue = scaleRange.endInclusive,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * Error shake animation for wrong answers and locked nodes.
 */
fun Modifier.shake(
    shakeTrigger: Any?
): Modifier = composed {
    val offsetX = remember { Animatable(0f) }
    val view = LocalView.current

    LaunchedEffect(shakeTrigger) {
        if (shakeTrigger == null) return@LaunchedEffect
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        val keyframes = listOf(0f, -14f, 14f, -10f, 10f, -6f, 6f, 0f)
        for (target in keyframes) {
            offsetX.animateTo(
                targetValue = target,
                animationSpec = tween(durationMillis = 40, easing = LinearEasing)
            )
        }
    }

    this.graphicsLayer {
        translationX = offsetX.value * density
    }
}

/**
 * Interactive 3D Perspective Tilt on touch drag / gyroscopic feel.
 */
fun Modifier.tilt3D(
    maxAngle: Float = 10f
): Modifier = composed {
    var rotX by remember { mutableFloatStateOf(0f) }
    var rotY by remember { mutableFloatStateOf(0f) }

    this
        .graphicsLayer {
            rotationX = rotX
            rotationY = rotY
            cameraDistance = 16f * density
            transformOrigin = TransformOrigin.Center
        }
        .pointerInput(Unit) {
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                val width = size.width
                val height = size.height
                val normX = ((down.position.x / width) - 0.5f) * 2f
                val normY = ((down.position.y / height) - 0.5f) * 2f

                rotX = -normY * maxAngle
                rotY = normX * maxAngle

                val up = waitForUpOrCancellation()
                rotX = 0f
                rotY = 0f
            }
        }
}
