package com.simats.duolingo.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.simats.duolingo.data.PhoenixStageData
import com.simats.duolingo.ui.components.PhoenixCreature
import com.simats.duolingo.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*

// ─────────────────────────────────────────────────────────────────────────────
// DATA CLASSES
// ─────────────────────────────────────────────────────────────────────────────

private data class Particle(
    val id: Int,
    val startX: Float,
    val startY: Float,
    val velocityX: Float,
    val velocityY: Float,
    val size: Float,
    val color: Color,
    var lifetime: Float,
    val maxLifetime: Float,
    val angle: Float,
    val angularVelocity: Float,
    val type: ParticleType,
)

private enum class ParticleType { SPARK, STAR, RING, TRAIL, ASH }

private data class ShockwaveRing(
    val id: Int,
    var progress: Float,
    val color: Color,
    val maxRadius: Float,
    val delay: Float = 0f,
)

// ─────────────────────────────────────────────────────────────────────────────
// PHOENIX EVOLUTION CINEMATIC OVERLAY
// Full-screen 5-phase evolution ceremony
// Phase 0: Old mascot shake  → Phase 1: Explosion burst  →
// Phase 2: Void/black-hole   → Phase 3: New mascot emerges →
// Phase 4: Celebrate + title card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun PhoenixEvolutionCinematic(
    fromStage: PhoenixStageData,
    toStage: PhoenixStageData,
    onComplete: () -> Unit,
) {
    var phase by remember { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    // Particle / shockwave mutable state
    val particles = remember { mutableStateListOf<Particle>() }
    val shockwaves = remember { mutableStateListOf<ShockwaveRing>() }
    var particleIdCounter by remember { mutableIntStateOf(0) }

    // Animatables
    val bgAlpha        = remember { Animatable(0f) }
    val oldScale       = remember { Animatable(1f) }
    val oldAlpha       = remember { Animatable(1f) }
    val oldRotation    = remember { Animatable(0f) }
    val newScale       = remember { Animatable(0f) }
    val newAlpha       = remember { Animatable(0f) }
    val newGlow        = remember { Animatable(0f) }
    val titleAlpha     = remember { Animatable(0f) }
    val titleSlide     = remember { Animatable(80f) }
    val voidRadius     = remember { Animatable(0f) }
    val cosmicSpin     = remember { Animatable(0f) }
    val auraScale      = remember { Animatable(0.3f) }
    val auraAlpha      = remember { Animatable(0f) }

    // Particle physics engine at ~60 fps
    LaunchedEffect(Unit) {
        val dt = 0.016f
        val gravity = 300f
        while (true) {
            delay(16L)
            val iter = particles.iterator()
            while (iter.hasNext()) {
                val p = iter.next()
                p.lifetime -= dt
                if (p.lifetime <= 0f) iter.remove()
            }
            val wIter = shockwaves.iterator()
            while (wIter.hasNext()) {
                val w = wIter.next()
                if (w.delay > 0f) {
                    w.progress -= dt  // still waiting
                } else {
                    w.progress += dt / 1.1f
                    if (w.progress >= 1f) wIter.remove()
                }
            }
        }
    }

    // Infinite cosmic ring spin
    LaunchedEffect(Unit) {
        cosmicSpin.animateTo(
            36000f,
            infiniteRepeatable(tween(30_000, easing = LinearEasing))
        )
    }

    fun spawnBurst(cx: Float, cy: Float, fromClr: Color, toClr: Color, count: Int) {
        repeat(count) { i ->
            val angle = (i.toFloat() / count) * 2f * PI.toFloat() + (Math.random() * 0.3f).toFloat()
            val speed = 180f + (Math.random() * 420f).toFloat()
            val colors = listOf(fromClr, toClr, Color.White, Color(0xFFFFD700), Color(0xFFFF4500))
            val types = ParticleType.entries.toTypedArray()
            val life = 0.7f + (Math.random() * 0.9f).toFloat()
            particles.add(
                Particle(
                    id = particleIdCounter++,
                    startX = cx, startY = cy,
                    velocityX = cos(angle) * speed,
                    velocityY = sin(angle) * speed,
                    size = 4f + (Math.random() * 14f).toFloat(),
                    color = colors[i % colors.size],
                    lifetime = life, maxLifetime = life,
                    angle = (Math.random() * 360f).toFloat(),
                    angularVelocity = (-200f + (Math.random() * 400f)).toFloat(),
                    type = types[i % types.size]
                )
            )
        }
    }

    fun spawnEmergence(cx: Float, cy: Float, clr: Color, count: Int) {
        repeat(count) { i ->
            val angle = (i.toFloat() / count) * 2f * PI.toFloat()
            val speed = 90f + (Math.random() * 220f).toFloat()
            val life = 0.9f + (Math.random() * 0.7f).toFloat()
            particles.add(
                Particle(
                    id = particleIdCounter++,
                    startX = cx, startY = cy,
                    velocityX = cos(angle) * speed,
                    velocityY = sin(angle) * speed,
                    size = 3f + (Math.random() * 9f).toFloat(),
                    color = clr,
                    lifetime = life, maxLifetime = life,
                    angle = 0f, angularVelocity = (Math.random() * 300f - 150f).toFloat(),
                    type = ParticleType.STAR
                )
            )
        }
    }

    fun spawnConfetti(cx: Float, cy: Float, fromClr: Color, toClr: Color) {
        val confettiColors = listOf(
            Color(0xFFFFD700), Color(0xFFFF6B6B), Color(0xFF4ECDC4),
            Color(0xFF45B7D1), toClr, fromClr
        )
        repeat(100) { i ->
            val angle = (Math.random() * 2 * PI).toFloat()
            val speed = 200f + (Math.random() * 380f).toFloat()
            val life = 1.6f + (Math.random() * 1f).toFloat()
            particles.add(
                Particle(
                    id = particleIdCounter++,
                    startX = cx, startY = cy,
                    velocityX = cos(angle) * speed,
                    velocityY = sin(angle) * speed - 220f,
                    size = 5f + (Math.random() * 13f).toFloat(),
                    color = confettiColors[i % confettiColors.size],
                    lifetime = life, maxLifetime = life,
                    angle = (Math.random() * 360f).toFloat(),
                    angularVelocity = (Math.random() * 720f - 360f).toFloat(),
                    type = ParticleType.TRAIL
                )
            )
        }
    }

    // Phase sequencer
    LaunchedEffect(Unit) {
        val cx = 540f; val cy = 960f  // approximate canvas center (will be auto-centered in Canvas)

        // Phase 0: Fade in + shake
        bgAlpha.animateTo(1f, tween(400))
        auraAlpha.animateTo(0.55f, tween(600))
        auraScale.animateTo(1f, spring(stiffness = Spring.StiffnessLow))

        repeat(10) { i ->
            oldRotation.animateTo(if (i % 2 == 0) 14f else -14f,
                spring(dampingRatio = 0.25f, stiffness = Spring.StiffnessMediumLow))
        }
        oldRotation.animateTo(0f, spring())
        oldScale.animateTo(1.25f, tween(220, easing = FastOutSlowInEasing))
        oldScale.animateTo(1f, tween(180))
        delay(100)
        phase = 1

        // Phase 1: Explosion
        spawnBurst(cx, cy, fromStage.auraColor, toStage.auraColor, 90)
        shockwaves.addAll(listOf(
            ShockwaveRing(0, 0f, fromStage.auraColor, 700f, delay = 0f),
            ShockwaveRing(1, 0f, toStage.auraColor, 560f, delay = -0.12f),
            ShockwaveRing(2, 0f, Color.White, 420f, delay = -0.24f),
        ))
        coroutineScope.launch { oldScale.animateTo(2.8f, tween(340, easing = FastOutSlowInEasing)) }
        coroutineScope.launch { oldAlpha.animateTo(0f, tween(440, easing = FastOutSlowInEasing)) }
        delay(500)
        phase = 2

        // Phase 2: Void collapse
        voidRadius.animateTo(280f, spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessLow))
        // Ring particles imploding
        repeat(36) { i ->
            val a = (i.toFloat() / 36f) * 2f * PI.toFloat()
            val life = 1.1f
            particles.add(Particle(
                id = particleIdCounter++,
                startX = cx + cos(a) * 160f, startY = cy + sin(a) * 160f,
                velocityX = -cos(a) * 100f, velocityY = -sin(a) * 100f,
                size = 6f, color = toStage.auraColor,
                lifetime = life, maxLifetime = life,
                angle = 0f, angularVelocity = 100f,
                type = ParticleType.RING
            ))
        }
        delay(700)
        voidRadius.animateTo(0f, tween(320, easing = FastOutSlowInEasing))
        phase = 3

        // Phase 3: Emergence
        spawnEmergence(cx, cy, toStage.auraColor, 65)
        shockwaves.addAll(listOf(
            ShockwaveRing(10, 0f, toStage.auraColor, 600f),
            ShockwaveRing(11, 0f, Color.White, 380f, delay = -0.15f),
        ))
        newGlow.animateTo(1f, tween(200))
        coroutineScope.launch {
            newAlpha.animateTo(1f, tween(320))
        }
        newScale.animateTo(1.5f, spring(dampingRatio = 0.28f, stiffness = Spring.StiffnessMedium))
        newScale.animateTo(1f, spring(dampingRatio = 0.55f))
        auraAlpha.animateTo(0.85f, tween(400))
        auraScale.animateTo(1.25f, spring(stiffness = Spring.StiffnessLow))
        delay(350)
        phase = 4

        // Phase 4: Title + confetti
        titleAlpha.animateTo(1f, tween(420))
        titleSlide.animateTo(0f, spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium))
        spawnConfetti(cx, cy, fromStage.auraColor, toStage.auraColor)

        delay(3200)
        bgAlpha.animateTo(0f, tween(500))
        delay(500)
        onComplete()
    }

    // ── RENDER ────────────────────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = bgAlpha.value },
        contentAlignment = Alignment.Center
    ) {
        // Deep space background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        0.0f to Color(0xFF0D0D1A),
                        0.5f to Color(0xFF12002A),
                        1.0f to Color(0xFF000000),
                    )
                )
        )

        // ── Canvas: particles + shockwaves + cosmic rings + aura ─────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f

            // Backdrop aura glow
            val auraClr = if (phase >= 3) toStage.auraColor else fromStage.auraColor
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(auraClr.copy(0.4f * auraAlpha.value), Color.Transparent),
                    center = Offset(cx, cy),
                    radius = 360f * auraScale.value
                ),
                radius = 360f * auraScale.value,
                center = Offset(cx, cy)
            )

            // Cosmic orbit rings (3 rings, 24 dots each)
            for (r in 0 until 3) {
                val ringR = 170f + r * 85f
                val rotOff = cosmicSpin.value + r * 40f
                for (d in 0 until 24) {
                    val dotA = Math.toRadians(d * 15.0 + rotOff).toFloat()
                    val dotX = cx + cos(dotA) * ringR
                    val dotY = cy + sin(dotA) * ringR
                    val a = 0.18f + 0.12f * sin(d.toFloat()).absoluteValue
                    drawCircle(
                        color = auraClr.copy(a),
                        radius = 3.5f + r * 1.5f,
                        center = Offset(dotX, dotY)
                    )
                }
            }

            // Void black hole (phase 2)
            if (voidRadius.value > 1f) {
                val vr = voidRadius.value
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(Color.Black, Color(0xFF1A0035).copy(0.6f), Color.Transparent),
                        center = Offset(cx, cy), radius = vr
                    ),
                    radius = vr, center = Offset(cx, cy)
                )
                drawCircle(
                    color = Color(0xFF9B59B6).copy(0.9f),
                    radius = vr,
                    center = Offset(cx, cy),
                    style = Stroke(3.5f)
                )
            }

            // Shockwave rings
            for (w in shockwaves) {
                val prog = w.progress.coerceIn(0f, 1f)
                if (prog > 0f) {
                    val r = w.maxRadius * prog
                    val a = (1f - prog) * 0.72f
                    drawCircle(
                        color = w.color.copy(a),
                        radius = r, center = Offset(cx, cy),
                        style = Stroke(4f * (1f - prog) + 1f)
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(w.color.copy(a * 0.15f), Color.Transparent),
                            center = Offset(cx, cy), radius = r
                        ),
                        radius = r, center = Offset(cx, cy)
                    )
                }
            }

            // Particles
            for (p in particles) {
                val t = 1f - (p.lifetime / p.maxLifetime)
                val px = cx + p.velocityX * t * 1.6f
                val py = cy + p.velocityY * t * 1.6f + 0.5f * 380f * t * t
                val la = (p.lifetime / p.maxLifetime).coerceIn(0f, 1f)
                val rot = p.angle + p.angularVelocity * t

                when (p.type) {
                    ParticleType.SPARK ->
                        rotate(rot, Offset(px, py)) {
                            drawRect(
                                color = p.color.copy(la),
                                topLeft = Offset(px - p.size / 2f, py - p.size / 4f),
                                size = androidx.compose.ui.geometry.Size(p.size, p.size / 2f)
                            )
                        }
                    ParticleType.STAR -> {
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(p.color.copy(la), p.color.copy(0f)),
                                Offset(px, py), p.size
                            ),
                            radius = p.size, center = Offset(px, py)
                        )
                        drawLine(p.color.copy(la * 0.6f), Offset(px - p.size * 1.6f, py), Offset(px + p.size * 1.6f, py), 1.5f)
                        drawLine(p.color.copy(la * 0.6f), Offset(px, py - p.size * 1.6f), Offset(px, py + p.size * 1.6f), 1.5f)
                    }
                    ParticleType.RING ->
                        drawCircle(p.color.copy(la * 0.75f), p.size, Offset(px, py), style = Stroke(2f))
                    ParticleType.TRAIL ->
                        rotate(rot, Offset(px, py)) {
                            drawRect(
                                brush = Brush.linearGradient(
                                    listOf(p.color.copy(la), p.color.copy(0f)),
                                    Offset(px - p.size * 2.5f, py), Offset(px + p.size * 2.5f, py)
                                ),
                                topLeft = Offset(px - p.size * 2.5f, py - p.size * 0.35f),
                                size = androidx.compose.ui.geometry.Size(p.size * 5f, p.size * 0.7f)
                            )
                        }
                    ParticleType.ASH ->
                        drawCircle(p.color.copy(la * 0.45f), p.size * 0.55f, Offset(px, py))
                }
            }
        }

        // Old mascot (phases 0–1)
        AnimatedVisibility(
            visible = phase <= 1,
            exit = fadeOut(tween(200))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(270.dp)
                        .blur(35.dp)
                        .background(fromStage.auraColor.copy(0.4f), CircleShape)
                )
                Image(
                    painter = painterResource(fromStage.drawableResId),
                    contentDescription = fromStage.name,
                    modifier = Modifier
                        .size(190.dp)
                        .scale(oldScale.value)
                        .graphicsLayer {
                            alpha = oldAlpha.value
                            rotationZ = oldRotation.value
                        },
                    contentScale = ContentScale.Fit
                )
            }
        }

        // New mascot (phases 3–4) with living procedural Phoenix Creature
        AnimatedVisibility(
            visible = phase >= 3,
            enter = fadeIn(tween(300)) + scaleIn(initialScale = 0.4f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(320.dp)
                        .blur(45.dp)
                        .background(toStage.auraColor.copy(0.55f * newGlow.value), CircleShape)
                )
                PhoenixCreature(
                    stage = toStage.id.toFloat(),
                    ascendant = toStage.id >= 18,
                    modifier = Modifier
                        .size(240.dp)
                        .scale(newScale.value)
                        .graphicsLayer { alpha = newAlpha.value }
                )
            }
        }

        // Phase 4 title card
        AnimatedVisibility(
            visible = phase >= 4,
            enter = fadeIn(tween(400)) + slideInVertically { it / 2 },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 72.dp)
        ) {
            Column(
                modifier = Modifier
                    .graphicsLayer {
                        alpha = titleAlpha.value
                        translationY = titleSlide.value
                    }
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "✨  EVOLUTION COMPLETE  ✨",
                    color = Color(0xFFFFD700),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                Text(
                    toStage.name,
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
                Text(
                    toStage.subtitle,
                    color = toStage.auraColor,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(toStage.auraColor)
                        .padding(horizontal = 28.dp, vertical = 10.dp)
                ) {
                    Text(
                        "\"${toStage.quote}\"",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        // Phase pill
        AnimatedVisibility(
            visible = phase in 1..2,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(0.75f))
                    .border(1.dp, if (phase == 1) fromStage.auraColor else toStage.auraColor, RoundedCornerShape(20.dp))
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    if (phase == 1) "⚡  TRANSFORMING..." else "🌀  VOID COLLAPSE...",
                    color = if (phase == 1) fromStage.auraColor else toStage.auraColor,
                    fontSize = 12.sp, fontWeight = FontWeight.Black, letterSpacing = 1.5.sp
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PHOENIX STAGE DETAIL – UPGRADED TURNTABLE with 3D physics + aura canvas
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun PhoenixStageDetailOverlay(
    stage: PhoenixStageData,
    isEquipped: Boolean,
    onDismiss: () -> Unit,
    onEquip: () -> Unit,
    onEvolve: (() -> Unit)? = null,
) {
    val coroutineScope = rememberCoroutineScope()

    var rotationAngle by remember { mutableFloatStateOf(0f) }
    var tiltAngle by remember { mutableFloatStateOf(0f) }
    val bounceScale = remember { Animatable(1f) }
    val panelScale  = remember { Animatable(0.82f) }
    val panelAlpha  = remember { Animatable(0f) }
    val orbitalAngle = remember { Animatable(0f) }
    val auraPulse  = remember { Animatable(0.85f) }
    val auraAlpha  = remember { Animatable(0.3f) }

    var isLiveProcedural by remember { mutableStateOf(true) }
    var sparkIndex by remember { mutableIntStateOf(0) }
    val sparks = listOf("🔥", "✨", "🌟", "💫", "⚡️", "🌙", "💥")

    LaunchedEffect(Unit) {
        // Entry spring
        panelAlpha.animateTo(1f, tween(280))
        panelScale.animateTo(1f, spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium))

        // Orbit
        coroutineScope.launch {
            orbitalAngle.animateTo(36000f, infiniteRepeatable(tween(8000, easing = LinearEasing)))
        }
        // Aura breathe
        coroutineScope.launch {
            while (true) {
                auraPulse.animateTo(1.2f, tween(1300, easing = EaseInOutSine))
                auraAlpha.animateTo(0.6f, tween(1300))
                auraPulse.animateTo(0.8f, tween(1300, easing = EaseInOutSine))
                auraAlpha.animateTo(0.22f, tween(1300))
            }
        }
        // Spark cycle
        while (true) {
            delay(680)
            sparkIndex = (sparkIndex + 1) % sparks.size
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(0.78f))
            .statusBarsPadding()
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.93f)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF1A1A2E), Color(0xFF16213E), Color(0xFF0F3460))
                    )
                )
                .border(
                    1.5.dp,
                    Brush.linearGradient(listOf(stage.auraColor.copy(0.85f), stage.auraColor.copy(0.2f))),
                    RoundedCornerShape(28.dp)
                )
                .scale(panelScale.value)
                .graphicsLayer { alpha = panelAlpha.value }
                .clickable(enabled = false) {}
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        Modifier.clip(RoundedCornerShape(8.dp)).background(stage.auraColor)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) { Text("STAGE ${stage.id} OF 18", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black) }
                    Box(
                        Modifier.clip(RoundedCornerShape(8.dp)).background(stage.auraColor.copy(0.22f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) { Text(stage.element, color = stage.auraColor, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                }
                IconButton(onDismiss, Modifier.size(28.dp)) {
                    Text("✕", color = Color.White.copy(0.6f), fontSize = 16.sp)
                }
            }

            // 3D Turntable
            Box(
                modifier = Modifier
                    .size(245.dp)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { _, drag ->
                                rotationAngle += drag.x * 0.55f
                                tiltAngle = (tiltAngle + drag.y * 0.32f).coerceIn(-35f, 35f)
                            },
                            onDragEnd = {
                                coroutineScope.launch {
                                    val snapR = rotationAngle
                                    rotationAngle = 0f
                                    val ra = Animatable(snapR)
                                    ra.animateTo(0f, spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow))
                                    rotationAngle = ra.value
                                }
                                coroutineScope.launch {
                                    val snapT = tiltAngle
                                    tiltAngle = 0f
                                    val ta = Animatable(snapT)
                                    ta.animateTo(0f, spring(dampingRatio = 0.6f))
                                    tiltAngle = ta.value
                                }
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        detectTapGestures {
                            coroutineScope.launch {
                                bounceScale.animateTo(1.28f, spring(0.3f, Spring.StiffnessMedium))
                                delay(80)
                                bounceScale.animateTo(1f, spring(0.5f))
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                // Canvas: aura + orbital dots with tail
                Canvas(Modifier.fillMaxSize()) {
                    val cx = size.width / 2f; val cy = size.height / 2f
                    val baseR = 98f * auraPulse.value

                    // Layered aura rings
                    for (i in 4 downTo 0) {
                        val r = baseR + i * 22f
                        val a = auraAlpha.value * (1f - i * 0.18f)
                        drawCircle(
                            Brush.radialGradient(
                                listOf(stage.auraColor.copy(a), Color.Transparent),
                                Offset(cx, cy), r
                            ), r, Offset(cx, cy)
                        )
                    }
                    // Orbit outline
                    drawCircle(stage.auraColor.copy(0.28f), 108f, Offset(cx, cy), style = Stroke(1.5f))
                    // Orbiting dot + 14-step comet tail
                    val orbitA = Math.toRadians(orbitalAngle.value.toDouble())
                    for (t in 0 until 14) {
                        val ta = Math.toRadians(orbitalAngle.value.toDouble() - t * 8)
                        val tx = cx + cos(ta).toFloat() * 108f
                        val ty = cy + sin(ta).toFloat() * 108f
                        drawCircle(stage.auraColor.copy((1f - t / 14f) * 0.65f), 5.5f - t * 0.35f, Offset(tx, ty))
                    }
                    val dotX = cx + cos(orbitA).toFloat() * 108f
                    val dotY = cy + sin(orbitA).toFloat() * 108f
                    drawCircle(stage.auraColor, 7.5f, Offset(dotX, dotY))
                    // Shadow
                    drawOval(stage.auraColor.copy(0.28f),
                        Offset(cx - 72f, cy + 82f),
                        androidx.compose.ui.geometry.Size(144f, 22f))
                }

                // Emoji spark on orbit
                Text(
                    sparks[sparkIndex], fontSize = 20.sp,
                    modifier = Modifier.offset(
                        x = (cos(Math.toRadians(orbitalAngle.value.toDouble() + 180)) * 108).dp,
                        y = (sin(Math.toRadians(orbitalAngle.value.toDouble() + 180)) * 108).dp
                    )
                )

                // Mascot with 3D perspective: Live Procedural Canvas Creature or Artwork
                if (isLiveProcedural) {
                    PhoenixCreature(
                        stage = stage.id.toFloat(),
                        ascendant = stage.id >= 18,
                        modifier = Modifier
                            .size(190.dp)
                            .scale(bounceScale.value)
                            .graphicsLayer {
                                rotationY = rotationAngle
                                rotationX = -tiltAngle
                                cameraDistance = 12f * density
                            }
                    )
                } else {
                    Image(
                        painterResource(stage.drawableResId), stage.name,
                        modifier = Modifier
                            .size(180.dp)
                            .scale(bounceScale.value)
                            .graphicsLayer {
                                rotationY = rotationAngle
                                rotationX = -tiltAngle
                                cameraDistance = 12f * density
                            },
                        contentScale = ContentScale.Fit
                    )
                }
            }

            // Mode Selector Pill
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isLiveProcedural) stage.auraColor else Color.White.copy(0.12f))
                        .clickable { isLiveProcedural = true }
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Text(
                        "✨ LIVE ANIMATION",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (!isLiveProcedural) stage.auraColor else Color.White.copy(0.12f))
                        .clickable { isLiveProcedural = false }
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Text(
                        "🎨 ILLUSTRATION",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Text(
                "Drag to orbit in 3D  •  Tap to celebrate",
                color = Color.White.copy(0.4f), fontSize = 10.sp, fontWeight = FontWeight.Bold
            )

            // Lore
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(stage.name, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                Text(stage.subtitle, color = Color.White.copy(0.58f), fontSize = 13.sp, textAlign = TextAlign.Center)
                Text("\"${stage.quote}\"", color = stage.auraColor, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(top = 4.dp))
            }

            // Action buttons
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isEquipped)
                                Brush.horizontalGradient(listOf(Color(0xFF58CC02), Color(0xFF2DA300)))
                            else
                                Brush.horizontalGradient(listOf(stage.auraColor, stage.auraColor.copy(0.72f)))
                        )
                        .clickable(onClick = onEquip)
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(if (isEquipped) "✓" else "🔥", fontSize = 15.sp)
                        Text(if (isEquipped) "EQUIPPED" else "EQUIP", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
                    }
                }

                if (onEvolve != null) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Brush.horizontalGradient(listOf(Color(0xFF7B2FBE), Color(0xFFE040FB))))
                            .clickable(onClick = onEvolve)
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("⚡", fontSize = 15.sp)
                            Text("EVOLVE →", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
    }
}

private val EaseInOutSine = CubicBezierEasing(0.37f, 0f, 0.63f, 1f)
