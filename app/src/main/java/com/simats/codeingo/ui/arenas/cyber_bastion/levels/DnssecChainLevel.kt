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
import com.simats.codeingo.ui.components.Duolingo3DButton
import com.simats.codeingo.ui.components.Duolingo3DButtonColor
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

@Composable
fun DnssecChainLevel(
    onComplete: () -> Unit,
    onActionSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    var isChainValidated by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "PKI & DNSSEC CHAIN OF TRUST",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Root CA ➔ Intermediate CA ➔ Leaf Certificate Hierarchy",
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
                .padding(14.dp)
        ) {
            Text(
                text = "X.509 PKI Chain of Trust: Verify root certificate authority digital signature down through intermediates to validate the server leaf certificate.",
                fontSize = 13.5.sp,
                color = dynamicColors.textPrimary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(12.dp))
                .background(dynamicColors.cardBorder.copy(alpha = 0.3f))
                .border(1.dp, if (isChainValidated) DuolingoGreen else AmberGold, RoundedCornerShape(12.dp))
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isChainValidated) "CERTIFICATE CHAIN VALIDATED ✓" else "ROOT CA SIGNATURE VERIFIED",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = if (isChainValidated) DuolingoGreen else AmberGold
            )
        }

        if (!isChainValidated) {
            Duolingo3DButton(
                title = "📜 Verify Certificate Chain",
                style = Duolingo3DButtonColor.GREEN,
                onClick = {
                    isChainValidated = true
                    onActionSuccess()
                    onComplete()
                },
                modifier = Modifier.fillMaxWidth(0.85f)
            )
        }
    }
}
