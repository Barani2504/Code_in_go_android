package com.simats.duolingo.feature.lesson

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.duolingo.core.sound.rememberJuiceFeedback
import com.simats.duolingo.core.theme.*
import com.simats.duolingo.data.AppState
import com.simats.duolingo.data.repository.GameStateRepository
import com.simats.duolingo.domain.model.*
import com.simats.duolingo.ui.components.PhoenixCreature
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Interactive Lesson Player for the DSA Course:
 * Hosts exercises with progress tracking, combo multipliers, juice haptics,
 * feedback banners, and lesson completion celebration.
 */
@Composable
fun DsaLessonPlayerScreen(
    lesson: DsaLesson,
    onFinishLesson: () -> Unit,
    modifier: Modifier = Modifier
) {
    val juiceFeedback = rememberJuiceFeedback()
    val scope = rememberCoroutineScope()

    var currentExerciseIndex by remember { mutableIntStateOf(0) }
    var correctCount by remember { mutableIntStateOf(0) }
    var isLessonCompleted by remember { mutableStateOf(false) }

    // Answer evaluation state
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var selectedTrueFalse by remember { mutableStateOf<Boolean?>(null) }
    var selectedComplexity by remember { mutableStateOf<String?>(null) }
    var placedTokens by remember { mutableStateOf<List<String>>(emptyList()) }

    var feedbackState by remember { mutableStateOf<FeedbackState>(FeedbackState.None) }
    val shakeOffset = remember { Animatable(0f) }

    val combo by GameStateRepository.combo.collectAsState()
    val currentExercise = lesson.exercises.getOrNull(currentExerciseIndex)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DsaCourseBackgroundGradient)
    ) {
        if (isLessonCompleted) {
            // ── Lesson Completion Celebration ───────────────────────────────
            val accuracy = if (lesson.exercises.isNotEmpty()) correctCount.toFloat() / lesson.exercises.size else 1f
            val (earnedXp, earnedGems) = remember {
                GameStateRepository.completeLesson(lesson, accuracy)
            }

            LessonVictoryScreen(
                lesson = lesson,
                earnedXp = earnedXp,
                earnedGems = earnedGems,
                accuracy = accuracy,
                onContinue = onFinishLesson
            )
        } else if (currentExercise != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 18.dp, vertical = 12.dp)
            ) {
                // ── Top Header Bar ──────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onFinishLesson) {
                        Text("✕", color = Color.White.copy(0.7f), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }

                    // Progress Bar
                    val progress = (currentExerciseIndex.toFloat() / lesson.exercises.size).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .weight(1f)
                            .height(14.dp)
                            .padding(horizontal = 12.dp)
                            .clip(RoundedCornerShape(50)),
                        color = DsaGreenPrimary,
                        trackColor = Color(0x33FFFFFF)
                    )

                    // Combo Flame & Hearts
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (combo > 1) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFFF9600))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("🔥 x$combo", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                            }
                        }

                        Text("❤️ ${AppState.heartsCount}", color = Color(0xFFFF4B4B), fontSize = 14.sp, fontWeight = FontWeight.Black)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ── Exercise Host Content ───────────────────────────────────
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .graphicsLayer { translationX = shakeOffset.value }
                ) {
                    when (currentExercise) {
                        is MultipleChoiceExercise -> {
                            MultipleChoiceView(
                                exercise = currentExercise,
                                selectedIndex = selectedOptionIndex,
                                onSelect = {
                                    if (feedbackState == FeedbackState.None) selectedOptionIndex = it
                                }
                            )
                        }
                        is TrueFalseSwipeExercise -> {
                            TrueFalseView(
                                exercise = currentExercise,
                                selectedAnswer = selectedTrueFalse,
                                onSelect = {
                                    if (feedbackState == FeedbackState.None) selectedTrueFalse = it
                                }
                            )
                        }
                        is ComplexityDialExercise -> {
                            ComplexityDialView(
                                exercise = currentExercise,
                                selectedComplexity = selectedComplexity,
                                onSelect = {
                                    if (feedbackState == FeedbackState.None) selectedComplexity = it
                                }
                            )
                        }
                        is FillCodeExercise -> {
                            FillCodeView(
                                exercise = currentExercise,
                                placedTokens = placedTokens,
                                onToggleToken = { token ->
                                    if (feedbackState == FeedbackState.None) {
                                        placedTokens = if (placedTokens.contains(token)) {
                                            placedTokens - token
                                        } else {
                                            placedTokens + token
                                        }
                                    }
                                }
                            )
                        }
                        else -> {
                            // Generic interactive prompt fallback
                            GenericExerciseView(
                                exercise = currentExercise,
                                selectedIndex = selectedOptionIndex,
                                onSelect = {
                                    if (feedbackState == FeedbackState.None) selectedOptionIndex = it
                                }
                            )
                        }
                    }
                }

                // ── Check / Continue Bottom Action Button ───────────────────
                if (feedbackState == FeedbackState.None) {
                    val canCheck = when (currentExercise) {
                        is MultipleChoiceExercise -> selectedOptionIndex != null
                        is TrueFalseSwipeExercise -> selectedTrueFalse != null
                        is ComplexityDialExercise -> selectedComplexity != null
                        is FillCodeExercise -> placedTokens.isNotEmpty()
                        else -> selectedOptionIndex != null
                    }

                    Button(
                        onClick = {
                            val isCorrect = when (currentExercise) {
                                is MultipleChoiceExercise -> selectedOptionIndex == currentExercise.correctIndex
                                is TrueFalseSwipeExercise -> selectedTrueFalse == currentExercise.isTrue
                                is ComplexityDialExercise -> selectedComplexity == currentExercise.correctComplexity
                                is FillCodeExercise -> placedTokens == currentExercise.correctTokens
                                else -> selectedOptionIndex == 0
                            }

                            if (isCorrect) {
                                correctCount++
                                GameStateRepository.registerCorrectAnswer()
                                juiceFeedback.onCorrectAnswer(combo)
                                feedbackState = FeedbackState.Correct(currentExercise.explanation)
                            } else {
                                GameStateRepository.registerWrongAnswer()
                                juiceFeedback.onWrongAnswer()
                                scope.launch {
                                    shakeOffset.snapTo(0f)
                                    shakeOffset.animateTo(24f, tween(50))
                                    shakeOffset.animateTo(-24f, tween(50))
                                    shakeOffset.animateTo(12f, tween(50))
                                    shakeOffset.animateTo(0f, tween(50))
                                }
                                feedbackState = FeedbackState.Wrong(currentExercise.explanation)
                            }
                        },
                        enabled = canCheck,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DsaGreenPrimary,
                            disabledContainerColor = Color(0x33FFFFFF)
                        )
                    ) {
                        Text("CHECK ANSWER", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
                    }
                }
            }

            // ── Feedback Banner (Slides up from bottom) ─────────────────────
            AnimatedVisibility(
                visible = feedbackState !is FeedbackState.None,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                FeedbackBanner(
                    state = feedbackState,
                    onContinue = {
                        feedbackState = FeedbackState.None
                        selectedOptionIndex = null
                        selectedTrueFalse = null
                        selectedComplexity = null
                        placedTokens = emptyList()

                        if (currentExerciseIndex + 1 < lesson.exercises.size) {
                            currentExerciseIndex++
                        } else {
                            isLessonCompleted = true
                        }
                    }
                )
            }
        }
    }
}

/* ================================================================== */
/*  EXERCISE COMPONENT VIEWS                                          */
/* ================================================================== */

@Composable
private fun MultipleChoiceView(
    exercise: MultipleChoiceExercise,
    selectedIndex: Int?,
    onSelect: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = exercise.prompt,
            color = Color.White,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 26.sp
        )

        exercise.codeSnippet?.let { code ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF14122C))
                    .border(1.dp, Color(0xFF3B325E), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = code,
                    color = Color(0xFFFFD54F),
                    fontSize = 13.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        exercise.options.forEachIndexed { index, option ->
            val isSelected = selectedIndex == index
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) Color(0xFF2C2260) else Color(0xFF201B42))
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) DsaGreenPrimary else Color(0xFF382F63),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { onSelect(index) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) DsaGreenPrimary else Color(0x22FFFFFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "${('A' + index)}",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Text(
                    text = option,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TrueFalseView(
    exercise: TrueFalseSwipeExercise,
    selectedAnswer: Boolean?,
    onSelect: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = exercise.prompt,
            color = Color.White.copy(0.7f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF241D4D))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("STATEMENT", color = Color(0xFFFFD54F), fontSize = 11.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "\"${exercise.statement}\"",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 26.sp
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { onSelect(false) },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedAnswer == false) DsaRedPrimary else Color(0xFF2B2050)
                )
            ) {
                Text("FALSE ✕", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
            }

            Button(
                onClick = { onSelect(true) },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedAnswer == true) DsaGreenPrimary else Color(0xFF2B2050)
                )
            ) {
                Text("TRUE ✓", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun ComplexityDialView(
    exercise: ComplexityDialExercise,
    selectedComplexity: String?,
    onSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = exercise.prompt,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 25.sp
        )

        exercise.codeSnippet?.let { code ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF14122C))
                    .border(1.dp, Color(0xFF3B325E), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = code,
                    color = Color(0xFFFFD54F),
                    fontSize = 13.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }
        }

        Text(
            "SELECT THE ASYMPTOTIC RUNTIME:",
            color = Color.White.copy(0.6f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        exercise.options.chunked(2).forEach { rowOptions ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowOptions.forEach { opt ->
                    val isSelected = selectedComplexity == opt
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) Color(0xFF2C2260) else Color(0xFF201B42))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) DsaYellowPrimary else Color(0xFF382F63),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { onSelect(opt) }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = opt,
                            color = if (isSelected) DsaYellowPrimary else Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FillCodeView(
    exercise: FillCodeExercise,
    placedTokens: List<String>,
    onToggleToken: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = exercise.prompt,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF14122C))
                .border(1.dp, Color(0xFF3B325E), RoundedCornerShape(16.dp))
                .padding(18.dp)
        ) {
            Text(
                text = exercise.codeWithBlanks,
                color = Color(0xFFFFD54F),
                fontSize = 14.sp,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                lineHeight = 22.sp
            )
        }

        Text("TAP TO SELECT TOKENS IN ORDER:", color = Color.White.copy(0.6f), fontSize = 11.sp, fontWeight = FontWeight.Bold)

        // Draggable / Tap token chips
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            exercise.tokens.forEach { token ->
                val isSelected = placedTokens.contains(token)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) DsaGreenPrimary else Color(0xFF26204E))
                        .border(1.dp, if (isSelected) Color.White else Color(0xFF3C3264), RoundedCornerShape(12.dp))
                        .clickable { onToggleToken(token) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = token,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
private fun GenericExerciseView(
    exercise: Exercise,
    selectedIndex: Int?,
    onSelect: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = exercise.prompt,
            color = Color.White,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold
        )

        listOf("Option A: Correct Pattern", "Option B: Flawed Pattern").forEachIndexed { i, opt ->
            val isSelected = selectedIndex == i
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) Color(0xFF2C2260) else Color(0xFF201B42))
                    .border(1.dp, if (isSelected) DsaGreenPrimary else Color(0xFF382F63), RoundedCornerShape(16.dp))
                    .clickable { onSelect(i) }
                    .padding(16.dp)
            ) {
                Text(opt, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

/* ================================================================== */
/*  FEEDBACK BANNER                                                   */
/* ================================================================== */

sealed interface FeedbackState {
    object None : FeedbackState
    data class Correct(val explanation: String) : FeedbackState
    data class Wrong(val explanation: String) : FeedbackState
}

@Composable
private fun FeedbackBanner(
    state: FeedbackState,
    onContinue: () -> Unit
) {
    val isCorrect = state is FeedbackState.Correct
    val bannerColor = if (isCorrect) Color(0xFF1B4D20) else Color(0xFF5A1A22)
    val titleText = if (isCorrect) "EXCELLENT! ✨" else "NOT QUITE! 💔"
    val buttonColor = if (isCorrect) DsaGreenPrimary else DsaRedPrimary
    val explanation = when (state) {
        is FeedbackState.Correct -> state.explanation
        is FeedbackState.Wrong -> state.explanation
        FeedbackState.None -> ""
    }

    Surface(
        color = bannerColor,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        shadowElevation = 12.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(if (isCorrect) "✓" else "✕", color = if (isCorrect) DsaGreenPrimary else DsaRedPrimary, fontSize = 22.sp, fontWeight = FontWeight.Black)
                Text(titleText, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
            }

            Text(
                text = explanation,
                color = Color.White.copy(0.9f),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(Modifier.height(4.dp))

            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
            ) {
                Text("CONTINUE", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

/* ================================================================== */
/*  LESSON VICTORY CELEBRATION SCREEN                                 */
/* ================================================================== */

@Composable
private fun LessonVictoryScreen(
    lesson: DsaLesson,
    earnedXp: Int,
    earnedGems: Int,
    accuracy: Float,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Phoenix Companion celebration flap
        Box(modifier = Modifier.size(160.dp)) {
            PhoenixCreature(
                stage = AppState.activePhoenixStage.toFloat(),
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "LESSON COMPLETE! 🎉",
            color = DsaYellowPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )

        Text(
            text = lesson.title,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(24.dp))

        // Rewards Stat Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF241D4D))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("TOTAL XP", color = Color.White.copy(0.6f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("+$earnedXp", color = Color(0xFFFFD700), fontSize = 22.sp, fontWeight = FontWeight.Black)
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF241D4D))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("GEMS", color = Color.White.copy(0.6f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("+$earnedGems 💎", color = Color(0xFF1CB0F6), fontSize = 20.sp, fontWeight = FontWeight.Black)
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF241D4D))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("ACCURACY", color = Color.White.copy(0.6f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("${(accuracy * 100).toInt()}%", color = DsaGreenPrimary, fontSize = 20.sp, fontWeight = FontWeight.Black)
                }
            }
        }

        Spacer(Modifier.height(36.dp))

        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DsaGreenPrimary)
        ) {
            Text("CONTINUE TO PATH", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
        }
    }
}
