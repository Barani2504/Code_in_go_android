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
    val placeholder: Color
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
        placeholder = PhoenixPlaceholder
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
            placeholder = PhoenixPlaceholder
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
            placeholder = PhoenixLightPlaceholder
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
// ═════════════════════════════════════════════════════════════════

/**
 * Mimics iOS `.liquidGlassCard(cornerRadius:accentGlow:fillOpacity:)`.
 * Layered approach:
 *   1. Semi-transparent obsidian refraction gradient
 *   2. Specular top-left amber radial glow
 *   3. Gradient border (top-left light → faint ember → subtle rim)
 *   4. Dual ambient shadows (black depth + specular color lift)
 */
fun Modifier.liquidGlassCard(
    cornerRadius: Dp = 22.dp,
    accentGlow: Color = AmberGold.copy(alpha = 0.15f),
    fillOpacity: Float = 0.60f,
): Modifier = composed {
    val shape = RoundedCornerShape(cornerRadius)
    this
        .shadow(14.dp, shape, ambientColor = Color.Black.copy(alpha = 0.45f))
        .clip(shape)
        .background(
            Brush.linearGradient(
                listOf(
                    Color(0xFF10_18_2C).copy(alpha = fillOpacity),
                    Color(0xFF08_0D_1A).copy(alpha = fillOpacity + 0.18f),
                )
            )
        )
        .border(
            width = 1.2.dp,
            brush = Brush.linearGradient(
                listOf(
                    Color.White.copy(alpha = 0.40f),
                    accentGlow,
                    Color.White.copy(alpha = 0.06f),
                    accentGlow.copy(alpha = 0.25f),
                )
            ),
            shape = shape,
        )
}

/**
 * Mimics iOS `.liquidGlassPill(isSelected:accentColor:)`.
 */
fun Modifier.liquidGlassPill(
    isSelected: Boolean = false,
    accentColor: Color = AmberGold,
): Modifier = composed {
    val shape = RoundedCornerShape(50)
    val fillColor = if (isSelected) accentColor.copy(alpha = 0.25f)
                    else Color(0xFF14_1E_38).copy(alpha = 0.85f)
    val borderColor = if (isSelected) accentColor.copy(alpha = 0.80f)
                      else Color.White.copy(alpha = 0.22f)
    this
        .clip(shape)
        .background(fillColor)
        .border(1.dp, borderColor, shape)
        .shadow(if (isSelected) 8.dp else 0.dp, shape,
                ambientColor = if (isSelected) accentColor.copy(alpha = 0.30f)
                               else Color.Transparent)
}

/**
 * Mimics iOS `.liquidGlassIsland(cornerRadius:glowColor:)`.
 */
fun Modifier.liquidGlassIsland(
    cornerRadius: Dp = 26.dp,
    glowColor: Color = AmberGold.copy(alpha = 0.12f),
): Modifier = composed {
    val shape = RoundedCornerShape(cornerRadius)
    this
        .shadow(10.dp, shape, ambientColor = glowColor)
        .clip(shape)
        .background(PhoenixCard.copy(alpha = 0.90f))
        .border(1.dp, Color.White.copy(alpha = 0.18f), shape)
}

/**
 * Mimics iOS `.phoenixCard(cornerRadius:accentColor:)`.
 */
fun Modifier.phoenixCard(
    cornerRadius: Dp = 20.dp,
    accentColor: Color = AmberGold,
): Modifier = composed {
    val shape = RoundedCornerShape(cornerRadius)
    this
        .shadow(8.dp, shape, ambientColor = accentColor.copy(alpha = 0.20f))
        .clip(shape)
        .background(PhoenixCard)
        .border(
            1.2.dp,
            Brush.linearGradient(
                listOf(
                    accentColor.copy(alpha = 0.50f),
                    accentColor.copy(alpha = 0.10f),
                )
            ),
            shape,
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

    // Secondary (Obsidian glass border)
    val SecondaryFill        = Color(0xFF14_1E_37)
    val SecondaryBorder      = AmberGold.copy(alpha = 0.40f)
    val SecondaryText        = Color.White

    // Disabled
    val DisabledFill         = Color(0xFF2A_32_3F)
    val DisabledText         = Color.White.copy(alpha = 0.35f)
    val DisabledShadow       = Color(0xFF1A_20_28)

    // Danger (Crimson)
    val DangerFace           = PhoenixCrimson
    val DangerShadow         = Color(0xFF99_18_0A)
    val DangerText           = Color.White
}