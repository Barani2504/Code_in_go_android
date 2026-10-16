package com.simats.codeingo.ui.arenas.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.LocalReduceMotion

@Composable
fun ArenaComboHUD(
    comboCount: Int,
    score: Int,
    equippedTool: ArenaTacticalTool? = null,
    toolUsed: Boolean = false,
    onActivateTool: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val reduceMotion = LocalReduceMotion.current

    val multiplier = when {
        comboCount >= 8 -> 3.0f
        comboCount >= 5 -> 2.0f
        comboCount >= 3 -> 1.5f
        else -> 1.0f
    }

    val flameProgress = (comboCount % 8) / 8f
    val animatedFlameProgress by animateFloatAsState(
        targetValue = if (comboCount >= 8) 1f else flameProgress,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "flameProgress"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (comboCount >= 5 && !reduceMotion) 1.15f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val comboColor = when {
        comboCount >= 8 -> Color(0xFFFF2A6D)
        comboCount >= 5 -> Color(0xFFFF6D00)
        comboCount >= 3 -> AmberGold
        else -> dynamicColors.textSecondary
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(dynamicColors.cardBackground.copy(alpha = 0.85f))
            .border(1.dp, dynamicColors.cardBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Combo & Streak section
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(comboColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = comboColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${comboCount}x",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = comboColor
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "COMBO",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = dynamicColors.textSecondary,
                        modifier = Modifier.padding(bottom = 1.dp)
                    )
                    if (multiplier > 1.0f) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(comboColor.copy(alpha = 0.25f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "${multiplier}x XP",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = comboColor
                            )
                        }
                    }
                }

                // Mini Flame Meter
                Spacer(modifier = Modifier.height(3.dp))
                Box(
                    modifier = Modifier
                        .width(70.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(dynamicColors.cardBorder)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedFlameProgress.coerceIn(0.05f, 1f))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(AmberGold, Color(0xFFFF6D00), Color(0xFFFF2A6D))
                                )
                            )
                    )
                }
            }
        }

        // Tactical tool button if equipped
        if (equippedTool != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (toolUsed) dynamicColors.cardBorder.copy(alpha = 0.3f)
                        else equippedTool.accentColor.copy(alpha = 0.18f)
                    )
                    .border(
                        1.dp,
                        if (toolUsed) Color.Transparent else equippedTool.accentColor,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable(enabled = !toolUsed, onClick = onActivateTool)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = equippedTool.icon,
                        contentDescription = equippedTool.title,
                        tint = if (toolUsed) dynamicColors.textSecondary else equippedTool.accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (toolUsed) "USED" else equippedTool.title.split(" ").first(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (toolUsed) dynamicColors.textSecondary else equippedTool.accentColor
                    )
                }
            }
        }

        // Score display
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "SCORE",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = dynamicColors.textSecondary
            )
            Text(
                text = "$score",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = dynamicColors.textPrimary
            )
        }
    }
}
