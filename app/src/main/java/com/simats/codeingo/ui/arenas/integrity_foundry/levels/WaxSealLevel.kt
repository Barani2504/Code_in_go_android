package com.simats.codeingo.ui.arenas.integrity_foundry.levels

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.arenas.crypto.ArenaCryptoEngine
import com.simats.codeingo.ui.components.Tactile3DButton
import com.simats.codeingo.ui.components.Tactile3DButtonColor
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.ArenaSuccessGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray

@Composable
fun WaxSealLevel(
    onComplete: () -> Unit,
    onDeductHeart: () -> Unit,
    onActionSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current

    // RFC 2104 HMAC Verification:
    // Key: "CitadelSecretKey"
    // Message: "Transfer=1000Coins"
    val testKey = "CitadelSecretKey".toByteArray()
    val testMsg = "Transfer=1000Coins".toByteArray()
    val hmacSteps = remember { ArenaCryptoEngine.computeHmacSteps(testKey, testMsg) }

    // Strict 4-stage pipeline (RFC 2104):
    // Stage 1: Key XOR ipad (0x36)
    // Stage 2: Inner Hash H_in = H((K ⊕ ipad) ∥ M)
    // Stage 3: Key XOR opad (0x5C)
    // Stage 4: Outer Tag = H((K ⊕ opad) ∥ H_in)
    var activeStage by remember { mutableIntStateOf(0) }
    val progressAnim by animateFloatAsState(targetValue = activeStage / 4.0f, label = "hmacProgress")

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "HMAC INTEGRITY WAX SEAL WORKSHOP",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "RFC 2104: H((K ⊕ opad) ∥ H((K ⊕ ipad) ∥ M))",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
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
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = AmberGold, modifier = Modifier.size(16.dp))
                    Text(text = "Length-Extension Defense:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AmberGold)
                }
                Text(
                    text = "A naïve hash H(K ∥ M) is vulnerable to Merkle-Damgård length extension. HMAC uses a nested two-pass structure with inner pad (0x36) and outer pad (0x5C), proving authenticity without revealing secret K!",
                    fontSize = 12.sp,
                    color = dynamicColors.textPrimary,
                    lineHeight = 16.sp
                )
            }
        }

        // Progress Bar
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "RFC 2104 PIPELINE PROGRESS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textSecondary)
                Text(text = "$activeStage / 4 STAGES", fontSize = 10.sp, fontWeight = FontWeight.Black, color = AmberGold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { progressAnim },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (activeStage >= 4) ArenaSuccessGreen else AmberGold,
                trackColor = dynamicColors.cardBorder.copy(alpha = 0.3f)
            )
        }

        // 4 Sequential Stages Display
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            val stagesInfo = listOf(
                Pair("Stage 1: K ⊕ ipad (0x36)", "K_ipad = " + hmacSteps.kIpadHex.take(16) + "..."),
                Pair("Stage 2: Inner Digest H_in", "H_in = " + hmacSteps.innerHashHex.take(20) + "..."),
                Pair("Stage 3: K ⊕ opad (0x5C)", "K_opad = " + hmacSteps.kOpadHex.take(16) + "..."),
                Pair("Stage 4: Outer HMAC Wax Seal", "Tag = " + hmacSteps.outerTagHex.take(24) + "...")
            )

            stagesInfo.forEachIndexed { idx, (title, hexSnippet) ->
                val isCompleted = activeStage > idx
                val isCurrent = activeStage == idx

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isCompleted) ArenaSuccessGreen.copy(alpha = 0.15f)
                            else if (isCurrent) AmberGold.copy(alpha = 0.15f)
                            else dynamicColors.cardBorder.copy(alpha = 0.2f)
                        )
                        .border(
                            1.dp,
                            if (isCompleted) ArenaSuccessGreen
                            else if (isCurrent) AmberGold
                            else dynamicColors.cardBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = title,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCompleted) ArenaSuccessGreen else dynamicColors.textPrimary
                            )
                            if (isCompleted || isCurrent) {
                                Text(
                                    text = hexSnippet,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (isCompleted) ArenaSuccessGreen else AmberGold
                                )
                            }
                        }
                        Text(
                            text = if (isCompleted) "✓ SEALED" else if (isCurrent) "ACTIVE" else "PENDING",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isCompleted) ArenaSuccessGreen else SubtextGray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Tactile3DButton(
            title = when (activeStage) {
                0 -> "1. Apply Inner Pad (K ⊕ ipad) ➔"
                1 -> "2. Compute Inner Hash H_in ➔"
                2 -> "3. Apply Outer Pad (K ⊕ opad) ➔"
                3 -> "4. Seal HMAC Outer Tag 🔒"
                else -> "Integrity Seal Verified! Advance ➔"
            },
            style = if (activeStage >= 4) Tactile3DButtonColor.GREEN else Tactile3DButtonColor.AMBER,
            onClick = {
                if (activeStage < 4) {
                    activeStage++
                    onActionSuccess()
                } else {
                    onComplete()
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
