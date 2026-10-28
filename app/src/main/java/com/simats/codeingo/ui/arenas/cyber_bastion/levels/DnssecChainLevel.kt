package com.simats.codeingo.ui.arenas.cyber_bastion.levels

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
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

private data class ChainChoice(
    val id: String,
    val text: String,
    val isCorrect: Boolean,
    val explanation: String
)

private data class ChainTier(
    val tierIndex: Int,
    val nodeName: String,
    val nodeSubtitle: String,
    val prompt: String,
    val choices: List<ChainChoice>
)

@Composable
fun DnssecChainLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val view = LocalView.current
    val reduceMotion = LocalReduceMotion.current
    val isDark = dynamicColors.isDark

    val emerald = ArenaPalettes.Arena5Primary
    val cyan = ArenaPalettes.Arena5Accent
    val cardBg = if (isDark) ArenaPalettes.Arena5CardDark else dynamicColors.cardBackground

    val tiers = remember {
        listOf(
            ChainTier(
                tierIndex = 0,
                nodeName = "1. TRUST ANCHOR (ROOT CA)",
                nodeSubtitle = "Self-Signed Root Authority / DNSSEC KSK Root Zone",
                prompt = "A client initiates TLS to bastion.cyber.internal. Where must the cryptographic Trust Anchor originate?",
                choices = listOf(
                    ChainChoice(
                        "t0_correct",
                        "Locally pre-installed OS / Browser Trust Store (or IANA Root KSK)",
                        true,
                        "Correct! Trust Anchors must be established out-of-band in the local operating system trust store."
                    ),
                    ChainChoice(
                        "t0_w1",
                        "Downloaded in real-time over cleartext HTTP from the contacted server",
                        false,
                        "Dangerous! An attacker on the network could inject their own rogue Root CA over cleartext."
                    ),
                    ChainChoice(
                        "t0_w2",
                        "Synthesized dynamically by local DNS resolvers without keys",
                        false,
                        "Incorrect! DNS resolvers cannot synthesize root cryptographic trust without anchored keys."
                    )
                )
            ),
            ChainTier(
                tierIndex = 1,
                nodeName = "2. INTERMEDIATE CA SIGNATURE",
                nodeSubtitle = "Issuer: GlobalRoot CA | Subject: CyberBastion Intermediate",
                prompt = "The server presents CyberBastion Intermediate CA. How does the client mathematically verify this tier?",
                choices = listOf(
                    ChainChoice(
                        "t1_correct",
                        "Verify digital signature with Root CA public key & assert basicConstraints: CA:TRUE",
                        true,
                        "Correct! The client uses Root CA's public key to verify the Intermediate CA signature and checks CA delegation constraints."
                    ),
                    ChainChoice(
                        "t1_w1",
                        "Verify the Intermediate signature using its own self-signed private key",
                        false,
                        "Incorrect! Self-signed verification only applies to the Root anchor, not intermediate authorities."
                    ),
                    ChainChoice(
                        "t1_w2",
                        "Check if the Intermediate CA hostname matches the browser URL bar",
                        false,
                        "Incorrect! Intermediate CAs issue certificates for many domains; they do not match the end-entity URL."
                    )
                )
            ),
            ChainTier(
                tierIndex = 2,
                nodeName = "3. LEAF SERVER CERTIFICATE",
                nodeSubtitle = "Subject: bastion.cyber.internal | SAN: DNS:bastion.cyber.internal",
                prompt = "The leaf certificate is inspected. Which validation suite must pass before accepting the host identity?",
                choices = listOf(
                    ChainChoice(
                        "t2_correct",
                        "Signature verified by Intermediate CA + Current time in [NotBefore, NotAfter] + SAN matches",
                        true,
                        "Correct! Hostname matching, certificate validity period, and valid signature from Intermediate CA are all mandatory."
                    ),
                    ChainChoice(
                        "t2_w1",
                        "Confirm the server bundled its RSA private key in the certificate packet",
                        false,
                        "Fatal! Server private keys must never be transmitted or bundled in certificates."
                    ),
                    ChainChoice(
                        "t2_w2",
                        "Verify that the certificate never expires and supports legacy 512-bit keys",
                        false,
                        "Insecure! Certificates must have bounded lifetimes, and 512-bit keys are broken."
                    )
                )
            ),
            ChainTier(
                tierIndex = 3,
                nodeName = "4. REVOCATION CHECK (OCSP / CRL)",
                nodeSubtitle = "Real-Time Key Compromise Status Inspection",
                prompt = "What cryptographic mechanism guarantees the leaf certificate key has not been revoked by the CA?",
                choices = listOf(
                    ChainChoice(
                        "t3_correct",
                        "OCSP Stapling (RFC 6066 / 6960 timestamped signed response) or CRL inspection",
                        true,
                        "Correct! OCSP stapling delivers a cryptographic proof of active validity signed directly by the CA without extra DNS latency."
                    ),
                    ChainChoice(
                        "t3_w1",
                        "Transmitting ICMP Ping probes to the CA server every 60 seconds",
                        false,
                        "Incorrect! ICMP Echo requests convey zero cryptographic revocation state."
                    ),
                    ChainChoice(
                        "t3_w2",
                        "Allowing expired browser DNS cache entries to dictate revocation",
                        false,
                        "Incorrect! DNS cache TTL has no relationship to X.509 certificate revocation status."
                    )
                )
            )
        )
    }

    var currentTierIndex by remember { mutableIntStateOf(0) }
    var selectedChoiceId by remember { mutableStateOf<String?>(null) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var isTierPassed by remember { mutableStateOf(false) }

    val currentTier = tiers.getOrNull(currentTierIndex)
    val isCompleted = currentTierIndex >= tiers.size

    val shuffledChoices = remember(currentTierIndex) {
        currentTier?.choices?.shuffled() ?: emptyList()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Mission Header with Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "PKI & DNSSEC CHAIN OF TRUST",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = emerald,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "RFC 5280 X.509 Cryptographic Hierarchy",
                    fontSize = 10.sp,
                    color = dynamicColors.textSecondary
                )
            }
            ArenaStatusBadge(
                type = if (isCompleted) ArenaBadgeType.SUCCESS else ArenaBadgeType.INFO,
                label = if (isCompleted) "CHAIN VERIFIED" else "TIER ${currentTierIndex + 1}/4"
            )
        }

        // Live Chain Visualizer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(cardBg)
                .border(1.2.dp, if (isCompleted) emerald else cyan.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "HIERARCHICAL CERTIFICATE PATH",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = cyan
                )

                tiers.forEachIndexed { idx, tier ->
                    val isVerified = idx < currentTierIndex || (idx == currentTierIndex && isTierPassed)
                    val isCurrent = idx == currentTierIndex && !isTierPassed

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when {
                                    isVerified -> emerald.copy(alpha = 0.12f)
                                    isCurrent -> cyan.copy(alpha = 0.08f)
                                    else -> Color.Transparent
                                }
                            )
                            .border(
                                1.dp,
                                when {
                                    isVerified -> emerald
                                    isCurrent -> cyan
                                    else -> cardBg.copy(alpha = 0.5f)
                                },
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isVerified -> emerald
                                        isCurrent -> cyan
                                        else -> SubtextGray.copy(alpha = 0.4f)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isVerified) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(14.dp)
                                )
                            } else {
                                Text(
                                    text = "${idx + 1}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = tier.nodeName,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isVerified) emerald else dynamicColors.textPrimary
                            )
                            Text(
                                text = tier.nodeSubtitle,
                                fontSize = 8.5.sp,
                                fontFamily = FontFamily.Monospace,
                                color = SubtextGray
                            )
                        }

                        Text(
                            text = when {
                                isVerified -> "VALIDATED ✓"
                                isCurrent -> "VERIFYING..."
                                else -> "LOCKED"
                            },
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = when {
                                isVerified -> emerald
                                isCurrent -> cyan
                                else -> SubtextGray
                            }
                        )
                    }

                    if (idx < tiers.size - 1) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = null,
                                tint = if (idx < currentTierIndex) emerald else SubtextGray.copy(alpha = 0.5f),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        }

        if (currentTier != null && !isCompleted) {
            // Tier Prompt Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(cardBg)
                    .border(1.dp, emerald.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = currentTier.nodeName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = emerald
                    )
                    Text(
                        text = currentTier.prompt,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = dynamicColors.textPrimary,
                        lineHeight = 17.sp
                    )
                }
            }

            // Interactive Choices (minTouchTarget48)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                shuffledChoices.forEach { choice ->
                    val isSelected = selectedChoiceId == choice.id
                    val isCorrect = choice.isCorrect
                    val borderColor = when {
                        isSelected && isCorrect -> ArenaSuccessGreen
                        isSelected && !isCorrect -> ArenaDangerRed
                        else -> cardBg.copy(alpha = 0.8f)
                    }
                    val bgTint = when {
                        isSelected && isCorrect -> ArenaSuccessGreen.copy(alpha = 0.15f)
                        isSelected && !isCorrect -> ArenaDangerRed.copy(alpha = 0.15f)
                        else -> cardBg
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .minTouchTarget48()
                            .clip(RoundedCornerShape(12.dp))
                            .background(bgTint)
                            .border(1.2.dp, borderColor, RoundedCornerShape(12.dp))
                            .clickable(enabled = !isTierPassed) {
                                selectedChoiceId = choice.id
                                ArenaHaptics.tap(view, reduceMotion)
                                if (choice.isCorrect) {
                                    ArenaHaptics.success(view, reduceMotion)
                                    isTierPassed = true
                                    feedbackMessage = choice.explanation
                                    onActionSuccess()
                                } else {
                                    ArenaHaptics.error(view, reduceMotion)
                                    feedbackMessage = choice.explanation
                                    onDeductHeart()
                                }
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = when {
                                    isSelected && isCorrect -> Icons.Default.CheckCircle
                                    isSelected && !isCorrect -> Icons.Default.Warning
                                    else -> Icons.Default.Security
                                },
                                contentDescription = null,
                                tint = when {
                                    isSelected && isCorrect -> ArenaSuccessGreen
                                    isSelected && !isCorrect -> ArenaDangerRed
                                    else -> cyan
                                },
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = choice.text,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = dynamicColors.textPrimary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Feedback Banner
            AnimatedVisibility(visible = feedbackMessage != null, enter = fadeIn(), exit = fadeOut()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isTierPassed) ArenaSuccessGreen.copy(alpha = 0.12f)
                            else ArenaDangerRed.copy(alpha = 0.12f)
                        )
                        .border(
                            1.dp,
                            if (isTierPassed) ArenaSuccessGreen else ArenaDangerRed,
                            RoundedCornerShape(10.dp)
                        )
                        .padding(10.dp)
                ) {
                    Text(
                        text = feedbackMessage ?: "",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isTierPassed) ArenaSuccessGreen else ArenaDangerRed,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Advance Button
            if (isTierPassed) {
                Tactile3DButton(
                    title = if (currentTierIndex == tiers.size - 1) "Certify Complete Trust Chain ➔" else "Next Authority Tier ➔",
                    style = Tactile3DButtonColor.GREEN,
                    onClick = {
                        ArenaHaptics.tap(view, reduceMotion)
                        if (currentTierIndex < tiers.size - 1) {
                            currentTierIndex++
                            selectedChoiceId = null
                            feedbackMessage = null
                            isTierPassed = false
                        } else {
                            currentTierIndex++
                            onComplete()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            // All Tiers Completed
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(emerald.copy(alpha = 0.15f))
                    .border(1.5.dp, emerald, RoundedCornerShape(16.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = emerald,
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = "CHAIN OF TRUST CRYPTOGRAPHICALLY CERTIFIED!",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = emerald,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "From Root CA anchor down through intermediate signatures, leaf certificate SAN validation, and OCSP revocation verification — the host identity is verified.",
                        fontSize = 11.5.sp,
                        color = dynamicColors.textPrimary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Tactile3DButton(
                title = "Complete Level & Proceed ➔",
                style = Tactile3DButtonColor.GREEN,
                onClick = onComplete,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
