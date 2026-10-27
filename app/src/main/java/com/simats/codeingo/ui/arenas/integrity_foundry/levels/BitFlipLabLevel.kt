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
import com.simats.codeingo.ui.theme.ArenaSuccessGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.LocalReduceMotion
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.theme.pressScale
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
    val reduceMotion = LocalReduceMotion.current
    val view = LocalView.current

    val primary = ArenaPalettes.Arena4Primary
    val accent = ArenaPalettes.Arena4Accent

    // 8-bit interactive register
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

    // Live Hamming distance between pre-flip and post-flip digest
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

    val isTargetReached = flipCount >= 3 && avalanchePercentage >= 35f

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Mission Briefing & Status Badge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(if (isDark) ArenaPalettes.Arena4CardDark else dynamicColors.cardBackground)
                .border(1.2.dp, primary.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = null,
                            tint = primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "STRICT AVALANCHE RADAR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = primary
                        )
                    }

                    ArenaStatusBadge(
                        type = if (isTargetReached) ArenaBadgeType.SUCCESS else ArenaBadgeType.INFO,
                        label = if (isTargetReached) "SAC SATISFIED ✓" else "FLIPS: $flipCount / 3"
                    )
                }

                Text(
                    text = "Strict Avalanche Criterion (SAC): In SHA-256, changing a single bit in the input causes approximately 50% (~128 of 256) of output bits to invert pseudo-randomly. Tap bits to observe live Hamming bit distance!",
                    fontSize = 12.sp,
                    color = dynamicColors.textPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }

        // 8-Bit Interactive Register (Enforced 48dp+ touch target per bit)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(0.95f)
        ) {
            Text(
                text = "INTERACTIVE 8-BIT INPUT REGISTER (TAP ANY BIT):",
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
                            .minTouchTarget48()
                            .height(52.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isOne) primary.copy(alpha = 0.3f)
                                else if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
                            )
                            .border(
                                1.5.dp,
                                if (isOne) primary else Color.Transparent,
                                RoundedCornerShape(10.dp)
                            )
                            .pressScale()
                            .clickable {
                                previousDigest = currentDigest.copyOf()
                                bits[idx] = if (isOne) 0 else 1
                                flipCount++
                                ArenaHaptics.tap(view, reduceMotion)
                                onActionSuccess()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$bit",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = if (isOne) primary else dynamicColors.textPrimary
                        )
                    }
                }
            }
        }

        // Live Hamming Distance & Avalanche Metrics
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(16.dp))
                .background(if (isDark) ArenaPalettes.Arena4BackgroundDark else Color(0xFFF8FAFC))
                .border(1.5.dp, if (isTargetReached) ArenaSuccessGreen else primary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HAMMING BIT DISTANCE (ΔH):",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = SubtextGray
                    )
                    Text(
                        text = "$hammingDistance / 256 bits inverted",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (isTargetReached) ArenaSuccessGreen else primary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AVALANCHE PERCENTAGE:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = SubtextGray
                    )
                    Text(
                        text = "${"%.1f".format(avalanchePercentage)}% (Expected ~50%)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (isTargetReached) ArenaSuccessGreen else accent
                    )
                }
            }
        }

        // Output Hash Hex Snippet
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                .padding(10.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(text = "CURRENT SHA-256 DIGEST:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SubtextGray)
                Text(
                    text = currentHashHex.chunked(32).joinToString("\n"),
                    fontSize = 10.5.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = dynamicColors.textPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Tactile3DButton(
            title = if (isTargetReached) "AVALANCHE EFFECT CONFIRMED! ADVANCE ➔" else "FLIP AT LEAST 3 BITS (${flipCount}/3)",
            style = if (isTargetReached) Tactile3DButtonColor.GREEN else Tactile3DButtonColor.DISABLED,
            isEnabled = isTargetReached,
            onClick = {
                if (isTargetReached) {
                    ArenaHaptics.success(view, reduceMotion)
                    onComplete()
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .minTouchTarget48()
        )

        Spacer(modifier = Modifier.height(12.dp))
    }
}
