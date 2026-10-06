package com.simats.codeingo.ui.phoenix

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.components.CandyCrushStarsView
import com.simats.codeingo.ui.components.DuolingoButton
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DuolingoBlue
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoGreenDark
import com.simats.codeingo.ui.theme.DuolingoOrange
import com.simats.codeingo.ui.theme.DuolingoOrangeDark
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.InputBorder
import com.simats.codeingo.ui.theme.SubtextGray
import kotlinx.coroutines.delay

data class EggParticle(
    val id: Int,
    var x: Float,
    var y: Float,
    val size: Float,
    val color: Color
)

@Composable
fun PhoenixEggHatch3DView(
    unitId: Int = 1,
    levelNumber: Int = 5,
    totalLevelsInUnit: Int = 6,
    isBoss: Boolean = false,
    xpEarned: Int = 100,
    starsEarned: Int = 5,
    accuracyPercentage: Int = 100,
    onContinue: (() -> Unit)? = null,
    onFinish: () -> Unit = {},
    onUpgradePhoenixNextUnit: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var crackStep by remember { mutableIntStateOf(0) }
    var isShattered by remember { mutableStateOf(false) }
    var shockwaveScale by remember { mutableFloatStateOf(0.1f) }
    var shockwaveOpacity by remember { mutableFloatStateOf(0f) }

    val particles = remember {
        mutableStateListOf<EggParticle>().apply {
            repeat(16) { i ->
                add(
                    EggParticle(
                        id = i,
                        x = (Math.random() * 260 - 130).toFloat(),
                        y = (Math.random() * 260 - 130).toFloat(),
                        size = (Math.random() * 8 + 4).toFloat(),
                        color = if (i % 2 == 0) AmberGold else Color(0xFFFF5722)
                    )
                )
            }
        }
    }

    // Cracking Sequence
    // For regular levels: show progressive cracks but do NOT shatter the egg (matches iOS).
    // Only Boss / final levels shatter the egg and reveal the Phoenix.
    val isHatchingFinalLevel = isBoss || levelNumber >= totalLevelsInUnit
    LaunchedEffect(Unit) {
        delay(300)
        crackStep = 1
        delay(500)
        crackStep = 2
        delay(500)
        crackStep = 3
        delay(500)
        crackStep = 4
        if (isHatchingFinalLevel) {
            delay(600)
            isShattered = true
            shockwaveScale = 2.5f
            shockwaveOpacity = 0.9f
        }
    }

    val animatedShockwaveScale by animateFloatAsState(
        targetValue = shockwaveScale,
        animationSpec = tween(900, easing = FastOutSlowInEasing),
        label = "ShockwaveScale"
    )

    val animatedPhoenixAscend by animateFloatAsState(
        targetValue = if (isShattered) 0f else 60f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "PhoenixAscend"
    )

    val animatedPhoenixScale by animateFloatAsState(
        targetValue = if (isShattered) 1f else 0.2f,
        animationSpec = tween(900, easing = FastOutSlowInEasing),
        label = "PhoenixScale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "EggGlow")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowPulse"
    )

    val eggWobble by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "EggWobble"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF2C1438),
                        Color(0xFF14081E),
                        Color.Black
                    ),
                    center = Offset(500f, 600f),
                    radius = 900f
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        // Shockwave Ring
        if (isShattered) {
            Canvas(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(240.dp)
                    .scale(animatedShockwaveScale)
            ) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(AmberGold, DuolingoOrange, Color.Transparent)
                    ),
                    radius = size.minDimension / 2f,
                    style = Stroke(width = 4.dp.toPx())
                )
            }
        }

        // Particle Embers
        if (isShattered) {
            particles.forEach { p ->
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = p.x.dp, y = p.y.dp)
                        .size(p.size.dp)
                        .clip(CircleShape)
                        .background(p.color)
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Banner
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = if (isShattered) "🔥 PHOENIX ASCENDS! 🔥"
                           else if (levelNumber == 1) "🎉 LEVEL 1 COMPLETE! 🎉"
                           else "🎉 LEVEL $levelNumber COMPLETE! 🎉",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isShattered) AmberGold else DuolingoGreen
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isShattered)
                        "The sacred egg has shattered and set the Phoenix free!"
                    else if (levelNumber == 1)
                        "Level 1 Complete! The Phoenix Egg has started cracking!"
                    else
                        "Level $levelNumber Complete! The Phoenix Egg crack is deepening!",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )
            }

            // 3D Egg & Phoenix Stage
            Box(
                modifier = Modifier
                    .size(200.dp, 240.dp)
                    .rotate(if (!isShattered) eggWobble else 0f),
                contentAlignment = Alignment.Center
            ) {
                // Background Flame Glow
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawOval(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                AmberGold.copy(alpha = glowPulse * 0.7f),
                                DuolingoRed.copy(alpha = glowPulse * 0.3f),
                                Color.Transparent
                            ),
                            center = center,
                            radius = size.width * 0.6f
                        ),
                        topLeft = Offset.Zero,
                        size = size
                    )
                }

                if (!isShattered) {
                    // Intact Egg with Progressive Cracks
                    Canvas(modifier = Modifier.size(140.dp, 180.dp)) {
                        val w = size.width
                        val h = size.height

                        val eggPath = Path().apply {
                            moveTo(w * 0.5f, 0f)
                            cubicTo(w * 0.95f, 0f, w, h * 0.6f, w * 0.5f, h)
                            cubicTo(0f, h * 0.6f, w * 0.05f, 0f, w * 0.5f, 0f)
                            close()
                        }

                        // Shell Body
                        drawPath(
                            path = eggPath,
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFFFF9600), Color(0xFFE03A00), Color(0xFF6B0B00))
                            )
                        )

                        // Specular stroke
                        drawPath(
                            path = eggPath,
                            brush = Brush.linearGradient(
                                colors = listOf(Color.White.copy(alpha = 0.6f), Color.Transparent)
                            ),
                            style = Stroke(width = 3.dp.toPx())
                        )

                        // Cracks Layer
                        if (crackStep >= 1) {
                            val c1 = Path().apply {
                                moveTo(w * 0.50f, h * 0.20f)
                                lineTo(w * 0.58f, h * 0.32f)
                                lineTo(w * 0.52f, h * 0.42f)
                                lineTo(w * 0.64f, h * 0.52f)
                            }
                            drawPath(c1, Color.Yellow, style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                        }

                        if (crackStep >= 2) {
                            val c2 = Path().apply {
                                moveTo(w * 0.50f, h * 0.20f)
                                lineTo(w * 0.42f, h * 0.30f)
                                lineTo(w * 0.46f, h * 0.42f)
                                lineTo(w * 0.34f, h * 0.54f)
                            }
                            drawPath(c2, AmberGold, style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                        }

                        if (crackStep >= 3) {
                            val c3 = Path().apply {
                                moveTo(w * 0.20f, h * 0.52f)
                                lineTo(w * 0.46f, h * 0.50f)
                                lineTo(w * 0.64f, h * 0.52f)
                                lineTo(w * 0.85f, h * 0.50f)
                            }
                            drawPath(c3, Color.White, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                        }

                        if (crackStep >= 4) {
                            val c4 = Path().apply {
                                moveTo(w * 0.46f, h * 0.50f)
                                lineTo(w * 0.48f, h * 0.75f)
                                lineTo(w * 0.40f, h * 0.90f)
                            }
                            drawPath(c4, Color.Yellow, style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                        }
                    }
                } else {
                    // Ascending Majestic Phoenix (Boss level only)
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .offset(y = animatedPhoenixAscend.dp)
                            .scale(animatedPhoenixScale),
                        contentAlignment = Alignment.Center
                    ) {
                        // Phoenix aura glow
                        Box(
                            modifier = Modifier
                                .size(180.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            AmberGold.copy(alpha = 0.75f),
                                            DuolingoOrange.copy(alpha = 0.35f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                        // Actual Phoenix image — matches iOS RealisticPhoenixBirdFaceView
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = com.simats.codeingo.R.drawable.phoenix_stage_2),
                            contentDescription = "Phoenix Ascended",
                            modifier = Modifier.size(110.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Animated Candy Crush Stars
            CandyCrushStarsView(earnedStars = starsEarned)

            Spacer(modifier = Modifier.height(10.dp))

            // Stats Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatPill("XP EARNED", "+$xpEarned ⭐", AmberGold, Modifier.weight(1f))
                StatPill("ACCURACY", "$accuracyPercentage%", DuolingoGreen, Modifier.weight(1f))
                StatPill("STAGE", "Lv. $levelNumber 🔥", DuolingoOrange, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(14.dp))

            val isLastLevel = isBoss || levelNumber >= totalLevelsInUnit
            // Bottom Action Button
            DuolingoButton(
                text = if (isLastLevel) "UPGRADE PHOENIX & NEXT UNIT ➔" else (if (onContinue != null || isShattered) "CONTINUE" else "CLAIM REWARDS"),
                faceColor = if (isLastLevel) DuolingoOrange else DuolingoGreen,
                shadowColor = if (isLastLevel) DuolingoOrangeDark else DuolingoGreenDark,
                onClick = {
                    if (isLastLevel && onUpgradePhoenixNextUnit != null) {
                        onUpgradePhoenixNextUnit()
                    } else if (onContinue != null) {
                        onContinue()
                    } else {
                        onFinish()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            )
        }
    }
}

@Composable
private fun StatPill(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(CardBackground)
            .border(1.dp, InputBorder, shape)
            .padding(vertical = 10.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            color = color
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = SubtextGray
        )
    }
}
