package com.simats.codeingo.ui.arenas.block_cipher_forge.levels

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
import androidx.compose.material.icons.filled.ArrowForward
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.components.Duolingo3DButton
import com.simats.codeingo.ui.components.Duolingo3DButtonColor
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

@Composable
fun ShiftRowsSliderLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current

    // Initial 4x4 rows (un-shifted)
    val initialRows = remember {
        listOf(
            listOf(0x63, 0xEB, 0x9F, 0xA0), // Row 0: shift 0
            listOf(0xC9, 0x2F, 0x85, 0x1A), // Row 1: shift 1
            listOf(0xFE, 0x30, 0x27, 0x54), // Row 2: shift 2
            listOf(0x01, 0x67, 0x89, 0xAB)  // Row 3: shift 3
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "AES 4x4 STATE MATRIX",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Shift Row 0 by 0, Row 1 by 1, Row 2 by 2, Row 3 by 3 (Left)",
                fontSize = 11.sp,
                color = dynamicColors.textSecondary,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

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
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isCorrect) DuolingoGreen.copy(alpha = 0.12f) else dynamicColors.cardBorder.copy(alpha = 0.2f))
                        .border(1.dp, if (isCorrect) DuolingoGreen else dynamicColors.cardBorder, RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Row Label & Status
                    Column(modifier = Modifier.width(60.dp)) {
                        Text(
                            text = "Row $rowIndex",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = dynamicColors.textPrimary
                        )
                        Text(
                            text = if (isCorrect) "✓ $targetShift byte" else "$currentShift/$targetShift",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCorrect) DuolingoGreen else AmberGold
                        )
                    }

                    // 4 Bytes representation
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        displayedBytes.forEach { byteVal ->
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(dynamicColors.cardBorder.copy(alpha = 0.4f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = byteVal.toString(16).uppercase().padStart(2, '0'),
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    color = if (isCorrect) DuolingoGreen else dynamicColors.textPrimary
                                )
                            }
                        }
                    }

                    // Left Shift Button
                    IconButton(
                        onClick = {
                            shifts[rowIndex] = (shifts[rowIndex] + 1) % 4
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00C3FF).copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Shift",
                            tint = Color(0xFF00C3FF),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Lock Alignment Button
        Duolingo3DButton(
            title = if (allCorrect) "Lock State Permutation" else "Align Rows ($shifts)",
            style = if (allCorrect) Duolingo3DButtonColor.GREEN else Duolingo3DButtonColor.AMBER,
            onClick = {
                if (allCorrect) {
                    onActionSuccess()
                    onComplete()
                } else {
                    onDeductHeart()
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
