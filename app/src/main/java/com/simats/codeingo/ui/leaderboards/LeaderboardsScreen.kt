package com.simats.codeingo.ui.leaderboards

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
import com.simats.codeingo.ui.phoenix.PhoenixAtmosphericBackgroundView
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DsaBlue
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.InputBorder
import com.simats.codeingo.ui.theme.SubtextGray

data class LeaderboardUser(
    val name: String,
    val xp: Int,
    val avatarEmoji: String,
    val rankDelta: String = "=",
    val isCurrentUser: Boolean = false
)

@Composable
fun LeaderboardsScreen(
    modifier: Modifier = Modifier
) {
    val totalXP by GameManager.instance.totalXP.collectAsState()

    val leaderboardUsers = listOf(
        LeaderboardUser("Alex River", 540, "🦁", "▲ +2"),
        LeaderboardUser("Sophia Chen", 480, "🦊", "▲ +1"),
        LeaderboardUser("Marcus Vance", 420, "🐼", "="),
        LeaderboardUser("You (Explorer)", maxOf(380, totalXP), "🦅", "▲ +3", isCurrentUser = true),
        LeaderboardUser("Priya Sharma", 350, "🐨", "▼ -1"),
        LeaderboardUser("Liam Wilson", 290, "🐯", "="),
        LeaderboardUser("Emma Watson", 230, "🐰", "▲ +1"),
        LeaderboardUser("David Kim", 180, "🐻", "▼ -2"),
        LeaderboardUser("Zara Patel", 140, "🦉", "="),
        LeaderboardUser("Lucas Scott", 110, "🐺", "▼ -1")
    ).sortedByDescending { it.xp }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        PhoenixAtmosphericBackgroundView()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
        ) {
            // 1. 3-Shield Graphic Header
            item {
                Locked3ShieldHeaderGraphic()
            }

            // 2. League Header Card
            item {
                LeagueHeaderCard()
            }

            // 3. What are Leaderboards Guide Card
            item {
                WhatAreLeaderboardsCard()
            }

            // 4. Promotion Zone Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(DuolingoGreen.copy(alpha = 0.5f))
                    )
                    Text(
                        text = "PROMOTION ZONE (TOP 3 ADVANCE)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = DuolingoGreen
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(DuolingoGreen.copy(alpha = 0.5f))
                    )
                }
            }

            // 5. Active Rankings List
            itemsIndexed(leaderboardUsers) { index, user ->
                val rank = index + 1
                LeaderboardUserRow(rank = rank, user = user)
            }
        }
    }
}

// ──────────────────────────────────────────────
// 3-Shield Graphic Header (Exact parity with iOS)
// ──────────────────────────────────────────────
@Composable
private fun Locked3ShieldHeaderGraphic() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        contentAlignment = Alignment.Center
    ) {
        // Ambient glow
        Box(
            modifier = Modifier
                .size(160.dp, 70.dp)
                .background(Brush.radialGradient(listOf(AmberGold.copy(alpha = 0.25f), Color.Transparent)))
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Left Bronze Shield
            Box(
                modifier = Modifier
                    .offset(x = 12.dp, y = 8.dp)
                    .size(60.dp, 72.dp)
                    .rotate(-14f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFFCD7F32), Color(0xFF8B4513))
                        )
                    )
                    .shadow(6.dp, RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(28.dp)
                )
            }

            // Center Phoenix Gold Shield
            Box(
                modifier = Modifier
                    .size(80.dp, 92.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(AmberGold, Color(0xFFFA8000))
                        )
                    )
                    .border(2.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
                    .shadow(12.dp, RoundedCornerShape(22.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(38.dp)
                )
            }

            // Right Silver Shield
            Box(
                modifier = Modifier
                    .offset(x = (-12).dp, y = 8.dp)
                    .size(60.dp, 72.dp)
                    .rotate(14f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFFE0E0E0), Color(0xFF9E9E9E))
                        )
                    )
                    .shadow(6.dp, RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun LeagueHeaderCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardBackground.copy(alpha = 0.85f))
            .border(1.5.dp, AmberGold.copy(alpha = 0.45f), RoundedCornerShape(20.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = AmberGold,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "OBSIDIAN LEAGUE",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        }

        Text(
            text = "Top 3 advance to the Sunstone League in 2 days",
            fontSize = 12.sp,
            color = SubtextGray,
            textAlign = TextAlign.Center
        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(AmberGold.copy(alpha = 0.15f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = "⏳ Season Ends in 2d 14h",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AmberGold
            )
        }
    }
}

@Composable
private fun WhatAreLeaderboardsCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackground.copy(alpha = 0.65f))
            .border(1.dp, InputBorder, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "LEADERBOARD RULES",
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = AmberGold
        )
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "⚡", fontSize = 12.sp)
            Text(text = "Complete lessons and practice workouts to earn XP", fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "👑", fontSize = 12.sp)
            Text(text = "Finish in Top 3 to advance to the next higher league", fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "💎", fontSize = 12.sp)
            Text(text = "Earn bonus gems and unlock rare companion evolutions", fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
        }
    }
}

@Composable
private fun LeaderboardUserRow(rank: Int, user: LeaderboardUser) {
    val isPromoted = rank <= 3
    val shape = RoundedCornerShape(16.dp)

    val bgColor = when {
        user.isCurrentUser -> AmberGold.copy(alpha = 0.15f)
        isPromoted -> CardBackground.copy(alpha = 0.9f)
        else -> Color(0xFF101824).copy(alpha = 0.75f)
    }

    val borderColor = when {
        user.isCurrentUser -> AmberGold
        rank == 1 -> AmberGold
        rank == 2 -> Color(0xFFE0E0E0)
        rank == 3 -> Color(0xFFCD7F32)
        else -> InputBorder
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(bgColor)
            .border(if (user.isCurrentUser || isPromoted) 1.5.dp else 1.dp, borderColor, shape)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank Badge
        Box(
            modifier = Modifier.width(32.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            when (rank) {
                1 -> Text(text = "🥇", fontSize = 18.sp)
                2 -> Text(text = "🥈", fontSize = 18.sp)
                3 -> Text(text = "🥉", fontSize = 18.sp)
                else -> Text(
                    text = "$rank",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = SubtextGray
                )
            }
        }

        // Avatar
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = user.avatarEmoji, fontSize = 22.sp)
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Name & Delta
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.name,
                fontSize = 14.sp,
                fontWeight = if (user.isCurrentUser) FontWeight.Black else FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = user.rankDelta,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = when {
                    user.rankDelta.startsWith("▲") -> DuolingoGreen
                    user.rankDelta.startsWith("▼") -> DuolingoRed
                    else -> SubtextGray
                }
            )
        }

        // XP Pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(AmberGold.copy(alpha = 0.18f))
                .border(1.dp, AmberGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = AmberGold,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = "${user.xp} XP",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = AmberGold
                )
            }
        }
    }
}
