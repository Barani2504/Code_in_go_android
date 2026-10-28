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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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

@Composable
fun AddRoundKeyLockLevel(
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

    // State Byte S = 0xA5 (10100101)
    // Target Ciphertext C = 0xF9 (11111001)
    // AES AddRoundKey property: S ⊕ K = C  <=>  K = S ⊕ C
    // Expected Key Byte K = 0xA5 ⊕ 0xF9 = 0x5C (01011100)
    val stateByte = 0xA5
    val targetCipherByte = 0xF9
    val expectedKeyByte = stateByte xor targetCipherByte // 0x5C

    // 8 interactive bits for the player to derive the Round Key K
    val userBits = remember { mutableStateListOf(0, 0, 0, 0, 0, 0, 0, 0) }

    fun computeCurrentByte(): Int {
        var v = 0
        userBits.forEachIndexed { idx, bit ->
            if (bit == 1) v = v or (1 shl (7 - idx))
        }
        return v
    }

    val currentCalculatedKey = computeCurrentByte()
    val isSolved = currentCalculatedKey == expectedKeyByte

    val matchingBitsCount = (0..7).count { idx ->
        val expectedBit = ((stateByte xor targetCipherByte) shr (7 - idx)) and 1
        userBits[idx] == expectedBit
    }

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
                    text = "ADDROUNDKEY SELF-INVERSE DERIVATION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = accent,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Bitwise XOR Subkey Matrix",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = dynamicColors.textPrimary
                )
            }
            ArenaStatusBadge(
                type = if (isSolved) ArenaBadgeType.SUCCESS else ArenaBadgeType.WARNING,
                label = if (isSolved) "0x5C KEY DERIVED" else "$matchingBitsCount/8 BITS MATCH"
            )
        }

        // Equation and Theory Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(cardBg)
                .border(
                    1.dp,
                    if (isSolved) ArenaSuccessGreen.copy(alpha = 0.6f) else dynamicColors.cardBorder,
                    RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Self-Inverting Involution Property:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = primary
                )
                Text(
                    text = "In AES decryption, AddRoundKey uses the exact same bitwise XOR circuit as encryption: S ⊕ K = C  ⟺  K = S ⊕ C. Given State S and Target Ciphertext C, deduce Round Key K:",
                    fontSize = 11.sp,
                    color = dynamicColors.textSecondary,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "STATE (S):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = dynamicColors.textSecondary
                    )
                    Text(
                        text = "0x" + stateByte.toString(16).uppercase() + " (${stateByte.toString(2).padStart(8, '0')})",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = dynamicColors.textPrimary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "TARGET CIPHER (C):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = dynamicColors.textSecondary
                    )
                    Text(
                        text = "0x" + targetCipherByte.toString(16).uppercase() + " (${targetCipherByte.toString(2).padStart(8, '0')})",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = dynamicColors.textPrimary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "DERIVED KEY (K = S ⊕ C):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = accent
                    )
                    Text(
                        text = "0x" + currentCalculatedKey.toString(16).uppercase().padStart(2, '0') + " (${currentCalculatedKey.toString(2).padStart(8, '0')})",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        color = if (isSolved) ArenaSuccessGreen else accent
                    )
                }
            }
        }

        Text(
            text = "Toggle the 8 Key Bits to solve K = S ⊕ C (Bitwise XOR):",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = dynamicColors.textPrimary
        )

        // 8 Interactive Bit Switches (48dp x 56dp for touch target compliance)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(8) { bitIdx ->
                val sBit = (stateByte shr (7 - bitIdx)) and 1
                val cBit = (targetCipherByte shr (7 - bitIdx)) and 1
                val expectedBit = sBit xor cBit
                val bitVal = userBits[bitIdx]
                val bitMatches = bitVal == expectedBit

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "$sBit", fontSize = 10.sp, color = dynamicColors.textSecondary)
                    Text(text = "⊕", fontSize = 8.sp, color = accent)
                    Text(text = "$cBit", fontSize = 10.sp, color = dynamicColors.textSecondary)
                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .minTouchTarget48()
                            .height(52.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSolved) ArenaSuccessGreen.copy(alpha = 0.2f)
                                else if (bitMatches) accent.copy(alpha = 0.15f)
                                else dynamicColors.cardBorder.copy(alpha = 0.3f)
                            )
                            .border(
                                1.5.dp,
                                if (isSolved) ArenaSuccessGreen
                                else if (bitMatches) accent
                                else dynamicColors.cardBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                ArenaHaptics.tap(view, reduceMotion)
                                userBits[bitIdx] = 1 - userBits[bitIdx]
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$bitVal",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = if (isSolved) ArenaSuccessGreen else if (bitMatches) accent else dynamicColors.textPrimary
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = isSolved,
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
                    text = "✓ Key Byte 0x5C (01011100) successfully derived! XOR involution confirmed.",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = ArenaSuccessGreen,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f, fill = false))

        Tactile3DButton(
            title = if (isSolved) "Unlock Cryptographic Vault ✓" else "Verify Key Byte ($matchingBitsCount/8 Bits)",
            style = if (isSolved) Tactile3DButtonColor.GREEN else Tactile3DButtonColor.AMBER,
            onClick = {
                if (isSolved) {
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
