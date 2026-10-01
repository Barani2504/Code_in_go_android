package com.simats.codeingo.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier
) {
    val gameManager = GameManager.instance
    val totalXP by gameManager.totalXP.collectAsState()
    val streakDays by gameManager.streakDays.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        // User Avatar & Name
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFA560E8)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "V", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.width(18.dp))

            Column {
                Text(
                    text = "Vishal Rao",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = "Joined September 2026",
                    fontSize = 13.sp,
                    color = SubtextGray
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Statistics Grid
        Text(
            text = "STATISTICS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = SubtextGray,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                emoji = "🔥",
                value = "$streakDays",
                label = "Day Streak",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                emoji = "⭐",
                value = "$totalXP",
                label = "Total XP",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                emoji = "🛡️",
                value = "Silver",
                label = "Current League",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                emoji = "🥇",
                value = "1",
                label = "Top 3 Finishes",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        var selectedProfileTab by remember { mutableStateOf(0) }

        // Tab Selector (Overview vs Achievements)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(CardBackground)
                .border(1.dp, InputBorder, RoundedCornerShape(14.dp))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selectedProfileTab == 0) DuolingoBlue else Color.Transparent)
                    .clickable { selectedProfileTab = 0 }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "OVERVIEW",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = if (selectedProfileTab == 0) Color.White else SubtextGray
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selectedProfileTab == 1) DuolingoBlue else Color.Transparent)
                    .clickable { selectedProfileTab = 1 }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "BADGES & XP",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = if (selectedProfileTab == 1) Color.White else SubtextGray
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (selectedProfileTab == 1) {
            DSAAchievementsView()
        } else {
            // Achievements Section Header with View All
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ACHIEVEMENTS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = SubtextGray,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "VIEW ALL",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = DuolingoBlue,
                    modifier = Modifier.clickable { selectedProfileTab = 1 }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            val achievements = listOf(
                Triple("Wildfire", "Reach a 7 day streak", "🔥"),
                Triple("Sage", "Earn 500 XP in DSA courses", "🧠"),
                Triple("Sharpshooter", "Complete 5 lessons without mistakes", "🎯"),
                Triple("Phoenix Ascendant", "Evolve your Phoenix companion", "🦅")
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                achievements.forEach { (title, desc, badge) ->
                    val shape = RoundedCornerShape(16.dp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(shape)
                            .background(CardBackground)
                            .border(1.dp, InputBorder, shape)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = badge, fontSize = 32.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = desc,
                                fontSize = 12.sp,
                                color = SubtextGray
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))
    }
}

@Composable
private fun StatCard(
    emoji: String,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = modifier
            .clip(shape)
            .background(CardBackground)
            .border(1.dp, InputBorder, shape)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = emoji, fontSize = 24.sp)
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = SubtextGray
            )
        }
    }
}
