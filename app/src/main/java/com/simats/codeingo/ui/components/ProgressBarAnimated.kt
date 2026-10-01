package com.simats.codeingo.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.simats.codeingo.ui.theme.DuolingoGreen

/**
 * Animated capsule progress bar matching Duolingo lesson progress style.
 */
@Composable
fun ProgressBarAnimated(
    progress: Float, // 0f to 1f
    modifier: Modifier = Modifier,
    height: Dp = 16.dp,
    barColor: Color = DuolingoGreen,
    trackColor: Color = Color(0xFF2E384D)
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "ProgressBarAnimation"
    )

    Box(
        modifier = modifier
            .height(height)
            .fillMaxWidth()
            .clip(CircleShape)
            .background(trackColor)
    ) {
        if (animatedProgress > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .clip(CircleShape)
                    .background(barColor)
            ) {
                // Top specular highlight pill for 3D appearance
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height * 0.35f)
                        .clip(RoundedCornerShape(bottomStart = 2.dp, bottomEnd = 2.dp))
                        .background(Color.White.copy(alpha = 0.25f))
                )
            }
        }
    }
}
