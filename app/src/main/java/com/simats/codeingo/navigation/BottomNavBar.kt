package com.simats.codeingo.navigation

import android.view.HapticFeedbackConstants
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.data.model.DashboardTab
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.CodeingoTheme
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.PhoenixMotion
import com.simats.codeingo.ui.theme.pressScale
import kotlin.math.abs

// ══════════════════════════════════════════════════════════════════
// 📱 LiquidFloatBottomNavBar — High-Level Liquid Floating Tab Bar
// Features:
// 1. Floating Capsule Geometry (hovering above bottom navigation insets)
// 2. Translucent Liquid Glassmorphism with dual-tone specular perimeter rim
// 3. Morphing Liquid Indicator Pill with fluid viscoelastic horizontal stretch
// 4. Ambient Radial Glow drawn dynamically beneath active tab
// 5. Active Icon Liquid Spring Lift & Elevation Wobble
// 6. Instagram-Level Silky Responsive Micro-Interactions & Haptics
// ══════════════════════════════════════════════════════════════════

@Composable
fun BottomNavBar(
    selectedTab: DashboardTab,
    onTabSelected: (DashboardTab) -> Unit,
    modifier: Modifier = Modifier,
    swipePosition: Float? = null
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

    val selectedIndex = coreTabs.indexOf(selectedTab).coerceAtLeast(0)
    val targetIndex = swipePosition?.coerceIn(0f, (coreTabs.size - 1).toFloat()) ?: selectedIndex.toFloat()

    // Animated float index for smooth liquid interpolation
    val animatedIndex by animateFloatAsState(
        targetValue = targetIndex,
        animationSpec = spring(
            dampingRatio = 0.72f,
            stiffness = 340f
        ),
        label = "liquidTabIndex"
    )

    // Dynamic Viscoelastic Stretch: elongates horizontally during motion
    val delta = targetIndex - animatedIndex
    val stretchX = 1f + (abs(delta) * 0.40f).coerceIn(0f, 0.42f)
    val squashY = 1f - (abs(delta) * 0.20f).coerceIn(0f, 0.22f)

    val capsuleShape = RoundedCornerShape(32.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        // 1. Outer Translucent Liquid Glass Floating Capsule
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .shadow(
                    elevation = 18.dp,
                    shape = capsuleShape,
                    spotColor = if (isDark) Color(0x77000000) else Color(0x33475569)
                )
                .clip(capsuleShape)
                .background(
                    if (isDark) {
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF131D33).copy(alpha = 0.88f),
                                Color(0xFF090E1B).copy(alpha = 0.94f)
                            )
                        )
                    } else {
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.92f),
                                Color(0xFFEFF3F8).copy(alpha = 0.96f)
                            )
                        )
                    }
                )
                .border(
                    width = 1.2.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = if (isDark) 0.24f else 0.65f),
                            Color.White.copy(alpha = if (isDark) 0.05f else 0.18f)
                        )
                    ),
                    shape = capsuleShape
                )
        ) {
            // Specular Top-Rim Sheen Arc
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.horizontalGradient(
                            if (isDark) listOf(
                                Color.Transparent,
                                Color(0xFFFF9E44).copy(alpha = 0.45f),
                                Color.White.copy(alpha = 0.4f),
                                Color(0xFFFF9E44).copy(alpha = 0.45f),
                                Color.Transparent
                            ) else listOf(
                                Color.Transparent,
                                Color(0xFFFF7A00).copy(alpha = 0.35f),
                                Color.White.copy(alpha = 0.6f),
                                Color(0xFFFF7A00).copy(alpha = 0.35f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // 2. Sliding Liquid Morphing Pill & Radial Glow
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp, vertical = 6.dp)
            ) {
                val tabCount = coreTabs.size
                val tabWidth = maxWidth / tabCount
                val pillWidth = tabWidth - 6.dp
                val pillLeft = tabWidth * animatedIndex + 3.dp

                // Ambient Radial Liquid Glow beneath the active pill
                Canvas(
                    modifier = Modifier
                        .offset(x = pillLeft)
                        .width(pillWidth)
                        .fillMaxHeight()
                        .graphicsLayer {
                            scaleX = stretchX * 1.35f
                            scaleY = squashY * 1.45f
                            alpha = 0.55f
                        }
                ) {
                    val centerOffset = Offset(size.width / 2f, size.height / 2f)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFF6E0F).copy(alpha = 0.60f),
                                Color(0xFFFF4010).copy(alpha = 0.25f),
                                Color.Transparent
                            ),
                            center = centerOffset,
                            radius = size.width * 0.75f
                        ),
                        radius = size.width * 0.75f,
                        center = centerOffset
                    )
                }

                // Liquid Morphing Pill Indicator
                Box(
                    modifier = Modifier
                        .offset(x = pillLeft)
                        .width(pillWidth)
                        .fillMaxHeight()
                        .graphicsLayer {
                            scaleX = stretchX
                            scaleY = squashY
                        }
                        .clip(RoundedCornerShape(26.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    AmberGold,
                                    Color(0xFFFF5722),
                                    Color(0xFFFF3D00)
                                )
                            )
                        )
                        .drawBehind {
                            // Glossy top-half shine on the liquid pill
                            drawRoundRect(
                                color = Color.White.copy(alpha = 0.34f),
                                topLeft = Offset(4.dp.toPx(), 2.dp.toPx()),
                                size = Size(size.width - 8.dp.toPx(), size.height * 0.40f),
                                cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx())
                            )
                        }
                )

                // 3. Tab Items Row with Dynamic Elevations & Micro-Animations
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    coreTabs.forEachIndexed { index, tab ->
                        val isSelected = if (swipePosition != null) {
                            abs(animatedIndex - index) < 0.5f
                        } else {
                            selectedTab == tab
                        }
                        val interactionSource = remember { MutableInteractionSource() }

                        // Animated Icon Scale & Spring Elevation Lift
                        val iconScale by animateFloatAsState(
                            targetValue = if (isSelected) 1.20f else 1.0f,
                            animationSpec = spring(
                                dampingRatio = 0.55f,
                                stiffness = 420f
                            ),
                            label = "tabIconScale_${tab.name}"
                        )

                        val iconOffsetY by animateDpAsState(
                            targetValue = if (isSelected) (-2.5).dp else 0.dp,
                            animationSpec = spring(
                                dampingRatio = 0.6f,
                                stiffness = 400f
                            ),
                            label = "tabIconOffset_${tab.name}"
                        )

                        val contentColor by animateColorAsState(
                            targetValue = if (isSelected) Color.White
                            else if (isDark) Color(0xFF94A3B8)
                            else Color(0xFF64748B),
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
                                .fillMaxHeight()
                                .pressScale(targetScale = 0.92f)
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null
                                ) {
                                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                    onTabSelected(tab)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                                modifier = Modifier.offset(y = iconOffsetY)
                            ) {
                                Icon(
                                    imageVector = tabIcon,
                                    contentDescription = tab.title,
                                    tint = contentColor,
                                    modifier = Modifier
                                        .size(23.dp)
                                        .scale(iconScale)
                                )
                                Text(
                                    text = tab.title,
                                    fontSize = 10.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                    fontFamily = FontFamily.SansSerif,
                                    color = contentColor,
                                    letterSpacing = 0.2.sp,
                                    maxLines = 1
                                )
                            }
                        }
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
