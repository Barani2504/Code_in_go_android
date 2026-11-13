package com.simats.codeingo.ui.challenge

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.ui.components.motion.pressSquish
import com.simats.codeingo.ui.motion.SoundEffectsManager
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.PhoenixCrimson
import com.simats.codeingo.ui.theme.PhoenixGreen
import com.simats.codeingo.ui.theme.SubtextGray
import java.util.Calendar

/**
 * 🛡️ Daily Cipher & Wordle-Style Spoiler-Free Share Grid
 *
 * Implements Phase 4.4:
 * - Deterministic date-seeded daily puzzle identical for all learners globally
 * - Generates Wordle-style spoiler-free emoji share grid:
 *     AshCrypt Daily Cipher #42 🛡️
 *     🟩🟨🟩🟩
 *     Solved in 3 attempts!
 */
@Composable
fun DailyCipherChallengeCard(
    modifier: Modifier = Modifier,
    onCompleted: (xpEarned: Int) -> Unit = {}
) {
    val context = LocalContext.current
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

    // Daily Cipher Puzzle Bank (seeded by day of year)
    val dailyPuzzles = listOf(
        DailyPuzzle("PHOENIX", "SKRHQLA", 3, "Classic Caesar Shift of +3"),
        DailyPuzzle("CYBER", "HCGJX", 5, "Caesar Shift of +5"),
        DailyPuzzle("DIFFIE", "GLIILH", 3, "Caesar Shift of +3"),
        DailyPuzzle("SHANNON", "VKDQQrq", 3, "Caesar Shift of +3"),
        DailyPuzzle("TURING", "WXULQJ", 3, "Caesar Shift of +3")
    )
    val activePuzzle = dailyPuzzles[dayOfYear % dailyPuzzles.size]

    var userGuess by remember { mutableStateOf("") }
    var attempts by remember { mutableIntStateOf(0) }
    var isSolved by remember { mutableStateOf(false) }
    var guessFeedbackGrid by remember { mutableStateOf<List<List<Char>>>(emptyList()) }

    val cardBg = if (isDark) Color(0xFF111827) else Color.White
    val borderColor = if (isDark) Color(0xFF1F2937) else Color(0xFFE2E8F0)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(cardBg)
            .border(1.5.dp, AmberGold.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("🛡️", fontSize = 16.sp)
                    Text(
                        text = "DAILY CIPHER #$dayOfYear",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = AmberGold
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AmberGold.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (isSolved) "COMPLETED" else "NEW TODAY",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isSolved) PhoenixGreen else AmberGold
                    )
                }
            }

            // Ciphertext Clue
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = "Intercepted Ciphertext:", fontSize = 10.sp, color = SubtextGray)
                Text(
                    text = activePuzzle.ciphertext,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = AmberGold
                )
                Text(
                    text = "Hint: ${activePuzzle.hint}",
                    fontSize = 10.5.sp,
                    color = dynamicColors.textSecondary
                )
            }

            if (!isSolved) {
                // Input Field
                OutlinedTextField(
                    value = userGuess,
                    onValueChange = { userGuess = it.uppercase().take(activePuzzle.plaintext.length) },
                    label = { Text("Enter Decrypted Plaintext (${activePuzzle.plaintext.length} Letters)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberGold,
                        unfocusedBorderColor = borderColor,
                        focusedTextColor = dynamicColors.textPrimary,
                        unfocusedTextColor = dynamicColors.textPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Submit Guess Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pressSquish {
                            if (userGuess.length == activePuzzle.plaintext.length) {
                                attempts++
                                val feedback = userGuess.mapIndexed { i, c ->
                                    if (c == activePuzzle.plaintext[i]) 'G' // Green
                                    else if (activePuzzle.plaintext.contains(c)) 'Y' // Yellow
                                    else 'B' // Black/Gray
                                }
                                guessFeedbackGrid = guessFeedbackGrid + listOf(feedback)

                                if (userGuess == activePuzzle.plaintext) {
                                    isSolved = true
                                    SoundEffectsManager.playLevelComplete()
                                    GameManager.instance.addXP(30)
                                    onCompleted(30)
                                } else {
                                    SoundEffectsManager.playWrong()
                                }
                            }
                        }
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (userGuess.length == activePuzzle.plaintext.length) PhoenixGreen else SubtextGray)
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "DECRYPT CIPHER (ATTEMPT ${attempts + 1})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            } else {
                // Solved Celebration & Share Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(PhoenixGreen.copy(alpha = 0.12f))
                        .border(1.2.dp, PhoenixGreen, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🎉 CIPHER CRACKED! Plaintext: ${activePuzzle.plaintext}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = PhoenixGreen
                    )
                    Text(
                        text = "+30 XP Awarded • Solved in $attempts attempt(s)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = dynamicColors.textPrimary
                    )

                    // Wordle-Style Share Button
                    val shareGridText = buildString {
                        appendLine("AshCrypt Daily Cipher #$dayOfYear 🛡️")
                        appendLine("Solved in $attempts attempt(s)")
                        guessFeedbackGrid.forEach { row ->
                            row.forEach { f ->
                                append(if (f == 'G') "🟩" else if (f == 'Y') "🟨" else "⬛")
                            }
                            appendLine()
                        }
                        append("Play at ashcrypt.app")
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pressSquish {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("AshCrypt Daily Cipher", shareGridText))
                                Toast.makeText(context, "Copied spoiler-free grid to clipboard!", Toast.LENGTH_SHORT).show()
                            }
                            .clip(RoundedCornerShape(10.dp))
                            .background(AmberGold)
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = Color.Black, modifier = Modifier.size(16.dp))
                            Text(text = "SHARE SPOILER-FREE RESULT", fontSize = 11.5.sp, fontWeight = FontWeight.Black, color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}

private data class DailyPuzzle(
    val plaintext: String,
    val ciphertext: String,
    val shift: Int,
    val hint: String
)
