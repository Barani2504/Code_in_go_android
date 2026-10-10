package com.simats.codeingo.ui.leaderboards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.domain.PhoenixEmotionManager
import com.simats.codeingo.ui.components.AppButton
import com.simats.codeingo.ui.components.AppButtonStyle
import com.simats.codeingo.ui.components.AppCard
import com.simats.codeingo.ui.phoenix.PhoenixAtmosphericBackgroundView
import androidx.compose.ui.draw.rotate
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.simats.codeingo.ui.theme.liquidGlassCard
import com.simats.codeingo.ui.theme.liquidGlassPill
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.PhoenixGreen
import com.simats.codeingo.ui.theme.floating
import com.simats.codeingo.ui.theme.pressScale
import com.simats.codeingo.ui.theme.staggeredAppear

// ══════════════════════════════════════════════════════════════════
// 🏆 LeaderboardsScreen — 3D Metallic Podium & League Ranks
// Exact parity with iOS LeaderboardsView.swift
// ══════════════════════════════════════════════════════════════════

private data class RankEntry(
    val rank: Int,
    val name: String,
    val xp: Int,
    val avatar: String,
    val isUser: Boolean = false
)

private val staticRankings = listOf(
    RankEntry(1, "Vikram S.",      540, "🦊"),
    RankEntry(2, "Sarah M.",       480, "🦉"),
    RankEntry(3, "You (Learner)",  420, "👤", isUser = true),
    RankEntry(4, "Alex K.",        360, "🦁"),
    RankEntry(5, "Elena R.",       310, "🦄"),
    RankEntry(6, "Chen W.",        280, "🐼"),
    RankEntry(7, "Priya N.",       220, "🐯"),
    RankEntry(8, "David L.",       180, "🐻")
)

@Composable
fun LeaderboardsScreen(
    completedLessons: Int = 0,
    onStartLesson: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isUnlocked = completedLessons >= 3
    val isDark = LocalDynamicThemeColors.current.isDark

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LocalDynamicThemeColors.current.background)
    ) {
        PhoenixAtmosphericBackgroundView()

        if (!isUnlocked) {
            LockedLeaderboardsView(completedLessons = completedLessons, onStartLesson = onStartLesson)
        } else {
            UnlockedLeaderboardsView()
        }
    }
}

@Composable
private fun LockedLeaderboardsView(
    completedLessons: Int,
    onStartLesson: (() -> Unit)?
) {
    val isDark = LocalDynamicThemeColors.current.isDark
    val remaining = (3 - completedLessons).coerceAtLeast(1)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 3D Metallic Shield Emblem
        Box(
            modifier = Modifier
                .size(100.dp)
                .floating(distanceDp = 6.dp, durationMs = 2200)
                .clip(CircleShape)
                .background(AmberGold.copy(alpha = 0.18f))
                .border(2.dp, AmberGold.copy(alpha = 0.45f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Leaderboards Shield",
                tint = AmberGold,
                modifier = Modifier.size(52.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Unlock Leaderboards!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif,
            color = if (isDark) Color.White else Color(0xFF12_18_26),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Complete $remaining more lesson${if (remaining > 1) "s" else ""} to enter the Bronze League and compete with coders worldwide!",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.SansSerif,
            color = if (isDark) Color.White.copy(alpha = 0.65f) else Color(0xFF64_74_8B),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        if (onStartLesson != null) {
            Spacer(modifier = Modifier.height(28.dp))
            AppButton(
                title = "START NEXT LESSON",
                style = AppButtonStyle.PRIMARY_AMBER,
                onClick = onStartLesson
            )
        }
    }
}

@Composable
private fun UnlockedLeaderboardsView() {
    val isDark = LocalDynamicThemeColors.current.isDark

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // League Header Card
            AppCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .staggeredAppear(0),
                cornerRadius = 24.dp,
                accentGlow = AmberGold.copy(alpha = 0.20f),
                contentPadding = 18.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(AmberGold.copy(alpha = 0.20f))
                            .border(1.5.dp, AmberGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🥉", fontSize = 28.sp)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "BRONZE LEAGUE",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.SansSerif,
                            color = AmberGold,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = "Top 3 advance to Silver League in 3 days!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.SansSerif,
                            color = if (isDark) Color.White.copy(alpha = 0.65f) else Color(0xFF64_74_8B)
                        )
                    }
                }
            }
        }

        item {
            // 3D Top 3 Podium Stage
            PodiumStageView(staticRankings.take(3))
        }

        item {
            // Promotion Zone Divider
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(PhoenixGreen.copy(alpha = 0.4f))
                )
                Text(
                    text = "PROMOTION ZONE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif,
                    color = PhoenixGreen,
                    letterSpacing = 1.sp
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(PhoenixGreen.copy(alpha = 0.4f))
                )
            }
        }

        // Remaining Rank Items (4 to 8)
        itemsIndexed(staticRankings.drop(3)) { index, entry ->
            RankRowCard(entry = entry, index = index + 4)
        }
    }
}

@Composable
private fun PodiumStageView(topThree: List<RankEntry>) {
    if (topThree.size < 3) return
    val first = topThree[0]
    val second = topThree[1]
    val third = topThree[2]
    val isDark = LocalDynamicThemeColors.current.isDark

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .staggeredAppear(1)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        // 2nd Place (Silver)
        PodiumPedestal(entry = second, place = 2, height = 110.dp, color = Color(0xFFC0C0C0), modifier = Modifier.weight(1f))

        // 1st Place (Gold)
        PodiumPedestal(entry = first, place = 1, height = 140.dp, color = AmberGold, modifier = Modifier.weight(1.1f))

        // 3rd Place (Bronze)
        PodiumPedestal(entry = third, place = 3, height = 90.dp, color = Color(0xFFCD7F32), modifier = Modifier.weight(1f))
    }
}

@Composable
private fun PodiumPedestal(
    entry: RankEntry,
    place: Int,
    height: androidx.compose.ui.unit.Dp,
    color: Color,
    modifier: Modifier = Modifier
) {
    val isDark = LocalDynamicThemeColors.current.isDark

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar + Crown
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.25f))
                .border(2.dp, color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = entry.avatar, fontSize = 26.sp)
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = entry.name.substringBefore(" "),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            color = if (isDark) Color.White else Color(0xFF12_18_26),
            maxLines = 1
        )

        Text(
            text = "${entry.xp} XP",
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif,
            color = AmberGold
        )

        Spacer(modifier = Modifier.height(6.dp))

        // 3D Pedestal Block
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(color.copy(alpha = if (isDark) 0.35f else 0.25f), color.copy(alpha = if (isDark) 0.15f else 0.08f))
                    )
                )
                .border(
                    1.5.dp,
                    Brush.verticalGradient(listOf(color, color.copy(alpha = 0.3f))),
                    RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$place",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif,
                color = color
            )
        }
    }
}

@Composable
private fun RankRowCard(entry: RankEntry, index: Int) {
    val isDark = LocalDynamicThemeColors.current.isDark

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .staggeredAppear(index, baseDelayMs = 30)
            .pressScale(targetScale = 0.98f)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (entry.isUser) AmberGold.copy(alpha = if (isDark) 0.18f else 0.12f)
                else (if (isDark) Color(0xFF14_1F_36).copy(alpha = 0.70f) else Color(0xFFF1_F4_FA))
            )
            .border(
                if (entry.isUser) 1.8.dp else 1.dp,
                if (entry.isUser) AmberGold else (if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFD7_DE_EB)),
                RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "${entry.rank}",
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif,
            color = if (entry.isUser) AmberGold else (if (isDark) Color.White.copy(alpha = 0.6f) else Color(0xFF64_74_8B)),
            modifier = Modifier.width(22.dp)
        )

        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(if (isDark) Color.White.copy(alpha = 0.08f) else Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(text = entry.avatar, fontSize = 20.sp)
        }

        Text(
            text = entry.name,
            fontSize = 15.sp,
            fontWeight = if (entry.isUser) FontWeight.Black else FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            color = if (isDark) Color.White else Color(0xFF12_18_26),
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "${entry.xp} XP",
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif,
            color = AmberGold
        )
    }
}
