package com.simats.duolingo.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.simats.duolingo.data.AppState
import com.simats.duolingo.ui.theme.*

@Composable
fun LeaderboardsScreen() {
    // Unlocks after completing 3+ lessons – simplified static mock
    val isUnlocked = false

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
            // Locked state – shields graphic
            Row(
                horizontalArrangement = Arrangement.spacedBy(-14.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                ShieldBadge("🥉", Color(0xFFCC8855), rotation = -14f, size = 65.dp)
                ShieldBadge("🥇", Color(0xFFFFD700), rotation = 0f, size = 85.dp)
                ShieldBadge("🥈", Color(0xFFC0C0C0), rotation = 14f, size = 65.dp)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Compete with the world!",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = "Complete 3 lessons to unlock Leaderboards",
                    color = DuolingoSubtext,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                )
            }

            // What are leaderboards card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DuolingoCardBg)
                    .border(1.dp, DuolingoInputBorder, RoundedCornerShape(16.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text("🛡️  WHAT ARE LEADERBOARDS?",
                    color = DuolingoSubtext, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 0.8.sp)
                Text(
                    "Compete with other learners! Every week, the top finishers of each league are promoted to a higher tier.",
                    color = Color.White, fontSize = 14.sp
                )
            }

            // Locked skeleton rows
            repeat(5) { i ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DuolingoInputBg)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("#${i + 1}", color = DuolingoSubtext, fontSize = 16.sp, fontWeight = FontWeight.Black,
                        modifier = Modifier.width(30.dp))
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(50))
                            .background(DuolingoInputBorder)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(DuolingoInputBorder)
                        )
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.3f)
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(DuolingoInputBorder.copy(0.6f))
                        )
                    }
                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(DuolingoInputBorder)
                    )
                }
            }
        }
    }
}

@Composable
private fun ShieldBadge(emoji: String, color: Color, rotation: Float, size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(listOf(color, color.copy(alpha = 0.5f)))
            )
            .rotate(rotation),
        contentAlignment = Alignment.Center
    ) {
        Text(emoji, fontSize = (size.value * 0.4f).sp)
    }
}
