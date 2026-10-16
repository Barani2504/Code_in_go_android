package com.simats.codeingo.ui.arenas.cyber_bastion.levels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.components.Duolingo3DButton
import com.simats.codeingo.ui.components.Duolingo3DButtonColor
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

@Composable
fun TlsHandshakeLevel(
    onComplete: () -> Unit,
    onActionSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    var step by remember { mutableIntStateOf(0) }
    val steps = listOf(
        "ClientHello: Propose TLS 1.3 ciphers & KeyShare",
        "KeyShare: Send Ephemeral ECDH Share (x25519)",
        "ServerHello: Derive Handshake Traffic Keys!"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "TLS 1.3 1-RTT HANDSHAKE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Zero-round-trip / 1-round-trip authenticated encryption setup",
                fontSize = 11.sp,
                color = dynamicColors.textSecondary,
                textAlign = TextAlign.Center
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(dynamicColors.cardBorder.copy(alpha = 0.2f))
                .border(1.dp, AmberGold.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "🌐 TLS 1.3 eliminates obsolete cipher negotiation by sending the Diffie-Hellman KeyShare directly inside the ClientHello packet.",
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = dynamicColors.textPrimary,
                textAlign = TextAlign.Center
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(12.dp))
                .background(dynamicColors.cardBorder.copy(alpha = 0.3f))
                .border(1.dp, if (step >= 2) DuolingoGreen else AmberGold, RoundedCornerShape(12.dp))
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = steps[step.coerceIn(0, 2)],
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = if (step >= 2) DuolingoGreen else AmberGold,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center
            )
        }

        Duolingo3DButton(
            title = if (step < 2) "🚀 Send Packet (${step + 1}/3)" else "Handshake Keys Established ✓",
            style = if (step >= 2) Duolingo3DButtonColor.GREEN else Duolingo3DButtonColor.BLUE,
            onClick = {
                step++
                onActionSuccess()
                if (step >= 3) onComplete()
            },
            modifier = Modifier.fillMaxWidth(0.85f)
        )
    }
}
