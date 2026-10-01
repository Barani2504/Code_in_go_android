package com.simats.codeingo.ui.exercise

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.data.model.DSAExerciseItem
import com.simats.codeingo.data.model.DSAExerciseType
import com.simats.codeingo.data.repository.CourseRepository
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.ui.components.DuolingoButton
import com.simats.codeingo.ui.components.ProgressBarAnimated
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DuolingoBlue
import com.simats.codeingo.ui.theme.DuolingoBlueDark
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoGreenDark
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.DuolingoRedDark
import com.simats.codeingo.ui.theme.InputBorder
import com.simats.codeingo.ui.theme.SubtextGray

@Composable
fun AssessmentScreen(
    lessonId: String,
    onComplete: (xpEarned: Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameManager = GameManager.instance
    val heartsCount by gameManager.heartsCount.collectAsState()

    // Retrieve exercise items for this lesson
    val lesson = CourseRepository.getLessonById(lessonId)
    val exercises = remember(lesson) {
        if (!lesson?.exercises.isNullOrEmpty()) {
            lesson!!.exercises
        } else {
            // Default interactive questions if not in repository
            listOf(
                DSAExerciseItem(
                    id = "q1",
                    type = DSAExerciseType.MULTIPLE_CHOICE,
                    prompt = "What is the worst-case time complexity of searching in an unsorted array of size n?",
                    explanation = "In an unsorted array, we must potentially inspect every element sequentially: O(n).",
                    conceptId = "array_search",
                    options = listOf("O(1)", "O(log n)", "O(n)", "O(n²)"),
                    correctIndex = 2
                ),
                DSAExerciseItem(
                    id = "q2",
                    type = DSAExerciseType.TRUE_FALSE_SWIPE,
                    prompt = "True or False: In a Singly Linked List, accessing an element by index takes O(1) time.",
                    explanation = "False! Linked lists do not support random access; traversing to index k takes O(k) time.",
                    conceptId = "linked_list_traversal",
                    options = listOf("True", "False"),
                    correctAnswers = listOf("False")
                ),
                DSAExerciseItem(
                    id = "q3",
                    type = DSAExerciseType.MULTIPLE_CHOICE,
                    prompt = "Which data structure follows the Last In, First Out (LIFO) principle?",
                    explanation = "A Stack follows LIFO: elements pushed last are popped first.",
                    conceptId = "stack_lifo",
                    options = listOf("Queue", "Stack", "Binary Tree", "Hash Table"),
                    correctIndex = 1
                )
            )
        }
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var selectedToken by remember { mutableStateOf<String?>(null) }
    var selectedComplexity by remember { mutableStateOf<String?>(null) }
    var matchPairsResult by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var parsonsOrder by remember { mutableStateOf<List<String>>(emptyList()) }
    var isChecked by remember { mutableStateOf(false) }
    var isAnswerCorrect by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }
    var correctCount by remember { mutableIntStateOf(0) }

    val currentExercise = exercises.getOrNull(currentIndex) ?: exercises.first()
    val progress = (currentIndex.toFloat() + if (isChecked) 0.5f else 0f) / exercises.size.toFloat()

    val hasSelection = selectedOptionIndex != null ||
        selectedToken != null ||
        selectedComplexity != null ||
        matchPairsResult.isNotEmpty() ||
        parsonsOrder.isNotEmpty()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        if (isFinished) {
            // Lesson Complete Celebration Screen
            LessonCompleteScreen(
                totalQuestions = exercises.size,
                correctCount = correctCount,
                xpEarned = correctCount * 10 + 5,
                onContinue = {
                    val earnedXP = gameManager.awardLessonXP(
                        baseXP = 15,
                        accuracyPercentage = correctCount.toDouble() / exercises.size.toDouble(),
                        speedSeconds = 40
                    )
                    gameManager.completeLesson(lessonId, nextLevelIndex = 2)
                    onComplete(earnedXP)
                }
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                // Top Header (Close button + Progress bar + Hearts)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = SubtextGray
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    ProgressBarAnimated(
                        progress = progress,
                        height = 14.dp,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Hearts",
                            tint = DuolingoRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$heartsCount",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = DuolingoRed
                        )
                    }
                }

                // Question Area
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Character Avatar / Prompt Header
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(DuolingoGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🦅", fontSize = 28.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(CardBackground)
                                .border(1.dp, InputBorder, RoundedCornerShape(16.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "Select the correct answer",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SubtextGray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Question Prompt
                    Text(
                        text = currentExercise.prompt,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        lineHeight = 28.sp
                    )

                    if (!currentExercise.codeSnippet.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0F1720))
                                .border(1.dp, InputBorder, RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Text(
                                text = currentExercise.codeSnippet!!,
                                fontSize = 13.sp,
                                color = Color(0xFF68D391),
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Dynamic Exercise Types
                    when (currentExercise.type) {
                        DSAExerciseType.MATCH_PAIRS -> {
                            val leftList = remember(currentExercise.id) { currentExercise.pairs.keys.shuffled() }
                            val rightList = remember(currentExercise.id) { currentExercise.pairs.values.shuffled() }
                            MatchPairsExerciseView(
                                leftItems = leftList,
                                rightItems = rightList,
                                solution = currentExercise.pairs,
                                isChecked = isChecked,
                                onMatchedChanged = { matches ->
                                    matchPairsResult = matches
                                }
                            )
                        }

                        DSAExerciseType.ORDER_STEPS -> {
                            val shuffledSteps = remember(currentExercise.id) {
                                if (currentExercise.parsonsLines.isNotEmpty()) {
                                    currentExercise.parsonsLines.shuffled()
                                } else {
                                    currentExercise.options.shuffled()
                                }
                            }
                            OrderStepsExerciseView(
                                steps = shuffledSteps,
                                solution = if (currentExercise.parsonsLines.isNotEmpty()) currentExercise.parsonsLines else currentExercise.options,
                                isChecked = isChecked,
                                onStepsChanged = { order ->
                                    parsonsOrder = order
                                }
                            )
                        }

                        DSAExerciseType.FILL_CODE -> {
                            val correctToken = currentExercise.correctAnswers.firstOrNull() ?: ""
                            FillCodeExerciseView(
                                codeTemplate = currentExercise.codeSnippet ?: currentExercise.prompt,
                                wordBank = currentExercise.options.ifEmpty { listOf(correctToken, "pop()", "push()", "len()") },
                                correctToken = correctToken,
                                selectedToken = selectedToken,
                                isChecked = isChecked,
                                onTokenSelected = { token ->
                                    selectedToken = token
                                }
                            )
                        }

                        DSAExerciseType.COMPLEXITY_DIAL -> {
                            val correctComplexity = currentExercise.correctAnswers.firstOrNull() ?: "O(n)"
                            ComplexityDialExerciseView(
                                promptCode = currentExercise.codeSnippet ?: currentExercise.prompt,
                                correctComplexity = correctComplexity,
                                selectedComplexity = selectedComplexity,
                                isChecked = isChecked,
                                onComplexitySelected = { complexity ->
                                    selectedComplexity = complexity
                                }
                            )
                        }

                        else -> {
                            // Multiple Choice / True False Swipe / Standard
                            val options = if (currentExercise.type == DSAExerciseType.TRUE_FALSE_SWIPE) {
                                listOf("True", "False")
                            } else {
                                currentExercise.options
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                options.forEachIndexed { index, optionText ->
                                    val isSelected = selectedOptionIndex == index
                                    val shape = RoundedCornerShape(16.dp)

                                    val borderColor by animateColorAsState(
                                        targetValue = when {
                                            isChecked && isSelected && isAnswerCorrect -> DuolingoGreen
                                            isChecked && isSelected && !isAnswerCorrect -> DuolingoRed
                                            isSelected -> DuolingoBlue
                                            else -> InputBorder
                                        },
                                        label = "BorderColor"
                                    )

                                    val bgColor = when {
                                        isChecked && isSelected && isAnswerCorrect -> DuolingoGreen.copy(alpha = 0.15f)
                                        isChecked && isSelected && !isAnswerCorrect -> DuolingoRed.copy(alpha = 0.15f)
                                        isSelected -> DuolingoBlue.copy(alpha = 0.15f)
                                        else -> CardBackground
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(shape)
                                            .background(bgColor)
                                            .border(2.dp, borderColor, shape)
                                            .clickable(enabled = !isChecked) {
                                                selectedOptionIndex = index
                                            }
                                            .padding(horizontal = 18.dp, vertical = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .border(2.dp, borderColor, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${index + 1}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else SubtextGray
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(16.dp))

                                        Text(
                                            text = optionText,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }

                // Bottom Action Bar (CHECK button or feedback drawer)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isChecked) (if (isAnswerCorrect) Color(0xFF0F3014) else Color(0xFF381414)) else DarkBackground)
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Column {
                        if (isChecked) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isAnswerCorrect) Icons.Default.Check else Icons.Default.Close,
                                    contentDescription = null,
                                    tint = if (isAnswerCorrect) DuolingoGreen else DuolingoRed,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isAnswerCorrect) "Nicely done!" else "Correct solution:",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isAnswerCorrect) DuolingoGreen else DuolingoRed
                                )
                            }
                            if (!isAnswerCorrect) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currentExercise.explanation,
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.9f),
                                    lineHeight = 18.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        val canCheck = hasSelection || isChecked
                        DuolingoButton(
                            text = if (!isChecked) "CHECK" else "CONTINUE",
                            faceColor = when {
                                !canCheck -> CardBackground
                                isChecked && !isAnswerCorrect -> DuolingoRed
                                else -> DuolingoGreen
                            },
                            shadowColor = when {
                                !canCheck -> Color(0xFF142028)
                                isChecked && !isAnswerCorrect -> DuolingoRedDark
                                else -> DuolingoGreenDark
                            },
                            textColor = if (!canCheck) SubtextGray else Color.White,
                            onClick = {
                                if (!isChecked) {
                                    if (hasSelection) {
                                        val correct = when (currentExercise.type) {
                                            DSAExerciseType.MATCH_PAIRS -> {
                                                matchPairsResult.size == currentExercise.pairs.size &&
                                                    currentExercise.pairs.all { matchPairsResult[it.key] == it.value }
                                            }
                                            DSAExerciseType.ORDER_STEPS -> {
                                                val expected = if (currentExercise.parsonsLines.isNotEmpty()) {
                                                    currentExercise.parsonsLines
                                                } else {
                                                    currentExercise.options
                                                }
                                                parsonsOrder == expected
                                            }
                                            DSAExerciseType.FILL_CODE -> {
                                                selectedToken == (currentExercise.correctAnswers.firstOrNull() ?: "")
                                            }
                                            DSAExerciseType.COMPLEXITY_DIAL -> {
                                                selectedComplexity == (currentExercise.correctAnswers.firstOrNull() ?: "O(n)")
                                            }
                                            DSAExerciseType.TRUE_FALSE_SWIPE -> {
                                                val ans = if (selectedOptionIndex == 0) "True" else "False"
                                                currentExercise.correctAnswers.contains(ans)
                                            }
                                            else -> {
                                                selectedOptionIndex == currentExercise.correctIndex
                                            }
                                        }

                                        isAnswerCorrect = correct
                                        isChecked = true

                                        if (correct) {
                                            correctCount += 1
                                            gameManager.recordCorrectAnswer()
                                        } else {
                                            val wrongAnswer = when (currentExercise.type) {
                                                DSAExerciseType.FILL_CODE -> selectedToken ?: ""
                                                DSAExerciseType.COMPLEXITY_DIAL -> selectedComplexity ?: ""
                                                else -> currentExercise.options.getOrNull(selectedOptionIndex ?: -1) ?: ""
                                            }
                                            gameManager.recordWrongAnswer(currentExercise, wrongAnswer)
                                        }
                                    }
                                } else {
                                    // Move to next question or complete
                                    if (currentIndex + 1 < exercises.size) {
                                        currentIndex += 1
                                        selectedOptionIndex = null
                                        selectedToken = null
                                        selectedComplexity = null
                                        matchPairsResult = emptyMap()
                                        parsonsOrder = emptyList()
                                        isChecked = false
                                        isAnswerCorrect = false
                                    } else {
                                        isFinished = true
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonCompleteScreen(
    totalQuestions: Int,
    correctCount: Int,
    xpEarned: Int,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Text(text = "🎉", fontSize = 72.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Lesson Complete!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "You're building your algorithmic instincts step by step.",
            fontSize = 14.sp,
            color = SubtextGray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Stats Row (XP + Accuracy)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val statShape = RoundedCornerShape(16.dp)

            // Total XP Card
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(statShape)
                    .background(Color(0xFFFFC800).copy(alpha = 0.15f))
                    .border(1.5.dp, Color(0xFFFFC800), statShape)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "TOTAL XP", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFFFFC800))
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⭐", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "+$xpEarned", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color.White)
                }
            }

            // Accuracy Card
            val accuracy = if (totalQuestions > 0) ((correctCount.toFloat() / totalQuestions.toFloat()) * 100).toInt() else 100
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(statShape)
                    .background(DuolingoGreen.copy(alpha = 0.15f))
                    .border(1.5.dp, DuolingoGreen, statShape)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "ACCURACY", fontSize = 11.sp, fontWeight = FontWeight.Black, color = DuolingoGreen)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🎯", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "$accuracy%", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        DuolingoButton(
            text = "CONTINUE",
            faceColor = DuolingoGreen,
            shadowColor = DuolingoGreenDark,
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}
