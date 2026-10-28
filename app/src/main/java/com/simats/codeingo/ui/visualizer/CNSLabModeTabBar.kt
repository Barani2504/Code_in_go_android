package com.simats.codeingo.ui.visualizer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DsaBlue
import com.simats.codeingo.ui.theme.InputBorder
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.visualizer.model.CNSLabMode

@Composable
fun CNSLabModeTabBar(
    selectedMode: CNSLabMode,
    onSelectMode: (CNSLabMode) -> Unit,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackground)
            .border(1.2.dp, InputBorder, RoundedCornerShape(16.dp))
            .padding(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CNSLabMode.entries.forEach { mode ->
                val isSelected = selectedMode == mode
                val shape = RoundedCornerShape(12.dp)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp) // Minimum 48dp accessible touch target
                        .clip(shape)
                        .background(
                            if (isSelected) accentColor else (if (isDark) Color.White.copy(alpha = 0.04f) else Color.Black.copy(alpha = 0.03f))
                        )
                        .border(
                            1.dp,
                            if (isSelected) accentColor else InputBorder.copy(alpha = 0.5f),
                            shape
                        )
                        .clickable { onSelectMode(mode) }
                        .padding(horizontal = 4.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = mode.icon,
                            fontSize = 13.sp
                        )
                        Text(
                            text = mode.title,
                            fontSize = 9.5.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                            color = if (isSelected) Color.White else (if (isDark) Color.LightGray else Color(0xFF475569)),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
