package com.simats.codeingo.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.liquidGlassPill
import com.simats.codeingo.ui.theme.pressScale

// ══════════════════════════════════════════════════════════════════
// 🏷️ AppChip / Liquid Glass Pill
// ══════════════════════════════════════════════════════════════════

@Composable
fun AppChip(
    text: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    accentColor: Color = AmberGold,
    icon: ImageVector? = null,
    onClick: () -> Unit
) {
    val isDark = LocalDynamicThemeColors.current.isDark
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .pressScale(targetScale = 0.94f)
            .liquidGlassPill(isSelected = isSelected, accentColor = accentColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) accentColor else (if (isDark) Color.White.copy(alpha = 0.8f) else Color(0xFF4A_55_68)),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                color = if (isSelected) accentColor else (if (isDark) Color.White else Color(0xFF1A_20_2C)),
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}
