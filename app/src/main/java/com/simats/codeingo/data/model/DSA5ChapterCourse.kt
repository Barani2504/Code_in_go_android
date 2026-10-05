package com.simats.codeingo.data.model

import androidx.compose.ui.graphics.Color
import com.simats.codeingo.ui.theme.*

// ══════════════════════════════════════════════════════════════════
// DSA 5-Chapter Course Model
// Direct port of iOS DSA5ChapterCourse.swift
// ══════════════════════════════════════════════════════════════════

// ── Environment Themes ────────────────────────────────────────────

enum class DSAEnvironmentTheme(
    val displayName: String,
    val icon: String,    // Material icon name
    val emoji: String,
) {
    HIGHWAY       ("Futuristic Highway",        "directions_car",   "🚀"),
    CHAIN_WORLD   ("Magical Chain World",        "link",             "🔗"),
    TOWER_PLATES  ("Restaurant Tower",           "restaurant",       "🍽️"),
    TICKET_STATION("Futuristic Ticket Station",  "local_activity",   "🎟️"),
    DATA_KINGDOM  ("Magical Data Kingdom",       "park",             "🌳"),
}

// ── Interaction Types ─────────────────────────────────────────────

enum class DSALevelInteractionType(val displayName: String) {
    MULTIPLE_CHOICE          ("Multiple Choice"),
    TAP_CORRECT_NODE         ("Tap the Correct Node"),
    DRAG_AND_DROP_ORDER      ("Drag & Drop Order"),
    CONNECT_NODES            ("Connect Nodes"),
    INSERT_NODE              ("Insert a Node"),
    PUSH_STACK               ("Push to Stack"),
    POP_STACK                ("Pop from Stack"),
    STACK_SEQUENCE_CHALLENGE ("Stack Sequence Challenge"),
    FIFO_QUEUE_ORDER         ("Understand FIFO"),
    ENQUEUE_LINE             ("Enqueue to Back"),
    DEQUEUE_LINE             ("Dequeue from Front"),
    QUEUE_SEQUENCE_CHALLENGE ("Queue Sequence Puzzle"),
    FIND_TREE_ROOT           ("Find the Root"),
    TREE_PARENT_CHILD        ("Parent & Child"),
    FIND_TREE_LEAVES         ("Find the Leaves"),
    TREE_TRAVERSAL           ("Tree Traversal"),
    TIMED_BOSS_RACE          ("Timed Boss Race"),
    BROKEN_CHAIN_BOSS        ("Broken Chain Repair"),
    ESCAPE_TOWER_BOSS        ("Escape the Tower Boss"),
    TICKET_RUSH_BOSS         ("Ticket Rush Boss"),
    SAVE_KINGDOM_BOSS        ("Save Data Kingdom Boss"),
}

// ── Visual Primitives ─────────────────────────────────────────────

data class DSAVisualBoxItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val value: String,
    val index: Int,
    val isHighlighted: Boolean = false,
    val isTarget: Boolean = false,
    val colorName: String = "blue",
)

data class DSALinkedNode(
    val id: String = java.util.UUID.randomUUID().toString(),
    val value: String,
    val nextId: String? = null,
    val isHead: Boolean = false,
    val isNull: Boolean = false,
    val isHighlighted: Boolean = false,
)

data class DSAStackPlate(
    val id: String = java.util.UUID.randomUUID().toString(),
    val value: String,
    val color: Color,
    val isRemoved: Boolean = false,
)

data class DSAQueuePerson(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val emoji: String,
    val color: Color,
    val isServed: Boolean = false,
)

data class DSATreeNodeItem(
    val id: String,
    val label: String,
    val level: Int,
    val parentId: String?,
    val isRoot: Boolean = false,
    val isLeaf: Boolean = false,
    val isSelected: Boolean = false,
    val isHighlighted: Boolean = false,
    val xPos: Float = 0f,
    val yPos: Float = 0f,
)

// ── Rapid Boss Question ───────────────────────────────────────────

data class DSARapidQuestion(
    val id: String = java.util.UUID.randomUUID().toString(),
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
)

// ── Level Specification ───────────────────────────────────────────

data class DSALevelSpec(
    val id: String,
    val chapterId: Int,
    val levelNumber: Int,
    val title: String,
    val subtitle: String,
    val interactionType: DSALevelInteractionType,
    val storyExplanation: String,
    val promptQuestion: String,
    val hint: String,
    val explanation: String,
    val xpReward: Int = 15,
    // Multiple choice
    val mcOptions: List<String> = emptyList(),
    val mcCorrectIndex: Int = 0,
    // Tap-box
    val boxes: List<DSAVisualBoxItem> = emptyList(),
    val correctBoxIndex: Int? = null,
    // Drag & drop
    val draggableItems: List<String> = emptyList(),
    val correctOrder: List<String> = emptyList(),
    // Linked list
    val linkedNodes: List<DSALinkedNode> = emptyList(),
    val targetNodeValue: String = "",
    val insertNodeValue: String = "",
    val brokenNodeIndex: Int = 1,
    // Stack
    val stackInitial: List<String> = emptyList(),
    val targetPushItems: List<String> = emptyList(),
    val stackSequence: List<String> = emptyList(),
    val stackSequenceAnswer: String = "40",
    // Queue
    val queueInitial: List<String> = emptyList(),
    val queueEnqueueItems: List<String> = emptyList(),
    val queueSequence: List<String> = emptyList(),
    val queueSequenceAnswer: List<String> = emptyList(),
    // Tree
    val treeNodes: List<DSATreeNodeItem> = emptyList(),
    val correctLeafIds: Set<String> = emptySet(),
    val traversalSolution: List<String> = emptyList(),
    // Boss
    val timeLimitSeconds: Int = 45,
    val bossTotalQuestions: Int = 5,
    val bossRapidQuestions: List<DSARapidQuestion> = emptyList(),
) {
    /** True when this level is a boss challenge. */
    val isBoss: Boolean get() = interactionType in listOf(
        DSALevelInteractionType.TIMED_BOSS_RACE,
        DSALevelInteractionType.BROKEN_CHAIN_BOSS,
        DSALevelInteractionType.ESCAPE_TOWER_BOSS,
        DSALevelInteractionType.TICKET_RUSH_BOSS,
        DSALevelInteractionType.SAVE_KINGDOM_BOSS,
    )
}

// ── Chapter Definition ────────────────────────────────────────────

data class DSAChapterModel(
    val id: Int,
    val number: Int,
    val title: String,
    val subtitle: String,
    val theme: DSAEnvironmentTheme,
    val story: String,
    val badgeName: String,
    val primaryColor: Color,
    val darkColor: Color,
    val accentColor: Color,
    val characterEmoji: String,
    val completionBonusXP: Int = 100,
    val levels: List<DSALevelSpec>,
) {
    val levelCount: Int get() = levels.size
    val bossLevel: DSALevelSpec? get() = levels.firstOrNull { it.isBoss }
}

// ══════════════════════════════════════════════════════════════════
// 5-CHAPTER MASTER CATALOG
// ══════════════════════════════════════════════════════════════════
typealias DSA5ChapterCourse = DSA5ChapterData

object DSA5ChapterData {

    val chapters: List<DSAChapterModel> = listOf(

        // ──────────────────────────────────────────────────────────
        // CHAPTER 1 — ARRAY: "The Data Highway"
        // ──────────────────────────────────────────────────────────
        DSAChapterModel(
            id = 1, number = 1,
            title = "Array",
            subtitle = "The Data Highway",
            theme = DSAEnvironmentTheme.HIGHWAY,
            story = "Welcome to the futuristic Data Highway! Every vehicle here parks in an exact numbered parking position. An Array stores elements in contiguous ordered positions called indexes, starting at 0!",
            badgeName = "Array Master",
            primaryColor = Color(red = 28f/255, green = 176f/255, blue = 246f/255),
            darkColor    = Color(red = 18f/255, green = 120f/255, blue = 180f/255),
            accentColor  = Color(red = 88f/255, green = 204f/255, blue = 2f/255),
            characterEmoji = "🏎️",
            completionBonusXP = 100,
            levels = listOf(
                DSALevelSpec(
                    id = "c1_l1", chapterId = 1, levelNumber = 1,
                    title = "Meet the Array",
                    subtitle = "Learn zero-based indexing",
                    interactionType = DSALevelInteractionType.MULTIPLE_CHOICE,
                    storyExplanation = "Every element in an array has a zero-based position called an Index. The first item is at index 0, the second is at index 1, and so on!",
                    promptQuestion = "Which index contains Mango?",
                    hint = "Start counting from 0: Apple is at 0, Banana is at 1...",
                    explanation = "Mango is in the 3rd box, which corresponds to index 2 in zero-based indexing! 🥭",
                    xpReward = 10,
                    mcOptions = listOf("0", "1", "2", "3"),
                    mcCorrectIndex = 2,
                    boxes = listOf(
                        DSAVisualBoxItem(value = "Apple",  index = 0, colorName = "red"),
                        DSAVisualBoxItem(value = "Banana", index = 1, colorName = "yellow"),
                        DSAVisualBoxItem(value = "Mango",  index = 2, colorName = "orange"),
                        DSAVisualBoxItem(value = "Orange", index = 3, colorName = "green"),
                    ),
                ),
                DSALevelSpec(
                    id = "c1_l2", chapterId = 1, levelNumber = 2,
                    title = "Find the Element",
                    subtitle = "Direct access in action",
                    interactionType = DSALevelInteractionType.TAP_CORRECT_NODE,
                    storyExplanation = "Because array elements are stored right next to each other in memory, your computer can jump directly to any index in instant O(1) time!",
                    promptQuestion = "What value is stored at index 3? Tap the correct box.",
                    hint = "Count indexes: 0, 1, 2, 3...",
                    explanation = "Index 0 is 10, index 1 is 25, index 2 is 40, and index 3 holds 55! 🎯",
                    xpReward = 15,
                    boxes = listOf(
                        DSAVisualBoxItem(value = "10", index = 0),
                        DSAVisualBoxItem(value = "25", index = 1),
                        DSAVisualBoxItem(value = "40", index = 2),
                        DSAVisualBoxItem(value = "55", index = 3),
                        DSAVisualBoxItem(value = "70", index = 4),
                    ),
                    correctBoxIndex = 3,
                ),
                DSALevelSpec(
                    id = "c1_l3", chapterId = 1, levelNumber = 3,
                    title = "Build the Array",
                    subtitle = "Construct contiguous memory",
                    interactionType = DSALevelInteractionType.DRAG_AND_DROP_ORDER,
                    storyExplanation = "Array elements must sit in consecutive memory slots without empty gaps in between.",
                    promptQuestion = "Arrange these elements in ascending array order from index 0 to 3.",
                    hint = "Place smallest numbers first: 10, then 20...",
                    explanation = "Perfect assembly! [10, 20, 30, 40] is now locked into consecutive memory slots. 🏎️💨",
                    xpReward = 15,
                    draggableItems = listOf("30", "10", "40", "20"),
                    correctOrder   = listOf("10", "20", "30", "40"),
                ),
                DSALevelSpec(
                    id = "c1_l4", chapterId = 1, levelNumber = 4,
                    title = "Array Challenge",
                    subtitle = "Test your highway reflexes",
                    interactionType = DSALevelInteractionType.MULTIPLE_CHOICE,
                    storyExplanation = "When accessing an array by index, the CPU computes: Address = BaseAddress + (Index * ItemSize). That's why it's so lightning fast!",
                    promptQuestion = "What happens when an element is accessed by its index?",
                    hint = "Does the computer search one by one, or jump directly?",
                    explanation = "Instant direct O(1) memory lookup! The memory address is calculated instantly. ⚡",
                    xpReward = 15,
                    mcOptions = listOf(
                        "It searches every item one by one (O(n))",
                        "Direct instant access via memory address calculation (O(1))",
                        "The array doubles in size",
                        "All other items shift to the right",
                    ),
                    mcCorrectIndex = 1,
                ),
                DSALevelSpec(
                    id = "c1_boss", chapterId = 1, levelNumber = 5,
                    title = "Chapter Boss: The Highway Race",
                    subtitle = "Answer 5 rapid questions before timer runs out!",
                    interactionType = DSALevelInteractionType.TIMED_BOSS_RACE,
                    storyExplanation = "A runaway glitch vehicle is speeding down the Data Highway! Answer 5 questions correctly to overtake it before time runs out!",
                    promptQuestion = "Race through all 5 Highway questions!",
                    hint = "Keep calm, trust your index math!",
                    explanation = "You won The Highway Race! You have mastered Arrays and unlocked the Array Master trophy! 🏆",
                    xpReward = 100, timeLimitSeconds = 50, bossTotalQuestions = 5,
                    bossRapidQuestions = listOf(
                        DSARapidQuestion(prompt = "If array size is 5, what is the index of the last element?",
                            options = listOf("4","5","0","1"), correctIndex = 0,
                            explanation = "Since arrays start at 0, the last element of size 5 is at index 4!"),
                        DSARapidQuestion(prompt = "What is the time complexity to access array[k]?",
                            options = listOf("O(1)","O(n)","O(log n)","O(n²)"), correctIndex = 0,
                            explanation = "Array index access is instantaneous O(1) time."),
                        DSARapidQuestion(prompt = "What happens when you insert an element at index 0?",
                            options = listOf("All existing elements shift right","The array is deleted","Index 0 is replaced","It takes O(1) time"),
                            correctIndex = 0, explanation = "Inserting at the beginning requires shifting all n elements right (O(n))."),
                        DSARapidQuestion(prompt = "In [10, 20, 30, 40, 50], what is at index 2?",
                            options = listOf("20","30","40","10"), correctIndex = 1,
                            explanation = "Index 0=10, 1=20, 2=30."),
                        DSARapidQuestion(prompt = "Why are array elements contiguous?",
                            options = listOf("Stored next to each other in memory","Randomly scattered across RAM","Stored on the cloud","Connected with arrows"),
                            correctIndex = 0, explanation = "Contiguous means contiguous memory blocks without gaps."),
                    ),
                ),
            ),
        ),

        // ──────────────────────────────────────────────────────────
        // CHAPTER 2 — LINKED LIST: "The Chain Quest"
        // ──────────────────────────────────────────────────────────
        DSAChapterModel(
            id = 2, number = 2,
            title = "Linked List",
            subtitle = "The Chain Quest",
            theme = DSAEnvironmentTheme.CHAIN_WORLD,
            story = "Welcome to the magical Chain Quest! Unlike arrays, linked list nodes can float anywhere in memory. Each node holds its data and a magical pointer arrow connecting to the next node!",
            badgeName = "Chain Master",
            primaryColor = Color(red = 175f/255, green = 82f/255, blue = 222f/255),
            darkColor    = Color(red = 120f/255, green = 40f/255, blue = 170f/255),
            accentColor  = Color(red = 255f/255, green = 215f/255, blue = 0f/255),
            characterEmoji = "🧙‍♂️",
            completionBonusXP = 100,
            levels = listOf(
                DSALevelSpec(
                    id = "c2_l1", chapterId = 2, levelNumber = 1,
                    title = "Meet the Nodes",
                    subtitle = "Anatomy of a node",
                    interactionType = DSALevelInteractionType.MULTIPLE_CHOICE,
                    storyExplanation = "A Linked List node consists of two parts: Data (the value stored) and Next (a reference or pointer to the next node). The last node points to NULL!",
                    promptQuestion = "What does the arrow in a linked-list node represent?",
                    hint = "It shows how the node finds its friend in memory...",
                    explanation = "The arrow represents the Next Reference (pointer) connecting to the next node in memory! 🔗",
                    xpReward = 10,
                    mcOptions = listOf("Index","Connection to the next node","Size","Value"),
                    mcCorrectIndex = 1,
                    linkedNodes = listOf(
                        DSALinkedNode(id = "n1", value = "10", nextId = "n2", isHead = true),
                        DSALinkedNode(id = "n2", value = "20", nextId = "n3"),
                        DSALinkedNode(id = "n3", value = "30", nextId = "n4"),
                        DSALinkedNode(id = "n4", value = "40", isNull = true),
                    ),
                ),
                DSALevelSpec(
                    id = "c2_l2", chapterId = 2, levelNumber = 2,
                    title = "Follow the Chain",
                    subtitle = "Traverse from node to node",
                    interactionType = DSALevelInteractionType.TAP_CORRECT_NODE,
                    storyExplanation = "To find an item, you start at the HEAD and follow pointers step by step. You cannot jump directly like an array!",
                    promptQuestion = "In HEAD → 10 → 20 → 30 → 40 → NULL, what node comes after 20?",
                    hint = "Follow the pointer leaving 20...",
                    explanation = "Node 20's next pointer points straight to 30! Watch the wizard fly across! ✨",
                    xpReward = 15,
                    linkedNodes = listOf(
                        DSALinkedNode(id = "n1", value = "10", nextId = "n2", isHead = true),
                        DSALinkedNode(id = "n2", value = "20", nextId = "n3"),
                        DSALinkedNode(id = "n3", value = "30", nextId = "n4"),
                        DSALinkedNode(id = "n4", value = "40", isNull = true),
                    ),
                    targetNodeValue = "30",
                ),
                DSALevelSpec(
                    id = "c2_l3", chapterId = 2, levelNumber = 3,
                    title = "Build the Chain",
                    subtitle = "Link the magical runes",
                    interactionType = DSALevelInteractionType.CONNECT_NODES,
                    storyExplanation = "Connecting nodes in order requires assigning each node's `next` pointer to the upcoming node, ending with NULL.",
                    promptQuestion = "Connect these nodes in order: 10 → 20 → 30 → 40.",
                    hint = "Tap 10 then 20, then 20 to 30, then 30 to 40...",
                    explanation = "Magical energy flows! The chain 10 → 20 → 30 → 40 → NULL is fully forged. 🔗⚡",
                    xpReward = 20,
                    draggableItems = listOf("10","20","30","40"),
                    correctOrder   = listOf("10","20","30","40"),
                ),
                DSALevelSpec(
                    id = "c2_l4", chapterId = 2, levelNumber = 4,
                    title = "Insert a Node",
                    subtitle = "Rewire pointers in O(1)",
                    interactionType = DSALevelInteractionType.INSERT_NODE,
                    storyExplanation = "Unlike an array, inserting into a linked list doesn't shift any other elements! You simply rewire 2 pointers: 20.next = 30, and 30.next = 40.",
                    promptQuestion = "Insert 30 between 20 and 40.",
                    hint = "Drag or tap 30 into the glowing slot between 20 and 40!",
                    explanation = "Pointer rewire complete! 20 → 30 → 40. No elements had to be shifted! 🎉",
                    xpReward = 25,
                    linkedNodes = listOf(
                        DSALinkedNode(id = "n1", value = "10", nextId = "n2", isHead = true),
                        DSALinkedNode(id = "n2", value = "20", nextId = "n4"),
                        DSALinkedNode(id = "n4", value = "40", isNull = true),
                    ),
                    insertNodeValue = "30",
                ),
                DSALevelSpec(
                    id = "c2_boss", chapterId = 2, levelNumber = 5,
                    title = "Chapter Boss: The Broken Chain",
                    subtitle = "Repair the dangling pointer before the magic collapses!",
                    interactionType = DSALevelInteractionType.BROKEN_CHAIN_BOSS,
                    storyExplanation = "Dark magic broke the link between Node 20 and Node 40! Restore the lost link or direct connection to save the chain!",
                    promptQuestion = "Select the correct target node to reconnect the broken pointer!",
                    hint = "The chain must flow from 10 → 20 → [?] → 40...",
                    explanation = "Chain restored! The magical current flows again. You earned the Chain Master trophy! 🏆",
                    xpReward = 100,
                    mcOptions = listOf("Connect 20 → 30, and 30 → 40","Connect 20 → NULL","Delete Head 10","Swap 10 and 40"),
                    mcCorrectIndex = 0,
                ),
            ),
        ),

        // ──────────────────────────────────────────────────────────
        // CHAPTER 3 — STACK: "The Tower of Plates"
        // ──────────────────────────────────────────────────────────
        DSAChapterModel(
            id = 3, number = 3,
            title = "Stack",
            subtitle = "The Tower of Plates",
            theme = DSAEnvironmentTheme.TOWER_PLATES,
            story = "Welcome to the bustling Chef's Tower! In this kitchen, clean plates are stacked on top of one another. The last plate placed on the stack is always the first plate taken! That is LIFO: Last In, First Out!",
            badgeName = "Stack Master",
            primaryColor = Color(red = 255f/255, green = 149f/255, blue = 0f/255),
            darkColor    = Color(red = 190f/255, green = 100f/255, blue = 0f/255),
            accentColor  = Color(red = 255f/255, green = 75f/255,  blue = 85f/255),
            characterEmoji = "👨‍🍳",
            completionBonusXP = 100,
            levels = listOf(
                DSALevelSpec(
                    id = "c3_l1", chapterId = 3, levelNumber = 1,
                    title = "Stack Basics",
                    subtitle = "LIFO: Last In, First Out",
                    interactionType = DSALevelInteractionType.MULTIPLE_CHOICE,
                    storyExplanation = "When plates are stacked: Plate 1 is at the bottom, Plate 2 in middle, and Plate 3 is right at the top. You cannot pull from the bottom without causing a crash!",
                    promptQuestion = "Which plate will be removed first?",
                    hint = "Look at the very top plate...",
                    explanation = "Plate 3! It was placed on top last, so it comes off first. That is LIFO! 🍽️",
                    xpReward = 10,
                    mcOptions = listOf("Plate 1","Plate 2","Plate 3","Any random plate"),
                    mcCorrectIndex = 2,
                    stackInitial = listOf("Plate 1","Plate 2","Plate 3"),
                ),
                DSALevelSpec(
                    id = "c3_l2", chapterId = 3, levelNumber = 2,
                    title = "Push",
                    subtitle = "Add elements onto the top",
                    interactionType = DSALevelInteractionType.PUSH_STACK,
                    storyExplanation = "Adding an item to a stack is called PUSH. Every pushed element drops straight onto the top of the tower in O(1) time!",
                    promptQuestion = "Push the numbers 10, 20, and 30 onto the stack.",
                    hint = "Tap 10 first, then 20, then 30...",
                    explanation = "All 3 plates pushed! 30 sits proudly at the top of the tower. 🥞",
                    xpReward = 15,
                    targetPushItems = listOf("10","20","30"),
                ),
                DSALevelSpec(
                    id = "c3_l3", chapterId = 3, levelNumber = 3,
                    title = "Pop",
                    subtitle = "Remove the top element",
                    interactionType = DSALevelInteractionType.POP_STACK,
                    storyExplanation = "Removing an item from a stack is called POP. It always takes the topmost item off and returns it.",
                    promptQuestion = "Stack has 30 on top of 20 on top of 10. What happens when POP is performed?",
                    hint = "Pop always removes the current top plate...",
                    explanation = "30 is popped! 20 is now the new top plate. 🎈",
                    xpReward = 15,
                    mcOptions = listOf(
                        "30 is removed from the top",
                        "10 is removed from the bottom",
                        "20 is removed from the middle",
                        "All plates are cleared",
                    ),
                    mcCorrectIndex = 0,
                    stackInitial = listOf("10","20","30"),
                ),
                DSALevelSpec(
                    id = "c3_l4", chapterId = 3, levelNumber = 4,
                    title = "Stack Challenge",
                    subtitle = "Trace operations step-by-step",
                    interactionType = DSALevelInteractionType.STACK_SEQUENCE_CHALLENGE,
                    storyExplanation = "Let's trace: PUSH 10 → [10], PUSH 20 → [10, 20], PUSH 30 → [10, 20, 30], POP → [10, 20], PUSH 40 → [10, 20, 40].",
                    promptQuestion = "What is now at the top of the stack?",
                    hint = "30 was removed by the POP, and 40 was pushed on top of 20!",
                    explanation = "40 is at the top! You accurately traced the stack's state in memory. 🎯",
                    xpReward = 20,
                    mcOptions = listOf("10","20","30","40"),
                    mcCorrectIndex = 3,
                    stackSequence = listOf("PUSH 10","PUSH 20","PUSH 30","POP","PUSH 40"),
                    stackSequenceAnswer = "40",
                ),
                DSALevelSpec(
                    id = "c3_boss", chapterId = 3, levelNumber = 5,
                    title = "Chapter Boss: Escape the Tower",
                    subtitle = "Execute the correct PUSH and POP combo to unlock the exit door!",
                    interactionType = DSALevelInteractionType.ESCAPE_TOWER_BOSS,
                    storyExplanation = "The kitchen door lock requires matching the exact target stack: [Gold, Diamond]. Perform operations carefully to unlock it!",
                    promptQuestion = "Select the correct sequence to leave [Gold, Diamond] in the stack:",
                    hint = "Push Iron, Push Gold, Pop Iron... wait, push in correct order!",
                    explanation = "Exit unlocked! You conquered the Stack and earned the Stack Master trophy! 🏆",
                    xpReward = 100,
                    mcOptions = listOf("PUSH Gold → PUSH Diamond","PUSH Diamond → PUSH Gold","POP → POP → PUSH Gold","PUSH Diamond → POP"),
                    mcCorrectIndex = 0,
                ),
            ),
        ),

        // ──────────────────────────────────────────────────────────
        // CHAPTER 4 — QUEUE: "The Waiting Line"
        // ──────────────────────────────────────────────────────────
        DSAChapterModel(
            id = 4, number = 4,
            title = "Queue",
            subtitle = "The Waiting Line",
            theme = DSAEnvironmentTheme.TICKET_STATION,
            story = "Welcome to the Hyperloop Ticket Station! Passengers stand in an orderly line. The first person to step into line is the first person to get their ticket! That is FIFO: First In, First Out!",
            badgeName = "Queue Master",
            primaryColor = Color(red = 88f/255,  green = 204f/255, blue = 2f/255),
            darkColor    = Color(red = 70f/255,  green = 163f/255, blue = 2f/255),
            accentColor  = Color(red = 255f/255, green = 195f/255, blue = 0f/255),
            characterEmoji = "🎟️",
            completionBonusXP = 100,
            levels = listOf(
                DSALevelSpec(
                    id = "c4_l1", chapterId = 4, levelNumber = 1,
                    title = "Understand FIFO",
                    subtitle = "First In, First Out",
                    interactionType = DSALevelInteractionType.FIFO_QUEUE_ORDER,
                    storyExplanation = "Queue line: Person A → Person B → Person C → Person D. In a fair queue, service starts at the front (head)!",
                    promptQuestion = "Who gets served first?",
                    hint = "Who arrived at the ticket window first?",
                    explanation = "Person A! They were first in line, so they get served first. That is FIFO! 🎟️",
                    xpReward = 10,
                    mcOptions = listOf("Person A","Person B","Person C","Person D"),
                    mcCorrectIndex = 0,
                    queueInitial = listOf("Person A","Person B","Person C","Person D"),
                ),
                DSALevelSpec(
                    id = "c4_l2", chapterId = 4, levelNumber = 2,
                    title = "Enqueue",
                    subtitle = "Join the back of the line",
                    interactionType = DSALevelInteractionType.ENQUEUE_LINE,
                    storyExplanation = "Adding an item to a queue is called ENQUEUE. New arrivals always join the TAIL (rear) of the line.",
                    promptQuestion = "Add passengers A, B, and C to the queue in order.",
                    hint = "Tap A, then B, then C to join the line...",
                    explanation = "Line formed! A is at the front, B is middle, C is at the tail. 🚶‍♂️🚶‍♀️",
                    xpReward = 15,
                    queueEnqueueItems = listOf("A","B","C"),
                ),
                DSALevelSpec(
                    id = "c4_l3", chapterId = 4, levelNumber = 3,
                    title = "Dequeue",
                    subtitle = "Service at the front",
                    interactionType = DSALevelInteractionType.DEQUEUE_LINE,
                    storyExplanation = "Removing an item from a queue is called DEQUEUE. It always serves the passenger at the HEAD (front).",
                    promptQuestion = "Queue is: A → B → C. If we dequeue once, who leaves?",
                    hint = "Dequeue removes from the FRONT, not the back!",
                    explanation = "Person A boards the train! B now moves to the front of the queue. 🚄",
                    xpReward = 15,
                    mcOptions = listOf("A","B","C","None"),
                    mcCorrectIndex = 0,
                    queueInitial = listOf("A","B","C"),
                ),
                DSALevelSpec(
                    id = "c4_l4", chapterId = 4, levelNumber = 4,
                    title = "Queue Puzzle",
                    subtitle = "Sequence evaluation",
                    interactionType = DSALevelInteractionType.QUEUE_SEQUENCE_CHALLENGE,
                    storyExplanation = "Operations: ENQUEUE A, ENQUEUE B, ENQUEUE C, DEQUEUE (A leaves), ENQUEUE D (joins back).",
                    promptQuestion = "What is the final state of the queue?",
                    hint = "A left, so B is in front, followed by C, then newly arrived D!",
                    explanation = "B → C → D is correct! You mastered FIFO queue state tracking. 🏅",
                    xpReward = 20,
                    mcOptions = listOf("B → C → D","A → B → C","D → C → B","C → D → B"),
                    mcCorrectIndex = 0,
                    queueSequence = listOf("ENQUEUE A","ENQUEUE B","ENQUEUE C","DEQUEUE","ENQUEUE D"),
                    queueSequenceAnswer = listOf("B","C","D"),
                ),
                DSALevelSpec(
                    id = "c4_boss", chapterId = 4, levelNumber = 5,
                    title = "Chapter Boss: Ticket Rush",
                    subtitle = "Rapid ticket station rush hour!",
                    interactionType = DSALevelInteractionType.TICKET_RUSH_BOSS,
                    storyExplanation = "Rush hour at the station! Passengers are arriving rapidly. Decide who gets served next in strict FIFO order before the timer expires!",
                    promptQuestion = "Rapid FIFO Dispatch Challenge!",
                    hint = "Always pick the passenger at the front of the line!",
                    explanation = "Rush hour cleared with zero violations! You earned the Queue Master trophy! 🏆",
                    xpReward = 100, timeLimitSeconds = 45, bossTotalQuestions = 5,
                    bossRapidQuestions = listOf(
                        DSARapidQuestion(prompt = "Queue has [X, Y, Z]. Who is served by DEQUEUE?",
                            options = listOf("X","Y","Z","None"), correctIndex = 0,
                            explanation = "X was enqueued first, so X is dequeued first."),
                        DSARapidQuestion(prompt = "Where does ENQUEUE add a new element?",
                            options = listOf("At the rear (tail)","At the front (head)","In the middle","At index 0"),
                            correctIndex = 0, explanation = "Enqueue always inserts at the tail/back."),
                        DSARapidQuestion(prompt = "What principle governs a Queue?",
                            options = listOf("FIFO (First In, First Out)","LIFO (Last In, First Out)","Random Access","FILO"),
                            correctIndex = 0, explanation = "Queue is First In, First Out."),
                        DSARapidQuestion(prompt = "If a printer processes 3 jobs, what data structure should it use?",
                            options = listOf("Queue","Stack","Binary Tree","2D Array"),
                            correctIndex = 0, explanation = "Print jobs are processed in the order received (FIFO)."),
                        DSARapidQuestion(prompt = "What is time complexity of Enqueue and Dequeue in an optimal linked queue?",
                            options = listOf("O(1) for both","O(n) for both","O(log n)","O(n²)"),
                            correctIndex = 0, explanation = "With head and tail pointers, both operations run in O(1) time."),
                    ),
                ),
            ),
        ),

        // ──────────────────────────────────────────────────────────
        // CHAPTER 5 — TREE: "The Data Kingdom"
        // ──────────────────────────────────────────────────────────
        DSAChapterModel(
            id = 5, number = 5,
            title = "Tree",
            subtitle = "The Data Kingdom",
            theme = DSAEnvironmentTheme.DATA_KINGDOM,
            story = "Welcome to the enchanted Data Kingdom! In this hierarchy, power stems from the royal ROOT monarch at the very top, branching downward to parents, children, and finally the leaf nodes at the edge!",
            badgeName = "Tree Master",
            primaryColor = Color(red = 50f/255,  green = 215f/255, blue = 75f/255),
            darkColor    = Color(red = 20f/255,  green = 140f/255, blue = 50f/255),
            accentColor  = Color(red = 255f/255, green = 214f/255, blue = 10f/255),
            characterEmoji = "👑",
            completionBonusXP = 150,
            levels = listOf(
                DSALevelSpec(
                    id = "c5_l1", chapterId = 5, levelNumber = 1,
                    title = "Find the Root",
                    subtitle = "The monarch of the hierarchy",
                    interactionType = DSALevelInteractionType.FIND_TREE_ROOT,
                    storyExplanation = "Every tree starts with exactly ONE node that has NO parent. This apex node is the ROOT! Tap the crown at the top.",
                    promptQuestion = "Which node is the root? Tap the top node.",
                    hint = "Look at the very highest node with the crown...",
                    explanation = "ROOT selected! All other nodes in the kingdom branch out from the Root. 🌳👑",
                    xpReward = 10,
                    treeNodes = listOf(
                        DSATreeNodeItem(id = "root", label = "ROOT", level = 0, parentId = null, isRoot = true, xPos = 0f, yPos = 0f),
                        DSATreeNodeItem(id = "a",    label = "A",    level = 1, parentId = "root", xPos = -60f, yPos = 70f),
                        DSATreeNodeItem(id = "b",    label = "B",    level = 1, parentId = "root", xPos = 60f,  yPos = 70f),
                    ),
                ),
                DSALevelSpec(
                    id = "c5_l2", chapterId = 5, levelNumber = 2,
                    title = "Parent and Child",
                    subtitle = "Generational relationships",
                    interactionType = DSALevelInteractionType.TREE_PARENT_CHILD,
                    storyExplanation = "In the tree: A connects down to B and C. A is the PARENT of B and C. B and C are the CHILDREN of A.",
                    promptQuestion = "Who is the parent of B?",
                    hint = "Look at the node directly above B connected by a branch...",
                    explanation = "A is the parent of B! B is a child node of A. 🌿",
                    xpReward = 15,
                    mcOptions = listOf("A","C","ROOT","No parent"),
                    mcCorrectIndex = 0,
                    treeNodes = listOf(
                        DSATreeNodeItem(id = "a", label = "A", level = 0, parentId = null, isRoot = true, xPos = 0f,   yPos = 0f),
                        DSATreeNodeItem(id = "b", label = "B", level = 1, parentId = "a", isLeaf = true, xPos = -50f, yPos = 65f),
                        DSATreeNodeItem(id = "c", label = "C", level = 1, parentId = "a", isLeaf = true, xPos = 50f,  yPos = 65f),
                    ),
                ),
                DSALevelSpec(
                    id = "c5_l3", chapterId = 5, levelNumber = 3,
                    title = "Find the Leaves",
                    subtitle = "Nodes with no children",
                    interactionType = DSALevelInteractionType.FIND_TREE_LEAVES,
                    storyExplanation = "A LEAF node is a node that has NO children (degree = 0). They form the outer canopy of the tree!",
                    promptQuestion = "Tap all leaf nodes (C, D, E, F) to make them glow!",
                    hint = "Leaves are the bottom-most nodes with no branches extending downward.",
                    explanation = "All leaf nodes illuminated! Leaves have zero descendants. ✨🍃",
                    xpReward = 20,
                    treeNodes = listOf(
                        DSATreeNodeItem(id = "root", label = "ROOT", level = 0, parentId = null, isRoot = true, xPos = 0f,    yPos = 0f),
                        DSATreeNodeItem(id = "a",    label = "A",    level = 1, parentId = "root", xPos = -70f,  yPos = 60f),
                        DSATreeNodeItem(id = "b",    label = "B",    level = 1, parentId = "root", xPos = 70f,   yPos = 60f),
                        DSATreeNodeItem(id = "c",    label = "C",    level = 2, parentId = "a", isLeaf = true, xPos = -105f, yPos = 125f),
                        DSATreeNodeItem(id = "d",    label = "D",    level = 2, parentId = "a", isLeaf = true, xPos = -35f,  yPos = 125f),
                        DSATreeNodeItem(id = "e",    label = "E",    level = 2, parentId = "b", isLeaf = true, xPos = 35f,   yPos = 125f),
                        DSATreeNodeItem(id = "f",    label = "F",    level = 2, parentId = "b", isLeaf = true, xPos = 105f,  yPos = 125f),
                    ),
                    correctLeafIds = setOf("c","d","e","f"),
                ),
                DSALevelSpec(
                    id = "c5_l4", chapterId = 5, levelNumber = 4,
                    title = "Tree Traversal",
                    subtitle = "Preorder: Root → Left → Right",
                    interactionType = DSALevelInteractionType.TREE_TRAVERSAL,
                    storyExplanation = "Visiting every node is called Traversal. In Preorder Traversal, we visit: 1. Current Root, 2. Left Subtree, 3. Right Subtree.",
                    promptQuestion = "Which order represents Preorder traversal of tree with Root A, left B, right C?",
                    hint = "Root first (A), then left child (B), then right child (C)!",
                    explanation = "A → B → C is Preorder traversal! Watch the energy pulse along the branches! ⚡",
                    xpReward = 25,
                    mcOptions = listOf("A → B → C","B → A → C","B → C → A","C → B → A"),
                    mcCorrectIndex = 0,
                    traversalSolution = listOf("A","B","C"),
                ),
                DSALevelSpec(
                    id = "c5_boss", chapterId = 5, levelNumber = 5,
                    title = "Chapter Boss: Save the Data Kingdom",
                    subtitle = "Disorganized tree! Complete all 5 trials to restore the Kingdom!",
                    interactionType = DSALevelInteractionType.SAVE_KINGDOM_BOSS,
                    storyExplanation = "The Royal Tree has fallen into disorder! The Grand Monarch requires you to pass 5 diagnostic trials: identify root, parents, children, leaves, and preorder sequence!",
                    promptQuestion = "Pass all 5 Royal Tree trials to restore harmony!",
                    hint = "Recall: Root is top, leaves have no children, preorder visits Root first!",
                    explanation = "THE DATA KINGDOM IS SAVED! Royal trumpets sound and fireworks burst across the sky! You are awarded Tree Master and +150 XP! 👑🎆",
                    xpReward = 150, timeLimitSeconds = 60, bossTotalQuestions = 5,
                    bossRapidQuestions = listOf(
                        DSARapidQuestion(prompt = "Trial 1: What is the only node without any parent?",
                            options = listOf("The Root","The Leaf","The Edge","The Branch"), correctIndex = 0,
                            explanation = "The root is the topmost ancestor with zero parents."),
                        DSARapidQuestion(prompt = "Trial 2: In a binary tree, how many children can a node have at most?",
                            options = listOf("2","1","4","Unlimited"), correctIndex = 0,
                            explanation = "Binary means at most 2 children (left and right)."),
                        DSARapidQuestion(prompt = "Trial 3: What do we call a node with degree 0 (no children)?",
                            options = listOf("Leaf node","Root node","Internal node","Sibling"), correctIndex = 0,
                            explanation = "Nodes with no children are leaves."),
                        DSARapidQuestion(prompt = "Trial 4: What is the sequence for Inorder traversal?",
                            options = listOf("Left → Root → Right","Root → Left → Right","Left → Right → Root","Right → Left → Root"),
                            correctIndex = 0, explanation = "Inorder visits Left child, then Root, then Right child."),
                        DSARapidQuestion(prompt = "Trial 5: What is the depth of the root node?",
                            options = listOf("0","1","10","Infinite"), correctIndex = 0,
                            explanation = "Root is at depth 0."),
                    ),
                ),
            ),
        ),
    )
}
