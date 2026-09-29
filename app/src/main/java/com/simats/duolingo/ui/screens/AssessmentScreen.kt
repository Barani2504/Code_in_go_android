package com.simats.duolingo.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.simats.duolingo.data.*
import com.simats.duolingo.ui.components.DuolingoButton
import com.simats.duolingo.ui.theme.*

@Composable
fun AssessmentScreen(
    unitId: Int,
    levelNumber: Int,
    heartsCount: Int,
    onHeartLost: () -> Unit,
    onComplete: () -> Unit,
    onDismiss: () -> Unit,
) {
    val questions = remember(unitId) { questionsForUnit(unitId) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableIntStateOf(-1) }
    var answered by remember { mutableStateOf(false) }
    var correctStreak by remember { mutableIntStateOf(0) }
    var hp by remember { mutableIntStateOf(heartsCount) }

    val current = if (currentIndex < questions.size) questions[currentIndex] else null

    if (current == null) {
        // All questions done
        LaunchedEffect(Unit) { onComplete() }
        return
    }

    val isCorrect = selectedOption == current.correctIndex

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DuolingoDarkBg)
    ) {
        // ── Top bar ────────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("✕", color = DuolingoSubtext, fontSize = 22.sp,
                modifier = Modifier.clickable(onClick = onDismiss))

            // Progress bar
            val progress = currentIndex.toFloat() / questions.size.toFloat()
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(DuolingoInputBorder)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .clip(RoundedCornerShape(50))
                        .background(DuolingoGreen)
                )
            }

            // Hearts
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("❤️", fontSize = 18.sp)
                Text("$hp", color = Color.Red, fontSize = 15.sp, fontWeight = FontWeight.Black)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // ── Prompt ────────────────────────────────────────────────────────
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("🦜", fontSize = 64.sp)
                Text(
                    text = current.prompt,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                current.codeSnippet?.let { snippet ->
                    Text(
                        text = snippet,
                        color = DuolingoGreen,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(DuolingoInputBg)
                            .padding(12.dp)
                            .fillMaxWidth()
                    )
                }
            }

            // ── Answer choices ────────────────────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                current.options.forEachIndexed { idx, option ->
                    val bgColor = when {
                        !answered           -> DuolingoInputBg
                        idx == current.correctIndex -> DuolingoGreen.copy(alpha = 0.25f)
                        idx == selectedOption       -> Color.Red.copy(alpha = 0.2f)
                        else                       -> DuolingoInputBg
                    }
                    val borderColor = when {
                        !answered           -> DuolingoInputBorder
                        idx == current.correctIndex -> DuolingoGreen
                        idx == selectedOption       -> Color.Red
                        else                       -> DuolingoInputBorder
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(bgColor)
                            .border(2.dp, borderColor, RoundedCornerShape(14.dp))
                            .clickable(enabled = !answered) {
                                selectedOption = idx
                                answered = true
                                if (idx != current.correctIndex) {
                                    hp = (hp - 1).coerceAtLeast(0)
                                    onHeartLost()
                                } else {
                                    correctStreak++
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Letter badge
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(DuolingoCardBg)
                                .border(1.dp, DuolingoInputBorder, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = ('A' + idx).toString(),
                                color = DuolingoSubtext,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(option, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Feedback footer ───────────────────────────────────────────────
            if (answered) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isCorrect) DuolingoGreen.copy(0.15f) else Color.Red.copy(0.12f))
                        .border(
                            2.dp,
                            if (isCorrect) DuolingoGreen else Color.Red,
                            RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = if (isCorrect) "🎉 Correct!" else "❌ Wrong – the answer was: ${current.options[current.correctIndex]}",
                            color = if (isCorrect) DuolingoGreen else Color.Red,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        DuolingoButton(
                            text = if (currentIndex < questions.size - 1) "CONTINUE" else "FINISH",
                            backgroundColor = if (isCorrect) DuolingoGreen else DuolingoBlue,
                            shadowColor = if (isCorrect) DuolingoGreenDark else DuolingoBlueDark,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                currentIndex++
                                selectedOption = -1
                                answered = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
