package com.simats.codeingo.motion

import com.simats.codeingo.ui.components.motion.EffectBudgetManager
import com.simats.codeingo.ui.components.motion.MotionEffectType
import com.simats.codeingo.ui.components.motion.MotionTier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit Tests for AshCrypt MotionTier Rules and EffectBudgetManager (Part 1 & Part 7).
 */
class MotionTierBudgetTest {

    @Before
    fun setUp() {
        EffectBudgetManager.setCalmMode(false)
        EffectBudgetManager.setQuietHoursEnabled(false)
        EffectBudgetManager.releaseConfetti()
        EffectBudgetManager.releaseExplosion()
        EffectBudgetManager.releaseExplosion()
    }

    @Test
    fun `test MotionTier T1 allows only tactile micro interactions`() {
        val t1 = MotionTier.T1_MICRO
        assertTrue("T1 must allow haptic tick", t1.allows(MotionEffectType.HAPTIC_TICK))
        assertTrue("T1 must allow spring press", t1.allows(MotionEffectType.SPRING_PRESS))
        assertTrue("T1 must allow LED indicator", t1.allows(MotionEffectType.LED_INDICATOR))

        assertFalse("T1 must not allow glow border", t1.allows(MotionEffectType.GLOW_BORDER))
        assertFalse("T1 must not allow error shake", t1.allows(MotionEffectType.ERROR_SHAKE))
        assertFalse("T1 must not allow confetti burst", t1.allows(MotionEffectType.CONFETTI_BURST))
        assertFalse("T1 must not allow full screen fanfare", t1.allows(MotionEffectType.FULL_SCREEN_FANFARE))
    }

    @Test
    fun `test MotionTier T2 allows answer feedback but not celebrations`() {
        val t2 = MotionTier.T2_ANSWER
        assertTrue("T2 must allow glow border", t2.allows(MotionEffectType.GLOW_BORDER))
        assertTrue("T2 must allow error shake", t2.allows(MotionEffectType.ERROR_SHAKE))
        assertTrue("T2 must allow token shatter", t2.allows(MotionEffectType.TOKEN_SHATTER))
        assertTrue("T2 must allow local burst", t2.allows(MotionEffectType.LOCAL_BURST))

        assertFalse("T2 must not allow confetti burst", t2.allows(MotionEffectType.CONFETTI_BURST))
        assertFalse("T2 must not allow number roll", t2.allows(MotionEffectType.NUMBER_ROLL))
        assertFalse("T2 must not allow full screen fanfare", t2.allows(MotionEffectType.FULL_SCREEN_FANFARE))
    }

    @Test
    fun `test MotionTier T3 allows session victory but not hero ceremonies`() {
        val t3 = MotionTier.T3_LEVEL_COMPLETE
        assertTrue("T3 must allow confetti burst", t3.allows(MotionEffectType.CONFETTI_BURST))
        assertTrue("T3 must allow number roll", t3.allows(MotionEffectType.NUMBER_ROLL))
        assertTrue("T3 must allow star rating pop", t3.allows(MotionEffectType.STAR_RATING_POP))

        assertFalse("T3 must not allow full screen fanfare", t3.allows(MotionEffectType.FULL_SCREEN_FANFARE))
        assertFalse("T3 must not allow boss shockwave", t3.allows(MotionEffectType.BOSS_SHOCKWAVE))
    }

    @Test
    fun `test MotionTier T4 allows hero milestone ceremonies`() {
        val t4 = MotionTier.T4_MILESTONE
        assertTrue("T4 must allow full screen fanfare", t4.allows(MotionEffectType.FULL_SCREEN_FANFARE))
        assertTrue("T4 must allow boss shockwave", t4.allows(MotionEffectType.BOSS_SHOCKWAVE))
        assertTrue("T4 must allow mascot evolution", t4.allows(MotionEffectType.MASCOT_EVOLUTION_CEREMONY))
        assertTrue("T4 must allow confetti burst", t4.allows(MotionEffectType.CONFETTI_BURST))
    }

    @Test
    fun `test EffectBudgetManager enforces single concurrent confetti system`() {
        assertTrue("First confetti acquisition must succeed", EffectBudgetManager.acquireConfetti())
        assertFalse("Second concurrent confetti acquisition must be denied", EffectBudgetManager.acquireConfetti())

        EffectBudgetManager.releaseConfetti()
        assertTrue("Confetti acquisition after release must succeed", EffectBudgetManager.acquireConfetti())
    }

    @Test
    fun `test EffectBudgetManager caps explosion instances to maximum two`() {
        assertTrue("First explosion must succeed", EffectBudgetManager.acquireExplosion())
        assertTrue("Second explosion must succeed", EffectBudgetManager.acquireExplosion())
        assertFalse("Third explosion must be rejected by budget cap", EffectBudgetManager.acquireExplosion())

        EffectBudgetManager.releaseExplosion()
        assertTrue("Explosion acquisition after release must succeed", EffectBudgetManager.acquireExplosion())
    }

    @Test
    fun `test Calm Mode strictly suppresses all sensory hazard effects`() {
        EffectBudgetManager.setCalmMode(true)

        assertFalse("Confetti must be completely blocked in Calm Mode", EffectBudgetManager.acquireConfetti())
        assertFalse("Explosions must be completely blocked in Calm Mode", EffectBudgetManager.acquireExplosion())
        assertFalse("Visual shakes must be blocked in Calm Mode", EffectBudgetManager.isShakeAllowed())
        assertFalse("Visual flashes must be blocked in Calm Mode", EffectBudgetManager.isFlashAllowed())
        assertEquals("Allowed particles in Calm Mode must be zero", 0, EffectBudgetManager.maxAllowedParticles(null))
    }
}

