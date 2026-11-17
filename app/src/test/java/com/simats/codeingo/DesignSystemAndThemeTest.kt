package com.simats.codeingo

import com.simats.codeingo.ui.components.AshButtonStyle
import com.simats.codeingo.ui.theme.AshDarkPalette
import com.simats.codeingo.ui.theme.AshLightPalette
import com.simats.codeingo.ui.theme.AshType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DesignSystemAndThemeTest {

    @Test
    fun testSevenColorPaletteIntegrity() {
        // Dark palette roles
        assertNotNull(AshDarkPalette.bg)
        assertNotNull(AshDarkPalette.surface)
        assertNotNull(AshDarkPalette.ink)
        assertNotNull(AshDarkPalette.inkMuted)
        assertNotNull(AshDarkPalette.brand)
        assertNotNull(AshDarkPalette.accent)
        assertNotNull(AshDarkPalette.success)
        assertNotNull(AshDarkPalette.danger)
        assertNotNull(AshDarkPalette.onBrand)
        assertTrue(AshDarkPalette.isDark)

        // Light palette roles
        assertNotNull(AshLightPalette.bg)
        assertNotNull(AshLightPalette.surface)
        assertNotNull(AshLightPalette.ink)
        assertNotNull(AshLightPalette.inkMuted)
        assertNotNull(AshLightPalette.brand)
        assertNotNull(AshLightPalette.accent)
        assertNotNull(AshLightPalette.success)
        assertNotNull(AshLightPalette.danger)
        assertNotNull(AshLightPalette.onBrand)
        assertFalse(AshLightPalette.isDark)
    }

    @Test
    fun testAshTypeScaleConstraints() {
        // Enforce minimum 12sp rule across all typography tokens
        assertTrue(AshType.display.fontSize.value >= 12f)
        assertTrue(AshType.title.fontSize.value >= 12f)
        assertTrue(AshType.heading.fontSize.value >= 12f)
        assertTrue(AshType.body.fontSize.value >= 15f)
        assertTrue(AshType.bodySmall.fontSize.value >= 12f)
        assertTrue(AshType.label.fontSize.value >= 12f)
        assertTrue(AshType.caption.fontSize.value >= 12f)

        // Verify exact values from spec
        assertEquals(32f, AshType.display.fontSize.value, 0.1f)
        assertEquals(22f, AshType.title.fontSize.value, 0.1f)
        assertEquals(18f, AshType.heading.fontSize.value, 0.1f)
        assertEquals(16f, AshType.body.fontSize.value, 0.1f)
        assertEquals(14f, AshType.bodySmall.fontSize.value, 0.1f)
        assertEquals(14f, AshType.label.fontSize.value, 0.1f)
        assertEquals(12f, AshType.caption.fontSize.value, 0.1f)
    }

    @Test
    fun testAshButtonStyleEnumValues() {
        val styles = AshButtonStyle.values()
        assertTrue(styles.contains(AshButtonStyle.BRAND))
        assertTrue(styles.contains(AshButtonStyle.SURFACE))
        assertTrue(styles.contains(AshButtonStyle.SUCCESS))
        assertTrue(styles.contains(AshButtonStyle.DANGER))
        assertTrue(styles.contains(AshButtonStyle.DISABLED))
    }
}
