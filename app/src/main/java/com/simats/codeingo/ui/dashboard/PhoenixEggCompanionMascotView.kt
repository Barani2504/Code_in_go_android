package com.simats.codeingo.ui.dashboard

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DuolingoOrange
import com.simats.codeingo.ui.theme.DuolingoRed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

@Composable
fun PhoenixEggCompanionMascotView(
    levelNumber: Int = 1,
    isBoss: Boolean = false,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val currentLevel = remember(levelNumber, isBoss) {
        if (isBoss) 5
        else {
            val offset = ((levelNumber - 1) % 6) + 1
            offset.coerceIn(1, 5)
        }
    }

    var tapShake by remember { mutableFloatStateOf(0f) }
    var isFullyHatched by remember { mutableStateOf(false) }

    val statusPhrase = when {
        isFullyHatched -> "PHOENIX UNLOCKED! 🦅🌟"
        currentLevel == 1 -> "L1 • Hairline Crack 🐣"
        currentLevel == 2 -> "L2 • Deepening Crack 🔥"
        currentLevel == 3 -> "L3 • Fissuring Shell ⚡"
        currentLevel == 4 -> "L4 • Almost Hatching! 🪺"
        currentLevel == 5 -> "L5 • Phoenix Peeking! 🦅🔥"
        else -> "Phoenix Ready! 🦅"
    }

    val infiniteTransition = rememberInfiniteTransition(label = "EggCompanion")

    val idleWobble by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "IdleWobble"
    )

    val idleBreath by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "IdleBreath"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowAlpha"
    )

    val wingFlap by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "WingFlap"
    )

    val headBob by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HeadBob"
    )

    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                coroutineScope.launch {
                    tapShake = 8f
                    delay(70)
                    tapShake = -8f
                    delay(70)
                    tapShake = 4f
                    delay(70)
                    tapShake = 0f
                    if (currentLevel == 5) {
                        isFullyHatched = true
                    }
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Floating Speech Bubble
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF141C26))
                .border(
                    width = 1.5.dp,
                    color = if (currentLevel == 5 || isFullyHatched) AmberGold else DuolingoOrange,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 9.dp, vertical = 4.dp)
        ) {
            Text(
                text = statusPhrase,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = if (currentLevel == 5 || isFullyHatched) AmberGold else Color.White
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 3D Egg Mascot Box
        Box(
            modifier = Modifier
                .size(70.dp, 84.dp)
                .scale(idleBreath)
                .rotate(idleWobble + tapShake),
            contentAlignment = Alignment.Center
        ) {
            // Radial Glow
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            (if (currentLevel == 5) AmberGold else DuolingoOrange).copy(alpha = glowAlpha * 0.7f),
                            DuolingoRed.copy(alpha = glowAlpha * 0.25f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = size.width * 0.55f
                    ),
                    topLeft = Offset.Zero,
                    size = size
                )
            }

            // Ground Shadow
            Box(
                modifier = Modifier
                    .size(42.dp, 10.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = 2.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.45f))
            )

            if (currentLevel < 5 && !isFullyHatched) {
                // LEVELS 1 - 4: Intact Egg with Progressively Increasing Glowing Cracks
                Canvas(modifier = Modifier.size(54.dp, 70.dp)) {
                    val w = size.width
                    val h = size.height

                    val eggPath = Path().apply {
                        moveTo(w * 0.5f, 0f)
                        cubicTo(w * 0.95f, 0f, w, h * 0.6f, w * 0.5f, h)
                        cubicTo(0f, h * 0.6f, w * 0.05f, 0f, w * 0.5f, 0f)
                        close()
                    }

                    // Shell Body with 3D gradient
                    drawPath(
                        path = eggPath,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFFD56B),
                                Color(0xFFFF9600),
                                Color(0xFFC83200)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(w, h)
                        )
                    )

                    // Specular rim highlight
                    drawPath(
                        path = eggPath,
                        brush = Brush.linearGradient(
                            colors = listOf(LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.7f), AmberGold.copy(alpha = 0.3f), Color.Transparent),
                            start = Offset(0f, 0f),
                            end = Offset(w, h)
                        ),
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // Gloss highlight
                    drawOval(
                        color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.45f),
                        topLeft = Offset(w * 0.22f, h * 0.14f),
                        size = Size(w * 0.20f, h * 0.26f)
                    )

                    // Stage 1: Hairline Crack
                    if (currentLevel >= 1) {
                        val c1 = Path().apply {
                            moveTo(w * 0.55f, h * 0.20f)
                            lineTo(w * 0.68f, h * 0.30f)
                            lineTo(w * 0.60f, h * 0.40f)
                            lineTo(w * 0.76f, h * 0.50f)
                        }
                        drawPath(c1, Color.Yellow, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                    }

                    // Stage 2: Secondary Branching Cracks
                    if (currentLevel >= 2) {
                        val c2 = Path().apply {
                            moveTo(w * 0.68f, h * 0.30f)
                            lineTo(w * 0.82f, h * 0.28f)
                            moveTo(w * 0.60f, h * 0.40f)
                            lineTo(w * 0.50f, h * 0.48f)
                            lineTo(w * 0.64f, h * 0.60f)
                        }
                        drawPath(c2, AmberGold, style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                    }

                    // Stage 3: Deep Magma Fissures
                    if (currentLevel >= 3) {
                        val c3 = Path().apply {
                            moveTo(w * 0.32f, h * 0.34f)
                            lineTo(w * 0.44f, h * 0.42f)
                            lineTo(w * 0.36f, h * 0.54f)
                            lineTo(w * 0.48f, h * 0.68f)
                        }
                        drawPath(c3, Color.White, style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                    }

                    // Stage 4: Light Beams Escaping
                    if (currentLevel >= 4) {
                        val deepCrack = Path().apply {
                            moveTo(w * 0.18f, h * 0.48f)
                            lineTo(w * 0.40f, h * 0.44f)
                            lineTo(w * 0.58f, h * 0.50f)
                            lineTo(w * 0.82f, h * 0.46f)
                        }
                        drawPath(deepCrack, Color.Yellow, style = Stroke(width = 2.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                        drawCircle(Color.Yellow.copy(alpha = 0.6f), radius = 6.dp.toPx(), center = Offset(w * 0.58f, h * 0.50f))
                    }
                }
            } else {
                // LEVEL 5: Realistic Broken Egg with Peeking Phoenix Bird ("Little Shown")
                Box(
                    modifier = Modifier.size(56.dp, 72.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Broken Lower Shell
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val bottomEgg = Path().apply {
                            moveTo(0f, h * 0.44f)
                            lineTo(w * 0.16f, h * 0.36f)
                            lineTo(w * 0.28f, h * 0.46f)
                            lineTo(w * 0.44f, h * 0.34f)
                            lineTo(w * 0.58f, h * 0.48f)
                            lineTo(w * 0.74f, h * 0.36f)
                            lineTo(w * 0.88f, h * 0.45f)
                            lineTo(w, h * 0.40f)
                            cubicTo(w, h * 0.78f, w * 0.80f, h, w * 0.5f, h)
                            cubicTo(w * 0.20f, h, 0f, h * 0.78f, 0f, h * 0.44f)
                            close()
                        }
                        drawPath(
                            path = bottomEgg,
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFFFFB732), Color(0xFFFF6600), Color(0xFFB01800)),
                                start = Offset(0f, h * 0.35f),
                                end = Offset(w, h)
                            )
                        )
                        drawPath(
                            path = bottomEgg,
                            brush = Brush.linearGradient(listOf(Color.Yellow, DuolingoOrange, DuolingoRed)),
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                    }

                    // Peeking Baby Phoenix Bird (Head, Eyes, Beak, Wingtips)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .offset(y = (headBob - 8).dp)
                    ) {
                        // 3 Feathery Crest Plumes
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(3.dp, 10.dp)
                                    .rotate(-20f)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(AmberGold)
                            )
                            Box(
                                modifier = Modifier
                                    .size(4.dp, 13.dp)
                                    .offset(y = (-2).dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color.Yellow)
                            )
                            Box(
                                modifier = Modifier
                                    .size(3.dp, 10.dp)
                                    .rotate(20f)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(AmberGold)
                            )
                        }

                        // Cute Baby Phoenix Head
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color(0xFFFFDE59), Color(0xFFFF914D), Color(0xFFFF5757))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            // Eyes
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(4.dp, 5.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black),
                                    contentAlignment = Alignment.TopStart
                                ) {
                                    Box(modifier = Modifier.size(1.5.dp).clip(CircleShape).background(Color.White))
                                }
                                Box(
                                    modifier = Modifier
                                        .size(4.dp, 5.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black),
                                    contentAlignment = Alignment.TopStart
                                ) {
                                    Box(modifier = Modifier.size(1.5.dp).clip(CircleShape).background(Color.White))
                                }
                            }

                            // Golden Beak
                            Box(
                                modifier = Modifier
                                    .size(5.dp, 4.dp)
                                    .offset(y = 4.dp)
                                    .clip(RoundedCornerShape(1.dp))
                                    .background(Color.Yellow)
                            )
                        }

                        // Fluttering Wingtips peeking over the rim
                        Row(
                            modifier = Modifier.width(44.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp, 8.dp)
                                    .rotate(-28f + wingFlap)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(DuolingoOrange)
                            )
                            Box(
                                modifier = Modifier
                                    .size(10.dp, 8.dp)
                                    .rotate(28f - wingFlap)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(DuolingoOrange)
                            )
                        }
                    }

                    // Tilted Broken Top Shell Cap
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .offset(x = (-6).dp, y = (-12).dp)
                            .rotate(-22f)
                    ) {
                        val w = size.width
                        val h = size.height
                        val topEgg = Path().apply {
                            moveTo(w * 0.5f, 0f)
                            cubicTo(w * 0.85f, 0f, w, h * 0.24f, w, h * 0.40f)
                            lineTo(w * 0.88f, h * 0.45f)
                            lineTo(w * 0.74f, h * 0.36f)
                            lineTo(w * 0.58f, h * 0.48f)
                            lineTo(w * 0.44f, h * 0.34f)
                            lineTo(w * 0.28f, h * 0.46f)
                            lineTo(w * 0.16f, h * 0.36f)
                            lineTo(0f, h * 0.44f)
                            cubicTo(0f, h * 0.24f, w * 0.15f, 0f, w * 0.5f, 0f)
                            close()
                        }
                        drawPath(
                            path = topEgg,
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFFFFD56B), Color(0xFFFF9600), Color(0xFFC83200)),
                                start = Offset(0f, 0f),
                                end = Offset(w, h * 0.45f)
                            )
                        )
                        drawPath(
                            path = topEgg,
                            color = Color.Yellow.copy(alpha = 0.7f),
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                    }
                }
            }
        }
    }
}
