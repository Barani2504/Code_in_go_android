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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.arenas.crypto.ArenaCryptoEngine
import com.simats.codeingo.ui.components.Tactile3DButton
import com.simats.codeingo.ui.components.Tactile3DButtonColor
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.ArenaSuccessGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray
import java.security.MessageDigest

@Composable
fun BitFlipLabLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    // 8-bit interactive input register
    val bits = remember { mutableStateListOf(0, 1, 1, 0, 0, 0, 1, 1) }
    var flipCount by remember { mutableIntStateOf(0) }

    fun computeDigest(bitList: List<Int>): ByteArray {
        val byteVal = bitList.fold(0) { acc, bit -> (acc shl 1) or bit }.toByte()
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(byteArrayOf(byteVal))
    }

    var previousDigest by remember { mutableStateOf<ByteArray?>(null) }
    val currentDigest = remember(bits.toList()) { computeDigest(bits.toList()) }

    val currentHashHex = remember(currentDigest) {
        currentDigest.joinToString("") { "%02x".format(it) }
    }

    // Live Hamming distance between pre-flip digest and post-flip digest
    val hammingDistance = remember(previousDigest, currentDigest) {
        if (previousDigest != null) {
            ArenaCryptoEngine.hammingBitDistance(previousDigest!!, currentDigest)
        } else {
            0
        }
    }

    val avalanchePercentage = if (previousDigest != null) {
        (hammingDistance / 256f) * 100f
    } else {
        0f
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Mission Briefing
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(if (isDark) Color(0xFF10192A) else dynamicColors.cardBackground)
                .border(1.2.dp, AmberGold.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = null,
                        tint = AmberGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "STRICT AVALANCHE HAMMING RADAR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = AmberGold
                    )
                }
                Text(
                    text = "Strict Avalanche Criterion (SAC): Flipping any single input bit must cause each output bit to flip with probability 0.5 (about 50% on average). Observe the Hamming distance between H(M) and H(M')!",
                    fontSize = 12.5.sp,
                    color = dynamicColors.textPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp
                )
            }
        }

        // 8-Bit Interactive Register (48dp+ touch target)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "INTERACTIVE 8-BIT REGISTER (TAP ANY BIT TO FLIP):",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = SubtextGray
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                bits.forEachIndexed { idx, bit ->
                    val isOne = bit == 1
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isOne) AmberGold.copy(alpha = 0.25f)
                                else if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)
                            )
                            .border(
                                1.5.dp,
                                if (isOne) AmberGold else dynamicColors.cardBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                previousDigest = currentDigest.copyOf()
                                bits[idx] = if (bits[idx] == 0) 1 else 0
                                flipCount++
                                onActionSuccess()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$bit",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = if (isOne) AmberGold else dynamicColors.textPrimary
                        )
                    }
                }
            }
        }

        // Digest Comparison Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(if (isDark) Color(0xFF0F1A2E) else dynamicColors.cardBackground)
                .border(1.dp, AmberGold.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "SHA-256 DIGEST: H(M)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = SubtextGray
                )
                Text(
                    text = currentHashHex.chunked(32).joinToString("\n"),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = dynamicColors.textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HAMMING DISTANCE ΔH:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = AmberGold
                    )
                    Text(
                        text = if (previousDigest != null) "$hammingDistance / 256 bits (${String.format("%.1f", avalanchePercentage)}%)" else "Tap a bit to measure ΔH",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (avalanchePercentage in 40f..60f) ArenaSuccessGreen else AmberGold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Tactile3DButton(
            title = if (flipCount >= 2) "Confirm Avalanche Metric ➔" else "Flip at least 2 bits (${flipCount}/2)",
            style = if (flipCount >= 2) Tactile3DButtonColor.GREEN else Tactile3DButtonColor.AMBER,
            isEnabled = flipCount >= 2,
            onClick = onComplete,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
