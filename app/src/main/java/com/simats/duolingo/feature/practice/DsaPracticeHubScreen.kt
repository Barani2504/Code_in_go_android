package com.simats.duolingo.feature.practice

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.duolingo.core.theme.*
import com.simats.duolingo.data.repository.GameStateRepository
import com.simats.duolingo.ui.theme.*

data class MistakeItem(
    val id: String,
    val conceptTag: String,
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    var isRepaired: Boolean = false
)

data class InterviewProblem(
    val title: String,
    val difficulty: String,
    val company: String,
    val timeComplexity: String,
    val prompt: String,
    val hint: String
)

@Composable
fun DsaPracticeHubScreen(
    onStartPractice: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Mistake Vault, 1: Daily Review, 2: Interview Mode
    var repairingMistake by remember { mutableStateOf<MistakeItem?>(null) }
    var rewardToast by remember { mutableStateOf<String?>(null) }

    // Mock initial mistakes
    val mistakes = remember {
        mutableStateListOf(
            MistakeItem(
                id = "m1",
                conceptTag = "Array Memory",
                prompt = "What is the time complexity of inserting an element at index 0 of an array of size n?",
                options = listOf("O(1)", "O(log n)", "O(n)", "O(n^2)"),
                correctIndex = 2,
                explanation = "All n elements must shift one position to the right to make space for the new head element."
            ),
            MistakeItem(
                id = "m2",
                conceptTag = "Stack LIFO",
                prompt = "Which stack operation retrieves the top item WITHOUT removing it?",
                options = listOf("pop()", "push()", "peek()", "isEmpty()"),
                correctIndex = 2,
                explanation = "peek() inspects the top element while leaving the stack unchanged."
            ),
            MistakeItem(
                id = "m3",
                conceptTag = "Linked List",
                prompt = "When reversing a singly linked list, which pointer must be updated first for curr?",
                options = listOf("curr.next = prev", "prev = curr", "curr = next", "head = curr"),
                correctIndex = 0,
                explanation = "Save next = curr.next, then point curr.next backwards to prev."
            )
        )
    }

    val interviewProblems = listOf(
        InterviewProblem(
            title = "Two Sum (Array / HashMap)",
            difficulty = "EASY",
            company = "Google, Meta",
            timeComplexity = "O(n) time, O(n) space",
            prompt = "Given an array of integers and a target, return indices of two numbers that add up to target.",
            hint = "Use a hash map to store complement (target - num) seen so far."
        ),
        InterviewProblem(
            title = "Valid Parentheses (Stack)",
            difficulty = "EASY",
            company = "Amazon, Apple",
            timeComplexity = "O(n) time, O(n) space",
            prompt = "Determine if the input string containing brackets '()[]{}' is valid.",
            hint = "Push opening brackets onto a stack. When a closing bracket arrives, verify it matches stack top."
        ),
        InterviewProblem(
            title = "Reverse Linked List",
            difficulty = "MEDIUM",
            company = "Microsoft, Netflix",
            timeComplexity = "O(n) time, O(1) space",
            prompt = "Reverse a singly linked list iteratively and return its new head.",
            hint = "Maintain 3 pointers: prev, curr, and next to flip edges in one pass."
        ),
        InterviewProblem(
            title = "Lowest Common Ancestor (BST)",
            difficulty = "MEDIUM",
            company = "Uber, Bloomberg",
            timeComplexity = "O(h) time, O(1) space",
            prompt = "Find the lowest common ancestor node of two given nodes in a Binary Search Tree.",
            hint = "If both nodes are smaller than root, go left. If both are larger, go right. Otherwise root is LCA."
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DsaCourseBackgroundGradient)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ── Top Header ────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .background(Color(0xFF1E1A3C))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF9600).copy(0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🛠️", fontSize = 22.sp)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Practice Hub & Mistake Vault",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black
                    )
                    val activeMistakes = mistakes.count { !it.isRepaired }
                    Text(
                        text = "$activeMistakes mistakes waiting for repair (+25 XP each)",
                        color = Color(0xFFFF9600),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // ── Tab Selector ──────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TabPill(
                    title = "Mistake Vault",
                    icon = "🗄️",
                    isSelected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.weight(1f)
                )
                TabPill(
                    title = "Daily Review",
                    icon = "📅",
                    isSelected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.weight(1f)
                )
                TabPill(
                    title = "Interview",
                    icon = "🧠",
                    isSelected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    modifier = Modifier.weight(1f)
                )
            }

            // ── Tab Body Content ──────────────────────────────────────────
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (selectedTab == 0) {
                    // Mistake Vault List
                    items(mistakes, key = { it.id }) { item ->
                        MistakeVaultCard(
                            item = item,
                            onRepair = { repairingMistake = item }
                        )
                    }
                } else if (selectedTab == 1) {
                    // Daily Review Quiz Card
                    item {
                        DailyReviewCard(
                            onComplete = {
                                rewardToast = "+30 XP Earned in Daily Review!"
                            }
                        )
                    }
                } else {
                    // Interview Mode Problem List
                    items(interviewProblems, key = { it.title }) { problem ->
                        InterviewProblemCard(problem = problem)
                    }
                }

                item {
                    Spacer(Modifier.height(30.dp))
                }
            }
        }

        // ── Repair Exercise Modal ─────────────────────────────────────────
        repairingMistake?.let { item ->
            MistakeRepairModal(
                item = item,
                onDismiss = { repairingMistake = null },
                onSuccess = {
                    item.isRepaired = true
                    repairingMistake = null
                    rewardToast = "✓ Mistake Repaired! +25 XP & +5 Gems awarded!"
                }
            )
        }

        // ── Toast Notification ────────────────────────────────────────────
        rewardToast?.let { toast ->
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF26204E))
                    .border(1.5.dp, Color(0xFF58CC02), RoundedCornerShape(16.dp))
                    .padding(horizontal = 18.dp, vertical = 12.dp)
            ) {
                Text(toast, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun TabPill(
    title: String,
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) Color(0xFFFF9600) else Color(0xFF26204E))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(icon, fontSize = 13.sp)
            Text(
                text = title,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold
            )
        }
    }
}

@Composable
private fun MistakeVaultCard(
    item: MistakeItem,
    onRepair: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E1A3C))
            .border(
                1.dp,
                if (item.isRepaired) Color(0xFF58CC02).copy(0.4f) else Color(0xFFFF9600).copy(0.4f),
                RoundedCornerShape(16.dp)
            )
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (item.isRepaired) Color(0xFF58CC02).copy(0.2f) else Color(0xFFFF9600).copy(0.2f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = item.conceptTag.uppercase(),
                    color = if (item.isRepaired) Color(0xFF58CC02) else Color(0xFFFF9600),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = item.prompt,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2
            )
        }

        Spacer(Modifier.width(10.dp))

        if (item.isRepaired) {
            Text("REPAIRED ✓", color = Color(0xFF58CC02), fontSize = 12.sp, fontWeight = FontWeight.Black)
        } else {
            Button(
                onClick = onRepair,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9600)),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text("REPAIR", fontSize = 11.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun DailyReviewCard(onComplete: () -> Unit) {
    var step by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var isDone by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF1E1A3C))
            .border(1.5.dp, Color(0xFF1CB0F6).copy(0.5f), RoundedCornerShape(20.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("⚡ 5-Minute Daily Spaced Repetition", color = Color(0xFF1CB0F6), fontSize = 14.sp, fontWeight = FontWeight.Black)
            Text("+30 XP", color = Color(0xFFFFC800), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        if (isDone) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("🎉", fontSize = 40.sp)
                Text("Daily Review Completed!", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
                Text("You reinforced key memory associations today.", color = DuolingoSubtext, fontSize = 12.sp)
            }
        } else {
            Text(
                text = "What is the worst-case search time in an unbalance Binary Search Tree?",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            listOf("O(1)", "O(log n)", "O(n) - degenerate linked list", "O(n log n)").forEachIndexed { index, opt ->
                val isSelected = selectedOption == index
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0xFF1CB0F6).copy(0.2f) else Color(0xFF14122C))
                        .border(1.dp, if (isSelected) Color(0xFF1CB0F6) else Color.White.copy(0.1f), RoundedCornerShape(12.dp))
                        .clickable { selectedOption = index }
                        .padding(14.dp)
                ) {
                    Text(opt, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Button(
                onClick = {
                    if (selectedOption != null) {
                        isDone = true
                        onComplete()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1CB0F6)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("CHECK ANSWER", fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun InterviewProblemCard(problem: InterviewProblem) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF1E1A3C))
            .border(1.dp, Color(0xFFCE82FF).copy(0.4f), RoundedCornerShape(18.dp))
            .clickable { expanded = !expanded }
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(problem.title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF58CC02).copy(0.2f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(problem.difficulty, color = Color(0xFF58CC02), fontSize = 10.sp, fontWeight = FontWeight.Black)
            }
        }

        Text("Asked at: ${problem.company}", color = DuolingoSubtext, fontSize = 11.sp)
        Text("Target: ${problem.timeComplexity}", color = Color(0xFFFFC800), fontSize = 11.sp, fontWeight = FontWeight.Bold)

        Text(problem.prompt, color = Color.White.copy(0.9f), fontSize = 13.sp)

        if (expanded) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF26204E))
                    .padding(10.dp)
            ) {
                Text("💡 Phoenix Hint: ${problem.hint}", color = Color(0xFF00CD9C), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MistakeRepairModal(
    item: MistakeItem,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var isAnswered by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(0.7f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF1E1A3C))
                .border(2.dp, Color(0xFFFF9600), RoundedCornerShape(24.dp))
                .clickable(enabled = false) {}
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("REPAIR MISTAKE 🛠️", color = Color(0xFFFF9600), fontSize = 15.sp, fontWeight = FontWeight.Black)
                IconButton(onClick = onDismiss) {
                    Text("✕", color = Color.White)
                }
            }

            Text(item.prompt, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)

            item.options.forEachIndexed { index, option ->
                val isSelected = selectedIndex == index
                val isItemCorrect = index == item.correctIndex
                val bgColor = when {
                    isAnswered && isItemCorrect -> Color(0xFF58CC02)
                    isAnswered && isSelected -> Color(0xFFFF4B4B)
                    isSelected -> Color(0xFFFF9600)
                    else -> Color(0xFF14122C)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(bgColor)
                        .clickable(enabled = !isAnswered) { selectedIndex = index }
                        .padding(14.dp)
                ) {
                    Text(option, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (isAnswered) {
                Text(item.explanation, color = Color.White.copy(0.8f), fontSize = 12.sp)
                Button(
                    onClick = {
                        if (isCorrect) onSuccess() else onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isCorrect) Color(0xFF58CC02) else Color(0xFFFF4B4B))
                ) {
                    Text(if (isCorrect) "COLLECT REWARD +25 XP" else "CLOSE", fontWeight = FontWeight.Black)
                }
            } else {
                Button(
                    onClick = {
                        if (selectedIndex != null) {
                            isAnswered = true
                            isCorrect = selectedIndex == item.correctIndex
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9600))
                ) {
                    Text("VERIFY REPAIR", fontWeight = FontWeight.Black)
                }
            }
        }
    }
}
