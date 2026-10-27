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
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material3.Icon
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
    val reduceMotion = LocalReduceMotion.current
    val view = LocalView.current

    val primary = ArenaPalettes.Arena4Primary
    val accent = ArenaPalettes.Arena4Accent

    // Stages:
    // 0: Select Delimiter (0x80)
    // 1: Select Zero-Padding target length (448 bits)
    // 2: Select 64-bit Length encoding (24 bits)
    // 3: 512-bit block forged -> Compress into 256-bit digest
    // 4: Complete
    var stage by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var feedbackText by remember { mutableStateOf<String?>(null) }

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
                            imageVector = Icons.Default.Compress,
                            contentDescription = null,
                            tint = primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "MERKLE-DAMGÅRD 512-BIT PRESS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = primary
                        )
                    }

                    ArenaStatusBadge(
                        type = when (stage) {
                            4 -> ArenaBadgeType.SUCCESS
                            3 -> ArenaBadgeType.WARNING
                            else -> ArenaBadgeType.INFO
                        },
                        label = when (stage) {
                            0 -> "STAGE 1: DELIMITER"
                            1 -> "STAGE 2: PADDING"
                            2 -> "STAGE 3: LENGTH"
                            3 -> "STAGE 4: COMPRESS"
                            else -> "256-BIT DIGEST"
                        }
                    )
                }

                Text(
                    text = when (stage) {
                        0 -> "Input message M = 'CNS' (3 bytes = 24 bits). Standard SHA-256 operates on 512-bit (64-byte) blocks. How must the message boundary be marked?"
                        1 -> "With delimiter appended (32 bits total), how far must we pad with zeros before appending the 64-bit length?"
                        2 -> "Final block formation: In what format is the original 24-bit message length encoded into the 64-bit trailer?"
                        3 -> "The 512-bit chunk is fully assembled! Trigger the Davies-Meyer hydraulic press to compress it into a 256-bit digest."
                        else -> "🎉 256-bit digest forged! The 512-bit block was crushed through 64 compression rounds into a collision-resistant fingerprint."
                    },
                    fontSize = 12.sp,
                    color = dynamicColors.textPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }

        // 512-Bit Chunk Anatomy Visualizer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(if (isDark) ArenaPalettes.Arena4BackgroundDark else Color(0xFFF8FAFC))
                .border(1.5.dp, if (stage >= 3) ArenaSuccessGreen else primary.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (stage >= 3) ArenaSuccessGreen else primary
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
                            .height(46.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(primary.copy(alpha = 0.25f))
                            .border(1.dp, primary, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "M: 'CNS' (24b)", fontSize = 10.sp, fontWeight = FontWeight.Black, color = primary)
                    }

                    // Delimiter block
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (stage >= 1) accent.copy(alpha = 0.25f) else Color.Transparent)
                            .border(1.dp, if (stage >= 1) accent else SubtextGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = if (stage >= 1) "0x80 (1b)" else "?", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (stage >= 1) accent else SubtextGray)
                    }

                    // Zero pad block
                    Box(
                        modifier = Modifier
                            .weight(2f)
                            .height(46.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (stage >= 2) Color(0xFF3B82F6).copy(alpha = 0.20f) else Color.Transparent)
                            .border(1.dp, if (stage >= 2) Color(0xFF3B82F6) else SubtextGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = if (stage >= 2) "000... (416b)" else "Pad ➔ 448b", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (stage >= 2) Color(0xFF60A5FA) else SubtextGray)
                    }

                    // Length block
                    Box(
                        modifier = Modifier
                            .weight(1.5f)
                            .height(46.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (stage >= 3) ArenaSuccessGreen.copy(alpha = 0.25f) else Color.Transparent)
                            .border(1.dp, if (stage >= 3) ArenaSuccessGreen else SubtextGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = if (stage >= 3) "L=24 (64b)" else "Len", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (stage >= 3) ArenaSuccessGreen else SubtextGray)
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
                    .background(ArenaSuccessGreen.copy(alpha = 0.12f))
                    .border(1.2.dp, ArenaSuccessGreen, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "CRUSHED 256-BIT HASH DIGEST:", fontSize = 10.sp, fontWeight = FontWeight.Black, color = ArenaSuccessGreen)
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

        // Interactive Stage Decision
        when (stage) {
            0 -> {
                // Delimiter Question
                Column(modifier = Modifier.fillMaxWidth(0.95f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "HOW IS THE SHA-256 MESSAGE DELIMITER FORMED?",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    listOf(
                        "Append a single '1' bit followed by zeros (0x80 byte)" to true,
                        "Append a null terminator (0x00 byte)" to false,
                        "Append standard newline character (0x0A)" to false
                    ).forEach { (opt, isCorrect) ->
                        val isSelected = selectedOption == opt
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .minTouchTarget48()
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) (if (isCorrect) ArenaSuccessGreen else ArenaDangerRed)
                                    else if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
                                )
                                .border(1.dp, if (isSelected) Color.White else Color.Transparent, RoundedCornerShape(12.dp))
                                .pressScale()
                                .clickable {
                                    selectedOption = opt
                                    if (isCorrect) {
                                        ArenaHaptics.success(view, reduceMotion)
                                        stage = 1
                                        selectedOption = null
                                        feedbackText = "✓ 0x80 appended! Now pad with zeros."
                                        onActionSuccess()
                                    } else {
                                        ArenaHaptics.error(view, reduceMotion)
                                        onDeductHeart()
                                        feedbackText = "❌ A single '1' bit is required to unambiguously demarcate message end."
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = opt,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected && isCorrect) Color.Black else dynamicColors.textPrimary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )
                        }
                    }
                }
            }
            1 -> {
                // Padding Question
                Column(modifier = Modifier.fillMaxWidth(0.95f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "TO WHAT LENGTH MUST ZEROS BE PADDED BEFORE THE TRAILER?",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    listOf(
                        "448 bits (512 - 64 bits for trailer)" to true,
                        "512 bits directly" to false,
                        "256 bits" to false
                    ).forEach { (opt, isCorrect) ->
                        val isSelected = selectedOption == opt
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .minTouchTarget48()
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) (if (isCorrect) ArenaSuccessGreen else ArenaDangerRed)
                                    else if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
                                )
                                .border(1.dp, if (isSelected) Color.White else Color.Transparent, RoundedCornerShape(12.dp))
                                .pressScale()
                                .clickable {
                                    selectedOption = opt
                                    if (isCorrect) {
                                        ArenaHaptics.success(view, reduceMotion)
                                        stage = 2
                                        selectedOption = null
                                        feedbackText = "✓ Padded to 448 bits! Leaves room for 64-bit length."
                                        onActionSuccess()
                                    } else {
                                        ArenaHaptics.error(view, reduceMotion)
                                        onDeductHeart()
                                        feedbackText = "❌ Must pad to 448 bits so 64-bit length completes the 512-bit block."
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = opt,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected && isCorrect) Color.Black else dynamicColors.textPrimary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            2 -> {
                // Length trailer question
                Column(modifier = Modifier.fillMaxWidth(0.95f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "WHAT VALUE IS STORED IN THE 64-BIT LENGTH TRAILER?",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    listOf(
                        "Bit length: L = 24 bits (0x0000000000000018)" to true,
                        "Byte length: L = 3 bytes (0x0000000000000003)" to false,
                        "Block size: L = 512 bits" to false
                    ).forEach { (opt, isCorrect) ->
                        val isSelected = selectedOption == opt
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .minTouchTarget48()
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) (if (isCorrect) ArenaSuccessGreen else ArenaDangerRed)
                                    else if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
                                )
                                .border(1.dp, if (isSelected) Color.White else Color.Transparent, RoundedCornerShape(12.dp))
                                .pressScale()
                                .clickable {
                                    selectedOption = opt
                                    if (isCorrect) {
                                        ArenaHaptics.success(view, reduceMotion)
                                        stage = 3
                                        selectedOption = null
                                        feedbackText = "✓ Exactly 512 bits assembled! Ready for hydraulic press."
                                        onActionSuccess()
                                    } else {
                                        ArenaHaptics.error(view, reduceMotion)
                                        onDeductHeart()
                                        feedbackText = "❌ SHA-256 stores bit length (3 bytes × 8 = 24 bits)."
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = opt,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected && isCorrect) Color.Black else dynamicColors.textPrimary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            3 -> {
                // Compress button
                Tactile3DButton(
                    title = "COMPRESS 512-BIT CHUNK INTO 256-BIT DIGEST 💥",
                    style = Tactile3DButtonColor.AMBER,
                    onClick = {
                        ArenaHaptics.criticalStrike(view, reduceMotion)
                        stage = 4
                        feedbackText = "🎉 SHA-256 compression complete! 256-bit digest generated."
                        onActionSuccess()
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .minTouchTarget48()
                )
            }
            else -> {
                // Advance
                Tactile3DButton(
                    title = "256-BIT DIGEST PRODUCED! ADVANCE FOUNDRY ➔",
                    style = Tactile3DButtonColor.GREEN,
                    onClick = {
                        ArenaHaptics.success(view, reduceMotion)
                        onComplete()
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .minTouchTarget48()
                )
            }
        }

        if (feedbackText != null) {
            Text(
                text = feedbackText!!,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (feedbackText!!.startsWith("✓") || feedbackText!!.startsWith("🎉")) ArenaSuccessGreen else ArenaDangerRed,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}
