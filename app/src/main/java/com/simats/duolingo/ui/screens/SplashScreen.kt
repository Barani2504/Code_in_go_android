package com.simats.duolingo.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.duolingo.R
import kotlinx.coroutines.delay
import kotlin.math.*

private data class RealisticSplashBubble(
    val baseAngle: Double,
    val maxDistance: Float,
    val size: Float,
    val color: Color,
    val swirlDirection: Float
)

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    var t by remember { mutableFloatStateOf(0f) }
    var finished by remember { mutableStateOf(false) }

    // Pre-generate 32 realistic 3D glossy bubble particles matching SplashScreenView.swift
    val bubbles = remember {
        val colors = listOf(
            Color(0xFF57CC02), // Duo Green
            Color(0xFF8CE614), // Lime Green
            Color(0xFFFFC700), // Duolingo Gold
            Color(0xFF2EE07A), // Mint Green
            Color.White,
            Color(0xFF38C2F5), // Sky Blue
            Color(0xFFFA8000)  // Vivid Orange
        )
        (0 until 32).map { i ->
            val angle = (i.toDouble() / 32.0) * 2.0 * Math.PI + (kotlin.random.Random.nextDouble(-0.12, 0.12))
            val dist = kotlin.random.Random.nextDouble(110.0, 215.0).toFloat()
            val sz = kotlin.random.Random.nextDouble(14.0, 36.0).toFloat()
            val col = colors[i % colors.size]
            val swirl = if (i % 2 == 0) 1.0f else -1.0f
            RealisticSplashBubble(angle, dist, sz, col, swirl)
        }
    }

    LaunchedEffect(Unit) {
        val startMs = System.currentTimeMillis()
        while (t < 3.8f) {
            delay(16L)
            t = (System.currentTimeMillis() - startMs) / 1000f
            if (t >= 3.5f && !finished) {
                finished = true
                onFinished()
            }
        }
    }

    // ── Continuous Frame Physics Calculations ─────────────────────────────────
    fun easeOut3(p: Float) = 1f - (1f - p).pow(3)
    fun clamp(value: Float, lo: Float, hi: Float) = value.coerceIn(lo, hi)
    fun phase(t: Float, start: Float, dur: Float) = clamp((t - start) / dur, 0f, 1f)

    // Phase 0: Fly In
    val p0 = phase(t, 0f, 0.45f)
    val easeFlyIn = easeOut3(p0)

    // Phase 0.5: Squeeze
    val pSq = phase(t, 0.45f, 0.17f)
    val squeezeAmount = sin(pSq * Math.PI.toFloat())

    // Phase 1: Blast
    val pBlast = phase(t, 0.62f, 0.58f)
    val easeBlast = 1f - (1f - pBlast).pow(2.8f)

    // Phase 2: Vortex
    val pVortex = phase(t, 1.25f, 0.70f)
    val easeVortex = pVortex.pow(2.2f)

    // Phase 3: Landing
    val pLanding = phase(t, 1.95f, 0.55f)
    val springBounce = exp(-5.5f * pLanding) * cos(14.0f * pLanding)

    // Hover Loop
    val hoverTime = if (t > 2.45f) t - 2.45f else 0f
    val hoverY = sin(hoverTime * 3.5f) * 7.0f
    val hoverRot = sin(hoverTime * 2.8f) * 3.5f

    // Mascot Y-offset
    val mascotYOffset: Float = when {
        t < 0.45f -> -380f * (1f - easeFlyIn)
        t < 0.62f -> 14f * squeezeAmount
        t < 0.82f -> -10f * (t - 0.62f) / 0.20f
        t < 1.95f -> 0f
        else      -> -14f * (1f - springBounce) + hoverY
    }

    val mascotScaleX: Float = when {
        t < 0.45f -> 0.02f + 1.03f * easeFlyIn
        t < 0.62f -> 1.05f + 0.28f * squeezeAmount
        t < 0.78f -> 1.33f - (t - 0.62f) * 4.0f
        t < 1.95f -> 0.05f
        else      -> 1.0f + 0.24f * springBounce
    }

    val mascotScaleY: Float = when {
        t < 0.45f -> 0.02f + 1.03f * easeFlyIn
        t < 0.62f -> 1.05f - 0.32f * squeezeAmount
        t < 0.78f -> 0.73f + (t - 0.62f) * 5.0f
        t < 1.95f -> 0.05f
        else      -> 1.0f - 0.24f * springBounce
    }

    val mascotOpacity: Float = when {
        t < 0.15f -> t / 0.15f
        t < 0.62f -> 1.0f
        t < 0.76f -> 1.0f - (t - 0.62f) / 0.14f
        t < 1.90f -> 0.0f
        t < 2.15f -> (t - 1.90f) / 0.25f
        else      -> 1.0f
    }

    val mascotRotation: Float = when {
        t < 0.45f -> -25.0f * (1f - easeFlyIn)
        t > 2.45f -> hoverRot
        else      -> 0.0f
    }

    // Dual Shockwaves
    val sw1Scale = if (t in 0.62f..1.15f) 0.2f + 2.2f * ((t - 0.62f) / 0.53f) else 0.1f
    val sw1Opacity = if (t in 0.62f..1.15f) sin(((t - 0.62f) / 0.53f) * Math.PI.toFloat()) * 0.65f else 0.0f

    val sw2Scale = if (t in 0.65f..1.25f) 0.1f + 2.6f * ((t - 0.65f) / 0.60f) else 0.1f
    val sw2Opacity = if (t in 0.65f..1.25f) sin(((t - 0.65f) / 0.60f) * Math.PI.toFloat()) * 0.45f else 0.0f

    // Central Energy Orb
    val orbProgress = clamp((t - 1.65f) / 0.35f, 0f, 1f)
    val orbScale = sin(orbProgress * Math.PI.toFloat()) * 1.8f
    val orbOpacity = sin(orbProgress * Math.PI.toFloat())

    // Logo & Progress
    val logoP = clamp((t - 1.95f) / 0.45f, 0f, 1f)
    val progressRatio = clamp(t / 3.3f, 0f, 1f)

    val pathStars = listOf("✨", "🌟", "💫", "⚡", "✨", "🌟")
    val starStepInt = (t / 0.35f).toInt()
    val starOpacity = if (t >= 1.95f) 1.0f else 0.0f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF5CD103),
                        Color(0xFF4DBD00)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 1000f)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // ── Animated Mascot & 3D Bubble Blast Stage ────────────────────────
            Box(
                modifier = Modifier
                    .size(240.dp),
                contentAlignment = Alignment.Center
            ) {
                // Ground soft shadow
                if (mascotOpacity > 0f) {
                    Box(
                        modifier = Modifier
                            .offset(y = 85.dp)
                            .size(width = 140.dp, height = 26.dp)
                            .scale(if (t < 0.45f) easeFlyIn else 1.0f)
                            .alpha(mascotOpacity * 0.28f)
                            .blur(6.dp)
                            .background(Color.Black, CircleShape)
                    )
                }

                // Dual Shockwave Rings on blast
                if (sw1Opacity > 0f) {
                    Box(
                        modifier = Modifier
                            .size(190.dp)
                            .scale(sw1Scale)
                            .blur(2.dp)
                            .border(width = 8.dp, color = Color.White.copy(alpha = sw1Opacity), shape = CircleShape)
                    )
                }
                if (sw2Opacity > 0f) {
                    Box(
                        modifier = Modifier
                            .size(190.dp)
                            .scale(sw2Scale)
                            .border(width = 4.dp, color = Color(0xFFC0FF4D).copy(alpha = sw2Opacity), shape = CircleShape)
                    )
                }

                // Radial Pulsing Glow Aura behind Duo
                if (t >= 1.95f) {
                    val auraPulse = 1.0f + sin(t * 3.0f) * 0.15f
                    Box(
                        modifier = Modifier
                            .size(230.dp)
                            .scale(auraPulse)
                            .alpha(0.45f)
                            .blur(18.dp)
                            .background(Color.White, CircleShape)
                    )
                }

                // 3D Glossy Bubbles Canvas (Blast & Vortex attraction)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f

                    bubbles.forEach { b ->
                        val (dist, angle, scale, opacity) = when {
                            t < 0.62f -> Quad(0f, b.baseAngle, 0f, 0f)
                            t < 1.25f -> {
                                val d = b.maxDistance * easeBlast
                                val s = sin(pBlast * Math.PI.toFloat() / 2f) * 1.15f
                                val op = min(1f, pBlast * 4f)
                                Quad(d, b.baseAngle, s, op)
                            }
                            t < 1.95f -> {
                                val d = b.maxDistance * (1f - easeVortex)
                                val swirlAngle = b.baseAngle + b.swirlDirection * easeVortex * Math.PI * 2.2
                                val s = 1.15f * (1f - easeVortex * 0.75f)
                                val op = max(0f, 1f - pVortex.pow(2.5f))
                                Quad(d, swirlAngle, s, op)
                            }
                            else -> Quad(0f, b.baseAngle, 0f, 0f)
                        }

                        if (opacity > 0f && scale > 0f) {
                            val bx = cx + cos(angle).toFloat() * dist
                            val by = cy + sin(angle).toFloat() * dist
                            val bSize = b.size * scale

                            // 3D Spherical Radial Gradient Body
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        b.color.copy(alpha = opacity * 0.98f),
                                        b.color.copy(alpha = opacity),
                                        b.color.copy(alpha = opacity * 0.7f)
                                    ),
                                    center = Offset(bx - bSize * 0.18f, by - bSize * 0.18f),
                                    radius = bSize * 0.75f
                                ),
                                radius = bSize / 2f,
                                center = Offset(bx, by)
                            )

                            // Specular Glass Arc Highlight (Top-left)
                            drawArc(
                                brush = Brush.verticalGradient(
                                    listOf(Color.White.copy(alpha = opacity * 0.88f), Color.Transparent),
                                    startY = by - bSize * 0.45f,
                                    endY = by
                                ),
                                startAngle = 180f,
                                sweepAngle = 90f,
                                useCenter = false,
                                topLeft = Offset(bx - bSize * 0.35f, by - bSize * 0.4f),
                                size = Size(bSize * 0.7f, bSize * 0.5f),
                                style = Stroke(width = max(1.5f, bSize * 0.12f))
                            )

                            // Secondary micro specular dot
                            drawCircle(
                                color = Color.White.copy(alpha = opacity * 0.75f),
                                radius = max(1f, bSize * 0.08f),
                                center = Offset(bx - bSize * 0.15f, by - bSize * 0.22f)
                            )

                            // Outer rim highlight ring
                            drawCircle(
                                brush = Brush.linearGradient(
                                    listOf(Color.White.copy(alpha = opacity * 0.6f), Color.White.copy(alpha = opacity * 0.1f)),
                                    start = Offset(bx - bSize / 2f, by - bSize / 2f),
                                    end = Offset(bx + bSize / 2f, by + bSize / 2f)
                                ),
                                radius = bSize / 2f,
                                center = Offset(bx, by),
                                style = Stroke(width = max(1f, bSize * 0.06f))
                            )
                        }
                    }
                }

                // Central Energy Fusion Orb
                if (orbOpacity > 0f) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .scale(orbScale)
                            .alpha(orbOpacity)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(Color.White, Color(0xFFC0FF4D), Color(0xFF58CC02))
                                ),
                                CircleShape
                            )
                            .blur(6.dp)
                    )
                }

                // Orbiting Celebration Sparkles
                if (starOpacity > 0f) {
                    Text(
                        text = pathStars[starStepInt % pathStars.size],
                        fontSize = 30.sp,
                        modifier = Modifier
                            .offset(x = (-80).dp, y = (-70).dp)
                            .alpha(if (starStepInt % 2 == 0) 0.95f else 0.25f)
                    )
                    Text(
                        text = pathStars[(starStepInt + 1) % pathStars.size],
                        fontSize = 26.sp,
                        modifier = Modifier
                            .offset(x = 78.dp, y = (-56).dp)
                            .alpha(if (starStepInt % 2 == 1) 0.95f else 0.25f)
                    )
                }

                // Cutout Duo Mascot
                Box(
                    modifier = Modifier
                        .size(205.dp)
                        .offset(y = mascotYOffset.dp)
                        .scale(scaleX = mascotScaleX, scaleY = mascotScaleY)
                        .alpha(mascotOpacity),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.duo_backpack),
                        contentDescription = "Code in Go Duo",
                        modifier = Modifier.size(205.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Animated Brand Title "duolingo" & Tagline ─────────────────────
            Column(
                modifier = Modifier
                    .alpha(logoP)
                    .offset(y = (25f * (1f - logoP)).dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "duolingo",
                    color = Color.White,
                    fontSize = 54.sp,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    text = "Learn a language for free. Forever.",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // ── Bottom Loading Progress Bar ────────────────────────────────────
            Box(
                modifier = Modifier
                    .padding(horizontal = 60.dp)
                    .padding(bottom = 50.dp)
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.3f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progressRatio)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White)
                )
            }
        }
    }
}

private data class Quad(
    val dist: Float,
    val angle: Double,
    val scale: Float,
    val opacity: Float
)
