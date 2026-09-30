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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.random.Random

/* ================================================================== */
/*  1.  CONSTANTS & PALETTE                                           */
/* ================================================================== */

const val MAX_STAGE = 18
const val CHAPTER_COUNT = 5
const val LEVELS_PER_CHAPTER = 7
const val MAX_LEVEL = CHAPTER_COUNT * LEVELS_PER_CHAPTER      // 35
const val LOSSES_TO_DEATH = 3
const val WINS_TO_ASCEND = 5

val stageNames = listOf(
    "Mystic Egg", "Hairline Crack", "Shaking Egg", "Hatching!",
    "Ember Chick", "Fluffy Flameling", "Spark Fledgling", "Ash Wing",
    "Cinder Glider", "Ember Flyer", "Flame Sprite", "Blaze Wing",
    "Inferno Youngling", "Sunfire Hawk", "Radiant Firebird", "Solar Phoenix",
    "Eternal Ember", "PHOENIX ASCENDANT"
)

val chapterNames = listOf(
    "Syntax Sanctum", "Loop Labyrinth", "Function Forge",
    "Data Dominion", "Algorithm Apex"
)

private val EmberYellow = Color(0xFFFFE082)
private val EmberOrange = Color(0xFFFF7043)
private val EmberCrimson = Color(0xFFD50000)
private val EmberGold = Color(0xFFFFC107)
private val AshGrey = Color(0xFF7A7A7A)
private val DeepBg0 = Color(0xFF07060F)
private val DeepBg1 = Color(0xFF160C2B)
private val DeepBg2 = Color(0xFF2A1030)
private val MintGreen = Color(0xFF58CC02)

private fun bodyColor(p: Float) =
    if (p < 0.5f) lerp(EmberYellow, EmberOrange, p * 2f)
    else lerp(EmberOrange, EmberCrimson, (p - 0.5f) * 2f)

private fun flameColor(p: Float) = lerp(EmberOrange, EmberGold, p)

/** Stage the phoenix *should* be at for a given course level. */
fun stageForLevel(level: Int): Int =
    (1 + (level - 1) * (MAX_STAGE - 1) / (MAX_LEVEL - 1)).coerceIn(1, MAX_STAGE)

/* ================================================================== */
/*  2.  GAME MODEL                                                    */
/* ================================================================== */

enum class PhoenixEvent { VICTORY, EVOLVE, ASCEND, HURT, DEATH, REBIRTH }

data class PhoenixSave(
    val level: Int = 1,
    val stage: Int = 1,
    val winStreak: Int = 0,
    val lossStreak: Int = 0,
    val totalWins: Int = 0,
    val totalLosses: Int = 0,
    val isAshes: Boolean = false,
    val damage: Float = 0f,
    val ascendant: Boolean = false
) {
    val chapter: Int get() = ((level - 1) / LEVELS_PER_CHAPTER).coerceIn(0, CHAPTER_COUNT - 1)
    val levelInChapter: Int get() = ((level - 1) % LEVELS_PER_CHAPTER) + 1
    val progress: Float get() = (level - 1f) / (MAX_LEVEL - 1f)
}

val PhoenixSaveSaver = listSaver<PhoenixSave, Any>(
    save = {
        listOf(
            it.level, it.stage, it.winStreak, it.lossStreak, it.totalWins,
            it.totalLosses, it.isAshes, it.damage, it.ascendant
        )
    },
    restore = {
        PhoenixSave(
            level = it[0] as Int, stage = it[1] as Int,
            winStreak = it[2] as Int, lossStreak = it[3] as Int,
            totalWins = it[4] as Int, totalLosses = it[5] as Int,
            isAshes = it[6] as Boolean, damage = it[7] as Float,
            ascendant = it[8] as Boolean
        )
    }
)

/** Pure transition: win → new save + which cinematic to play. */
fun PhoenixSave.afterWin(): Pair<PhoenixSave, PhoenixEvent> {
    val wasAshes = isAshes
    val newLevel = (level + 1).coerceAtMost(MAX_LEVEL)
    val newStreak = if (wasAshes) 1 else winStreak + 1
    val newStage =
        if (wasAshes) (stage - 1).coerceAtLeast(1)          // rebirth costs one evolution
        else maxOf(stage, stageForLevel(newLevel))
    val newAscendant = newStreak >= WINS_TO_ASCEND

    val next = copy(
        level = newLevel,
        stage = newStage,
        winStreak = newStreak,
        lossStreak = 0,
        totalWins = totalWins + 1,
        isAshes = false,
        damage = (damage - 0.5f).coerceAtLeast(0f),
        ascendant = newAscendant
    )
    val event = when {
        wasAshes -> PhoenixEvent.REBIRTH
        newAscendant && !ascendant -> PhoenixEvent.ASCEND
        newStage > stage -> PhoenixEvent.EVOLVE
        else -> PhoenixEvent.VICTORY
    }
    return next to event
}

/** Pure transition: loss → new save + which cinematic to play. */
fun PhoenixSave.afterLose(): Pair<PhoenixSave, PhoenixEvent> {
    val newLossStreak = lossStreak + 1
    val dead = newLossStreak >= LOSSES_TO_DEATH
    val next = copy(
        level = (level - 1).coerceAtLeast(1),
        winStreak = 0,
        lossStreak = newLossStreak,
        totalLosses = totalLosses + 1,
        isAshes = dead,
        damage = if (dead) 1f else (damage + 0.34f).coerceAtMost(1f),
        ascendant = false
    )
    return next to if (dead) PhoenixEvent.DEATH else PhoenixEvent.HURT
}

/* ================================================================== */
/*  3.  THE CREATURE                                                  */
/* ================================================================== */

@Composable
fun PhoenixCreature(
    stage: Float,
    damage: Float,
    burn: Float,
    hurtPulse: Float,
    rise: Float,
    ascendant: Boolean,
    modifier: Modifier = Modifier
) {
    val inf = rememberInfiniteTransition(label = "phoenix-idle")

    val bob by inf.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bob"
    )
    val flap by inf.animateFloat(
        -1f, 1f,
        infiniteRepeatable(tween(720, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "flap"
    )
    val flicker by inf.animateFloat(
        0.86f, 1.14f,
        infiniteRepeatable(tween(380, easing = LinearEasing), RepeatMode.Reverse),
        label = "flicker"
    )
    val sway by inf.animateFloat(
        -1f, 1f,
        infiniteRepeatable(tween(3400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "sway"
    )

    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f

        val alive = (1f - burn).coerceIn(0f, 1f)
        val p = ((stage - 1f) / (MAX_STAGE - 1f)).coerceIn(0f, 1f)

        // ---- ambient aura -------------------------------------------------
        val auraAlpha = (0.14f + 0.55f * p) * flicker * alive
        if (auraAlpha > 0.01f) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(
                        flameColor(p).copy(alpha = auraAlpha),
                        flameColor(p).copy(alpha = auraAlpha * 0.25f),
                        Color.Transparent
                    ),
                    center = Offset(cx, h * 0.52f),
                    radius = w * (0.32f + 0.24f * p)
                ),
                radius = w * 0.56f,
                center = Offset(cx, h * 0.52f)
            )
        }

        // ---- ash pile ------------------------------------------------------
        if (burn > 0.01f) {
            drawAshes(
                center = Offset(cx, h * 0.80f),
                w = w,
                intensity = burn,
                flicker = flicker,
                glow = if (rise < 1f) 1f - rise else 0f
            )
        }

        // ---- living creature ----------------------------------------------
        if (alive > 0.01f) {
            val cy = h * 0.50f +
                    (1f - rise) * h * 0.22f +
                    (bob - 0.5f) * h * 0.030f
            val center = Offset(cx + sway * w * 0.007f, cy)

            val alpha = alive * (0.25f + 0.75f * rise)
            val scaleIn = 0.35f + 0.65f * rise

            val eggA = (1f - (stage - 3f)).coerceIn(0f, 1f)
            val birdA = ((stage - 3f)).coerceIn(0f, 1f)

            scale(scaleIn, scaleIn, center) {
                if (eggA > 0.01f) {
                    drawEgg(
                        stage = stage, c = center, w = w,
                        flap = flap, flicker = flicker, alpha = eggA * alpha
                    )
                }
                if (birdA > 0.01f) {
                    drawBird(
                        stage = max(stage, 3.6f), c = center, w = w,
                        flap = flap, flicker = flicker,
                        damage = damage, hurt = hurtPulse,
                        ascendant = ascendant, alpha = birdA * alpha
                    )
                }
            }
        }
    }
}

/* ------------------------------ EGG ------------------------------- */

private fun DrawScope.drawEgg(
    stage: Float, c: Offset, w: Float, flap: Float, flicker: Float, alpha: Float
) {
    val wobble = if (stage < 4f) flap * (stage - 1f) * 3.5f else 0f

    rotate(wobble, c) {
        val ew = w * 0.34f
        val eh = w * 0.44f
        val tl = Offset(c.x - ew / 2, c.y - eh / 2)

        // outer heat glow
        drawOval(
            brush = Brush.radialGradient(
                listOf(EmberOrange.copy(alpha = 0.35f * flicker * alpha), Color.Transparent),
                center = c, radius = ew * 1.2f
            ),
            topLeft = Offset(c.x - ew, c.y - eh), size = Size(ew * 2, eh * 2)
        )

        // shell
        drawOval(
            brush = Brush.radialGradient(
                listOf(
                    Color(0xFFFFF8E1),
                    Color(0xFFFFCC80),
                    Color(0xFFEF6C00)
                ),
                center = Offset(c.x - ew * 0.18f, c.y - eh * 0.22f),
                radius = eh * 0.85f
            ),
            topLeft = tl, size = Size(ew, eh), alpha = alpha
        )

        // rim light
        drawOval(
            color = Color.White.copy(alpha = 0.28f * alpha),
            topLeft = Offset(tl.x + ew * 0.08f, tl.y + eh * 0.06f),
            size = Size(ew * 0.84f, eh * 0.88f),
            style = Stroke(w * 0.006f)
        )

        // speckles
        drawCircle(Color(0x55FFFFFF), ew * 0.075f, Offset(c.x - ew * 0.16f, c.y - eh * 0.14f), alpha = alpha)
        drawCircle(Color(0x33BF360C), ew * 0.055f, Offset(c.x + ew * 0.17f, c.y + eh * 0.10f), alpha = alpha)
        drawCircle(Color(0x33BF360C), ew * 0.04f, Offset(c.x - ew * 0.20f, c.y + eh * 0.22f), alpha = alpha)

        // cracks
        val cracks = (stage.toInt() - 1).coerceIn(0, 3)
        repeat(cracks) { i ->
            val sx = c.x - ew * 0.26f + i * ew * 0.24f
            val path = Path().apply {
                moveTo(sx, c.y - eh * 0.52f)
                lineTo(sx + ew * 0.07f, c.y - eh * 0.34f)
                lineTo(sx - ew * 0.06f, c.y - eh * 0.20f)
                lineTo(sx + ew * 0.06f, c.y - eh * 0.04f)
            }
            drawPath(
                path,
                Color(0xFF4E342E).copy(alpha = alpha),
                style = Stroke(w * 0.007f + i * 0.6f, cap = StrokeCap.Round)
            )
            drawPath(
                path,
                EmberGold.copy(alpha = 0.75f * alpha * flicker),
                style = Stroke(w * 0.0035f, cap = StrokeCap.Round)
            )
        }

        // inner light leaking through cracks
        if (stage >= 3f) {
            val leak = ((stage - 3f) / 1f).coerceIn(0f, 1f)
            drawOval(
                brush = Brush.radialGradient(
                    listOf(
                        EmberGold.copy(alpha = 0.7f * leak * flicker * alpha),
                        Color.Transparent
                    ),
                    center = c, radius = ew * 0.6f
                ),
                topLeft = tl, size = Size(ew, eh)
            )
        }
    }
}

/* ------------------------------ BIRD ------------------------------ */

private fun DrawScope.drawBird(
    stage: Float,
    c: Offset,
    w: Float,
    flap: Float,
    flicker: Float,
    damage: Float,
    hurt: Float,
    ascendant: Boolean,
    alpha: Float
) {
    val p = ((stage - 1f) / (MAX_STAGE - 1f)).coerceIn(0f, 1f)
    val s = 0.40f + 0.60f * p
    val r = w * 0.165f * s

    val body = bodyColor(p)
    val accent = flameColor(p)
    val hot = lerp(accent, Color.White, 0.40f)
    val st = stage.toInt()

    val bodyC = Offset(c.x, c.y + r * 0.30f)
    val headR = r * 0.58f
    val headC = Offset(c.x, bodyC.y - r * 1.30f)

    val tilt = damage * 7f + hurt * 5f

    rotate(tilt, bodyC) {

        /* ---------- TAIL ---------- */
        val tails = (3 + st / 3).coerceAtMost(9)
        val tailLen = r * (1.0f + 1.9f * p)
        repeat(tails) { i ->
            val denom = max(1f, (tails - 1) / 2f)
            val frac = (i - (tails - 1) / 2f) / denom
            val ang = frac * 36f + sin((flap + 1f) * PI.toFloat() * 0.5f + i) * 3.5f
            val len = tailLen * (1f - abs(frac) * 0.22f) * flicker.coerceAtMost(1.06f)

            rotate(ang, bodyC) {
                val path = Path().apply {
                    moveTo(bodyC.x - r * 0.10f, bodyC.y + r * 0.30f)
                    cubicTo(
                        bodyC.x - r * 0.17f, bodyC.y + len * 0.50f,
                        bodyC.x - r * 0.08f, bodyC.y + len * 0.86f,
                        bodyC.x, bodyC.y + len
                    )
                    cubicTo(
                        bodyC.x + r * 0.08f, bodyC.y + len * 0.86f,
                        bodyC.x + r * 0.17f, bodyC.y + len * 0.50f,
                        bodyC.x + r * 0.10f, bodyC.y + r * 0.30f
                    )
                    close()
                }
                drawPath(
                    path,
                    Brush.verticalGradient(
                        listOf(hot, accent, body, Color.Transparent),
                        startY = bodyC.y, endY = bodyC.y + len
                    ),
                    alpha = alpha
                )
            }
        }

        /* ---------- WINGS ---------- */
        val layers = (1 + st / 6).coerceAtMost(3)
        val wingLen = r * (1.05f + 1.85f * p)
        for (side in listOf(1f, -1f)) {
            scale(side, 1f, bodyC) {
                drawWing(
                    bodyC = bodyC, r = r, wingLen = wingLen,
                    flap = flap, layers = layers, st = st,
                    body = body, accent = accent, hot = hot,
                    damage = damage, alpha = alpha
                )
            }
        }

        /* ---------- BODY ---------- */
        drawOval(
            brush = Brush.radialGradient(
                listOf(
                    lerp(body, Color.White, 0.50f),
                    body,
                    lerp(body, Color.Black, 0.32f)
                ),
                center = Offset(bodyC.x - r * 0.32f, bodyC.y - r * 0.42f),
                radius = r * 1.7f
            ),
            topLeft = Offset(bodyC.x - r * 0.88f, bodyC.y - r * 0.98f),
            size = Size(r * 1.76f, r * 1.96f),
            alpha = alpha
        )

        // belly
        drawOval(
            color = EmberYellow.copy(alpha = 0.70f * alpha),
            topLeft = Offset(bodyC.x - r * 0.50f, bodyC.y - r * 0.05f),
            size = Size(r * 1.0f, r * 0.92f)
        )

        // chest feather arcs
        repeat(3) { i ->
            val yy = bodyC.y + r * 0.05f + i * r * 0.22f
            val path = Path().apply {
                moveTo(bodyC.x - r * 0.42f, yy)
                quadraticBezierTo(bodyC.x, yy + r * 0.20f, bodyC.x + r * 0.42f, yy)
            }
            drawPath(
                path,
                body.copy(alpha = 0.45f * alpha),
                style = Stroke(r * 0.07f, cap = StrokeCap.Round)
            )
        }

        /* ---------- CREST ---------- */
        val crest = (1 + st / 4).coerceAtMost(5)
        repeat(crest) { i ->
            val denom = max(1f, (crest - 1) / 2f)
            val rel = (i - (crest - 1) / 2f) / denom
            val off = rel * headR * 0.62f
            val hgt = headR * (0.85f + 0.95f * p) *
                    (1f - abs(rel) * 0.22f) * flicker
            drawFlame(
                base = Offset(headC.x + off, headC.y - headR * 0.72f),
                hgt = hgt,
                wid = headR * 0.26f,
                color = lerp(accent, EmberYellow, (i % 2) * 0.55f).copy(alpha = alpha)
            )
        }

        /* ---------- HEAD ---------- */
        drawCircle(
            brush = Brush.radialGradient(
                listOf(lerp(body, Color.White, 0.40f), body, lerp(body, Color.Black, 0.15f)),
                center = Offset(headC.x - headR * 0.32f, headC.y - headR * 0.34f),
                radius = headR * 1.7f
            ),
            radius = headR, center = headC, alpha = alpha
        )

        /* ---------- EYES ---------- */
        val eyeY = headC.y - headR * 0.06f
        for (side in listOf(-1f, 1f)) {
            val e = Offset(headC.x + side * headR * 0.40f, eyeY)

            drawCircle(Color.White.copy(alpha = alpha), headR * 0.26f, e)

            val pupilR = headR * (0.145f - damage * 0.03f)
            drawCircle(Color(0xFF15130F).copy(alpha = alpha), pupilR, e)
            drawCircle(
                Color.White.copy(alpha = 0.9f * alpha),
                headR * 0.055f,
                Offset(e.x - headR * 0.06f, e.y - headR * 0.08f)
            )

            // angry / pained brow
            if (damage > 0.05f || hurt > 0.05f) {
                val intensity = max(damage, hurt)
                val brow = Path().apply {
                    moveTo(e.x - side * headR * 0.22f, e.y - headR * 0.32f)
                    lineTo(e.x + side * headR * 0.20f, e.y - headR * 0.20f)
                }
                drawPath(
                    brow,
                    Color(0xFF2B2118).copy(alpha = intensity * alpha),
                    style = Stroke(headR * 0.10f, cap = StrokeCap.Round)
                )
            }
            // red damage rim
            if (damage > 0.02f) {
                drawCircle(
                    EmberCrimson.copy(alpha = damage * 0.55f * alpha),
                    headR * 0.30f, e, style = Stroke(headR * 0.07f)
                )
            }
        }

        /* ---------- BEAK ---------- */
        val beak = Path().apply {
            moveTo(headC.x - headR * 0.22f, headC.y + headR * 0.24f)
            lineTo(headC.x + headR * 0.22f, headC.y + headR * 0.24f)
            lineTo(headC.x, headC.y + headR * 0.70f)
            close()
        }
        drawPath(beak, Color(0xFFFFA000).copy(alpha = alpha))

        /* ---------- ASCENDANT CROWN ---------- */
        if (ascendant || st >= MAX_STAGE) {
            drawCircle(
                EmberGold.copy(alpha = 0.85f * flicker * alpha),
                radius = headR * 1.28f, center = headC,
                style = Stroke(w * 0.009f)
            )
            drawCircle(
                Color.White.copy(alpha = 0.35f * flicker * alpha),
                radius = headR * 1.45f, center = headC,
                style = Stroke(w * 0.004f)
            )
        }
    }
}

/* ----------------------------- WING ------------------------------- */

private fun DrawScope.drawWing(
    bodyC: Offset, r: Float, wingLen: Float, flap: Float, layers: Int, st: Int,
    body: Color, accent: Color, hot: Color, damage: Float, alpha: Float
) {
    val shoulder = Offset(bodyC.x + r * 0.48f, bodyC.y - r * 0.32f)
    val featherBase = (5 + st / 3).coerceAtMost(9)

    for (l in layers - 1 downTo 0) {
        val layerFrac = if (layers <= 1) 0f else l / (layers - 1f)
        val len = wingLen * (1f - layerFrac * 0.30f)
        val rot = -28f + flap * 24f - l * 13f

        rotate(rot, shoulder) {
            // missing feathers grow with damage
            val missing = (damage * 4f).toInt()
            val count = (featherBase - missing).coerceAtLeast(3)

            for (i in 0 until count) {
                val f = if (count <= 1) 0f else i / (count - 1f)
                val ang = -44f + f * 66f
                val flen = len * (1f - f * 0.26f)
                val fwid = r * 0.26f * (1f - f * 0.34f)

                rotate(ang, shoulder) {
                    val base = Offset(shoulder.x + f * len * 0.22f, shoulder.y)
                    val path = featherPath(base, flen, fwid)
                    val col = lerp(
                        lerp(body, accent, f),
                        hot,
                        (1f - layerFrac) * 0.40f
                    )
                    drawPath(path, col.copy(alpha = 0.95f * alpha))
                    drawPath(
                        path,
                        Color.White.copy(alpha = 0.12f * alpha),
                        style = Stroke(1.6f)
                    )
                }
            }
        }
    }
}

private fun featherPath(base: Offset, len: Float, wid: Float): Path = Path().apply {
    moveTo(base.x, base.y)
    cubicTo(
        base.x + len * 0.24f, base.y - wid,
        base.x + len * 0.72f, base.y - wid * 0.86f,
        base.x + len, base.y - wid * 0.10f
    )
    cubicTo(
        base.x + len * 0.72f, base.y + wid * 0.86f,
        base.x + len * 0.24f, base.y + wid,
        base.x, base.y
    )
    close()
}

/* ----------------------------- ASHES ------------------------------ */

private fun DrawScope.drawAshes(
    center: Offset, w: Float, intensity: Float, flicker: Float, glow: Float
) {
    val moundW = w * 0.44f
    val moundH = w * 0.11f
    val baseY = center.y
    val a = intensity.coerceIn(0f, 1f)

    // ground scorch
    drawOval(
        brush = Brush.radialGradient(
            listOf(Color(0x66000000), Color.Transparent),
            center = Offset(center.x, baseY), radius = moundW * 0.9f
        ),
        topLeft = Offset(center.x - moundW * 0.9f, baseY - moundH * 0.6f),
        size = Size(moundW * 1.8f, moundH * 1.2f)
    )

    // mound
    val mound = Path().apply {
        moveTo(center.x - moundW / 2, baseY)
        cubicTo(
            center.x - moundW * 0.32f, baseY - moundH * 1.75f,
            center.x + moundW * 0.32f, baseY - moundH * 1.75f,
            center.x + moundW / 2, baseY
        )
        close()
    }
    drawPath(
        mound,
        Brush.verticalGradient(
            listOf(Color(0xFF585858), Color(0xFF2B2B2B), Color(0xFF141414)),
            startY = baseY - moundH * 1.75f, endY = baseY
        ),
        alpha = a
    )

    // lingering embers inside the pile
    val embers = listOf(
        Offset(-0.22f, -0.30f), Offset(0.10f, -0.22f), Offset(-0.05f, -0.52f),
        Offset(0.24f, -0.38f), Offset(-0.30f, -0.16f), Offset(0.02f, -0.72f)
    )
    embers.forEachIndexed { i, o ->
        val pulse = (0.55f + 0.45f * sin(flicker * 4f + i)).coerceIn(0f, 1f)
        val pos = Offset(center.x + o.x * moundW, baseY + o.y * moundH * 1.8f)
        drawCircle(
            EmberOrange.copy(alpha = (0.75f * pulse + glow) * a),
            moundW * 0.030f * (0.7f + pulse * 0.6f),
            pos
        )
        drawCircle(
            EmberGold.copy(alpha = (0.45f * pulse + glow * 0.6f) * a),
            moundW * 0.016f,
            pos
        )
    }

    // smoke wisps
    repeat(5) { i ->
        val t = (flicker * 0.4f + i * 0.19f) % 1f
        val x = center.x + sin(t * 6.28f + i * 2f) * moundW * 0.30f
        val y = baseY - moundH * 1.2f - t * w * 0.30f
        drawCircle(
            Color(0xFF9E9E9E).copy(alpha = (1f - t) * 0.18f * a),
            w * (0.02f + t * 0.055f), Offset(x, y)
        )
    }
}

/* ----------------------------- FLAME ------------------------------ */

private fun DrawScope.drawFlame(base: Offset, hgt: Float, wid: Float, color: Color) {
    val path = Path().apply {
        moveTo(base.x, base.y)
        cubicTo(
            base.x - wid * 1.7f, base.y - hgt * 0.30f,
            base.x - wid * 0.45f, base.y - hgt * 0.72f,
            base.x, base.y - hgt
        )
        cubicTo(
            base.x + wid * 0.45f, base.y - hgt * 0.72f,
            base.x + wid * 1.7f, base.y - hgt * 0.30f,
            base.x, base.y
        )
        close()
    }
    drawPath(path, color)
}

/* ================================================================== */
/*  4.  FX OVERLAY  (particles, shockwave, rays)                      */
/* ================================================================== */

enum class BurstMode { VICTORY, HURT, DEATH, REBIRTH }

private class FxParticle(
    val angle: Float,
    val speed: Float,
    val size: Float,
    val colorIdx: Int,
    val gravity: Float,
    val isFeather: Boolean
)

private val VICTORY_PALETTE =
    listOf(EmberGold, EmberOrange, EmberYellow, Color.White, Color(0xFFFFD54F))
private val HURT_PALETTE =
    listOf(EmberCrimson, Color(0xFF8E0000), EmberOrange, Color(0xFF5D4037))
private val DEATH_PALETTE =
    listOf(AshGrey, Color(0xFF424242), EmberOrange, Color(0xFF212121))
private val REBIRTH_PALETTE =
    listOf(Color.White, EmberGold, EmberOrange, Color(0xFF80D8FF), EmberYellow)

@Composable
private fun FxOverlay(
    progress: Float,
    mode: BurstMode,
    modifier: Modifier = Modifier
) {
    val particles = remember {
        List(96) {
            FxParticle(
                angle = Random.nextFloat() * 2f * PI.toFloat(),
                speed = 0.10f + Random.nextFloat() * 0.55f,
                size = 4f + Random.nextFloat() * 12f,
                colorIdx = Random.nextInt(16),
                gravity = 0.4f + Random.nextFloat() * 1.4f,
                isFeather = Random.nextFloat() < 0.28f
            )
        }
    }

    val palette = when (mode) {
        BurstMode.VICTORY -> VICTORY_PALETTE
        BurstMode.HURT -> HURT_PALETTE
        BurstMode.DEATH -> DEATH_PALETTE
        BurstMode.REBIRTH -> REBIRTH_PALETTE
    }

    Canvas(modifier) {
        if (progress <= 0f) return@Canvas
        val t = progress.coerceIn(0f, 1f)
        val e = FastOutSlowInEasing.transform(t)
        val c = center

        // ---- shockwave rings -------------------------------------------
        if (mode != BurstMode.HURT) {
            repeat(2) { ring ->
                val rt = ((t - ring * 0.14f) / (1f - ring * 0.14f)).coerceIn(0f, 1f)
                if (rt > 0f) {
                    val re = FastOutSlowInEasing.transform(rt)
                    drawCircle(
                        color = (if (mode == BurstMode.DEATH) AshGrey else EmberGold)
                            .copy(alpha = (1f - rt) * 0.85f),
                        radius = re * size.width * (0.55f - ring * 0.10f),
                        center = c,
                        style = Stroke(14f * (1f - rt) + 2f)
                    )
                }
            }
        }

        // ---- radial light rays (victory / rebirth) ----------------------
        if (mode == BurstMode.VICTORY || mode == BurstMode.REBIRTH) {
            val rays = 14
            repeat(rays) { i ->
                val ang = i * (360f / rays) + t * 40f
                rotate(ang, c) {
                    val rayAlpha = (1f - t) * 0.35f
                    val len = size.width * (0.25f + 0.35f * e)
                    val path = Path().apply {
                        moveTo(c.x + 20f, c.y - 8f)
                        lineTo(c.x + len, c.y - 2f)
                        lineTo(c.x + len, c.y + 2f)
                        lineTo(c.x + 20f, c.y + 8f)
                        close()
                    }
                    drawPath(
                        path,
                        Brush.horizontalGradient(
                            listOf(EmberGold.copy(alpha = rayAlpha), Color.Transparent),
                            startX = c.x, endX = c.x + len
                        )
                    )
                }
            }
        }

        // ---- particles ---------------------------------------------------
        particles.forEach { pt ->
            val d = pt.speed * e * size.width
            val x = c.x + cos(pt.angle) * d
            val y = c.y + sin(pt.angle) * d + pt.gravity * 340f * t * t
            val fade = (1f - t).coerceIn(0f, 1f)
            val col = palette[pt.colorIdx % palette.size]

            if (pt.isFeather && mode != BurstMode.DEATH) {
                rotate(pt.angle * 57.3f + t * 220f, Offset(x, y)) {
                    val path = Path().apply {
                        val L = pt.size * 2.4f * (1f - t * 0.35f)
                        val W = pt.size * 0.7f
                        moveTo(x - L / 2, y)
                        cubicTo(x - L / 4, y - W, x + L / 4, y - W, x + L / 2, y)
                        cubicTo(x + L / 4, y + W, x - L / 4, y + W, x - L / 2, y)
                        close()
                    }
                    drawPath(path, col.copy(alpha = fade * 0.95f))
                }
            } else {
                drawCircle(
                    col.copy(alpha = fade),
                    pt.size * (1f - t * 0.5f),
                    Offset(x, y)
                )
                // glow
                drawCircle(
                    col.copy(alpha = fade * 0.25f),
                    pt.size * (1f - t * 0.5f) * 2.2f,
                    Offset(x, y)
                )
            }
        }
    }
}

/* ================================================================== */
/*  5.  BACKGROUND                                                    */
/* ================================================================== */

private class Mote(val x: Float, val phase: Float, val size: Float, val speed: Float)

@Composable
private fun EmberBackground(modifier: Modifier = Modifier) {
    val inf = rememberInfiniteTransition(label = "bg")
    val t by inf.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(11000, easing = LinearEasing)),
        label = "t"
    )
    val motes = remember {
        List(52) {
            Mote(
                x = Random.nextFloat(),
                phase = Random.nextFloat(),
                size = 1f + Random.nextFloat() * 3.5f,
                speed = 0.6f + Random.nextFloat() * 0.9f
            )
        }
    }

    Canvas(modifier) {
        drawRect(
            Brush.verticalGradient(
                listOf(DeepBg0, DeepBg1, DeepBg2, Color(0xFF160A1E))
            )
        )
        // subtle vignette glow at the bottom
        drawCircle(
            brush = Brush.radialGradient(
                listOf(EmberOrange.copy(alpha = 0.10f), Color.Transparent),
                center = Offset(size.width / 2f, size.height * 1.05f),
                radius = size.width * 0.9f
            ),
            radius = size.width * 0.9f,
            center = Offset(size.width / 2f, size.height * 1.05f)
        )

        motes.forEach { m ->
            val prog = ((m.phase + t * m.speed) % 1f)
            val x = m.x * size.width + sin(prog * 6.28f + m.x * 12f) * 26f
            val y = size.height * (1f - prog)
            val fade = (1f - prog) * (0.35f + 0.65f * sin(prog * PI.toFloat()))
            drawCircle(
                EmberGold.copy(alpha = fade * 0.55f),
                m.size * (0.4f + fade * 0.6f),
                Offset(x, y)
            )
        }
    }
}

/* ================================================================== */
/*  6.  HUD PIECES                                                    */
/* ================================================================== */

@Composable
private fun TopHud(save: PhoenixSave, modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Chapter pill
        Box(
            Modifier
                .clip(RoundedCornerShape(50))
                .background(Color(0x22FFFFFF))
                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(50))
                .padding(horizontal = 14.dp, vertical = 7.dp)
        ) {
            Text(
                "CH ${save.chapter + 1} · ${chapterNames[save.chapter]}",
                color = Color.White.copy(alpha = 0.92f),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Streak flame
            if (save.winStreak > 0) {
                StreakChip(save.winStreak)
                Spacer(Modifier.width(8.dp))
            }
            Box(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (save.isAshes) Color(0x33FF1744) else Color(0x22FFC107)
                    )
                    .padding(horizontal = 12.dp, vertical = 7.dp)
            ) {
                Text(
                    "Lv ${save.level}",
                    color = if (save.isAshes) Color(0xFFFF8A80) else EmberGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun StreakChip(streak: Int) {
    val inf = rememberInfiniteTransition(label = "streak")
    val pulse by inf.animateFloat(
        0.9f, 1.12f,
        infiniteRepeatable(tween(700, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )
    val hot = streak >= WINS_TO_ASCEND
    Box(
        Modifier
            .graphicsLayer { scaleX = pulse; scaleY = pulse }
            .clip(RoundedCornerShape(50))
            .background(
                if (hot) Brush.horizontalGradient(listOf(EmberGold, EmberCrimson))
                else Brush.horizontalGradient(listOf(EmberOrange, EmberGold))
            )
            .padding(horizontal = 11.dp, vertical = 6.dp)
    ) {
        Text(
            "🔥 $streak",
            color = Color(0xFF2B1A00),
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun ChapterProgress(save: PhoenixSave, modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        repeat(CHAPTER_COUNT) { ch ->
            val startLevel = ch * LEVELS_PER_CHAPTER + 1
            val done =
                (save.level - startLevel + 1).coerceIn(0, LEVELS_PER_CHAPTER).toFloat()
            val frac = done / LEVELS_PER_CHAPTER
            Box(
                Modifier
                    .weight(1f)
                    .height(9.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0x22FFFFFF))
            ) {
                if (frac > 0.001f) {
                    Box(
                        Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(frac)
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (frac >= 1f) Brush.horizontalGradient(
                                    listOf(MintGreen, Color(0xFF89E219))
                                )
                                else Brush.horizontalGradient(
                                    listOf(EmberOrange, EmberGold)
                                )
                            )
                    )
                }
            }
        }
    }
}

/* ================================================================== */
/*  7.  MAIN SCREEN                                                   */
/* ================================================================== */

@Composable
fun PhoenixEvolutionScreen(modifier: Modifier = Modifier) {

    /* ---------- persistent game state ---------- */
    var save by rememberSaveable(stateSaver = PhoenixSaveSaver) {
        mutableStateOf(PhoenixSave())
    }

    /* ---------- animation drivers ---------- */
    val stageAnim = remember { Animatable(1f) }
    val burst = remember { Animatable(0f) }
    val bannerT = remember { Animatable(0f) }
    val flash = remember { Animatable(0f) }
    val shake = remember { Animatable(0f) }
    val squash = remember { Animatable(1f) }
    val burn = remember { Animatable(0f) }
    val hurtPulse = remember { Animatable(0f) }
    val rise = remember { Animatable(1f) }
    val fly = remember { Animatable(0f) }

    var burstMode by remember { mutableStateOf(BurstMode.VICTORY) }
    var flashColor by remember { mutableStateOf(EmberGold) }
    var banner by remember { mutableStateOf("") }
    var bannerColor by remember { mutableStateOf(EmberGold) }

    val scope = rememberCoroutineScope()

    /* ---------- restore on first composition ---------- */
    LaunchedEffect(Unit) {
        stageAnim.snapTo(save.stage.toFloat())
        burn.snapTo(if (save.isAshes) 1f else 0f)
        rise.snapTo(1f)
    }

    /* =================== WIN CINEMATIC =================== */
    suspend fun playWin(event: PhoenixEvent) {
        banner = when (event) {
            PhoenixEvent.REBIRTH -> "REBORN!"
            PhoenixEvent.ASCEND -> "ASCENDED!"
            PhoenixEvent.EVOLVE -> "EVOLVED!"
            else -> "VICTORY!"
        }
        bannerColor = when (event) {
            PhoenixEvent.REBIRTH -> Color(0xFFFFF59D)
            PhoenixEvent.ASCEND -> EmberCrimson
            else -> EmberGold
        }

        // banner pop
        scope.launch {
            bannerT.snapTo(0f)
            bannerT.animateTo(1f, tween(1500, easing = LinearEasing))
        }

        // screen shake
        scope.launch {
            shake.snapTo(if (event == PhoenixEvent.ASCEND) 1.6f else 1f)
            shake.animateTo(
                0f,
                spring(Spring.DampingRatioHighBouncy, Spring.StiffnessMedium)
            )
        }

        if (event == PhoenixEvent.REBIRTH) {
            burstMode = BurstMode.REBIRTH
            flashColor = Color.White

            scope.launch {
                flash.snapTo(0f)
                flash.animateTo(1f, tween(140))
                flash.animateTo(0f, tween(800))
            }
            scope.launch {
                burn.snapTo(1f)
                delay(180)
                burn.animateTo(0f, tween(950, easing = FastOutSlowInEasing))
            }
            scope.launch {
                rise.snapTo(0f)
                rise.animateTo(1f, tween(1150, easing = FastOutSlowInEasing))
            }
            scope.launch {
                burst.snapTo(0f)
                delay(260)
                burst.animateTo(1f, tween(1700, easing = LinearEasing))
                burst.snapTo(0f)
            }
            hurtPulse.animateTo(0f, tween(300))
            stageAnim.snapTo(save.stage.toFloat())
        } else {
            burstMode = BurstMode.VICTORY
            flashColor = if (event == PhoenixEvent.ASCEND) Color.White else EmberGold

            // anticipation squash → bouncy release
            scope.launch {
                squash.snapTo(0.72f)
                squash.animateTo(
                    1f,
                    spring(Spring.DampingRatioHighBouncy, Spring.StiffnessLow)
                )
            }
            scope.launch {
                flash.snapTo(0f)
                flash.animateTo(if (event == PhoenixEvent.ASCEND) 0.85f else 0.5f, tween(120))
                flash.animateTo(0f, tween(650))
            }
            scope.launch {
                burst.snapTo(0f)
                delay(160)
                burst.animateTo(
                    1f,
                    tween(if (event == PhoenixEvent.ASCEND) 2000 else 1250, easing = LinearEasing)
                )
                burst.snapTo(0f)
            }
            // little victory hop
            scope.launch {
                fly.snapTo(0f)
                fly.animateTo(1f, tween(420, easing = FastOutSlowInEasing))
                fly.animateTo(0f, tween(620, easing = FastOutLinearInEasing))
            }
            // morph stage
            if (event == PhoenixEvent.EVOLVE || event == PhoenixEvent.ASCEND) {
                scope.launch {
                    delay(200)
                    stageAnim.animateTo(
                        save.stage.toFloat(),
                        tween(1000, easing = FastOutSlowInEasing)
                    )
                }
            } else {
                scope.launch { stageAnim.animateTo(save.stage.toFloat(), tween(400)) }
            }
            hurtPulse.animateTo(0f, tween(400))
        }
    }

    /* =================== LOSE CINEMATIC =================== */
    suspend fun playLose(event: PhoenixEvent) {
        shake.snapTo(if (event == PhoenixEvent.DEATH) 2.2f else 1.2f)

        if (event == PhoenixEvent.DEATH) {
            banner = "ASHES..."
            bannerColor = Color(0xFFFF8A80)
            burstMode = BurstMode.DEATH
            flashColor = EmberCrimson

            scope.launch {
                bannerT.snapTo(0f)
                bannerT.animateTo(1f, tween(2200, easing = LinearEasing))
            }
            scope.launch {
                flash.snapTo(0f)
                flash.animateTo(0.95f, tween(170))
                flash.animateTo(0f, tween(1000))
            }
            scope.launch {
                shake.animateTo(
                    0f,
                    spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium)
                )
            }
            scope.launch {
                hurtPulse.snapTo(1f)
                hurtPulse.animateTo(0f, tween(700))
            }
            scope.launch {
                burst.snapTo(0f)
                burst.animateTo(1f, tween(1000, easing = LinearEasing))
                burst.snapTo(0f)
            }
            scope.launch {
                squash.snapTo(1.14f)
                squash.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium))
            }
            delay(260)
            burn.animateTo(1f, tween(1500, easing = FastOutSlowInEasing))
            hurtPulse.snapTo(0f)
        } else {
            banner = "OUCH!"
            bannerColor = Color(0xFFFF8A80)
            burstMode = BurstMode.HURT
            flashColor = EmberCrimson

            scope.launch {
                bannerT.snapTo(0f)
                bannerT.animateTo(1f, tween(1100, easing = LinearEasing))
            }
            scope.launch {
                flash.snapTo(0f)
                flash.animateTo(0.45f, tween(110))
                flash.animateTo(0f, tween(520))
            }
            scope.launch {
                shake.animateTo(
                    0f,
                    spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessHigh)
                )
            }
            scope.launch {
                hurtPulse.snapTo(1f)
                hurtPulse.animateTo(0f, tween(950, easing = LinearEasing))
            }
            scope.launch {
                burst.snapTo(0f)
                burst.animateTo(1f, tween(720, easing = LinearEasing))
                burst.snapTo(0f)
            }
            scope.launch {
                squash.snapTo(1.16f)
                squash.animateTo(
                    1f,
                    spring(Spring.DampingRatioHighBouncy, Spring.StiffnessMedium)
                )
            }
        }
    }

    /* =================== HANDLERS =================== */
    fun onWin() {
        val (next, event) = save.afterWin()
        save = next
        scope.launch { playWin(event) }
    }

    fun onLose() {
        val (next, event) = save.afterLose()
        save = next
        scope.launch { playLose(event) }
    }

    /* =================== UI =================== */
    Box(modifier.fillMaxSize()) {
        EmberBackground(Modifier.fillMaxSize())

        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopHud(save)

            Spacer(Modifier.height(6.dp))

            /* ---------- CREATURE STAGE ---------- */
            Box(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .graphicsLayer {
                        translationX = shake.value * 22f
                        translationY = -fly.value * 46f
                    },
                contentAlignment = Alignment.Center
            ) {
                PhoenixCreature(
                    stage = stageAnim.value,
                    damage = save.damage,
                    burn = burn.value,
                    hurtPulse = hurtPulse.value,
                    rise = rise.value,
                    ascendant = save.ascendant,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleY = squash.value
                            scaleX = 2f - squash.value
                        }
                )

                FxOverlay(
                    progress = burst.value,
                    mode = burstMode,
                    modifier = Modifier.fillMaxSize()
                )

                // Full-screen colour flash
                if (flash.value > 0.01f) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(flashColor.copy(alpha = flash.value * 0.55f))
                    )
                }

                // Banner text
                if (bannerT.value > 0f && banner.isNotEmpty()) {
                    val t = bannerT.value
                    Text(
                        banner,
                        color = bannerColor,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 8.dp)
                            .graphicsLayer {
                                val pop =
                                    FastOutSlowInEasing.transform((t * 3.2f).coerceAtMost(1f))
                                scaleX = 0.45f + pop * 0.75f
                                scaleY = 0.45f + pop * 0.75f
                                alpha = (1.5f - t * 1.5f).coerceIn(0f, 1f)
                                translationY = -t * 70f
                                rotationZ = (1f - pop) * -6f
                            }
                    )
                }
            }

            /* ---------- NAME + PROGRESS ---------- */
            AnimatedContent(
                targetState = if (save.isAshes) -1 else save.stage,
                transitionSpec = {
                    (slideInVertically { it / 2 } + fadeIn()) togetherWith
                            (slideOutVertically { -it / 2 } + fadeOut())
                },
                label = "name"
            ) { st ->
                Text(
                    text = if (st == -1) "Ashes of the Phoenix"
                    else stageNames[(st - 1).coerceIn(0, MAX_STAGE - 1)],
                    color = if (st == -1) Color(0xFFBDBDBD) else Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }

            Spacer(Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { save.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(50)),
                color = MintGreen,
                trackColor = Color(0x33FFFFFF)
            )

            Spacer(Modifier.height(10.dp))

            ChapterProgress(save)

            Spacer(Modifier.height(6.dp))

            Text(
                text = if (save.isAshes)
                    "Win a lesson to rise from the ashes"
                else
                    "Level ${save.levelInChapter}/$LEVELS_PER_CHAPTER · Chapter ${save.chapter + 1} of $CHAPTER_COUNT",
                color = Color.White.copy(alpha = 0.55f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(14.dp))

            /* ---------- CONTROLS (hook these to your quiz result) ---------- */
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = ::onLose,
                    enabled = !save.isAshes,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8E2A2A),
                        disabledContainerColor = Color(0x33FFFFFF)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                ) {
                    Text(
                        "LOSE",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Button(
                    onClick = ::onWin,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (save.isAshes) EmberGold else MintGreen
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(2f)
                        .height(54.dp)
                ) {
                    Text(
                        when {
                            save.isAshes -> "RISE FROM ASHES"
                            save.level >= MAX_LEVEL -> "COMPLETE LESSON"
                            else -> "WIN LESSON"
                        },
                        fontWeight = FontWeight.ExtraBold,
                        color = if (save.isAshes) Color(0xFF2B1A00) else Color.White
                    )
                }
            }
        }
    }
}

/* ================================================================== */
/*  8.  PUBLIC HOOK — call these from your quiz screen                 */
/* ================================================================== */

/**
 * Attach this to the ViewModel / state holder that owns [PhoenixSave]
 * so the phoenix grows through the 5 chapters × 7 levels.
 *
 *   // quiz finished successfully
 *   val (next, event) = save.afterWin()
 *   repo.save(next)          // persist via DataStore / Room
 *   ui.play(event)
 *
 *   // quiz failed
 *   val (next, event) = save.afterLose()
 *   repo.save(next)
 *   ui.play(event)
 */
object PhoenixRules {
    fun win(save: PhoenixSave): Pair<PhoenixSave, PhoenixEvent> = save.afterWin()
    fun lose(save: PhoenixSave): Pair<PhoenixSave, PhoenixEvent> = save.afterLose()
}