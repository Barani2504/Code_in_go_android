
package com.simats.codeingo.ui.dashboard

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.components.motion.CardFace
import com.simats.codeingo.ui.components.motion.FlipCard
import com.simats.codeingo.ui.components.motion.NumberRollText
import com.simats.codeingo.ui.components.motion.pulse
import com.simats.codeingo.ui.components.motion.bounce
import com.simats.codeingo.ui.components.motion.pressSquish
import com.simats.codeingo.ui.components.motion.shimmerGlossSweep
import com.simats.codeingo.ui.mascot.MascotEmotion
import com.simats.codeingo.ui.mascot.MascotView
import com.simats.codeingo.ui.mascot.PhoenixStage
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.AppFontFamily
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DuolingoBlue
import com.simats.codeingo.ui.theme.DuolingoCardBg
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoInputBg
import com.simats.codeingo.ui.theme.DuolingoInputBorder
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.PhoenixCrimson
import com.simats.codeingo.ui.theme.PhoenixGreen
import com.simats.codeingo.ui.theme.SubtextGray

/**
 * 1. CONTINUE HERO CARD — Next Best Action
 * Recommends the immediate next level on the living realm map.
 */
@Composable
fun ContinueHeroCard(
    activeLevelIndex: Int,
    chapterId: Int,
    chapterTitle: String,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val heroGradient = if (isDark) {
        Brush.horizontalGradient(
            colors = listOf(
                Color(0xFF1E293B),
                Color(0xFF0F172A)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color.White,
                Color(0xFFF8FAFC)
            )
        )
    }
    val cardBorder = if (isDark) AmberGold.copy(alpha = 0.5f) else AmberGold.copy(alpha = 0.7f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(heroGradient)
            .border(1.5.dp, cardBorder, RoundedCornerShape(22.dp))
            .shimmerGlossSweep(enabled = true)
            .padding(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header Row: Chapter Tag + Next Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AmberGold.copy(alpha = 0.15f))
                        .padding(horizontal = 9.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "NEXT BEST ACTION",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = AppFontFamily,
                        color = AmberGold
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "XP",
                        tint = AmberGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "+15 XP",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberGold
                    )
                }
            }

            // Body: Chapter Name & Node Title
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Chapter $chapterId: $chapterTitle",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) SubtextGray else dynamicColors.textSecondary
                )
                Text(
                    text = "Node $activeLevelIndex Defense Challenge",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = dynamicColors.textPrimary
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // CTA Button with tactile feedback
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .pressSquish(onClick = onContinueClick)
                    .clip(RoundedCornerShape(14.dp))
                    .background(PhoenixGreen)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "CONTINUE CHALLENGE",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Go",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * 2. TODAY'S MISSIONS CAROUSEL (M3 Horizontal Scroll)
 * Shows live daily missions from common backend, or falls back to the 3 standard missions:
 * Lesson, Mistake Vault review, and Arena trial.
 */
@Composable
fun TodaysMissionsCarousel(
    onOpenLesson: () -> Unit,
    onOpenMistakeVault: () -> Unit,
    onOpenArena: () -> Unit,
    dailyQuests: List<com.simats.codeingo.data.network.model.DailyQuestItem> = emptyList(),
    onClaimQuest: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "TODAY'S MISSIONS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                fontFamily = AppFontFamily,
                color = SubtextGray
            )
            Text(
                text = if (dailyQuests.isNotEmpty()) "${dailyQuests.count { !it.isClaimed }} ACTIVE" else "3 ACTIVE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = AmberGold
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (dailyQuests.isNotEmpty()) {
                dailyQuests.forEach { quest ->
                    val isCompleted = quest.isCompleted
                    val isClaimed = quest.isClaimed
                    MissionCard(
                        icon = if (isClaimed) "✅" else if (isCompleted) "🎁" else "🎯",
                        title = quest.title,
                        description = quest.requirement + if (quest.targetCount > 1) " (${quest.currentCount}/${quest.targetCount})" else "",
                        rewardXp = quest.xpReward,
                        isCompleted = isCompleted,
                        isClaimed = isClaimed,
                        onClick = {
                            if (isCompleted && !isClaimed) {
                                onClaimQuest?.invoke(quest.id)
                            } else {
                                onOpenLesson()
                            }
                        }
                    )
                }
            } else {
                MissionCard(
                    icon = "🎯",
                    title = "Secure 1 Node",
                    description = "Complete any lesson node",
                    rewardXp = 20,
                    isCompleted = false,
                    isClaimed = false,
                    onClick = onOpenLesson
                )
                MissionCard(
                    icon = "🛡️",
                    title = "Mistake Vault",
                    description = "Review past vulnerabilities",
                    rewardXp = 10,
                    isCompleted = false,
                    isClaimed = false,
                    onClick = onOpenMistakeVault
                )
                MissionCard(
                    icon = "⚔️",
                    title = "Arena Trial",
                    description = "Survive 1 cipher arena wave",
                    rewardXp = 25,
                    isCompleted = false,
                    isClaimed = false,
                    onClick = onOpenArena
                )
            }
        }
    }
}

@Composable
private fun MissionCard(
    icon: String,
    title: String,
    description: String,
    rewardXp: Int,
    isCompleted: Boolean,
    isClaimed: Boolean = false,
    onClick: () -> Unit
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    val cardBg = if (isDark) Color(0xFF131B2A) else Color.White
    val borderColor = when {
        isClaimed -> Color(0xFF10B981).copy(alpha = 0.5f)
        isCompleted -> AmberGold.copy(alpha = 0.8f)
        else -> if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
    }

    Box(
        modifier = Modifier
            .width(200.dp)
            .pressSquish(onClick = onClick)
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(1.2.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = icon, fontSize = 20.sp)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isClaimed) Color(0xFF10B981).copy(alpha = 0.2f) else AmberGold.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (isClaimed) "CLAIMED" else if (isCompleted) "CLAIM +$rewardXp XP" else "+$rewardXp XP",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isClaimed) Color(0xFF10B981) else AmberGold
                    )
                }
            }

            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = dynamicColors.textPrimary
            )

            Text(
                text = description,
                fontSize = 11.sp,
                color = dynamicColors.textSecondary,
                lineHeight = 14.sp
            )
        }
    }
}

/**
 * 3. DAILY GOAL RING & STREAK FREEZE INDICATOR
 * Displays today's XP progress ring alongside streak protection status.
 */
@Composable
fun DailyGoalAndStreakPill(
    currentXp: Int,
    targetXp: Int = 50,
    streakDays: Int,
    hasStreakFreeze: Boolean = true,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    val cardBg = if (isDark) Color(0xFF111827) else Color.White
    val borderColor = if (isDark) Color(0xFF1F2937) else Color(0xFFE2E8F0)
    val progress = (currentXp.toFloat() / targetXp.toFloat()).coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(cardBg)
            .border(1.dp, borderColor, RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Daily Goal Circular Ring
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.size(42.dp),
                        color = PhoenixGreen,
                        trackColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.08f),
                        strokeWidth = 4.dp,
                        strokeCap = StrokeCap.Round
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = PhoenixGreen
                    )
                }

                Column {
                    Text(
                        text = "DAILY GOAL",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = AppFontFamily,
                        color = if (isDark) SubtextGray else dynamicColors.textSecondary
                    )
                    Text(
                        text = "$currentXp / $targetXp XP",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = dynamicColors.textPrimary
                    )
                }
            }

            // Right: Streak Flame & Freeze Shield
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(PhoenixCrimson.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🔥", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$streakDays DAYS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = PhoenixCrimson
                        )
                    }
                }

                if (hasStreakFreeze) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0284C7).copy(alpha = 0.18f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🛡️", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ACTIVE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF38BDF8)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 4. DAILY REWARD CHEST (Interactive 3D FlipCard)
 * Tapping flips the chest to reveal daily bonus crystals and streak freeze.
 */
@Composable
fun DailyRewardChestCard(
    modifier: Modifier = Modifier,
    onClaimReward: (() -> Unit)? = null
) {
    var cardFace by remember { mutableStateOf(CardFace.FRONT) }
    var hasClaimed by remember { mutableStateOf(false) }

    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    val chestGradient = if (isDark) {
        Brush.horizontalGradient(
            colors = listOf(Color(0xFF291B0B), Color(0xFF1F1608))
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(Color(0xFFFFFBEB), Color(0xFFFEF3C7))
        )
    }

    FlipCard(
        cardFace = cardFace,
        onFlip = { next ->
            cardFace = next
            if (next == CardFace.BACK && !hasClaimed) {
                hasClaimed = true
                onClaimReward?.invoke()
            }
        },
        enableClickFlip = true,
        modifier = modifier.fillMaxWidth(),
        frontContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(chestGradient)
                    .border(1.5.dp, AmberGold, RoundedCornerShape(18.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("🎁", fontSize = 28.sp, modifier = Modifier.pulse(minScale = 1.0f, maxScale = 1.12f, periodMs = 600))
                        Column {
                            Text(
                                text = "DAILY CHEST READY",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isDark) AmberGold else Color(0xFFB45309)
                            )
                            Text(
                                text = "Tap to claim daily defense reward",
                                fontSize = 11.sp,
                                color = if (isDark) SubtextGray else Color(0xFF78350F)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AmberGold)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "CLAIM",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }
                }
            }
        },
        backContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF0F172A))
                    .border(1.5.dp, PhoenixGreen, RoundedCornerShape(18.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("✨", fontSize = 24.sp)
                        Column {
                            Text(
                                text = "+25 GEMS • STREAK PROTECTED",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = PhoenixGreen
                            )
                            Text(
                                text = "Daily chest claimed for today! Return tomorrow.",
                                fontSize = 11.sp,
                                color = SubtextGray
                            )
                        }
                    }

                    Text("CLAIMED ✓", fontSize = 11.sp, fontWeight = FontWeight.Black, color = PhoenixGreen)
                }
            }
        }
    )
}
