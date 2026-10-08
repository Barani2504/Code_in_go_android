package com.simats.codeingo.ui.dashboard

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.domain.LocalizationManager
import com.simats.codeingo.ui.phoenix.PhoenixAtmosphericBackgroundView
import com.simats.codeingo.ui.phoenix.SmoothFlyingPhoenixView
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.PhoenixMotion
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ══════════════════════════════════════════════════════════════════
// ⏳ LoadingScreen — Exact Parity with iOS LoadingView.swift
// ══════════════════════════════════════════════════════════════════

@Composable
fun LoadingScreen(
    onFinishedLoading: () -> Unit,
    modifier: Modifier = Modifier
) {
    var dotCount by remember { mutableIntStateOf(1) }
    val isDark = LocalDynamicThemeColors.current.isDark

    LaunchedEffect(Unit) {
        val dotJob = launch {
            while (true) {
                delay(400)
                dotCount = (dotCount % 3) + 1
            }
        }
        delay(2400)
        dotJob.cancel()
        onFinishedLoading()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "loadingKinematics")

    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    val note1Y by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -35f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "note1Y"
    )

    val note2Y by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -42f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, delayMillis = 200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "note2Y"
    )

    val note3Y by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -48f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, delayMillis = 400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "note3Y"
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        PhoenixAtmosphericBackgroundView()

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
                        .size(200.dp)
                        .scale(glowScale)
                        .blur(24.dp)
                        .clip(CircleShape)
                        .background(AmberGold.copy(alpha = 0.35f))
                )

                // Floating DSA Icons
                Text(
                    text = "📚",
                    fontSize = 26.sp,
                    modifier = Modifier.offset(x = (-50).dp, y = (-70).dp + note1Y.dp)
                )
                Text(
                    text = "🧠",
                    fontSize = 24.sp,
                    modifier = Modifier.offset(x = 45.dp, y = (-80).dp + note2Y.dp)
                )
                Text(
                    text = "⚡️",
                    fontSize = 22.sp,
                    modifier = Modifier.offset(x = 10.dp, y = (-95).dp + note3Y.dp)
                )

                // Smooth Flying Wings Phoenix Mascot
                SmoothFlyingPhoenixView(size = 190.dp)
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Localized Loading Text
            Text(
                text = LocalizationManager.instance.string("loading").replace("...", "").uppercase(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif,
                color = if (isDark) Color.White else Color(0xFF12_18_26),
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Animated 3-dot pulse bar
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (i in 1..3) {
                    val active = i <= dotCount
                    val dotScale by animateFloatAsState(
                        targetValue = if (active) 1.25f else 0.8f,
                        animationSpec = PhoenixMotion.BounceSpring,
                        label = "dotScale_$i"
                    )
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .scale(dotScale)
                            .clip(CircleShape)
                            .background(
                                if (active) AmberGold
                                else (if (isDark) Color.White.copy(alpha = 0.20f) else Color.Black.copy(alpha = 0.15f))
                            )
                    )
                }
            }
        }
    }
}
