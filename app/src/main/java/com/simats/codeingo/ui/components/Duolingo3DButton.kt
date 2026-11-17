package com.simats.codeingo.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Deprecated("Use AshButtonStyle instead", ReplaceWith("AshButtonStyle"))
typealias Duolingo3DButtonStyle = Duolingo3DButtonColor
@Deprecated("Use AshButtonStyle instead", ReplaceWith("AshButtonStyle"))
typealias Tactile3DButtonStyle = Duolingo3DButtonColor
@Deprecated("Use AshButtonStyle instead", ReplaceWith("AshButtonStyle"))
typealias Tactile3DButtonColor = Duolingo3DButtonColor

@Deprecated("Use AshButtonStyle instead")
enum class Duolingo3DButtonColor {
    GREEN,
    BLUE,
    AMBER,
    WHITE,
    DISABLED;

    companion object {
        val Green = GREEN
        val Blue = BLUE
        val Amber = AMBER
        val White = WHITE
        val Disabled = DISABLED
    }

    fun toAshButtonStyle(): AshButtonStyle = when (this) {
        GREEN -> AshButtonStyle.SUCCESS
        BLUE -> AshButtonStyle.BRAND
        AMBER -> AshButtonStyle.BRAND
        WHITE -> AshButtonStyle.SURFACE
        DISABLED -> AshButtonStyle.DISABLED
    }
}

@Deprecated("Use AshButton instead", ReplaceWith("AshButton(title, modifier, icon, style.toAshButtonStyle(), isEnabled, height, action, onClick)"))
@Composable
fun Duolingo3DButton(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    style: Duolingo3DButtonColor = Duolingo3DButtonColor.GREEN,
    isEnabled: Boolean = true,
    height: Dp = 50.dp,
    action: (() -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    AshButton(
        title = title,
        modifier = modifier,
        icon = icon,
        style = style.toAshButtonStyle(),
        isEnabled = isEnabled,
        height = height,
        action = action,
        onClick = onClick
    )
}

@Deprecated("Use AshButton instead", ReplaceWith("AshButton(title, modifier, icon, style.toAshButtonStyle(), isEnabled, height, action, onClick)"))
@Composable
fun Tactile3DButton(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    style: Duolingo3DButtonColor = Duolingo3DButtonColor.GREEN,
    isEnabled: Boolean = true,
    height: Dp = 50.dp,
    action: (() -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    AshButton(
        title = title,
        modifier = modifier,
        icon = icon,
        style = style.toAshButtonStyle(),
        isEnabled = isEnabled,
        height = height,
        action = action,
        onClick = onClick
    )
}
