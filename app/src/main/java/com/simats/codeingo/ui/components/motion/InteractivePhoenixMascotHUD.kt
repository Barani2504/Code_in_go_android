package com.simats.codeingo.ui.components.motion

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.AppFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Super-interactive Phoenix Mascot Companion with Animmaster squash-and-stretch rebound physics.
 * Exceeds Duolingo's static owl by reacting dynamically to user touches with:
 * - Physics rebound bounce & tilt wobble
 * - Funny contextual speech bubbles
 * - Specular Unlumen ambient flame auras
 * - Interactive haptic feedback
 */
@Composable
fun InteractivePhoenixMascotHUD(
    mascotEmoji: String = "🦅",
    mascotName: String = "Phoenix Operative",
    currentStage: Int = 1,
    streakDays: Int = 3,
    onTapReaction: () -> Unit = {}
) {
    val view = LocalView.current
    val coroutineScope = rememberCoroutineScope()
    val bounceY = remember { Animatable(0f) }
    val tiltZ = remember { Animatable(0f) }
    val squash = remember { Animatable(1f) }

    var bubbleIndex by remember { mutableIntStateOf(0) }
    var showBubble by remember { mutableStateOf(false) }

    val funnyQuotes = remember {
        listOf(
            "Ready to crack some RSA keys? 🔑",
            "Keep that $streakDays-day streak ablaze! 🔥",
            "Zero Trust: Never trust, always verify! 🛡️",
            "Tap me again, I dare you! 😄",
            "Ciphers don't stand a chance today! ⚡",
            "Mascot Evolution Stage $currentStage active! 👑"
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Playful Speech Bubble
            AnimatedVisibility(
                visible = showBubble,
                enter = scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.5.dp, AmberGold, RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = funnyQuotes[bubbleIndex % funnyQuotes.size],
                        fontFamily = AppFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
            }

            // 2. Interactive Kinetic Mascot Avatar
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .graphicsLayer {
                        translationY = bounceY.value
                        rotationZ = tiltZ.value
                        scaleX = 2f - squash.value
                        scaleY = squash.value
                    }
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                AmberGold.copy(alpha = 0.35f),
                                Color(0xFFFF4500).copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        )
                    )
                    .unlumenNeonGlow(AmberGold)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                        onTapReaction()
                        bubbleIndex++
                        showBubble = true

                        coroutineScope.launch {
                            // Rebound physics animation
                            squash.animateTo(0.82f, spring(stiffness = Spring.StiffnessHigh))
                            bounceY.animateTo(-24f, spring(stiffness = Spring.StiffnessMediumLow))
                            tiltZ.animateTo(12f, spring(stiffness = Spring.StiffnessHigh))

                            squash.animateTo(1.15f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                            tiltZ.animateTo(-8f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                            bounceY.animateTo(0f, spring(dampingRatio = Spring.DampingRatioHighBouncy))
                            tiltZ.animateTo(0f, spring(stiffness = Spring.StiffnessLow))
                            squash.animateTo(1f, spring(stiffness = Spring.StiffnessLow))
                        }

                        coroutineScope.launch {
                            delay(3500)
                            showBubble = false
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = mascotEmoji,
                    fontSize = 52.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Stage Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF334155))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "STAGE $currentStage • $mascotName",
                    fontFamily = AppFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp,
                    color = AmberGold
                )
            }
        }
    }
}

