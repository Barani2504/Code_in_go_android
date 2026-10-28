package com.simats.codeingo.ui.arenas.cipher_sanctum.levels

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

@Composable
fun RailFenceGridLevel(
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

    // Plaintext: "DEFENDTHEWALLX" (14 chars)
    // 3-Rail Zigzag Transposition:
    // Rail 0: D . . . N . . . E . . . L       -> "DNEL"
    // Rail 1: . E . E . D . H . W . L . X     -> "EEDHWLX"
    // Rail 2: . . F . . . T . . . A . . .     -> "FTA"
    // Ciphertext: "DNELEEDHWLXFTA"
    val correctRailRows = remember { listOf("DNEL", "EEDHWLX", "FTA") }
    val initialScrambled = remember { mutableStateListOf("FTA", "DNEL", "EEDHWLX") }
    var selectedIdx by remember { mutableStateOf<Int?>(null) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var lastCheckSuccess by remember { mutableStateOf<Boolean?>(null) }

    val isAligned = initialScrambled.toList() == correctRailRows

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
                    text = "RAIL FENCE TRANSPOSITION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = primary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "3-Rail Zigzag Coordinate Reordering",
                    fontSize = 10.sp,
                    color = dynamicColors.textSecondary
                )
            }
            ArenaStatusBadge(
                type = if (isAligned) ArenaBadgeType.SUCCESS else ArenaBadgeType.INFO,
                label = if (isAligned) "RAILS RESTORED" else "RAILS SCRAMBLED"
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
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = null,
                        tint = primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Transposition Permutation:",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = primary
                    )
                }
                Text(
                    text = "A transposition cipher changes letter positions rather than character values. The plaintext 'DEFENDTHEWALLX' bounces across 3 rails. Tap two rail slots to swap and reassemble Rail 0 (top), Rail 1 (middle), and Rail 2 (bottom)!",
                    fontSize = 11.sp,
                    color = dynamicColors.textPrimary,
                    lineHeight = 15.sp
                )
            }
        }

        // Ciphertext Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(cardBg)
                .border(1.2.dp, primary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "CIPHERTEXT: DNELEEDHWLXFTA",
                fontSize = 12.5.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                color = primary
            )
        }

        // 3-Rail Zigzag Visualizer Grid Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(if (isDark) Color(0xFF0D0A02) else Color(0xFF1E1705))
                .border(1.2.dp, if (isAligned) ArenaSuccessGreen else primary.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "ZIGZAG READOUT PATTERN (TOP ➔ MIDDLE ➔ BOTTOM)",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = SubtextGray
                )
                Text(
                    text = "Rail 0: D . . . N . . . E . . . L\n" +
                           "Rail 1: . E . E . D . H . W . L . X\n" +
                           "Rail 2: . . F . . . T . . . A . . .",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (isAligned) ArenaSuccessGreen else primary,
                    lineHeight = 16.sp
                )
            }
        }

        // 3 Interactive Rails (minTouchTarget48 compliant)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            initialScrambled.forEachIndexed { i, railChunk ->
                val isSelected = selectedIdx == i
                val isRowCorrect = railChunk == correctRailRows[i]

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .minTouchTarget48()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            when {
                                isSelected -> primary.copy(alpha = 0.25f)
                                isAligned -> ArenaSuccessGreen.copy(alpha = 0.15f)
                                else -> cardBg
                            }
                        )
                        .border(
                            1.2.dp,
                            when {
                                isSelected -> primary
                                isAligned -> ArenaSuccessGreen
                                else -> primary.copy(alpha = 0.4f)
                            },
                            RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            ArenaHaptics.tap(view, reduceMotion)
                            feedbackMessage = null
                            if (selectedIdx == null) {
                                selectedIdx = i
                            } else {
                                val first = selectedIdx!!
                                val temp = initialScrambled[first]
                                initialScrambled[first] = initialScrambled[i]
                                initialScrambled[i] = temp
                                selectedIdx = null

                                if (initialScrambled.toList() == correctRailRows) {
                                    ArenaHaptics.success(view, reduceMotion)
                                    feedbackMessage = "✓ Rails matched! Top: DNEL, Mid: EEDHWLX, Bot: FTA."
                                }
                            }
                        }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Rail Slot $i: \"$railChunk\"",
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                color = if (isAligned) ArenaSuccessGreen else dynamicColors.textPrimary
                            )
                            Text(
                                text = if (isSelected) "Selected for swap ⇄" else "Expected length: ${correctRailRows[i].length} characters",
                                fontSize = 9.sp,
                                color = if (isSelected) primary else SubtextGray
                            )
                        }

                        Text(
                            text = if (isRowCorrect) "ALIGNED ✓" else "SWAP ⇄",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = if (isRowCorrect) ArenaSuccessGreen else primary
                        )
                    }
                }
            }
        }

        // Live Feedback Message
        AnimatedVisibility(visible = feedbackMessage != null, enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                .background(
                    if (lastCheckSuccess == true || isAligned) ArenaSuccessGreen.copy(alpha = 0.12f)
                    else ArenaDangerRed.copy(alpha = 0.12f)
                )
                .border(
                    1.dp,
                    if (lastCheckSuccess == true || isAligned) ArenaSuccessGreen else ArenaDangerRed,
                    RoundedCornerShape(10.dp)
                )
                .padding(10.dp)
            ) {
                Text(
                    text = feedbackMessage ?: "",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (lastCheckSuccess == true || isAligned) ArenaSuccessGreen else ArenaDangerRed,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Active Submission Button
        Tactile3DButton(
            title = if (isAligned) "Transposition Restored ✓" else "Verify Rail Fence Alignment",
            style = if (isAligned) Tactile3DButtonColor.GREEN else Tactile3DButtonColor.AMBER,
            onClick = {
                if (isAligned) {
                    ArenaHaptics.success(view, reduceMotion)
                    lastCheckSuccess = true
                    feedbackMessage = "✓ Correct! Reading rows in sequence (DNEL + EEDHWLX + FTA) reveals the 3-rail zigzag transposition!"
                    onActionSuccess()
                    onComplete()
                } else {
                    ArenaHaptics.error(view, reduceMotion)
                    lastCheckSuccess = false
                    feedbackMessage = "❌ Incorrect Rail Order: The top rail must contain 4 letters (DNEL), middle 7 letters (EEDHWLX), bottom 3 letters (FTA)!"
                    onDeductHeart()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .minTouchTarget48()
        )
    }
}
