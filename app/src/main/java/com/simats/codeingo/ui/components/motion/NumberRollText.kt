package com.simats.codeingo.ui.components.motion

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

/**
 * Animated Number Roll Counter for AshCrypt.
 *
 * Smoothly interpolates integer values (XP awards, gem earnings, streaks, accuracy scores)
 * using spring/easing transitions with vertical digit-roll animations.
 */
@Composable
fun NumberRollText(
    targetValue: Int,
    modifier: Modifier = Modifier,
    prefix: String = "",
    suffix: String = "",
    style: TextStyle = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.Black,
        fontFamily = FontFamily.Monospace
    ),
    durationMs: Int = 800
) {
    var displayValue by remember { mutableIntStateOf(0) }
    val animatable = remember { Animatable(0f) }

    LaunchedEffect(targetValue) {
        animatable.snapTo(displayValue.toFloat())
        animatable.animateTo(
            targetValue = targetValue.toFloat(),
            animationSpec = tween(durationMillis = durationMs, easing = FastOutSlowInEasing)
        ) {
            displayValue = this.value.roundToInt()
        }
    }

    Row(modifier = modifier) {
        if (prefix.isNotEmpty()) {
            Text(text = prefix, style = style)
        }

        displayValue.toString().forEach { char ->
            AnimatedContent(
                targetState = char,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInVertically { height -> height } + fadeIn() togetherWith
                                slideOutVertically { height -> -height } + fadeOut()
                    } else {
                        slideInVertically { height -> -height } + fadeIn() togetherWith
                                slideOutVertically { height -> height } + fadeOut()
                    }
                },
                label = "digitRoll"
            ) { digit ->
                Text(
                    text = digit.toString(),
                    style = style
                )
            }
        }

        if (suffix.isNotEmpty()) {
            Text(text = suffix, style = style)
        }
    }
}

