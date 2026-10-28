package com.simats.codeingo.ui.visualizer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.data.model.DSAVisualizerType
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DsaRed
import com.simats.codeingo.ui.theme.DsaTeal
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.visualizer.engine.TlsEngine
import com.simats.codeingo.ui.visualizer.model.CNSLabMode
import kotlin.math.roundToInt

@Composable
fun TLS13HandshakeVisualizer(mode: CNSLabMode = CNSLabMode.EXPLORE) {
    when (mode) {
        CNSLabMode.CHALLENGE -> {
            CNSChallengeCardView(labType = DSAVisualizerType.STEP_RECORDER)
        }
        CNSLabMode.CODE_TEST -> {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                CNSTestVectorCardView(labType = DSAVisualizerType.STEP_RECORDER)
                CNSLanguageImplementationCardView(structureName = DSAVisualizerType.STEP_RECORDER.title)
            }
        }
        CNSLabMode.ATTACK -> {
            TLS13HandshakeAttackView()
        }
        CNSLabMode.EXPLORE -> {
            TLS13HandshakeExploreView()
        }
    }
}

@Composable
private fun TLS13HandshakeAttackView() {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    val greenColor = if (isDark) DuolingoGreen else Color(0xFF15803D)
    val redColor = if (isDark) DsaRed else Color(0xFFDC2626)

    var tamperByte by remember { mutableIntStateOf(5) }
    val tamperResult = remember(tamperByte) {
        TlsEngine.testRecordTamper(tamperByteIndex = tamperByte)
    }

    val chain = remember {
        listOf(
            TlsEngine.X509Cert("CN=codeingo.simats.com", "CN=Simats Secure CA G2", false, 2024, 2028, true),
            TlsEngine.X509Cert("CN=Simats Secure CA G2", "CN=GlobalTrust Root CA", false, 2020, 2030, true),
            TlsEngine.X509Cert("CN=GlobalTrust Root CA", "CN=GlobalTrust Root CA", true, 2015, 2035, true)
        )
    }
    val chainValidation = remember {
        TlsEngine.validateCertificateChain(chain, "codeingo.simats.com", 2026)
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                .background(DsaRed.copy(alpha = 0.15f)).border(1.dp, DsaRed.copy(alpha = 0.5f), RoundedCornerShape(10.dp)).padding(12.dp)
        ) {
            Text(
                text = "🔓 ATTACK SUITE: In-Flight AEAD Record Tampering & X.509 Chain Validation",
                fontSize = 11.sp, fontWeight = FontWeight.Black, color = redColor
            )
        }

        // Interactive Record Tampering
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, redColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "INTERACTIVE RECORD TAMPER DEMO", fontSize = 11.5.sp, fontWeight = FontWeight.Black, color = redColor)
                Text(text = "Slide to flip 1 bit in the encrypted TLS 1.3 record body:", fontSize = 10.sp, color = SubtextGray)
                Slider(
                    value = tamperByte.toFloat(),
                    onValueChange = { tamperByte = it.roundToInt() },
                    valueRange = 0f..10f,
                    colors = SliderDefaults.colors(thumbColor = DsaRed, activeTrackColor = DsaRed)
                )

                Text(text = "Original Record: ${tamperResult.originalRecordHex.take(24)}...", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = SubtextGray)
                Text(text = "Tampered Record: ${tamperResult.tamperedRecordHex.take(24)}...", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = redColor)

                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(DsaRed.copy(alpha = 0.15f)).padding(8.dp)) {
                    Text(text = tamperResult.errorDetail ?: "Verified", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = redColor)
                }
            }
        }

        // X.509 Chain Validator
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, greenColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "X.509 PKI TRUST CHAIN VALIDATOR", fontSize = 11.5.sp, fontWeight = FontWeight.Black, color = greenColor)
                chain.forEachIndexed { i, cert ->
                    Column(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(dynamicColors.inputBackground).padding(8.dp)
                    ) {
                        Text(text = "${i + 1}. ${cert.subject}", fontSize = 11.sp, fontWeight = FontWeight.Black, color = dynamicColors.textPrimary)
                        Text(text = "Issuer: ${cert.issuer}  |  Validity: ${cert.validFromYear}-${cert.validToYear}", fontSize = 9.5.sp, color = SubtextGray)
                    }
                }

                Text(
                    text = if (chainValidation.isValid) "✅ Certificate Chain Validated to Trusted Root Anchor" else "❌ Chain Rejected: ${chainValidation.failureReason}",
                    fontSize = 11.sp, fontWeight = FontWeight.Black, color = if (chainValidation.isValid) greenColor else redColor
                )
            }
        }
    }
}

@Composable
private fun TLS13HandshakeExploreView() {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    val tealColor = if (isDark) DsaTeal else Color(0xFF0D9488)
    val orangeColor = if (isDark) Color(0xFFFF9600) else Color(0xFFEA580C)

    var flightIndex by remember { mutableIntStateOf(0) }
    val packets = remember { TlsEngine.getHandshakePackets() }
    val currentPacket = packets.getOrElse(flightIndex) { packets.first() }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, orangeColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "PACKET ${flightIndex + 1} / ${packets.size}: ${currentPacket.messageType}", fontSize = 12.sp, fontWeight = FontWeight.Black, color = orangeColor)
                    Text(text = currentPacket.sender, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = tealColor)
                }

                Text(text = currentPacket.description, fontSize = 10.5.sp, color = SubtextGray)

                // Packet Headers & Fields Table
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(dynamicColors.inputBackground).padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    currentPacket.fields.forEach { (k, v) ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "$k:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SubtextGray)
                            Text(text = v, fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = dynamicColors.textPrimary)
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier.weight(1f).height(48.dp).clip(RoundedCornerShape(10.dp)).background(dynamicColors.inputBackground)
                            .clickable { if (flightIndex > 0) flightIndex-- }.padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "◀ PREV PACKET", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary)
                    }
                    Box(
                        modifier = Modifier.weight(1f).height(48.dp).clip(RoundedCornerShape(10.dp)).background(orangeColor)
                            .clickable { if (flightIndex < packets.size - 1) flightIndex++ else flightIndex = 0 }.padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "NEXT PACKET ▶", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
