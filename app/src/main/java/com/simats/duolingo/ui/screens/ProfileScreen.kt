package com.simats.duolingo.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.simats.duolingo.data.AppState
import com.simats.duolingo.ui.components.DuolingoButton
import com.simats.duolingo.ui.theme.*

@Composable
fun ProfileScreen(onOpenCreateProfile: () -> Unit = {}) {
    val isLoggedIn = AppState.isLoggedIn
    val userName   = AppState.userName
    val userHandle = AppState.userHandle

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DuolingoDarkBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // ── User avatar card ─────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(DuolingoCardBg)
                .border(1.5.dp, DuolingoInputBorder, RoundedCornerShape(20.dp))
                .height(180.dp)
        ) {
            // Edit button top-right
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(14.dp)
                    .clip(CircleShape)
                    .background(DuolingoDarkBg)
                    .clickable(onClick = onOpenCreateProfile)
                    .padding(8.dp)
            ) {
                Text("✏️", fontSize = 14.sp)
            }

            // Avatar centre
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .border(2.5.dp, DuolingoBlue, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoggedIn) {
                        Box(
                            modifier = Modifier.size(72.dp).clip(CircleShape).background(Color(0xFFA560E8)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(userName.firstOrNull()?.uppercase() ?: "V", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Text("👤", fontSize = 40.sp)
                    }
                }
            }
        }

        if (isLoggedIn) {
            // Username + handle
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(userName, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                Text("@$userHandle", color = DuolingoSubtext, fontSize = 14.sp)
            }

            // Stats grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                StatCard("🔥", "Day Streak", "${AppState.dayStreak}", modifier = Modifier.weight(1f))
                StatCard("💎", "Gems", "500", modifier = Modifier.weight(1f))
                StatCard("🏆", "Achievements", "3", modifier = Modifier.weight(1f))
            }

            // Achievements section
            Text("Achievements", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AchievementBadge("🔥", "Wildfire", "5 day streak", modifier = Modifier.weight(1f))
                AchievementBadge("📚", "Sage", "10 lessons done", modifier = Modifier.weight(1f))
                AchievementBadge("🏅", "Champion", "Top 10 league", modifier = Modifier.weight(1f))
            }
        } else {
            // Not logged in – create profile CTA
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Create a profile to save your progress!",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
                DuolingoButton(
                    text = "CREATE PROFILE",
                    backgroundColor = DuolingoGreen,
                    shadowColor = DuolingoGreenDark,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onOpenCreateProfile
                )
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun StatCard(emoji: String, label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(DuolingoCardBg)
            .border(1.dp, DuolingoInputBorder, RoundedCornerShape(14.dp))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(emoji, fontSize = 24.sp)
        Text(value, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
        Text(label, color = DuolingoSubtext, fontSize = 11.sp)
    }
}

@Composable
private fun AchievementBadge(emoji: String, title: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(DuolingoCardBg)
            .border(1.dp, DuolingoInputBorder, RoundedCornerShape(14.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(emoji, fontSize = 28.sp)
        Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Text(subtitle, color = DuolingoSubtext, fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}
