package com.simats.codeingo.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 🍎 Vector-drawn Apple Logo for official "Sign in with Apple" buttons.
 * Faithful vector geometry of the iconic Apple silhouette and leaf.
 */
@Composable
fun AppleLogoView(
    size: Dp = 20.dp,
    tint: Color = Color.White,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Apple leaf on top
        val leafPath = Path().apply {
            moveTo(w * 0.53f, h * 0.22f)
            cubicTo(w * 0.54f, h * 0.14f, w * 0.60f, h * 0.04f, w * 0.68f, 0f)
            cubicTo(w * 0.69f, h * 0.08f, w * 0.66f, h * 0.17f, w * 0.60f, h * 0.22f)
            cubicTo(w * 0.54f, h * 0.27f, w * 0.48f, h * 0.26f, w * 0.53f, h * 0.22f)
            close()
        }
        drawPath(path = leafPath, brush = SolidColor(tint))

        // Main Apple body with bite on right side
        val bodyPath = Path().apply {
            moveTo(w * 0.77f, h * 0.48f)
            cubicTo(w * 0.77f, h * 0.38f, w * 0.85f, h * 0.33f, w * 0.86f, h * 0.33f)
            cubicTo(w * 0.81f, h * 0.26f, w * 0.73f, h * 0.25f, w * 0.70f, h * 0.25f)
            cubicTo(w * 0.62f, h * 0.24f, w * 0.54f, h * 0.29f, w * 0.49f, h * 0.29f)
            cubicTo(w * 0.44f, h * 0.29f, w * 0.38f, h * 0.25f, w * 0.31f, h * 0.25f)
            cubicTo(w * 0.20f, h * 0.25f, w * 0.10f, h * 0.32f, w * 0.05f, h * 0.41f)
            cubicTo(-0.06f, h * 0.60f, w * 0.02f, h * 0.87f, w * 0.13f, h * 1.01f)
            cubicTo(w * 0.18f, h * 1.08f, w * 0.24f, h * 1.16f, w * 0.32f, h * 1.16f)
            cubicTo(w * 0.40f, h * 1.16f, w * 0.43f, h * 1.11f, w * 0.52f, h * 1.11f)
            cubicTo(w * 0.61f, h * 1.11f, w * 0.64f, h * 1.16f, w * 0.72f, h * 1.16f)
            cubicTo(w * 0.80f, h * 1.16f, w * 0.85f, h * 1.09f, w * 0.90f, h * 1.02f)
            cubicTo(w * 0.96f, h * 0.94f, w * 0.98f, h * 0.90f, w * 1.00f, h * 0.86f)
            cubicTo(w * 0.99f, h * 0.86f, w * 0.77f, h * 0.78f, w * 0.77f, h * 0.48f)
            close()
        }
        drawPath(path = bodyPath, brush = SolidColor(tint))
    }
}
