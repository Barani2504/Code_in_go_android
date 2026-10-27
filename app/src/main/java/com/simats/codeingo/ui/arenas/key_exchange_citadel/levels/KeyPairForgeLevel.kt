package com.simats.codeingo.ui.arenas.key_exchange_citadel.levels

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

@Composable
fun KeyPairForgeLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    // Step 0: Select distinct primes p = 5, q = 11
    // Step 1: Compute modulus n = 55, Euler totient phi(n) = 40
    // Step 2: Choose public exponent e = 3 (gcd(3, 40) = 1)
    // Step 3: Derive private trapdoor exponent d = 27 (3 * 27 = 81 = 2 * 40 + 1)
    // Step 4: Vault keys into Public (e=3, n=55) and Private (d=27, n=55)
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
                        0 -> "Step 1: Pick two distinct prime numbers p = 5 and q = 11 to begin forging the RSA key pair."
                        1 -> "Step 2: Calculate public modulus n = p × q = 55 and Euler Totient φ(n) = (p-1)(q-1) = 40."
                        2 -> "Step 3: Select public encryption exponent e = 3 such that gcd(e, φ(n)) = gcd(3, 40) = 1."
                        3 -> "Step 4: Derive private trapdoor exponent d ≡ e⁻¹ mod φ(n) ➔ 3 × 27 = 81 ≡ 1 mod 40 ➔ d = 27."
                        else -> "🎉 Key Pair Forged! Public Key (e=3, n=55) broadcast to the realm; Private Key (d=27, n=55) safely vaulted!"
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
                        text = if (step >= 1) "55 (5×11)" else "p × q",
                        fontSize = 14.sp,
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
                        text = if (step >= 1) "40 (4×10)" else "(p-1)(q-1)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (step >= 1) AmberGold else dynamicColors.textPrimary
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
                    .border(1.dp, if (step >= 2) AmberGold else Color.Transparent, RoundedCornerShape(14.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "PUBLIC EXPONENT e", fontSize = 9.sp, fontWeight = FontWeight.Black, color = SubtextGray)
                    Text(
                        text = if (step >= 2) "e = 3" else "gcd(e, φ)=1",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (step >= 2) AmberGold else dynamicColors.textPrimary
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
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "PRIVATE EXPONENT d", fontSize = 9.sp, fontWeight = FontWeight.Black, color = SubtextGray)
                    Text(
                        text = if (step >= 3) "d = 27" else "e⁻¹ mod φ(n)",
                        fontSize = 14.sp,
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
                        .background(Color(0xFF3B82F6).copy(alpha = 0.15f))
                        .border(1.dp, Color(0xFF3B82F6), RoundedCornerShape(14.dp))
                        .padding(10.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(imageVector = Icons.Default.Public, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(14.dp))
                            Text(text = "PUBLIC KEY", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF60A5FA))
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

        Spacer(modifier = Modifier.height(6.dp))

        Tactile3DButton(
            title = when (step) {
                0 -> "1. Select Primes p=5, q=11 →"
                1 -> "2. Compute Modulus n=55, φ=40 →"
                2 -> "3. Select Exponent e=3 →"
                3 -> "4. Derive Private Exponent d=27 🔓"
                else -> "Lock Key Pair in Citadel Vault →"
            },
            style = if (step >= 4) Tactile3DButtonColor.GREEN else Tactile3DButtonColor.AMBER,
            onClick = {
                if (step < 4) {
                    step++
                    onActionSuccess()
                } else {
                    onComplete()
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
