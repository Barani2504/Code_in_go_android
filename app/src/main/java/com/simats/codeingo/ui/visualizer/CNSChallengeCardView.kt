package com.simats.codeingo.ui.visualizer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
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
import com.simats.codeingo.data.model.DSAVisualizerType
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DsaBlue
import com.simats.codeingo.ui.theme.DsaGreen
import com.simats.codeingo.ui.theme.DsaRed
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.InputBorder
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.visualizer.model.CNSChallengeQuestion
import com.simats.codeingo.ui.visualizer.model.CNSChallengeRepository

@Composable
fun CNSChallengeCardView(
    labType: DSAVisualizerType,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val amberColor = if (isDark) AmberGold else Color(0xFFB45309)
    val greenColor = if (isDark) DuolingoGreen else Color(0xFF15803D)
    val redColor = if (isDark) DsaRed else Color(0xFFDC2626)

    val questions = remember(labType) {
        CNSChallengeRepository.getChallengesForLab(labType)
    }

    // Maps question id -> selected option index
    val selectedAnswers = remember(labType) { mutableStateMapOf<String, Int>() }
    // Maps question id -> show hint boolean
    val showHints = remember(labType) { mutableStateMapOf<String, Boolean>() }

    val correctCount = questions.count { q ->
        selectedAnswers[q.id] == q.correctIndex
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header Card with Progress
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackground)
                .border(1.5.dp, AmberGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "🎯 INTERACTIVE CHALLENGES",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = amberColor
                    )
                    Text(
                        text = "Test your cryptographic intuition on ${labType.title}",
                        fontSize = 11.sp,
                        color = SubtextGray
                    )
                }

                // Score Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (correctCount == questions.size && questions.isNotEmpty()) greenColor.copy(alpha = 0.2f) else dynamicColors.inputBackground)
                        .border(1.dp, if (correctCount == questions.size && questions.isNotEmpty()) greenColor else InputBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "$correctCount / ${questions.size} Solved",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = if (correctCount == questions.size && questions.isNotEmpty()) greenColor else dynamicColors.textPrimary
                    )
                }
            }
        }

        // Questions List
        questions.forEachIndexed { qIdx, question ->
            val selectedOption = selectedAnswers[question.id]
            val isAnswered = selectedOption != null
            val isCorrect = selectedOption == question.correctIndex
            val showHint = showHints[question.id] ?: false

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardBackground)
                    .border(
                        1.5.dp,
                        when {
                            isCorrect -> greenColor.copy(alpha = 0.6f)
                            isAnswered -> redColor.copy(alpha = 0.5f)
                            else -> InputBorder
                        },
                        RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Question Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CHALLENGE #${qIdx + 1}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isCorrect) greenColor else amberColor
                        )

                        // Hint toggle button
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { showHints[question.id] = !showHint }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = "Hint",
                                tint = AmberGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (showHint) "Hide Hint" else "Hint",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberGold
                            )
                        }
                    }

                    // Question Text
                    Text(
                        text = question.question,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = dynamicColors.textPrimary,
                        lineHeight = 19.sp
                    )

                    // Hint Dropdown
                    AnimatedVisibility(
                        visible = showHint,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(AmberGold.copy(alpha = if (isDark) 0.12f else 0.08f))
                                .border(1.dp, AmberGold.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "💡 Hint: ${question.hint}",
                                fontSize = 11.sp,
                                color = amberColor,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    // Options List (Min 48dp touch target)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        question.options.forEachIndexed { optIdx, optionText ->
                            val isThisSelected = selectedOption == optIdx
                            val isThisCorrectAnswer = optIdx == question.correctIndex

                            val bgColor = when {
                                isAnswered && isThisCorrectAnswer -> greenColor.copy(alpha = 0.15f)
                                isAnswered && isThisSelected && !isCorrect -> redColor.copy(alpha = 0.15f)
                                else -> dynamicColors.inputBackground
                            }

                            val borderColor = when {
                                isAnswered && isThisCorrectAnswer -> greenColor
                                isAnswered && isThisSelected && !isCorrect -> redColor
                                else -> dynamicColors.inputBorder
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Min)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(bgColor)
                                    .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                                    .clickable {
                                        selectedAnswers[question.id] = optIdx
                                    }
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Option letter pill (A, B, C, D)
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(if (isThisSelected) (if (isCorrect) greenColor else redColor) else InputBorder.copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${('A'.code + optIdx).toChar()}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isThisSelected) Color.White else SubtextGray
                                    )
                                }

                                Text(
                                    text = optionText,
                                    fontSize = 12.sp,
                                    fontWeight = if (isThisSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = dynamicColors.textPrimary,
                                    lineHeight = 16.sp,
                                    modifier = Modifier.weight(1f)
                                )

                                if (isAnswered && isThisCorrectAnswer) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Correct",
                                        tint = greenColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                } else if (isAnswered && isThisSelected && !isCorrect) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Incorrect",
                                        tint = redColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Pedagogical Explanation Box (Revealed when answered)
                    AnimatedVisibility(
                        visible = isAnswered,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isCorrect) greenColor.copy(alpha = 0.12f) else redColor.copy(alpha = 0.08f))
                                .border(1.dp, if (isCorrect) greenColor.copy(alpha = 0.4f) else redColor.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = if (isCorrect) "✨ Correct Explanation:" else "❌ Why that's incorrect:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isCorrect) greenColor else redColor
                                )
                                Text(
                                    text = question.explanation,
                                    fontSize = 11.5.sp,
                                    color = dynamicColors.textPrimary,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Reset Button
        if (selectedAnswers.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(dynamicColors.inputBackground)
                    .border(1.dp, dynamicColors.inputBorder, RoundedCornerShape(12.dp))
                    .clickable { selectedAnswers.clear(); showHints.clear() }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Challenges",
                        tint = SubtextGray,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Reset All Challenges for this Lab",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SubtextGray
                    )
                }
            }
        }
    }
}
