package com.simats.codeingo.ui.onboarding

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.components.AppButton
import com.simats.codeingo.ui.components.AppButtonStyle
import com.simats.codeingo.ui.components.AppCard
import com.simats.codeingo.ui.components.CandyCrushStarsView
import com.simats.codeingo.ui.components.DuolingoSpeechBubbleView
import com.simats.codeingo.ui.phoenix.PhoenixAnimatedMascotView
import com.simats.codeingo.ui.phoenix.PhoenixAtmosphericBackgroundView
import com.simats.codeingo.ui.phoenix.PhoenixMascotPose
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.PhoenixMotion
import com.simats.codeingo.ui.theme.pressScale
import com.simats.codeingo.ui.theme.staggeredAppear
import kotlinx.coroutines.delay

// ══════════════════════════════════════════════════════════════════
// 🚀 BeginnerOnboardingFlowView — 12-Step Journey with Phoenix
// Exact Parity with iOS BeginnerOnboardingFlowView.swift
// ══════════════════════════════════════════════════════════════════

enum class OnboardingStep(val stepIndex: Int) {
    WELCOME(0),
    TEN_QUESTIONS_INTRO(1),
    LANGUAGE_SELECTION(2),
    REFERRAL_SOURCE(3),
    TEMPERATURE_GAME(4),
    FIRST_LESSON_INTRO(5),
    SENTENCE_COMPLETION(6),
    PROUD_CELEBRATION(7),
    STREAK_EARNED(8),
    STREAK_GOAL(9),
    WIDGET_CHEER(10),
    LOADING_CEFR(11)
}

@Composable
fun BeginnerOnboardingFlowView(
    onCompleteOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableStateOf(OnboardingStep.WELCOME) }
    var selectedLanguage by remember { mutableStateOf<String?>("Python 🐍") }
    var selectedReferral by remember { mutableStateOf<String?>("LeetCode & Tech Interviews 💼") }
    var placedWord by remember { mutableStateOf<String?>(null) }
    var isSentenceChecked by remember { mutableStateOf(false) }
    var isSentenceCorrect by remember { mutableStateOf(false) }
    var streakCountDisplay by remember { mutableIntStateOf(0) }
    var selectedStreakGoalDays by remember { mutableIntStateOf(7) }

    val totalSteps = OnboardingStep.entries.size
    val currentProgress = (currentStep.stepIndex + 1).toFloat() / totalSteps.toFloat()
    val animatedProgress by animateFloatAsState(
        targetValue = currentProgress,
        animationSpec = PhoenixMotion.SnappySpring,
        label = "onboardingProgress"
    )

    fun advanceStep() {
        val nextIdx = currentStep.stepIndex + 1
        if (nextIdx < totalSteps) {
            currentStep = OnboardingStep.entries[nextIdx]
        } else {
            onCompleteOnboarding()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        PhoenixAtmosphericBackgroundView()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top Bar: Back button + Animated Progress Bar
            if (currentStep != OnboardingStep.LOADING_CEFR) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 12.dp)
                ) {
                    if (currentStep.stepIndex > 0) {
                        IconButton(
                            onClick = {
                                if (currentStep.stepIndex > 0) {
                                    currentStep = OnboardingStep.entries[currentStep.stepIndex - 1]
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "Back",
                                tint = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.85f)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(32.dp))
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    // Segmented / Glowing Progress Bar
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(12.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFF14_12_24).copy(alpha = 0.85f))
                            .border(1.dp, LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.15f), RoundedCornerShape(50))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedProgress.coerceAtLeast(0.06f))
                                .height(12.dp)
                                .clip(RoundedCornerShape(50))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(AmberGold, Color(0xFFFF8C1A))
                                    )
                                )
                        )
                    }
                }
            }

            // Step Content Router
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(380, easing = FastOutSlowInEasing)
                    ) + fadeIn(tween(380)) togetherWith
                            slideOutHorizontally(
                                targetOffsetX = { -it },
                                animationSpec = tween(380, easing = FastOutSlowInEasing)
                            ) + fadeOut(tween(380))
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                label = "onboardingRouter"
            ) { step ->
                when (step) {
                    OnboardingStep.WELCOME -> {
                        WelcomeStepView(onContinue = { advanceStep() })
                    }
                    OnboardingStep.TEN_QUESTIONS_INTRO -> {
                        TenQuestionsStepView(onContinue = { advanceStep() })
                    }
                    OnboardingStep.LANGUAGE_SELECTION -> {
                        LanguageSelectionStepView(
                            selected = selectedLanguage,
                            onSelect = { selectedLanguage = it },
                            onContinue = { advanceStep() }
                        )
                    }
                    OnboardingStep.REFERRAL_SOURCE -> {
                        ReferralStepView(
                            selected = selectedReferral,
                            onSelect = { selectedReferral = it },
                            onContinue = { advanceStep() }
                        )
                    }
                    OnboardingStep.TEMPERATURE_GAME -> {
                        CreativeTemperatureGaugeView(
                            onCorrectAnswer = { advanceStep() }
                        )
                    }
                    OnboardingStep.FIRST_LESSON_INTRO -> {
                        FirstLessonIntroStepView(onContinue = { advanceStep() })
                    }
                    OnboardingStep.SENTENCE_COMPLETION -> {
                        SentenceCompletionStepView(
                            placedWord = placedWord,
                            isChecked = isSentenceChecked,
                            isCorrect = isSentenceCorrect,
                            onPlaceWord = { placedWord = it },
                            onCheck = {
                                isSentenceChecked = true
                                isSentenceCorrect = (placedWord == "0")
                            },
                            onContinue = { advanceStep() },
                            onRetry = {
                                isSentenceChecked = false
                                isSentenceCorrect = false
                                placedWord = null
                            }
                        )
                    }
                    OnboardingStep.PROUD_CELEBRATION -> {
                        ProudCelebrationStepView(onContinue = { advanceStep() })
                    }
                    OnboardingStep.STREAK_EARNED -> {
                        StreakEarnedStepView(
                            streakCount = streakCountDisplay,
                            onStartOdometer = { streakCountDisplay = 1 },
                            onContinue = { advanceStep() }
                        )
                    }
                    OnboardingStep.STREAK_GOAL -> {
                        StreakGoalStepView(
                            selectedDays = selectedStreakGoalDays,
                            onSelectDays = { selectedStreakGoalDays = it },
                            onContinue = { advanceStep() }
                        )
                    }
                    OnboardingStep.WIDGET_CHEER -> {
                        WidgetCheerStepView(onContinue = { advanceStep() })
                    }
                    OnboardingStep.LOADING_CEFR -> {
                        LoadingCefrStepView(onComplete = { onCompleteOnboarding() })
                    }
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// Step Composable Views
// ══════════════════════════════════════════════════════════════════

@Composable
private fun WelcomeStepView(onContinue: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        DuolingoSpeechBubbleView(text = "Hi there! I'm Phoenix!")
        Spacer(modifier = Modifier.height(24.dp))
        PhoenixAnimatedMascotView(pose = PhoenixMascotPose.Welcoming, size = 210.dp)
        Spacer(modifier = Modifier.weight(1f))
        AppButton(title = "CONTINUE", style = AppButtonStyle.PRIMARY_AMBER, onClick = onContinue)
    }
}

@Composable
private fun TenQuestionsStepView(onContinue: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        DuolingoSpeechBubbleView(text = "Just 10 quick questions before we start your first lesson!")
        Spacer(modifier = Modifier.height(24.dp))
        PhoenixAnimatedMascotView(pose = PhoenixMascotPose.Welcoming, size = 210.dp)
        Spacer(modifier = Modifier.weight(1f))
        AppButton(title = "CONTINUE", style = AppButtonStyle.PRIMARY_AMBER, onClick = onContinue)
    }
}

@Composable
private fun LanguageSelectionStepView(
    selected: String?,
    onSelect: (String) -> Unit,
    onContinue: () -> Unit
) {
    val languages = listOf("Python 🐍", "Swift 🦅", "C++ ⚡", "Java ☕", "Go / Golang 🐹", "JavaScript 🌐")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PhoenixAnimatedMascotView(pose = PhoenixMascotPose.Noting(isWriting = selected != null), size = 100.dp)
            Spacer(modifier = Modifier.width(10.dp))
            DuolingoSpeechBubbleView(text = "Which language do you code in?", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            languages.forEachIndexed { index, lang ->
                val isSelected = selected == lang
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredAppear(index, baseDelayMs = 35)
                        .pressScale(targetScale = 0.97f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) AmberGold.copy(alpha = 0.20f) else Color(0xFF141F38).copy(alpha = 0.8f))
                        .border(if (isSelected) 1.8.dp else 1.dp, if (isSelected) AmberGold else LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onSelect(lang) }
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = lang,
                        color = LocalDynamicThemeColors.current.textPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }
        }

        AppButton(
            title = "CONTINUE",
            style = if (selected != null) AppButtonStyle.PRIMARY_AMBER else AppButtonStyle.DISABLED,
            isEnabled = selected != null,
            onClick = onContinue
        )
    }
}

@Composable
private fun ReferralStepView(
    selected: String?,
    onSelect: (String) -> Unit,
    onContinue: () -> Unit
) {
    val goals = listOf(
        "LeetCode & Tech Interviews 💼",
        "Computer Science Course 🎓",
        "Competitive Programming 🏆",
        "High-Performance Engineering 🚀",
        "Brain Training & Problem Solving 🧠"
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PhoenixAnimatedMascotView(pose = PhoenixMascotPose.Noting(isWriting = selected != null), size = 100.dp)
            Spacer(modifier = Modifier.width(10.dp))
            DuolingoSpeechBubbleView(text = "Why are you learning Data Structures?", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            goals.forEachIndexed { index, goal ->
                val isSelected = selected == goal
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredAppear(index, baseDelayMs = 35)
                        .pressScale(targetScale = 0.97f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) AmberGold.copy(alpha = 0.20f) else Color(0xFF141F38).copy(alpha = 0.8f))
                        .border(if (isSelected) 1.8.dp else 1.dp, if (isSelected) AmberGold else LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onSelect(goal) }
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = goal,
                        color = LocalDynamicThemeColors.current.textPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }
        }

        AppButton(
            title = "CONTINUE",
            style = if (selected != null) AppButtonStyle.PRIMARY_AMBER else AppButtonStyle.DISABLED,
            isEnabled = selected != null,
            onClick = onContinue
        )
    }
}

@Composable
private fun FirstLessonIntroStepView(onContinue: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        DuolingoSpeechBubbleView(
            text = "Okay! Here's your first 2 minute lesson.",
            highlightedText = "2 minute",
            highlightColor = AmberGold
        )
        Spacer(modifier = Modifier.height(24.dp))
        PhoenixAnimatedMascotView(pose = PhoenixMascotPose.Welcoming, size = 210.dp)
        Spacer(modifier = Modifier.weight(1f))
        AppButton(title = "CONTINUE", style = AppButtonStyle.PRIMARY_AMBER, onClick = onContinue)
    }
}

@Composable
private fun SentenceCompletionStepView(
    placedWord: String?,
    isChecked: Boolean,
    isCorrect: Boolean,
    onPlaceWord: (String) -> Unit,
    onCheck: () -> Unit,
    onContinue: () -> Unit,
    onRetry: () -> Unit
) {
    val choices = listOf("0", "1", "NULL", "n - 1")
    val isDark = LocalDynamicThemeColors.current.isDark

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(AmberGold.copy(alpha = 0.18f))
                .border(1.dp, AmberGold.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text(
                text = "DATA HIGHWAY: ZERO-BASED INDEX",
                color = AmberGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Complete the DSA Concept",
            color = if (isDark) Color.White else Color(0xFF12_18_26),
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sentence Card
        AppCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 18.dp,
            accentGlow = AmberGold.copy(alpha = 0.15f),
            contentPadding = 18.dp
        ) {
            Column {
                Text(
                    text = "In contiguous memory, an Array's first element is always stored at index",
                    color = if (isDark) Color.White else Color(0xFF12_18_26),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (placedWord != null) AmberGold else Color.White.copy(alpha = 0.08f))
                        .border(1.5.dp, if (placedWord != null) AmberGold else LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = placedWord ?: "  [ ? ]  ",
                        color = if (placedWord != null) Color(0xFF1A1205) else LocalDynamicThemeColors.current.placeholder,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Word Bank
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            for (word in choices) {
                val isUsed = placedWord == word
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .pressScale(targetScale = 0.93f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isUsed) Color.Transparent else (if (isDark) Color(0xFF14_1F_38) else Color(0xFFE2_E8_F0)))
                        .border(1.5.dp, if (isUsed) LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.1f) else AmberGold.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                        .clickable(enabled = !isChecked, interactionSource = remember { MutableInteractionSource() }, indication = null) { onPlaceWord(word) }
                        .padding(vertical = 12.dp)
                ) {
                    Text(
                        text = word,
                        color = if (isUsed) LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.25f) else (if (isDark) Color.White else Color(0xFF12_18_26)),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Feedback Text
        if (isChecked) {
            Text(
                text = if (isCorrect) "Awesome! In programming, array indexing starts at 0!" else "Not quite! Remember: base offset starts at index 0.",
                color = if (isCorrect) AmberGold else Color(0xFFFF4B4B),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        AppButton(
            title = if (isChecked) (if (isCorrect) "CONTINUE" else "TRY AGAIN") else "CHECK",
            style = if (isChecked) (if (isCorrect) AppButtonStyle.SUCCESS_GREEN else AppButtonStyle.DANGER_CRIMSON) else AppButtonStyle.PRIMARY_AMBER,
            isEnabled = placedWord != null,
            onClick = {
                if (!isChecked) onCheck()
                else if (isCorrect) onContinue()
                else onRetry()
            }
        )
    }
}

@Composable
private fun ProudCelebrationStepView(onContinue: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        CandyCrushStarsView(earnedStars = 5)
        Spacer(modifier = Modifier.height(24.dp))
        DuolingoSpeechBubbleView(text = "Incredible work! You crushed your first DSA concept!")
        Spacer(modifier = Modifier.height(20.dp))
        PhoenixAnimatedMascotView(pose = PhoenixMascotPose.StarryEyes, size = 190.dp)
        Spacer(modifier = Modifier.weight(1f))
        AppButton(title = "CONTINUE", style = AppButtonStyle.PRIMARY_AMBER, onClick = onContinue)
    }
}

@Composable
private fun StreakEarnedStepView(
    streakCount: Int,
    onStartOdometer: () -> Unit,
    onContinue: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(300)
        onStartOdometer()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = Icons.Default.LocalFireDepartment,
            contentDescription = "Streak Flame",
            tint = AmberGold,
            modifier = Modifier.size(72.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "$streakCount",
            color = AmberGold,
            fontSize = 54.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif
        )

        Text(
            text = "DAY STREAK!",
            color = LocalDynamicThemeColors.current.textPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        DuolingoSpeechBubbleView(text = "Practice every day to keep your fire burning strong!")

        Spacer(modifier = Modifier.weight(1f))
        AppButton(title = "CONTINUE", style = AppButtonStyle.PRIMARY_AMBER, onClick = onContinue)
    }
}

@Composable
private fun StreakGoalStepView(
    selectedDays: Int,
    onSelectDays: (Int) -> Unit,
    onContinue: () -> Unit
) {
    val options = listOf(7, 14, 30, 50)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        DuolingoSpeechBubbleView(text = "Set your streak goal to build unstoppable coding momentum!")
        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            for (days in options) {
                val isSelected = selectedDays == days
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .pressScale(targetScale = 0.97f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) AmberGold.copy(alpha = 0.20f) else Color(0xFF141F38))
                        .border(1.5.dp, if (isSelected) AmberGold else LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onSelectDays(days) }
                        .padding(horizontal = 18.dp, vertical = 16.dp)
                ) {
                    Column {
                        Text(
                            text = "$days-Day Streak Challenge",
                            color = LocalDynamicThemeColors.current.textPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.SansSerif
                        )
                        Text(
                            text = if (days == 7) "Casual" else if (days == 14) "Regular" else if (days == 30) "Serious" else "Master",
                            color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.SansSerif
                        )
                    }

                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = AmberGold,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        AppButton(title = "SET GOAL", style = AppButtonStyle.PRIMARY_AMBER, onClick = onContinue)
    }
}

@Composable
private fun WidgetCheerStepView(onContinue: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        DuolingoSpeechBubbleView(text = "Add the Phoenix widget to your Home Screen to never lose your streak!")
        Spacer(modifier = Modifier.height(24.dp))
        PhoenixAnimatedMascotView(pose = PhoenixMascotPose.StreakFlame, size = 190.dp)
        Spacer(modifier = Modifier.weight(1f))
        AppButton(title = "LET'S CODE!", style = AppButtonStyle.PRIMARY_AMBER, onClick = onContinue)
    }
}

@Composable
private fun LoadingCefrStepView(onComplete: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2200)
        onComplete()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        CircularProgressIndicator(
            color = AmberGold,
            strokeWidth = 4.dp,
            modifier = Modifier.size(54.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "COMPILING ENVIRONMENT...",
            color = LocalDynamicThemeColors.current.textPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tailoring algorithms to your selected language",
            color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.7f),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.SansSerif
        )
    }
}
