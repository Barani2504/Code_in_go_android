package com.simats.codeingo.ui.exercise

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.ui.components.AppButton
import com.simats.codeingo.ui.components.AppButtonStyle
import com.simats.codeingo.ui.components.ProgressBarAnimated
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

/**
 * 🧭 DiagnosticScreen — Placement Assessment
 * 12 questions evaluating learner readiness across 4 key skills:
 *  - Skill 1: Binary & Hexadecimal (3 questions)
 *  - Skill 2: Modular Arithmetic (3 questions)
 *  - Skill 3: Bitwise XOR & Logic (3 questions)
 *  - Skill 4: Security Core & CIA Triad (3 questions)
 *
 * Rules:
 *  - Zero heart cost (free diagnostic)
 *  - Zero XP earned (preserves leaderboard integrity)
 *  - Direct recommendation: Chapter 0 (Basecamp) vs Chapter 1 (Cipher Sanctum)
 *  - Skip confirmation dialog explaining 0 XP skip policy
 */
@Composable
fun DiagnosticScreen(
    modifier: Modifier = Modifier,
    gameManager: GameManager = GameManager.instance,
    onDismiss: () -> Unit = {},
    onSelectChapter: (chapterId: Int) -> Unit = {}
) {
    val dynamicColors = LocalDynamicThemeColors.current

    val questions = remember { getDiagnosticQuestions() }
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var isSubmitted by remember { mutableStateOf(false) }

    // Skill score counters
    var binaryScore by remember { mutableIntStateOf(0) }
    var moduloScore by remember { mutableIntStateOf(0) }
    var xorScore by remember { mutableIntStateOf(0) }
    var securityScore by remember { mutableIntStateOf(0) }

    var isFinished by remember { mutableStateOf(false) }
    var showSkipConfirmDialog by remember { mutableStateOf(false) }

    val currentQuestion = questions.getOrNull(currentIndex)
    val totalScore = binaryScore + moduloScore + xorScore + securityScore

    val slateAccent = Color(0xFF64748B)
    val cyanAccent = Color(0xFF06B6D4)
    val amberAccent = Color(0xFFF59E0B)
    val greenAccent = Color(0xFF10B981)
    val crimsonAccent = Color(0xFFEF4444)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(dynamicColors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        if (isFinished) {
            // ── RESULTS & PLACEMENT RECOMMENDATION SCREEN ───────────
            val recommendedChapter = if (totalScore >= 9) 1 else 0
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (totalScore >= 9) "🎯 READY FOR THE SANCTUM!" else "🏕️ BASECAMP RECOMMENDED",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = if (totalScore >= 9) greenAccent else amberAccent,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Placement Diagnostic Completed: $totalScore / 12 Correct",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = dynamicColors.textSecondary
                )

                // ── SKILL BREAKDOWN MATRIX ──────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(dynamicColors.cardBackground)
                        .border(1.5.dp, slateAccent.copy(alpha = 0.25f), RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            "FOUNDATIONAL SKILLS BREAKDOWN",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = slateAccent,
                            letterSpacing = 0.5.sp
                        )

                        SkillScoreBar(label = "Binary & Hexadecimal", score = binaryScore, total = 3, accentColor = cyanAccent)
                        SkillScoreBar(label = "Modular Arithmetic", score = moduloScore, total = 3, accentColor = amberAccent)
                        SkillScoreBar(label = "Bitwise XOR & Logic", score = xorScore, total = 3, accentColor = Color(0xFFA855F7))
                        SkillScoreBar(label = "Security Core & CIA", score = securityScore, total = 3, accentColor = greenAccent)
                    }
                }

                // ── RECOMMENDATION CARD ─────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            if (recommendedChapter == 1) greenAccent.copy(alpha = 0.12f)
                            else amberAccent.copy(alpha = 0.12f)
                        )
                        .border(
                            1.5.dp,
                            if (recommendedChapter == 1) greenAccent else amberAccent,
                            RoundedCornerShape(18.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = if (recommendedChapter == 1) "⚡" else "🏕️", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (recommendedChapter == 1) "Recommended: Chapter 1 (Cipher Sanctum)" else "Recommended: Chapter 0 (Basecamp)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = if (recommendedChapter == 1) greenAccent else amberAccent
                            )
                        }
                        Text(
                            text = if (recommendedChapter == 1) {
                                "You demonstrated strong mastery of binary representations, clock arithmetic, and XOR operations! You are fully prepared to jump straight into Caesar, substitution, and Vigenère ciphers."
                            } else {
                                "We recommend spending ~15 minutes in Chapter 0 (Basecamp) to master binary switches, modular clock rings, and XOR properties before tackling classical ciphers."
                            },
                            fontSize = 12.sp,
                            color = dynamicColors.textPrimary,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Primary Recommended Action Button
                AppButton(
                    title = if (recommendedChapter == 1) "ENTER CHAPTER 1: CIPHER SANCTUM" else "START CHAPTER 0: BASECAMP",
                    style = AppButtonStyle.SUCCESS_GREEN,
                    onClick = {
                        gameManager.setDiagnosticResult(recommendedChapter)
                        onSelectChapter(recommendedChapter)
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                )

                // Secondary Alternative Action
                if (recommendedChapter == 0) {
                    AppButton(
                        title = "SKIP BASECAMP (GO TO CHAPTER 1)",
                        style = AppButtonStyle.SECONDARY_GLASS,
                        onClick = { showSkipConfirmDialog = true },
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    )
                } else {
                    AppButton(
                        title = "EXPLORE CHAPTER 0 ANYWAY",
                        style = AppButtonStyle.SECONDARY_GLASS,
                        onClick = {
                            gameManager.setDiagnosticResult(recommendedChapter)
                            onSelectChapter(0)
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    )
                }
            }
        } else if (currentQuestion != null) {
            // ── DIAGNOSTIC QUESTION ARENA ───────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = dynamicColors.textSecondary)
                    }
                    Spacer(modifier = Modifier.width(6.dp))

                    val progress = (currentIndex.toFloat() / questions.size)
                    ProgressBarAnimated(progress = progress, modifier = Modifier.weight(1f).height(14.dp))

                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "${currentIndex + 1}/${questions.size}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = dynamicColors.textSecondary
                    )
                }

                // Question Category Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(dynamicColors.inputBackground)
                        .border(1.dp, dynamicColors.inputBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = currentQuestion.category,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = slateAccent
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Prompt Card
                Text(
                    text = currentQuestion.prompt,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = dynamicColors.textPrimary,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Multiple Choice Options
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    currentQuestion.options.forEachIndexed { index, option ->
                        val isSelected = selectedOptionIndex == index
                        val isCorrectOption = index == currentQuestion.correctIndex

                        val tileBg = when {
                            isSubmitted && isCorrectOption -> greenAccent.copy(alpha = 0.2f)
                            isSubmitted && isSelected && !isCorrectOption -> crimsonAccent.copy(alpha = 0.2f)
                            isSelected -> cyanAccent.copy(alpha = 0.18f)
                            else -> dynamicColors.inputBackground
                        }

                        val tileBorder = when {
                            isSubmitted && isCorrectOption -> greenAccent
                            isSubmitted && isSelected && !isCorrectOption -> crimsonAccent
                            isSelected -> cyanAccent
                            else -> dynamicColors.inputBorder
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(tileBg)
                                .border(1.5.dp, tileBorder, RoundedCornerShape(14.dp))
                                .clickable(enabled = !isSubmitted) { selectedOptionIndex = index }
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                        ) {
                            Text(
                                text = option,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = dynamicColors.textPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Bottom Check / Continue Button
                AppButton(
                    title = if (!isSubmitted) "CONFIRM ANSWER" else if (currentIndex < questions.size - 1) "NEXT QUESTION" else "VIEW PLACEMENT RESULTS",
                    style = if (selectedOptionIndex != null) AppButtonStyle.SUCCESS_GREEN else AppButtonStyle.DISABLED,
                    isEnabled = selectedOptionIndex != null,
                    onClick = {
                        if (!isSubmitted) {
                            isSubmitted = true
                            val correct = selectedOptionIndex == currentQuestion.correctIndex
                            if (correct) {
                                when (currentQuestion.skillCategory) {
                                    SkillCategory.BINARY_HEX -> binaryScore++
                                    SkillCategory.MODULO     -> moduloScore++
                                    SkillCategory.XOR        -> xorScore++
                                    SkillCategory.SECURITY   -> securityScore++
                                }
                            }
                        } else {
                            if (currentIndex < questions.size - 1) {
                                currentIndex++
                                selectedOptionIndex = null
                                isSubmitted = false
                            } else {
                                isFinished = true
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .padding(bottom = 12.dp)
                )
            }
        }

        // ── SKIP CHAPTER 0 CONFIRMATION DIALOG ──────────────────────
        if (showSkipConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showSkipConfirmDialog = false },
                title = {
                    Text(
                        "Skip Foundations Primer?",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = dynamicColors.textPrimary
                    )
                },
                text = {
                    Text(
                        "You can jump straight into Chapter 1 (Classical Ciphers). " +
                                "Skipped Basecamp levels award 0 XP to keep the global leaderboard fair. " +
                                "You can revisit Basecamp at any time from the dashboard map.",
                        fontSize = 13.sp,
                        color = dynamicColors.textSecondary,
                        lineHeight = 18.sp
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        showSkipConfirmDialog = false
                        gameManager.setDiagnosticResult(1)
                        onSelectChapter(1)
                    }) {
                        Text("SKIP TO CH 1", color = amberAccent, fontWeight = FontWeight.Black)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSkipConfirmDialog = false }) {
                        Text("START BASECAMP", color = dynamicColors.textPrimary, fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = dynamicColors.cardBackground,
                shape = RoundedCornerShape(18.dp)
            )
        }
    }
}

@Composable
private fun SkillScoreBar(
    label: String,
    score: Int,
    total: Int,
    accentColor: Color
) {
    val dynamicColors = LocalDynamicThemeColors.current
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = dynamicColors.textPrimary)
            Text("$score / $total", fontSize = 12.sp, fontWeight = FontWeight.Black, color = accentColor)
        }
        val fraction = score.toFloat() / total
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape)
                .background(dynamicColors.inputBackground)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(accentColor)
            )
        }
    }
}

private enum class SkillCategory {
    BINARY_HEX, MODULO, XOR, SECURITY
}

private data class DiagnosticQuestionItem(
    val category: String,
    val skillCategory: SkillCategory,
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int
)

private fun getDiagnosticQuestions(): List<DiagnosticQuestionItem> = listOf(
    // Skill 1: Binary & Hexadecimal
    DiagnosticQuestionItem(
        category = "Binary & Hex",
        skillCategory = SkillCategory.BINARY_HEX,
        prompt = "How many distinct states can be represented by a 4-bit nibble (0000 to 1111)?",
        options = listOf("16 states (0 to 15)", "4 states", "8 states", "32 states"),
        correctIndex = 0
    ),
    DiagnosticQuestionItem(
        category = "Binary & Hex",
        skillCategory = SkillCategory.BINARY_HEX,
        prompt = "What is the decimal equivalent of hexadecimal 0x1A?",
        options = listOf("26 (1×16 + 10)", "16", "20", "110"),
        correctIndex = 0
    ),
    DiagnosticQuestionItem(
        category = "Binary & Hex",
        skillCategory = SkillCategory.BINARY_HEX,
        prompt = "Which binary bit has weight 128 (2^7) in an 8-bit unsigned byte?",
        options = listOf("The Most Significant Bit (Bit 7, leftmost)", "The Least Significant Bit (Bit 0, rightmost)", "Bit 4 (middle)", "All bits have equal weight"),
        correctIndex = 0
    ),

    // Skill 2: Modular Arithmetic
    DiagnosticQuestionItem(
        category = "Clock Math / Modulo",
        skillCategory = SkillCategory.MODULO,
        prompt = "What is the result of 29 mod 7?",
        options = listOf("1 (29 = 4×7 + 1)", "4", "3", "0"),
        correctIndex = 0
    ),
    DiagnosticQuestionItem(
        category = "Clock Math / Modulo",
        skillCategory = SkillCategory.MODULO,
        prompt = "On a 12-hour clock, what hour is 4 hours before 2 o'clock (-2 mod 12)?",
        options = listOf("10 o'clock (-2 + 12 = 10)", "2 o'clock", "6 o'clock", "8 o'clock"),
        correctIndex = 0
    ),
    DiagnosticQuestionItem(
        category = "Clock Math / Modulo",
        skillCategory = SkillCategory.MODULO,
        prompt = "In the Caesar cipher alphabet ring (0='A' to 25='Z'), what letter is (24 + 3) mod 26?",
        options = listOf("Letter 'B' (index 1)", "Letter 'A' (index 0)", "Letter 'Z' (index 25)", "Letter 'C' (index 2)"),
        correctIndex = 0
    ),

    // Skill 3: Bitwise XOR & Logic
    DiagnosticQuestionItem(
        category = "Bitwise XOR & Logic",
        skillCategory = SkillCategory.XOR,
        prompt = "What is 1 XOR 1 in Boolean logic?",
        options = listOf("0 (inputs are identical)", "1", "2", "Undefined"),
        correctIndex = 0
    ),
    DiagnosticQuestionItem(
        category = "Bitwise XOR & Logic",
        skillCategory = SkillCategory.XOR,
        prompt = "If message P is encrypted as C = P ⊕ K, how is plaintext P recovered?",
        options = listOf("P = C ⊕ K (XOR with key K again)", "P = C AND K", "P = C OR K", "P = NOT C"),
        correctIndex = 0
    ),
    DiagnosticQuestionItem(
        category = "Bitwise XOR & Logic",
        skillCategory = SkillCategory.XOR,
        prompt = "Why does encrypting two different messages with the same One-Time Pad key (Two-Time Pad) fail?",
        options = listOf("C₁ ⊕ C₂ cancels the key, exposing P₁ ⊕ P₂", "It causes an infinite loop", "The ciphertexts become 0", "XOR cannot encrypt twice"),
        correctIndex = 0
    ),

    // Skill 4: Security Core & CIA Triad
    DiagnosticQuestionItem(
        category = "Security Core",
        skillCategory = SkillCategory.SECURITY,
        prompt = "Which pillar of the CIA Triad is violated when an attacker secretly eavesdrops on private network traffic?",
        options = listOf("Confidentiality", "Integrity", "Availability", "Authentication"),
        correctIndex = 0
    ),
    DiagnosticQuestionItem(
        category = "Security Core",
        skillCategory = SkillCategory.SECURITY,
        prompt = "Does symmetric encryption alone (like AES-CBC) guarantee that a message was NOT tampered with in transit?",
        options = listOf("No, encryption provides Confidentiality; Integrity requires a MAC or AEAD (like GCM)", "Yes, encrypted data can never be altered", "Yes, AES automatically checks signatures", "Only on Android"),
        correctIndex = 0
    ),
    DiagnosticQuestionItem(
        category = "Security Core",
        skillCategory = SkillCategory.SECURITY,
        prompt = "What is the primary objective of a volumetric DDoS flood attack?",
        options = listOf("To destroy Availability by exhausting network bandwidth and server resources", "To steal confidential user passwords", "To modify bank account balances", "To reverse engineer SSL certificates"),
        correctIndex = 0
    )
)
