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

    val sectionAccent = if (dynamicColors.isDark) AmberGold else Color(0xFFB45309)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "TLS 1.3 AEAD SUITE LAB",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = sectionAccent,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Assemble Authenticated Encryption with Associated Data (AEAD)",
                fontSize = 10.5.sp,
                color = if (dynamicColors.isDark) dynamicColors.textSecondary else Color(0xFF475569),
                textAlign = TextAlign.Center
            )
        }

        // Explanatory Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (dynamicColors.isDark) Color(0xFF0F172A).copy(alpha = 0.85f) else Color.White)
                .border(1.dp, if (dynamicColors.isDark) sectionAccent.copy(alpha = 0.4f) else Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
                .padding(10.dp)
        ) {
            Column {
                Text(
                    text = "🔒 TLS 1.3 Pruning & AEAD Mandate",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = sectionAccent
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "TLS 1.3 removed legacy algorithms (Static RSA, RC4, 3DES, CBC mode, SHA-1). Only modern AEAD ciphers that combine confidentiality and integrity in a single cryptographic primitive are allowed!",
                    fontSize = 11.sp,
                    color = if (dynamicColors.isDark) dynamicColors.textPrimary else Color(0xFF334155),
                    lineHeight = 15.sp
                )
            }
        }

        // Live Generated Suite Token Badge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (dynamicColors.isDark) dynamicColors.cardBackground else Color.White)
                .border(
                    1.5.dp,
                    if (isSuiteSecure) (if (dynamicColors.isDark) DuolingoGreen else Color(0xFF059669)) else DuolingoRed,
                    RoundedCornerShape(12.dp)
                )
                .padding(10.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "NEGOTIATED CIPHER SUITE TOKEN",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = dynamicColors.textSecondary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "TLS_${selectedCipher.replace("-", "_")}_$selectedHash",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = if (isSuiteSecure) DuolingoGreen else DuolingoRed,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isSuiteSecure) "✓ AEAD Compliant • Forward Secrecy Guaranteed"
                    else "⚠️ Insecure Primitives Present in Suite!",
                    fontSize = 10.5.sp,
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
                color = sectionAccent
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
                                else (if (dynamicColors.isDark) Color(0xFF0F172A).copy(alpha = 0.6f) else Color.White)
                            )
                            .border(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) (if (kex.contains("ECDHE")) (if (dynamicColors.isDark) DuolingoGreen else Color(0xFF059669)) else DuolingoRed) else (if (dynamicColors.isDark) Color(0xFF334155) else Color(0xFFCBD5E1)),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedKex = kex }
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = kex,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                            color = if (isSelected) (if (kex.contains("ECDHE")) (if (dynamicColors.isDark) DuolingoGreen else Color(0xFF059669)) else DuolingoRed) else (if (dynamicColors.isDark) Color(0xFFE2E8F0) else Color(0xFF0F172A)),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Slot 2: Bulk Cipher (AEAD vs Legacy) in a clean 2x2 grid
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "SLOT 2: ENCRYPTION & AUTHENTICATION (AEAD)",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = sectionAccent
            )

            val cipherPairs = listOf(
                Pair(Pair("AES-256-GCM", "Modern AEAD"), Pair("CHACHA20-POLY1305", "Fast AEAD")),
                Pair(Pair("3DES-EDE-CBC", "Legacy 64-bit"), Pair("RC4-128", "Deprecated stream"))
            )

            cipherPairs.forEach { (c1, c2) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(c1, c2).forEach { (cipher, desc) ->
                        val isSelected = selectedCipher == cipher
                        val isSecure = cipher.contains("GCM") || cipher.contains("POLY1305")
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) (if (isSecure) DuolingoGreen.copy(alpha = 0.15f) else DuolingoRed.copy(alpha = 0.15f))
                                    else (if (dynamicColors.isDark) Color(0xFF0F172A).copy(alpha = 0.6f) else Color.White)
                                )
                                .border(
                                    if (isSelected) 2.dp else 1.dp,
                                    if (isSelected) (if (isSecure) (if (dynamicColors.isDark) DuolingoGreen else Color(0xFF059669)) else DuolingoRed) else (if (dynamicColors.isDark) Color(0xFF334155) else Color(0xFFCBD5E1)),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedCipher = cipher }
                                .padding(horizontal = 8.dp, vertical = 8.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = cipher,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) (if (isSecure) (if (dynamicColors.isDark) DuolingoGreen else Color(0xFF059669)) else DuolingoRed) else (if (dynamicColors.isDark) Color.White else Color(0xFF0F172A)),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = desc,
                                    fontSize = 9.5.sp,
                                    color = if (dynamicColors.isDark) dynamicColors.textSecondary else Color(0xFF475569),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        // Slot 3: Hash Algorithm
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "SLOT 3: HKDF PRF HASH",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = sectionAccent
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
                                else (if (dynamicColors.isDark) Color(0xFF0F172A).copy(alpha = 0.6f) else Color.White)
                            )
                            .border(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) (if (isSecure) (if (dynamicColors.isDark) DuolingoGreen else Color(0xFF059669)) else DuolingoRed) else (if (dynamicColors.isDark) Color(0xFF334155) else Color(0xFFCBD5E1)),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedHash = hash }
                            .padding(vertical = 8.dp, horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = hash,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                            color = if (isSelected) (if (isSecure) (if (dynamicColors.isDark) DuolingoGreen else Color(0xFF059669)) else DuolingoRed) else (if (dynamicColors.isDark) Color(0xFFE2E8F0) else Color(0xFF0F172A)),
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
                .height(46.dp),
            shape = RoundedCornerShape(12.dp),
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
                fontSize = 12.sp,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
