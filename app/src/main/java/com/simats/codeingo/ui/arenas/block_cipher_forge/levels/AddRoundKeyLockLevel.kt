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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.components.Duolingo3DButton
import com.simats.codeingo.ui.components.Duolingo3DButtonColor
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

@Composable
fun AddRoundKeyLockLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current

    // Target state byte and round key byte
    val stateByte = 0xA5 // 10100101
    val keyByte = 0x5C   // 01011100
    val expectedXOR = stateByte xor keyByte // 11111001 = 0xF9

    // 8 interactive bits toggled by player
    val userBits = remember { mutableStateListOf(0, 0, 0, 0, 0, 0, 0, 0) }

    fun computeCurrentByte(): Int {
        var v = 0
        userBits.forEachIndexed { idx, bit ->
            if (bit == 1) v = v or (1 shl (7 - idx))
        }
        return v
    }

    val currentCalculated = computeCurrentByte()
    val isSolved = currentCalculated == expectedXOR

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "ADDROUNDKEY BITWISE LOCK",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "State ⊕ RoundKey = Ciphertext (Bitwise XOR)",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = dynamicColors.textSecondary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Binary Truth Table Demonstration
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(dynamicColors.cardBorder.copy(alpha = 0.2f))
                .padding(10.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "STATE BYTE:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textSecondary)
                    Text(text = "0x" + stateByte.toString(16).uppercase() + " (${stateByte.toString(2).padStart(8, '0')})", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = dynamicColors.textPrimary)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "KEY BYTE:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AmberGold)
                    Text(text = "0x" + keyByte.toString(16).uppercase() + " (${keyByte.toString(2).padStart(8, '0')})", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = AmberGold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "RESULT XOR:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSolved) DuolingoGreen else Color(0xFF00C3FF))
                    Text(text = "0x" + currentCalculated.toString(16).uppercase().padStart(2, '0'), fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Black, color = if (isSolved) DuolingoGreen else Color(0xFF00C3FF))
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Toggle the 8 XOR bits (1 ⊕ 1 = 0, 1 ⊕ 0 = 1):", fontSize = 11.sp, color = dynamicColors.textPrimary)
        Spacer(modifier = Modifier.height(8.dp))

        // 8 Interactive Bit Switches
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(8) { bitIdx ->
                val stateBit = (stateByte shr (7 - bitIdx)) and 1
                val keyBit = (keyByte shr (7 - bitIdx)) and 1
                val expectedBit = stateBit xor keyBit
                val bitVal = userBits[bitIdx]
                val bitCorrect = bitVal == expectedBit

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "$stateBit", fontSize = 9.sp, color = dynamicColors.textSecondary)
                    Text(text = "⊕", fontSize = 8.sp, color = AmberGold)
                    Text(text = "$keyBit", fontSize = 9.sp, color = AmberGold)
                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (bitCorrect) DuolingoGreen.copy(alpha = 0.2f) else dynamicColors.cardBorder.copy(alpha = 0.3f))
                            .border(1.dp, if (bitCorrect) DuolingoGreen else dynamicColors.cardBorder, RoundedCornerShape(8.dp))
                            .clickable {
                                userBits[bitIdx] = 1 - userBits[bitIdx]
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$bitVal",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = if (bitCorrect) DuolingoGreen else dynamicColors.textPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Unlock Vault Button
        Duolingo3DButton(
            title = if (isSolved) "Unlock Cryptographic Vault" else "Compute Bitwise State",
            style = if (isSolved) Duolingo3DButtonColor.GREEN else Duolingo3DButtonColor.AMBER,
            onClick = {
                if (isSolved) {
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
