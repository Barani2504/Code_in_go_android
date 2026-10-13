package com.simats.codeingo.ui.phoenix

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.simats.codeingo.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

val NodeFlyFrames = intArrayOf(
    R.drawable.node_fly_0, R.drawable.node_fly_1, R.drawable.node_fly_2, R.drawable.node_fly_3,
    R.drawable.node_fly_4, R.drawable.node_fly_5, R.drawable.node_fly_6, R.drawable.node_fly_7,
    R.drawable.node_fly_8, R.drawable.node_fly_9, R.drawable.node_fly_10, R.drawable.node_fly_11,
    R.drawable.node_fly_12, R.drawable.node_fly_13, R.drawable.node_fly_14, R.drawable.node_fly_15,
    R.drawable.node_fly_16, R.drawable.node_fly_17, R.drawable.node_fly_18, R.drawable.node_fly_19,
    R.drawable.node_fly_20, R.drawable.node_fly_21, R.drawable.node_fly_22, R.drawable.node_fly_23
)

val WaveFrames = intArrayOf(
    R.drawable.wave_0, R.drawable.wave_1, R.drawable.wave_2, R.drawable.wave_3,
    R.drawable.wave_4, R.drawable.wave_5, R.drawable.wave_6, R.drawable.wave_7,
    R.drawable.wave_8, R.drawable.wave_9, R.drawable.wave_10, R.drawable.wave_11,
    R.drawable.wave_12, R.drawable.wave_13, R.drawable.wave_14, R.drawable.wave_15,
    R.drawable.wave_16, R.drawable.wave_17, R.drawable.wave_18, R.drawable.wave_19,
    R.drawable.wave_20, R.drawable.wave_21, R.drawable.wave_22, R.drawable.wave_23
)

val FlyFlapFrames = intArrayOf(
    R.drawable.fly_0, R.drawable.fly_1, R.drawable.fly_2, R.drawable.fly_3,
    R.drawable.fly_4, R.drawable.fly_5, R.drawable.fly_6, R.drawable.fly_7,
    R.drawable.fly_8, R.drawable.fly_9, R.drawable.fly_10, R.drawable.fly_11,
    R.drawable.fly_12, R.drawable.fly_13, R.drawable.fly_14, R.drawable.fly_15
)

/**
 * AnimatedGIFView — Jetpack Compose equivalent of iOS AnimatedGIFView.swift.
 * Faithfully mirrors iOS AnimatedGIFView (commit d3b5daf):
 * - Priority 1A: 24-frame PNG sequence for `phoenix_flying_nodes` (100% transparent 3D model, zero background)
 * - Priority 1B: 24-frame PNG sequence for `phoenix_waving` (100% transparent, zero background)
 * - Priority 1C: 16-frame PNG sequence for `phoenix_flying`
 * - Fallback: Coil animated GIF decoder for R.raw assets
 */
@Composable
fun AnimatedGIFView(
    resourceName: String = "phoenix_flying",
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    speed: Double = 1.0,
    durationSeconds: Double? = null
) {
    val context = LocalContext.current
    val cleanName = resourceName.removeSuffix(".gif")

    val isFlyingNodes = cleanName == "phoenix_flying_nodes" ||
            cleanName.contains("fly_node") ||
            cleanName.contains("flying_between_nodes")
    val isWaving = cleanName == "phoenix_waving" || cleanName == "phoenix-waving" || cleanName.contains("wave")
    val isFlyingFlap = cleanName == "phoenix_flying"

    // Check pre-compiled compile-time frame sequences (bypassed if explicit .gif is requested)
    val frameResources = remember(cleanName, resourceName) {
        if (resourceName.endsWith(".gif")) {
            null
        } else {
            when {
                isFlyingNodes -> NodeFlyFrames
                isWaving -> WaveFrames
                isFlyingFlap -> FlyFlapFrames
                else -> null
            }
        }
    }

    if (frameResources != null && frameResources.size > 1) {
        val baseDurationMs = when {
            isFlyingNodes -> (durationSeconds ?: 2.5) * 1000.0
            isWaving -> (durationSeconds ?: 2.2) * 1000.0
            else -> (durationSeconds ?: 1.6) * 1000.0
        }
        val effectiveDurationMs = maxOf(100.0, baseDurationMs / maxOf(0.01, speed))
        val frameDelayMs = maxOf(16L, (effectiveDurationMs / frameResources.size).toLong())

        var currentFrameIndex by remember { mutableIntStateOf(0) }
        LaunchedEffect(speed, durationSeconds, frameResources) {
            while (isActive) {
                delay(frameDelayMs)
                currentFrameIndex = (currentFrameIndex + 1) % frameResources.size
            }
        }

        Image(
            painter = painterResource(id = frameResources[currentFrameIndex]),
            contentDescription = cleanName,
            contentScale = ContentScale.Fit,
            modifier = modifier.size(size)
        )
        return
    }

    // Fallback: Coil Animated GIF rendering from raw resources
    val gifResId = when (cleanName) {
        "phoenix_flying_nodes" -> R.raw.phoenix_flying_nodes
        "phoenix_waving", "phoenix-waving" -> R.raw.phoenix_waving
        "phoenix_stage_1" -> R.raw.phoenix_stage_1
        "phoenix_stage_2" -> R.raw.phoenix_stage_2
        "phoenix_stage_3" -> R.raw.phoenix_stage_3
        "phoenix_stage_4" -> R.raw.phoenix_stage_4
        "phoenix_stage_5" -> R.raw.phoenix_stage_5
        "phoenix_flying" -> R.raw.phoenix_flying
        else -> R.raw.phoenix_stage_1
    }

    val imageLoader = remember(context) {
        ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(gifResId)
            .crossfade(false)
            .build(),
        imageLoader = imageLoader
    )

    Image(
        painter = painter,
        contentDescription = "Animated Phoenix GIF",
        contentScale = ContentScale.Fit,
        modifier = modifier.size(size)
    )
}
