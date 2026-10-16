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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import kotlinx.coroutines.delay

@Composable
fun CryptanalysisSprintLevel(onComplete: () -> Unit, onDeductHeart: () -> Unit) {
    var secondsLeft by remember { mutableIntStateOf(20) }
    var solvedCount by remember { mutableIntStateOf(0) }
    var currentProblem by remember { mutableStateOf("Shift 'K' by -3:" to 'H') }

    LaunchedEffect(Unit) {
        while (secondsLeft > 0 && solvedCount < 3) {
            delay(1000)
            secondsLeft--
        }
        if (secondsLeft == 0 && solvedCount < 3) {
            onDeductHeart()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (secondsLeft < 5) DuolingoRed else AmberGold)
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = "⏱️ $secondsLeft SECONDS REMAINING (${solvedCount}/3)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(if (LocalDynamicThemeColors.current.isDark) Color(0xFF0F1A2E) else LocalDynamicThemeColors.current.cardBackground)
                .border(1.dp, AmberGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = currentProblem.first, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = LocalDynamicThemeColors.current.textPrimary)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf('G', 'H', 'I', 'J').forEach { opt ->
                Box(
                    modifier = Modifier
                        .size(60.dp, 56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (LocalDynamicThemeColors.current.isDark) Color(0xFF14243C) else LocalDynamicThemeColors.current.inputBackground)
                        .border(1.5.dp, AmberGold.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .clickable {
                            if (opt == currentProblem.second) {
                                solvedCount++
                                if (solvedCount >= 3) {
                                    onComplete()
                                } else {
                                    currentProblem = if (solvedCount == 1) "Shift 'D' by -3:" to 'A' else "Shift 'O' by -3:" to 'L'
                                }
                            } else {
                                onDeductHeart()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "$opt", fontSize = 18.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
                }
            }
        }
    }
}
