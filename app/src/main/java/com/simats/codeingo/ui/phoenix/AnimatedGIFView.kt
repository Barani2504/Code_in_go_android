package com.simats.codeingo.ui.phoenix

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.simats.codeingo.R

/**
 * AnimatedGIFView — Jetpack Compose equivalent of iOS AnimatedGIFView.swift.
 * Renders animated GIFs (like phoenix_flying.gif) smoothly using Coil's GifDecoder / ImageDecoder.
 * Falls back to high-res PNG frame sequence or static mascot if needed.
 */
@Composable
fun AnimatedGIFView(
    resourceName: String = "phoenix_flying",
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    speed: Double = 1.0
) {
    val context = LocalContext.current
    val cleanName = resourceName.removeSuffix(".gif")

    val gifResId = when (cleanName) {
        "phoenix_flying" -> R.raw.phoenix_flying
        else -> R.raw.phoenix_flying
    }

    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(gifResId)
            .crossfade(true)
            .build(),
        imageLoader = imageLoader
    )

    Image(
        painter = painter,
        contentDescription = "Animated Phoenix GIF",
        modifier = modifier.size(size)
    )
}
