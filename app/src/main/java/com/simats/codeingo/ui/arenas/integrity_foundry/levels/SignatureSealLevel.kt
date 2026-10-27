package com.simats.codeingo.ui.arenas.integrity_foundry.levels

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
fun SignatureSealLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current

    // Hash-Then-Sign Verification (ToyRSA5_11: n=55, e=3, d=27):
    // Digest h = 8 (from H(Message) mod 55)
    // Signature s = h^d mod n = 8^27 mod 55 = 2
    // Verification: s^e mod n = 2^3 mod 55 = 8 == h (Valid!)
    val digestH = 8L
    val privateD = 27L
    val publicE = 3L
    val modulusN = 55L

    val signatureS = remember { ArenaCryptoEngine.modPow(digestH, privateD, modulusN) } // 2L
    val verifiedDigest = remember { ArenaCryptoEngine.modPow(signatureS, publicE, modulusN) } // 8L

    var step by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "ASYMMETRIC DIGITAL SIGNATURE SEAL",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Hash-Then-Sign: Sign with Private Key d • Verify with Public Key e",
                fontSize = 11.sp,
                color = dynamicColors.textSecondary,
                textAlign = TextAlign.Center
            )
        }

        // Explanatory Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(dynamicColors.cardBorder.copy(alpha = 0.2f))
                .border(1.dp, AmberGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(imageVector = Icons.Default.VpnKey, contentDescription = null, tint = AmberGold, modifier = Modifier.size(16.dp))
                    Text(text = "Non-Repudiation & Integrity:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AmberGold)
                }
                Text(
                    text = "A digital signature hashes message M ➔ h, then encrypts h with the signer's secret private key d: s = hᵈ mod n. Anyone with public key (e, n) computes sᵉ mod n and checks if it matches h!",
                    fontSize = 12.sp,
                    color = dynamicColors.textPrimary,
                    lineHeight = 16.sp
                )
            }
        }

        // Signature Pipeline Status Cards
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            // Card 1: Message Digest
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (step >= 1) AmberGold.copy(alpha = 0.15f) else dynamicColors.cardBorder.copy(alpha = 0.2f))
                    .border(1.dp, if (step >= 1) AmberGold else dynamicColors.cardBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(text = "1. MESSAGE HASH DIGEST (h):", fontSize = 10.sp, fontWeight = FontWeight.Black, color = SubtextGray)
                        Text(text = "h = H(\"Transfer 1000 Coins\") mod 55 = $digestH", fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary)
                    }
                    Text(text = if (step >= 1) "✓ HASHED" else "READY", fontSize = 10.sp, fontWeight = FontWeight.Black, color = if (step >= 1) AmberGold else SubtextGray)
                }
            }

            // Card 2: Private Key Signature Generation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (step >= 2) Color(0xFF3B82F6).copy(alpha = 0.15f) else dynamicColors.cardBorder.copy(alpha = 0.2f))
                    .border(1.dp, if (step >= 2) Color(0xFF3B82F6) else dynamicColors.cardBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(text = "2. SIGN WITH PRIVATE KEY (d = 27):", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF60A5FA))
                        Text(
                            text = if (step >= 2) "s = hᵈ mod 55 = 8²⁷ mod 55 = $signatureS" else "s = hᵈ mod n (Private)",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = dynamicColors.textPrimary
                        )
                    }
                    Text(text = if (step >= 2) "✓ SIGNED" else "PENDING", fontSize = 10.sp, fontWeight = FontWeight.Black, color = if (step >= 2) Color(0xFF60A5FA) else SubtextGray)
                }
            }

            // Card 3: Public Key Signature Verification
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (step >= 3) ArenaSuccessGreen.copy(alpha = 0.15f) else dynamicColors.cardBorder.copy(alpha = 0.2f))
                    .border(1.dp, if (step >= 3) ArenaSuccessGreen else dynamicColors.cardBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(text = "3. VERIFY WITH PUBLIC KEY (e = 3):", fontSize = 10.sp, fontWeight = FontWeight.Black, color = if (step >= 3) ArenaSuccessGreen else SubtextGray)
                        Text(
                            text = if (step >= 3) "sᵉ mod 55 = 2³ mod 55 = $verifiedDigest == h ✓" else "sᵉ mod n ≟ h (Public Verify)",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = if (step >= 3) ArenaSuccessGreen else dynamicColors.textPrimary
                        )
                    }
                    Text(text = if (step >= 3) "✓ VALID" else "UNVERIFIED", fontSize = 10.sp, fontWeight = FontWeight.Black, color = if (step >= 3) ArenaSuccessGreen else SubtextGray)
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Tactile3DButton(
            title = when (step) {
                0 -> "1. Compute Hash Digest h=8 ➔"
                1 -> "2. Sign Digest with Private Key (d=27) ✍️"
                2 -> "3. Verify Signature with Public Key (e=3) 🔍"
                else -> "Digital Signature Validated! Advance ➔"
            },
            style = if (step >= 3) Tactile3DButtonColor.GREEN else Tactile3DButtonColor.AMBER,
            onClick = {
                if (step < 3) {
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
