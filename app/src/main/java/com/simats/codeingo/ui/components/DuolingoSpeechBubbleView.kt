package com.simats.codeingo.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

enum class SpeechBubblePointerDirection {
    BOTTOM,
    LEADING,
    TRAILING
}

@Composable
fun DuolingoSpeechBubbleView(
    text: String,
    modifier: Modifier = Modifier,
    highlightedText: String? = null,
    highlightColor: Color = AmberGold,
    pointerDirection: SpeechBubblePointerDirection = SpeechBubblePointerDirection.BOTTOM
) {
    val popScale = remember { Animatable(0.8f) }
    val popAlpha = remember { Animatable(0.0f) }

    LaunchedEffect(Unit) {
        popScale.animateTo(1.0f, animationSpec = spring(dampingRatio = 0.65f, stiffness = 400f))
        popAlpha.animateTo(1.0f, animationSpec = spring(dampingRatio = 0.8f, stiffness = 500f))
    }

    val containerBg = Color(0xFF141F38).copy(alpha = 0.92f)
    val strokeColor = AmberGold.copy(alpha = 0.40f)

    Column(
        modifier = modifier
            .scale(popScale.value)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = AmberGold.copy(alpha = 0.15f),
                spotColor = AmberGold.copy(alpha = 0.25f)
            ),
        horizontalAlignment = Alignment.Start
    ) {
        // Main Speech Bubble Body
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(containerBg)
                .border(1.5.dp, strokeColor, RoundedCornerShape(18.dp))
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            val annotatedText = buildAnnotatedString {
                if (highlightedText != null && text.contains(highlightedText)) {
                    val parts = text.split(highlightedText)
                    append(parts.firstOrNull() ?: "")
                    withStyle(style = SpanStyle(color = highlightColor, fontWeight = FontWeight.Black)) {
                        append(highlightedText)
                    }
                    if (parts.size > 1) {
                        append(parts.drop(1).joinToString(highlightedText))
                    }
                } else {
                    append(text)
                }
            }

            Text(
                text = annotatedText,
                color = LocalDynamicThemeColors.current.textPrimary,
                fontSize = 19.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif,
                lineHeight = 24.sp
            )
        }

        // Pointer Triangle
        if (pointerDirection == SpeechBubblePointerDirection.BOTTOM) {
            Row(modifier = Modifier.offset(y = (-1).dp)) {
                Spacer(modifier = Modifier.width(44.dp))
                Canvas(modifier = Modifier.size(width = 20.dp, height = 12.dp)) {
                    val path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(size.width * 0.5f, size.height)
                        lineTo(size.width, 0f)
                        close()
                    }
                    drawPath(path, color = containerBg)
                    // Draw bottom stroke edges
                    val strokePath = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(size.width * 0.5f, size.height)
                        lineTo(size.width, 0f)
                    }
                    drawPath(
                        strokePath,
                        color = strokeColor,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f * density)
                    )
                }
            }
        }
    }
}
