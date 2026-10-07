package com.simats.codeingo.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.R
import com.simats.codeingo.ui.components.ProgressBarAnimated
import com.simats.codeingo.ui.theme.AmberGold
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

// ══════════════════════════════════════════════════════════════════
// 🌌 SplashScreen — Phoenix Bird Flight Theme with Soaring Dynamics
// ══════════════════════════════════════════════════════════════════

@Composable
fun SplashScreen(
    onFinishedSplash: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = remember { Animatable(0f) }
    val mascotScale = remember { Animatable(0.2f) }
    val mascotAlpha = remember { Animatable(0f) }
    val entranceOffsetY = remember { Animatable(100f) }
    val entranceBanking = remember { Animatable(-12f) }
    val titleAlpha = remember { Animatable(0f) }
    val titleOffsetY = remember { Animatable(30f) }
    val bubbleExplosion = remember { Animatable(0f) }

    // 🕊️ Continuous Flight Dynamics Transitions
    val infiniteTransition = rememberInfiniteTransition(label = "PhoenixFlight")

    // 1. Soaring altitude bobbing (graceful vertical gliding lift)
    val flightAltitude by infiniteTransition.animateFloat(
        initialValue = -13f,
        targetValue = 13f,
        animationSpec = infiniteRepeatable(
            animation = tween(1900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flightAltitude"
    )

    // 2. Aerodynamic banking roll / tilt during flight
    val flightBanking by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flightBanking"
    )

    // 3. Wing flap aerodynamic breathing (wings spread & lift)
    val wingFlapScaleY by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wingFlapScaleY"
    )
    val wingFlapScaleX by infiniteTransition.animateFloat(
        initialValue = 1.04f,
        targetValue = 0.96f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wingFlapScaleX"
    )

    // 4. Downward air slipstream & particle progression
    val flightStreamProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "flightStreamProgress"
    )

    // 5. Thermal flight shockwave pulse
    val thermalPulseScale by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "thermalPulseScale"
    )
    val thermalPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "thermalPulseAlpha"
    )

    LaunchedEffect(Unit) {
        // Mascot swoops up into flight
        launch {
            mascotAlpha.animateTo(1f, tween(400))
        }
        launch {
            mascotScale.animateTo(1.15f, tween(500, easing = FastOutSlowInEasing))
            mascotScale.animateTo(1.0f, tween(350, easing = FastOutSlowInEasing))
        }
        launch {
            entranceOffsetY.animateTo(0f, tween(800, easing = FastOutSlowInEasing))
        }
        launch {
            entranceBanking.animateTo(0f, tween(900, easing = FastOutSlowInEasing))
        }
        // Fiery ember blast
        launch {
            delay(400)
            bubbleExplosion.animateTo(1f, tween(1200, easing = FastOutSlowInEasing))
        }
        // Title entrance
        launch {
            delay(500)
            titleAlpha.animateTo(1f, tween(600))
        }
        launch {
            delay(500)
            titleOffsetY.animateTo(0f, tween(600, easing = FastOutSlowInEasing))
        }
        // Progress bar fills over 2.6 seconds
        launch {
            progress.animateTo(1f, tween(2600, easing = LinearEasing))
            delay(300)
            onFinishedSplash()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF080E1A),
                        Color(0xFF0B111F),
                        Color(0xFF111222)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // 🌌 Background Flight Atmosphere: Airflow Streaks, Embers & Expanding Shockwaves
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f - 40.dp.toPx())
            val explosion = bubbleExplosion.value
            val streamT = flightStreamProgress

            // 1. Expanding Thermal Flight Wave
            val waveRadius = 140.dp.toPx() * thermalPulseScale
            drawCircle(
                color = AmberGold.copy(alpha = (thermalPulseAlpha * 0.4f).coerceIn(0f, 1f)),
                radius = waveRadius,
                center = center,
                style = Stroke(width = 2.5.dp.toPx())
            )

            // 2. High-Speed Aerodynamic Wind Slipstreams rushing downwards
            for (i in 0 until 16) {
                val xRel = ((i * 39) % 280 - 140).dp.toPx()
                val length = (30 + (i * 11) % 45).dp.toPx()
                val speed = 0.8f + (i % 4) * 0.25f
                val yRelProgress = (streamT * speed + (i * 0.13f)) % 1f
                val startY = center.y - 120.dp.toPx() + (yRelProgress * 320.dp.toPx())
                val endY = startY + length
                val alpha = (sin(yRelProgress * Math.PI.toFloat()) * 0.42f).coerceIn(0f, 1f)

                drawLine(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            AmberGold.copy(alpha = alpha),
                            Color.White.copy(alpha = alpha * 0.9f),
                            Color.Transparent
                        ),
                        startY = startY,
                        endY = endY
                    ),
                    start = Offset(center.x + xRel, startY),
                    end = Offset(center.x + xRel, endY),
                    strokeWidth = (1.5f + (i % 3) * 0.8f).dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // 3. Fiery Plumage Sparks & Flight Wake Embers
            for (i in 0 until 24) {
                val phase = (streamT + (i * 0.042f)) % 1f
                val driftAngle = ((i * 53) % 70 - 35) * (Math.PI / 180.0)
                val dist = phase * 150.dp.toPx()
                val px = center.x + (sin(driftAngle) * dist).toFloat() + ((i % 5 - 2) * 14.dp.toPx())
                val py = center.y + 30.dp.toPx() + (cos(driftAngle) * dist).toFloat()
                val pAlpha = ((1f - phase) * 0.8f).coerceIn(0f, 1f)
                val pSize = (4f + (i % 4) * 2.5f).dp.toPx() * (1f - phase * 0.4f)

                val color = when (i % 5) {
                    0 -> AmberGold
                    1 -> Color(0xFFFF6D00)
                    2 -> Color(0xFFFFAB40)
                    3 -> Color(0xFFFF3D00)
                    else -> Color.White
                }

                drawCircle(
                    color = color.copy(alpha = pAlpha),
                    radius = pSize,
                    center = Offset(px, py)
                )
            }

            // 4. Initial Ember Burst Animation
            if (explosion > 0.05f) {
                val emberColors = listOf(
                    AmberGold,
                    Color(0xFFFF7A1A),
                    Color(0xFFFFC733),
                    Color(0xFFFF401F),
                    Color(0xFFFF9E0D),
                    Color.White,
                    Color(0xFFFF590D)
                )
                for (i in 0 until 32) {
                    val angle = (i.toFloat() / 32f) * 2f * Math.PI.toFloat()
                    val radius = (90.dp.toPx() + (i % 6) * 22.dp.toPx()) * explosion
                    val x = center.x + cos(angle.toDouble()).toFloat() * radius
                    val y = center.y + sin(angle.toDouble()).toFloat() * radius
                    val bubbleSize = (8.dp.toPx() + (i % 4) * 4.dp.toPx()) * (1f - explosion * 0.25f)
                    val alpha = (1f - explosion * 0.65f).coerceIn(0f, 1f)

                    drawCircle(
                        color = emberColors[i % emberColors.size].copy(alpha = alpha * 0.85f),
                        radius = bubbleSize,
                        center = Offset(x, y)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // 🦅 Center Phoenix Flying Mascot
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(210.dp)
                    .graphicsLayer {
                        val currentY = entranceOffsetY.value + flightAltitude
                        translationY = currentY.dp.toPx()
                        scaleX = mascotScale.value * wingFlapScaleX
                        scaleY = mascotScale.value * wingFlapScaleY
                        rotationZ = entranceBanking.value + flightBanking
                        alpha = mascotAlpha.value
                    }
            ) {
                // Outer Pulsing Celestial Flight Halo
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    AmberGold.copy(alpha = 0.42f),
                                    Color(0xFFFF6D00).copy(alpha = 0.22f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Inner Radiant Core
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFFD54F).copy(alpha = 0.35f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Phoenix Bird Image
                Image(
                    painter = painterResource(id = R.drawable.phoenix),
                    contentDescription = "Code in Go Phoenix Mascot",
                    modifier = Modifier.size(165.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Brand Titles
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .alpha(titleAlpha.value)
                    .offset(y = titleOffsetY.value.dp)
            ) {
                Text(
                    text = "Code in Go",
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif,
                    color = LocalDynamicThemeColors.current.textPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Master Data Structures & Algorithms",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = AmberGold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Bottom Loading Progress
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp, vertical = 48.dp)
            ) {
                ProgressBarAnimated(
                    progress = progress.value,
                    height = 12.dp,
                    barColor = AmberGold,
                    trackcolor = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.15f)
                )
            }
        }
    }
}

