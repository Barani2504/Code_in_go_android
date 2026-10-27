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
import androidx.compose.material.icons.filled.VpnKey
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
fun SignatureSealLevel(
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

    // ToyRSA Parameters: n=55, e=3, d=27
    // Digest h = 8
    // Signature s = 8^27 mod 55 = 2
    // Verification: 2^3 mod 55 = 8 == h
    val digestH = 8L
    val privateD = 27L
    val publicE = 3L
    val modulusN = 55L

    val signatureS = remember { ArenaCryptoEngine.modPow(digestH, privateD, modulusN) } // 2L
    val verifiedDigest = remember { ArenaCryptoEngine.modPow(signatureS, publicE, modulusN) } // 8L

    // Step 0: Signing Key Choice
    // Step 1: Signature Calculation Choice (s=2)
    // Step 2: Verification Key & Equation Choice
    // Step 3: Complete
    var step by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var feedbackText by remember { mutableStateOf<String?>(null) }

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
                        Icon(imageVector = Icons.Default.VpnKey, contentDescription = null, tint = primary, modifier = Modifier.size(16.dp))
                        Text(
                            text = "ASYMMETRIC SIGNATURE SEAL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = primary
                        )
                    }

                    ArenaStatusBadge(
                        type = if (step >= 3) ArenaBadgeType.SUCCESS else ArenaBadgeType.ENCRYPTED,
                        label = if (step >= 3) "SIGNATURE VALIDATED ✓" else "PHASE ${step + 1} / 3"
                    )
                }

                Text(
                    text = "Hash-Then-Sign Paradigm: Sign message digest h with private key d (s = hᵈ mod n); anyone verifies using public key e (sᵉ mod n == h). Guarantees authenticity & non-repudiation.",
                    fontSize = 12.sp,
                    color = dynamicColors.textPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }

        // Live Signature Pipeline Cards
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth(0.95f)) {
            // Card 1: Digest
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (step >= 1) primary.copy(alpha = 0.15f) else dynamicColors.cardBorder.copy(alpha = 0.15f))
                    .border(1.dp, if (step >= 1) primary else dynamicColors.cardBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(text = "1. MESSAGE HASH DIGEST (h):", fontSize = 10.sp, fontWeight = FontWeight.Black, color = SubtextGray)
                        Text(text = "h = H(\"Transfer 1000 Coins\") mod 55 = $digestH", fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary)
                    }
                    Text(text = if (step >= 1) "✓ HASHED" else "PENDING", fontSize = 10.sp, fontWeight = FontWeight.Black, color = if (step >= 1) primary else SubtextGray)
                }
            }

            // Card 2: Signature
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (step >= 2) accent.copy(alpha = 0.15f) else dynamicColors.cardBorder.copy(alpha = 0.15f))
                    .border(1.dp, if (step >= 2) accent else dynamicColors.cardBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(text = "2. SIGN WITH PRIVATE KEY (d = 27):", fontSize = 10.sp, fontWeight = FontWeight.Black, color = accent)
                        Text(
                            text = if (step >= 2) "s = hᵈ mod 55 = 8²⁷ mod 55 = $signatureS" else "s = hᵈ mod n (Private)",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = dynamicColors.textPrimary
                        )
                    }
                    Text(text = if (step >= 2) "✓ SIGNED" else "PENDING", fontSize = 10.sp, fontWeight = FontWeight.Black, color = if (step >= 2) accent else SubtextGray)
                }
            }

            // Card 3: Verification
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (step >= 3) ArenaSuccessGreen.copy(alpha = 0.15f) else dynamicColors.cardBorder.copy(alpha = 0.15f))
                    .border(1.dp, if (step >= 3) ArenaSuccessGreen else dynamicColors.cardBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(text = "3. VERIFY WITH PUBLIC KEY (e = 3):", fontSize = 10.sp, fontWeight = FontWeight.Black, color = ArenaSuccessGreen)
                        Text(
                            text = if (step >= 3) "sᵉ mod 55 = 2³ mod 55 = $verifiedDigest == h ✓" else "Check sᵉ mod n == h (Public)",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = dynamicColors.textPrimary
                        )
                    }
                    Text(text = if (step >= 3) "✓ VALID" else "PENDING", fontSize = 10.sp, fontWeight = FontWeight.Black, color = if (step >= 3) ArenaSuccessGreen else SubtextGray)
                }
            }
        }

        // Interactive Stage Decisions
        when (step) {
            0 -> {
                // Key Choice Question
                Column(modifier = Modifier.fillMaxWidth(0.95f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "WHICH KEY MUST ALICE USE TO SIGN THE DIGEST h=8?",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    listOf(
                        "Alice's Secret Private Key (d = 27)" to true,
                        "Alice's Public Key (e = 3)" to false,
                        "Bob's Public Key" to false
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
                                        step = 1
                                        selectedOption = null
                                        feedbackText = "✓ Alice uses private key d=27! Only she can create this signature."
                                        onActionSuccess()
                                    } else {
                                        ArenaHaptics.error(view, reduceMotion)
                                        onDeductHeart()
                                        feedbackText = "❌ A digital signature MUST be signed with the author's private key."
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
                // Signature Computation Question
                Column(modifier = Modifier.fillMaxWidth(0.95f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "SOLVE SIGNATURE s ≡ 8²⁷ mod 55:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(2 to true, 8 to false, 24 to false, 40 to false).forEach { (candidate, isCorrect) ->
                            val isSelected = selectedOption == "$candidate"
                            Box(
                                modifier = Modifier
                                    .weight(1f)
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
                                        selectedOption = "$candidate"
                                        if (isCorrect) {
                                            ArenaHaptics.success(view, reduceMotion)
                                            step = 2
                                            selectedOption = null
                                            feedbackText = "✓ Signature s = 2 generated! Now verify with public key."
                                            onActionSuccess()
                                        } else {
                                            ArenaHaptics.error(view, reduceMotion)
                                            onDeductHeart()
                                            feedbackText = "❌ By Euler's totient theorem, 8²⁷ mod 55 = 2."
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "s = $candidate",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (isSelected && isCorrect) Color.Black else dynamicColors.textPrimary
                                )
                            }
                        }
                    }
                }
            }
            2 -> {
                // Verification Question
                Column(modifier = Modifier.fillMaxWidth(0.95f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "HOW DOES BOB VERIFY SIGNATURE s = 2?",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    listOf(
                        "Compute sᵉ mod 55 = 2³ mod 55 = 8 (matches h!)" to true,
                        "Decipher s using Alice's private key" to false,
                        "Hash the signature with MD5" to false
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
                                        step = 3
                                        selectedOption = null
                                        feedbackText = "🎉 Correct! sᵉ mod 55 = 2³ = 8 == h! Non-repudiation proved."
                                        onActionSuccess()
                                    } else {
                                        ArenaHaptics.error(view, reduceMotion)
                                        onDeductHeart()
                                        feedbackText = "❌ Bob uses public key e=3 to verify: sᵉ mod n must equal h."
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
            else -> {
                // Advance
                Tactile3DButton(
                    title = "DIGITAL SIGNATURE VALIDATED! ADVANCE FOUNDRY ➔",
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
