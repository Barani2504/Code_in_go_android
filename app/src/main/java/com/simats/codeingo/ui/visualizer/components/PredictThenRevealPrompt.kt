package com.simats.codeingo.ui.visualizer.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.ui.components.motion.pressSquish
import com.simats.codeingo.ui.motion.SoundEffectsManager
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.PhoenixCrimson
import com.simats.codeingo.ui.theme.PhoenixGreen
import com.simats.codeingo.ui.theme.SubtextGray

data class PredictionOption(
    val id: String,
    val text: String,
    val isCorrect: Boolean,
    val explanation: String
)

/**
 * 🔮 Predict-Then-Reveal Active Learning Prompt
 *
 * Implements Phase 4.1:
 * - Prompts learner to predict the outcome of the next cryptographic transformation
 * - Awards +5 XP on correct guess with sensory sound cue
 * - Turns passive watching into active cognitive engagement
 */
@Composable
fun PredictThenRevealPrompt(
    title: String = "PREDICT THE NEXT TRANSFORMATION",
    prompt: String,
    options: List<PredictionOption>,
    onRevealStep: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    var selectedOptionId by remember { mutableStateOf<String?>(null) }
    var hasAnswered by remember { mutableStateOf(false) }

    val cardBg = if (isDark) Color(0xFF131A2A) else Color(0xFFF8FAFC)
    val borderColor = if (isDark) AmberGold.copy(alpha = 0.5f) else AmberGold.copy(alpha = 0.7f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(cardBg)
            .border(1.5.dp, borderColor, RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("🔮", fontSize = 16.sp)
                    Text(
                        text = title,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = AmberGold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AmberGold.copy(alpha = 0.15f))
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "+5 BONUS XP",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black,
                        color = AmberGold
                    )
                }
            }

            // Prompt Question
            Text(
                text = prompt,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = dynamicColors.textPrimary
            )

            // Options List
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                options.forEach { option ->
                    val isSelected = selectedOptionId == option.id
                    val itemBg = when {
                        !hasAnswered -> if (isDark) Color(0xFF1E293B) else Color.White
                        isSelected && option.isCorrect -> PhoenixGreen.copy(alpha = 0.18f)
                        isSelected && !option.isCorrect -> PhoenixCrimson.copy(alpha = 0.18f)
                        option.isCorrect -> PhoenixGreen.copy(alpha = 0.10f)
                        else -> if (isDark) Color(0xFF1E293B) else Color.White
                    }

                    val itemBorder = when {
                        !hasAnswered -> if (isSelected) AmberGold else if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
                        isSelected && option.isCorrect -> PhoenixGreen
                        isSelected && !option.isCorrect -> PhoenixCrimson
                        option.isCorrect -> PhoenixGreen.copy(alpha = 0.5f)
                        else -> if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pressSquish {
                                if (!hasAnswered) {
                                    selectedOptionId = option.id
                                    hasAnswered = true
                                    if (option.isCorrect) {
                                        SoundEffectsManager.playCorrect()
                                        GameManager.instance.addXP(5)
                                    } else {
                                        SoundEffectsManager.playWrong()
                                    }
                                }
                            }
                            .clip(RoundedCornerShape(12.dp))
                            .background(itemBg)
                            .border(1.2.dp, itemBorder, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = option.text,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = dynamicColors.textPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            if (hasAnswered) {
                                if (option.isCorrect) {
                                    Text("✅ CORRECT", fontSize = 10.sp, fontWeight = FontWeight.Black, color = PhoenixGreen)
                                } else if (isSelected) {
                                    Text("❌ REVEALED", fontSize = 10.sp, fontWeight = FontWeight.Black, color = PhoenixCrimson)
                                }
                            }
                        }
                    }
                }
            }

            // Post-answer Explanation & Reveal Action Button
            AnimatedVisibility(visible = hasAnswered, enter = fadeIn(), exit = fadeOut()) {
                val chosen = options.firstOrNull { it.id == selectedOptionId }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (chosen != null) {
                        Text(
                            text = chosen.explanation,
                            fontSize = 11.sp,
                            color = dynamicColors.textSecondary,
                            lineHeight = 15.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pressSquish {
                                SoundEffectsManager.playTap()
                                onRevealStep()
                            }
                            .clip(RoundedCornerShape(12.dp))
                            .background(PhoenixGreen)
                            .padding(vertical = 11.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "CONTINUE TRANSFORMATION ▶",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
