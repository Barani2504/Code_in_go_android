package com.simats.codeingo.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.graphics.Brush
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
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.PhoenixCrimson
import com.simats.codeingo.ui.theme.PhoenixEmber
import com.simats.codeingo.ui.theme.PhoenixGreen
import com.simats.codeingo.ui.theme.PhoenixMotion

// ══════════════════════════════════════════════════════════════════
// 🔘 AppButton — Duolingo / Ashnode 3D Pushable Button System
// Features: 4dp physical extruded bevel shadow, glossy light sheen,
// tactile press depression, haptic feedback, and dynamic themes.
// ══════════════════════════════════════════════════════════════════

enum class AppButtonStyle {
    PRIMARY_AMBER,
    SUCCESS_GREEN,
    ACTION_BLUE,
    DANGER_CRIMSON,
    EMBER_ORANGE,
    SECONDARY_GLASS,
    DISABLED;

    @Composable
    fun getFaceBrush(isDark: Boolean): Brush {
        return when (this) {
            PRIMARY_AMBER -> Brush.verticalGradient(
                listOf(AmberGold, Color(0xFFFF_A5_00))
            )
            SUCCESS_GREEN -> Brush.verticalGradient(
                listOf(PhoenixGreen, Color(0xFF1E_AE_45))
            )
            ACTION_BLUE -> Brush.verticalGradient(
                listOf(Color(0xFF1C_B0_F6), Color(0xFF0F_8A_CC))
            )
            DANGER_CRIMSON -> Brush.verticalGradient(
                listOf(PhoenixCrimson, Color(0xFFB5_16_06))
            )
            EMBER_ORANGE -> Brush.verticalGradient(
                listOf(PhoenixEmber, Color(0xFFD6_3E_05))
            )
            SECONDARY_GLASS -> Brush.verticalGradient(
                if (isDark) listOf(Color(0xFF14_1E_37), Color(0xFF0F_16_2B))
                else listOf(Color.White, Color(0xFFF4_F6_FC))
            )
            DISABLED -> Brush.verticalGradient(
                if (isDark) listOf(Color(0xFF23_2C_3D), Color(0xFF1B_23_32))
                else listOf(Color(0xFFE2_E8_F0), Color(0xFFD0_D8_E5))
            )
        }
    }

    @Composable
    fun getShadowColor(isDark: Boolean): Color {
        return when (this) {
            PRIMARY_AMBER -> AmberGoldDark
            SUCCESS_GREEN -> Color(0xFF12_7A_2E)
            ACTION_BLUE -> Color(0xFF0B_68_9E)
            DANGER_CRIMSON -> Color(0xFF88_0E_03)
            EMBER_ORANGE -> Color(0xFFA6_2F_03)
            SECONDARY_GLASS -> if (isDark) AmberGold.copy(alpha = 0.45f) else Color(0xFFCBD5E1)
            DISABLED -> if (isDark) Color(0xFF14_1B_26) else Color(0xFFB8_C2_D1)
        }
    }

    @Composable
    fun getTextColor(isDark: Boolean): Color {
        return when (this) {
            PRIMARY_AMBER -> Color(0xFF1A_10_00)
            SUCCESS_GREEN, ACTION_BLUE, DANGER_CRIMSON, EMBER_ORANGE -> Color.White
            SECONDARY_GLASS -> if (isDark) Color.White else Color(0xFF12_18_26)
            DISABLED -> if (isDark) Color.White.copy(alpha = 0.35f) else Color(0xFF8A_96_A6)
        }
    }
}

@Composable
fun AppButton(
    title: String,
    modifier: Modifier = Modifier,
    style: AppButtonStyle = AppButtonStyle.PRIMARY_AMBER,
    icon: ImageVector? = null,
    isEnabled: Boolean = true,
    height: Dp = 52.dp,
    cornerRadius: Dp = 16.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp),
    fontSize: androidx.compose.ui.unit.TextUnit = 15.sp,
    onClick: () -> Unit
) {
    val activeStyle = if (isEnabled) style else AppButtonStyle.DISABLED
    val isDark = LocalDynamicThemeColors.current.isDark
    val depth = 4.dp
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val view = LocalView.current

    val currentOffset by animateDpAsState(
        targetValue = if (isPressed && isEnabled) depth else 0.dp,
        animationSpec = PhoenixMotion.PressSpringDp,
        label = "appButtonOffset"
    )

    LaunchedEffect(isPressed) {
        if (isPressed && isEnabled) {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = height + depth)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = isEnabled
            ) {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                onClick()
            },
        contentAlignment = Alignment.BottomCenter
    ) {
        val shape = RoundedCornerShape(cornerRadius)

        // 1. 3D Bevel Shadow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .matchParentSize()
                .padding(top = depth)
                .clip(shape)
                .background(activeStyle.getShadowColor(isDark))
        )

        // 2. Top Interactive Surface
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = height)
                .offset(y = currentOffset - depth)
                .clip(shape)
                .background(activeStyle.getFaceBrush(isDark)),
            contentAlignment = Alignment.Center
        ) {
            // Subtle Glossy Top-Rim Shine Highlight
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .align(Alignment.TopCenter)
                    .clip(shape)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.22f), Color.Transparent)
                        )
                    )
            )

            // Button Content (Icon + Typography)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(contentPadding)
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = activeStyle.getTextColor(isDark),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = title.uppercase(),
                    color = activeStyle.getTextColor(isDark),
                    fontSize = fontSize,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = 0.8.sp
                )
            }
        }
    }
}
