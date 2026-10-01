package com.simats.codeingo.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.InputBorder

@Composable
fun TopStatsHeader(
    phoenixStage: Int,
    streakDays: Int,
    totalXP: Int,
    heartsCount: Int,
    heartTimerString: String? = null,
    onMenuClick: () -> Unit,
    onPhoenixClick: () -> Unit,
    onHeartsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkBackground)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hamburger side menu button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(CardBackground)
                    .border(1.dp, InputBorder, CircleShape)
                    .clickable { onMenuClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Phoenix Stage Badge
            val stageShape = RoundedCornerShape(12.dp)
            Row(
                modifier = Modifier
                    .clip(stageShape)
                    .background(Color(0xFFFA8000).copy(alpha = 0.18f))
                    .border(1.2.dp, Color(0xFFFA8000).copy(alpha = 0.4f), stageShape)
                .clickable { onPhoenixClick() }
                .padding(horizontal = 8.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "🔥", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Stage $phoenixStage",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFFC800)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Streak
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🔥", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "$streakDays",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFA8000)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // XP
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "⭐", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "$totalXP",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = AmberGold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Hearts (click to open Shop)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onHeartsClick() }
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "❤️", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = if (heartTimerString != null) "$heartsCount ($heartTimerString)" else "$heartsCount",
                    fontSize = if (heartTimerString != null) 11.sp else 13.sp,
                    fontWeight = FontWeight.Black,
                    color = DuolingoRed
                )
            }
        }

        // Bottom border line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(InputBorder.copy(alpha = 0.6f))
                .align(Alignment.BottomCenter)
        )
    }
}
