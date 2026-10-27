package com.simats.codeingo.ui.arenas.cyber_bastion.levels

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.simats.codeingo.ui.arenas.crypto.ArenaCryptoEngine
import com.simats.codeingo.ui.components.Tactile3DButton
import com.simats.codeingo.ui.components.Tactile3DButtonColor
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.ArenaDangerRed
import com.simats.codeingo.ui.theme.ArenaSuccessGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray

@Composable
fun SynCookieDefenseLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current

    // Sub-toggles default to FALSE (Defect 17 fix: player must deliberately configure fields)
    var isSynCookiesActive by remember { mutableStateOf(false) }
    var timeBitsEnabled by remember { mutableStateOf(false) }
    var mssBitsEnabled by remember { mutableStateOf(false) }
    var hmacBitsEnabled by remember { mutableStateOf(false) }

    val isConfigComplete = isSynCookiesActive && timeBitsEnabled && mssBitsEnabled && hmacBitsEnabled

    // Injected clock and live SYN cookie calculation via ArenaCryptoEngine
    val fixedClockMs = 1700000000000L
    val sampleCookie = remember(isConfigComplete) {
        if (isConfigComplete) {
            ArenaCryptoEngine.computeSynCookie(
                srcIp = "192.0.2.10",
                dstIp = "198.51.100.5",
                srcPort = 49152,
                dstPort = 443,
                secretKey = "BastionSecretKey".toByteArray(),
                clockMs = fixedClockMs
            )
        } else {
            0x12345678L
        }
    }

    val backlogUsage by animateFloatAsState(
        targetValue = if (isConfigComplete) 0.05f else 0.98f,
        label = "backlogAnim"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "RFC 4987 SYN COOKIE DDOS MITIGATION",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Stateless 32-bit TCP Initial Sequence Number (ISN) Generator",
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
                Text(
                    text = "⚡ Threat: TCP Half-Open Connection Exhaustion",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = AmberGold
                )
                Text(
                    text = "A SYN flood fills the server's connection backlog with spoofed half-open sockets. RFC 4987 SYN Cookies allocate ZERO kernel memory on SYN: connection state is packed directly into the 32-bit server ISN (5-bit timer + 3-bit MSS + 24-bit cryptographic hash)!",
                    fontSize = 11.5.sp,
                    color = dynamicColors.textPrimary,
                    lineHeight = 16.sp
                )
            }
        }

        // Live Backlog Queue Meter
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (isConfigComplete) ArenaSuccessGreen.copy(alpha = 0.12f) else ArenaDangerRed.copy(alpha = 0.12f))
                .border(1.5.dp, if (isConfigComplete) ArenaSuccessGreen else ArenaDangerRed, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "TCP BACKLOG QUEUE OCCUPANCY:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SubtextGray)
                    Text(
                        text = if (isConfigComplete) "5% (STATELESS HEALTHY)" else "98% (BACKLOG SATURATED)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isConfigComplete) ArenaSuccessGreen else ArenaDangerRed
                    )
                }

                LinearProgressIndicator(
                    progress = { backlogUsage },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (isConfigComplete) ArenaSuccessGreen else ArenaDangerRed,
                    trackColor = dynamicColors.cardBorder.copy(alpha = 0.3f)
                )

                if (isConfigComplete) {
                    Text(
                        text = "LIVE ISN COOKIE: 0x" + "%08X".format(sampleCookie and 0xFFFFFFFFL),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        color = ArenaSuccessGreen
                    )
                }
            }
        }

        // Interactive Switchboard (48dp height minimum for touch compliance)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            DefenseSwitchRow("1. Enable Stateless SYN Cookies", "Allocate zero backlog memory on inbound SYN", isSynCookiesActive) {
                isSynCookiesActive = it
            }
            DefenseSwitchRow("2. Encode 5-Bit Slow Counter", "Wrap counter t mod 32 (increments every 64s)", timeBitsEnabled) {
                timeBitsEnabled = it
            }
            DefenseSwitchRow("3. Encode 3-Bit MSS Table Index", "Preserve Maximum Segment Size without TCP options", mssBitsEnabled) {
                mssBitsEnabled = it
            }
            DefenseSwitchRow("4. Encode 24-Bit HMAC Hash", "s = H(srcIp, srcPort, dstIp, dstPort, t, Secret)", hmacBitsEnabled) {
                hmacBitsEnabled = it
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Tactile3DButton(
            title = if (isConfigComplete) "Thwart SYN Flood Attack ✓" else "Configure All 4 ISN Fields",
            style = if (isConfigComplete) Tactile3DButtonColor.GREEN else Tactile3DButtonColor.AMBER,
            onClick = {
                if (isConfigComplete) {
                    onActionSuccess()
                    onComplete()
                } else {
                    onDeductHeart()
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun DefenseSwitchRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val dynamicColors = LocalDynamicThemeColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(dynamicColors.cardBorder.copy(alpha = 0.2f))
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary)
            Text(text = description, fontSize = 9.sp, color = SubtextGray)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = ArenaSuccessGreen,
                uncheckedThumbColor = SubtextGray,
                uncheckedTrackColor = dynamicColors.cardBorder
            )
        )
    }
}
