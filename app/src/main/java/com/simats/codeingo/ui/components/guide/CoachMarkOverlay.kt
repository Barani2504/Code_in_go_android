package com.simats.codeingo.ui.components.guide

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.simats.codeingo.R
import com.simats.codeingo.data.model.phoenixEmotion
import com.simats.codeingo.domain.guide.GuideScript
import com.simats.codeingo.domain.guide.GuideStep
import com.simats.codeingo.domain.guide.PhoenixGuideDirector
import com.simats.codeingo.ui.theme.AshTheme

/**
 * Registry holding target bounds reported dynamically via onGloballyPositioned.
 */
class CoachMarkState {
    private val _targetBounds = mutableStateMapOf<String, Rect>()
    val targetBounds: Map<String, Rect> get() = _targetBounds

    var activeScript by mutableStateOf<GuideScript?>(null)
        private set

    var currentStepIndex by mutableIntStateOf(0)
        private set

    val currentStep: GuideStep?
        get() = activeScript?.steps?.getOrNull(currentStepIndex)

    fun registerTarget(tag: String, rect: Rect) {
        _targetBounds[tag] = rect
    }

    fun unregisterTarget(tag: String) {
        _targetBounds.remove(tag)
    }

    fun startTour(script: GuideScript) {
        activeScript = script
        currentStepIndex = 0
    }

    fun nextStep(onTourFinished: (() -> Unit)? = null) {
        val script = activeScript ?: return
        if (currentStepIndex + 1 < script.steps.size) {
            currentStepIndex++
        } else {
            endTour()
            onTourFinished?.invoke()
        }
    }

    fun endTour() {
        activeScript = null
        currentStepIndex = 0
    }
}

val LocalCoachMarkState = compositionLocalOf { CoachMarkState() }

/**
 * Modifier to dynamically register element bounds into the coach mark registry.
 * Accurately tracks screen coordinates even across different screen densities and font scales.
 */
fun Modifier.coachMarkTarget(tag: String, state: CoachMarkState): Modifier = this.then(
    Modifier.onGloballyPositioned { coordinates ->
        if (coordinates.isAttached) {
            val bounds = coordinates.boundsInRoot()
            state.registerTarget(tag, bounds)
        }
    }
)

/**
 * Fullscreen CoachMark overlay that dims background, cuts a rounded spotlight
 * around real UI elements, positions Ash next to it, and provides a clear speech bubble.
 */
@Composable
fun CoachMarkOverlay(
    state: CoachMarkState,
    userId: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val script = state.activeScript
    val step = state.currentStep

    AnimatedVisibility(
        visible = script != null && step != null,
        enter = fadeIn(tween(250)),
        exit = fadeOut(tween(200)),
        modifier = modifier.zIndex(9999f)
    ) {
        if (script == null || step == null) return@AnimatedVisibility

        val density = LocalDensity.current
        val targetRect = step.targetTag?.let { state.targetBounds[it] }

        // Pulse animation for spotlight ring
        val infiniteTransition = rememberInfiniteTransition(label = "coachMarkPulse")
        val pulseRing by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "pulseRing"
        )

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    // Tap overlay advances step
                    state.nextStep {
                        PhoenixGuideDirector.markTourSeen(context, script.id, userId)
                    }
                }
        ) {
            val screenWidth = maxWidth
            val screenHeight = maxHeight
            val screenHeightPx = with(density) { screenHeight.toPx() }
            val brandColor = AshTheme.colors.brand

            // 1. Dimmed Background with Cutout Spotlight
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = 0.99f } // Forces offscreen layer for BlendMode.Clear
            ) {
                // Dimmed whole screen
                drawRect(color = Color.Black.copy(alpha = 0.75f))

                if (targetRect != null && targetRect.width > 0 && targetRect.height > 0) {
                    val pad = 12.dp.toPx()
                    val cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
                    val spotlightRect = Rect(
                        left = targetRect.left - pad,
                        top = targetRect.top - pad,
                        right = targetRect.right + pad,
                        bottom = targetRect.bottom + pad
                    )

                    // Cut out transparent spotlight
                    drawRoundRect(
                        color = Color.Transparent,
                        topLeft = spotlightRect.topLeft,
                        size = spotlightRect.size,
                        cornerRadius = cornerRadius,
                        blendMode = BlendMode.Clear
                    )

                    // Draw glowing border around spotlight
                    drawRoundRect(
                        color = brandColor.copy(alpha = 0.8f),
                        topLeft = spotlightRect.topLeft,
                        size = spotlightRect.size,
                        cornerRadius = cornerRadius,
                        style = Stroke(width = 3.dp.toPx())
                    )

                    // Pulsing expansion ring
                    val pulseExpansion = pulseRing * 10.dp.toPx()
                    drawRoundRect(
                        color = brandColor.copy(alpha = (1f - pulseRing) * 0.5f),
                        topLeft = Offset(spotlightRect.left - pulseExpansion, spotlightRect.top - pulseExpansion),
                        size = Size(spotlightRect.width + pulseExpansion * 2, spotlightRect.height + pulseExpansion * 2),
                        cornerRadius = CornerRadius(cornerRadius.x + pulseExpansion, cornerRadius.y + pulseExpansion),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }

            // 2. Position Ash Mascot & Speech Bubble
            val isTargetInBottomHalf = if (targetRect != null) {
                targetRect.center.y > screenHeightPx * 0.45f
            } else {
                false
            }

            val contentAlignment = if (targetRect == null) {
                Alignment.Center
            } else if (isTargetInBottomHalf) {
                Alignment.TopCenter
            } else {
                Alignment.BottomCenter
            }

            // Calculate vertical padding so bubble is placed right above or below the target
            val targetOffsetDp = if (targetRect != null) {
                with(density) {
                    if (isTargetInBottomHalf) {
                        val spaceAbove = targetRect.top.toDp()
                        Modifier.padding(top = (spaceAbove - 320.dp).coerceAtLeast(32.dp))
                    } else {
                        val spaceBelow = targetRect.bottom.toDp()
                        Modifier.padding(top = (spaceBelow + 16.dp).coerceAtMost(screenHeight - 340.dp))
                    }
                }
            } else {
                Modifier.padding(vertical = 40.dp)
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(targetOffsetDp),
                contentAlignment = contentAlignment
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Ash Mascot Display
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val emotion = phoenixEmotion(step.pose.emotionId)
                        val context = LocalContext.current
                        val drawableId = remember(emotion.id) {
                            val resId = context.resources.getIdentifier(
                                emotion.drawableResName,
                                "drawable",
                                context.packageName
                            )
                            if (resId != 0) resId else R.drawable.phoenix
                        }

                        Image(
                            painter = painterResource(id = drawableId),
                            contentDescription = emotion.title,
                            modifier = Modifier
                                .size(76.dp)
                                .scale(1.05f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    SpeechBubble(
                        text = step.line,
                        actionLabel = step.actionLabel,
                        onActionClick = {
                            state.nextStep {
                                PhoenixGuideDirector.markTourSeen(context, script.id, userId)
                            }
                        },
                        secondaryLabel = if (step.canSkip) "Skip Tour" else null,
                        onSecondaryClick = if (step.canSkip) {
                            {
                                state.endTour()
                                PhoenixGuideDirector.markTourSeen(context, script.id, userId)
                            }
                        } else null,
                        tail = if (isTargetInBottomHalf) SpeechBubbleTail.BOTTOM_LEFT else SpeechBubbleTail.TOP_LEFT
                    )
                }
            }
        }
    }
}
