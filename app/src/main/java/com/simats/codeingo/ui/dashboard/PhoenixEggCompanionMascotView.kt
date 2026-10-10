package com.simats.codeingo.ui.dashboard

import android.os.Build
import android.provider.Settings
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.simats.codeingo.R
import com.simats.codeingo.domain.GameManager

/**
 * Phoenix Stage configuration matching manifest.json glow_rgb per stage (1..5).
 */
data class PhoenixStageConfig(
    val stage: Int,
    val title: String,
    val animResId: Int,
    val staticResId: Int,
    val glowColor: Color
)

/**
 * Returns stage config derived from chapter index (1..5).
 */
fun getPhoenixStageConfig(stage: Int): PhoenixStageConfig {
    val clamped = stage.coerceIn(1, 5)
    return when (clamped) {
        1 -> PhoenixStageConfig(
            stage = 1,
            title = "Ember Hatchling",
            animResId = R.drawable.phoenix_anim_stage_1,
            staticResId = R.drawable.phoenix_stage_1_static,
            glowColor = Color(255, 120, 30) // manifest: [255, 120, 30]
        )
        2 -> PhoenixStageConfig(
            stage = 2,
            title = "Blaze Fledgling",
            animResId = R.drawable.phoenix_anim_stage_2,
            staticResId = R.drawable.phoenix_stage_2_static,
            glowColor = Color(255, 100, 20) // manifest: [255, 100, 20]
        )
        3 -> PhoenixStageConfig(
            stage = 3,
            title = "Inferno Phoenix",
            animResId = R.drawable.phoenix_anim_stage_3,
            staticResId = R.drawable.phoenix_stage_3_static,
            glowColor = Color(255, 170, 40) // manifest: [255, 170, 40]
        )
        4 -> PhoenixStageConfig(
            stage = 4,
            title = "Solar Phoenix",
            animResId = R.drawable.phoenix_anim_stage_4,
            staticResId = R.drawable.phoenix_stage_4_static,
            glowColor = Color(255, 225, 110) // manifest: [255, 225, 110]
        )
        else -> PhoenixStageConfig(
            stage = 5,
            title = "Celestial Phoenix",
            animResId = R.drawable.phoenix_anim_stage_5,
            staticResId = R.drawable.phoenix_stage_5_static,
            glowColor = Color(120, 200, 255) // manifest: [120, 200, 255]
        )
    }
}

/**
 * Single function mapping stage -> drawable resource ID (animated or static).
 */
fun getPhoenixStageDrawable(stage: Int, staticOnly: Boolean = false): Int {
    val cfg = getPhoenixStageConfig(stage)
    return if (staticOnly) cfg.staticResId else cfg.animResId
}

/**
 * PhoenixEggCompanionMascotView:
 * Renders the living Animated Phoenix Companion next to the active level node on the Learn screen.
 * Replaces the egg with exact position, size, anchor, and z-order.
 * Features:
 * - Animated stage WebP image (or _static.png when Reduced Motion is enabled).
 * - Canvas glow drawn BEHIND the Phoenix using stage-specific glow_rgb.
 * - Gentle floating translation and breathing scale applied through graphicsLayer.
 * - Pauses animation when screen lifecycle is not resumed.
 */
@Composable
fun PhoenixEggCompanionMascotView(
    levelNumber: Int = 1,
    isBoss: Boolean = false,
    isFlying: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Lifecycle visibility check: pause animation when screen is not visible/resumed
    var isScreenVisible by remember { mutableStateOf(true) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            isScreenVisible = event.targetState.isAtLeast(Lifecycle.State.RESUMED)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Reduced motion: Check system "Remove animations" / animator duration setting
    val isReducedMotion = remember(context) {
        try {
            val animScale = Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1.0f
            )
            animScale == 0f
        } catch (_: Exception) {
            false
        }
    }

    // Derive stage from existing chapter/evolution state (clamped to 1..5)
    val maxUnlockedChapter by GameManager.instance.maxUnlockedChapter.collectAsState()
    val chapterFromLevel = ((levelNumber - 1) / 5) + 1
    val stage = maxOf(maxUnlockedChapter, chapterFromLevel).coerceIn(1, 5)
    val config = remember(stage) { getPhoenixStageConfig(stage) }

    // Floating & breathing motion (disabled if reduced motion or screen is not visible)
    val infiniteTransition = rememberInfiniteTransition(label = "PhoenixCompanionFloat")

    val floatY by infiniteTransition.animateFloat(
        initialValue = if (!isReducedMotion && isScreenVisible) -4f else 0f,
        targetValue = if (!isReducedMotion && isScreenVisible) 4f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PhoenixFloatY"
    )

    val breathScale by infiniteTransition.animateFloat(
        initialValue = if (!isReducedMotion && isScreenVisible) 0.98f else 1.0f,
        targetValue = if (!isReducedMotion && isScreenVisible) 1.03f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PhoenixBreathScale"
    )

    val glowPulse by infiniteTransition.animateFloat(
        initialValue = if (!isReducedMotion && isScreenVisible) 0.45f else 0.55f,
        targetValue = if (!isReducedMotion && isScreenVisible) 0.85f else 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PhoenixGlowPulse"
    )

    val displayDrawable = if (isReducedMotion) config.staticResId else config.animResId

    // Container matching egg size (72.dp default) & centered anchor
    Box(
        modifier = modifier.size(72.dp),
        contentAlignment = Alignment.Center
    ) {
        // 1. BEHIND Canvas: Radial Glow drawn using manifest glow_rgb
        Canvas(
            modifier = Modifier
                .size(92.dp)
                .graphicsLayer {
                    translationY = floatY
                    scaleX = breathScale
                    scaleY = breathScale
                }
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val glowRadius = size.width * 0.48f

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        config.glowColor.copy(alpha = glowPulse * 0.55f),
                        config.glowColor.copy(alpha = glowPulse * 0.20f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = glowRadius
                ),
                radius = glowRadius,
                center = center
            )
        }

        // 2. Animated Phoenix Image loaded with Coil at display size
        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(context)
                .data(displayDrawable)
                .crossfade(true)
                .build()
        )

        Image(
            painter = painter,
            contentDescription = "${config.title} Companion Mascot",
            modifier = Modifier
                .size(72.dp)
                .graphicsLayer {
                    translationY = floatY
                    scaleX = breathScale
                    scaleY = breathScale
                }
        )
    }
}
