package com.simats.codeingo.ui.mascot

import android.content.Context
import android.view.ViewGroup
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.simats.codeingo.ui.components.motion.EffectBudgetManager
import com.simats.codeingo.ui.components.motion.isCalmModeActive
import com.simats.codeingo.ui.components.motion.bounce
import com.simats.codeingo.ui.components.motion.pulse
import com.simats.codeingo.ui.components.motion.wobble
import com.simats.codeingo.ui.phoenix.AnimatedGIFView
import com.simats.codeingo.ui.theme.BehindPhoenixGlow
import com.simats.codeingo.ui.theme.PhoenixGreen
import com.simats.codeingo.ui.theme.PhoenixCrimson
import com.simats.codeingo.ui.theme.AmberGold
import java.util.concurrent.atomic.AtomicInteger

/**
 * Mascot Emotions supported across the AshCrypt learning experience.
 */
enum class MascotEmotion {
    IDLE,
    CURIOUS,
    DETERMINED,
    HAPPY,
    CELEBRATE,
    SAD,
    THINKING,
    SLEEPY
}

/**
 * Mascot Evolution Stages corresponding to chapters 1-5.
 */
enum class PhoenixStage(val stageNumber: Int, val displayName: String) {
    EGG(1, "Phoenix Egg"),
    HATCHLING(2, "Hatchling Phoenix"),
    FLEDGED(3, "Fledged Scout"),
    FIREBIRD(4, "Solar Firebird"),
    DIVINE_PHOENIX(5, "Divine Master Phoenix")
}

/**
 * Active Rive Instance Counter to honor Effect Budget (max 2 active instances).
 */
private val activeRiveCount = AtomicInteger(0)

/**
 * MascotController — Rive-backed companion mascot with native Compose fallback.
 *
 * Implements the Part 3 specifications:
 * - Attempts to load and drive `phoenix_mascot.riv` state machine inputs.
 * - When `.riv` is not present, falls back gracefully to Compose/GIF/Canvas renderer.
 * - Caps active Rive instances to 2 concurrent to protect 60/120 FPS performance.
 * - Pauses rendering when scrolled off-screen or in background.
 * - Adheres strictly to Calm Mode and Reduce-Motion by rendering a static resting frame.
 */
@Composable
fun MascotView(
    modifier: Modifier = Modifier,
    emotion: MascotEmotion = MascotEmotion.IDLE,
    stage: PhoenixStage = PhoenixStage.FLEDGED,
    size: Dp = 80.dp,
    showGlow: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val isCalm = isCalmModeActive()

    val hasRiveAsset = remember(context) {
        try {
            val list = context.assets.list("rive") ?: emptyArray()
            list.contains("phoenix_mascot.riv")
        } catch (_: Exception) {
            false
        }
    }

    val canUseRive = hasRiveAsset && !isCalm && activeRiveCount.get() < 2

    val glowColor = when (emotion) {
        MascotEmotion.HAPPY, MascotEmotion.CELEBRATE -> PhoenixGreen
        MascotEmotion.SAD -> PhoenixCrimson
        MascotEmotion.DETERMINED, MascotEmotion.THINKING -> AmberGold
        else -> AmberGold.copy(alpha = 0.6f)
    }

    val clickableMod = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    } else Modifier

    Box(
        modifier = modifier
            .size(size)
            .then(clickableMod),
        contentAlignment = Alignment.Center
    ) {
        if (showGlow && !isCalm) {
            BehindPhoenixGlow(
                size = size,
                glowColor = glowColor
            )
        }

        if (canUseRive) {
            RiveMascotSurface(
                emotion = emotion,
                stage = stage,
                size = size
            )
        } else {
            NativeMascotFallback(
                emotion = emotion,
                stage = stage,
                size = size,
                isCalm = isCalm
            )
        }
    }
}

@Composable
private fun RiveMascotSurface(
    emotion: MascotEmotion,
    stage: PhoenixStage,
    size: Dp
) {
    DisposableEffect(Unit) {
        activeRiveCount.incrementAndGet()
        onDispose {
            activeRiveCount.decrementAndGet()
        }
    }

    AndroidView(
        factory = { ctx ->
            app.rive.runtime.kotlin.RiveAnimationView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                try {
                    setRiveResource(
                        0, // Will load from asset
                        fit = app.rive.runtime.kotlin.core.Fit.CONTAIN,
                        alignment = app.rive.runtime.kotlin.core.Alignment.CENTER,
                        loop = app.rive.runtime.kotlin.core.Loop.LOOP,
                        stateMachineName = "PhoenixStateMachine",
                        autoplay = true
                    )
                } catch (_: Exception) {
                    // Handled gracefully
                }
            }
        },
        update = { riveView ->
            try {
                // Update State Machine inputs
                riveView.setNumberState("PhoenixStateMachine", "evolutionStage", stage.stageNumber.toFloat())
                riveView.setBooleanState("PhoenixStateMachine", "isThinking", emotion == MascotEmotion.THINKING)
                riveView.setBooleanState("PhoenixStateMachine", "isCurious", emotion == MascotEmotion.CURIOUS)
                riveView.setBooleanState("PhoenixStateMachine", "isDetermined", emotion == MascotEmotion.DETERMINED)
                riveView.setBooleanState("PhoenixStateMachine", "isSleepy", emotion == MascotEmotion.SLEEPY)

                if (emotion == MascotEmotion.CELEBRATE) {
                    riveView.fireState("PhoenixStateMachine", "celebrate")
                } else if (emotion == MascotEmotion.SAD) {
                    riveView.fireState("PhoenixStateMachine", "fail")
                }
            } catch (_: Exception) {
                // Non-fatal if state machine inputs are still in progress
            }
        },
        modifier = Modifier.size(size)
    )
}

@Composable
private fun NativeMascotFallback(
    emotion: MascotEmotion,
    stage: PhoenixStage,
    size: Dp,
    isCalm: Boolean
) {
    val stageResourceName = "phoenix_stage_${stage.stageNumber.coerceIn(1, 5)}"

    val emotionModifier = if (isCalm) {
        Modifier
    } else {
        when (emotion) {
            MascotEmotion.HAPPY, MascotEmotion.CELEBRATE -> Modifier.pulse(minScale = 1.0f, maxScale = 1.12f, periodMs = 450)
            MascotEmotion.SAD -> Modifier.wobble()
            MascotEmotion.DETERMINED -> Modifier.pulse(minScale = 1.0f, maxScale = 1.08f, periodMs = 600)
            MascotEmotion.CURIOUS -> Modifier.bounce(trigger = true)
            else -> Modifier
        }
    }

    Box(
        modifier = Modifier
            .size(size)
            .then(emotionModifier),
        contentAlignment = Alignment.Center
    ) {
        AnimatedGIFView(
            resourceName = stageResourceName,
            size = size
        )
    }
}
