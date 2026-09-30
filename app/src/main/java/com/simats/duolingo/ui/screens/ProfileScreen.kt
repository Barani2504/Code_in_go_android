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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.duolingo.data.AppState
import com.simats.duolingo.ui.components.DuolingoButton
import com.simats.duolingo.ui.theme.*

@Composable
fun ProfileScreen(
    onOpenCreateProfile: () -> Unit = {}
) {
    val isLoggedIn = AppState.isLoggedIn
    val userName = AppState.userName.ifEmpty { "Learner" }
    val userHandle = AppState.userHandle.ifEmpty { "learner3454" }
    val currentLang = AppState.selectedLanguage

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DuolingoDarkBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. User Header & Avatar Card
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(DuolingoCardBg)
                    .border(1.5.dp, DuolingoInputBorder, RoundedCornerShape(20.dp))
            ) {
                // Centered Dashed Silhouette Avatar Graphic
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .border(2.5.dp, DuolingoBlue, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoggedIn) {
                            Box(
                                modifier = Modifier
                                    .size(86.dp)
                                    .clip(CircleShape)
                                    .background(Color(165, 96, 232)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = userName.firstOrNull()?.uppercase() ?: "V",
                                    color = Color.White,
                                    fontSize = 38.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Text("👤", fontSize = 54.sp)
                        }
                    }
                }

                // Edit Pencil Button (Top Right)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(DuolingoDarkBg)
                        .border(1.dp, DuolingoInputBorder, CircleShape)
                        .clickable(onClick = onOpenCreateProfile),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✏️", fontSize = 14.sp)
                }
            }

            // Name, Handle, Joined date, Flag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = if (isLoggedIn) userName else "Learner",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = if (isLoggedIn) "@$userHandle" else "@learner",
                        color = DuolingoSubtext,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Joined September 2026",
                        color = DuolingoSubtext,
                        fontSize = 12.5.sp
                    )
                }

                Text(currentLang?.flagEmoji ?: "🐍", fontSize = 28.sp)
            }

            // Following / Followers
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("0 Following", color = DuolingoBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("0 Followers", color = DuolingoBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            // Unauthenticated Profile Creation Banner
            if (!isLoggedIn) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(DuolingoCardBg)
                        .border(1.dp, DuolingoInputBorder, RoundedCornerShape(18.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Create a profile to save your progress!",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    DuolingoButton(
                        text = "CREATE A PROFILE",
                        backgroundColor = DuolingoGreen,
                        shadowColor = DuolingoGreenDark,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onOpenCreateProfile
                    )
                }
            }
        }

        // 2. Statistics Section (2x2 Grid)
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Statistics", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileStatCard("🔥", "Day streak", "${AppState.dayStreak}", modifier = Modifier.weight(1f))
                ProfileStatCard("⚡", "Total XP", "420", modifier = Modifier.weight(1f))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileStatCard("🛡️", "Current league", "Bronze", modifier = Modifier.weight(1f))
                ProfileStatCard("👑", "Top 3 finishes", "0", modifier = Modifier.weight(1f))
            }
        }

        // 3. Achievements Section
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Achievements", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)

            ProfileAchievementRow("🔥", "Wildfire", "Reach a 3 day streak", 1, 3)
            ProfileAchievementRow("🧙‍♂️", "Sage", "Earn 250 XP in total", 420, 250)
            ProfileAchievementRow("🏆", "Champion", "Unlock League leaderboards", 3, 3)
        }

        // 4. Add Friends Section
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Friends", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DuolingoCardBg)
                    .border(1.dp, DuolingoInputBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("👥", fontSize = 28.sp)
                Column(modifier = Modifier.weight(1f)) {
                    Text("Find friends", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text("Search for friends to compete with", color = DuolingoSubtext, fontSize = 12.sp)
                }
                Text("›", color = DuolingoSubtext, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun ProfileStatCard(
    emoji: String,
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(DuolingoCardBg)
            .border(1.dp, DuolingoInputBorder, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(emoji, fontSize = 26.sp)
        Column {
            Text(value, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Black)
            Text(title, color = DuolingoSubtext, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ProfileAchievementRow(
    emoji: String,
    title: String,
    subtitle: String,
    progress: Int,
    total: Int,
) {
    val isCompleted = progress >= total
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DuolingoCardBg)
            .border(1.dp, DuolingoInputBorder, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(if (isCompleted) Color(0xFFFF9600).copy(0.2f) else DuolingoInputBorder),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 24.sp)
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = DuolingoSubtext, fontSize = 12.sp)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(DuolingoInputBorder)
            ) {
                val fraction = (progress.toFloat() / total.toFloat()).coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction)
                        .clip(CircleShape)
                        .background(if (isCompleted) Color(0xFFFF9600) else DuolingoBlue)
                )
            }
        }
    }
}
