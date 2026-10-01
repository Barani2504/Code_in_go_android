package com.simats.codeingo.ui.shop

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DuolingoBlue
import com.simats.codeingo.ui.theme.DuolingoBlueDark
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoGreenDark
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.InputBorder
import com.simats.codeingo.ui.theme.SubtextGray

@Composable
fun ShopScreen(
    modifier: Modifier = Modifier
) {
    val gameManager = GameManager.instance
    val gemsCount by gameManager.gemsCount.collectAsState()
    val heartsCount by gameManager.heartsCount.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Header with Gem Balance
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Shop",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = "Power-ups & Hearts",
                    fontSize = 13.sp,
                    color = SubtextGray
                )
            }

            // Gem pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(DuolingoBlue.copy(alpha = 0.2f))
                    .border(1.5.dp, DuolingoBlue, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "💎", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "$gemsCount",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = DuolingoBlue
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Power-ups Section
        Text(
            text = "POWER-UPS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = SubtextGray,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        val shape = RoundedCornerShape(18.dp)

        // Refill Hearts
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(CardBackground)
                .border(1.dp, InputBorder, shape)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(DuolingoRed.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "❤️", fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Refill Hearts",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Get full 10 hearts so you can keep practicing without waiting",
                    fontSize = 12.sp,
                    color = SubtextGray,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            DuolingoButton(
                text = "💎 350",
                faceColor = DuolingoBlue,
                shadowColor = DuolingoBlueDark,
                onClick = {
                    if (gemsCount >= 350 && heartsCount < 10) {
                        gameManager.refillHearts()
                    }
                },
                modifier = Modifier.width(90.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Streak Freeze
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(CardBackground)
                .border(1.dp, InputBorder, shape)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF38C2F5).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🧊", fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Streak Freeze",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Equip a freeze so your streak stays protected if you miss a day",
                    fontSize = 12.sp,
                    color = SubtextGray,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            DuolingoButton(
                text = "💎 200",
                faceColor = DuolingoBlue,
                shadowColor = DuolingoBlueDark,
                onClick = { /* Buy streak freeze */ },
                modifier = Modifier.width(90.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Double XP Boost
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(CardBackground)
                .border(1.dp, InputBorder, shape)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFC800).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "⭐", fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Double XP Boost",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Earn 2x XP on all completed lessons for the next 15 minutes",
                    fontSize = 12.sp,
                    color = SubtextGray,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            DuolingoButton(
                text = "💎 100",
                faceColor = DuolingoBlue,
                shadowColor = DuolingoBlueDark,
                onClick = { /* Buy double XP */ },
                modifier = Modifier.width(90.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
