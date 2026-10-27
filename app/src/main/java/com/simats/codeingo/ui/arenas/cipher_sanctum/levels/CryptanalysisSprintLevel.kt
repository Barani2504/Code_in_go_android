package com.simats.codeingo.ui.arenas.cipher_sanctum.levels

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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.components.Tactile3DButton
import com.simats.codeingo.ui.components.Tactile3DButtonColor
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.ArenaDangerRed
import com.simats.codeingo.ui.theme.ArenaSuccessGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import kotlinx.coroutines.delay

data class SprintProblem(
    val prompt: String,
    val targetLetter: Char,
    val options: List<Char>
)

@Composable
fun CryptanalysisSprintLevel(onComplete: () -> Unit, onDeductHeart: () -> Unit) {
    val dynamicColors = LocalDynamicThemeColors.current

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

    // Monotonic timer loop
    LaunchedEffect(isTimerRunning, secondsLeft) {
        if (isTimerRunning && secondsLeft > 0 && solvedIndex < problems.size) {
            delay(1000)
            secondsLeft--
        } else if (secondsLeft == 0 && solvedIndex < problems.size) {
            isTimerRunning = false
            if (!hasTimerDeductedHeart) {
                hasTimerDeductedHeart = true
                onDeductHeart()
                feedbackMessage = "⏱️ Time expired! -1 Heart. Tap Retry to restart sprint."
            }
        }
    }

    val currentProblem = problems.getOrNull(solvedIndex)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Timer Banner
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (secondsLeft < 5) ArenaDangerRed else AmberGold)
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text(
                text = "⏱️ $secondsLeft SECONDS REMAINING (${solvedIndex}/${problems.size})",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
            )
        }

        // Mission Prompt Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(if (dynamicColors.isDark) Color(0xFF0F1A2E) else dynamicColors.cardBackground)
                .border(1.dp, AmberGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "RAPID MODULAR SPRINT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = AmberGold
                )
                Text(
                    text = currentProblem?.prompt ?: "All puzzles cleared!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = dynamicColors.textPrimary,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Interactive Options (with dynamic options per problem and 48dp+ touch target)
        if (currentProblem != null && secondsLeft > 0) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                currentProblem.options.forEach { opt ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (dynamicColors.isDark) Color(0xFF14243C) else dynamicColors.inputBackground)
                            .border(1.5.dp, AmberGold.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .clickable {
                                val now = System.currentTimeMillis()
                                if (opt == currentProblem.targetLetter) {
                                    feedbackMessage = "✓ Correct! Next sprint puzzle!"
                                    val nextIndex = solvedIndex + 1
                                    if (nextIndex >= problems.size) {
                                        solvedIndex = nextIndex
                                        isTimerRunning = false
                                        onComplete()
                                    } else {
                                        solvedIndex = nextIndex
                                    }
                                } else {
                                    // Debounce penalty (800ms) to prevent rapid accidental taps from draining all hearts
                                    if (now - lastPenaltyTimestamp > 800L) {
                                        lastPenaltyTimestamp = now
                                        onDeductHeart()
                                        feedbackMessage = "❌ Incorrect letter: '$opt'. Shift backwards by 3!"
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$opt",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = dynamicColors.textPrimary
                        )
                    }
                }
            }
        }

        if (feedbackMessage != null) {
            Text(
                text = feedbackMessage!!,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (feedbackMessage!!.startsWith("✓")) ArenaSuccessGreen else ArenaDangerRed,
                textAlign = TextAlign.Center
            )
        }

        // Retry button if timer ran out
        if (secondsLeft == 0 && solvedIndex < problems.size) {
            Spacer(modifier = Modifier.height(4.dp))
            Tactile3DButton(
                title = "🔄 Retry Sprint (20s)",
                style = Tactile3DButtonColor.AMBER,
                onClick = {
                    secondsLeft = 20
                    solvedIndex = 0
                    hasTimerDeductedHeart = false
                    isTimerRunning = true
                    feedbackMessage = "Sprint restarted! Move quickly!"
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
