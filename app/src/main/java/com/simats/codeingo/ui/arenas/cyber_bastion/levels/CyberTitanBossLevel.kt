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
import com.simats.codeingo.ui.theme.ArenaDangerRed
import com.simats.codeingo.ui.theme.ArenaSuccessGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

enum class TitanPhase(val phaseNumber: Int) {
    PHASE_1(1),
    PHASE_2(2),
    PHASE_3(3)
}

data class TitanChoice(
    val id: String,
    val text: String,
    val isCorrect: Boolean,
    val explanation: String
)

data class TitanChallenge(
    val phase: TitanPhase,
    val scenario: String,
    val choices: List<TitanChoice>
)

@Composable
fun CyberTitanBossLevel(
    onDefeatBoss: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    var bossHP by remember { mutableIntStateOf(100) }
    var currentPhase by remember { mutableStateOf(TitanPhase.PHASE_1) }
    var selectedChoiceId by remember { mutableStateOf<String?>(null) }
    var feedback by remember { mutableStateOf("The Cyber Titan launches multi-vector network assaults! Defend the Cyber Bastion!") }

    val challenges = remember {
        listOf(
            TitanChallenge(
                phase = TitanPhase.PHASE_1,
                scenario = "SYN Flood Assault: Inbound TCP connection backlog is saturated with spoofed half-open handshakes! Deploy countermeasure:",
                choices = listOf(
                    TitanChoice("t1_correct", "Deploy SYN Cookies (Stateless Handshake)", true, "Correct: SYN Cookies allocate zero kernel memory until the final client ACK arrives!"),
                    TitanChoice("t1_d1", "Disable Ingress Firewall", false, "Misconception: Disabling the firewall exposes all services to direct saturation."),
                    TitanChoice("t1_d2", "Accept all half-open sockets", false, "Misconception: Accepting half-open sockets exhausts RAM in seconds."),
                    TitanChoice("t1_d3", "Send ICMP Redirects", false, "Misconception: ICMP Redirects do not solve socket table memory exhaustion.")
                )
            ),
            TitanChallenge(
                phase = TitanPhase.PHASE_2,
                scenario = "MITM Rogue Certificate: A compromised intermediate CA in the network path issued a fake certificate! Deploy defense:",
                choices = listOf(
                    TitanChoice("t2_correct", "Enforce Certificate Transparency & Pinning", true, "Correct: CT logs expose rogue intermediate CAs and public key pinning blocks fraudulent certs!"),
                    TitanChoice("t2_d1", "Accept Self-Signed Certificate", false, "Misconception: Blindly accepting certificates eliminates all cryptographic authenticity."),
                    TitanChoice("t2_d2", "Disable TLS Verification", false, "Misconception: Disabling verification opens plaintext eavesdropping to any MITM actor."),
                    TitanChoice("t2_d3", "Downgrade to HTTP", false, "Misconception: Downgrading to cleartext HTTP removes all encryption entirely.")
                )
            ),
            TitanChallenge(
                phase = TitanPhase.PHASE_3,
                scenario = "Perimeter Port Scanning: The Titan scans open TCP ports across the corporate perimeter! Deploy network perimeter policy:",
                choices = listOf(
                    TitanChoice("t3_correct", "Default-Deny Ingress Stateful Firewall", true, "Correct: Default-deny drops all unsolicited inbound port probes while allowing established sessions!"),
                    TitanChoice("t3_d1", "Allow Any-Any Ingress Traffic", false, "Misconception: Any-Any ingress grants the attacker access to all internal open ports."),
                    TitanChoice("t3_d2", "Disable WAF and IDS Rules", false, "Misconception: Disabling inspection blinds defenders to network probing."),
                    TitanChoice("t3_d3", "Forward all traffic directly to DMZ", false, "Misconception: Unfiltered port forwarding exposes DMZ hosts to direct exploitation.")
                )
            )
        )
    }

    val currentChallenge = challenges.first { it.phase == currentPhase }
    val shuffledChoices = remember(currentPhase) {
        currentChallenge.choices.shuffled()
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
            Text(text = "👾 THE CYBER TITAN", fontSize = 16.sp, color = ArenaDangerRed, fontWeight = FontWeight.Black)
            Text(text = "PHASE ${currentPhase.phaseNumber} / 3", fontSize = 12.sp, fontWeight = FontWeight.Black, color = AmberGold)
        }

        // Boss Health Bar
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
                    .background(ArenaDangerRed)
            )
        }
        Text(text = "$bossHP / 100 HP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textSecondary)

        // Scenario Card
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
                Text(text = currentChallenge.scenario, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary, textAlign = TextAlign.Center)
                Text(text = feedback, fontSize = 11.sp, color = AmberGold, textAlign = TextAlign.Center)
            }
        }

        // Choices Grid (48dp+ touch target)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            shuffledChoices.chunked(2).forEach { rowOptions ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    rowOptions.forEach { choice ->
                        val isSelected = selectedChoiceId == choice.id
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) (if (choice.isCorrect) ArenaSuccessGreen else ArenaDangerRed)
                                    else dynamicColors.cardBorder.copy(alpha = 0.25f)
                                )
                                .border(1.dp, if (isSelected) Color.White else AmberGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .clickable {
                                    selectedChoiceId = choice.id
                                    if (choice.isCorrect) {
                                        onActionSuccess()
                                        val newHP = maxOf(0, bossHP - 35)
                                        bossHP = newHP
                                        if (newHP <= 0 || currentPhase == TitanPhase.PHASE_3) {
                                            onDefeatBoss()
                                        } else {
                                            currentPhase = when (currentPhase) {
                                                TitanPhase.PHASE_1 -> TitanPhase.PHASE_2
                                                TitanPhase.PHASE_2 -> TitanPhase.PHASE_3
                                                TitanPhase.PHASE_3 -> TitanPhase.PHASE_3
                                            }
                                            selectedChoiceId = null
                                            feedback = "💥 Titan assault deflected! ${choice.explanation}"
                                        }
                                    } else {
                                        onDeductHeart()
                                        feedback = "❌ Network defense breached: ${choice.explanation}"
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = choice.text,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isSelected && choice.isCorrect) Color.Black else dynamicColors.textPrimary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
