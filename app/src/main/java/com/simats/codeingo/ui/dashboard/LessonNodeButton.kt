package com.simats.codeingo.ui.dashboard

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.DuolingoRedDark
import com.simats.codeingo.ui.theme.InputBorder
import com.simats.codeingo.ui.theme.SubtextGray

@Composable
fun LessonNodeButton(
    levelNumber: Int,
    icon: String,
    title: String,
    xOffset: Dp = 0.dp,
    themeColor: Color,
    themeDarkColor: Color,
    isUnlocked: Boolean,
    isActive: Boolean,
    isBoss: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val size = if (isBoss) 76.dp else 68.dp
    val shadowOffset = if (isBoss) 8.dp else 7.dp
    val pressOffsetY by animateDpAsState(
        targetValue = if (isPressed) 5.dp else 0.dp,
        animationSpec = tween(durationMillis = 100),
        label = "NodePressOffset"
    )

    val faceColor = when {
        !isUnlocked -> CardBackground
        isBoss -> DuolingoRed
        else -> themeColor
    }

    val shadowColor = when {
        !isUnlocked -> Color(0xFF142028)
        isBoss -> DuolingoRedDark
        else -> themeDarkColor
    }

    Box(
        modifier = modifier
            .offset(x = xOffset)
            .size(width = size + 8.dp, height = size + shadowOffset + 4.dp)
            .clickable(interactionSource = interactionSource, indication = null) { onClick() },
        contentAlignment = Alignment.TopCenter
    ) {
        // 3D Shadow Layer
        Box(
            modifier = Modifier
                .offset(y = if (isPressed) 3.dp else shadowOffset)
                .size(size)
                .clip(CircleShape)
                .background(shadowColor)
        )

        // 3D Front Face Layer
        Box(
            modifier = Modifier
                .offset(y = pressOffsetY)
                .size(size)
                .clip(CircleShape)
                .background(faceColor)
                .border(
                    width = if (isBoss) 2.5.dp else 2.dp,
                    color = when {
                        !isUnlocked -> InputBorder
                        isBoss -> Color(0xFFFFD700).copy(alpha = 0.8f)
                        else -> Color.White.copy(alpha = 0.25f)
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isUnlocked) {
                if (isBoss) {
                    Text(text = "🔥", fontSize = 32.sp)
                } else {
                    Text(
                        text = if (icon.startsWith("x.") || icon.contains("grid") || icon.contains("text")) "📝" else "⭐",
                        fontSize = 26.sp
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = SubtextGray.copy(alpha = 0.5f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
