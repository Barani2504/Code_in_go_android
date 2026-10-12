package com.simats.codeingo.ui.exercise

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.data.model.DSAExerciseType
import com.simats.codeingo.data.repository.QuizQuestion
import com.simats.codeingo.data.repository.QuizQuestionsData
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.ui.components.AppButton
import com.simats.codeingo.ui.components.AppButtonStyle
import com.simats.codeingo.ui.components.ProgressBarAnimated
import com.simats.codeingo.ui.phoenix.PhoenixEggHatch3DView
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DuolingoBlue
import com.simats.codeingo.ui.theme.DuolingoCardBg
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoInputBg
import com.simats.codeingo.ui.theme.DuolingoInputBorder
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.FeedbackManager
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.theme.correctPop
import com.simats.codeingo.ui.theme.pressScale
import com.simats.codeingo.ui.theme.pulse
import com.simats.codeingo.ui.theme.shake
import com.simats.codeingo.ui.theme.staggeredAppear
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    val feedback = remember { FeedbackManager(context) }
    DisposableEffect(Unit) { onDispose { feedback.onDestroy() } }

    val questions = remember(unitId, levelNumber) { QuizQuestionsData.getQuestionsForUnit(unitId, levelNumber) }

    var currentIndex       by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var matchedPairs        by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var orderedSteps        by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedFillToken   by remember { mutableStateOf<String?>(null) }
    var selectedComplexity  by remember { mutableStateOf<String?>(null) }
    var selectedTrueFalse   by remember { mutableStateOf<Boolean?>(null) }

    var isChecked       by remember { mutableStateOf(false) }
    var isAnswerCorrect by remember { mutableStateOf(false) }
    var isFinished      by remember { mutableStateOf(false) }
    var correctCount    by remember { mutableIntStateOf(0) }
    var showHintDialog  by remember { mutableStateOf(false) }
    var wrongAnswerCount by remember { mutableIntStateOf(0) }
    var timeRemaining   by remember { mutableIntStateOf(45) }

    // Bug fix: single-composable slide via Animatable — no dual-state bleed
    val slideOffsetPx   = remember { Animatable(0f) }
    var isTransitioning by remember { mutableStateOf(false) }

    fun advanceQuestion() {
        scope.launch {
            isTransitioning = true
            val fullWidth = with(density) { 420.dp.toPx() }
            slideOffsetPx.snapTo(fullWidth)          // place off-screen right
            if (currentIndex + 1 < questions.size) currentIndex += 1 else { isFinished = true; return@launch }
            slideOffsetPx.animateTo(0f, animationSpec = tween(320, easing = FastOutSlowInEasing))
            isTransitioning = false
        }
    }

    val currentQuestion: QuizQuestion = questions.getOrElse(currentIndex) {
        QuizQuestion(typeTitle = "DSA Challenge", speakerName = "Duo", speakerImage = "DuoPencil",
            promptSentence = "Analyze the algorithmic logic:", targetPrompt = "Select the correct option")
    }

    LaunchedEffect(currentIndex) {
        timeRemaining     = 45
        selectedOptionIndex = null
        matchedPairs      = emptyMap()
        orderedSteps      = currentQuestion.orderStepsInitial
        selectedFillToken = null
        selectedComplexity = null
        selectedTrueFalse = null
        isChecked         = false
        isAnswerCorrect   = false
    }

    LaunchedEffect(currentIndex, isChecked, isFinished) {
        while (!isChecked && !isFinished && timeRemaining > 0) { delay(1000); timeRemaining -= 1 }
    }

    val hasSelection = when (currentQuestion.gameType) {
        DSAExerciseType.MULTIPLE_CHOICE  -> selectedOptionIndex != null
        DSAExerciseType.MATCH_PAIRS      -> matchedPairs.size >= currentQuestion.matchSolution.size && currentQuestion.matchSolution.isNotEmpty()
        DSAExerciseType.ORDER_STEPS      -> orderedSteps.isNotEmpty()
        DSAExerciseType.FILL_CODE        -> selectedFillToken != null
        DSAExerciseType.COMPLEXITY_DIAL  -> selectedComplexity != null
        DSAExerciseType.TRUE_FALSE_SWIPE -> selectedTrueFalse != null
        else                             -> selectedOptionIndex != null
    }

    val correctAnswerSummary: String = when (currentQuestion.gameType) {
        DSAExerciseType.MULTIPLE_CHOICE  -> currentQuestion.options.getOrElse(currentQuestion.correctOptionIndex) { "" }
        DSAExerciseType.MATCH_PAIRS      -> "All pairs connected!"
        DSAExerciseType.ORDER_STEPS      -> currentQuestion.orderStepsSolution.joinToString(" → ")
        DSAExerciseType.FILL_CODE        -> currentQuestion.fillCodeCorrectToken
        DSAExerciseType.COMPLEXITY_DIAL  -> currentQuestion.complexityDialCorrect
        DSAExerciseType.TRUE_FALSE_SWIPE -> if (currentQuestion.trueFalseIsCorrectTrue) "True ✅" else "False ❌"
        else                             -> currentQuestion.options.getOrElse(currentQuestion.correctOptionIndex) { "" }
    }

    val starsEarned = when (correctCount) { 10 -> 5; in 8..9 -> 4; in 6..7 -> 3; in 4..5 -> 2; in 1..3 -> 1; else -> 0 }
    val accuracyPercentage = if (questions.isNotEmpty()) ((correctCount.toFloat() / questions.size) * 100).toInt() else 100
    val totalXPEarned = (unitId * 10) + (starsEarned * 5)

    Box(modifier = modifier.fillMaxSize().background(LocalDynamicThemeColors.current.background)) {
        if (isFinished) {
            PhoenixEggHatch3DView(
                unitId = unitId, levelNumber = levelNumber, totalLevelsInUnit = totalLevelsInUnit,
                isBoss = isBoss, xpEarned = totalXPEarned, starsEarned = starsEarned, accuracyPercentage = accuracyPercentage,
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
                    gameManager.unlockNextLevel(currentLevelIndex = levelNumber, isBoss = true, nextUnitFirstLevelIndex = levelNumber + 1)
                    onUpgradePhoenixNextUnit?.invoke() ?: onComplete(totalXPEarned)
                }
            )
        } else if (heartsCount <= 0) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp).statusBarsPadding().navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
            ) {
                Text("💔", fontSize = 64.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Out of Hearts!", fontSize = 26.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Take a break or visit the Phoenix Sanctuary to restore hearts.", fontSize = 14.sp, color = SubtextGray, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(32.dp))
                AppButton(title = "RETURN TO DASHBOARD", style = AppButtonStyle.ACTION_BLUE, onClick = onDismiss, modifier = Modifier.fillMaxWidth().height(50.dp))
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {

                // ── Top Bar ─────────────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Close", tint = SubtextGray, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    val progress = if (questions.isNotEmpty()) currentIndex.toFloat() / questions.size else 0f
                    ProgressBarAnimated(progress = progress, modifier = Modifier.weight(1f).height(14.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.shake(shakeTrigger = wrongAnswerCount.takeIf { it > 0 })
                    ) {
                        Icon(Icons.Default.Favorite, "Hearts", tint = DuolingoRed, modifier = Modifier.size(20.dp))
                        Text("$heartsCount", fontSize = 15.sp, fontWeight = FontWeight.Black, color = DuolingoRed)
                    }
                }

                // ── Scrollable content — slides as a SINGLE composable (no AnimatedContent bug) ──
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .offset { IntOffset(slideOffsetPx.value.roundToInt(), 0) }
                        .verticalScroll(scrollState)
                        .padding(horizontal = 20.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Question badge + title + hint button
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(CardBackground).padding(horizontal = 8.dp, vertical = 4.dp)
                        ) { Text("Q${currentIndex + 1}/${questions.size}", fontSize = 12.sp, fontWeight = FontWeight.Black, color = SubtextGray) }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(currentQuestion.typeTitle, fontSize = 20.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
                        Spacer(modifier = Modifier.weight(1f))
                        if (currentQuestion.hint != null) {
                            Box(
                                modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(DuolingoBlue)
                                    .clickable { showHintDialog = true }.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🎮", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Cheat Code", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.White)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Timer with animated colour + pulse
                    val timerColor by animateColorAsState(
                        targetValue = when { timeRemaining <= 5 -> DuolingoRed; timeRemaining <= 10 -> AmberGold; else -> SubtextGray },
                        animationSpec = tween(400), label = "timerColor"
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Text(
                            "⏳ ${timeRemaining}s", fontSize = 14.sp, fontWeight = FontWeight.Black, color = timerColor,
                            modifier = if (timeRemaining <= 10) Modifier.pulse(1.0f..1.12f, 600) else Modifier
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Owl chat bubble
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier.size(50.dp).clip(CircleShape)
                                .background(DuolingoGreen.copy(alpha = 0.2f)).border(1.5.dp, DuolingoGreen, CircleShape),
                            contentAlignment = Alignment.Center
                        ) { Text("🦩", fontSize = 26.sp) }
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(
                            modifier = Modifier.weight(1f).clip(RoundedCornerShape(18.dp))
                                .background(DuolingoInputBg).border(1.5.dp, DuolingoInputBorder, RoundedCornerShape(18.dp)).padding(14.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(currentQuestion.promptSentence, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = LocalDynamicThemeColors.current.textPrimary)
                                Text(currentQuestion.targetPrompt, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AmberGold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // ── GAMIFIED EXERCISE RENDERER ───────────────────────────
                    // isTransitioning blocks all interaction during the slide animation
                    val interactionEnabled = !isTransitioning

                    when (currentQuestion.gameType) {

                        // ── A/B/C/D Badge tiles ──────────────────────────────
                        DSAExerciseType.MULTIPLE_CHOICE -> {
                            val badges = listOf("A", "B", "C", "D", "E", "F")
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                currentQuestion.options.forEachIndexed { index, optionText ->
                                    val isSelected   = selectedOptionIndex == index
                                    val isCorrectOpt = index == currentQuestion.correctOptionIndex
                                    val badge = badges.getOrElse(index) { "${index + 1}" }
                                    val shape = RoundedCornerShape(16.dp)

                                    val bgColor by animateColorAsState(
                                        targetValue = when {
                                            isChecked && isSelected && isCorrectOpt  -> DuolingoGreen.copy(alpha = 0.22f)
                                            isChecked && isSelected && !isCorrectOpt -> DuolingoRed.copy(alpha = 0.22f)
                                            isChecked && isCorrectOpt                -> DuolingoGreen.copy(alpha = 0.18f)
                                            isSelected                               -> DuolingoBlue.copy(alpha = 0.22f)
                                            else                                     -> DuolingoCardBg
                                        }, label = "optBg"
                                    )
                                    val borderColor by animateColorAsState(
                                        targetValue = when {
                                            isChecked && isSelected && isCorrectOpt  -> DuolingoGreen
                                            isChecked && isSelected && !isCorrectOpt -> DuolingoRed
                                            isChecked && isCorrectOpt                -> DuolingoGreen
                                            isSelected                               -> DuolingoBlue
                                            else                                     -> DuolingoInputBorder
                                        }, label = "optBorder"
                                    )
                                    val badgeBg by animateColorAsState(
                                        targetValue = when {
                                            isChecked && isSelected && isCorrectOpt  -> DuolingoGreen
                                            isChecked && isSelected && !isCorrectOpt -> DuolingoRed
                                            isChecked && isCorrectOpt                -> DuolingoGreen
                                            isSelected                               -> DuolingoBlue
                                            else                                     -> SubtextGray.copy(alpha = 0.4f)
                                        }, label = "badgeBg"
                                    )

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .staggeredAppear(index = index, baseDelayMs = 60)
                                            .correctPop(trigger = isChecked && isSelected && isCorrectOpt)
                                            .shake(shakeTrigger = if (isChecked && isSelected && !isCorrectOpt) index else null)
                                            .pressScale(0.96f)
                                            .clip(shape)
                                            .background(bgColor)
                                            .border(if (isSelected || (isChecked && isCorrectOpt)) 2.dp else 1.dp, borderColor, shape)
                                            .clickable(enabled = !isChecked && interactionEnabled) {
                                                selectedOptionIndex = index; feedback.tap()
                                            }
                                            .padding(horizontal = 14.dp, vertical = 14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        // Animated letter badge
                                        Box(
                                            modifier = Modifier.size(32.dp).clip(CircleShape).background(badgeBg),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = if (isChecked && isSelected) (if (isCorrectOpt) "✓" else "✕") else badge,
                                                fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White
                                            )
                                        }
                                        Text(
                                            text = optionText, fontSize = 15.sp, fontWeight = FontWeight.Bold,
                                            color = LocalDynamicThemeColors.current.textPrimary, modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }

                        DSAExerciseType.MATCH_PAIRS -> {
                            MatchPairsExerciseView(
                                leftItems = currentQuestion.matchLeft, rightItems = currentQuestion.matchRight,
                                solution = currentQuestion.matchSolution, isChecked = isChecked,
                                onMatchedChanged = { pairs -> if (interactionEnabled) { matchedPairs = pairs; feedback.tap() } }
                            )
                        }

                        DSAExerciseType.ORDER_STEPS -> {
                            OrderStepsExerciseView(
                                initialSteps = currentQuestion.orderStepsInitial, solution = currentQuestion.orderStepsSolution,
                                isChecked = isChecked,
                                onStepsChanged = { steps -> if (interactionEnabled) orderedSteps = steps }
                            )
                        }

                        DSAExerciseType.FILL_CODE -> {
                            FillCodeExerciseView(
                                codeTemplate = currentQuestion.fillCodeTemplate, wordBank = currentQuestion.fillCodeWordBank,
                                correctToken = currentQuestion.fillCodeCorrectToken, isChecked = isChecked,
                                externalSelectedToken = selectedFillToken,
                                onTokenSelected = { token -> if (interactionEnabled) { selectedFillToken = token; feedback.tap() } }
                            )
                        }

                        DSAExerciseType.COMPLEXITY_DIAL -> {
                            ComplexityDialExerciseView(
                                promptCode = currentQuestion.complexityCodeSnippet, correctComplexity = currentQuestion.complexityDialCorrect,
                                isChecked = isChecked, externalSelectedComplexity = selectedComplexity,
                                onComplexitySelected = { comp -> if (interactionEnabled) { selectedComplexity = comp; feedback.tap() } }
                            )
                        }

                        DSAExerciseType.TRUE_FALSE_SWIPE -> {
                            TrueFalseSwipeExerciseView(
                                statement = currentQuestion.trueFalseStatement, isCorrectTrue = currentQuestion.trueFalseIsCorrectTrue,
                                explanation = currentQuestion.hint, isChecked = isChecked, externalDecision = selectedTrueFalse,
                                onDecisionMade = { decision -> if (interactionEnabled) { selectedTrueFalse = decision; feedback.tap() } }
                            )
                        }

                        else -> {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                currentQuestion.options.forEachIndexed { index, optionText ->
                                    val isSelected = selectedOptionIndex == index
                                    val shape = RoundedCornerShape(14.dp)
                                    Row(
                                        modifier = Modifier.fillMaxWidth().clip(shape)
                                            .background(if (isSelected) DuolingoBlue.copy(alpha = 0.22f) else DuolingoCardBg)
                                            .border(if (isSelected) 2.dp else 1.dp, if (isSelected) DuolingoBlue else DuolingoInputBorder, shape)
                                            .clickable(enabled = !isChecked && interactionEnabled) { selectedOptionIndex = index; feedback.tap() }
                                            .padding(horizontal = 16.dp, vertical = 14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) { Text(optionText, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = LocalDynamicThemeColors.current.textPrimary) }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // ── Bottom Feedback & Action Bar ─────────────────────────────
                val barBgColor by animateColorAsState(
                    targetValue = when {
                        isChecked && isAnswerCorrect  -> DuolingoGreen.copy(alpha = 0.15f)
                        isChecked && !isAnswerCorrect -> DuolingoRed.copy(alpha = 0.15f)
                        else                          -> CardBackground
                    }, animationSpec = tween(350), label = "barBg"
                )
                Column(modifier = Modifier.fillMaxWidth().background(barBgColor).padding(horizontal = 20.dp, vertical = 16.dp)) {
                    AnimatedVisibility(
                        visible = isChecked,
                        enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(300)) + fadeIn(tween(250)),
                        exit  = slideOutVertically(targetOffsetY = { it }) + fadeOut(tween(150))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)
                                .correctPop(trigger = isChecked && isAnswerCorrect),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(if (isAnswerCorrect) "🎯" else "💔", fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    if (isAnswerCorrect) "AMAZING! CORRECT! ✨" else "NOT QUITE! 🐣",
                                    fontSize = 17.sp, fontWeight = FontWeight.Black,
                                    color = if (isAnswerCorrect) DuolingoGreen else DuolingoRed
                                )
                                if (!isAnswerCorrect && correctAnswerSummary.isNotEmpty()) {
                                    Text("Correct answer: $correctAnswerSummary", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = LocalDynamicThemeColors.current.textPrimary)
                                }
                            }
                        }
                    }

                    val canCheck = hasSelection || isChecked
                    AppButton(
                        title = if (!isChecked) "CHECK ANSWER" else "CONTINUE",
                        style = when {
                            !canCheck                     -> AppButtonStyle.DISABLED
                            isChecked && !isAnswerCorrect -> AppButtonStyle.DANGER_CRIMSON
                            else                          -> AppButtonStyle.SUCCESS_GREEN
                        },
                        isEnabled = canCheck && !isTransitioning,
                        onClick = {
                            if (!isChecked) {
                                if (hasSelection) {
                                    val correct = when (currentQuestion.gameType) {
                                        DSAExerciseType.MULTIPLE_CHOICE  -> selectedOptionIndex == currentQuestion.correctOptionIndex
                                        DSAExerciseType.MATCH_PAIRS      -> matchedPairs.size == currentQuestion.matchSolution.size && currentQuestion.matchSolution.all { matchedPairs[it.key] == it.value }
                                        DSAExerciseType.ORDER_STEPS      -> orderedSteps == currentQuestion.orderStepsSolution
                                        DSAExerciseType.FILL_CODE        -> selectedFillToken == currentQuestion.fillCodeCorrectToken
                                        DSAExerciseType.COMPLEXITY_DIAL  -> selectedComplexity == currentQuestion.complexityDialCorrect
                                        DSAExerciseType.TRUE_FALSE_SWIPE -> selectedTrueFalse == currentQuestion.trueFalseIsCorrectTrue
                                        else                             -> selectedOptionIndex == currentQuestion.correctOptionIndex
                                    }
                                    isAnswerCorrect = correct
                                    isChecked = true
                                    if (correct) { correctCount += 1; feedback.correct() }
                                    else { gameManager.loseHeart(); feedback.wrong(); wrongAnswerCount += 1 }
                                }
                            } else {
                                advanceQuestion()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    )
                }
            }
        }

        if (showHintDialog) {
            AlertDialog(
                onDismissRequest = { showHintDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🎮", fontSize = 20.sp); Spacer(modifier = Modifier.width(8.dp))
                        Text("Phoenix Cheat Code", fontSize = 18.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
                    }
                },
                text = { Text(currentQuestion.hint ?: "Master the data structure patterns!", fontSize = 14.sp, color = SubtextGray) },
                confirmButton = { TextButton(onClick = { showHintDialog = false }) { Text("GOT IT! ⚡️", fontWeight = FontWeight.Black, color = DuolingoBlue) } },
                containerColor = DuolingoCardBg,
                shape = RoundedCornerShape(18.dp)
            )
        }
    }
}
