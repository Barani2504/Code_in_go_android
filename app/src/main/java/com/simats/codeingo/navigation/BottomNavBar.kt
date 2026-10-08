package com.simats.codeingo.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.data.model.DashboardTab
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

/**
 * BottomNavBar — Liquid Glass Docked Tab Bar matching iOS MainDashboardView TabView.
 * Core Tabs:
 * - LEARN (Home)
 * - VISUALIZER (Lab / Tools)
 * - LEADERBOARDS (Ranks)
 * - PROFILE (Developer Profile)
 */
@Composable
fun BottomNavBar(
    selectedTab: DashboardTab,
    onTabSelected: (DashboardTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val coreTabs = listOf(
        DashboardTab.LEARN,
        DashboardTab.VISUALIZER,
        DashboardTab.LEADERBOARDS,
        DashboardTab.PROFILE
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(if (isDark) Color(0xFF0C101A).copy(alpha = 0.94f) else Color.White.copy(alpha = 0.95f))
            .navigationBarsPadding()
    ) {
        // Top hairline glass stroke
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFD7DEEB))
                .align(Alignment.TopCenter)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            coreTabs.forEach { tab ->
                val isSelected = selectedTab == tab
                val unselectedColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                val iconColor by animateColorAsState(
                    targetValue = if (isSelected) Color(0xFF1CB0F6) else unselectedColor,
                    animationSpec = spring(),
                    label = "tabColor"
                )

                val tabIcon: ImageVector = when (tab) {
                    DashboardTab.LEARN -> Icons.Default.Home
                    DashboardTab.VISUALIZER -> Icons.Default.Build
                    DashboardTab.LEADERBOARDS -> Icons.Default.Leaderboard
                    DashboardTab.PROFILE -> Icons.Default.Person
                    else -> Icons.Default.Home
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onTabSelected(tab) }
                        .padding(vertical = 4.dp)
                ) {
                    // Selected active pill glow
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Color(0xFF1CB0F6).copy(alpha = if (isDark) 0.18f else 0.12f) else Color.Transparent)
                    ) {
                        Icon(
                            imageVector = tabIcon,
                            contentDescription = tab.title,
                            tint = iconColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = tab.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                        color = iconColor
                    )
                }
            }
        }
    }
}
