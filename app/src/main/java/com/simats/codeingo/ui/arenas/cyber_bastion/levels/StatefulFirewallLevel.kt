package com.simats.codeingo.ui.arenas.cyber_bastion.levels

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.theme.pressScale

data class NetworkPacket(
    val srcIp: String,
    val dstIp: String,
    val protocol: String,
    val port: Int,
    val service: String,
    val tcpFlag: String,
    val shouldAllow: Boolean,
    val threatExplanation: String
)

@Composable
fun StatefulFirewallLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    val packetQueue = remember {
        listOf(
            NetworkPacket(
                srcIp = "192.168.1.105",
                dstIp = "10.0.0.1",
                protocol = "TCP",
                port = 443,
                service = "HTTPS (TLS)",
                tcpFlag = "SYN",
                shouldAllow = true,
                threatExplanation = "Valid encrypted web traffic on standard TLS port 443."
            ),
            NetworkPacket(
                srcIp = "45.33.32.1",
                dstIp = "10.0.0.1",
                protocol = "TCP",
                port = 23,
                service = "Telnet (Cleartext)",
                tcpFlag = "SYN",
                shouldAllow = false,
                threatExplanation = "Insecure cleartext Telnet port scan detected. Drop immediately!"
            ),
            NetworkPacket(
                srcIp = "185.220.101.5",
                dstIp = "10.0.0.1",
                protocol = "TCP",
                port = 445,
                service = "SMB (EternalBlue)",
                tcpFlag = "SYN",
                shouldAllow = false,
                threatExplanation = "Malicious port 445 SMB scan attempting lateral exploit. Drop!"
            ),
            NetworkPacket(
                srcIp = "192.168.1.105",
                dstIp = "10.0.0.1",
                protocol = "TCP",
                port = 443,
                service = "HTTPS (TLS)",
                tcpFlag = "ESTABLISHED",
                shouldAllow = true,
                threatExplanation = "Stateful connection tracked in state table. Allow throughput!"
            )
        )
    }

    var currentPacketIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var feedbackMessage by remember { androidx.compose.runtime.mutableStateOf<String?>(null) }
    var isFeedbackSuccess by remember { androidx.compose.runtime.mutableStateOf(true) }

    val isFinished = currentPacketIndex >= packetQueue.size
    val currentPacket = packetQueue.getOrNull(currentPacketIndex)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = AmberGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "STATEFUL 5-TUPLE FIREWALL SWITCHBOARD",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = AmberGold
                    )
                }
                Text(
                    text = "Inspect incoming packets using the 5-Tuple: [Src IP, Dst IP, Protocol, Port, State]. Allow legitimate TLS port 443 traffic and DROP malicious scans!",
                    fontSize = 12.5.sp,
                    color = dynamicColors.textPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp
                )
            }
        }

        // Packet Counter & Progress
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "INCOMING PACKET STREAM",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = SubtextGray
            )
            Text(
                text = "PACKET ${minOf(currentPacketIndex + 1, packetQueue.size)} OF ${packetQueue.size}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = AmberGold
            )
        }

        // Live Packet Header Card
        if (currentPacket != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isDark) Color(0xFF090E17) else Color(0xFFF8FAFC))
                    .border(1.5.dp, AmberGold.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "5-TUPLE HEADER",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = AmberGold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AmberGold.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "FLAG: ${currentPacket.tcpFlag}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = AmberGold
                            )
                        }
                    }

                    // 5-Tuple Table
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        TupleRow("SOURCE IP", currentPacket.srcIp)
                        TupleRow("DESTINATION IP", currentPacket.dstIp)
                        TupleRow("PROTOCOL", currentPacket.protocol)
                        TupleRow("DEST PORT", "${currentPacket.port} (${currentPacket.service})")
                    }
                }
            }
        }

        // Feedback Banner
        feedbackMessage?.let { msg ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isFeedbackSuccess) DuolingoGreen.copy(alpha = 0.15f)
                        else DuolingoRed.copy(alpha = 0.15f)
                    )
                    .border(
                        1.dp,
                        if (isFeedbackSuccess) DuolingoGreen else DuolingoRed,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = msg,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isFeedbackSuccess) DuolingoGreen else DuolingoRed,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Interactive Firewall Action Buttons (Allow / Drop)
        if (!isFinished && currentPacket != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Drop Button (Red)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFEF4444))
                        .pressScale()
                        .clickable {
                            if (!currentPacket.shouldAllow) {
                                score++
                                isFeedbackSuccess = true
                                feedbackMessage = "MALICIOUS PACKET BLOCKED! 🛡️ ${currentPacket.threatExplanation}"
                                onActionSuccess()
                            } else {
                                isFeedbackSuccess = false
                                feedbackMessage = "FALSE POSITIVE! Legitimate traffic was blocked."
                                onDeductHeart()
                            }
                            currentPacketIndex++
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Block, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Text(text = "DROP PACKET", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                }

                // Allow Button (Green)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(DuolingoGreen)
                        .pressScale()
                        .clickable {
                            if (currentPacket.shouldAllow) {
                                score++
                                isFeedbackSuccess = true
                                feedbackMessage = "LEGITIMATE PACKET ALLOWED! ✓ ${currentPacket.threatExplanation}"
                                onActionSuccess()
                            } else {
                                isFeedbackSuccess = false
                                feedbackMessage = "BREACH! Malicious scan bypassed the firewall."
                                onDeductHeart()
                            }
                            currentPacketIndex++
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Text(text = "ALLOW PACKET", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.Black)
                    }
                }
            }
        } else {
            // Victory Button
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.horizontalGradient(listOf(DuolingoGreen, Color(0xFF059669))))
                    .pressScale(targetScale = 0.96f)
                    .clickable {
                        onComplete()
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    Text(
                        text = "FIREWALL DEFENDED! ADVANCE BASTION →",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = Color.Black,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TupleRow(key: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = key, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SubtextGray)
        Text(
            text = value,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            color = LocalDynamicThemeColors.current.textPrimary
        )
    }
}
