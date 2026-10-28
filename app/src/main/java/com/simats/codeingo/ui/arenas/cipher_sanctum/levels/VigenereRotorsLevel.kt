package com.simats.codeingo.ui.arenas.cipher_sanctum.levels

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.simats.codeingo.ui.theme.SubtextGray

private data class VigenereRotorPosition(
    val index: Int,
    val plainChar: Char,
    val keyChar: Char,
    val cipherChar: Char,
    val plainVal: Int,
    val keyVal: Int,
    val cipherVal: Int,
    val options: List<Char>
)

@Composable
fun VigenereRotorsLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val view = LocalView.current
    val reduceMotion = LocalReduceMotion.current
    val isDark = dynamicColors.isDark

    val primary = ArenaPalettes.Arena1Primary
    val accent = ArenaPalettes.Arena1Accent
    val cardBg = if (isDark) ArenaPalettes.Arena1CardDark else dynamicColors.cardBackground

    // Plaintext: "SANCTUM", Keyword: "KEY" repeated -> "KEYKEYK"
    // C_i = (P_i + K_i) mod 26
    val rotorPositions = remember {
        listOf(
            VigenereRotorPosition(0, 'S', 'K', 'C', 18, 10, 2, listOf('C', 'B', 'D')),
            VigenereRotorPosition(1, 'A', 'E', 'E', 0, 4, 4, listOf('E', 'F', 'D')),
            VigenereRotorPosition(2, 'N', 'Y', 'L', 13, 24, 11, listOf('L', 'M', 'K')),
            VigenereRotorPosition(3, 'C', 'K', 'M', 2, 10, 12, listOf('M', 'L', 'N')),
            VigenereRotorPosition(4, 'T', 'E', 'X', 19, 4, 23, listOf('X', 'W', 'Y')),
            VigenereRotorPosition(5, 'U', 'Y', 'S', 20, 24, 18, listOf('S', 'T', 'R')),
            VigenereRotorPosition(6, 'M', 'K', 'W', 12, 10, 22, listOf('W', 'V', 'X'))
        )
    }

    var currentRotorIndex by remember { mutableIntStateOf(0) }
    val solvedRotors = remember { mutableStateListOf<Boolean>(false, false, false, false, false, false, false) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var lastCheckSuccess by remember { mutableStateOf<Boolean?>(null) }

    val isCompleted = currentRotorIndex >= rotorPositions.size
    val activePosition = rotorPositions.getOrNull(currentRotorIndex)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Mission Header with Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "VIGENÈRE POLYALPHABETIC ROTORS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = primary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Tabula Recta Multi-Shift Keystream",
                    fontSize = 10.sp,
                    color = dynamicColors.textSecondary
                )
            }
            ArenaStatusBadge(
                type = if (isCompleted) ArenaBadgeType.SUCCESS else ArenaBadgeType.INFO,
                label = if (isCompleted) "KEYSTREAM SEALED" else "ROTOR ${currentRotorIndex + 1}/7"
            )
        }

        // Mission Briefing Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(cardBg)
                .border(1.dp, primary.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = null,
                        tint = primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Polyalphabetic Encryption: C = (P + K) mod 26",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = primary
                    )
                }
                Text(
                    text = "The Vigenère cipher defeats frequency analysis by shifting each character using a repeating keyword stream ('KEYKEYK'). For each rotor column, compute the modulo 26 sum and select the resulting ciphertext symbol!",
                    fontSize = 11.sp,
                    color = dynamicColors.textPrimary,
                    lineHeight = 15.sp
                )
            }
        }

        // Live Keystream Ribbon
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(cardBg)
                .border(1.2.dp, if (isCompleted) ArenaSuccessGreen else primary.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "KEYSTREAM ALIGNMENT TRACK",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = SubtextGray
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    rotorPositions.forEachIndexed { i, pos ->
                        val isLocked = solvedRotors[i]
                        val isCurrent = i == currentRotorIndex

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    when {
                                        isLocked -> ArenaSuccessGreen.copy(alpha = 0.2f)
                                        isCurrent -> primary.copy(alpha = 0.15f)
                                        else -> cardBg
                                    }
                                )
                                .border(
                                    1.2.dp,
                                    when {
                                        isLocked -> ArenaSuccessGreen
                                        isCurrent -> primary
                                        else -> cardBg.copy(alpha = 0.6f)
                                    },
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${pos.plainChar}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = dynamicColors.textPrimary
                            )
                            Text(
                                text = "+${pos.keyChar}",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = primary
                            )
                            Text(text = "↓", fontSize = 9.sp, color = SubtextGray)
                            Text(
                                text = if (isLocked) "${pos.cipherChar}" else "?",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = if (isLocked) ArenaSuccessGreen else primary
                            )
                        }
                    }
                }
            }
        }

        if (activePosition != null && !isCompleted) {
            // Active Rotor Math Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(cardBg)
                    .border(1.2.dp, primary, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "SOLVE ROTOR ${currentRotorIndex + 1}: '${activePosition.plainChar}' + '${activePosition.keyChar}'",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = primary
                    )
                    Text(
                        text = "P = '${activePosition.plainChar}' (${activePosition.plainVal})  |  K = '${activePosition.keyChar}' (${activePosition.keyVal})",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = dynamicColors.textPrimary
                    )
                    Text(
                        text = "C = (${activePosition.plainVal} + ${activePosition.keyVal}) mod 26 = ${activePosition.plainVal + activePosition.keyVal} mod 26 = ${activePosition.cipherVal}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = accent
                    )
                }
            }

            // Interactive Choices (minTouchTarget48)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                activePosition.options.shuffled(remember(currentRotorIndex) { kotlin.random.Random(currentRotorIndex) }).forEach { choiceChar ->
                    val isCorrectChoice = choiceChar == activePosition.cipherChar

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .minTouchTarget48()
                            .clip(RoundedCornerShape(12.dp))
                            .background(cardBg)
                            .border(1.2.dp, primary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .clickable {
                                ArenaHaptics.tap(view, reduceMotion)
                                if (isCorrectChoice) {
                                    ArenaHaptics.success(view, reduceMotion)
                                    lastCheckSuccess = true
                                    solvedRotors[currentRotorIndex] = true
                                    feedbackMessage = "✓ Correct! (${activePosition.plainVal} + ${activePosition.keyVal}) mod 26 = ${activePosition.cipherVal} = '$choiceChar'"
                                    onActionSuccess()

                                    currentRotorIndex++
                                    if (currentRotorIndex >= rotorPositions.size) {
                                        onComplete()
                                    }
                                } else {
                                    ArenaHaptics.error(view, reduceMotion)
                                    lastCheckSuccess = false
                                    feedbackMessage = "❌ Incorrect arithmetic: '${activePosition.plainChar}' (${activePosition.plainVal}) + '${activePosition.keyChar}' (${activePosition.keyVal}) mod 26 is not '$choiceChar'!"
                                    onDeductHeart()
                                }
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Ciphertext Symbol: '$choiceChar'",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = dynamicColors.textPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Char Index: ${choiceChar - 'A'}",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = primary
                            )
                        }
                    }
                }
            }

            // Live Feedback Banner
            AnimatedVisibility(visible = feedbackMessage != null, enter = fadeIn(), exit = fadeOut()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (lastCheckSuccess == true) ArenaSuccessGreen.copy(alpha = 0.12f)
                            else ArenaDangerRed.copy(alpha = 0.12f)
                        )
                        .border(
                            1.dp,
                            if (lastCheckSuccess == true) ArenaSuccessGreen else ArenaDangerRed,
                            RoundedCornerShape(10.dp)
                        )
                        .padding(10.dp)
                ) {
                    Text(
                        text = feedbackMessage ?: "",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (lastCheckSuccess == true) ArenaSuccessGreen else ArenaDangerRed,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } else {
            // Completed Keystream Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(ArenaSuccessGreen.copy(alpha = 0.15f))
                    .border(1.5.dp, ArenaSuccessGreen, RoundedCornerShape(16.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = ArenaSuccessGreen,
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = "VIGENÈRE KEYSTREAM FULLY ALIGNED!",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = ArenaSuccessGreen,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Plaintext 'SANCTUM' combined with repeating key 'KEY' produces ciphertext 'CELMXSW'. The polyalphabetic shift neutralizes single-letter frequency attacks!",
                        fontSize = 11.5.sp,
                        color = dynamicColors.textPrimary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Tactile3DButton(
                title = "Complete Level & Proceed ➔",
                style = Tactile3DButtonColor.GREEN,
                onClick = onComplete,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
