package com.simats.codeingo.ui.components.motion

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * AshCrypt Material 3 Expressive & Standard MotionScheme.
 *
 * Provides centralized, physics-based motion specs matching the Material 3 Expressive guidelines
 * while remaining 100% compatible with stable Compose BOM 2024.12.01 / M3 1.3.1.
 *
 * - Expressive: High energy, bouncy, energetic spring physics for hero moments, mascot celebrations,
 *   combo multipliers, and level victories.
 * - Standard: Crisp, predictable, high-utility springs and easings for tooltips, menus, and simple controls.
 */
data class AshMotionScheme(
    // ── Expressive Specs (Hero Moments, Celebrations, Combos) ──
    val expressiveSpatial: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy, // 0.60f
        stiffness = Spring.StiffnessLow // ~400f
    ),
    val expressiveSpatialDp: AnimationSpec<Dp> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    ),
    val expressiveEffects: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMediumLow
    ),
    val expressivePop: AnimationSpec<Float> = spring(
        dampingRatio = 0.52f,
        stiffness = 500f
    ),

    // ── Standard Specs (Utility, Navigation, Drawers, Micro-interactions) ──
    val standardSpatial: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    ),
    val standardSpatialDp: AnimationSpec<Dp> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    ),
    val standardEffects: AnimationSpec<Float> = tween(
        durationMillis = 200,
        easing = FastOutSlowInEasing
    ),
    val standardFade: AnimationSpec<Float> = tween(
        durationMillis = 150,
        easing = LinearOutSlowInEasing
    ),

    // ── Token Morph & Card Flip ──
    val flipCardSpring: AnimationSpec<Float> = spring(
        dampingRatio = 0.75f,
        stiffness = Spring.StiffnessLow
    )
)

val LocalAshMotionScheme = staticCompositionLocalOf { AshMotionScheme() }

object AshMotion {
    val scheme: AshMotionScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalAshMotionScheme.current
}

