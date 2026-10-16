package com.simats.codeingo.ui.arenas.common

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

/**
 * 🌌 Dynamic Arena Environment Background
 * Provides a stunning, high-tech cyber atmosphere in both Dark Mode and Light Mode.
 * - Dark Mode: Deep obsidian to cyber navy gradient with pulsating neon particle auras and subtle cyber circuit grid lines.
 * - Light Mode: Crisp frosted cyan-slate gradient with soft ambient light blooms, ensuring high contrast and avoiding washed-out text.
 */
@Composable
fun ArenaEnvironmentBackground(
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val infiniteTransition = rememberInfiniteTransition(label = "arena_ambient")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    val gridOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "grid_offset"
    )

    val bgGradient = if (isDark) {
        Brush.verticalGradient(
            listOf(
                Color(0xFF070B14),
                Color(0xFF0B132B),
                Color(0xFF060911)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color(0xFFF1F5F9),
                Color(0xFFE2E8F0),
                Color(0xFFEDF2F7)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgGradient)
    ) {
        // High-tech subtle circuit grid lines & ambient glowing beacons
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Ambient background radial aura at top
            drawCircle(
                brush = Brush.radialGradient(
                    colors = if (isDark) {
                        listOf(
                            accentColor.copy(alpha = 0.18f * pulseAlpha),
                            Color.Transparent
                        )
                    } else {
                        listOf(
                            accentColor.copy(alpha = 0.09f * pulseAlpha),
                            Color.Transparent
                        )
                    },
                    center = Offset(width * 0.5f, height * 0.15f),
                    radius = width * 0.65f
                )
            )

            // Ambient glow at bottom right
            drawCircle(
                brush = Brush.radialGradient(
                    colors = if (isDark) {
                        listOf(
                            accentColor.copy(alpha = 0.14f * pulseAlpha),
                            Color.Transparent
                        )
                    } else {
                        listOf(
                            accentColor.copy(alpha = 0.07f * pulseAlpha),
                            Color.Transparent
                        )
                    },
                    center = Offset(width * 0.85f, height * 0.8f),
                    radius = width * 0.5f
                )
            )

            // Subtle cyber grid lines
            val gridStep = 44f
            val gridLineColor = if (isDark) {
                Color.White.copy(alpha = 0.025f)
            } else {
                Color(0xFF475569).copy(alpha = 0.04f)
            }

            // Horizontal lines
            var y = gridOffset % gridStep
            while (y < height) {
                drawLine(
                    color = gridLineColor,
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f
                )
                y += gridStep
            }

            // Vertical lines
            var x = 0f
            while (x < width) {
                drawLine(
                    color = gridLineColor,
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 1f
                )
                x += gridStep
            }
        }
    }
}
