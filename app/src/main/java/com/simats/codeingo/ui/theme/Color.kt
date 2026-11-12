package com.simats.codeingo.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// ══════════════════════════════════════════════════════
// PHOENIX BIRD LIFE — CORE PALETTE
// Mirrors iOS Core/Theme.swift exactly (pt → dp 1:1)
// ══════════════════════════════════════════════════════

/** Midnight volcanic obsidian — deepest background layer.  iOS: 7,13,23 */
val PhoenixObsidian      = Color(0xFF07_0D_17)

/** Deep space navy — secondary background / tab bar.  iOS: 13,20,38 */
val PhoenixDeepNavy      = Color(0xFF0D_14_26)

/** Glassmorphic card surface — all cards / sheets.  iOS: 17,26,48 */
val PhoenixCard          = Color(0xFF11_1A_30)

/** Input field fill — slightly lighter than card.  iOS: 20,30,55 */
val PhoenixInput         = Color(0xFF14_1E_37)

/** Subtle border line on cards / inputs — 22 % amber.  iOS: 255,138,20 @ 0.22 */
val PhoenixBorder        = Color(0xFF_FF8A14).copy(alpha = 0.22f)

/** Muted subtext — warm slate.  iOS: 180,165,140 */
val PhoenixSubtext       = Color(0xFFB4_A5_8C)

/** Placeholder text in fields.  iOS: 140,125,105 */
val PhoenixPlaceholder   = Color(0xFF8C_7D_69)

// Light Mode Tokens (iOS Core/Theme.swift)
val PhoenixLightBg         = Color(0xFFF5_F7_FC)
val PhoenixLightHeaderBg   = Color(0xFFFF_FF_FF)
val PhoenixLightCard       = Color(0xFFFF_FF_FF)
val PhoenixLightInput      = Color(0xFFF1_F4_FA)
val PhoenixLightBorder     = Color(0xFFD7_DE_EB)
val PhoenixLightText       = Color(0xFF12_18_26)
val PhoenixLightSubtext    = Color(0xFF64_70_80)
val PhoenixLightPlaceholder= Color(0xFF9B_A2_AF)

// ── ACCENT PALETTE ────────────────────────────────────

/** Primary amber gold — headings, icons, CTA.  iOS: FFC800 */
val AmberGold            = Color(0xFFFF_C8_00)

/** Volcanic gold (darker amber) — shadows, pressed states.  iOS: CC8C00 */
val AmberGoldDark        = Color(0xFFCC_8C_00)

/** Solar crimson — fire, danger, boss battle.  iOS: E82410 */
val PhoenixCrimson       = Color(0xFFE8_24_10)

/** Ember orange — progress bars, streaks.  iOS: FF5712 */
val PhoenixEmber         = Color(0xFFFF_57_12)

/** Phoenix green — correct answers, success.  iOS: 33D966 */
val PhoenixGreen         = Color(0xFF33_D9_66)

/** Liquid glass tint overlay.  iOS: 0x141E38 */
val LiquidGlassTint      = Color(0xFF14_1E_38)

/** Emerald green — BinaryTree Forest world accent */
val EmeraldGreen         = Color(0xFF00_C8_5A)

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
val DsaPurple            = Color(0xFFA0_5A_FF)
val DsaPurpleDark        = Color(0xFF82_3C_DC)
@Deprecated("Use Warning")
val DsaOrange            = Warning
@Deprecated("Use WarningDark")
val DsaOrangeDark        = WarningDark
@Deprecated("Use Danger")
val DsaRed               = Danger
@Deprecated("Use DangerDark")
val DsaRedDark           = DangerDark
val DsaTeal              = Color(0xFF00_CD_9C)

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
