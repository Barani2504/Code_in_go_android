package com.simats.codeingo.ui.visualizer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DsaRed
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.InputBorder
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.visualizer.engine.DhEngine
import com.simats.codeingo.ui.visualizer.model.CNSLabMode
import kotlin.math.roundToInt

@Composable
fun DiffieHellmanVisualizer(mode: CNSLabMode = CNSLabMode.EXPLORE) {
    when (mode) {
        CNSLabMode.CHALLENGE -> {
            CNSChallengeCardView(labType = DSAVisualizerType.BINARY_TREE)
        }
        CNSLabMode.CODE_TEST -> {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                CNSTestVectorCardView(labType = DSAVisualizerType.BINARY_TREE)
                CNSLanguageImplementationCardView(structureName = DSAVisualizerType.BINARY_TREE.title)
            }
        }
        CNSLabMode.ATTACK -> {
            DiffieHellmanAttackView()
        }
        CNSLabMode.EXPLORE -> {
            DiffieHellmanExploreView()
        }
    }
}

@Composable
private fun DiffieHellmanAttackView() {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    val amberColor = if (isDark) AmberGold else Color(0xFFB45309)
    val tealColor = if (isDark) Color(0xFF00CD9C) else Color(0xFF047857)
    val redColor = if (isDark) DsaRed else Color(0xFFDC2626)

    val domain = DhEngine.PRESET_DOMAINS[0]
    val p = domain.p
    val g = domain.g
    val aSecret = 6L
    val bSecret = 15L
    val mallorySecret = 9L

    val honestState = remember { DhEngine.computeHonestExchange(p, g, aSecret, bSecret) }
    val mitmState = remember { DhEngine.computeMitmExchange(p, g, aSecret, bSecret, mallorySecret) }
    val dlogResult = remember { DhEngine.bruteForceDiscreteLog(p, g, honestState.alicePublic) }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                .background(DsaRed.copy(alpha = 0.15f)).border(1.dp, DsaRed.copy(alpha = 0.5f), RoundedCornerShape(10.dp)).padding(12.dp)
        ) {
            Text(
                text = "🔓 ATTACK SUITE: Active Man-in-the-Middle (Mallory) Interception & DLOG Search",
                fontSize = 11.sp, fontWeight = FontWeight.Black, color = redColor
            )
        }

        // Mallory Active MITM
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, DsaRed.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "😈 ACTIVE MALLORY MITM KEY SUBSTITUTION", fontSize = 12.sp, fontWeight = FontWeight.Black, color = redColor)
                Text(text = "Mallory intercepts Alice's public key (A=$g^a mod p) and Bob's public key (B=$g^b mod p). She sends her own public key M = $g^m mod p = ${mitmState.malloryPublic} to both parties.", fontSize = 10.5.sp, color = dynamicColors.textPrimary)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Alice Secret (with Mallory): ${mitmState.aliceComputedSecret}", fontSize = 10.5.sp, fontFamily = FontFamily.Monospace, color = amberColor)
                    Text(text = "Bob Secret (with Mallory): ${mitmState.bobComputedSecret}", fontSize = 10.5.sp, fontFamily = FontFamily.Monospace, color = tealColor)
                }

                Text(
                    text = "Result: Alice and Bob have DIFFERENT secrets! Neither knows they are compromised. Mallory decrypts, reads, and re-encrypts all communications in real time.",
                    fontSize = 10.sp, fontWeight = FontWeight.Bold, color = redColor
                )
            }
        }

        // Discrete Log Brute Force
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, InputBorder, RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "🔢 DISCRETE LOGARITHM (DLOG) BRUTE FORCE", fontSize = 11.5.sp, fontWeight = FontWeight.Black, color = amberColor)
                Text(text = "Given A = ${honestState.alicePublic}, testing g^x mod $p until match: took ${dlogResult.totalSteps} steps (Secret x = ${dlogResult.actualSecret}).", fontSize = 10.5.sp, color = dynamicColors.textPrimary)
                Text(text = "In 2048-bit groups, the search space is 2^2048, rendering brute force mathematically impossible.", fontSize = 10.sp, color = SubtextGray)
            }
        }
    }
}

@Composable
private fun DiffieHellmanExploreView() {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    val amberColor = if (isDark) AmberGold else Color(0xFFB45309)
    val tealColor = if (isDark) Color(0xFF00CD9C) else Color(0xFF047857)
    val greenColor = if (isDark) DuolingoGreen else Color(0xFF15803D)

    val domain = DhEngine.PRESET_DOMAINS[0]
    val p = domain.p
    val g = domain.g

    var aSecret by remember { mutableIntStateOf(6) }
    var bSecret by remember { mutableIntStateOf(15) }

    val honestState = remember(p, g, aSecret, bSecret) {
        DhEngine.computeHonestExchange(p, g, aSecret.toLong(), bSecret.toLong())
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, Color(0xFF00CD9C).copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "PUBLIC DOMAIN: ${domain.name}", fontSize = 11.5.sp, fontWeight = FontWeight.Black, color = tealColor)

                // Alice Slider
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "👩 Alice private a", fontSize = 11.sp, color = tealColor, fontWeight = FontWeight.Bold)
                    Text(text = "a = $aSecret", fontSize = 12.sp, fontWeight = FontWeight.Black, color = tealColor, fontFamily = FontFamily.Monospace)
                }
                Slider(
                    value = aSecret.toFloat(), onValueChange = { aSecret = it.roundToInt().coerceIn(2, (p - 2).toInt()) }, valueRange = 2f..(p - 2).toFloat(),
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF00CD9C), activeTrackColor = Color(0xFF00CD9C))
                )

                // Bob Slider
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "👨 Bob private b", fontSize = 11.sp, color = amberColor, fontWeight = FontWeight.Bold)
                    Text(text = "b = $bSecret", fontSize = 12.sp, fontWeight = FontWeight.Black, color = amberColor, fontFamily = FontFamily.Monospace)
                }
                Slider(
                    value = bSecret.toFloat(), onValueChange = { bSecret = it.roundToInt().coerceIn(2, (p - 2).toInt()) }, valueRange = 2f..(p - 2).toFloat(),
                    colors = SliderDefaults.colors(thumbColor = AmberGold, activeTrackColor = AmberGold)
                )

                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .background(DuolingoGreen.copy(alpha = 0.15f)).border(1.5.dp, DuolingoGreen, RoundedCornerShape(12.dp)).padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(text = "✨ MATCHED SHARED SECRET", fontSize = 11.sp, fontWeight = FontWeight.Black, color = greenColor)
                        Text(text = "K = ${honestState.aliceSharedSecret}", fontSize = 22.sp, fontWeight = FontWeight.Black, color = dynamicColors.textPrimary, fontFamily = FontFamily.Monospace)
                        Text(text = "Alice: B^a mod p = ${honestState.aliceSharedSecret}  |  Bob: A^b mod p = ${honestState.bobSharedSecret}", fontSize = 10.sp, color = SubtextGray, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}
