package com.simats.codeingo.ui.components.motion

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import kotlinx.coroutines.launch

/**
 * Native 3D FlipCard Composable.
 *
 * Implements physics-based 3D rotation along the Y axis using [graphicsLayer]:
 * - Applies [cameraDistance] to prevent perspective distortion or clipping.
 * - Switches content cleanly between [frontContent] and [backContent] at the 90-degree boundary,
 *   mirroring the back side by 180 degrees so text and icons render right-side up.
 * - Used for: Fact Swipe explanation reveals, Mistake Vault cards, Crypto Codex collectibles, Daily Chests.
 */
enum class CardFace(val angle: Float) {
    FRONT(0f) {
        override val next: CardFace get() = BACK
    },
    BACK(180f) {
        override val next: CardFace get() = FRONT
    };

    abstract val next: CardFace
}

@Composable
fun FlipCard(
    modifier: Modifier = Modifier,
    cardFace: CardFace = CardFace.FRONT,
    onFlip: ((CardFace) -> Unit)? = null,
    enableClickFlip: Boolean = true,
    cameraDistanceDp: Float = 16f,
    frontContent: @Composable () -> Unit,
    backContent: @Composable () -> Unit
) {
    val view = LocalView.current
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    val rotation = remember { Animatable(cardFace.angle) }

    LaunchedEffect(cardFace) {
        if (rotation.targetValue != cardFace.angle) {
            rotation.animateTo(
                targetValue = cardFace.angle,
                animationSpec = spring(
                    dampingRatio = 0.72f,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
    }

    val angle = rotation.value % 360f
    val isFrontFacing = angle <= 90f || angle >= 270f

    Box(
        modifier = modifier
            .graphicsLayer {
                rotationY = rotation.value
                cameraDistance = cameraDistanceDp * density.density
                transformOrigin = TransformOrigin.Center
            }
            .then(
                if (enableClickFlip) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        val nextFace = if (isFrontFacing) CardFace.BACK else CardFace.FRONT
                        onFlip?.invoke(nextFace) ?: run {
                            coroutineScope.launch {
                                rotation.animateTo(
                                    targetValue = nextFace.angle,
                                    animationSpec = spring(
                                        dampingRatio = 0.72f,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                            }
                        }
                    }
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isFrontFacing) {
            Box {
                frontContent()
            }
        } else {
            Box(
                modifier = Modifier.graphicsLayer {
                    // Invert back face so text and icons are not horizontally mirrored
                    rotationY = 180f
                }
            ) {
                backContent()
            }
        }
    }
}

