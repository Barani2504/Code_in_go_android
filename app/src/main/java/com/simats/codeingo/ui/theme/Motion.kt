package com.simats.codeingo.ui.theme

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// ══════════════════════════════════════════════════════════════════
// ⚡ PHOENIX MOTION & GAME-FEEL ENGINE (B0-B1)
// ══════════════════════════════════════════════════════════════════
// Three Effect Tiers:
//   FULL    — Full springs, 60-particle bursts, shakes, screen edge lighting, combos
//   REDUCED — Fades only, subtle scales, no shakes, zero particles (low power/battery saver)
//   OFF     — User toggle: static, instantaneous transitions, zero motion effects
//
// PHOENIX LOCK GUARANTEE:
// The Phoenix GIF is completely independent of these tiers and plays
// unchanged in FULL, REDUCED, and OFF. No color tint, blur, alpha changes,
// clipping, or overlays are ever drawn on top of the Phoenix pixels.
// ══════════════════════════════════════════════════════════════════

enum class EffectTier {
    FULL,
    REDUCED,
    OFF
}

/** Global CompositionLocal for the active effect tier */
val LocalEffectTier = compositionLocalOf { EffectTier.FULL }

/** Legacy boolean reduce-motion support (maps to REDUCED or OFF) */
val LocalReduceMotion = compositionLocalOf { false }

object MotionController {
    private val _effectTier = MutableStateFlow(EffectTier.FULL)
    val effectTier: StateFlow<EffectTier> = _effectTier.asStateFlow()

    private val _soundEnabled = MutableStateFlow(true)
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _hapticsEnabled = MutableStateFlow(true)
    val hapticsEnabled: StateFlow<Boolean> = _hapticsEnabled.asStateFlow()

    fun setEffectTier(tier: EffectTier) {
        _effectTier.value = tier
    }

    fun setSoundEnabled(enabled: Boolean) {
        _soundEnabled.value = enabled
    }

    fun setHapticsEnabled(enabled: Boolean) {
        _hapticsEnabled.value = enabled
    }
}

// ──────────────────────────────────────────────────────────────────
// 📐 PHOENIX MOTION CONSTANTS (Compose + SwiftUI Cross-Platform)
// ──────────────────────────────────────────────────────────────────
object PhoenixMotion {
    /**
     * Tactile Press:
     * Compose: stiffness = 986f, dampingRatio = 0.60f
     * SwiftUI: response = 0.20s, dampingFraction = 0.60
     */
    val PressSpring: AnimationSpec<Float> = spring(
        dampingRatio = 0.60f,
        stiffness = 986f
    )
    val PressSpringDp: AnimationSpec<Dp> = spring(
        dampingRatio = 0.60f,
        stiffness = 986f
    )

    /**
     * Celebratory Bounce:
     * Compose: stiffness = 322f, dampingRatio = 0.50f
     * SwiftUI: response = 0.35s, dampingFraction = 0.50
     */
    val BounceSpring: AnimationSpec<Float> = spring(
        dampingRatio = 0.50f,
        stiffness = 322f
    )
    val BounceSpringDp: AnimationSpec<Dp> = spring(
        dampingRatio = 0.50f,
        stiffness = 322f
    )

    /**
     * Snappy Feedback:
     * Compose: stiffness = 438f, dampingRatio = 0.75f
     * SwiftUI: response = 0.30s, dampingFraction = 0.75
     */
    val SnappySpring: AnimationSpec<Float> = spring(
        dampingRatio = 0.75f,
        stiffness = 438f
    )
    val SnappySpringDp: AnimationSpec<Dp> = spring(
        dampingRatio = 0.75f,
        stiffness = 438f
    )

    /**
     * Gentle Transition:
     * Compose: stiffness = 158f, dampingRatio = 0.80f
     * SwiftUI: response = 0.50s, dampingFraction = 0.80
     */
    val GentleSpring: AnimationSpec<Float> = spring(
        dampingRatio = 0.80f,
        stiffness = 158f
    )
    val GentleSpringDp: AnimationSpec<Dp> = spring(
        dampingRatio = 0.80f,
        stiffness = 158f
    )

    /**
     * Floating & Hover Physics:
     * Compose: stiffness = 62f, dampingRatio = 0.85f
     * SwiftUI: response = 0.80s, dampingFraction = 0.85
     */
    val FloatSpring: AnimationSpec<Float> = spring(
        dampingRatio = 0.85f,
        stiffness = 62f
    )

    // Platform-neutral Durations (ms)
    const val DurationFastMs = 150
    const val DurationMediumMs = 300
    const val DurationSlowMs = 500
}

// ──────────────────────────────────────────────────────────────────
// 🎯 GATED INTERACTION MODIFIERS (Tier-Aware, Cancel-Safe)
// ──────────────────────────────────────────────────────────────────

/**
 * 3D Press Scale with automatic haptic feedback.
 * Cancel-safe: rapid taps or exit cleanly resets without state bleed.
 * Optimized: Animates directly in graphicsLayer without triggering composable recompositions.
 */
fun Modifier.pressScale(
    targetScale: Float = 0.96f,
    onClick: (() -> Unit)? = null
): Modifier = composed {
    val tier = LocalEffectTier.current
    val reduceMotion = LocalReduceMotion.current || tier != EffectTier.FULL
    val effectiveTarget = when {
        tier == EffectTier.OFF -> 1.0f
        reduceMotion -> 0.985f
        else -> targetScale
    }
    val scaleAnim = remember { Animatable(1.0f) }
    val coroutineScope = rememberCoroutineScope()

    this
        .graphicsLayer {
            scaleX = scaleAnim.value
            scaleY = scaleAnim.value
        }
        .pointerInput(tier, effectiveTarget) {
            if (tier == EffectTier.OFF) return@pointerInput
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false)
                val downJob = coroutineScope.launch {
                    scaleAnim.animateTo(effectiveTarget, PhoenixMotion.PressSpring)
                }
                waitForUpOrCancellation()
                downJob.cancel()
                coroutineScope.launch {
                    scaleAnim.animateTo(1.0f, PhoenixMotion.PressSpring)
                }
            }
        }
}

/**
 * Snappy error shake modifier.
 * Cancel-safe: suppressed in REDUCED and OFF tiers.
 */
fun Modifier.shake(
    shakeTrigger: Any? = null,
    amplitude: Float = 8f
): Modifier = composed {
    val tier = LocalEffectTier.current
    val offsetX = remember { Animatable(0f) }

    LaunchedEffect(shakeTrigger) {
        if (shakeTrigger == null || tier != EffectTier.FULL) return@LaunchedEffect
        val keyframes = listOf(-amplitude, amplitude * 0.75f, -amplitude * 0.5f, amplitude * 0.25f, 0f)
        for (kf in keyframes) {
            offsetX.animateTo(kf, animationSpec = tween(35, easing = LinearEasing))
        }
    }

    this.graphicsLayer {
        translationX = offsetX.value
    }
}

/**
 * Pulsing scale/alpha animation.
 * In REDUCED: alpha pulse only (no scale changes).
 * In OFF: completely static.
 */
fun Modifier.pulse(
    scaleRange: ClosedFloatingPointRange<Float> = 0.97f..1.03f,
    durationMs: Int = 1200,
    enabled: Boolean = true
): Modifier = composed {
    if (!enabled) return@composed this
    val tier = LocalEffectTier.current
    if (tier == EffectTier.OFF) return@composed this

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val isReduced = tier == EffectTier.REDUCED || LocalReduceMotion.current

    val scale by infiniteTransition.animateFloat(
        initialValue = if (isReduced) 1.0f else scaleRange.start,
        targetValue = if (isReduced) 1.0f else scaleRange.endInclusive,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
        this.alpha = alpha
    }
}

/**
 * Floating vertical oscillation.
 */
fun Modifier.floating(
    distanceDp: Dp = 6.dp,
    durationMs: Int = 2000
): Modifier = composed {
    val tier = LocalEffectTier.current
    if (tier == EffectTier.OFF) return@composed this

    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -distanceDp.value,
        targetValue = distanceDp.value,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatingY"
    )
    this.graphicsLayer { translationY = offsetY }
}

/**
 * Shimmer horizontal sweep effect.
 */
fun Modifier.shimmer(
    shimmerColor: Color = Color.White.copy(alpha = 0.3f),
    durationMs: Int = 1400
): Modifier = composed {
    val tier = LocalEffectTier.current
    if (tier == EffectTier.OFF) return@composed this

    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
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
        val shimmerWidth = width * 0.6f
        val x = (width + shimmerWidth * 2) * progress - shimmerWidth
        val brush = Brush.linearGradient(
            colors = listOf(Color.Transparent, shimmerColor, Color.Transparent),
            start = Offset(x, 0f),
            end = Offset(x + shimmerWidth, height)
        )
        drawRect(brush = brush)
    }
}

/**
 * Celebratory bounce pop on correct answer.
 * Cancel-safe: resets and re-plays each time trigger flips to true.
 */
fun Modifier.correctPop(
    trigger: Boolean
): Modifier = composed {
    val tier = LocalEffectTier.current
    val scale = remember { Animatable(1f) }

    LaunchedEffect(trigger) {
        if (!trigger || tier == EffectTier.OFF) return@LaunchedEffect
        val peak = if (tier == EffectTier.REDUCED) 1.04f else 1.18f
        scale.animateTo(peak, animationSpec = PhoenixMotion.BounceSpring)
        scale.animateTo(1f, animationSpec = PhoenixMotion.SnappySpring)
    }

    this.graphicsLayer {
        scaleX = scale.value
        scaleY = scale.value
    }
}

/**
 * 3D Tilt perspective modifier. Gated to FULL tier only.
 */
fun Modifier.tilt3D(
    maxAngle: Float = 8f
): Modifier = composed {
    val tier = LocalEffectTier.current
    if (tier != EffectTier.FULL) return@composed this

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
                val normX = if (width > 0) ((down.position.x / width) - 0.5f) * 2f else 0f
                val normY = if (height > 0) ((down.position.y / height) - 0.5f) * 2f else 0f

                rotX = -normY * maxAngle
                rotY = normX * maxAngle

                waitForUpOrCancellation()
                rotX = 0f
                rotY = 0f
            }
        }
}

fun Modifier.bounceOnAppear(
    delayMs: Long = 0L,
    initialScale: Float = 0.70f
): Modifier = composed {
    val tier = LocalEffectTier.current
    val scale = remember { Animatable(if (tier == EffectTier.OFF) 1f else initialScale) }

    LaunchedEffect(Unit) {
        if (tier == EffectTier.OFF) return@LaunchedEffect
        if (delayMs > 0) delay(delayMs)
        scale.animateTo(1f, animationSpec = PhoenixMotion.BounceSpring)
    }

    this.graphicsLayer {
        scaleX = scale.value
        scaleY = scale.value
    }
}

fun Modifier.staggeredAppear(
    index: Int,
    baseDelayMs: Long = 60L
): Modifier = composed {
    val tier = LocalEffectTier.current
    val alpha = remember { Animatable(if (tier == EffectTier.OFF) 1f else 0f) }
    val offsetY = remember { Animatable(if (tier == EffectTier.OFF) 0f else 20f) }

    LaunchedEffect(Unit) {
        if (tier == EffectTier.OFF) return@LaunchedEffect
        delay(index * baseDelayMs)
        alpha.animateTo(1f, tween(240, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(Unit) {
        if (tier == EffectTier.OFF) return@LaunchedEffect
        delay(index * baseDelayMs)
        offsetY.animateTo(0f, PhoenixMotion.SnappySpring)
    }

    this.graphicsLayer {
        this.alpha = alpha.value
        translationY = offsetY.value
    }
}

// ──────────────────────────────────────────────────────────────────
// 💥 HIGH-PERFORMANCE PARTICLE ENGINE (Hard cap 60, pooled, 1 Canvas)
// ──────────────────────────────────────────────────────────────────

data class PooledParticle(
    var active: Boolean = false,
    var x: Float = 0f,
    var y: Float = 0f,
    var vx: Float = 0f,
    var vy: Float = 0f,
    var size: Float = 8f,
    var alpha: Float = 1f,
    var color: Color = Color.Yellow,
    var currentLife: Float = 0f,
    var maxLife: Float = 40f
)

class ParticlePool(val capacity: Int = 60) {
    val particles = Array(capacity) { PooledParticle() }

    fun spawnBurst(origin: Offset, colors: List<Color>, count: Int = 24) {
        val toSpawn = minOf(count, capacity)
        var spawned = 0
        for (p in particles) {
            if (!p.active) {
                p.active = true
                p.x = origin.x
                p.y = origin.y
                val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
                val speed = Random.nextFloat() * 8f + 3f
                p.vx = cos(angle) * speed
                p.vy = sin(angle) * speed - 2.5f // upward lift
                p.size = Random.nextFloat() * 6f + 4f
                p.alpha = 1f
                p.color = colors.random()
                p.currentLife = 0f
                p.maxLife = Random.nextFloat() * 25f + 25f
                spawned++
                if (spawned >= toSpawn) break
            }
        }
    }

    fun update(): Boolean {
        var anyActive = false
        for (p in particles) {
            if (p.active) {
                p.x += p.vx
                p.y += p.vy
                p.vy += 0.28f // gravity
                p.currentLife += 1f
                p.alpha = (1f - (p.currentLife / p.maxLife)).coerceIn(0f, 1f)
                if (p.currentLife >= p.maxLife || p.alpha <= 0.02f) {
                    p.active = false
                } else {
                    anyActive = true
                }
            }
        }
        return anyActive
    }

    fun clear() {
        for (p in particles) p.active = false
    }
}

@Composable
fun ParticleBurstCanvas(
    pool: ParticlePool,
    modifier: Modifier = Modifier
) {
    val tier = LocalEffectTier.current
    if (tier != EffectTier.FULL) return

    var tick by remember { mutableIntStateOf(0) }
    LaunchedEffect(pool) {
        while (isActive) {
            delay(16L) // ~60 FPS lockstep for simulation
            val hasActive = pool.update()
            if (hasActive) {
                tick = (tick + 1) % 10000
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        // Read tick to trigger redraw
        @Suppress("UNUSED_VARIABLE")
        val drawTick = tick
        for (p in pool.particles) {
            if (p.active && p.alpha > 0.01f) {
                drawCircle(
                    color = p.color.copy(alpha = p.alpha),
                    radius = p.size,
                    center = Offset(p.x, p.y)
                )
            }
        }
    }
}

@Composable
fun ParticleBurstCanvas(
    trigger: Any?,
    modifier: Modifier = Modifier,
    burstOrigin: Offset = Offset.Zero,
    particleCount: Int = 60,
    colors: List<Color> = listOf(Color(0xFFFFB300), Color(0xFFFF8A00), Color(0xFF00E676), Color(0xFF2979FF))
) {
    val pool = remember { ParticlePool(particleCount) }
    LaunchedEffect(trigger) {
        if (trigger != null && trigger != 0) {
            pool.spawnBurst(origin = burstOrigin, colors = colors, count = particleCount)
        }
    }
    ParticleBurstCanvas(pool = pool, modifier = modifier)
}

// ──────────────────────────────────────────────────────────────────
// 🌈 SCREEN-EDGE LIGHTING SWEEP (Part C2)
// ──────────────────────────────────────────────────────────────────

@Composable
fun ScreenEdgeLighting(
    color: Color,
    trigger: Any?,
    modifier: Modifier = Modifier
) {
    val tier = LocalEffectTier.current
    if (tier == EffectTier.OFF) return

    val alpha = remember { Animatable(0f) }

    LaunchedEffect(trigger) {
        if (trigger == null) return@LaunchedEffect
        alpha.snapTo(0.85f)
        alpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(if (tier == EffectTier.REDUCED) 400 else 650, easing = FastOutSlowInEasing)
        )
    }

    if (alpha.value <= 0.01f) return

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { this.alpha = alpha.value }
    ) {
        val strokeWidth = 5.dp.toPx()
        val glowBrush = Brush.radialGradient(
            colors = listOf(color.copy(alpha = 0.5f), Color.Transparent),
            center = center,
            radius = size.maxDimension / 2f
        )
        // Outer perimeter stroke
        drawRect(
            brush = Brush.verticalGradient(
                listOf(color.copy(alpha = 0.9f), color.copy(alpha = 0.3f), color.copy(alpha = 0.9f))
            ),
            style = Stroke(width = strokeWidth)
        )
    }
}

// ──────────────────────────────────────────────────────────────────
// 💬 MASCOT SPEECH BUBBLE WITH FAST TYPEWRITER REVEAL (B1)
// ──────────────────────────────────────────────────────────────────

/**
 * Fast typewriter text reveal (~25ms/char). Tap to skip immediately.
 * Sits beside/above the Phoenix and never overlaps it.
 */
@Composable
fun TypewriterText(
    fullText: String = "",
    text: String = fullText,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle(fontSize = 15.sp, color = Color.White),
    textStyle: TextStyle = style,
    charDelayMs: Long = 25L,
    onComplete: () -> Unit = {}
) {
    val textToUse = if (fullText.isNotEmpty()) fullText else text
    val styleToUse = if (textStyle != style) textStyle else style
    val tier = LocalEffectTier.current
    var displayedChars by remember(textToUse) {
        mutableIntStateOf(if (tier == EffectTier.OFF) textToUse.length else 0)
    }

    LaunchedEffect(textToUse, tier) {
        if (tier == EffectTier.OFF) {
            displayedChars = textToUse.length
            onComplete()
            return@LaunchedEffect
        }
        displayedChars = 0
        for (i in 1..textToUse.length) {
            delay(charDelayMs)
            displayedChars = i
        }
        onComplete()
    }

    Text(
        text = textToUse.take(displayedChars),
        style = styleToUse,
        modifier = modifier.clickable {
            // Tap to skip reveal instantly
            displayedChars = textToUse.length
            onComplete()
        }
    )
}

// ──────────────────────────────────────────────────────────────────
// 🔥 BEHIND-PHOENIX GLOW (B1)
// ──────────────────────────────────────────────────────────────────

/**
 * Soft static or slow-pulsing background halo drawn strictly BENEATH
 * the Phoenix in z-order. It never overlaps or tints the Phoenix pixels.
 */
@Composable
fun BehindPhoenixGlow(
    color: Color = Color(0xFFFFB300),
    glowColor: Color = color,
    size: Dp = 100.dp,
    modifier: Modifier = Modifier
) {
    val activeColor = if (glowColor != color) glowColor else color
    val tier = LocalEffectTier.current
    if (tier == EffectTier.OFF) return

    val infiniteTransition = rememberInfiniteTransition(label = "behindGlow")
    val isPulsing = tier == EffectTier.FULL

    val glowAlpha by if (isPulsing) {
        infiniteTransition.animateFloat(
            initialValue = 0.25f,
            targetValue = 0.55f,
            animationSpec = infiniteRepeatable(
                animation = tween(2200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "glowAlpha"
        )
    } else {
        remember { mutableFloatStateOf(0.30f) }
    }

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer { this.alpha = glowAlpha }
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        activeColor.copy(alpha = 0.45f),
                        activeColor.copy(alpha = 0.15f),
                        Color.Transparent
                    )
                ),
                shape = CircleShape
            )
    )
}

// ──────────────────────────────────────────────────────────────────
// ⏱️ BOSS TIMER RING (B1)
// ──────────────────────────────────────────────────────────────────

/**
 * Circular countdown ring placed AROUND the Phoenix, outside its bounds
 * with safe padding. It never overlaps or clips the Phoenix pixels.
 */
@Composable
fun BossTimerRing(
    timeRemainingSeconds: Int,
    totalTimeSeconds: Int,
    modifier: Modifier = Modifier,
    paddingAroundMascot: Dp = 8.dp,
    ringStrokeWidth: Dp = 3.5.dp,
    content: @Composable () -> Unit
) {
    val progress = if (totalTimeSeconds > 0) {
        (timeRemainingSeconds.toFloat() / totalTimeSeconds.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val ringColor = when {
        timeRemainingSeconds <= 5 -> Color(0xFFFF3B30)
        timeRemainingSeconds <= 10 -> Color(0xFFFF9500)
        else -> Color(0xFF00E676)
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(400, easing = LinearEasing),
        label = "bossTimerProgress"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Outer countdown canvas
        Canvas(
            modifier = Modifier
                .matchParentSize()
                .padding(paddingAroundMascot)
        ) {
            val strokePx = ringStrokeWidth.toPx()
            // Track background
            drawCircle(
                color = Color.White.copy(alpha = 0.10f),
                style = Stroke(width = strokePx)
            )
            // Animated remaining arc
            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }

        // Phoenix content safely nested inside, never clipped
        content()
    }
}

// ──────────────────────────────────────────────────────────────────
// 💥 FEEDBACK MANAGER (Haptics + Sound with Settings & Tier Respect)
// ──────────────────────────────────────────────────────────────────

/**
 * Centralized haptic and sound controller with tier gating,
 * combo escalation, and user/system settings observance.
 */
class FeedbackManager(private val context: Context) {

    private val vibrator: Vibrator? = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vm?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    } catch (_: Throwable) {
        null
    }

    private val isSystemHapticsEnabled: Boolean
        get() = try {
            Settings.System.getInt(context.contentResolver, Settings.System.HAPTIC_FEEDBACK_ENABLED, 1) != 0
        } catch (_: Throwable) {
            true
        }

    fun tap(tier: EffectTier = EffectTier.FULL) {
        if (tier == EffectTier.OFF || !MotionController.hapticsEnabled.value || !isSystemHapticsEnabled) return
        try {
            val v = vibrator ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(14L, 70))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(14L)
            }
        } catch (_: Throwable) {}
    }

    fun correct(combo: Int = 1, tier: EffectTier = EffectTier.FULL) {
        if (tier == EffectTier.OFF || !MotionController.hapticsEnabled.value || !isSystemHapticsEnabled) return
        try {
            val v = vibrator ?: return
            val intensity = when {
                combo >= 5 -> 220
                combo >= 3 -> 175
                combo >= 2 -> 140
                else -> 100
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Double-tick pulse with escalating strength
                v.vibrate(
                    VibrationEffect.createWaveform(
                        longArrayOf(0L, 35L, 45L, 45L),
                        intArrayOf(0, intensity, 0, (intensity * 0.75f).toInt()),
                        -1
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(longArrayOf(0L, 35L, 45L, 45L), -1)
            }
        } catch (_: Throwable) {}
    }

    fun wrong(tier: EffectTier = EffectTier.FULL) {
        if (tier == EffectTier.OFF || !MotionController.hapticsEnabled.value || !isSystemHapticsEnabled) return
        try {
            val v = vibrator ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(110L, 190))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(110L)
            }
        } catch (_: Throwable) {}
    }

    fun fizzle(tier: EffectTier = EffectTier.FULL) {
        if (tier == EffectTier.OFF || !MotionController.hapticsEnabled.value || !isSystemHapticsEnabled) return
        try {
            val v = vibrator ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(
                    VibrationEffect.createWaveform(
                        longArrayOf(0L, 20L, 25L, 20L, 25L, 15L),
                        intArrayOf(0, 90, 0, 60, 0, 30),
                        -1
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(50L)
            }
        } catch (_: Throwable) {}
    }

    fun bossWarning(tier: EffectTier = EffectTier.FULL) {
        if (tier == EffectTier.OFF || !MotionController.hapticsEnabled.value || !isSystemHapticsEnabled) return
        try {
            val v = vibrator ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(45L, 160))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(45L)
            }
        } catch (_: Throwable) {}
    }

    fun timerWarning(tier: EffectTier = EffectTier.FULL) = bossWarning(tier)
    fun levelComplete(tier: EffectTier = EffectTier.FULL) = correct(5, tier)

    fun onDestroy() {}
}

// ──────────────────────────────────────────────────────────────────
// 🃏 QUESTION TRANSITION SPECS (Shared-Axis X + Fade)
// ──────────────────────────────────────────────────────────────────

object QuestionTransition {
    val enterNext: EnterTransition =
        slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(300, easing = FastOutSlowInEasing)
        ) + fadeIn(tween(200))

    val exitNext: ExitTransition =
        slideOutHorizontally(
            targetOffsetX = { -it / 3 },
            animationSpec = tween(260, easing = FastOutSlowInEasing)
        ) + fadeOut(tween(180))

    val enterPrev: EnterTransition =
        slideInHorizontally(
            initialOffsetX = { -it },
            animationSpec = tween(300, easing = FastOutSlowInEasing)
        ) + fadeIn(tween(200))

    val exitPrev: ExitTransition =
        slideOutHorizontally(
            targetOffsetX = { it / 3 },
            animationSpec = tween(260, easing = FastOutSlowInEasing)
        ) + fadeOut(tween(180))
}
