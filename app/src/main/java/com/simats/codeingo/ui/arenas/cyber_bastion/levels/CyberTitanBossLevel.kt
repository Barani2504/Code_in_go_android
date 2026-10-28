package com.simats.codeingo.ui.arenas.cyber_bastion.levels

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.simats.codeingo.ui.theme.ArenaDangerRed
import com.simats.codeingo.ui.theme.ArenaSuccessGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.LocalReduceMotion
import com.simats.codeingo.ui.theme.SubtextGray

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
    onTriggerShake: () -> Unit = {},
    onTriggerShockwave: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    val reduceMotion = LocalReduceMotion.current
    val view = LocalView.current

    val emerald = ArenaPalettes.Arena5Primary
    val cyan = ArenaPalettes.Arena5Accent
    val cardBg = if (isDark) ArenaPalettes.Arena5CardDark else dynamicColors.cardBackground

    var bossHP by remember { mutableIntStateOf(100) }
    var currentPhase by remember { mutableStateOf(TitanPhase.PHASE_1) }
    var selectedChoiceId by remember { mutableStateOf<String?>(null) }
    var feedback by remember { mutableStateOf("The Cyber Titan launches multi-vector network assaults! Defend the Bastion perimeter!") }
    var lastHitSuccess by remember { mutableStateOf<Boolean?>(null) }

    val animatedHpFraction by animateFloatAsState(
        targetValue = (bossHP / 100f).coerceIn(0f, 1f),
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 180f),
        label = "cyberTitanHpAnim"
    )

    val challenges = remember {
        listOf(
            TitanChallenge(
                phase = TitanPhase.PHASE_1,
                scenario = "SYN Flood Assault: Inbound TCP connection backlog is saturated with spoofed half-open handshakes! Deploy countermeasure:",
                choices = listOf(
                    TitanChoice(
                        "t1_correct",
                        "Deploy SYN Cookies (Stateless Handshake)",
                        true,
                        "CRITICAL HIT! SYN Cookies allocate zero kernel memory until the final client ACK arrives!"
                    ),
                    TitanChoice(
                        "t1_d1",
                        "Disable Ingress Firewall Rules",
                        false,
                        "Misconception: Disabling the firewall exposes all services to direct saturation."
                    ),
                    TitanChoice(
                        "t1_d2",
                        "Accept all half-open sockets",
                        false,
                        "Misconception: Accepting half-open sockets exhausts server memory within seconds."
                    ),
                    TitanChoice(
                        "t1_d3",
                        "Broadcast ICMP Redirects",
                        false,
                        "Misconception: ICMP Redirects do not solve socket backlog memory exhaustion."
                    )
                )
            ),
            TitanChallenge(
                phase = TitanPhase.PHASE_2,
                scenario = "MITM Rogue Certificate: A compromised intermediate CA in the network path issued a fake certificate! Deploy defense:",
                choices = listOf(
                    TitanChoice(
                        "t2_correct",
                        "Enforce Certificate Transparency & Pinning",
                        true,
                        "CRITICAL HIT! CT logs expose rogue intermediate CAs and public key pinning blocks fraudulent certs!"
                    ),
                    TitanChoice(
                        "t2_d1",
                        "Accept Self-Signed Certificate",
                        false,
                        "Misconception: Blindly accepting untrusted certificates eliminates all cryptographic authenticity."
                    ),
                    TitanChoice(
                        "t2_d2",
                        "Disable TLS Verification",
                        false,
                        "Misconception: Disabling verification opens plaintext eavesdropping to any MITM actor."
                    ),
                    TitanChoice(
                        "t2_d3",
                        "Downgrade to Cleartext HTTP",
                        false,
                        "Misconception: Downgrading to cleartext HTTP removes all encryption entirely."
                    )
                )
            ),
            TitanChallenge(
                phase = TitanPhase.PHASE_3,
                scenario = "Perimeter Port Scanning: The Titan scans open TCP ports across the corporate perimeter! Deploy network perimeter policy:",
                choices = listOf(
                    TitanChoice(
                        "t3_correct",
                        "Default-Deny Ingress Stateful Firewall",
                        true,
                        "TITAN DEFEATED! Default-deny drops all unsolicited inbound port probes while statefully allowing established sessions!"
                    ),
                    TitanChoice(
                        "t3_d1",
                        "Allow Any-Any Ingress Traffic",
                        false,
                        "Misconception: Any-Any ingress grants the attacker complete access to internal open ports."
                    ),
                    TitanChoice(
                        "t3_d2",
                        "Disable WAF and IDS Rules",
                        false,
                        "Misconception: Disabling inspection blinds defenders to network probing."
                    ),
                    TitanChoice(
                        "t3_d3",
                        "Forward all traffic directly to DMZ",
                        false,
                        "Misconception: Unfiltered port forwarding exposes DMZ hosts to direct exploitation."
                    )
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
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Boss Header Ribbon
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "👾 THE CYBER TITAN",
                    fontSize = 14.sp,
                    color = ArenaDangerRed,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Perimeter Intrusion & DoS Assault",
                    fontSize = 10.sp,
                    color = dynamicColors.textSecondary
                )
            }
            ArenaStatusBadge(
                type = ArenaBadgeType.DANGER,
                label = "PHASE ${currentPhase.phaseNumber} / 3"
            )
        }

        // Animated Boss Health Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(cardBg)
                .border(1.2.dp, ArenaDangerRed.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TITAN INTEGRITY MATRIX:",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = SubtextGray
                    )
                    Text(
                        text = "$bossHP / 100 HP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (bossHP <= 35) ArenaDangerRed else emerald
                    )
                }

                LinearProgressIndicator(
                    progress = { animatedHpFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = if (bossHP <= 35) ArenaDangerRed else emerald,
                    trackColor = cardBg.copy(alpha = 0.5f)
                )
            }
        }

        // Scenario Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(cardBg)
                .border(1.2.dp, emerald.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = currentChallenge.scenario,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = dynamicColors.textPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp
                )
                Text(
                    text = feedback,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = when (lastHitSuccess) {
                        true -> ArenaSuccessGreen
                        false -> ArenaDangerRed
                        else -> cyan
                    },
                    textAlign = TextAlign.Center
                )
            }
        }

        // Choices Grid (2x2 grid, minTouchTarget48 compliant)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            shuffledChoices.chunked(2).forEach { rowOptions ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    rowOptions.forEach { choice ->
                        val isSelected = selectedChoiceId == choice.id
                        val isCorrect = choice.isCorrect
                        val bgTint = when {
                            isSelected && isCorrect -> ArenaSuccessGreen.copy(alpha = 0.2f)
                            isSelected && !isCorrect -> ArenaDangerRed.copy(alpha = 0.2f)
                            else -> cardBg
                        }
                        val borderColor = when {
                            isSelected && isCorrect -> ArenaSuccessGreen
                            isSelected && !isCorrect -> ArenaDangerRed
                            else -> cyan.copy(alpha = 0.4f)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .minTouchTarget48()
                                .clip(RoundedCornerShape(12.dp))
                                .background(bgTint)
                                .border(1.2.dp, borderColor, RoundedCornerShape(12.dp))
                                .clickable {
                                    selectedChoiceId = choice.id
                                    ArenaHaptics.tap(view, reduceMotion)
                                    if (choice.isCorrect) {
                                        ArenaHaptics.criticalStrike(view, reduceMotion)
                                        lastHitSuccess = true
                                        onTriggerShake()
                                        onTriggerShockwave()
                                        onActionSuccess()

                                        val newHP = maxOf(0, bossHP - 35)
                                        bossHP = newHP
                                        if (newHP <= 0 || currentPhase == TitanPhase.PHASE_3) {
                                            feedback = choice.explanation
                                            onDefeatBoss()
                                        } else {
                                            currentPhase = when (currentPhase) {
                                                TitanPhase.PHASE_1 -> TitanPhase.PHASE_2
                                                TitanPhase.PHASE_2 -> TitanPhase.PHASE_3
                                                TitanPhase.PHASE_3 -> TitanPhase.PHASE_3
                                            }
                                            selectedChoiceId = null
                                            feedback = choice.explanation
                                        }
                                    } else {
                                        ArenaHaptics.error(view, reduceMotion)
                                        lastHitSuccess = false
                                        onTriggerShake()
                                        onDeductHeart()
                                        feedback = "❌ BREACH: ${choice.explanation}"
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = choice.text,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected && choice.isCorrect) ArenaSuccessGreen else dynamicColors.textPrimary,
                                textAlign = TextAlign.Center,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
