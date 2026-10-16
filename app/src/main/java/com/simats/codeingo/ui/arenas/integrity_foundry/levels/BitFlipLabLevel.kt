package com.simats.codeingo.ui.arenas.integrity_foundry.levels

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
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

    // 8-bit interactive input register
    val bits = remember { mutableStateListOf(0, 1, 1, 0, 0, 0, 1, 1) }
    var flipCount by remember { mutableIntStateOf(0) }

    // Compute live SHA-256 hash of the 8 bits
    val currentHashHex = remember(bits.toList()) {
        val byteVal = bits.fold(0) { acc, bit -> (acc shl 1) or bit }.toByte()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(byteArrayOf(byteVal))
        digest.joinToString("") { "%02x".format(it) }
    }

    // Number of 1-bits in hash to simulate avalanche calculation
    val activeBitPercentage = remember(currentHashHex) {
        val ones = currentHashHex.map { it.digitToInt(16).countOneBits() }.sum()
        String.format("%.1f", (ones / 256f) * 100f)
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
                        text = "STRICT AVALANCHE CRITERION LAB",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = AmberGold
                    )
                }
                Text(
                    text = "Tap any bit in the 8-bit input register below to flip it (0 ↔ 1). Observe how a single flipped bit triggers a 50%+ pseudorandom explosion across the 256-bit SHA digest!",
                    fontSize = 12.5.sp,
                    color = dynamicColors.textPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp
                )
            }
        }

        // 8-Bit Interactive Register
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "INTERACTIVE 8-BIT INPUT REGISTER (TAP TO FLIP):",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = SubtextGray
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                bits.forEachIndexed { idx, bit ->
                    val isOne = bit == 1
                    Box(
                        modifier = Modifier
                            .size(38.dp)
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
                            .pressScale()
                            .clickable {
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

        // Avalanche Real-Time Metric
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (flipCount > 0) DuolingoGreen.copy(alpha = 0.15f)
                    else Color(0xFF3B82F6).copy(alpha = 0.15f)
                )
                .border(
                    1.dp,
                    if (flipCount > 0) DuolingoGreen.copy(alpha = 0.6f)
                    else Color(0xFF3B82F6).copy(alpha = 0.4f),
                    RoundedCornerShape(14.dp)
                )
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "💥 AVALANCHE: $activeBitPercentage% BITS ACTIVE",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = if (flipCount > 0) DuolingoGreen else Color(0xFF60A5FA)
                )
                Text(
                    text = "• FLIPS: $flipCount",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = SubtextGray
                )
            }
        }

        // 256-Bit SHA Hash Digest Matrix Preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(if (isDark) Color(0xFF090E17) else Color(0xFFF8FAFC))
                .border(1.2.dp, AmberGold.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "256-BIT SHA-256 HASH DIGEST (HEX STREAM):",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = SubtextGray
                )

                Text(
                    text = currentHashHex,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = if (flipCount > 0) AmberGold else dynamicColors.textPrimary,
                    lineHeight = 18.sp,
                    letterSpacing = 1.sp
                )
            }
        }

        // Complete Action Button
        val canComplete = flipCount >= 2
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (canComplete) Brush.horizontalGradient(listOf(DuolingoGreen, Color(0xFF059669)))
                    else Brush.horizontalGradient(listOf(Color(0xFF64748B), Color(0xFF475569)))
                )
                .pressScale(targetScale = 0.96f)
                .clickable(enabled = canComplete) {
                    onComplete()
                },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = if (canComplete) "AVALANCHE VERIFIED! ADVANCE FOUNDRY →" else "TEST ${2 - flipCount} MORE BIT FLIPS TO ADVANCE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
