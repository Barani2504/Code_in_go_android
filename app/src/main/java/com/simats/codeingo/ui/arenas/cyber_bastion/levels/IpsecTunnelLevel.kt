package com.simats.codeingo.ui.arenas.cyber_bastion.levels

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.theme.pressScale

@Composable
fun IpsecTunnelLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    // Step 0: Raw Original IP Packet
    // Step 1: Prepend ESP Header (SPI + Anti-Replay Sequence Number)
    // Step 2: Encrypt Payload & Original IP with AES
    // Step 3: Append ESP Trailer (Padding + Next Header)
    // Step 4: Append ICV Authentication Tag & Prepend New Gateway IP
    var step by remember { mutableIntStateOf(0) }

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
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = AmberGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "IPSEC ESP TUNNEL MODE DISSECTOR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = AmberGold
                    )
                }
                Text(
                    text = when (step) {
                        0 -> "Step 1: Original internal IP packet (192.168.1.5 ➔ 192.168.2.10). In Tunnel Mode, the entire packet is encrypted!"
                        1 -> "Step 2: Prepend ESP Header: SPI (Security Parameters Index) and Sequence Number (Anti-Replay protection)."
                        2 -> "Step 3: Encrypt the Original IP Header + Payload with AES-256-CBC, shielding private routing topology."
                        3 -> "Step 4: Append ESP Trailer: Variable padding byte alignment and Next Header protocol tag (4 = IPv4)."
                        else -> "🎉 Tunnel Packet Complete! Appended ICV HMAC authentication tag and prepended outer New Gateway IP Header!"
                    },
                    fontSize = 12.5.sp,
                    color = dynamicColors.textPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp
                )
            }
        }

        // Live Packet Architecture Ribbon
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(if (isDark) Color(0xFF090E17) else Color(0xFFF8FAFC))
                .border(1.5.dp, if (step >= 4) DuolingoGreen else AmberGold.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ESP TUNNEL PACKET STRUCTURE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = SubtextGray
                    )
                    Text(
                        text = if (step >= 4) "TUNNEL SECURED 🔒" else "ASSEMBLING...",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (step >= 4) DuolingoGreen else AmberGold
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // New Outer IP
                    if (step >= 4) {
                        PacketBlock("New IP", "Gateway IP", Color(0xFF3B82F6))
                    }

                    // ESP Header
                    if (step >= 1) {
                        PacketBlock("ESP Header", "SPI & Seq# (Anti-Replay)", AmberGold)
                    }

                    // Encrypted Section (Orig IP + Payload)
                    if (step >= 2) {
                        PacketBlock("🔒 Encrypted", "Orig IP + Data", DuolingoGreen)
                    } else {
                        PacketBlock("Cleartext", "Orig IP + Data", Color(0xFFEF4444))
                    }

                    // ESP Trailer
                    if (step >= 3) {
                        PacketBlock("ESP Trailer", "Pad + NextHdr", Color(0xFFA855F7))
                    }

                    // ICV Auth Tag
                    if (step >= 4) {
                        PacketBlock("ESP Auth", "ICV MAC", Color(0xFF10B981))
                    }
                }
            }
        }

        // Feature Highlight Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (step >= 1) AmberGold.copy(alpha = 0.12f)
                    else Color(0xFF3B82F6).copy(alpha = 0.12f)
                )
                .border(
                    1.dp,
                    if (step >= 1) AmberGold.copy(alpha = 0.4f)
                    else Color(0xFF3B82F6).copy(alpha = 0.4f),
                    RoundedCornerShape(16.dp)
                )
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "ANTI-REPLAY WINDOW SECURITY:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = AmberGold
                )
                Text(
                    text = "The 32-bit Sequence Number in the ESP Header monotonically increments with every packet. The receiver maintains a 64-packet bitmap window to detect and drop duplicate packets injected by eavesdroppers!",
                    fontSize = 12.sp,
                    color = dynamicColors.textPrimary,
                    lineHeight = 17.sp
                )
            }
        }

        // Action Stepper Button
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (step >= 4) Brush.horizontalGradient(listOf(DuolingoGreen, Color(0xFF059669)))
                    else Brush.horizontalGradient(listOf(AmberGold, Color(0xFFD97706)))
                )
                .pressScale(targetScale = 0.96f)
                .clickable {
                    if (step < 4) {
                        step++
                        onActionSuccess()
                    } else {
                        onComplete()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (step >= 4) Icons.Default.Check else Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = when (step) {
                        0 -> "1. ATTACH ESP HEADER (SPI + SEQ#) →"
                        1 -> "2. ENCRYPT ENTIRE INNER PACKET →"
                        2 -> "3. ATTACH ESP TRAILER (PAD + NEXT) →"
                        3 -> "4. SEAL ICV & PREPEND NEW IP →"
                        else -> "TUNNEL FORGED! ADVANCE BASTION →"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Black,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
private fun PacketBlock(title: String, subtitle: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.20f))
            .border(1.2.dp, color, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Black, color = color)
        Text(text = subtitle, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SubtextGray)
    }
}
