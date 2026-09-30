package com.simats.duolingo.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.duolingo.R
import com.simats.duolingo.data.AppState
import com.simats.duolingo.ui.components.DuolingoButton
import com.simats.duolingo.ui.theme.*

private data class LeaderboardUser(
    val rank: Int,
    val name: String,
    val xp: Int,
    val avatar: String,
    val isUser: Boolean
)

@Composable
fun LeaderboardsScreen(
    onStartLesson: () -> Unit = {}
) {
    // Unlocks when at least 3 levels unlocked or can be toggled
    val isUnlocked = AppState.unlockedLevelIndices.size >= 8

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DuolingoDarkBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isUnlocked) {
            // Locked 3-Shield Graphic Header
            LockedHeaderGraphic()

            // Locked Headline & Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Unlock Leaderboards!",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Complete 3 more lessons to start competing",
                    color = DuolingoSubtext,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }

            // "WHAT ARE LEADERBOARDS?" Explanatory Banner Card with DuoBackpack
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(DuolingoCardBg)
                    .border(1.5.dp, DuolingoInputBorder, RoundedCornerShape(18.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "DO LESSONS. EARN XP.",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Earn XP to compete with other learners each week.",
                        color = DuolingoSubtext,
                        fontSize = 12.5.sp,
                        lineHeight = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF9600).copy(0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.duo_backpack),
                        contentDescription = "Mascot",
                        modifier = Modifier.size(52.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            // Locked Skeleton Leaderboard Preview List
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(80, 110, 95, 120, 75, 100).forEachIndexed { idx, widthDp ->
                    val opacity = (6 - idx) * 0.15f
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(DuolingoCardBg.copy(alpha = opacity))
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(DuolingoInputBorder.copy(alpha = opacity))
                        )
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(DuolingoInputBorder.copy(alpha = opacity))
                        )
                        Box(
                            modifier = Modifier
                                .size(width = widthDp.dp, height = 16.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(DuolingoInputBorder.copy(alpha = opacity))
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .size(width = 45.dp, height = 16.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(DuolingoInputBorder.copy(alpha = opacity))
                        )
                    }
                }
            }
        } else {
            // Unlocked League Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.Yellow.copy(0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏆", fontSize = 42.sp)
                }
                Text("Bronze League", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
                Text("Top 20 advance to Silver League", color = DuolingoGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            // Active Rankings List
            val rankings = listOf(
                LeaderboardUser(1, "Vikram S.", 540, "🦊", false),
                LeaderboardUser(2, "Sarah M.", 480, "🦉", false),
                LeaderboardUser(3, "${AppState.userName} (You)", 420, "👤", true),
                LeaderboardUser(4, "Alex K.", 360, "🦁", false),
                LeaderboardUser(5, "Elena R.", 310, "🦄", false),
                LeaderboardUser(6, "Chen W.", 280, "🐼", false),
                LeaderboardUser(7, "Priya N.", 220, "🐯", false),
                LeaderboardUser(8, "David L.", 180, "🐻", false)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rankings.forEach { user ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (user.isUser) DuolingoBlue.copy(alpha = 0.2f) else DuolingoCardBg)
                            .border(
                                width = if (user.isUser) 2.dp else 1.dp,
                                color = if (user.isUser) DuolingoBlue else DuolingoInputBorder,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "#${user.rank}",
                            color = when (user.rank) {
                                1 -> Color(0xFFFFD700)
                                2 -> Color(0xFFC0C0C0)
                                3 -> Color(0xFFCD7F32)
                                else -> DuolingoSubtext
                            },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.width(32.dp)
                        )

                        Text(user.avatar, fontSize = 24.sp)

                        Text(
                            text = user.name,
                            color = if (user.isUser) DuolingoBlue else Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = "${user.xp} XP",
                            color = DuolingoSubtext,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LockedHeaderGraphic() {
    Box(contentAlignment = Alignment.Center) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text("✨", fontSize = 20.sp, modifier = Modifier.offset(y = (-20).dp))
            Text("🌟", fontSize = 16.sp, modifier = Modifier.offset(y = 15.dp))
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy((-14).dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Bronze Shield
            Box(
                modifier = Modifier
                    .size(width = 65.dp, height = 75.dp)
                    .graphicsLayer { rotationZ = -14f }
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFFCC804D), Color(0xFF804D26))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("🛡️", fontSize = 32.sp)
            }

            // Center Gold Shield
            Box(
                modifier = Modifier
                    .size(width = 85.dp, height = 95.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFFFFE066), Color(0xFFF2B300))
                        )
                    )
                    .border(2.dp, Color.White.copy(0.6f), RoundedCornerShape(22.dp))
                    .shadow(8.dp, RoundedCornerShape(22.dp), spotColor = Color.Yellow.copy(0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Text("🔥", fontSize = 40.sp)
            }

            // Right Silver Shield
            Box(
                modifier = Modifier
                    .size(width = 65.dp, height = 75.dp)
                    .graphicsLayer { rotationZ = 14f }
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFFD9D9D9), Color(0xFF8C8C8C))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("🛡️", fontSize = 32.sp)
            }
        }
    }
}
