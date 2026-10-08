package com.simats.codeingo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.domain.LocalizationManager
import com.simats.codeingo.ui.phoenix.PhoenixDynamicLogoView
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

// ══════════════════════════════════════════════════════════════════
// 📱 HeaderView — Top Bar with Brand Logo & Language Selector Pill
// Exact Parity with iOS HeaderView.swift
// ══════════════════════════════════════════════════════════════════

@Composable
fun HeaderView(
    modifier: Modifier = Modifier,
    onOpenLanguagePicker: () -> Unit = {},
    onOpenLogin: () -> Unit = {}
) {
    val localizationManager = LocalizationManager.instance
    val selectedLanguage by localizationManager.selectedLanguage.collectAsState()
    val isDark = LocalDynamicThemeColors.current.isDark

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(if (isDark) Color(0xFF0D1626).copy(alpha = 0.95f) else Color.White.copy(alpha = 0.95f))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Interactive Phoenix Logo (Clean branding for guest)
            PhoenixDynamicLogoView(
                showTitle = true,
                showSubtitleBadge = false,
                size = 32.dp,
                enableTapSheet = false
            )

            Spacer(modifier = Modifier.weight(1f))

            // Language Picker Button - Liquid Glass Pill (Duolingo Style)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.04f))
                    .border(1.dp, if (isDark) Color.White.copy(alpha = 0.20f) else Color(0xFFD7DEEB), RoundedCornerShape(100.dp))
                    .clickable { onOpenLanguagePicker() }
                    .padding(horizontal = 11.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = null,
                    tint = if (isDark) Color.White else Color(0xFF182030),
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                if (selectedLanguage != null) {
                    Text(
                        text = selectedLanguage!!.flagEmoji,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = selectedLanguage!!.name.uppercase(),
                        color = LocalDynamicThemeColors.current.textPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif
                    )
                } else {
                    Text(
                        text = "SITE LANGUAGE",
                        color = LocalDynamicThemeColors.current.textPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }
        }

        // Bottom border gradient divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.horizontalGradient(
                        colors = if (isDark) listOf(
                            Color.White.copy(alpha = 0.35f),
                            AmberGold.copy(alpha = 0.30f),
                            Color.Transparent
                        ) else listOf(
                            Color(0xFFD7DEEB),
                            AmberGold.copy(alpha = 0.30f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}
