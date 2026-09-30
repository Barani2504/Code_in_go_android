package com.simats.duolingo.feature.visualizer

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

enum class VisualizerType(val label: String, val icon: String, val color: Color) {
    STACK("Stack", "🥞", Color(0xFF1CB0F6)),
    ARRAY("Array", "📊", Color(0xFF58CC02)),
    QUEUE("Queue", "🚶‍♂️", Color(0xFFFF9600)),
    LINKED_LIST("Linked List", "🔗", Color(0xFFCE82FF)),
    BINARY_TREE("Binary Tree", "🌲", Color(0xFF2B70C9)),
    AVL_TREE("AVL Tree", "⚖️", Color(0xFFFF4B4B)),
    TRIE("Trie", "🔍", Color(0xFF00CD9C)),
    GRAPH("Graph", "🕸️", Color(0xFFFF86D0)),
    SORTING("Sorting", "📶", Color(0xFFFFC800)),
    STEP_RECORDER("Step Engine", "⏱️", Color(0xFF9C27B0))
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
        // ── Top Header with Language Pill ──────────────────────────────────
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
                    text = "DSA Visualizer 🔬",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Watch structures & algorithms come alive",
                    color = DuolingoSubtext,
                    fontSize = 12.sp
                )
            }

            // Language Selector Pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF26204E))
                    .border(1.dp, Color(0xFF58CC02).copy(alpha = 0.5f), RoundedCornerShape(14.dp))
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
                    fontWeight = FontWeight.Bold
                )
                Text("▼", color = DuolingoSubtext, fontSize = 9.sp)
            }
        }

        // ── Visualizer Type Tabs ──────────────────────────────────────────
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            items(VisualizerType.entries) { type ->
                val isSelected = selectedType == type
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) type.color else Color(0xFF26204E))
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) Color.White.copy(0.4f) else Color.White.copy(0.1f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { selectedType = type }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(type.icon, fontSize = 14.sp)
                    Text(
                        text = type.label,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold
                    )
                }
            }
        }

        // ── Visualizer Content Area ───────────────────────────────────────
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
            IdiomaticCodeCard(structure = selectedType.label, language = currentLang)

            Spacer(Modifier.height(30.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 1. STACK VISUALIZER CARD (LIFO)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun StackVisualizerCard() {
    var items by remember { mutableStateOf(listOf(10, 20, 30)) }
    var nextVal by remember { mutableIntStateOf(40) }
    var actionNote by remember { mutableStateOf("Stack initialized. Last In, First Out.") }

    CardContainer(
        title = "🥞 Stack (LIFO)",
        subtitle = "Pancakes in a stack. Push & Pop occur strictly at the TOP.",
        accentColor = Color(0xFF1CB0F6)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Visual Stack Elements
            Column(
                modifier = Modifier
                    .width(180.dp)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                    .background(Color(0xFF14122C))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (items.isEmpty()) {
                    Text("Empty Stack (Underflow)", color = DuolingoSubtext, fontSize = 12.sp, modifier = Modifier.padding(vertical = 24.dp))
                } else {
                    Text("⬆ TOP", color = Color(0xFF1CB0F6), fontSize = 11.sp, fontWeight = FontWeight.Black)
                    items.asReversed().forEachIndexed { index, value ->
                        val isTop = index == 0
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isTop) Color(0xFF1CB0F6) else Color(0xFF26204E))
                                .border(1.dp, Color(0xFF1CB0F6).copy(0.6f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$value",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
                // Base
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(Color.White.copy(0.3f), RoundedCornerShape(3.dp))
                )
            }

            Text(actionNote, color = Color(0xFF1CB0F6), fontSize = 12.sp, fontWeight = FontWeight.Bold)

            // Operation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        if (items.size < 6) {
                            items = items + nextVal
                            actionNote = "PUSH($nextVal) onto TOP -> O(1) constant time!"
                            nextVal += 10
                        } else {
                            actionNote = "Stack Overflow! Capacity reached."
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1CB0F6))
                ) {
                    Text("PUSH $nextVal", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        if (items.isNotEmpty()) {
                            val popped = items.last()
                            items = items.dropLast(1)
                            actionNote = "POP() removed $popped from TOP -> O(1) LIFO!"
                        } else {
                            actionNote = "Stack Underflow! Stack is already empty."
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4B4B))
                ) {
                    Text("POP", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        if (items.isNotEmpty()) {
                            actionNote = "PEEK() inspected ${items.last()} without removing."
                        } else {
                            actionNote = "PEEK() -> Stack is empty."
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26204E))
                ) {
                    Text("PEEK", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 2. ARRAY VISUALIZER CARD (Contiguous Memory)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ArrayVisualizerCard() {
    var array by remember { mutableStateOf(listOf(10, 20, 30, 40, 50)) }
    var highlightedIndex by remember { mutableStateOf<Int?>(null) }
    var actionNote by remember { mutableStateOf("Contiguous memory buffer. Address: base + i * size.") }
    val scope = rememberCoroutineScope()

    CardContainer(
        title = "📊 Array (Contiguous Buffer)",
        subtitle = "Random access in O(1). Insert & delete require shifting O(n).",
        accentColor = Color(0xFF58CC02)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Visual Array Cells
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                array.forEachIndexed { index, value ->
                    val isHighlighted = highlightedIndex == index
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("[$index]", color = DuolingoSubtext, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isHighlighted) Color(0xFFFFC800) else Color(0xFF26204E))
                                .border(
                                    2.dp,
                                    if (isHighlighted) Color.White else Color(0xFF58CC02),
                                    RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$value",
                                color = if (isHighlighted) Color.Black else Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }

            Text(actionNote, color = Color(0xFF58CC02), fontSize = 12.sp, fontWeight = FontWeight.Bold)

            // Operations
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            for (i in array.indices) {
                                highlightedIndex = i
                                actionNote = "Scanning index [$i] -> value ${array[i]}"
                                delay(300)
                            }
                            highlightedIndex = null
                            actionNote = "Conveyor sweep complete! Visited ${array.size} elements in O(n)."
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF58CC02))
                ) {
                    Text("SCAN O(n)", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        if (array.size < 6) {
                            array = listOf(array.first() - 5) + array
                            actionNote = "Insert at index [0] shifted all ${array.size - 1} elements right! O(n)."
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26204E))
                ) {
                    Text("+ SHIFT HEAD", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        if (array.isNotEmpty()) {
                            highlightedIndex = 2.coerceAtMost(array.size - 1)
                            actionNote = "Direct memory read: array[2] = ${array[highlightedIndex!!]} in O(1)!"
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1CB0F6))
                ) {
                    Text("READ [2] O(1)", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 3. QUEUE VISUALIZER CARD (FIFO)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun QueueVisualizerCard() {
    var items by remember { mutableStateOf(listOf("A", "B", "C")) }
    var nextItemIndex by remember { mutableIntStateOf(4) }
    var actionNote by remember { mutableStateOf("Queue initialized. First In, First Out (FIFO).") }

    CardContainer(
        title = "🚶‍♂️ Queue (FIFO)",
        subtitle = "Checkout line. Enqueue at REAR, Dequeue from FRONT.",
        accentColor = Color(0xFFFF9600)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF14122C))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("FRONT ➡️", color = Color(0xFFFF4B4B), fontSize = 11.sp, fontWeight = FontWeight.Black)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (items.isEmpty()) {
                        Text("Queue Empty", color = DuolingoSubtext, fontSize = 12.sp)
                    } else {
                        items.forEach { item ->
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFF9600))
                                    .border(1.dp, Color.White.copy(0.3f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(item, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }

                Text("⬅️ REAR", color = Color(0xFF58CC02), fontSize = 11.sp, fontWeight = FontWeight.Black)
            }

            Text(actionNote, color = Color(0xFFFF9600), fontSize = 12.sp, fontWeight = FontWeight.Bold)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        if (items.size < 5) {
                            val newItem = ('A' + nextItemIndex - 1).toString()
                            items = items + newItem
                            actionNote = "ENQUEUE('$newItem') at REAR -> O(1) constant time!"
                            nextItemIndex++
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF58CC02))
                ) {
                    Text("ENQUEUE", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        if (items.isNotEmpty()) {
                            val removed = items.first()
                            items = items.drop(1)
                            actionNote = "DEQUEUE() served '$removed' from FRONT -> O(1) FIFO!"
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4B4B))
                ) {
                    Text("DEQUEUE", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 4. LINKED LIST VISUALIZER CARD
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun LinkedListVisualizerCard() {
    var nodes by remember { mutableStateOf(listOf(12, 99, 37)) }
    var actionNote by remember { mutableStateOf("Nodes connected by next pointers. Dynamic memory allocation.") }

    CardContainer(
        title = "🔗 Singly Linked List",
        subtitle = "Non-contiguous nodes. Insertion at HEAD is O(1).",
        accentColor = Color(0xFFCE82FF)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item {
                    Text("HEAD ➔", color = Color(0xFFCE82FF), fontSize = 12.sp, fontWeight = FontWeight.Black)
                }
                items(nodes) { value ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF26204E))
                                .border(1.5.dp, Color(0xFFCE82FF), RoundedCornerShape(10.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text("$value", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
                        }
                        Text(" ➔ ", color = Color(0xFFCE82FF), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
                item {
                    Text("NULL", color = Color.Red.copy(0.8f), fontSize = 12.sp, fontWeight = FontWeight.Black)
                }
            }

            Text(actionNote, color = Color(0xFFCE82FF), fontSize = 12.sp, fontWeight = FontWeight.Bold)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        val newV = (10..90).random()
                        nodes = listOf(newV) + nodes
                        actionNote = "Inserted $newV at HEAD: new_node.next = head; head = new_node. O(1)!"
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCE82FF))
                ) {
                    Text("+ HEAD O(1)", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        nodes = nodes.reversed()
                        actionNote = "Reversed 3-pointer dance: curr.next = prev. O(n) traversal."
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26204E))
                ) {
                    Text("REVERSE", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 5. BINARY TREE & BST VISUALIZER CARD
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun BinaryTreeVisualizerCard() {
    var traversalType by remember { mutableStateOf("In-Order (L-Root-R)") }
    var traversalResult by remember { mutableStateOf("10 ➔ 20 ➔ 30 ➔ 40 ➔ 50") }

    CardContainer(
        title = "🌲 Binary Search Tree (BST)",
        subtitle = "Left subtree < Root < Right subtree. Average search O(log n).",
        accentColor = Color(0xFF2B70C9)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Simplified Tree Diagram
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TreeNodeBubble(value = "30", isRoot = true)
                Text("┌──────┴──────┐", color = Color.White.copy(0.4f), fontFamily = FontFamily.Monospace)
                Row(horizontalArrangement = Arrangement.spacedBy(40.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        TreeNodeBubble(value = "20")
                        Text("┌──┴──┐", color = Color.White.copy(0.4f), fontFamily = FontFamily.Monospace)
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            TreeNodeBubble(value = "10")
                            Box(modifier = Modifier.size(28.dp))
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        TreeNodeBubble(value = "40")
                        Text("┌──┴──┐", color = Color.White.copy(0.4f), fontFamily = FontFamily.Monospace)
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Box(modifier = Modifier.size(28.dp))
                            TreeNodeBubble(value = "50")
                        }
                    }
                }
            }

            Text("Traversal: $traversalType", color = Color(0xFF2B70C9), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(traversalResult, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = {
                        traversalType = "In-Order (Sorted)"
                        traversalResult = "10 ➔ 20 ➔ 30 ➔ 40 ➔ 50"
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B70C9))
                ) {
                    Text("IN-ORDER", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        traversalType = "Pre-Order (Root First)"
                        traversalResult = "30 ➔ 20 ➔ 10 ➔ 40 ➔ 50"
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26204E))
                ) {
                    Text("PRE-ORDER", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        traversalType = "Post-Order (Leaves First)"
                        traversalResult = "10 ➔ 20 ➔ 50 ➔ 40 ➔ 30"
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26204E))
                ) {
                    Text("POST-ORDER", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun TreeNodeBubble(value: String, isRoot: Boolean = false) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(if (isRoot) Color(0xFF2B70C9) else Color(0xFF26204E))
            .border(1.5.dp, Color(0xFF2B70C9), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(value, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 6. AVL TREE VISUALIZER CARD (Self-Balancing)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AvlTreeVisualizerCard() {
    var rotationState by remember { mutableStateOf("Balanced (BF: 0)") }

    CardContainer(
        title = "⚖️ AVL Self-Balancing Tree",
        subtitle = "Maintains balance factor in {-1, 0, +1} through single/double rotations.",
        accentColor = Color(0xFFFF4B4B)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Balance Factor = Height(Left) - Height(Right)", color = DuolingoSubtext, fontSize = 12.sp)
            Text("Current: $rotationState", color = Color(0xFFFF4B4B), fontSize = 13.sp, fontWeight = FontWeight.Bold)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { rotationState = "Right Rotation (LL Case applied) -> Balanced" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4B4B))
                ) {
                    Text("RIGHT ROTATE", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { rotationState = "Left Rotation (RR Case applied) -> Balanced" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26204E))
                ) {
                    Text("LEFT ROTATE", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 7. TRIE VISUALIZER CARD (Prefix Tree)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun TrieVisualizerCard() {
    var query by remember { mutableStateOf("c") }
    val dictionary = listOf("cat", "car", "cart", "card", "dog", "door")
    val matched = dictionary.filter { it.startsWith(query.lowercase()) }

    CardContainer(
        title = "🔍 Trie (Prefix Tree)",
        subtitle = "Shared character branches for ultra-fast dictionary autocomplete.",
        accentColor = Color(0xFF00CD9C)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("c", "ca", "car", "do").forEach { p ->
                    FilterChip(
                        selected = query == p,
                        onClick = { query = p },
                        label = { Text(p, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            Text("Prefix '$query' matches:", color = Color(0xFF00CD9C), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                matched.forEach { word ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF00CD9C).copy(0.2f))
                            .border(1.dp, Color(0xFF00CD9C), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(word, color = Color.White, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 8. GRAPH VISUALIZER CARD
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun GraphVisualizerCard() {
    var traversalResult by remember { mutableStateOf("Select BFS or DFS") }

    CardContainer(
        title = "🕸️ Graph (BFS / DFS)",
        subtitle = "Vertices & Edges. BFS uses Queue; DFS uses Recursion / Stack.",
        accentColor = Color(0xFFFF86D0)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Graph: [0]—[1], [0]—[2], [1]—[3], [2]—[4]", color = DuolingoSubtext, fontSize = 12.sp)
            Text(traversalResult, color = Color(0xFFFF86D0), fontSize = 13.sp, fontWeight = FontWeight.Bold)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { traversalResult = "BFS (Level Ripple): 0 ➔ 1 ➔ 2 ➔ 3 ➔ 4" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF86D0))
                ) {
                    Text("BFS RIPPLE", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
                Button(
                    onClick = { traversalResult = "DFS (Deep Dive): 0 ➔ 1 ➔ 3 ➔ 2 ➔ 4" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26204E))
                ) {
                    Text("DFS DIVER", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 9. SORTING VISUALIZER CARD
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun SortingVisualizerCard() {
    var bars by remember { mutableStateOf(listOf(45, 12, 85, 32, 60, 22, 95, 50)) }
    var activeSortNote by remember { mutableStateOf("Unsorted list of numbers.") }
    val scope = rememberCoroutineScope()

    CardContainer(
        title = "📶 Sorting Algorithms",
        subtitle = "Compare elements and swap them into non-decreasing order.",
        accentColor = Color(0xFFFFC800)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Bars visualization
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF14122C))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                bars.forEach { h ->
                    Box(
                        modifier = Modifier
                            .width(22.dp)
                            .height((h * 0.9).dp)
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(Color(0xFFFFC800))
                    )
                }
            }

            Text(activeSortNote, color = Color(0xFFFFC800), fontSize = 12.sp, fontWeight = FontWeight.Bold)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        scope.launch {
                            val mutable = bars.toMutableList()
                            val n = mutable.size
                            for (i in 0 until n - 1) {
                                for (j in 0 until n - i - 1) {
                                    if (mutable[j] > mutable[j + 1]) {
                                        val temp = mutable[j]
                                        mutable[j] = mutable[j + 1]
                                        mutable[j + 1] = temp
                                        bars = mutable.toList()
                                        activeSortNote = "Swapped ${mutable[j]} and ${mutable[j + 1]}"
                                        delay(80)
                                    }
                                }
                            }
                            activeSortNote = "Bubble Sort Complete! O(n^2) comparisons."
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC800))
                ) {
                    Text("BUBBLE SORT", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 10.sp)
                }

                Button(
                    onClick = {
                        bars = bars.shuffled()
                        activeSortNote = "Array shuffled randomly."
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26204E))
                ) {
                    Text("SHUFFLE", fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 10. STEP RECORDER ENGINE CARD (Master Spec Visualizer Engine)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun StepRecorderEngineCard() {
    var stepIndex by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var speed by remember { mutableFloatStateOf(1f) }

    val steps = listOf(
        "Allocate array [10, 20, 30, 40] in memory" to "Line 1: arr = [10, 20, 30, 40]",
        "Shift element 40 to index 4" to "Line 2: arr[4] = arr[3]",
        "Shift element 30 to index 3" to "Line 3: arr[3] = arr[2]",
        "Write 25 into index 2" to "Line 4: arr[2] = 25"
    )

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay((800 / speed).toLong())
            if (stepIndex < steps.size - 1) {
                stepIndex++
            } else {
                isPlaying = false
            }
        }
    }

    CardContainer(
        title = "⏱️ Step Recorder Engine",
        subtitle = "Synchronized Code Execution, Phoenix Narration & Live Complexity.",
        accentColor = Color(0xFF9C27B0)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Phoenix Narration Bubble
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF2A2050))
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("🔥", fontSize = 24.sp)
                Column {
                    Text("PHOENIX EXPLAINS", color = Color(0xFFFF9600), fontSize = 10.sp, fontWeight = FontWeight.Black)
                    Text(steps[stepIndex].first, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Synchronized Code Panel
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF14122C))
                    .padding(12.dp)
            ) {
                Text(
                    text = steps[stepIndex].second,
                    color = Color(0xFF00CD9C),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Controls: Step back, Play/Pause, Step forward, Speed
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { if (stepIndex > 0) stepIndex-- }) {
                    Text("⏮", color = Color.White, fontSize = 18.sp)
                }
                IconButton(onClick = { isPlaying = !isPlaying }) {
                    Text(if (isPlaying) "⏸" else "▶", color = Color(0xFF9C27B0), fontSize = 22.sp)
                }
                IconButton(onClick = { if (stepIndex < steps.size - 1) stepIndex++ }) {
                    Text("⏭", color = Color.White, fontSize = 18.sp)
                }

                TextButton(onClick = {
                    speed = when (speed) {
                        1f -> 1.5f
                        1.5f -> 2f
                        else -> 1f
                    }
                }) {
                    Text("${speed}x", color = Color.White, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// IDIOMATIC CODE IMPLEMENTATION CARD
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun IdiomaticCodeCard(structure: String, language: String) {
    val code = remember(structure, language) {
        DsaLanguageCodeProvider.getCode(structure, language)
    }

    CardContainer(
        title = "💻 $structure in $language",
        subtitle = "Production idiomatic syntax with optimal time/space complexity.",
        accentColor = Color(0xFF58CC02)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF14122C))
                    .padding(14.dp)
            ) {
                Text(
                    text = code,
                    color = Color(0xFFE2E8F0),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// REUSABLE CARD CONTAINER
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun CardContainer(
    title: String,
    subtitle: String,
    accentColor: Color,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF1E1A3C))
            .border(1.5.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column {
            Text(title, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Black)
            Text(subtitle, color = DuolingoSubtext, fontSize = 12.sp)
        }
        content()
    }
}
