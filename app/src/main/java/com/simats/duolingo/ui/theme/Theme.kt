package com.simats.duolingo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ─── Duolingo Brand Colors ────────────────────────────────────────────────────
val DuolingoDarkBg       = Color(0xFF131F24)
val DuolingoHeaderBg     = Color(0xFF131F24)
val DuolingoCardBg       = Color(0xFF18252D)
val DuolingoInputBg      = Color(0xFF152128)
val DuolingoInputBorder  = Color(0xFF37464F)
val DuolingoGreen        = Color(0xFF58CC02)
val DuolingoGreenDark    = Color(0xFF46A302)
val DuolingoBlue         = Color(0xFF1CB0F6)
val DuolingoBlueDark     = Color(0xFF1899D6)
val DuolingoTextGray     = Color(0xFFAFAFAF)
val DuolingoSubtext      = Color(0xFF8CA0AA)
val DuolingoPlaceholder  = Color(0xFF647882)
val AmberGold            = Color(0xFFFFBF00)
val DuolingoPurple       = Color(0xFFCE82FF)
val DuolingoTeal         = Color(0xFF00CD9C)
val DuolingoOrange       = Color(0xFFFF9600)

private val DarkColorScheme = darkColorScheme(
    primary        = DuolingoGreen,
    secondary      = DuolingoBlue,
    background     = DuolingoDarkBg,
    surface        = DuolingoCardBg,
    onPrimary      = Color.White,
    onSecondary    = Color.White,
    onBackground   = Color.White,
    onSurface      = Color.White,
)

@Composable
fun DuolingoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content     = content,
    )
}