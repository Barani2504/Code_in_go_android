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
import com.simats.codeingo.ui.arenas.crypto.ArenaCryptoEngine
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.ArenaDangerRed
import com.simats.codeingo.ui.theme.ArenaSuccessGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray

enum class SphinxPhase(val phaseNumber: Int) {
    PHASE_1(1),
    PHASE_2(2),
    PHASE_3(3)
}

data class SphinxChoice(
    val id: String,
    val text: String,
    val isCorrect: Boolean,
    val explanation: String
)

data class SphinxRiddle(
    val phase: SphinxPhase,
    val ciphertext: String,
    val plaintext: String,
    val shift: Int,
    val choices: List<SphinxChoice>
)

@Composable
fun SphinxBossBattleLevel(onDefeatBoss: () -> Unit, onDeductHeart: () -> Unit) {
    var bossHP by remember { mutableIntStateOf(100) }
    var currentPhase by remember { mutableStateOf(SphinxPhase.PHASE_1) }
    var selectedChoiceId by remember { mutableStateOf<String?>(null) }
    var feedbackMessage by remember {
        mutableStateOf("The Sphinx challenges you: Decrypt Caesar shift K=3 to shatter the shadow ward!")
    }

    // Verified via ArenaCryptoEngine: Caesar shift K=3
    // MASTER -> PDVWHU (6 letters)
    // SECRET -> VHFUHW (6 letters)
    // SHADOW -> VKDGRZ (6 letters)
    val riddles = remember {
        listOf(
            SphinxRiddle(
                phase = SphinxPhase.PHASE_1,
                ciphertext = ArenaCryptoEngine.caesarEncrypt("MASTER", 3), // "PDVWHU"
                plaintext = "MASTER",
                shift = 3,
                choices = listOf(
                    SphinxChoice("c1_correct", "MASTER", true, "Correct: P-3=M, D-3=A, V-3=S, W-3=T, H-3=E, U-3=R"),
                    SphinxChoice("c1_d1", "MISTER", false, "Misconception: 'I' shifts to 'L', not 'D' (A + 3 = D)."),
                    SphinxChoice("c1_d2", "MONSTER", false, "Misconception: 'MONSTER' has 7 letters; ciphertext 'PDVWHU' has only 6!"),
                    SphinxChoice("c1_d3", "MATRIX", false, "Misconception: 'T','R','I','X' do not decrypt from 'V','W','H','U'.")
                )
            ),
            SphinxRiddle(
                phase = SphinxPhase.PHASE_2,
                ciphertext = ArenaCryptoEngine.caesarEncrypt("SECRET", 3), // "VHFUHW"
                plaintext = "SECRET",
                shift = 3,
                choices = listOf(
                    SphinxChoice("c2_correct", "SECRET", true, "Correct: V-3=S, H-3=E, F-3=C, U-3=R, H-3=E, W-3=T"),
                    SphinxChoice("c2_d1", "SECURE", false, "Misconception: 'U' encrypted under K=3 yields 'X', not 'U'."),
                    SphinxChoice("c2_d2", "SILENT", false, "Misconception: 'I','L','N' do not match ciphertext positions."),
                    SphinxChoice("c2_d3", "SHADOW", false, "Misconception: 'SHADOW' is the Phase 3 plaintext ward!")
                )
            ),
            SphinxRiddle(
                phase = SphinxPhase.PHASE_3,
                ciphertext = ArenaCryptoEngine.caesarEncrypt("SHADOW", 3), // "VKDGRZ"
                plaintext = "SHADOW",
                shift = 3,
                choices = listOf(
                    SphinxChoice("c3_correct", "SHADOW", true, "Correct: V-3=S, K-3=H, D-3=A, G-3=D, R-3=O, Z-3=W"),
                    SphinxChoice("c3_d1", "SIGNAL", false, "Misconception: 'I','N','L' do not map from 'K','D','R'."),
                    SphinxChoice("c3_d2", "SPHINX", false, "Misconception: 'P','H','I' do not map from 'K','D','G'."),
                    SphinxChoice("c3_d3", "SYSTEM", false, "Misconception: 'Y','T','E','M' do not map from 'K','D','G','R'.")
                )
            )
        )
    }

    val currentRiddle = riddles.first { it.phase == currentPhase }
    // Seeded shuffle so options are consistently randomized per phase attempt
    val shuffledChoices = remember(currentPhase) {
        currentRiddle.choices.shuffled()
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🦅 THE SPHINX OF SHADOWS",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = ArenaDangerRed,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "PHASE ${currentPhase.phaseNumber} / 3",
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
                        .background(ArenaDangerRed)
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
                .border(1.5.dp, ArenaDangerRed.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "RUNE CIPHERTEXT: [ ${currentRiddle.ciphertext} ] (K = ${currentRiddle.shift})",
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

        // Decryption Answer Choices (48dp+ touch target compliant)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            shuffledChoices.chunked(2).forEach { rowOptions ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    rowOptions.forEach { choice ->
                        val isSelected = selectedChoiceId == choice.id
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) {
                                        if (choice.isCorrect) ArenaSuccessGreen else ArenaDangerRed
                                    } else if (LocalDynamicThemeColors.current.isDark) {
                                        Color(0xFF14243C)
                                    } else {
                                        LocalDynamicThemeColors.current.inputBackground
                                    }
                                )
                                .border(
                                    1.5.dp,
                                    if (isSelected) Color.White else AmberGold.copy(alpha = 0.4f),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    selectedChoiceId = choice.id
                                    if (choice.isCorrect) {
                                        val newHP = maxOf(0, bossHP - 35)
                                        bossHP = newHP
                                        if (newHP <= 0 || currentPhase == SphinxPhase.PHASE_3) {
                                            onDefeatBoss()
                                        } else {
                                            currentPhase = when (currentPhase) {
                                                SphinxPhase.PHASE_1 -> SphinxPhase.PHASE_2
                                                SphinxPhase.PHASE_2 -> SphinxPhase.PHASE_3
                                                SphinxPhase.PHASE_3 -> SphinxPhase.PHASE_3
                                            }
                                            selectedChoiceId = null
                                            feedbackMessage = "💥 Critical strike! ${choice.explanation}"
                                        }
                                    } else {
                                        onDeductHeart()
                                        feedbackMessage = "❌ Counterattack: ${choice.explanation}"
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = choice.text,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isSelected && choice.isCorrect) Color.Black else LocalDynamicThemeColors.current.textPrimary,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}
