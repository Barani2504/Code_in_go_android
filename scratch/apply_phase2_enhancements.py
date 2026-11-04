import os
import re

def update_all():
    update_dsa_views()
    update_celebration_overlay()
    update_assessment_screen()

def update_dsa_views():
    path = "app/src/main/java/com/simats/codeingo/ui/exercise/DSAGamifiedExerciseViews.kt"
    with open(path, "r", encoding="utf-8") as f:
        content = f.read()

    # 1. Imports
    if "com.simats.codeingo.ui.components.motion.FlipCard" not in content:
        import_marker = "import androidx.compose.animation.animateColorAsState"
        new_imports = """import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.simats.codeingo.ui.components.motion.FlipCard
import com.simats.codeingo.ui.components.motion.CardFace
import com.simats.codeingo.ui.components.motion.ExplodeHost
import com.simats.codeingo.ui.components.motion.AshMotionScheme
import com.simats.codeingo.ui.components.motion.AshMotionModifiers.bounce
import com.simats.codeingo.ui.components.motion.AshMotionModifiers.pulse
import com.simats.codeingo.ui.components.motion.AshMotionModifiers.shake
import com.simats.codeingo.ui.components.motion.AshMotionModifiers.wobble
import com.simats.codeingo.ui.components.motion.AshMotionModifiers.pressSquish
import androidx.compose.animation.animateColorAsState"""
        content = content.replace(import_marker, new_imports, 1)

    # 2. MatchPairsExerciseView: Add Bezier connector canvas
    match_target = """    val leftItemCentersInWindow = remember { mutableStateMapOf<String, Offset>() }
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
    ) {"""

    match_replacement = """    val leftItemCentersInWindow = remember { mutableStateMapOf<String, Offset>() }
    val rightBoundsInWindow = remember { mutableStateMapOf<String, Rect>() }
    var parentTopLeftInWindow by remember { mutableStateOf(Offset.Zero) }

    LaunchedEffect(wrongPairLeft, wrongPairRight) {
        if (wrongPairLeft != null || wrongPairRight != null) {
            delay(500)
            wrongPairLeft = null
            wrongPairRight = null
            selectedLeft = null
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .onGloballyPositioned { coords ->
                parentTopLeftInWindow = coords.boundsInWindow().topLeft
            }
    ) {
        // ── Synapse Dynamic Bezier Connectors ────────────────────────
        Canvas(modifier = Modifier.matchParentSize()) {
            // 1. Confirmed matched pair synapse lines with electric glow
            matchedPairs.forEach { (leftKey, rightVal) ->
                val startInWindow = leftItemCentersInWindow[leftKey]
                val rightRect = rightBoundsInWindow[rightVal]
                if (startInWindow != null && rightRect != null) {
                    val start = startInWindow - parentTopLeftInWindow
                    val end = rightRect.centerLeft - parentTopLeftInWindow
                    val control1 = Offset(start.x + (end.x - start.x) * 0.5f, start.y)
                    val control2 = Offset(start.x + (end.x - start.x) * 0.5f, end.y)
                    val path = Path().apply {
                        moveTo(start.x, start.y)
                        cubicTo(control1.x, control1.y, control2.x, control2.y, end.x, end.y)
                    }
                    // Outer neon pulse aura
                    drawPath(
                        path = path,
                        color = PhoenixGreen.copy(alpha = 0.35f),
                        style = Stroke(width = 6.dp.toPx())
                    )
                    // Core electric laser beam
                    drawPath(
                        path = path,
                        color = PhoenixGreen,
                        style = Stroke(width = 2.5.dp.toPx())
                    )
                }
            }

            // 2. Active dragging synapse beam
            val currentDragItem = activeDraggingLeft
            if (currentDragItem != null) {
                val startInWindow = leftItemCentersInWindow[currentDragItem]
                if (startInWindow != null) {
                    val start = startInWindow - parentTopLeftInWindow
                    val end = start + activeDragOffset
                    val control1 = Offset(start.x + (end.x - start.x) * 0.5f, start.y)
                    val control2 = Offset(start.x + (end.x - start.x) * 0.5f, end.y)
                    val path = Path().apply {
                        moveTo(start.x, start.y)
                        cubicTo(control1.x, control1.y, control2.x, control2.y, end.x, end.y)
                    }
                    drawPath(
                        path = path,
                        color = Color(0xFF1CB0F6).copy(alpha = 0.35f),
                        style = Stroke(width = 5.dp.toPx())
                    )
                    drawPath(
                        path = path,
                        color = Color(0xFF1CB0F6),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }
        }"""
    if match_target in content:
        content = content.replace(match_target, match_replacement, 1)

    # 3. OrderStepsExerciseView: Add drag tilt
    order_target = """                val dragScale by animateFloatAsState(
                    targetValue = if (isDraggedCard) 1.04f else 1.0f,
                    animationSpec = spring(dampingRatio = 0.8f, stiffness = 380f),
                    label = "orderDragScale"
                )

                val borderColor by animateColorAsState"""

    order_replacement = """                val dragScale by animateFloatAsState(
                    targetValue = if (isDraggedCard) 1.04f else 1.0f,
                    animationSpec = spring(dampingRatio = 0.8f, stiffness = 380f),
                    label = "orderDragScale"
                )

                val dragTilt by animateFloatAsState(
                    targetValue = if (isDraggedCard) (dragOffsetY / 30f).coerceIn(-4.5f, 4.5f) else 0f,
                    animationSpec = spring(dampingRatio = 0.8f, stiffness = 380f),
                    label = "orderDragTilt"
                )

                val borderColor by animateColorAsState"""

    if order_target in content:
        content = content.replace(order_target, order_replacement, 1)

    order_graphics_target = """                        .graphicsLayer {
                            if (isDraggedCard) {
                                translationY = if (isSettling) settleAnim.value else dragOffsetY
                                scaleX = dragScale
                                scaleY = dragScale
                            } else {
                                translationY = animatedShiftY
                            }
                        }"""

    order_graphics_replacement = """                        .graphicsLayer {
                            if (isDraggedCard) {
                                translationY = if (isSettling) settleAnim.value else dragOffsetY
                                scaleX = dragScale
                                scaleY = dragScale
                                rotationZ = dragTilt
                            } else {
                                translationY = animatedShiftY
                            }
                        }"""
    if order_graphics_target in content:
        content = content.replace(order_graphics_target, order_graphics_replacement, 1)

    # OrderSteps protocol flow between items:
    order_flow_target = """                        } else {
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
// 3. CYBER-IDE HACKER TERMINAL"""

    order_flow_replacement = """                        } else {
                            Icon(
                                imageVector = if (isCorrectPosition) Icons.Default.CheckCircle else Icons.Default.Close,
                                contentDescription = if (isCorrectPosition) "Correct" else "Incorrect",
                                tint = if (isCorrectPosition) PhoenixGreen else PhoenixCrimson,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Protocol Message-Flow Handshake Arrow between steps
                if (index < currentSteps.size - 1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val isEven = index % 2 == 0
                        val packetLabel = if (isEven) "Client ➔ Server [Key Exchange]" else "Server ➔ Client [Cipher Handshake]"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF0F172A))
                                .border(0.8.dp, Color(0xFF1E293B), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "⚡ $packetLabel",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = if (isChecked) (if (isCorrectPosition) PhoenixGreen else PhoenixCrimson) else Color(0xFF38BDF8)
                            )
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// 3. CYBER-IDE HACKER TERMINAL"""

    if order_flow_target in content:
        content = content.replace(order_flow_target, order_flow_replacement, 1)

    # 4. FillCodeExerciseView: Add ExplodeEffect + Mini Terminal
    fill_target = """    val feedback = remember { FeedbackManager(context) }
    var selectedToken by remember(codeTemplate, wordBank) { mutableStateOf<String?>(null) }
    val activeToken = externalSelectedToken ?: selectedToken

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {"""

    fill_replacement = """    val feedback = remember { FeedbackManager(context) }
    var selectedToken by remember(codeTemplate, wordBank) { mutableStateOf<String?>(null) }
    val activeToken = externalSelectedToken ?: selectedToken

    var explodeTrigger by remember { mutableStateOf(false) }
    var slotCenter by remember { mutableStateOf(Offset(200f, 200f)) }

    LaunchedEffect(isChecked) {
        if (isChecked && activeToken != null && activeToken != correctToken) {
            explodeTrigger = true
        }
    }

    Box(modifier = modifier.fillMaxWidth()) {
        ExplodeHost(
            trigger = explodeTrigger,
            origin = slotCenter,
            shardCount = 28,
            onComplete = { explodeTrigger = false }
        )

        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {"""

    if fill_target in content:
        content = content.replace(fill_target, fill_replacement, 1)

    # Close Box & Add Mini Terminal at end of FillCodeExerciseView
    fill_end_target = """            FlowRow(
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
}"""

    fill_end_replacement = """            FlowRow(
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

        // Mini Cryptographic Terminal Live Execution Box
        AnimatedVisibility(
            visible = activeToken != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF090D16))
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(">_", fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Black, color = AmberGold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("CRYPTO REPL LIVE EXECUTION", fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = SubtextGray)
                }
                Text(
                    text = "$ python cipher_runtime.py --token=\"$activeToken\"",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF94A3B8)
                )
                Text(
                    text = if (isChecked) {
                        if (activeToken == correctToken) ">>> [PASS] AST Verified. Cipher block valid. Output hash intact."
                        else ">>> [FAIL] Integrity check failed! Signature mismatch."
                    } else {
                        ">>> [STAGED] Token inserted into AST slot. Awaiting execution..."
                    },
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (isChecked) (if (activeToken == correctToken) PhoenixGreen else PhoenixCrimson) else Color(0xFF38BDF8)
                )
            }
        }
    }
    }
}"""

    if fill_end_target in content:
        content = content.replace(fill_end_target, fill_end_replacement, 1)

    # 5. TrueFalseSwipeExerciseView: 3D FlipCard with stamps and misconception breakdown
    tf_full_target = """        // Swipeable Fact Card with 3D Depth
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
        }"""

    tf_full_replacement = """        // Swipeable 3D FlipCard with Rubber-Stamp Overlays & Analysis
        val cardShape = RoundedCornerShape(20.dp)
        val rotationDeg = (dragOffsetX.value / 25f).coerceIn(-15f, 15f)

        var cardFace by remember(statement) { mutableStateOf(CardFace.FRONT) }

        LaunchedEffect(isChecked) {
            if (isChecked && activeDecision != isCorrectTrue) {
                delay(400)
                cardFace = CardFace.BACK
            }
        }

        FlipCard(
            cardFace = cardFace,
            onFlip = { nextFace -> cardFace = nextFace },
            enableClickFlip = isChecked,
            modifier = Modifier.fillMaxWidth(),
            frontContent = {
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
                        // Dynamic Rubber-Stamp Badges
                        if (dragOffsetX.value > 40f || activeDecision == true) {
                            Box(
                                modifier = Modifier
                                    .graphicsLayer { rotationZ = -5f }
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PhoenixGreen.copy(alpha = 0.18f))
                                    .border(2.dp, PhoenixGreen, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text("CONFIRMED TRUE • VALID ✅", fontSize = 13.sp, fontWeight = FontWeight.Black, color = PhoenixGreen)
                            }
                        } else if (dragOffsetX.value < -40f || activeDecision == false) {
                            Box(
                                modifier = Modifier
                                    .graphicsLayer { rotationZ = 5f }
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PhoenixCrimson.copy(alpha = 0.18f))
                                    .border(2.dp, PhoenixCrimson, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text("VULNERABILITY • FALSE ❌", fontSize = 13.sp, fontWeight = FontWeight.Black, color = PhoenixCrimson)
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

                        if (isChecked) {
                            Text(
                                text = "🔄 TAP TO FLIP FOR ANALYSIS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = AmberGold
                            )
                        }
                    }
                }
            },
            backContent = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(cardShape)
                        .background(Color(0xFF0F1522))
                        .border(1.8.dp, AmberGold.copy(alpha = 0.6f), cardShape)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🔐", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CRYPTOGRAPHIC ANALYSIS",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = AmberGold
                            )
                        }

                        Text(
                            text = explanation ?: "Cryptographic security requires formal proof under adversarial models (e.g. IND-CPA, IND-CCA2) to ensure privacy against modern computational attacks.",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFE2E8F0),
                            lineHeight = 18.sp
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(alpha = 0.05f))
                                .border(1.dp, Color(0xFF334155), RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Text(
                                    text = "⚠️ SECURITY TAKEAWAY",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFF97316)
                                )
                                Text(
                                    text = if (isCorrectTrue) {
                                        "Verified primitive: conforms to standard provable security benchmarks."
                                    } else {
                                        "Flawed assumption: relying on unverified assumptions compromises entire ciphertext integrity."
                                    },
                                    fontSize = 11.sp,
                                    color = Color(0xFFCBD5E1)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "🔄 TAP TO RETURN TO STATEMENT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = AmberGold
                            )
                        }
                    }
                }
            }
        )"""

    if tf_full_target in content:
        content = content.replace(tf_full_target, tf_full_replacement, 1)

    with open(path, "w", encoding="utf-8") as f:
        f.write(content)
    print("DSAGamifiedExerciseViews.kt updated successfully.")

def update_celebration_overlay():
    path = "app/src/main/java/com/simats/codeingo/ui/components/motion/CelebrationOverlay.kt"
    with open(path, "r", encoding="utf-8") as f:
        content = f.read()

    # Imports
    if "com.simats.codeingo.ui.components.motion.ConfettiHost" not in content:
        import_marker = "import androidx.compose.ui.draw.clip"
        new_imports = """import androidx.compose.ui.draw.clip
import com.simats.codeingo.ui.components.motion.ConfettiHost
import com.simats.codeingo.ui.components.motion.NumberRollText
import com.simats.codeingo.ui.components.motion.AshMotionModifiers.bounce
import com.simats.codeingo.ui.components.motion.AshMotionModifiers.pulse
import com.simats.codeingo.ui.components.motion.AshMotionModifiers.tada"""
        content = content.replace(import_marker, new_imports, 1)

    # Replace ConfettiParticleField() with ConfettiHost(trigger = true)
    if "ConfettiParticleField()" in content:
        content = content.replace("ConfettiParticleField()", "ConfettiHost(trigger = true)", 1)

    # Use NumberRollText inside RewardCard calls or update RewardCard
    reward_row_target = """            // 3. Dynamic Reward Chips with Kinetic Springs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RewardCard(
                    icon = "⚡",
                    label = "XP EARNED",
                    value = "+${xpAnim.value.toInt()}",
                    accentColor = Color(0xFFF59E0B)
                )
                RewardCard(
                    icon = "💎",
                    label = "GEMS",
                    value = "+${gemsAnim.value.toInt()}",
                    accentColor = Color(0xFF06B6D4)
                )
                RewardCard(
                    icon = "🔥",
                    label = "STREAK",
                    value = "$streakDays DAYS",
                    accentColor = Color(0xFFEF4444)
                )
            }"""

    reward_row_replacement = """            // 3. Dynamic Reward Chips with Rolling Numbers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RewardCard(
                    icon = "⚡",
                    label = "XP EARNED",
                    value = "+${xpEarned}",
                    accentColor = Color(0xFFF59E0B)
                )
                RewardCard(
                    icon = "💎",
                    label = "GEMS",
                    value = "+${gemsEarned}",
                    accentColor = Color(0xFF06B6D4)
                )
                RewardCard(
                    icon = "🔥",
                    label = "STREAK",
                    value = "$streakDays DAYS",
                    accentColor = Color(0xFFEF4444)
                )
            }"""
    if reward_row_target in content:
        content = content.replace(reward_row_target, reward_row_replacement, 1)

    with open(path, "w", encoding="utf-8") as f:
        f.write(content)
    print("CelebrationOverlay.kt updated successfully.")

def update_assessment_screen():
    path = "app/src/main/java/com/simats/codeingo/ui/exercise/AssessmentScreen.kt"
    with open(path, "r", encoding="utf-8") as f:
        content = f.read()

    # 1. Imports
    if "com.simats.codeingo.ui.components.motion.AshMotionModifiers" not in content:
        import_marker = "import com.simats.codeingo.ui.components.motion.shimmerGlossSweep"
        new_imports = """import com.simats.codeingo.ui.components.motion.shimmerGlossSweep
import com.simats.codeingo.ui.components.motion.AshMotionModifiers.wobble
import com.simats.codeingo.ui.components.motion.AshMotionModifiers.pressSquish
import androidx.compose.ui.graphics.Brush"""
        content = content.replace(import_marker, new_imports, 1)

    # 2. Add Themed Level Intro Banner right inside scrollable Column:
    intro_target = """                    // Chapter / Stage Sub-Header + Cheat Code
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {"""

    intro_replacement = """                    // Themed Challenge Intro Banner
                    AnimatedVisibility(
                        visible = currentIndex == 0 && !isChecked,
                        enter = fadeIn() + slideInVertically()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            AmberGold.copy(alpha = 0.18f),
                                            Color(0xFF38BDF8).copy(alpha = 0.12f)
                                        )
                                    )
                                )
                                .border(1.dp, AmberGold.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🎯", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "MISSION: CHAPTER $unitId CRYPTO DEFENSE",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = AppFontFamily,
                                        color = AmberGold
                                    )
                                    Text(
                                        text = "Complete all ${questions.size} challenges to secure the protocol.",
                                        fontSize = 11.sp,
                                        color = SubtextGray
                                    )
                                }
                            }
                        }
                    }

                    // Chapter / Stage Sub-Header + Cheat Code
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {"""

    if intro_target in content:
        content = content.replace(intro_target, intro_replacement, 1)

    # 3. Add shine sweep to ProgressBarAnimated
    bar_target = "ProgressBarAnimated(progress = progress, modifier = Modifier.weight(1f).height(16.dp))"
    bar_replacement = "ProgressBarAnimated(progress = progress, modifier = Modifier.weight(1f).height(16.dp).shimmerGlossSweep(enabled = true))"
    if bar_target in content:
        content = content.replace(bar_target, bar_replacement, 1)

    # 4. Reactive mascot avatar
    mascot_box_target = """                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(PhoenixGreen.copy(alpha = 0.1f))
                                    .border(1.5.dp, PhoenixGreen.copy(alpha = 0.4f), RoundedCornerShape(18.dp)),
                                contentAlignment = Alignment.Center
                            ) {"""

    mascot_box_replacement = """                            val mascotMod = when {
                                isChecked && isAnswerCorrect -> Modifier.pulse(1.0f..1.12f, 400)
                                isChecked && !isAnswerCorrect -> Modifier.wobble()
                                currentCombo >= 2 -> Modifier.pulse(1.0f..1.1f, 500)
                                else -> Modifier
                            }
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .then(mascotMod)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(PhoenixGreen.copy(alpha = 0.1f))
                                    .border(1.5.dp, PhoenixGreen.copy(alpha = 0.4f), RoundedCornerShape(18.dp)),
                                contentAlignment = Alignment.Center
                            ) {"""

    if mascot_box_target in content:
        content = content.replace(mascot_box_target, mascot_box_replacement, 1)

    with open(path, "w", encoding="utf-8") as f:
        f.write(content)
    print("AssessmentScreen.kt updated successfully.")

if __name__ == "__main__":
    update_all()
