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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.data.model.DSAExerciseType
import com.simats.codeingo.data.repository.QuizQuestion
import com.simats.codeingo.data.repository.QuizQuestionsData
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.ui.components.DuolingoButton
import com.simats.codeingo.ui.components.ProgressBarAnimated
import com.simats.codeingo.ui.phoenix.PhoenixEggHatch3DView
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DuolingoBlue
import com.simats.codeingo.ui.theme.DuolingoBlueDark
import com.simats.codeingo.ui.theme.DuolingoCardBg
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoGreenDark
import com.simats.codeingo.ui.theme.DuolingoInputBg
import com.simats.codeingo.ui.theme.DuolingoInputBorder
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.DuolingoRedDark
import com.simats.codeingo.ui.theme.InputBorder
import com.simats.codeingo.ui.theme.SubtextGray
import kotlinx.coroutines.delay

@Composable
fun AssessmentScreen(
    unitId: Int = 1,
    levelNumber: Int = 1,
    totalLevelsInUnit: Int = 6,
    isBoss: Boolean = false,
    lessonId: String = "",
    onComplete: (xpEarned: Int) -> Unit = {},
    onDismiss: () -> Unit,
    onUpgradePhoenixNextUnit: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val gameManager = GameManager.instance
    val heartsCount by gameManager.heartsCount.collectAsState()

    // Retrieve curated 10 questions for this unit and level
    val questions = remember(unitId, levelNumber) {
        QuizQuestionsData.getQuestionsForUnit(unitId, levelNumber)
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var matchedPairs by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var orderedSteps by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedFillToken by remember { mutableStateOf<String?>(null) }
    var selectedComplexity by remember { mutableStateOf<String?>(null) }
    var selectedTrueFalse by remember { mutableStateOf<Boolean?>(null) }

    var isChecked by remember { mutableStateOf(false) }
    var isAnswerCorrect by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }
    var correctCount by remember { mutableIntStateOf(0) }
    var showHintDialog by remember { mutableStateOf(false) }

    // Generous 45s countdown indicator per question
    var timeRemaining by remember { mutableIntStateOf(45) }

    val currentQuestion: QuizQuestion = questions.getOrElse(currentIndex) {
        QuizQuestion(
            typeTitle = "DSA Challenge",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Analyze the algorithmic logic:",
            targetPrompt = "Select the correct option"
        )
    }

    // Reset exercise state when question index changes
    LaunchedEffect(currentIndex) {
        timeRemaining = 45
        selectedOptionIndex = null
        matchedPairs = emptyMap()
        orderedSteps = currentQuestion.orderStepsInitial
        selectedFillToken = null
        selectedComplexity = null
        selectedTrueFalse = null
        isChecked = false
        isAnswerCorrect = false
    }

    // 1-second countdown timer
    LaunchedEffect(currentIndex, isChecked, isFinished) {
        while (!isChecked && !isFinished && timeRemaining > 0) {
            delay(1000)
            timeRemaining -= 1
        }
    }

    val hasSelection = when (currentQuestion.gameType) {
        DSAExerciseType.MULTIPLE_CHOICE -> selectedOptionIndex != null
        DSAExerciseType.MATCH_PAIRS -> matchedPairs.size >= currentQuestion.matchSolution.size && currentQuestion.matchSolution.isNotEmpty()
        DSAExerciseType.ORDER_STEPS -> orderedSteps.isNotEmpty()
        DSAExerciseType.FILL_CODE -> selectedFillToken != null
        DSAExerciseType.COMPLEXITY_DIAL -> selectedComplexity != null
        DSAExerciseType.TRUE_FALSE_SWIPE -> selectedTrueFalse != null
        else -> selectedOptionIndex != null
    }

    val correctAnswerSummary: String = when (currentQuestion.gameType) {
        DSAExerciseType.MULTIPLE_CHOICE -> {
            currentQuestion.options.getOrElse(currentQuestion.correctOptionIndex) { "" }
        }
        DSAExerciseType.MATCH_PAIRS -> "All pairs connected!"
        DSAExerciseType.ORDER_STEPS -> currentQuestion.orderStepsSolution.joinToString(" → ")
        DSAExerciseType.FILL_CODE -> currentQuestion.fillCodeCorrectToken
        DSAExerciseType.COMPLEXITY_DIAL -> currentQuestion.complexityDialCorrect
        DSAExerciseType.TRUE_FALSE_SWIPE -> if (currentQuestion.trueFalseIsCorrectTrue) "True ✅" else "False ❌"
        else -> currentQuestion.options.getOrElse(currentQuestion.correctOptionIndex) { "" }
    }

    // Calculate stars and XP
    val starsEarned = when (correctCount) {
        10 -> 5
        in 8..9 -> 4
        in 6..7 -> 3
        in 4..5 -> 2
        in 1..3 -> 1
        else -> 0
    }
    val accuracyPercentage = if (questions.isNotEmpty()) {
        ((correctCount.toFloat() / questions.size.toFloat()) * 100).toInt()
    } else 100
    val totalXPEarned = (unitId * 10) + (starsEarned * 5)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        if (isFinished) {
            // Phoenix Egg Hatch 3D Cutscene with CandyCrushStarsView
            PhoenixEggHatch3DView(
                unitId = unitId,
                levelNumber = levelNumber,
                totalLevelsInUnit = totalLevelsInUnit,
                isBoss = isBoss,
                xpEarned = totalXPEarned,
                starsEarned = starsEarned,
                accuracyPercentage = accuracyPercentage,
                onContinue = {
                    gameManager.addStars(starsEarned)
                    gameManager.awardLessonXP(unitId, accuracyPercentage.toDouble() / 100.0, 45 - timeRemaining)
                    gameManager.completeLessonAndExtendStreak()
                    gameManager.unlockNextLevel(currentLevelIndex = levelNumber, isBoss = false)
                    onComplete(totalXPEarned)
                },
                onFinish = {
                    gameManager.addStars(starsEarned)
                    gameManager.awardLessonXP(unitId, accuracyPercentage.toDouble() / 100.0, 45 - timeRemaining)
                    gameManager.completeLessonAndExtendStreak()
                    gameManager.unlockNextLevel(currentLevelIndex = levelNumber, isBoss = false)
                    onComplete(totalXPEarned)
                },
                onUpgradePhoenixNextUnit = {
                    gameManager.addStars(starsEarned)
                    gameManager.awardLessonXP(unitId, accuracyPercentage.toDouble() / 100.0, 45 - timeRemaining)
                    gameManager.completeLessonAndExtendStreak()
                    gameManager.unlockNextLevel(
                        currentLevelIndex = levelNumber,
                        isBoss = true,
                        nextUnitFirstLevelIndex = levelNumber + 1
                    )
                    onUpgradePhoenixNextUnit?.invoke() ?: onComplete(totalXPEarned)
                }
            )
        } else if (heartsCount <= 0) {
            // Out of Hearts Dialog / Screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "💔", fontSize = 64.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Out of Hearts!",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Take a break, practice past lessons, or visit the Phoenix Sanctuary to restore hearts.",
                    fontSize = 14.sp,
                    color = SubtextGray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                DuolingoButton(
                    text = "RETURN TO DASHBOARD",
                    faceColor = DuolingoBlue,
                    shadowColor = DuolingoBlueDark,
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            // Main Quiz View
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                // 1. Top Header Bar: Close (✕), Progress Bar, Hearts Count
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
                            tint = SubtextGray,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    val progress = if (questions.isNotEmpty()) {
                        (currentIndex.toFloat() / questions.size.toFloat())
                    } else 0f
                    ProgressBarAnimated(
                        progress = progress,
                        modifier = Modifier
                            .weight(1f)
                            .height(14.dp)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Hearts",
                            tint = DuolingoRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "$heartsCount",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = DuolingoRed
                        )
                    }
                }

                // Scrollable Question Content
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 20.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Question Count Badge + Exercise Title + Cheat Code Hint Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CardBackground)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Q${currentIndex + 1}/${questions.size}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = SubtextGray
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = currentQuestion.typeTitle,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        if (currentQuestion.hint != null) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DuolingoBlue)
                                    .clickable { showHintDialog = true }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🎮", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Cheat Code",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Speed timer challenge indicator
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Text(
                            text = "⏳ ${timeRemaining}s",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = if (timeRemaining <= 5) DuolingoRed else SubtextGray
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Speaker Chat Bubble Card
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Speaker Avatar
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(DuolingoGreen.copy(alpha = 0.2f))
                                .border(1.5.dp, DuolingoGreen, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🦉", fontSize = 26.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Speech Bubble
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(18.dp))
                                .background(DuolingoInputBg)
                                .border(1.5.dp, DuolingoInputBorder, RoundedCornerShape(18.dp))
                                .padding(14.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = currentQuestion.promptSentence,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = currentQuestion.targetPrompt,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AmberGold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Dynamic Gamified Exercise Renderer
                    when (currentQuestion.gameType) {
                        DSAExerciseType.MULTIPLE_CHOICE -> {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                currentQuestion.options.forEachIndexed { index, optionText ->
                                    val isSelected = selectedOptionIndex == index
                                    val isCorrectOpt = index == currentQuestion.correctOptionIndex
                                    val shape = RoundedCornerShape(14.dp)

                                    val bgColor by animateColorAsState(
                                        targetValue = when {
                                            isChecked && isSelected && isCorrectOpt -> DuolingoGreen.copy(alpha = 0.22f)
                                            isChecked && isSelected && !isCorrectOpt -> DuolingoRed.copy(alpha = 0.22f)
                                            isChecked && isCorrectOpt -> DuolingoGreen.copy(alpha = 0.18f)
                                            isSelected -> DuolingoBlue.copy(alpha = 0.22f)
                                            else -> DuolingoCardBg
                                        },
                                        label = "optBg"
                                    )

                                    val borderColor by animateColorAsState(
                                        targetValue = when {
                                            isChecked && isSelected && isCorrectOpt -> DuolingoGreen
                                            isChecked && isSelected && !isCorrectOpt -> DuolingoRed
                                            isChecked && isCorrectOpt -> DuolingoGreen
                                            isSelected -> DuolingoBlue
                                            else -> DuolingoInputBorder
                                        },
                                        label = "optBorder"
                                    )

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(shape)
                                            .background(bgColor)
                                            .border(if (isSelected) 2.dp else 1.dp, borderColor, shape)
                                            .clickable(enabled = !isChecked) {
                                                selectedOptionIndex = index
                                            }
                                            .padding(horizontal = 16.dp, vertical = 14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = optionText,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = if (isChecked) {
                                                    if (isCorrectOpt) DuolingoGreen else DuolingoRed
                                                } else DuolingoBlue,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        DSAExerciseType.MATCH_PAIRS -> {
                            MatchPairsExerciseView(
                                leftItems = currentQuestion.matchLeft,
                                rightItems = currentQuestion.matchRight,
                                solution = currentQuestion.matchSolution,
                                isChecked = isChecked,
                                onMatchedChanged = { pairs ->
                                    matchedPairs = pairs
                                }
                            )
                        }

                        DSAExerciseType.ORDER_STEPS -> {
                            OrderStepsExerciseView(
                                initialSteps = currentQuestion.orderStepsInitial,
                                solution = currentQuestion.orderStepsSolution,
                                isChecked = isChecked,
                                onStepsChanged = { steps ->
                                    orderedSteps = steps
                                }
                            )
                        }

                        DSAExerciseType.FILL_CODE -> {
                            FillCodeExerciseView(
                                codeTemplate = currentQuestion.fillCodeTemplate,
                                wordBank = currentQuestion.fillCodeWordBank,
                                correctToken = currentQuestion.fillCodeCorrectToken,
                                isChecked = isChecked,
                                externalSelectedToken = selectedFillToken,
                                onTokenSelected = { token ->
                                    selectedFillToken = token
                                }
                            )
                        }

                        DSAExerciseType.COMPLEXITY_DIAL -> {
                            ComplexityDialExerciseView(
                                promptCode = currentQuestion.complexityCodeSnippet,
                                correctComplexity = currentQuestion.complexityDialCorrect,
                                isChecked = isChecked,
                                externalSelectedComplexity = selectedComplexity,
                                onComplexitySelected = { comp ->
                                    selectedComplexity = comp
                                }
                            )
                        }

                        DSAExerciseType.TRUE_FALSE_SWIPE -> {
                            TrueFalseSwipeExerciseView(
                                statement = currentQuestion.trueFalseStatement,
                                isCorrectTrue = currentQuestion.trueFalseIsCorrectTrue,
                                explanation = currentQuestion.hint,
                                isChecked = isChecked,
                                externalDecision = selectedTrueFalse,
                                onDecisionMade = { decision ->
                                    selectedTrueFalse = decision
                                }
                            )
                        }

                        else -> {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                currentQuestion.options.forEachIndexed { index, optionText ->
                                    val isSelected = selectedOptionIndex == index
                                    val shape = RoundedCornerShape(14.dp)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(shape)
                                            .background(if (isSelected) DuolingoBlue.copy(alpha = 0.22f) else DuolingoCardBg)
                                            .border(if (isSelected) 2.dp else 1.dp, if (isSelected) DuolingoBlue else DuolingoInputBorder, shape)
                                            .clickable(enabled = !isChecked) { selectedOptionIndex = index }
                                            .padding(horizontal = 16.dp, vertical = 14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = optionText, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Bottom Feedback & Action Bar
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            when {
                                isChecked && isAnswerCorrect -> DuolingoGreen.copy(alpha = 0.15f)
                                isChecked && !isAnswerCorrect -> DuolingoRed.copy(alpha = 0.15f)
                                else -> CardBackground
                            }
                        )
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    if (isChecked) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isAnswerCorrect) "🎯" else "💔",
                                fontSize = 28.sp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isAnswerCorrect) "AMAZING! CORRECT! ✨" else "NOT QUITE! 🐣",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isAnswerCorrect) DuolingoGreen else DuolingoRed
                                )
                                if (!isAnswerCorrect && correctAnswerSummary.isNotEmpty()) {
                                    Text(
                                        text = "Correct answer: $correctAnswerSummary",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
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
                                    val correct = when (currentQuestion.gameType) {
                                        DSAExerciseType.MULTIPLE_CHOICE -> {
                                            selectedOptionIndex == currentQuestion.correctOptionIndex
                                        }
                                        DSAExerciseType.MATCH_PAIRS -> {
                                            matchedPairs.size == currentQuestion.matchSolution.size &&
                                                currentQuestion.matchSolution.all { matchedPairs[it.key] == it.value }
                                        }
                                        DSAExerciseType.ORDER_STEPS -> {
                                            orderedSteps == currentQuestion.orderStepsSolution
                                        }
                                        DSAExerciseType.FILL_CODE -> {
                                            selectedFillToken == currentQuestion.fillCodeCorrectToken
                                        }
                                        DSAExerciseType.COMPLEXITY_DIAL -> {
                                            selectedComplexity == currentQuestion.complexityDialCorrect
                                        }
                                        DSAExerciseType.TRUE_FALSE_SWIPE -> {
                                            selectedTrueFalse == currentQuestion.trueFalseIsCorrectTrue
                                        }
                                        else -> {
                                            selectedOptionIndex == currentQuestion.correctOptionIndex
                                        }
                                    }

                                    isAnswerCorrect = correct
                                    isChecked = true

                                    if (correct) {
                                        correctCount += 1
                                    } else {
                                        gameManager.loseHeart()
                                    }
                                }
                            } else {
                                if (currentIndex + 1 < questions.size) {
                                    currentIndex += 1
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

        // Cheat Code Hint Dialog
        if (showHintDialog) {
            AlertDialog(
                onDismissRequest = { showHintDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🎮", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Phoenix Cheat Code",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                },
                text = {
                    Text(
                        text = currentQuestion.hint ?: "Master the data structure patterns to unlock the Phoenix Bird!",
                        fontSize = 14.sp,
                        color = SubtextGray
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showHintDialog = false }) {
                        Text(text = "GOT IT! ⚡️", fontWeight = FontWeight.Black, color = DuolingoBlue)
                    }
                },
                containerColor = DuolingoCardBg,
                shape = RoundedCornerShape(18.dp)
            )
        }
    }
}
