package com.simats.codeingo.ui.phoenix

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import com.simats.codeingo.domain.AppTheme
import com.simats.codeingo.domain.ThemeManager
import com.simats.codeingo.ui.theme.AmberGold
import kotlin.math.sin

// ══════════════════════════════════════════════════════════════════
// 🌌 PhoenixAtmosphericBackgroundView — Cosmic Realm Background
// Exact parity with iOS PhoenixAtmosphericBackgroundView.swift:
// - Dark Mode: Deep volcanic obsidian canvas (#080E1A -> #0B111F -> #111222)
// - Light Mode: Luminous golden sunrise celestial canvas (#F5F7FC -> #ECF1FA -> #F2F5FD)
// - Floating animated embers drifting upward with shimmer
// - Celestial star dust with gentle pulse
// ══════════════════════════════════════════════════════════════════

private data class EmberSeed(
    val id: Int,
    val xRatio: Float,
    val startYRatio: Float,
    val speed: Float,
    val size: Float,
    val color: Color
)

private data class StarSeed(
    val id: Int,
    val xRatio: Float,
    val yRatio: Float,
    val size: Float,
    val pulseSpeed: Float
)

@Composable
fun PhoenixAtmosphericBackgroundView(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    val themeManager = ThemeManager.instance
    val currentTheme by themeManager.currentTheme.collectAsState()
    val isSystemDark = isSystemInDarkTheme()

    val isDark = when (currentTheme) {
        AppTheme.DARK -> true
        AppTheme.LIGHT -> false
        AppTheme.SYSTEM -> isSystemDark
    }

    val infiniteTransition = rememberInfiniteTransition(label = "emberAnim")
    val animTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 100000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    val embers = remember {
        listOf(
            EmberSeed(0, 0.08f, 0.92f, 16.0f, 3.8f, AmberGold),
            EmberSeed(1, 0.18f, 0.74f, 19.0f, 2.8f, Color(0xFFFF731E)),
            EmberSeed(2, 0.28f, 0.88f, 13.0f, 4.8f, Color(0xFFFF4733)),
            EmberSeed(3, 0.38f, 0.65f, 17.5f, 3.2f, Color(0xFFFFBF33)),
            EmberSeed(4, 0.48f, 0.95f, 14.0f, 5.2f, AmberGold),
            EmberSeed(5, 0.58f, 0.82f, 20.0f, 2.6f, Color(0xFFFF8026)),
            EmberSeed(6, 0.68f, 0.90f, 15.5f, 4.2f, Color(0xFFFF4D38)),
            EmberSeed(7, 0.78f, 0.70f, 21.0f, 3.0f, AmberGold),
            EmberSeed(8, 0.88f, 0.85f, 17.0f, 3.6f, Color(0xFFFF9919)),
            EmberSeed(9, 0.94f, 0.60f, 18.0f, 2.4f, Color(0xFFFF592E)),
            EmberSeed(10, 0.12f, 0.48f, 16.5f, 3.4f, Color(0xFFFF8C26)),
            EmberSeed(11, 0.24f, 0.35f, 22.0f, 2.5f, AmberGold),
            EmberSeed(12, 0.35f, 0.52f, 14.5f, 4.5f, Color(0xFFFF402E)),
            EmberSeed(13, 0.45f, 0.30f, 19.5f, 3.0f, Color(0xFFFFB340)),
            EmberSeed(14, 0.55f, 0.42f, 15.0f, 4.0f, AmberGold),
            EmberSeed(15, 0.65f, 0.25f, 23.0f, 2.2f, Color(0xFFFF6626)),
            EmberSeed(16, 0.75f, 0.55f, 16.0f, 3.5f, Color(0xFFFF4D38)),
            EmberSeed(17, 0.84f, 0.38f, 20.5f, 2.8f, AmberGold),
            EmberSeed(18, 0.05f, 0.22f, 18.5f, 3.2f, Color(0xFFFF6B1A)),
            EmberSeed(19, 0.92f, 0.18f, 17.0f, 3.6f, Color(0xFFFF5933)),
            EmberSeed(20, 0.16f, 0.12f, 15.0f, 2.8f, AmberGold),
            EmberSeed(21, 0.32f, 0.15f, 19.0f, 3.5f, Color(0xFFFF7A26)),
            EmberSeed(22, 0.52f, 0.08f, 13.5f, 4.6f, Color(0xFFFFCC4D)),
            EmberSeed(23, 0.70f, 0.14f, 21.5f, 2.4f, Color(0xFFFF5933)),
            EmberSeed(24, 0.86f, 0.06f, 16.5f, 3.8f, AmberGold),
            EmberSeed(25, 0.40f, 0.80f, 12.0f, 5.4f, Color(0xFFFFE699)),
            EmberSeed(26, 0.60f, 0.62f, 18.0f, 3.0f, Color(0xFFFF8C1A)),
            EmberSeed(27, 0.80f, 0.45f, 14.0f, 4.0f, Color(0xFFFF472E))
        )
    }

    val stars = remember {
        listOf(
            StarSeed(0, 0.10f, 0.12f, 1.8f, 2.8f),
            StarSeed(1, 0.25f, 0.08f, 1.4f, 3.4f),
            StarSeed(2, 0.85f, 0.15f, 2.0f, 2.5f),
            StarSeed(3, 0.92f, 0.28f, 1.5f, 3.8f),
            StarSeed(4, 0.06f, 0.45f, 1.6f, 3.1f),
            StarSeed(5, 0.94f, 0.52f, 1.7f, 2.9f),
            StarSeed(6, 0.15f, 0.68f, 1.5f, 3.6f),
            StarSeed(7, 0.88f, 0.72f, 1.8f, 2.7f),
            StarSeed(8, 0.08f, 0.85f, 1.4f, 3.2f),
            StarSeed(9, 0.75f, 0.88f, 1.6f, 2.6f)
        )
    }

    val topColor by animateColorAsState(
        targetValue = if (isDark) Color(0xFF080E1A) else Color(0xFFF5F7FC),
        animationSpec = tween(300),
        label = "topBgColor"
    )
    val midColor by animateColorAsState(
        targetValue = if (isDark) Color(0xFF0B111F) else Color(0xFFECF1FA),
        animationSpec = tween(300),
        label = "midBgColor"
    )
    val bottomColor by animateColorAsState(
        targetValue = if (isDark) Color(0xFF111222) else Color(0xFFF2F5FD),
        animationSpec = tween(300),
        label = "bottomBgColor"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(topColor, midColor, bottomColor)))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val density = this.density

            // 1. Draw glowing radiant nebulae
            val nebulaAlpha = if (isDark) 0.12f else 0.06f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(AmberGold.copy(alpha = nebulaAlpha), Color.Transparent),
                    center = Offset(width * 0.5f, height * 0.25f),
                    radius = width * 0.6f
                ),
                radius = width * 0.6f,
                center = Offset(width * 0.5f, height * 0.25f)
            )
            val crimsonAlpha = if (isDark) 0.08f else 0.04f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFF3B30).copy(alpha = crimsonAlpha), Color.Transparent),
                    center = Offset(width * 0.75f, height * 0.70f),
                    radius = width * 0.5f
                ),
                radius = width * 0.5f,
                center = Offset(width * 0.75f, height * 0.70f)
            )

            // 2. Draw stars (more subtle in light mode)
            val starBaseAlpha = if (isDark) 0.25f else 0.10f
            val starColor = if (isDark) Color.White else Color(0xFFB0B7C6)
            for (star in stars) {
                val pulse = (sin((animTime * star.pulseSpeed).toDouble()).toFloat() + 1f) * 0.5f
                val alpha = starBaseAlpha + 0.65f * pulse
                drawCircle(
                    color = starColor.copy(alpha = alpha.coerceIn(0f, 1f)),
                    radius = star.size * density,
                    center = Offset(star.xRatio * width, star.yRatio * height)
                )
            }

            // 3. Draw drifting embers
            for (ember in embers) {
                val travel = (animTime * ember.speed * 8f) % (height + 100f)
                var curY = (ember.startYRatio * height) - travel
                if (curY < -50f) {
                    curY += (height + 100f)
                }
                val sway = sin((animTime * 2f + ember.id).toDouble()).toFloat() * 12f * density
                val curX = (ember.xRatio * width) + sway

                val yRatio = (curY / height).coerceIn(0f, 1f)
                val alphaBase = (sin((yRatio * Math.PI).toDouble()).toFloat()).coerceIn(0.2f, 0.85f)
                val effectiveAlpha = if (isDark) alphaBase else alphaBase * 0.7f

                drawCircle(
                    color = ember.color.copy(alpha = effectiveAlpha),
                    radius = (ember.size * 0.5f) * density,
                    center = Offset(curX, curY)
                )
            }
        }

        content()
    }
}
