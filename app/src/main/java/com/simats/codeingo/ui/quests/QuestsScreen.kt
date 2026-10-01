package com.simats.codeingo.ui.quests

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
import androidx.compose.foundation.lazy.items
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
import com.simats.codeingo.ui.components.DuolingoButton
import com.simats.codeingo.ui.components.ProgressBarAnimated
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DuolingoBlue
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoGreenDark
import com.simats.codeingo.ui.theme.InputBorder
import com.simats.codeingo.ui.theme.SubtextGray

@Composable
fun QuestsScreen(
    modifier: Modifier = Modifier
) {
    val gameManager = GameManager.instance
    val quests by gameManager.dailyQuests.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFC800).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🎯", fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = "Daily Quests",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = "Resets in 14 hours",
                    fontSize = 13.sp,
                    color = SubtextGray
                )
            }
        }

        // Quests List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp)
        ) {
            items(quests) { quest ->
                val shape = RoundedCornerShape(18.dp)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape)
                        .background(CardBackground)
                        .border(1.5.dp, if (quest.isCompleted && !quest.isClaimed) AmberGold else InputBorder, shape)
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = quest.title,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        // Reward Badge
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "⭐ +${quest.xpReward} XP", fontSize = 12.sp, fontWeight = FontWeight.Black, color = AmberGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "💎 +${quest.gemReward}", fontSize = 12.sp, fontWeight = FontWeight.Black, color = DuolingoBlue)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = quest.requirement,
                        fontSize = 13.sp,
                        color = SubtextGray
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress Bar
                    ProgressBarAnimated(
                        progress = quest.currentCount.toFloat() / quest.targetCount.toFloat(),
                        height = 10.dp,
                        barColor = if (quest.isCompleted) DuolingoGreen else AmberGold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${quest.currentCount} / ${quest.targetCount}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SubtextGray
                        )

                        if (quest.isCompleted && !quest.isClaimed) {
                            DuolingoButton(
                                text = "CLAIM REWARD",
                                faceColor = AmberGold,
                                shadowColor = Color(0xFFD7A000),
                                onClick = { gameManager.claimQuest(quest.id) },
                                modifier = Modifier.width(140.dp)
                            )
                        } else if (quest.isClaimed) {
                            Text(
                                text = "CLAIMED ✓",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = DuolingoGreen
                            )
                        }
                    }
                }
            }
        }
    }
}
