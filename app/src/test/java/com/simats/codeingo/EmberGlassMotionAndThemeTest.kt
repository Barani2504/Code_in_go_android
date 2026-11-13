package com.simats.codeingo

import androidx.compose.ui.graphics.Color
import com.simats.codeingo.ui.theme.AshThemeMode
import com.simats.codeingo.ui.theme.createAshColors
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 🧪 EmberGlassMotionAndThemeTest
 * Tests verifying the Ember Glass Design System:
 * 1. Palette tokens across Ember Dark, Daylight, and AMOLED modes.
 * 2. Theme isolation and contrast rules.
 */
class EmberGlassMotionAndThemeTest {

    @Test
    fun testEmberDarkTheme_colors() {
        val darkTokens = createAshColors(AshThemeMode.EMBER_DARK)
        assertTrue(darkTokens.isDark)
        assertNotEquals(darkTokens.background, darkTokens.onSurface)
    }

    @Test
    fun testDaylightTheme_colors() {
        val daylightTokens = createAshColors(AshThemeMode.DAYLIGHT)
        assertFalse(daylightTokens.isDark)
        assertNotEquals(daylightTokens.background, daylightTokens.onSurface)
    }

    @Test
    fun testAmoledTheme_trueBlack() {
        val amoledTokens = createAshColors(AshThemeMode.AMOLED)
        assertTrue(amoledTokens.isDark)
        assertEquals(Color(0xFF000000), amoledTokens.background)
        assertEquals(0f, amoledTokens.background.red, 0.001f)
        assertEquals(0f, amoledTokens.background.green, 0.001f)
        assertEquals(0f, amoledTokens.background.blue, 0.001f)
    }
}

