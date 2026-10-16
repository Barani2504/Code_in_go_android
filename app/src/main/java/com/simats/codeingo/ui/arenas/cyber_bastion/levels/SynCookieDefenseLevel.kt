package com.simats.codeingo.ui.arenas.cyber_bastion.levels

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

@Composable
fun SynCookieDefenseLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current

    // Interactive SYN Cookie Defense Console
    var isSynCookiesActive by remember { mutableStateOf(false) }
    var timeBitsEnabled by remember { mutableStateOf(true) }
    var mssBitsEnabled by remember { mutableStateOf(true) }
    var hmacBitsEnabled by remember { mutableStateOf(true) }
    var isThwarted by remember { mutableStateOf(false) }

    val isConfigComplete = isSynCookiesActive && timeBitsEnabled && mssBitsEnabled && hmacBitsEnabled
    val backlogUsage by animateFloatAsState(
        targetValue = if (isConfigComplete) 0.05f else 0.98f,
        label = "backlogAnim"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "SYN COOKIE DDOS MITIGATION",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Stateless TCP sequence number defense against SYN floods",
                fontSize = 11.sp,
                color = dynamicColors.textSecondary,
                textAlign = TextAlign.Center
            )
        }

        // Explanatory Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(dynamicColors.cardBorder.copy(alpha = 0.2f))
                .border(1.dp, AmberGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = "⚡ Threat: TCP Half-Open Socket Exhaustion",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AmberGold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Spoofed SYN packets flood the server, exhausting the backlog table before receiving ACKs. With SYN Cookies, the server allocates ZERO memory on SYN, encoding connection state into the 32-bit Initial Sequence Number (ISN)!",
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
                .clip(RoundedCornerShape(14.dp))
                .background(dynamicColors.cardBackground)
                .border(
                    1.5.dp,
                    if (isConfigComplete) DuolingoGreen else DuolingoRed,
                    RoundedCornerShape(14.dp)
                )
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TCP BACKLOG BUFFER OCCUPANCY",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = dynamicColors.textSecondary
                    )
                    Text(
                        text = if (isConfigComplete) "5% (HEALTHY)" else "98% (OVERFLOW CRITICAL)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isConfigComplete) DuolingoGreen else DuolingoRed
                    )
                }

                LinearProgressIndicator(
                    progress = { backlogUsage },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = if (isConfigComplete) DuolingoGreen else DuolingoRed,
                    trackColor = dynamicColors.cardBorder.copy(alpha = 0.3f)
                )

                Text(
                    text = if (isConfigComplete) "Stateless mode active: server holds 0 unauthenticated state bytes."
                    else "Attacker flooding SYN table with 250,000 pkts/sec! Memory exhaustion imminent.",
                    fontSize = 11.sp,
                    color = if (isConfigComplete) DuolingoGreen else DuolingoRed,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Mode Switcher: Stateful Backlog vs SYN Cookies
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(dynamicColors.cardBackground)
                .border(1.dp, dynamicColors.cardBorder.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Cryptographic SYN Cookie Engine",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = dynamicColors.textPrimary
                    )
                    Text(
                        text = if (isSynCookiesActive) "Enabled: Stateless ISN verification" else "Disabled: Allocating socket per SYN (vulnerable)",
                        fontSize = 10.5.sp,
                        color = dynamicColors.textSecondary
                    )
                }
                Switch(
                    checked = isSynCookiesActive,
                    onCheckedChange = { isSynCookiesActive = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = DuolingoGreen
                    )
                )
            }
        }

        // 32-bit ISN Encoder Layout
        AnimatedVisibility(visible = isSynCookiesActive) {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "32-BIT INITIAL SEQUENCE NUMBER (ISN) ENCODER",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Black,
                    color = AmberGold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Time bits (5 bits)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (timeBitsEnabled) DuolingoGreen.copy(alpha = 0.2f) else dynamicColors.cardBorder.copy(alpha = 0.2f))
                            .border(1.dp, if (timeBitsEnabled) DuolingoGreen else dynamicColors.cardBorder.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .clickable { timeBitsEnabled = !timeBitsEnabled }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "5 BITS", fontSize = 10.sp, fontWeight = FontWeight.Black, color = AmberGold)
                            Text(text = "Slow Clock", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary)
                            Text(text = "Anti-replay", fontSize = 9.sp, color = dynamicColors.textSecondary)
                        }
                    }

                    // MSS index (3 bits)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (mssBitsEnabled) DuolingoGreen.copy(alpha = 0.2f) else dynamicColors.cardBorder.copy(alpha = 0.2f))
                            .border(1.dp, if (mssBitsEnabled) DuolingoGreen else dynamicColors.cardBorder.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .clickable { mssBitsEnabled = !mssBitsEnabled }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "3 BITS", fontSize = 10.sp, fontWeight = FontWeight.Black, color = AmberGold)
                            Text(text = "MSS Table", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary)
                            Text(text = "Max segment", fontSize = 9.sp, color = dynamicColors.textSecondary)
                        }
                    }

                    // HMAC bits (24 bits)
                    Box(
                        modifier = Modifier
                            .weight(1.5f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (hmacBitsEnabled) DuolingoGreen.copy(alpha = 0.2f) else dynamicColors.cardBorder.copy(alpha = 0.2f))
                            .border(1.dp, if (hmacBitsEnabled) DuolingoGreen else dynamicColors.cardBorder.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .clickable { hmacBitsEnabled = !hmacBitsEnabled }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "24 BITS", fontSize = 10.sp, fontWeight = FontWeight.Black, color = AmberGold)
                            Text(text = "4-Tuple HMAC", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary)
                            Text(text = "Secret Keyed", fontSize = 9.sp, color = dynamicColors.textSecondary)
                        }
                    }
                }
            }
        }

        // Action Trigger Button
        Button(
            onClick = {
                if (isConfigComplete) {
                    isThwarted = true
                    onActionSuccess()
                    onComplete()
                } else {
                    isThwarted = false
                    onDeductHeart()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isConfigComplete) DuolingoGreen else AmberGold
            )
        ) {
            Text(
                text = if (isConfigComplete) "✓ DEPLOY SYN COOKIE SHIELD (NEUTRALIZE DDOS)"
                else "ACTIVATE STATELESS SYN COOKIE ENGINE",
                fontWeight = FontWeight.Black,
                fontSize = 12.5.sp,
                color = Color.Black
            )
        }
    }
}
