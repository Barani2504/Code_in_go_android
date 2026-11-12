package com.simats.codeingo.ui.exercise.types

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
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
import com.simats.codeingo.domain.scoring.CnsExerciseScoring
import com.simats.codeingo.ui.components.AppCard
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.AppFontFamily
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.PhoenixCrimson
import com.simats.codeingo.ui.theme.PhoenixGreen

// ══════════════════════════════════════════════════════════════════
// 1. CIPHER WHEEL EXERCISE (Rotate wheel to decrypt Caesar/Vigenere)
// ══════════════════════════════════════════════════════════════════

@Composable
fun CipherWheelExerciseView(
    ciphertext: String,
    targetPlaintext: String,
    onShiftSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    var shift by remember { mutableIntStateOf(0) }

    val liveDecrypted = remember(shift, ciphertext) {
        val s = (shift % 26 + 26) % 26
        ciphertext.uppercase().map { ch ->
            if (ch in 'A'..'Z') {
                val pos = (ch - 'A' - s + 26) % 26
                ('A' + pos)
            } else ch
        }.joinToString("")
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AppCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp, contentPadding = 16.dp) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "CIPHERTEXT: $ciphertext",
                    fontSize = 15.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = dynamicColors.textSecondary
                )
                Text(
                    text = "DECRYPTED PREVIEW: $liveDecrypted",
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    color = if (isDark) AmberGold else Color(0xFFB45309)
                )
            }
        }

        // Shift selector Dial
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                onClick = {
                    shift = (shift - 1 + 26) % 26
                    onShiftSelected(shift)
                }
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease shift", tint = dynamicColors.textPrimary)
            }

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(AmberGold.copy(alpha = 0.15f))
                    .border(2.dp, AmberGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "k = $shift", fontSize = 18.sp, fontWeight = FontWeight.Black, color = dynamicColors.textPrimary)
                    Text(text = "ROT-$shift", fontSize = 10.sp, color = dynamicColors.textSecondary)
                }
            }

            IconButton(
                onClick = {
                    shift = (shift + 1) % 26
                    onShiftSelected(shift)
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Increase shift", tint = dynamicColors.textPrimary)
            }
        }

        Slider(
            value = shift.toFloat(),
            onValueChange = {
                shift = it.toInt()
                onShiftSelected(shift)
            },
            valueRange = 0f..25f,
            steps = 24,
            colors = SliderDefaults.colors(
                thumbColor = AmberGold,
                activeTrackColor = AmberGold
            )
        )
    }
}

// ══════════════════════════════════════════════════════════════════
// 2. BIT FLIPPER EXERCISE (Flip bits & inspect avalanche effect)
// ══════════════════════════════════════════════════════════════════

@Composable
fun BitFlipperExerciseView(
    initialBits: String = "10101100",
    onBitsChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val bitsList = remember { mutableStateListOf(*initialBits.toCharArray().toTypedArray()) }
    val currentBits = bitsList.joinToString("")
    val hammingDist = CnsExerciseScoring.calculateAvalancheHammingDistance(initialBits, currentBits)
    val avalanchePercent = (hammingDist.toFloat() / initialBits.length * 100).toInt()

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Tap any bit to invert ($0 \\leftrightarrow 1$) and observe avalanche propagation:",
            fontSize = 13.sp,
            color = dynamicColors.textSecondary,
            textAlign = TextAlign.Center
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            bitsList.forEachIndexed { index, bitChar ->
                val isFlipped = bitChar != initialBits[index]
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isFlipped) PhoenixCrimson.copy(alpha = 0.25f)
                            else dynamicColors.inputBackground
                        )
                        .border(
                            1.5.dp,
                            if (isFlipped) PhoenixCrimson else dynamicColors.inputBorder,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            bitsList[index] = if (bitChar == '1') '0' else '1'
                            onBitsChanged(bitsList.joinToString(""))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = bitChar.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (isFlipped) PhoenixCrimson else dynamicColors.textPrimary
                    )
                }
            }
        }

        // Avalanche Effect Meter
        AppCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 16.dp, contentPadding = 12.dp) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Avalanche Delta: $hammingDist bits flipped",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = dynamicColors.textPrimary
                )
                Text(
                    text = "$avalanchePercent%",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = if (avalanchePercent >= 50) PhoenixGreen else AmberGold
                )
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// 3. ROUND BUILDER EXERCISE (Arrange round operations in order)
// ══════════════════════════════════════════════════════════════════

@Composable
fun RoundBuilderExerciseView(
    initialSteps: List<String>,
    onOrderChanged: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val steps = remember { mutableStateListOf(*initialSteps.toTypedArray()) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        steps.forEachIndexed { index, stepName ->
            AppCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 14.dp,
                contentPadding = 10.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Step ${index + 1}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberGold,
                        modifier = Modifier.width(55.dp)
                    )
                    Text(
                        text = stepName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = dynamicColors.textPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    if (index > 0) {
                        IconButton(
                            onClick = {
                                val temp = steps[index]
                                steps[index] = steps[index - 1]
                                steps[index - 1] = temp
                                onOrderChanged(steps.toList())
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.ArrowUpward, contentDescription = "Move up", tint = dynamicColors.textPrimary)
                        }
                    }
                    if (index < steps.size - 1) {
                        IconButton(
                            onClick = {
                                val temp = steps[index]
                                steps[index] = steps[index + 1]
                                steps[index + 1] = temp
                                onOrderChanged(steps.toList())
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.ArrowDownward, contentDescription = "Move down", tint = dynamicColors.textPrimary)
                        }
                    }
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// 4. MODULAR MATH EXERCISE (Keypad calculation for RSA / DH)
// ══════════════════════════════════════════════════════════════════

@Composable
fun ModularMathExerciseView(
    equationPrompt: String,
    onAnswerChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    var inputString by remember { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        AppCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 18.dp,
            contentPadding = 16.dp
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = equationPrompt,
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    color = dynamicColors.textPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(dynamicColors.inputBackground)
                        .border(1.5.dp, AmberGold, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (inputString.isEmpty()) "Enter result" else inputString,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (inputString.isEmpty()) dynamicColors.textSecondary else dynamicColors.textPrimary
                    )
                }
            }
        }

        // Numeric Keypad
        val keys = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("C", "0", "⌫")
        )

        Column(
            modifier = Modifier.fillMaxWidth(0.85f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            keys.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { key ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(dynamicColors.inputBackground)
                                .border(1.dp, dynamicColors.inputBorder, RoundedCornerShape(12.dp))
                                .clickable {
                                    when (key) {
                                        "C" -> inputString = ""
                                        "⌫" -> if (inputString.isNotEmpty()) inputString = inputString.dropLast(1)
                                        else -> if (inputString.length < 8) inputString += key
                                    }
                                    onAnswerChanged(inputString)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = key,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = dynamicColors.textPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// 5. PACKET TRIAGE EXERCISE (Allow / Drop / Suspicious Firewall)
// ══════════════════════════════════════════════════════════════════

data class TriagePacket(
    val id: String,
    val protocol: String,
    val port: Int,
    val flags: String,
    val payloadSnippet: String
)

@Composable
fun PacketTriageExerciseView(
    packets: List<TriagePacket>,
    onClassified: (Map<String, String>) -> Unit,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val classifications = remember { mutableStateMapOf<String, String>() }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        packets.forEach { packet ->
            val currentChoice = classifications[packet.id]
            AppCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 16.dp,
                contentPadding = 12.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${packet.protocol} : PORT ${packet.port}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = dynamicColors.textPrimary
                        )
                        Text(
                            text = "FLAGS: ${packet.flags}",
                            fontSize = 11.sp,
                            color = dynamicColors.textSecondary
                        )
                    }
                    Text(
                        text = "Payload: \"${packet.payloadSnippet}\"",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = dynamicColors.textSecondary
                    )

                    // 3 Actions: ALLOW, SUSPICIOUS, DROP
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("ALLOW" to PhoenixGreen, "SUSPICIOUS" to AmberGold, "DROP" to PhoenixCrimson).forEach { (action, actionColor) ->
                            val isSelected = currentChoice == action
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) actionColor else actionColor.copy(alpha = 0.12f))
                                    .border(1.dp, actionColor, RoundedCornerShape(8.dp))
                                    .clickable {
                                        classifications[packet.id] = action
                                        onClassified(classifications.toMap())
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = action,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isSelected) Color.White else actionColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// 6. SPOT THE FLAW EXERCISE (Tap vulnerable line in transcript)
// ══════════════════════════════════════════════════════════════════

@Composable
fun SpotTheFlawExerciseView(
    codeLines: List<String>,
    onLineSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val dynamicColors = LocalDynamicThemeColors.current
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        codeLines.forEachIndexed { index, line ->
            val isSelected = selectedIndex == index
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isSelected) PhoenixCrimson.copy(alpha = 0.20f)
                        else dynamicColors.inputBackground
                    )
                    .border(
                        1.5.dp,
                        if (isSelected) PhoenixCrimson else dynamicColors.inputBorder,
                        RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        selectedIndex = index
                        onLineSelected(index)
                    }
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${index + 1} ",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = dynamicColors.textSecondary,
                        modifier = Modifier.width(26.dp)
                    )
                    Text(
                        text = line,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) PhoenixCrimson else dynamicColors.textPrimary
                    )
                }
            }
        }
    }
}

