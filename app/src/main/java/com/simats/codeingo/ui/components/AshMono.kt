package com.simats.codeingo.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.AshTheme

/**
 * Dedicated Monospace Text component for AshCrypt (Phase 1).
 * Used exclusively for ciphertext, hex, keys, and code data representations.
 * Guaranteed minimum font size of 12sp.
 */
@Composable
fun AshMono(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = AshTheme.colors.accent,
    fontSize: TextUnit = 14.sp,
    fontWeight: FontWeight = FontWeight.SemiBold,
    style: TextStyle = TextStyle.Default
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = if (fontSize < 12.sp) 12.sp else fontSize,
        fontWeight = fontWeight,
        fontFamily = FontFamily.Monospace,
        style = style
    )
}
