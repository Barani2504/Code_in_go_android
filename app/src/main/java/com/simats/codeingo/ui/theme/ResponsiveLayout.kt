package com.simats.codeingo.ui.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ══════════════════════════════════════════════════════════════════
// 📐 Responsive Layout & Screen Metrics System
// Universal dimensioning system adapting to:
// - Small phones (360x640dp, 320dp wide)
// - Standard phones (390-412dp wide)
// - Tall phones (20:9, 21:9)
// - Foldables (unfolded 600-840dp)
// - Tablets (Expanded 840dp+)
// - Landscape and split-screen modes
// ══════════════════════════════════════════════════════════════════

enum class WindowWidthClass {
    Compact,   // Phones (< 600dp)
    Medium,    // Small tablets, foldables (600dp .. 839dp)
    Expanded   // Tablets, desktop (>= 840dp)
}

enum class WindowHeightClass {
    Compact,   // Landscape phones (< 480dp)
    Medium,    // Standard phones & small tablets (480dp .. 899dp)
    Expanded   // Tall tablets (>= 900dp)
}

/**
 * Encapsulates responsive metrics, dynamic scale factors, and safe page margins.
 */
data class ScreenMetrics(
    val widthClass: WindowWidthClass,
    val heightClass: WindowHeightClass,
    val screenWidthDp: Dp,
    val screenHeightDp: Dp,
    val scaleFactor: Float,          // Reference width 390dp. Clamped to 0.85f .. 1.25f
    val horizontalPageMargin: Dp,    // 16dp Compact, 24dp Medium, 32dp Expanded
    val maxContentWidth: Dp = 600.dp,// Maximum width for centered forms, lessons, and dialogs on large screens
    val isLandscape: Boolean,
    val isTabletOrFoldable: Boolean
) {
    /** Scales an intrinsic element size proportionally by scaleFactor within safe bounds */
    fun scaledDp(baseDp: Dp): Dp = (baseDp.value * scaleFactor).dp
}

val LocalScreenMetrics = staticCompositionLocalOf<ScreenMetrics> {
    error("No ScreenMetrics provided. Wrap your root hierarchy in ProvideScreenMetrics.")
}

@Composable
fun rememberScreenMetrics(): ScreenMetrics {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    val widthClass = when {
        configuration.screenWidthDp < 600 -> WindowWidthClass.Compact
        configuration.screenWidthDp < 840 -> WindowWidthClass.Medium
        else -> WindowWidthClass.Expanded
    }

    val heightClass = when {
        configuration.screenHeightDp < 480 -> WindowHeightClass.Compact
        configuration.screenHeightDp < 900 -> WindowHeightClass.Medium
        else -> WindowHeightClass.Expanded
    }

    // Reference design width: 390dp (iOS reference / Pixel-class phone)
    val rawScale = configuration.screenWidthDp / 390f
    val clampedScale = rawScale.coerceIn(0.85f, 1.25f)

    val pageMargin = when (widthClass) {
        WindowWidthClass.Compact -> 16.dp
        WindowWidthClass.Medium -> 24.dp
        WindowWidthClass.Expanded -> 32.dp
    }

    return ScreenMetrics(
        widthClass = widthClass,
        heightClass = heightClass,
        screenWidthDp = screenWidth,
        screenHeightDp = screenHeight,
        scaleFactor = clampedScale,
        horizontalPageMargin = pageMargin,
        maxContentWidth = 600.dp,
        isLandscape = isLandscape,
        isTabletOrFoldable = widthClass != WindowWidthClass.Compact
    )
}

@Composable
fun ProvideScreenMetrics(
    content: @Composable () -> Unit
) {
    val metrics = rememberScreenMetrics()
    CompositionLocalProvider(LocalScreenMetrics provides metrics) {
        content()
    }
}

/**
 * Global accessor for responsive layout dimensions.
 */
object AppDimens {
    val metrics: ScreenMetrics
        @Composable
        @ReadOnlyComposable
        get() = LocalScreenMetrics.current

    /** Safe drawing insets as padding values */
    val safeDrawingPadding: PaddingValues
        @Composable
        get() = WindowInsets.safeDrawing.asPaddingValues()

    /** Minimum touch target size per Material guidelines (48dp) */
    val minTouchTarget: Dp = 48.dp
}

/**
 * Constrains content width on large screens (tablets, foldables) to prevent stretched UIs,
 * while expanding to fill the width on phones.
 */
fun Modifier.adaptiveContentWidth(
    maxWidth: Dp = 600.dp,
    alignment: Alignment.Horizontal = Alignment.CenterHorizontally
): Modifier = this
    .fillMaxWidth()
    .wrapContentWidth(alignment)
    .widthIn(max = maxWidth)

/**
 * Ensures interactive touch targets are at least 48dp in height and width.
 */
fun Modifier.safeInteractiveTarget(
    minSize: Dp = 48.dp
): Modifier = this
    .widthIn(min = minSize)
    .heightIn(min = minSize)

/**
 * Applies responsive horizontal page margins based on the current window size class.
 */
@Composable
fun Modifier.adaptiveHorizontalPadding(): Modifier {
    val margin = AppDimens.metrics.horizontalPageMargin
    return this.padding(horizontal = margin)
}
