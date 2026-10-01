package com.simats.codeingo.ui.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DsaBlue
import com.simats.codeingo.ui.theme.DsaGreen
import com.simats.codeingo.ui.theme.DsaOrange
import com.simats.codeingo.ui.theme.DsaPurple
import com.simats.codeingo.ui.theme.DsaRed
import com.simats.codeingo.ui.theme.DuolingoBlue
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoInputBg
import com.simats.codeingo.ui.theme.DuolingoInputBorder
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.InputBorder
import com.simats.codeingo.ui.theme.SubtextGray

data class DSAAchievementItem(
    val id: String,
    val badge: String,
    val title: String,
    val description: String,
    val xpReward: Int,
    val category: String,
    val isUnlocked: Boolean
)

data class XPActivityReward(
    val title: String,
    val icon: String,
    val xpReward: Int
)

@Composable
fun DSAAchievementsView(
    modifier: Modifier = Modifier
) {
    val gameManager = GameManager.instance
    val streakDays by gameManager.streakDays.collectAsState()
    val totalXP by gameManager.totalXP.collectAsState()

    val achievements = remember(streakDays, totalXP) {
        listOf(
            // Beginner
            DSAAchievementItem("1", "🥉", "Array Apprentice", "Complete the Arrays lesson", 25, "Beginner", true),
            DSAAchievementItem("2", "🥉", "Variable Virtuoso", "Complete Programming Basics", 25, "Beginner", true),
            DSAAchievementItem("3", "🥉", "Stack Starter", "Complete your first Stack lesson", 30, "Beginner", totalXP >= 50),

            // Intermediate
            DSAAchievementItem("4", "🥈", "Stack Master", "Perfect score on Stack quiz", 50, "Intermediate", totalXP >= 100),
            DSAAchievementItem("5", "🥈", "Queue Champion", "Complete Queue with 100% accuracy", 50, "Intermediate", totalXP >= 150),
            DSAAchievementItem("6", "🥈", "Linked List Legend", "Master Linked List concepts", 60, "Intermediate", totalXP >= 200),

            // Expert
            DSAAchievementItem("7", "🥇", "Tree Explorer", "Complete Trees & BST units", 75, "Expert", totalXP >= 300),
            DSAAchievementItem("8", "🥇", "Graph Guru", "Complete Graph Algorithms", 75, "Expert", totalXP >= 400),
            DSAAchievementItem("9", "🥇", "Sort Savant", "Master all sorting algorithms", 80, "Expert", totalXP >= 500),

            // Streak
            DSAAchievementItem("10", "🔥", "7-Day Streak", "Learn 7 days in a row", 100, "Streak", streakDays >= 7),
            DSAAchievementItem("11", "🔥", "30-Day Streak", "Learn 30 days in a row", 300, "Streak", streakDays >= 30),
            DSAAchievementItem("12", "🔥", "100-Day Streak", "The DSA Centurion!", 1000, "Streak", streakDays >= 100),

            // Milestone
            DSAAchievementItem("13", "💎", "100 XP Club", "Earn your first 100 XP", 20, "Milestone", totalXP >= 100),
            DSAAchievementItem("14", "💎", "500 XP Elite", "Earn 500 total XP", 50, "Milestone", totalXP >= 500),
            DSAAchievementItem("15", "💎", "1000 XP Master", "Earn 1000 total XP", 100, "Milestone", totalXP >= 1000),

            // Special
            DSAAchievementItem("16", "🏆", "DSA Champion", "Complete all 5 learning levels", 500, "Special", false),
            DSAAchievementItem("17", "⚡", "Speed Learner", "Complete 3 lessons in one day", 75, "Special", totalXP >= 80),
            DSAAchievementItem("18", "🧠", "Perfect Mind", "10 perfect lessons in a row", 150, "Special", false)
        )
    }

    val categories = remember { listOf("All", "Beginner", "Intermediate", "Expert", "Streak", "Milestone", "Special") }
    var selectedCategory by remember { mutableStateOf("All") }

    val filteredAchievements = remember(selectedCategory, achievements) {
        if (selectedCategory == "All") achievements
        else achievements.filter { it.category == selectedCategory }
    }

    val unlockedCount = remember(achievements) { achievements.count { it.isUnlocked } }

    val xpActivities = remember {
        listOf(
            XPActivityReward("Complete Lesson", "📖", 15),
            XPActivityReward("Quiz Mastery", "🎯", 25),
            XPActivityReward("Daily Challenge", "⚡", 40),
            XPActivityReward("Coding Challenge", "💻", 50),
            XPActivityReward("Perfect Lesson", "✨", 20),
            XPActivityReward("7-Day Streak", "🔥", 100)
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header Stats Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF141448), Color(0xFF261458))
                    )
                )
                .border(1.dp, DsaPurple.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                .padding(vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatBox(
                    value = "$unlockedCount",
                    label = "Unlocked",
                    color = DsaGreen,
                    icon = Icons.Default.CheckCircle,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .height(44.dp)
                        .width(1.dp)
                        .background(Color.White.copy(alpha = 0.15f))
                )

                StatBox(
                    value = "${achievements.size - unlockedCount}",
                    label = "Remaining",
                    color = DsaOrange,
                    icon = Icons.Default.Lock,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .height(44.dp)
                        .width(1.dp)
                        .background(Color.White.copy(alpha = 0.15f))
                )

                StatBox(
                    value = "${unlockedCount * 25}",
                    label = "XP Earned",
                    color = AmberGold,
                    icon = Icons.Default.Star,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = selectedCategory == cat
                val shape = RoundedCornerShape(20.dp)

                Box(
                    modifier = Modifier
                        .clip(shape)
                        .background(if (isSelected) DsaPurple else CardBackground)
                        .border(1.5.dp, if (isSelected) DsaPurple else InputBorder, shape)
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = cat,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isSelected) Color.White else SubtextGray
                    )
                }
            }
        }

        // XP Activity Table
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "XP REWARDS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = SubtextGray,
                letterSpacing = 1.sp
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                xpActivities.forEach { act ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardBackground)
                            .border(1.dp, InputBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AmberGold.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = act.icon, fontSize = 16.sp)
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = act.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AmberGold.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "+${act.xpReward} XP",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = AmberGold
                            )
                        }
                    }
                }
            }
        }

        // Achievement Grid
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "BADGES (${filteredAchievements.size})",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = SubtextGray,
                letterSpacing = 1.sp
            )

            // Two-column layout
            val chunked = filteredAchievements.chunked(2)
            chunked.forEach { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    pair.forEach { item ->
                        AchievementCard(
                            item = item,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (pair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun StatBox(
    value: String,
    label: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        }
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = SubtextGray
        )
    }
}

@Composable
private fun AchievementCard(
    item: DSAAchievementItem,
    modifier: Modifier = Modifier
) {
    val catColor = when (item.category) {
        "Beginner" -> DsaGreen
        "Intermediate" -> DsaBlue
        "Expert" -> DsaPurple
        "Streak" -> DsaOrange
        "Milestone" -> DsaRed
        "Special" -> AmberGold
        else -> DsaBlue
    }

    val shape = RoundedCornerShape(18.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .background(CardBackground)
            .border(
                width = if (item.isUnlocked) 2.dp else 1.dp,
                color = if (item.isUnlocked) catColor.copy(alpha = 0.5f) else InputBorder,
                shape = shape
            )
            .padding(14.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Circular Badge Icon
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(if (item.isUnlocked) catColor.copy(alpha = 0.2f) else DuolingoInputBg)
                    .border(
                        width = 2.dp,
                        color = if (item.isUnlocked) catColor else DuolingoInputBorder,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.badge,
                    fontSize = 28.sp,
                    color = if (item.isUnlocked) Color.White else Color.White.copy(alpha = 0.35f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = if (item.isUnlocked) Color.White else SubtextGray,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = item.description,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = SubtextGray,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(AmberGold.copy(alpha = 0.1f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = AmberGold,
                        modifier = Modifier.size(10.dp)
                    )
                    Text(
                        text = "+${item.xpReward} XP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = AmberGold
                    )
                }
            }
        }
    }
}
