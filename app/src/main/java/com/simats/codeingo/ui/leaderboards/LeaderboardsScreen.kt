package com.simats.codeingo.ui.leaderboards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DuolingoBlue
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.InputBorder
import com.simats.codeingo.ui.theme.SubtextGray

data class LeaderboardUser(
    val name: String,
    val xp: Int,
    val avatarEmoji: String,
    val isCurrentUser: Boolean = false
)

@Composable
fun LeaderboardsScreen(
    modifier: Modifier = Modifier
) {
    val totalXP by GameManager.instance.totalXP.collectAsState()

    val leaderboardUsers = listOf(
        LeaderboardUser("Alex River", 480, "🦁"),
        LeaderboardUser("Sophia Chen", 390, "🦊"),
        LeaderboardUser("Marcus Vance", 340, "🐼"),
        LeaderboardUser("You (Explorer)", totalXP, "🦅", isCurrentUser = true),
        LeaderboardUser("Priya Sharma", 280, "🐨"),
        LeaderboardUser("Liam Wilson", 210, "🐯"),
        LeaderboardUser("Emma Watson", 160, "🐰"),
        LeaderboardUser("David Kim", 120, "🐻")
    ).sortedByDescending { it.xp }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // League Header Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFA8000).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🛡️", fontSize = 34.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Silver League",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            Text(
                text = "Top 5 advance to the Gold League in 2 days",
                fontSize = 13.sp,
                color = SubtextGray
            )
        }

        // Leaderboard List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp)
        ) {
            itemsIndexed(leaderboardUsers) { index, user ->
                val rank = index + 1
                val isPromoted = rank <= 3
                val shape = RoundedCornerShape(16.dp)

                val bgColor = when {
                    user.isCurrentUser -> DuolingoBlue.copy(alpha = 0.2f)
                    isPromoted -> CardBackground
                    else -> Color(0xFF131F28)
                }

                val borderColor = when {
                    user.isCurrentUser -> DuolingoBlue
                    rank == 1 -> AmberGold
                    rank == 2 -> Color(0xFFC0C0C0)
                    rank == 3 -> Color(0xFFCD7F32)
                    else -> InputBorder
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape)
                        .background(bgColor)
                        .border(1.5.dp, borderColor, shape)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rank badge
                    Text(
                        text = "$rank",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = when (rank) {
                            1 -> AmberGold
                            2 -> Color(0xFFC0C0C0)
                            3 -> Color(0xFFCD7F32)
                            else -> SubtextGray
                        },
                        modifier = Modifier.width(28.dp)
                    )

                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = user.avatarEmoji, fontSize = 22.sp)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Text(
                        text = user.name,
                        fontSize = 15.sp,
                        fontWeight = if (user.isCurrentUser) FontWeight.Black else FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "${user.xp} XP",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = AmberGold
                    )
                }
            }
        }
    }
}
