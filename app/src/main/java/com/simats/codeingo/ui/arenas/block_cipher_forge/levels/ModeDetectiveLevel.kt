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
import com.simats.codeingo.ui.components.Duolingo3DButton
import com.simats.codeingo.ui.components.Duolingo3DButtonColor
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

@Composable
fun ModeDetectiveLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current

    var selectedModeTab by remember { mutableIntStateOf(0) } // 0: ECB, 1: CBC, 2: CTR
    var ecbLeakSpotted by remember { mutableStateOf(false) }
    var ivReuseTested by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "CIPHER MODE AUDITOR",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Spot ECB visual pattern leakage & CTR IV-reuse vulnerability",
                fontSize = 11.sp,
                color = dynamicColors.textSecondary,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Mode Selector Tabs (ECB vs CBC vs CTR)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("ECB Leakage", "CBC Chaining", "CTR Nonce Reuse").forEachIndexed { index, title ->
                val isSelected = selectedModeTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Color(0xFF00C3FF).copy(alpha = 0.2f) else dynamicColors.cardBorder.copy(alpha = 0.2f))
                        .border(1.dp, if (isSelected) Color(0xFF00C3FF) else Color.Transparent, RoundedCornerShape(8.dp))
                        .clickable { selectedModeTab = index }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color(0xFF00C3FF) else dynamicColors.textSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Visual Canvas per tab
        when (selectedModeTab) {
            0 -> {
                // ECB Leak Canvas
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "ECB Encrypted Bitmap (Tux Penguin Leakage)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DuolingoRed
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Simulated 6x6 pixel grid showing structural outline
                    Column(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black)
                            .clickable {
                                ecbLeakSpotted = true
                                onActionSuccess()
                            }
                            .padding(4.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        val penguinPattern = listOf(
                            listOf(0, 0, 1, 1, 0, 0),
                            listOf(0, 1, 1, 1, 1, 0),
                            listOf(0, 1, 0, 0, 1, 0),
                            listOf(1, 1, 1, 1, 1, 1),
                            listOf(1, 1, 0, 0, 1, 1),
                            listOf(0, 1, 1, 1, 1, 0)
                        )
                        penguinPattern.forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                row.forEach { cell ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(20.dp)
                                            .background(if (cell == 1) (if (ecbLeakSpotted) DuolingoRed else Color(0xFF6B7280)) else Color(0xFF1F2937))
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (ecbLeakSpotted) "✓ Structural Leak Flagged! Identical blocks encrypt to identical ciphertext." else "Tap the penguin outline to flag ECB leakage!",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (ecbLeakSpotted) DuolingoGreen else AmberGold,
                        textAlign = TextAlign.Center
                    )
                }
            }
            1 -> {
                // CBC Chaining Info
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Cipher Block Chaining (CBC)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "C[i] = E(P[i] ⊕ C[i-1]), C[0] = IV\n1 bit flip in C[1] corrupts P[1] completely and flips the exact bit in P[2].",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = dynamicColors.textSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
            2 -> {
                // CTR Nonce Reuse Demonstration
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "CTR Keystream Reuse Exploit", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DuolingoRed)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "C₁ = P₁ ⊕ Keystream\nC₂ = P₂ ⊕ Keystream\nC₁ ⊕ C₂ = P₁ ⊕ P₂ (Zero secrecy!)",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = AmberGold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Duolingo3DButton(
                        title = if (ivReuseTested) "✓ Nonce Collision Proven" else "Simulate IV Reuse Trap",
                        style = Duolingo3DButtonColor.BLUE,
                        onClick = {
                            ivReuseTested = true
                            onActionSuccess()
                        },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Complete Mission Button
        val ready = ecbLeakSpotted && ivReuseTested
        Duolingo3DButton(
            title = if (ready) "Certify Mode Audit" else "Complete Both Audits (${if (ecbLeakSpotted) 1 else 0}/2)",
            style = if (ready) Duolingo3DButtonColor.GREEN else Duolingo3DButtonColor.AMBER,
            onClick = {
                if (ready) {
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
