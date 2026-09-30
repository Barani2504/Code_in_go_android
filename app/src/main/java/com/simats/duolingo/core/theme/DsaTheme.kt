package com.simats.duolingo.core.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/* ================================================================== */
/*  DSA COURSE DESIGN SYSTEM TOKENS                                   */
/* ================================================================== */

val DsaDarkBgStart = Color(0xFF1B1B3A)
val DsaDarkBgEnd = Color(0xFF3B2A5C)
val DsaCardDark = Color(0xFF24244D)
val DsaBorderDark = Color(0xFF45386B)

val DsaGreenPrimary = Color(0xFF58CC02)
val DsaGreenDark = Color(0xFF46A302)

val DsaYellowPrimary = Color(0xFFFFC800)
val DsaYellowDark = Color(0xFFD4A600)

val DsaBluePrimary = Color(0xFF1CB0F6)
val DsaBlueDark = Color(0xFF1485BA)

val DsaRedPrimary = Color(0xFFFF4B4B)
val DsaRedDark = Color(0xFFD93636)

val DsaPurpleCrown = Color(0xFFA855F7)
val DsaGoldCrown = Color(0xFFFFD700)

val DsaCourseBackgroundGradient = Brush.verticalGradient(
    listOf(DsaDarkBgStart, DsaDarkBgEnd)
)

val DsaCardGlassGradient = Brush.verticalGradient(
    listOf(Color(0xFF2C2454).copy(alpha = 0.85f), Color(0xFF1F1A3E).copy(alpha = 0.95f))
)
