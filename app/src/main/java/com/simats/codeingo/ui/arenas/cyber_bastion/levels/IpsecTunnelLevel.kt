package com.simats.codeingo.ui.arenas.cyber_bastion.levels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Security
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
import com.simats.codeingo.ui.components.Tactile3DButton
import com.simats.codeingo.ui.components.Tactile3DButtonColor
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.ArenaSuccessGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray

@Composable
fun IpsecTunnelLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    // RFC 4303 IPsec ESP Tunnel Mode Assembly:
    // Step 0: Original Internal Packet (198.51.100.5 -> 203.0.113.10, RFC 5737 Doc IP)
    // Step 1: Append ESP Trailer (Padding, Pad Length, Next Header=4 [IPv4])
    // Step 2: Encrypt Payload [Inner IP + ESP Trailer] with AEAD (AES-256-GCM)
    // Step 3: Prepend ESP Header (SPI + Anti-Replay Sequence Number) & Append ICV Auth Tag
    // Step 4: Prepend Outer New Gateway IP Header (192.0.2.1 -> 192.0.2.2)
    var step by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
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
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = AmberGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "IPSEC ESP TUNNEL MODE DISSECTOR (RFC 4303)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = AmberGold
                    )
                }
                Text(
                    text = when (step) {
                        0 -> "Step 1: Original Internal Packet (198.51.100.5 ➔ 203.0.113.10). In ESP Tunnel Mode, the entire original IP packet becomes the encrypted inner payload!"
                        1 -> "Step 2: Append ESP Trailer: Padding bytes + Pad Length + Next Header (4 = IPv4 encapsulation). The trailer is encrypted together with the payload!"
                        2 -> "Step 3: AEAD Encrypt: AES-256-GCM shields the inner IP header and payload from intermediate surveillance."
                        3 -> "Step 4: Prepend cleartext ESP Header (SPI + Sequence Number for Anti-Replay) and Append ICV Authentication Tag."
                        else -> "🎉 Tunnel Packet Complete! Prepended Outer New Gateway IP Header (192.0.2.1 ➔ 192.0.2.2). The packet is ready for public transit!"
                    },
                    fontSize = 12.sp,
                    color = dynamicColors.textPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }

        // Live Packet Architecture Ribbon
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(if (isDark) Color(0xFF090E17) else Color(0xFFF8FAFC))
                .border(1.5.dp, if (step >= 4) ArenaSuccessGreen else AmberGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RFC 4303 PACKET BYTE STREAM",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = SubtextGray
                    )
                    Text(
                        text = if (step >= 4) "TUNNEL SEALED 🔒" else "STEP $step / 4",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (step >= 4) ArenaSuccessGreen else AmberGold
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Outer Gateway IP (Cleartext)
                    if (step >= 4) {
                        PacketBlock("Outer New IP", "192.0.2.1 ➔ 192.0.2.2", Color(0xFF3B82F6), isEncrypted = false)
                    }

                    // ESP Header (Cleartext Authenticated)
                    if (step >= 3) {
                        PacketBlock("ESP Header", "SPI + Seq# (Anti-Replay)", AmberGold, isEncrypted = false)
                    }

                    // Encrypted Inner IP Packet
                    val isPayloadEncrypted = step >= 2
                    PacketBlock(
                        if (isPayloadEncrypted) "CIPHERTEXT: Inner IP" else "PLAINTEXT: Inner IP",
                        "198.51.100.5 ➔ 203.0.113.10",
                        if (isPayloadEncrypted) Color(0xFFFF2A6D) else Color(0xFF10B981),
                        isEncrypted = isPayloadEncrypted
                    )

                    // ESP Trailer
                    if (step >= 1) {
                        PacketBlock(
                            if (isPayloadEncrypted) "CIPHERTEXT: Trailer" else "ESP Trailer",
                            "Pad + NextHdr=4",
                            if (isPayloadEncrypted) Color(0xFFFF2A6D) else AmberGold,
                            isEncrypted = isPayloadEncrypted
                        )
                    }

                    // ICV Authentication Tag
                    if (step >= 3) {
                        PacketBlock("ICV Auth Tag", "AEAD GMAC Tag (16B)", ArenaSuccessGreen, isEncrypted = false)
                    }
                }

                // Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "■ Blue/Gold: Cleartext Headers", fontSize = 9.5.sp, color = SubtextGray)
                    Text(text = "■ Pink: Encrypted Payload", fontSize = 9.5.sp, color = Color(0xFFFF2A6D))
                    Text(text = "■ Green: ICV Tag", fontSize = 9.5.sp, color = ArenaSuccessGreen)
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Tactile3DButton(
            title = when (step) {
                0 -> "1. Append ESP Trailer (NextHdr=4) ➔"
                1 -> "2. AEAD Encrypt [Inner IP + Trailer] 🔒"
                2 -> "3. Add ESP Header (SPI/Seq) & ICV Tag ➔"
                3 -> "4. Prepend Outer Gateway IP (192.0.2.1) ➔"
                else -> "Deploy Encapsulated Tunnel Packet ➔"
            },
            style = if (step >= 4) Tactile3DButtonColor.GREEN else Tactile3DButtonColor.AMBER,
            onClick = {
                if (step < 4) {
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
            Text(text = title, fontSize = 10.sp, fontWeight = FontWeight.Black, color = color)
            Text(text = subtitle, fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}
