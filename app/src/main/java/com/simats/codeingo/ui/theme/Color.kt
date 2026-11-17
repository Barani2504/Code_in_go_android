package com.simats.codeingo.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// ══════════════════════════════════════════════════════
// ASHCRYPT CORE 7-COLOR PALETTE TOKENS (Phase 1)
// ══════════════════════════════════════════════════════

val PhoenixObsidian      = Color(0xFF0E_11_16) // AshDarkPalette.bg
val PhoenixDeepNavy      = Color(0xFF17_1C_24) // AshDarkPalette.surface
val PhoenixCard          = Color(0xFF17_1C_24) // AshDarkPalette.surface
val PhoenixInput         = Color(0xFF0E_11_16) // AshDarkPalette.bg
val PhoenixBorder        = Color(0xFFFF_7A_3D).copy(alpha = 0.25f)
val PhoenixSubtext       = Color(0xFFA6_B0_BF) // AshDarkPalette.inkMuted
val PhoenixPlaceholder   = Color(0xFFA6_B0_BF).copy(alpha = 0.6f)

// Light Mode Tokens
val PhoenixLightBg         = Color(0xFFFA_F7_F2) // AshLightPalette.bg
val PhoenixLightHeaderBg   = Color(0xFFFF_FF_FF) // AshLightPalette.surface
val PhoenixLightCard       = Color(0xFFFF_FF_FF) // AshLightPalette.surface
val PhoenixLightInput      = Color(0xFFFA_F7_F2) // AshLightPalette.bg
val PhoenixLightBorder     = Color(0xFF4B_55_65).copy(alpha = 0.25f)
val PhoenixLightText       = Color(0xFF14_18_1F) // AshLightPalette.ink
val PhoenixLightSubtext    = Color(0xFF4B_55_65) // AshLightPalette.inkMuted
val PhoenixLightPlaceholder= Color(0xFF4B_55_65).copy(alpha = 0.6f)

// ── ACCENT & BRAND PALETTE ─────────────────────────────
val AmberGold            = Color(0xFFFF_7A_3D) // Brand Ember (Dark)
val AmberGoldDark        = Color(0xFFC2_41_0C) // Brand Ember (Light)
val PhoenixCrimson       = Color(0xFFFF_6B_7A) // Danger Status (Dark)
val PhoenixEmber         = Color(0xFFFF_7A_3D) // Brand
val PhoenixGreen         = Color(0xFF34_D3_99) // Success Status (Dark)
val LiquidGlassTint      = Color(0xFF17_1C_24)
val EmeraldGreen         = Color(0xFF34_D3_99)

// ══════════════════════════════════════════════════════
// THEME-ADAPTIVE LEGACY ALIASES — Phoenix equivalents
// Mirrors iOS Core/Theme.swift dynamicColor getters.
// Automatically flips between Dark Mode and Light Mode
// so every screen using these names automatically matches the theme.
// ══════════════════════════════════════════════════════

// ══════════════════════════════════════════════════════
// SEMANTIC DESIGN TOKENS (ASHCRYPT)
// ══════════════════════════════════════════════════════

val Success         = PhoenixGreen
val SuccessDark     = Color(0xFF1A_AE_47)
val Danger          = PhoenixCrimson
val DangerDark      = Color(0xFFBB_1C_0A)
val Warning         = PhoenixEmber
val WarningDark     = Color(0xFFCC_43_0E)
val Info            = Color(0xFF1C_B0_F6)
val InfoDark        = Color(0xFF18_99_D6)

val Surface: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalDynamicThemeColors.current.cardBackground

val SurfaceMuted: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalDynamicThemeColors.current.inputBackground

val OnSurfaceMuted: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalDynamicThemeColors.current.textSecondary

// ══════════════════════════════════════════════════════
// THEME-ADAPTIVE LEGACY ALIASES — Deprecated in AshCrypt
// ══════════════════════════════════════════════════════

// Backgrounds
@Deprecated("Use LocalDynamicThemeColors.current.background or ashTokens.colors.background")
val DuolingoDarkBg: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalDynamicThemeColors.current.background

@Deprecated("Use LocalDynamicThemeColors.current.headerBackground")
val DuolingoHeaderBg: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalDynamicThemeColors.current.headerBackground

// Cards & Inputs
@Deprecated("Use Surface or ashTokens.colors.surface")
val DuolingoCardBg: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalDynamicThemeColors.current.cardBackground

@Deprecated("Use SurfaceMuted or ashTokens.colors.surfaceMuted")
val DuolingoInputBg: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalDynamicThemeColors.current.inputBackground

@Deprecated("Use LocalDynamicThemeColors.current.inputBorder")
val DuolingoInputBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalDynamicThemeColors.current.inputBorder

@Deprecated("Use LocalDynamicThemeColors.current.inputText")
val DuolingoInputText: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalDynamicThemeColors.current.inputText

@Deprecated("Use LocalDynamicThemeColors.current.placeholder")
val DuolingoPlaceholder: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalDynamicThemeColors.current.placeholder

@Deprecated("Use OnSurfaceMuted")
val DuolingoTextGray: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalDynamicThemeColors.current.textSecondary

@Deprecated("Use OnSurfaceMuted")
val DuolingoSubtext: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalDynamicThemeColors.current.textSecondary

// Accents — legacy "Blue" → Phoenix amber/ember
@Deprecated("Use AmberGold or ashTokens.colors.primary")
val DuolingoBlue         = AmberGold
@Deprecated("Use AmberGoldDark or ashTokens.colors.primaryDark")
val DuolingoBlueDark     = AmberGoldDark

/**
 * Adaptive text color for amber/gold accents.
 * Resolves to luminous AmberGold in Dark Mode, and deep high-contrast warm bronze (Color(0xFFB45309))
 * in Light Mode so texts remain perfectly readable on light surfaces.
 */
val AmberGoldText: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalDynamicThemeColors.current.amberText

// Accents — legacy "Green" → Success
@Deprecated("Use Success or ashTokens.colors.success")
val DuolingoGreen        = Success
@Deprecated("Use SuccessDark or ashTokens.colors.successDark")
val DuolingoGreenDark    = SuccessDark

// Misc aliases kept for backward compat
@Deprecated("Use Warning")
val DuolingoOrange       = Warning
@Deprecated("Use WarningDark")
val DuolingoOrangeDark   = WarningDark
@Deprecated("Use Danger")
val DuolingoRed          = Danger
@Deprecated("Use DangerDark")
val DuolingoRedDark      = DangerDark

// Neutral brand aliases (Defect 20)
val ArenaSuccessGreen    = Success
val ArenaDangerRed       = Danger
val ArenaAccentGold      = AmberGold
val TactileGreen         = Success
val TactileRed           = Danger
val TactileAmber         = AmberGold

val DarkBackground: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalDynamicThemeColors.current.background

val CardBackground: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalDynamicThemeColors.current.cardBackground

val InputBackground: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalDynamicThemeColors.current.inputBackground

val InputBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalDynamicThemeColors.current.inputBorder

val SubtextGray: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalDynamicThemeColors.current.textSecondary

// ══════════════════════════════════════════════════════
// DIFFICULTY TIER COLORS
// ══════════════════════════════════════════════════════
@Deprecated("Use Success")
val DsaGreen             = Success
@Deprecated("Use SuccessDark")
val DsaGreenDark         = SuccessDark
@Deprecated("Use Info")
val DsaBlue              = Info
@Deprecated("Use InfoDark")
val DsaBlueDark          = InfoDark
val DsaPurple            = Color(0xFF3D_D9_C4) // Accent Teal
val DsaPurpleDark        = Color(0xFF0B_7F_74) // Accent Teal Dark
@Deprecated("Use Warning")
val DsaOrange            = Warning
@Deprecated("Use WarningDark")
val DsaOrangeDark        = WarningDark
@Deprecated("Use Danger")
val DsaRed               = Danger
@Deprecated("Use DangerDark")
val DsaRedDark           = DangerDark
val DsaTeal              = Color(0xFF3D_D9_C4) // Accent Teal

// ══════════════════════════════════════════════════════
// GOOGLE AUTH COLORS  (unchanged)
// ══════════════════════════════════════════════════════
val GoogleGreen          = Color(0xFF34_A8_53)
val GoogleBlue           = Color(0xFF42_85_F4)
val GoogleRed            = Color(0xFFEA_43_35)
val GoogleYellow         = Color(0xFFDB_BC_05)
val GoogleTextDark       = Color(0xFF20_21_24)
val GoogleTextSecondary  = Color(0xFF5F_63_68)
val GoogleBorderLight    = Color(0xFFDA_DC_E0)
val GoogleBgLight        = Color(0xFFF1_F3_F4)
