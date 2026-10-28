package com.simats.codeingo.ui.arenas.block_cipher_forge.levels

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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
fun ShiftRowsSliderLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val view = LocalView.current
    val reduceMotion = LocalReduceMotion.current
    val isDark = dynamicColors.isDark

    val primary = ArenaPalettes.Arena2Primary
    val accent = ArenaPalettes.Arena2Accent
    val cardBg = if (isDark) ArenaPalettes.Arena2CardDark else dynamicColors.cardBackground

    // Initial 4x4 rows (un-shifted NIST AES sample state)
    val initialRows = remember {
        listOf(
            listOf(0x63, 0xEB, 0x9F, 0xA0), // Row 0: target shift 0
            listOf(0xC9, 0x2F, 0x85, 0x1A), // Row 1: target shift 1
            listOf(0xFE, 0x30, 0x27, 0x54), // Row 2: target shift 2
            listOf(0x01, 0x67, 0x89, 0xAB)  // Row 3: target shift 3
        )
    }

    // Current shift offset of each row (0..3)
    val shifts = remember { mutableStateListOf(0, 0, 0, 0) }

    fun getShiftedRow(rowIndex: Int): List<Int> {
        val row = initialRows[rowIndex]
        val s = shifts[rowIndex]
        return List(4) { col -> row[(col + s) % 4] }
    }

    val isRow0Correct = shifts[0] == 0
    val isRow1Correct = shifts[1] == 1
    val isRow2Correct = shifts[2] == 2
    val isRow3Correct = shifts[3] == 3
    val allCorrect = isRow0Correct && isRow1Correct && isRow2Correct && isRow3Correct

    val correctCount = listOf(isRow0Correct, isRow1Correct, isRow2Correct, isRow3Correct).count { it }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Mission Header with ArenaStatusBadge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "AES 4x4 STATE MATRIX",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = accent,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "ShiftRows Cyclic Permutation",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = dynamicColors.textPrimary
                )
            }
            ArenaStatusBadge(
                type = if (allCorrect) ArenaBadgeType.SUCCESS else ArenaBadgeType.WARNING,
                label = if (allCorrect) "DIFFUSION LOCKED" else "$correctCount/4 ROWS ALIGNED"
            )
        }

        // Objective and Guidance Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(cardBg)
                .border(1.dp, if (allCorrect) ArenaSuccessGreen.copy(alpha = 0.6f) else dynamicColors.cardBorder, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Cyclic Shift Rule (Left Permutation):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = primary
                )
                Text(
                    text = "Row r is cyclically rotated left by r bytes: Row 0 by 0, Row 1 by 1, Row 2 by 2, Row 3 by 3. This diffuses bytes across multiple columns for the subsequent MixColumns step.",
                    fontSize = 11.sp,
                    color = dynamicColors.textSecondary,
                    lineHeight = 16.sp
                )
            }
        }

        // 4 Interactive Rows
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(4) { rowIndex ->
                val targetShift = rowIndex
                val currentShift = shifts[rowIndex]
                val isCorrect = currentShift == targetShift
                val displayedBytes = getShiftedRow(rowIndex)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isCorrect) ArenaSuccessGreen.copy(alpha = 0.12f)
                            else dynamicColors.cardBorder.copy(alpha = 0.2f)
                        )
                        .border(
                            1.dp,
                            if (isCorrect) ArenaSuccessGreen else dynamicColors.cardBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Row Label & Target Indicator
                    Column(modifier = Modifier.width(64.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isCorrect) Icons.Default.Lock else Icons.Default.LockOpen,
                                contentDescription = null,
                                tint = if (isCorrect) ArenaSuccessGreen else accent,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Row $rowIndex",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = dynamicColors.textPrimary
                            )
                        }
                        Text(
                            text = if (isCorrect) "✓ $targetShift byte" else "Shift: $currentShift (Need $targetShift)",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isCorrect) ArenaSuccessGreen else accent
                        )
                    }

                    // 4 Bytes Representation
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        displayedBytes.forEach { byteVal ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (isCorrect) ArenaSuccessGreen.copy(alpha = 0.2f)
                                        else dynamicColors.cardBorder.copy(alpha = 0.4f)
                                    )
                                    .border(
                                        1.dp,
                                        if (isCorrect) ArenaSuccessGreen.copy(alpha = 0.5f) else Color.Transparent,
                                        RoundedCornerShape(6.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = byteVal.toString(16).uppercase().padStart(2, '0'),
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    color = if (isCorrect) ArenaSuccessGreen else dynamicColors.textPrimary
                                )
                            }
                        }
                    }

                    // Shift Controls (>=48dp touch target compliant)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        // Left Shift Button (cyclically shift left by 1)
                        IconButton(
                            onClick = {
                                ArenaHaptics.tap(view, reduceMotion)
                                shifts[rowIndex] = (shifts[rowIndex] + 1) % 4
                            },
                            modifier = Modifier
                                .minTouchTarget48()
                                .size(48.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(primary.copy(alpha = 0.2f))
                                    .border(1.dp, primary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Shift Left",
                                    tint = primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Live Permutation Feedback
        AnimatedVisibility(
            visible = allCorrect,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(ArenaSuccessGreen.copy(alpha = 0.15f))
                    .border(1.dp, ArenaSuccessGreen, RoundedCornerShape(10.dp))
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✓ Perfect State Permutation! Inter-column diffusion verified.",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = ArenaSuccessGreen,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f, fill = false))

        // Lock Alignment Action Button
        Tactile3DButton(
            title = if (allCorrect) "Lock State Permutation ✓" else "Align Rows ($correctCount/4)",
            style = if (allCorrect) Tactile3DButtonColor.GREEN else Tactile3DButtonColor.AMBER,
            onClick = {
                if (allCorrect) {
                    ArenaHaptics.success(view, reduceMotion)
                    onActionSuccess()
                    onComplete()
                } else {
                    ArenaHaptics.error(view, reduceMotion)
                    onDeductHeart()
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
