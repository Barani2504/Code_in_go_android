package com.simats.codeingo.ui.exercise.labs

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

/**
 * 🏕️ XorBoardLab — Chapter 0 Micro-Lab
 * Interactive XOR Studio demonstrating:
 *  - XOR Truth Table (0⊕0=0, 0⊕1=1, 1⊕0=1, 1⊕1=0)
 *  - 4-bit Plaintext (P) ⊕ Key (K) = Ciphertext (C)
 *  - Self-Inverse / Reversibility: (P ⊕ K) ⊕ K = P
 *  - The Two-Time Pad Hazard: C1 ⊕ C2 = P1 ⊕ P2 (Key Cancelation)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun XorBoardLab(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {}
) {
    val dynamicColors = LocalDynamicThemeColors.current

    // 4-bit state for Plaintext (A) and Key (B)
    var plaintextBits by remember { mutableStateOf(intArrayOf(1, 0, 1, 0)) } // 1010
    var keyBits by remember { mutableStateOf(intArrayOf(0, 1, 1, 0)) }       // 0110

    // Computed Ciphertext: C = P ⊕ K
    val ciphertextBits = IntArray(4) { i -> plaintextBits[i] xor keyBits[i] }

    // Decrypted: C ⊕ K
    val decryptedBits = IntArray(4) { i -> ciphertextBits[i] xor keyBits[i] }

    val isReversibleMatched = plaintextBits.contentEquals(decryptedBits)

    val slateAccent = Color(0xFF64748B)
    val cyanAccent = Color(0xFF06B6D4)
    val amberAccent = Color(0xFFF59E0B)
    val purpleAccent = Color(0xFFA855F7)
    val greenAccent = Color(0xFF10B981)
    val crimsonAccent = Color(0xFFEF4444)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(dynamicColors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // ── TOP NAV HEADER ──────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = dynamicColors.textSecondary)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⚡", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "XOR BOARD LAB",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = dynamicColors.textPrimary
                    )
                }
                Text(
                    "Chapter 0: Foundations Primer",
                    fontSize = 11.sp,
                    color = slateAccent,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // ── SCROLLABLE WORKSPACE ────────────────────────────────────
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── 1. XOR TRUTH TABLE CARD ─────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(dynamicColors.cardBackground)
                    .border(1.5.dp, slateAccent.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "THE XOR (⊕) TRUTH TABLE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = slateAccent,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        "Rule: The output is 1 if and only if the two inputs are DIFFERENT.",
                        fontSize = 12.sp,
                        color = dynamicColors.textSecondary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        TruthTableCell("0 ⊕ 0", "0", isOne = false, modifier = Modifier.weight(1f))
                        TruthTableCell("0 ⊕ 1", "1", isOne = true, modifier = Modifier.weight(1f))
                        TruthTableCell("1 ⊕ 0", "1", isOne = true, modifier = Modifier.weight(1f))
                        TruthTableCell("1 ⊕ 1", "0", isOne = false, modifier = Modifier.weight(1f))
                    }
                }
            }

            // ── 2. LIVE 4-BIT ENCRYPTION & DECRYPTION ENGINE ────────
            Text(
                "TAP BITS TO ENCRYPT & DECRYPT (P ⊕ K = C)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = slateAccent,
                letterSpacing = 0.5.sp
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(dynamicColors.cardBackground)
                    .border(1.5.dp, purpleAccent.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Row 1: Plaintext (P)
                    BitRowSection(
                        label = "PLAINTEXT (P)",
                        bits = plaintextBits,
                        color = cyanAccent,
                        editable = true,
                        onToggle = { idx ->
                            val copy = plaintextBits.clone()
                            copy[idx] = if (copy[idx] == 1) 0 else 1
                            plaintextBits = copy
                        }
                    )

                    // Operator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("⊕ (XOR with Key)", fontSize = 14.sp, fontWeight = FontWeight.Black, color = purpleAccent)
                    }

                    // Row 2: Key (K)
                    BitRowSection(
                        label = "SECRET KEY (K)",
                        bits = keyBits,
                        color = amberAccent,
                        editable = true,
                        onToggle = { idx ->
                            val copy = keyBits.clone()
                            copy[idx] = if (copy[idx] == 1) 0 else 1
                            keyBits = copy
                        }
                    )

                    // Divider
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(slateAccent.copy(alpha = 0.3f)))

                    // Row 3: Ciphertext (C = P ⊕ K)
                    BitRowSection(
                        label = "CIPHERTEXT (C = P ⊕ K)",
                        bits = ciphertextBits,
                        color = greenAccent,
                        editable = false
                    )

                    // Divider
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(slateAccent.copy(alpha = 0.3f)))

                    // Row 4: Decrypted (C ⊕ K = P)
                    BitRowSection(
                        label = "DECRYPT (C ⊕ K ➔ RECOVERS P)",
                        bits = decryptedBits,
                        color = if (isReversibleMatched) cyanAccent else crimsonAccent,
                        editable = false
                    )

                    // Success callout
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(greenAccent.copy(alpha = 0.12f))
                            .border(1.dp, greenAccent.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "✨ Self-Inverse verified: (P ⊕ K) ⊕ K = P. The exact same hardware circuit encrypts AND decrypts!",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = greenAccent
                        )
                    }
                }
            }

            // ── 3. TWO-TIME PAD HAZARD TEASER ────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(crimsonAccent.copy(alpha = 0.08f))
                    .border(1.5.dp, crimsonAccent.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⚠️", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "The Deadly Key-Reuse Hazard",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = crimsonAccent
                        )
                    }
                    Text(
                        text = "What happens if you use the same key K to encrypt two messages P₁ and P₂?\n\n" +
                                "   C₁ = P₁ ⊕ K\n" +
                                "   C₂ = P₂ ⊕ K\n\n" +
                                "When an eavesdropper XORs the two ciphertexts together:\n" +
                                "   C₁ ⊕ C₂ = (P₁ ⊕ K) ⊕ (P₂ ⊕ K) = P₁ ⊕ P₂!\n\n" +
                                "The key K cancels itself out completely! An attacker now has the XOR of your two plaintexts, allowing English frequency analysis to recover BOTH messages. This is why One-Time Pads must NEVER reuse a key!",
                        fontSize = 12.sp,
                        color = dynamicColors.textSecondary,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TruthTableCell(
    expression: String,
    result: String,
    isOne: Boolean,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val amberAccent = Color(0xFFF59E0B)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(dynamicColors.inputBackground)
            .border(1.dp, if (isOne) amberAccent.copy(alpha = 0.6f) else dynamicColors.inputBorder, RoundedCornerShape(10.dp))
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(expression, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textSecondary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "= $result",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = if (isOne) amberAccent else dynamicColors.textPrimary,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun BitRowSection(
    label: String,
    bits: IntArray,
    color: Color,
    editable: Boolean,
    onToggle: (Int) -> Unit = {}
) {
    val dynamicColors = LocalDynamicThemeColors.current

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (i in 0..3) {
                val bit = bits[i]
                val isOne = bit == 1

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isOne) color.copy(alpha = 0.2f) else dynamicColors.inputBackground)
                        .border(
                            1.dp,
                            if (isOne) color else dynamicColors.inputBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .then(if (editable) Modifier.clickable { onToggle(i) } else Modifier)
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$bit",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isOne) color else dynamicColors.textSecondary,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
