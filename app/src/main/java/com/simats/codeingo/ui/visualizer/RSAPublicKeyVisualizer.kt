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
import com.simats.codeingo.ui.theme.DsaTeal
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.InputBorder
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.visualizer.engine.RsaEngine
import com.simats.codeingo.ui.visualizer.model.CNSLabMode
import java.math.BigInteger
import kotlin.math.roundToInt

@Composable
fun RSAPublicKeyVisualizer(mode: CNSLabMode = CNSLabMode.EXPLORE) {
    when (mode) {
        CNSLabMode.CHALLENGE -> {
            CNSChallengeCardView(labType = DSAVisualizerType.AVL_TREE)
        }
        CNSLabMode.CODE_TEST -> {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                CNSTestVectorCardView(labType = DSAVisualizerType.AVL_TREE)
                CNSLanguageImplementationCardView(structureName = DSAVisualizerType.AVL_TREE.title)
            }
        }
        CNSLabMode.ATTACK -> {
            RSAPublicKeyAttackView()
        }
        CNSLabMode.EXPLORE -> {
            RSAPublicKeyExploreView()
        }
    }
}

@Composable
private fun RSAPublicKeyAttackView() {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    val amberColor = if (isDark) AmberGold else Color(0xFFB45309)
    val tealColor = if (isDark) DsaTeal else Color(0xFF0D9488)
    val greenColor = if (isDark) DuolingoGreen else Color(0xFF15803D)
    val redColor = if (isDark) DsaRed else Color(0xFFDC2626)

    val rsaKey = remember { RsaEngine.generateKeys(41L, 53L, preferredE = 17) }
    val malleability = remember(rsaKey) {
        RsaEngine.testMalleability(15, 20, rsaKey.e, rsaKey.d, rsaKey.n)
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                .background(DsaRed.copy(alpha = 0.15f)).border(1.dp, DsaRed.copy(alpha = 0.5f), RoundedCornerShape(10.dp)).padding(12.dp)
        ) {
            Text(
                text = "🔓 ATTACK SUITE: Textbook RSA Homomorphic Malleability & OAEP Defense",
                fontSize = 11.sp, fontWeight = FontWeight.Black, color = redColor
            )
        }

        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, DsaRed.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "⚠️ HOMOMORPHIC CIPHERTEXT TAMPERING ATTACK", fontSize = 12.sp, fontWeight = FontWeight.Black, color = redColor)
                Text(text = "In raw unpadded RSA, c1 * c2 mod n decrypts directly to (m1 * m2 mod n) without needing the private key d!", fontSize = 10.5.sp, color = dynamicColors.textPrimary)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "c1 (m1=15): ${malleability.c1}", fontSize = 10.5.sp, fontFamily = FontFamily.Monospace, color = amberColor)
                    Text(text = "c2 (m2=20): ${malleability.c2}", fontSize = 10.5.sp, fontFamily = FontFamily.Monospace, color = tealColor)
                }

                Text(text = "Multiplied Ciphertext Product mod n: ${malleability.cProductModN}", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = dynamicColors.textPrimary)
                Text(text = "Decrypted Output: ${malleability.decryptedProduct} == Expected (15*20 mod n): ${malleability.expectedProductModN}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = greenColor)
                Text(text = "🛡️ Defense: Mandate RSA-OAEP randomized padding to destroy homomorphic properties.", fontSize = 10.sp, color = SubtextGray)
            }
        }
    }
}

@Composable
private fun RSAPublicKeyExploreView() {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    val amberColor = if (isDark) AmberGold else Color(0xFFB45309)
    val tealColor = if (isDark) DsaTeal else Color(0xFF0D9488)
    val greenColor = if (isDark) DuolingoGreen else Color(0xFF15803D)

    val pVal = 41L
    val qVal = 53L
    val rsaKey = remember { RsaEngine.generateKeys(pVal, qVal, preferredE = 17) }
    var messageNum by remember { mutableIntStateOf(65) } // 'A'

    val modExpTrace = remember(messageNum, rsaKey) {
        RsaEngine.traceModExp(BigInteger.valueOf(messageNum.toLong()), rsaKey.e, rsaKey.n)
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, DsaTeal.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "RSA KEYPAIR PARAMETERS", fontSize = 12.sp, fontWeight = FontWeight.Black, color = tealColor)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Primes: p=$pVal, q=$qVal", fontSize = 11.sp, color = SubtextGray)
                    Text(text = "Modulus n: ${rsaKey.n}", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = dynamicColors.textPrimary)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Public e: ${rsaKey.e}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = amberColor, fontFamily = FontFamily.Monospace)
                    Text(text = "Private d: ${rsaKey.d}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = greenColor, fontFamily = FontFamily.Monospace)
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBackground)
                .border(1.5.dp, InputBorder, RoundedCornerShape(16.dp)).padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "MESSAGE SYMBOL (m)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = tealColor)
                    Text(text = "$messageNum ('${messageNum.toChar()}')", fontSize = 13.sp, fontWeight = FontWeight.Black, color = dynamicColors.textPrimary, fontFamily = FontFamily.Monospace)
                }
                Slider(
                    value = messageNum.toFloat(), onValueChange = { messageNum = it.roundToInt() }, valueRange = 2f..100f,
                    colors = SliderDefaults.colors(thumbColor = DsaTeal, activeTrackColor = DsaTeal)
                )

                val ciphertext = modExpTrace.result
                val decrypted = BigInteger.valueOf(ciphertext.toLong()).modPow(rsaKey.d, rsaKey.n)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(AmberGold.copy(alpha = 0.15f)).padding(10.dp)) {
                        Column {
                            Text(text = "CIPHERTEXT (c)", fontSize = 10.sp, color = amberColor, fontWeight = FontWeight.Bold)
                            Text(text = "$ciphertext", fontSize = 15.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = dynamicColors.textPrimary)
                            Text(text = "c = m^e mod n", fontSize = 9.sp, color = SubtextGray)
                        }
                    }
                    Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(DuolingoGreen.copy(alpha = 0.15f)).padding(10.dp)) {
                        Column {
                            Text(text = "DECRYPTED (m)", fontSize = 10.sp, color = greenColor, fontWeight = FontWeight.Bold)
                            Text(text = "$decrypted", fontSize = 15.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = dynamicColors.textPrimary)
                            Text(text = "m = c^d mod n", fontSize = 9.sp, color = SubtextGray)
                        }
                    }
                }
            }
        }
    }
}
