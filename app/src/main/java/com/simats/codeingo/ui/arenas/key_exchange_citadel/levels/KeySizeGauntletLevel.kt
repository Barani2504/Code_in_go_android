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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.arenas.common.ArenaBadgeType
import com.simats.codeingo.ui.arenas.common.ArenaHaptics
import com.simats.codeingo.ui.arenas.common.ArenaPalettes
import com.simats.codeingo.ui.arenas.common.ArenaStatusBadge
import com.simats.codeingo.ui.arenas.common.minTouchTarget48
import com.simats.codeingo.ui.components.Tactile3DButton
import com.simats.codeingo.ui.components.Tactile3DButtonColor
import com.simats.codeingo.ui.theme.ArenaDangerRed
import com.simats.codeingo.ui.theme.ArenaSuccessGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.LocalReduceMotion
import com.simats.codeingo.ui.theme.pressScale

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
    val isDark = dynamicColors.isDark
    val reduceMotion = LocalReduceMotion.current
    val view = LocalView.current

    val primary = ArenaPalettes.Arena3Primary
    val accent = ArenaPalettes.Arena3Accent

    // NIST SP 800-57 Key Size Equivalences Gauntlet
    val questions = remember {
        listOf(
            KeySizeQuestion(
                prompt = "For AES-128 (128-bit security tier), what minimum RSA key size does NIST SP 800-57 mandate?",
                securityLevel = "128-bit Security Tier",
                correctKey = "RSA-3072",
                options = listOf("RSA-3072", "RSA-2048", "RSA-1024", "RSA-512"),
                explanation = "NIST mandates RSA-3072 for 128-bit symmetric security. RSA-2048 offers only 112 bits of security!"
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Mission Briefing & Status Badge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(if (isDark) ArenaPalettes.Arena3CardDark else dynamicColors.cardBackground)
                .border(1.2.dp, primary.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "NIST SP 800-57 GAUNTLET",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = primary
                    )

                    ArenaStatusBadge(
                        type = if (currentQ != null) ArenaBadgeType.INFO else ArenaBadgeType.SUCCESS,
                        label = currentQ?.securityLevel ?: "GAUNTLET CLEARED"
                    )
                }

                Text(
                    text = "Symmetric vs Asymmetric Security Bit Equivalency. Match appropriate asymmetric key lengths to the desired symmetric protection tier.",
                    fontSize = 12.sp,
                    color = dynamicColors.textPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }

        if (currentQ != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isDark) ArenaPalettes.Arena3BackgroundDark else Color(0xFFF8FAFC))
                    .border(1.5.dp, primary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "ROUND ${roundIndex + 1} / 3: ${currentQ.securityLevel}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = accent
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

            // Options (48dp+ touch targets enforced)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth(0.95f)) {
                currentQ.options.chunked(2).forEach { rowOpts ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        rowOpts.forEach { opt ->
                            val isSelected = selectedOption == opt
                            val isCorrect = opt == currentQ.correctKey

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .minTouchTarget48()
                                    .height(52.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) {
                                            if (isCorrect) ArenaSuccessGreen else ArenaDangerRed
                                        } else if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
                                    )
                                    .border(
                                        1.5.dp,
                                        if (isSelected) Color.White else primary.copy(alpha = 0.4f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .pressScale()
                                    .clickable {
                                        selectedOption = opt
                                        if (isCorrect) {
                                            ArenaHaptics.success(view, reduceMotion)
                                            onActionSuccess()
                                            feedbackMessage = "✓ Correct! ${currentQ.explanation}"
                                            val nextRound = roundIndex + 1
                                            if (nextRound >= questions.size) {
                                                ArenaHaptics.criticalStrike(view, reduceMotion)
                                                roundIndex = nextRound
                                                onComplete()
                                            } else {
                                                roundIndex = nextRound
                                                selectedOption = null
                                            }
                                        } else {
                                            ArenaHaptics.error(view, reduceMotion)
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
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        } else {
            // All cleared
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
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
                title = "PROCEED TO QUANTUM BOSS CHAMBER ➔",
                style = Tactile3DButtonColor.GREEN,
                onClick = {
                    ArenaHaptics.success(view, reduceMotion)
                    onComplete()
                },
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .minTouchTarget48()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}
