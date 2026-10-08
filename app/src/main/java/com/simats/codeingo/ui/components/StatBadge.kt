package com.simats.codeingo.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.PhoenixCrimson
import com.simats.codeingo.ui.theme.PhoenixEmber
import com.simats.codeingo.ui.theme.pressScale
import com.simats.codeingo.ui.theme.pulse

// ══════════════════════════════════════════════════════════════════
// 🏆 StatBadge — Interactive Gamified Stat Badges (XP, Streak, Hearts)
// ══════════════════════════════════════════════════════════════════

enum class StatBadgeType {
    XP,
    STREAK,
    HEARTS;

    val icon: ImageVector
        get() = when (this) {
            XP -> Icons.Default.Star
            STREAK -> Icons.Default.LocalFireDepartment
            HEARTS -> Icons.Default.Favorite
        }

    val accentColor: Color
        get() = when (this) {
            XP -> AmberGold
            STREAK -> PhoenixEmber
            HEARTS -> PhoenixCrimson
        }
}

@Composable
fun StatBadge(
    type: StatBadgeType,
    value: String,
    modifier: Modifier = Modifier,
    isLowHeartsWarning: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val isDark = LocalDynamicThemeColors.current.isDark
    val interactionSource = remember { MutableInteractionSource() }

    val bgModifier = if (onClick != null) {
        Modifier
            .pressScale(targetScale = 0.92f)
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
    } else Modifier

    val pulseMod = if (isLowHeartsWarning && type == StatBadgeType.HEARTS) {
        Modifier.pulse(scaleRange = 0.96f..1.06f, durationMs = 800)
    } else Modifier

    Box(
        modifier = modifier
            .then(pulseMod)
            .then(bgModifier)
            .clip(RoundedCornerShape(50))
            .background(
                Brush.linearGradient(
                    if (isDark) listOf(
                        Color(0xFF14_1E_37).copy(alpha = 0.85f),
                        Color(0xFF0F_16_2B).copy(alpha = 0.95f)
                    ) else listOf(
                        Color.White.copy(alpha = 0.95f),
                        Color(0xFFF1_F4_FA).copy(alpha = 0.90f)
                    )
                )
            )
            .padding(horizontal = 10.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = type.icon,
                contentDescription = null,
                tint = type.accentColor,
                modifier = Modifier.size(17.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            AnimatedContent(
                targetState = value,
                transitionSpec = {
                    slideInVertically { it } togetherWith slideOutVertically { -it }
                },
                label = "statBadgeValue"
            ) { targetValue ->
                Text(
                    text = targetValue,
                    color = if (isDark) Color.White else Color(0xFF12_18_26),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif
                )
            }
        }
    }
}
