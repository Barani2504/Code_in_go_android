package com.simats.codeingo.ui.components.motion

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * In-House Native Compose Particle Shatter System.
 *
 * Clean, zero-external-dependency replacement for the legacy JitPack ExplodingComposable.
 * Generates an explosive burst of geometric particles with gravity, rotational spin,
 * and lifetime fade.
 *
 * Used for:
 * - Wrong token shatter in Fill-in-Code
 * - Deprecated algorithm card disintegration (MD5 / DES)
 * - Boss shield barrier fracture
 *
 * Sensory Safety & Budget:
 * - Strictly capped to max 2 concurrent instances via [EffectBudgetManager].
 * - Bypassed in Calm Mode (replaces shatter with an accessible soft fade).
 */
data class ShatterShard(
    val id: Int,
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val size: Float,
    val color: Color,
    var rotation: Float,
    val rotSpeed: Float
)

@Composable
fun ExplodeHost(
    trigger: Boolean,
    origin: Offset,
    colors: List<Color> = listOf(Color(0xFFE82410), Color(0xFFFF5712), Color(0xFFFFC800)),
    shardCount: Int = 32,
    onComplete: () -> Unit = {}
) {
    if (!trigger) return

    val isCalm = isCalmModeActive()
    if (isCalm) {
        // Calm mode: instant safe completion without particles
        LaunchedEffect(Unit) { onComplete() }
        return
    }

    val canExplode = remember { EffectBudgetManager.acquireExplosion() }
    if (!canExplode) {
        LaunchedEffect(Unit) { onComplete() }
        return
    }

    val shards = remember {
        mutableStateListOf<ShatterShard>().apply {
            val count = shardCount.coerceIn(16, 40)
            repeat(count) { id ->
                val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
                val speed = Random.nextFloat() * 12f + 4f
                add(
                    ShatterShard(
                        id = id,
                        x = origin.x,
                        y = origin.y,
                        vx = cos(angle) * speed,
                        vy = sin(angle) * speed - 4f, // upward blast impulse
                        size = Random.nextFloat() * 8f + 4f,
                        color = colors[id % colors.size],
                        rotation = Random.nextFloat() * 360f,
                        rotSpeed = (Random.nextFloat() - 0.5f) * 24f
                    )
                )
            }
        }
    }

    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        try {
            progress.animateTo(1f, tween(650, easing = LinearEasing))
        } finally {
            EffectBudgetManager.releaseExplosion()
            onComplete()
        }
    }

    val currentAlpha = (1f - progress.value).coerceIn(0f, 1f)

    Canvas(modifier = Modifier.fillMaxSize()) {
        val dt = 1f
        for (shard in shards) {
            shard.x += shard.vx * dt
            shard.y += shard.vy * dt
            shard.vy += 0.45f * dt // gravity pull
            shard.rotation += shard.rotSpeed

            rotate(shard.rotation, pivot = Offset(shard.x, shard.y)) {
                drawRect(
                    color = shard.color.copy(alpha = currentAlpha),
                    topLeft = Offset(shard.x - shard.size / 2, shard.y - shard.size / 2),
                    size = Size(shard.size, shard.size)
                )
            }
        }
    }
}

