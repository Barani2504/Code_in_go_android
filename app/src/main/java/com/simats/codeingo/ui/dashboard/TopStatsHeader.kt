package com.simats.codeingo.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.phoenix.PhoenixDynamicLogoView
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.liquidGlassIsland

/**
 * TopStatsHeader — Floating Liquid Glass Island Navigation & Stats Bar.
 * Exact parity with iOS MainDashboardView.swift topStatsHeader.
 * Features:
 * - Circular side menu trigger button
 * - PhoenixDynamicLogoView (size 28dp) with live emotion aura & tap sheet
 * - Streak pill (Ice 🧊 if pending restore, Flame 🔥 otherwise)
 * - Stars pill (🌟)
 * - XP pill (⚡)
 * - Hearts pill (❤️ with 5-minute regeneration countdown)
 */
@Composable
fun TopStatsHeader(
    streakDays: Int,
    isStreakPendingRestore: Boolean = false,
    savedStreakDays: Int = 1,
    totalStars: Int = 12,
    totalXP: Int = 120,
    heartsCount: Int = 10,
    heartTimerString: String? = null,
    isBossActive: Boolean = false,
    onMenuClick: () -> Unit,
    onPhoenixClick: () -> Unit = {},
    onStreakClick: () -> Unit = {},
    onHeartsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        // Floating Island Container
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .liquidGlassIsland(
                    cornerRadius = 24.dp,
                    glowColor = AmberGold.copy(alpha = if (isDark) 0.18f else 0.08f)
                )
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // 1. Side Menu Hamburger Button
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.04f))
                    .border(
                        1.dp,
                        Brush.linearGradient(
                            if (isDark) listOf(Color.White.copy(alpha = 0.40f), Color.White.copy(alpha = 0.10f))
                            else listOf(Color.Black.copy(alpha = 0.14f), Color.Black.copy(alpha = 0.05f))
                        ),
                        CircleShape
                    )
                    .clickable { onMenuClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Side Menu",
                    tint = if (isDark) Color.White else Color(0xFF182030),
                    modifier = Modifier.size(15.dp)
                )
            }

            // 2. Phoenix Dynamic Emotion Logo (26dp, opens emotion sheet on tap)
            PhoenixDynamicLogoView(
                showTitle = false,
                showSubtitleBadge = false,
                size = 26.dp,
                enableTapSheet = true,
                onTap = onPhoenixClick
            )

            Spacer(modifier = Modifier.weight(1f))

            // Stats Pill Row (Compact & Horizontally Scrollable on ultra-narrow displays)
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val pillBg = if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.03f)

                // 3. Streak Pill
                val streakEmoji = if (isStreakPendingRestore) "🧊" else "🔥"
                val streakVal = if (isStreakPendingRestore) savedStreakDays else streakDays
                val streakColor = if (isStreakPendingRestore) Color(0xFF22D3EE) else (if (isDark) Color(0xFFFF9500) else Color(0xFFE66600))

                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(pillBg)
                        .border(0.9.dp, streakColor.copy(alpha = if (isDark) 0.35f else 0.45f), CircleShape)
                        .clickable { onStreakClick() }
                        .padding(horizontal = 6.dp, vertical = 3.5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.5.dp)
                ) {
                    Text(text = streakEmoji, fontSize = 11.5.sp)
                    Text(
                        text = "$streakVal",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = streakColor
                    )
                }

                // 4. Stars Pill
                val starsColor = if (isDark) Color(0xFFFFD700) else Color(0xFFC78500)
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(pillBg)
                        .border(
                            0.9.dp,
                            if (isBossActive) Color.Red.copy(alpha = 0.6f) else starsColor.copy(alpha = if (isDark) 0.35f else 0.40f),
                            CircleShape
                        )
                        .padding(horizontal = 6.dp, vertical = 3.5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.5.dp)
                ) {
                    Text(text = "🌟", fontSize = 11.sp)
                    Text(
                        text = "$totalStars",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = starsColor
                    )
                }

                // 5. XP Pill
                val xpColor = if (isDark) AmberGold else Color(0xFFC77300)
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(pillBg)
                        .border(0.9.dp, xpColor.copy(alpha = if (isDark) 0.35f else 0.40f), CircleShape)
                        .padding(horizontal = 6.dp, vertical = 3.5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.5.dp)
                ) {
                    Text(text = "⚡", fontSize = 11.sp)
                    Text(
                        text = "$totalXP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = xpColor
                    )
                }

                // 6. Hearts Pill (with countdown if regenerating)
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(pillBg)
                        .border(0.9.dp, Color(0xFFFF4D4D).copy(alpha = if (isDark) 0.35f else 0.45f), CircleShape)
                        .clickable { onHeartsClick() }
                        .padding(horizontal = 6.dp, vertical = 3.5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.5.dp)
                ) {
                    Text(text = "❤️", fontSize = 11.sp)
                    Text(
                        text = "$heartsCount",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFF4D4D)
                    )
                    if (heartTimerString != null) {
                        Text(
                            text = heartTimerString,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFFFF8888) else Color(0xFFE04040)
                        )
                    }
                }
            }
        }
    }
}
