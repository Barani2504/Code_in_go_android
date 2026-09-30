package com.example.codelearn.phoenix

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/* ------------------------------------------------------------------ */
/*  DATA                                                               */
/* ------------------------------------------------------------------ */

const val MAX_STAGE = 18

val stageNames = listOf(
    "Mystic Egg", "Hairline Crack", "Shaking Egg", "Hatching!",
    "Ember Chick", "Fluffy Flameling", "Spark Fledgling", "Ash Wing",
    "Cinder Glider", "Ember Flyer", "Flame Sprite", "Blaze Wing",
    "Inferno Youngling", "Sunfire Hawk", "Radiant Firebird", "Solar Phoenix",
    "Eternal Ember", "PHOENIX ASCENDANT"
)

private val Yellow = Color(0xFFFFE082)
private val Orange = Color(0xFFFF7043)
private val Crimson = Color(0xFFD50000)
private val Gold = Color(0xFFFFC107)

private fun bodyColor(p: Float) =
    if (p < 0.5f) lerp(Yellow, Orange, p * 2f) else lerp(Orange, Crimson, (p - 0.5f) * 2f)

private fun flameColor(p: Float) = lerp(Orange, Gold, p)

private data class Particle(val angle: Float, val speed: Float, val size: Float, val color: Color)

/* ------------------------------------------------------------------ */
/*  THE CREATURE  (pass a Float stage 1f..18f; fractional = morphing)  */
/* ------------------------------------------------------------------ */

@Composable
fun PhoenixCreature(stage: Float, modifier: Modifier = Modifier) {
    val inf = rememberInfiniteTransition(label = "idle")
    val bob by inf.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "bob"
    )
    val flap by inf.animateFloat(
        -1f, 1f,
        infiniteRepeatable(tween(650, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "flap"
    )
    val flicker by inf.animateFloat(
        0.85f, 1.15f,
        infiniteRepeatable(tween(420, easing = LinearEasing), RepeatMode.Reverse), label = "flicker"
    )

    Canvas(modifier) {
        val w = size.width
        val cx = w / 2f
        val cy = size.height / 2f + (bob - 0.5f) * size.height * 0.05f
        val center = Offset(cx, cy)
        val p = ((stage - 1f) / (MAX_STAGE - 1f)).coerceIn(0f, 1f)
        val eggScale = 1f - (stage - 4f).coerceIn(0f, 1f)
        val birdScale = (stage - 4f).coerceIn(0f, 1f)

        // Aura grows from stage 10 on
        val aura = ((stage - 9f) / 9f).coerceIn(0f, 1f)
        if (aura > 0f) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(flameColor(p).copy(alpha = 0.55f * aura * flicker), Color.Transparent),
                    center = center, radius = w * (0.35f + 0.2f * aura) * flicker
                ),
                radius = w * 0.55f, center = center
            )
        }

        if (eggScale > 0.01f) scale(eggScale, center) { drawEgg(stage, center, w, flap) }
        if (birdScale > 0.01f) scale(birdScale, center) { drawBird(stage, p, center, w, flap, flicker) }
    }
}

private fun DrawScope.drawEgg(stage: Float, c: Offset, w: Float, flap: Float) {
    val wobble = if (stage < 4f) flap * (stage - 1f) * 3f else 0f
    rotate(wobble, c) {
        val ew = w * 0.36f
        val eh = w * 0.46f
        val tl = Offset(c.x - ew / 2, c.y - eh / 2)
        drawOval(
            brush = Brush.radialGradient(
                listOf(Color(0xFFFFF3E0), Color(0xFFFFB74D), Color(0xFFE65100)),
                center = Offset(c.x - ew * 0.15f, c.y - eh * 0.2f), radius = eh * 0.7f
            ),
            topLeft = tl, size = Size(ew, eh)
        )
        // spots
        drawCircle(Color(0x55FFFFFF), ew * 0.08f, Offset(c.x - ew * 0.15f, c.y - eh * 0.12f))
        drawCircle(Color(0x33BF360C), ew * 0.06f, Offset(c.x + ew * 0.18f, c.y + eh * 0.1f))
        // cracks grow with stage
        val cracks = (stage.toInt() - 1).coerceIn(0, 3)
        repeat(cracks) { i ->
            val path = Path().apply {
                val sx = c.x - ew * 0.25f + i * ew * 0.25f
                moveTo(sx, c.y - eh * 0.5f)
                lineTo(sx + ew * 0.06f, c.y - eh * 0.32f)
                lineTo(sx - ew * 0.05f, c.y - eh * 0.2f)
                lineTo(sx + ew * 0.05f, c.y - eh * 0.05f)
            }
            drawPath(path, Color(0xFF4E342E), style = Stroke(w * 0.008f + i, cap = StrokeCap.Round))
            // inner glow
            drawPath(path, Color(0xFFFFEB3B).copy(alpha = 0.6f), style = Stroke(w * 0.004f, cap = StrokeCap.Round))
        }
    }
}

private fun DrawScope.drawBird(
    stage: Float, p: Float, c: Offset, w: Float, flap: Float, flicker: Float
) {
    val s = 0.45f + 0.55f * p
    val r = w * 0.17f * s
    val body = bodyColor(p)
    val accent = flameColor(p)
    val bodyC = Offset(c.x, c.y + r * 0.3f)
    val headR = r * 0.62f
    val headC = Offset(c.x, bodyC.y - r * 1.15f)
    val st = stage.toInt()

    // Tail feathers (behind)
    val tails = (3 + st / 3).coerceAtMost(9)
    val tailLen = r * (0.9f + 1.7f * p)
    repeat(tails) { i ->
        val ang = (i - (tails - 1) / 2f) * 15f + flap * 3f
        rotate(ang, bodyC) {
            drawOval(
                brush = Brush.verticalGradient(
                    listOf(body, accent, Color.Transparent),
                    startY = bodyC.y, endY = bodyC.y + tailLen
                ),
                topLeft = Offset(bodyC.x - r * 0.13f, bodyC.y + r * 0.3f),
                size = Size(r * 0.26f, tailLen * (0.8f + 0.1f * (i % 3)) * flicker.coerceAtMost(1.05f))
            )
        }
    }

    // Wings
    val layers = (1 + st / 6).coerceAtMost(3)
    val wingLen = r * (0.8f + 1.9f * p)
    for (side in listOf(-1f, 1f)) {
        val root = Offset(bodyC.x + side * r * 0.7f, bodyC.y - r * 0.2f)
        for (l in 0 until layers) {
            rotate(side * (flap * 18f - 10f - l * 14f), root) {
                val len = wingLen * (1f - l * 0.18f)
                val path = Path().apply {
                    moveTo(root.x, root.y)
                    cubicTo(
                        root.x + side * len * 0.5f, root.y - len * 0.9f,
                        root.x + side * len * 0.9f, root.y - len * 0.6f,
                        root.x + side * len, root.y - len * 0.1f
                    )
                    cubicTo(
                        root.x + side * len * 0.7f, root.y + len * 0.05f,
                        root.x + side * len * 0.4f, root.y + len * 0.3f,
                        root.x, root.y + len * 0.25f
                    )
                    close()
                }
                drawPath(path, lerp(body, accent, l / 3f).copy(alpha = 0.95f))
            }
        }
    }

    // Body
    drawCircle(
        brush = Brush.radialGradient(
            listOf(lerp(body, Color.White, 0.35f), body, lerp(body, Color.Black, 0.25f)),
            center = Offset(bodyC.x - r * 0.3f, bodyC.y - r * 0.3f), radius = r * 1.4f
        ),
        radius = r, center = bodyC
    )
    // belly
    drawOval(
        Yellow.copy(alpha = 0.8f),
        topLeft = Offset(bodyC.x - r * 0.5f, bodyC.y - r * 0.1f), size = Size(r, r * 0.9f)
    )

    // Crest flames on head
    val crest = (1 + st / 4).coerceAtMost(5)
    repeat(crest) { i ->
        val off = (i - (crest - 1) / 2f) * headR * 0.45f
        val hgt = headR * (0.9f + 0.9f * p) * (1f - kotlin.math.abs(i - (crest - 1) / 2f) * 0.15f) * flicker
        drawFlame(
            Offset(headC.x + off, headC.y - headR * 0.75f), hgt, headR * 0.28f,
            lerp(accent, Yellow, (i % 2) * 0.5f)
        )
    }

    // Head
    drawCircle(
        Brush.radialGradient(
            listOf(lerp(body, Color.White, 0.3f), body),
            center = Offset(headC.x - headR * 0.3f, headC.y - headR * 0.3f), radius = headR * 1.5f
        ),
        headR, headC
    )
    // Eyes
    for (side in listOf(-1f, 1f)) {
        val e = Offset(headC.x + side * headR * 0.4f, headC.y - headR * 0.05f)
        drawCircle(Color.White, headR * 0.24f, e)
        drawCircle(Color(0xFF212121), headR * 0.13f, e.copy(x = e.x + side * headR * 0.03f))
        drawCircle(Color.White, headR * 0.05f, Offset(e.x - headR * 0.03f, e.y - headR * 0.05f))
    }
    // Beak
    val beak = Path().apply {
        moveTo(headC.x - headR * 0.2f, headC.y + headR * 0.25f)
        lineTo(headC.x + headR * 0.2f, headC.y + headR * 0.25f)
        lineTo(headC.x, headC.y + headR * 0.65f)
        close()
    }
    drawPath(beak, Color(0xFFFFA000))

    // Final form: golden halo ring
    if (st >= 18) {
        drawCircle(
            Gold.copy(alpha = 0.8f), radius = headR * 1.25f, center = headC,
            style = Stroke(w * 0.008f)
        )
    }
}

private fun DrawScope.drawFlame(base: Offset, hgt: Float, wid: Float, color: Color) {
    val path = Path().apply {
        moveTo(base.x, base.y)
        cubicTo(base.x - wid * 1.6f, base.y - hgt * 0.3f, base.x - wid * 0.4f, base.y - hgt * 0.7f, base.x, base.y - hgt)
        cubicTo(base.x + wid * 0.4f, base.y - hgt * 0.7f, base.x + wid * 1.6f, base.y - hgt * 0.3f, base.x, base.y)
        close()
    }
    drawPath(path, color)
}

/* ------------------------------------------------------------------ */
/*  LEVEL-UP SCREEN  (squash & stretch, shake, particles, ring, text)  */
/* ------------------------------------------------------------------ */

@Composable
fun PhoenixEvolutionScreen() {
    var level by remember { mutableIntStateOf(1) }
    val stageAnim = remember { Animatable(1f) }
    val burst = remember { Animatable(0f) }
    val squash = remember { Animatable(1f) }
    val shake = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    val palette = listOf(Gold, Orange, Crimson, Yellow, Color(0xFFFFFFFF), Color(0xFF29B6F6), Color(0xFF66BB6A))
    val particles = remember {
        List(60) {
            Particle(
                Random.nextFloat() * 2f * PI.toFloat(),
                0.12f + Random.nextFloat() * 0.5f,
                5f + Random.nextFloat() * 11f,
                palette.random()
            )
        }
    }

    fun levelUp() {
        if (level >= MAX_STAGE) return
        level++
        val target = level
        // 1) anticipation squash -> bouncy release (Candy Crush jelly feel)
        scope.launch {
            squash.snapTo(0.7f)
            squash.animateTo(1f, spring(Spring.DampingRatioHighBouncy, Spring.StiffnessLow))
        }
        // 2) evolve morph, slightly delayed so the squash reads first
        scope.launch {
            delay(250)
            stageAnim.animateTo(target.toFloat(), tween(900, easing = FastOutSlowInEasing))
        }
        // 3) particle burst + ring
        scope.launch {
            delay(250)
            burst.snapTo(0f)
            burst.animateTo(1f, tween(if (target == MAX_STAGE) 1800 else 1100, easing = LinearEasing))
            burst.snapTo(0f)
        }
        // 4) screen shake
        scope.launch {
            shake.snapTo(1f)
            shake.animateTo(0f, spring(Spring.DampingRatioHighBouncy, Spring.StiffnessMedium))
        }
    }

    val xp by animateFloatAsState(
        (level - 1f) / (MAX_STAGE - 1f), tween(700, easing = FastOutSlowInEasing), label = "xp"
    )

    Column(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF1B1B3A), Color(0xFF3B2A5C))))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "Stage $level / $MAX_STAGE", color = Color.White,
            fontSize = 18.sp, fontWeight = FontWeight.Bold
        )

        Box(
            Modifier
                .fillMaxWidth()
                .height(380.dp)
                .graphicsLayer { translationX = shake.value * 24f },
            contentAlignment = Alignment.Center
        ) {
            PhoenixCreature(
                stage = stageAnim.value,
                modifier = Modifier
                    .size(300.dp)
                    .graphicsLayer {
                        scaleY = squash.value
                        scaleX = 2f - squash.value
                    }
            )

            // Particle + shockwave overlay
            Canvas(Modifier.fillMaxSize()) {
                val t = burst.value
                if (t > 0f) {
                    val c = center
                    val e = FastOutSlowInEasing.transform(t)
                    drawCircle(
                        Gold.copy(alpha = (1f - t) * 0.9f), radius = e * size.width * 0.6f,
                        center = c, style = Stroke(12f * (1f - t) + 2f)
                    )
                    particles.forEach { pt ->
                        val d = pt.speed * e * size.width
                        val x = c.x + cos(pt.angle) * d
                        val y = c.y + sin(pt.angle) * d + 350f * t * t // gravity
                        drawCircle(pt.color.copy(alpha = 1f - t), pt.size * (1f - t * 0.5f), Offset(x, y))
                    }
                }
            }

            // "LEVEL UP!" pop text
            val t = burst.value
            if (t > 0f) {
                Text(
                    if (level == MAX_STAGE) "FINAL FORM!" else "LEVEL UP!",
                    color = Gold, fontSize = 34.sp, fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .graphicsLayer {
                            val pop = FastOutSlowInEasing.transform((t * 3f).coerceAtMost(1f))
                            scaleX = 0.5f + pop * 0.7f
                            scaleY = 0.5f + pop * 0.7f
                            alpha = (1.4f - t * 1.4f).coerceIn(0f, 1f)
                            translationY = -t * 60f
                        }
                )
            }
        }

        AnimatedContent(
            targetState = level,
            transitionSpec = {
                (slideInVertically { it } + fadeIn()) togetherWith (slideOutVertically { -it } + fadeOut())
            },
            label = "name"
        ) { lv ->
            Text(
                stageNames[lv - 1], color = Color.White,
                fontSize = 26.sp, fontWeight = FontWeight.ExtraBold
            )
        }

        // Duolingo-style XP bar
        LinearProgressIndicator(
            progress = { xp },
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .clip(RoundedCornerShape(50)),
            color = Color(0xFF58CC02),
            trackColor = Color(0x33FFFFFF),
        )

        Button(
            onClick = ::levelUp,
            enabled = level < MAX_STAGE,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF58CC02)),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                if (level < MAX_STAGE) "EVOLVE (simulate lesson complete)" else "MAX LEVEL",
                fontWeight = FontWeight.Bold
            )
        }
    }
}
