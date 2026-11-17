package com.simats.codeingo.ui.onboarding

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.R
import com.simats.codeingo.data.model.phoenixEmotion
import com.simats.codeingo.domain.AuthService
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.domain.guide.PhoenixPose
import com.simats.codeingo.ui.components.AshButton
import com.simats.codeingo.ui.components.AshButtonStyle
import com.simats.codeingo.ui.components.AshMono
import com.simats.codeingo.ui.components.guide.SpeechBubble
import com.simats.codeingo.ui.components.guide.SpeechBubbleTail
import com.simats.codeingo.ui.phoenix.Phoenix3DLiveWavingMascotView
import com.simats.codeingo.ui.phoenix.PhoenixAtmosphericBackgroundView
import com.simats.codeingo.ui.theme.AshTheme
import com.simats.codeingo.ui.theme.AshType
import com.simats.codeingo.ui.theme.PhoenixMotion
import kotlinx.coroutines.delay

enum class OnboardingStep(val stepIndex: Int) {
    WELCOME(0),
    GOAL_SELECTION(1),
    DAILY_TIME(2),
    PRIOR_LEVEL(3),
    CAESAR_INTRO(4),
    CAESAR_LESSON(5),
    FIRST_SUCCESS(6),
    INITIALIZING(7)
}

/**
 * BeginnerOnboardingFlowView — 8-step guided journey with Ash the Phoenix mascot:
 * 1. Welcome (Ash says hello)
 * 2. Goal Selection (exam / career / curiosity)
 * 3. Daily Time (5 / 10 / 15 min)
 * 4. Prior Level (beginner / a little / comfortable)
 * 5. First Lesson Intro: Caesar's Secret Note
 * 6. Interactive Caesar Shift Dial (solve KHOOR -> HELLO)
 * 7. First Success Celebration (+20 XP & Day 1 streak)
 * 8. Initializing & Transition to Home
 */
@Composable
fun BeginnerOnboardingFlowView(
    onCompleteOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentStep by remember { mutableStateOf(OnboardingStep.WELCOME) }
    var selectedGoal by remember { mutableStateOf<String?>("🎓 Ace University Exams (CS8792 / CNS)") }
    var selectedTimeMinutes by remember { mutableIntStateOf(10) }
    var selectedPriorLevel by remember { mutableStateOf<String?>("🌱 Complete Beginner (Never heard of it)") }

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
            // Persist learner preferences per user
            val userId = AuthService.shared.currentUserId ?: "guest"
            val prefs = context.getSharedPreferences("ashcrypt_onboarding_prefs", Context.MODE_PRIVATE)
            prefs.edit()
                .putString("user_goal_$userId", selectedGoal)
                .putInt("daily_minutes_$userId", selectedTimeMinutes)
                .putString("prior_level_$userId", selectedPriorLevel)
                .putBoolean("onboarding_completed_$userId", true)
                .apply()

            // Award starting XP and ignite streak
            try {
                GameManager.instance.addXP(20)
            } catch (_: Throwable) {}

            onCompleteOnboarding()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AshTheme.colors.bg)
    ) {
        PhoenixAtmosphericBackgroundView()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top Navigation & Progress Bar
            if (currentStep != OnboardingStep.INITIALIZING) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentStep.stepIndex > 0) {
                        IconButton(
                            onClick = {
                                val prevIdx = currentStep.stepIndex - 1
                                if (prevIdx >= 0) {
                                    currentStep = OnboardingStep.entries[prevIdx]
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "Back",
                                tint = AshTheme.colors.ink
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(48.dp))
                    }

                    // Rounded Progress Bar
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(10.dp)
                            .clip(CircleShape)
                            .background(AshTheme.colors.surface)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedProgress)
                                .height(10.dp)
                                .clip(CircleShape)
                                .background(AshTheme.colors.brand)
                        )
                    }

                    Spacer(modifier = Modifier.width(48.dp))
                }
            }

            AnimatedContent(
                targetState = currentStep,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "onboardingRouter",
                modifier = Modifier.weight(1f)
            ) { step ->
                when (step) {
                    OnboardingStep.WELCOME -> {
                        WelcomeStep(onContinue = { advanceStep() })
                    }
                    OnboardingStep.GOAL_SELECTION -> {
                        GoalSelectionStep(
                            selected = selectedGoal,
                            onSelect = { selectedGoal = it },
                            onContinue = { advanceStep() }
                        )
                    }
                    OnboardingStep.DAILY_TIME -> {
                        DailyTimeStep(
                            selectedMinutes = selectedTimeMinutes,
                            onSelectMinutes = { selectedTimeMinutes = it },
                            onContinue = { advanceStep() }
                        )
                    }
                    OnboardingStep.PRIOR_LEVEL -> {
                        PriorLevelStep(
                            selected = selectedPriorLevel,
                            onSelect = { selectedPriorLevel = it },
                            onContinue = { advanceStep() }
                        )
                    }
                    OnboardingStep.CAESAR_INTRO -> {
                        CaesarIntroStep(onContinue = { advanceStep() })
                    }
                    OnboardingStep.CAESAR_LESSON -> {
                        CaesarDialLessonStep(onDecodedSuccessfully = { advanceStep() })
                    }
                    OnboardingStep.FIRST_SUCCESS -> {
                        FirstSuccessCelebrationStep(onContinue = { advanceStep() })
                    }
                    OnboardingStep.INITIALIZING -> {
                        InitializingStep(onReady = { advanceStep() })
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// STEP 1: WELCOME
// ─────────────────────────────────────────────────────────────────
@Composable
private fun WelcomeStep(onContinue: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))

        SpeechBubble(
            text = "Hi, I'm Ash! Your fiery guide to cryptography and network security. Together, we'll turn secret codes into superpowers!",
            actionLabel = "Hi Ash!",
            onActionClick = onContinue,
            tail = SpeechBubbleTail.BOTTOM_LEFT
        )

        Spacer(modifier = Modifier.height(20.dp))

        Phoenix3DLiveWavingMascotView(size = 240.dp)

        Spacer(modifier = Modifier.weight(1f))
    }
}

// ─────────────────────────────────────────────────────────────────
// STEP 2: GOAL SELECTION
// ─────────────────────────────────────────────────────────────────
@Composable
private fun GoalSelectionStep(
    selected: String?,
    onSelect: (String) -> Unit,
    onContinue: () -> Unit
) {
    val goals = listOf(
        "🎓 Ace University Exams (CS8792 / CNS)",
        "💼 Tech Career & Security Analyst Jobs",
        "🕵️‍♂️ Curiosity & Ethical Hacking",
        "🧠 Master Modern Number Theory & Ciphers"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        SpeechBubble(
            text = "What brings you to cryptography? I'll tune your study path to match your target.",
            actionLabel = "Continue",
            onActionClick = onContinue,
            tail = SpeechBubbleTail.BOTTOM_LEFT
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            goals.forEach { goal ->
                val isSelected = selected == goal
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) AshTheme.colors.brand.copy(alpha = 0.15f) else AshTheme.colors.surface)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) AshTheme.colors.brand else AshTheme.colors.accent.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { onSelect(goal) }
                        .padding(horizontal = 18.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = goal,
                            style = AshType.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = AshTheme.colors.ink,
                            modifier = Modifier.weight(1f)
                        )

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = AshTheme.colors.brand,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        AshButton(
            text = "Continue",
            onClick = onContinue,
            style = if (selected != null) AshButtonStyle.PRIMARY else AshButtonStyle.OUTLINE,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        )
    }
}

// ─────────────────────────────────────────────────────────────────
// STEP 3: DAILY TIME
// ─────────────────────────────────────────────────────────────────
@Composable
private fun DailyTimeStep(
    selectedMinutes: Int,
    onSelectMinutes: (Int) -> Unit,
    onContinue: () -> Unit
) {
    val times = listOf(
        Pair(5, "⚡ 5 min / day (Casual Ember)"),
        Pair(10, "🔥 10 min / day (Steady Flame)"),
        Pair(15, "🌟 15 min / day (Blazing Phoenix)")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        SpeechBubble(
            text = "Consistency beats cramming every single time. How much daily time works best for you?",
            actionLabel = "Lock It In",
            onActionClick = onContinue,
            tail = SpeechBubbleTail.BOTTOM_LEFT
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            times.forEach { (mins, label) ->
                val isSelected = selectedMinutes == mins
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) AshTheme.colors.brand.copy(alpha = 0.15f) else AshTheme.colors.surface)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) AshTheme.colors.brand else AshTheme.colors.accent.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { onSelectMinutes(mins) }
                        .padding(horizontal = 18.dp, vertical = 18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            style = AshType.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = AshTheme.colors.ink
                        )

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = AshTheme.colors.brand,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        AshButton(
            text = "Set Daily Goal",
            onClick = onContinue,
            style = AshButtonStyle.PRIMARY,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        )
    }
}

// ─────────────────────────────────────────────────────────────────
// STEP 4: PRIOR LEVEL
// ─────────────────────────────────────────────────────────────────
@Composable
private fun PriorLevelStep(
    selected: String?,
    onSelect: (String) -> Unit,
    onContinue: () -> Unit
) {
    val levels = listOf(
        Pair("beginner", "🌱 Complete Beginner\nNever heard of cryptography before"),
        Pair("little", "🔑 A Little Experience\nKnow basic passwords and HTTPS padlocks"),
        Pair("comfortable", "🛡️ Comfortable\nKnow modular math or basic classic ciphers")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        SpeechBubble(
            text = "Where are you starting from? No matter your background, we build from ground zero.",
            actionLabel = "Continue",
            onActionClick = onContinue,
            tail = SpeechBubbleTail.BOTTOM_LEFT
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            levels.forEach { (id, label) ->
                val isSelected = selected?.contains(id, ignoreCase = true) == true
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) AshTheme.colors.brand.copy(alpha = 0.15f) else AshTheme.colors.surface)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) AshTheme.colors.brand else AshTheme.colors.accent.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { onSelect(label) }
                        .padding(horizontal = 18.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = label,
                        style = AshType.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        ),
                        color = AshTheme.colors.ink
                    )
                }
            }
        }

        AshButton(
            text = "Let's Begin!",
            onClick = onContinue,
            style = AshButtonStyle.PRIMARY,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        )
    }
}

// ─────────────────────────────────────────────────────────────────
// STEP 5: CAESAR INTRO
// ─────────────────────────────────────────────────────────────────
@Composable
private fun CaesarIntroStep(onContinue: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))

        SpeechBubble(
            text = "Over 2,000 years ago, Julius Caesar sent secret battle orders by shifting every letter by 3. Let's crack an intercepted message right now!",
            actionLabel = "Decode The Note",
            onActionClick = onContinue,
            tail = SpeechBubbleTail.BOTTOM_LEFT
        )

        Spacer(modifier = Modifier.height(24.dp))

        Image(
            painter = painterResource(id = R.drawable.phoenix_emotion_4), // Curious Ash
            contentDescription = "Ash Curious",
            modifier = Modifier.size(190.dp)
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}

// ─────────────────────────────────────────────────────────────────
// STEP 6: INTERACTIVE CAESAR SHIFT DIAL
// ─────────────────────────────────────────────────────────────────
@Composable
private fun CaesarDialLessonStep(onDecodedSuccessfully: () -> Unit) {
    // Original ciphertext is "KHOOR" (shifted +3 from "HELLO")
    val ciphertext = "KHOOR"
    var shiftDial by remember { mutableIntStateOf(0) } // shift offset applied by user

    // Function shifting character in alphabet
    fun shiftChar(c: Char, shift: Int): Char {
        if (c !in 'A'..'Z') return c
        val originalPos = c - 'A'
        val newPos = (originalPos + shift + 26) % 26
        return 'A' + newPos
    }

    val liveDecoded = ciphertext.map { shiftChar(it, shiftDial) }.joinToString("")
    val isCorrect = liveDecoded == "HELLO"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "CAESAR'S SECRET SCROLL",
            style = AshType.labelSmall.copy(
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                letterSpacing = 1.sp
            ),
            color = AshTheme.colors.brand
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Rotate the dial until the secret message makes sense!",
            style = AshType.bodyMedium.copy(fontSize = 14.sp),
            color = AshTheme.colors.inkMuted,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Intercepted Cipher Display Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(AshTheme.colors.surface)
                .border(2.dp, if (isCorrect) AshTheme.colors.brand else AshTheme.colors.accent.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "ORIGINAL CIPHERTEXT: K H O O R",
                    style = AshType.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = AshTheme.colors.inkMuted
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    liveDecoded.forEach { ch ->
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isCorrect) AshTheme.colors.brand else AshTheme.colors.accent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            AshMono(
                                text = ch.toString(),
                                fontSize = 24.sp,
                                color = if (isCorrect) AshTheme.colors.onBrand else AshTheme.colors.ink
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (isCorrect) {
                    Text(
                        text = "🎉 MATCH! Message decrypted: HELLO",
                        style = AshType.labelMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        ),
                        color = AshTheme.colors.brand
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Dial Controls
        Text(
            text = "Alphabet Shift: ${if (shiftDial > 0) "+$shiftDial" else "$shiftDial"}",
            style = AshType.titleMedium.copy(
                fontWeight = FontWeight.Black,
                fontSize = 18.sp
            ),
            color = AshTheme.colors.ink
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { if (shiftDial > -6) shiftDial -= 1 },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(AshTheme.colors.surface)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Shift Left",
                    tint = AshTheme.colors.brand
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Slider(
                value = shiftDial.toFloat(),
                onValueChange = { shiftDial = it.toInt() },
                valueRange = -6f..6f,
                steps = 11,
                colors = SliderDefaults.colors(
                    thumbColor = AshTheme.colors.brand,
                    activeTrackColor = AshTheme.colors.brand,
                    inactiveTrackColor = AshTheme.colors.surface
                ),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(
                onClick = { if (shiftDial < 6) shiftDial += 1 },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(AshTheme.colors.surface)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Shift Right",
                    tint = AshTheme.colors.brand
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        AshButton(
            text = if (isCorrect) "DECODE & VERIFY ✓" else "Try Shift -3 (Left 3)",
            onClick = {
                if (isCorrect) {
                    onDecodedSuccessfully()
                } else {
                    shiftDial = -3
                }
            },
            style = if (isCorrect) AshButtonStyle.PRIMARY else AshButtonStyle.OUTLINE,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        )
    }
}

// ─────────────────────────────────────────────────────────────────
// STEP 7: FIRST SUCCESS CELEBRATION
// ─────────────────────────────────────────────────────────────────
@Composable
private fun FirstSuccessCelebrationStep(onContinue: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = Icons.Default.LocalFireDepartment,
            contentDescription = "Fire",
            tint = AshTheme.colors.brand,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "FIRST CIPHER CRACKED!",
            style = AshType.titleLarge.copy(
                fontWeight = FontWeight.Black,
                fontSize = 24.sp
            ),
            color = AshTheme.colors.ink
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "+20 XP Earned • 1-Day Streak Ignited",
            style = AshType.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            ),
            color = AshTheme.colors.brand
        )

        Spacer(modifier = Modifier.height(20.dp))

        SpeechBubble(
            text = "You cracked your first cryptographic message! Julius Caesar's secret note was indeed 'HELLO'. You're officially a codebreaker!",
            actionLabel = "Enter Sanctuary",
            onActionClick = onContinue,
            tail = SpeechBubbleTail.BOTTOM_LEFT
        )

        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.drawable.phoenix_emotion_2), // Celebrating Ash
            contentDescription = "Ash Celebrating",
            modifier = Modifier.size(170.dp)
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}

// ─────────────────────────────────────────────────────────────────
// STEP 8: INITIALIZING
// ─────────────────────────────────────────────────────────────────
@Composable
private fun InitializingStep(onReady: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1800)
        onReady()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        CircularProgressIndicator(
            color = AshTheme.colors.brand,
            strokeWidth = 4.dp,
            modifier = Modifier.size(54.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "IGNITING ASHCRYPT...",
            style = AshType.titleMedium.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            ),
            color = AshTheme.colors.ink
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Preparing Realm 1: Secrets 101",
            style = AshType.bodyMedium.copy(fontSize = 13.sp),
            color = AshTheme.colors.inkMuted
        )
    }
}
