package com.simats.codeingo.ui.exercise

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import com.simats.codeingo.ui.theme.PhoenixCrimson
import com.simats.codeingo.ui.theme.PhoenixGreen
import com.simats.codeingo.ui.theme.PhoenixMotion
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
// 1. DUOLINGO-STYLE MATCH PAIRS (Speed Match with 3D Tactile Push Pills)
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
            delay(500)
            wrongPairLeft = null
            wrongPairRight = null
            selectedLeft = null
        }
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Duolingo progress header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (selectedLeft != null) "🔗 Tap match for '$selectedLeft':" else "Tap an item on left, then its match on right:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (selectedLeft != null) AmberGold else DuolingoSubtext
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (matchedPairs.size == solution.size) PhoenixGreen.copy(alpha = 0.2f) else DuolingoInputBg)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "⚡ ${matchedPairs.size}/${solution.size}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = if (matchedPairs.size == solution.size) PhoenixGreen else Color(0xFF1C_B0_F6)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Left Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                leftItems.forEachIndexed { idx, item ->
                    val isMatched = matchedPairs.containsKey(item)
                    val isSelected = selectedLeft == item
                    val isWrong = wrongPairLeft == item

                    DuolingoTactileButton(
                        text = item,
                        isSelected = isSelected,
                        isMatched = isMatched,
                        isWrong = isWrong,
                        isEnabled = !isMatched && !isChecked,
                        onClick = {
                            selectedLeft = if (selectedLeft == item) null else item
                            wrongPairLeft = null
                            wrongPairRight = null
                        },
                        modifier = Modifier
                            .staggeredAppear(index = idx, baseDelayMs = 40)
                            .shake(shakeTrigger = if (isWrong) wrongPairLeft else null)
                            .correctPop(trigger = isMatched)
                    )
                }
            }

            // Right Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rightItems.forEachIndexed { idx, item ->
                    val isMatched = matchedPairs.containsValue(item)
                    val isWrong = wrongPairRight == item

                    DuolingoTactileButton(
                        text = item,
                        isMonospace = true,
                        isSelected = false,
                        isMatched = isMatched,
                        isWrong = isWrong,
                        isEnabled = !isMatched && !isChecked && selectedLeft != null,
                        onClick = {
                            val currentLeft = selectedLeft ?: return@DuolingoTactileButton
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
                        },
                        modifier = Modifier
                            .staggeredAppear(index = idx, baseDelayMs = 40)
                            .shake(shakeTrigger = if (isWrong) wrongPairRight else null)
                            .correctPop(trigger = isMatched)
                    )
                }
            }
        }
    }
}

// =========================================================================
// 2. PARSONS PUZZLE / ORDER STEPS (Tactile 3D Cards with Tap-to-Swap)
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

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            currentSteps.forEachIndexed { index, step ->
                val shape = RoundedCornerShape(14.dp)
                val isCorrectPosition = index < solution.size && solution[index] == step
                val isSelectedForSwap = selectedStepIndex == index
                val depth = 3.dp

                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val currentOffset by animateDpAsState(
                    targetValue = if (isPressed && !isChecked) depth else 0.dp,
                    animationSpec = PhoenixMotion.PressSpringDp,
                    label = "stepOffset"
                )

                val bgColor by animateColorAsState(
                    targetValue = when {
                        isChecked && isCorrectPosition -> PhoenixGreen.copy(alpha = 0.2f)
                        isChecked && !isCorrectPosition -> PhoenixCrimson.copy(alpha = 0.2f)
                        isSelectedForSwap -> AmberGold.copy(alpha = 0.24f)
                        else -> DuolingoInputBg
                    },
                    label = "stepBg"
                )

                val borderColor by animateColorAsState(
                    targetValue = when {
                        isChecked && isCorrectPosition -> PhoenixGreen
                        isChecked && !isCorrectPosition -> PhoenixCrimson
                        isSelectedForSwap -> AmberGold
                        else -> DuolingoInputBorder
                    },
                    label = "stepBorder"
                )

                val shadowColor = when {
                    isChecked && isCorrectPosition -> Color(0xFF1A_AE_47)
                    isChecked && !isCorrectPosition -> Color(0xFFB5_16_06)
                    isSelectedForSwap -> Color(0xFFCC_8C_00)
                    else -> Color(0xFF0C_13_22)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredAppear(index = index, baseDelayMs = 50)
                        .correctPop(trigger = isChecked && isCorrectPosition)
                        .shake(shakeTrigger = if (isChecked && !isCorrectPosition) index else null)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            enabled = !isChecked
                        ) {
                            val activeSelected = selectedStepIndex
                            if (activeSelected == null) {
                                selectedStepIndex = index
                            } else if (activeSelected == index) {
                                selectedStepIndex = null
                            } else {
                                val mutable = currentSteps.toMutableList()
                                val temp = mutable[activeSelected]
                                mutable[activeSelected] = mutable[index]
                                mutable[index] = temp
                                currentSteps = mutable
                                selectedStepIndex = null
                            }
                        }
                ) {
                    // 3D Shadow layer
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .offset(y = depth)
                            .clip(shape)
                            .background(shadowColor)
                    )
                    // Top Face layer
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = currentOffset)
                            .clip(shape)
                            .background(bgColor)
                            .border(if (isSelectedForSwap || (isChecked && isCorrectPosition)) 2.dp else 1.2.dp, borderColor, shape)
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Sequence Number Badge
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
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
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowUp,
                                        contentDescription = "Move Up",
                                        tint = if (index > 0) Color(0xFF1C_B0_F6) else DuolingoSubtext.copy(alpha = 0.25f)
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
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Move Down",
                                        tint = if (index < currentSteps.size - 1) Color(0xFF1C_B0_F6) else DuolingoSubtext.copy(alpha = 0.25f)
                                    )
                                }
                            }
                        } else {
                            Icon(
                                imageVector = if (isCorrectPosition) Icons.Default.CheckCircle else Icons.Default.Close,
                                contentDescription = if (isCorrectPosition) "Correct" else "Incorrect",
                                tint = if (isCorrectPosition) PhoenixGreen else PhoenixCrimson,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// 3. FILL IN CODE (Duolingo Sentence Builder with 3D Chips & Ghost Placeholders)
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
        // macOS / Duolingo Code Window Container
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF111722))
                .border(1.5.dp, DuolingoInputBorder, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header dots + filename
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

            Spacer(modifier = Modifier.height(2.dp))

            // Code Template with Duolingo-style slot
            val parts = codeTemplate.split("[___]")
            if (parts.size >= 2) {
                Text(
                    text = parts[0],
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace,
                    color = Color.White
                )

                // Interactive Slot — tap to eject token back into the bank!
                val slotBorderColor by animateColorAsState(
                    targetValue = when {
                        activeToken == null -> AmberGold
                        isChecked && activeToken == correctToken -> PhoenixGreen
                        isChecked && activeToken != correctToken -> PhoenixCrimson
                        else -> Color(0xFF1C_B0_F6)
                    }, label = "slotBorder"
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            when {
                                activeToken == null -> AmberGold.copy(alpha = 0.12f)
                                isChecked && activeToken == correctToken -> PhoenixGreen.copy(alpha = 0.22f)
                                isChecked && activeToken != correctToken -> PhoenixCrimson.copy(alpha = 0.22f)
                                else -> Color(0xFF1C_B0_F6).copy(alpha = 0.2f)
                            }
                        )
                        .border(1.8.dp, slotBorderColor, RoundedCornerShape(10.dp))
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
                                isChecked && activeToken == correctToken -> PhoenixGreen
                                isChecked && activeToken != correctToken -> PhoenixCrimson
                                else -> Color(0xFF1C_B0_F6)
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

        // Duolingo Word Bank with 3D Chips & Ghost Footprints
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
                    val isSlotted = activeToken == token
                    DuolingoChip(
                        token = token,
                        isSlotted = isSlotted,
                        isEnabled = !isChecked,
                        onClick = {
                            val next = if (isSlotted) null else token
                            selectedToken = next
                            onTokenSelected(next)
                        },
                        modifier = Modifier.staggeredAppear(index = idx, baseDelayMs = 50)
                    )
                }
            }
        }
    }
}

// =========================================================================
// 4. BIG-O COMPLEXITY DIAL (Tactile 3D Speed Tier Cards)
// =========================================================================
private data class ComplexityTier(val label: String, val name: String, val speedTag: String, val color: Color, val shadowColor: Color)

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

    val tiers = remember {
        listOf(
            ComplexityTier("O(1)", "Constant", "⚡ Instant", PhoenixGreen, Color(0xFF1E_8E_3E)),
            ComplexityTier("O(log n)", "Logarithmic", "🚀 Binary", DsaTeal, Color(0xFF00_9E_78)),
            ComplexityTier("O(n)", "Linear", "🏃 Single Pass", Color(0xFF1C_B0_F6), Color(0xFF0F_8A_CC)),
            ComplexityTier("O(n log n)", "Linearithmic", "⚖️ Merge Sort", DsaPurple, Color(0xFF7A_3B_CC)),
            ComplexityTier("O(n²)", "Quadratic", "🐢 Nested", DuolingoOrange, Color(0xFFB5_3C_05)),
            ComplexityTier("O(2ⁿ)", "Exponential", "💥 Recursive", PhoenixCrimson, Color(0xFFA5_14_06))
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
                Text(text = "ANALYZE CODE ALGORITHM", fontSize = 11.sp, fontWeight = FontWeight.Black, color = DuolingoSubtext)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "⏱️", fontSize = 14.sp)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF111722))
                    .padding(10.dp)
            ) {
                Text(text = promptCode, fontSize = 12.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace, color = Color.White)
            }
        }

        // 2-Column Grid of 3D tactile complexity cards
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = "Select Worst-Case Time Complexity:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DuolingoSubtext)

            for (i in tiers.indices step 2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    for (j in 0..1) {
                        val item = tiers.getOrNull(i + j)
                        if (item != null) {
                            val isSelected = activeComplexity == item.label
                            val isCorrect = item.label == correctComplexity
                            val depth = 3.dp

                            val interactionSource = remember { MutableInteractionSource() }
                            val isPressed by interactionSource.collectIsPressedAsState()
                            val currentOffset by animateDpAsState(
                                targetValue = if (isPressed && !isChecked) depth else 0.dp,
                                animationSpec = PhoenixMotion.PressSpringDp,
                                label = "complexityOffset"
                            )

                            val shape = RoundedCornerShape(14.dp)
                            val borderColor by animateColorAsState(
                                targetValue = when {
                                    isChecked && isSelected && isCorrect -> PhoenixGreen
                                    isChecked && isSelected && !isCorrect -> PhoenixCrimson
                                    isChecked && isCorrect -> PhoenixGreen
                                    isSelected -> item.color
                                    else -> DuolingoInputBorder
                                }, label = "compBorder"
                            )
                            val cardShadow = when {
                                isChecked && isSelected && isCorrect -> Color(0xFF1E_8E_3E)
                                isChecked && isSelected && !isCorrect -> Color(0xFFA5_14_06)
                                isChecked && isCorrect -> Color(0xFF1E_8E_3E)
                                isSelected -> item.shadowColor
                                else -> Color(0xFF0C_13_22)
                            }
                            val faceBg by animateColorAsState(
                                targetValue = when {
                                    isChecked && isSelected && isCorrect -> PhoenixGreen.copy(alpha = 0.22f)
                                    isChecked && isSelected && !isCorrect -> PhoenixCrimson.copy(alpha = 0.22f)
                                    isChecked && isCorrect -> PhoenixGreen.copy(alpha = 0.18f)
                                    isSelected -> item.color.copy(alpha = 0.22f)
                                    else -> DuolingoCardBg
                                }, label = "compFace"
                            )

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .staggeredAppear(index = i + j, baseDelayMs = 40)
                                    .clickable(
                                        interactionSource = interactionSource,
                                        indication = null,
                                        enabled = !isChecked
                                    ) {
                                        selectedComplexity = item.label
                                        onComplexitySelected(item.label)
                                    }
                            ) {
                                // 3D Shadow
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .offset(y = depth)
                                        .clip(shape)
                                        .background(cardShadow)
                                )
                                // Face
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .offset(y = currentOffset)
                                        .clip(shape)
                                        .background(faceBg)
                                        .border(if (isSelected || (isChecked && isCorrect)) 2.dp else 1.2.dp, borderColor, shape)
                                        .padding(horizontal = 12.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(item.color))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.label,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace,
                                            color = LocalDynamicThemeColors.current.textPrimary
                                        )
                                        Text(text = item.speedTag, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = item.color)
                                    }
                                    if (isChecked && (isSelected || isCorrect)) {
                                        Icon(
                                            imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Close,
                                            contentDescription = null,
                                            tint = if (isCorrect) PhoenixGreen else PhoenixCrimson,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
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
// 5. TRUE / FALSE FACT SWIPE (Tinder Gestures + 3D Duolingo Decision Buttons)
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
            Text(text = "FACT CHECK CHALLENGE", fontSize = 11.sp, fontWeight = FontWeight.Black, color = DuolingoSubtext)
            Text(text = "👈 Drag Left / Right 👉", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AmberGold)
        }

        // Swipeable Fact Card with 3D Depth
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
                                        dragOffsetX.animateTo(300f, tween(150))
                                        chosenDecision = true
                                        onDecisionMade(true)
                                        dragOffsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                    } else if (currentOffset < -80f) {
                                        dragOffsetX.animateTo(-300f, tween(150))
                                        chosenDecision = false
                                        onDecisionMade(false)
                                        dragOffsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                    } else {
                                        dragOffsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                    }
                                }
                            },
                            onHorizontalDrag = { _, dragAmount ->
                                coroutineScope.launch { dragOffsetX.snapTo(dragOffsetX.value + dragAmount) }
                            }
                        )
                    }
                }
                .correctPop(trigger = isChecked && activeDecision == isCorrectTrue)
                .shake(shakeTrigger = if (isChecked && activeDecision != isCorrectTrue) isChecked else null)
                .clip(cardShape)
                .background(
                    when {
                        dragOffsetX.value > 40f -> PhoenixGreen.copy(alpha = 0.2f)
                        dragOffsetX.value < -40f -> PhoenixCrimson.copy(alpha = 0.2f)
                        activeDecision == true -> PhoenixGreen.copy(alpha = 0.16f)
                        activeDecision == false -> PhoenixCrimson.copy(alpha = 0.16f)
                        else -> DuolingoInputBg
                    }
                )
                .border(
                    width = if (activeDecision != null) 2.dp else 1.5.dp,
                    color = when {
                        dragOffsetX.value > 40f -> PhoenixGreen
                        dragOffsetX.value < -40f -> PhoenixCrimson
                        activeDecision == null -> DuolingoInputBorder
                        isChecked -> if (activeDecision == isCorrectTrue) PhoenixGreen else PhoenixCrimson
                        activeDecision == true -> PhoenixGreen
                        else -> PhoenixCrimson
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
                if (dragOffsetX.value > 50f) {
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(PhoenixGreen).padding(horizontal = 14.dp, vertical = 4.dp)
                    ) { Text("TRUE 👉", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White) }
                } else if (dragOffsetX.value < -50f) {
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(PhoenixCrimson).padding(horizontal = 14.dp, vertical = 4.dp)
                    ) { Text("👈 FALSE", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White) }
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
                            color = if (activeDecision) PhoenixGreen else PhoenixCrimson
                        )
                    }
                }
            }
        }

        // Two 3D Tactile Push Buttons (TRUE / FALSE)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // TRUE 3D Push Button
            DuolingoDecisionButton(
                title = "TRUE",
                iconText = "✅",
                color = PhoenixGreen,
                shadowColor = Color(0xFF1E_8E_3E),
                isSelected = activeDecision == true,
                isEnabled = !isChecked,
                onClick = {
                    chosenDecision = true
                    onDecisionMade(true)
                },
                modifier = Modifier.weight(1f)
            )

            // FALSE 3D Push Button
            DuolingoDecisionButton(
                title = "FALSE",
                iconText = "❌",
                color = PhoenixCrimson,
                shadowColor = Color(0xFFA5_14_06),
                isSelected = activeDecision == false,
                isEnabled = !isChecked,
                onClick = {
                    chosenDecision = false
                    onDecisionMade(false)
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════
// 🧩 SHARED DUOLINGO 3D TACTILE COMPONENT PRIMITIVES
// ═════════════════════════════════════════════════════════════════════════

/** 3D tactile button for Match Pairs */
@Composable
private fun DuolingoTactileButton(
    text: String,
    isSelected: Boolean,
    isMatched: Boolean,
    isWrong: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isMonospace: Boolean = false
) {
    val depth = 3.dp
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val currentOffset by animateDpAsState(
        targetValue = if ((isPressed && isEnabled) || isSelected) depth else 0.dp,
        animationSpec = PhoenixMotion.PressSpringDp,
        label = "matchOffset"
    )

    val shape = RoundedCornerShape(14.dp)

    val borderColor by animateColorAsState(
        targetValue = when {
            isMatched -> PhoenixGreen
            isWrong -> PhoenixCrimson
            isSelected -> Color(0xFF1C_B0_F6)
            else -> DuolingoInputBorder
        }, label = "matchBorder"
    )

    val shadowColor = when {
        isMatched -> Color(0xFF1A_AE_47)
        isWrong -> Color(0xFFB5_16_06)
        isSelected -> Color(0xFF0F_8A_CC)
        else -> Color(0xFF0C_13_22)
    }

    val faceColor by animateColorAsState(
        targetValue = when {
            isMatched -> PhoenixGreen.copy(alpha = 0.2f)
            isWrong -> PhoenixCrimson.copy(alpha = 0.22f)
            isSelected -> Color(0xFF1C_B0_F6).copy(alpha = 0.28f)
            else -> DuolingoInputBg
        }, label = "matchFace"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = isEnabled,
                onClick = onClick
            )
    ) {
        // Shadow Layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = depth)
                .clip(shape)
                .background(shadowColor)
        )
        // Face Layer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = currentOffset)
                .clip(shape)
                .background(faceColor)
                .border(if (isSelected || isMatched) 2.dp else 1.2.dp, borderColor, shape)
                .padding(horizontal = 12.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = if (isMonospace) FontWeight.Black else FontWeight.Bold,
                fontFamily = if (isMonospace) FontFamily.Monospace else FontFamily.Default,
                color = if (isMatched) PhoenixGreen else LocalDynamicThemeColors.current.textPrimary,
                modifier = Modifier.weight(1f)
            )
            if (isMatched) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Matched",
                    tint = PhoenixGreen,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/** 3D word chip with ghost placeholder for Duolingo sentence builder */
@Composable
private fun DuolingoChip(
    token: String,
    isSlotted: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val depth = 3.dp
    val shape = RoundedCornerShape(12.dp)

    if (isSlotted) {
        // Recessed Ghost Placeholder remaining in the bank
        Box(
            modifier = modifier
                .clip(shape)
                .background(Color.White.copy(alpha = 0.05f))
                .border(1.dp, Color.White.copy(alpha = 0.15f), shape)
                .padding(horizontal = 16.dp, vertical = 11.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = token,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color.Transparent
            )
        }
    } else {
        // Active 3D Tactile Chip
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val currentOffset by animateDpAsState(
            targetValue = if (isPressed && isEnabled) depth else 0.dp,
            animationSpec = PhoenixMotion.PressSpringDp,
            label = "chipOffset"
        )

        Box(
            modifier = modifier.clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = isEnabled,
                onClick = onClick
            )
        ) {
            // Shadow Layer
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(y = depth)
                    .clip(shape)
                    .background(Color(0xFF0C_13_22))
            )
            // Face Layer
            Box(
                modifier = Modifier
                    .offset(y = currentOffset)
                    .clip(shape)
                    .background(DuolingoCardBg)
                    .border(1.2.dp, DuolingoInputBorder, shape)
                    .padding(horizontal = 16.dp, vertical = 11.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = token,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = LocalDynamicThemeColors.current.textPrimary
                )
            }
        }
    }
}

/** 3D push decision button for True/False */
@Composable
private fun DuolingoDecisionButton(
    title: String,
    iconText: String,
    color: Color,
    shadowColor: Color,
    isSelected: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val depth = 3.5.dp
    val shape = RoundedCornerShape(16.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val currentOffset by animateDpAsState(
        targetValue = if ((isPressed && isEnabled) || isSelected) depth else 0.dp,
        animationSpec = PhoenixMotion.PressSpringDp,
        label = "decisionOffset"
    )

    Box(
        modifier = modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = isEnabled,
            onClick = onClick
        )
    ) {
        // Shadow Layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = depth)
                .clip(shape)
                .background(if (isSelected) shadowColor else Color(0xFF0C_13_22))
        )
        // Face Layer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = currentOffset)
                .clip(shape)
                .background(if (isSelected) color.copy(alpha = 0.25f) else DuolingoCardBg)
                .border(if (isSelected) 2.2.dp else 1.2.dp, if (isSelected) color else DuolingoInputBorder, shape)
                .padding(vertical = 15.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Black, color = LocalDynamicThemeColors.current.textPrimary)
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = iconText, fontSize = 16.sp)
        }
    }
}
