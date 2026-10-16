package com.simats.codeingo.ui.arenas.cipher_sanctum.levels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray

@Composable
fun SphinxBossBattleLevel(onDefeatBoss: () -> Unit, onDeductHeart: () -> Unit) {
    var bossHP by remember { mutableIntStateOf(100) }
    var currentPhase by remember { mutableIntStateOf(1) } // 1: "PHDVWHU"->MASTER, 2: "VHFUHW"->SECRET, 3: "VKDGRZ"->SHADOW
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var feedbackMessage by remember { mutableStateOf("The Sphinx challenges you: Decrypt Caesar shift K=3 to shatter the shadow ward!") }

    val phaseData = when (currentPhase) {
        1 -> Triple("PHDVWHU", "MASTER", listOf("MASTER", "MISTER", "MONSTER", "MATRIX"))
        2 -> Triple("VHFUHW", "SECRET", listOf("SECRET", "SECURE", "SILENT", "SHADOW"))
        else -> Triple("VKDGRZ", "SHADOW", listOf("SHADOW", "SIGNAL", "SPHINX", "SYSTEM"))
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Boss Status Bar
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "🦅 THE SPHINX OF SHADOWS",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = DuolingoRed,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "PHASE $currentPhase / 3",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = AmberGold
                )
            }

            // Boss HP Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.1f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(bossHP / 100f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(DuolingoRed)
                )
            }
            Text(text = "$bossHP / 100 HP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SubtextGray)
        }

        // Cipher Riddle Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(if (LocalDynamicThemeColors.current.isDark) Color(0xFF2E1010) else Color(0xFFFFEBEE))
                .border(1.5.dp, DuolingoRed.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "RUNE CIPHERTEXT: [ ${phaseData.first} ] (K = 3)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = AmberGold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp
                )
                Text(
                    text = feedbackMessage,
                    fontSize = 12.sp,
                    color = LocalDynamicThemeColors.current.textPrimary,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Decryption Answer Choices
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            phaseData.third.chunked(2).forEach { rowOptions ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    rowOptions.forEach { option ->
                        val isCorrect = option == phaseData.second
                        val isSelected = selectedAnswer == option
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) {
                                        if (isCorrect) DuolingoGreen else DuolingoRed
                                    } else if (LocalDynamicThemeColors.current.isDark) Color(0xFF14243C) else LocalDynamicThemeColors.current.inputBackground
                                )
                                .border(1.5.dp, if (isSelected) Color.White else AmberGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .clickable {
                                    selectedAnswer = option
                                    if (isCorrect) {
                                        val newHP = maxOf(0, bossHP - 35)
                                        bossHP = newHP
                                        if (newHP <= 0 || currentPhase >= 3) {
                                            onDefeatBoss()
                                        } else {
                                            currentPhase++
                                            selectedAnswer = null
                                            feedbackMessage = "💥 Critical strike! The Sphinx conjures a new ward!"
                                        }
                                    } else {
                                        onDeductHeart()
                                        feedbackMessage = "❌ The Sphinx counterattacks! Shift modularly backwards: P = (C - 3) mod 26."
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = option,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = LocalDynamicThemeColors.current.textPrimary,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}
