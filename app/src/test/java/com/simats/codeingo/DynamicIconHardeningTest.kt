package com.simats.codeingo

import com.simats.codeingo.domain.EmotionRule
import com.simats.codeingo.domain.PhoenixEmotionManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DynamicIconHardeningTest {

    @Test
    fun testAllAliases_parityAndIntegrity() {
        // Assert exactly 29 launcher aliases exist (1 default + 28 emotion aliases)
        val aliases = PhoenixEmotionManager.ALL_ALIASES
        assertEquals(29, aliases.size)
        assertEquals("MainActivityAliasDefault", aliases[0])

        val emotionIds = PhoenixEmotionManager.ALL_EMOTION_IDS
        assertEquals(28, emotionIds.size)

        for (id in emotionIds) {
            assertTrue("Alias list must contain MainActivityAlias_$id", aliases.contains("MainActivityAlias_$id"))
        }
    }

    @Test
    fun testEmotionRuleMapping_parityWithIOS() {
        // Verify frozen emotion rule mappings
        assertEquals(6, EmotionRule.STREAK_MILESTONE.targetEmotionId) // Starry Prodigy
        assertEquals(23, EmotionRule.DAILY_URGENCY_NIGHT.targetEmotionId) // Furious Warning
        assertEquals(15, EmotionRule.DAILY_URGENCY_EVE.targetEmotionId) // Worried Ember
        assertEquals(18, EmotionRule.DAILY_NOT_PRACTICED.targetEmotionId) // Pouting Grudge
        assertEquals(17, EmotionRule.ZERO_HEARTS.targetEmotionId) // Heartbroken Cry
        assertEquals(19, EmotionRule.LOW_HEARTS.targetEmotionId) // Relieved Sigh
        assertEquals(21, EmotionRule.HEARTS_REFILLED.targetEmotionId) // Grateful Tears
        assertEquals(6, EmotionRule.PERFECT_SCORE.targetEmotionId) // Starry Prodigy
        assertEquals(13, EmotionRule.HIGH_COMBO.targetEmotionId) // Eureka Spark
        assertEquals(26, EmotionRule.LEADERBOARD_TOP.targetEmotionId) // Crown Champion
        assertEquals(28, EmotionRule.BOSS_BATTLE.targetEmotionId) // Battle Standard
    }

    @Test
    fun testDynamicIconToggle_stateFlow() {
        val manager = PhoenixEmotionManager.instance
        assertTrue("Dynamic icon should default to enabled", manager.isDynamicIconEnabled.value)

        manager.setDynamicIconEnabled(false)
        assertFalse("Dynamic icon toggle disabled", manager.isDynamicIconEnabled.value)

        manager.setDynamicIconEnabled(true)
        assertTrue("Dynamic icon toggle re-enabled", manager.isDynamicIconEnabled.value)
    }

    @Test
    fun testAliasResolutionLogic() {
        // Test parsing of resource names to alias strings
        val resolveAlias: (String?) -> String = { named ->
            val iconName = named ?: "phoenix_emotion_0"
            val rawId = when {
                iconName.startsWith("phoenix_emotion_") -> iconName.removePrefix("phoenix_emotion_").toIntOrNull() ?: 0
                iconName.startsWith("AppIcon_") -> iconName.removePrefix("AppIcon_").toIntOrNull() ?: 0
                iconName.toIntOrNull() != null -> iconName.toInt()
                else -> 0
            }
            val id = if (PhoenixEmotionManager.ALL_EMOTION_IDS.contains(rawId)) rawId else 0
            "MainActivityAlias_$id"
        }

        assertEquals("MainActivityAlias_0", resolveAlias(null))
        assertEquals("MainActivityAlias_6", resolveAlias("phoenix_emotion_6"))
        assertEquals("MainActivityAlias_15", resolveAlias("AppIcon_15"))
        assertEquals("MainActivityAlias_23", resolveAlias("23"))
        assertEquals("MainActivityAlias_0", resolveAlias("invalid_name"))
        assertEquals("MainActivityAlias_0", resolveAlias("phoenix_emotion_999")) // Out of range
    }
}
