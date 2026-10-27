package com.simats.codeingo.ui.arenas.key_exchange_citadel.levels

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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
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
import com.simats.codeingo.ui.components.Tactile3DButton
import com.simats.codeingo.ui.components.Tactile3DButtonColor
import com.simats.codeingo.ui.theme.ArenaDangerRed
import com.simats.codeingo.ui.theme.ArenaSuccessGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.LocalReduceMotion
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.theme.pressScale

@Composable
fun KeyPairForgeLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    val reduceMotion = LocalReduceMotion.current
    val view = LocalView.current

    val primary = ArenaPalettes.Arena3Primary
    val accent = ArenaPalettes.Arena3Accent

    // Step 0: Prime selection (p=5, q=11)
    // Step 1: Compute modulus n = 55 (phi = 40)
    // Step 2: Choose public exponent e (gcd(e, 40) = 1 -> e = 3)
    // Step 3: Derive private trapdoor exponent d (3*d = 1 mod 40 -> d = 27)
    // Step 4: Final key vault verification
    var step by remember { mutableIntStateOf(0) }
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
                .background(if (isDark) ArenaPalettes.Arena3CardDark else dynamicColors.cardBackground)
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
                            imageVector = Icons.Default.VpnKey,
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "RSA KEY PAIR FORGE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = primary
                        )
                    }

                    ArenaStatusBadge(
                        type = when (step) {
                            4 -> ArenaBadgeType.SUCCESS
                            2, 3 -> ArenaBadgeType.WARNING
                            else -> ArenaBadgeType.ENCRYPTED
                        },
                        label = when (step) {
                            0 -> "STEP 1: PRIMES"
                            1 -> "STEP 2: MODULUS"
                            2 -> "STEP 3: PUBLIC e"
                            3 -> "STEP 4: PRIVATE d"
                            else -> "KEYS FORGED"
                        }
                    )
                }

                Text(
                    text = when (step) {
                        0 -> "Step 1: RSA requires two large distinct primes. Pick the valid prime pair to construct the forge foundation."
                        1 -> "Step 2: Calculate public modulus n = p × q = 5 × 11. (Totient φ(n) = (5-1)(11-1) = 40 is kept secret)."
                        2 -> "Step 3: Choose public encryption exponent e coprime to φ(n)=40 (i.e., gcd(e, 40) = 1)."
                        3 -> "Step 4: Solve private exponent d ≡ e⁻¹ mod φ(n). Find d such that (3 × d) mod 40 ≡ 1."
                        else -> "🎉 Key Pair Forged! Public Key (e=3, n=55) is safely shared; Private Key (d=27, n=55) is vaulted!"
                    },
                    fontSize = 12.sp,
                    color = dynamicColors.textPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }

        // Live Parameter Monitor
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Modulus n
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                    .border(1.dp, if (step >= 1) accent else Color.Transparent, RoundedCornerShape(14.dp))
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "MODULUS n", fontSize = 9.sp, fontWeight = FontWeight.Black, color = SubtextGray)
                    Text(
                        text = if (step >= 1) "55 (5×11)" else "p × q",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (step >= 1) accent else dynamicColors.textPrimary
                    )
                }
            }

            // Totient phi(n)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                    .border(1.dp, if (step >= 1) primary else Color.Transparent, RoundedCornerShape(14.dp))
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "TOTIENT φ(n)", fontSize = 9.sp, fontWeight = FontWeight.Black, color = SubtextGray)
                    Text(
                        text = if (step >= 1) "40 (4×10)" else "(p-1)(q-1)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (step >= 1) primary else dynamicColors.textPrimary
                    )
                }
            }
        }

        // Exponents e & d
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Exponent e
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                    .border(1.dp, if (step >= 2) accent else Color.Transparent, RoundedCornerShape(14.dp))
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "PUBLIC e", fontSize = 9.sp, fontWeight = FontWeight.Black, color = SubtextGray)
                    Text(
                        text = if (step >= 2) "e = 3" else "gcd(e, 40)=1",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (step >= 2) accent else dynamicColors.textPrimary
                    )
                }
            }

            // Exponent d
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                    .border(1.dp, if (step >= 3) ArenaSuccessGreen else Color.Transparent, RoundedCornerShape(14.dp))
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "PRIVATE d", fontSize = 9.sp, fontWeight = FontWeight.Black, color = SubtextGray)
                    Text(
                        text = if (step >= 3) "d = 27" else "e⁻¹ mod 40",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (step >= 3) ArenaSuccessGreen else dynamicColors.textPrimary
                    )
                }
            }
        }

        // Vault Display (Step 4)
        if (step >= 4) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Public Vault
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(primary.copy(alpha = 0.15f))
                        .border(1.dp, primary, RoundedCornerShape(14.dp))
                        .padding(10.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(imageVector = Icons.Default.Public, contentDescription = null, tint = primary, modifier = Modifier.size(14.dp))
                            Text(text = "PUBLIC KEY", fontSize = 10.sp, fontWeight = FontWeight.Black, color = primary)
                        }
                        Text(text = "{e: 3, n: 55}", fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Black, color = dynamicColors.textPrimary)
                    }
                }

                // Private Vault
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(ArenaSuccessGreen.copy(alpha = 0.15f))
                        .border(1.dp, ArenaSuccessGreen, RoundedCornerShape(14.dp))
                        .padding(10.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = ArenaSuccessGreen, modifier = Modifier.size(14.dp))
                            Text(text = "PRIVATE VAULT", fontSize = 10.sp, fontWeight = FontWeight.Black, color = ArenaSuccessGreen)
                        }
                        Text(text = "{d: 27, n: 55}", fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Black, color = dynamicColors.textPrimary)
                    }
                }
            }
        }

        // Interactive Options for Current Step
        when (step) {
            0 -> {
                // Select prime pair
                Column(modifier = Modifier.fillMaxWidth(0.95f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "CHOOSE VALID RSA PRIME PAIR:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = accent,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    listOf(
                        "p = 5, q = 11" to true,
                        "p = 4, q = 9" to false,
                        "p = 6, q = 10" to false
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
                                        feedbackText = "✓ 5 and 11 are valid primes! Next: calculate modulus n."
                                        onActionSuccess()
                                    } else {
                                        ArenaHaptics.error(view, reduceMotion)
                                        onDeductHeart()
                                        feedbackText = "❌ Both p and q must be prime numbers!"
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = opt,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = if (isSelected && isCorrect) Color.Black else dynamicColors.textPrimary
                            )
                        }
                    }
                }
            }
            1 -> {
                // Compute modulus n = 5 * 11
                Column(modifier = Modifier.fillMaxWidth(0.95f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "COMPUTE PUBLIC MODULUS n = 5 × 11:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = accent,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(44, 55, 60, 65).forEach { candidate ->
                            val isSelected = selectedOption == "$candidate"
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .minTouchTarget48()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) (if (candidate == 55) ArenaSuccessGreen else ArenaDangerRed)
                                        else if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
                                    )
                                    .border(1.dp, if (isSelected) Color.White else Color.Transparent, RoundedCornerShape(12.dp))
                                    .pressScale()
                                    .clickable {
                                        selectedOption = "$candidate"
                                        if (candidate == 55) {
                                            ArenaHaptics.success(view, reduceMotion)
                                            step = 2
                                            selectedOption = null
                                            feedbackText = "✓ n = 55! Totient φ(55) = (4)(10) = 40 derived."
                                            onActionSuccess()
                                        } else {
                                            ArenaHaptics.error(view, reduceMotion)
                                            onDeductHeart()
                                            feedbackText = "❌ 5 × 11 = 55."
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$candidate",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (isSelected && candidate == 55) Color.Black else dynamicColors.textPrimary
                                )
                            }
                        }
                    }
                }
            }
            2 -> {
                // Select e coprime to 40
                Column(modifier = Modifier.fillMaxWidth(0.95f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "SELECT EXPONENT e WHERE gcd(e, 40) = 1:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = accent,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(3 to true, 4 to false, 5 to false, 8 to false).forEach { (candidate, isCorrect) ->
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
                                            step = 3
                                            selectedOption = null
                                            feedbackText = "✓ gcd(3, 40) = 1! e = 3 is coprime to φ(n)."
                                            onActionSuccess()
                                        } else {
                                            ArenaHaptics.error(view, reduceMotion)
                                            onDeductHeart()
                                            feedbackText = "❌ gcd($candidate, 40) ≠ 1! It shares factors with 40."
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "e = $candidate",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (isSelected && isCorrect) Color.Black else dynamicColors.textPrimary
                                )
                            }
                        }
                    }
                }
            }
            3 -> {
                // Solve d where (3 * d) mod 40 == 1
                Column(modifier = Modifier.fillMaxWidth(0.95f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "SOLVE TRAPDOOR d: (3 × d) mod 40 ≡ 1:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = accent,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(27 to true, 13 to false, 7 to false, 31 to false).forEach { (candidate, isCorrect) ->
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
                                            ArenaHaptics.criticalStrike(view, reduceMotion)
                                            step = 4
                                            selectedOption = null
                                            feedbackText = "🎉 Correct! 3 × 27 = 81 = (2 × 40) + 1 ≡ 1 mod 40!"
                                            onActionSuccess()
                                        } else {
                                            ArenaHaptics.error(view, reduceMotion)
                                            onDeductHeart()
                                            feedbackText = "❌ (3 × $candidate) mod 40 ≠ 1."
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "d = $candidate",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (isSelected && isCorrect) Color.Black else dynamicColors.textPrimary
                                )
                            }
                        }
                    }
                }
            }
            else -> {
                // Step 4: Advance
                Tactile3DButton(
                    title = "LOCK KEY PAIR IN CITADEL VAULT ➔",
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
