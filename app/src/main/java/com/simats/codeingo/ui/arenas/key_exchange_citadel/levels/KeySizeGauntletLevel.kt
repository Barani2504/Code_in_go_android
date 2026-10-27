package com.simats.codeingo.ui.arenas.key_exchange_citadel.levels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.simats.codeingo.ui.components.Tactile3DButton
import com.simats.codeingo.ui.components.Tactile3DButtonColor
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.ArenaDangerRed
import com.simats.codeingo.ui.theme.ArenaSuccessGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

data class KeySizeQuestion(
    val prompt: String,
    val securityLevel: String,
    val correctKey: String,
    val options: List<String>,
    val explanation: String
)

@Composable
fun KeySizeGauntletLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current

    // NIST SP 800-57 Key Size Equivalences Gauntlet
    val questions = remember {
        listOf(
            KeySizeQuestion(
                prompt = "For AES-128 (128-bit security), what minimum RSA key size does NIST SP 800-57 mandate?",
                securityLevel = "128-bit Security Tier",
                correctKey = "RSA-3072",
                options = listOf("RSA-3072", "RSA-2048", "RSA-1024", "RSA-512"),
                explanation = "NIST SP 800-57 requires RSA-3072 to match 128-bit symmetric security. RSA-2048 only offers 112 bits!"
            ),
            KeySizeQuestion(
                prompt = "To achieve 128-bit security equivalent to RSA-3072, what ECC key size is required?",
                securityLevel = "128-bit Security Tier",
                correctKey = "ECC-256 (secp256r1)",
                options = listOf("ECC-256 (secp256r1)", "ECC-128", "ECC-512", "ECC-3072"),
                explanation = "Elliptic curves achieve 128-bit security with only 256-bit keys (2n rule against Pollard's rho)!"
            ),
            KeySizeQuestion(
                prompt = "For AES-256 (256-bit security tier), what gargantuan RSA key size is required?",
                securityLevel = "256-bit Security Tier",
                correctKey = "RSA-15360",
                options = listOf("RSA-15360", "RSA-4096", "RSA-8192", "RSA-2048"),
                explanation = "NIST mandates an impractical RSA-15360 to match 256-bit security, showing why ECC-512 is favored!"
            )
        )
    }

    var roundIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    val currentQ = questions.getOrNull(roundIndex)
    val isFinished = roundIndex >= questions.size

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "NIST SP 800-57 KEY-SIZE GAUNTLET",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Symmetric vs Asymmetric Security Bit Equivalency",
                fontSize = 11.sp,
                color = dynamicColors.textSecondary,
                textAlign = TextAlign.Center
            )
        }

        if (currentQ != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(dynamicColors.cardBorder.copy(alpha = 0.2f))
                    .border(1.dp, AmberGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "GAUNTLET ROUND ${roundIndex + 1} / 3: ${currentQ.securityLevel}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = AmberGold
                    )
                    Text(
                        text = currentQ.prompt,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = dynamicColors.textPrimary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Options (48dp touch targets)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                currentQ.options.chunked(2).forEach { rowOpts ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        rowOpts.forEach { opt ->
                            val isSelected = selectedOption == opt
                            val isCorrect = opt == currentQ.correctKey

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) {
                                            if (isCorrect) ArenaSuccessGreen else ArenaDangerRed
                                        } else dynamicColors.cardBorder.copy(alpha = 0.25f)
                                    )
                                    .border(
                                        1.5.dp,
                                        if (isSelected) Color.White else AmberGold.copy(alpha = 0.4f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        selectedOption = opt
                                        if (isCorrect) {
                                            onActionSuccess()
                                            feedbackMessage = "✓ Correct! ${currentQ.explanation}"
                                            val nextRound = roundIndex + 1
                                            if (nextRound >= questions.size) {
                                                roundIndex = nextRound
                                                onComplete()
                                            } else {
                                                roundIndex = nextRound
                                                selectedOption = null
                                            }
                                        } else {
                                            onDeductHeart()
                                            feedbackMessage = "❌ Incorrect size: ${currentQ.explanation}"
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = opt,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (isSelected && isCorrect) Color.Black else dynamicColors.textPrimary
                                )
                            }
                        }
                    }
                }
            }

            if (feedbackMessage != null) {
                Text(
                    text = feedbackMessage!!,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (feedbackMessage!!.startsWith("✓")) ArenaSuccessGreen else ArenaDangerRed,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // All cleared
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(ArenaSuccessGreen.copy(alpha = 0.15f))
                    .border(1.dp, ArenaSuccessGreen, RoundedCornerShape(14.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🏆 NIST SP 800-57 Key-Size Equivalencies Mastered!",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = ArenaSuccessGreen
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Tactile3DButton(
                title = "Proceed to Boss Chamber ➔",
                style = Tactile3DButtonColor.GREEN,
                onClick = onComplete,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
