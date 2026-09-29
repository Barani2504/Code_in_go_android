package com.simats.duolingo.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.*
import com.simats.duolingo.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.*

/**
 * Animated splash screen – mirrors SplashScreenView.swift physics exactly:
 *  Phase 0  → 0.45 s : mascot fly-in from top
 *  Phase 0.5 → 0.62 s : kinetic squeeze
 *  Phase 1  → 1.25 s : bubble blast explosion
 *  Phase 2  → 1.95 s : spiral vortex fusion
 *  Phase 3  → 3.5 s  : landing + hover + logo reveal → call onFinished
 */
@Composable
fun SplashScreen(onFinished: () -> Unit) {
    // elapsed time in seconds (updated at 60 fps)
    var t by remember { mutableFloatStateOf(0f) }
    var finished by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val startMs = System.currentTimeMillis()
        while (t < 4.0f) {
            delay(16L)
            t = (System.currentTimeMillis() - startMs) / 1000f
            if (t >= 3.5f && !finished) {
                finished = true
                onFinished()
            }
        }
    }

    // ── physics helpers ────────────────────────────────────────────────────────
    fun easeOut3(p: Float) = 1f - (1f - p).pow(3)
    fun clamp(value: Float, lo: Float, hi: Float) = value.coerceIn(lo, hi)
    fun phase(t: Float, start: Float, dur: Float) = clamp((t - start) / dur, 0f, 1f)

    val p0      = phase(t, 0f, 0.45f)
    val flyIn   = easeOut3(p0)
    val pSq     = phase(t, 0.45f, 0.17f)
    val squeeze = sin(pSq * PI.toFloat())
    val pBlast  = phase(t, 0.62f, 0.58f)
    val blast   = 1f - (1f - pBlast).pow(2.8f)
    val pVortex = phase(t, 1.25f, 0.70f)
    val vortex  = pVortex.pow(2.2f)
    val pLand   = phase(t, 1.95f, 0.55f)
    val spring  = exp(-5.5f * pLand) * cos(14f * pLand)
    val hoverT  = if (t > 2.45f) t - 2.45f else 0f
    val hoverY  = sin(hoverT * 3.5f) * 7f

    // Mascot Y-offset (dp)
    val mascotY: Float = when {
        t < 0.45f -> -380f * (1f - flyIn)
        t < 0.62f -> 14f * squeeze
        t < 0.82f -> -10f * (t - 0.62f) / 0.20f
        t < 1.95f -> 0f
        else      -> -14f * (1f - spring) + hoverY
    }
    val mascotOpacity: Float = when {
        t < 0.15f -> t / 0.15f
        t < 0.62f -> 1f
        t < 0.76f -> 1f - (t - 0.62f) / 0.14f
        t < 1.90f -> 0f
        t < 2.15f -> (t - 1.90f) / 0.25f
        else      -> 1f
    }

    val logoP       = clamp((t - 1.95f) / 0.45f, 0f, 1f)
    val progressRatio = clamp(t / 3.3f, 0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF5CD001), Color(0xFF4BBC00)),
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 1000f)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.weight(1f))

            // ── Mascot area ───────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .offset(y = mascotY.dp)
                    .alpha(mascotOpacity),
                contentAlignment = Alignment.Center
            ) {
                // Green glow behind mascot
                if (t >= 1.95f) {
                    val glowPulse = (1f + sin(t * 3f) * 0.15f)
                    Box(
                        modifier = Modifier
                            .size(230.dp)
                            .scale(glowPulse)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(Color.White.copy(alpha = 0.35f))
                            .blur(18.dp)
                    )
                }

                // Mascot emoji placeholder (real asset: DuoBackpack would use Image(painterResource))
                Text("🦜", fontSize = 120.sp)
            }

            Spacer(Modifier.height(32.dp))

            // ── Brand title ───────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .alpha(logoP)
                    .offset(y = (25f * (1f - logoP)).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Code in Go",
                    color = Color.White,
                    fontSize = 52.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Black,
                )
                Text(
                    text = "Learn programming. For free. Forever.",
                    color = Color.White.copy(alpha = 0.88f),
                    fontSize = 15.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                )
            }

            Spacer(Modifier.weight(1f))

            // ── Progress bar ──────────────────────────────────────────────────
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
