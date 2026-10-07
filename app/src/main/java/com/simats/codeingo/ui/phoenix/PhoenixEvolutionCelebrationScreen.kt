package com.simats.codeingo.ui.phoenix

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.components.Duolingo3DButton
import com.simats.codeingo.ui.components.Duolingo3DButtonStyle
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.SubtextGray
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

data class EvolutionParticle(
    val id: Int,
    val x: Float,
    val y: Float,
    val size: Float,
    val color: Color
)

@Composable
fun PhoenixEvolutionCelebrationScreen(
    fromStageId: Int,
    toStageId: Int,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fromStage = allPhoenixStages.firstOrNull { it.id == fromStageId } ?: allPhoenixStages.first()
    val toStage = allPhoenixStages.firstOrNull { it.id == toStageId } ?: allPhoenixStages.getOrElse(toStageId - 1) { allPhoenixStages.last() }

    var animPhase by remember { mutableStateOf(0) }
    val mascotScale = remember { Animatable(0.6f) }
    val shakeOffset = remember { Animatable(0f) }
    val shockwaveRadius = remember { Animatable(0f) }
    val shockwaveOpacity = remember { Animatable(1f) }
    val particleOpacity = remember { Animatable(1f) }
    val particles = remember { mutableStateListOf<EvolutionParticle>() }

    // Pulsing idle aura for the hero mascot
    val infiniteTransition = rememberInfiniteTransition(label = "AuraPulse")
    val auraScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "AuraScale"
    )

    LaunchedEffect(Unit) {
        // Step 1: Squash and shake
        mascotScale.animateTo(0.75f, animationSpec = tween(300, easing = LinearEasing))
        shakeOffset.animateTo(-8f, animationSpec = tween(100))
        shakeOffset.animateTo(8f, animationSpec = tween(100))
        shakeOffset.animateTo(-6f, animationSpec = tween(80))
        shakeOffset.animateTo(0f, animationSpec = tween(80))

        // Step 2: Burst, Stretch & Shockwave
        animPhase = 1
        val particleColors = listOf(Color(0xFFFFD700), Color(0xFFFA8000), Color(0xFFFF4500), Color.White, toStage.auraColor)
        val newParticles = (0 until 40).map { i ->
            val angle = i * (2.0 * Math.PI / 40.0)
            val speed = Random.nextDouble(90.0, 220.0)
            EvolutionParticle(
                id = i,
                x = (cos(angle) * speed).toFloat(),
                y = (sin(angle) * speed).toFloat(),
                size = Random.nextDouble(4.0, 10.0).toFloat(),
                color = particleColors.random()
            )
        }
        particles.clear()
        particles.addAll(newParticles)

        // Launch concurrent animations
        coroutineScope {
            launch {
                mascotScale.animateTo(1.18f, animationSpec = spring(dampingRatio = 0.55f, stiffness = 400f))
                mascotScale.animateTo(1.0f, animationSpec = spring(dampingRatio = 0.7f, stiffness = 500f))
            }
            launch {
                shockwaveRadius.animateTo(380f, animationSpec = tween(800, easing = FastOutSlowInEasing))
            }
            launch {
                shockwaveOpacity.animateTo(0f, animationSpec = tween(800, easing = LinearEasing))
            }
            launch {
                delay(100)
                particleOpacity.animateTo(0f, animationSpec = tween(1000, easing = LinearEasing))
            }
        }

        delay(500)
        animPhase = 2
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF3B2A5C),
                        Color(0xFF1B1B3A),
                        Color(0xFF0C0C1E)
                    ),
                    radius = 900f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Shockwave Expanding Ring
        if (shockwaveRadius.value > 0f) {
            Box(
                modifier = Modifier
                    .size(shockwaveRadius.value.dp)
                    .border(5.dp, toStage.auraColor.copy(alpha = shockwaveOpacity.value), CircleShape)
            )
        }

        // Particle Burst
        particles.forEach { p ->
            Box(
                modifier = Modifier
                    .offset { IntOffset(p.x.roundToInt(), p.y.roundToInt()) }
                    .size(p.size.dp)
                    .clip(CircleShape)
                    .background(p.color.copy(alpha = particleOpacity.value))
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Banner
            Box(
                modifier = Modifier
                    .scale(if (animPhase >= 1) 1.0f else 0.5f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .border(1.dp, AmberGold.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "🔥 PHOENIX EVOLUTION! 🔥",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = AmberGold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Hero Mascot Creature with Pulsing Aura
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .offset { IntOffset(shakeOffset.value.roundToInt(), 0) },
                contentAlignment = Alignment.Center
            ) {
                // Background Radial Glow
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .scale(auraScale)
                        .blur(30.dp)
                        .clip(CircleShape)
                        .background(toStage.auraColor.copy(alpha = 0.45f))
                )

                // Stage Avatar container
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .scale(mascotScale.value)
                        .clip(RoundedCornerShape(36.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF1D2640),
                                    Color(0xFF0F1424)
                                )
                            )
                        )
                        .border(
                            width = 2.5.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(toStage.auraColor, toStage.auraColor.copy(alpha = 0.3f))
                            ),
                            shape = RoundedCornerShape(36.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = getPhoenixDrawableId(toStage.id)),
                        contentDescription = toStage.name,
                        modifier = Modifier.size(130.dp)
                    )
                }
            }

            // Stage Info & Level Up Text Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(if (animPhase >= 2) 1.0f else 0.8f)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(1.dp, LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.12f), RoundedCornerShape(22.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(toStage.auraColor.copy(alpha = 0.2f))
                        .border(1.dp, toStage.auraColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "STAGE ${toStage.id} REACHED",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = toStage.auraColor,
                        letterSpacing = 0.8.sp
                    )
                }

                Text(
                    text = toStage.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = LocalDynamicThemeColors.current.textPrimary,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = toStage.subtitle,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = SubtextGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Text(
                    text = "“${toStage.quote}”",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AmberGold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Ascend With Companion 3D Button
            Duolingo3DButton(
                title = "✨ ASCEND WITH COMPANION",
                style = Duolingo3DButtonStyle.GREEN,
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
