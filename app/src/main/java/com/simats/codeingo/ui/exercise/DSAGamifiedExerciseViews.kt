package com.simats.codeingo.ui.exercise

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DsaPurple
import com.simats.codeingo.ui.theme.DsaTeal
import com.simats.codeingo.ui.theme.DuolingoBlue
import com.simats.codeingo.ui.theme.DuolingoCardBg
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoInputBg
import com.simats.codeingo.ui.theme.DuolingoInputBorder
import com.simats.codeingo.ui.theme.DuolingoOrange
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.DuolingoSubtext
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.theme.bounceOnAppear
import com.simats.codeingo.ui.theme.correctPop
import com.simats.codeingo.ui.theme.pressScale
import com.simats.codeingo.ui.theme.pulse
import com.simats.codeingo.ui.theme.shake
import com.simats.codeingo.ui.theme.staggeredAppear
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

// =========================================================================
// 1. MATCH PAIRS EXERCISE VIEW
// =========================================================================
@Composable
fun MatchPairsExerciseView(
    leftItems: List<String>,
    rightItems: List<String>,
    solution: Map<String, String>,
    isChecked: Boolean,
    onMatchedChanged: (Map<String, String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedLeft by remember(leftItems) { mutableStateOf<String?>(null) }
    val matchedPairs = remember(leftItems, rightItems) { mutableStateMapOf<String, String>() }
    var wrongPairLeft by remember(leftItems) { mutableStateOf<String?>(null) }
    var wrongPairRight by remember(rightItems) { mutableStateOf<String?>(null) }

    LaunchedEffect(wrongPairLeft, wrongPairRight) {
        if (wrongPairLeft != null || wrongPairRight != null) {
            delay(600)
            wrongPairLeft = null
            wrongPairRight = null
            selectedLeft = null
        }
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Gamified status hint
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (selectedLeft != null) "🔗 Match '$selectedLeft' with right:" else "Tap an item on the left, then its match on right:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (selectedLeft != null) AmberGold else DuolingoSubtext
            )
            Text(
                text = "${matchedPairs.size}/${solution.size}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = if (matchedPairs.size == solution.size) DuolingoGreen else DuolingoBlue
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Left Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                leftItems.forEachIndexed { idx, item ->
                    val isMatched = matchedPairs.containsKey(item)
                    val isSelected = selectedLeft == item
                    val isWrong = wrongPairLeft == item
                    val shape = RoundedCornerShape(14.dp)

                    val bgColor by animateColorAsState(
                        targetValue = when {
                            isMatched -> DuolingoGreen.copy(alpha = 0.18f)
                            isWrong -> DuolingoRed.copy(alpha = 0.22f)
                            isSelected -> DuolingoBlue.copy(alpha = 0.28f)
                            else -> DuolingoInputBg
                        },
                        label = "leftBg"
                    )

                    val borderColor by animateColorAsState(
                        targetValue = when {
                            isMatched -> DuolingoGreen
                            isWrong -> DuolingoRed
                            isSelected -> DuolingoBlue
                            else -> DuolingoInputBorder
                        },
                        label = "leftBorder"
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .staggeredAppear(index = idx, baseDelayMs = 50)
                            .shake(shakeTrigger = if (isWrong) wrongPairLeft else null)
                            .correctPop(trigger = isMatched)
                            .pressScale(0.96f)
                            .clip(shape)
                            .background(bgColor)
                            .border(if (isSelected || isMatched) 2.dp else 1.dp, borderColor, shape)
                            .clickable(enabled = !isMatched && !isChecked) {
                                selectedLeft = if (selectedLeft == item) null else item
                                wrongPairLeft = null
                                wrongPairRight = null
                            }
                            .padding(horizontal = 12.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isMatched) DuolingoGreen else LocalDynamicThemeColors.current.textPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        if (isMatched) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Matched",
                                tint = DuolingoGreen,
                                modifier = Modifier.size(16.dp)
                            )
                        } else if (isSelected) {
                            Box(
                                modifier = Modifier.size(8.dp).clip(CircleShape).background(DuolingoBlue)
                            )
                        }
                    }
                }
            }

            // Right Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rightItems.forEachIndexed { idx, item ->
                    val isMatched = matchedPairs.containsValue(item)
                    val isWrong = wrongPairRight == item
                    val shape = RoundedCornerShape(14.dp)

                    val bgColor by animateColorAsState(
                        targetValue = when {
                            isMatched -> DuolingoGreen.copy(alpha = 0.18f)
                            isWrong -> DuolingoRed.copy(alpha = 0.22f)
                            selectedLeft != null -> DuolingoCardBg
                            else -> DuolingoInputBg
                        },
                        label = "rightBg"
                    )

                    val borderColor by animateColorAsState(
                        targetValue = when {
                            isMatched -> DuolingoGreen
                            isWrong -> DuolingoRed
                            selectedLeft != null -> AmberGold.copy(alpha = 0.6f)
                            else -> DuolingoInputBorder
                        },
                        label = "rightBorder"
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .staggeredAppear(index = idx, baseDelayMs = 50)
                            .shake(shakeTrigger = if (isWrong) wrongPairRight else null)
                            .correctPop(trigger = isMatched)
                            .pressScale(0.96f)
                            .clip(shape)
                            .background(bgColor)
                            .border(if (isMatched || isWrong) 2.dp else 1.dp, borderColor, shape)
                            .clickable(enabled = !isMatched && !isChecked && selectedLeft != null) {
                                val currentLeft = selectedLeft ?: return@clickable
                                if (solution[currentLeft] == item) {
                                    matchedPairs[currentLeft] = item
                                    selectedLeft = null
                                    wrongPairLeft = null
                                    wrongPairRight = null
                                    onMatchedChanged(matchedPairs.toMap())
                                } else {
                                    wrongPairLeft = currentLeft
                                    wrongPairRight = item
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = if (isMatched) DuolingoGreen else LocalDynamicThemeColors.current.textPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        if (isMatched) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Matched",
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

// =========================================================================
// 2. PARSONS PUZZLE / ORDER STEPS EXERCISE VIEW (Tap-to-Swap + Arrow Nudges)
// =========================================================================
@Composable
fun OrderStepsExerciseView(
    initialSteps: List<String>,
    solution: List<String>,
    isChecked: Boolean,
    onStepsChanged: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentSteps by remember(initialSteps) { mutableStateOf(initialSteps) }
    var selectedStepIndex by remember(initialSteps) { mutableStateOf<Int?>(null) }

    LaunchedEffect(currentSteps) {
        onStepsChanged(currentSteps)
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (selectedStepIndex != null) "⚡ Tap any other step to swap!" else "Reorder into the correct sequence:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (selectedStepIndex != null) AmberGold else DuolingoSubtext
            )
            if (selectedStepIndex != null) {
                Text(
                    text = "Tap selected to cancel",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SubtextGray
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            currentSteps.forEachIndexed { index, step ->
                val shape = RoundedCornerShape(14.dp)
                val isCorrectPosition = index < solution.size && solution[index] == step
                val isSelectedForSwap = selectedStepIndex == index

                val bgColor by animateColorAsState(
                    targetValue = when {
                        isChecked && isCorrectPosition -> DuolingoGreen.copy(alpha = 0.18f)
                        isChecked && !isCorrectPosition -> DuolingoRed.copy(alpha = 0.18f)
                        isSelectedForSwap -> AmberGold.copy(alpha = 0.22f)
                        else -> DuolingoInputBg
                    },
                    label = "stepBg"
                )

                val borderColor by animateColorAsState(
                    targetValue = when {
                        isChecked && isCorrectPosition -> DuolingoGreen
                        isChecked && !isCorrectPosition -> DuolingoRed
                        isSelectedForSwap -> AmberGold
                        else -> DuolingoInputBorder
                    },
                    label = "stepBorder"
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredAppear(index = index, baseDelayMs = 60)
                        .correctPop(trigger = isChecked && isCorrectPosition)
                        .shake(shakeTrigger = if (isChecked && !isCorrectPosition) index else null)
                        .pressScale(0.97f)
                        .clip(shape)
                        .background(bgColor)
                        .border(if (isSelectedForSwap || (isChecked && isCorrectPosition)) 2.dp else 1.2.dp, borderColor, shape)
                        .clickable(enabled = !isChecked) {
                            val activeSelected = selectedStepIndex
                            if (activeSelected == null) {
                                selectedStepIndex = index
                            } else if (activeSelected == index) {
                                selectedStepIndex = null
                            } else {
                                // Swap items!
                                val mutable = currentSteps.toMutableList()
                                val temp = mutable[activeSelected]
                                mutable[activeSelected] = mutable[index]
                                mutable[index] = temp
                                currentSteps = mutable
                                selectedStepIndex = null
                            }
                        }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sequence Badge
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (isSelectedForSwap) AmberGold else AmberGold.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isSelectedForSwap) Color.Black else AmberGold
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = step,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = LocalDynamicThemeColors.current.textPrimary,
                        modifier = Modifier.weight(1f)
                    )

                    if (!isChecked) {
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    if (index > 0) {
                                        val mutable = currentSteps.toMutableList()
                                        val temp = mutable[index]
                                        mutable[index] = mutable[index - 1]
                                        mutable[index - 1] = temp
                                        currentSteps = mutable
                                        selectedStepIndex = null
                                    }
                                },
                                enabled = index > 0,
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowUp,
                                    contentDescription = "Move Up",
                                    tint = if (index > 0) DuolingoBlue else DuolingoSubtext.copy(alpha = 0.25f)
                                )
                            }

                            IconButton(
                                onClick = {
                                    if (index < currentSteps.size - 1) {
                                        val mutable = currentSteps.toMutableList()
                                        val temp = mutable[index]
                                        mutable[index] = mutable[index + 1]
                                        mutable[index + 1] = temp
                                        currentSteps = mutable
                                        selectedStepIndex = null
                                    }
                                },
                                enabled = index < currentSteps.size - 1,
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Move Down",
                                    tint = if (index < currentSteps.size - 1) DuolingoBlue else DuolingoSubtext.copy(alpha = 0.25f)
                                )
                            }
                        }
                    } else {
                        Icon(
                            imageVector = if (isCorrectPosition) Icons.Default.CheckCircle else Icons.Default.Close,
                            contentDescription = if (isCorrectPosition) "Correct" else "Incorrect",
                            tint = if (isCorrectPosition) DuolingoGreen else DuolingoRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// 3. FILL IN CODE EXERCISE VIEW (Slot Glowing + Pop in/out)
// =========================================================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FillCodeExerciseView(
    codeTemplate: String,
    wordBank: List<String>,
    correctToken: String,
    isChecked: Boolean,
    externalSelectedToken: String? = null,
    onTokenSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedToken by remember(codeTemplate, wordBank) { mutableStateOf<String?>(null) }
    val activeToken = externalSelectedToken ?: selectedToken

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Code Window Container
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF141A22))
                .border(1.5.dp, DuolingoInputBorder, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header: 3 colored circles + file name
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFF5F56)))
                Spacer(modifier = Modifier.width(6.dp))
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFFBD2E)))
                Spacer(modifier = Modifier.width(6.dp))
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF27C93F)))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "algorithm_solution.py",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = DuolingoSubtext
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Code Template with Token Slot
            val parts = codeTemplate.split("[___]")
            if (parts.size >= 2) {
                Text(
                    text = parts[0],
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace,
                    color = Color.White
                )

                // Interactive Token Slot — clickable to eject token!
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            when {
                                activeToken == null -> AmberGold.copy(alpha = 0.14f)
                                isChecked && activeToken == correctToken -> DuolingoGreen.copy(alpha = 0.22f)
                                isChecked && activeToken != correctToken -> DuolingoRed.copy(alpha = 0.22f)
                                else -> Color(0xFF00E5FF).copy(alpha = 0.2f)
                            }
                        )
                        .border(
                            width = 1.8.dp,
                            color = when {
                                activeToken == null -> AmberGold
                                isChecked && activeToken == correctToken -> DuolingoGreen
                                isChecked && activeToken != correctToken -> DuolingoRed
                                else -> Color(0xFF00E5FF)
                            },
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable(enabled = !isChecked && activeToken != null) {
                            selectedToken = null
                            onTokenSelected(null)
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = activeToken ?: "[ ? TAP TOKEN BELOW ]",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = when {
                                activeToken == null -> AmberGold
                                isChecked && activeToken == correctToken -> DuolingoGreen
                                isChecked && activeToken != correctToken -> DuolingoRed
                                else -> Color(0xFF00E5FF)
                            }
                        )
                        if (activeToken != null && !isChecked) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "✕", fontSize = 11.sp, color = SubtextGray)
                        }
                    }
                }

                Text(
                    text = parts[1],
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace,
                    color = Color.White
                )
            } else {
                Text(
                    text = codeTemplate,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace,
                    color = Color.White
                )
            }
        }

        // Word Bank Chips
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "Select token to complete code:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = DuolingoSubtext
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                wordBank.forEachIndexed { idx, token ->
                    val isSelected = activeToken == token
                    val shape = RoundedCornerShape(12.dp)

                    Box(
                        modifier = Modifier
                            .staggeredAppear(index = idx, baseDelayMs = 60)
                            .pressScale(0.94f)
                            .clip(shape)
                            .background(
                                when {
                                    isSelected -> DuolingoBlue.copy(alpha = 0.28f)
                                    else -> DuolingoCardBg
                                }
                            )
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) DuolingoBlue else DuolingoInputBorder,
                                shape = shape
                            )
                            .clickable(enabled = !isChecked) {
                                val next = if (isSelected) null else token
                                selectedToken = next
                                onTokenSelected(next)
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = token,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (isSelected) DuolingoBlue else LocalDynamicThemeColors.current.textPrimary
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// 4. BIG-O COMPLEXITY DIAL EXERCISE VIEW (Speed Tier Badges)
// =========================================================================
private data class ComplexityItem(val label: String, val name: String, val speedTag: String, val color: Color)

@Composable
fun ComplexityDialExerciseView(
    promptCode: String,
    correctComplexity: String,
    isChecked: Boolean,
    externalSelectedComplexity: String? = null,
    onComplexitySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedComplexity by remember(promptCode) { mutableStateOf<String?>(null) }
    val activeComplexity = externalSelectedComplexity ?: selectedComplexity

    val complexities = remember {
        listOf(
            ComplexityItem("O(1)", "Constant", "⚡ Instant", DuolingoGreen),
            ComplexityItem("O(log n)", "Logarithmic", "🚀 Binary", DsaTeal),
            ComplexityItem("O(n)", "Linear", "🏃 Single Pass", DuolingoBlue),
            ComplexityItem("O(n log n)", "Linearithmic", "⚖️ Merge Sort", DsaPurple),
            ComplexityItem("O(n²)", "Quadratic", "🐢 Nested", DuolingoOrange),
            ComplexityItem("O(2ⁿ)", "Exponential", "💥 Recursive", DuolingoRed)
        )
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Code snippet container
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DuolingoInputBg)
                .border(1.2.dp, DuolingoInputBorder, RoundedCornerShape(16.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ANALYZE CODE ALGORITHM",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = DuolingoSubtext
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "⏱️", fontSize = 14.sp)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF141A22))
                    .padding(10.dp)
            ) {
                Text(
                    text = promptCode,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace,
                    color = Color.White
                )
            }
        }

        // 2-Column Complexity Grid with speed tier badges
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "Select the Worst-Case Time Complexity:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = DuolingoSubtext
            )

            for (i in complexities.indices step 2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (j in 0..1) {
                        val item = complexities.getOrNull(i + j)
                        if (item != null) {
                            val isSelected = activeComplexity == item.label
                            val isCorrect = item.label == correctComplexity
                            val shape = RoundedCornerShape(14.dp)

                            val bgColor = when {
                                isChecked && isSelected -> if (isCorrect) DuolingoGreen.copy(alpha = 0.22f) else DuolingoRed.copy(alpha = 0.22f)
                                isChecked && isCorrect -> DuolingoGreen.copy(alpha = 0.18f)
                                isSelected -> item.color.copy(alpha = 0.24f)
                                else -> DuolingoCardBg
                            }

                            val borderColor = when {
                                isChecked && isSelected -> if (isCorrect) DuolingoGreen else DuolingoRed
                                isChecked && isCorrect -> DuolingoGreen
                                isSelected -> item.color
                                else -> DuolingoInputBorder
                            }

                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .staggeredAppear(index = i + j, baseDelayMs = 40)
                                    .pressScale(0.96f)
                                    .clip(shape)
                                    .background(bgColor)
                                    .border(if (isSelected) 2.dp else 1.dp, borderColor, shape)
                                    .clickable(enabled = !isChecked) {
                                        selectedComplexity = item.label
                                        onComplexitySelected(item.label)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(item.color)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.label,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace,
                                        color = LocalDynamicThemeColors.current.textPrimary
                                    )
                                    Text(
                                        text = item.speedTag,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = item.color
                                    )
                                }
                                if (isChecked && isSelected) {
                                    Icon(
                                        imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Close,
                                        contentDescription = null,
                                        tint = if (isCorrect) DuolingoGreen else DuolingoRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// 5. TRUE / FALSE SWIPE EXERCISE VIEW (Tinder-style Drag + Interactive Buttons)
// =========================================================================
@Composable
fun TrueFalseSwipeExerciseView(
    statement: String,
    isCorrectTrue: Boolean,
    explanation: String? = null,
    isChecked: Boolean,
    externalDecision: Boolean? = null,
    onDecisionMade: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var chosenDecision by remember(statement) { mutableStateOf<Boolean?>(null) }
    val activeDecision = externalDecision ?: chosenDecision

    val coroutineScope = rememberCoroutineScope()
    val dragOffsetX = remember(statement) { Animatable(0f) }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Drag hint banner
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "FACT CHECK CHALLENGE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = DuolingoSubtext
            )
            Text(
                text = "👈 Drag Left / Right 👉",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AmberGold
            )
        }

        // Swipeable Fact Card
        val cardShape = RoundedCornerShape(20.dp)
        val rotationDeg = (dragOffsetX.value / 25f).coerceIn(-15f, 15f)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(dragOffsetX.value.roundToInt(), 0) }
                .graphicsLayer { rotationZ = rotationDeg }
                .pointerInput(statement, isChecked) {
                    if (!isChecked) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                val currentOffset = dragOffsetX.value
                                coroutineScope.launch {
                                    if (currentOffset > 80f) {
                                        // Swipe right -> TRUE
                                        dragOffsetX.animateTo(300f, tween(150))
                                        chosenDecision = true
                                        onDecisionMade(true)
                                        dragOffsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                    } else if (currentOffset < -80f) {
                                        // Swipe left -> FALSE
                                        dragOffsetX.animateTo(-300f, tween(150))
                                        chosenDecision = false
                                        onDecisionMade(false)
                                        dragOffsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                    } else {
                                        // Spring back
                                        dragOffsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                    }
                                }
                            },
                            onHorizontalDrag = { _, dragAmount ->
                                coroutineScope.launch {
                                    dragOffsetX.snapTo(dragOffsetX.value + dragAmount)
                                }
                            }
                        )
                    }
                }
                .correctPop(trigger = isChecked && activeDecision == isCorrectTrue)
                .shake(shakeTrigger = if (isChecked && activeDecision != isCorrectTrue) isChecked else null)
                .clip(cardShape)
                .background(
                    when {
                        dragOffsetX.value > 40f -> DuolingoGreen.copy(alpha = 0.2f)
                        dragOffsetX.value < -40f -> DuolingoRed.copy(alpha = 0.2f)
                        activeDecision == true -> DuolingoGreen.copy(alpha = 0.14f)
                        activeDecision == false -> DuolingoRed.copy(alpha = 0.14f)
                        else -> DuolingoInputBg
                    }
                )
                .border(
                    width = if (activeDecision != null) 2.dp else 1.5.dp,
                    color = when {
                        dragOffsetX.value > 40f -> DuolingoGreen
                        dragOffsetX.value < -40f -> DuolingoRed
                        activeDecision == null -> DuolingoInputBorder
                        isChecked -> if (activeDecision == isCorrectTrue) DuolingoGreen else DuolingoRed
                        activeDecision == true -> DuolingoGreen
                        else -> DuolingoRed
                    },
                    shape = cardShape
                )
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Stamp preview during live dragging
                if (dragOffsetX.value > 50f) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DuolingoGreen)
                            .padding(horizontal = 14.dp, vertical = 4.dp)
                    ) {
                        Text("TRUE 👉", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                } else if (dragOffsetX.value < -50f) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DuolingoRed)
                            .padding(horizontal = 14.dp, vertical = 4.dp)
                    ) {
                        Text("👈 FALSE", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                }

                Text(
                    text = statement,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = LocalDynamicThemeColors.current.textPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                if (activeDecision != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black.copy(alpha = 0.35f))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (activeDecision) "SELECTED: TRUE ✅" else "SELECTED: FALSE ❌",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = if (activeDecision) DuolingoGreen else DuolingoRed
                        )
                    }
                }
            }
        }

        // Two Interactive Decision Buttons (Tap alternatives with bounce & active glow)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // TRUE Button
            val isTrueSelected = activeDecision == true
            val trueShape = RoundedCornerShape(16.dp)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .bounceOnAppear(delayMs = 0)
                    .pressScale(0.95f)
                    .clip(trueShape)
                    .background(if (isTrueSelected) DuolingoGreen.copy(alpha = 0.25f) else DuolingoCardBg)
                    .border(
                        width = if (isTrueSelected) 2.5.dp else 1.2.dp,
                        color = if (isTrueSelected) DuolingoGreen else DuolingoInputBorder,
                        shape = trueShape
                    )
                    .clickable(enabled = !isChecked) {
                        chosenDecision = true
                        onDecisionMade(true)
                    }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "TRUE", fontSize = 16.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "✅", fontSize = 16.sp)
                }
            }

            // FALSE Button
            val isFalseSelected = activeDecision == false
            val falseShape = RoundedCornerShape(16.dp)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .bounceOnAppear(delayMs = 80)
                    .pressScale(0.95f)
                    .clip(falseShape)
                    .background(if (isFalseSelected) DuolingoRed.copy(alpha = 0.25f) else DuolingoCardBg)
                    .border(
                        width = if (isFalseSelected) 2.5.dp else 1.2.dp,
                        color = if (isFalseSelected) DuolingoRed else DuolingoInputBorder,
                        shape = falseShape
                    )
                    .clickable(enabled = !isChecked) {
                        chosenDecision = false
                        onDecisionMade(false)
                    }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "FALSE", fontSize = 16.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "❌", fontSize = 16.sp)
                }
            }
        }
    }
}
