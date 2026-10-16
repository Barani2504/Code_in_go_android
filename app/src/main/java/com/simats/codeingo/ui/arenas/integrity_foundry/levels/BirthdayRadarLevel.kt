package com.simats.codeingo.ui.arenas.integrity_foundry.levels

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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
fun BirthdayRadarLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current

    // Interactive HMAC nested padding pipeline
    // Stage 1: Key XOR with ipad (0x36) + Message
    // Stage 2: Inner Hash H_in computed
    // Stage 3: Key XOR with opad (0x5C) + H_in
    // Stage 4: Outer Hash HMAC tag sealed
    var ipadApplied by remember { mutableStateOf(false) }
    var innerHashComputed by remember { mutableStateOf(false) }
    var opadApplied by remember { mutableStateOf(false) }
    var hmacSealed by remember { mutableStateOf(false) }

    val completedStages = listOf(ipadApplied, innerHashComputed, opadApplied, hmacSealed).count { it }
    val progressAnim by animateFloatAsState(targetValue = completedStages / 4.0f, label = "hmacProgress")

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "HMAC NESTED PAD WORKSHOP",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "H((K ⊕ opad) ∥ H((K ⊕ ipad) ∥ M)) — Length-Extension Defense",
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Shield",
                        tint = AmberGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Why Not Plain H(Key ∥ Message)?",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberGold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "In Merkle-Damgård hashes, an attacker can append data to M and continue hashing from H(K ∥ M) without knowing K. HMAC's outer hash completely prevents this length extension attack!",
                    fontSize = 11.5.sp,
                    color = dynamicColors.textPrimary,
                    lineHeight = 16.sp
                )
            }
        }

        // Pipeline Progress Indicator
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "HMAC PIPELINE COMPLETION",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = dynamicColors.textSecondary
                )
                Text(
                    text = "$completedStages / 4 STAGES",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = AmberGold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { progressAnim },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (hmacSealed) DuolingoGreen else AmberGold,
                trackColor = dynamicColors.cardBorder.copy(alpha = 0.3f)
            )
        }

        // Stage 1: Inner Pad (ipad 0x36)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (ipadApplied) DuolingoGreen.copy(alpha = 0.12f)
                    else dynamicColors.cardBackground
                )
                .border(
                    1.dp,
                    if (ipadApplied) DuolingoGreen else dynamicColors.cardBorder.copy(alpha = 0.4f),
                    RoundedCornerShape(14.dp)
                )
                .clickable {
                    ipadApplied = !ipadApplied
                    if (!ipadApplied) {
                        innerHashComputed = false
                        opadApplied = false
                        hmacSealed = false
                    }
                }
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "STAGE 1: INNER PAD (ipad = 0x36)",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (ipadApplied) DuolingoGreen else dynamicColors.textPrimary
                    )
                    Text(
                        text = "K_in = K ⊕ 0x3636...36 repeated for 64 bytes",
                        fontSize = 10.5.sp,
                        fontFamily = FontFamily.Monospace,
                        color = dynamicColors.textSecondary
                    )
                }
                Text(
                    text = if (ipadApplied) "✓ APPLIED" else "+ APPLY ipad",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = if (ipadApplied) DuolingoGreen else AmberGold
                )
            }
        }

        // Stage 2: Inner Hash H(K_in || M)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (innerHashComputed) DuolingoGreen.copy(alpha = 0.12f)
                    else dynamicColors.cardBackground
                )
                .border(
                    1.dp,
                    if (innerHashComputed) DuolingoGreen else dynamicColors.cardBorder.copy(alpha = 0.4f),
                    RoundedCornerShape(14.dp)
                )
                .clickable(enabled = ipadApplied) {
                    innerHashComputed = !innerHashComputed
                    if (!innerHashComputed) {
                        opadApplied = false
                        hmacSealed = false
                    }
                }
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "STAGE 2: COMPUTE INNER HASH",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (innerHashComputed) DuolingoGreen else if (ipadApplied) dynamicColors.textPrimary else dynamicColors.textSecondary.copy(alpha = 0.5f)
                    )
                    Text(
                        text = if (innerHashComputed) "H_in = e3b0c44298fc1c149afbf4c8..." else "H(K_in ∥ Message Payload)",
                        fontSize = 10.5.sp,
                        fontFamily = FontFamily.Monospace,
                        color = dynamicColors.textSecondary
                    )
                }
                Text(
                    text = if (innerHashComputed) "✓ COMPUTED" else "HASH INNER",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = if (innerHashComputed) DuolingoGreen else if (ipadApplied) AmberGold else dynamicColors.textSecondary.copy(alpha = 0.4f)
                )
            }
        }

        // Stage 3: Outer Pad (opad 0x5C)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (opadApplied) DuolingoGreen.copy(alpha = 0.12f)
                    else dynamicColors.cardBackground
                )
                .border(
                    1.dp,
                    if (opadApplied) DuolingoGreen else dynamicColors.cardBorder.copy(alpha = 0.4f),
                    RoundedCornerShape(14.dp)
                )
                .clickable(enabled = innerHashComputed) {
                    opadApplied = !opadApplied
                    if (!opadApplied) hmacSealed = false
                }
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "STAGE 3: OUTER PAD (opad = 0x5C)",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (opadApplied) DuolingoGreen else if (innerHashComputed) dynamicColors.textPrimary else dynamicColors.textSecondary.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "K_out = K ⊕ 0x5C5C...5C repeated for 64 bytes",
                        fontSize = 10.5.sp,
                        fontFamily = FontFamily.Monospace,
                        color = dynamicColors.textSecondary
                    )
                }
                Text(
                    text = if (opadApplied) "✓ APPLIED" else "+ APPLY opad",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = if (opadApplied) DuolingoGreen else if (innerHashComputed) AmberGold else dynamicColors.textSecondary.copy(alpha = 0.4f)
                )
            }
        }

        // Stage 4: Outer Hash HMAC Seal
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (hmacSealed) DuolingoGreen.copy(alpha = 0.18f)
                    else dynamicColors.cardBackground
                )
                .border(
                    1.dp,
                    if (hmacSealed) DuolingoGreen else dynamicColors.cardBorder.copy(alpha = 0.4f),
                    RoundedCornerShape(14.dp)
                )
                .clickable(enabled = opadApplied) {
                    hmacSealed = true
                }
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "STAGE 4: SEAL HMAC TAG",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (hmacSealed) DuolingoGreen else if (opadApplied) dynamicColors.textPrimary else dynamicColors.textSecondary.copy(alpha = 0.5f)
                    )
                    Text(
                        text = if (hmacSealed) "HMAC = a591a6d40bf420404a011733cfb7b190..." else "H(K_out ∥ H_in)",
                        fontSize = 10.5.sp,
                        fontFamily = FontFamily.Monospace,
                        color = dynamicColors.textSecondary
                    )
                }
                Text(
                    text = if (hmacSealed) "✓ SEALED" else "SEAL TAG",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = if (hmacSealed) DuolingoGreen else if (opadApplied) AmberGold else dynamicColors.textSecondary.copy(alpha = 0.4f)
                )
            }
        }

        // Verification & Emit Button
        Button(
            onClick = {
                if (hmacSealed) {
                    onActionSuccess()
                    onComplete()
                } else {
                    onDeductHeart()
                }
            },
            enabled = hmacSealed,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (hmacSealed) DuolingoGreen else AmberGold
            )
        ) {
            Text(
                text = if (hmacSealed) "✓ TRANSMIT VERIFIED HMAC TOKEN" else "COMPLETE ALL 4 PIPELINE STAGES",
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                color = Color.Black
            )
        }
    }
}
