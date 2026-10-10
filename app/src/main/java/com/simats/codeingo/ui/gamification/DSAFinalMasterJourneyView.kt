package com.simats.codeingo.ui.gamification

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.components.AppButton
import com.simats.codeingo.ui.components.AppButtonStyle
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.pressScale
import com.simats.codeingo.ui.theme.tilt3D

/**
 * DSAFinalMasterJourneyView
 * Faithful Android Jetpack Compose counterpart to iOS DSAFinalMasterJourneyView.swift:
 * ARRAY → LINKED LIST → STACK → QUEUE → TREE → 🏆 DATA STRUCTURES MASTER
 * Celebrates 25-stage full curriculum completion with trophy badges, stats, and progression waterfall.
 */
@Composable
fun DSAFinalMasterJourneyView(
    totalXP: Int = 1250,
    totalLessons: Int = 25,
    correctAnswers: Int = 48,
    perfectLessons: Int = 18,
    currentStreak: Int = 3,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val badgeScale = remember { Animatable(0.85f) }
    val trophyRotation = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        badgeScale.animateTo(
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    LaunchedEffect(Unit) {
        trophyRotation.animateTo(
            targetValue = 10f,
            animationSpec = tween(800, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0A0F1E),
                        Color(0xFF121932),
                        Color(0xFF190F28)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Close Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.12f))
                        .pressScale()
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.8f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Grand Trophy Badge
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(180.dp)
                        .padding(top = 10.dp)
                ) {
                    // Pulsing Ring
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .scale(badgeScale.value)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color.Yellow.copy(alpha = 0.35f), Color.Transparent)
                                )
                            )
                    )

                    // Golden Medal
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .shadow(25.dp, CircleShape, spotColor = Color.Yellow)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFFFFD700), Color(0xFFFF8C00))
                                )
                            )
                            .border(3.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🏆",
                            fontSize = 60.sp,
                            modifier = Modifier.rotate(trophyRotation.value)
                        )
                    }
                }

                // Congratulatory Headline
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "DATA STRUCTURES MASTER",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = Color.Yellow,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Congratulations!",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = LocalDynamicThemeColors.current.textPrimary
                    )
                    Text(
                        text = "You have completed your Data Structures journey.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }

                // 5-Chapter Progression Waterfall
                WaterfallProgressionChain()

                // Performance Stats Grid
                StatsGrid(
                    totalXP = totalXP,
                    totalLessons = totalLessons,
                    correctAnswers = correctAnswers,
                    perfectLessons = perfectLessons,
                    currentStreak = currentStreak
                )

                // Finish Action Button using AppButton
                AppButton(
                    title = "CLAIM MASTER TROPHY 🏆",
                    style = AppButtonStyle.PRIMARY_AMBER,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onDismiss() }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun WaterfallProgressionChain() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.06f))
            .border(1.5.dp, Color.Yellow.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        WaterfallItem("ARRAY", "The Data Highway", "🚀", Color.Cyan)
        WaterfallArrow()
        WaterfallItem("LINKED LIST", "The Chain Quest", "🔗", Color(0xFFA855F7))
        WaterfallArrow()
        WaterfallItem("STACK", "The Tower of Plates", "🍽️", Color(0xFFF97316))
        WaterfallArrow()
        WaterfallItem("QUEUE", "The Waiting Line", "🎟️", Color(0xFF22C55E))
        WaterfallArrow()
        WaterfallItem("TREE", "The Data Kingdom", "🌳", Color(0xFF10B981))
        WaterfallArrow()
        WaterfallMasterItem()
    }
}

@Composable
private fun WaterfallItem(title: String, subtitle: String, emoji: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 16.sp)
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = LocalDynamicThemeColors.current.textPrimary
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.6f)
            )
        }

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "PASSED",
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = color
            )
        }
    }
}

@Composable
private fun WaterfallArrow() {
    Icon(
        imageVector = Icons.Default.ArrowDownward,
        contentDescription = null,
        tint = Color.Yellow.copy(alpha = 0.8f),
        modifier = Modifier.size(14.dp)
    )
}

@Composable
private fun WaterfallMasterItem() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Yellow.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Yellow.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "👑", fontSize = 20.sp)
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "🏆 DATA STRUCTURES MASTER",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = Color.Yellow
            )
            Text(
                text = "Mastery Certified",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.8f)
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color.Yellow)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "LEGENDARY",
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun StatsGrid(
    totalXP: Int,
    totalLessons: Int,
    correctAnswers: Int,
    perfectLessons: Int,
    currentStreak: Int
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatTile(emoji = "⚡", iconColor = Color.Yellow, value = "$totalXP XP", label = "Total XP", modifier = Modifier.weight(1f))
            StatTile(emoji = "📖", iconColor = Color(0xFF38BDF8), value = "$totalLessons", label = "Total Lessons", modifier = Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatTile(emoji = "🎯", iconColor = Color(0xFF22C55E), value = "$correctAnswers", label = "Correct Answers", modifier = Modifier.weight(1f))
            StatTile(emoji = "⭐", iconColor = Color(0xFFF97316), value = "$perfectLessons", label = "Perfect Lessons", modifier = Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatTile(emoji = "🔥", iconColor = Color(0xFFEF4444), value = "$currentStreak Days", label = "Current Streak", modifier = Modifier.weight(1f))
            StatTile(emoji = "🛡️", iconColor = Color(0xFFA855F7), value = "Master", label = "DSA Badge", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatTile(
    emoji: String,
    iconColor: Color,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.07f))
            .border(1.dp, LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
            .tilt3D()
            .pressScale()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 18.sp)
        }

        Column {
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = LocalDynamicThemeColors.current.textPrimary
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace,
                color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.6f)
            )
        }
    }
}
