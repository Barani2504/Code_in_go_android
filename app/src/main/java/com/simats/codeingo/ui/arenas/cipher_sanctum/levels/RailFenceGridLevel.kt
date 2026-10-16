package com.simats.codeingo.ui.arenas.cipher_sanctum.levels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

@Composable
fun RailFenceGridLevel(onComplete: () -> Unit) {
    val blocks = remember { mutableStateListOf("EXT ", "CIPH", "DONE", "ER T") }
    var selectedIdx by remember { mutableStateOf<Int?>(null) }
    val correctOrder = listOf("CIPH", "ER T", "EXT ", "DONE")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(if (LocalDynamicThemeColors.current.isDark) Color(0xFF0F1A2E) else LocalDynamicThemeColors.current.cardBackground)
                .border(1.dp, AmberGold.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "👆 Rail-Fence Grid: Reorder the scrambled columns to recover the secret plaintext.",
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = LocalDynamicThemeColors.current.textPrimary,
                textAlign = TextAlign.Center
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            blocks.forEachIndexed { i, block ->
                val isSelected = selectedIdx == i
                Box(
                    modifier = Modifier
                        .size(68.dp, 60.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) AmberGold else if (LocalDynamicThemeColors.current.isDark) Color(0xFF14243C) else LocalDynamicThemeColors.current.inputBackground)
                        .border(1.5.dp, if (isSelected) Color.White else AmberGold.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .clickable {
                            if (selectedIdx == null) {
                                selectedIdx = i
                            } else {
                                val first = selectedIdx!!
                                val temp = blocks[first]
                                blocks[first] = blocks[i]
                                blocks[i] = temp
                                selectedIdx = null
                                if (blocks.toList() == correctOrder) {
                                    onComplete()
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = block, fontSize = 14.sp, fontWeight = FontWeight.Black, color = if (isSelected) Color.Black else LocalDynamicThemeColors.current.textPrimary)
                }
            }
        }
    }
}
