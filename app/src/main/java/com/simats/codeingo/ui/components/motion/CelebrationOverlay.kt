

package com.simats.codeingo.ui.components.motion

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.simats.codeingo.ui.components.motion.ConfettiHost
import com.simats.codeingo.ui.components.motion.NumberRollText
import com.simats.codeingo.ui.components.motion.bounce
import com.simats.codeingo.ui.components.motion.pulse
import com.simats.codeingo.ui.components.motion.tada
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.simats.codeingo.ui.components.Duolingo3DButton
import com.simats.codeingo.ui.components.Duolingo3DButtonColor
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.AppFontFamily
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Supercharged Gamified Lesson Completion & Boss Victory Celebration Overlay.
 *
 * Integrates:
 * - Lottie vector confetti / celebration explosion
 * - Canvas particle burst physics
 * - Animmaster kinetic spring-bounced reward cards (+XP, +Gems, Streak)
 * - Tactile Duolingo 3D Button with haptic vibration feedback
 */
@Composable
fun CelebrationOverlay(
    visible: Boolean,
    title: String = "LESSON COMPLETED!",
    subtitle: String = "Cryptography Defense Mastered",
    xpEarned: Int = 15,
    gemsEarned: Int = 5,
    streakDays: Int = 3,
    onContinue: () -> Unit
) {
    if (!visible) return

    val view = LocalView.current
    val scaleAnim = remember { Animatable(0.4f) }
    val xpAnim = remember { Animatable(0f) }
    val gemsAnim = remember { Animatable(0f) }

    LaunchedEffect(visible) {
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        scaleAnim.animateTo(
            1f,
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
        delay(200)
        xpAnim.animateTo(xpEarned.toFloat(), tween(600, easing = FastOutSlowInEasing))
        gemsAnim.animateTo(gemsEarned.toFloat(), tween(600, easing = FastOutSlowInEasing))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.78f)),
        contentAlignment = Alignment.Center
    ) {
        // Ambient Celebration Glow Field
        CelebrationGlowField(
            modifier = Modifier.fillMaxSize(),
            color = AmberGold
        )

        // 1. Procedural Confetti Particle Shower
        ConfettiHost(trigger = true)

        // 2. High-Impact Modal Container
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .graphicsLayer {
                    scaleX = scaleAnim.value
                    scaleY = scaleAnim.value
                }
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1F2937),
                            Color(0xFF111827)
                        )
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(AmberGold, Color(0xFFFF4500), Color(0xFF3B82F6))
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Radiant Mascot Crown / Star Emblem
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(AmberGold.copy(alpha = 0.4f), Color.Transparent)
                        )
                    )
                    .unlumenNeonGlow(AmberGold),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "👑",
                    fontSize = 44.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = title,
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.Black,
                fontSize = 24.sp,
                color = AmberGold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = Color(0xFFD1D5DB),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Dynamic Reward Chips with Rolling Numbers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RewardCard(
                    icon = "⚡",
                    label = "XP EARNED",
                    value = "+${xpEarned}",
                    accentColor = Color(0xFFF59E0B)
                )
                RewardCard(
                    icon = "💎",
                    label = "GEMS",
                    value = "+${gemsEarned}",
                    accentColor = Color(0xFF06B6D4)
                )
                RewardCard(
                    icon = "🔥",
                    label = "STREAK",
                    value = "$streakDays DAYS",
                    accentColor = Color(0xFFEF4444)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 4. Authentic 3D Duolingo Continue Button
            Duolingo3DButton(
                title = "CONTINUE DEFENSE",
                onClick = onContinue,
                style = Duolingo3DButtonColor.GREEN,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun RewardCard(
    icon: String,
    label: String,
    value: String,
    accentColor: Color
) {
    Column(
        modifier = Modifier
            .width(88.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E2638))
            .border(1.2.dp, accentColor.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .shimmerGlossSweep()
            .padding(vertical = 12.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = icon, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontFamily = AppFontFamily,
            fontWeight = FontWeight.Black,
            fontSize = 16.sp,
            color = accentColor
        )
        Text(
            text = label,
            fontFamily = AppFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp,
            color = Color.Gray
        )
    }
}

/**
 * Procedural Canvas Confetti Particle Field:
 * Generates an animated celebration shower without requiring external network dependencies.
 */
@Composable
fun ConfettiParticleField() {
    val particles = remember {
        List(45) {
            ConfettiParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 10f + 6f,
                speed = Random.nextFloat() * 0.008f + 0.004f,
                color = listOf(
                    Color(0xFFFFD700),
                    Color(0xFFFF4500),
                    Color(0xFF00E5FF),
                    Color(0xFF76FF03),
                    Color(0xFFE040FB)
                ).random()
            )
        }
    }

    val anim = rememberInfiniteTransition(label = "confetti").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "confettiFloat"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { p ->
            val currentY = ((p.y + anim.value * (1f + p.speed * 20f)) % 1f) * size.height
            val sinOffset = kotlin.math.sin((anim.value * 12f + p.x * 10f).toDouble()).toFloat() * 20f
            val currentX = (p.x * size.width) + sinOffset

            drawCircle(
                color = p.color,
                radius = p.size,
                center = Offset(currentX, currentY)
            )
        }
    }
}

private data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val color: Color
)

