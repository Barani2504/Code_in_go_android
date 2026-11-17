package com.simats.codeingo.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.motion.AshHaptics
import com.simats.codeingo.ui.theme.LocalReduceMotion
import com.simats.codeingo.ui.theme.AppFontFamily
import com.simats.codeingo.ui.theme.AshTheme

enum class AshButtonStyle {
    BRAND,
    SURFACE,
    SUCCESS,
    DANGER,
    DISABLED;

    companion object {
        val Brand = BRAND
        val Surface = SURFACE
        val Success = SUCCESS
        val Danger = DANGER
        val Disabled = DISABLED
    }
}

/**
 * Standard pressable tactile button for AshCrypt (Phase 1).
 * Features: flat top, 4-dp physical bottom edge, 4-dp press depression, and tactile haptics.
 */
@Composable
fun AshButton(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    style: AshButtonStyle = AshButtonStyle.BRAND,
    isEnabled: Boolean = true,
    height: Dp = 50.dp,
    action: (() -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    val colors = AshTheme.colors
    val activeStyle = if (isEnabled) style else AshButtonStyle.DISABLED
    val depth = 4.dp
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val view = LocalView.current
    val reduceMotion = LocalReduceMotion.current

    val surfaceColor = when (activeStyle) {
        AshButtonStyle.BRAND -> colors.brand
        AshButtonStyle.SURFACE -> colors.surface
        AshButtonStyle.SUCCESS -> colors.success
        AshButtonStyle.DANGER -> colors.danger
        AshButtonStyle.DISABLED -> colors.inkMuted.copy(alpha = 0.25f)
    }

    val shadowColor = when (activeStyle) {
        AshButtonStyle.BRAND -> colors.brand.copy(alpha = 0.65f)
        AshButtonStyle.SURFACE -> colors.inkMuted.copy(alpha = 0.35f)
        AshButtonStyle.SUCCESS -> colors.success.copy(alpha = 0.7f)
        AshButtonStyle.DANGER -> colors.danger.copy(alpha = 0.7f)
        AshButtonStyle.DISABLED -> colors.inkMuted.copy(alpha = 0.15f)
    }

    val textColor = when (activeStyle) {
        AshButtonStyle.BRAND -> colors.onBrand
        AshButtonStyle.SURFACE -> colors.ink
        AshButtonStyle.SUCCESS, AshButtonStyle.DANGER -> Color.White
        AshButtonStyle.DISABLED -> colors.inkMuted
    }

    val currentOffset by animateDpAsState(
        targetValue = if (isPressed && isEnabled) depth else 0.dp,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 1200f),
        label = "btnOffset"
    )

    LaunchedEffect(isPressed) {
        if (isPressed && isEnabled) {
            AshHaptics.tap(view, reduceMotion)
        }
    }

    val clickHandler = {
        if (isEnabled) {
            action?.invoke()
            onClick()
        }
    }

    Box(
        modifier = modifier
            .height(height + depth)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = isEnabled,
                onClick = clickHandler
            )
    ) {
        // Bottom bevel shadow (4dp edge)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset(y = depth)
                .clip(RoundedCornerShape(16.dp))
                .background(shadowColor)
        )

        // Flat top plate
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset(y = currentOffset)
                .clip(RoundedCornerShape(16.dp))
                .background(surfaceColor)
                .border(
                    width = if (activeStyle == AshButtonStyle.SURFACE) 1.5.dp else 1.dp,
                    color = if (activeStyle == AshButtonStyle.SURFACE) colors.inkMuted.copy(alpha = 0.3f) else shadowColor,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = title.uppercase(),
                    color = textColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = AppFontFamily,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
