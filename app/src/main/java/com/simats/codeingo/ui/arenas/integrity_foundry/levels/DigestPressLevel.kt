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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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

@Composable
fun DigestPressLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    // Interactive Stages:
    // 0: Raw Message M = "CNS" (24 bits)
    // 1: Add 0x80 delimiter (1 bit + 7 zeros)
    // 2: Zero Padding to 448 bits
    // 3: Append 64-bit length L=24 (Standard 512-bit block achieved!)
    // 4: Hydraulic Compression into 256-bit Digest
    var stage by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
                        imageVector = Icons.Default.Compress,
                        contentDescription = null,
                        tint = AmberGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "MERKLE-DAMGÅRD 512-BIT HYDRAULIC PRESS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = AmberGold
                    )
                }
                Text(
                    text = when (stage) {
                        0 -> "Step 1: Input message M = 'CNS' (3 bytes = 24 bits). Standard SHA-256 requires 512-bit (64-byte) blocks."
                        1 -> "Step 2: Append single '1' bit followed by seven '0' bits (0x80 byte) to demarcate message end."
                        2 -> "Step 3: Pad with zeros until message length reaches exactly 448 bits (512 - 64 bits)."
                        3 -> "Step 4: Append 64-bit big-endian length integer (L = 24). Standard 512-bit block is forged!"
                        else -> "🎉 Compression Success! The 512-bit chunk was crushed through 64 Davies-Meyer rounds into a 256-bit hash digest!"
                    },
                    fontSize = 12.5.sp,
                    color = dynamicColors.textPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp
                )
            }
        }

        // 512-Bit Chamber Visualizer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(if (isDark) Color(0xFF090E17) else Color(0xFFF8FAFC))
                .border(1.5.dp, if (stage >= 3) DuolingoGreen else AmberGold.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SHA-256 CHUNK CHAMBER",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = SubtextGray
                    )
                    Text(
                        text = if (stage >= 3) "512 BITS (64 BYTES) ✓" else "CURRENT: ${when (stage) { 0 -> "24 bits"; 1 -> "32 bits"; else -> "448 bits" }}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (stage >= 3) DuolingoGreen else AmberGold
                    )
                }

                // Chunk Anatomy Ribbon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Message block
                    Box(
                        modifier = Modifier
                            .weight(1.5f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AmberGold.copy(alpha = 0.25f))
                            .border(1.dp, AmberGold, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "M: 'CNS' (24b)", fontSize = 10.sp, fontWeight = FontWeight.Black, color = AmberGold)
                    }

                    // Delimiter block
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (stage >= 1) Color(0xFF3B82F6).copy(alpha = 0.25f) else Color.Transparent)
                            .border(1.dp, if (stage >= 1) Color(0xFF3B82F6) else SubtextGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = if (stage >= 1) "10000000" else "?", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (stage >= 1) Color(0xFF60A5FA) else SubtextGray)
                    }

                    // Zero pad block
                    Box(
                        modifier = Modifier
                            .weight(2f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (stage >= 2) Color(0xFF8B5CF6).copy(alpha = 0.20f) else Color.Transparent)
                            .border(1.dp, if (stage >= 2) Color(0xFF8B5CF6) else SubtextGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = if (stage >= 2) "000...000 (416b)" else "Pad ➔ 448b", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (stage >= 2) Color(0xFFA78BFA) else SubtextGray)
                    }

                    // Length block
                    Box(
                        modifier = Modifier
                            .weight(1.5f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (stage >= 3) DuolingoGreen.copy(alpha = 0.25f) else Color.Transparent)
                            .border(1.dp, if (stage >= 3) DuolingoGreen else SubtextGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = if (stage >= 3) "L=24 (64b)" else "Len", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (stage >= 3) DuolingoGreen else SubtextGray)
                    }
                }
            }
        }

        // Output Digest Box (When Compressed)
        if (stage >= 4) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DuolingoGreen.copy(alpha = 0.12f))
                    .border(1.2.dp, DuolingoGreen, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "CRUSHED 256-BIT HASH DIGEST:", fontSize = 10.sp, fontWeight = FontWeight.Black, color = DuolingoGreen)
                    Text(
                        text = "e2d6b384c7a523b1239f884109fa771a25be11b98024cf4b54e70b9ac782a691",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = dynamicColors.textPrimary
                    )
                }
            }
        }

        // Interactive Hydraulic Action Button
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (stage >= 4) Brush.horizontalGradient(listOf(DuolingoGreen, Color(0xFF059669)))
                    else Brush.horizontalGradient(listOf(AmberGold, Color(0xFFD97706)))
                )
                .pressScale(targetScale = 0.96f)
                .clickable {
                    if (stage < 4) {
                        stage++
                        onActionSuccess()
                    } else {
                        onComplete()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (stage >= 4) Icons.Default.Check else Icons.Default.Compress,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = when (stage) {
                        0 -> "1. APPEND '1' DELIMITER (0x80) →"
                        1 -> "2. ZERO-PAD TO 448 BITS →"
                        2 -> "3. APPEND 64-BIT LENGTH (512b) →"
                        3 -> "4. COMPRESS 512-BIT CHUNK 💥"
                        else -> "256-BIT DIGEST PRODUCED! ADVANCE →"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Black,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
