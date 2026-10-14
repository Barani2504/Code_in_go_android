package com.simats.codeingo.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.simats.codeingo.ui.theme.EffectTier
import com.simats.codeingo.ui.theme.LocalEffectTier
import com.simats.codeingo.ui.theme.PhoenixMotion
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// ══════════════════════════════════════════════════════════════════
// 💊 DEVICE-AWARE NOTCH / DYNAMIC PILL HUD (Part C2)
// ══════════════════════════════════════════════════════════════════
// Behavior:
// - Hugs camera cutout when present, or floats below top status bar.
// - Morphs via spring physics into transient status badges:
//   +XP, Combo xN, Streak Up, Heart Lost, Level Complete, Timer Warning.
// - Boss Intro expands into a full-width "BOSS" banner and retracts.
// - Auto-collapses after ~1.5s (queued FIFO, never stacked).
// - Ambient Glow Ring around punch-hole (Green: correct, Red: wrong, Yellow: combo).
// - Guaranteed NEVER to overlap Phoenix pixels.
// ══════════════════════════════════════════════════════════════════

sealed class DynamicPillEvent {
    data class XP(val amount: Int) : DynamicPillEvent()
    data class Combo(val multiplier: Int) : DynamicPillEvent()
    data class Streak(val days: Int) : DynamicPillEvent()
    object HeartLost : DynamicPillEvent()
    data class LevelComplete(val xpEarned: Int = 0) : DynamicPillEvent()
    data class TimerWarning(val secondsRemaining: Int) : DynamicPillEvent()
    data class BossIntro(val bossTitle: String = "BOSS BATTLE") : DynamicPillEvent()
    data class Custom(val emoji: String, val title: String, val subtitle: String?, val color: Color) : DynamicPillEvent()
}

object DynamicPillController {
    private val eventChannel = Channel<DynamicPillEvent>(capacity = 16)

    private val _currentEvent = MutableStateFlow<DynamicPillEvent?>(null)
    val currentEvent: StateFlow<DynamicPillEvent?> = _currentEvent.asStateFlow()

    private val _ambientGlowColor = MutableStateFlow<Color?>(null)
    val ambientGlowColor: StateFlow<Color?> = _ambientGlowColor.asStateFlow()

    private val _edgeLightingColor = MutableStateFlow<Color?>(null)
    val edgeLightingColor: StateFlow<Color?> = _edgeLightingColor.asStateFlow()

    fun post(event: DynamicPillEvent) {
        postEvent(event)
    }

    fun postEvent(event: DynamicPillEvent) {
        eventChannel.trySend(event)
    }

    fun triggerAmbientGlow(color: Color) {
        _ambientGlowColor.value = color
    }

    fun triggerEdgeLighting(color: Color) {
        _edgeLightingColor.value = color
    }

    suspend fun processQueue() {
        for (event in eventChannel) {
            _currentEvent.value = event
            val duration = when (event) {
                is DynamicPillEvent.BossIntro -> 2800L
                is DynamicPillEvent.LevelComplete -> 2200L
                else -> 1600L
            }
            delay(duration)
            _currentEvent.value = null
            delay(250L) // smooth collapse buffer between queued items
        }
    }
}

@Composable
fun DynamicPillHUD(
    modifier: Modifier = Modifier,
    isPhoenixNearTop: Boolean = false
) {
    val tier = LocalEffectTier.current
    val currentEvent by DynamicPillController.currentEvent.collectAsState()
    val ambientGlow by DynamicPillController.ambientGlowColor.collectAsState()
    val density = LocalDensity.current

    // Detect device cutout insets
    val cutoutInsets = WindowInsets.displayCutout.asPaddingValues()
    val topCutoutPx = with(density) { cutoutInsets.calculateTopPadding() }
    val hasCutout = topCutoutPx > 8.dp

    // Start background queue consumer
    LaunchedEffect(Unit) {
        DynamicPillController.processQueue()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .zIndex(99f), // above screen content, beneath Phoenix if Phoenix is near top
        contentAlignment = Alignment.TopCenter
    ) {
        // Cutout Ambient Glow Ring / Top Bar Glow
        if (ambientGlow != null && tier != EffectTier.OFF) {
            CutoutAmbientGlowRing(
                color = ambientGlow!!,
                hasCutout = hasCutout,
                onDismiss = { DynamicPillController.triggerAmbientGlow(Color.Transparent) }
            )
        }

        // Screen Edge Lighting Sweep (Part C2)
        val edgeLight by DynamicPillController.edgeLightingColor.collectAsState()
        if (edgeLight != null && tier != EffectTier.OFF) {
            ScreenEdgeLightingSweep(
                color = edgeLight!!,
                onFinished = { DynamicPillController.triggerEdgeLighting(Color.Transparent) }
            )
        }

        // Dynamic Pill Morphing Container
        if (currentEvent != null && !isPhoenixNearTop && tier != EffectTier.OFF) {
            DynamicPillBody(
                event = currentEvent!!,
                hasCutout = hasCutout,
                cutoutTopPadding = if (hasCutout) topCutoutPx else 10.dp
            )
        }
    }
}

@Composable
private fun DynamicPillBody(
    event: DynamicPillEvent,
    hasCutout: Boolean,
    cutoutTopPadding: Dp
) {
    val isBoss = event is DynamicPillEvent.BossIntro

    val targetWidth = if (isBoss) 340.dp else 210.dp
    val targetHeight = if (isBoss) 54.dp else 42.dp
    val cornerRadius = if (isBoss) 16.dp else 24.dp

    val animatedWidth by animateDpAsState(
        targetValue = targetWidth,
        animationSpec = PhoenixMotion.SnappySpringDp,
        label = "pillWidth"
    )
    val animatedHeight by animateDpAsState(
        targetValue = targetHeight,
        animationSpec = PhoenixMotion.SnappySpringDp,
        label = "pillHeight"
    )

    val topOffset = if (hasCutout) (cutoutTopPadding * 0.4f) else (cutoutTopPadding + 4.dp)

    Box(
        modifier = Modifier
            .offset(y = topOffset)
            .size(width = animatedWidth, height = animatedHeight)
            .shadow(18.dp, RoundedCornerShape(cornerRadius), spotColor = Color.Black.copy(alpha = 0.6f))
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                Brush.horizontalGradient(
                    when (event) {
                        is DynamicPillEvent.BossIntro -> listOf(Color(0xFF8B0000), Color(0xFF2B0004))
                        is DynamicPillEvent.Combo -> listOf(Color(0xFFFF8F00), Color(0xFF1E1000))
                        is DynamicPillEvent.XP -> listOf(Color(0xFF00C853), Color(0xFF002E12))
                        is DynamicPillEvent.Streak -> listOf(Color(0xFFFF6D00), Color(0xFF3E1200))
                        is DynamicPillEvent.HeartLost -> listOf(Color(0xFFD50000), Color(0xFF330000))
                        is DynamicPillEvent.LevelComplete -> listOf(Color(0xFFAA00FF), Color(0xFF1C0033))
                        is DynamicPillEvent.TimerWarning -> listOf(Color(0xFFFF3D00), Color(0xFF280A00))
                        is DynamicPillEvent.Custom -> listOf(event.color, Color.Black)
                    }
                )
            )
            .border(
                1.4.dp,
                Brush.horizontalGradient(
                    listOf(Color.White.copy(alpha = 0.65f), Color.White.copy(alpha = 0.15f))
                ),
                RoundedCornerShape(cornerRadius)
            )
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            when (event) {
                is DynamicPillEvent.BossIntro -> {
                    Text("⚠️", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        event.bossTitle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.2.sp
                    )
                }
                is DynamicPillEvent.Combo -> {
                    Text("🔥", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "COMBO x${event.multiplier}!",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
                is DynamicPillEvent.XP -> {
                    Text("⚡", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "+${event.amount} XP",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
                is DynamicPillEvent.Streak -> {
                    Text("🔥", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "${event.days} DAY STREAK!",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
                is DynamicPillEvent.HeartLost -> {
                    Text("💔", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "HEART LOST",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFF8A80)
                    )
                }
                is DynamicPillEvent.LevelComplete -> {
                    Text("🏆", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "STAGE CLEAR!",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
                is DynamicPillEvent.TimerWarning -> {
                    Text("⏳", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "${event.secondsRemaining}s LEFT",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFFD180)
                    )
                }
                is DynamicPillEvent.Custom -> {
                    Text(event.emoji, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        event.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun CutoutAmbientGlowRing(
    color: Color,
    hasCutout: Boolean,
    onDismiss: () -> Unit
) {
    val alphaAnim = remember { Animatable(0f) }

    LaunchedEffect(color) {
        if (color == Color.Transparent) return@LaunchedEffect
        alphaAnim.snapTo(0.9f)
        alphaAnim.animateTo(
            targetValue = 0f,
            animationSpec = tween(900, easing = FastOutSlowInEasing)
        )
        onDismiss()
    }

    if (alphaAnim.value <= 0.02f) return

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .graphicsLayer { this.alpha = alphaAnim.value }
    ) {
        val centerX = size.width / 2f
        val centerY = if (hasCutout) 24.dp.toPx() else 10.dp.toPx()

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(color.copy(alpha = 0.8f), color.copy(alpha = 0.2f), Color.Transparent),
                center = Offset(centerX, centerY),
                radius = 48.dp.toPx()
            ),
            radius = 48.dp.toPx(),
            center = Offset(centerX, centerY)
        )
    }
}

@Composable
private fun ScreenEdgeLightingSweep(
    color: Color,
    onFinished: () -> Unit
) {
    val alphaAnim = remember { Animatable(0f) }

    LaunchedEffect(color) {
        if (color == Color.Transparent) return@LaunchedEffect
        alphaAnim.snapTo(0.85f)
        alphaAnim.animateTo(
            targetValue = 0f,
            animationSpec = tween(650, easing = FastOutSlowInEasing)
        )
        onFinished()
    }

    if (alphaAnim.value <= 0.02f) return

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { this.alpha = alphaAnim.value }
    ) {
        val stroke = 6.dp.toPx()
        drawRect(
            color = color,
            style = Stroke(width = stroke)
        )
    }
}
