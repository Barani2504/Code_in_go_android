package com.simats.codeingo.ui.arenas.block_cipher_forge.levels

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
import com.simats.codeingo.ui.components.Tactile3DButton
import com.simats.codeingo.ui.components.Tactile3DButtonColor
import com.simats.codeingo.ui.theme.ArenaDangerRed
import com.simats.codeingo.ui.theme.ArenaSuccessGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.LocalReduceMotion

@Composable
fun ModeDetectiveLevel(
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

    var selectedModeTab by remember { mutableIntStateOf(0) } // 0: ECB, 1: CBC, 2: CTR
    var ecbLeakSpotted by remember { mutableStateOf(false) }
    var cbcBitFlipped by remember { mutableStateOf(false) }
    var ctrReuseTested by remember { mutableStateOf(false) }

    val completedCount = listOf(ecbLeakSpotted, cbcBitFlipped, ctrReuseTested).count { it }
    val ready = completedCount == 3

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
                    text = "BLOCK CIPHER MODE AUDITOR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = accent,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "ECB, CBC & CTR Security Audit",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = dynamicColors.textPrimary
                )
            }
            ArenaStatusBadge(
                type = if (ready) ArenaBadgeType.SUCCESS else ArenaBadgeType.WARNING,
                label = if (ready) "ALL 3 MODES CERTIFIED" else "$completedCount/3 AUDITS COMPLETED"
            )
        }

        // Mode Selector Tabs (ECB vs CBC vs CTR)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("1. ECB Leak", "2. CBC Flip", "3. CTR Nonce").forEachIndexed { index, title ->
                val isSelected = selectedModeTab == index
                val isDone = when (index) {
                    0 -> ecbLeakSpotted
                    1 -> cbcBitFlipped
                    else -> ctrReuseTested
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .minTouchTarget48()
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isSelected) primary.copy(alpha = 0.2f)
                            else if (isDone) ArenaSuccessGreen.copy(alpha = 0.12f)
                            else dynamicColors.cardBorder.copy(alpha = 0.2f)
                        )
                        .border(
                            1.5.dp,
                            if (isSelected) primary
                            else if (isDone) ArenaSuccessGreen.copy(alpha = 0.5f)
                            else Color.Transparent,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            ArenaHaptics.tap(view, reduceMotion)
                            selectedModeTab = index
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = title,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                            color = if (isSelected) primary else if (isDone) ArenaSuccessGreen else dynamicColors.textSecondary
                        )
                        Text(
                            text = if (isDone) "✓ Done" else "Pending",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDone) ArenaSuccessGreen else accent
                        )
                    }
                }
            }
        }

        // Visual Canvas per tab
        when (selectedModeTab) {
            0 -> {
                // Tab 0: ECB Leak Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(cardBg)
                        .border(1.dp, dynamicColors.cardBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "ECB Encrypted Bitmap (The Linux Tux Dilemma)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ArenaDangerRed
                        )
                        Text(
                            text = "In Electronic Codebook (ECB), every identical 16-byte plaintext block produces the identical ciphertext block. Tap the encrypted canvas to flag the leaked penguin silhouette:",
                            fontSize = 11.sp,
                            color = dynamicColors.textSecondary,
                            textAlign = TextAlign.Center
                        )

                        // Simulated 6x6 pixel grid showing structural outline
                        Column(
                            modifier = Modifier
                                .size(148.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.Black)
                                .border(1.dp, if (ecbLeakSpotted) ArenaDangerRed else dynamicColors.cardBorder, RoundedCornerShape(10.dp))
                                .clickable {
                                    if (!ecbLeakSpotted) {
                                        ArenaHaptics.success(view, reduceMotion)
                                        ecbLeakSpotted = true
                                        onActionSuccess()
                                    }
                                }
                                .padding(6.dp),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
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
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    row.forEach { cell ->
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(18.dp)
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(
                                                    if (cell == 1) {
                                                        if (ecbLeakSpotted) primary else Color(0xFF718096)
                                                    } else {
                                                        Color(0xFF1A202C)
                                                    }
                                                )
                                        )
                                    }
                                }
                            }
                        }

                        Text(
                            text = if (ecbLeakSpotted)
                                "✓ Structural Leak Flagged! ECB lacks diffusion across blocks."
                            else
                                "Tap canvas to trigger audit detection",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (ecbLeakSpotted) ArenaSuccessGreen else accent
                        )
                    }
                }
            }
            1 -> {
                // Tab 1: CBC Bit-Flipping Interactive Lab
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(cardBg)
                        .border(1.dp, dynamicColors.cardBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Cipher Block Chaining (CBC) Malleability",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = primary
                        )
                        Text(
                            text = "In CBC: P[i] = Decrypt(C[i]) ⊕ C[i-1]. Flipping bit b in ciphertext C[1] completely scrambles P[1], but flips the exact same bit b in plaintext P[2]!",
                            fontSize = 11.sp,
                            color = dynamicColors.textSecondary,
                            textAlign = TextAlign.Center
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(dynamicColors.cardBorder.copy(alpha = 0.25f))
                                .padding(10.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Ciphertext C[1] bit #5: ${if (cbcBitFlipped) "1 (MODIFIED)" else "0 (ORIGINAL)"}",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = if (cbcBitFlipped) ArenaDangerRed else dynamicColors.textPrimary
                                )
                                Text(
                                    text = "Decrypted P[1]: ${if (cbcBitFlipped) "[SCRAMBLED NOISE: 0x8F 0x2A ...]" else "admin=false;id=04"}",
                                    fontSize = 10.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (cbcBitFlipped) ArenaDangerRed else dynamicColors.textSecondary
                                )
                                Text(
                                    text = "Decrypted P[2]: ${if (cbcBitFlipped) "admin=TRUE!;id=04 (PRECISE FORGERY!)" else "admin=false;id=04"}",
                                    fontSize = 10.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = if (cbcBitFlipped) ArenaSuccessGreen else dynamicColors.textSecondary
                                )
                            }
                        }

                        Tactile3DButton(
                            title = if (cbcBitFlipped) "✓ Bit-Flipping Malleability Proven" else "Inject 1-Bit Flip into C[1]",
                            style = if (cbcBitFlipped) Tactile3DButtonColor.GREEN else Tactile3DButtonColor.BLUE,
                            onClick = {
                                ArenaHaptics.tap(view, reduceMotion)
                                cbcBitFlipped = true
                                onActionSuccess()
                            },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        )
                    }
                }
            }
            2 -> {
                // Tab 2: CTR Nonce Reuse Demonstration
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(cardBg)
                        .border(1.dp, dynamicColors.cardBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "CTR Keystream Reuse (Two-Time Pad Trap)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ArenaDangerRed
                        )
                        Text(
                            text = "Counter Mode converts AES into a stream cipher. If the same Nonce/IV is used twice with the same key, XORing both ciphertexts cancels the keystream:",
                            fontSize = 11.sp,
                            color = dynamicColors.textSecondary,
                            textAlign = TextAlign.Center
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(dynamicColors.cardBorder.copy(alpha = 0.25f))
                                .padding(10.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "C₁ = P₁ ⊕ E_k(Nonce || Counter)",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = dynamicColors.textPrimary
                                )
                                Text(
                                    text = "C₂ = P₂ ⊕ E_k(Nonce || Counter)",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = dynamicColors.textPrimary
                                )
                                Text(
                                    text = "C₁ ⊕ C₂ = P₁ ⊕ P₂  (ZERO SECRECY!)",
                                    fontSize = 11.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    color = accent
                                )
                            }
                        }

                        Tactile3DButton(
                            title = if (ctrReuseTested) "✓ Nonce Collision Exploit Certified" else "Simulate CTR Nonce Collision",
                            style = if (ctrReuseTested) Tactile3DButtonColor.GREEN else Tactile3DButtonColor.BLUE,
                            onClick = {
                                ArenaHaptics.tap(view, reduceMotion)
                                ctrReuseTested = true
                                onActionSuccess()
                            },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f, fill = false))

        // Complete Mission Button
        Tactile3DButton(
            title = if (ready) "Certify Mode Audit (3/3) ✓" else "Complete All Audits ($completedCount/3)",
            style = if (ready) Tactile3DButtonColor.GREEN else Tactile3DButtonColor.AMBER,
            onClick = {
                if (ready) {
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
