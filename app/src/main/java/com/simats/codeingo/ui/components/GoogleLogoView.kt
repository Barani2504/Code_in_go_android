package com.simats.codeingo.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Canvas-drawn Google 4-color G logo — faithful port of the iOS GoogleLogoView.
 * Draws the 4 colored arcs of the Google "G" plus the blue horizontal bar.
 */
@Composable
fun GoogleLogoView(
    size: Dp = 38.dp,
    modifier: Modifier = Modifier
) {
    val googleBlue = Color(0xFF4285F4)
    val googleRed = Color(0xFFEA4335)
    val googleYellow = Color(0xFFFBBC05)
    val googleGreen = Color(0xFF34A853)

    Canvas(modifier = modifier.size(size)) {
        val strokeWidth = this.size.width * 0.18f
        val padding = strokeWidth / 2f
        val arcSize = Size(
            this.size.width - strokeWidth,
            this.size.height - strokeWidth
        )
        val topLeft = Offset(padding, padding)
        val style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)

        // Blue arc: top-right quadrant (350° to 70° → start=-80, sweep=80)
        drawArc(
            color = googleBlue,
            startAngle = -80f,
            sweepAngle = 80f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = style
        )

        // Red arc: top-left (70° to 160° → start=160, sweep=-90)
        drawArc(
            color = googleRed,
            startAngle = -170f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = style
        )

        // Yellow arc: bottom-left (160° to 250° → start=160, sweep=90)
        drawArc(
            color = googleYellow,
            startAngle = 110f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = style
        )

        // Green arc: bottom-right (250° to 350° → start=250, sweep=100)
        drawArc(
            color = googleGreen,
            startAngle = 20f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = style
        )

        // Blue horizontal bar (the "crossbar" of the G)
        val barY = this.size.height / 2f
        val barStartX = this.size.width * 0.48f
        val barEndX = this.size.width - padding
        drawLine(
            color = googleBlue,
            start = Offset(barStartX, barY),
            end = Offset(barEndX, barY),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Butt
        )
    }
}
