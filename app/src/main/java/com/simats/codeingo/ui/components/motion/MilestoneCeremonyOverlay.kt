package com.simats.codeingo.ui.components.motion

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.mascot.MascotEmotion
import com.simats.codeingo.ui.mascot.MascotView
import com.simats.codeingo.ui.mascot.PhoenixStage
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.AppFontFamily
import com.simats.codeingo.ui.theme.PhoenixGreen
import com.simats.codeingo.ui.theme.SubtextGray

/**
 * Milestone Ceremony Type (MotionTier.T4_MILESTONE)
 */
enum class MilestoneType(val title: String, val badge: String, val defaultRewardXp: Int) {
    CHAPTER_CLEAR("CHAPTER DEFENDED!", "👑", 50),
    GRAND_MASTER("GRAND MASTER OF CRYPTOGRAPHY!", "🏆", 150),
    LEAGUE_PROMOTION("LEAGUE PROMOTION!", "⚡", 35),
    STREAK_MILESTONE("STREAK FLAME IGNITED!", "🔥", 25)
}

/**
 * High-Impact Full-Screen Milestone Ceremony Overlay.
 *
 * Implements MotionTier.T4:
 * - Emits festive cryptographic party showers via [ConfettiHost].
 * - Full ceremonial entry animation with spring bounce.
 * - Rolling digit number counters for XP and rewards.
 * - Reactive companion mascot in celebrate pose.
 */
@Composable
fun MilestoneCeremonyOverlay(
    visible: Boolean,
    type: MilestoneType = MilestoneType.CHAPTER_CLEAR,
    chapterNumber: Int = 1,
    rewardXp: Int = type.defaultRewardXp,
    onContinue: () -> Unit
) {
    if (!visible) return

    val view = LocalView.current
    val scaleAnim = remember { Animatable(0.4f) }

    LaunchedEffect(visible) {
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        scaleAnim.animateTo(
            1f,
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.82f)),
        contentAlignment = Alignment.Center
    ) {
        // Ambient Celebration Glow Field
        CelebrationGlowField(
            modifier = Modifier.fillMaxSize(),
            color = AmberGold
        )

        // 1. T4 Konfetti Particle Shower
        ConfettiHost(trigger = true)

        // 2. High-Impact Modal Container
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .graphicsLayer {
                    scaleX = scaleAnim.value
                    scaleY = scaleAnim.value
                }
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(AmberGold, Color(0xFF10B981), Color(0xFF38BDF8))
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Radiant Mascot in Celebration Pose
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(AmberGold.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                MascotView(
                    emotion = MascotEmotion.CELEBRATE,
                    stage = PhoenixStage.DIVINE_PHOENIX,
                    size = 72.dp,
                    showGlow = false
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = type.badge,
                fontSize = 32.sp
            )

            Text(
                text = type.title,
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.Black,
                fontSize = 22.sp,
                color = AmberGold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (type == MilestoneType.CHAPTER_CLEAR) {
                    "All security nodes in Chapter $chapterNumber successfully secured."
                } else if (type == MilestoneType.GRAND_MASTER) {
                    "You have mastered all 5 realms of Cryptography & Network Security!"
                } else {
                    "Your cryptographic skills have elevated your global league standing."
                },
                fontSize = 13.sp,
                color = SubtextGray,
                textAlign = TextAlign.Center,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // XP Award Card with Number Roll
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.06f))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("⚡", fontSize = 20.sp)
                    NumberRollText(
                        targetValue = rewardXp,
                        prefix = "+",
                        suffix = " XP",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            color = AmberGold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Single Clear Action Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .pressSquish(onClick = onContinue)
                    .clip(RoundedCornerShape(14.dp))
                    .background(PhoenixGreen)
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "CLAIM CEREMONY REWARD ➔",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }
    }
}

