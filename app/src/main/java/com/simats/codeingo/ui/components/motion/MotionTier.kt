package com.simats.codeingo.ui.components.motion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * AshCrypt Motion Tier Hierarchy.
 *
 * Each tier strictly gates which visual and kinetic effects are allowed to execute,
 * preventing UI visual noise and guaranteeing smooth 60/120fps performance.
 *
 * T1 (Micro): Tap / select states. Spring physics + haptic tick. No particles, no screen shakes.
 * T2 (Answer): Answer evaluation. Glowing border or error shake, small localized burst (<= 12 particles).
 * T3 (Level Complete): Standard session victory. Single confetti system (<= 60 particles), XP count-up, star bursts.
 * T4 (Milestone): Major accomplishments (Boss defeat, Chapter clear, Streak milestone, Grand Master). Full ceremony.
 */
enum class MotionTier(val level: Int, val description: String) {
    T1_MICRO(
        level = 1,
        description = "Tactile micro-interaction: spring press, haptic tick, radio LED"
    ),
    T2_ANSWER(
        level = 2,
        description = "Answer evaluation: glow, error shake, small token burst (<= 12 particles)"
    ),
    T3_LEVEL_COMPLETE(
        level = 3,
        description = "Session victory: single confetti burst (<= 60 particles), XP count-up, stars"
    ),
    T4_MILESTONE(
        level = 4,
        description = "Hero achievement: full ceremony, boss shockwave, mascot fanfare, confetti shower"
    );

    fun allows(effect: MotionEffectType): Boolean {
        return when (effect) {
            MotionEffectType.HAPTIC_TICK -> true
            MotionEffectType.SPRING_PRESS -> true
            MotionEffectType.LED_INDICATOR -> true
            MotionEffectType.GLOW_BORDER -> this.level >= 2
            MotionEffectType.ERROR_SHAKE -> this.level >= 2
            MotionEffectType.TOKEN_SHATTER -> this.level >= 2
            MotionEffectType.LOCAL_BURST -> this.level >= 2
            MotionEffectType.NUMBER_ROLL -> this.level >= 3
            MotionEffectType.STAR_RATING_POP -> this.level >= 3
            MotionEffectType.CONFETTI_BURST -> this.level >= 3
            MotionEffectType.FULL_SCREEN_FANFARE -> this.level >= 4
            MotionEffectType.BOSS_SHOCKWAVE -> this.level >= 4
            MotionEffectType.MASCOT_EVOLUTION_CEREMONY -> this.level >= 4
        }
    }
}

/**
 * Granular classification of all motion and visual effects in AshCrypt.
 */
enum class MotionEffectType {
    HAPTIC_TICK,
    SPRING_PRESS,
    LED_INDICATOR,
    GLOW_BORDER,
    ERROR_SHAKE,
    TOKEN_SHATTER,
    LOCAL_BURST,
    NUMBER_ROLL,
    STAR_RATING_POP,
    CONFETTI_BURST,
    FULL_SCREEN_FANFARE,
    BOSS_SHOCKWAVE,
    MASCOT_EVOLUTION_CEREMONY
}

val LocalCurrentMotionTier = staticCompositionLocalOf { MotionTier.T1_MICRO }

object MotionTiers {
    val current: MotionTier
        @Composable
        @ReadOnlyComposable
        get() = LocalCurrentMotionTier.current
}

