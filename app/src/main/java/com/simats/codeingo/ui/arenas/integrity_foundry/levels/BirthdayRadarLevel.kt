package com.simats.codeingo.ui.arenas.integrity_foundry.levels

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
import com.simats.codeingo.ui.theme.SubtextGray

@Composable
fun BirthdayRadarLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current

    // Birthday Bound Radar: O(2^(n/2)) collision search complexity
    val bitLengths = listOf(16, 32, 64, 128, 256)
    var selectedBitIndex by remember { mutableIntStateOf(3) } // 128b default
    val currentBits = bitLengths[selectedBitIndex]
    val collisionExponent = currentBits / 2

    // Track simulated evaluations
    var simulationChecked by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "BIRTHDAY BOUND COLLISION RADAR",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Square-Root Collision Search Complexity: O(2^(n/2))",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = dynamicColors.textSecondary,
                textAlign = TextAlign.Center
            )
        }

        // Explanatory Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(dynamicColors.cardBorder.copy(alpha = 0.2f))
                .border(1.dp, AmberGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "🎯 The Birthday Paradox in Cryptography:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AmberGold
                )
                Text(
                    text = "Finding ANY collision H(m₁) = H(m₂) with m₁ ≠ m₂ requires only ~√(2ⁿ) = 2^(n/2) hash computations with 50% probability, halving nominal digest bit security! To resist collisions with 128 bits of work, a hash must be at least 256 bits long.",
                    fontSize = 12.sp,
                    color = dynamicColors.textPrimary,
                    lineHeight = 16.sp
                )
            }
        }

        // Bit Length Tabs (48dp height for touch compliance)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "SELECT HASH DIGEST SIZE (n BITS):",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = SubtextGray
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                bitLengths.forEachIndexed { idx, bits ->
                    val isSelected = selectedBitIndex == idx
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) AmberGold.copy(alpha = 0.25f)
                                else dynamicColors.cardBorder.copy(alpha = 0.2f)
                            )
                            .border(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) AmberGold else dynamicColors.cardBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                selectedBitIndex = idx
                                simulationChecked = false
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${bits}b",
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (isSelected) AmberGold else dynamicColors.textPrimary
                        )
                    }
                }
            }
        }

        // Radar Computation Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(if (collisionExponent >= 128) ArenaSuccessGreen.copy(alpha = 0.15f) else dynamicColors.cardBorder.copy(alpha = 0.25f))
                .border(1.5.dp, if (collisionExponent >= 128) ArenaSuccessGreen else AmberGold, RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "NOMINAL SIZE (PRE-IMAGE):", fontSize = 10.sp, fontWeight = FontWeight.Black, color = SubtextGray)
                    Text(text = "2^$currentBits hashes", fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "COLLISION BOUND (BIRTHDAY):", fontSize = 10.sp, fontWeight = FontWeight.Black, color = AmberGold)
                    Text(
                        text = "2^$collisionExponent hashes (~2^(n/2))",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        color = if (collisionExponent >= 128) ArenaSuccessGreen else Color(0xFFFF6D00)
                    )
                }
                Text(
                    text = when {
                        currentBits <= 32 -> "⚠️ Trivial Collision: Instant GPU break in milliseconds!"
                        currentBits == 64 -> "⚠️ Weak: 2³² ≈ 4.3 billion hashes. Solvable in minutes."
                        currentBits == 128 -> "⚠️ Deprecated (MD5): 2⁶⁴ is within reach of distributed clusters."
                        else -> "✓ Cryptographically Secure (SHA-256): 2¹²⁸ exceeds global compute power!"
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (collisionExponent >= 128) ArenaSuccessGreen else Color(0xFFFF6D00)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Tactile3DButton(
            title = if (currentBits == 256) "Lock 256-Bit Collision Defense (2¹²⁸) ✓" else "Calibrate to 256-Bit Secure Tier",
            style = if (currentBits == 256) Tactile3DButtonColor.GREEN else Tactile3DButtonColor.AMBER,
            onClick = {
                if (currentBits == 256) {
                    onActionSuccess()
                    onComplete()
                } else {
                    selectedBitIndex = 4 // Auto calibrate to 256b
                    onActionSuccess()
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
