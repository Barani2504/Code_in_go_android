package com.simats.codeingo.ui.arenas.key_exchange_citadel.levels

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import com.simats.codeingo.ui.theme.ArenaDangerRed
import com.simats.codeingo.ui.theme.ArenaSuccessGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.LocalReduceMotion
import com.simats.codeingo.ui.theme.pressScale

enum class QuantumPhase(val phaseNumber: Int) {
    PHASE_1(1),
    PHASE_2(2),
    PHASE_3(3)
}

data class QuantumChoice(
    val id: String,
    val text: String,
    val isCorrect: Boolean,
    val explanation: String
)

data class QuantumChallenge(
    val phase: QuantumPhase,
    val question: String,
    val choices: List<QuantumChoice>
)

@Composable
fun QuantumCryptanalystBossLevel(
    onDefeatBoss: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit = {},
    onTriggerShake: () -> Unit = {},
    onTriggerShockwave: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    val reduceMotion = LocalReduceMotion.current
    val view = LocalView.current

    val primary = ArenaPalettes.Arena3Primary
    val accent = ArenaPalettes.Arena3Accent

    var bossHP by remember { mutableIntStateOf(100) }
    var currentPhase by remember { mutableStateOf(QuantumPhase.PHASE_1) }
    var selectedChoiceId by remember { mutableStateOf<String?>(null) }
    var feedback by remember { mutableStateOf("The Quantum Cryptanalyst prepares Shor's algorithm! Deploy quantum-safe defenses!") }

    val animatedHpFraction by animateFloatAsState(
        targetValue = (bossHP / 100f).coerceIn(0f, 1f),
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 180f),
        label = "bossHpAnim"
    )

    val challenges = remember {
        listOf(
            QuantumChallenge(
                phase = QuantumPhase.PHASE_1,
                question = "Shor's Factorization: Which widely deployed cipher is broken in polynomial time by Shor's algorithm?",
                choices = listOf(
                    QuantumChoice("q1_correct", "RSA-2048", true, "Correct: Shor's algorithm factors integers in polynomial time O((log n)³)!"),
                    QuantumChoice("q1_d1", "AES-256", false, "Misconception: Symmetric ciphers only face Grover's quadratic speedup (halving 256 bits to 128 bits of security)."),
                    QuantumChoice("q1_d2", "SHA-256", false, "Misconception: Hash functions are not broken by Shor's algorithm; Grover requires 2¹²⁸ steps."),
                    QuantumChoice("q1_d3", "ChaCha20", false, "Misconception: ChaCha20 is a symmetric stream cipher with 256-bit security!")
                )
            ),
            QuantumChallenge(
                phase = QuantumPhase.PHASE_2,
                question = "Shor's Discrete Log: Which key agreement protocol is broken by quantum computers?",
                choices = listOf(
                    QuantumChoice("q2_correct", "ECDH (Curve25519)", true, "Correct: Shor's algorithm solves elliptic curve discrete logarithms in polynomial time!"),
                    QuantumChoice("q2_d1", "AES-GCM", false, "Misconception: AES-GCM is an AEAD symmetric cipher unaffected by Shor's period-finding."),
                    QuantumChoice("q2_d2", "HMAC-SHA256", false, "Misconception: HMAC is a keyed hash message authentication code, not discrete log."),
                    QuantumChoice("q2_d3", "Argon2id", false, "Misconception: Argon2id is a memory-hard password hashing function.")
                )
            ),
            QuantumChallenge(
                phase = QuantumPhase.PHASE_3,
                question = "Post-Quantum Cryptography: Which NIST-standardized lattice-based KEM provides Shor resistance?",
                choices = listOf(
                    QuantumChoice("q3_correct", "ML-KEM (Kyber)", true, "Correct: FIPS 203 ML-KEM is based on Module Learning With Errors (M-LWE)!"),
                    QuantumChoice("q3_d1", "RSA-4096", false, "Misconception: Increasing RSA key size does not prevent polynomial quantum collapse!"),
                    QuantumChoice("q3_d2", "Diffie-Hellman", false, "Misconception: Classical DH is completely vulnerable to quantum discrete log."),
                    QuantumChoice("q3_d3", "ECC-P384", false, "Misconception: Elliptic curves of any classical size are broken by Shor's algorithm.")
                )
            )
        )
    }

    val currentChallenge = challenges.first { it.phase == currentPhase }
    val shuffledChoices = remember(currentPhase) {
        currentChallenge.choices.shuffled()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Boss Title & Status Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "👾 THE QUANTUM CRYPTANALYST",
                    fontSize = 14.sp,
                    color = ArenaDangerRed,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "LATTICE-BASED POST-QUANTUM SHOWDOWN",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = accent
                )
            }

            ArenaStatusBadge(
                type = ArenaBadgeType.DANGER,
                label = "PHASE ${currentPhase.phaseNumber} / 3"
            )
        }

        // Boss Animated HP Gauge
        Column(
            modifier = Modifier.fillMaxWidth(0.95f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedHpFraction)
                        .height(14.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(ArenaDangerRed)
                )
            }
            Text(
                text = "$bossHP / 100 HP",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = ArenaDangerRed
            )
        }

        // Challenge Question Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(if (isDark) ArenaPalettes.Arena3CardDark else dynamicColors.cardBackground)
                .border(1.5.dp, primary.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = currentChallenge.question,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = dynamicColors.textPrimary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = feedback,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = accent,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Choices (48dp+ touch target enforced, 2-column responsive layout)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth(0.95f)) {
            shuffledChoices.chunked(2).forEach { rowOptions ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    rowOptions.forEach { choice ->
                        val isSelected = selectedChoiceId == choice.id
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .minTouchTarget48()
                                .height(54.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) {
                                        if (choice.isCorrect) ArenaSuccessGreen else ArenaDangerRed
                                    } else if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) Color.White else primary.copy(alpha = 0.5f),
                                    RoundedCornerShape(12.dp)
                                )
                                .pressScale()
                                .clickable {
                                    selectedChoiceId = choice.id
                                    if (choice.isCorrect) {
                                        ArenaHaptics.criticalStrike(view, reduceMotion)
                                        onTriggerShake()
                                        onTriggerShockwave()
                                        onActionSuccess()

                                        val newHP = maxOf(0, bossHP - 35)
                                        bossHP = newHP

                                        if (newHP <= 0 || currentPhase == QuantumPhase.PHASE_3) {
                                            feedback = "🎉 Quantum Cryptanalyst neutralized by ML-KEM lattice cryptography!"
                                            onDefeatBoss()
                                        } else {
                                            currentPhase = when (currentPhase) {
                                                QuantumPhase.PHASE_1 -> QuantumPhase.PHASE_2
                                                QuantumPhase.PHASE_2 -> QuantumPhase.PHASE_3
                                                QuantumPhase.PHASE_3 -> QuantumPhase.PHASE_3
                                            }
                                            selectedChoiceId = null
                                            feedback = "💥 Qubit decoherence achieved! ${choice.explanation}"
                                        }
                                    } else {
                                        ArenaHaptics.error(view, reduceMotion)
                                        onDeductHeart()
                                        feedback = "❌ Quantum Counterattack: ${choice.explanation}"
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = choice.text,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isSelected && choice.isCorrect) Color.Black else dynamicColors.textPrimary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}
