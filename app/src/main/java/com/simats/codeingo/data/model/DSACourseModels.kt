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

// MARK: - 3. Exercise Types (Comprehensive CNS Types)
enum class ExerciseType(val value: String, val displayName: String) {
    MULTIPLE_CHOICE("MultipleChoice", "Concept Check"),
    MULTI_SELECT("MultiSelect", "Multi-Select"),
    TRUE_FALSE_SWIPE("TrueFalseSwipe", "Fact Swipe"),
    MATCH_PAIRS("MatchPairs", "Match Pairs"),
    ORDER_STEPS("OrderSteps", "Protocol Sequencing"),
    FILL_CODE("FillCode", "Fill in Code"),
    PREDICT_OUTPUT("PredictOutput", "Predict Ciphertext"),
    BUG_HUNT("BugHunt", "Vulnerability Hunt"),
    BUILD_STRUCTURE("BuildStructure", "Build Feistel / Block"),
    OPERATE_VISUALIZER("OperateVisualizer", "Operate Visualizer"),
    KEY_SIZE_DIAL("KeySizeDial", "Key Size & Security Level Dial"),
    SORT_INTO_BUCKETS("SortIntoBuckets", "Cipher Classification"),
    TRACE_ROUTE("TraceRoute", "Trace Key Exchange"),
    SPEED_ROUND("SpeedRound", "30s Speed Round"),
    CODE_SANDBOX("CodeSandbox", "Cipher Sandbox"),
    
    // ── Phase 2 CNS Domain Superpower Exercise Types ──
    CIPHER_WHEEL("CipherWheel", "Cipher Wheel Decryption"),
    BIT_FLIPPER("BitFlipper", "Avalanche Bit Flipper"),
    ROUND_BUILDER("RoundBuilder", "Round Transformation Builder"),
    MODULAR_MATH("ModularMath", "Modular Arithmetic Keypad"),
    PACKET_TRIAGE("PacketTriage", "Firewall Packet Triage"),
    SPOT_THE_FLAW("SpotTheFlaw", "Vulnerability Flaw Locator");

    companion object {
        @Deprecated("Use KEY_SIZE_DIAL", ReplaceWith("KEY_SIZE_DIAL"))
        val COMPLEXITY_DIAL = KEY_SIZE_DIAL
    }
}

@Deprecated("Use ExerciseType", ReplaceWith("ExerciseType"))
typealias DSAExerciseType = ExerciseType

// MARK: - 4. Exercise Item Model
@Serializable
data class DSAExerciseItem(
    val id: String,
    val type: ExerciseType,
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
    val timeComplexityOptions: List<String> = listOf("64-bit", "128-bit", "192-bit", "256-bit", "2048-bit", "4096-bit"),
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
