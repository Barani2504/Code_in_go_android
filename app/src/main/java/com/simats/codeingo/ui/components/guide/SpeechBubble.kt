package com.simats.codeingo.ui.components.guide

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.components.AshButton
import com.simats.codeingo.ui.components.AshButtonStyle
import com.simats.codeingo.ui.theme.AshTheme
import com.simats.codeingo.ui.theme.AshType
import kotlinx.coroutines.delay

enum class SpeechBubbleTail {
    NONE,
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT
}

/**
 * Ash SpeechBubble with typewriter effect, skippable on tap, tail pointer,
 * <= 18 words per line, 16 sp typography, and tactile primary button.
 */
@Composable
fun SpeechBubble(
    text: String,
    modifier: Modifier = Modifier,
    actionLabel: String = "Next",
    onActionClick: () -> Unit,
    secondaryLabel: String? = null,
    onSecondaryClick: (() -> Unit)? = null,
    tail: SpeechBubbleTail = SpeechBubbleTail.BOTTOM_LEFT,
    typewriterEnabled: Boolean = true
) {
    var displayedCharCount by remember(text) {
        mutableIntStateOf(if (typewriterEnabled) 0 else text.length)
    }

    var isFinishedTyping by remember(text) {
        mutableStateOf(!typewriterEnabled)
    }

    // Typewriter coroutine
    LaunchedEffect(text, typewriterEnabled) {
        if (!typewriterEnabled) {
            displayedCharCount = text.length
            isFinishedTyping = true
            return@LaunchedEffect
        }
        displayedCharCount = 0
        isFinishedTyping = false
        for (i in 1..text.length) {
            displayedCharCount = i
            delay(18) // ~55 chars per second for lively pacing
        }
        isFinishedTyping = true
    }

    val bubbleShape = RoundedCornerShape(20.dp)
    val surfaceColor = AshTheme.colors.surface
    val borderColor = AshTheme.colors.accent.copy(alpha = 0.5f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Top tail if needed
        if (tail == SpeechBubbleTail.TOP_LEFT || tail == SpeechBubbleTail.TOP_RIGHT) {
            TailPointer(
                tail = tail,
                color = surfaceColor,
                borderColor = borderColor,
                isTop = true
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 6.dp, shape = bubbleShape)
                .clip(bubbleShape)
                .background(surfaceColor)
                .border(2.dp, borderColor, bubbleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    // Tap bubble to skip typewriter animation immediately
                    if (!isFinishedTyping) {
                        displayedCharCount = text.length
                        isFinishedTyping = true
                    }
                }
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = text.take(displayedCharCount),
                    style = AshType.bodyLarge.copy(
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = AshTheme.colors.ink,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (secondaryLabel != null && onSecondaryClick != null) {
                        AshButton(
                            text = secondaryLabel,
                            onClick = onSecondaryClick,
                            style = AshButtonStyle.OUTLINE,
                            modifier = Modifier.height(44.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    AshButton(
                        text = actionLabel,
                        onClick = onActionClick,
                        style = AshButtonStyle.PRIMARY,
                        modifier = Modifier.height(44.dp)
                    )
                }
            }
        }

        // Bottom tail if needed
        if (tail == SpeechBubbleTail.BOTTOM_LEFT || tail == SpeechBubbleTail.BOTTOM_RIGHT) {
            TailPointer(
                tail = tail,
                color = surfaceColor,
                borderColor = borderColor,
                isTop = false
            )
        }
    }
}

@Composable
private fun TailPointer(
    tail: SpeechBubbleTail,
    color: Color,
    borderColor: Color,
    isTop: Boolean
) {
    val alignment = if (tail == SpeechBubbleTail.TOP_LEFT || tail == SpeechBubbleTail.BOTTOM_LEFT) {
        Alignment.CenterStart
    } else {
        Alignment.CenterEnd
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        contentAlignment = alignment
    ) {
        Canvas(
            modifier = Modifier
                .size(width = 18.dp, height = 12.dp)
                .offset(y = if (isTop) 1.dp else (-1).dp)
        ) {
            val path = Path().apply {
                if (isTop) {
                    moveTo(size.width / 2f, 0f)
                    lineTo(size.width, size.height)
                    lineTo(0f, size.height)
                    close()
                } else {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width / 2f, size.height)
                    close()
                }
            }
            drawPath(path = path, color = color)
        }
    }
}
