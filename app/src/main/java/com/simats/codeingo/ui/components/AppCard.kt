package com.simats.codeingo.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.liquidGlassCard

// ══════════════════════════════════════════════════════════════════
// 🪟 AppCard — Liquid Glass Card Container
// ══════════════════════════════════════════════════════════════════

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 22.dp,
    accentGlow: Color = AmberGold.copy(alpha = 0.15f),
    fillOpacity: Float = 0.60f,
    contentPadding: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .liquidGlassCard(
                cornerRadius = cornerRadius,
                accentGlow = accentGlow,
                fillOpacity = fillOpacity
            )
            .padding(contentPadding),
        content = content
    )
}
