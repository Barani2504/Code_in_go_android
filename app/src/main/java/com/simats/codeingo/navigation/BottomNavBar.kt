package com.simats.codeingo.navigation

import android.view.HapticFeedbackConstants
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.data.model.DashboardTab
import com.simats.codeingo.ui.theme.CodeingoTheme
import com.simats.codeingo.ui.theme.DuolingoBlue
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.PhoenixMotion
import com.simats.codeingo.ui.theme.pressScale

// ══════════════════════════════════════════════════════════════════
// 📱 BottomNavBar — Frosted Docked 5-Tab Navigation Bar
// Exact 1:1 Parity with iOS MainDashboardView.swift TabView
// 1. Learn (house.fill)
// 2. Visualizer (wrench.and.screwdriver.fill)
// 3. Leaderboards (building.2.fill)
// 4. Shop (bag.fill)
// 5. Profile (person.fill)
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
        DashboardTab.SHOP,
        DashboardTab.PROFILE
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                if (isDark) Color(0xFF0D121F).copy(alpha = 0.95f)
                else Color(0xFFFAFAFC).copy(alpha = 0.96f)
            )
            .navigationBarsPadding()
    ) {
        // Specular Top Hairline Stroke matching iOS .overlay(Rectangle().frame(height: 0.5)...)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        if (isDark) listOf(
                            Color.White.copy(alpha = 0.04f),
                            Color(0xFF1CB0F6).copy(alpha = 0.30f),
                            Color.White.copy(alpha = 0.04f)
                        ) else listOf(
                            Color.Transparent,
                            Color(0xFFD7DEEB),
                            Color.Transparent
                        )
                    )
                )
                .align(Alignment.TopCenter)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            coreTabs.forEach { tab ->
                val isSelected = selectedTab == tab
                val interactionSource = remember { MutableInteractionSource() }

                val iconScale by animateFloatAsState(
                    targetValue = if (isSelected) 1.15f else 1.0f,
                    animationSpec = PhoenixMotion.BounceSpring,
                    label = "tabIconScale_${tab.name}"
                )

                val tabColor by animateColorAsState(
                    targetValue = if (isSelected) DuolingoBlue
                    else (if (isDark) Color(0xFF94A3B8) else Color(0xFF738099)),
                    animationSpec = spring(),
                    label = "tabColor_${tab.name}"
                )

                val tabIcon: ImageVector = when (tab) {
                    DashboardTab.LEARN -> Icons.Default.Home
                    DashboardTab.VISUALIZER -> Icons.Default.Build
                    DashboardTab.LEADERBOARDS -> Icons.Default.Leaderboard
                    DashboardTab.SHOP -> Icons.Default.ShoppingBag
                    DashboardTab.PROFILE -> Icons.Default.Person
                    else -> Icons.Default.Home
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .pressScale(targetScale = 0.93f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isSelected) DuolingoBlue.copy(alpha = if (isDark) 0.16f else 0.12f)
                            else Color.Transparent
                        )
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            onTabSelected(tab)
                        }
                        .padding(vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = tabIcon,
                            contentDescription = tab.title,
                            tint = tabColor,
                            modifier = Modifier
                                .size(23.dp)
                                .scale(iconScale)
                        )
                        Text(
                            text = tab.title,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif,
                            color = tabColor,
                            letterSpacing = 0.2.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun BottomNavBarPreview() {
    CodeingoTheme {
        BottomNavBar(
            selectedTab = DashboardTab.LEARN,
            onTabSelected = {}
        )
    }
}
