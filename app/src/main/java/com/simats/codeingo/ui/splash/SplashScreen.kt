package com.simats.codeingo.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.components.ProgressBarAnimated
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoGreenDark
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SplashScreen(
    onFinishedSplash: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = remember { Animatable(0f) }
    val mascotScale = remember { Animatable(0.2f) }
    val mascotAlpha = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }
    val titleOffsetY = remember { Animatable(30f) }
    val bubbleExplosion = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Mascot fly-in
        launch {
            mascotAlpha.animateTo(1f, tween(400))
        }
        launch {
            mascotScale.animateTo(1.15f, tween(500, easing = FastOutSlowInEasing))
            mascotScale.animateTo(1.0f, tween(300, easing = FastOutSlowInEasing))
        }
        // Bubble blast
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
                        Color(0xFF58CC02),
                        Color(0xFF46A302),
                        Color(0xFF2E7200)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Animated background particles / bubbles
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f - 40.dp.toPx())
            val explosion = bubbleExplosion.value
            val bubbleColors = listOf(
                Color(0xFF58CC02),
                Color(0xFF88E714),
                Color(0xFFFFC800),
                Color(0xFF2EE07A),
                Color.White,
                Color(0xFF38C2F5),
                Color(0xFFFA8000)
            )

            for (i in 0 until 24) {
                val angle = (i.toFloat() / 24f) * 2f * Math.PI.toFloat()
                val radius = (80.dp.toPx() + (i % 5) * 20.dp.toPx()) * explosion
                val x = center.x + cos(angle.toDouble()).toFloat() * radius
                val y = center.y + sin(angle.toDouble()).toFloat() * radius
                val bubbleSize = (8.dp.toPx() + (i % 4) * 4.dp.toPx()) * (1f - explosion * 0.3f)
                val alpha = (1f - explosion * 0.7f).coerceIn(0f, 1f)

                drawCircle(
                    color = bubbleColors[i % bubbleColors.size].copy(alpha = alpha * 0.8f),
                    radius = bubbleSize,
                    center = Offset(x, y)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Center Mascot
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(180.dp)
                    .scale(mascotScale.value)
                    .alpha(mascotAlpha.value)
            ) {
                // Soft glow behind mascot
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                )
                // Mascot representation
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.simats.codeingo.R.drawable.phoenix),
                    contentDescription = "Codeingo Phoenix Mascot",
                    modifier = Modifier.size(140.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Brand Titles
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .alpha(titleAlpha.value)
                    .offset(y = titleOffsetY.value.dp)
            ) {
                Text(
                    text = "DSA Learn",
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Master Data Structures & Algorithms",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.9f)
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
                    barColor = Color.White,
                    trackColor = Color.White.copy(alpha = 0.25f)
                )
            }
        }
    }
}
