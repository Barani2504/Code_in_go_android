package com.simats.codeingo.ui.theme

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.material3.LocalContentColor
import com.simats.codeingo.domain.AppTheme
import com.simats.codeingo.domain.ThemeManager

// ─────────────────────────────────────────────────────────────────
// Phoenix Bird Life Material Color Schemes
// Mirrors iOS Core/Theme.swift — ThemeManager adaptive switching
// ─────────────────────────────────────────────────────────────────
private val PhoenixDarkColorScheme = darkColorScheme(
    primary          = AmberGold,
    onPrimary        = Color(0xFF1A_0F_00),
    primaryContainer = AmberGoldDark,
    onPrimaryContainer = Color.White,
    secondary        = PhoenixEmber,
    onSecondary      = Color.White,
    secondaryContainer = Color(0xFF4A_20_00),
    onSecondaryContainer = Color.White,
    tertiary         = DsaPurple,
    onTertiary       = Color.White,
    background       = PhoenixObsidian,
    onBackground     = Color.White,
    surface          = PhoenixDeepNavy,
    onSurface        = Color.White,
    surfaceVariant   = PhoenixCard,
    onSurfaceVariant = PhoenixSubtext,
    outline          = PhoenixBorder,
    outlineVariant   = PhoenixBorder.copy(alpha = 0.6f),
    error            = PhoenixCrimson,
    onError          = Color.White,
)

private val PhoenixLightColorScheme = lightColorScheme(
    primary          = AmberGold,
    onPrimary        = Color(0xFF1A_0F_00),
    primaryContainer = AmberGoldDark,
    onPrimaryContainer = Color.White,
    secondary        = PhoenixEmber,
    onSecondary      = Color.White,
    secondaryContainer = Color(0xFFFF_E0_D0),
    onSecondaryContainer = Color(0xFF4A_20_00),
    tertiary         = DsaPurple,
    onTertiary       = Color.White,
    background       = PhoenixLightBg,
    onBackground     = PhoenixLightText,
    surface          = PhoenixLightCard,
    onSurface        = PhoenixLightText,
    surfaceVariant   = PhoenixLightInput,
    onSurfaceVariant = PhoenixLightSubtext,
    outline          = PhoenixLightBorder,
    outlineVariant   = PhoenixLightBorder.copy(alpha = 0.7f),
    error            = PhoenixCrimson,
    onError          = Color.White,
)

data class DynamicThemeColors(
    val isDark: Boolean,
    val background: Color,
    val headerBackground: Color,
    val cardBackground: Color,
    val inputBackground: Color,
    val inputBorder: Color,
    val inputText: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val placeholder: Color,
    val cardBorder: Color = inputBorder,
    val divider: Color = inputBorder.copy(alpha = 0.5f),
    val secondaryButtonFill: Color = if (isDark) Color(0xFF14_1E_37) else Color.White.copy(alpha = 0.90f),
    val secondaryButtonBorder: Color = if (isDark) AmberGold.copy(alpha = 0.40f) else AmberGold.copy(alpha = 0.50f),
    val secondaryButtonText: Color = if (isDark) Color.White else Color(0xFF12_18_26)
)

val LocalDynamicThemeColors = staticCompositionLocalOf {
    DynamicThemeColors(
        isDark = true,
        background = PhoenixObsidian,
        headerBackground = PhoenixDeepNavy,
        cardBackground = PhoenixCard,
        inputBackground = PhoenixInput,
        inputBorder = PhoenixBorder,
        inputText = Color.White,
        textPrimary = Color.White,
        textSecondary = PhoenixSubtext,
        placeholder = PhoenixPlaceholder,
        cardBorder = PhoenixBorder,
        divider = PhoenixBorder.copy(alpha = 0.5f),
        secondaryButtonFill = Color(0xFF14_1E_37),
        secondaryButtonBorder = AmberGold.copy(alpha = 0.40f),
        secondaryButtonText = Color.White
    )
}

@Composable
fun CodeingoTheme(
    content: @Composable () -> Unit
) {
    val themeManager = ThemeManager.instance
    val currentTheme by themeManager.currentTheme.collectAsState()
    val systemDark = isSystemInDarkTheme()

    val isDark = when (currentTheme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.SYSTEM -> systemDark
    }

    val dynamicColors = if (isDark) {
        DynamicThemeColors(
            isDark = true,
            background = PhoenixObsidian,
            headerBackground = PhoenixDeepNavy,
            cardBackground = PhoenixCard,
            inputBackground = PhoenixInput,
            inputBorder = PhoenixBorder,
            inputText = Color.White,
            textPrimary = Color.White,
            textSecondary = PhoenixSubtext,
            placeholder = PhoenixPlaceholder,
            cardBorder = PhoenixBorder,
            divider = PhoenixBorder.copy(alpha = 0.5f),
            secondaryButtonFill = Color(0xFF14_1E_37),
            secondaryButtonBorder = AmberGold.copy(alpha = 0.40f),
            secondaryButtonText = Color.White
        )
    } else {
        DynamicThemeColors(
            isDark = false,
            background = PhoenixLightBg,
            headerBackground = PhoenixLightHeaderBg,
            cardBackground = PhoenixLightCard,
            inputBackground = PhoenixLightInput,
            inputBorder = PhoenixLightBorder,
            inputText = PhoenixLightText,
            textPrimary = PhoenixLightText,
            textSecondary = PhoenixLightSubtext,
            placeholder = PhoenixLightPlaceholder,
            cardBorder = PhoenixLightBorder,
            divider = PhoenixLightBorder.copy(alpha = 0.7f),
            secondaryButtonFill = Color.White.copy(alpha = 0.90f),
            secondaryButtonBorder = AmberGold.copy(alpha = 0.50f),
            secondaryButtonText = PhoenixLightText
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !isDark
                isAppearanceLightNavigationBars = !isDark
            }
        }
    }

    CompositionLocalProvider(LocalDynamicThemeColors provides dynamicColors) {
        MaterialTheme(
            colorScheme = if (isDark) PhoenixDarkColorScheme else PhoenixLightColorScheme,
            typography  = AppTypography,
            shapes      = AppShapes,
        ) {
            CompositionLocalProvider(LocalContentColor provides dynamicColors.textPrimary) {
                content()
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════════
// LIQUID GLASS UI SYSTEM
// Compose equivalents of iOS LiquidGlassCardModifier and friends.
// Exact parity with iOS Core/Theme.swift LiquidGlassCardModifier.
// ═════════════════════════════════════════════════════════════════

/**
 * Mimics iOS `.liquidGlassCard(cornerRadius:strokeColor:specularGlow:fillOpacity:)`.
 * Adapts between volcanic obsidian refraction (Dark) and luminous frosted glass (Light).
 */
fun Modifier.liquidGlassCard(
    cornerRadius: Dp = 22.dp,
    accentGlow: Color = AmberGold.copy(alpha = 0.15f),
    fillOpacity: Float = 0.60f,
): Modifier = composed {
    val isDark = LocalDynamicThemeColors.current.isDark
    val shape = RoundedCornerShape(cornerRadius)
    val shadowAmbient = if (isDark) Color.Black.copy(alpha = 0.45f) else Color(0x14_26_33_59)
    val specularAmbient = accentGlow.copy(alpha = if (isDark) 0.30f else 0.12f)
    val fillBrush = Brush.linearGradient(
        if (isDark) listOf(
            Color(0xFF10_18_2C).copy(alpha = fillOpacity),
            Color(0xFF08_0D_1A).copy(alpha = fillOpacity + 0.18f),
        ) else listOf(
            Color.White.copy(alpha = fillOpacity * 0.95f),
            Color(0xFFF4_F6_FC).copy(alpha = fillOpacity * 0.90f),
        )
    )
    val borderBrush = Brush.linearGradient(
        if (isDark) listOf(
            Color.White.copy(alpha = 0.40f),
            accentGlow,
            Color.White.copy(alpha = 0.06f),
            accentGlow.copy(alpha = 0.25f),
        ) else listOf(
            Color.White,
            Color(0xFFD7_DE_EB).copy(alpha = 0.80f),
            Color.White.copy(alpha = 0.60f),
            accentGlow.copy(alpha = 0.30f),
        )
    )

    this
        .shadow(14.dp, shape, ambientColor = shadowAmbient, spotColor = shadowAmbient)
        .shadow(8.dp, shape, ambientColor = specularAmbient, spotColor = specularAmbient)
        .clip(shape)
        .background(fillBrush)
        .border(width = 1.2.dp, brush = borderBrush, shape = shape)
}

/**
 * Mimics iOS `.liquidGlassPill(isSelected:accentColor:)`.
 */
fun Modifier.liquidGlassPill(
    isSelected: Boolean = false,
    accentColor: Color = AmberGold,
): Modifier = composed {
    val isDark = LocalDynamicThemeColors.current.isDark
    val shape = RoundedCornerShape(50)
    val fillColor = if (isSelected) accentColor.copy(alpha = 0.28f)
                    else if (isDark) Color(0xFF12_1A_2E).copy(alpha = 0.55f)
                    else Color.White.copy(alpha = 0.85f)
    val borderBrush = Brush.linearGradient(
        if (isSelected) listOf(
            accentColor.copy(alpha = 0.90f),
            accentColor.copy(alpha = 0.40f)
        ) else if (isDark) listOf(
            Color.White.copy(alpha = 0.32f),
            Color.White.copy(alpha = 0.08f)
        ) else listOf(
            Color(0xFFD7_DE_EB),
            Color.White.copy(alpha = 0.60f)
        )
    )
    val shadowColor = if (isSelected) accentColor.copy(alpha = 0.35f)
                      else if (isDark) Color.Black.copy(alpha = 0.25f)
                      else Color.Black.copy(alpha = 0.05f)

    this
        .shadow(if (isSelected) 8.dp else 4.dp, shape, ambientColor = shadowColor, spotColor = shadowColor)
        .clip(shape)
        .background(fillColor)
        .border(if (isSelected) 1.5.dp else 1.dp, borderBrush, shape)
}

/**
 * Mimics iOS `.liquidGlassIsland(cornerRadius:glowColor:)`.
 */
fun Modifier.liquidGlassIsland(
    cornerRadius: Dp = 26.dp,
    glowColor: Color = AmberGold.copy(alpha = 0.12f),
): Modifier = composed {
    val isDark = LocalDynamicThemeColors.current.isDark
    val shape = RoundedCornerShape(cornerRadius)
    val fillColor = if (isDark) Color(0xFF0A_10_1E).copy(alpha = 0.70f)
                    else Color.White.copy(alpha = 0.88f)
    val borderBrush = Brush.linearGradient(
        if (isDark) listOf(
            Color.White.copy(alpha = 0.38f),
            Color.White.copy(alpha = 0.12f),
            glowColor,
            Color.White.copy(alpha = 0.05f)
        ) else listOf(
            Color.White,
            Color(0xFFD7_DE_EB).copy(alpha = 0.80f),
            glowColor,
            Color.White.copy(alpha = 0.40f)
        )
    )
    val shadowAmbient = if (isDark) Color.Black.copy(alpha = 0.50f) else Color(0x1A_26_33_59)

    this
        .shadow(18.dp, shape, ambientColor = shadowAmbient, spotColor = shadowAmbient)
        .shadow(10.dp, shape, ambientColor = glowColor.copy(alpha = 0.30f), spotColor = glowColor.copy(alpha = 0.30f))
        .clip(shape)
        .background(fillColor)
        .border(1.2.dp, borderBrush, shape)
}

/**
 * Mimics iOS `.phoenixCard(cornerRadius:accentColor:)`.
 * Delegates directly to liquidGlassCard with accentGlow matching iOS PhoenixCardModifier.
 */
fun Modifier.phoenixCard(
    cornerRadius: Dp = 20.dp,
    accentColor: Color = AmberGold,
): Modifier = composed {
    this.liquidGlassCard(
        cornerRadius = cornerRadius,
        accentGlow = accentColor.copy(alpha = 0.15f),
        fillOpacity = 0.60f
    )
}

// ═════════════════════════════════════════════════════════════════
// DUOLINGO 3D BUTTON STYLE HELPERS
// Compose equivalents of iOS DuolingoButtonStyle /
// PhoenixPrimaryButtonStyle / PhoenixSecondaryButtonStyle.
// The actual interactive button composable is in:
//   ui/components/Duolingo3DButton.kt
// These constants mirror the iOS style tokens used there.
// ═════════════════════════════════════════════════════════════════

object ButtonTokens {
    val Height         = 52.dp
    val CornerRadius   = 16.dp
    val ShadowOffset   = 4.dp

    // Primary (Amber Gold)
    val PrimaryFace    = AmberGold
    val PrimaryFaceLow = Color(0xFFFF_99_00)
    val PrimaryShadow  = AmberGoldDark
    val PrimaryText    = Color(0xFF1A_12_05)

    // Secondary (Theme-adaptive glass border)
    val SecondaryFill: Color
        @Composable
        get() = LocalDynamicThemeColors.current.secondaryButtonFill

    val SecondaryBorder: Color
        @Composable
        get() = LocalDynamicThemeColors.current.secondaryButtonBorder

    val SecondaryText: Color
        @Composable
        get() = LocalDynamicThemeColors.current.secondaryButtonText

    // Disabled
    val DisabledFill: Color
        @Composable
        get() = if (LocalDynamicThemeColors.current.isDark) Color(0xFF2A_32_3F) else Color(0xFFE2E8F0)

    val DisabledText: Color
        @Composable
        get() = if (LocalDynamicThemeColors.current.isDark) Color.White.copy(alpha = 0.35f) else Color(0xFF94A3B8)

    val DisabledShadow: Color
        @Composable
        get() = if (LocalDynamicThemeColors.current.isDark) Color(0xFF1A_20_28) else Color(0xFFCBD5E1)

    // Danger (Crimson)
    val DangerFace           = PhoenixCrimson
    val DangerShadow         = Color(0xFF99_18_0A)
    val DangerText           = Color.White
}