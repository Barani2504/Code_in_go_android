package com.simats.codeingo.data.model

import androidx.compose.ui.graphics.Color
import com.simats.codeingo.ui.theme.DsaBlue
import com.simats.codeingo.ui.theme.DsaBlueDark
import com.simats.codeingo.ui.theme.DsaGreen
import com.simats.codeingo.ui.theme.DsaGreenDark
import com.simats.codeingo.ui.theme.DsaOrange
import com.simats.codeingo.ui.theme.DsaOrangeDark
import com.simats.codeingo.ui.theme.DsaPurple
import com.simats.codeingo.ui.theme.DsaPurpleDark
import com.simats.codeingo.ui.theme.DsaRed
import com.simats.codeingo.ui.theme.DsaRedDark
import com.simats.codeingo.ui.theme.DsaTeal
import java.util.UUID

// MARK: - DSA Level
enum class DSALevel(val value: Int, val title: String, val emoji: String) {
    PROGRAMMING_BASICS(1, "Programming Basics", "🟢"),
    LINEAR_STRUCTURES(2, "Linear Structures", "🔵"),
    NON_LINEAR_STRUCTURES(3, "Non-Linear Structures", "🟣"),
    ALGORITHMS(4, "Algorithms", "🟠"),
    ADVANCED_DSA(6, "Advanced DSA", "🔴");

    val themeColor: Color
        get() = when (this) {
            PROGRAMMING_BASICS -> DsaGreen
            LINEAR_STRUCTURES -> DsaBlue
            NON_LINEAR_STRUCTURES -> DsaPurple
            ALGORITHMS -> DsaOrange
            ADVANCED_DSA -> DsaRed
        }

    val themeDarkColor: Color
        get() = when (this) {
            PROGRAMMING_BASICS -> DsaGreenDark
            LINEAR_STRUCTURES -> DsaBlueDark
            NON_LINEAR_STRUCTURES -> DsaPurpleDark
            ALGORITHMS -> DsaOrangeDark
            ADVANCED_DSA -> DsaRedDark
        }
}

// MARK: - DSA Topic
enum class DSATopic(val title: String) {
    // Level 1
    VARIABLES("Variables"),
    ARRAYS("Arrays"),
    STRINGS("Strings"),
    // Level 2
    LINKED_LIST("Linked List"),
    STACK("Stack"),
    QUEUE("Queue"),
    // Level 3
    TREES("Trees"),
    BST("Binary Search Tree"),
    HEAP("Heap"),
    GRAPH("Graph"),
    // Level 4
    SEARCHING("Searching"),
    SORTING("Sorting"),
    RECURSION("Recursion"),
    DYNAMIC_PROGRAMMING("Dynamic Programming"),
    // Level 5
    GREEDY("Greedy"),
    GRAPH_ALGORITHMS("Graph Algorithms"),
    BACKTRACKING("Backtracking"),
    ADVANCED_DP("Advanced DP");

    val level: DSALevel
        get() = when (this) {
            VARIABLES, ARRAYS, STRINGS -> DSALevel.PROGRAMMING_BASICS
            LINKED_LIST, STACK, QUEUE -> DSALevel.LINEAR_STRUCTURES
            TREES, BST, HEAP, GRAPH -> DSALevel.NON_LINEAR_STRUCTURES
            SEARCHING, SORTING, RECURSION, DYNAMIC_PROGRAMMING -> DSALevel.ALGORITHMS
            GREEDY, GRAPH_ALGORITHMS, BACKTRACKING, ADVANCED_DP -> DSALevel.ADVANCED_DSA
        }

    val description: String
        get() = when (this) {
            VARIABLES -> "Building blocks of programming"
            ARRAYS -> "Sequential data in memory"
            STRINGS -> "Text manipulation & patterns"
            LINKED_LIST -> "Nodes connected by pointers"
            STACK -> "LIFO - Last In, First Out"
            QUEUE -> "FIFO - First In, First Out"
            TREES -> "Hierarchical data structure"
            BST -> "Sorted binary tree"
            HEAP -> "Priority-based tree"
            GRAPH -> "Vertices and edges"
            SEARCHING -> "Find elements efficiently"
            SORTING -> "Arrange data in order"
            RECURSION -> "Functions calling themselves"
            DYNAMIC_PROGRAMMING -> "Optimal substructure problems"
            GREEDY -> "Locally optimal choices"
            GRAPH_ALGORITHMS -> "Traversal & shortest paths"
            BACKTRACKING -> "Explore all possibilities"
            ADVANCED_DP -> "Complex DP patterns"
        }

    val xpReward: Int
        get() = when (this) {
            VARIABLES, ARRAYS, STRINGS -> 10
            LINKED_LIST, STACK, QUEUE -> 15
            TREES, BST, HEAP, GRAPH -> 20
            SEARCHING, SORTING, RECURSION, DYNAMIC_PROGRAMMING -> 25
            GREEDY, GRAPH_ALGORITHMS, BACKTRACKING, ADVANCED_DP -> 30
        }
}

// MARK: - DSA Question Type
enum class DSAQuestionType(val title: String) {
    MULTIPLE_CHOICE("Multiple Choice"),
    PREDICT_OUTPUT("Predict the Output"),
    CONCEPT_CHECK("Concept Check"),
    VISUAL_MATCH("Visual Match"),
    CODE_COMPLETION("Complete the Code")
}

// MARK: - DSA Quiz Question
data class DSAQuizQuestion(
    val id: String = UUID.randomUUID().toString(),
    val type: DSAQuestionType,
    val topic: DSATopic,
    val prompt: String,
    val visualContext: String? = null,
    val codeSnippet: String? = null,
    val options: List<String> = emptyList(),
    val correctIndex: Int,
    val explanation: String,
    val xpReward: Int = 15,
    val hint: String? = null
)

// MARK: - DSA Achievement
data class DSAAchievement(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val badge: String,
    val requirement: String,
    var isUnlocked: Boolean = false
)

// MARK: - XP Activity
enum class XPActivity(val title: String, val xpReward: Int, val iconName: String) {
    COMPLETE_LESSON("Complete Lesson", 10, "book.fill"),
    QUIZ("Quiz", 15, "questionmark.circle.fill"),
    DAILY_CHALLENGE("Daily Challenge", 30, "calendar.badge.clock"),
    CODING_CHALLENGE("Coding Challenge", 50, "code"),
    PERFECT_LESSON("Perfect Lesson", 10, "star.fill"),
    SEVEN_DAY_STREAK("7-Day Streak", 100, "flame.fill")
}

// MARK: - DSA Visualizer Type
enum class DSAVisualizerType(val title: String, val iconName: String) {
    ARRAY("Array", "grid"),
    STACK("Stack", "layers"),
    QUEUE("Queue", "queue"),
    LINKED_LIST("Linked List", "link"),
    BINARY_TREE("Binary Tree", "account_tree"),
    AVL_TREE("AVL Tree", "alt_route"),
    TRIE("Trie", "spellcheck"),
    GRAPH("Graph", "hub"),
    SORTING("Bubble Sort", "swap_vert"),
    STEP_RECORDER("Step Player", "play_circle");

    val color: Color
        get() = when (this) {
            ARRAY -> DsaGreen
            STACK -> DsaBlue
            QUEUE -> DsaPurple
            LINKED_LIST -> DsaOrange
            BINARY_TREE -> Color(0xFF00CD9C)
            AVL_TREE -> DsaTeal
            TRIE -> Color.Cyan
            GRAPH -> Color(0xFFA05AFF)
            SORTING -> DsaRed
            STEP_RECORDER -> Color(0xFFFF9600)
        }

    val category: String
        get() = when (this) {
            ARRAY, STACK, QUEUE, LINKED_LIST -> "Linear Structure"
            BINARY_TREE, AVL_TREE, TRIE -> "Tree Structure"
            GRAPH -> "Graph Network"
            SORTING -> "Sorting Algorithm"
            STEP_RECORDER -> "Execution Tracer"
        }

    val filterCategory: String
        get() = when (this) {
            ARRAY, STACK, QUEUE, LINKED_LIST -> "Linear"
            BINARY_TREE, AVL_TREE, TRIE, GRAPH -> "Trees & Graphs"
            SORTING, STEP_RECORDER -> "Algorithms"
        }

    val tagline: String
        get() = when (this) {
            STACK -> "LIFO · Last In, First Out"
            ARRAY -> "Contiguous Sequential Memory"
            QUEUE -> "FIFO · First In, First Out"
            LINKED_LIST -> "Chained Pointer-Linked Nodes"
            BINARY_TREE -> "Hierarchical Parent & Child Nodes"
            AVL_TREE -> "Self-Balancing BST with Rotations"
            TRIE -> "Prefix Tree for Fast Text Retrieval"
            GRAPH -> "Connected Vertices & Edges"
            SORTING -> "Bubble Sort Neighbor Swaps"
            STEP_RECORDER -> "Live Memory & Code Tracing"
        }

    val shortDescription: String
        get() = when (this) {
            STACK -> "Data structure where elements are added and removed from the top. Essential for function calls, browser history, and undo operations."
            ARRAY -> "Consecutive memory slots allowing instant O(1) random access by index, but requiring O(n) shifts for middle insertions."
            QUEUE -> "FIFO structure where new items enter at the rear and exit from the front in strict order of arrival. Used in CPU scheduling and print queues."
            LINKED_LIST -> "Nodes connected dynamically via memory pointers. Enables fast O(1) insertions at the head without resizing arrays."
            BINARY_TREE -> "Hierarchical structure where each node has at most two children. The core of Binary Search Trees (BST) and ordered searching."
            AVL_TREE -> "Self-balancing binary search tree maintaining O(log n) guaranteed search time using single and double height rotations."
            TRIE -> "Specialized search tree for storing words and prefixes. Powers search engine autocomplete, spell-checking, and IP routing."
            GRAPH -> "Networks of vertices connected by edges. Models maps, social connections, web links, and network topologies."
            SORTING -> "Foundational sorting technique comparing adjacent items and bubbling largest elements to the end step by step."
            STEP_RECORDER -> "Interactive code execution engine displaying step-by-step memory allocation, pointer shifts, and algorithmic complexity."
        }

    val difficulty: String
        get() = when (this) {
            ARRAY, STACK, QUEUE, LINKED_LIST, SORTING -> "Beginner"
            BINARY_TREE, TRIE, STEP_RECORDER -> "Intermediate"
            AVL_TREE, GRAPH -> "Advanced"
        }

    val difficultyColor: Color
        get() = when (difficulty) {
            "Beginner" -> DsaGreen
            "Intermediate" -> DsaOrange
            else -> DsaRed
        }

    val timeComplexity: String
        get() = when (this) {
            STACK -> "O(1)"
            ARRAY -> "O(1) Access"
            QUEUE -> "O(1)"
            LINKED_LIST -> "O(1) Head"
            BINARY_TREE -> "O(log n)"
            AVL_TREE -> "O(log n)"
            TRIE -> "O(L)"
            GRAPH -> "O(V + E)"
            SORTING -> "O(n²)"
            STEP_RECORDER -> "O(n)"
        }

    val spaceComplexity: String
        get() = when (this) {
            STACK, ARRAY, QUEUE, LINKED_LIST -> "O(n)"
            BINARY_TREE, AVL_TREE -> "O(n)"
            TRIE -> "O(N · L)"
            GRAPH -> "O(V + E)"
            SORTING -> "O(1)"
            STEP_RECORDER -> "O(n)"
        }

    val keyOperations: List<String>
        get() = when (this) {
            STACK -> listOf("push()", "pop()", "peek()", "isEmpty()")
            ARRAY -> listOf("access[i]", "append()", "insert()", "delete()")
            QUEUE -> listOf("enqueue()", "dequeue()", "front()", "isEmpty()")
            LINKED_LIST -> listOf("insertHead()", "insertTail()", "delete()", "traverse()")
            BINARY_TREE -> listOf("insert()", "inorder()", "preorder()", "postorder()")
            AVL_TREE -> listOf("rotateLeft()", "rotateRight()", "rebalance()", "height()")
            TRIE -> listOf("insert()", "search()", "startsWith()", "delete()")
            GRAPH -> listOf("BFS", "DFS", "addVertex()", "addEdge()")
            SORTING -> listOf("compare()", "swap()", "bubbleStep()", "isSorted()")
            STEP_RECORDER -> listOf("stepForward()", "stepBack()", "play()", "memoryTrace()")
        }

    val realWorldApplications: List<String>
        get() = when (this) {
            STACK -> listOf(
                "Browser Back / Forward history navigation",
                "Undo & Redo operations in code editors",
                "Function call frames & recursion stack in CPUs",
                "Compiler syntax parsing (matching parentheses & brackets)"
            )
            ARRAY -> listOf(
                "Fast index-based lookups and math buffers",
                "Image & video frame pixel buffers (RGB grids)",
                "Dynamic arrays (ArrayList, Vector, Swift Array)",
                "CPU cache line prefetching for max performance"
            )
            QUEUE -> listOf(
                "Operating system CPU process scheduling (Round-Robin)",
                "Printer spooling and asynchronous background workers",
                "Breadth-First Search (BFS) shortest path algorithms",
                "Message brokers and stream pipelines (Kafka, RabbitMQ)"
            )
            LINKED_LIST -> listOf(
                "Music playlist Next / Previous track controls",
                "LRU (Least Recently Used) cache eviction structures",
                "Dynamic memory allocation free lists in OS kernels",
                "Undo history without continuous large memory reallocation"
            )
            BINARY_TREE -> listOf(
                "Hierarchical file systems (folders, subfolders, files)",
                "DOM (Document Object Model) trees in web browsers",
                "Binary Search Trees (BST) for fast ordered retrieval",
                "Abstract Syntax Trees (AST) in programming language compilers"
            )
            AVL_TREE -> listOf(
                "Database indexing requiring guaranteed fast lookups",
                "Real-time telecom & router table lookups",
                "High-performance memory managers avoiding tree degradation",
                "Ordered sets and maps with strict O(log n) guarantees"
            )
            TRIE -> listOf(
                "Google search bar autocomplete suggestions",
                "Mobile phone keyboard spell checkers and predictive text",
                "IP network routing longest prefix matching (CIDR)",
                "Word game anagram and vocabulary solvers"
            )
            GRAPH -> listOf(
                "Google Maps & GPS navigation turn-by-turn routing",
                "Social media friendship and connection networks",
                "Web page rank crawling and link connectivity",
                "Package dependency graphs (npm, Swift Package Manager)"
            )
            SORTING -> listOf(
                "Preparing data for fast binary search O(log n)",
                "Game leaderboards and high-score ranking tables",
                "Database ORDER BY and sorting query operations",
                "E-commerce filter-by-price or rating feeds"
            )
            STEP_RECORDER -> listOf(
                "Interactive coding interview algorithm walkthroughs",
                "Visualizing pointer manipulations and shift costs",
                "Debugging complex loop conditions and state changes",
                "Deep conceptual mastery of time vs space tradeoffs"
            )
        }

    val nextType: DSAVisualizerType
        get() {
            val all = entries
            val idx = all.indexOf(this)
            return if (idx >= 0) all[(idx + 1) % all.size] else STACK
        }
}
