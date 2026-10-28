package com.simats.codeingo.ui.arenas.cipher_sanctum.levels

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material3.LinearProgressIndicator
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
import com.simats.codeingo.ui.arenas.crypto.ArenaCryptoEngine
import com.simats.codeingo.ui.theme.ArenaDangerRed
import com.simats.codeingo.ui.theme.ArenaSuccessGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.LocalReduceMotion
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
fun SphinxBossBattleLevel(
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

    val primary = ArenaPalettes.Arena1Primary
    val accent = ArenaPalettes.Arena1Accent
    val cardBg = if (isDark) ArenaPalettes.Arena1CardDark else dynamicColors.cardBackground

    var bossHP by remember { mutableIntStateOf(100) }
    var currentPhase by remember { mutableStateOf(SphinxPhase.PHASE_1) }
    var selectedChoiceId by remember { mutableStateOf<String?>(null) }
    var feedbackMessage by remember {
        mutableStateOf("The Sphinx challenges you: Decrypt Caesar shift K=3 to shatter the shadow ward!")
    }
    var lastHitSuccess by remember { mutableStateOf<Boolean?>(null) }

    val animatedHpFraction by animateFloatAsState(
        targetValue = (bossHP / 100f).coerceIn(0f, 1f),
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 180f),
        label = "sphinxHpAnim"
    )

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
                    SphinxChoice("c1_correct", "MASTER", true, "CRITICAL HIT! P-3=M, D-3=A, V-3=S, W-3=T, H-3=E, U-3=R"),
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
                    SphinxChoice("c2_correct", "SECRET", true, "CRITICAL HIT! V-3=S, H-3=E, F-3=C, U-3=R, H-3=E, W-3=T"),
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
                    SphinxChoice("c3_correct", "SHADOW", true, "SPHINX DEFEATED! V-3=S, K-3=H, D-3=A, G-3=D, R-3=O, Z-3=W"),
                    SphinxChoice("c3_d1", "SIGNAL", false, "Misconception: 'I','N','L' do not map from 'K','D','R'."),
                    SphinxChoice("c3_d2", "SPHINX", false, "Misconception: 'P','H','I' do not map from 'K','D','G'."),
                    SphinxChoice("c3_d3", "SYSTEM", false, "Misconception: 'Y','T','E','M' do not map from 'K','D','G','R'.")
                )
            )
        )
    }

    val currentRiddle = riddles.first { it.phase == currentPhase }
    val shuffledChoices = remember(currentPhase) {
        currentRiddle.choices.shuffled()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Boss Status Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "🦅 THE SPHINX OF SHADOWS",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = ArenaDangerRed,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Guardian of the Classical Ciphers",
                    fontSize = 10.sp,
                    color = dynamicColors.textSecondary
                )
            }
            ArenaStatusBadge(
                type = ArenaBadgeType.DANGER,
                label = "PHASE ${currentPhase.phaseNumber} / 3"
            )
        }

        // Animated Boss Health Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(cardBg)
                .border(1.2.dp, ArenaDangerRed.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SPHINX WARD INTEGRITY:",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = SubtextGray
                    )
                    Text(
                        text = "$bossHP / 100 HP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (bossHP <= 35) ArenaDangerRed else primary
                    )
                }

                LinearProgressIndicator(
                    progress = { animatedHpFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = if (bossHP <= 35) ArenaDangerRed else primary,
                    trackColor = cardBg.copy(alpha = 0.5f)
                )
            }
        }

        // Ciphertext Riddle Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(cardBg)
                .border(1.2.dp, primary.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "ANCIENT CIPHERTEXT CIPHER BLOCK",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = SubtextGray
                )
                Text(
                    text = "🔒 ${currentRiddle.ciphertext}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = primary,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Decrypt using Caesar Shift Key K = ${currentRiddle.shift}",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = dynamicColors.textSecondary
                )
            }
        }

        // Feedback / Dialogue Banner
        AnimatedVisibility(visible = feedbackMessage.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        when (lastHitSuccess) {
                            true -> ArenaSuccessGreen.copy(alpha = 0.12f)
                            false -> ArenaDangerRed.copy(alpha = 0.12f)
                            else -> cardBg
                        }
                    )
                    .border(
                        1.dp,
                        when (lastHitSuccess) {
                            true -> ArenaSuccessGreen
                            false -> ArenaDangerRed
                            else -> primary.copy(alpha = 0.4f)
                        },
                        RoundedCornerShape(10.dp)
                    )
                    .padding(10.dp)
            ) {
                Text(
                    text = feedbackMessage,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (lastHitSuccess) {
                        true -> ArenaSuccessGreen
                        false -> ArenaDangerRed
                        else -> primary
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Choices Grid (2x2 grid, minTouchTarget48 compliant)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            shuffledChoices.chunked(2).forEach { rowOptions ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    rowOptions.forEach { choice ->
                        val isSelected = selectedChoiceId == choice.id
                        val isCorrect = choice.isCorrect
                        val bgTint = when {
                            isSelected && isCorrect -> ArenaSuccessGreen.copy(alpha = 0.2f)
                            isSelected && !isCorrect -> ArenaDangerRed.copy(alpha = 0.2f)
                            else -> cardBg
                        }
                        val borderColor = when {
                            isSelected && isCorrect -> ArenaSuccessGreen
                            isSelected && !isCorrect -> ArenaDangerRed
                            else -> primary.copy(alpha = 0.4f)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .minTouchTarget48()
                                .clip(RoundedCornerShape(12.dp))
                                .background(bgTint)
                                .border(1.2.dp, borderColor, RoundedCornerShape(12.dp))
                            .clickable {
                                selectedChoiceId = choice.id
                                ArenaHaptics.tap(view, reduceMotion)
                                if (choice.isCorrect) {
                                    ArenaHaptics.criticalStrike(view, reduceMotion)
                                    lastHitSuccess = true
                                    onTriggerShake()
                                    onTriggerShockwave()
                                    onActionSuccess()

                                    val newHP = maxOf(0, bossHP - 35)
                                    bossHP = newHP
                                    if (newHP <= 0 || currentPhase == SphinxPhase.PHASE_3) {
                                        feedbackMessage = choice.explanation
                                        onDefeatBoss()
                                    } else {
                                        currentPhase = when (currentPhase) {
                                            SphinxPhase.PHASE_1 -> SphinxPhase.PHASE_2
                                            SphinxPhase.PHASE_2 -> SphinxPhase.PHASE_3
                                            SphinxPhase.PHASE_3 -> SphinxPhase.PHASE_3
                                        }
                                        selectedChoiceId = null
                                        feedbackMessage = choice.explanation
                                    }
                                } else {
                                    ArenaHaptics.error(view, reduceMotion)
                                    lastHitSuccess = false
                                    onTriggerShake()
                                    onDeductHeart()
                                    feedbackMessage = "❌ Breached: ${choice.explanation}"
                                }
                            }
                            .padding(horizontal = 10.dp, vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = choice.text,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = if (isSelected && choice.isCorrect) ArenaSuccessGreen else dynamicColors.textPrimary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
