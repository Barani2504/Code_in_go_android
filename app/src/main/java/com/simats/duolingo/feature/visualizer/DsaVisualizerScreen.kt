package com.simats.duolingo.feature.visualizer

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.duolingo.core.theme.*
import com.simats.duolingo.data.AppState
import com.simats.duolingo.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ── DSA Color Palette (matching iOS exactly) ────────────────────────────────
private val DsaBlue = Color(0xFF1CB0F6)
private val DsaGreen = Color(0xFF58CC02)
private val DsaRed = Color(0xFFFF4B4B)
private val DsaOrange = Color(0xFFFF9600)
private val DsaPurple = Color(0xFFCE82FF)
private val DsaTeal = Color(0xFF00CD9C)
private val DsaCyan = Color(0xFF00BCD4)
private val DsaGraphPurple = Color(0xFFA05AFF)
private val DsaYellow = Color(0xFFFFC800)
private val CardBg = Color(0xFF1E1A3C)
private val InputBg = Color(0xFF14122C)
private val InputBorder = Color.White.copy(alpha = 0.12f)

enum class VisualizerType(val label: String, val icon: String, val color: Color) {
    ARRAY("Array", "📊", DsaGreen),
    STACK("Stack", "🥞", DsaBlue),
    QUEUE("Queue", "🚶‍♂️", DsaPurple),
    LINKED_LIST("Linked List", "🔗", DsaOrange),
    BINARY_TREE("Binary Tree", "🌲", DsaTeal),
    AVL_TREE("AVL Tree", "⚖️", DsaTeal),
    TRIE("Trie", "🔍", DsaCyan),
    GRAPH("Graph", "🕸️", DsaGraphPurple),
    SORTING("Bubble Sort", "📶", DsaRed),
    STEP_RECORDER("Step Player", "⏱️", DsaOrange)
}

@Composable
fun DsaVisualizerScreen(
    onOpenLanguagePicker: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedType by remember { mutableStateOf(VisualizerType.STACK) }
    val currentLang = AppState.selectedLanguage?.name ?: "Python"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DsaCourseBackgroundGradient)
    ) {
        // ── Top Header with Language Pill ─────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "DSA Visualizer",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Watch structures & code come alive",
                    color = DuolingoSubtext,
                    fontSize = 13.sp
                )
            }

            // Language Selector Pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBg)
                    .border(1.2.dp, InputBorder, RoundedCornerShape(12.dp))
                    .clickable(onClick = onOpenLanguagePicker)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(AppState.selectedLanguage?.flagEmoji ?: "🐍", fontSize = 16.sp)
                Text(
                    text = currentLang,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black
                )
                Text("▼", color = DuolingoSubtext, fontSize = 10.sp, fontWeight = FontWeight.Black)
            }
        }

        // ── Visualizer Type Tabs ─────────────────────────────────────────
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            items(VisualizerType.entries) { type ->
                val isSelected = selectedType == type
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) type.color else CardBg)
                        .border(
                            width = 1.5.dp,
                            color = if (isSelected) type.color else InputBorder,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { selectedType = type }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(type.icon, fontSize = 13.sp)
                    Text(
                        text = type.label,
                        color = if (isSelected) Color.White else DuolingoSubtext,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        // ── Visualizer Content Area ──────────────────────────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (selectedType) {
                VisualizerType.STACK -> StackVisualizerCard()
                VisualizerType.ARRAY -> ArrayVisualizerCard()
                VisualizerType.QUEUE -> QueueVisualizerCard()
                VisualizerType.LINKED_LIST -> LinkedListVisualizerCard()
                VisualizerType.BINARY_TREE -> BinaryTreeVisualizerCard()
                VisualizerType.AVL_TREE -> AvlTreeVisualizerCard()
                VisualizerType.TRIE -> TrieVisualizerCard()
                VisualizerType.GRAPH -> GraphVisualizerCard()
                VisualizerType.SORTING -> SortingVisualizerCard()
                VisualizerType.STEP_RECORDER -> StepRecorderEngineCard()
            }

            // Idiomatic Implementation Code Card
            if (selectedType != VisualizerType.STEP_RECORDER) {
                IdiomaticCodeCard(structure = selectedType.label, language = currentLang)
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 1. STACK VISUALIZER – Full spring animations, highlight pulse on push/peek
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
@Composable
private fun StackVisualizerCard() {
    var items by remember { mutableStateOf(listOf(10, 20, 30)) }
    var nextVal by remember { mutableIntStateOf(40) }
    var actionNote by remember { mutableStateOf("Stack visualizer ready") }
    var highlightTop by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    VisualizerCardContainer(
        title = "Stack Visualizer",
        subtitle = "LIFO – Last In, First Out",
        accentColor = DsaBlue
    ) {
        // Stack Visual
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TOP label
            if (items.isNotEmpty()) {
                Row(
                    modifier = Modifier.padding(bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DsaBlue.copy(alpha = 0.15f))
                            .border(1.dp, DsaBlue, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("TOP", color = DsaBlue, fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                    Spacer(Modifier.width(6.dp))
                    Text("⟵ PUSH/POP here", color = DuolingoSubtext, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Stack Items (reversed so TOP is on top visually)
            Column(
                modifier = Modifier.width(200.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (items.isEmpty()) {
                    Text(
                        "Stack is empty",
                        color = DuolingoSubtext,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                } else {
                    items.asReversed().forEachIndexed { revIdx, value ->
                        val isTop = revIdx == 0
                        val animatedScale by animateFloatAsState(
                            targetValue = if (isTop && highlightTop) 1.04f else 1f,
                            animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMediumLow),
                            label = "stackScale"
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .scale(animatedScale)
                                .background(
                                    if (isTop) DsaBlue.copy(alpha = 0.4f) else DsaBlue.copy(alpha = 0.15f)
                                )
                                .border(
                                    width = if (isTop) 2.dp else 1.dp,
                                    color = DsaBlue
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$value",
                                color = if (isTop) Color.White else DsaBlue,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
                // Bottom cap
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(InputBorder)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Status
        ActionStatusText(text = actionNote, color = DsaBlue)

        // Operation Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Push
            ActionButton(
                label = "Push $nextVal",
                icon = "➕",
                color = DsaBlue,
                modifier = Modifier.weight(1f)
            ) {
                scope.launch {
                    items = items + nextVal
                    actionNote = "Pushed $nextVal to top of stack"
                    nextVal += 10
                    highlightTop = true
                    delay(500)
                    highlightTop = false
                }
            }
            // Pop
            ActionButton(
                label = "Pop",
                icon = "➖",
                color = if (items.isEmpty()) CardBg else DsaRed,
                modifier = Modifier.weight(1f),
                enabled = items.isNotEmpty()
            ) {
                val popped = items.last()
                items = items.dropLast(1)
                actionNote = "Popped $popped from top of stack"
            }
            // Peek
            ActionButton(
                label = "Peek",
                icon = "👁",
                color = if (items.isEmpty()) CardBg else DsaGreen,
                modifier = Modifier.weight(1f),
                enabled = items.isNotEmpty()
            ) {
                scope.launch {
                    highlightTop = true
                    actionNote = "Peek: top element is ${items.lastOrNull() ?: 0}"
                    delay(800)
                    highlightTop = false
                }
            }
        }
    }

    // Concept Card
    DsaConceptCard(
        title = "Key Concepts",
        items = listOf(
            "push()" to "Add element to top",
            "pop()" to "Remove top element",
            "peek()" to "View top without removing",
            "isEmpty()" to "Check if stack is empty"
        ),
        color = DsaBlue
    )
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 2. ARRAY VISUALIZER – Tap to select, scale animations, insert/delete at index
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
@Composable
private fun ArrayVisualizerCard() {
    var arrayItems by remember { mutableStateOf(listOf(10, 25, 8, 42, 17)) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var highlightInsert by remember { mutableStateOf<Int?>(null) }
    var actionNote by remember { mutableStateOf("Tap any cell to select it") }
    val scope = rememberCoroutineScope()

    VisualizerCardContainer(
        title = "Array Visualizer",
        subtitle = "Sequential memory cells with index access",
        accentColor = DsaGreen
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Index Labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                arrayItems.indices.forEach { index ->
                    Text(
                        "[$index]",
                        color = DuolingoSubtext,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Cells with tap-to-select and scale animations
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                arrayItems.forEachIndexed { index, value ->
                    val isSelected = selectedIndex == index
                    val isInsert = highlightInsert == index
                    val animatedScale by animateFloatAsState(
                        targetValue = when {
                            isInsert -> 1.15f
                            isSelected -> 1.05f
                            else -> 1f
                        },
                        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMediumLow),
                        label = "arrayScale"
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .scale(animatedScale)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) DsaGreen.copy(alpha = 0.35f)
                                else DsaGreen.copy(alpha = 0.12f)
                            )
                            .border(
                                width = if (isSelected) 2.5.dp else 1.5.dp,
                                color = if (isSelected) DsaGreen else DsaGreen.copy(0.4f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                selectedIndex = if (selectedIndex == index) null else index
                                actionNote = if (selectedIndex != null)
                                    "Selected index [$index] = $value"
                                else "Selection cleared"
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "$value",
                            color = if (isSelected) Color.White else DsaGreen,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            ActionStatusText(text = actionNote, color = DsaGreen)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ActionButton(
                    label = "Insert", icon = "➕", color = DsaGreen,
                    modifier = Modifier.weight(1f)
                ) {
                    scope.launch {
                        val newVal = (1..99).random()
                        val insertIdx = if (selectedIndex != null) selectedIndex!! + 1 else arrayItems.size
                        val idx = insertIdx.coerceAtMost(arrayItems.size)
                        arrayItems = arrayItems.toMutableList().apply { add(idx, newVal) }
                        highlightInsert = idx
                        actionNote = "Inserted $newVal at index [$idx]"
                        selectedIndex = null
                        delay(600)
                        highlightInsert = null
                    }
                }
                ActionButton(
                    label = "Delete", icon = "🗑", color = if (selectedIndex != null) DsaRed else CardBg,
                    modifier = Modifier.weight(1f),
                    enabled = selectedIndex != null
                ) {
                    val idx = selectedIndex!!
                    val removed = arrayItems[idx]
                    arrayItems = arrayItems.toMutableList().apply { removeAt(idx) }
                    actionNote = "Deleted $removed from index [$idx]"
                    selectedIndex = null
                }
                ActionButton(
                    label = "Reset", icon = "↺", color = DsaOrange,
                    modifier = Modifier.weight(1f)
                ) {
                    arrayItems = listOf(10, 25, 8, 42, 17)
                    selectedIndex = null
                    actionNote = "Array reset to original"
                }
            }
        }
    }

    DsaConceptCard(
        title = "Key Concepts",
        items = listOf(
            "O(1)" to "Access by index",
            "O(n)" to "Search unsorted array",
            "O(n)" to "Insert/Delete at middle",
            "O(1)" to "Insert/Delete at end"
        ),
        color = DsaGreen
    )
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 3. QUEUE VISUALIZER – Horizontal queue with FRONT/BACK labels, scale anim
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
@Composable
private fun QueueVisualizerCard() {
    var queueItems by remember { mutableStateOf(listOf(10, 20, 30)) }
    var nextEnqueue by remember { mutableIntStateOf(40) }
    var actionNote by remember { mutableStateOf("Queue is FIFO – First In, First Out") }
    var highlightFront by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    VisualizerCardContainer(
        title = "Queue Visualizer",
        subtitle = "FIFO – First In, First Out",
        accentColor = DsaPurple
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Queue Visual (horizontal)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // FRONT label
                Column(
                    modifier = Modifier.width(54.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("FRONT", color = DsaGreen, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    Text("➡", color = DsaGreen, fontSize = 18.sp)
                }

                // Queue items
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.Start
                ) {
                    if (queueItems.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .width(130.dp)
                                .height(52.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(InputBg)
                                .border(1.dp, InputBorder, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Empty Queue", color = DuolingoSubtext, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        queueItems.forEachIndexed { index, value ->
                            val isFront = index == 0
                            val isBack = index == queueItems.size - 1
                            val cellColor = when {
                                isFront -> DsaGreen
                                isBack -> DsaRed
                                else -> DsaPurple
                            }
                            val animatedScale by animateFloatAsState(
                                targetValue = if (isFront && highlightFront) 1.08f else 1f,
                                animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMediumLow),
                                label = "queueScale"
                            )
                            Box(
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(52.dp)
                                    .scale(animatedScale)
                                    .background(cellColor.copy(alpha = if (isFront) 0.3f else if (isBack) 0.2f else 0.15f))
                                    .border(
                                        width = if (isFront) 2.5.dp else 1.5.dp,
                                        color = cellColor
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "$value",
                                    color = cellColor,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }

                // BACK label
                Column(
                    modifier = Modifier.width(54.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("BACK", color = DsaRed, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    Text("⬅", color = DsaRed, fontSize = 18.sp)
                }
            }

            // Direction labels
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 54.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("DEQUEUE", color = DsaGreen, fontSize = 9.sp, fontWeight = FontWeight.Black)
                Text("ENQUEUE", color = DsaRed, fontSize = 9.sp, fontWeight = FontWeight.Black)
            }

            ActionStatusText(text = actionNote, color = DsaPurple)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionButton(
                    label = "Enqueue $nextEnqueue", icon = "➕", color = DsaRed,
                    modifier = Modifier.weight(1f)
                ) {
                    queueItems = queueItems + nextEnqueue
                    actionNote = "Enqueued $nextEnqueue to back of queue"
                    nextEnqueue += 10
                }
                ActionButton(
                    label = "Dequeue", icon = "➖",
                    color = if (queueItems.isEmpty()) CardBg else DsaGreen,
                    modifier = Modifier.weight(1f),
                    enabled = queueItems.isNotEmpty()
                ) {
                    scope.launch {
                        highlightFront = true
                        val removed = queueItems.first()
                        queueItems = queueItems.drop(1)
                        actionNote = "Dequeued $removed from front of queue"
                        delay(500)
                        highlightFront = false
                    }
                }
            }
        }
    }

    DsaConceptCard(
        title = "Key Concepts",
        items = listOf(
            "enqueue()" to "Add to the back",
            "dequeue()" to "Remove from the front",
            "front()" to "Peek at front item",
            "Use cases" to "BFS, task scheduling"
        ),
        color = DsaPurple
    )
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 4. LINKED LIST VISUALIZER – Data/Next pointer cells, HEAD/TAIL, arrows
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
@Composable
private fun LinkedListVisualizerCard() {
    var nodes by remember { mutableStateOf(listOf(10, 20, 30)) }
    var nextVal by remember { mutableIntStateOf(40) }
    var actionNote by remember { mutableStateOf("Tap [+] to add a node") }
    var highlightIndex by remember { mutableStateOf<Int?>(null) }
    val scope = rememberCoroutineScope()

    VisualizerCardContainer(
        title = "Linked List Visualizer",
        subtitle = "Dynamic nodes connected by pointers",
        accentColor = DsaOrange
    ) {
        // Nodes Visual
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            nodes.forEachIndexed { index, value ->
                val isHighlighted = highlightIndex == index
                val animatedScale by animateFloatAsState(
                    targetValue = if (isHighlighted) 1.08f else 1f,
                    animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMediumLow),
                    label = "llScale"
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Node Box
                    Column(
                        modifier = Modifier.scale(animatedScale),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row {
                            // Data cell
                            Box(
                                modifier = Modifier
                                    .width(48.dp)
                                    .height(44.dp)
                                    .background(
                                        if (isHighlighted) DsaOrange.copy(0.4f)
                                        else DsaOrange.copy(0.15f)
                                    )
                                    .border(
                                        width = if (isHighlighted) 2.5.dp else 1.5.dp,
                                        color = DsaOrange
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "$value",
                                    color = if (isHighlighted) Color.White else DsaOrange,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            // Next pointer cell
                            Box(
                                modifier = Modifier
                                    .width(28.dp)
                                    .height(44.dp)
                                    .background(DsaOrange.copy(0.08f))
                                    .border(1.dp, DsaOrange.copy(0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    if (index < nodes.size - 1) "→" else "✕",
                                    color = if (index < nodes.size - 1) DsaOrange else DuolingoSubtext,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        // HEAD / TAIL label
                        val label = when {
                            index == 0 -> "HEAD"
                            index == nodes.size - 1 -> "TAIL"
                            else -> ""
                        }
                        if (label.isNotEmpty()) {
                            Text(
                                label,
                                color = if (index == 0) DsaGreen else DsaRed,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(top = 3.dp)
                            )
                        }
                    }

                    // Arrow connector
                    if (index < nodes.size - 1) {
                        Text(
                            "→",
                            color = DsaOrange.copy(0.5f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 2.dp, vertical = 0.dp)
                        )
                    }
                }
            }

            // NULL terminator
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(44.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(InputBg)
                        .border(1.5.dp, InputBorder, RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("nil", color = DuolingoSubtext, fontSize = 12.sp, fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.height(16.dp))
            }
        }

        ActionStatusText(text = actionNote, color = DsaOrange)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ActionButton(
                label = "Insert Head", icon = "⬅", color = DsaGreen,
                modifier = Modifier.weight(1f)
            ) {
                scope.launch {
                    nodes = listOf(nextVal) + nodes
                    actionNote = "Inserted $nextVal at HEAD"
                    highlightIndex = 0
                    nextVal += 10
                    delay(600)
                    highlightIndex = null
                }
            }
            ActionButton(
                label = "Insert Tail", icon = "➡", color = DsaOrange,
                modifier = Modifier.weight(1f)
            ) {
                scope.launch {
                    nodes = nodes + nextVal
                    actionNote = "Inserted $nextVal at TAIL"
                    highlightIndex = nodes.size - 1
                    nextVal += 10
                    delay(600)
                    highlightIndex = null
                }
            }
            ActionButton(
                label = "Delete Head", icon = "🗑",
                color = if (nodes.isEmpty()) CardBg else DsaRed,
                modifier = Modifier.weight(1f),
                enabled = nodes.isNotEmpty()
            ) {
                val removed = nodes.first()
                nodes = nodes.drop(1)
                actionNote = "Deleted HEAD node ($removed)"
            }
        }
    }

    DsaConceptCard(
        title = "Key Concepts",
        items = listOf(
            "O(1)" to "Insert at head/tail",
            "O(n)" to "Search for element",
            "No random access" to "Must traverse from head",
            "Dynamic size" to "No pre-allocation needed"
        ),
        color = DsaOrange
    )
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 5. BINARY TREE VISUALIZER – Circular nodes, tap-to-select, tree connectors
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
@Composable
private fun BinaryTreeVisualizerCard() {
    val treeValues = listOf<Int?>(50, 30, 70, 20, 40, 60, 80)
    var highlightIndex by remember { mutableStateOf<Int?>(null) }
    var actionNote by remember { mutableStateOf("Binary Search Tree – Tap any node") }

    val nodeColor = DsaTeal

    VisualizerCardContainer(
        title = "Binary Tree Visualizer",
        subtitle = "BST: Left < Root < Right",
        accentColor = nodeColor
    ) {
        // Tree Visual (3-level layout)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Level 0 – Root
            TreeNodeCircle(
                value = treeValues[0],
                index = 0,
                highlightIndex = highlightIndex,
                color = nodeColor,
                onTap = {
                    highlightIndex = if (highlightIndex == 0) null else 0
                    actionNote = if (highlightIndex != null)
                        "Node ${treeValues[0]} – Index 0"
                    else "Selection cleared"
                }
            )

            // Connector lines level 0->1
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .drawBehind {
                        val w = size.width
                        val h = size.height
                        // Left connector
                        drawLine(
                            color = Color.White.copy(alpha = 0.2f),
                            start = Offset(w * 0.5f, 0f),
                            end = Offset(w * 0.25f, h),
                            strokeWidth = 2f
                        )
                        // Right connector
                        drawLine(
                            color = Color.White.copy(alpha = 0.2f),
                            start = Offset(w * 0.5f, 0f),
                            end = Offset(w * 0.75f, h),
                            strokeWidth = 2f
                        )
                    }
            ) {}

            // Level 1 – children
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TreeNodeCircle(
                    value = treeValues[1], index = 1, highlightIndex = highlightIndex, color = nodeColor,
                    onTap = {
                        highlightIndex = if (highlightIndex == 1) null else 1
                        actionNote = if (highlightIndex != null) "Node ${treeValues[1]} – Index 1" else "Selection cleared"
                    }
                )
                TreeNodeCircle(
                    value = treeValues[2], index = 2, highlightIndex = highlightIndex, color = nodeColor,
                    onTap = {
                        highlightIndex = if (highlightIndex == 2) null else 2
                        actionNote = if (highlightIndex != null) "Node ${treeValues[2]} – Index 2" else "Selection cleared"
                    }
                )
            }

            // Connector lines level 1->2
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .drawBehind {
                        val w = size.width
                        val h = size.height
                        // Left subtree connectors
                        drawLine(Color.White.copy(0.2f), Offset(w * 0.25f, 0f), Offset(w * 0.13f, h), 2f)
                        drawLine(Color.White.copy(0.2f), Offset(w * 0.25f, 0f), Offset(w * 0.37f, h), 2f)
                        // Right subtree connectors
                        drawLine(Color.White.copy(0.2f), Offset(w * 0.75f, 0f), Offset(w * 0.63f, h), 2f)
                        drawLine(Color.White.copy(0.2f), Offset(w * 0.75f, 0f), Offset(w * 0.87f, h), 2f)
                    }
            ) {}

            // Level 2 – leaves
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                (3..6).forEach { i ->
                    TreeNodeCircle(
                        value = treeValues.getOrNull(i), index = i, highlightIndex = highlightIndex, color = nodeColor,
                        onTap = {
                            highlightIndex = if (highlightIndex == i) null else i
                            actionNote = if (highlightIndex != null)
                                "Node ${treeValues.getOrNull(i)} – Index $i"
                            else "Selection cleared"
                        }
                    )
                }
            }
        }

        ActionStatusText(text = actionNote, color = nodeColor)
    }

    DsaConceptCard(
        title = "Key Concepts",
        items = listOf(
            "O(log n)" to "Search in balanced BST",
            "In-order" to "Sorted output (L,Root,R)",
            "Pre-order" to "Root first (Root,L,R)",
            "Post-order" to "Root last (L,R,Root)"
        ),
        color = nodeColor
    )
}

@Composable
private fun TreeNodeCircle(
    value: Int?,
    index: Int,
    highlightIndex: Int?,
    color: Color,
    onTap: () -> Unit
) {
    if (value == null) return
    val isHighlighted = highlightIndex == index
    val animatedScale by animateFloatAsState(
        targetValue = if (isHighlighted) 1.1f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMediumLow),
        label = "treeScale"
    )
    Box(
        modifier = Modifier
            .size(46.dp)
            .scale(animatedScale)
            .clip(CircleShape)
            .background(if (isHighlighted) color else color.copy(0.2f))
            .border(
                width = if (isHighlighted) 2.5.dp else 1.5.dp,
                color = color,
                shape = CircleShape
            )
            .clickable(onClick = onTap),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "$value",
            color = if (isHighlighted) Color.White else color,
            fontSize = 15.sp,
            fontWeight = FontWeight.Black
        )
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 6. AVL TREE VISUALIZER – Balance factor nodes, rotation controls
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
@Composable
private fun AvlTreeVisualizerCard() {
    var rootValue by remember { mutableIntStateOf(30) }
    var leftValue by remember { mutableIntStateOf(20) }
    var rightValue by remember { mutableIntStateOf(40) }
    var bfRoot by remember { mutableIntStateOf(1) }
    var bfLeft by remember { mutableIntStateOf(0) }
    var bfRight by remember { mutableIntStateOf(-1) }
    var actionNote by remember { mutableStateOf("Balanced Tree (Balance factors ∈ {-1, 0, 1})") }

    VisualizerCardContainer(
        title = "AVL Tree (Balance Doctor)",
        subtitle = "Self-balancing BST guaranteeing O(log n)",
        accentColor = DsaTeal
    ) {
        // Tree Canvas
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Root
            AvlNodeView(value = rootValue, bf = bfRoot)

            // Connecting branches
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .drawBehind {
                        val w = size.width
                        val h = size.height
                        // Left branch
                        drawLine(
                            color = DsaTeal.copy(alpha = 0.5f),
                            start = Offset(w * 0.5f, 0f),
                            end = Offset(w * 0.35f, h),
                            strokeWidth = 2.5f,
                            cap = StrokeCap.Round
                        )
                        // Right branch
                        drawLine(
                            color = DsaTeal.copy(alpha = 0.5f),
                            start = Offset(w * 0.5f, 0f),
                            end = Offset(w * 0.65f, h),
                            strokeWidth = 2.5f,
                            cap = StrokeCap.Round
                        )
                    }
            ) {}

            // Children
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(80.dp)) {
                    AvlNodeView(value = leftValue, bf = bfLeft)
                    AvlNodeView(value = rightValue, bf = bfRight)
                }
            }
        }

        // Rotation Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    val temp = rootValue
                    rootValue = rightValue
                    rightValue = temp + 15
                    leftValue = temp
                    bfRoot = 0; bfLeft = 0; bfRight = 0
                    actionNote = "Rotated Left: New root $rootValue, BF restored to 0"
                },
                colors = ButtonDefaults.buttonColors(containerColor = DsaTeal),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Rotate Left (LL)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    val temp = rootValue
                    rootValue = leftValue
                    leftValue = maxOf(5, temp - 15)
                    rightValue = temp
                    bfRoot = 0; bfLeft = 0; bfRight = 0
                    actionNote = "Rotated Right: New root $rootValue, BF restored to 0"
                },
                colors = ButtonDefaults.buttonColors(containerColor = DsaBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Rotate Right (RR)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            IconButton(
                onClick = {
                    rootValue = 30; leftValue = 20; rightValue = 40
                    bfRoot = 1; bfLeft = 0; bfRight = -1
                    actionNote = "Balanced Tree reset"
                }
            ) {
                Text("↺", fontSize = 18.sp, color = DuolingoSubtext)
            }
        }

        ActionStatusText(text = actionNote, color = DuolingoSubtext)
    }
}

@Composable
private fun AvlNodeView(value: Int, bf: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(DsaTeal.copy(0.35f))
                .border(2.dp, DsaTeal, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("$value", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Black)
        }
        Text(
            "BF: ${if (bf > 0) "+$bf" else "$bf"}",
            color = if (kotlin.math.abs(bf) > 1) DsaRed else DsaGreen,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .padding(top = 2.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.Black.copy(0.4f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 7. TRIE VISUALIZER – Visual prefix tree canvas, search field
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
@Composable
private fun TrieVisualizerCard() {
    val words = listOf("CAT", "CAR", "CARD", "DO", "DOG")
    var searchInput by remember { mutableStateOf("CA") }
    val actionNote = "Shared prefix 'CA' branches into 'T', 'R', and 'RD'"

    VisualizerCardContainer(
        title = "Trie (Crystal Cave of Words)",
        subtitle = "Prefix tree for instant string lookups & autocomplete",
        accentColor = DsaCyan
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Stored Words Chips
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("Words in Cave:", color = DuolingoSubtext, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    words.forEach { word ->
                        Text(
                            word,
                            color = DsaCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DsaCyan.copy(0.18f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Interactive Prefix Tree Canvas
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Root
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(DsaCyan.copy(0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("ROOT", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black)
                }

                // Level 1: 'C' and 'D'
                Row(horizontalArrangement = Arrangement.spacedBy(80.dp)) {
                    TrieNodeView("C", isEnd = false, isMatch = searchInput.uppercase().startsWith("C"))
                    TrieNodeView("D", isEnd = false, isMatch = searchInput.uppercase().startsWith("D"))
                }

                // Level 2: 'A' and 'O'
                Row(horizontalArrangement = Arrangement.spacedBy(80.dp)) {
                    TrieNodeView("A", isEnd = false, isMatch = searchInput.uppercase().startsWith("CA"))
                    TrieNodeView("O", isEnd = true, isMatch = searchInput.uppercase().startsWith("DO"))
                }

                // Level 3: 'T', 'R', 'G'
                Row(horizontalArrangement = Arrangement.spacedBy(36.dp)) {
                    TrieNodeView("T", isEnd = true, isMatch = searchInput.uppercase() == "CAT")
                    TrieNodeView("R", isEnd = true, isMatch = searchInput.uppercase() == "CAR")
                    TrieNodeView("G", isEnd = true, isMatch = searchInput.uppercase() == "DOG")
                }
            }

            // Autocomplete Search Simulation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(InputBg)
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("🔍", fontSize = 16.sp)
                BasicTextField(
                    value = searchInput,
                    onValueChange = { searchInput = it },
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    decorationBox = { inner ->
                        if (searchInput.isEmpty()) {
                            Text(
                                "Search prefix (e.g. CA, DO)...",
                                color = DuolingoSubtext,
                                fontSize = 14.sp
                            )
                        }
                        inner()
                    }
                )
            }

            Text(actionNote, color = DuolingoSubtext, fontSize = 12.sp)
        }
    }
}

@Composable
private fun TrieNodeView(char: String, isEnd: Boolean, isMatch: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    when {
                        isMatch -> DsaCyan
                        isEnd -> DsaCyan.copy(0.4f)
                        else -> InputBg
                    }
                )
                .border(
                    width = if (isEnd) 2.dp else 1.dp,
                    color = DsaCyan,
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                char,
                color = if (isMatch) Color.Black else Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )
        }
        if (isEnd) {
            Text("★ WORD", color = DsaYellow, fontSize = 8.sp, fontWeight = FontWeight.Black)
        }
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 8. GRAPH VISUALIZER – Sky Islands with positioned nodes, animated traversal
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
@Composable
private fun GraphVisualizerCard() {
    var visitedOrder by remember { mutableStateOf(listOf<String>()) }
    var currentIsland by remember { mutableStateOf<String?>(null) }
    var actionNote by remember { mutableStateOf("Tap BFS or DFS to traverse Sky Islands") }
    val scope = rememberCoroutineScope()

    // Island positions (relative fractions)
    data class IslandPos(val id: String, val xFrac: Float, val yFrac: Float)
    val islands = listOf(
        IslandPos("A", 0.15f, 0.2f),
        IslandPos("B", 0.45f, 0.2f),
        IslandPos("C", 0.8f, 0.45f),
        IslandPos("D", 0.15f, 0.75f),
        IslandPos("E", 0.45f, 0.75f)
    )

    // Edges (connections)
    val edges = listOf(0 to 1, 1 to 2, 2 to 4, 4 to 3, 1 to 3)

    fun animateTraversal(order: List<String>) {
        scope.launch {
            visitedOrder = emptyList()
            currentIsland = null
            for (island in order) {
                delay(500)
                currentIsland = island
                visitedOrder = visitedOrder + island
            }
            delay(300)
            currentIsland = null
        }
    }

    VisualizerCardContainer(
        title = "Graph: Sky Islands",
        subtitle = "Vertices & Bridges • BFS Flood vs DFS Diver",
        accentColor = DsaGraphPurple
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Floating Islands Layout
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .drawBehind {
                        // Draw edges
                        for ((from, to) in edges) {
                            val f = islands[from]
                            val t = islands[to]
                            val isTraversed = visitedOrder.contains(f.id) && visitedOrder.contains(t.id)
                            drawLine(
                                color = if (isTraversed) DsaYellow else DsaGraphPurple.copy(alpha = 0.4f),
                                start = Offset(f.xFrac * size.width, f.yFrac * size.height),
                                end = Offset(t.xFrac * size.width, t.yFrac * size.height),
                                strokeWidth = if (isTraversed) 3.5f else 2f,
                                cap = StrokeCap.Round
                            )
                        }
                    }
            ) {
                val boxWidth = maxWidth
                val boxHeight = maxHeight

                islands.forEach { island ->
                    val isCurrent = currentIsland == island.id
                    val isVisited = visitedOrder.contains(island.id)
                    val animatedScale by animateFloatAsState(
                        targetValue = if (isCurrent) 1.2f else 1f,
                        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMediumLow),
                        label = "graphScale"
                    )

                    Box(
                        modifier = Modifier
                            .offset(
                                x = boxWidth * island.xFrac - 22.dp,
                                y = boxHeight * island.yFrac - 22.dp
                            )
                            .size(44.dp)
                            .scale(animatedScale)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isCurrent -> DsaYellow
                                    isVisited -> DsaGraphPurple
                                    else -> InputBg
                                }
                            )
                            .border(2.dp, if (isCurrent) Color.White else DsaGraphPurple, CircleShape)
                            .then(
                                if (isCurrent) Modifier.shadow(12.dp, CircleShape, ambientColor = DsaYellow)
                                else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "🏝️ ${island.id}",
                            color = if (isCurrent) Color.Black else Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // Traversal History
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Visited Path: ", color = DuolingoSubtext, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(
                    if (visitedOrder.isEmpty()) "None" else visitedOrder.joinToString(" → "),
                    color = DsaYellow,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Action Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        actionNote = "BFS (Queue): Discovers neighbors layer by layer (A → B, D → C, E)"
                        animateTraversal(listOf("A", "B", "D", "C", "E"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DsaGraphPurple),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("🌊 BFS (Ripple)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        actionNote = "DFS (Stack/Recursion): Dives deep along single branch before backtracking"
                        animateTraversal(listOf("A", "B", "C", "E", "D"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DsaBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("🤿 DFS (Diver)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                IconButton(onClick = {
                    visitedOrder = emptyList()
                    currentIsland = null
                    actionNote = "Graph reset"
                }) {
                    Text("↺", fontSize = 18.sp, color = DuolingoSubtext)
                }
            }

            Text(actionNote, color = DuolingoSubtext, fontSize = 12.sp)
        }
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 9. SORTING VISUALIZER – Bar chart with value labels, highlighted pairs, step-by-step
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
@Composable
private fun SortingVisualizerCard() {
    var bars by remember { mutableStateOf(listOf(64, 34, 25, 12, 22, 11, 90)) }
    var highlightIndices by remember { mutableStateOf(setOf<Int>()) }
    var isSorting by remember { mutableStateOf(false) }
    var step by remember { mutableIntStateOf(0) }
    var actionNote by remember { mutableStateOf("Tap Sort to watch Bubble Sort step by step") }

    VisualizerCardContainer(
        title = "Bubble Sort Visualizer",
        subtitle = "O(n²) – Compare adjacent elements",
        accentColor = DsaRed
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Bars
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                bars.forEachIndexed { index, value ->
                    val isHighlighted = highlightIndices.contains(index)
                    val animatedScale by animateFloatAsState(
                        targetValue = if (isHighlighted) 1.05f else 1f,
                        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMediumLow),
                        label = "sortScale"
                    )
                    val animatedHeight by animateDpAsState(
                        targetValue = (value * 1.25f).dp,
                        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMediumLow),
                        label = "barHeight"
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(
                            "$value",
                            color = if (isHighlighted) Color.White else DsaRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(animatedHeight)
                                .scale(animatedScale)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isHighlighted) DsaRed else DsaRed.copy(0.3f))
                                .border(1.5.dp, if (isHighlighted) Color.White else DsaRed, RoundedCornerShape(6.dp))
                        )
                    }
                }
            }

            ActionStatusText(text = actionNote, color = DsaRed)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ActionButton(
                    label = if (isSorting) "Next Step" else "Sort Step",
                    icon = "→",
                    color = DsaRed,
                    modifier = Modifier.weight(1f)
                ) {
                    isSorting = true
                    val n = bars.size
                    val totalPasses = (n * (n - 1)) / 2
                    if (step >= totalPasses) {
                        actionNote = "Array is sorted! Tap Reset to start again."
                        highlightIndices = emptySet()
                        return@ActionButton
                    }

                    // Find current pass and index
                    var pass = 0
                    var idx = 0
                    var s = 0
                    loop@ for (i in 0 until n) {
                        for (j in 0 until (n - i - 1)) {
                            if (s == step) {
                                pass = i
                                idx = j
                                break@loop
                            }
                            s++
                        }
                    }

                    highlightIndices = setOf(idx, idx + 1)
                    val mutable = bars.toMutableList()
                    if (mutable[idx] > mutable[idx + 1]) {
                        val temp = mutable[idx]
                        mutable[idx] = mutable[idx + 1]
                        mutable[idx + 1] = temp
                        actionNote = "Pass ${pass + 1}: Swapped ${mutable[idx + 1]} and ${mutable[idx]}"
                    } else {
                        actionNote = "Pass ${pass + 1}: No swap needed (${mutable[idx]} <= ${mutable[idx + 1]})"
                    }
                    bars = mutable
                    step++
                }

                ActionButton(
                    label = "Reset", icon = "↺", color = DsaOrange,
                    modifier = Modifier.weight(1f)
                ) {
                    bars = listOf(64, 34, 25, 12, 22, 11, 90)
                    highlightIndices = emptySet()
                    step = 0
                    isSorting = false
                    actionNote = "Tap Sort to watch Bubble Sort step by step"
                }
            }
        }
    }

    DsaConceptCard(
        title = "Key Concepts",
        items = listOf(
            "O(n²)" to "Time complexity",
            "O(1)" to "Space complexity",
            "Stable sort" to "Preserves relative order",
            "Best case" to "O(n) if already sorted"
        ),
        color = DsaRed
    )
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 10. STEP RECORDER ENGINE CARD (matching iOS DSAVisualizerEngineView)
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
private data class SimulatorStep(
    val stateDescription: String,
    val narration: String,
    val highlightedIndices: List<Int>,
    val codeLine: Int,
    val comparisons: Int,
    val shifts: Int,
    val pointerUpdates: Int,
    val arrayState: List<Int>,
    val activePointers: Map<String, Int>
)

@Composable
private fun StepRecorderEngineCard() {
    val steps = remember {
        listOf(
            SimulatorStep(
                stateDescription = "Array initialized",
                narration = "Array [10, 20, 30, 40] allocated contiguously in memory.",
                highlightedIndices = emptyList(),
                codeLine = 1,
                comparisons = 0,
                shifts = 0,
                pointerUpdates = 0,
                arrayState = listOf(10, 20, 30, 40),
                activePointers = mapOf("i" to 0)
            ),
            SimulatorStep(
                stateDescription = "Shift element at index 3",
                narration = "Element 40 shifts right to index 4 to make space.",
                highlightedIndices = listOf(3),
                codeLine = 3,
                comparisons = 1,
                shifts = 1,
                pointerUpdates = 1,
                arrayState = listOf(10, 20, 30, 40, 40),
                activePointers = mapOf("i" to 3)
            ),
            SimulatorStep(
                stateDescription = "Shift element at index 2",
                narration = "Element 30 shifts right to index 3.",
                highlightedIndices = listOf(2),
                codeLine = 3,
                comparisons = 2,
                shifts = 2,
                pointerUpdates = 2,
                arrayState = listOf(10, 20, 30, 30, 40),
                activePointers = mapOf("i" to 2)
            ),
            SimulatorStep(
                stateDescription = "Insert 25 at index 2",
                narration = "Value 25 written at index 2 in O(1) direct write time.",
                highlightedIndices = listOf(2),
                codeLine = 5,
                comparisons = 2,
                shifts = 2,
                pointerUpdates = 3,
                arrayState = listOf(10, 20, 25, 30, 40),
                activePointers = mapOf("index" to 2)
            )
        )
    }

    val codeSnippets = remember {
        mapOf(
            "Python" to listOf(
                "def insert_at(arr, index, val):",
                "    arr.append(None)  # O(1) amortized",
                "    for i in range(len(arr) - 2, index - 1, -1):",
                "        arr[i + 1] = arr[i]  # Shift right",
                "    arr[index] = val  # Direct write"
            ),
            "Swift" to listOf(
                "func insert(val: Int, at index: Int) {",
                "    elements.append(0)",
                "    for i in stride(from: count - 2, through: index, by: -1) {",
                "        elements[i + 1] = elements[i]",
                "    }",
                "    elements[index] = val",
                "}"
            ),
            "Java" to listOf(
                "public void insert(int[] arr, int index, int val) {",
                "    // Expand capacity if needed",
                "    for (int i = size - 1; i >= index; i--) {",
                "        arr[i + 1] = arr[i]; // Shift right",
                "    }",
                "    arr[index] = val; // Direct write",
                "}"
            ),
            "C++" to listOf(
                "void insert(vector<int>& arr, int index, int val) {",
                "    arr.push_back(0);",
                "    for (int i = arr.size() - 2; i >= index; --i) {",
                "        arr[i + 1] = arr[i];",
                "    }",
                "    arr[index] = val;",
                "}"
            )
        )
    }

    var currentStepIndex by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var speed by remember { mutableFloatStateOf(1f) }
    var selectedLanguage by remember { mutableStateOf("Python") }

    val currentStep = steps[currentStepIndex.coerceIn(0, steps.size - 1)]

    LaunchedEffect(isPlaying, speed) {
        while (isPlaying) {
            delay((1000 / speed).toLong())
            if (currentStepIndex < steps.size - 1) {
                currentStepIndex++
            } else {
                isPlaying = false
            }
        }
    }

    VisualizerCardContainer(
        title = "Step Recorder Engine",
        subtitle = "Synchronized Code Execution & Live Complexity",
        accentColor = DsaOrange
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // 1. World Badge & Complexity Pill Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DsaOrange.copy(alpha = 0.15f))
                        .border(1.dp, DsaOrange.copy(0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("📍", fontSize = 10.sp)
                    Text("Locker Row Avenue", color = DsaOrange, fontSize = 11.sp, fontWeight = FontWeight.Black)
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CardBg)
                        .border(1.dp, InputBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Time: O(n)", color = DsaGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    Text("•", color = DuolingoSubtext, fontSize = 10.sp)
                    Text("Space: O(1)", color = DsaCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
            }

            // 2. Interactive Structure Canvas View (visual array with active pointers)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(InputBg)
                    .border(1.dp, InputBorder, RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    currentStep.arrayState.forEachIndexed { idx, value ->
                        val isHighlighted = currentStep.highlightedIndices.contains(idx)
                        val pointerName = currentStep.activePointers.entries.firstOrNull { it.value == idx }?.key

                        val animatedScale by animateFloatAsState(
                            targetValue = if (isHighlighted) 1.12f else 1f,
                            animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMediumLow),
                            label = "simScale"
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Active pointer label
                            Text(
                                text = pointerName ?: " ",
                                color = DsaOrange,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.height(14.dp)
                            )

                            // Cell Box
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .scale(animatedScale)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isHighlighted) DsaOrange
                                        else DsaBlue.copy(alpha = 0.35f)
                                    )
                                    .border(
                                        width = if (isHighlighted) 2.5.dp else 1.dp,
                                        color = if (isHighlighted) Color(0xFFFFC800) else DsaBlue,
                                        shape = RoundedCornerShape(10.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "$value",
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            // Index label
                            Text("[$idx]", color = DuolingoSubtext, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }

            // 3. Phoenix Narration Bubble
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DsaOrange.copy(alpha = 0.12f))
                    .border(1.dp, DsaOrange.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(10.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("🔥", fontSize = 22.sp)
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("PHOENIX EXPLAINS:", color = DsaOrange, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    Text(currentStep.narration, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }

            // 4. Live Complexity Cost Meter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple("⚖️", "Comparisons", "${currentStep.comparisons}"),
                    Triple("⇄", "Shifts/Moves", "${currentStep.shifts}"),
                    Triple("🔗", "Pointers", "${currentStep.pointerUpdates}")
                ).forEach { (icon, title, value) ->
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(InputBg)
                            .border(1.dp, InputBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(icon, fontSize = 11.sp)
                        Column {
                            Text(title, color = DuolingoSubtext, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }

            // 5. Synchronized Code Panel with Language Selector
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black.copy(alpha = 0.45f))
                    .border(1.dp, InputBorder, RoundedCornerShape(10.dp))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("ALGORITHM LOGIC", color = DuolingoSubtext, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("Python", "Swift", "Java", "C++").forEach { lang ->
                            val isSel = selectedLanguage == lang
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) DsaBlue else Color.Transparent)
                                    .clickable { selectedLanguage = lang }
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    lang,
                                    color = if (isSel) Color.White else DuolingoSubtext,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                val lines = codeSnippets[selectedLanguage] ?: codeSnippets["Python"]!!
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    lines.forEachIndexed { idx, line ->
                        val isHighlighted = (currentStep.codeLine == idx + 1)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isHighlighted) Color.Yellow.copy(alpha = 0.18f) else Color.Transparent)
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "${idx + 1}".padStart(2),
                                color = DuolingoSubtext.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                line,
                                color = if (isHighlighted) Color(0xFFFFD166) else Color.White.copy(alpha = 0.85f),
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (isHighlighted) FontWeight.Black else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // 6. Playback Slider & Transport Controls
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Step ${currentStepIndex + 1} of ${steps.size}", color = DuolingoSubtext, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    TextButton(onClick = {
                        speed = when (speed) {
                            0.5f -> 1f
                            1f -> 2f
                            2f -> 3f
                            else -> 0.5f
                        }
                    }) {
                        Text("${speed}x", color = DsaOrange, fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }
                }

                Slider(
                    value = currentStepIndex.toFloat(),
                    onValueChange = {
                        isPlaying = false
                        currentStepIndex = it.toInt()
                    },
                    valueRange = 0f..(steps.size - 1).toFloat(),
                    steps = steps.size - 2,
                    colors = SliderDefaults.colors(
                        thumbColor = DsaOrange,
                        activeTrackColor = DsaOrange,
                        inactiveTrackColor = InputBorder
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = { if (currentStepIndex > 0) currentStepIndex-- },
                        enabled = currentStepIndex > 0
                    ) {
                        Text("⏮", color = if (currentStepIndex > 0) Color.White else DuolingoSubtext, fontSize = 18.sp)
                    }
                    Spacer(Modifier.width(16.dp))
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(DsaOrange)
                            .clickable { isPlaying = !isPlaying },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (isPlaying) "⏸" else "▶", color = Color.White, fontSize = 20.sp)
                    }
                    Spacer(Modifier.width(16.dp))
                    IconButton(
                        onClick = { if (currentStepIndex < steps.size - 1) currentStepIndex++ },
                        enabled = currentStepIndex < steps.size - 1
                    ) {
                        Text("⏭", color = if (currentStepIndex < steps.size - 1) Color.White else DuolingoSubtext, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// IDIOMATIC CODE IMPLEMENTATION CARD (matching iOS DSALanguageImplementationCardView)
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
@Composable
private fun IdiomaticCodeCard(structure: String, language: String) {
    val clipboardManager = LocalClipboardManager.current
    var activeLanguage by remember(language) { mutableStateOf(language) }
    var isExpanded by remember { mutableStateOf(true) }
    var showCopied by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val supportedLanguages = listOf("Python", "Java", "C++", "Swift", "Go", "JavaScript", "Kotlin")

    val langFlag = when (activeLanguage.lowercase().trim()) {
        "python" -> "🐍"
        "java" -> "☕️"
        "c++", "cpp" -> "⚙️"
        "swift" -> "🦅"
        "go" -> "🐹"
        "javascript", "js" -> "📜"
        "kotlin" -> "📱"
        else -> "💻"
    }

    val fileExt = when (activeLanguage.lowercase().trim()) {
        "python" -> "py"
        "java" -> "java"
        "c++", "cpp" -> "cpp"
        "swift" -> "swift"
        "go" -> "go"
        "javascript", "js" -> "js"
        "kotlin" -> "kt"
        else -> "txt"
    }

    val code = remember(structure, activeLanguage) {
        DsaLanguageCodeProvider.getCode(structure, activeLanguage)
    }

    val lines = remember(code) { code.lines() }

    val complexityChips = remember(structure) {
        when {
            structure.contains("Stack", ignoreCase = true) -> listOf("Push" to "O(1)", "Pop" to "O(1)", "Peek" to "O(1)", "Space" to "O(n)")
            structure.contains("Queue", ignoreCase = true) -> listOf("Enqueue" to "O(1)", "Dequeue" to "O(1)", "Front" to "O(1)", "Space" to "O(n)")
            structure.contains("Linked", ignoreCase = true) -> listOf("Insert Head" to "O(1)", "Search" to "O(n)", "Delete" to "O(n)", "Space" to "O(n)")
            structure.contains("AVL", ignoreCase = true) -> listOf("Search" to "O(log n)", "Insert" to "O(log n)", "Rotate" to "O(1)", "Space" to "O(n)")
            structure.contains("Tree", ignoreCase = true) || structure.contains("BST", ignoreCase = true) -> listOf("Search" to "O(h)", "Insert" to "O(h)", "Inorder" to "O(n)", "Space" to "O(h)")
            structure.contains("Trie", ignoreCase = true) -> listOf("Insert" to "O(k)", "Search" to "O(k)", "Prefix" to "O(k)", "Space" to "O(Σ*k)")
            structure.contains("Graph", ignoreCase = true) -> listOf("Add Edge" to "O(1)", "BFS" to "O(V+E)", "DFS" to "O(V+E)", "Space" to "O(V+E)")
            structure.contains("Sort", ignoreCase = true) -> listOf("Best" to "O(n)", "Average" to "O(n²)", "Worst" to "O(n²)", "Space" to "O(1)")
            else -> listOf("Access" to "O(1)", "Search" to "O(n)", "Insert" to "O(n)", "Delete" to "O(n)")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(InputBg)
            .border(1.5.dp, InputBorder, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(langFlag, fontSize = 22.sp)

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(activeLanguage, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
                    Text("• $structure", color = DuolingoSubtext, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Text("Idiomatic Data Structure Implementation", color = DuolingoSubtext, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }

            // Copy button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(CardBg)
                    .border(1.dp, if (showCopied) DsaGreen else InputBorder, RoundedCornerShape(10.dp))
                    .clickable {
                        clipboardManager.setText(AnnotatedString(code))
                        showCopied = true
                        coroutineScope.launch {
                            delay(1800)
                            showCopied = false
                        }
                    }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(if (showCopied) "✓" else "📋", fontSize = 11.sp)
                    Text(
                        if (showCopied) "Copied!" else "Copy",
                        color = if (showCopied) DsaGreen else Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Collapse button
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { isExpanded = !isExpanded }
                    .padding(4.dp)
            ) {
                Text(
                    text = if (isExpanded) "▲" else "▼",
                    color = DuolingoSubtext,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Language chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(supportedLanguages) { lang ->
                val isSelected = activeLanguage.equals(lang, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) DsaBlue else CardBg)
                        .border(1.dp, if (isSelected) DsaBlue else InputBorder, RoundedCornerShape(12.dp))
                        .clickable { activeLanguage = lang }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = lang,
                        color = if (isSelected) Color.White else DuolingoSubtext,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        // Big-O Chips Bar
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(complexityChips) { (op, time) ->
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CardBg)
                        .border(1.dp, InputBorder.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(op, color = DuolingoSubtext, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    val timeColor = when {
                        time.contains("1") -> DsaGreen
                        time.contains("log") -> DsaCyan
                        else -> DsaOrange
                    }
                    Text(time, color = timeColor, fontSize = 11.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                }
            }
        }

        // Code Box
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.2.dp, InputBorder, RoundedCornerShape(14.dp))
                    .background(Color(0xFF0F172A))
            ) {
                // Window Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.4f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFF5F56)))
                        Box(Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFFBD2E)))
                        Box(Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF27C93F)))
                    }
                    Spacer(Modifier.weight(1f))
                    val fileName = "${structure.lowercase().replace(" ", "")}.$fileExt"
                    Text(fileName, color = DuolingoSubtext, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium)
                }

                HorizontalDivider(color = InputBorder.copy(alpha = 0.4f), thickness = 1.dp)

                // Code Lines with line numbers
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp)
                        .verticalScroll(rememberScrollState())
                        .horizontalScroll(rememberScrollState())
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    lines.forEachIndexed { idx, line ->
                        Row(verticalAlignment = Alignment.Top) {
                            Text(
                                text = "${idx + 1}".padStart(3),
                                color = DuolingoSubtext.copy(alpha = 0.5f),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.width(28.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            val trimmed = line.trim()
                            val color = when {
                                trimmed.startsWith("//") || trimmed.startsWith("#") || trimmed.startsWith("/*") -> DuolingoSubtext
                                trimmed.startsWith("class ") || trimmed.startsWith("struct ") || trimmed.startsWith("public class ") || trimmed.startsWith("type ") -> Color(0xFFFFD166)
                                trimmed.startsWith("def ") || trimmed.startsWith("func ") || trimmed.startsWith("fun ") || trimmed.startsWith("public ") || trimmed.startsWith("template ") -> DsaCyan
                                trimmed.contains("return ") -> DsaGreen
                                else -> Color.White.copy(alpha = 0.92f)
                            }
                            Text(
                                text = line,
                                color = color,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// REUSABLE VISUALIZER CARD CONTAINER (matching iOS VisualizerCard)
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
@Composable
private fun VisualizerCardContainer(
    title: String,
    subtitle: String,
    accentColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardBg)
            .border(1.5.dp, InputBorder, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
            Text(subtitle, color = DuolingoSubtext, fontSize = 13.sp)
        }
        content()
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// DSA CONCEPT CARD (matching iOS DSAConceptCard)
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
@Composable
private fun DsaConceptCard(
    title: String,
    items: List<Pair<String, String>>,
    color: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(CardBg)
            .border(1.2.dp, InputBorder, RoundedCornerShape(18.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)

        items.forEach { (key, value) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    key,
                    color = color,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(color.copy(0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
                Text(
                    value,
                    color = DuolingoSubtext,
                    fontSize = 13.sp
                )
            }
        }
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// SHARED COMPONENTS
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
private fun ActionStatusText(text: String, color: Color) {
    Text(
        text = text,
        color = color,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(vertical = 6.dp, horizontal = 12.dp)
    )
}

@Composable
private fun ActionButton(
    label: String,
    icon: String,
    color: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(color)
            .then(
                if (enabled) Modifier.clickable(onClick = onClick)
                else Modifier
            )
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = 12.sp)
            Text(
                label,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1
            )
        }
    }
}
