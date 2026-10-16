package com.simats.codeingo.ui.arenas.cyber_bastion.levels

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

@Composable
fun CipherSuiteLabLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current

    // Interactive Slots for TLS 1.3 AEAD Suite Assembler
    var selectedKex by remember { mutableStateOf("ECDHE (X25519)") }
    var selectedCipher by remember { mutableStateOf("AES-256-GCM") }
    var selectedHash by remember { mutableStateOf("SHA-384") }

    val isKexSecure = selectedKex == "ECDHE (X25519)"
    val isCipherSecure = selectedCipher == "AES-256-GCM" || selectedCipher == "CHACHA20-POLY1305"
    val isHashSecure = selectedHash == "SHA-384"
    val isSuiteSecure = isKexSecure && isCipherSecure && isHashSecure

    var hasAttemptedHandshake by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "TLS 1.3 AEAD SUITE LAB",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Assemble Authenticated Encryption with Associated Data (AEAD)",
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
            Column {
                Text(
                    text = "🔒 TLS 1.3 Pruning & AEAD Mandate",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AmberGold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "TLS 1.3 removed legacy algorithms (Static RSA, RC4, 3DES, CBC mode, SHA-1). Only modern AEAD ciphers that combine confidentiality and integrity in a single cryptographic primitive are allowed!",
                    fontSize = 11.5.sp,
                    color = dynamicColors.textPrimary,
                    lineHeight = 16.sp
                )
            }
        }

        // Live Generated Suite Token Badge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(dynamicColors.cardBackground)
                .border(
                    1.5.dp,
                    if (isSuiteSecure) DuolingoGreen else DuolingoRed,
                    RoundedCornerShape(14.dp)
                )
                .padding(14.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "NEGOTIATED CIPHER SUITE TOKEN",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = dynamicColors.textSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "TLS_${selectedCipher.replace("-", "_")}_$selectedHash",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = if (isSuiteSecure) DuolingoGreen else DuolingoRed,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isSuiteSecure) "✓ AEAD Compliant • Forward Secrecy Guaranteed"
                    else "⚠️ Insecure Primitives Present in Suite!",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSuiteSecure) DuolingoGreen else DuolingoRed
                )
            }
        }

        // Slot 1: Key Exchange Selector
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "SLOT 1: KEY EXCHANGE (FORWARD SECRECY)",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("ECDHE (X25519)", "Static RSA (Legacy)").forEach { kex ->
                    val isSelected = selectedKex == kex
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) (if (kex.contains("ECDHE")) DuolingoGreen.copy(alpha = 0.2f) else DuolingoRed.copy(alpha = 0.2f))
                                else dynamicColors.cardBorder.copy(alpha = 0.2f)
                            )
                            .border(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) (if (kex.contains("ECDHE")) DuolingoGreen else DuolingoRed) else dynamicColors.cardBorder.copy(alpha = 0.3f),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedKex = kex }
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = kex,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                            color = if (isSelected) (if (kex.contains("ECDHE")) DuolingoGreen else DuolingoRed) else dynamicColors.textPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Slot 2: Bulk Cipher (AEAD vs Legacy)
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "SLOT 2: ENCRYPTION & AUTHENTICATION (AEAD)",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold
            )
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(
                    Pair("AES-256-GCM", "Modern Galois/Counter AEAD"),
                    Pair("CHACHA20-POLY1305", "High-speed modern AEAD"),
                    Pair("3DES-EDE-CBC", "Legacy 64-bit block (Sweet32 attack)"),
                    Pair("RC4-128", "Deprecated biased stream cipher")
                ).forEach { (cipher, desc) ->
                    val isSelected = selectedCipher == cipher
                    val isSecure = cipher.contains("GCM") || cipher.contains("POLY1305")
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) (if (isSecure) DuolingoGreen.copy(alpha = 0.15f) else DuolingoRed.copy(alpha = 0.15f))
                                else dynamicColors.cardBorder.copy(alpha = 0.15f)
                            )
                            .border(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) (if (isSecure) DuolingoGreen else DuolingoRed) else dynamicColors.cardBorder.copy(alpha = 0.3f),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedCipher = cipher }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = cipher,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) (if (isSecure) DuolingoGreen else DuolingoRed) else dynamicColors.textPrimary
                                )
                                Text(
                                    text = desc,
                                    fontSize = 10.5.sp,
                                    color = dynamicColors.textSecondary
                                )
                            }
                            if (isSelected) {
                                Text(
                                    text = if (isSecure) "✓ AEAD" else "✕ INSECURE",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isSecure) DuolingoGreen else DuolingoRed
                                )
                            }
                        }
                    }
                }
            }
        }

        // Slot 3: Hash Algorithm
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "SLOT 3: HKDF PRF HASH",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("SHA-384", "SHA-1 (Legacy Broken)").forEach { hash ->
                    val isSelected = selectedHash == hash
                    val isSecure = hash.startsWith("SHA-384")
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) (if (isSecure) DuolingoGreen.copy(alpha = 0.2f) else DuolingoRed.copy(alpha = 0.2f))
                                else dynamicColors.cardBorder.copy(alpha = 0.2f)
                            )
                            .border(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) (if (isSecure) DuolingoGreen else DuolingoRed) else dynamicColors.cardBorder.copy(alpha = 0.3f),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedHash = hash }
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = hash,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                            color = if (isSelected) (if (isSecure) DuolingoGreen else DuolingoRed) else dynamicColors.textPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Handshake Execution Action Button
        Button(
            onClick = {
                hasAttemptedHandshake = true
                if (isSuiteSecure) {
                    onActionSuccess()
                    onComplete()
                } else {
                    onDeductHeart()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (hasAttemptedHandshake && isSuiteSecure) DuolingoGreen
                else if (hasAttemptedHandshake) DuolingoRed
                else AmberGold
            )
        ) {
            Text(
                text = if (hasAttemptedHandshake && isSuiteSecure) "✓ TLS 1.3 HANDSHAKE ESTABLISHED"
                else if (hasAttemptedHandshake) "✕ HANDSHAKE REJECTED (INSECURE SUITE)"
                else "NEGOTIATE SECURE TLS 1.3 SESSION",
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                color = Color.Black
            )
        }
    }
}
