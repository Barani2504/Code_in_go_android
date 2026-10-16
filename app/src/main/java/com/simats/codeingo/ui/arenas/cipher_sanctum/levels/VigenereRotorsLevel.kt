package com.simats.codeingo.ui.arenas.cipher_sanctum.levels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.simats.codeingo.ui.components.Duolingo3DButton
import com.simats.codeingo.ui.components.Duolingo3DButtonStyle
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

@Composable
fun VigenereRotorsLevel(onComplete: () -> Unit) {
    var step by remember { mutableIntStateOf(0) }
    val plaintextWord = "SANCTUM"
    val secretKey = "KEYKEYK"

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
                text = "🔄 Vigenère Rotors: The secret keyword repeats across the plaintext stream to defeat single-character frequency analysis.",
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = LocalDynamicThemeColors.current.textPrimary,
                textAlign = TextAlign.Center
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            plaintextWord.forEachIndexed { i, pChar ->
                val kChar = secretKey[i]
                val isAligned = i <= step
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isAligned) AmberGold.copy(alpha = 0.2f) else if (LocalDynamicThemeColors.current.isDark) Color(0xFF142032) else LocalDynamicThemeColors.current.inputBackground)
                        .border(1.dp, if (isAligned) AmberGold else Color.Transparent, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Text(text = "$pChar", fontSize = 16.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
                    Text(text = "↓", fontSize = 10.sp, color = AmberGold)
                    Text(text = if (isAligned) "$kChar" else "?", fontSize = 14.sp, fontWeight = FontWeight.Black, color = AmberGold)
                }
            }
        }

        Duolingo3DButton(
            title = if (step < plaintextWord.length - 1) "ALIGN NEXT KEY SYMBOL (${step + 1}/${plaintextWord.length})" else "KEYSTREAM ALIGNED! ✓",
            style = Duolingo3DButtonStyle.GREEN,
            onClick = {
                if (step < plaintextWord.length - 1) {
                    step++
                } else {
                    onComplete()
                }
            },
            modifier = Modifier.fillMaxWidth(0.85f)
        )
    }
}
