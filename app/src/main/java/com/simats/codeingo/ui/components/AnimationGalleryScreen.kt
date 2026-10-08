package com.simats.codeingo.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.CodeingoTheme
import com.simats.codeingo.ui.theme.DuolingoDarkBg
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.PhoenixGreen
import com.simats.codeingo.ui.theme.bounceOnAppear
import com.simats.codeingo.ui.theme.floating
import com.simats.codeingo.ui.theme.pressScale
import com.simats.codeingo.ui.theme.pulse
import com.simats.codeingo.ui.theme.shake
import com.simats.codeingo.ui.theme.staggeredAppear
import com.simats.codeingo.ui.theme.tilt3D

// ══════════════════════════════════════════════════════════════════
// 🎨 AnimationGalleryScreen — Motion & Design System Showcase
// ══════════════════════════════════════════════════════════════════

@Composable
fun AnimationGalleryScreen() {
    val isDark = LocalDynamicThemeColors.current.isDark
    var selectedChipIndex by remember { mutableIntStateOf(0) }
    var xpCount by remember { mutableIntStateOf(480) }
    var streakCount by remember { mutableIntStateOf(7) }
    var heartsCount by remember { mutableIntStateOf(5) }
    var shakeTrigger by remember { mutableStateOf<Any?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DuolingoDarkBg)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Phoenix Motion & Design Gallery",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif,
            color = if (isDark) Color.White else Color(0xFF12_18_26),
            modifier = Modifier.staggeredAppear(0)
        )

        // 1. Stat Badges with Number Pop Transitions
        AppCard(
            modifier = Modifier
                .fillMaxWidth()
                .staggeredAppear(1)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "1. Stat Badges (Tap to Increment)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = AmberGold
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatBadge(type = StatBadgeType.XP, value = "$xpCount XP", onClick = { xpCount += 25 })
                    StatBadge(type = StatBadgeType.STREAK, value = "$streakCount", onClick = { streakCount += 1 })
                    StatBadge(
                        type = StatBadgeType.HEARTS,
                        value = "$heartsCount",
                        isLowHeartsWarning = heartsCount <= 1,
                        onClick = { heartsCount = if (heartsCount > 1) heartsCount - 1 else 5 }
                    )
                }
            }
        }

        // 2. 3D Pushable Buttons
        AppCard(
            modifier = Modifier
                .fillMaxWidth()
                .staggeredAppear(2)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "2. 3D Tactile Push Buttons",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = AmberGold
                )
                AppButton(
                    title = "Primary Amber Button",
                    style = AppButtonStyle.PRIMARY_AMBER,
                    icon = Icons.Default.Bolt,
                    onClick = { shakeTrigger = Any() }
                )
                AppButton(
                    title = "Success Green Button",
                    style = AppButtonStyle.SUCCESS_GREEN,
                    icon = Icons.Default.Celebration,
                    onClick = { showDialog = true }
                )
                AppButton(
                    title = "Secondary Glass Button",
                    style = AppButtonStyle.SECONDARY_GLASS,
                    onClick = {}
                )
            }
        }

        // 3. Selection Chips / Pills
        AppCard(
            modifier = Modifier
                .fillMaxWidth()
                .staggeredAppear(3)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "3. Liquid Glass Pills",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = AmberGold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("All", "Arrays", "Trees", "Graphs").forEachIndexed { index, name ->
                        AppChip(
                            text = name,
                            isSelected = selectedChipIndex == index,
                            onClick = { selectedChipIndex = index }
                        )
                    }
                }
            }
        }

        // 4. Custom Modifiers: Floating, 3D Tilt, Pulse, Shake
        AppCard(
            modifier = Modifier
                .fillMaxWidth()
                .shake(shakeTrigger)
                .staggeredAppear(4)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "4. Motion Modifiers (Floating, Tilt3D, Pulse)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = AmberGold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppCard(
                        modifier = Modifier
                            .size(80.dp)
                            .floating(distanceDp = 6.dp)
                    ) {
                        Text("Float", modifier = Modifier.align(Alignment.Center), fontWeight = FontWeight.Bold, color = AmberGold)
                    }
                    AppCard(
                        modifier = Modifier
                            .size(80.dp)
                            .pulse(scaleRange = 0.95f..1.08f)
                    ) {
                        Text("Pulse", modifier = Modifier.align(Alignment.Center), fontWeight = FontWeight.Bold, color = PhoenixGreen)
                    }
                    AppCard(
                        modifier = Modifier
                            .size(80.dp)
                            .tilt3D(maxAngle = 14f)
                    ) {
                        Text("3D Tilt", modifier = Modifier.align(Alignment.Center), fontWeight = FontWeight.Bold, color = Color(0xFF1C_B0_F6))
                    }
                }
            }
        }

        // 5. Skeleton Loaders
        AppCard(
            modifier = Modifier
                .fillMaxWidth()
                .staggeredAppear(5)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "5. Shimmer Skeleton Placeholders",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = AmberGold
                )
                SkeletonBox(modifier = Modifier.fillMaxWidth().height(18.dp))
                SkeletonBox(modifier = Modifier.fillMaxWidth(0.6f).height(14.dp))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

    if (showDialog) {
        AppDialog(onDismissRequest = { showDialog = false }) {
            Text(
                text = "Phoenix Bird Life",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif,
                color = AmberGold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Spring dialog transitions seamlessly with frosted glass backdrop and specular amber glow.",
                fontSize = 14.sp,
                color = if (isDark) Color.White else Color(0xFF12_18_26)
            )
            Spacer(modifier = Modifier.height(18.dp))
            AppButton(
                title = "CONTINUE",
                style = AppButtonStyle.PRIMARY_AMBER,
                onClick = { showDialog = false }
            )
        }
    }
}

@Preview(name = "Light Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewAnimationGallery() {
    CodeingoTheme {
        AnimationGalleryScreen()
    }
}
