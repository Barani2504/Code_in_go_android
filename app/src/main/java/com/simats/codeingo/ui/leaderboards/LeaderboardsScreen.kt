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
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.domain.PhoenixEmotionManager
import com.simats.codeingo.ui.phoenix.PhoenixAtmosphericBackgroundView
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.InputBorder
import com.simats.codeingo.ui.theme.SubtextGray

// ══════════════════════════════════════════════════════════════════
// 🏆  LeaderboardsScreen — Full iOS LeaderboardsView.swift parity
// Locked view (< 3 lessons): 3-shield graphic, headline, info card,
//   skeleton preview list
// Unlocked view (>= 3 lessons): Bronze League header + active rankings
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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F17))
    ) {
        // Phoenix atmospheric background
        PhoenixAtmosphericBackgroundView()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 60.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (!isUnlocked) {
                // ── LOCKED STATE ─────────────────────────────────────
                item { LockedHeaderGraphic() }
                item {
                    LockedHeadlineAndButton(
                        completedLessons = completedLessons,
                        onStartLesson = onStartLesson
                    )
                }
                item { WhatAreLeaderboardsCard() }
                item { LockedSkeletonPreviewList() }
            } else {
                // ── UNLOCKED STATE ────────────────────────────────────
                item { UnlockedLeagueHeader() }
                item { ActiveRankingsList() }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// 1. Locked 3-Shield Graphic Header
// ══════════════════════════════════════════════════════════════════
@Composable
private fun LockedHeaderGraphic() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(top = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        // Ambient glow ellipse
        Box(
            modifier = Modifier
                .size(180.dp, 80.dp)
                .background(
                    Brush.radialGradient(
                        listOf(AmberGold.copy(alpha = 0.18f), Color.Transparent)
                    )
                )
                .blur(20.dp)
        )

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Bronze Shield
            Box(
                modifier = Modifier
                    .offset(x = 14.dp, y = 8.dp)
                    .size(65.dp, 75.dp)
                    .rotate(-14f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFFCC7333), Color(0xFF7A3B10))
                        )
                    )
                    .shadow(6.dp, RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Shield, null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(30.dp)
                )
            }

            // Center Phoenix Gold Shield (zIndex front)
            Box(
                modifier = Modifier
                    .size(85.dp, 95.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.linearGradient(listOf(AmberGold, Color(0xFFF2A000)))
                    )
                    .border(2.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
                    .shadow(12.dp, RoundedCornerShape(22.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.LocalFireDepartment, null,
                    tint = Color.White,
                    modifier = Modifier.size(38.dp)
                )
            }

            // Right Silver Shield
            Box(
                modifier = Modifier
                    .offset(x = (-14).dp, y = 8.dp)
                    .size(65.dp, 75.dp)
                    .rotate(14f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFFC8C8C8), Color(0xFF7F7F7F))
                        )
                    )
                    .shadow(6.dp, RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Shield, null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// 2. Locked Headline, Subtitle & Start Button
// ══════════════════════════════════════════════════════════════════
@Composable
private fun LockedHeadlineAndButton(
    completedLessons: Int,
    onStartLesson: (() -> Unit)?
) {
    val lessonsNeeded = maxOf(1, 3 - completedLessons)
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Unlock Leaderboards",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Complete $lessonsNeeded more lesson${if (lessonsNeeded > 1) "s" else ""} to start competing!",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.55f),
            textAlign = TextAlign.Center
        )
        Button(
            onClick = { onStartLesson?.invoke() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFE55A0C), Color(0xFFB23205))
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "START A LESSON",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// 3. "WHAT ARE LEADERBOARDS?" Info Card
// ══════════════════════════════════════════════════════════════════
@Composable
private fun WhatAreLeaderboardsCard() {
    val emotion by PhoenixEmotionManager.instance.currentEmotion.collectAsState()
    val infiniteTransition = rememberInfiniteTransition(label = "mascotBob")
    val bobOffset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -5f,
        animationSpec = infiniteRepeatable(
            tween(1800, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ), label = "bob"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardBackground.copy(alpha = 0.75f))
            .border(1.5.dp, AmberGold.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    Icons.Default.LocalFireDepartment, null,
                    tint = AmberGold,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    "WHAT ARE LEADERBOARDS?",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = AmberGold
                )
            }
            Text(
                "Do lessons, earn XP",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Text(
                "Earn XP by completing lessons and compete with learners around the world each week.",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.65f),
                lineHeight = 17.sp
            )
        }

        // Phoenix emotion mascot with bobbing animation
        Box(
            modifier = Modifier
                .offset(y = bobOffset.dp)
                .size(64.dp),
            contentAlignment = Alignment.Center
        ) {
            // Aura glow
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        Brush.radialGradient(listOf(AmberGold.copy(alpha = 0.3f), Color.Transparent))
                    )
                    .blur(6.dp)
            )
            Text(emotion.emoji, fontSize = 40.sp)
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// 4. Locked Skeleton Preview List (fade-out shimmer rows)
// ══════════════════════════════════════════════════════════════════
@Composable
private fun LockedSkeletonPreviewList() {
    val namePillWidths = listOf(80.dp, 110.dp, 95.dp, 120.dp, 75.dp, 100.dp)
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        repeat(6) { idx ->
            val opacity = (6 - idx) * 0.18f
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = opacity * 0.3f))
                    .border(1.dp, Color.White.copy(alpha = opacity * 0.5f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Rank dot
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                )
                // Avatar circle
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.10f))
                )
                // Name pill
                Box(
                    modifier = Modifier
                        .height(14.dp)
                        .width(namePillWidths[idx % 6])
                        .clip(RoundedCornerShape(7.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                )
                Spacer(modifier = Modifier.weight(1f))
                // XP pill
                Box(
                    modifier = Modifier
                        .height(14.dp)
                        .width(45.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(Color.White.copy(alpha = 0.10f))
                )
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// 5. Unlocked — League Header (Bronze League iOS style)
// ══════════════════════════════════════════════════════════════════
@Composable
private fun UnlockedLeagueHeader() {
    val infiniteTransition = rememberInfiniteTransition(label = "trophyGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f, targetValue = 0.65f,
        animationSpec = infiniteRepeatable(tween(1600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glowAlpha"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Trophy badge with glow
        Box(contentAlignment = Alignment.Center) {
            // Ambient glow
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(AmberGold.copy(alpha = glowAlpha * 0.5f), Color.Transparent)
                        )
                    )
                    .blur(12.dp)
            )
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(AmberGold, Color(0xFFFF8C00)))
                    )
                    .shadow(10.dp, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.EmojiEvents, null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Text(
            "Bronze League",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )

        // Top 20% advance pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(AmberGold.copy(alpha = 0.18f))
                .border(1.dp, AmberGold.copy(alpha = 0.4f), RoundedCornerShape(50.dp))
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(
                "TOP 20% ADVANCE TO SILVER",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold
            )
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// 6. Active Rankings List
// ══════════════════════════════════════════════════════════════════
@Composable
private fun ActiveRankingsList() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        staticRankings.forEach { item ->
            RankRowView(entry = item)
        }
    }
}

@Composable
private fun RankRowView(entry: RankEntry) {
    val isTop3 = entry.rank <= 3
    val bgColor = when {
        entry.isUser -> AmberGold.copy(alpha = 0.15f)
        isTop3 -> CardBackground.copy(alpha = 0.88f)
        else -> Color(0xFF101824).copy(alpha = 0.75f)
    }
    val borderColor = when {
        entry.isUser -> AmberGold
        entry.rank == 1 -> AmberGold
        entry.rank == 2 -> Color(0xFFDDDDDD)
        entry.rank == 3 -> Color(0xFFCD7F32)
        else -> InputBorder
    }
    val nameColor = if (entry.isUser) AmberGold else Color.White
    val xpColor = if (isTop3) AmberGold else SubtextGray
    val xpBg = if (isTop3) AmberGold.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.07f)
    val xpBorder = if (isTop3) AmberGold.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.10f)
    val avatarBg = if (entry.isUser) AmberGold.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.08f)
    val avatarBorder = if (entry.isUser) AmberGold.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.12f)
    val shape = RoundedCornerShape(16.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(bgColor)
            .border(if (entry.isUser || isTop3) 1.5.dp else 1.dp, borderColor, shape)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Rank badge
        Box(modifier = Modifier.width(28.dp), contentAlignment = Alignment.Center) {
            when (entry.rank) {
                1 -> Text("🥇", fontSize = 20.sp)
                2 -> Text("🥈", fontSize = 20.sp)
                3 -> Text("🥉", fontSize = 20.sp)
                else -> Text(
                    "${entry.rank}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White.copy(alpha = 0.45f)
                )
            }
        }

        // Avatar circle
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(avatarBg)
                .border(if (entry.isUser) 2.dp else 1.dp, avatarBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(entry.avatar, fontSize = 22.sp)
        }

        // Name
        Text(
            text = entry.name,
            fontSize = 16.sp,
            fontWeight = if (entry.isUser) FontWeight.Black else FontWeight.Bold,
            color = nameColor,
            modifier = Modifier.weight(1f)
        )

        // XP badge
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(xpBg)
                .border(1.dp, xpBorder, RoundedCornerShape(20.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                "${entry.xp} XP",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = xpColor,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
