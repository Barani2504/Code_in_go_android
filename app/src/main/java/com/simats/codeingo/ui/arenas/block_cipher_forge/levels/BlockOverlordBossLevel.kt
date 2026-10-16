package com.simats.codeingo.ui.arenas.block_cipher_forge.levels

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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.graphics.Brush
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
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

@Composable
fun BlockOverlordBossLevel(
    timeFreezeActive: Boolean,
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current

    var bossHP by remember { mutableIntStateOf(100) }
    var currentPhase by remember { mutableIntStateOf(1) }
    var feedback by remember { mutableStateOf("The Block Overlord shields the AES Citadel! Break its cipher defenses!") }

    // Phase 1: Round operations sequence assembly
    val p1Sequence = remember { mutableStateListOf("MixColumns", "SubBytes", "AddRoundKey", "ShiftRows") }
    val p1CorrectSequence = listOf("SubBytes", "ShiftRows", "MixColumns", "AddRoundKey")

    // Phase 2: Disarm Round 10 MixColumns trap
    var p2Round10MixColumnsDisabled by remember { mutableStateOf(false) }

    // Phase 3: Avalanche Storm stabilization
    var p3NodesStabilized by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Boss Header & HP Gauge
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "👾 THE BLOCK OVERLORD",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = DuolingoRed
                )
                Text(
                    text = "PHASE $currentPhase OF 3",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = AmberGold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Boss Health Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(dynamicColors.cardBorder.copy(alpha = 0.3f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(bossHP / 100f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(DuolingoRed, Color(0xFFFF6D00))
                            )
                        )
                )
            }
            Text(
                text = "$bossHP / 100 HP",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = dynamicColors.textSecondary
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Phase Interactive Content
        when (currentPhase) {
            1 -> {
                // PHASE 1: AES Round Sequence Reorder
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Phase 1: Assemble Standard AES Round Order",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = dynamicColors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap any operation to swap with the first to arrange:\nSubBytes → ShiftRows → MixColumns → AddRoundKey",
                        fontSize = 10.sp,
                        color = dynamicColors.textSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    p1Sequence.forEachIndexed { index, op ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (p1CorrectSequence[index] == op) DuolingoGreen.copy(alpha = 0.15f)
                                    else dynamicColors.cardBorder.copy(alpha = 0.25f)
                                )
                                .border(
                                    1.dp,
                                    if (p1CorrectSequence[index] == op) DuolingoGreen else dynamicColors.cardBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    // Swap with next position cyclically
                                    val nextPos = (index + 1) % 4
                                    val tmp = p1Sequence[index]
                                    p1Sequence[index] = p1Sequence[nextPos]
                                    p1Sequence[nextPos] = tmp
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Step ${index + 1}: $op",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = dynamicColors.textPrimary
                            )
                            Text(
                                text = if (p1CorrectSequence[index] == op) "✓ LOCKED" else "SWAP ⟳",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = if (p1CorrectSequence[index] == op) DuolingoGreen else AmberGold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val p1Solved = p1Sequence == p1CorrectSequence
                    Duolingo3DButton(
                        title = if (p1Solved) "Strike Overlord (-35 HP)" else "Align Round Sequence",
                        style = if (p1Solved) Duolingo3DButtonColor.AMBER else Duolingo3DButtonColor.BLUE,
                        onClick = {
                            if (p1Solved) {
                                bossHP = 65
                                currentPhase = 2
                                onActionSuccess()
                                feedback = "💥 Phase 1 Shattered! Overlord charges Round 10 conveyor assault!"
                            } else {
                                onDeductHeart()
                                feedback = "❌ Sequence incorrect! AES rounds must be SubBytes → ShiftRows → MixColumns → AddRoundKey."
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            2 -> {
                // PHASE 2: Round 10 MixColumns Trap
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Phase 2: Defuse the Round 10 Conveyor Trap",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = dynamicColors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "CRITICAL: In Round 10 of AES-128, MixColumns is OMITTED! The Overlord inserted a rogue MixColumns unit into Round 10.",
                        fontSize = 11.sp,
                        color = dynamicColors.textSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (p2Round10MixColumnsDisabled) DuolingoGreen.copy(alpha = 0.15f) else DuolingoRed.copy(alpha = 0.15f))
                            .border(1.dp, if (p2Round10MixColumnsDisabled) DuolingoGreen else DuolingoRed, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "ROUND 10 AES PIPELINE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = AmberGold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (p2Round10MixColumnsDisabled)
                                    "SubBytes → ShiftRows → [OMITTED] → AddRoundKey"
                                else
                                    "SubBytes → ShiftRows → [ROGUE MIXCOLUMNS] → AddRoundKey",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = if (p2Round10MixColumnsDisabled) DuolingoGreen else DuolingoRed
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Duolingo3DButton(
                        title = if (p2Round10MixColumnsDisabled) "Reflect Round 10 Blast (-35 HP)" else "DISARM MIXCOLUMNS IN ROUND 10",
                        style = if (p2Round10MixColumnsDisabled) Duolingo3DButtonColor.AMBER else Duolingo3DButtonColor.GREEN,
                        onClick = {
                            if (!p2Round10MixColumnsDisabled) {
                                p2Round10MixColumnsDisabled = true
                                onActionSuccess()
                            } else {
                                bossHP = 30
                                currentPhase = 3
                                onActionSuccess()
                                feedback = "💥 Phase 2 Cleared! Overlord initiates Avalanche Chaos Surge!"
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            3 -> {
                // PHASE 3: Avalanche Chaos Surge
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Phase 3: Contain Avalanche Surge (Target: 8 Nodes)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = dynamicColors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap fluctuating nodes to restore strict diffusion balance: ~50% bit flip on average.",
                        fontSize = 10.sp,
                        color = dynamicColors.textSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Node Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        repeat(4) { col ->
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                repeat(2) { row ->
                                    val idx = col * 2 + row
                                    val isNodeTapped = idx < p3NodesStabilized
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isNodeTapped) DuolingoGreen else Color(0xFFFF2A6D).copy(alpha = 0.3f))
                                            .border(1.dp, if (isNodeTapped) DuolingoGreen else Color(0xFFFF2A6D), RoundedCornerShape(8.dp))
                                            .clickable(enabled = !isNodeTapped) {
                                                p3NodesStabilized++
                                                onActionSuccess()
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isNodeTapped) Icons.Default.Check else Icons.Default.Bolt,
                                            contentDescription = null,
                                            tint = if (isNodeTapped) Color.Black else Color(0xFFFF2A6D),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val p3Solved = p3NodesStabilized >= 8
                    Duolingo3DButton(
                        title = if (p3Solved) "Deliver Final Citadel Strike (-30 HP)" else "Stabilize Surge ($p3NodesStabilized / 8)",
                        style = if (p3Solved) Duolingo3DButtonColor.AMBER else Duolingo3DButtonColor.BLUE,
                        onClick = {
                            if (p3Solved) {
                                bossHP = 0
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
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = feedback,
            fontSize = 11.sp,
            color = AmberGold,
            textAlign = TextAlign.Center
        )
    }
}
