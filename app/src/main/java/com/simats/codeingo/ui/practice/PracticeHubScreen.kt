package com.simats.codeingo.ui.practice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.R
import com.simats.codeingo.data.model.MistakeVaultItem
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.ui.components.AppButton
import com.simats.codeingo.ui.components.AppButtonStyle
import com.simats.codeingo.ui.phoenix.PhoenixAtmosphericBackgroundView
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DsaBlue
import com.simats.codeingo.ui.theme.DsaGreen
import com.simats.codeingo.ui.theme.DsaOrange
import com.simats.codeingo.ui.theme.DsaPurple
import com.simats.codeingo.ui.theme.DsaRed
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoGreenDark
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.InputBorder
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.theme.liquidGlassCard
import com.simats.codeingo.ui.theme.pressScale
import com.simats.codeingo.ui.theme.tilt3D

data class InterviewProblem(
    val title: String,
    val complexity: String,
    val difficulty: String,
    val diffColor: Color,
    val approach: String,
    val sampleCode: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeHubScreen(
    modifier: Modifier = Modifier,
    onOpenArrayKingdom: (() -> Unit)? = null,
    onOpenStackTower: (() -> Unit)? = null,
    onOpenQueueStation: (() -> Unit)? = null,
    onOpenLinkedListRoad: (() -> Unit)? = null,
    onOpenBinaryTreeForest: (() -> Unit)? = null
) {
    val gameManager = GameManager.instance
    val mistakes by gameManager.mistakeVault.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Mistake Vault, 1: Daily Review, 2: Interview Prep

    var repairingMistake by remember { mutableStateOf<MistakeVaultItem?>(null) }
    var selectedProblem by remember { mutableStateOf<InterviewProblem?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        PhoenixAtmosphericBackgroundView()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFA8000).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🛠️", fontSize = 22.sp)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Spaced Repetition & Repair",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = LocalDynamicThemeColors.current.textPrimary
                    )
                    val unrepairedCount = mistakes.count { !it.isRepaired }
                    Text(
                        text = "$unrepairedCount mistakes waiting in vault",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = DsaOrange
                    )
                }
            }

            // Segment Tabs (matching iOS)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val tabs = listOf(
                    Pair("Mistake Vault", "📦"),
                    Pair("Daily Review", "⚡"),
                    Pair("Interview Mode", "💼")
                )
                tabs.forEachIndexed { index, (title, icon) ->
                    val isSelected = selectedTab == index
                    val shape = RoundedCornerShape(16.dp)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(shape)
                            .background(if (isSelected) DuolingoGreen else CardBackground.copy(alpha = 0.75f))
                            .border(1.5.dp, if (isSelected) DuolingoGreen else InputBorder, shape)
                            .pressScale()
                            .clickable { selectedTab = index }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = icon, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isSelected) Color.White else SubtextGray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3D Worlds Practice Banners (matching iOS)
            WorldPracticeBanners(
                onOpenArrayKingdom = onOpenArrayKingdom,
                onOpenStackTower = onOpenStackTower,
                onOpenQueueStation = onOpenQueueStation,
                onOpenLinkedListRoad = onOpenLinkedListRoad,
                onOpenBinaryTreeForest = onOpenBinaryTreeForest
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Tab Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (selectedTab) {
                    0 -> MistakeVaultTab(
                        mistakes = mistakes,
                        onStartRepair = { mistake -> repairingMistake = mistake }
                    )
                    1 -> DailyReviewTab(
                        onCompleteReview = {
                            gameManager.awardLessonXP(baseXP = 25, accuracyPercentage = 1.0, speedSeconds = 30)
                        }
                    )
                    2 -> InterviewPrepTab(
                        onSelectProblem = { prob -> selectedProblem = prob }
                    )
                }
            }

            // Mistake Repair Bottom Sheet
            if (repairingMistake != null) {
                val item = repairingMistake!!
                ModalBottomSheet(
                    onDismissRequest = { repairingMistake = null },
                    containerColor = DarkBackground
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "🛠️ Mistake Repair",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = LocalDynamicThemeColors.current.textPrimary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = item.prompt,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = LocalDynamicThemeColors.current.textPrimary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(CardBackground)
                                .border(1.dp, InputBorder, RoundedCornerShape(14.dp))
                                .padding(14.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "EXPLANATION & WHY:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = DsaOrange
                                )
                                Text(
                                    text = item.explanation,
                                    fontSize = 13.sp,
                                    color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.9f),
                                    lineHeight = 18.sp
                                )
                                Divider(color = InputBorder)
                                Text(
                                    text = "Correct Answer: ${item.correctAnswer}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DuolingoGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        AppButton(
                            title = "MARK REPAIRED (+10 XP) ✨",
                            style = AppButtonStyle.SUCCESS_GREEN,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                gameManager.repairMistake(item.id)
                                repairingMistake = null
                            }
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }

            // Problem Detail Bottom Sheet
            if (selectedProblem != null) {
                val prob = selectedProblem!!
                ModalBottomSheet(
                    onDismissRequest = { selectedProblem = null },
                    containerColor = DarkBackground
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = prob.title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = LocalDynamicThemeColors.current.textPrimary
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(prob.diffColor.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = prob.difficulty,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = prob.diffColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = prob.complexity,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            color = AmberGold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "KEY PATTERN & APPROACH",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = SubtextGray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = prob.approach,
                            fontSize = 13.sp,
                            color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.9f),
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "PYTHON IMPLEMENTATION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = SubtextGray
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0F1720))
                                .border(1.dp, InputBorder, RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Text(
                                text = prob.sampleCode,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF68D391),
                                lineHeight = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        AppButton(
                            title = "GOT IT! 👍",
                            style = AppButtonStyle.SUCCESS_GREEN,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { selectedProblem = null }
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────────────
// 1. Mistake Vault Tab
// ──────────────────────────────────────────────
@Composable
private fun MistakeVaultTab(
    mistakes: List<MistakeVaultItem>,
    onStartRepair: (MistakeVaultItem) -> Unit
) {
    if (mistakes.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(DuolingoGreen.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = DuolingoGreen, modifier = Modifier.size(36.dp))
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "No Mistakes Recorded!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = LocalDynamicThemeColors.current.textPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Complete lessons and any incorrect answers will automatically be saved here for spaced repair.",
                    fontSize = 13.sp,
                    color = SubtextGray,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(mistakes) { item ->
                val shape = RoundedCornerShape(16.dp)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape)
                        .background(CardBackground)
                        .border(
                            1.5.dp,
                            if (item.isRepaired) DuolingoGreen.copy(alpha = 0.5f) else DsaOrange.copy(alpha = 0.5f),
                            shape
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(DsaOrange.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = item.conceptId.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = DsaOrange,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        if (item.isRepaired) {
                            Text(
                                text = "REPAIRED ✨",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = DuolingoGreen
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(DsaOrange)
                                    .clickable { onStartRepair(item) }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "REPAIR (+10 XP)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = LocalDynamicThemeColors.current.textPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = item.prompt,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = LocalDynamicThemeColors.current.textPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Wrong: ${item.wrongAnswerGiven}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DuolingoRed
                        )
                        Text(text = "•", color = SubtextGray)
                        Text(
                            text = "Correct: ${item.correctAnswer}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DuolingoGreen
                        )
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────────────
// 2. Daily Review Tab (Spaced Repetition Workout)
// ──────────────────────────────────────────────
@Composable
private fun DailyReviewTab(
    onCompleteReview: () -> Unit
) {
    var isReviewActive by remember { mutableStateOf(false) }
    var currentReviewIdx by remember { mutableIntStateOf(0) }
    var reviewCompleted by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var isAnswerChecked by remember { mutableStateOf(false) }

    val drillQuestions = remember {
        listOf(
            Triple("What is the time complexity of pushing to a dynamic array amortized?", listOf("O(1)", "O(n)", "O(log n)", "O(n²)"), 0),
            Triple("Which data structure operates on LIFO (Last-In-First-Out)?", listOf("Queue", "Stack", "Heap", "Tree"), 1),
            Triple("What algorithm is ideal for shortest paths on unweighted graphs?", listOf("DFS", "Dijkstra", "BFS", "Bellman-Ford"), 2),
            Triple("What is the balance factor constraint in an AVL Tree?", listOf("{-1, 0, +1}", "{0, 1, 2}", "{-2, +2}", "Any integer"), 0),
            Triple("In a Hash Map, how is average lookup complexity maintained at O(1)?", listOf("Sorting keys", "Uniform hash distribution", "Binary search", "Dynamic resizing"), 1)
        )
    }

    if (reviewCompleted) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "🎉", fontSize = 64.sp)
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "DAILY REVIEW COMPLETE!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "You earned +25 XP and reinforced 5 core DSA memory traces.",
                fontSize = 13.sp,
                color = SubtextGray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(28.dp))
            AppButton(
                title = "CONTINUE →",
                style = AppButtonStyle.SUCCESS_GREEN,
                onClick = {
                    reviewCompleted = false
                    isReviewActive = false
                    currentReviewIdx = 0
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    } else if (isReviewActive) {
        val q = drillQuestions[currentReviewIdx]
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "QUESTION ${currentReviewIdx + 1} OF ${drillQuestions.size}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = AmberGold
                    )
                    Text(text = "+25 XP Workout", fontSize = 12.sp, color = DuolingoGreen, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = q.first,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = LocalDynamicThemeColors.current.textPrimary,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                q.second.forEachIndexed { idx, opt ->
                    val isSelected = selectedOption == idx
                    val isCorrect = idx == q.third
                    val shape = RoundedCornerShape(14.dp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clip(shape)
                            .background(
                                when {
                                    isAnswerChecked && isCorrect -> DuolingoGreen.copy(alpha = 0.2f)
                                    isAnswerChecked && isSelected && !isCorrect -> DuolingoRed.copy(alpha = 0.2f)
                                    isSelected -> DsaBlue.copy(alpha = 0.2f)
                                    else -> CardBackground
                                }
                            )
                            .border(
                                1.5.dp,
                                when {
                                    isAnswerChecked && isCorrect -> DuolingoGreen
                                    isAnswerChecked && isSelected && !isCorrect -> DuolingoRed
                                    isSelected -> DsaBlue
                                    else -> InputBorder
                                },
                                shape
                            )
                            .pressScale()
                            .clickable(enabled = !isAnswerChecked) { selectedOption = idx }
                            .padding(16.dp)
                    ) {
                        Text(
                            text = opt,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = LocalDynamicThemeColors.current.textPrimary
                        )
                    }
                }
            }

            AppButton(
                title = if (!isAnswerChecked) "CHECK ANSWER" else if (currentReviewIdx < drillQuestions.lastIndex) "NEXT QUESTION →" else "FINISH REVIEW 🏆",
                style = if (selectedOption != null) AppButtonStyle.SUCCESS_GREEN else AppButtonStyle.SECONDARY_GLASS,
                isEnabled = selectedOption != null || isAnswerChecked,
                onClick = {
                    if (!isAnswerChecked) {
                        if (selectedOption != null) isAnswerChecked = true
                    } else {
                        if (currentReviewIdx < drillQuestions.lastIndex) {
                            currentReviewIdx += 1
                            selectedOption = null
                            isAnswerChecked = false
                        } else {
                            onCompleteReview()
                            reviewCompleted = true
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )
        }
    } else {
        // Daily Review Overview Card
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(CardBackground)
                    .border(1.5.dp, DuolingoGreen.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "5 Quick Reviews",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = LocalDynamicThemeColors.current.textPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Optimized for long-term retention using Leitner curve",
                                fontSize = 12.sp,
                                color = SubtextGray
                            )
                        }
                        Text(text = "🔥", fontSize = 32.sp)
                    }

                    AppButton(
                        title = "START DAILY REVIEW (+25 XP)",
                        style = AppButtonStyle.SUCCESS_GREEN,
                        onClick = { isReviewActive = true },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

// ──────────────────────────────────────────────
// 3. Interview Prep Tab (Real Interview Patterns)
// ──────────────────────────────────────────────
@Composable
private fun InterviewPrepTab(
    onSelectProblem: (InterviewProblem) -> Unit
) {
    val problems = remember {
        listOf(
            InterviewProblem(
                title = "Two Sum (Hash Map)",
                complexity = "O(n) time • O(n) space",
                difficulty = "Easy",
                diffColor = DuolingoGreen,
                approach = "Use a hash map to store complements (target - num). Check in O(1) time if complement exists in single pass.",
                sampleCode = "def twoSum(nums, target):\n    seen = {}\n    for i, num in enumerate(nums):\n        complement = target - num\n        if complement in seen:\n            return [seen[complement], i]\n        seen[num] = i\n    return []"
            ),
            InterviewProblem(
                title = "Reverse Linked List (Pointers)",
                complexity = "O(n) time • O(1) space",
                difficulty = "Easy",
                diffColor = DuolingoGreen,
                approach = "Iterate through list keeping track of prev and curr pointers. Reverse curr.next = prev in each step.",
                sampleCode = "def reverseList(head):\n    prev = None\n    curr = head\n    while curr:\n        nxt = curr.next\n        curr.next = prev\n        prev = curr\n        curr = nxt\n    return prev"
            ),
            InterviewProblem(
                title = "Valid Parentheses (Stack)",
                complexity = "O(n) time • O(n) space",
                difficulty = "Easy",
                diffColor = DuolingoGreen,
                approach = "Push opening brackets to stack. For closing brackets, pop and ensure matching pair.",
                sampleCode = "def isValid(s):\n    stack = []\n    mapping = {')': '(', '}': '{', ']': '['}\n    for char in s:\n        if char in mapping:\n            top = stack.pop() if stack else '#'\n            if mapping[char] != top: return False\n        else:\n            stack.append(char)\n    return not stack"
            ),
            InterviewProblem(
                title = "Binary Tree Level Order (Queue)",
                complexity = "O(n) time • O(n) space",
                difficulty = "Medium",
                diffColor = AmberGold,
                approach = "Perform BFS using a queue. Pop nodes level by level and append children for next level.",
                sampleCode = "from collections import deque\ndef levelOrder(root):\n    if not root: return []\n    res, q = [], deque([root])\n    while q:\n        level = []\n        for _ in range(len(q)):\n            node = q.popleft()\n            level.append(node.val)\n            if node.left: q.append(node.left)\n            if node.right: q.append(node.right)\n        res.append(level)\n    return res"
            ),
            InterviewProblem(
                title = "Course Schedule (Topological Graph)",
                complexity = "O(V + E) time • O(V + E) space",
                difficulty = "Medium",
                diffColor = AmberGold,
                approach = "Build adjacency graph and in-degree array. Push 0 in-degree nodes to queue (Kahn's Algorithm).",
                sampleCode = "from collections import deque\ndef canFinish(numCourses, prerequisites):\n    adj = [[] for _ in range(numCourses)]\n    indegree = [0] * numCourses\n    for dest, src in prerequisites:\n        adj[src].append(dest)\n        indegree[dest] += 1\n    q = deque([i for i in range(numCourses) if indegree[i] == 0])\n    visited = 0\n    while q:\n        node = q.popleft()\n        visited += 1\n        for neighbor in adj[node]:\n            indegree[neighbor] -= 1\n            if indegree[neighbor] == 0: q.append(neighbor)\n    return visited == numCourses"
            ),
            InterviewProblem(
                title = "LRU Cache (Doubly Linked + Map)",
                complexity = "O(1) get & put • O(capacity) space",
                difficulty = "Hard",
                diffColor = DsaRed,
                approach = "Combine a hash map with a doubly linked list for O(1) eviction of least recently used node.",
                sampleCode = "class Node:\n    def __init__(self, k, v):\n        self.k, self.v = k, v\n        self.prev = self.next = None\n\nclass LRUCache:\n    def __init__(self, capacity):\n        self.cap = capacity\n        self.map = {}\n        self.head, self.tail = Node(0,0), Node(0,0)\n        self.head.next = self.tail\n        self.tail.prev = self.head"
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(problems) { prob ->
            val shape = RoundedCornerShape(16.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .background(CardBackground)
                    .border(1.2.dp, InputBorder, shape)
                    .pressScale()
                    .clickable { onSelectProblem(prob) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = prob.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = LocalDynamicThemeColors.current.textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = prob.complexity,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = SubtextGray
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(prob.diffColor.copy(alpha = 0.2f))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = prob.difficulty,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = prob.diffColor
                    )
                }
            }
        }
    }
}

// ──────────────────────────────────────────────
// 4. World Practice Banners (matching iOS)
// ──────────────────────────────────────────────
@Composable
private fun WorldPracticeBanners(
    onOpenArrayKingdom: (() -> Unit)?,
    onOpenStackTower: (() -> Unit)?,
    onOpenQueueStation: (() -> Unit)?,
    onOpenLinkedListRoad: (() -> Unit)?,
    onOpenBinaryTreeForest: (() -> Unit)?
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            PracticeBannerCard(
                tag = "3D SORTING ARENA",
                tagColor = AmberGold,
                title = "👑 Array Kingdom",
                subtitle = "Bubble Sort Challenge",
                emotionRes = R.drawable.phoenix_emotion_4,
                gradientColors = listOf(AmberGold, Color(0xFFFA8000)),
                onClick = { onOpenArrayKingdom?.invoke() }
            )
        }
        item {
            PracticeBannerCard(
                tag = "3D LIFO TOWER",
                tagColor = DsaBlue,
                title = "🗼 Stack Tower",
                subtitle = "Tower Collapse",
                emotionRes = R.drawable.phoenix_emotion_28,
                gradientColors = listOf(Color(0xFF00E5FF), DsaBlue),
                onClick = { onOpenStackTower?.invoke() }
            )
        }
        item {
            PracticeBannerCard(
                tag = "3D FIFO STATION",
                tagColor = DuolingoGreen,
                title = "🚋 Queue Station",
                subtitle = "Station Chaos",
                emotionRes = R.drawable.phoenix_emotion_25,
                gradientColors = listOf(DuolingoGreen, Color(0xFF00897B)),
                onClick = { onOpenQueueStation?.invoke() }
            )
        }
        item {
            PracticeBannerCard(
                tag = "3D POINTER ROAD",
                tagColor = DsaPurple,
                title = "🛣️ Linked List Road",
                subtitle = "Broken Road",
                emotionRes = R.drawable.phoenix_emotion_4,
                gradientColors = listOf(DsaPurple, Color(0xFF6200EA)),
                onClick = { onOpenLinkedListRoad?.invoke() }
            )
        }
        item {
            PracticeBannerCard(
                tag = "3D TREE FOREST",
                tagColor = Color(0xFF00CD9C),
                title = "🌲 Binary Tree Forest",
                subtitle = "Lost Forest",
                emotionRes = R.drawable.phoenix_emotion_13,
                gradientColors = listOf(Color(0xFF00CD9C), Color(0xFF7B1FA2)),
                onClick = { onOpenBinaryTreeForest?.invoke() }
            )
        }
    }
}

@Composable
private fun PracticeBannerCard(
    tag: String,
    tagColor: Color,
    title: String,
    subtitle: String,
    emotionRes: Int,
    gradientColors: List<Color>,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = Modifier
            .width(260.dp)
            .clip(shape)
            .background(CardBackground.copy(alpha = 0.85f))
            .border(1.5.dp, tagColor.copy(alpha = 0.45f), shape)
            .pressScale()
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(gradientColors)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = emotionRes),
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = tag,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = tagColor
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(DuolingoRed)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "BOSS",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        color = LocalDynamicThemeColors.current.textPrimary
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = LocalDynamicThemeColors.current.textPrimary,
                maxLines = 1
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = SubtextGray,
                maxLines = 1
            )
        }

        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            tint = tagColor,
            modifier = Modifier.size(18.dp)
        )
    }
}

