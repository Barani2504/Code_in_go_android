package com.simats.codeingo.ui.arenas.integrity_foundry.levels

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.simats.codeingo.ui.arenas.crypto.ArenaCryptoEngine
import com.simats.codeingo.ui.components.Tactile3DButton
import com.simats.codeingo.ui.components.Tactile3DButtonColor
import com.simats.codeingo.ui.theme.ArenaDangerRed
import com.simats.codeingo.ui.theme.ArenaSuccessGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.LocalReduceMotion
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.theme.pressScale

@Composable
fun WaxSealLevel(
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

    // RFC 2104 HMAC Verification:
    val testKey = "CitadelSecretKey".toByteArray()
    val testMsg = "Transfer=1000Coins".toByteArray()
    val hmacSteps = remember { ArenaCryptoEngine.computeHmacSteps(testKey, testMsg) }

    // 4-stage RFC 2104 interactive pipeline:
    // Stage 0: ipad (0x36) constant
    // Stage 1: Inner Hash H_in
    // Stage 2: opad (0x5C) constant
    // Stage 3: Outer Tag & length extension immunity
    // Stage 4: Complete
    var activeStage by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var feedbackText by remember { mutableStateOf<String?>(null) }

    val progressAnim by animateFloatAsState(targetValue = activeStage / 4.0f, label = "hmacProgress")

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
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
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = primary, modifier = Modifier.size(16.dp))
                        Text(
                            text = "HMAC WAX SEAL WORKSHOP",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = primary
                        )
                    }

                    ArenaStatusBadge(
                        type = if (activeStage >= 4) ArenaBadgeType.SUCCESS else ArenaBadgeType.ENCRYPTED,
                        label = if (activeStage >= 4) "RFC 2104 VERIFIED ✓" else "STAGE ${activeStage + 1} / 4"
                    )
                }

                Text(
                    text = "RFC 2104: H((K ⊕ opad) ∥ H((K ⊕ ipad) ∥ M))\nA naïve hash H(K ∥ M) is vulnerable to length extension. HMAC uses nested inner and outer pads to guarantee authenticity.",
                    fontSize = 12.sp,
                    color = dynamicColors.textPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }

        // Pipeline Progress Bar
        Column(modifier = Modifier.fillMaxWidth(0.95f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "RFC 2104 PIPELINE PROGRESS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SubtextGray)
                Text(text = "$activeStage / 4 STAGES", fontSize = 10.sp, fontWeight = FontWeight.Black, color = primary)
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { progressAnim },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (activeStage >= 4) ArenaSuccessGreen else primary,
                trackColor = dynamicColors.cardBorder.copy(alpha = 0.3f)
            )
        }

        // 4 Sequential Stages Display
        Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth(0.95f)) {
            val stagesInfo = listOf(
                Pair("Stage 1: K ⊕ ipad (0x36)", "K_ipad = " + hmacSteps.kIpadHex.take(16) + "..."),
                Pair("Stage 2: Inner Digest H_in", "H_in = " + hmacSteps.innerHashHex.take(20) + "..."),
                Pair("Stage 3: K ⊕ opad (0x5C)", "K_opad = " + hmacSteps.kOpadHex.take(16) + "..."),
                Pair("Stage 4: Outer HMAC Wax Seal", "Tag = " + hmacSteps.outerTagHex.take(24) + "...")
            )

            stagesInfo.forEachIndexed { idx, (title, hexSnippet) ->
                val isCompleted = activeStage > idx
                val isCurrent = activeStage == idx

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isCompleted) ArenaSuccessGreen.copy(alpha = 0.15f)
                            else if (isCurrent) primary.copy(alpha = 0.15f)
                            else dynamicColors.cardBorder.copy(alpha = 0.15f)
                        )
                        .border(
                            1.dp,
                            if (isCompleted) ArenaSuccessGreen
                            else if (isCurrent) primary
                            else dynamicColors.cardBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = title, fontSize = 10.sp, fontWeight = FontWeight.Black, color = if (isCompleted) ArenaSuccessGreen else primary)
                            Text(text = hexSnippet, fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = dynamicColors.textPrimary)
                        }
                        Text(
                            text = if (isCompleted) "✓ COMPLETE" else if (isCurrent) "ACTIVE" else "LOCKED",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isCompleted) ArenaSuccessGreen else if (isCurrent) primary else SubtextGray
                        )
                    }
                }
            }
        }

        // Interactive Stage Decisions
        when (activeStage) {
            0 -> {
                // ipad question
                Column(modifier = Modifier.fillMaxWidth(0.95f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "WHICH BYTE VALUE IS REPEATED 64 TIMES TO FORM ipad?",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    listOf(
                        "0x36 (00110110 in binary)" to true,
                        "0x5C (01011100 in binary)" to false,
                        "0x00 (all zeros)" to false
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
                                        activeStage = 1
                                        selectedOption = null
                                        feedbackText = "✓ ipad = 0x36 repeated! K ⊕ ipad computed."
                                        onActionSuccess()
                                    } else {
                                        ArenaHaptics.error(view, reduceMotion)
                                        onDeductHeart()
                                        feedbackText = "❌ In RFC 2104, ipad is 0x36 and opad is 0x5C."
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
            1 -> {
                // Inner hash question
                Column(modifier = Modifier.fillMaxWidth(0.95f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "HOW IS THE INNER HASH H_in CALCULATED?",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    listOf(
                        "H_in = H((K ⊕ ipad) ∥ M)" to true,
                        "H_in = H(K) ∥ H(M)" to false,
                        "H_in = K ⊕ M" to false
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
                                        activeStage = 2
                                        selectedOption = null
                                        feedbackText = "✓ Inner hash H_in generated over (K ⊕ ipad) ∥ Message."
                                        onActionSuccess()
                                    } else {
                                        ArenaHaptics.error(view, reduceMotion)
                                        onDeductHeart()
                                        feedbackText = "❌ Inner pass concatenates padded key with the raw message."
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = opt,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = if (isSelected && isCorrect) Color.Black else dynamicColors.textPrimary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            2 -> {
                // opad question
                Column(modifier = Modifier.fillMaxWidth(0.95f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "WHICH BYTE VALUE IS REPEATED 64 TIMES TO FORM opad?",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    listOf(
                        "0x5C (01011100 in binary)" to true,
                        "0x36 (ipad)" to false,
                        "0xAA" to false
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
                                        activeStage = 3
                                        selectedOption = null
                                        feedbackText = "✓ opad = 0x5C repeated! Ready for outer wax seal."
                                        onActionSuccess()
                                    } else {
                                        ArenaHaptics.error(view, reduceMotion)
                                        onDeductHeart()
                                        feedbackText = "❌ Outer pad constant is 0x5C."
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
                // Outer Tag & Length Extension Immunity Question
                Column(modifier = Modifier.fillMaxWidth(0.95f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "WHY DOES HMAC PREVENT LENGTH EXTENSION ATTACKS?",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    listOf(
                        "Outer hash envelopes H_in with (K ⊕ opad), hiding internal digest state" to true,
                        "HMAC encrypts message bytes using AES" to false,
                        "SHA-256 blocks length extension automatically" to false
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
                                        ArenaHaptics.criticalStrike(view, reduceMotion)
                                        activeStage = 4
                                        selectedOption = null
                                        feedbackText = "🎉 Correct! Two-pass nesting shields internal state from continuation."
                                        onActionSuccess()
                                    } else {
                                        ArenaHaptics.error(view, reduceMotion)
                                        onDeductHeart()
                                        feedbackText = "❌ Merkle-Damgård exposes state; HMAC's outer hash seals it."
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
            else -> {
                // Advance
                Tactile3DButton(
                    title = "HMAC WAX SEAL CERTIFIED! ADVANCE FOUNDRY ➔",
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
