package com.simats.codeingo.ui.motion

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.animation.AnimatedContent
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
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.components.motion.EffectBudgetManager
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.LocalReduceMotion
import com.simats.codeingo.ui.theme.MotionController
import com.simats.codeingo.ui.theme.PhoenixCrimson
import com.simats.codeingo.ui.theme.PhoenixEmber
import com.simats.codeingo.ui.theme.PhoenixGreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * ══════════════════════════════════════════════════════════════════
 * 🌋 ASHCRYPT EMBER GLASS MOTION & SENSORY ENGINE (Phase 3)
 *
 * Implements:
 * 1. Motion Specs: Snappy, Springy, Cinematic, Gentle (honoring LocalReduceMotion)
 * 2. Animated Number Ticker (for XP, gems, streak count-ups)
 * 3. Liquid Progress Bar with molten bubbling fill
 * 4. Dynamic Streak Flame (scales visual intensity with streak length)
 * 5. Node Ignite Canvas (celebratory unlock burst)
 * 6. SoundEffectsManager (SoundPool / ToneGenerator audio engine with quiet-hours)
 * 7. Frosted Ember Glass Card Modifier
 * ══════════════════════════════════════════════════════════════════
 */
object AshMotionEngine {

    @Composable
    fun snappy(): AnimationSpec<Float> {
        val reduce = LocalReduceMotion.current
        return if (reduce) tween(durationMillis = 150, easing = LinearEasing)
        else spring(dampingRatio = 0.72f, stiffness = 550f)
    }

    @Composable
    fun springy(): AnimationSpec<Float> {
        val reduce = LocalReduceMotion.current
        return if (reduce) tween(durationMillis = 200, easing = LinearEasing)
        else spring(dampingRatio = 0.60f, stiffness = 320f)
    }

    @Composable
    fun cinematic(): AnimationSpec<Float> {
        val reduce = LocalReduceMotion.current
        return if (reduce) tween(durationMillis = 250, easing = LinearEasing)
        else tween(durationMillis = 450, easing = FastOutSlowInEasing)
    }

    @Composable
    fun gentle(): AnimationSpec<Float> {
        val reduce = LocalReduceMotion.current
        return if (reduce) tween(durationMillis = 150, easing = LinearEasing)
        else tween(durationMillis = 280, easing = FastOutSlowInEasing)
    }
}

// ──────────────────────────────────────────────────────────────────
// 1. ANIMATED NUMBER TICKER (XP, GEMS, STREAK)
// ──────────────────────────────────────────────────────────────────

@Composable
fun NumberTickerText(
    value: Int,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
    prefix: String = "",
    suffix: String = ""
) {
    val reduce = LocalReduceMotion.current
    if (reduce) {
        Text(text = "$prefix$value$suffix", style = textStyle, modifier = modifier)
        return
    }

    val numString = value.toString()
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        if (prefix.isNotEmpty()) {
            Text(text = prefix, style = textStyle)
        }
        numString.forEach { digitChar ->
            AnimatedContent(
                targetState = digitChar,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInVertically { height -> height } + fadeIn()).togetherWith(
                            slideOutVertically { height -> -height } + fadeOut()
                        )
                    } else {
                        (slideInVertically { height -> -height } + fadeIn()).togetherWith(
                            slideOutVertically { height -> height } + fadeOut()
                        )
                    }
                },
                label = "digitScroll"
            ) { char ->
                Text(text = char.toString(), style = textStyle)
            }
        }
        if (suffix.isNotEmpty()) {
            Text(text = suffix, style = textStyle)
        }
    }
}

// ──────────────────────────────────────────────────────────────────
// 2. LIQUID PROGRESS BAR (Molten bubbling fill)
// ──────────────────────────────────────────────────────────────────

@Composable
fun LiquidProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 14.dp,
    barColor: Color = AmberGold
) {
    val clampedProgress = progress.coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = clampedProgress,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "liquidProgress"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "liquidShimmer")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveOffset"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(height / 2))
            .background(Color.White.copy(alpha = 0.08f))
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(height / 2))
    ) {
        // Active Fill
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .fillMaxHeight()
                .clip(RoundedCornerShape(height / 2))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            barColor,
                            PhoenixEmber,
                            PhoenixCrimson
                        )
                    )
                )
        ) {
            // Molten light-sweep overlay
            Canvas(modifier = Modifier.fillMaxSize()) {
                val startX = size.width * (waveOffset * 1.5f - 0.5f)
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.45f),
                            Color.Transparent
                        ),
                        startX = startX,
                        endX = startX + 60f
                    ),
                    start = Offset(startX, 0f),
                    end = Offset(startX + 60f, size.height),
                    strokeWidth = size.height
                )
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────
// 3. DYNAMIC STREAK FLAME CANVAS
// ──────────────────────────────────────────────────────────────────

@Composable
fun StreakFlameCanvas(
    streakDays: Int,
    modifier: Modifier = Modifier.size(36.dp)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "flamePulse")
    val flameFlicker by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flameFlicker"
    )

    val intensityColor = when {
        streakDays >= 30 -> PhoenixCrimson
        streakDays >= 7  -> PhoenixEmber
        else             -> AmberGold
    }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val scale = flameFlicker

        val path = Path().apply {
            moveTo(width * 0.5f, height * 0.1f * scale)
            cubicTo(
                width * 0.85f, height * 0.45f,
                width * 0.95f, height * 0.85f,
                width * 0.5f, height * 0.95f
            )
            cubicTo(
                width * 0.05f, height * 0.85f,
                width * 0.15f, height * 0.45f,
                width * 0.5f, height * 0.1f * scale
            )
            close()
        }

        drawPath(
            path = path,
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.9f),
                    intensityColor,
                    Color.Transparent
                ),
                center = Offset(width * 0.5f, height * 0.65f),
                radius = width * 0.65f * scale
            )
        )
    }
}

// ──────────────────────────────────────────────────────────────────
// 4. NODE IGNITE UNLOCK CANVAS
// ──────────────────────────────────────────────────────────────────

@Composable
fun NodeIgniteBurst(
    trigger: Int,
    modifier: Modifier = Modifier.size(72.dp),
    accentColor: Color = AmberGold
) {
    if (trigger <= 0) return

    val progress = remember(trigger) { Animatable(0f) }
    LaunchedEffect(trigger) {
        progress.snapTo(0f)
        progress.animateTo(1f, animationSpec = tween(600, easing = FastOutSlowInEasing))
    }

    if (progress.value < 1f) {
        Canvas(modifier = modifier) {
            val radius = size.minDimension * 0.5f * progress.value
            val alpha = (1f - progress.value).coerceIn(0f, 1f)

            drawCircle(
                color = accentColor.copy(alpha = alpha * 0.6f),
                radius = radius,
                style = Stroke(width = 3.dp.toPx() * (1f - progress.value))
            )
        }
    }
}

// ──────────────────────────────────────────────────────────────────
// 5. SOUND EFFECTS MANAGER (SoundPool / ToneGenerator Audio Engine)
// ──────────────────────────────────────────────────────────────────

class SoundEffectsManager private constructor(context: Context) {

    private var toneGenerator: ToneGenerator? = try {
        ToneGenerator(AudioManager.STREAM_MUSIC, 70)
    } catch (_: Throwable) {
        null
    }

    fun playTap() {
        if (!shouldPlaySound()) return
        toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 25)
    }

    fun playCorrect() {
        if (!shouldPlaySound()) return
        toneGenerator?.startTone(ToneGenerator.TONE_CDMA_CONFIRM, 90)
    }

    fun playWrong() {
        if (!shouldPlaySound()) return
        toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 120)
    }

    fun playCombo(multiplier: Int) {
        if (!shouldPlaySound()) return
        val duration = (60 + multiplier * 15).coerceAtMost(160)
        toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, duration)
    }

    fun playLevelUp() {
        if (!shouldPlaySound()) return
        GlobalScope.launch(Dispatchers.Default) {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_PIP, 60)
            delay(80)
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_CONFIRM, 120)
        }
    }

    fun playChest() {
        if (!shouldPlaySound()) return
        toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 150)
    }

    private fun shouldPlaySound(): Boolean {
        if (!MotionController.soundEnabled.value) return false
        if (EffectBudgetManager.isQuietTime()) return false
        return true
    }

    fun release() {
        toneGenerator?.release()
        toneGenerator = null
    }

    companion object {
        @Volatile
        private var instance: SoundEffectsManager? = null

        fun getInstance(context: Context): SoundEffectsManager {
            return instance ?: synchronized(this) {
                instance ?: SoundEffectsManager(context.applicationContext).also { instance = it }
            }
        }

        fun playTap() {
            instance?.playTap()
        }

        fun playCorrect() {
            instance?.playCorrect()
        }

        fun playWrong() {
            instance?.playWrong()
        }

        fun playLevelComplete() {
            instance?.playLevelUp()
        }
    }
}

// ──────────────────────────────────────────────────────────────────
// 6. FROSTED EMBER GLASS CARD COMPONENT
// ──────────────────────────────────────────────────────────────────

@Composable
fun EmberGlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    contentPadding: Dp = 16.dp,
    borderColor: Color = Color.White.copy(alpha = 0.12f),
    content: @Composable () -> Unit
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val surfaceColor = if (dynamicColors.isDark) {
        Color(0xFF13_1B_2E).copy(alpha = 0.78f)
    } else {
        Color.White.copy(alpha = 0.90f)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(surfaceColor)
            .border(1.2.dp, borderColor, RoundedCornerShape(cornerRadius))
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(cornerRadius), ambientColor = AmberGold.copy(alpha = 0.08f), spotColor = Color.Black.copy(alpha = 0.35f))
            .padding(contentPadding)
    ) {
        content()
    }
}

