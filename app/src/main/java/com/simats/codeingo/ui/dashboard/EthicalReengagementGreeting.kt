package com.simats.codeingo.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.components.motion.EffectBudgetManager
import com.simats.codeingo.ui.mascot.MascotEmotion
import com.simats.codeingo.ui.mascot.MascotView
import com.simats.codeingo.ui.mascot.PhoenixStage
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.AppFontFamily
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray
import java.util.Calendar

/**
 * Ethical Re-engagement Greeting Card.
 *
 * Implements Part 2E:
 * - Warm, respectful mascot greeting based on wall-clock time of day.
 * - Detects Quiet Hours (22:00 - 07:00) and displays a restful message with zero guilt copy.
 * - "Welcome back" mini goal encouragement: "1 node keeps your progress safe."
 */
@Composable
fun EthicalReengagementGreeting(
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current

    val (greetingTitle, greetingSubtitle, mascotEmotion) = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when {
            hour in 5..11 -> Triple(
                "Good Morning, Defender!",
                "The cipher gates are open. Complete 1 node to advance.",
                MascotEmotion.HAPPY
            )
            hour in 12..17 -> Triple(
                "Good Afternoon!",
                "Ready to master block ciphers and key exchanges?",
                MascotEmotion.CURIOUS
            )
            hour in 18..21 -> Triple(
                "Good Evening!",
                "One quick challenge keeps your cryptosystem secure.",
                MascotEmotion.DETERMINED
            )
            else -> Triple(
                "Quiet Hours Active 🌙",
                "Rest easy! Your streak is safely preserved tonight.",
                MascotEmotion.SLEEPY
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF0F172A))
            .border(1.2.dp, Color(0xFF1E293B), RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            MascotView(
                emotion = mascotEmotion,
                stage = PhoenixStage.FLEDGED,
                size = 54.dp,
                showGlow = false
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = greetingTitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = AppFontFamily,
                    color = dynamicColors.textPrimary
                )
                Text(
                    text = greetingSubtitle,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = SubtextGray,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

