package com.simats.duolingo.domain.model

import java.util.UUID

/* ================================================================== */
/*  COURSE & CURRICULUM CORE DOMAIN MODELS                            */
/* ================================================================== */

enum class Difficulty(val label: String, val colorHex: Long) {
    GREEN("Foundations", 0xFF58CC02),
    YELLOW("Intermediate", 0xFFFFC800),
    BLUE("Advanced", 0xFF1CB0F6),
    RED("Mastery", 0xFFFF4B4B)
}

enum class LessonType {
    NORMAL,
    CHALLENGE,
    CHECKPOINT,
    CHEST,
    BOSS
}

enum class NodeState {
    LOCKED,
    AVAILABLE,
    IN_PROGRESS,
    COMPLETED,
    LEGENDARY
}

data class PhoenixTrigger(
    val triggerId: String,
    val targetStage: Int,
    val description: String
)

data class BossBattle(
    val id: String,
    val title: String,
    val subtitle: String,
    val bossName: String,
    val maxHp: Int = 100,
    val timeLimitSeconds: Int = 90,
    val rewardXp: Int = 50,
    val rewardGems: Int = 20,
    val phases: Int = 3
)

data class Course(
    val id: String,
    val title: String,
    val units: List<DsaUnit>
)

data class DsaUnit(
    val id: String,
    val index: Int,
    val title: String,
    val subtitle: String,
    val difficulty: Difficulty,
    val worldTheme: String,
    val themeColorHex: Long,
    val themeDarkColorHex: Long,
    val lessons: List<DsaLesson>,
    val boss: BossBattle,
    val phoenixEvolutionsAwarded: List<PhoenixTrigger>
)

data class DsaLesson(
    val id: String,
    val unitId: String,
    val index: Int,
    val title: String,
    val subtitle: String,
    val type: LessonType,
    val exercises: List<Exercise>,
    val xpReward: Int = 15,
    val crowns: Int = 0,
    val isCompleted: Boolean = false,
    val xOffsetDp: Float = 0f
)

/* ================================================================== */
/*  EXERCISE SEALED HIERARCHY (15 EXERCISE SUBTYPES)                  */
/* ================================================================== */

sealed interface Exercise {
    val id: String
    val prompt: String
    val hint: String?
    val explanation: String
    val conceptId: String
    val xpValue: Int
        get() = 10
}

data class MultipleChoiceExercise(
    override val id: String = UUID.randomUUID().toString(),
    override val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    override val hint: String? = null,
    override val explanation: String,
    override val conceptId: String = "general",
    val codeSnippet: String? = null
) : Exercise

data class MultiSelectExercise(
    override val id: String = UUID.randomUUID().toString(),
    override val prompt: String,
    val options: List<String>,
    val correctIndices: Set<Int>,
    override val hint: String? = null,
    override val explanation: String,
    override val conceptId: String = "general"
) : Exercise

data class TrueFalseSwipeExercise(
    override val id: String = UUID.randomUUID().toString(),
    override val prompt: String,
    val statement: String,
    val isTrue: Boolean,
    override val hint: String? = null,
    override val explanation: String,
    override val conceptId: String = "general"
) : Exercise

data class MatchPairsExercise(
    override val id: String = UUID.randomUUID().toString(),
    override val prompt: String,
    val pairs: List<Pair<String, String>>, // term to definition
    override val hint: String? = null,
    override val explanation: String,
    override val conceptId: String = "general"
) : Exercise

data class OrderStepsExercise(
    override val id: String = UUID.randomUUID().toString(),
    override val prompt: String,
    val steps: List<String>, // Scrambled order
    val correctOrder: List<Int>, // Indices matching the correct sequence
    override val hint: String? = null,
    override val explanation: String,
    override val conceptId: String = "general"
) : Exercise

data class FillCodeExercise(
    override val id: String = UUID.randomUUID().toString(),
    override val prompt: String,
    val codeWithBlanks: String, // Uses ___ for blanks
    val tokens: List<String>, // Draggable tokens
    val correctTokens: List<String>,
    override val hint: String? = null,
    override val explanation: String,
    override val conceptId: String = "general"
) : Exercise

data class PredictOutputExercise(
    override val id: String = UUID.randomUUID().toString(),
    override val prompt: String,
    val initialStructureState: String,
    val codeSnippet: String,
    val options: List<String>,
    val correctIndex: Int,
    override val hint: String? = null,
    override val explanation: String,
    override val conceptId: String = "general"
) : Exercise

data class BugHuntExercise(
    override val id: String = UUID.randomUUID().toString(),
    override val prompt: String,
    val codeLines: List<String>,
    val buggyLineIndex: Int,
    val fixOptions: List<String>,
    val correctFixIndex: Int,
    override val hint: String? = null,
    override val explanation: String,
    override val conceptId: String = "general"
) : Exercise

data class BuildStructureExercise(
    override val id: String = UUID.randomUUID().toString(),
    override val prompt: String,
    val structureType: String, // e.g. "binary_tree", "linked_list", "graph"
    val targetItems: List<String>,
    val validConnections: List<Pair<String, String>>,
    override val hint: String? = null,
    override val explanation: String,
    override val conceptId: String = "general"
) : Exercise

data class OperateVisualizerExercise(
    override val id: String = UUID.randomUUID().toString(),
    override val prompt: String,
    val structure: String, // "array", "linked_list", "stack", "queue", "bst", "heap", "graph"
    val initialItems: List<String>,
    val targetOperation: String, // e.g. "insert(2, 42)", "delete(0)", "rotate_left"
    val expectedStepIndices: List<Int>,
    override val hint: String? = null,
    override val explanation: String,
    override val conceptId: String = "general"
) : Exercise

data class ComplexityDialExercise(
    override val id: String = UUID.randomUUID().toString(),
    override val prompt: String,
    val codeSnippet: String? = null,
    val options: List<String> = listOf("O(1)", "O(log n)", "O(n)", "O(n log n)", "O(n²)", "O(2ⁿ)"),
    val correctComplexity: String,
    override val hint: String? = null,
    override val explanation: String,
    override val conceptId: String = "general"
) : Exercise

data class SortIntoBucketsExercise(
    override val id: String = UUID.randomUUID().toString(),
    override val prompt: String,
    val buckets: List<String>,
    val itemsWithBucket: List<Pair<String, Int>>, // item name to bucket index
    override val hint: String? = null,
    override val explanation: String,
    override val conceptId: String = "general"
) : Exercise

data class TraceRouteExercise(
    override val id: String = UUID.randomUUID().toString(),
    override val prompt: String,
    val traversalAlgorithm: String, // "preorder", "inorder", "postorder", "bfs", "dfs"
    val nodes: List<String>,
    val correctVisitingSequence: List<String>,
    override val hint: String? = null,
    override val explanation: String,
    override val conceptId: String = "general"
) : Exercise

data class SpeedRoundExercise(
    override val id: String = UUID.randomUUID().toString(),
    override val prompt: String,
    val rapidQuestions: List<MultipleChoiceExercise>,
    val durationSeconds: Int = 30,
    override val hint: String? = null,
    override val explanation: String = "Rapid-fire review",
    override val conceptId: String = "general"
) : Exercise

data class CodeSandboxExercise(
    override val id: String = UUID.randomUUID().toString(),
    override val prompt: String,
    val starterCode: String,
    val solutionCode: String,
    val testCases: List<Pair<String, String>>, // input to expected output
    override val hint: String? = null,
    override val explanation: String,
    override val conceptId: String = "general"
) : Exercise
