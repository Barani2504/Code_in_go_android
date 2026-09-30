package com.simats.duolingo.data

import java.util.UUID

// ─── Lesson path node ─────────────────────────────────────────────────────────
data class LessonNodeItem(
    val id: Int,
    val levelNumber: Int,
    val icon: NodeIcon,
    val xOffsetDp: Float,
    val title: String,
)

enum class NodeIcon { STAR, BOOK, DUMBBELL, CHEST, HEADPHONES, TROPHY }

enum class UnitCharacterType {
    DUO_BACKPACK,
    LILY_PURPLE,
    VIKRAM_BEES,
    OSCAR_ARTIST,
    JUNIOR_PARTY
}

// ─── Unit block ────────────────────────────────────────────────────────────────
data class UnitModel(
    val id: Int,
    val sectionNumber: Int,
    val unitNumber: Int,
    val titleDefault: String,
    val themeColorHex: Long,
    val themeDarkColorHex: Long,
    val characterType: UnitCharacterType,
    val nodes: List<LessonNodeItem>,
)

// ─── Quiz question ─────────────────────────────────────────────────────────────
data class QuizQuestion(
    val id: String = UUID.randomUUID().toString(),
    val type: QuestionType,
    val prompt: String,
    val codeSnippet: String? = null,
    val options: List<String>,
    val correctIndex: Int,
)

enum class QuestionType { MULTIPLE_CHOICE, CODE_COMPLETE, ARRANGE }

// ─── Static unit data (mirrors Swift units array) ─────────────────────────────
val allUnits = listOf(
    UnitModel(
        id = 1, sectionNumber = 1, unitNumber = 1,
        titleDefault = "Write basic syntax",
        themeColorHex = 0xFF58CC02, themeDarkColorHex = 0xFF46A302,
        characterType = UnitCharacterType.DUO_BACKPACK,
        nodes = listOf(
            LessonNodeItem(1, 1,  NodeIcon.STAR,     -10f, "Write basic syntax"),
            LessonNodeItem(2, 2,  NodeIcon.STAR,     -45f, "Declare Variables"),
            LessonNodeItem(3, 3,  NodeIcon.DUMBBELL, -65f, "Practice & Syntax"),
            LessonNodeItem(4, 4,  NodeIcon.CHEST,      0f, "Bonus Reward Chest"),
            LessonNodeItem(5, 5,  NodeIcon.STAR,      50f, "Basic Functions"),
            LessonNodeItem(6, 6,  NodeIcon.TROPHY,     0f, "Unit 1 Trophy"),
        )
    ),
    UnitModel(
        id = 2, sectionNumber = 1, unitNumber = 2,
        titleDefault = "Solo trip: Debug runtime errors",
        themeColorHex = 0xFFCE82FF, themeDarkColorHex = 0xFFAA5ADC,
        characterType = UnitCharacterType.LILY_PURPLE,
        nodes = listOf(
            LessonNodeItem(7,  7,  NodeIcon.BOOK,      0f,  "Error Logs"),
            LessonNodeItem(8,  8,  NodeIcon.STAR,     45f,  "Stack Traces"),
            LessonNodeItem(9,  9,  NodeIcon.CHEST,    65f,  "Debugging Chest"),
            LessonNodeItem(10, 10, NodeIcon.HEADPHONES,10f, "Audio Logs"),
            LessonNodeItem(11, 11, NodeIcon.DUMBBELL, -50f, "Fixing Bugs Practice"),
            LessonNodeItem(12, 12, NodeIcon.TROPHY,    0f,  "Unit 2 Trophy"),
        )
    ),
    UnitModel(
        id = 3, sectionNumber = 1, unitNumber = 3,
        titleDefault = "Solo trip: Setup environment",
        themeColorHex = 0xFF00CD9C, themeDarkColorHex = 0xFF00A57D,
        characterType = UnitCharacterType.VIKRAM_BEES,
        nodes = listOf(
            LessonNodeItem(13, 13, NodeIcon.BOOK,       0f,  "Setup Guidebook"),
            LessonNodeItem(14, 14, NodeIcon.STAR,      -40f, "IDE Configuration"),
            LessonNodeItem(15, 15, NodeIcon.CHEST,     -60f, "Packages Reward Chest"),
            LessonNodeItem(16, 16, NodeIcon.HEADPHONES,  0f, "Podcast Setup"),
            LessonNodeItem(17, 17, NodeIcon.TROPHY,     45f, "Unit 3 Trophy"),
        )
    ),
    UnitModel(
        id = 4, sectionNumber = 1, unitNumber = 4,
        titleDefault = "Compile code",
        themeColorHex = 0xFFFF9600, themeDarkColorHex = 0xFFDC7800,
        characterType = UnitCharacterType.OSCAR_ARTIST,
        nodes = listOf(
            LessonNodeItem(18, 18, NodeIcon.STAR,      10f, "Build Scripts"),
            LessonNodeItem(19, 19, NodeIcon.DUMBBELL,  50f, "Compilation Practice"),
            LessonNodeItem(20, 20, NodeIcon.CHEST,     60f, "Build Tool Chest"),
            LessonNodeItem(21, 21, NodeIcon.HEADPHONES,-10f,"Build Output"),
            LessonNodeItem(22, 22, NodeIcon.TROPHY,     0f, "Unit 4 Trophy"),
        )
    ),
    UnitModel(
        id = 5, sectionNumber = 1, unitNumber = 5,
        titleDefault = "Deploy application",
        themeColorHex = 0xFF1CB0F6, themeDarkColorHex = 0xFF1899D6,
        characterType = UnitCharacterType.JUNIOR_PARTY,
        nodes = listOf(
            LessonNodeItem(23, 23, NodeIcon.STAR,  -10f, "CI/CD Pipeline"),
            LessonNodeItem(24, 24, NodeIcon.BOOK,  -45f, "Deployment Guide"),
            LessonNodeItem(25, 25, NodeIcon.CHEST, -65f, "Launch Day Chest"),
            LessonNodeItem(26, 26, NodeIcon.TROPHY,  0f, "Mastery Trophy"),
        )
    ),
)

// ─── Per-unit quiz questions (sample set) ─────────────────────────────────────
fun questionsForUnit(unitId: Int): List<QuizQuestion> = when (unitId) {
    1 -> listOf(
        QuizQuestion(
            type = QuestionType.MULTIPLE_CHOICE,
            prompt = "Which keyword declares a variable in Python?",
            options = listOf("var", "let", "val", "No keyword needed"),
            correctIndex = 3
        ),
        QuizQuestion(
            type = QuestionType.CODE_COMPLETE,
            prompt = "Complete the Python print statement:",
            codeSnippet = "_____(\"Hello, World!\")",
            options = listOf("print", "echo", "log", "write"),
            correctIndex = 0
        ),
        QuizQuestion(
            type = QuestionType.MULTIPLE_CHOICE,
            prompt = "What is the output of: 2 ** 3 in Python?",
            options = listOf("6", "8", "9", "5"),
            correctIndex = 1
        ),
    )
    2 -> listOf(
        QuizQuestion(
            type = QuestionType.MULTIPLE_CHOICE,
            prompt = "Which tool shows a Java stack trace?",
            options = listOf("println", "Exception.printStackTrace()", "log.d", "debug()"),
            correctIndex = 1
        ),
        QuizQuestion(
            type = QuestionType.CODE_COMPLETE,
            prompt = "Complete the try-catch block:",
            codeSnippet = "try { }\n_____ (Exception e) { }",
            options = listOf("catch", "except", "handle", "rescue"),
            correctIndex = 0
        ),
        QuizQuestion(
            type = QuestionType.MULTIPLE_CHOICE,
            prompt = "What is a NullPointerException?",
            options = listOf("Memory overflow", "Dereferencing a null reference", "Division by zero", "Syntax error"),
            correctIndex = 1
        ),
    )
    3 -> listOf(
        QuizQuestion(
            type = QuestionType.MULTIPLE_CHOICE,
            prompt = "Which command initializes a new Go module?",
            options = listOf("go init", "go mod init", "go start", "go new"),
            correctIndex = 1
        ),
        QuizQuestion(
            type = QuestionType.CODE_COMPLETE,
            prompt = "Complete the package declaration in Go:",
            codeSnippet = "_____ main\n\nimport \"fmt\"",
            options = listOf("module", "package", "namespace", "include"),
            correctIndex = 1
        ),
    )
    4 -> listOf(
        QuizQuestion(
            type = QuestionType.MULTIPLE_CHOICE,
            prompt = "Which flag produces optimized release builds in GCC/Clang?",
            options = listOf("-g", "-O3", "-Wall", "-c"),
            correctIndex = 1
        ),
        QuizQuestion(
            type = QuestionType.CODE_COMPLETE,
            prompt = "Complete the Makefile target:",
            codeSnippet = "all: main.o\n\t$(CC) -o app main.o\n\nclean:\n\t_____ -f *.o app",
            options = listOf("del", "rm", "clean", "remove"),
            correctIndex = 1
        ),
    )
    else -> listOf(
        QuizQuestion(
            type = QuestionType.MULTIPLE_CHOICE,
            prompt = "Which container engine is standard for Dockerfile deployment?",
            options = listOf("Docker", "Vagrant", "VirtualBox", "QEMU"),
            correctIndex = 0
        ),
        QuizQuestion(
            type = QuestionType.CODE_COMPLETE,
            prompt = "Complete the GitHub Actions workflow trigger:",
            codeSnippet = "on:\n  ____:\n    branches: [ main ]",
            options = listOf("push", "pull", "commit", "build"),
            correctIndex = 0
        ),
    )
}
