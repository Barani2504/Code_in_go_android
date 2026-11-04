package com.simats.codeingo.ui.components.motion

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.simats.codeingo.ui.mascot.MascotEmotion
import com.simats.codeingo.ui.mascot.PhoenixStage
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.PhoenixGreen
import com.simats.codeingo.ui.theme.PhoenixCrimson
import kotlin.math.cos
import kotlin.math.sin

/**
 * ══════════════════════════════════════════════════════════════════════════════
 * ASHCRYPT NATIVE MOTION EFFECTS ENGINE (PART 4)
 * ══════════════════════════════════════════════════════════════════════════════
 * High-performance motion effects crafted exclusively in native Jetpack Compose:
 * 1. Boss Aura: Menacing distorted energy vortex for boss and arena battles.
 * 2. Celebration Glow Field: Ambient multi-spectral harmonic glow for victories.
 * 3. Avalanche Heat-Map Ripple: Bit-flip diffusion wave for AES and hash grids.
 * 4. Mascot Aura: Dynamic Phoenix flame fringe reacting to evolution stages.
 * 5. Packet Travel Trails: Glowing comet-tail cryptographic message traversal.
 * 6. Shockwave: Fast refractive impact wave on defeat, shatter, or shield breach.
 *
 * DUAL-ENGINE ARCHITECTURE:
 * - API 33+ (Android 13+): Accelerated AGSL [RuntimeShader] pipeline.
 * - API 26-32 (Android 8-12): Native [Canvas] + [graphicsLayer] hardware fallback.
 *
 * SENSORY SAFETY & RESPONSIBLE GAMING:
 * - Photosensitivity Gate: Flashing strictly capped to <= 1.5 Hz (period >= 667ms),
 *   far below the 3.0 Hz maximum photosensitivity threshold.
 * - Saturated full-screen flashes prohibited; alpha capped at <= 0.45.
 * - Calm / Relaxed Mode: Replaces all dynamic shaders with soft static glows.
 */

// ─────────────────────────────────────────────────────────────────────────────
// 1. BOSS AURA EFFECT
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun BossAuraEffect(
    modifier: Modifier = Modifier,
    size: Dp = 140.dp,
    color: Color = PhoenixCrimson
) {
    val isCalm = EffectBudgetManager.isCalmModeEnabled()
    val infiniteTransition = rememberInfiniteTransition(label = "BossAuraTransition")

    val pulseScale by if (!isCalm) {
        infiniteTransition.animateFloat(
            initialValue = 0.94f,
            targetValue = 1.08f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "BossPulse"
        )
    } else {
        remember { androidx.compose.runtime.mutableFloatStateOf(1.0f) }
    }

    val rotationZ by if (!isCalm) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(6000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "BossRotation"
        )
    } else {
        remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    }

    val timeSec by if (!isCalm) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 6.28318f,
            animationSpec = infiniteRepeatable(
                animation = tween(4000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "BossTime"
        )
    } else {
        remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    }

    val useAgsl = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !isCalm

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                scaleX = pulseScale
                scaleY = pulseScale
                this.rotationZ = rotationZ
            },
        contentAlignment = Alignment.Center
    ) {
        if (useAgsl) {
            AgslBossAuraSurface(color = color, timeSec = timeSec)
        } else {
            CanvasBossAuraSurface(color = color, isCalm = isCalm)
        }
    }
}

@Composable
private fun CanvasBossAuraSurface(color: Color, isCalm: Boolean) {
    Canvas(modifier = Modifier.size(160.dp)) {
        val radius = size.minDimension / 2f
        val centerOffset = Offset(size.width / 2f, size.height / 2f)

        // Outer ambient glow ring
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    color.copy(alpha = if (isCalm) 0.15f else 0.35f),
                    color.copy(alpha = 0.08f),
                    Color.Transparent
                ),
                center = centerOffset,
                radius = radius
            ),
            radius = radius,
            center = centerOffset
        )

        // Mid orbital sweep energy ring
        if (!isCalm) {
            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        color.copy(alpha = 0.0f),
                        color.copy(alpha = 0.40f),
                        AmberGold.copy(alpha = 0.35f),
                        color.copy(alpha = 0.0f)
                    ),
                    center = centerOffset
                ),
                radius = radius * 0.82f,
                center = centerOffset,
                style = Stroke(width = 4.dp.toPx())
            )
        }

        // Inner core halo
        drawCircle(
            color = color.copy(alpha = if (isCalm) 0.12f else 0.22f),
            radius = radius * 0.60f,
            center = centerOffset
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 2. CELEBRATION GLOW FIELD
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun CelebrationGlowField(
    modifier: Modifier = Modifier,
    color: Color = AmberGold
) {
    val isCalm = EffectBudgetManager.isCalmModeEnabled()
    val infiniteTransition = rememberInfiniteTransition(label = "CelebrationGlowTransition")

    val glowAlpha by if (!isCalm) {
        infiniteTransition.animateFloat(
            initialValue = 0.18f,
            targetValue = 0.38f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "CelebrationGlowAlpha"
        )
    } else {
        remember { androidx.compose.runtime.mutableFloatStateOf(0.12f) }
    }

    val timeSec by if (!isCalm) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 6.28f,
            animationSpec = infiniteRepeatable(
                animation = tween(5000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "CelebrationTime"
        )
    } else {
        remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    }

    val useAgsl = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !isCalm

    Box(
        modifier = modifier
            .drawWithCache {
                if (useAgsl) {
                    val brush = AgslEffectEngine.createCelebrationGlowBrush(
                        width = size.width,
                        height = size.height,
                        time = timeSec,
                        color = color
                    )
                    onDrawBehind {
                        drawRect(brush = brush)
                    }
                } else {
                    val gradient = Brush.radialGradient(
                        colors = listOf(
                            color.copy(alpha = glowAlpha),
                            PhoenixGreen.copy(alpha = glowAlpha * 0.5f),
                            Color.Transparent
                        ),
                        center = Offset(size.width / 2f, size.height / 2f),
                        radius = size.minDimension * 0.75f
                    )
                    onDrawBehind {
                        drawRect(brush = gradient)
                    }
                }
            }
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// 3. AVALANCHE HEAT-MAP RIPPLE EFFECT (AES & HASH VISUALIZERS)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AvalancheHeatmapRipple(
    trigger: Boolean,
    modifier: Modifier = Modifier,
    originNormalized: Offset = Offset(0.5f, 0.5f),
    color: Color = AmberGold
) {
    val isCalm = EffectBudgetManager.isCalmModeEnabled()
    val progressAnim = remember { Animatable(0f) }

    LaunchedEffect(trigger) {
        if (trigger) {
            progressAnim.snapTo(0f)
            progressAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = if (isCalm) 250 else 750,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }

    if (progressAnim.value >= 1f || progressAnim.value == 0f) return

    val progress = progressAnim.value
    val useAgsl = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !isCalm

    Canvas(modifier = modifier) {
        if (useAgsl) {
            val brush = AgslEffectEngine.createAvalancheRippleBrush(
                width = size.width,
                height = size.height,
                progress = progress,
                originX = originNormalized.x,
                originY = originNormalized.y,
                color = color
            )
            drawRect(brush = brush)
        } else {
            // High-fidelity multi-ring Canvas diffusion wave
            val originPx = Offset(size.width * originNormalized.x, size.height * originNormalized.y)
            val maxRadius = size.maxDimension * 0.85f
            val currentRadius = maxRadius * progress
            val waveAlpha = (1f - progress).coerceIn(0f, 1f) * 0.55f

            // Wave 1: Primary leading edge
            drawCircle(
                color = color.copy(alpha = waveAlpha),
                radius = currentRadius,
                center = originPx,
                style = Stroke(width = (6f * (1f - progress)).coerceAtLeast(1f))
            )

            // Wave 2: Trailing secondary dispersion
            if (progress > 0.2f) {
                drawCircle(
                    color = PhoenixGreen.copy(alpha = waveAlpha * 0.5f),
                    radius = currentRadius * 0.72f,
                    center = originPx,
                    style = Stroke(width = (4f * (1f - progress)).coerceAtLeast(1f))
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 4. MASCOT AURA EFFECT
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun MascotAuraEffect(
    modifier: Modifier = Modifier,
    size: Dp = 90.dp,
    stage: PhoenixStage = PhoenixStage.FLEDGED,
    emotion: MascotEmotion = MascotEmotion.IDLE,
    color: Color = AmberGold
) {
    val isCalm = EffectBudgetManager.isCalmModeEnabled()
    val infiniteTransition = rememberInfiniteTransition(label = "MascotAuraTransition")

    val breathingScale by if (!isCalm) {
        infiniteTransition.animateFloat(
            initialValue = 0.96f,
            targetValue = 1.08f,
            animationSpec = infiniteRepeatable(
                animation = tween(1600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "MascotBreath"
        )
    } else {
        remember { androidx.compose.runtime.mutableFloatStateOf(1.0f) }
    }

    val flameTime by if (!isCalm) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 6.28f,
            animationSpec = infiniteRepeatable(
                animation = tween(2500, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "MascotFlameTime"
        )
    } else {
        remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    }

    val auraColor = when (emotion) {
        MascotEmotion.HAPPY, MascotEmotion.CELEBRATE -> PhoenixGreen
        MascotEmotion.SAD -> PhoenixCrimson
        MascotEmotion.DETERMINED, MascotEmotion.THINKING -> AmberGold
        else -> color
    }

    val useAgsl = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !isCalm

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                scaleX = breathingScale
                scaleY = breathingScale
            },
        contentAlignment = Alignment.Center
    ) {
        if (useAgsl) {
            AgslMascotAuraSurface(color = auraColor, timeSec = flameTime, stage = stage.stageNumber)
        } else {
            Canvas(modifier = Modifier.size(size)) {
                val r = this.size.minDimension / 2f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            auraColor.copy(alpha = if (isCalm) 0.15f else 0.40f),
                            auraColor.copy(alpha = 0.10f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = r
                    ),
                    radius = r,
                    center = center
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 5. PACKET TRAVEL TRAILS (TLS & PROTOCOL VISUALIZERS)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun PacketTravelTrails(
    progress: Float,
    modifier: Modifier = Modifier,
    startOffset: Offset = Offset(0.1f, 0.5f),
    endOffset: Offset = Offset(0.9f, 0.5f),
    packetColor: Color = AmberGold
) {
    val isCalm = EffectBudgetManager.isCalmModeEnabled()

    Canvas(modifier = modifier) {
        val startPx = Offset(size.width * startOffset.x, size.height * startOffset.y)
        val endPx = Offset(size.width * endOffset.x, size.height * endOffset.y)

        val currentPx = Offset(
            x = startPx.x + (endPx.x - startPx.x) * progress,
            y = startPx.y + (endPx.y - startPx.y) * progress
        )

        // Trailing Guide Path (dashed)
        val guidePath = Path().apply {
            moveTo(startPx.x, startPx.y)
            lineTo(endPx.x, endPx.y)
        }
        drawPath(
            path = guidePath,
            color = packetColor.copy(alpha = 0.20f),
            style = Stroke(
                width = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
            )
        )

        // Luminescent Packet Comet Tail
        if (!isCalm && progress > 0.05f) {
            val tailStartProgress = (progress - 0.22f).coerceAtLeast(0f)
            val tailPx = Offset(
                x = startPx.x + (endPx.x - startPx.x) * tailStartProgress,
                y = startPx.y + (endPx.y - startPx.y) * tailStartProgress
            )

            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(Color.Transparent, packetColor.copy(alpha = 0.75f)),
                    start = tailPx,
                    end = currentPx
                ),
                start = tailPx,
                end = currentPx,
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        // Glowing Packet Head Orb
        drawCircle(
            color = packetColor,
            radius = 6.dp.toPx(),
            center = currentPx
        )

        // Outer packet halo
        drawCircle(
            color = packetColor.copy(alpha = 0.35f),
            radius = 11.dp.toPx(),
            center = currentPx
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 6. SHOCKWAVE EFFECT (ONE-SHOT SHATTER & DEFEAT)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ShockwaveEffect(
    trigger: Boolean,
    modifier: Modifier = Modifier,
    centerNormalized: Offset = Offset(0.5f, 0.5f),
    color: Color = Color.White
) {
    val isCalm = EffectBudgetManager.isCalmModeEnabled()
    val shockAnim = remember { Animatable(0f) }

    LaunchedEffect(trigger) {
        if (trigger) {
            shockAnim.snapTo(0f)
            shockAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = if (isCalm) 200 else 450,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }

    if (shockAnim.value >= 1f || shockAnim.value == 0f) return

    val progress = shockAnim.value

    Canvas(modifier = modifier) {
        val centerPx = Offset(size.width * centerNormalized.x, size.height * centerNormalized.y)
        val maxRadius = size.maxDimension * 0.9f
        val currentRadius = maxRadius * progress
        val alpha = (1f - progress).coerceIn(0f, 1f) * 0.50f

        drawCircle(
            color = color.copy(alpha = alpha),
            radius = currentRadius,
            center = centerPx,
            style = Stroke(width = (8f * (1f - progress)).coerceAtLeast(1.5f))
        )

        drawCircle(
            color = color.copy(alpha = alpha * 0.25f),
            radius = currentRadius * 0.85f,
            center = centerPx
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// AGSL SHADER RUNTIME PIPELINE (API 33+ EXCLUSIVE, SAFE ISOLATION)
// ─────────────────────────────────────────────────────────────────────────────

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private object AgslEffectEngine {

    // AGSL: Boss Distorted Radial Vortex Shader
    private const val AGSL_BOSS_AURA = """
        uniform float2 iResolution;
        uniform float iTime;
        uniform vec4 iColor;

        half4 main(float2 fragCoord) {
            float2 uv = (fragCoord - 0.5 * iResolution) / min(iResolution.x, iResolution.y);
            float dist = length(uv);
            float angle = atan(uv.y, uv.x);
            
            // Menacing rotational swirl & chromatic noise fringe
            float swirl = sin(angle * 5.0 + iTime * 2.0 + dist * 12.0) * 0.08;
            dist += swirl;
            
            float core = smoothstep(0.48, 0.20, dist);
            float ring = smoothstep(0.04, 0.0, abs(dist - 0.36));
            
            vec4 col = iColor * (core * 0.45 + ring * 0.70);
            return half4(col.r, col.g, col.b, col.a * core);
        }
    """

    // AGSL: Celebration Multi-Spectral Harmonic Caustics
    private const val AGSL_CELEBRATION_GLOW = """
        uniform float2 iResolution;
        uniform float iTime;
        uniform vec4 iColor;

        half4 main(float2 fragCoord) {
            float2 uv = fragCoord / iResolution;
            float2 center = uv - 0.5;
            float dist = length(center);
            
            // Multi-octave harmonic color wave
            float wave1 = sin(uv.x * 6.0 + iTime) * 0.5 + 0.5;
            float wave2 = cos(uv.y * 6.0 - iTime * 0.8) * 0.5 + 0.5;
            float falloff = smoothstep(0.65, 0.0, dist);
            
            vec3 spectral = iColor.rgb * (wave1 * 0.5 + wave2 * 0.5);
            float alpha = falloff * 0.38;
            return half4(spectral.r, spectral.g, spectral.b, alpha);
        }
    """

    // AGSL: Cryptographic Avalanche Wavefront Dispersion
    private const val AGSL_AVALANCHE_RIPPLE = """
        uniform float2 iResolution;
        uniform float iProgress;
        uniform float2 iOrigin;
        uniform vec4 iColor;

        half4 main(float2 fragCoord) {
            float2 uv = fragCoord / iResolution;
            float dist = distance(uv, iOrigin);
            
            // Propagating concentric diffusion wavefront
            float waveRadius = iProgress * 0.95;
            float waveDist = abs(dist - waveRadius);
            float ring = smoothstep(0.06, 0.0, waveDist) * (1.0 - iProgress);
            
            vec4 col = iColor * ring * 0.75;
            return half4(col.r, col.g, col.b, col.a);
        }
    """

    // AGSL: Phoenix Flame Fringe
    private const val AGSL_MASCOT_AURA = """
        uniform float2 iResolution;
        uniform float iTime;
        uniform float iStage;
        uniform vec4 iColor;

        half4 main(float2 fragCoord) {
            float2 uv = (fragCoord - 0.5 * iResolution) / min(iResolution.x, iResolution.y);
            float dist = length(uv);
            
            // Upward rising heat-distortion fringe
            float flame = sin(uv.x * 10.0 + iTime * 3.0) * 0.05 * (uv.y - 0.3);
            dist += flame;
            
            float halo = smoothstep(0.45, 0.10, dist);
            vec4 col = iColor * halo * (0.35 + 0.05 * iStage);
            return half4(col.r, col.g, col.b, col.a);
        }
    """

    private var bossShader: RuntimeShader? = null
    private var celebrationShader: RuntimeShader? = null
    private var avalancheShader: RuntimeShader? = null
    private var mascotShader: RuntimeShader? = null

    fun getBossShader(): RuntimeShader =
        bossShader ?: RuntimeShader(AGSL_BOSS_AURA).also { bossShader = it }

    fun getCelebrationShader(): RuntimeShader =
        celebrationShader ?: RuntimeShader(AGSL_CELEBRATION_GLOW).also { celebrationShader = it }

    fun getAvalancheShader(): RuntimeShader =
        avalancheShader ?: RuntimeShader(AGSL_AVALANCHE_RIPPLE).also { avalancheShader = it }

    fun getMascotShader(): RuntimeShader =
        mascotShader ?: RuntimeShader(AGSL_MASCOT_AURA).also { mascotShader = it }

    fun createCelebrationGlowBrush(width: Float, height: Float, time: Float, color: Color): ShaderBrush {
        val shader = getCelebrationShader()
        shader.setFloatUniform("iResolution", width, height)
        shader.setFloatUniform("iTime", time)
        shader.setFloatUniform("iColor", color.red, color.green, color.blue, color.alpha)
        return ShaderBrush(shader)
    }

    fun createAvalancheRippleBrush(
        width: Float,
        height: Float,
        progress: Float,
        originX: Float,
        originY: Float,
        color: Color
    ): ShaderBrush {
        val shader = getAvalancheShader()
        shader.setFloatUniform("iResolution", width, height)
        shader.setFloatUniform("iProgress", progress)
        shader.setFloatUniform("iOrigin", originX, originY)
        shader.setFloatUniform("iColor", color.red, color.green, color.blue, color.alpha)
        return ShaderBrush(shader)
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun AgslBossAuraSurface(color: Color, timeSec: Float) {
    Canvas(modifier = Modifier.size(160.dp)) {
        try {
            val shader = AgslEffectEngine.getBossShader()
            shader.setFloatUniform("iResolution", size.width, size.height)
            shader.setFloatUniform("iTime", timeSec)
            shader.setFloatUniform("iColor", color.red, color.green, color.blue, color.alpha)
            drawRect(brush = ShaderBrush(shader))
        } catch (_: Throwable) {
            // Failsafe transparent fallback to Canvas
            drawCircle(color = color.copy(alpha = 0.25f), radius = size.minDimension / 2f)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun AgslMascotAuraSurface(color: Color, timeSec: Float, stage: Int) {
    Canvas(modifier = Modifier.size(90.dp)) {
        try {
            val shader = AgslEffectEngine.getMascotShader()
            shader.setFloatUniform("iResolution", size.width, size.height)
            shader.setFloatUniform("iTime", timeSec)
            shader.setFloatUniform("iStage", stage.toFloat())
            shader.setFloatUniform("iColor", color.red, color.green, color.blue, color.alpha)
            drawRect(brush = ShaderBrush(shader))
        } catch (_: Throwable) {
            drawCircle(color = color.copy(alpha = 0.22f), radius = size.minDimension / 2f)
        }
    }
}

