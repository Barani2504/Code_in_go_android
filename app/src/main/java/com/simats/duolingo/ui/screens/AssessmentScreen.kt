package com.simats.duolingo.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.duolingo.R
import com.simats.duolingo.data.*
import com.simats.duolingo.ui.components.DuolingoButton
import com.simats.duolingo.ui.theme.*
import kotlinx.coroutines.delay

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
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var isChecked by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }
    var timeRemaining by remember { mutableIntStateOf(15) }
    var showHintDialog by remember { mutableStateOf(false) }

    // 1-second quiz timer
    LaunchedEffect(currentIndex, isChecked, isFinished) {
        if (!isChecked && !isFinished) {
            timeRemaining = 15
            while (timeRemaining > 0) {
                delay(1000)
                timeRemaining -= 1
            }
        }
    }

    if (isFinished) {
        // Lesson Completion Celebration Screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DuolingoDarkBg)
                .statusBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF9600).copy(0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.duo_backpack),
                        contentDescription = "Mascot",
                        modifier = Modifier.size(110.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Text(
                    text = "Lesson Complete!",
                    color = Color(0xFFFF9600),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black
                )

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(DuolingoCardBg)
                        .border(1.5.dp, Color(0xFFFF9600), RoundedCornerShape(16.dp))
                        .padding(horizontal = 24.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("⚡", fontSize = 22.sp)
                    Text("+10 XP EARNED", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
                }

                DuolingoButton(
                    text = "CONTINUE",
                    backgroundColor = DuolingoGreen,
                    shadowColor = DuolingoGreenDark,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    onComplete()
                }
            }
        }
        return
    }

    if (AppState.heartsCount <= 0) {
        // Out of Hearts View
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DuolingoDarkBg)
                .statusBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text("💔", fontSize = 64.sp)
                Text("You're out of hearts!", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
                Text(
                    "Hearts regenerate automatically every 5 minutes, or you can refill them in the shop.",
                    color = DuolingoSubtext,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                DuolingoButton(
                    text = "BACK TO DASHBOARD",
                    backgroundColor = DuolingoBlue,
                    shadowColor = DuolingoBlueDark,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    onDismiss()
                }
            }
        }
        return
    }

    val current = if (currentIndex < questions.size) questions[currentIndex] else questions.first()
    val isAnswerCorrect = selectedOptionIndex == current.correctIndex

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DuolingoDarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // ── 1. Top Header Bar (Close ✕, Progress Bar, Hearts Count) ──────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "✕",
                    color = DuolingoSubtext,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(onClick = onDismiss)
                )

                // Progress Bar
                val progressFraction = ((currentIndex).toFloat() / questions.size.toFloat()).coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(14.dp)
                        .clip(CircleShape)
                        .background(DuolingoInputBorder)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progressFraction)
                            .clip(CircleShape)
                            .background(DuolingoGreen)
                    )
                }

                // Hearts
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("❤️", fontSize = 18.sp)
                    Text(
                        text = "${AppState.heartsCount}",
                        color = Color.Red,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // ── Question Body ─────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header with Timer and Hint
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SELECT THE CORRECT ANSWER",
                        color = DuolingoSubtext,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        // Timer Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (timeRemaining <= 5) Color.Red.copy(0.2f) else DuolingoCardBg)
                                .border(1.dp, if (timeRemaining <= 5) Color.Red else DuolingoInputBorder, RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "⏱️ ${timeRemaining}s",
                                color = if (timeRemaining <= 5) Color.Red else Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Hint button
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(DuolingoCardBg)
                                .border(1.dp, DuolingoInputBorder, CircleShape)
                                .clickable { showHintDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("💡", fontSize = 16.sp)
                        }
                    }
                }

                // Mascot Speaker with Prompt
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.duo_backpack),
                        contentDescription = "Speaker Mascot",
                        modifier = Modifier.size(64.dp),
                        contentScale = ContentScale.Fit
                    )

                    // Speech bubble with question prompt
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(DuolingoCardBg)
                            .border(1.5.dp, DuolingoInputBorder, RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = current.prompt,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Code snippet if any
                current.codeSnippet?.let { snippet ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(18, 28, 34))
                            .border(1.dp, DuolingoInputBorder, RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = snippet,
                            color = DuolingoGreen,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Options list
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    current.options.forEachIndexed { index, option ->
                        val isSelected = selectedOptionIndex == index
                        val optionBg = when {
                            !isChecked && isSelected -> DuolingoBlue.copy(alpha = 0.2f)
                            isChecked && index == current.correctIndex -> DuolingoGreen.copy(alpha = 0.25f)
                            isChecked && isSelected && !isAnswerCorrect -> Color.Red.copy(alpha = 0.25f)
                            else -> DuolingoCardBg
                        }
                        val optionBorder = when {
                            !isChecked && isSelected -> DuolingoBlue
                            isChecked && index == current.correctIndex -> DuolingoGreen
                            isChecked && isSelected && !isAnswerCorrect -> Color.Red
                            else -> DuolingoInputBorder
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(optionBg)
                                .border(2.dp, optionBorder, RoundedCornerShape(16.dp))
                                .clickable(enabled = !isChecked) {
                                    selectedOptionIndex = index
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Shortcut badge (1, 2, 3, 4)
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DuolingoInputBg)
                                    .border(1.dp, DuolingoInputBorder, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    color = DuolingoSubtext,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = option,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // ── Bottom Action & Feedback Bar ─────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        when {
                            !isChecked -> DuolingoDarkBg
                            isAnswerCorrect -> DuolingoGreen.copy(0.18f)
                            else -> Color.Red.copy(0.18f)
                        }
                    )
                    .border(
                        1.dp,
                        when {
                            !isChecked -> DuolingoInputBorder
                            isAnswerCorrect -> DuolingoGreen
                            else -> Color.Red
                        },
                        RoundedCornerShape(0.dp)
                    )
                    .padding(20.dp)
            ) {
                if (!isChecked) {
                    DuolingoButton(
                        text = "CHECK",
                        backgroundColor = DuolingoGreen,
                        shadowColor = DuolingoGreenDark,
                        enabled = selectedOptionIndex != null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        isChecked = true
                        if (selectedOptionIndex != current.correctIndex) {
                            onHeartLost()
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(if (isAnswerCorrect) "✓" else "✕", color = if (isAnswerCorrect) DuolingoGreen else Color.Red, fontSize = 24.sp, fontWeight = FontWeight.Black)
                            Column {
                                Text(
                                    text = if (isAnswerCorrect) "Nicely done!" else "Correct solution:",
                                    color = if (isAnswerCorrect) DuolingoGreen else Color.Red,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black
                                )
                                if (!isAnswerCorrect) {
                                    Text(
                                        text = current.options[current.correctIndex],
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        DuolingoButton(
                            text = "CONTINUE",
                            backgroundColor = if (isAnswerCorrect) DuolingoGreen else Color.Red,
                            shadowColor = if (isAnswerCorrect) DuolingoGreenDark else Color(180, 0, 0),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (currentIndex + 1 < questions.size) {
                                currentIndex += 1
                                selectedOptionIndex = null
                                isChecked = false
                            } else {
                                isFinished = true
                            }
                        }
                    }
                }
            }
        }

        // Hint Dialog
        if (showHintDialog) {
            AlertDialog(
                onDismissRequest = { showHintDialog = false },
                title = { Text("Lesson Hint", color = Color.White, fontWeight = FontWeight.Bold) },
                text = { Text("Look carefully at the keyword and syntax rules for this language.", color = DuolingoSubtext) },
                confirmButton = {
                    TextButton(onClick = { showHintDialog = false }) {
                        Text("Got it", color = DuolingoGreen, fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = DuolingoCardBg
            )
        }
    }
}
