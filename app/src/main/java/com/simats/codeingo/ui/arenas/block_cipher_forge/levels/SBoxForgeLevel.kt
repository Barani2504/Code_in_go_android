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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.simats.codeingo.ui.arenas.crypto.ArenaCryptoEngine
import com.simats.codeingo.ui.components.Duolingo3DButton
import com.simats.codeingo.ui.components.Duolingo3DButtonColor
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import kotlinx.coroutines.delay

@Composable
fun SBoxForgeLevel(
    toolActive: Boolean,
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current

    // Target challenges: byte -> (expected row, expected col, substituted byte)
    val challenges = remember {
        listOf(
            Triple(0x53, 0x05, 0x03), // 0x53 -> S[5][3] = 0xED
            Triple(0x00, 0x00, 0x00), // 0x00 -> S[0][0] = 0x63
            Triple(0x76, 0x07, 0x06)  // 0x76 -> S[7][6] = 0x38
        )
    }
    var challengeIndex by remember { mutableIntStateOf(0) }
    val currentChallenge = challenges[challengeIndex]

    var selectedRow by remember { mutableIntStateOf(0) }
    var selectedCol by remember { mutableIntStateOf(0) }

    val currentInputByte = currentChallenge.first
    val targetRow = currentChallenge.second
    val targetCol = currentChallenge.third
    val expectedSubstitutedByte = ArenaCryptoEngine.sBoxLookup(currentInputByte)

    // Current cell under crosshairs
    val currentCoordinateByte = (selectedRow shl 4) or selectedCol
    val currentSBoxLookup = ArenaCryptoEngine.sBoxLookup(currentCoordinateByte)
    val isCoordinateMatched = selectedRow == targetRow && selectedCol == targetCol

    var feedbackMsg by remember { mutableStateOf("Dial Row and Col hex nibbles to locate the S-Box cell!") }
    var strikeAnim by remember { mutableStateOf(false) }

    LaunchedEffect(strikeAnim) {
        if (strikeAnim) {
            delay(400L)
            strikeAnim = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Target Header
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TARGET BYTE ${challengeIndex + 1} OF ${challenges.size}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = AmberGold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "FIPS-197 AES",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = dynamicColors.textSecondary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Anvil Forge Display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(dynamicColors.cardBorder.copy(alpha = 0.2f))
                    .border(1.dp, if (strikeAnim) AmberGold else dynamicColors.cardBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "INPUT STATE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textSecondary)
                        Text(
                            text = "0x" + currentInputByte.toString(16).uppercase().padStart(2, '0'),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = dynamicColors.textPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = AmberGold)
                    Spacer(modifier = Modifier.width(16.dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "S-BOX FORGED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textSecondary)
                        Text(
                            text = if (isCoordinateMatched) "0x" + expectedSubstitutedByte.toString(16).uppercase().padStart(2, '0') else "0x??",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = if (isCoordinateMatched) DuolingoGreen else Color(0xFF00C3FF)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Theory breakdown
        Text(
            text = "S(x) = Affine(x⁻¹ mod x⁸+x⁴+x³+x+1) ⊕ 0x63",
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            color = dynamicColors.textSecondary
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Hex Coordinates Dial (Row: 0..F, Col: 0..F)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Row Dial
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(dynamicColors.cardBorder.copy(alpha = 0.15f))
                    .padding(10.dp)
            ) {
                Text(text = "ROW (High Nibble)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { selectedRow = (selectedRow - 1 + 16) % 16 },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Dec", tint = dynamicColors.textPrimary)
                    }
                    Text(
                        text = "0x" + selectedRow.toString(16).uppercase(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (selectedRow == targetRow || toolActive) DuolingoGreen else dynamicColors.textPrimary,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    IconButton(
                        onClick = { selectedRow = (selectedRow + 1) % 16 },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Inc", tint = dynamicColors.textPrimary)
                    }
                }
            }

            // Col Dial
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(dynamicColors.cardBorder.copy(alpha = 0.15f))
                    .padding(10.dp)
            ) {
                Text(text = "COL (Low Nibble)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { selectedCol = (selectedCol - 1 + 16) % 16 },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Dec", tint = dynamicColors.textPrimary)
                    }
                    Text(
                        text = "0x" + selectedCol.toString(16).uppercase(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (selectedCol == targetCol || toolActive) DuolingoGreen else dynamicColors.textPrimary,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    IconButton(
                        onClick = { selectedCol = (selectedCol + 1) % 16 },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Inc", tint = dynamicColors.textPrimary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Micro S-Box Crosshair Grid Preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(dynamicColors.cardBorder.copy(alpha = 0.25f))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Matrix Crosshair: S[${selectedRow.toString(16).uppercase()}][${selectedCol.toString(16).uppercase()}] = 0x${currentSBoxLookup.toString(16).uppercase().padStart(2, '0')}",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (isCoordinateMatched) DuolingoGreen else AmberGold
                )
                Text(
                    text = feedbackMsg,
                    fontSize = 11.sp,
                    color = dynamicColors.textSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Strike Anvil Action Button
        Duolingo3DButton(
            title = if (isCoordinateMatched) "Forge Byte (0x${expectedSubstitutedByte.toString(16).uppercase().padStart(2, '0')})" else "Strike Anvil",
            style = if (isCoordinateMatched) Duolingo3DButtonColor.GREEN else Duolingo3DButtonColor.AMBER,
            onClick = {
                strikeAnim = true
                if (isCoordinateMatched) {
                    onActionSuccess()
                    if (challengeIndex + 1 < challenges.size) {
                        challengeIndex++
                        feedbackMsg = "Byte substituted! Onto coordinate ${(challengeIndex + 1)}."
                    } else {
                        onComplete()
                    }
                } else {
                    onDeductHeart()
                    feedbackMsg = "Coordinate mismatch! Split byte 0x${currentInputByte.toString(16).uppercase()} into row 0x${targetRow.toString(16).uppercase()} and col 0x${targetCol.toString(16).uppercase()}."
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
