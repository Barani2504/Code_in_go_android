package com.simats.codeingo.ui.arenas.cyber_bastion.levels

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material3.Icon
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
import com.simats.codeingo.ui.arenas.crypto.ArenaCryptoEngine
import com.simats.codeingo.ui.components.Tactile3DButton
import com.simats.codeingo.ui.components.Tactile3DButtonColor
import com.simats.codeingo.ui.theme.ArenaDangerRed
import com.simats.codeingo.ui.theme.ArenaSuccessGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.LocalReduceMotion
import com.simats.codeingo.ui.theme.SubtextGray

@Composable
fun SynCookieDefenseLevel(
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

    // Sub-toggles default to FALSE (player must deliberately configure each field)
    var isSynCookiesActive by remember { mutableStateOf(false) }
    var timeBitsEnabled by remember { mutableStateOf(false) }
    var mssBitsEnabled by remember { mutableStateOf(false) }
    var hmacBitsEnabled by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isConfigComplete = isSynCookiesActive && timeBitsEnabled && mssBitsEnabled && hmacBitsEnabled

    // Fixed clock and live SYN cookie calculation via ArenaCryptoEngine
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
        // Mission Header with Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "RFC 4987 SYN COOKIE DDOS MITIGATION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = emerald,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Stateless 32-bit TCP ISN Synthesizer",
                    fontSize = 10.sp,
                    color = dynamicColors.textSecondary
                )
            }
            ArenaStatusBadge(
                type = if (isConfigComplete) ArenaBadgeType.SUCCESS else ArenaBadgeType.WARNING,
                label = if (isConfigComplete) "DDOS SHIELDED" else "FLOOD DETECTED"
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
                        text = "Threat: TCP Half-Open Connection Exhaustion",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = emerald
                    )
                }
                Text(
                    text = "A SYN flood fills the server's connection backlog with spoofed half-open handshakes. RFC 4987 SYN Cookies allocate ZERO kernel memory on SYN: connection state is packed directly into the 32-bit server ISN (5-bit timer + 3-bit MSS + 24-bit cryptographic hash)!",
                    fontSize = 11.sp,
                    color = dynamicColors.textPrimary,
                    lineHeight = 15.sp
                )
            }
        }

        // Live Backlog Queue Meter
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (isConfigComplete) emerald.copy(alpha = 0.12f) else ArenaDangerRed.copy(alpha = 0.12f))
                .border(1.5.dp, if (isConfigComplete) emerald else ArenaDangerRed, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TCP BACKLOG QUEUE OCCUPANCY:",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = SubtextGray
                    )
                    Text(
                        text = if (isConfigComplete) "5% (STATELESS HEALTHY)" else "98% (BACKLOG SATURATED)",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (isConfigComplete) emerald else ArenaDangerRed
                    )
                }

                LinearProgressIndicator(
                    progress = { backlogUsage },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (isConfigComplete) emerald else ArenaDangerRed,
                    trackColor = cardBg.copy(alpha = 0.5f)
                )

                if (isConfigComplete) {
                    Text(
                        text = "LIVE ISN COOKIE: 0x" + "%08X".format(sampleCookie and 0xFFFFFFFFL),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        color = emerald
                    )
                }
            }
        }

        // Interactive Switchboard (minTouchTarget48 compliant)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            DefenseSwitchRow(
                title = "1. Enable Stateless SYN Cookies",
                description = "Allocate zero backlog memory on inbound SYN",
                checked = isSynCookiesActive,
                cardBg = cardBg,
                emerald = emerald
            ) {
                ArenaHaptics.tap(view, reduceMotion)
                isSynCookiesActive = it
                errorMessage = null
            }

            DefenseSwitchRow(
                title = "2. Encode 5-Bit Slow Counter",
                description = "Wrap counter t mod 32 (increments every 64s for replay window)",
                checked = timeBitsEnabled,
                cardBg = cardBg,
                emerald = emerald
            ) {
                ArenaHaptics.tap(view, reduceMotion)
                timeBitsEnabled = it
                errorMessage = null
            }

            DefenseSwitchRow(
                title = "3. Encode 3-Bit MSS Table Index",
                description = "Preserve Maximum Segment Size without storing TCP options",
                checked = mssBitsEnabled,
                cardBg = cardBg,
                emerald = emerald
            ) {
                ArenaHaptics.tap(view, reduceMotion)
                mssBitsEnabled = it
                errorMessage = null
            }

            DefenseSwitchRow(
                title = "4. Encode 24-Bit HMAC Hash",
                description = "s = HMAC-SHA256(srcIp, srcPort, dstIp, dstPort, t, Secret)",
                checked = hmacBitsEnabled,
                cardBg = cardBg,
                emerald = emerald
            ) {
                ArenaHaptics.tap(view, reduceMotion)
                hmacBitsEnabled = it
                errorMessage = null
            }
        }

        // Error message if player attempts to submit prematurely
        AnimatedVisibility(visible = errorMessage != null, enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(ArenaDangerRed.copy(alpha = 0.12f))
                    .border(1.dp, ArenaDangerRed, RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = errorMessage ?: "",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ArenaDangerRed,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Tactile3DButton(
            title = if (isConfigComplete) "Thwart SYN Flood Attack ✓" else "Configure All 4 ISN Fields",
            style = if (isConfigComplete) Tactile3DButtonColor.GREEN else Tactile3DButtonColor.AMBER,
            onClick = {
                if (isConfigComplete) {
                    ArenaHaptics.success(view, reduceMotion)
                    onActionSuccess()
                    onComplete()
                } else {
                    ArenaHaptics.error(view, reduceMotion)
                    errorMessage = "❌ Defense Incomplete: All 4 ISN fields must be enabled to generate a valid RFC 4987 cookie!"
                    onDeductHeart()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .minTouchTarget48()
        )
    }
}

@Composable
private fun DefenseSwitchRow(
    title: String,
    description: String,
    checked: Boolean,
    cardBg: Color,
    emerald: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    val dynamicColors = LocalDynamicThemeColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .minTouchTarget48()
            .clip(RoundedCornerShape(10.dp))
            .background(cardBg)
            .border(1.dp, if (checked) emerald else cardBg.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (checked) emerald else dynamicColors.textPrimary
            )
            Text(
                text = description,
                fontSize = 9.sp,
                color = SubtextGray
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = emerald,
                uncheckedThumbColor = SubtextGray,
                uncheckedTrackColor = cardBg.copy(alpha = 0.8f)
            )
        )
    }
}
