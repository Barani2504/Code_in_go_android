package com.simats.codeingo.navigation

import android.view.HapticFeedbackConstants
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.data.model.DashboardTab
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.PhoenixMotion
import com.simats.codeingo.ui.theme.pressScale

// ══════════════════════════════════════════════════════════════════
// 📱 BottomNavBar — Floating Liquid Glass Docked Navigation Bar
// Exact parity with iOS MainDashboardView TabView & Liquid Glass System
// ══════════════════════════════════════════════════════════════════

@Composable
fun BottomNavBar(
    selectedTab: DashboardTab,
    onTabSelected: (DashboardTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    val view = LocalView.current

    val coreTabs = listOf(
        DashboardTab.LEARN,
        DashboardTab.VISUALIZER,
        DashboardTab.LEADERBOARDS,
        DashboardTab.PROFILE
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                if (isDark) Color(0xFF07_0D_17).copy(alpha = 0.96f)
                else Color.White.copy(alpha = 0.96f)
            )
            .navigationBarsPadding()
    ) {
        // Top Hairline Specular Glass Stroke
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        if (isDark) listOf(
                            Color.White.copy(alpha = 0.05f),
                            AmberGold.copy(alpha = 0.35f),
                            Color.White.copy(alpha = 0.05f)
                        ) else listOf(
                            Color.Transparent,
                            Color(0xFFD7_DE_EB),
                            Color.Transparent
                        )
                    )
                )
                .align(Alignment.TopCenter)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            coreTabs.forEach { tab ->
                val isSelected = selectedTab == tab
                val interactionSource = remember { MutableInteractionSource() }

                val iconScale by animateFloatAsState(
                    targetValue = if (isSelected) 1.18f else 1.0f,
                    animationSpec = PhoenixMotion.BounceSpring,
                    label = "tabIconScale_${tab.name}"
                )

                val tabColor by animateColorAsState(
                    targetValue = if (isSelected) AmberGold else (if (isDark) Color.White.copy(alpha = 0.50f) else Color(0xFF64_74_8B)),
                    animationSpec = spring(),
                    label = "tabColor_${tab.name}"
                )

                val tabIcon: ImageVector = when (tab) {
                    DashboardTab.LEARN -> Icons.Default.Home
                    DashboardTab.VISUALIZER -> Icons.Default.Build
                    DashboardTab.LEADERBOARDS -> Icons.Default.Leaderboard
                    DashboardTab.PROFILE -> Icons.Default.Person
                    else -> Icons.Default.Home
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .pressScale(targetScale = 0.92f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isSelected) AmberGold.copy(alpha = if (isDark) 0.15f else 0.10f)
                            else Color.Transparent
                        )
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            onTabSelected(tab)
                        }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = tabIcon,
                            contentDescription = tab.name,
                            tint = tabColor,
                            modifier = Modifier
                                .size(24.dp)
                                .scale(iconScale)
                        )
                        Text(
                            text = tab.name,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif,
                            color = tabColor,
                            letterSpacing = 0.4.sp
                        )
                    }
                }
            }
        }
    }
}
