package com.simats.duolingo.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.simats.duolingo.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(onFinished: () -> Unit) {
    var dotCount by remember { mutableIntStateOf(1) }
    val glowAnim = rememberInfiniteTransition(label = "glow")
    val glowScale by glowAnim.animateFloat(
        initialValue = 0.8f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            tween(900, easing = EaseInOut),
            RepeatMode.Reverse
        ), label = "glowScale"
    )
    val mascotY by glowAnim.animateFloat(
        initialValue = 0f, targetValue = -14f,
        animationSpec = infiniteRepeatable(
            tween(800, easing = EaseInOut),
            RepeatMode.Reverse
        ), label = "mascotY"
    )

    LaunchedEffect(Unit) {
        // Dots cycle
        repeat(100) {
            delay(400)
            dotCount = (dotCount % 3) + 1
        }
    }
    LaunchedEffect(Unit) {
        delay(2500)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DuolingoDarkBg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            Spacer(Modifier.weight(1f))

            // Mascot with glow
            Box(contentAlignment = Alignment.Center) {
                // Orange glow
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .scale(glowScale)
                        .clip(CircleShape)
                        .background(DuolingoOrange.copy(alpha = 0.18f))
                        .blur(24.dp)
                )
                Text(
                    text = "🦜",
                    fontSize = 100.sp,
                    modifier = Modifier.offset(y = mascotY.dp)
                )
            }

            // Animated loading text
            Text(
                text = "Loading" + ".".repeat(dotCount),
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
            )

            Text(
                text = "Preparing your lesson...",
                color = DuolingoSubtext,
                fontSize = 14.sp,
            )

            Spacer(Modifier.weight(1f))
        }
    }
}
