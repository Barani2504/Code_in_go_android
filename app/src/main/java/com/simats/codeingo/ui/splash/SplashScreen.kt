package com.simats.codeingo.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.phoenix.SmoothFlyingPhoenixView
import com.simats.codeingo.ui.theme.AmberGold
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin

// ══════════════════════════════════════════════════════════════════
// 🌌 SplashScreen — Exact Parity with iOS SplashScreenView.swift
// 3D Glossy Ember Particles, Physics Timeline, Spiral Vortex,
// Kinetic Energy Squeeze, Dual Shockwaves & Elastic Landing
// ══════════════════════════════════════════════════════════════════

private data class RealisticSplashBubble(
    val baseAngle: Double,
    val maxDistance: Float,
    val size: Float,
    val color: Color,
    val swirlDirection: Double
)

@Composable
fun SplashScreen(
    onFinishedSplash: () -> Unit,
    modifier: Modifier = Modifier
) {
    var elapsedTimeSec by remember { mutableFloatStateOf(0f) }

    // Pre-generate 32 realistic 3D glossy fiery ember particles (Phoenix Palette)
    val bubbles = remember {
        val colors = listOf(
            AmberGold,
            Color(0xFFFF7A1A),
            Color(0xFFFFC733),
            Color(0xFFF2401F),
            Color(0xFFFF9E0D),
            Color.White,
            Color(0xFFFF590D)
        )
        (0 until 32).map { i ->
            val angle = (i.toDouble() / 32.0) * 2.0 * PI + (Math.random() - 0.5) * 0.24
            val distance = (110f + Math.random().toFloat() * 110f)
            val size = (14f + Math.random().toFloat() * 22f)
            val color = colors[i % colors.size]
            val swirlDir = if (i % 2 == 0) 1.0 else -1.0
            RealisticSplashBubble(
                baseAngle = angle,
                maxDistance = distance,
                size = size,
                color = color,
                swirlDirection = swirlDir
            )
        }
    }

    LaunchedEffect(Unit) {
        val startNanos = System.nanoTime()
        while (true) {
            withFrameNanos { frameTimeNanos ->
                elapsedTimeSec = (frameTimeNanos - startNanos) / 1_000_000_000f
            }
            if (elapsedTimeSec >= 3.5f) {
                onFinishedSplash()
                break
            }
        }
    }

    val t = elapsedTimeSec

    // Phase 0: Distance Fly-In [0.0 ... 0.45s]
    val p0 = min(1.0f, max(0.0f, t / 0.45f))
    val easeFlyIn = 1.0f - (1.0f - p0).pow(3)

    // Phase 0.5: Kinetic Energy Squeeze [0.45s ... 0.62s]
    val pSqueeze = min(1.0f, max(0.0f, (t - 0.45f) / 0.17f))
    val squeezeAmount = sin(pSqueeze * PI.toFloat())

    // Phase 1: Explosion Blast into 3D Embers [0.62s ... 1.25s]
    val pBlast = min(1.0f, max(0.0f, (t - 0.62f) / 0.58f))
    val easeBlast = 1.0f - (1.0f - pBlast).pow(2.8f)

    // Phase 2: Spiral Vortex Magnetic Fusion [1.25s ... 1.95s]
    val pVortex = min(1.0f, max(0.0f, (t - 1.25f) / 0.70f))
    val easeVortex = pVortex.pow(2.2f)

    // Phase 3: Fluid Mascot Re-Formation & Elastic Landing [1.95s ... 3.5s]
    val pLanding = min(1.0f, max(0.0f, (t - 1.95f) / 0.55f))
    val springBounce = (exp(-5.5f * pLanding) * cos(14.0f * pLanding))

    // Continuous Floating Hover Loop
    val hoverTime = max(0f, t - 2.45f)
    val hoverY = sin(hoverTime * 3.5f) * 7.0f
    val hoverRot = sin(hoverTime * 2.8f) * 3.5f

    // Mascot Character Transformations
    val mascotYOffset: Float = when {
        t < 0.45f -> -380.0f * (1.0f - easeFlyIn)
        t < 0.62f -> 14.0f * squeezeAmount
        t < 0.82f -> -10.0f * (t - 0.62f) / 0.20f
        t < 1.95f -> -5.0f * sin(((t - 0.82f) / 1.13f) * PI.toFloat())
        else -> -14.0f * (1.0f - springBounce) + hoverY
    }

    val mascotScaleX: Float = when {
        t < 0.45f -> 0.20f + 0.85f * easeFlyIn
        t < 0.62f -> 1.05f + 0.28f * squeezeAmount
        t < 0.78f -> 1.25f - (t - 0.62f) * 2.0f
        t < 1.95f -> 0.88f + 0.12f * sin(((t - 0.78f) / 1.17f) * PI.toFloat())
        else -> 1.0f + 0.20f * springBounce
    }

    val mascotScaleY: Float = when {
        t < 0.45f -> 0.20f + 0.85f * easeFlyIn
        t < 0.62f -> 1.05f - 0.32f * squeezeAmount
        t < 0.78f -> 0.85f + (t - 0.62f) * 2.0f
        t < 1.95f -> 0.88f + 0.12f * sin(((t - 0.78f) / 1.17f) * PI.toFloat())
        else -> 1.0f - 0.20f * springBounce
    }

    val mascotOpacity: Float = when {
        t < 0.15f -> t / 0.15f
        t < 0.62f -> 1.0f
        t < 1.25f -> max(0.65f, 1.0f - pBlast * 0.35f)
        t < 1.95f -> min(1.0f, 0.65f + easeVortex * 0.35f)
        else -> 1.0f
    }

    val mascotRotation: Float = when {
        t < 0.45f -> -25.0f * (1.0f - easeFlyIn)
        t > 2.45f -> hoverRot
        else -> 0.0f
    }

    // Shockwaves
    val sw1Scale = if (t in 0.62f..1.15f) 0.2f + 2.2f * ((t - 0.62f) / 0.53f) else 0.1f
    val sw1Opacity = if (t in 0.62f..1.15f) sin(((t - 0.62f) / 0.53f) * PI.toFloat()) * 0.65f else 0.0f

    val sw2Scale = if (t in 0.65f..1.25f) 0.1f + 2.6f * ((t - 0.65f) / 0.60f) else 0.1f
    val sw2Opacity = if (t in 0.65f..1.25f) sin(((t - 0.65f) / 0.60f) * PI.toFloat()) * 0.45f else 0.0f

    // Central Energy Orb
    val orbProgress = min(1.0f, max(0.0f, (t - 1.65f) / 0.35f))
    val orbScale = sin(orbProgress * PI.toFloat()) * 1.8f
    val orbOpacity = sin(orbProgress * PI.toFloat())

    // Logo & Progress
    val logoOpacity = min(1.0f, max(0.0f, t / 0.40f))
    val logoScale = 0.90f + 0.10f * min(1.0f, max(0.0f, (t - 1.95f) / 0.45f))
    val logoYOffset = if (t < 1.95f) 0.0f else 4.0f * sin(min(1.0f, (t - 1.95f) / 0.35f) * PI.toFloat())
    val progressRatio = min(1.0f, t / 3.3f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0F_0A_1A),
                        Color(0xFF3D_14_08),
                        Color(0xFF12_0D_21)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Stage: Mascot + Embers + Shockwaves
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(260.dp)
            ) {
                // Ground Soft Shadow
                Box(
                    modifier = Modifier
                        .size(width = 140.dp, height = 26.dp)
                        .offset(y = 85.dp)
                        .scale(if (t < 0.45f) 0.05f + 0.95f * easeFlyIn else 1.0f)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.45f))
                        .blur(8.dp)
                )

                // Canvas for Shockwaves & 3D Glossy Ember Particles
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val centerOffset = Offset(size.width / 2f, size.height / 2f)

                    // Shockwave 1
                    if (sw1Opacity > 0f) {
                        drawCircle(
                            color = AmberGold.copy(alpha = sw1Opacity),
                            radius = (95.dp.toPx()) * sw1Scale,
                            center = centerOffset,
                            style = Stroke(width = 6.dp.toPx())
                        )
                    }

                    // Shockwave 2
                    if (sw2Opacity > 0f) {
                        drawCircle(
                            color = Color(0xFFFF7A1A).copy(alpha = sw2Opacity),
                            radius = (95.dp.toPx()) * sw2Scale,
                            center = centerOffset,
                            style = Stroke(width = 4.dp.toPx())
                        )
                    }

                    // 32 3D Glossy Ember Particles
                    for (b in bubbles) {
                        val (dist, angle, scale, opacity) = when {
                            t < 0.62f -> listOf(0f, b.baseAngle.toFloat(), 0f, 0f)
                            t < 1.25f -> {
                                val d = b.maxDistance * easeBlast
                                val s = sin(pBlast * (PI.toFloat() / 2f)) * 1.15f
                                val op = min(1.0f, pBlast * 4f)
                                listOf(d, b.baseAngle.toFloat(), s, op)
                            }
                            t < 1.95f -> {
                                val d = b.maxDistance * (1.0f - easeVortex)
                                val swirlAngle = (b.baseAngle + b.swirlDirection * easeVortex * PI * 2.2).toFloat()
                                val s = 1.15f * (1.0f - easeVortex * 0.75f)
                                val op = max(0.0f, 1.0f - pVortex.pow(2.5f))
                                listOf(d, swirlAngle, s, op)
                            }
                            else -> listOf(0f, b.baseAngle.toFloat(), 0f, 0f)
                        }

                        if (opacity > 0f && scale > 0f) {
                            val px = centerOffset.x + cos(angle) * dist * density
                            val py = centerOffset.y + sin(angle) * dist * density
                            val radius = (b.size / 2f) * scale * density

                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        b.color.copy(alpha = opacity * 0.98f),
                                        b.color.copy(alpha = opacity * 0.65f)
                                    ),
                                    center = Offset(px - radius * 0.3f, py - radius * 0.3f),
                                    radius = radius * 1.2f
                                ),
                                radius = radius,
                                center = Offset(px, py)
                            )
                            // Specular Top-Left Highlight
                            drawCircle(
                                color = Color.White.copy(alpha = opacity * 0.85f),
                                radius = radius * 0.35f,
                                center = Offset(px - radius * 0.35f, py - radius * 0.35f)
                            )
                        }
                    }

                    // Central Energy Orb
                    if (orbOpacity > 0f) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color.White, AmberGold, Color(0xFFFF590D)),
                                center = centerOffset,
                                radius = 40.dp.toPx() * orbScale
                            ),
                            radius = 35.dp.toPx() * orbScale,
                            center = centerOffset,
                            alpha = orbOpacity
                        )
                    }
                }

                // Smooth Flying Phoenix Mascot Character
                Box(
                    modifier = Modifier
                        .size(205.dp)
                        .graphicsLayer {
                            scaleX = mascotScaleX
                            scaleY = mascotScaleY
                            translationY = mascotYOffset * density
                            rotationZ = mascotRotation
                            this.alpha = mascotOpacity
                        }
                ) {
                    SmoothFlyingPhoenixView(size = 205.dp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Animated Brand Title "Ashnode"
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsLayer {
                    scaleX = logoScale
                    scaleY = logoScale
                    translationY = logoYOffset * density
                    this.alpha = logoOpacity
                }
            ) {
                Text(
                    text = "Ashnode",
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Master Data Structures & Algorithms",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = AmberGold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Bottom Glowing Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 60.dp)
                    .height(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.12f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressRatio)
                        .height(10.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(
                                listOf(AmberGold, Color(0xFFFF8C1A))
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}
