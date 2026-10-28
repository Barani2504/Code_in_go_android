package com.simats.codeingo.ui.arenas.cipher_sanctum.levels

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.arenas.common.ArenaBadgeType
import com.simats.codeingo.ui.arenas.common.ArenaHaptics
import com.simats.codeingo.ui.arenas.common.ArenaPalettes
import com.simats.codeingo.ui.arenas.common.ArenaStatusBadge
import com.simats.codeingo.ui.arenas.common.minTouchTarget48
import com.simats.codeingo.ui.components.Tactile3DButton
import com.simats.codeingo.ui.components.Tactile3DButtonColor
import com.simats.codeingo.ui.theme.ArenaDangerRed
import com.simats.codeingo.ui.theme.ArenaSuccessGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.LocalReduceMotion
import com.simats.codeingo.ui.theme.SubtextGray
import kotlinx.coroutines.delay

data class SprintProblem(
    val prompt: String,
    val targetLetter: Char,
    val options: List<Char>
)

@Composable
fun CryptanalysisSprintLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val view = LocalView.current
    val reduceMotion = LocalReduceMotion.current
    val isDark = dynamicColors.isDark

    val primary = ArenaPalettes.Arena1Primary
    val accent = ArenaPalettes.Arena1Accent
    val cardBg = if (isDark) ArenaPalettes.Arena1CardDark else dynamicColors.cardBackground

    val problems = remember {
        listOf(
            SprintProblem("Shift 'K' backwards by 3:", 'H', listOf('G', 'H', 'I', 'J')),
            SprintProblem("Shift 'D' backwards by 3:", 'A', listOf('A', 'B', 'C', 'Z')),
            SprintProblem("Shift 'O' backwards by 3:", 'L', listOf('K', 'L', 'M', 'N'))
        )
    }

    var secondsLeft by remember { mutableIntStateOf(20) }
    var solvedIndex by remember { mutableIntStateOf(0) }
    var isTimerRunning by remember { mutableStateOf(true) }
    var hasTimerDeductedHeart by remember { mutableStateOf(false) }
    var lastPenaltyTimestamp by remember { mutableLongStateOf(0L) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var lastCheckSuccess by remember { mutableStateOf<Boolean?>(null) }

    // Monotonic timer loop
    LaunchedEffect(isTimerRunning, secondsLeft) {
        if (isTimerRunning && secondsLeft > 0 && solvedIndex < problems.size) {
            delay(1000)
            secondsLeft--
        } else if (secondsLeft == 0 && solvedIndex < problems.size) {
            isTimerRunning = false
            if (!hasTimerDeductedHeart) {
                hasTimerDeductedHeart = true
                ArenaHaptics.error(view, reduceMotion)
                onDeductHeart()
                feedbackMessage = "⏱️ Time expired! -1 Heart. Tap Restart to try again."
            }
        }
    }

    val currentProblem = problems.getOrNull(solvedIndex)
    val isFinished = solvedIndex >= problems.size

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Mission Header with Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "CRYPTANALYSIS SPEED SPRINT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = primary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "High-Pressure Modular Arithmetic",
                    fontSize = 10.sp,
                    color = dynamicColors.textSecondary
                )
            }
            ArenaStatusBadge(
                type = when {
                    isFinished -> ArenaBadgeType.SUCCESS
                    secondsLeft <= 5 -> ArenaBadgeType.DANGER
                    else -> ArenaBadgeType.WARNING
                },
                label = if (isFinished) "SPRINT CLEARED" else "⏱️ ${secondsLeft}s LEFT"
            )
        }

        // Mission Prompt Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(cardBg)
                .border(1.dp, primary.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "QUESTION ${minOf(solvedIndex + 1, problems.size)} OF ${problems.size}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = primary
                    )
                }
                Text(
                    text = currentProblem?.prompt ?: "All sprint challenges cleared!",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = dynamicColors.textPrimary,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Interactive Options (minTouchTarget48 compliant)
        if (currentProblem != null && secondsLeft > 0) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                currentProblem.options.forEach { opt ->
                    val isCorrect = opt == currentProblem.targetLetter

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .minTouchTarget48()
                            .clip(RoundedCornerShape(12.dp))
                            .background(cardBg)
                            .border(1.2.dp, primary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .clickable {
                                ArenaHaptics.tap(view, reduceMotion)
                                if (isCorrect) {
                                    ArenaHaptics.success(view, reduceMotion)
                                    lastCheckSuccess = true
                                    onActionSuccess()
                                    solvedIndex++
                                    feedbackMessage = "✓ Correct! Shifted backwards: $opt"
                                    if (solvedIndex >= problems.size) {
                                        isTimerRunning = false
                                        onComplete()
                                    }
                                } else {
                                    val now = System.currentTimeMillis()
                                    if (now - lastPenaltyTimestamp > 1500) {
                                        lastPenaltyTimestamp = now
                                        ArenaHaptics.error(view, reduceMotion)
                                        lastCheckSuccess = false
                                        onDeductHeart()
                                        feedbackMessage = "❌ Incorrect choice '$opt'! Modular shift: C - 3."
                                    }
                                }
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$opt",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = primary
                        )
                    }
                }
            }
        }

        // Live Feedback Message
        AnimatedVisibility(visible = feedbackMessage != null, enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (lastCheckSuccess == true) ArenaSuccessGreen.copy(alpha = 0.12f)
                        else ArenaDangerRed.copy(alpha = 0.12f)
                    )
                    .border(
                        1.dp,
                        if (lastCheckSuccess == true) ArenaSuccessGreen else ArenaDangerRed,
                        RoundedCornerShape(10.dp)
                    )
                    .padding(10.dp)
            ) {
                Text(
                    text = feedbackMessage ?: "",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (lastCheckSuccess == true) ArenaSuccessGreen else ArenaDangerRed,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Restart button if time ran out
        if (secondsLeft == 0 && !isFinished) {
            Tactile3DButton(
                title = "Restart Speed Sprint ↺",
                style = Tactile3DButtonColor.AMBER,
                onClick = {
                    secondsLeft = 20
                    solvedIndex = 0
                    isTimerRunning = true
                    hasTimerDeductedHeart = false
                    feedbackMessage = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .minTouchTarget48()
            )
        }
    }
}
