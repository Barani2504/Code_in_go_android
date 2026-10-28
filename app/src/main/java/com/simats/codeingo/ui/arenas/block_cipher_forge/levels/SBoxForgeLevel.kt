package com.simats.codeingo.ui.arenas.block_cipher_forge.levels

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Shield
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
import com.simats.codeingo.ui.arenas.crypto.ArenaCryptoEngine
import com.simats.codeingo.ui.components.Tactile3DButton
import com.simats.codeingo.ui.components.Tactile3DButtonColor
import com.simats.codeingo.ui.theme.ArenaDangerRed
import com.simats.codeingo.ui.theme.ArenaSuccessGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.LocalReduceMotion
import com.simats.codeingo.ui.theme.SubtextGray
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
    val view = LocalView.current
    val reduceMotion = LocalReduceMotion.current
    val isDark = dynamicColors.isDark

    val primary = ArenaPalettes.Arena2Primary
    val accent = ArenaPalettes.Arena2Accent
    val cardBg = if (isDark) ArenaPalettes.Arena2CardDark else dynamicColors.cardBackground

    // Target challenges: byte -> (expected row, expected col)
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
    var lastCheckSuccess by remember { mutableStateOf<Boolean?>(null) }
    var strikeAnim by remember { mutableStateOf(false) }

    LaunchedEffect(strikeAnim) {
        if (strikeAnim) {
            delay(350L)
            strikeAnim = false
        }
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
                    text = "AES S-BOX NON-LINEAR FORGE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = primary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Multiplicative Inverse in GF(2⁸) + Affine Transform",
                    fontSize = 10.sp,
                    color = dynamicColors.textSecondary
                )
            }
            ArenaStatusBadge(
                type = if (isCoordinateMatched) ArenaBadgeType.SUCCESS else ArenaBadgeType.ENCRYPTED,
                label = "BYTE ${challengeIndex + 1}/${challenges.size}"
            )
        }

        // Briefing Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(cardBg)
                .border(1.dp, primary.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Confusion via Substitution:",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = primary
                    )
                }
                Text(
                    text = "The AES SubBytes step replaces each state byte with its S-Box equivalent. Split input byte 0x${currentInputByte.toString(16).uppercase().padStart(2, '0')} into High Nibble (Row) and Low Nibble (Col) to locate the cell in the 16x16 lookup table!",
                    fontSize = 11.sp,
                    color = dynamicColors.textPrimary,
                    lineHeight = 15.sp
                )
            }
        }

        // Anvil Forge State Display Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(cardBg)
                .border(1.5.dp, if (strikeAnim) accent else primary.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "INPUT STATE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SubtextGray)
                    Text(
                        text = "0x" + currentInputByte.toString(16).uppercase().padStart(2, '0'),
                        fontSize = 22.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        color = dynamicColors.textPrimary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "➔ S-Box ➔", fontSize = 12.sp, fontWeight = FontWeight.Black, color = primary)
                Spacer(modifier = Modifier.width(16.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "SUBSTITUTED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SubtextGray)
                    Text(
                        text = if (isCoordinateMatched) "0x" + expectedSubstitutedByte.toString(16).uppercase().padStart(2, '0') else "0x??",
                        fontSize = 22.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        color = if (isCoordinateMatched) ArenaSuccessGreen else primary
                    )
                }
            }
        }

        // Interactive Nibble Dials (Row & Col, minTouchTarget48)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Row Dial (High Nibble)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(cardBg)
                    .border(1.dp, if (selectedRow == targetRow || toolActive) ArenaSuccessGreen else primary.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .padding(10.dp)
            ) {
                Text(text = "ROW (High Nibble)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SubtextGray)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            ArenaHaptics.tap(view, reduceMotion)
                            selectedRow = (selectedRow - 1 + 16) % 16
                        },
                        modifier = Modifier.minTouchTarget48().size(48.dp)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Dec", tint = primary)
                    }
                    Text(
                        text = "0x" + selectedRow.toString(16).uppercase(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (selectedRow == targetRow || toolActive) ArenaSuccessGreen else dynamicColors.textPrimary,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    IconButton(
                        onClick = {
                            ArenaHaptics.tap(view, reduceMotion)
                            selectedRow = (selectedRow + 1) % 16
                        },
                        modifier = Modifier.minTouchTarget48().size(48.dp)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Inc", tint = primary)
                    }
                }
            }

            // Col Dial (Low Nibble)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(cardBg)
                    .border(1.dp, if (selectedCol == targetCol || toolActive) ArenaSuccessGreen else primary.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .padding(10.dp)
            ) {
                Text(text = "COL (Low Nibble)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SubtextGray)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            ArenaHaptics.tap(view, reduceMotion)
                            selectedCol = (selectedCol - 1 + 16) % 16
                        },
                        modifier = Modifier.minTouchTarget48().size(48.dp)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Dec", tint = primary)
                    }
                    Text(
                        text = "0x" + selectedCol.toString(16).uppercase(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (selectedCol == targetCol || toolActive) ArenaSuccessGreen else dynamicColors.textPrimary,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    IconButton(
                        onClick = {
                            ArenaHaptics.tap(view, reduceMotion)
                            selectedCol = (selectedCol + 1) % 16
                        },
                        modifier = Modifier.minTouchTarget48().size(48.dp)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Inc", tint = primary)
                    }
                }
            }
        }

        // Micro S-Box Crosshair Coordinates Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(cardBg)
                .border(1.dp, if (isCoordinateMatched) ArenaSuccessGreen else primary.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Matrix Coordinate: S[0x${selectedRow.toString(16).uppercase()}][0x${selectedCol.toString(16).uppercase()}] = 0x${currentSBoxLookup.toString(16).uppercase().padStart(2, '0')}",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (isCoordinateMatched) ArenaSuccessGreen else primary
                )
                Text(
                    text = feedbackMsg,
                    fontSize = 10.5.sp,
                    color = dynamicColors.textSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Live Feedback Banner
        AnimatedVisibility(visible = lastCheckSuccess != null, enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (lastCheckSuccess == true) ArenaSuccessGreen.copy(alpha = 0.12f)
                        else ArenaDangerRed.copy(alpha = 0.12f)
                    )
                    .border(
                        1.dp,
                        if (lastCheckSuccess == true) ArenaSuccessGreen else ArenaDangerRed,
                        RoundedCornerShape(10.dp)
                    )
                    .padding(10.dp)
            ) {
                Text(
                    text = feedbackMsg,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (lastCheckSuccess == true) ArenaSuccessGreen else ArenaDangerRed,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Strike Anvil Action Button
        Tactile3DButton(
            title = if (isCoordinateMatched) "Forge Byte (0x${expectedSubstitutedByte.toString(16).uppercase().padStart(2, '0')}) ✓" else "Strike S-Box Anvil",
            style = if (isCoordinateMatched) Tactile3DButtonColor.GREEN else Tactile3DButtonColor.AMBER,
            onClick = {
                strikeAnim = true
                if (isCoordinateMatched) {
                    ArenaHaptics.success(view, reduceMotion)
                    lastCheckSuccess = true
                    onActionSuccess()
                    if (challengeIndex + 1 < challenges.size) {
                        challengeIndex++
                        feedbackMsg = "✓ Byte substituted! Coordinate located for Byte ${(challengeIndex + 1)}."
                    } else {
                        onComplete()
                    }
                } else {
                    ArenaHaptics.error(view, reduceMotion)
                    lastCheckSuccess = false
                    onDeductHeart()
                    feedbackMsg = "❌ Coordinate mismatch! Input 0x${currentInputByte.toString(16).uppercase().padStart(2, '0')} requires row 0x${targetRow.toString(16).uppercase()} and col 0x${targetCol.toString(16).uppercase()}."
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .minTouchTarget48()
        )
    }
}
