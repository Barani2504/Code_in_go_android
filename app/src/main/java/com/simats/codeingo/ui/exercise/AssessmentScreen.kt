package com.simats.codeingo.ui.exercise

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.components.DynamicPillHUD
import com.simats.codeingo.ui.components.DynamicPillController
import com.simats.codeingo.ui.components.DynamicPillEvent
import com.simats.codeingo.ui.theme.BehindPhoenixGlow
import com.simats.codeingo.ui.theme.ParticleBurstCanvas
import com.simats.codeingo.ui.theme.ScreenEdgeLighting
import com.simats.codeingo.ui.theme.TypewriterText
import com.simats.codeingo.data.model.DSAExerciseType
import com.simats.codeingo.data.repository.QuizQuestion
import com.simats.codeingo.data.repository.QuizQuestionsData
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.ui.components.AppButton
import com.simats.codeingo.ui.components.AppButtonStyle
import com.simats.codeingo.ui.components.ProgressBarAnimated
import com.simats.codeingo.ui.phoenix.AnimatedGIFView
import com.simats.codeingo.ui.phoenix.PhoenixEggHatch3DView
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DuolingoBlue
import com.simats.codeingo.ui.theme.DuolingoCardBg
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoInputBg
import com.simats.codeingo.ui.theme.DuolingoInputBorder
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.DuolingoSubtext
import com.simats.codeingo.ui.theme.FeedbackManager
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.PhoenixCrimson
import com.simats.codeingo.ui.theme.PhoenixGreen
import com.simats.codeingo.ui.theme.PhoenixMotion
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

    // Game-feel reactive triggers
    var edgeLightTrigger by remember { mutableIntStateOf(0) }
    var edgeLightColor   by remember { mutableStateOf(PhoenixGreen) }
    var particleTrigger  by remember { mutableIntStateOf(0) }
    var currentCombo     by remember { mutableIntStateOf(0) }

    // Single-composable slide via Animatable — prevents dual-state bleed
    val slideOffsetPx   = remember { Animatable(0f) }
    var isTransitioning by remember { mutableStateOf(false) }

    fun advanceQuestion() {
        scope.launch {
            isTransitioning = true
            val fullWidth = with(density) { 420.dp.toPx() }
            slideOffsetPx.snapTo(fullWidth)
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
        timeRemaining       = 45
        selectedOptionIndex = null
        matchedPairs        = emptyMap()
        orderedSteps        = currentQuestion.orderStepsInitial
        selectedFillToken   = null
        selectedComplexity  = null
        selectedTrueFalse   = null
        isChecked           = false
        isAnswerCorrect     = false
    }

    LaunchedEffect(currentIndex, isChecked, isFinished) {
        while (!isChecked && !isFinished && timeRemaining > 0) {
            delay(1000)
            timeRemaining -= 1
            if (timeRemaining == 10 || timeRemaining == 5) {
                feedback.timerWarning()
                DynamicPillController.post(DynamicPillEvent.TimerWarning(timeRemaining))
            }
        }
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

    LaunchedEffect(isFinished) {
        if (isFinished) {
            feedback.levelComplete()
            DynamicPillController.post(DynamicPillEvent.LevelComplete(totalXPEarned))
        }
    }

    Box(modifier = modifier.fillMaxSize().background(LocalDynamicThemeColors.current.background)) {
        // Device-aware Edge Lighting & Confetti Particle Canvas
        ScreenEdgeLighting(trigger = edgeLightTrigger, color = edgeLightColor)
        ParticleBurstCanvas(trigger = particleTrigger, burstOrigin = Offset(540f, 750f), particleCount = 60)

        // Dynamic Pill Notch HUD (Device-aware Cutout integration)
        DynamicPillHUD(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 4.dp)
        )

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

                // ── GAMIFIED ARENA HUD HEADER ────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Close", tint = SubtextGray, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(6.dp))

                    // Progress capsule
                    val progress = if (questions.isNotEmpty()) currentIndex.toFloat() / questions.size else 0f
                    ProgressBarAnimated(progress = progress, modifier = Modifier.weight(1f).height(16.dp))

                    Spacer(modifier = Modifier.width(14.dp))

                    // Heart Crystal Counter with dynamic red glow
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier
                            .shake(shakeTrigger = wrongAnswerCount.takeIf { it > 0 })
                            .clip(RoundedCornerShape(12.dp))
                            .background(PhoenixCrimson.copy(alpha = 0.14f))
                            .border(1.2.dp, PhoenixCrimson.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Favorite, "Hearts", tint = PhoenixCrimson, modifier = Modifier.size(18.dp))
                        Text("$heartsCount", fontSize = 15.sp, fontWeight = FontWeight.Black, color = PhoenixCrimson)
                    }
                }

                // ── Scrollable Arena Content ─────────────────────────────────
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .offset { IntOffset(slideOffsetPx.value.roundToInt(), 0) }
                        .verticalScroll(scrollState)
                        .padding(horizontal = 20.dp)
                ) {
                    Spacer(modifier = Modifier.height(6.dp))

                    // Chapter / Stage Sub-Header + Cheat Code
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CardBackground)
                                .border(1.dp, DuolingoInputBorder, RoundedCornerShape(8.dp))
                                .padding(horizontal = 9.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Q${currentIndex + 1} OF ${questions.size}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = SubtextGray
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Live Stage Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AmberGold.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "CH $unitId • STAGE $unitId",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = AmberGold
                            )
                        }

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
                                    Text("🎮", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Cheat Code", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.White)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Animated Countdown Timer Indicator
                    val timerColor by animateColorAsState(
                        targetValue = when { timeRemaining <= 5 -> PhoenixCrimson; timeRemaining <= 10 -> AmberGold; else -> SubtextGray },
                        animationSpec = tween(400), label = "timerColor"
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Text(
                            "⏳ ${timeRemaining}s", fontSize = 14.sp, fontWeight = FontWeight.Black, color = timerColor,
                            modifier = if (timeRemaining <= 10) Modifier.pulse(1.0f..1.12f, 600) else Modifier
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // ── LIVING PHOENIX COMPANION & DIALOGUE STAGE ─────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Animated Phoenix GIF Avatar for the current chapter stage!
                        val stageName = "phoenix_stage_${unitId.coerceIn(1, 5)}"
                        Box(
                            modifier = Modifier.size(64.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Behind-Phoenix Glow sits behind the mascot, never overlapping Phoenix pixels
                            BehindPhoenixGlow(
                                size = 64.dp,
                                glowColor = if (isChecked && isAnswerCorrect) PhoenixGreen else AmberGold
                            )
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(PhoenixGreen.copy(alpha = 0.1f))
                                    .border(1.5.dp, PhoenixGreen.copy(alpha = 0.4f), RoundedCornerShape(18.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                AnimatedGIFView(
                                    resourceName = stageName,
                                    size = 56.dp
                                )
                            }
                        }

                        // Cyber-Holographic Dialogue Card
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(18.dp))
                                .background(DuolingoInputBg)
                                .border(1.5.dp, DuolingoInputBorder, RoundedCornerShape(18.dp))
                                .padding(14.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                TypewriterText(
                                    text = currentQuestion.promptSentence,
                                    textStyle = TextStyle(
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LocalDynamicThemeColors.current.textPrimary
                                    )
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

                    Spacer(modifier = Modifier.height(22.dp))

                    // ── GAMIFIED EXERCISE RENDERER ───────────────────────────
                    val interactionEnabled = !isTransitioning

                    when (currentQuestion.gameType) {

                        // ── Holographic DSA Command Matrix ───────────────────
                        DSAExerciseType.MULTIPLE_CHOICE -> {
                            val badges = listOf("A", "B", "C", "D", "E", "F")
                            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                currentQuestion.options.forEachIndexed { index, optionText ->
                                    val isSelected   = selectedOptionIndex == index
                                    val isCorrectOpt = index == currentQuestion.correctOptionIndex
                                    val badge = badges.getOrElse(index) { "${index + 1}" }

                                    HolographicCommandTile(
                                        text = optionText,
                                        badgeText = badge,
                                        isSelected = isSelected,
                                        isChecked = isChecked,
                                        isCorrectOpt = isCorrectOpt,
                                        isEnabled = !isChecked && interactionEnabled,
                                        onClick = {
                                            selectedOptionIndex = index
                                            feedback.tap()
                                        },
                                        modifier = Modifier
                                            .staggeredAppear(index = index, baseDelayMs = 50)
                                            .correctPop(trigger = isChecked && isSelected && isCorrectOpt)
                                            .shake(shakeTrigger = if (isChecked && isSelected && !isCorrectOpt) index else null)
                                    )
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
                            val badges = listOf("A", "B", "C", "D")
                            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                currentQuestion.options.forEachIndexed { index, optionText ->
                                    HolographicCommandTile(
                                        text = optionText,
                                        badgeText = badges.getOrElse(index) { "${index + 1}" },
                                        isSelected = selectedOptionIndex == index,
                                        isChecked = isChecked,
                                        isCorrectOpt = index == currentQuestion.correctOptionIndex,
                                        isEnabled = !isChecked && interactionEnabled,
                                        onClick = { selectedOptionIndex = index; feedback.tap() }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))
                }

                // ── DUOLINGO + RPG CELEBRATORY EVALUATION DRAWER ─────────────
                val isCorrect = isAnswerCorrect
                val bottomDrawerBg by animateColorAsState(
                    targetValue = when {
                        isChecked && isCorrect  -> Color(0xFF132F1E) // Emerald Victory Glass
                        isChecked && !isCorrect -> Color(0xFF381519) // Volcanic Crimson Glass
                        else                    -> CardBackground
                    }, animationSpec = tween(300), label = "bottomDrawerBg"
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(bottomDrawerBg)
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    // Evaluation Sheet Animation
                    AnimatedVisibility(
                        visible = isChecked,
                        enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(280)) + fadeIn(tween(200)),
                        exit  = slideOutVertically(targetOffsetY = { it }) + fadeOut(tween(150))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .correctPop(trigger = isChecked && isCorrect)
                                    .clip(CircleShape)
                                    .background(if (isCorrect) PhoenixGreen else PhoenixCrimson),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isCorrect) "✓" else "✕",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isCorrect) "BRILLIANT! ✨" else "NOT QUITE! 🐣",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isCorrect) PhoenixGreen else PhoenixCrimson
                                )
                                if (!isCorrect && correctAnswerSummary.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "Correct solution: $correctAnswerSummary",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                } else if (isCorrect) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "+15 XP EARNED • STREAK EXTENDED 🔥",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = PhoenixGreen.copy(alpha = 0.9f)
                                    )
                                }
                            }
                        }
                    }

                    // Duolingo Dynamic 3D Action Button (Disabled Gray → Bright Green → Continue)
                    val canCheck = hasSelection || isChecked
                    val buttonTitle = when {
                        !isChecked -> "CHECK"
                        isCorrect  -> "CONTINUE"
                        else       -> "GOT IT"
                    }
                    val buttonStyle = when {
                        !isChecked && !canCheck -> AppButtonStyle.DISABLED
                        !isChecked && canCheck  -> AppButtonStyle.SUCCESS_GREEN
                        isCorrect               -> AppButtonStyle.SUCCESS_GREEN
                        else                    -> AppButtonStyle.DANGER_CRIMSON
                    }

                    AppButton(
                        title = buttonTitle,
                        style = buttonStyle,
                        isEnabled = (canCheck || isChecked) && !isTransitioning,
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
                                    if (correct) {
                                        correctCount += 1
                                        currentCombo += 1
                                        feedback.correct(currentCombo)
                                        DynamicPillController.post(DynamicPillEvent.XP(15))
                                        if (currentCombo >= 2) {
                                            DynamicPillController.post(DynamicPillEvent.Combo(currentCombo))
                                        }
                                        edgeLightColor = PhoenixGreen
                                        edgeLightTrigger++
                                        particleTrigger++
                                    } else {
                                        currentCombo = 0
                                        gameManager.loseHeart()
                                        feedback.wrong()
                                        wrongAnswerCount += 1
                                        DynamicPillController.post(DynamicPillEvent.HeartLost)
                                        edgeLightColor = PhoenixCrimson
                                        edgeLightTrigger++
                                    }
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

// ═════════════════════════════════════════════════════════════════════════
// 🔘 HolographicCommandTile — Isometric 3D DSA Matrix Option Card
// Features: 4dp extruded bottom shadow, mechanical depress on tap,
// electric blue selection state, and green/red answer evaluation.
// ═════════════════════════════════════════════════════════════════════════
@Composable
private fun HolographicCommandTile(
    text: String,
    badgeText: String,
    isSelected: Boolean,
    isChecked: Boolean,
    isCorrectOpt: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val depth = 4.dp
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val currentOffset by animateDpAsState(
        targetValue = if (isPressed && isEnabled) depth else 0.dp,
        animationSpec = PhoenixMotion.PressSpringDp,
        label = "tileOffset"
    )

    val shape = RoundedCornerShape(16.dp)

    val borderColor by animateColorAsState(
        targetValue = when {
            isChecked && isSelected && isCorrectOpt -> PhoenixGreen
            isChecked && isSelected && !isCorrectOpt -> PhoenixCrimson
            isChecked && isCorrectOpt -> PhoenixGreen
            isSelected -> Color(0xFF1C_B0_F6) // Duolingo Electric Blue
            else -> DuolingoInputBorder
        }, label = "tileBorder"
    )

    val shadowColor = when {
        isChecked && isSelected && isCorrectOpt -> Color(0xFF1A_AE_47)
        isChecked && isSelected && !isCorrectOpt -> Color(0xFFB5_16_06)
        isChecked && isCorrectOpt -> Color(0xFF1A_AE_47)
        isSelected -> Color(0xFF0F_8A_CC)
        else -> Color(0xFF0B_10_1D)
    }

    val faceColor by animateColorAsState(
        targetValue = when {
            isChecked && isSelected && isCorrectOpt -> PhoenixGreen.copy(alpha = 0.22f)
            isChecked && isSelected && !isCorrectOpt -> PhoenixCrimson.copy(alpha = 0.22f)
            isChecked && isCorrectOpt -> PhoenixGreen.copy(alpha = 0.18f)
            isSelected -> Color(0xFF1C_B0_F6).copy(alpha = 0.22f)
            else -> DuolingoCardBg
        }, label = "tileFace"
    )

    val badgeBg by animateColorAsState(
        targetValue = when {
            isChecked && isSelected && isCorrectOpt -> PhoenixGreen
            isChecked && isSelected && !isCorrectOpt -> PhoenixCrimson
            isChecked && isCorrectOpt -> PhoenixGreen
            isSelected -> Color(0xFF1C_B0_F6)
            else -> Color.White.copy(alpha = 0.08f)
        }, label = "tileBadgeBg"
    )

    val badgeTextColor by animateColorAsState(
        targetValue = when {
            isSelected || (isChecked && isCorrectOpt) -> Color.White
            else -> DuolingoSubtext
        }, label = "tileBadgeText"
    )

    val isCodeSyntax = text.contains("()") || text.contains("O(") || text.contains("[]") || text.contains("->")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = isEnabled,
                onClick = onClick
            )
    ) {
        // 3D Bottom Shadow Bevel
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = depth)
                .clip(shape)
                .background(shadowColor)
        )
        // Top Face Layer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = currentOffset)
                .clip(shape)
                .background(faceColor)
                .border(if (isSelected || (isChecked && isCorrectOpt)) 2.dp else 1.2.dp, borderColor, shape)
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Isometric Socket Badge
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(badgeBg)
                    .border(1.dp, borderColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isChecked && (isSelected || isCorrectOpt)) {
                        if (isCorrectOpt) "✓" else "✕"
                    } else badgeText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = badgeTextColor
                )
            }

            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = if (isCodeSyntax) FontWeight.Black else FontWeight.Bold,
                fontFamily = if (isCodeSyntax) FontFamily.Monospace else FontFamily.Default,
                color = LocalDynamicThemeColors.current.textPrimary,
                modifier = Modifier.weight(1f)
            )

            // Right Radio LED Socket Indicator
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .border(
                        1.5.dp,
                        if (isSelected || (isChecked && isCorrectOpt)) borderColor else SubtextGray.copy(alpha = 0.4f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected || (isChecked && isCorrectOpt)) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(borderColor)
                    )
                }
            }
        }
    }
}
