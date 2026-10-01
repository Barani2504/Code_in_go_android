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
}
