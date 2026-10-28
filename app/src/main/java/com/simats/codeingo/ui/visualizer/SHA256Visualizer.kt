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
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.data.model.DSAVisualizerType
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.visualizer.engine.Sha256Engine
import com.simats.codeingo.ui.visualizer.model.CNSLabMode
import kotlin.math.roundToInt

@Composable
fun SHA256Visualizer(mode: CNSLabMode = CNSLabMode.EXPLORE) {
    when (mode) {
        CNSLabMode.CHALLENGE -> {
            CNSChallengeCardView(labType = DSAVisualizerType.GRAPH)
        }
        CNSLabMode.CODE_TEST -> {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                CNSTestVectorCardView(labType = DSAVisualizerType.GRAPH)
                CNSLanguageImplementationCardView(structureName = DSAVisualizerType.GRAPH.title)
            }
        }
        CNSLabMode.ATTACK -> {
            SHA256AttackView()
        }
        CNSLabMode.EXPLORE -> {
            SHA256ExploreView()
        }
    }
}

@Composable
private fun SHA256AttackView() {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    val amberColor = if (isDark) AmberGold else Color(0xFFB45309)
    val purpleColor = if (isDark) Color(0xFFA05AFF) else Color(0xFF7E22CE)
    val greenColor = if (isDark) DuolingoGreen else Color(0xFF15803D)

    val msgA = "Integrity Check A"
    val msgB = "Integrity Check B"
    val traceA = remember { Sha256Engine.hashWithTrace(msgA.toByteArray(Charsets.UTF_8)) }
    val traceB = remember { Sha256Engine.hashWithTrace(msgB.toByteArray(Charsets.UTF_8)) }
    val avalanche = remember { Sha256Engine.computeAvalanche(traceA.finalHash, traceB.finalHash) }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                .background(purpleColor.copy(alpha = 0.15f)).border(1.dp, purpleColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp)).padding(12.dp)
        ) {
            Text(
                text = "🔓 ATTACK SUITE: Strict Avalanche Criterion (SAC) & Length Extension Vulnerability",
                fontSize = 11.sp, fontWeight = FontWeight.Black, color = purpleColor
            )
        }

        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, purpleColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "⚡ BIT-LEVEL HAMMING AVALANCHE ANALYSIS", fontSize = 12.sp, fontWeight = FontWeight.Black, color = amberColor)
                Text(text = "Message A: \"$msgA\"", fontSize = 10.5.sp, fontFamily = FontFamily.Monospace, color = SubtextGray)
                Text(text = "Message B: \"$msgB\"", fontSize = 10.5.sp, fontFamily = FontFamily.Monospace, color = SubtextGray)

                Text(
                    text = "Flipped Bits: ${avalanche.flippedBitsCount} / 256  (${ "%.1f".format(avalanche.percentage) }%)",
                    fontSize = 15.sp, fontWeight = FontWeight.Black, color = if (avalanche.percentage in 40f..60f) greenColor else amberColor
                )
                Text(text = "Notice how a 1-character difference cascades through 64 compression rounds, flipping roughly half the 256 bits uniformly.", fontSize = 10.sp, color = SubtextGray)
            }
        }
    }
}

@Composable
private fun SHA256ExploreView() {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    val amberColor = if (isDark) AmberGold else Color(0xFFB45309)
    val purpleColor = if (isDark) Color(0xFFA05AFF) else Color(0xFF7E22CE)

    var messageA by remember { mutableStateOf("Integrity Check A") }
    var selectedRoundStep by remember { mutableIntStateOf(0) } // 0..63

    val traceA = remember(messageA) {
        Sha256Engine.hashWithTrace(messageA.toByteArray(Charsets.UTF_8))
    }

    val currentBlockTrace = traceA.blocks.firstOrNull()
    val activeRoundState = currentBlockTrace?.steps?.getOrElse(selectedRoundStep) { currentBlockTrace.steps.first() }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, purpleColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "INPUT MESSAGE", fontSize = 11.sp, fontWeight = FontWeight.Black, color = purpleColor)
                BasicTextField(
                    value = messageA, onValueChange = { messageA = it },
                    textStyle = TextStyle(color = dynamicColors.textPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                    cursorBrush = SolidColor(purpleColor), modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (activeRoundState != null) {
            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                    .border(1.5.dp, purpleColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "COMPRESSION ROUND: ${activeRoundState.step}/63", fontSize = 11.sp, fontWeight = FontWeight.Black, color = purpleColor)
                        Text(text = "W[t]: 0x%08X".format(activeRoundState.w), fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = amberColor)
                    }

                    Slider(
                        value = selectedRoundStep.toFloat(),
                        onValueChange = { selectedRoundStep = it.roundToInt().coerceIn(0, 63) },
                        valueRange = 0f..63f,
                        steps = 62,
                        colors = SliderDefaults.colors(thumbColor = purpleColor, activeTrackColor = purpleColor)
                    )

                    // Real Working Registers A..H
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        listOf(
                            "A" to activeRoundState.a, "B" to activeRoundState.b,
                            "C" to activeRoundState.c, "D" to activeRoundState.d,
                            "E" to activeRoundState.e, "F" to activeRoundState.f,
                            "G" to activeRoundState.g, "H" to activeRoundState.h
                        ).forEach { (reg, valInt) ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = reg, fontSize = 10.sp, fontWeight = FontWeight.Black, color = purpleColor)
                                Text(text = "%04X".format((valInt ushr 16) and 0xFFFF), fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = dynamicColors.textPrimary)
                            }
                        }
                    }

                    Text(
                        text = "Final 256-bit Digest: ${traceA.finalHex}",
                        fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = purpleColor
                    )
                }
            }
        }
    }
}
