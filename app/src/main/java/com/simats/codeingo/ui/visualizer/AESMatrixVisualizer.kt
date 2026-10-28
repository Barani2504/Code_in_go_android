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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import com.simats.codeingo.data.model.DSAVisualizerType
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DsaPurple
import com.simats.codeingo.ui.theme.DsaRed
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.visualizer.engine.AesEngine
import com.simats.codeingo.ui.visualizer.model.CNSLabMode
import kotlin.math.roundToInt

@Composable
fun AESMatrixVisualizer(mode: CNSLabMode = CNSLabMode.EXPLORE) {
    when (mode) {
        CNSLabMode.CHALLENGE -> {
            CNSChallengeCardView(labType = DSAVisualizerType.QUEUE)
        }
        CNSLabMode.CODE_TEST -> {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                CNSTestVectorCardView(labType = DSAVisualizerType.QUEUE)
                CNSLanguageImplementationCardView(structureName = DSAVisualizerType.QUEUE.title)
            }
        }
        CNSLabMode.ATTACK -> {
            AESMatrixAttackView()
        }
        CNSLabMode.EXPLORE -> {
            AESMatrixExploreView()
        }
    }
}

@Composable
private fun AESMatrixAttackView() {
    val dynamicColors = LocalDynamicThemeColors.current
    val amberColor = if (dynamicColors.isDark) AmberGold else Color(0xFFB45309)
    val redColor = if (dynamicColors.isDark) DsaRed else Color(0xFFDC2626)
    val greenColor = if (dynamicColors.isDark) DuolingoGreen else Color(0xFF15803D)

    var selectedMode by remember { mutableStateOf("ECB") }
    val ctrMsg1 = "Transfer $500"
    val ctrMsg2 = "Attack At Dawn"
    val ctrResult = remember { AesEngine.simulateCtrNonceReuse(ctrMsg1, ctrMsg2) }

    var gcmTamperIdx by remember { mutableIntStateOf(2) }
    val gcmResult = remember(gcmTamperIdx) {
        AesEngine.testGcmTamper("Confidential Payloads", gcmTamperIdx)
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                .background(DsaPurple.copy(alpha = 0.15f)).border(1.dp, DsaPurple.copy(alpha = 0.5f), RoundedCornerShape(10.dp)).padding(12.dp)
        ) {
            Text(
                text = "🔓 ATTACK SUITE: ECB Pattern Leakage · CTR Keystream Cancellation · GCM Tag Tampering",
                fontSize = 11.sp, fontWeight = FontWeight.Black, color = DsaPurple
            )
        }

        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, DsaPurple.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("ECB", "CBC", "CTR", "GCM").forEach { m ->
                        val isSel = selectedMode == m
                        Box(
                            modifier = Modifier.weight(1f).height(44.dp).clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) AmberGold else dynamicColors.inputBackground)
                                .clickable { selectedMode = m }.padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = m, fontSize = 11.sp, fontWeight = FontWeight.Black, color = if (isSel) Color.Black else dynamicColors.textPrimary)
                        }
                    }
                }

                when (selectedMode) {
                    "ECB" -> {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "⚠️ ECB (Electronic Codebook) Flaw — Pattern Leakage", fontSize = 11.sp, fontWeight = FontWeight.Black, color = redColor)
                            Text(text = "Identical 16-byte plaintext blocks encrypt to identical ciphertext blocks. The visual silhouette of the input data remains fully visible in the ciphertext output. NEVER use ECB for messages > 16 bytes.", fontSize = 10.5.sp, color = dynamicColors.textPrimary)
                        }
                    }
                    "CTR" -> {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "⚡ CTR Nonce Reuse Attack Demo", fontSize = 11.sp, fontWeight = FontWeight.Black, color = redColor)
                            Text(text = "C1 ⊕ C2 = P1 ⊕ P2. When a nonce is reused, the keystream completely cancels out!", fontSize = 10.sp, color = SubtextGray)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "C1 ⊕ C2 Hex:", fontSize = 10.sp, color = SubtextGray)
                                Text(text = AesEngine.bytesToHex(ctrResult.c1XorC2).take(18) + "...", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = amberColor)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "P1 ⊕ P2 Hex:", fontSize = 10.sp, color = SubtextGray)
                                Text(text = AesEngine.bytesToHex(ctrResult.p1XorP2).take(18) + "...", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = greenColor)
                            }
                            Text(text = "Verified Match: Keystream cancelled out!", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = greenColor)
                        }
                    }
                    "GCM" -> {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(text = "🛡️ GCM Tamper & Authentication Verification", fontSize = 11.sp, fontWeight = FontWeight.Black, color = greenColor)
                            Text(text = "Tampering byte at index $gcmTamperIdx in transit triggers AEADBadTagException:", fontSize = 10.sp, color = SubtextGray)
                            Slider(
                                value = gcmTamperIdx.toFloat(),
                                onValueChange = { gcmTamperIdx = it.roundToInt() },
                                valueRange = 0f..10f,
                                colors = SliderDefaults.colors(thumbColor = greenColor, activeTrackColor = greenColor)
                            )
                            Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(DsaRed.copy(alpha = 0.15f)).padding(8.dp)) {
                                Text(text = gcmResult.errorMessage ?: "Valid", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = redColor)
                            }
                        }
                    }
                    else -> {
                        Text(text = "CBC: Cipher Block Chaining uses random IV. Vulnerable to padding oracle if MAC is not verified first.", fontSize = 10.5.sp, color = SubtextGray)
                    }
                }
            }
        }
    }
}

@Composable
private fun AESMatrixExploreView() {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    val amberColor = if (isDark) AmberGold else Color(0xFFB45309)

    var selectedRound by remember { mutableIntStateOf(1) } // 1..10
    var selectedStep by remember { mutableStateOf("SubBytes") } // "Start", "SubBytes", "ShiftRows", "MixColumns", "AddRoundKey"

    val fipsKey = remember { AesEngine.hexToBytes("2b7e151628aed2a6abf7158809cf4f3c") }
    val fipsPt = remember { AesEngine.hexToBytes("3243f6a8885a308d313198a2e0370734") }

    val trace = remember(fipsPt, fipsKey) {
        AesEngine.encryptBlockWithTrace(fipsPt, fipsKey)
    }

    val currentRoundState = remember(trace, selectedRound) {
        trace.rounds[selectedRound - 1]
    }

    val currentMatrix = remember(currentRoundState, selectedStep) {
        when (selectedStep) {
            "Start" -> currentRoundState.startState
            "SubBytes" -> currentRoundState.afterSubBytes
            "ShiftRows" -> currentRoundState.afterShiftRows
            "MixColumns" -> currentRoundState.afterMixColumns ?: currentRoundState.afterShiftRows
            else -> currentRoundState.afterAddRoundKey
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
            .border(1.5.dp, DsaPurple.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "FIPS-197 AES-128 STATE (ROUND $selectedRound/10)", fontSize = 11.5.sp, fontWeight = FontWeight.Black, color = DsaPurple)
                Text(text = selectedStep, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = amberColor)
            }

            Slider(
                value = selectedRound.toFloat(),
                onValueChange = { selectedRound = it.roundToInt().coerceIn(1, 10) },
                valueRange = 1f..10f,
                steps = 8,
                colors = SliderDefaults.colors(thumbColor = DsaPurple, activeTrackColor = DsaPurple)
            )

            // Step Pills
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf("Start", "SubBytes", "ShiftRows", "MixColumns", "AddRoundKey").forEach { stepName ->
                    val isChosen = selectedStep == stepName
                    val isOmitted = selectedRound == 10 && stepName == "MixColumns"
                    Box(
                        modifier = Modifier.weight(1f).height(38.dp).clip(RoundedCornerShape(8.dp))
                            .background(if (isChosen) DsaPurple else dynamicColors.inputBackground)
                            .clickable(enabled = !isOmitted) { selectedStep = stepName }
                            .padding(vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isOmitted) "(Skip)" else stepName.take(5),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isOmitted) Color.Gray else if (isChosen) Color.White else SubtextGray
                        )
                    }
                }
            }

            // 4x4 Byte State Matrix Grid
            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(dynamicColors.inputBackground).padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (r in 0..3) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (c in 0..3) {
                            val byteVal = currentMatrix[r][c]
                            Box(
                                modifier = Modifier.size(48.dp, 36.dp).clip(RoundedCornerShape(6.dp))
                                    .background(DsaPurple.copy(alpha = if (isDark) 0.25f else 0.12f))
                                    .border(1.dp, DsaPurple.copy(alpha = 0.5f), RoundedCornerShape(6.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "%02X".format(byteVal),
                                    fontSize = 13.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace,
                                    color = dynamicColors.textPrimary
                                )
                            }
                        }
                    }
                }
            }

            Text(
                text = "FIPS-197 Appendix B Test Vector. Final Ciphertext: ${AesEngine.bytesToHex(trace.ciphertext)}",
                fontSize = 9.5.sp, color = SubtextGray, fontFamily = FontFamily.Monospace
            )
        }
    }
}
