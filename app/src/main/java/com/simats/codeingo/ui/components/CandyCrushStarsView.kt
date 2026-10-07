package com.simats.codeingo.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.simats.codeingo.ui.theme.AmberGold
import kotlinx.coroutines.delay
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

@Composable
fun CandyCrushStarsView(
    earnedStars: Int,
    starSize: Dp = 42.dp,
    modifier: Modifier = Modifier
) {
    val scales = remember { List(5) { Animatable(1f) } }
    val rotations = remember { List(5) { Animatable(0f) } }
    val isLit = remember { List(5) { androidx.compose.runtime.mutableStateOf(false) } }

    LaunchedEffect(earnedStars) {
        for (i in 0 until minOf(earnedStars, 5)) {
            delay(280)
            isLit[i].value = true
            rotations[i].snapTo(if (i % 2 == 0) 18f else -18f)
            scales[i].animateTo(1.6f, tween(120, easing = FastOutSlowInEasing))
            rotations[i].animateTo(0f, spring(dampingRatio = 0.5f))
            scales[i].animateTo(1.15f, spring(dampingRatio = 0.55f))
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (index in 0 until 5) {
            val lit = isLit[index].value
            val scale = scales[index].value
            val rotation = rotations[index].value

            Box(
                modifier = Modifier
                    .size(starSize)
                    .scale(scale)
                    .rotate(rotation),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Star ${index + 1}",
                    tint = if (lit) AmberGold else LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.2f),
                    modifier = Modifier.size(starSize)
                )
            }
        }
    }
}
