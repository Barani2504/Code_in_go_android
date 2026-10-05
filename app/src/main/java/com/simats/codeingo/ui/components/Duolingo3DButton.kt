package com.simats.codeingo.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.AmberGoldDark

// ══════════════════════════════════════════════════════════════════
// 🔘 Duolingo 3D Tactile Push Button (Exact Parity with iOS)
// Features: 4dp physical bevel shadow, smooth 4dp depression on press,
// authentic color schemes, and tactile feedback.
// ══════════════════════════════════════════════════════════════════

typealias Duolingo3DButtonStyle = Duolingo3DButtonColor

enum class Duolingo3DButtonColor {
    GREEN,
    BLUE,
    AMBER,
    WHITE,
    DISABLED;

    companion object {
        val Green = GREEN
        val Blue = BLUE
        val Amber = AMBER
        val White = WHITE
        val Disabled = DISABLED
    }

    val surfaceColor: Color
        get() = when (this) {
            GREEN -> Color(0xFF58CC02)
            BLUE -> Color(0xFF1CB0F6)
            AMBER -> AmberGold
            WHITE -> Color(0xFF243240)
            DISABLED -> Color(0xFF283644)
        }

    val shadowColor: Color
        get() = when (this) {
            GREEN -> Color(0xFF46A302)
            BLUE -> Color(0xFF188ECE)
            AMBER -> AmberGoldDark
            WHITE -> Color(0xFF19232D)
            DISABLED -> Color(0xFF1E2832)
        }

    val textColor: Color
        get() = when (this) {
            GREEN, BLUE -> Color.White
            AMBER -> Color(0xFF1A1205)
            WHITE -> Color(0xFF1CB0F6)
            DISABLED -> Color.White.copy(alpha = 0.4f)
        }
}

@Composable
fun Duolingo3DButton(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    style: Duolingo3DButtonColor = Duolingo3DButtonColor.GREEN,
    isEnabled: Boolean = true,
    height: Dp = 50.dp,
    action: (() -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    val activeStyle = if (isEnabled) style else Duolingo3DButtonColor.DISABLED
    val depth = 4.dp
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val view = LocalView.current

    val currentOffset by animateDpAsState(
        targetValue = if (isPressed && isEnabled) depth else 0.dp,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 1200f),
        label = "btnOffset"
    )

    LaunchedEffect(isPressed) {
        if (isPressed && isEnabled) {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height + depth)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = isEnabled
            ) {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                action?.invoke()
                onClick()
            },
        contentAlignment = Alignment.BottomCenter
    ) {
        // Bottom 3D Bevel Shadow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(16.dp))
                .background(activeStyle.shadowColor)
        )

        // Top Button Surface
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset(y = currentOffset - depth)
                .clip(RoundedCornerShape(16.dp))
                .background(activeStyle.surfaceColor),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = activeStyle.textColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = title.uppercase(),
                    color = activeStyle.textColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = 0.8.sp
                )
            }
        }
    }
}
