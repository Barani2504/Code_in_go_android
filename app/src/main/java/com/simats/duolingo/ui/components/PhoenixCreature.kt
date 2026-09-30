package com.simats.duolingo.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.random.Random

/* ================================================================== */
/*  CONSTANTS & COLOR PALETTE FOR PHOENIX CREATURE                    */
/* ================================================================== */

const val MAX_STAGE = 18

val EmberYellow = Color(0xFFFFE082)
val EmberOrange = Color(0xFFFF7043)
val EmberCrimson = Color(0xFFD50000)
val EmberGold = Color(0xFFFFC107)
val AshGrey = Color(0xFF7A7A7A)
val DeepBg0 = Color(0xFF07060F)
val DeepBg1 = Color(0xFF160C2B)
val DeepBg2 = Color(0xFF2A1030)
val MintGreen = Color(0xFF58CC02)

fun bodyColor(p: Float): Color =
    if (p < 0.5f) lerp(EmberYellow, EmberOrange, p * 2f)
    else lerp(EmberOrange, EmberCrimson, (p - 0.5f) * 2f)

fun flameColor(p: Float): Color = lerp(EmberOrange, EmberGold, p)

/* ================================================================== */
/*  PROCEDURAL ANIMATED PHOENIX CREATURE                              */
/* ================================================================== */

@Composable
fun PhoenixCreature(
    stage: Float,
    damage: Float = 0f,
    burn: Float = 0f,
    hurtPulse: Float = 0f,
    rise: Float = 1f,
    ascendant: Boolean = false,
    modifier: Modifier = Modifier
) {
    val inf = rememberInfiniteTransition(label = "phoenix-idle")

    val bob by inf.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bob"
    )
    val flap by inf.animateFloat(
        initialValue = -1f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(720, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "flap"
    )
    val flicker by inf.animateFloat(
        initialValue = 0.86f, targetValue = 1.14f,
        animationSpec = infiniteRepeatable(tween(380, easing = LinearEasing), RepeatMode.Reverse),
        label = "flicker"
    )
    val sway by inf.animateFloat(
        initialValue = -1f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "sway"
    )

    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f

        val alive = (1f - burn).coerceIn(0f, 1f)
        val p = ((stage - 1f) / (MAX_STAGE - 1f)).coerceIn(0f, 1f)

        // ---- Ambient Aura -------------------------------------------------
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

        // ---- Ash Pile ------------------------------------------------------
        if (burn > 0.01f) {
            drawAshes(
                center = Offset(cx, h * 0.80f),
                w = w,
                intensity = burn,
                flicker = flicker,
                glow = if (rise < 1f) 1f - rise else 0f
            )
        }

        // ---- Living Creature ----------------------------------------------
        if (alive > 0.01f) {
            val cy = h * 0.50f +
                    (1f - rise) * h * 0.22f +
                    (bob - 0.5f) * h * 0.030f
            val center = Offset(cx + sway * w * 0.007f, cy)

            val alpha = alive * (0.25f + 0.75f * rise)
            val scaleIn = 0.35f + 0.65f * rise

            val eggA = (1f - (stage - 3f)).coerceIn(0f, 1f)
            val birdA = (stage - 3f).coerceIn(0f, 1f)

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

/* ------------------------------ EGG DRAWING ------------------------------- */

private fun DrawScope.drawEgg(
    stage: Float, c: Offset, w: Float, flap: Float, flicker: Float, alpha: Float
) {
    val wobble = if (stage < 4f) flap * (stage - 1f) * 3.5f else 0f

    rotate(wobble, c) {
        val ew = w * 0.34f
        val eh = w * 0.44f
        val tl = Offset(c.x - ew / 2, c.y - eh / 2)

        // Outer heat glow
        drawOval(
            brush = Brush.radialGradient(
                listOf(EmberOrange.copy(alpha = 0.35f * flicker * alpha), Color.Transparent),
                center = c, radius = ew * 1.2f
            ),
            topLeft = Offset(c.x - ew, c.y - eh), size = Size(ew * 2, eh * 2)
        )

        // Shell
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

        // Rim light
        drawOval(
            color = Color.White.copy(alpha = 0.28f * alpha),
            topLeft = Offset(tl.x + ew * 0.08f, tl.y + eh * 0.06f),
            size = Size(ew * 0.84f, eh * 0.88f),
            style = Stroke(w * 0.006f)
        )

        // Speckles
        drawCircle(Color(0x55FFFFFF), ew * 0.075f, Offset(c.x - ew * 0.16f, c.y - eh * 0.14f), alpha = alpha)
        drawCircle(Color(0x33BF360C), ew * 0.055f, Offset(c.x + ew * 0.17f, c.y + eh * 0.10f), alpha = alpha)
        drawCircle(Color(0x33BF360C), ew * 0.04f, Offset(c.x - ew * 0.20f, c.y + eh * 0.22f), alpha = alpha)

        // Cracks
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

        // Inner light leaking through cracks
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

/* ------------------------------ BIRD DRAWING ------------------------------ */

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

        // Belly
        drawOval(
            color = EmberYellow.copy(alpha = 0.70f * alpha),
            topLeft = Offset(bodyC.x - r * 0.50f, bodyC.y - r * 0.05f),
            size = Size(r * 1.0f, r * 0.92f)
        )

        // Chest feather arcs
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

            // Angry / pained brow
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
            // Red damage rim
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

/* ----------------------------- WINGS ------------------------------ */

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

    // Ground scorch
    drawOval(
        brush = Brush.radialGradient(
            listOf(Color(0x66000000), Color.Transparent),
            center = Offset(center.x, baseY), radius = moundW * 0.9f
        ),
        topLeft = Offset(center.x - moundW * 0.9f, baseY - moundH * 0.6f),
        size = Size(moundW * 1.8f, moundH * 1.2f)
    )

    // Mound
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

    // Lingering embers inside the pile
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

    // Smoke wisps
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
/*  FX OVERLAY  (particles, shockwave, rays)                          */
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
fun FxOverlay(
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

        // ---- Shockwave Rings -------------------------------------------
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

        // ---- Radial Light Rays (Victory / Rebirth) ----------------------
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

        // ---- Particles ---------------------------------------------------
        particles.forEach { pt ->
            val d = pt.speed * e * size.width
            val x = c.x + cos(pt.angle) * d
            val y = c.y + sin(pt.angle) * d + pt.gravity * 340f * t * t
            val fade = (1f - t).coerceIn(0f, 1f)
            val col = palette[pt.colorIdx % palette.size]

            if (pt.isFeather && mode != BurstMode.DEATH) {
                rotate(pt.angle * 57.3f + t * 220f, Offset(x, y)) {
                    val path = Path().apply {
                        val length = pt.size * 2.4f * (1f - t * 0.35f)
                        val width = pt.size * 0.7f
                        moveTo(x - length / 2, y)
                        cubicTo(x - length / 4, y - width, x + length / 4, y - width, x + length / 2, y)
                        cubicTo(x + length / 4, y + width, x - length / 4, y + width, x - length / 2, y)
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
/*  EMBER BACKGROUND                                                  */
/* ================================================================== */

private class Mote(val x: Float, val phase: Float, val size: Float, val speed: Float)

@Composable
fun EmberBackground(modifier: Modifier = Modifier) {
    val inf = rememberInfiniteTransition(label = "bg")
    val t by inf.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(11000, easing = LinearEasing)),
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
        // Subtle vignette glow at bottom
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
