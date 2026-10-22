package com.simats.codeingo.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.data.model.UnitModel
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.pressScale

/**
 * UnitSectionBanner — DSA Chapter Header Banner Card with Lore Era Tag & Guidebook Button.
 * Exact parity with iOS MainDashboardView.swift unitSectionView.
 */
@Composable
fun UnitSectionBanner(
    unit: UnitModel,
    isUnlocked: Boolean,
    onGuidebookClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val eraLore = when (unit.id) {
        1 -> "The Classical Cryptography Era"
        2 -> "The Symmetric Block Fortress"
        3 -> "The Asymmetric Spire Era"
        4 -> "The Cryptographic Integrity Era"
        5 -> "The Network Defense Sovereign"
        6 -> "The Identity Vault Era"
        7 -> "The Web Fortress Era"
        8 -> "The Spectrum Citadel Era"
        9 -> "The Defense Operations Era"
        10 -> "The Cryptographic Wilds Era"
        else -> "Phoenix Security Realm ${unit.unitNumber}"
    }

    val topicsPreview = when (unit.id) {
        1 -> "Caesar & Atbash • Vigenère Tableau • Rotor Stepping • Frequency Radio"
        2 -> "AES-128 State Matrix • S-Box Non-Linearity • ShiftRows • MixColumns GF(2^8)"
        3 -> "Diffie-Hellman Key Exchange • Modular Trapdoors • RSA Clock • ECC Point Addition"
        4 -> "SHA-256 Digest • Keccak Sponge Chamber • Merkle Tree Proofs • Digital Signatures"
        5 -> "TLS 1.3 1-RTT Handshake • 5-Tuple Stateful Firewall • ARP Defense • IPsec Packets"
        6 -> "3 Factors & Argon2id • TOTP Time Buckets • Kerberos TGT • KMS & Envelope Encryption • OAuth PKCE"
        7 -> "HTTPS & SOP • SQLi & Parameterized Queries • XSS & CSRF • OWASP Top 10"
        8 -> "WPA3 SAE Handshake • PGP & S/MIME • Anti-Phishing • SSH Host Verification"
        9 -> "IDS vs IPS • DMZ & Honeypots • Malware Zoo • NIST Incident Response"
        10 -> "CSPRNG Entropy • Padding Oracles • Historic Crypto Flaws • Post-Quantum FIPS 203"
        else -> "Cryptography & Network Security Principles"
    }

    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        if (isUnlocked) {
            // Unlocked Chapter Banner
            val bannerGradient = if (isDark) listOf(
                unit.themeColor.copy(alpha = 0.50f),
                unit.themeDarkColor.copy(alpha = 0.40f),
                Color(0xFF0A1020).copy(alpha = 0.85f)
            ) else listOf(
                unit.themeColor.copy(alpha = 0.90f),
                unit.themeDarkColor.copy(alpha = 0.95f),
                Color(0xFF121C30)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(Brush.linearGradient(bannerGradient))
                    .border(
                        1.5.dp,
                        Brush.linearGradient(
                            listOf(
                                Color.White.copy(alpha = 0.50f),
                                unit.themeColor.copy(alpha = 0.70f),
                                Color.White.copy(alpha = 0.10f)
                            )
                        ),
                        RoundedCornerShape(22.dp)
                    )
                    .shadow(12.dp, RoundedCornerShape(22.dp), spotColor = unit.themeColor.copy(alpha = 0.30f))
                    .padding(18.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Phoenix Era Lore Tag
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.35f))
                            .border(1.dp, Color.White.copy(alpha = 0.35f), CircleShape)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = eraLore.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White
                        )
                    }

                    Text(
                        text = unit.titleDefault,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )

                    Text(
                        text = topicsPreview,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.88f),
                        lineHeight = 16.sp
                    )
                }

                // Guide Button with pressScale
                Row(
                    modifier = Modifier
                        .pressScale(0.92f)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.35f))
                        .border(
                            1.2.dp,
                            Brush.linearGradient(
                                listOf(Color.White.copy(alpha = 0.50f), Color.White.copy(alpha = 0.15f))
                            ),
                            CircleShape
                        )
                        .clickable { onGuidebookClick() }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Guide",
                        tint = unit.themeColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Guide",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }
        } else {
            // Locked Chapter Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(if (isDark) Color(0xFF0F1420).copy(alpha = 0.75f) else Color.White.copy(alpha = 0.85f))
                    .border(
                        1.dp,
                        if (isDark) dynamicColors.placeholder.copy(alpha = 0.12f) else Color(0xFFD7DEEB),
                        RoundedCornerShape(22.dp)
                    )
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = eraLore.uppercase(),
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = dynamicColors.textPrimary.copy(alpha = 0.55f)
                    )

                    Text(
                        text = unit.titleDefault,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = dynamicColors.textPrimary.copy(alpha = 0.65f)
                    )

                    Text(
                        text = "🔒 Complete Unit ${unit.unitNumber - 1} to unlock this world!",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = dynamicColors.textPrimary.copy(alpha = 0.50f)
                    )
                }
            }
        }
    }
}
