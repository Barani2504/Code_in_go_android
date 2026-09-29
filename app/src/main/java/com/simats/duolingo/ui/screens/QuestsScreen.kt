package com.simats.duolingo.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.simats.duolingo.ui.components.DuolingoButton
import com.simats.duolingo.ui.theme.*

data class QuestItem(
    val emoji: String,
    val title: String,
    val subtitle: String,
    val progress: Int,
    val total: Int,
    val reward: String,
)

private val dailyQuests = listOf(
    QuestItem("⚡", "Earn 10 XP today",         "Practice any skill",     0,  10, "10 XP"),
    QuestItem("📚", "Complete a lesson",          "Any unit",               0,  1,  "+1 Streak"),
    QuestItem("🔥", "Maintain your streak",       "Log in and practice",    1,  1,  "🔥 Streak"),
    QuestItem("💎", "Earn 5 gems",                "Win quizzes without mistakes", 0, 5, "5 💎"),
)

@Composable
fun QuestsScreen(onStartLesson: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DuolingoDarkBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text("Daily Quests", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
                Text("Resets in 14h 32m", color = DuolingoSubtext, fontSize = 13.sp)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(DuolingoCardBg)
                    .border(1.dp, DuolingoInputBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("🎁  CHESTS", color = DuolingoSubtext, fontSize = 11.sp, fontWeight = FontWeight.Black)
            }
        }

        // Chest row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf("Bronze" to "🥉", "Silver" to "🥈", "Gold" to "🥇").forEach { (label, emoji) ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(DuolingoCardBg)
                        .border(1.dp, DuolingoInputBorder, RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(emoji, fontSize = 30.sp)
                    Text(label, color = DuolingoSubtext, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Text("Daily Challenges", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)

        // Quest cards
        dailyQuests.forEach { quest ->
            QuestCard(quest = quest, onStartLesson = onStartLesson)
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun QuestCard(quest: QuestItem, onStartLesson: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DuolingoCardBg)
            .border(1.dp, DuolingoInputBorder, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(quest.emoji, fontSize = 28.sp)
            Column(modifier = Modifier.weight(1f)) {
                Text(quest.title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(quest.subtitle, color = DuolingoSubtext, fontSize = 12.sp)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(DuolingoGreen.copy(alpha = 0.15f))
                    .border(1.dp, DuolingoGreen.copy(0.4f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(quest.reward, color = DuolingoGreen, fontSize = 11.sp, fontWeight = FontWeight.Black)
            }
        }

        // Progress bar
        val prog = if (quest.total > 0) quest.progress.toFloat() / quest.total.toFloat() else 0f
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(50))
                .background(DuolingoInputBg)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(prog)
                    .clip(RoundedCornerShape(50))
                    .background(DuolingoGreen)
            )
        }

        Text(
            "${quest.progress}/${quest.total}",
            color = DuolingoSubtext,
            fontSize = 11.sp
        )

        if (quest.progress < quest.total) {
            DuolingoButton(
                text = "START",
                backgroundColor = DuolingoBlue,
                shadowColor = DuolingoBlueDark,
                modifier = Modifier.fillMaxWidth(),
                onClick = onStartLesson
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DuolingoGreen.copy(0.12f))
                    .border(1.dp, DuolingoGreen, RoundedCornerShape(12.dp))
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("✅  COMPLETED", color = DuolingoGreen, fontSize = 14.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}
