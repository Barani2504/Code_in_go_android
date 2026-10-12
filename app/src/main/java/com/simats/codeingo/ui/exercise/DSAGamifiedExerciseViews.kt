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
// 1. NEURAL SYNAPSE LINK (Match Pairs with Pointer Nodes & Sockets)
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
        // Synapse HUD Header with connection progress
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "⚡", fontSize = 14.sp)
                Text(
                    text = if (selectedLeft != null) "Connecting '$selectedLeft' →" else "Tap a Concept Node to connect:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedLeft != null) Color(0xFF1C_B0_F6) else DuolingoSubtext
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (matchedPairs.size == solution.size) PhoenixGreen.copy(alpha = 0.2f) else DuolingoInputBg)
                    .border(1.dp, if (matchedPairs.size == solution.size) PhoenixGreen else DuolingoInputBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${matchedPairs.size}/${solution.size} SYNAPSES",
                    fontSize = 11.sp,
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
            // Left Column: Concept Terminals with outward socket (—●)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                leftItems.forEachIndexed { idx, item ->
                    val isMatched = matchedPairs.containsKey(item)
                    val isSelected = selectedLeft == item
                    val isWrong = wrongPairLeft == item

                    SynapseNodeCard(
                        text = item,
                        isLeft = true,
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

            // Right Column: Definition Ports with inward socket (●—)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rightItems.forEachIndexed { idx, item ->
                    val isMatched = matchedPairs.containsValue(item)
                    val isWrong = wrongPairRight == item

                    SynapseNodeCard(
                        text = item,
                        isLeft = false,
                        isMonospace = true,
                        isSelected = false,
                        isMatched = isMatched,
                        isWrong = isWrong,
                        isEnabled = !isMatched && !isChecked && selectedLeft != null,
                        onClick = {
                            val currentLeft = selectedLeft ?: return@SynapseNodeCard
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
// 2. ALGORITHMIC EXECUTION PIPELINE (Order Steps with 3D Flow Connectors)
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
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "🔄", fontSize = 14.sp)
                Text(
                    text = if (selectedStepIndex != null) "Select target step to swap with:" else "Reorder into algorithmic sequence:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedStepIndex != null) AmberGold else DuolingoSubtext
                )
            }
            if (selectedStepIndex != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AmberGold.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(text = "CANCEL", fontSize = 11.sp, fontWeight = FontWeight.Black, color = AmberGold)
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            currentSteps.forEachIndexed { index, step ->
                val shape = RoundedCornerShape(14.dp)
                val isCorrectPosition = index < solution.size && solution[index] == step
                val isSelectedForSwap = selectedStepIndex == index
                val depth = 3.5.dp

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
                        isSelectedForSwap -> AmberGold.copy(alpha = 0.25f)
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
                        // Pipeline Step Badge
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
// 3. CYBER-IDE HACKER TERMINAL (Fill Code with Gutter Lines & RAM Chips)
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
        // Hacker IDE Editor Window
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F1522))
                .border(1.5.dp, DuolingoInputBorder, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // IDE Window Header Bar
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
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = "PYTHON", fontSize = 10.sp, fontWeight = FontWeight.Black, color = AmberGold)
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Code Canvas with Line Numbers Gutter
            Row(modifier = Modifier.fillMaxWidth()) {
                // Line Numbers Gutter
                Column(
                    modifier = Modifier.padding(end = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("01", fontSize = 12.sp, fontFamily = FontFamily.Monospace, color = Color.White.copy(alpha = 0.25f))
                    Text("02", fontSize = 12.sp, fontFamily = FontFamily.Monospace, color = Color.White.copy(alpha = 0.25f))
                    Text("03", fontSize = 12.sp, fontFamily = FontFamily.Monospace, color = Color.White.copy(alpha = 0.25f))
                }

                // Vertical Gutter Separator
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(68.dp)
                        .background(Color.White.copy(alpha = 0.1f))
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Code snippet with glowing drop slot
                val parts = codeTemplate.split("[___]")
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (parts.size >= 2) {
                        Text(
                            text = parts[0],
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White
                        )

                        // Interactive Drop Slot (clickable to eject token)
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
                                .padding(horizontal = 14.dp, vertical = 7.dp)
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
            }
        }

        // Memory RAM Chips Word Bank
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
                    RamChip(
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
// 4. TACHOMETER COMPLEXITY MATRIX (Visual Latency Speedometer Cards)
// =========================================================================
private data class ComplexityTier(val label: String, val name: String, val speedTag: String, val gaugeRatio: Float, val color: Color, val shadowColor: Color)

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
            ComplexityTier("O(1)", "Constant", "⚡ Instant", 0.15f, PhoenixGreen, Color(0xFF1E_8E_3E)),
            ComplexityTier("O(log n)", "Logarithmic", "🚀 Binary", 0.30f, DsaTeal, Color(0xFF00_9E_78)),
            ComplexityTier("O(n)", "Linear", "🏃 Single Pass", 0.50f, Color(0xFF1C_B0_F6), Color(0xFF0F_8A_CC)),
            ComplexityTier("O(n log n)", "Linearithmic", "⚖️ Merge Sort", 0.70f, DsaPurple, Color(0xFF7A_3B_CC)),
            ComplexityTier("O(n²)", "Quadratic", "🐢 Nested", 0.85f, DuolingoOrange, Color(0xFFB5_3C_05)),
            ComplexityTier("O(2ⁿ)", "Exponential", "💥 Recursive", 1.0f, PhoenixCrimson, Color(0xFFA5_14_06))
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

        // 2-Column Grid of 3D Tachometer cards with latency gauge bars
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
                            val depth = 3.5.dp

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
                                // Face with gauge bar
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .offset(y = currentOffset)
                                        .clip(shape)
                                        .background(faceBg)
                                        .border(if (isSelected || (isChecked && isCorrect)) 2.dp else 1.2.dp, borderColor, shape)
                                        .padding(horizontal = 12.dp, vertical = 12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
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

                                    // Visual Latency Gauge Bar
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(4.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.08f))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(item.gaugeRatio)
                                                .height(4.dp)
                                                .clip(CircleShape)
                                                .background(item.color)
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
// 5. VERDICT ARENA (True/False Fact Check with Dual Portal Gates)
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
        // Portal Hint Banner
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "VERDICT ARENA", fontSize = 11.sp, fontWeight = FontWeight.Black, color = DuolingoSubtext)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🔴 SWIPE LEFT", fontSize = 10.sp, fontWeight = FontWeight.Black, color = PhoenixCrimson)
                Text(text = "•", fontSize = 10.sp, color = DuolingoSubtext)
                Text(text = "SWIPE RIGHT 🟢", fontSize = 10.sp, fontWeight = FontWeight.Black, color = PhoenixGreen)
            }
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
                            text = if (activeDecision) "VERDICT: TRUE ✅" else "VERDICT: FALSE ❌",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = if (activeDecision) PhoenixGreen else PhoenixCrimson
                        )
                    }
                }
            }
        }

        // Two 3D Tactile Arcade Decision Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ArcadePowerButton(
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

            ArcadePowerButton(
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
// 🧩 UNIQUE DSA GAMIFIED PRIMITIVE COMPONENTS
// ═════════════════════════════════════════════════════════════════════════

/** Synapse Node Card with Outward / Inward LED Pins */
@Composable
private fun SynapseNodeCard(
    text: String,
    isLeft: Boolean,
    isSelected: Boolean,
    isMatched: Boolean,
    isWrong: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isMonospace: Boolean = false
) {
    val depth = 3.5.dp
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
        // Face Layer with LED Pin Socket
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
            if (!isLeft) {
                // Inward Receiver Pin on right column items
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isMatched) PhoenixGreen else borderColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = if (isMonospace) FontWeight.Black else FontWeight.Bold,
                fontFamily = if (isMonospace) FontFamily.Monospace else FontFamily.Default,
                color = if (isMatched) PhoenixGreen else LocalDynamicThemeColors.current.textPrimary,
                modifier = Modifier.weight(1f)
            )

            if (isLeft) {
                // Outward Transmitter Pin on left column items
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isMatched) PhoenixGreen else if (isSelected) Color(0xFF1C_B0_F6) else SubtextGray.copy(alpha = 0.4f))
                )
            } else if (isMatched) {
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

/** 3D RAM Data Chip with Ghost Footprint in the Bank */
@Composable
private fun RamChip(
    token: String,
    isSlotted: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val depth = 3.5.dp
    val shape = RoundedCornerShape(12.dp)

    if (isSlotted) {
        // Recessed Ghost Footprint Placeholder in the bank
        Box(
            modifier = modifier
                .clip(shape)
                .background(Color.White.copy(alpha = 0.04f))
                .border(1.dp, Color.White.copy(alpha = 0.12f), shape)
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
        // Active 3D RAM Data Chip
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
                    .background(Color(0xFF0A_10_1D))
            )
            // Face Layer
            Row(
                modifier = Modifier
                    .offset(y = currentOffset)
                    .clip(shape)
                    .background(DuolingoCardBg)
                    .border(1.2.dp, DuolingoInputBorder, shape)
                    .padding(horizontal = 16.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mini Chip LED Pin
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1C_B0_F6))
                )
                Spacer(modifier = Modifier.width(8.dp))
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

/** 3D Arcade Power Switch Button for True/False */
@Composable
private fun ArcadePowerButton(
    title: String,
    iconText: String,
    color: Color,
    shadowColor: Color,
    isSelected: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val depth = 4.dp
    val shape = RoundedCornerShape(16.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val currentOffset by animateDpAsState(
        targetValue = if ((isPressed && isEnabled) || isSelected) depth else 0.dp,
        animationSpec = PhoenixMotion.PressSpringDp,
        label = "arcadeOffset"
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
