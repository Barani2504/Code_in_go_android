package com.simats.codeingo.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.R
import com.simats.codeingo.ui.phoenix.PhoenixAtmosphericBackgroundView
import com.simats.codeingo.ui.theme.AmberGold
import kotlinx.coroutines.delay
import kotlin.math.sin

// ══════════════════════════════════════════════════════════════════
// ⏳ LoadingView — Intermediate Compilation & Loading Stage
// Exact Parity with iOS LoadingView.swift
// ══════════════════════════════════════════════════════════════════

@Composable
fun LoadingView(
    modifier: Modifier = Modifier,
    onFinishedLoading: () -> Unit = {}
) {
    var dotCount by remember { mutableIntStateOf(1) }

    LaunchedEffect(Unit) {
        launchDotsTimer { dotCount = it }
        delay(2800)
        onFinishedLoading()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "loadingDance")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    val mascotY = (sin(time.toDouble()) * 8.0).toFloat()
    val mascotRotation = (sin((time * 0.8f).toDouble()) * 4.0).toFloat()

    PhoenixAtmosphericBackgroundView(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(240.dp)
            ) {
                // Pulsing Glow
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .scale(1.0f + (sin((time * 2f).toDouble()) * 0.08).toFloat())
                        .clip(CircleShape)
                        .background(AmberGold.copy(alpha = 0.28f))
                )

                // Floating DSA Emojis
                Text(
                    text = "📚",
                    fontSize = 24.sp,
                    modifier = Modifier
                        .offset(x = (-50).dp, y = (-70).dp + mascotY.dp)
                )
                Text(
                    text = "🧠",
                    fontSize = 22.sp,
                    modifier = Modifier
                        .offset(x = 45.dp, y = (-80).dp - mascotY.dp)
                )
                Text(
                    text = "⚡️",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .offset(x = 10.dp, y = (-95).dp + (mascotY * 0.5f).dp)
                )

                // Mascot
                Image(
                    painter = painterResource(id = R.drawable.phoenix),
                    contentDescription = "Loading Phoenix",
                    modifier = Modifier
                        .size(175.dp)
                        .offset(y = mascotY.dp)
                        .rotate(mascotRotation)
                        .shadow(14.dp, CircleShape, spotColor = AmberGold)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            val dots = ".".repeat(dotCount)
            Text(
                text = "LOADING$dots",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Compiling your environment...",
                color = AmberGold,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}

private suspend fun launchDotsTimer(onTick: (Int) -> Unit) {
    var count = 1
    while (true) {
        delay(400)
        count = (count % 3) + 1
        onTick(count)
    }
}
