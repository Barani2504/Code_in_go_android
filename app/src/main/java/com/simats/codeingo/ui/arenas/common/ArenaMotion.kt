package com.simats.codeingo.ui.arenas.common

import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.EffectTier
import com.simats.codeingo.ui.theme.LocalEffectTier
import com.simats.codeingo.ui.theme.LocalReduceMotion
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// ══════════════════════════════════════════════════════════════════
// 🎮 ARENA PALETTE TOKENS (Non-Overlapping Per Arena)
// ══════════════════════════════════════════════════════════════════
object ArenaPalettes {
    // Arena 1: Cipher Sanctum — Ancient Papyrus & Amber Gold
    val Arena1Primary = Color(0xFFFFC800)
    val Arena1Accent = Color(0xFFD97706)
    val Arena1BackgroundDark = Color(0xFF181203)
    val Arena1CardDark = Color(0xFF261D07)

    // Arena 2: Block Cipher Forge — Industrial Magma & Crimson Red
    val Arena2Primary = Color(0xFFF02D4B)
    val Arena2Accent = Color(0xFFFF6D00)
    val Arena2BackgroundDark = Color(0xFF1E0508)
    val Arena2CardDark = Color(0xFF300B10)

    // Arena 3: Key Exchange Citadel — Celestial Sapphire & Royal Gold
    val Arena3Primary = Color(0xFF3B82F6) // Celestial Sapphire
    val Arena3Accent = Color(0xFFF59E0B)  // Royal Gold
    val Arena3BackgroundDark = Color(0xFF071228)
    val Arena3CardDark = Color(0xFF0F1E3D)

    // Arena 4: Integrity Foundry — Neon Amethyst & Deep Violet
    val Arena4Primary = Color(0xFFB946FA)
    val Arena4Accent = Color(0xFF9333EA)
    val Arena4BackgroundDark = Color(0xFF140624)
    val Arena4CardDark = Color(0xFF220C3A)

    // Arena 5: Cyber Bastion — Matrix Cyber Emerald & Terminal Cyan
    val Arena5Primary = Color(0xFF10B981)
    val Arena5Accent = Color(0xFF06B6D4)
    val Arena5BackgroundDark = Color(0xFF041812)
    val Arena5CardDark = Color(0xFF092920)
}

// ══════════════════════════════════════════════════════════════════
// 📳 STANDARDIZED ARENA HAPTICS
// ══════════════════════════════════════════════════════════════════
object ArenaHaptics {
    fun tap(view: View, reduceMotion: Boolean = false) {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
    }

    fun success(view: View, reduceMotion: Boolean = false) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
        } else {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }
    }

    fun criticalStrike(view: View, reduceMotion: Boolean = false) {
        if (!reduceMotion && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.performHapticFeedback(HapticFeedbackConstants.GESTURE_END)
        } else {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        }
    }

    fun error(view: View, reduceMotion: Boolean = false) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.performHapticFeedback(HapticFeedbackConstants.REJECT)
        } else {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// 💥 HIT-STOP FRAME FREEZE COORDINATOR
// ══════════════════════════════════════════════════════════════════
@Stable
class ArenaHitStopState {
    var isHitStopActive by mutableStateOf(false)
        private set

    suspend fun triggerHitStop(durationMs: Long = 70L, reduceMotion: Boolean = false) {
        if (reduceMotion) return
        isHitStopActive = true
        delay(durationMs)
        isHitStopActive = false
    }
}

@Composable
fun rememberHitStopState(): ArenaHitStopState = remember { ArenaHitStopState() }

// ══════════════════════════════════════════════════════════════════
// 📳 DIRECTIONAL SCREEN SHAKE (Spring Decay, Reduce-Motion Safe)
// ══════════════════════════════════════════════════════════════════
@Stable
class ArenaShakeController {
    var triggerCount by mutableStateOf(0)
        private set

    fun shake() {
        triggerCount++
    }
}

@Composable
fun rememberArenaShakeController(): ArenaShakeController = remember { ArenaShakeController() }

fun Modifier.arenaShake(
    controller: ArenaShakeController,
    intensity: Float = 10f
): Modifier = composed {
    val tier = LocalEffectTier.current
    val reduceMotion = LocalReduceMotion.current || tier != EffectTier.FULL
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }

    LaunchedEffect(controller.triggerCount) {
        if (controller.triggerCount == 0 || reduceMotion) return@LaunchedEffect
        // 4-stage damped harmonic oscillation
        val steps = listOf(
            Offset(-intensity, intensity * 0.5f),
            Offset(intensity * 0.8f, -intensity * 0.4f),
            Offset(-intensity * 0.4f, intensity * 0.2f),
            Offset(0f, 0f)
        )
        for (step in steps) {
            offsetX.animateTo(step.x, tween(30, easing = LinearEasing))
            offsetY.animateTo(step.y, tween(30, easing = LinearEasing))
        }
    }

    this.graphicsLayer {
        translationX = offsetX.value
        translationY = offsetY.value
    }
}

// ══════════════════════════════════════════════════════════════════
// 🌊 SHOCKWAVE EXPANDING RING CANVAS (Fading Ripple)
// ══════════════════════════════════════════════════════════════════
@Composable
fun ArenaShockwaveEffect(
    trigger: Any?,
    color: Color = ArenaPalettes.Arena3Accent,
    maxRadiusDp: Dp = 120.dp,
    modifier: Modifier = Modifier
) {
    val reduceMotion = LocalReduceMotion.current
    if (reduceMotion || trigger == null) return

    val progress = remember { Animatable(0f) }

    LaunchedEffect(trigger) {
        progress.snapTo(0f)
        progress.animateTo(1f, tween(380, easing = FastOutSlowInEasing))
    }

    if (progress.value > 0f && progress.value < 1f) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val maxRadiusPx = maxRadiusDp.toPx()
            val currentRadius = progress.value * maxRadiusPx
            val alpha = (1f - progress.value).coerceIn(0f, 1f)
            drawCircle(
                color = color.copy(alpha = alpha * 0.7f),
                radius = currentRadius,
                style = Stroke(width = (4f * (1f - progress.value)).coerceAtLeast(1f))
            )
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// ✨ CELEBRATORY SPARK & BURST PARTICLES
// ══════════════════════════════════════════════════════════════════
private data class ArenaParticle(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val color: Color,
    val radius: Float
)

@Composable
fun ArenaParticleBurst(
    trigger: Any?,
    colors: List<Color> = listOf(ArenaPalettes.Arena3Primary, ArenaPalettes.Arena3Accent, Color.White),
    particleCount: Int = 24,
    modifier: Modifier = Modifier
) {
    val tier = LocalEffectTier.current
    val reduceMotion = LocalReduceMotion.current || tier != EffectTier.FULL
    if (reduceMotion || trigger == null) return

    val particles = remember { mutableStateListOf<ArenaParticle>() }
    var animProgress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(trigger) {
        particles.clear()
        val rng = Random(System.currentTimeMillis())
        repeat(particleCount) {
            val angle = rng.nextDouble(0.0, 2.0 * Math.PI)
            val speed = rng.nextFloat() * 180f + 60f
            particles.add(
                ArenaParticle(
                    x = 0f,
                    y = 0f,
                    vx = (cos(angle) * speed).toFloat(),
                    vy = (sin(angle) * speed).toFloat(),
                    color = colors[rng.nextInt(colors.size)],
                    radius = rng.nextFloat() * 3f + 2f
                )
            )
        }
        val anim = Animatable(0f)
        anim.animateTo(1f, tween(450, easing = FastOutSlowInEasing)) {
            animProgress = value
        }
        particles.clear()
    }

    if (particles.isNotEmpty() && animProgress < 1f) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val center = this.center
            val alpha = (1f - animProgress).coerceIn(0f, 1f)
            particles.forEach { p ->
                val px = center.x + p.vx * animProgress
                val py = center.y + p.vy * animProgress + (80f * animProgress * animProgress) // gravity
                drawCircle(
                    color = p.color.copy(alpha = alpha),
                    radius = p.radius * (1f - animProgress * 0.5f),
                    center = Offset(px, py)
                )
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// 👁️ COLORBLIND-SAFE SHAPE-BASED STATUS BADGES (Shape + Icon + Text)
// ══════════════════════════════════════════════════════════════════
enum class ArenaBadgeType(
    val shapeDescription: String,
    val icon: ImageVector,
    val defaultColor: Color
) {
    SUCCESS("Circle", Icons.Default.Check, Color(0xFF10B981)),
    WARNING("Triangle", Icons.Default.Warning, Color(0xFFF59E0B)),
    DANGER("Octagon", Icons.Default.Close, Color(0xFFEF4444)),
    ENCRYPTED("Shield-Lock", Icons.Default.Lock, Color(0xFF3B82F6)),
    INFO("Square", Icons.Default.Info, Color(0xFF06B6D4))
}

@Composable
fun ArenaStatusBadge(
    type: ArenaBadgeType,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(type.defaultColor.copy(alpha = 0.15f))
            .border(1.dp, type.defaultColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = type.icon,
            contentDescription = "${type.shapeDescription}: $label",
            tint = type.defaultColor,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = type.defaultColor
        )
    }
}

// ══════════════════════════════════════════════════════════════════
// 📐 TOUCH TARGET 48dp ENFORCEMENT & 360dp RESPONSIVENESS
// ══════════════════════════════════════════════════════════════════
fun Modifier.minTouchTarget48(): Modifier = this.defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
