package com.simats.codeingo.ui.arenas.cyber_bastion.levels

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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

private data class IpsecChoice(
    val id: String,
    val text: String,
    val isCorrect: Boolean,
    val rationale: String
)

private data class IpsecStage(
    val stageIndex: Int,
    val stageTitle: String,
    val prompt: String,
    val explanation: String,
    val choices: List<IpsecChoice>
)

@Composable
fun IpsecTunnelLevel(
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

    val stages = remember {
        listOf(
            IpsecStage(
                stageIndex = 0,
                stageTitle = "STAGE 1: INNER ENCAPSULATION",
                prompt = "A host at 198.51.100.5 transmits data to 203.0.113.10. In RFC 4303 Tunnel Mode, what constitutes the protected inner payload?",
                explanation = "In ESP Tunnel Mode, the entire original IP packet (header + payload) is encapsulated as the inner encrypted payload.",
                choices = listOf(
                    IpsecChoice(
                        "c0_correct",
                        "Entire Original IP Packet (Headers + TCP/Data)",
                        true,
                        "Correct! Tunnel mode protects the whole inner IP datagram to shield private network topologies."
                    ),
                    IpsecChoice(
                        "c0_w1",
                        "Transport Mode: Only TCP Header + Data (Original IP exposed)",
                        false,
                        "Incorrect! That is Transport Mode. Tunnel mode encapsulates the complete original IP datagram."
                    ),
                    IpsecChoice(
                        "c0_w2",
                        "Application Data Only (Discard all lower headers)",
                        false,
                        "Incorrect! IPsec operates at Network Layer 3, not Application Layer."
                    )
                )
            ),
            IpsecStage(
                stageIndex = 1,
                stageTitle = "STAGE 2: ESP TRAILER ASSEMBLE",
                prompt = "Before AES-256-GCM AEAD encryption, the ESP Trailer is appended. What mandatory fields does RFC 4303 require here?",
                explanation = "The ESP Trailer consists of Padding, Pad Length (1 byte), and Next Header (1 byte, value 4 for IPv4).",
                choices = listOf(
                    IpsecChoice(
                        "c1_correct",
                        "Padding Bytes + Pad Length + Next Header (4 = IPv4)",
                        true,
                        "Correct! Next Header indicates the encapsulated protocol (4 for IPv4), and pad bytes align block boundaries."
                    ),
                    IpsecChoice(
                        "c1_w1",
                        "SPI (Security Parameter Index) + Sequence Counter",
                        false,
                        "Incorrect! SPI and Sequence Number reside in the ESP Header, not the trailer."
                    ),
                    IpsecChoice(
                        "c1_w2",
                        "ICV (Integrity Check Value) Authentication Tag",
                        false,
                        "Incorrect! ICV auth tag follows after the trailer in cleartext authenticated space."
                    )
                )
            ),
            IpsecStage(
                stageIndex = 2,
                stageTitle = "STAGE 3: AEAD ENCRYPTION BOUNDARY",
                prompt = "The gateway applies AES-256-GCM AEAD encryption. Exactly which boundary of the packet is shielded in ciphertext?",
                explanation = "Confidentiality covers the Inner IP Packet and the ESP Trailer. Outer headers remain readable for routing.",
                choices = listOf(
                    IpsecChoice(
                        "c2_correct",
                        "[Inner IP Header + Payload + ESP Trailer]",
                        true,
                        "Correct! The inner packet and trailer are encrypted together, concealing both data and internal host addresses."
                    ),
                    IpsecChoice(
                        "c2_w1",
                        "The entire datagram including Outer Gateway IP",
                        false,
                        "Incorrect! If outer IP headers were encrypted, public Internet routers could not route the packet!"
                    ),
                    IpsecChoice(
                        "c2_w2",
                        "Only the TCP Payload bytes (leaving inner IP visible)",
                        false,
                        "Incorrect! In Tunnel mode, internal IP addresses are strictly concealed inside ciphertext."
                    )
                )
            ),
            IpsecStage(
                stageIndex = 3,
                stageTitle = "STAGE 4: ANTI-REPLAY ESP HEADER",
                prompt = "An attacker captures valid packets and re-injects them. How does the cleartext ESP Header defeat replay attacks?",
                explanation = "A strictly monotonically increasing Sequence Number verified against a sliding acceptance window prevents packet replay.",
                choices = listOf(
                    IpsecChoice(
                        "c3_correct",
                        "Monotonically Increasing 64-bit Seq# with Sliding Window",
                        true,
                        "Correct! RFC 4303 uses an anti-replay window; duplicate or out-of-window sequence numbers are instantly dropped."
                    ),
                    IpsecChoice(
                        "c3_w1",
                        "Cryptographic NTP Timestamps on every gateway",
                        false,
                        "Incorrect! NTP synchronization is vulnerable to clock skew and replay within the sync window."
                    ),
                    IpsecChoice(
                        "c3_w2",
                        "Random Nonce generated per packet without state",
                        false,
                        "Incorrect! Pure random nonces cannot detect replay without tracking historical nonces."
                    )
                )
            ),
            IpsecStage(
                stageIndex = 4,
                stageTitle = "STAGE 5: GATEWAY ROUTING ENCAPSULATION",
                prompt = "Finally, which cleartext IP addresses are placed into the outer header to traverse the public Internet?",
                explanation = "The outer IP header carries the public gateway endpoint addresses: 192.0.2.1 ➔ 192.0.2.2 with protocol 50 (ESP).",
                choices = listOf(
                    IpsecChoice(
                        "c4_correct",
                        "Public Gateway Endpoints: 192.0.2.1 ➔ 192.0.2.2 (Protocol 50)",
                        true,
                        "Correct! Public routers forward between gateway interfaces. Inner private IPs are completely invisible!"
                    ),
                    IpsecChoice(
                        "c4_w1",
                        "Original Host Endpoints: 198.51.100.5 ➔ 203.0.113.10",
                        false,
                        "Incorrect! Private RFC 1918/RFC 5737 internal IPs cannot be routed across the public Internet."
                    ),
                    IpsecChoice(
                        "c4_w2",
                        "Subnet Broadcast: 255.255.255.255",
                        false,
                        "Incorrect! Broadcast packets are non-routable on the Internet backbone."
                    )
                )
            )
        )
    }

    var currentStageIndex by remember { mutableIntStateOf(0) }
    var selectedChoiceId by remember { mutableStateOf<String?>(null) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var isStagePassed by remember { mutableStateOf(false) }

    val currentStage = stages.getOrNull(currentStageIndex)
    val isCompleted = currentStageIndex >= stages.size

    val shuffledChoices = remember(currentStageIndex) {
        currentStage?.choices?.shuffled() ?: emptyList()
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
                    text = "IPSEC ESP TUNNEL DISSECTOR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = emerald,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "RFC 4303 Packet Frame Architecture",
                    fontSize = 10.sp,
                    color = dynamicColors.textSecondary
                )
            }
            ArenaStatusBadge(
                type = if (isCompleted) ArenaBadgeType.ENCRYPTED else ArenaBadgeType.INFO,
                label = if (isCompleted) "TUNNEL SEALED" else "PHASE ${currentStageIndex + 1}/5"
            )
        }

        // Live Packet Byte Stream Ribbon
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(cardBg)
                .border(1.5.dp, if (isCompleted) emerald else cyan.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RFC 4303 WIRE ANATOMY",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = cyan
                    )
                    Text(
                        text = if (currentStageIndex >= 3) "ENC: AES-256-GCM" else "PLAINTEXT STACK",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (currentStageIndex >= 3) emerald else SubtextGray
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Outer Gateway IP Header
                    if (currentStageIndex >= 5 || (currentStageIndex == 4 && isStagePassed)) {
                        PacketBlock("Outer IP (Proto 50)", "192.0.2.1 ➔ 192.0.2.2", cyan, isEncrypted = false)
                    }

                    // ESP Header (SPI + Seq)
                    if (currentStageIndex >= 4 || (currentStageIndex == 3 && isStagePassed)) {
                        PacketBlock("ESP Header", "SPI:0x9A3F | Seq#1042", emerald, isEncrypted = false)
                    }

                    // Encrypted Inner IP Packet
                    val isInnerEncrypted = currentStageIndex >= 3 || (currentStageIndex == 2 && isStagePassed)
                    PacketBlock(
                        if (isInnerEncrypted) "🔒 CIPHERTEXT Inner IP" else "PLAINTEXT Inner IP",
                        "198.51.100.5 ➔ 203.0.113.10",
                        if (isInnerEncrypted) Color(0xFFF43F5E) else emerald,
                        isEncrypted = isInnerEncrypted
                    )

                    // ESP Trailer
                    if (currentStageIndex >= 2 || (currentStageIndex == 1 && isStagePassed)) {
                        PacketBlock(
                            if (isInnerEncrypted) "🔒 CIPHERTEXT Trailer" else "ESP Trailer",
                            "Pad(0x01) | PadLen:1 | Next:4",
                            if (isInnerEncrypted) Color(0xFFF43F5E) else cyan,
                            isEncrypted = isInnerEncrypted
                        )
                    }

                    // ICV Authentication Tag
                    if (currentStageIndex >= 4 || (currentStageIndex == 3 && isStagePassed)) {
                        PacketBlock("ICV Auth Tag", "GMAC Tag (16B)", emerald, isEncrypted = false)
                    }
                }
            }
        }

        if (currentStage != null && !isCompleted) {
            // Stage Prompt Card
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
                        text = currentStage.stageTitle,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = emerald
                    )
                    Text(
                        text = currentStage.prompt,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = dynamicColors.textPrimary,
                        lineHeight = 17.sp
                    )
                }
            }

            // Interactive Choices (Each with minTouchTarget48)
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
                            .clickable(enabled = !isStagePassed) {
                                selectedChoiceId = choice.id
                                ArenaHaptics.tap(view, reduceMotion)
                                if (choice.isCorrect) {
                                    ArenaHaptics.success(view, reduceMotion)
                                    isStagePassed = true
                                    feedbackMessage = choice.rationale
                                    onActionSuccess()
                                } else {
                                    ArenaHaptics.error(view, reduceMotion)
                                    feedbackMessage = choice.rationale
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
                            if (isStagePassed) ArenaSuccessGreen.copy(alpha = 0.12f)
                            else ArenaDangerRed.copy(alpha = 0.12f)
                        )
                        .border(
                            1.dp,
                            if (isStagePassed) ArenaSuccessGreen else ArenaDangerRed,
                            RoundedCornerShape(10.dp)
                        )
                        .padding(10.dp)
                ) {
                    Text(
                        text = feedbackMessage ?: "",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isStagePassed) ArenaSuccessGreen else ArenaDangerRed,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Next Stage / Continue Button
            if (isStagePassed) {
                Tactile3DButton(
                    title = if (currentStageIndex == stages.size - 1) "Finalize Encapsulated IPsec Tunnel ➔" else "Next Protocol Stage ➔",
                    style = Tactile3DButtonColor.GREEN,
                    onClick = {
                        ArenaHaptics.tap(view, reduceMotion)
                        if (currentStageIndex < stages.size - 1) {
                            currentStageIndex++
                            selectedChoiceId = null
                            feedbackMessage = null
                            isStagePassed = false
                        } else {
                            currentStageIndex++
                            onComplete()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            // Completed State Card
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
                        text = "IPSEC ESP TUNNEL FULLY ASSEMBLED!",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = emerald,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "RFC 4303 tunnel mode encapsulates private traffic between gateways 192.0.2.1 ⇄ 192.0.2.2 with AES-256-GCM AEAD encryption and anti-replay defense.",
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

@Composable
private fun PacketBlock(title: String, subtitle: String, color: Color, isEncrypted: Boolean) {
    Box(
        modifier = Modifier
            .height(54.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.18f))
            .border(1.2.dp, color, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, fontSize = 9.5.sp, fontWeight = FontWeight.Black, color = color)
            Text(
                text = subtitle,
                fontSize = 8.5.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
