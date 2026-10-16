package com.simats.codeingo.ui.arenas.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.pressScale

/**
 * 🛡️ Robust & Elevated Docked Action Bar Across All 5 Battle Arenas
 * Features:
 * - Guaranteed clearance above Android navigation bar and gesture pill
 * - High-contrast light and dark mode colors
 * - Theory Codex on the left, Replay 🔄 and Skip ⏭ on the right
 */
@Composable
fun ArenaBottomBar(
    onOpenCodex: () -> Unit,
    onResetLevel: () -> Unit,
    onSkipLevel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val barBg = if (isDark) Color(0xFF0D1527).copy(alpha = 0.95f) else Color.White.copy(alpha = 0.96f)
    val barBorder = if (isDark) Color(0xFF1E293B) else Color(0xFFCBD5E1)
    val buttonBg = if (isDark) Color(0xFF1E293B).copy(alpha = 0.8f) else Color(0xFFF1F5F9)
    val buttonBorder = if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1)
    val buttonTextColor = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val skipAccent = if (isDark) AmberGold else Color(0xFFD97706)
    val skipBg = if (isDark) AmberGold.copy(alpha = 0.15f) else Color(0xFFFEF3C7)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(barBg)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        barBorder,
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .navigationBarsPadding()
            .padding(horizontal = 14.dp)
            .padding(top = 8.dp, bottom = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Theory Codex button (Left)
            Box(
                modifier = Modifier
                    .pressScale()
                    .clip(RoundedCornerShape(12.dp))
                    .background(buttonBg)
                    .border(1.dp, buttonBorder, RoundedCornerShape(12.dp))
                    .clickable(onClick = onOpenCodex)
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "📜", fontSize = 13.sp)
                    Text(
                        text = "Theory Codex",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = buttonTextColor
                    )
                }
            }

            // Two Action Buttons (Right): Replay (🔄) + Skip (⏭)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Replay / Reset level
                Box(
                    modifier = Modifier
                        .pressScale()
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(buttonBg)
                        .border(1.dp, buttonBorder, CircleShape)
                        .clickable(onClick = onResetLevel),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restart Level",
                        tint = buttonTextColor,
                        modifier = Modifier.size(17.dp)
                    )
                }

                // Skip to next stage
                Box(
                    modifier = Modifier
                        .pressScale()
                        .clip(RoundedCornerShape(12.dp))
                        .background(skipBg)
                        .border(1.2.dp, skipAccent.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        .clickable(onClick = onSkipLevel)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Skip",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = skipAccent
                        )
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Skip Stage",
                            tint = skipAccent,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
