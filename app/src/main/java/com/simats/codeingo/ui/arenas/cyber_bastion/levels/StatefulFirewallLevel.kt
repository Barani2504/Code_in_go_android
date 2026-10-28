package com.simats.codeingo.ui.arenas.cyber_bastion.levels

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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

// True 5-tuple: Source IP, Source Port, Destination IP, Destination Port, Protocol
data class FiveTuplePacket(
    val srcIp: String,
    val srcPort: Int,
    val dstIp: String,
    val dstPort: Int,
    val protocol: String,
    val tcpFlag: String,
    val shouldAllow: Boolean,
    val explanation: String
)

data class ConntrackEntry(
    val clientEndpoint: String,
    val serverEndpoint: String,
    val state: String
)

@Composable
fun StatefulFirewallLevel(
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

    // RFC 5737 Documentation IP Packet Stream (192.0.2.0/24, 198.51.100.0/24, 203.0.113.0/24)
    val packetQueue = remember {
        listOf(
            FiveTuplePacket(
                srcIp = "192.0.2.10",
                srcPort = 49152,
                dstIp = "198.51.100.5",
                dstPort = 443,
                protocol = "TCP",
                tcpFlag = "SYN (Outbound Init)",
                shouldAllow = true,
                explanation = "Legitimate outbound TLS connection to port 443. ALLOW creates connection tracking (conntrack) state."
            ),
            FiveTuplePacket(
                srcIp = "203.0.113.99",
                srcPort = 33890,
                dstIp = "192.0.2.10",
                dstPort = 23,
                protocol = "TCP",
                tcpFlag = "SYN (Inbound Scan)",
                shouldAllow = false,
                explanation = "Unsolicited inbound connection scan to port 23 (Telnet). Default-deny perimeter: DROP immediately!"
            ),
            FiveTuplePacket(
                srcIp = "203.0.113.88",
                srcPort = 44550,
                dstIp = "192.0.2.10",
                dstPort = 445,
                protocol = "TCP",
                tcpFlag = "ACK (Inbound Probe)",
                shouldAllow = false,
                explanation = "Out-of-state unsolicited ACK packet. No matching entry in connection tracking table: DROP!"
            ),
            FiveTuplePacket(
                srcIp = "198.51.100.5",
                srcPort = 443,
                dstIp = "192.0.2.10",
                dstPort = 49152,
                protocol = "TCP",
                tcpFlag = "SYN+ACK (Return Traffic)",
                shouldAllow = true,
                explanation = "Return packet matches established conntrack entry (192.0.2.10:49152 ⇄ 198.51.100.5:443). Stateful ALLOW!"
            )
        )
    }

    val conntrackTable = remember { mutableStateListOf<ConntrackEntry>() }
    var currentPacketIndex by remember { mutableIntStateOf(0) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var lastActionSuccess by remember { mutableStateOf(false) }

    val currentPacket = packetQueue.getOrNull(currentPacketIndex)
    val isFinished = currentPacketIndex >= packetQueue.size

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
                    text = "STATEFUL 5-TUPLE FIREWALL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = emerald,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Dynamic Conntrack Inspection Engine",
                    fontSize = 10.sp,
                    color = dynamicColors.textSecondary
                )
            }
            ArenaStatusBadge(
                type = if (isFinished) ArenaBadgeType.SUCCESS else ArenaBadgeType.INFO,
                label = if (isFinished) "PERIMETER SECURE" else "PACKET ${minOf(currentPacketIndex + 1, packetQueue.size)}/${packetQueue.size}"
            )
        }

        // Explanatory Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(cardBg)
                .border(1.dp, emerald.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = emerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Stateful vs Stateless Filtering:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = emerald
                    )
                }
                Text(
                    text = "A stateless filter only inspects single packet headers. A stateful firewall tracks the complete 5-tuple (Src IP/Port, Dst IP/Port, Protocol) and TCP state machine. Inbound packets matching an active outbound connection are permitted automatically!",
                    fontSize = 11.sp,
                    color = dynamicColors.textPrimary,
                    lineHeight = 15.sp
                )
            }
        }

        // Live Connection Tracking Table Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(cardBg)
                .border(1.2.dp, cyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CONNTRACK TABLE (ACTIVE: ${conntrackTable.size})",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = cyan
                    )
                    Text(
                        text = if (conntrackTable.isNotEmpty()) "STATE: ESTABLISHED" else "TABLE IDLE",
                        fontSize = 8.5.sp,
                        fontFamily = FontFamily.Monospace,
                        color = if (conntrackTable.isNotEmpty()) ArenaSuccessGreen else SubtextGray
                    )
                }

                if (conntrackTable.isEmpty()) {
                    Text(
                        text = "No active conntrack sessions recorded",
                        fontSize = 10.sp,
                        color = SubtextGray,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                } else {
                    conntrackTable.forEach { entry ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(cyan.copy(alpha = 0.08f))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${entry.clientEndpoint} ⇄ ${entry.serverEndpoint}",
                                fontSize = 9.5.sp,
                                fontFamily = FontFamily.Monospace,
                                color = dynamicColors.textPrimary
                            )
                            Text(
                                text = entry.state,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Black,
                                color = ArenaSuccessGreen
                            )
                        }
                    }
                }
            }
        }

        if (currentPacket != null) {
            // Packet Inspection Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(cardBg)
                    .border(1.5.dp, emerald, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "INCOMING 5-TUPLE PROBE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = emerald
                        )
                        Text(
                            text = "PKT ${currentPacketIndex + 1} OF ${packetQueue.size}",
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = cyan
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "SOURCE (IP:Port):", fontSize = 10.sp, color = SubtextGray)
                        Text(
                            text = "${currentPacket.srcIp}:${currentPacket.srcPort}",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = dynamicColors.textPrimary
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "DESTINATION (IP:Port):", fontSize = 10.sp, color = SubtextGray)
                        Text(
                            text = "${currentPacket.dstIp}:${currentPacket.dstPort}",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = dynamicColors.textPrimary
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "PROTOCOL & FLAGS:", fontSize = 10.sp, color = SubtextGray)
                        Text(
                            text = "${currentPacket.protocol} [${currentPacket.tcpFlag}]",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            color = if (currentPacket.shouldAllow) emerald else ArenaDangerRed
                        )
                    }
                }
            }

            // Firewall Decision Buttons (minTouchTarget48)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Tactile3DButton(
                    title = "ALLOW ✓",
                    style = Tactile3DButtonColor.GREEN,
                    onClick = {
                        ArenaHaptics.tap(view, reduceMotion)
                        if (currentPacket.shouldAllow) {
                            ArenaHaptics.success(view, reduceMotion)
                            lastActionSuccess = true
                            onActionSuccess()
                            if (currentPacketIndex == 0) {
                                conntrackTable.add(
                                    ConntrackEntry(
                                        "${currentPacket.srcIp}:${currentPacket.srcPort}",
                                        "${currentPacket.dstIp}:${currentPacket.dstPort}",
                                        "ESTABLISHED"
                                    )
                                )
                            }
                            feedbackMessage = "✓ ALLOWED: ${currentPacket.explanation}"
                            currentPacketIndex++
                            if (currentPacketIndex >= packetQueue.size) onComplete()
                        } else {
                            ArenaHaptics.error(view, reduceMotion)
                            lastActionSuccess = false
                            onDeductHeart()
                            feedbackMessage = "❌ PERIMETER BREACH: Rogue traffic allowed! ${currentPacket.explanation}"
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .minTouchTarget48()
                )

                Tactile3DButton(
                    title = "DROP ⛔",
                    style = Tactile3DButtonColor.AMBER,
                    onClick = {
                        ArenaHaptics.tap(view, reduceMotion)
                        if (!currentPacket.shouldAllow) {
                            ArenaHaptics.success(view, reduceMotion)
                            lastActionSuccess = true
                            onActionSuccess()
                            feedbackMessage = "✓ DROPPED: ${currentPacket.explanation}"
                            currentPacketIndex++
                            if (currentPacketIndex >= packetQueue.size) onComplete()
                        } else {
                            ArenaHaptics.error(view, reduceMotion)
                            lastActionSuccess = false
                            onDeductHeart()
                            feedbackMessage = "❌ FALSE POSITIVE: Legitimate traffic dropped! ${currentPacket.explanation}"
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .minTouchTarget48()
                )
            }
        } else {
            // Completed State
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
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = emerald,
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = "PERIMETER FIREWALL DEFENSE VERIFIED!",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = emerald,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "All 4 probes classified accurately. Legitimate return packets allowed via conntrack state while unsolicited port scans and out-of-order ACK probes were blocked.",
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

        // Live Feedback Message
        AnimatedVisibility(visible = feedbackMessage != null, enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (lastActionSuccess) ArenaSuccessGreen.copy(alpha = 0.12f)
                        else ArenaDangerRed.copy(alpha = 0.12f)
                    )
                    .border(
                        1.dp,
                        if (lastActionSuccess) ArenaSuccessGreen else ArenaDangerRed,
                        RoundedCornerShape(10.dp)
                    )
                    .padding(10.dp)
            ) {
                Text(
                    text = feedbackMessage ?: "",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (lastActionSuccess) ArenaSuccessGreen else ArenaDangerRed,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
