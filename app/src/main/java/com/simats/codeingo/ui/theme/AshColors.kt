package com.simats.codeingo.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * AshCrypt 7-Color Semantic Palette (Phase 1).
 * Every screen uses AT MOST 7 colors derived strictly from these semantic roles.
 *
 * Contrast Verified (WCAG AA >= 4.5:1 on bg and surface in both dark and light modes).
 * Button text on brand is onBrand: #1A0E08 (dark) / #FFFFFF (light).
 */
@Immutable
data class AshPalette(
    val bg: Color,
    val surface: Color,
    val ink: Color,
    val inkMuted: Color,
    val brand: Color,
    val accent: Color,
    val success: Color,
    val danger: Color,
    val onBrand: Color,
    val isDark: Boolean
)

val AshDarkPalette = AshPalette(
    bg = Color(0xFF0E1116),
    surface = Color(0xFF171C24),
    ink = Color(0xFFF3F5F8),
    inkMuted = Color(0xFFA6B0BF),
    brand = Color(0xFFFF7A3D),
    accent = Color(0xFF3DD9C4),
    success = Color(0xFF34D399),
    danger = Color(0xFFFF6B7A),
    onBrand = Color(0xFF1A0E08),
    isDark = true
)

val AshLightPalette = AshPalette(
    bg = Color(0xFFFAF7F2),
    surface = Color(0xFFFFFFFF),
    ink = Color(0xFF14181F),
    inkMuted = Color(0xFF4B5565),
    brand = Color(0xFFC2410C),
    accent = Color(0xFF0B7F74),
    success = Color(0xFF12805C),
    danger = Color(0xFFC2283A),
    onBrand = Color(0xFFFFFFFF),
    isDark = false
)

val LocalAshColors = staticCompositionLocalOf { AshDarkPalette }

val AshPalette.background: Color get() = bg
val AshPalette.border: Color get() = if (isDark) Color(0xFF262D38) else Color(0xFFE2E8F0)
val AshPalette.subtle: Color get() = inkMuted
val AshPalette.accentContainer: Color get() = accent.copy(alpha = 0.15f)
val AshPalette.surfaceContainer: Color get() = if (isDark) Color(0xFF1E2530) else Color(0xFFF1F5F9)
val AshPalette.statusSuccess: Color get() = success
val AshPalette.statusError: Color get() = danger

object AshTheme {
    val colors: AshPalette
        @Composable
        @ReadOnlyComposable
        get() = LocalAshColors.current

    val typography: AshTypography
        get() = AshType
}
