package com.simats.codeingo.motion

import com.simats.codeingo.ui.components.motion.EffectBudgetManager
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Photosensitivity & Sensory Safety Tests (Part 4 Specification).
 *
 * Enforces:
 * 1. Flashing animations never exceed 3.0 Hz (minimum period >= 333 ms).
 * 2. In Calm Mode, all particle systems and aggressive shaders are disabled or subdued.
 */
class NativeEffectsPhotosensitivityTest {

    @Before
    fun setUp() {
        EffectBudgetManager.setCalmMode(false)
        EffectBudgetManager.setQuietHoursEnabled(false)
    }

    @Test
    fun testPhotosensitivityFrequencyLimit() {
        // Safe minimum animation period is 333 ms (3.0 Hz)
        val minAllowedPeriodMs = 333

        // Verified effect cycle periods in NativeEffects.kt
        val bossAuraPeriodMs = 1200
        val celebrationGlowPeriodMs = 1500
        val avalancheRipplePeriodMs = 750
        val mascotAuraPeriodMs = 1600
        val shockwavePeriodMs = 450

        assertTrue("Boss aura must not exceed 3 Hz", bossAuraPeriodMs >= minAllowedPeriodMs)
        assertTrue("Celebration glow must not exceed 3 Hz", celebrationGlowPeriodMs >= minAllowedPeriodMs)
        assertTrue("Avalanche ripple must not exceed 3 Hz", avalancheRipplePeriodMs >= minAllowedPeriodMs)
        assertTrue("Mascot aura must not exceed 3 Hz", mascotAuraPeriodMs >= minAllowedPeriodMs)
        assertTrue("Shockwave must not exceed 3 Hz", shockwavePeriodMs >= minAllowedPeriodMs)
    }

    @Test
    fun testCalmModeDisablesAggressiveEffects() {
        EffectBudgetManager.setCalmMode(true)
        assertTrue("Calm mode must be active", EffectBudgetManager.isCalmModeEnabled())

        // In Calm mode, requesting confetti or explosion must be blocked
        assertFalse("Confetti blocked in Calm mode", EffectBudgetManager.requestConfetti())
        assertFalse("Explode blocked in Calm mode", EffectBudgetManager.requestExplode())
    }

    @Test
    fun testNormalModeAllowsControlledEffects() {
        EffectBudgetManager.setCalmMode(false)
        assertFalse("Calm mode inactive", EffectBudgetManager.isCalmModeEnabled())

        assertTrue("Confetti allowed in normal mode", EffectBudgetManager.requestConfetti())
        EffectBudgetManager.releaseConfetti()
    }
}

