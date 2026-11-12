package com.simats.codeingo.ui.theme

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class AshColorTokens(
    val primary: Color = AmberGold,
    val primaryDark: Color = AmberGoldDark,
    val success: Color = PhoenixGreen,
    val successDark: Color = Color(0xFF1A_AE_47),
    val danger: Color = PhoenixCrimson,
    val dangerDark: Color = Color(0xFFBB_1C_0A),
    val warning: Color = PhoenixEmber,
    val warningDark: Color = Color(0xFFCC_43_0E),
    val info: Color = Color(0xFF1C_B0_F6),
    val signalCyan: Color = Color(0xFF00_E5_FF),
    val background: Color = Color(0xFF0F_17_2A),
    val surface: Color = Color(0xFF13_1B_2E),
    val surfaceMuted: Color = Color(0xFF1A_23_3D),
    val onSurface: Color = Color.White,
    val onSurfaceMuted: Color = Color(0xFF94_A3_B8),
    val border: Color = Color.White.copy(alpha = 0.12f),
    val isDark: Boolean = true
)

@Immutable
data class AshRadiusTokens(
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 20.dp,
    val xl: Dp = 28.dp,
    val full: Dp = 999.dp
)

@Immutable
data class AshSpacingTokens(
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
    val xl: Dp = 32.dp,
    val xxl: Dp = 48.dp
)

@Immutable
data class AshElevationTokens(
    val none: Dp = 0.dp,
    val low: Dp = 2.dp,
    val medium: Dp = 6.dp,
    val high: Dp = 12.dp,
    val tactilePress: Dp = 4.dp
)

@Immutable
data class AshMotionTokens(
    val snappy: AnimationSpec<Float> = spring(dampingRatio = 0.75f, stiffness = 420f),
    val springy: AnimationSpec<Float> = spring(dampingRatio = 0.65f, stiffness = 320f),
    val cinematic: AnimationSpec<Float> = tween(durationMillis = 400),
    val gentle: AnimationSpec<Float> = tween(durationMillis = 250)
)

@Immutable
data class AshTokens(
    val colors: AshColorTokens = AshColorTokens(),
    val radius: AshRadiusTokens = AshRadiusTokens(),
    val spacing: AshSpacingTokens = AshSpacingTokens(),
    val elevation: AshElevationTokens = AshElevationTokens(),
    val motion: AshMotionTokens = AshMotionTokens()
)

val LocalAshTokens = staticCompositionLocalOf { AshTokens() }

/**
 * Accessor for the active AshTokens.
 */
val ashTokens: AshTokens
    @Composable
    @ReadOnlyComposable
    get() = LocalAshTokens.current

