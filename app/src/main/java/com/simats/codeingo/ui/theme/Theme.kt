package com.simats.codeingo.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// The iOS app forces .preferredColorScheme(.dark) everywhere.
// We define a single dark scheme matching the Duolingo palette.
private val DuolingoDarkColorScheme = darkColorScheme(
    primary = DuolingoGreen,
    onPrimary = Color.White,
    primaryContainer = DuolingoGreenDark,
    onPrimaryContainer = Color.White,
    secondary = DuolingoBlue,
    onSecondary = Color.White,
    secondaryContainer = DuolingoBlueDark,
    onSecondaryContainer = Color.White,
    tertiary = DsaPurple,
    onTertiary = Color.White,
    background = DuolingoDarkBg,
    onBackground = Color.White,
    surface = DuolingoDarkBg,
    onSurface = Color.White,
    surfaceVariant = DuolingoCardBg,
    onSurfaceVariant = DuolingoSubtext,
    outline = DuolingoInputBorder,
    outlineVariant = DuolingoInputBorder.copy(alpha = 0.6f),
    error = DsaRed,
    onError = Color.White,
)

@Composable
fun CodeingoTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DuolingoDarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = DuolingoDarkBg.toArgb()
            window.navigationBarColor = DuolingoDarkBg.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}