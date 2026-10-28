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
import com.simats.codeingo.ui.arenas.crypto.ArenaCryptoEngine
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DsaRed
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.visualizer.model.CNSLabMode
import kotlin.math.roundToInt

@Composable
fun ECCPointVisualizer(mode: CNSLabMode = CNSLabMode.EXPLORE) {
    when (mode) {
        CNSLabMode.CHALLENGE -> {
            CNSChallengeCardView(labType = DSAVisualizerType.TRIE)
        }
        CNSLabMode.CODE_TEST -> {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                CNSTestVectorCardView(labType = DSAVisualizerType.TRIE)
                CNSLanguageImplementationCardView(structureName = DSAVisualizerType.TRIE.title)
            }
        }
        CNSLabMode.ATTACK -> {
            ECCPointAttackView()
        }
        CNSLabMode.EXPLORE -> {
            ECCPointExploreView()
        }
    }
}

@Composable
private fun ECCPointAttackView() {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    val cyanColor = if (isDark) Color.Cyan else Color(0xFF0284C7)
    val amberColor = if (isDark) AmberGold else Color(0xFFB45309)
    val redColor = if (isDark) DsaRed else Color(0xFFDC2626)

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                .background(DsaRed.copy(alpha = 0.15f)).border(1.dp, DsaRed.copy(alpha = 0.5f), RoundedCornerShape(10.dp)).padding(12.dp)
        ) {
            Text(
                text = "🔓 ATTACK SUITE: Invalid Curve Point & Small Subgroup Attacks",
                fontSize = 11.sp, fontWeight = FontWeight.Black, color = redColor
            )
        }

        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, cyanColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "⚠️ INVALID CURVE INJECTION ATTACK", fontSize = 12.sp, fontWeight = FontWeight.Black, color = cyanColor)
                Text(text = "If an implementation does not verify that received points satisfy y² ≡ x³ + ax + b (mod p), an attacker can supply points on a weak twist curve with small composite order, solving for the secret scalar k in only a few queries.", fontSize = 10.5.sp, color = dynamicColors.textPrimary)
                Text(text = "Current Group Order = 19 (Prime). At k=19, scalar product hits Point at Infinity 𝒪.", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = amberColor)
            }
        }
    }
}

@Composable
private fun ECCPointExploreView() {
    val dynamicColors = LocalDynamicThemeColors.current
    val cyanColor = if (dynamicColors.isDark) Color.Cyan else Color(0xFF0284C7)

    var kScalar by remember { mutableIntStateOf(3) }
    val G = ArenaCryptoEngine.ToyF17Curve.G

    val computedPoint = remember(kScalar) {
        ArenaCryptoEngine.ToyF17Curve.multiply(kScalar, G)
    }

    val stepPoints = remember(kScalar) {
        val list = mutableListOf<Pair<Int, String>>()
        for (i in 1..kScalar) {
            val pt = ArenaCryptoEngine.ToyF17Curve.multiply(i, G)
            list.add(i to pt.toString())
        }
        list
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, cyanColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "SCALAR MULTIPLIER (k)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = cyanColor)
                    Text(text = "k = $kScalar  (Q = $kScalar · G)", fontSize = 14.sp, fontWeight = FontWeight.Black, color = cyanColor, fontFamily = FontFamily.Monospace)
                }

                Slider(
                    value = kScalar.toFloat(),
                    onValueChange = { kScalar = it.roundToInt().coerceIn(1, 19) },
                    valueRange = 1f..19f,
                    steps = 17,
                    colors = SliderDefaults.colors(thumbColor = cyanColor, activeTrackColor = cyanColor)
                )

                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .background(cyanColor.copy(alpha = 0.15f)).border(1.dp, cyanColor, RoundedCornerShape(12.dp)).padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(text = "POINT ON CURVE Q = $kScalar · (5, 1)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = cyanColor)
                        Text(text = "$computedPoint", fontSize = 24.sp, fontWeight = FontWeight.Black, color = dynamicColors.textPrimary, fontFamily = FontFamily.Monospace)
                        Text(text = if (kScalar == 19) "Point at Infinity 𝒪 (Identity Element)" else "Group addition via chord-and-tangent slope m in F₁₇", fontSize = 10.sp, color = SubtextGray)
                    }
                }

                // Sequence pills
                Text(text = "GENERATED SUBGROUP SEQUENCE:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SubtextGray)
                Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    stepPoints.forEach { (stepK, ptStr) ->
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(6.dp))
                                .background(if (stepK == kScalar) cyanColor else dynamicColors.inputBackground)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${stepK}G: $ptStr",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (stepK == kScalar) Color.Black else dynamicColors.textPrimary,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}
