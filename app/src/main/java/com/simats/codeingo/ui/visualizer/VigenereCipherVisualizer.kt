package com.simats.codeingo.ui.visualizer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.data.model.DSAVisualizerType
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DsaTeal
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.visualizer.engine.KasiskiEngine
import com.simats.codeingo.ui.visualizer.model.CNSLabMode

@Composable
fun VigenereCipherVisualizer(mode: CNSLabMode = CNSLabMode.EXPLORE) {
    when (mode) {
        CNSLabMode.CHALLENGE -> {
            CNSChallengeCardView(labType = DSAVisualizerType.LINKED_LIST)
        }
        CNSLabMode.CODE_TEST -> {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                CNSTestVectorCardView(labType = DSAVisualizerType.LINKED_LIST)
                CNSLanguageImplementationCardView(structureName = DSAVisualizerType.LINKED_LIST.title)
            }
        }
        CNSLabMode.ATTACK -> {
            VigenereAttackView()
        }
        CNSLabMode.EXPLORE -> {
            VigenereExploreView()
        }
    }
}

@Composable
private fun VigenereAttackView() {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    val amberColor = if (isDark) AmberGold else Color(0xFFB45309)
    val tealColor = if (isDark) DsaTeal else Color(0xFF0D9488)
    val greenColor = if (isDark) DuolingoGreen else Color(0xFF15803D)

    val ciphertextSample = "DEFENDTHEEASTWALLOFTHECASTLEFROMTHEENEMY"
    val kasiskiResult = remember { KasiskiEngine.kasiskiExamination(ciphertextSample, ngramLen = 3) }
    val ioc = remember { KasiskiEngine.indexOfCoincidence(ciphertextSample) }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                .background(DsaTeal.copy(alpha = 0.15f)).border(1.dp, DsaTeal.copy(alpha = 0.5f), RoundedCornerShape(10.dp)).padding(12.dp)
        ) {
            Text(
                text = "🔓 ATTACK SUITE: Kasiski 3-Gram Distance Spacing & Index of Coincidence (IoC)",
                fontSize = 11.sp, fontWeight = FontWeight.Black, color = tealColor
            )
        }

        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, DsaTeal.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "🔍 KASISKI EXAMINATION (REPEATED 3-GRAMS)", fontSize = 12.sp, fontWeight = FontWeight.Black, color = amberColor)
                Text(text = "Index of Coincidence (IoC): ${"%.4f".format(ioc)}  (English ≈ 0.0667, Flat Polyalphabetic ≈ 0.0385)", fontSize = 10.5.sp, fontFamily = FontFamily.Monospace, color = dynamicColors.textPrimary)

                Text(text = "Repeated 3-grams found: ${kasiskiResult.repeatedNgrams.size}", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = SubtextGray)
                kasiskiResult.repeatedNgrams.take(4).forEach { ng ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "3-gram \"${ng.ngram}\"", fontSize = 10.5.sp, fontFamily = FontFamily.Monospace, color = tealColor)
                        Text(text = "Positions: ${ng.positions}  |  Dists: ${ng.distances}", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = SubtextGray)
                    }
                }

                val candidateLen = kasiskiResult.mostLikelyKeyLength
                Text(text = "🏆 Most Likely Keyword Length by GCD Factors: $candidateLen", fontSize = 11.5.sp, fontWeight = FontWeight.Black, color = greenColor)
            }
        }
    }
}

@Composable
private fun VigenereExploreView() {
    val dynamicColors = LocalDynamicThemeColors.current
    val amberColor = if (dynamicColors.isDark) AmberGold else Color(0xFFB45309)
    val tealColor = if (dynamicColors.isDark) DsaTeal else Color(0xFF0D9488)

    var plaintext by remember { mutableStateOf("DEFEND THE EAST WALL OF THE CASTLE FROM THE ENEMY") }
    var key by remember { mutableStateOf("FORT") }

    val cleanPlain = plaintext.uppercase().filter { it in 'A'..'Z' }
    val cleanKey = key.uppercase().filter { it in 'A'..'Z' }.ifEmpty { "A" }

    val resultText = remember(cleanPlain, cleanKey) {
        cleanPlain.mapIndexed { i, pChar ->
            val kChar = cleanKey[i % cleanKey.length]
            val pVal = pChar - 'A'
            val kVal = kChar - 'A'
            val shifted = (pVal + kVal).mod(26)
            ('A'.code + shifted).toChar()
        }.joinToString("")
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, DsaTeal.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "PLAINTEXT", fontSize = 11.sp, fontWeight = FontWeight.Black, color = tealColor)
                BasicTextField(
                    value = plaintext, onValueChange = { if (it.length <= 60) plaintext = it },
                    textStyle = TextStyle(color = dynamicColors.textPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                    cursorBrush = SolidColor(DsaTeal), modifier = Modifier.fillMaxWidth()
                )
                Text(text = "SECRET KEYWORD", fontSize = 11.sp, fontWeight = FontWeight.Black, color = amberColor)
                BasicTextField(
                    value = key, onValueChange = { if (it.length <= 12) key = it },
                    textStyle = TextStyle(color = amberColor, fontSize = 15.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace),
                    cursorBrush = SolidColor(AmberGold), modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(dynamicColors.inputBackground)
                .border(1.dp, dynamicColors.inputBorder, RoundedCornerShape(14.dp)).padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "REPEATED KEYSTREAM ALIGNMENT", fontSize = 10.sp, fontWeight = FontWeight.Black, color = SubtextGray)
                Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    cleanPlain.take(25).forEachIndexed { i, pChar ->
                        val kChar = cleanKey[i % cleanKey.length]
                        val rChar = resultText.getOrElse(i) { '?' }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(dynamicColors.cardBackground).padding(horizontal = 7.dp, vertical = 5.dp)
                        ) {
                            Text(text = "$pChar", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary)
                            Text(text = "⊕", fontSize = 9.sp, color = SubtextGray)
                            Text(text = "$kChar", fontSize = 12.sp, fontWeight = FontWeight.Black, color = amberColor)
                            Text(text = "=", fontSize = 9.sp, color = SubtextGray)
                            Text(text = "$rChar", fontSize = 13.sp, fontWeight = FontWeight.Black, color = tealColor)
                        }
                    }
                }
            }
        }
    }
}
