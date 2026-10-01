package com.simats.codeingo.ui.exercise

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DsaBlue
import com.simats.codeingo.ui.theme.DuolingoBlue
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoInputBg
import com.simats.codeingo.ui.theme.DuolingoInputBorder
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.InputBorder
import com.simats.codeingo.ui.theme.SubtextGray

// MARK: - 1. Match Pairs Exercise View
@Composable
fun MatchPairsExerciseView(
    leftItems: List<String>,
    rightItems: List<String>,
    solution: Map<String, String>,
    isChecked: Boolean,
    onMatchedChanged: (Map<String, String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedLeft by remember { mutableStateOf<String?>(null) }
    val matchedPairs = remember { mutableStateMapOf<String, String>() }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Tap a concept on the left, then its match on the right:",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = SubtextGray
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Left Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                leftItems.forEach { item ->
                    val isMatched = matchedPairs.containsKey(item)
                    val isSelected = selectedLeft == item
                    val shape = RoundedCornerShape(14.dp)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(shape)
                            .background(
                                when {
                                    isMatched -> DuolingoGreen.copy(alpha = 0.2f)
                                    isSelected -> DuolingoBlue.copy(alpha = 0.25f)
                                    else -> DuolingoInputBg
                                }
                            )
                            .border(
                                width = if (isSelected || isMatched) 2.dp else 1.dp,
                                color = when {
                                    isMatched -> DuolingoGreen
                                    isSelected -> DuolingoBlue
                                    else -> DuolingoInputBorder
                                },
                                shape = shape
                            )
                            .clickable(enabled = !isMatched && !isChecked) {
                                selectedLeft = item
                            }
                            .padding(horizontal = 12.dp, vertical = 14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = item,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isMatched) DuolingoGreen else Color.White,
                                modifier = Modifier.weight(1f)
                            )
                            if (isMatched) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = DuolingoGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Right Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rightItems.forEach { item ->
                    val isMatched = matchedPairs.containsValue(item)
                    val shape = RoundedCornerShape(14.dp)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(shape)
                            .background(
                                if (isMatched) DuolingoGreen.copy(alpha = 0.2f) else DuolingoInputBg
                            )
                            .border(
                                width = if (isMatched) 2.dp else 1.dp,
                                color = if (isMatched) DuolingoGreen else DuolingoInputBorder,
                                shape = shape
                            )
                            .clickable(enabled = !isMatched && !isChecked && selectedLeft != null) {
                                val left = selectedLeft
                                if (left != null) {
                                    if (solution[left] == item) {
                                        matchedPairs[left] = item
                                        onMatchedChanged(matchedPairs.toMap())
                                    }
                                    selectedLeft = null
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = item,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isMatched) DuolingoGreen else Color.White,
                                modifier = Modifier.weight(1f)
                            )
                            if (isMatched) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = DuolingoGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// MARK: - 2. Order Steps (Parsons Puzzle)
@Composable
fun OrderStepsExerciseView(
    steps: List<String>,
    solution: List<String>,
    isChecked: Boolean,
    onStepsChanged: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentSteps = remember(steps) { mutableStateListOf(*steps.toTypedArray()) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Arrange the execution steps in correct order:",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = SubtextGray
        )

        Spacer(modifier = Modifier.height(14.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            currentSteps.forEachIndexed { index, step ->
                val shape = RoundedCornerShape(14.dp)
                val isCorrectPosition = index < solution.size && solution[index] == step

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape)
                        .background(DuolingoInputBg)
                        .border(
                            width = 1.2.dp,
                            color = if (isChecked) {
                                if (isCorrectPosition) DuolingoGreen else DuolingoRed
                            } else InputBorder,
                            shape = shape
                        )
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(AmberGold.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = AmberGold
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = step,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Monospace,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )

                    if (!isChecked) {
                        Row {
                            IconButton(
                                onClick = {
                                    if (index > 0) {
                                        val temp = currentSteps[index]
                                        currentSteps[index] = currentSteps[index - 1]
                                        currentSteps[index - 1] = temp
                                        onStepsChanged(currentSteps.toList())
                                    }
                                },
                                enabled = index > 0,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowUp,
                                    contentDescription = "Move Up",
                                    tint = if (index > 0) DuolingoBlue else SubtextGray.copy(alpha = 0.3f)
                                )
                            }

                            IconButton(
                                onClick = {
                                    if (index < currentSteps.size - 1) {
                                        val temp = currentSteps[index]
                                        currentSteps[index] = currentSteps[index + 1]
                                        currentSteps[index + 1] = temp
                                        onStepsChanged(currentSteps.toList())
                                    }
                                },
                                enabled = index < currentSteps.size - 1,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Move Down",
                                    tint = if (index < currentSteps.size - 1) DuolingoBlue else SubtextGray.copy(alpha = 0.3f)
                                )
                            }
                        }
                    } else {
                        Icon(
                            imageVector = if (isCorrectPosition) Icons.Default.CheckCircle else Icons.Default.Close,
                            contentDescription = null,
                            tint = if (isCorrectPosition) DuolingoGreen else DuolingoRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

// MARK: - 3. Fill in Code
@Composable
fun FillCodeExerciseView(
    codeTemplate: String,
    wordBank: List<String>,
    correctToken: String,
    selectedToken: String?,
    isChecked: Boolean,
    onTokenSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val parts = codeTemplate.split("[___]")

    Column(modifier = modifier.fillMaxWidth()) {
        // Code box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F1720))
                .border(1.5.dp, InputBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFF5F56)))
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFFBD2E)))
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF27C93F)))
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (parts.size >= 2) {
                    Text(text = parts[0], fontSize = 13.sp, fontFamily = FontFamily.Monospace, color = Color.White)

                    // The slot
                    Box(
                        modifier = Modifier
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when {
                                    isChecked && selectedToken == correctToken -> DuolingoGreen.copy(alpha = 0.2f)
                                    isChecked && selectedToken != null -> DuolingoRed.copy(alpha = 0.2f)
                                    selectedToken != null -> DuolingoBlue.copy(alpha = 0.2f)
                                    else -> AmberGold.copy(alpha = 0.12f)
                                }
                            )
                            .border(
                                width = 1.5.dp,
                                color = when {
                                    isChecked && selectedToken == correctToken -> DuolingoGreen
                                    isChecked && selectedToken != null -> DuolingoRed
                                    selectedToken != null -> DuolingoBlue
                                    else -> AmberGold.copy(alpha = 0.6f)
                                },
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable(enabled = selectedToken != null && !isChecked) {
                                onTokenSelected(null)
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = selectedToken ?: "[ ? tap token below ]",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = when {
                                isChecked && selectedToken == correctToken -> DuolingoGreen
                                isChecked && selectedToken != null -> DuolingoRed
                                selectedToken != null -> Color.Cyan
                                else -> AmberGold
                            }
                        )
                    }

                    Text(text = parts[1], fontSize = 13.sp, fontFamily = FontFamily.Monospace, color = Color.White)
                } else {
                    Text(text = codeTemplate, fontSize = 13.sp, fontFamily = FontFamily.Monospace, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Word Bank
        Text(
            text = "Select the missing token:",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = SubtextGray
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            wordBank.forEach { token ->
                val isSelected = selectedToken == token
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) DuolingoBlue.copy(alpha = 0.25f) else CardBackground)
                        .border(1.5.dp, if (isSelected) DuolingoBlue else InputBorder, RoundedCornerShape(12.dp))
                        .clickable(enabled = !isChecked) {
                            onTokenSelected(if (isSelected) null else token)
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = token,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = if (isSelected) DuolingoBlue else Color.White
                    )
                }
            }
        }
    }
}

// MARK: - 4. Big-O Complexity Dial
@Composable
fun ComplexityDialExerciseView(
    promptCode: String,
    correctComplexity: String,
    selectedComplexity: String?,
    isChecked: Boolean,
    onComplexitySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val complexities = listOf(
        Triple("O(1)", "Constant", DuolingoGreen),
        Triple("O(log n)", "Logarithmic", Color.Cyan),
        Triple("O(n)", "Linear", DuolingoBlue),
        Triple("O(n log n)", "Linearithmic", Color(0xFFA05AFF)),
        Triple("O(n²)", "Quadratic", Color(0xFFFF9600)),
        Triple("O(2ⁿ)", "Exponential", DuolingoRed)
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F1720))
                .border(1.dp, InputBorder, RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Text(
                text = promptCode,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Select the Worst-Case Time Complexity:",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = SubtextGray
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            complexities.chunked(2).forEach { rowPair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowPair.forEach { (label, name, color) ->
                        val isSelected = selectedComplexity == label
                        val isCorrect = label == correctComplexity
                        val shape = RoundedCornerShape(12.dp)

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(shape)
                                .background(
                                    when {
                                        isChecked && isSelected && isCorrect -> DuolingoGreen.copy(alpha = 0.2f)
                                        isChecked && isSelected && !isCorrect -> DuolingoRed.copy(alpha = 0.2f)
                                        isChecked && isCorrect -> DuolingoGreen.copy(alpha = 0.15f)
                                        isSelected -> color.copy(alpha = 0.25f)
                                        else -> CardBackground
                                    }
                                )
                                .border(
                                    width = 1.5.dp,
                                    color = when {
                                        isChecked && isSelected && isCorrect -> DuolingoGreen
                                        isChecked && isSelected && !isCorrect -> DuolingoRed
                                        isChecked && isCorrect -> DuolingoGreen
                                        isSelected -> color
                                        else -> InputBorder
                                    },
                                    shape = shape
                                )
                                .clickable(enabled = !isChecked) {
                                    onComplexitySelected(label)
                                }
                                .padding(horizontal = 12.dp, vertical = 12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = label,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color.White
                                    )
                                    Text(text = name, fontSize = 10.sp, color = SubtextGray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
