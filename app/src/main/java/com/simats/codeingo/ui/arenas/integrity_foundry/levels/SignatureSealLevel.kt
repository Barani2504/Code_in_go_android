package com.simats.codeingo.ui.arenas.integrity_foundry.levels

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import kotlin.math.roundToInt

@Composable
fun SignatureSealLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current

    // Interactive state: Calibrating the Birthday Bound collision resistance
    // User adjusts hash bit-length n and trial search space
    var bitLengthIndex by remember { mutableIntStateOf(4) } // 0: 16b, 1: 32b, 2: 64b, 3: 128b, 4: 256b
    val bitLengths = listOf(16, 32, 64, 128, 256)
    val currentN = bitLengths[bitLengthIndex]
    val birthdayBoundExponent = currentN / 2

    var calibratedTargetExponent by remember { mutableIntStateOf(64) }
    var simulationTested by remember { mutableStateOf(false) }
    var isCorrectMatch by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "BIRTHDAY BOUND RADAR",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Square-root collision search complexity: O(2^(n/2))",
                fontSize = 11.sp,
                color = dynamicColors.textSecondary,
                textAlign = TextAlign.Center
            )
        }

        // Educational premise box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(dynamicColors.cardBorder.copy(alpha = 0.2f))
                .border(1.dp, AmberGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = "🎯 Birthday Paradox Collision Calibrator",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = AmberGold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Finding ANY two messages m₁ ≠ m₂ such that H(m₁) = H(m₂) requires only ~√(2ⁿ) = 2^(n/2) hash evaluations with 50% probability, halving nominal security!",
                    fontSize = 12.sp,
                    color = dynamicColors.textPrimary,
                    lineHeight = 16.sp
                )
            }
        }

        // Hash Bit Length Selector Tabs
        Text(
            text = "SELECT HASH DIGEST SIZE (n BITS)",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = dynamicColors.textSecondary
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            bitLengths.forEachIndexed { idx, bits ->
                val isSelected = bitLengthIndex == idx
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isSelected) AmberGold.copy(alpha = 0.2f)
                            else dynamicColors.cardBorder.copy(alpha = 0.2f)
                        )
                        .border(
                            if (isSelected) 2.dp else 1.dp,
                            if (isSelected) AmberGold else dynamicColors.cardBorder.copy(alpha = 0.3f),
                            RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            bitLengthIndex = idx
                            simulationTested = false
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$bits b",
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                        color = if (isSelected) AmberGold else dynamicColors.textPrimary
                    )
                }
            }
        }

        // Comparative Search Space Visualizer Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(dynamicColors.cardBackground)
                .border(1.dp, dynamicColors.cardBorder.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Brute Force Preimage (Single Target)",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = dynamicColors.textSecondary
                    )
                    Text(
                        text = "2^$currentN trials",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = dynamicColors.textPrimary
                    )
                }
                LinearProgressIndicator(
                    progress = { 1.0f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF00C3FF),
                    trackColor = dynamicColors.cardBorder.copy(alpha = 0.3f)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Birthday Collision Bound √(2ⁿ)",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberGold
                    )
                    Text(
                        text = "2^$birthdayBoundExponent trials",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = AmberGold
                    )
                }
                LinearProgressIndicator(
                    progress = { 0.5f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = AmberGold,
                    trackColor = dynamicColors.cardBorder.copy(alpha = 0.3f)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(dynamicColors.cardBorder.copy(alpha = 0.15f))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "For SHA-$currentN, attacker needs only ~2^$birthdayBoundExponent operations to forge a collision!",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = dynamicColors.textPrimary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Interactive Target Dial: Calibrate defense against 256-bit collision attack
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(dynamicColors.cardBackground)
                .border(1.dp, dynamicColors.cardBorder.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "DEFENSIVE BOUND SPECIFICATION",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Black,
                    color = AmberGold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "What is the birthday bound trial space for a 256-bit hash (SHA-256)?",
                    fontSize = 12.5.sp,
                    color = dynamicColors.textPrimary,
                    textAlign = TextAlign.Center
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(16, 64, 128, 256).forEach { exp ->
                        val isChosen = calibratedTargetExponent == exp
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isChosen) AmberGold.copy(alpha = 0.25f)
                                    else dynamicColors.cardBorder.copy(alpha = 0.2f)
                                )
                                .border(
                                    if (isChosen) 2.dp else 1.dp,
                                    if (isChosen) AmberGold else dynamicColors.cardBorder.copy(alpha = 0.3f),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    calibratedTargetExponent = exp
                                    simulationTested = false
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "2^$exp",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = if (isChosen) AmberGold else dynamicColors.textPrimary
                            )
                        }
                    }
                }
            }
        }

        // Action Probe Button
        Button(
            onClick = {
                simulationTested = true
                if (calibratedTargetExponent == 128) {
                    isCorrectMatch = true
                    onActionSuccess()
                    onComplete()
                } else {
                    isCorrectMatch = false
                    onDeductHeart()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (simulationTested && isCorrectMatch) DuolingoGreen
                else if (simulationTested) DuolingoRed
                else AmberGold
            )
        ) {
            Text(
                text = if (simulationTested && isCorrectMatch) "✓ BOUND VERIFIED (2^128 SECURITY)"
                else if (simulationTested) "✕ MISALIGNED BOUND (RETRY)"
                else "PROBE BIRTHDAY BOUND COLLISION",
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                color = Color.Black
            )
        }

        if (simulationTested && isCorrectMatch) {
            Text(
                text = "Security Proof: Even with birthday bound halving, 2^128 operations exceeds 10^38 calculations, making SHA-256 collision infeasible on classical supercomputers!",
                fontSize = 11.5.sp,
                color = DuolingoGreen,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}
