package com.simats.codeingo.ui.arenas.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.components.Duolingo3DButton
import com.simats.codeingo.ui.components.Duolingo3DButtonColor
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import kotlinx.coroutines.delay

@Composable
fun ArenaBriefingView(
    arenaTitle: String,
    levelName: String,
    cryptographicObjective: String,
    mechanicsHint: String,
    isBoss: Boolean = false,
    onStartLevel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    var secondsLeft by remember { mutableIntStateOf(10) }

    LaunchedEffect(Unit) {
        while (secondsLeft > 0) {
            delay(1000L)
            secondsLeft--
        }
        onStartLevel()
    }

    val progress = (secondsLeft / 10f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.82f))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(dynamicColors.cardBackground)
                .border(
                    width = 2.dp,
                    color = if (isBoss) DuolingoRed else AmberGold,
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header badge
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        if (isBoss) DuolingoRed.copy(alpha = 0.2f)
                        else AmberGold.copy(alpha = 0.2f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isBoss) Icons.Default.WarningAmber else Icons.Default.Security,
                    contentDescription = null,
                    tint = if (isBoss) DuolingoRed else AmberGold,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isBoss) "BOSS ENCOUNTER" else "TACTICAL BRIEFING",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
                color = if (isBoss) DuolingoRed else AmberGold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = levelName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = dynamicColors.textPrimary,
                textAlign = TextAlign.Center
            )

            Text(
                text = arenaTitle,
                fontSize = 13.sp,
                color = dynamicColors.textSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Objective Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(dynamicColors.cardBorder.copy(alpha = 0.25f))
                    .padding(14.dp)
            ) {
                Text(
                    text = "CRYPTOGRAPHIC OBJECTIVE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AmberGold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = cryptographicObjective,
                    fontSize = 13.sp,
                    color = dynamicColors.textPrimary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "TACTICAL MECHANIC",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AmberGold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = mechanicsHint,
                    fontSize = 12.sp,
                    color = dynamicColors.textSecondary,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Auto-launch countdown indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(30.dp)
                ) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxSize(),
                        color = if (isBoss) DuolingoRed else AmberGold,
                        strokeWidth = 3.dp,
                        trackColor = dynamicColors.cardBorder
                    )
                    Text(
                        text = "$secondsLeft",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = dynamicColors.textPrimary
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Auto-engaging in ${secondsLeft}s",
                    fontSize = 12.sp,
                    color = dynamicColors.textSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Duolingo3DButton(
                title = "Engage Now",
                style = if (isBoss) Duolingo3DButtonColor.AMBER else Duolingo3DButtonColor.GREEN,
                onClick = onStartLevel,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
