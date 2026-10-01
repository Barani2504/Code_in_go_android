package com.simats.codeingo.data.model

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable
import java.util.UUID

// MARK: - 1. Unit Difficulty & Visual Theme
enum class DSADifficulty(val value: String, val badgeEmoji: String, val title: String) {
    GREEN("GREEN", "🟢", "Foundations & Linear"),
    YELLOW("YELLOW", "🟡", "Abstract & Hashing"),
    BLUE("BLUE", "🔵", "Non-Linear & Hierarchies"),
    RED("RED", "🔴", "Advanced Mastery");

    val color: Color
        get() = when (this) {
            GREEN -> Color(0xFF58CC02)
            YELLOW -> Color(0xFFFFC800)
            BLUE -> Color(0xFF1CB0F6)
            RED -> Color(0xFFFF4B4B)
        }

    val darkColor: Color
        get() = when (this) {
            GREEN -> Color(0xFF46A302)
            YELLOW -> Color(0xFFD7A000)
            BLUE -> Color(0xFF1899D6)
            RED -> Color(0xFFD23232)
        }
}

// MARK: - 2. Lesson Type
enum class DSALessonType(val value: String) {
    NORMAL("NORMAL"),
    CHALLENGE("CHALLENGE"),
    CHECKPOINT("CHECKPOINT"),
    CHEST("CHEST"),
    BOSS("BOSS")
}

// MARK: - 3. Exercise Types (15 Comprehensive Types)
enum class DSAExerciseType(val value: String, val displayName: String) {
    MULTIPLE_CHOICE("MultipleChoice", "Concept Check"),
    MULTI_SELECT("MultiSelect", "Multi-Select"),
    TRUE_FALSE_SWIPE("TrueFalseSwipe", "Fact Swipe"),
    MATCH_PAIRS("MatchPairs", "Match Pairs"),
    ORDER_STEPS("OrderSteps", "Parsons Puzzle"),
    FILL_CODE("FillCode", "Fill in Code"),
    PREDICT_OUTPUT("PredictOutput", "Predict Output"),
    BUG_HUNT("BugHunt", "Bug Hunt"),
    BUILD_STRUCTURE("BuildStructure", "Build Structure"),
    OPERATE_VISUALIZER("OperateVisualizer", "Operate Visualizer"),
    COMPLEXITY_DIAL("ComplexityDial", "Big-O Dial"),
    SORT_INTO_BUCKETS("SortIntoBuckets", "Bucket Classification"),
    TRACE_ROUTE("TraceRoute", "Trace Traversal"),
    SPEED_ROUND("SpeedRound", "30s Speed Round"),
    CODE_SANDBOX("CodeSandbox", "Code Sandbox")
}

// MARK: - 4. Exercise Item Model
@Serializable
data class DSAExerciseItem(
    val id: String,
    val type: DSAExerciseType,
    val prompt: String,
    val hint: String? = null,
    val explanation: String,
    val conceptId: String,
    val codeSnippet: String? = null,
    val options: List<String> = emptyList(),
    val correctAnswers: List<String> = emptyList(),
    val correctIndex: Int? = null,
    val pairs: Map<String, String> = emptyMap(),
    val parsonsLines: List<String> = emptyList(),
    val targetBucketA: List<String> = emptyList(),
    val targetBucketB: List<String> = emptyList(),
    val bucketALabel: String? = null,
    val bucketBLabel: String? = null,
    val timeComplexityOptions: List<String> = listOf("O(1)", "O(log n)", "O(n)", "O(n log n)", "O(n²)", "O(2ⁿ)"),
    val traversalTargetNodes: List<String> = emptyList()
)

// MARK: - 5. Step-Recorder Engine Cost Delta
@Serializable
data class DSACostDelta(
    val comparisons: Int = 0,
    val shifts: Int = 0,
    val pointerUpdates: Int = 0,
    val memoryAllocatedBytes: Int = 0
)

// MARK: - 6. Simulator Step
@Serializable
data class DSASimulatorStep(
    val id: String = UUID.randomUUID().toString(),
    val stateDescription: String,
    val narration: String,
    val highlightedIndices: List<Int> = emptyList(),
    val codeLine: Int? = null,
    val costDelta: DSACostDelta = DSACostDelta(),
    val arrayState: List<Int>? = null,
    val activePointers: Map<String, Int>? = null
)

// MARK: - 7. Boss Battle Specification
@Serializable
data class DSABossSpec(
    val id: String,
    val name: String,
    val title: String,
    val worldTheme: String,
    val avatarEmoji: String,
    val bossHp: Int = 100,
    val timeLimitSeconds: Int = 60,
    val quote: String,
    val defeatQuote: String,
    val phaseCount: Int = 3,
    val targetPhoenixStageAwarded: Int
)

// MARK: - 8. Course Lesson
@Serializable
data class DSACourseLesson(
    val id: String,
    val title: String,
    val type: DSALessonType = DSALessonType.NORMAL,
    val signatureActivity: String,
    val signatureDescription: String,
    val concepts: List<String>,
    val xpReward: Int = 15,
    val exercises: List<DSAExerciseItem> = emptyList(),
    var isCompleted: Boolean = false,
    var crownLevel: Int = 0
)

// MARK: - 9. Course Unit (11 Units in Spec)
@Serializable
data class DSACourseUnit(
    val id: String,
    val index: Int,
    val title: String,
    val difficulty: DSADifficulty,
    val worldTheme: String,
    val worldEmoji: String,
    val worldMetaphor: String,
    val lessons: List<DSACourseLesson>,
    val boss: DSABossSpec,
    val phoenixEvolutionsAwarded: List<Int>,
    var isUnlocked: Boolean = false,
    var isCompleted: Boolean = false
)
