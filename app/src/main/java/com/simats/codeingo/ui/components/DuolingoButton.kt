package com.simats.codeingo.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoGreenDark

/**
 * Faithful port of the iOS DuolingoButtonStyle.
 *
 * A 3D pushable button with:
 *   - A bottom "shadow" layer offset 4dp below the face
 *   - A top "face" layer that drops down to +4dp on press
 *   - The label text follows the face offset
 *   - 100ms ease-out animation
 */
@Composable
fun DuolingoButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    faceColor: Color = DuolingoGreen,
    shadowColor: Color = DuolingoGreenDark,
    textColor: Color = Color.White,
    borderStroke: Pair<Dp, Color>? = null,
    cornerRadius: Dp = 16.dp,
    enabled: Boolean = true,
    backgroundColor: Color = faceColor,
    content: @Composable () -> Unit
) {
    val effectiveFaceColor = if (backgroundColor != DuolingoGreen) backgroundColor else faceColor
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // On press: face drops 4dp, shadow stays — identical to iOS behaviour
    val faceOffset by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 0.dp,
        animationSpec = tween(durationMillis = 100),
        label = "faceOffset"
    )
    val shadowOffset by animateDpAsState(
        targetValue = if (isPressed) 0.dp else 4.dp,
        animationSpec = tween(durationMillis = 100),
        label = "shadowOffset"
    )

    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier.height(52.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        // Shadow layer
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .offset(y = shadowOffset),
            shape = shape,
            color = if (enabled) shadowColor else shadowColor.copy(alpha = 0.5f),
            content = {}
        )

        // Face layer (clickable)
        val faceModifier = if (borderStroke != null) {
            Modifier
                .fillMaxWidth()
                .height(48.dp)
                .offset(y = faceOffset)
                .border(borderStroke.first, borderStroke.second, shape)
        } else {
            Modifier
                .fillMaxWidth()
                .height(48.dp)
                .offset(y = faceOffset)
        }

        Surface(
            onClick = { if (enabled) onClick() },
            modifier = faceModifier,
            shape = shape,
            color = if (enabled) effectiveFaceColor else effectiveFaceColor.copy(alpha = 0.5f),
            interactionSource = interactionSource,
        ) {
            Box(contentAlignment = Alignment.Center) {
                content()
            }
        }
    }
}

/**
 * Convenience overload with a simple text label.
 */
@Composable
fun DuolingoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    faceColor: Color = DuolingoGreen,
    shadowColor: Color = DuolingoGreenDark,
    textColor: Color = Color.White,
    borderStroke: Pair<Dp, Color>? = null,
    cornerRadius: Dp = 16.dp,
    enabled: Boolean = true,
    backgroundColor: Color = faceColor,
) {
    DuolingoButton(
        onClick = onClick,
        modifier = modifier,
        faceColor = faceColor,
        shadowColor = shadowColor,
        textColor = textColor,
        borderStroke = borderStroke,
        cornerRadius = cornerRadius,
        enabled = enabled,
        backgroundColor = backgroundColor,
    ) {
        Text(
            text = text,
            color = if (enabled) textColor else textColor.copy(alpha = 0.5f),
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
        )
    }
}

