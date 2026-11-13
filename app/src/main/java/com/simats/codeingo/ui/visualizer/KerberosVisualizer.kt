package com.simats.codeingo.ui.visualizer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.PhoenixCrimson
import com.simats.codeingo.ui.theme.PhoenixGreen
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.visualizer.components.VisualizerPlaybackController

data class KerberosStepInfo(
    val title: String,
    val sender: String,
    val receiver: String,
    val packetName: String,
    val description: String,
    val ticketType: String,
    val encryptionKeyUsed: String
)

/**
 * 🎫 Kerberos Ticket Exchange & Mutual Authentication Visualizer
 *
 * Implements Phase 4.1:
 * - 6-Step interactive Kerberos V5 handshake tracer
 * - Demonstrates AS-REQ / AS-REP, TGS-REQ / TGS-REP, and AP-REQ
 * - Visualizes Ticket Granting Ticket (TGT) and Service Ticket isolation
 */
@Composable
fun KerberosVisualizer(modifier: Modifier = Modifier) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark

    var currentStep by remember { mutableIntStateOf(1) }
    var isPlaying by remember { mutableStateOf(false) }
    var playbackSpeed by remember { mutableStateOf(1.0f) }

    val steps = listOf(
        KerberosStepInfo(
            title = "1. Authentication Server Request (AS-REQ)",
            sender = "Client (Alice)",
            receiver = "Authentication Server (AS)",
            packetName = "AS-REQ [User ID, Service ID, Timestamp]",
            description = "Alice initiates login by sending her username in plaintext. No passwords cross the network.",
            ticketType = "None (Auth Request)",
            encryptionKeyUsed = "Plaintext Transmission"
        ),
        KerberosStepInfo(
            title = "2. Ticket Granting Ticket Issued (AS-REP)",
            sender = "Authentication Server (AS)",
            receiver = "Client (Alice)",
            packetName = "AS-REP [TGT + Encrypted Session Key]",
            description = "AS looks up Alice's secret key (hash of password) and encrypts a session key. Crucially, it issues the TGT encrypted with the KDC secret key.",
            ticketType = "TGT (Ticket Granting Ticket)",
            encryptionKeyUsed = "Alice Password Hash & KDC Secret"
        ),
        KerberosStepInfo(
            title = "3. Ticket Granting Service Request (TGS-REQ)",
            sender = "Client (Alice)",
            receiver = "Ticket Granting Server (TGS)",
            packetName = "TGS-REQ [TGT + Client Authenticator]",
            description = "Alice presents her un-forgeable TGT along with an Authenticator timestamp encrypted with the session key.",
            ticketType = "TGT Presentation",
            encryptionKeyUsed = "Session Key K_c,tgs"
        ),
        KerberosStepInfo(
            title = "4. Target Service Ticket Issued (TGS-REP)",
            sender = "Ticket Granting Server (TGS)",
            receiver = "Client (Alice)",
            packetName = "TGS-REP [Service Ticket ST + Service Session Key]",
            description = "TGS decrypts the TGT, validates timestamp, and issues a Service Ticket encrypted with the target service's secret key.",
            ticketType = "ST (Service Ticket)",
            encryptionKeyUsed = "Service Server Master Key"
        ),
        KerberosStepInfo(
            title = "5. Application Server Access Request (AP-REQ)",
            sender = "Client (Alice)",
            receiver = "Target Application Server",
            packetName = "AP-REQ [Service Ticket + Fresh Authenticator]",
            description = "Alice connects directly to the Target Server, handing over the Service Ticket. The server decrypts it using its own secret key.",
            ticketType = "Service Ticket Handover",
            encryptionKeyUsed = "Service Secret Key & K_c,ss"
        ),
        KerberosStepInfo(
            title = "6. Mutual Authentication & Secure Channel",
            sender = "Target Application Server",
            receiver = "Client (Alice)",
            packetName = "AP-REP [Mutual Auth Confirmed]",
            description = "Both Alice and Server have verified each other's identity without Alice ever typing her password to the target service. Session opened!",
            ticketType = "Authenticated Session Active",
            encryptionKeyUsed = "Ephemeral Session Key K_c,ss"
        )
    )

    val activeStep = steps[currentStep - 1]
    val cardBg = if (isDark) Color(0xFF111827) else Color.White
    val borderColor = if (isDark) Color(0xFF1F2937) else Color(0xFFE2E8F0)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Active Protocol Stage Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(cardBg)
                .border(1.2.dp, borderColor, RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = activeStep.title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = AmberGold
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AmberGold.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "KERBEROS V5",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = AmberGold
                        )
                    }
                }

                // Node Flow Animation (Sender -> Receiver)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(text = activeStep.sender, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary)
                    }

                    Text(text = "──▶ ${activeStep.packetName} ──▶", fontSize = 10.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = PhoenixGreen)

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(text = activeStep.receiver, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary)
                    }
                }

                // Packet Payload Details
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Ticket Token:", fontSize = 10.sp, color = SubtextGray)
                        Text(text = activeStep.ticketType, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AmberGold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Encryption Key:", fontSize = 10.sp, color = SubtextGray)
                        Text(text = activeStep.encryptionKeyUsed, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PhoenixGreen)
                    }
                }

                Text(
                    text = activeStep.description,
                    fontSize = 11.5.sp,
                    color = dynamicColors.textSecondary,
                    lineHeight = 15.sp
                )
            }
        }

        // Playback Controller
        VisualizerPlaybackController(
            currentStep = currentStep,
            totalSteps = 6,
            onStepChange = { currentStep = it },
            isPlaying = isPlaying,
            onPlayPauseToggle = { isPlaying = !isPlaying },
            playbackSpeed = playbackSpeed,
            onSpeedChange = { playbackSpeed = it },
            stepLabel = "KERBEROS STEP",
            accentColor = AmberGold
        )
    }
}

