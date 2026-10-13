package com.simats.codeingo.ui.phoenix

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.simats.codeingo.R
import com.simats.codeingo.ui.theme.AmberGold
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * PhoenixNodeJumpFlightView — Ultra-smooth 3D Phoenix Mascot with organic flight & sitting physics:
 * - 100% transparent, ZERO background artifacts, ZERO orange square clipping boxes.
 * - Majestic 3.6s flight along the track with S-curve lateral swing and aerodynamic lift apex.
 * - Lifelike wing flapping cycles in mid-air (frames 4..21) synchronized with flight kinematics.
 * - Takeoff wing-unfurling (0..4) and touchdown landing-flare (20..23 -> 0).
 * - Perched sitting firmly on its feet on the track with gentle breathing hover bob.
 * - Grounded drop shadow, touchdown ripple, trailing ember sparks, and haptic feedback.
 * - Tapping Phoenix re-triggers the slow flight sequence anytime.
 */
@Composable
fun PhoenixNodeJumpFlightView(
    modifier: Modifier = Modifier,
    width: Dp = 104.dp,
    height: Dp = 120.dp,
    flyFromOffset: DpOffset = DpOffset(0.dp, (-210).dp),
    shouldFly: Boolean = false,
    triggerFlightToken: Int = 0,
    showTrailGlow: Boolean = true,
    autoFlyOnAppear: Boolean = false,
    onLandingComplete: (() -> Unit)? = null
) {
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    var isFlying by remember { mutableStateOf(false) }
    val flightProgressAnim = remember { Animatable(1f) }
    val landingRippleScale = remember { Animatable(0.4f) }
    val landingRippleOpacity = remember { Animatable(0f) }
    val tapBounce = remember { Animatable(1f) }
    val landingSquashX = remember { Animatable(1f) }
    val landingSquashY = remember { Animatable(1f) }

    // Gentle Breathing Hover While Sitting
    val infiniteTransition = rememberInfiniteTransition(label = "sittingHoverBob")
    val hoverBob by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hoverBob"
    )

    val auraGlow by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "auraGlow"
    )

    fun launchSlowFlightAlongTrack() {
        if (isFlying) return
        coroutineScope.launch {
            isFlying = true
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)

            landingRippleOpacity.snapTo(0f)
            landingRippleScale.snapTo(0.4f)
            landingSquashX.snapTo(1f)
            landingSquashY.snapTo(1f)
            flightProgressAnim.snapTo(0f)

            // Truly slow, graceful, cinematic flight from previous node down along the track (3.6s)
            flightProgressAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 3600,
                    easing = CubicBezierEasing(0.42f, 0.0f, 0.58f, 1.0f)
                )
            )

            // Touchdown: land and SIT firmly on the track
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            isFlying = false

            // Touchdown ripple expansion
            launch {
                landingRippleOpacity.snapTo(0.85f)
                landingRippleOpacity.animateTo(0f, animationSpec = tween(650, easing = FastOutSlowInEasing))
            }
            launch {
                landingRippleScale.animateTo(
                    targetValue = 1.6f,
                    animationSpec = spring(dampingRatio = 0.65f, stiffness = 450f)
                )
            }
            // Juicy character landing squash & stretch settle
            launch {
                landingSquashX.animateTo(1.08f, spring(0.5f, 600f))
                landingSquashX.animateTo(1.0f, spring(0.6f, 400f))
            }
            launch {
                landingSquashY.animateTo(0.92f, spring(0.5f, 600f))
                landingSquashY.animateTo(1.0f, spring(0.6f, 400f))
            }

            onLandingComplete?.invoke()
        }
    }

    LaunchedEffect(autoFlyOnAppear) {
        if (autoFlyOnAppear) {
            delay(250)
            launchSlowFlightAlongTrack()
        }
    }

    LaunchedEffect(shouldFly) {
        if (shouldFly) {
            launchSlowFlightAlongTrack()
        }
    }

    LaunchedEffect(triggerFlightToken) {
        if (triggerFlightToken > 0) {
            launchSlowFlightAlongTrack()
        }
    }

    val p = flightProgressAnim.value

    // Computed Flight Offsets along the Track
    val currentFlightOffsetX: Dp = if (!isFlying) 0.dp else {
        val linearX = flyFromOffset.x.value * (1f - p)
        val arcOffset = sin(p * PI.toFloat()) * (if (flyFromOffset.x.value > 0) -26f else 26f)
        (linearX + arcOffset).dp
    }

    // Aerodynamic flight lift apex + subtle wingbeat heave during mid-air flaps
    val wingbeatHeave = if (isFlying) {
        val flapCycleP = p * 4f * PI.toFloat()
        sin(flapCycleP) * 3.2f
    } else 0f

    val currentFlightOffsetY: Dp = if (!isFlying) hoverBob.dp else {
        val linearY = flyFromOffset.y.value * (1f - p)
        val liftApex = -sin(p * PI.toFloat()) * 26f
        (linearY + liftApex + wingbeatHeave).dp
    }

    // Aerodynamic banking tilt into turns
    val flightBankAngle: Float = if (!isFlying) 0f else {
        val directionMultiplier = if (flyFromOffset.x.value < 0) 13f else -13f
        sin(p * PI.toFloat()) * directionMultiplier
    }

    // Dynamic camera swoop scale
    val flightDynamicScale: Float = if (isFlying) {
        1f + (sin(p * PI.toFloat()) * 0.16f)
    } else {
        tapBounce.value
    }

    // Continuous, Silky Smooth 3D Avian Wing Flap Frame Sequence
    var flightFrameIndex by remember { mutableIntStateOf(0) }
    LaunchedEffect(isFlying) {
        if (isFlying) {
            flightFrameIndex = 0
            // 48 frames across 3600ms = 75ms per frame (2 full 24-frame cycles, landing cleanly on frame 0)
            while (isActive && isFlying) {
                delay(75L)
                flightFrameIndex = (flightFrameIndex + 1) % NodeFlyFrames.size
            }
        } else {
            flightFrameIndex = 0
        }
    }

    val currentFrameResId: Int = if (isFlying) {
        NodeFlyFrames[flightFrameIndex]
    } else {
        NodeFlyFrames[0] // Firmly standing on its feet on the track!
    }

    Box(
        modifier = modifier
            .size(width, height)
            .offset(x = currentFlightOffsetX, y = currentFlightOffsetY)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                // Tapping Phoenix launches the slow flight so user can experience it anytime!
                coroutineScope.launch {
                    tapBounce.animateTo(1.08f, spring(0.5f, 500f))
                    tapBounce.animateTo(1.0f, spring(0.6f, 400f))
                }
                launchSlowFlightAlongTrack()
            },
        contentAlignment = Alignment.Center
    ) {
        // MARK: - 1. Grounded Track Drop Shadow (True 100% Alpha Gradient, Zero Box Clipping)
        val shadowW = width * (if (isFlying) 0.40f else 0.64f)
        val shadowH = if (isFlying) 8.dp else 14.dp
        Box(
            modifier = Modifier
                .size(shadowW, shadowH)
                .offset(y = height * 0.44f),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val alpha1 = if (isFlying) 0.08f else 0.38f
                val alpha2 = if (isFlying) 0.02f else 0.12f
                val rad = size.width * 0.46f
                drawOval(
                    brush = Brush.radialGradient(
                        colorStops = arrayOf(
                            0.0f to Color.Black.copy(alpha = alpha1),
                            0.50f to Color.Black.copy(alpha = alpha2),
                            0.85f to Color.Black.copy(alpha = alpha2 * 0.3f),
                            1.0f to Color.Transparent
                        ),
                        center = center,
                        radius = rad
                    ),
                    topLeft = Offset.Zero,
                    size = size
                )
            }
        }

        // MARK: - 2. Touchdown Landing Ripple
        val ripScale = landingRippleScale.value
        val ripOpacity = landingRippleOpacity.value
        if (ripOpacity > 0.01f) {
            val rippleDiameter = (width * 1.35f) * ripScale
            Box(
                modifier = Modifier
                    .size(rippleDiameter)
                    .offset(y = height * 0.40f)
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    drawCircle(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                AmberGold.copy(alpha = ripOpacity),
                                Color(0xFFFF9500).copy(alpha = ripOpacity * 0.5f),
                                Color.Transparent
                            )
                        ),
                        style = Stroke(width = 3.dp.toPx())
                    )
                }
            }
        }

        // MARK: - 3. Soft Golden Radiant Ember Aura (Smooth Mathematical Radial Gradient, Zero Clipping Bounds)
        if (showTrailGlow) {
            val glowDiameter = maxOf(width, height) * (if (isFlying) 1.55f else 1.25f)
            Canvas(
                modifier = Modifier
                    .size(glowDiameter)
                    .graphicsLayer { alpha = if (isFlying) 0.90f else 0.65f }
            ) {
                // Strictly kept within 0.44 of canvas diameter so it smoothly fades to 100% transparent well before edges!
                val glowRadius = size.width * 0.44f
                drawCircle(
                    brush = Brush.radialGradient(
                        colorStops = arrayOf(
                            0.0f to AmberGold.copy(alpha = (if (isFlying) 0.34f else 0.20f) * auraGlow),
                            0.35f to Color(0xFFFF9500).copy(alpha = (if (isFlying) 0.18f else 0.10f) * auraGlow),
                            0.70f to Color(0xFFFF7A00).copy(alpha = (if (isFlying) 0.05f else 0.02f) * auraGlow),
                            1.0f to Color.Transparent
                        ),
                        center = center,
                        radius = glowRadius
                    ),
                    radius = glowRadius,
                    center = center
                )
            }
        }

        // MARK: - 4. Trailing Ember Sparks During Flight (Zero Box Blur Artifacts, Smooth Canvas Drawing)
        if (isFlying) {
            Canvas(modifier = Modifier.matchParentSize()) {
                for (i in 0 until 6) {
                    val sparkDelay = i * 0.10f
                    val progressLag = maxOf(0f, p - sparkDelay)
                    val sparkY = (flyFromOffset.y.toPx() * (1f - progressLag)) + (i * 12.dp.toPx())
                    val sparkX = (flyFromOffset.x.toPx() * (1f - progressLag)) + (if (i % 2 == 0) 9.dp.toPx() else -9.dp.toPx())
                    val sparkRad = maxOf(1.5.dp.toPx(), (4.5f - i * 0.5f).dp.toPx())
                    val sparkAlpha = ((1f - sparkDelay) * 0.85f).coerceIn(0f, 1f)

                    drawCircle(
                        brush = Brush.radialGradient(
                            colorStops = arrayOf(
                                0.0f to AmberGold.copy(alpha = sparkAlpha),
                                0.7f to Color(0xFFFF9500).copy(alpha = sparkAlpha * 0.6f),
                                1.0f to Color.Transparent
                            ),
                            center = Offset(center.x + sparkX, center.y + sparkY),
                            radius = sparkRad * 1.5f
                        ),
                        radius = sparkRad * 1.5f,
                        center = Offset(center.x + sparkX, center.y + sparkY)
                    )
                }
            }
        }

        // MARK: - 5. Phoenix Mascot Artwork: 100% True Alpha, Zero Background
        Image(
            painter = painterResource(id = currentFrameResId),
            contentDescription = "Phoenix Companion",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(width, height)
                .graphicsLayer {
                    rotationZ = flightBankAngle
                    scaleX = flightDynamicScale * landingSquashX.value
                    scaleY = flightDynamicScale * landingSquashY.value
                }
        )
    }
}
