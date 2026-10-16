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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.VpnKey
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
fun KeyPairForgeLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    // Step 0: Initial primes p=3, q=11
    // Step 1: n = 33, phi(n) = 20 computed
    // Step 2: Public exponent e = 3 chosen
    // Step 3: Private exponent d = 7 derived
    // Step 4: Keys split into Public & Private Vaults
    var step by remember { mutableIntStateOf(0) }

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
                        imageVector = Icons.Default.VpnKey,
                        contentDescription = null,
                        tint = AmberGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "RSA ASYMMETRIC KEY PAIR FORGE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = AmberGold
                    )
                }
                Text(
                    text = when (step) {
                        0 -> "Step 1: Pick two distinct prime numbers p = 3 and q = 11 to begin forging an RSA key pair."
                        1 -> "Step 2: Calculate public modulus n = p × q = 33 and Euler Totient φ(n) = (p-1)(q-1) = 20."
                        2 -> "Step 3: Select public encryption exponent e = 3 such that gcd(e, φ(n)) = 1."
                        3 -> "Step 4: Compute private trapdoor exponent d ≡ e⁻¹ mod φ(n) ➔ 3 × 7 ≡ 21 ≡ 1 mod 20 ➔ d = 7."
                        else -> "🎉 Key Pair Forged! Modulus n & exponent e are public; trapdoor d is locked in private vault!"
                    },
                    fontSize = 12.5.sp,
                    color = dynamicColors.textPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp
                )
            }
        }

        // Mathematical Computation Cards
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
                    .border(1.dp, if (step >= 1) AmberGold else Color.Transparent, RoundedCornerShape(14.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "MODULUS n", fontSize = 10.sp, fontWeight = FontWeight.Black, color = SubtextGray)
                    Text(
                        text = if (step >= 1) "33 (3×11)" else "p × q",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (step >= 1) AmberGold else dynamicColors.textPrimary
                    )
                }
            }

            // Totient phi(n)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                    .border(1.dp, if (step >= 1) AmberGold else Color.Transparent, RoundedCornerShape(14.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "TOTIENT φ(n)", fontSize = 10.sp, fontWeight = FontWeight.Black, color = SubtextGray)
                    Text(
                        text = if (step >= 1) "20 (2×10)" else "(p-1)(q-1)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (step >= 1) AmberGold else dynamicColors.textPrimary
                    )
                }
            }
        }

        // Dual Vault Display (Public vs Private)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Public Key Vault (Shared with world)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF3B82F6).copy(alpha = 0.12f))
                    .border(1.2.dp, Color(0xFF3B82F6).copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                    .padding(14.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.Public, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(24.dp))
                    Text(text = "PUBLIC KEY", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFF60A5FA))
                    Text(
                        text = if (step >= 2) "{ e = 3, n = 33 }" else "{ e = ?, n = ? }",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = dynamicColors.textPrimary
                    )
                    Text(text = "Published to Internet", fontSize = 9.sp, color = SubtextGray)
                }
            }

            // Private Key Vault (Strictly confidential)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFEF4444).copy(alpha = 0.12f))
                    .border(1.2.dp, Color(0xFFEF4444).copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                    .padding(14.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(24.dp))
                    Text(text = "PRIVATE KEY", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFFEF4444))
                    Text(
                        text = if (step >= 3) "{ d = 7, n = 33 }" else "{ d = ?, n = ? }",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (step >= 3) DuolingoGreen else dynamicColors.textPrimary
                    )
                    Text(text = "Locked in Secure Vault", fontSize = 9.sp, color = SubtextGray)
                }
            }
        }

        // Action Stepper Button
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (step >= 4) Brush.horizontalGradient(listOf(DuolingoGreen, Color(0xFF059669)))
                    else Brush.horizontalGradient(listOf(AmberGold, Color(0xFFD97706)))
                )
                .pressScale(targetScale = 0.96f)
                .clickable {
                    if (step < 4) {
                        step++
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
                    imageVector = if (step >= 4) Icons.Default.Check else Icons.Default.VpnKey,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = when (step) {
                        0 -> "1. COMPUTE n & φ(n) →"
                        1 -> "2. SELECT PUBLIC EXPONENT e = 3 →"
                        2 -> "3. COMPUTE TRAPDOOR d = 7 →"
                        3 -> "4. SEAL KEYS INTO VAULTS →"
                        else -> "RSA KEY PAIR FORGED! ADVANCE →"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Black,
                    letterSpacing = 0.5.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
