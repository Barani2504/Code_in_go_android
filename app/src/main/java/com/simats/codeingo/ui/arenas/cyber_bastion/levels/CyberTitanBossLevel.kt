package com.simats.codeingo.ui.arenas.cyber_bastion.levels

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

@Composable
fun CyberTitanBossLevel(
    onDefeatBoss: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    var bossHP by remember { mutableIntStateOf(100) }
    var currentPhase by remember { mutableIntStateOf(1) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var feedback by remember { mutableStateOf("The Cyber Titan launches multi-vector network assaults! Defend the Cyber Bastion!") }

    val phaseData = when (currentPhase) {
        1 -> Triple(
            "SYN Flood: Inbound half-open connection backlog saturated! Deploy countermeasure!",
            "Deploy SYN Cookies (Stateless Handshake)",
            listOf("Deploy SYN Cookies (Stateless Handshake)", "Disable Ingress Firewall", "Accept all half-open sockets", "Send ICMP Redirect")
        )
        2 -> Triple(
            "MITM Rogue Certificate: Compromised intermediate CA detected in path! Deploy defense!",
            "Enforce Certificate Transparency & OCSP",
            listOf("Enforce Certificate Transparency & OCSP", "Accept Self-Signed Certificate", "Disable TLS Verification", "Downgrade to HTTP")
        )
        else -> Triple(
            "Data Exfiltration: Attacker probes open ports on perimeter! Deploy network policy!",
            "Stateful Deep Packet Inspection (DPI)",
            listOf("Stateful Deep Packet Inspection (DPI)", "Allow Any Any Traffic", "Disable WAF Rules", "Forward all traffic to DMZ")
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "👾 THE CYBER TITAN", fontSize = 16.sp, color = DuolingoRed, fontWeight = FontWeight.Black)
            Text(text = "PHASE $currentPhase / 3", fontSize = 12.sp, fontWeight = FontWeight.Black, color = AmberGold)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(14.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(dynamicColors.cardBorder.copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(bossHP / 100f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(DuolingoRed)
            )
        }
        Text(text = "$bossHP / 100 HP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textSecondary)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(dynamicColors.cardBorder.copy(alpha = 0.2f))
                .border(1.5.dp, AmberGold.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = phaseData.first, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary, textAlign = TextAlign.Center)
                Text(text = feedback, fontSize = 11.sp, color = AmberGold, textAlign = TextAlign.Center)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            phaseData.third.chunked(2).forEach { rowOptions ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    rowOptions.forEach { opt ->
                        val isCorrect = opt == phaseData.second
                        val isSelected = selectedAnswer == opt
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) (if (isCorrect) DuolingoGreen else DuolingoRed)
                                    else dynamicColors.cardBorder.copy(alpha = 0.25f)
                                )
                                .border(1.dp, if (isSelected) Color.White else AmberGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .clickable {
                                    selectedAnswer = opt
                                    if (isCorrect) {
                                        onActionSuccess()
                                        val newHP = maxOf(0, bossHP - 35)
                                        bossHP = newHP
                                        if (newHP <= 0 || currentPhase >= 3) {
                                            onDefeatBoss()
                                        } else {
                                            currentPhase++
                                            selectedAnswer = null
                                            feedback = "💥 Attack neutralized! The Cyber Titan switches exploit vectors!"
                                        }
                                    } else {
                                        onDeductHeart()
                                        feedback = "❌ Network breached! Deploy standard cyber security countermeasures."
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = opt,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isSelected) Color.White else dynamicColors.textPrimary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
