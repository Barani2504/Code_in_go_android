package com.simats.codeingo.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.data.model.Language
import com.simats.codeingo.ui.components.AppCard
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.PhoenixGreen
import com.simats.codeingo.ui.theme.pressScale
import com.simats.codeingo.ui.theme.staggeredAppear

// ══════════════════════════════════════════════════════════════════
// 🌐 LanguagePickerSheet — Glassmorphic Language Selector
// ══════════════════════════════════════════════════════════════════

@Composable
fun LanguagePickerSheet(
    selectedLanguageCode: String,
    onLanguageSelected: (Language) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val languages = Language.defaultLanguages
    val isDark = LocalDynamicThemeColors.current.isDark

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp))
            .background(if (isDark) Color(0xFF0D_14_26) else Color.White)
            .padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 32.dp)
    ) {
        // Drag Indicator Pill
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(width = 44.dp, height = 4.dp)
                .clip(RoundedCornerShape(50))
                .background(if (isDark) Color.White.copy(alpha = 0.25f) else Color(0xFFCBD5E1))
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "What language do you code in?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif,
                color = if (isDark) Color.White else Color(0xFF12_18_26),
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = if (isDark) Color.White.copy(alpha = 0.85f) else Color(0xFF12_18_26)
                )
            }
        }

        Text(
            text = "Select your primary language for examples, syntax, and exercises:",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.SansSerif,
            color = if (isDark) Color.White.copy(alpha = 0.65f) else Color(0xFF64_74_8B),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(languages) { language ->
                val isSelected = language.code.equals(selectedLanguageCode, ignoreCase = true)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pressScale(targetScale = 0.97f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isSelected) AmberGold.copy(alpha = if (isDark) 0.22f else 0.15f)
                            else (if (isDark) Color(0xFF14_1F_36).copy(alpha = 0.70f) else Color(0xFFF1_F4_FA))
                        )
                        .border(
                            if (isSelected) 1.8.dp else 1.dp,
                            if (isSelected) AmberGold else (if (isDark) Color.White.copy(alpha = 0.10f) else Color(0xFFD7_DE_EB)),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onLanguageSelected(language)
                        }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = language.flagEmoji,
                        fontSize = 26.sp,
                        modifier = Modifier.padding(end = 14.dp)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = language.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif,
                            color = if (isDark) Color.White else Color(0xFF12_18_26)
                        )
                        Text(
                            text = language.nativeName,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.SansSerif,
                            color = if (isDark) Color.White.copy(alpha = 0.60f) else Color(0xFF64_74_8B)
                        )
                    }

                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Selected",
                            tint = AmberGold,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}
