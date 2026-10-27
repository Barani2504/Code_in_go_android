package com.simats.codeingo.ui.arenas.cyber_bastion.levels

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
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.components.Tactile3DButton
import com.simats.codeingo.ui.components.Tactile3DButtonColor
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.ArenaDangerRed
import com.simats.codeingo.ui.theme.ArenaSuccessGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
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

    // RFC 5737 Documentation IP Packet Stream (192.0.2.0/24, 198.51.100.0/24, 203.0.113.0/24)
    val packetQueue = remember {
        listOf(
            FiveTuplePacket(
                srcIp = "192.0.2.10",
                srcPort = 49152,
                dstIp = "198.51.100.5",
                dstPort = 443,
                protocol = "TCP",
                tcpFlag = "SYN (Outbound)",
                shouldAllow = true,
                explanation = "Legitimate outbound TLS connection to port 443. ALLOW creates connection tracking entry."
            ),
            FiveTuplePacket(
                srcIp = "203.0.113.99",
                srcPort = 33890,
                dstIp = "192.0.2.10",
                dstPort = 23,
                protocol = "TCP",
                tcpFlag = "SYN (Inbound Scan)",
                shouldAllow = false,
                explanation = "Unsolicited inbound connection scan to port 23 (Telnet). Default-deny: DROP immediately!"
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
                explanation = "Return packet matches established conntrack entry (192.0.2.10:49152 ⇄ 198.51.100.5:443). ALLOW!"
            )
        )
    }

    val conntrackTable = remember { mutableStateListOf<ConntrackEntry>() }
    var currentPacketIndex by remember { mutableIntStateOf(0) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    val currentPacket = packetQueue.getOrNull(currentPacketIndex)
    val isFinished = currentPacketIndex >= packetQueue.size

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "STATEFUL 5-TUPLE FIREWALL SWITCHBOARD",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Dynamic Connection Tracking Table (conntrack) Inspection",
                fontSize = 10.5.sp,
                color = dynamicColors.textSecondary,
                textAlign = TextAlign.Center
            )
        }

        // Explanatory Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(dynamicColors.cardBorder.copy(alpha = 0.2f))
                .border(1.dp, AmberGold.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = AmberGold, modifier = Modifier.size(16.dp))
                    Text(text = "Stateful vs Stateless Filtering:", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = AmberGold)
                }
                Text(
                    text = "A stateless firewall only inspects individual headers. A stateful firewall tracks the 5-tuple (Src IP/Port, Dst IP/Port, Protocol) and TCP state. Inbound return packets matching an active outbound connection are permitted automatically!",
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
                .background(dynamicColors.cardBorder.copy(alpha = 0.25f))
                .padding(10.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "CONNTRACK TABLE (ACTIVE SESSIONS: ${conntrackTable.size})",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = SubtextGray
                )
                if (conntrackTable.isEmpty()) {
                    Text(text = "No active states (table empty)", fontSize = 10.sp, color = SubtextGray)
                } else {
                    conntrackTable.forEach { entry ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "${entry.clientEndpoint} ⇄ ${entry.serverEndpoint}", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = dynamicColors.textPrimary)
                            Text(text = entry.state, fontSize = 10.sp, fontWeight = FontWeight.Black, color = ArenaSuccessGreen)
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
                .background(dynamicColors.cardBackground)
                .border(1.2.dp, AmberGold, RoundedCornerShape(14.dp))
                .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "INCOMING PACKET (${currentPacketIndex + 1} / ${packetQueue.size})",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = AmberGold
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "SOURCE (IP:Port):", fontSize = 10.sp, color = SubtextGray)
                        Text(text = "${currentPacket.srcIp}:${currentPacket.srcPort}", fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "DESTINATION (IP:Port):", fontSize = 10.sp, color = SubtextGray)
                        Text(text = "${currentPacket.dstIp}:${currentPacket.dstPort}", fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "FLAGS & PROTOCOL:", fontSize = 10.sp, color = SubtextGray)
                        Text(text = "${currentPacket.protocol} [${currentPacket.tcpFlag}]", fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Black, color = AmberGold)
                    }
                }
            }

            // Firewall Decision Buttons (48dp height minimum)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Tactile3DButton(
                    title = "ALLOW ✓",
                    style = Tactile3DButtonColor.GREEN,
                    onClick = {
                        if (currentPacket.shouldAllow) {
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
                            feedbackMessage = "✓ Allowed: ${currentPacket.explanation}"
                            currentPacketIndex++
                            if (currentPacketIndex >= packetQueue.size) onComplete()
                        } else {
                            onDeductHeart()
                            feedbackMessage = "❌ Breached: Should have been dropped! ${currentPacket.explanation}"
                        }
                    },
                    modifier = Modifier.weight(1f)
                )

                Tactile3DButton(
                    title = "DROP ⛔",
                    style = Tactile3DButtonColor.AMBER,
                    onClick = {
                        if (!currentPacket.shouldAllow) {
                            onActionSuccess()
                            feedbackMessage = "✓ Dropped: ${currentPacket.explanation}"
                            currentPacketIndex++
                            if (currentPacketIndex >= packetQueue.size) onComplete()
                        } else {
                            onDeductHeart()
                            feedbackMessage = "❌ Mistake: Valid traffic blocked! ${currentPacket.explanation}"
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (feedbackMessage != null) {
            Text(
                text = feedbackMessage!!,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (feedbackMessage!!.startsWith("✓")) ArenaSuccessGreen else ArenaDangerRed,
                textAlign = TextAlign.Center
            )
        }
    }
}
