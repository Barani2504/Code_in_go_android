package com.simats.codeingo.ui.streak

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.domain.StreakManager
import com.simats.codeingo.ui.components.motion.pressSquish
import com.simats.codeingo.ui.motion.AshMotionEngine
import com.simats.codeingo.ui.motion.NumberTickerText
import com.simats.codeingo.ui.motion.StreakFlameCanvas
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.FeedbackManager
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.PhoenixCrimson
import com.simats.codeingo.ui.theme.PhoenixGreen
import com.simats.codeingo.ui.theme.SubtextGray

data class StreakSocietyMilestone(
    val days: Int,
    val title: String,
    val badge: String,
    val description: String
)

/**
 * 🔥 Streak Society & Ethical Retention Sheet
 *
 * Implements Phase 5.1:
 * - Countdown to local midnight deadline
 * - Weekend Amulet protection status
 * - 24h Streak Repair for 50 gems
 * - Streak Society milestone badges (7, 30, 100, 365)
 * - Quiet Mode toggle to eliminate guilt copy & notifications
 */
@Composable
fun StreakSocietySheet(
    streakDays: Int,
    isStreakPendingRestore: Boolean,
    savedStreakDays: Int,
    onRestoreStreak: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val gameManager = GameManager.instance
    val gemsCount by gameManager.gemsCount.collectAsState()
    val streakManager = remember { StreakManager(context) }

    var isWeekendAmuletEnabled by remember {
        mutableStateOf(streakManager.isWeekendAmuletEnabled(context))
    }
    var isQuietModeEnabled by remember {
        mutableStateOf(streakManager.isQuietModeEnabled(context))
    }

    val (hoursLeft, minutesLeft) = remember {
        streakManager.getTimeUntilMidnight()
    }

    val milestones = listOf(
        StreakSocietyMilestone(7, "Ember Initiate", "🔥", "7 days of unbroken cryptographic defense"),
        StreakSocietyMilestone(30, "Flame Guardian", "⚡", "30-day mastery of cipher protocols"),
        StreakSocietyMilestone(100, "Blaze Architect", "🛡️", "100-day elite security posture"),
        StreakSocietyMilestone(365, "Inferno Legend", "👑", "1 full year of continuous cryptanalysis")
    )

    val cardBg = if (isDark) Color(0xFF111827) else Color.White
    val borderColor = if (isDark) Color(0xFF1F2937) else Color(0xFFE2E8F0)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Flame Canvas & Streak Counter
        Box(contentAlignment = Alignment.Center) {
            StreakFlameCanvas(
                streakDays = if (isStreakPendingRestore) savedStreakDays else streakDays,
                modifier = Modifier.size(90.dp)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                NumberTickerText(
                    value = if (isStreakPendingRestore) savedStreakDays else streakDays,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = AmberGold
                    )
                )
                Text(
                    text = "DAY STREAK",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = dynamicColors.textPrimary
                )
            }

            Text(
                text = if (isStreakPendingRestore)
                    "Your flame is temporarily paused. Restore it within 24h to continue!"
                else
                    "Your streak represents consistent cryptographic practice, not a chore.",
                fontSize = 12.sp,
                color = dynamicColors.textSecondary,
                textAlign = TextAlign.Center
            )
        }

        // Midnight Countdown Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("⏳", fontSize = 16.sp)
                    Text(
                        text = "TODAY'S DEADLINE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = dynamicColors.textPrimary
                    )
                }

                Text(
                    text = "${hoursLeft}h ${minutesLeft}m remaining",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = AmberGold
                )
            }
        }

        // Streak Repair Button (If pending restore)
        if (isStreakPendingRestore) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .pressSquish {
                        if (gemsCount >= 50) {
                            FeedbackManager.tap()
                            gameManager.deductGems(50)
                            onRestoreStreak()
                        }
                    }
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (gemsCount >= 50) AmberGold else SubtextGray)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "REPAIR STREAK WITH 50 GEMS 💎",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }
        }

        // Weekend Amulet Protection Toggle
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(cardBg)
                .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Weekend Amulet 🛡️",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = dynamicColors.textPrimary
                    )
                    Text(
                        text = "Protects your streak on Saturday & Sunday automatically.",
                        fontSize = 10.5.sp,
                        color = dynamicColors.textSecondary
                    )
                }

                Switch(
                    checked = isWeekendAmuletEnabled,
                    onCheckedChange = { checked ->
                        FeedbackManager.tap()
                        isWeekendAmuletEnabled = checked
                        streakManager.setWeekendAmuletEnabled(context, checked)
                    },
                    colors = SwitchDefaults.colors(checkedThumbColor = AmberGold, checkedTrackColor = AmberGold.copy(alpha = 0.5f))
                )
            }
        }

        // Quiet Mode Toggle (No Guilt, No Reminders)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(cardBg)
                .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Quiet Mode 🌙",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = dynamicColors.textPrimary
                    )
                    Text(
                        text = "Mutes all push notifications and streak reminder alerts.",
                        fontSize = 10.5.sp,
                        color = dynamicColors.textSecondary
                    )
                }

                Switch(
                    checked = isQuietModeEnabled,
                    onCheckedChange = { checked ->
                        FeedbackManager.tap()
                        isQuietModeEnabled = checked
                        streakManager.setQuietModeEnabled(context, checked)
                    },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF38BDF8), checkedTrackColor = Color(0xFF0284C7).copy(alpha = 0.5f))
                )
            }
        }

        // Streak Society Milestones
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "STREAK SOCIETY MILESTONES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold
            )

            milestones.forEach { m ->
                val isUnlocked = streakDays >= m.days
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isUnlocked) AmberGold.copy(alpha = 0.12f) else if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC))
                        .border(
                            1.dp,
                            if (isUnlocked) AmberGold else borderColor,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(text = m.badge, fontSize = 20.sp)
                            Column {
                                Text(
                                    text = "${m.days} Days: ${m.title}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUnlocked) AmberGold else dynamicColors.textPrimary
                                )
                                Text(text = m.description, fontSize = 10.sp, color = dynamicColors.textSecondary)
                            }
                        }

                        if (isUnlocked) {
                            Text("UNLOCKED ✓", fontSize = 10.sp, fontWeight = FontWeight.Black, color = PhoenixGreen)
                        } else {
                            Text("${m.days - streakDays}d left", fontSize = 10.sp, color = SubtextGray)
                        }
                    }
                }
            }
        }
    }
}

