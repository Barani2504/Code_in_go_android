package com.simats.codeingo.ui.phoenix

import android.media.MediaPlayer
import android.os.Build
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.simats.codeingo.R
import com.simats.codeingo.ui.theme.AmberGold
import kotlinx.coroutines.Job
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

    val context = LocalContext.current
    var isWaving by remember { mutableStateOf(false) }
    var waveToken by remember { mutableIntStateOf(0) }
    var waveJob by remember { mutableStateOf<Job?>(null) }

    val imageLoader = remember(context) {
        ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

    LaunchedEffect(Unit) {
        try {
            imageLoader.execute(
                ImageRequest.Builder(context)
                    .data(R.raw.phoenix_waving)
                    .build()
            )
        } catch (_: Exception) {}
    }

    fun launchWavingAnimation() {
        if (isFlying) return
        waveJob?.cancel()
        waveJob = coroutineScope.launch {
            waveToken++
            isWaving = true
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)

            // Play friendly greeting audio
            try {
                val player = MediaPlayer.create(context, R.raw.phoenix_greeting)
                if (player != null) {
                    player.setVolume(0.85f, 0.85f)
                    player.setOnCompletionListener { mp ->
                        try { mp.release() } catch (_: Exception) {}
                    }
                    player.start()
                }
            } catch (_: Exception) {}

            // Interactive tap scale bounce
            launch {
                tapBounce.animateTo(1.10f, spring(0.52f, 600f))
                tapBounce.animateTo(1.0f, spring(0.62f, 400f))
            }

            // Play full 2.4s duration of phoenix-waving.gif
            delay(2400L)

            isWaving = false

            // Gentle landing settle
            launch {
                landingSquashX.animateTo(1.04f, spring(0.5f, 500f))
                landingSquashX.animateTo(1.0f, spring(0.6f, 400f))
            }
        }
    }

    fun launchSlowFlightAlongTrack() {
        if (isFlying) return
        waveJob?.cancel()
        isWaving = false
        coroutineScope.launch {
            isFlying = true
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)

            landingRippleOpacity.snapTo(0f)
            landingRippleScale.snapTo(0.4f)
            landingSquashX.snapTo(1f)
            landingSquashY.snapTo(1f)
            flightProgressAnim.snapTo(0f)

            // Smooth aerodynamic flight duration scaled to distance
            val flightDistance = kotlin.math.sqrt(
                (flyFromOffset.x.value * flyFromOffset.x.value) + (flyFromOffset.y.value * flyFromOffset.y.value)
            )
            val duration = when {
                flightDistance > 250f -> 3500
                flightDistance > 100f -> 2700
                flightDistance > 10f -> 2100
                else -> 1400
            }

            flightProgressAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = duration,
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
    val isAnyFlightActive = isFlying

    // Computed Flight Offsets
    val currentFlightOffsetX: Dp = if (isFlying) {
        val linearX = flyFromOffset.x.value * (1f - p)
        val arcOffset = sin(p * PI.toFloat()) * (if (flyFromOffset.x.value > 0) -24f else 24f)
        (linearX + arcOffset).dp
    } else 0.dp

    val currentFlightOffsetY: Dp = if (isFlying) {
        val linearY = flyFromOffset.y.value * (1f - p)
        val liftApex = -sin(p * PI.toFloat()) * 26f
        val wingbeatHeave = sin(p * 4f * PI.toFloat()) * 3.2f
        (linearY + liftApex + wingbeatHeave).dp
    } else hoverBob.dp

    // Aerodynamic banking tilt into turns
    val flightBankAngle: Float = if (isFlying) {
        val directionMultiplier = if (flyFromOffset.x.value < 0) 13f else -13f
        val verticalMultiplier = if (flyFromOffset.y.value > 0) -1f else 1f
        sin(p * PI.toFloat()) * directionMultiplier * verticalMultiplier
    } else 0f

    // Dynamic camera swoop scale
    val flightDynamicScale: Float = if (isFlying) {
        1f + (sin(p * PI.toFloat()) * 0.16f)
    } else tapBounce.value

    // Continuous, Silky Smooth 3D Avian Wing Flap Frame Sequence
    var flightFrameIndex by remember { mutableIntStateOf(0) }
    LaunchedEffect(isFlying) {
        if (isFlying) {
            flightFrameIndex = 0
            while (isActive && isFlying) {
                delay(75L)
                flightFrameIndex = (flightFrameIndex + 1) % NodeFlyFrames.size
            }
        } else {
            flightFrameIndex = 0
        }
    }

    val sittingOrFlyingResId: Int = if (isFlying) {
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
                // Tapping Phoenix in main dashboard triggers the 3D waving gesture (phoenix_waving)!
                launchWavingAnimation()
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
                val alpha1 = if (isAnyFlightActive) 0.08f else 0.38f
                val alpha2 = if (isAnyFlightActive) 0.02f else 0.12f
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
            val glowDiameter = maxOf(width, height) * (if (isAnyFlightActive) 1.55f else 1.25f)
            Canvas(
                modifier = Modifier
                    .size(glowDiameter)
                    .graphicsLayer { alpha = if (isAnyFlightActive) 0.90f else 0.65f }
            ) {
                val glowRadius = size.width * 0.44f
                drawCircle(
                    brush = Brush.radialGradient(
                        colorStops = arrayOf(
                            0.0f to AmberGold.copy(alpha = (if (isAnyFlightActive) 0.34f else 0.20f) * auraGlow),
                            0.35f to Color(0xFFFF9500).copy(alpha = (if (isAnyFlightActive) 0.18f else 0.10f) * auraGlow),
                            0.70f to Color(0xFFFF7A00).copy(alpha = (if (isAnyFlightActive) 0.05f else 0.02f) * auraGlow),
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

        // MARK: - 4. Phoenix Mascot Artwork: 100% True Alpha, Zero Background
        if (isWaving) {
            // Live 3D Waving from phoenix-waving.gif (R.raw.phoenix_waving)
            val wavingPainter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(context)
                    .data(R.raw.phoenix_waving)
                    .crossfade(false)
                    .setParameter("wave_token", waveToken, memoryCacheKey = "phoenix_waving_$waveToken")
                    .build(),
                imageLoader = imageLoader
            )
            Image(
                painter = wavingPainter,
                contentDescription = "Phoenix Companion Waving",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(width, height)
                    .graphicsLayer {
                        scaleX = tapBounce.value * landingSquashX.value
                        scaleY = tapBounce.value * landingSquashY.value
                    }
            )
        } else {
            Image(
                painter = painterResource(id = sittingOrFlyingResId),
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
}
