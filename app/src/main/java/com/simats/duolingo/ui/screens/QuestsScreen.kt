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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.duolingo.R
import com.simats.duolingo.ui.theme.*

@Composable
fun QuestsScreen(onStartLesson: () -> Unit = {}) {
    var earnedXP by remember { mutableIntStateOf(0) }

    val isQuest1Completed = earnedXP >= 10
    val isQuest2Unlocked = earnedXP >= 10
    val isQuest2Completed = earnedXP >= 25
    val isQuest3Unlocked = earnedXP >= 25

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DuolingoDarkBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Purple Hero Welcome Banner with Duo Mascot & Chest
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.linearGradient(
                        listOf(Color(133, 84, 224), Color(107, 56, 199))
                    )
                )
                .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = Color.Magenta.copy(0.3f))
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Welcome to Quests!",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Complete quests each day to earn rewards and build your streak.",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp,
                        lineHeight = 17.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF9600).copy(0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.duo_backpack),
                        contentDescription = "Mascot",
                        modifier = Modifier.size(60.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }

        // 2. Daily Quests Section Header with Timer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Daily Quests", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(DuolingoCardBg)
                    .border(1.dp, DuolingoInputBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("⏱️", fontSize = 12.sp)
                Text("14 HOURS", color = Color(0xFFFF9600), fontSize = 11.sp, fontWeight = FontWeight.Black)
            }
        }

        // 3. Quest 1: Earn 10 XP
        QuestRowCard(
            icon = "⚡",
            iconBg = Color(0xFFFF9600),
            title = "Earn 10 XP today",
            progress = (earnedXP.coerceAtMost(10)),
            maxProgress = 10,
            rewardText = "Chest",
            isCompleted = isQuest1Completed,
            isLocked = false
        )

        // 4. Quest 2: Spend 15 minutes learning
        QuestRowCard(
            icon = "⏳",
            iconBg = DuolingoBlue,
            title = "Spend 15 minutes learning",
            progress = if (isQuest2Unlocked) (earnedXP - 10).coerceIn(0, 15) else 0,
            maxProgress = 15,
            rewardText = "Gem",
            isCompleted = isQuest2Completed,
            isLocked = !isQuest2Unlocked
        )

        // 5. Quest 3: Complete 2 perfect lessons
        QuestRowCard(
            icon = "🎯",
            iconBg = DuolingoGreen,
            title = "Complete 2 perfect lessons",
            progress = if (isQuest3Unlocked) 1 else 0,
            maxProgress = 2,
            rewardText = "XP Boost",
            isCompleted = false,
            isLocked = !isQuest3Unlocked
        )

        // 6. Interactive XP Simulation Button for Testing
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(DuolingoCardBg)
                .border(1.dp, DuolingoInputBorder, RoundedCornerShape(14.dp))
                .clickable { earnedXP += 10 }
                .padding(14.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("⚡  SIMULATE +10 XP GAIN", color = Color(0xFFFF9600), fontSize = 13.sp, fontWeight = FontWeight.Black)
        }

        // 7. Monthly Challenges Banner Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(DuolingoCardBg)
                .border(1.5.dp, DuolingoInputBorder, RoundedCornerShape(20.dp))
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("SEPTEMBER CHALLENGE", color = DuolingoSubtext, fontSize = 12.sp, fontWeight = FontWeight.Black)
                Text("21 DAYS LEFT", color = Color(0xFFFF9600), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF9600).copy(0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏅", fontSize = 28.sp)
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Earn the Golden Phoenix Badge", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text("Complete 30 daily quests this month.", color = DuolingoSubtext, fontSize = 12.sp)
                }
            }
        }

        Spacer(Modifier.height(30.dp))
    }
}

@Composable
private fun QuestRowCard(
    icon: String,
    iconBg: Color,
    title: String,
    progress: Int,
    maxProgress: Int,
    rewardText: String,
    isCompleted: Boolean,
    isLocked: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(DuolingoCardBg)
            .border(
                width = 1.5.dp,
                color = if (isCompleted) DuolingoGreen else DuolingoInputBorder,
                shape = RoundedCornerShape(18.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(if (isLocked) DuolingoInputBorder else iconBg.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(if (isLocked) "🔒" else icon, fontSize = 22.sp)
        }

        // Title + Progress bar
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                color = if (isLocked) DuolingoSubtext else Color.White,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Bold
            )

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape)
                    .background(DuolingoInputBorder)
            ) {
                val fraction = (progress.toFloat() / maxProgress.toFloat()).coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction)
                        .clip(CircleShape)
                        .background(if (isCompleted) DuolingoGreen else iconBg)
                )
            }

            Text(
                text = "$progress / $maxProgress",
                color = DuolingoSubtext,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Reward Pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (isCompleted) DuolingoGreen else DuolingoCardBg)
                .border(1.dp, if (isCompleted) DuolingoGreen else DuolingoInputBorder, RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = if (isCompleted) "CLAIMED" else rewardText,
                color = if (isCompleted) Color.White else Color(0xFFFF9600),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}
