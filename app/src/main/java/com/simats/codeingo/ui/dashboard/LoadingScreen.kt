package com.simats.codeingo.ui.dashboard

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DsaBlue
import com.simats.codeingo.ui.theme.InputBorder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoadingScreen(
    onFinishedLoading: () -> Unit,
    modifier: Modifier = Modifier
) {
    var dotCount by remember { mutableIntStateOf(1) }

    LaunchedEffect(Unit) {
        val dotJob = launch {
            while (true) {
                delay(400)
                dotCount = (dotCount % 3) + 1
            }
        }
        delay(2200)
        dotJob.cancel()
        onFinishedLoading()
    }

    val transition = rememberInfiniteTransition(label = "LoadingAnim")
    val mascotY by transition.animateFloat(
        initialValue = 0f,
        targetValue = -18f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "MascotWiggleY"
    )
    val glowScale by transition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowScale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(240.dp)
            ) {
                // Background Pulsing Glow
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .scale(glowScale)
                        .clip(CircleShape)
                        .background(DsaBlue.copy(alpha = 0.25f))
                )

                // Mascot
                Text(
                    text = "🦅",
                    fontSize = 90.sp,
                    modifier = Modifier.offset(y = mascotY.dp)
                )

                // Floating Icons
                Text(
                    text = "📚",
                    fontSize = 24.sp,
                    modifier = Modifier.offset(x = (-50).dp, y = (-70).dp + (mascotY * 0.5f).dp)
                )
                Text(
                    text = "🧠",
                    fontSize = 22.sp,
                    modifier = Modifier.offset(x = 45.dp, y = (-80).dp + (mascotY * 0.3f).dp)
                )
                Text(
                    text = "⚡️",
                    fontSize = 20.sp,
                    modifier = Modifier.offset(x = 10.dp, y = (-95).dp + (mascotY * 0.6f).dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "LOADING...",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (i in 1..3) {
                    val active = i <= dotCount
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (active) DsaBlue else InputBorder)
                    )
                }
            }
        }
    }
}
