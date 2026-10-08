package com.simats.codeingo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.floating
import com.simats.codeingo.ui.theme.shimmer

// ══════════════════════════════════════════════════════════════════
// 🌌 EmptyState — Glassmorphic Empty State View
// ══════════════════════════════════════════════════════════════════

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    actionButtonTitle: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    val isDark = LocalDynamicThemeColors.current.isDark

    AppCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        accentGlow = AmberGold.copy(alpha = 0.12f),
        contentPadding = 24.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .floating(distanceDp = 5.dp, durationMs = 2000)
                    .clip(CircleShape)
                    .background(AmberGold.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AmberGold,
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif,
                color = if (isDark) Color.White else Color(0xFF1A_20_2C),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = subtitle,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.SansSerif,
                color = if (isDark) Color.White.copy(alpha = 0.60f) else Color(0xFF71_80_96),
                textAlign = TextAlign.Center
            )

            if (actionButtonTitle != null && onActionClick != null) {
                Spacer(modifier = Modifier.height(18.dp))
                AppButton(
                    title = actionButtonTitle,
                    style = AppButtonStyle.PRIMARY_AMBER,
                    height = 44.dp,
                    onClick = onActionClick
                )
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// ⚡ SkeletonLoader — Shimmering Placeholder Shapes
// ══════════════════════════════════════════════════════════════════

@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 12.dp
) {
    val isDark = LocalDynamicThemeColors.current.isDark
    val baseColor = if (isDark) Color(0xFF15_1E_33) else Color(0xFFE2_E8_F0)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(baseColor)
            .shimmer(
                shimmerColor = if (isDark) Color.White.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.65f)
            )
    )
}
