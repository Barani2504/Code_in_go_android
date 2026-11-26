package com.simats.codeingo.ui.visualizer.scaffold

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.ui.components.AshButton
import com.simats.codeingo.ui.components.AshButtonSize
import com.simats.codeingo.ui.components.AshButtonStyle
import com.simats.codeingo.ui.dashboard.PhoenixEggCompanionMascotView
import com.simats.codeingo.ui.motion.AshHaptics
import com.simats.codeingo.ui.sound.AshSoundPool
import com.simats.codeingo.ui.theme.AshTheme
import com.simats.codeingo.ui.theme.LocalReduceMotion
import com.simats.codeingo.ui.theme.accentContainer
import com.simats.codeingo.ui.theme.background
import com.simats.codeingo.ui.theme.border
import com.simats.codeingo.ui.theme.statusError
import com.simats.codeingo.ui.theme.statusSuccess
import com.simats.codeingo.ui.theme.subtle
import com.simats.codeingo.ui.theme.surfaceContainer
import kotlinx.coroutines.launch

/**
 * 🏛️ VisualizerScaffold — Uniform Four-Beat Scaffold for all AshCrypt Visualizers.
 *
 * Implements the "Story → Watch → Try → Check" template:
 *  - Story: 5-second one-sentence hook & real-life analogy.
 *  - Watch: Guided demo with <= 15-word narration steps, pulse on changed element.
 *  - Try: Interactive single-variable manipulation with "What changed?" plain-words banner.
 *  - Check: Intuition test question with Ash feedback and +20 XP.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisualizerScaffold(
    spec: VisualizerSpec,
    currentBeat: VisualizerBeat,
    onBeatChange: (VisualizerBeat) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    watchStepIndex: Int = 0,
    totalWatchSteps: Int = spec.watchSteps.size,
    onWatchStepChange: (Int) -> Unit = {},
    whatChangedText: String? = null,
    showDetails: Boolean = false,
    onToggleDetails: (Boolean) -> Unit = {},
    storyContent: (@Composable () -> Unit)? = null,
    watchContent: (@Composable (stepIndex: Int, showDetails: Boolean) -> Unit)? = null,
    tryContent: (@Composable (showDetails: Boolean) -> Unit)? = null,
    onCheckCompleted: () -> Unit = {}
) {
    val view = LocalView.current
    val reduceMotion = LocalReduceMotion.current
    val coroutineScope = rememberCoroutineScope()
    var showLostSheet by remember(spec.id) { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Check Question State
    var selectedOptionIndex by remember(spec.id) { mutableIntStateOf(-1) }
    var isAnswerSubmitted by remember(spec.id) { mutableStateOf(false) }
    var isCorrectAnswer by remember(spec.id) { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = AshTheme.colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // ── TOP BAR ──────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    IconButton(
                        onClick = {
                            AshSoundPool.playTap()
                            AshHaptics.tap(view, reduceMotion)
                            onBackClick()
                        },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AshTheme.colors.ink
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = spec.title,
                            style = AshTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AshTheme.colors.ink,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Realm ${spec.realmNumber}: ${spec.realmTitle}",
                            style = AshTheme.typography.labelSmall,
                            color = AshTheme.colors.subtle,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                }

                // Compact, aligned "I'm lost" pill button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(AshTheme.colors.surface)
                        .border(1.2.dp, AshTheme.colors.border, RoundedCornerShape(18.dp))
                        .clickable {
                            AshSoundPool.playTap()
                            AshHaptics.tap(view, reduceMotion)
                            showLostSheet = true
                        }
                        .padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = AshTheme.colors.brand
                    )
                    Text(
                        text = "I'm lost",
                        style = AshTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = AshTheme.colors.ink,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            // ── FOUR-BEAT STEPPER PROGRESS ───────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VisualizerBeat.entries.forEach { beat ->
                    val isActive = beat == currentBeat
                    val isPast = beat.stepNumber < currentBeat.stepNumber
                    val barColor by animateColorAsState(
                        targetValue = when {
                            isActive -> AshTheme.colors.brand
                            isPast -> AshTheme.colors.brand.copy(alpha = 0.5f)
                            else -> AshTheme.colors.border
                        },
                        animationSpec = tween(200),
                        label = "beatBar"
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                AshSoundPool.playTap()
                                onBeatChange(beat)
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(barColor)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${beat.stepNumber}. ${beat.title}",
                            style = AshTheme.typography.labelSmall,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                            color = if (isActive) AshTheme.colors.brand else AshTheme.colors.subtle,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // ── DETAILS TOGGLE BAR (Visible in Watch & Try) ───────
            if (currentBeat == VisualizerBeat.WATCH || currentBeat == VisualizerBeat.TRY) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = AshTheme.colors.subtle
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Show technical details",
                            style = AshTheme.typography.bodySmall,
                            color = AshTheme.colors.subtle
                        )
                    }
                    Switch(
                        checked = showDetails,
                        onCheckedChange = {
                            AshSoundPool.playTap()
                            onToggleDetails(it)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AshTheme.colors.surface,
                            checkedTrackColor = AshTheme.colors.brand,
                            uncheckedThumbColor = AshTheme.colors.subtle,
                            uncheckedTrackColor = AshTheme.colors.border
                        )
                    )
                }
            }

            // ── ASH NARRATION BUBBLE ─────────────────────────────
            val narrationLine = when (currentBeat) {
                VisualizerBeat.STORY -> spec.story.oneSentenceHook
                VisualizerBeat.WATCH -> spec.watchSteps.getOrNull(watchStepIndex)?.narration ?: "Watch the transformation step-by-step."
                VisualizerBeat.TRY -> "Change one control below to see how the output changes!"
                VisualizerBeat.CHECK -> "Test your intuition! Can you answer this question?"
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PhoenixEggCompanionMascotView(
                    levelNumber = spec.realmNumber.coerceAtLeast(1),
                    modifier = Modifier.size(54.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(AshTheme.colors.surface)
                        .border(1.5.dp, AshTheme.colors.border, RoundedCornerShape(14.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = narrationLine,
                        style = AshTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = AshTheme.colors.ink,
                        fontSize = 15.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            // ── "WHAT CHANGED?" HIGHLIGHT CALLOUT ─────────────────
            val currentWhatChanged = whatChangedText ?: when (currentBeat) {
                VisualizerBeat.WATCH -> spec.watchSteps.getOrNull(watchStepIndex)?.whatChanged
                else -> null
            }

            if (!currentWhatChanged.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AshTheme.colors.accentContainer)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "💡 What changed: ",
                            style = AshTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = AshTheme.colors.brand
                        )
                        Text(
                            text = currentWhatChanged,
                            style = AshTheme.typography.bodyMedium,
                            color = AshTheme.colors.ink,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // ── MAIN INTERACTIVE BODY (Scrollable Area) ───────────
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                when (currentBeat) {
                    VisualizerBeat.STORY -> {
                        if (storyContent != null) {
                            storyContent()
                        } else {
                            DefaultStoryView(spec.story)
                        }
                    }
                    VisualizerBeat.WATCH -> {
                        watchContent?.invoke(watchStepIndex, showDetails)
                    }
                    VisualizerBeat.TRY -> {
                        tryContent?.invoke(showDetails)
                    }
                    VisualizerBeat.CHECK -> {
                        CheckQuestionView(
                            question = spec.checkQuestion,
                            selectedIndex = selectedOptionIndex,
                            isSubmitted = isAnswerSubmitted,
                            isCorrect = isCorrectAnswer,
                            onSelect = { idx ->
                                if (!isAnswerSubmitted) {
                                    AshSoundPool.playTap()
                                    selectedOptionIndex = idx
                                }
                            }
                        )
                    }
                }
            }

            // ── BOTTOM CONTROLS & STEPPER ACTION BAR ─────────────
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = AshTheme.colors.surface,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back / Step Back button
                    if (currentBeat == VisualizerBeat.WATCH && watchStepIndex > 0) {
                        AshButton(
                            text = "Back",
                            onClick = {
                                AshSoundPool.playWhoosh()
                                AshHaptics.tap(view, reduceMotion)
                                onWatchStepChange(watchStepIndex - 1)
                            },
                            variant = AshButtonStyle.SURFACE,
                            size = AshButtonSize.REGULAR,
                            modifier = Modifier.weight(0.35f)
                        )
                    } else if (currentBeat.stepNumber > 1) {
                        AshButton(
                            text = "Prev",
                            onClick = {
                                AshSoundPool.playTap()
                                val prevBeat = VisualizerBeat.entries[currentBeat.stepNumber - 2]
                                onBeatChange(prevBeat)
                            },
                            variant = AshButtonStyle.SURFACE,
                            size = AshButtonSize.REGULAR,
                            modifier = Modifier.weight(0.35f)
                        )
                    }

                    // Primary Forward Button
                    val nextButtonText = when (currentBeat) {
                        VisualizerBeat.STORY -> "Watch Ash Demo"
                        VisualizerBeat.WATCH -> {
                            if (watchStepIndex < totalWatchSteps - 1) "Next Step (${watchStepIndex + 1}/$totalWatchSteps)"
                            else "Try It Yourself"
                        }
                        VisualizerBeat.TRY -> "Test Your Knowledge"
                        VisualizerBeat.CHECK -> {
                            if (!isAnswerSubmitted) "Check Answer"
                            else "Complete Lab (+${spec.checkQuestion.xpReward} XP)"
                        }
                    }

                    AshButton(
                        text = nextButtonText,
                        onClick = {
                            when (currentBeat) {
                                VisualizerBeat.STORY -> {
                                    AshSoundPool.playWhoosh()
                                    onBeatChange(VisualizerBeat.WATCH)
                                }
                                VisualizerBeat.WATCH -> {
                                    if (watchStepIndex < totalWatchSteps - 1) {
                                        AshSoundPool.playTap()
                                        AshHaptics.tap(view, reduceMotion)
                                        onWatchStepChange(watchStepIndex + 1)
                                    } else {
                                        AshSoundPool.playWhoosh()
                                        onBeatChange(VisualizerBeat.TRY)
                                    }
                                }
                                VisualizerBeat.TRY -> {
                                    AshSoundPool.playWhoosh()
                                    onBeatChange(VisualizerBeat.CHECK)
                                }
                                VisualizerBeat.CHECK -> {
                                    if (!isAnswerSubmitted) {
                                        if (selectedOptionIndex >= 0) {
                                            isAnswerSubmitted = true
                                            val correct = (selectedOptionIndex == spec.checkQuestion.correctIndex)
                                            isCorrectAnswer = correct
                                            if (correct) {
                                                AshSoundPool.playCorrect()
                                                AshHaptics.success(view, reduceMotion)
                                                GameManager.instance.addXP(spec.checkQuestion.xpReward)
                                            } else {
                                                AshSoundPool.playWrong()
                                                AshHaptics.error(view, reduceMotion)
                                            }
                                        }
                                    } else {
                                        AshSoundPool.playLevelUp()
                                        AshHaptics.success(view, reduceMotion)
                                        onCheckCompleted()
                                    }
                                }
                            }
                        },
                        variant = AshButtonStyle.BRAND,
                        size = AshButtonSize.REGULAR,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    // ── "I'M LOST" BOTTOM SHEET ──────────────────────────────────
    if (showLostSheet) {
        ModalBottomSheet(
            onDismissRequest = { showLostSheet = false },
            sheetState = sheetState,
            containerColor = AshTheme.colors.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PhoenixEggCompanionMascotView(
                    levelNumber = spec.realmNumber.coerceAtLeast(1),
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = spec.lostHelp.simpleHeadline,
                    style = AshTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AshTheme.colors.ink,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AshTheme.colors.background)
                        .border(1.dp, AshTheme.colors.border, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = spec.lostHelp.simpleAnalogy,
                        style = AshTheme.typography.bodyLarge,
                        color = AshTheme.colors.ink,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⭐ Key Takeaway: ",
                        style = AshTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = AshTheme.colors.brand
                    )
                    Text(
                        text = spec.lostHelp.takeawayBullet,
                        style = AshTheme.typography.bodyMedium,
                        color = AshTheme.colors.ink,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                AshButton(
                    text = "Got it, Ash!",
                    variant = AshButtonStyle.BRAND,
                    onClick = {
                        AshSoundPool.playTap()
                        coroutineScope.launch {
                            sheetState.hide()
                            showLostSheet = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun DefaultStoryView(story: VisualizerStory) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AshTheme.colors.surface)
            .border(1.5.dp, AshTheme.colors.border, RoundedCornerShape(16.dp))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = story.iconEmoji,
            fontSize = 48.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = story.title,
            style = AshTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = AshTheme.colors.ink,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = story.realLifeAnalogy,
            style = AshTheme.typography.bodyLarge,
            color = AshTheme.colors.ink,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(AshTheme.colors.accentContainer)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "Core Concept: ${story.conceptName}",
                style = AshTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = AshTheme.colors.brand
            )
        }
    }
}

@Composable
private fun CheckQuestionView(
    question: CheckQuestion,
    selectedIndex: Int,
    isSubmitted: Boolean,
    isCorrect: Boolean,
    onSelect: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AshTheme.colors.surface)
            .border(1.5.dp, AshTheme.colors.border, RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Text(
            text = "QUIZ CHALLENGE",
            style = AshTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = AshTheme.colors.brand
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = question.prompt,
            style = AshTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = AshTheme.colors.ink,
            fontSize = 16.sp,
            lineHeight = 22.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        question.options.forEachIndexed { index, optionText ->
            val isSelected = (index == selectedIndex)
            val isThisCorrect = (index == question.correctIndex)

            val buttonColor by animateColorAsState(
                targetValue = when {
                    !isSubmitted && isSelected -> AshTheme.colors.brand
                    isSubmitted && isThisCorrect -> AshTheme.colors.statusSuccess
                    isSubmitted && isSelected && !isCorrect -> AshTheme.colors.statusError
                    else -> AshTheme.colors.surface
                },
                label = "optionColor"
            )

            val textColor = when {
                !isSubmitted && isSelected -> AshTheme.colors.background
                isSubmitted && (isThisCorrect || isSelected) -> Color.White
                else -> AshTheme.colors.ink
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(buttonColor)
                    .border(
                        1.5.dp,
                        if (isSelected && !isSubmitted) AshTheme.colors.brand else AshTheme.colors.border,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable(enabled = !isSubmitted) { onSelect(index) }
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Text(
                    text = optionText,
                    style = AshTheme.typography.bodyLarge,
                    color = textColor,
                    fontWeight = if (isSelected || (isSubmitted && isThisCorrect)) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 15.sp
                )
            }
        }

        if (isSubmitted) {
            Spacer(modifier = Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (isCorrect) AshTheme.colors.statusSuccess.copy(alpha = 0.15f)
                        else AshTheme.colors.statusError.copy(alpha = 0.15f)
                    )
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = if (isCorrect) "🎉 Correct! +${question.xpReward} XP" else "❌ Keep going!",
                        style = AshTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isCorrect) AshTheme.colors.statusSuccess else AshTheme.colors.statusError
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = question.explanation,
                        style = AshTheme.typography.bodyMedium,
                        color = AshTheme.colors.ink,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
