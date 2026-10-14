package com.simats.codeingo.ui.exercise

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.simats.codeingo.ui.theme.FeedbackManager
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
// 1. NEURAL SYNAPSE LINK (Drag-to-Match Sentence Arena)
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
    val context = LocalContext.current
    val feedback = remember { FeedbackManager(context) }
    val coroutineScope = rememberCoroutineScope()

    var selectedLeft by remember(leftItems) { mutableStateOf<String?>(null) }
    val matchedPairs = remember(leftItems, rightItems) { mutableStateMapOf<String, String>() }
    var wrongPairLeft by remember(leftItems) { mutableStateOf<String?>(null) }
    var wrongPairRight by remember(rightItems) { mutableStateOf<String?>(null) }

    var activeDraggingLeft by remember { mutableStateOf<String?>(null) }
    var activeDragOffset by remember { mutableStateOf(Offset.Zero) }
    var hoveredTargetRight by remember { mutableStateOf<String?>(null) }

    val settleAnim = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    var isSettling by remember { mutableStateOf(false) }

    val leftItemCentersInWindow = remember { mutableStateMapOf<String, Offset>() }
    val rightBoundsInWindow = remember { mutableStateMapOf<String, Rect>() }

    LaunchedEffect(wrongPairLeft, wrongPairRight) {
        if (wrongPairLeft != null || wrongPairRight != null) {
            delay(500)
            wrongPairLeft = null
            wrongPairRight = null
            selectedLeft = null
        }
    }

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Synapse HUD Header with connection progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "⚡", fontSize = 14.sp)
                    Text(
                        text = when {
                            activeDraggingLeft != null -> "Drag '$activeDraggingLeft' to its pair dock ➔"
                            selectedLeft != null -> "Connecting '$selectedLeft' ➔ Tap or drag to target"
                            else -> "Drag side A sentence to pair with side B:"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (activeDraggingLeft != null || selectedLeft != null) Color(0xFF1C_B0_F6) else DuolingoSubtext
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
                // Left Column: Draggable Concept Terminals with outward socket (—●)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    leftItems.forEachIndexed { idx, item ->
                        val isMatched = matchedPairs.containsKey(item)
                        val isSelected = selectedLeft == item
                        val isWrong = wrongPairLeft == item
                        val isDragging = activeDraggingLeft == item

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .zIndex(if (isDragging) 80f else 1f)
                                .graphicsLayer {
                                    if (isDragging) {
                                        val offset = if (isSettling) settleAnim.value else activeDragOffset
                                        translationX = offset.x
                                        translationY = offset.y
                                        scaleX = 1.05f
                                        scaleY = 1.05f
                                    }
                                }
                                .onGloballyPositioned { coords ->
                                    if (!isDragging && !isSettling) {
                                        leftItemCentersInWindow[item] = coords.boundsInWindow().center
                                    }
                                }
                                .pointerInput(item, isMatched, isChecked) {
                                    if (!isMatched && !isChecked) {
                                        detectDragGestures(
                                            onDragStart = {
                                                activeDraggingLeft = item
                                                selectedLeft = item
                                                activeDragOffset = Offset.Zero
                                                feedback.tap()
                                            },
                                            onDrag = { change, dragAmount ->
                                                change.consume()
                                                activeDragOffset += dragAmount
                                                val centerWindow = (leftItemCentersInWindow[item] ?: Offset.Zero) + activeDragOffset
                                                val hovered = rightBoundsInWindow.entries.firstOrNull { it.value.contains(centerWindow) }?.key
                                                if (hovered != hoveredTargetRight) {
                                                    hoveredTargetRight = hovered
                                                    if (hovered != null) feedback.tap()
                                                }
                                            },
                                            onDragEnd = {
                                                val centerWindow = (leftItemCentersInWindow[item] ?: Offset.Zero) + activeDragOffset
                                                val dropTarget = rightBoundsInWindow.entries.firstOrNull { it.value.contains(centerWindow) }?.key
                                                val isTap = activeDragOffset.getDistance() < 12f

                                                if (dropTarget != null && !matchedPairs.containsValue(dropTarget)) {
                                                    if (solution[item] == dropTarget) {
                                                        matchedPairs[item] = dropTarget
                                                        selectedLeft = null
                                                        wrongPairLeft = null
                                                        wrongPairRight = null
                                                        onMatchedChanged(matchedPairs.toMap())
                                                        feedback.correct()
                                                        activeDraggingLeft = null
                                                        activeDragOffset = Offset.Zero
                                                        hoveredTargetRight = null
                                                    } else {
                                                        wrongPairLeft = item
                                                        wrongPairRight = dropTarget
                                                        feedback.wrong()
                                                        // Soft smooth glide back on mismatch
                                                        coroutineScope.launch {
                                                            isSettling = true
                                                            settleAnim.snapTo(activeDragOffset)
                                                            settleAnim.animateTo(
                                                                Offset.Zero,
                                                                spring(
                                                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                                                    stiffness = Spring.StiffnessLow
                                                                )
                                                            )
                                                            activeDragOffset = Offset.Zero
                                                            isSettling = false
                                                            activeDraggingLeft = null
                                                            hoveredTargetRight = null
                                                        }
                                                    }
                                                } else if (isTap) {
                                                    selectedLeft = if (selectedLeft == item) null else item
                                                    activeDraggingLeft = null
                                                    activeDragOffset = Offset.Zero
                                                    hoveredTargetRight = null
                                                } else {
                                                    // Soft smooth glide back on cancellation
                                                    coroutineScope.launch {
                                                        isSettling = true
                                                        settleAnim.snapTo(activeDragOffset)
                                                        settleAnim.animateTo(
                                                            Offset.Zero,
                                                            spring(
                                                                dampingRatio = Spring.DampingRatioLowBouncy,
                                                                stiffness = Spring.StiffnessLow
                                                            )
                                                        )
                                                        activeDragOffset = Offset.Zero
                                                        isSettling = false
                                                        activeDraggingLeft = null
                                                        hoveredTargetRight = null
                                                    }
                                                }
                                            },
                                            onDragCancel = {
                                                coroutineScope.launch {
                                                    isSettling = true
                                                    settleAnim.snapTo(activeDragOffset)
                                                    settleAnim.animateTo(
                                                        Offset.Zero,
                                                        spring(
                                                            dampingRatio = Spring.DampingRatioLowBouncy,
                                                            stiffness = Spring.StiffnessLow
                                                        )
                                                    )
                                                    activeDragOffset = Offset.Zero
                                                    isSettling = false
                                                    activeDraggingLeft = null
                                                    hoveredTargetRight = null
                                                }
                                            }
                                        )
                                    }
                                }
                        ) {
                            SynapseNodeCard(
                                text = item,
                                isLeft = true,
                                isSelected = isSelected,
                                isMatched = isMatched,
                                isWrong = isWrong,
                                isEnabled = !isMatched && !isChecked,
                                isDragging = isDragging,
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
                }

                // Right Column: Definition Ports with inward socket (●—)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rightItems.forEachIndexed { idx, item ->
                        val isMatched = matchedPairs.containsValue(item)
                        val isWrong = wrongPairRight == item
                        val isHovered = hoveredTargetRight == item

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned { coords ->
                                    rightBoundsInWindow[item] = coords.boundsInWindow()
                                }
                        ) {
                            SynapseNodeCard(
                                text = item,
                                isLeft = false,
                                isMonospace = true,
                                isSelected = false,
                                isMatched = isMatched,
                                isWrong = isWrong,
                                isHovered = isHovered,
                                isEnabled = !isMatched && !isChecked && (selectedLeft != null || activeDraggingLeft != null),
                                onClick = {
                                    val currentLeft = selectedLeft ?: return@SynapseNodeCard
                                    if (solution[currentLeft] == item) {
                                        matchedPairs[currentLeft] = item
                                        selectedLeft = null
                                        wrongPairLeft = null
                                        wrongPairRight = null
                                        onMatchedChanged(matchedPairs.toMap())
                                        feedback.correct()
                                    } else {
                                        wrongPairLeft = currentLeft
                                        wrongPairRight = item
                                        feedback.wrong()
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
    }
}

// =========================================================================
// 2. ALGORITHMIC EXECUTION PIPELINE (Order Steps with Predictive Slot Swapping)
// =========================================================================
@Composable
fun OrderStepsExerciseView(
    initialSteps: List<String>,
    solution: List<String>,
    isChecked: Boolean,
    onStepsChanged: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val feedback = remember { FeedbackManager(context) }
    val spacingPx = remember(density) { with(density) { 10.dp.toPx() } }
    val coroutineScope = rememberCoroutineScope()

    var currentSteps by remember(initialSteps) { mutableStateOf(initialSteps) }
    var draggedStep by remember { mutableStateOf<String?>(null) }
    var dragOffsetY by remember { mutableFloatStateOf(0f) }
    var dragStartIndex by remember { mutableIntStateOf(-1) }
    var targetDropIndex by remember { mutableIntStateOf(-1) }
    var selectedStepIndex by remember(initialSteps) { mutableStateOf<Int?>(null) }
    var itemHeightPx by remember { mutableFloatStateOf(160f) }

    val settleAnim = remember { Animatable(0f) }
    var isSettling by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "🔄", fontSize = 14.sp)
                Text(
                    text = if (draggedStep != null) "Sliding into slot ${targetDropIndex + 1}..." else "Drag cards to arrange execution flow:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (draggedStep != null) AmberGold else DuolingoSubtext
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(DuolingoInputBg)
                    .border(1.dp, DuolingoInputBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "DRAG TO REORDER",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = AmberGold
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            currentSteps.forEachIndexed { index, step ->
                val isDraggedCard = draggedStep == step
                val shape = RoundedCornerShape(14.dp)
                val isCorrectPosition = index < solution.size && solution[index] == step
                val isSelectedForSwap = selectedStepIndex == index
                val depth = if (isDraggedCard) 14.dp else 3.5.dp

                // Calculate visual slot displacement for non-dragged items
                val shiftSlots = when {
                    isDraggedCard -> 0
                    dragStartIndex == -1 -> 0
                    dragStartIndex < targetDropIndex && index in (dragStartIndex + 1)..targetDropIndex -> -1
                    dragStartIndex > targetDropIndex && index in targetDropIndex until dragStartIndex -> 1
                    else -> 0
                }

                val animatedShiftY by animateFloatAsState(
                    targetValue = shiftSlots * itemHeightPx,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    label = "stepShift_$index"
                )

                val dragScale by animateFloatAsState(
                    targetValue = if (isDraggedCard) 1.04f else 1.0f,
                    animationSpec = spring(dampingRatio = 0.8f, stiffness = 380f),
                    label = "orderDragScale"
                )

                val borderColor by animateColorAsState(
                    targetValue = when {
                        isChecked && isCorrectPosition -> PhoenixGreen
                        isChecked && !isCorrectPosition -> PhoenixCrimson
                        isDraggedCard -> AmberGold
                        isSelectedForSwap -> AmberGold
                        else -> DuolingoInputBorder
                    },
                    label = "stepBorder"
                )

                val bgColor by animateColorAsState(
                    targetValue = when {
                        isChecked && isCorrectPosition -> PhoenixGreen.copy(alpha = 0.2f)
                        isChecked && !isCorrectPosition -> PhoenixCrimson.copy(alpha = 0.2f)
                        isDraggedCard -> AmberGold.copy(alpha = 0.22f)
                        isSelectedForSwap -> AmberGold.copy(alpha = 0.25f)
                        else -> DuolingoInputBg
                    },
                    label = "stepBg"
                )

                val shadowColor = when {
                    isChecked && isCorrectPosition -> Color(0xFF1A_AE_47)
                    isChecked && !isCorrectPosition -> Color(0xFFB5_16_06)
                    isDraggedCard -> Color(0xFFCC_8C_00)
                    isSelectedForSwap -> Color(0xFFCC_8C_00)
                    else -> Color(0xFF0C_13_22)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(if (isDraggedCard) 100f else 1f)
                        .graphicsLayer {
                            if (isDraggedCard) {
                                translationY = if (isSettling) settleAnim.value else dragOffsetY
                                scaleX = dragScale
                                scaleY = dragScale
                            } else {
                                translationY = animatedShiftY
                            }
                        }
                        .onGloballyPositioned { coords ->
                            val measuredH = coords.size.height.toFloat() + spacingPx
                            if (measuredH > 50f) itemHeightPx = measuredH
                        }
                        .pointerInput(step, isChecked) {
                            if (!isChecked) {
                                detectDragGestures(
                                    onDragStart = {
                                        val startIdx = currentSteps.indexOf(step)
                                        if (startIdx != -1) {
                                            draggedStep = step
                                            dragStartIndex = startIdx
                                            targetDropIndex = startIdx
                                            dragOffsetY = 0f
                                            feedback.tap()
                                        }
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        dragOffsetY += dragAmount.y

                                        val effectiveH = itemHeightPx.coerceAtLeast(100f)
                                        val rawOffsetSlots = (dragOffsetY / effectiveH).roundToInt()
                                        val newTarget = (dragStartIndex + rawOffsetSlots).coerceIn(0, currentSteps.size - 1)

                                        if (newTarget != targetDropIndex) {
                                            targetDropIndex = newTarget
                                            feedback.tap()
                                        }
                                    },
                                    onDragEnd = {
                                        val isTap = kotlin.math.abs(dragOffsetY) < 10f
                                        val fromIdx = dragStartIndex
                                        val toIdx = targetDropIndex

                                        if (!isTap && fromIdx != -1 && toIdx != -1 && fromIdx != toIdx) {
                                            // Soft glide into the target slot
                                            val targetSettleY = (toIdx - fromIdx) * itemHeightPx
                                            coroutineScope.launch {
                                                isSettling = true
                                                settleAnim.snapTo(dragOffsetY)
                                                settleAnim.animateTo(
                                                    targetSettleY,
                                                    spring(
                                                        dampingRatio = Spring.DampingRatioLowBouncy,
                                                        stiffness = Spring.StiffnessLow
                                                    )
                                                )
                                                // Reorder array cleanly without mid-drag hitching
                                                val mutable = currentSteps.toMutableList()
                                                val item = mutable.removeAt(fromIdx)
                                                mutable.add(toIdx, item)
                                                currentSteps = mutable
                                                onStepsChanged(mutable)

                                                draggedStep = null
                                                dragStartIndex = -1
                                                targetDropIndex = -1
                                                dragOffsetY = 0f
                                                isSettling = false
                                            }
                                        } else {
                                            if (isTap) {
                                                val currIdx = currentSteps.indexOf(step)
                                                val sel = selectedStepIndex
                                                if (sel == null) {
                                                    selectedStepIndex = currIdx
                                                } else if (sel == currIdx) {
                                                    selectedStepIndex = null
                                                } else {
                                                    val mutable = currentSteps.toMutableList()
                                                    val temp = mutable[sel]
                                                    mutable[sel] = mutable[currIdx]
                                                    mutable[currIdx] = temp
                                                    currentSteps = mutable
                                                    selectedStepIndex = null
                                                    onStepsChanged(mutable)
                                                }
                                            }
                                            // Soft spring return to origin
                                            coroutineScope.launch {
                                                if (kotlin.math.abs(dragOffsetY) > 5f) {
                                                    isSettling = true
                                                    settleAnim.snapTo(dragOffsetY)
                                                    settleAnim.animateTo(
                                                        0f,
                                                        spring(
                                                            dampingRatio = Spring.DampingRatioLowBouncy,
                                                            stiffness = Spring.StiffnessLow
                                                        )
                                                    )
                                                }
                                                draggedStep = null
                                                dragStartIndex = -1
                                                targetDropIndex = -1
                                                dragOffsetY = 0f
                                                isSettling = false
                                            }
                                        }
                                        feedback.tap()
                                    },
                                    onDragCancel = {
                                        coroutineScope.launch {
                                            isSettling = true
                                            settleAnim.snapTo(dragOffsetY)
                                            settleAnim.animateTo(
                                                0f,
                                                spring(
                                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                                    stiffness = Spring.StiffnessLow
                                                )
                                            )
                                            draggedStep = null
                                            dragStartIndex = -1
                                            targetDropIndex = -1
                                            dragOffsetY = 0f
                                            isSettling = false
                                        }
                                    }
                                )
                            }
                        }
                        .correctPop(trigger = isChecked && isCorrectPosition)
                        .shake(shakeTrigger = if (isChecked && !isCorrectPosition) index else null)
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
                            .clip(shape)
                            .background(bgColor)
                            .border(if (isDraggedCard || isSelectedForSwap || (isChecked && isCorrectPosition)) 2.dp else 1.2.dp, borderColor, shape)
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Pipeline Step Badge
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isDraggedCard || isSelectedForSwap) AmberGold else AmberGold.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isDraggedCard || isSelectedForSwap) Color.Black else AmberGold
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

                        Spacer(modifier = Modifier.width(8.dp))

                        if (!isChecked) {
                            // 3D Drag Grip Handle with tactile 6-dot matrix
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isDraggedCard) AmberGold.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.06f))
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(2.5.dp)) {
                                        Box(modifier = Modifier.size(3.5.dp).clip(CircleShape).background(if (isDraggedCard) AmberGold else DuolingoSubtext))
                                        Box(modifier = Modifier.size(3.5.dp).clip(CircleShape).background(if (isDraggedCard) AmberGold else DuolingoSubtext))
                                        Box(modifier = Modifier.size(3.5.dp).clip(CircleShape).background(if (isDraggedCard) AmberGold else DuolingoSubtext))
                                    }
                                    Column(verticalArrangement = Arrangement.spacedBy(2.5.dp)) {
                                        Box(modifier = Modifier.size(3.5.dp).clip(CircleShape).background(if (isDraggedCard) AmberGold else DuolingoSubtext))
                                        Box(modifier = Modifier.size(3.5.dp).clip(CircleShape).background(if (isDraggedCard) AmberGold else DuolingoSubtext))
                                        Box(modifier = Modifier.size(3.5.dp).clip(CircleShape).background(if (isDraggedCard) AmberGold else DuolingoSubtext))
                                    }
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
    val context = LocalContext.current
    val feedback = remember { FeedbackManager(context) }
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
                                .pulse(enabled = activeToken == null && !isChecked, scaleRange = 0.98f..1.02f)
                                .correctPop(trigger = activeToken != null)
                                .shake(shakeTrigger = if (isChecked && activeToken != correctToken) isChecked else null)
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
                                    feedback.tap()
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
                            feedback.tap()
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
    val context = LocalContext.current
    val feedback = remember { FeedbackManager(context) }
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
                                        feedback.tap()
                                        dragOffsetX.animateTo(300f, tween(150))
                                        chosenDecision = true
                                        onDecisionMade(true)
                                        dragOffsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                    } else if (currentOffset < -80f) {
                                        feedback.tap()
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
                    feedback.tap()
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
                    feedback.tap()
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

/** Synapse Node Card with Outward / Inward LED Pins & Drag/Dock States */
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
    isMonospace: Boolean = false,
    isHovered: Boolean = false,
    isDragging: Boolean = false
) {
    val depth = if (isDragging) 6.5.dp else 3.5.dp
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val currentOffset by animateDpAsState(
        targetValue = if ((isPressed && isEnabled) || isSelected) depth else 0.dp,
        animationSpec = PhoenixMotion.PressSpringDp,
        label = "matchOffset"
    )

    val shape = RoundedCornerShape(14.dp)

    val scale by animateFloatAsState(
        targetValue = when {
            isDragging -> 1.05f
            isHovered -> 1.03f
            else -> 1.0f
        },
        animationSpec = PhoenixMotion.SnappySpring,
        label = "synapseScale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isMatched) 0.65f else 1.0f,
        animationSpec = tween(350),
        label = "synapseAlpha"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isMatched -> PhoenixGreen
            isWrong -> PhoenixCrimson
            isHovered -> Color(0xFF1C_B0_F6)
            isDragging || isSelected -> Color(0xFF1C_B0_F6)
            else -> DuolingoInputBorder
        }, label = "matchBorder"
    )

    val shadowColor = when {
        isMatched -> Color(0xFF1A_AE_47)
        isWrong -> Color(0xFFB5_16_06)
        isHovered -> Color(0xFF0F_8A_CC)
        isDragging || isSelected -> Color(0xFF0F_8A_CC)
        else -> Color(0xFF0C_13_22)
    }

    val faceColor by animateColorAsState(
        targetValue = when {
            isMatched -> PhoenixGreen.copy(alpha = 0.2f)
            isWrong -> PhoenixCrimson.copy(alpha = 0.22f)
            isHovered -> Color(0xFF1C_B0_F6).copy(alpha = 0.28f)
            isDragging || isSelected -> Color(0xFF1C_B0_F6).copy(alpha = 0.28f)
            else -> DuolingoInputBg
        }, label = "matchFace"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
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
                .border(if (isDragging || isHovered || isSelected || isMatched) 2.dp else 1.2.dp, borderColor, shape)
                .padding(horizontal = 12.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLeft && !isMatched) {
                // Tactile 6-dot drag handle on left items
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Box(modifier = Modifier.size(2.5.dp).clip(CircleShape).background(if (isDragging) Color(0xFF1C_B0_F6) else DuolingoSubtext.copy(alpha = 0.6f)))
                        Box(modifier = Modifier.size(2.5.dp).clip(CircleShape).background(if (isDragging) Color(0xFF1C_B0_F6) else DuolingoSubtext.copy(alpha = 0.6f)))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Box(modifier = Modifier.size(2.5.dp).clip(CircleShape).background(if (isDragging) Color(0xFF1C_B0_F6) else DuolingoSubtext.copy(alpha = 0.6f)))
                        Box(modifier = Modifier.size(2.5.dp).clip(CircleShape).background(if (isDragging) Color(0xFF1C_B0_F6) else DuolingoSubtext.copy(alpha = 0.6f)))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Box(modifier = Modifier.size(2.5.dp).clip(CircleShape).background(if (isDragging) Color(0xFF1C_B0_F6) else DuolingoSubtext.copy(alpha = 0.6f)))
                        Box(modifier = Modifier.size(2.5.dp).clip(CircleShape).background(if (isDragging) Color(0xFF1C_B0_F6) else DuolingoSubtext.copy(alpha = 0.6f)))
                    }
                }
            }

            if (!isLeft) {
                // Inward Receiver Pin on right column items
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .clip(CircleShape)
                        .background(if (isMatched) PhoenixGreen else if (isHovered) Color(0xFF1C_B0_F6) else borderColor)
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
                        .size(9.dp)
                        .clip(CircleShape)
                        .background(if (isMatched) PhoenixGreen else if (isDragging || isSelected) Color(0xFF1C_B0_F6) else SubtextGray.copy(alpha = 0.4f))
                )
            } else if (isMatched) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Matched",
                    tint = PhoenixGreen,
                    modifier = Modifier.size(16.dp)
                )
            } else if (isHovered) {
                Text(
                    text = "DOCK ⚡",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1C_B0_F6)
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
