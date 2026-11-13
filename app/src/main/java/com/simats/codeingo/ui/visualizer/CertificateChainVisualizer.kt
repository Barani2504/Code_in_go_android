package com.simats.codeingo.ui.visualizer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.components.motion.pressSquish
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.FeedbackManager
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.PhoenixCrimson
import com.simats.codeingo.ui.theme.PhoenixGreen
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.visualizer.components.VisualizerPlaybackController

/**
 * 📜 Digital Certificate Chain of Trust & PKI Hierarchy Visualizer
 *
 * Implements Phase 4.1:
 * - Root CA -> Intermediate CA -> End-Entity (Leaf) TLS Certificate
 * - Visualizes digital signature verification chaining: Sig = Sign(Hash(Cert), PrivateKey_Issuer)
 * - Interactive OCSP/CRL revocation toggle demonstrating instant trust failure
 */
@Composable
fun CertificateChainVisualizer(modifier: Modifier = Modifier) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    var isRevoked by remember { mutableStateOf(false) }
    var currentStep by remember { mutableIntStateOf(1) } // 1: Leaf, 2: Intermediate, 3: Root CA Trust
    var isPlaying by remember { mutableStateOf(false) }
    var playbackSpeed by remember { mutableStateOf(1.0f) }

    val cardBg = if (isDark) Color(0xFF111827) else Color.White
    val borderColor = if (isDark) Color(0xFF1F2937) else Color(0xFFE2E8F0)

    val validationColor = if (isRevoked) PhoenixCrimson else PhoenixGreen

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Status & Revocation Toggle Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(cardBg)
                .border(1.2.dp, borderColor, RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("📜", fontSize = 16.sp)
                        Text(
                            text = "X.509 CHAIN OF TRUST",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = dynamicColors.textPrimary
                        )
                    }

                    // Interactive Revocation Simulator
                    Box(
                        modifier = Modifier
                            .pressSquish {
                                FeedbackManager.tap()
                                isRevoked = !isRevoked
                            }
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isRevoked) PhoenixCrimson.copy(alpha = 0.2f)
                                else AmberGold.copy(alpha = 0.15f)
                            )
                            .border(
                                1.dp,
                                if (isRevoked) PhoenixCrimson else AmberGold,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = if (isRevoked) "🚨 RESTORE CHAIN" else "⚡ SIMULATE REVOCATION",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isRevoked) PhoenixCrimson else AmberGold
                        )
                    }
                }

                Text(
                    text = if (isRevoked)
                        "❌ SEC_ERROR_REVOKED_CERTIFICATE: OCSP responder returned REVOKED. Client aborts TLS handshake immediately. No encrypted tunnel is established."
                    else
                        "✅ VALID TRUST CHAIN: Browser validates each signature recursively until encountering a Root Certificate embedded in the OS trust store.",
                    fontSize = 11.5.sp,
                    color = validationColor,
                    lineHeight = 15.sp
                )
            }
        }

        // Certificate Hierarchy Stack
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Level 3: Root CA (Top)
            CertificateNodeCard(
                levelLabel = "ROOT CA (TIER 3)",
                subject = "CN = AshCrypt Global Root CA G1",
                issuer = "CN = AshCrypt Global Root CA G1 (Self-Signed)",
                keyType = "RSA 4096 / SHA-384",
                isTrusted = true,
                isActive = currentStep >= 3,
                isRevoked = false
            )

            Text(text = "▲ Signed by Root CA Private Key", fontSize = 10.sp, color = AmberGold, modifier = Modifier.padding(start = 24.dp))

            // Level 2: Intermediate CA (Middle)
            CertificateNodeCard(
                levelLabel = "INTERMEDIATE CA (TIER 2)",
                subject = "CN = AshCrypt Secure Server CA 2026",
                issuer = "CN = AshCrypt Global Root CA G1",
                keyType = "ECDSA secp384r1 / SHA-256",
                isTrusted = !isRevoked,
                isActive = currentStep >= 2,
                isRevoked = isRevoked
            )

            Text(text = "▲ Signed by Intermediate CA Private Key", fontSize = 10.sp, color = AmberGold, modifier = Modifier.padding(start = 24.dp))

            // Level 1: Leaf / End-Entity Certificate (Bottom)
            CertificateNodeCard(
                levelLabel = "LEAF SERVER CERTIFICATE (TIER 1)",
                subject = "CN = *.ashcrypt.app",
                issuer = "CN = AshCrypt Secure Server CA 2026",
                keyType = "ECDSA secp256r1 / SHA-256",
                isTrusted = !isRevoked,
                isActive = currentStep >= 1,
                isRevoked = isRevoked
            )
        }

        // Playback Controller
        VisualizerPlaybackController(
            currentStep = currentStep,
            totalSteps = 3,
            onStepChange = { currentStep = it },
            isPlaying = isPlaying,
            onPlayPauseToggle = { isPlaying = !isPlaying },
            playbackSpeed = playbackSpeed,
            onSpeedChange = { playbackSpeed = it },
            stepLabel = "VERIFICATION DEPTH",
            accentColor = AmberGold
        )
    }
}

@Composable
private fun CertificateNodeCard(
    levelLabel: String,
    subject: String,
    issuer: String,
    keyType: String,
    isTrusted: Boolean,
    isActive: Boolean,
    isRevoked: Boolean
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val borderCol = when {
        isRevoked -> PhoenixCrimson
        !isActive -> if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
        isTrusted -> PhoenixGreen
        else -> AmberGold
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isDark) Color(0xFF111827) else Color.White)
            .border(1.2.dp, borderCol, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = levelLabel, fontSize = 9.sp, fontWeight = FontWeight.Black, color = AmberGold)
                Text(
                    text = if (isRevoked) "REVOKED ❌" else if (isTrusted && isActive) "VERIFIED ✓" else "PENDING…",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isRevoked) PhoenixCrimson else if (isTrusted && isActive) PhoenixGreen else SubtextGray
                )
            }
            Text(text = subject, fontSize = 11.5.sp, fontWeight = FontWeight.Black, color = dynamicColors.textPrimary)
            Text(text = "Issuer: $issuer", fontSize = 10.sp, color = dynamicColors.textSecondary)
            Text(text = "Key: $keyType", fontSize = 9.5.sp, fontFamily = FontFamily.Monospace, color = SubtextGray)
        }
    }
}

