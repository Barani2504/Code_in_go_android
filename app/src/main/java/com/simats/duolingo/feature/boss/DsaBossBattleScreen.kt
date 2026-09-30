package com.simats.duolingo.feature.boss

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.duolingo.core.sound.rememberJuiceFeedback
import com.simats.duolingo.core.theme.*
import com.simats.duolingo.data.repository.GameStateRepository
import com.simats.duolingo.domain.model.BossBattle
import com.simats.duolingo.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DsaBossBattleScreen(
    boss: BossBattle,
    onDismiss: () -> Unit,
    onVictory: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val juice = rememberJuiceFeedback()
    val scope = rememberCoroutineScope()

    var currentPhase by remember { mutableIntStateOf(1) }
    var bossHp by remember { mutableIntStateOf(boss.maxHp) }
    val maxBossHp = remember { boss.maxHp }
    var learnerHearts by remember { mutableIntStateOf(3) }
    var timeRemaining by remember { mutableIntStateOf(boss.timeLimitSeconds) }
    var isBattleActive by remember { mutableStateOf(true) }
    var isVictory by remember { mutableStateOf(false) }
    var isDefeat by remember { mutableStateOf(false) }

    // Shake & Flash animations
    val bossShake = remember { Animatable(0f) }
    var bossFlashRed by remember { mutableStateOf(false) }
    var attackFloatingText by remember { mutableStateOf<String?>(null) }

    // Question State
    var questionIndex by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var isAnswerLocked by remember { mutableStateOf(false) }

    val questions = remember(boss.id) { getBossQuestions(boss.id) }
    val currentQuestion = questions[questionIndex % questions.size]

    // 1-second battle countdown timer
    LaunchedEffect(isBattleActive) {
        while (isBattleActive) {
            delay(1000)
            if (timeRemaining > 0) {
                timeRemaining--
            } else {
                isBattleActive = false
                isDefeat = true
                juice.onWrongAnswer()
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF190A23), Color(0xFF131028), Color(0xFF0B091A))
                )
            )
    ) {
        if (isVictory) {
            // ── Victory Screen ────────────────────────────────────────────
            BossVictoryView(
                boss = boss,
                onClaim = {
                    onVictory()
                    onDismiss()
                }
            )
        } else if (isDefeat) {
            // ── Defeat Screen ─────────────────────────────────────────────
            BossDefeatView(
                onRetry = {
                    bossHp = maxBossHp
                    learnerHearts = 3
                    timeRemaining = boss.timeLimitSeconds
                    currentPhase = 1
                    isDefeat = false
                    isBattleActive = true
                },
                onExit = onDismiss
            )
        } else {
            // ── Active Battle Arena ───────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Top HUD
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onDismiss) {
                        Text("✕", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }

                    // Timer Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (timeRemaining < 20) Color(0xFFFF4B4B) else Color(0xFF26204E))
                            .border(1.dp, Color.White.copy(0.2f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("⏳ ${timeRemaining}s", color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
                    }

                    // Learner Hearts
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (i in 1..3) {
                            Text(
                                text = if (i <= learnerHearts) "❤️" else "🖤",
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                // Boss Avatar & Health Bar Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(x = bossShake.value.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (bossFlashRed) Color(0xFF5A1010) else Color(0xFF26204E))
                        .border(1.5.dp, Color(0xFFFF9600).copy(0.6f), RoundedCornerShape(20.dp))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("👹", fontSize = 34.sp)
                        Column {
                            Text(
                                text = boss.bossName,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = boss.title,
                                color = Color(0xFFFF9600),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // HP Bar
                    val hpRatio = (bossHp.toFloat() / maxBossHp).coerceIn(0f, 1f)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("BOSS HEALTH", color = DuolingoSubtext, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("$bossHp / $maxBossHp HP", color = Color(0xFFFF4B4B), fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                        LinearProgressIndicator(
                            progress = { hpRatio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            color = Color(0xFFFF4B4B),
                            trackColor = Color(0xFF14122C)
                        )
                    }

                    // Phase Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFF9600).copy(0.2f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "⚔️ PHASE $currentPhase OF 3",
                            color = Color(0xFFFF9600),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    attackFloatingText?.let { damageTxt ->
                        Text(
                            text = damageTxt,
                            color = Color(0xFFFFC800),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                // Interactive Challenge Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF1A1636))
                        .border(1.dp, Color.White.copy(0.15f), RoundedCornerShape(20.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "QUESTION ${(questionIndex % questions.size) + 1} OF ${questions.size}",
                        color = Color(0xFF00CD9C),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )

                    Text(
                        text = currentQuestion.prompt,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        currentQuestion.options.forEachIndexed { idx, option ->
                            val isSelected = selectedOptionIndex == idx
                            val isCorrectChoice = idx == currentQuestion.correctIndex
                            val bg = when {
                                isAnswerLocked && isCorrectChoice -> Color(0xFF58CC02)
                                isAnswerLocked && isSelected -> Color(0xFFFF4B4B)
                                isSelected -> Color(0xFFFF9600)
                                else -> Color(0xFF26204E)
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(bg)
                                    .border(
                                        1.dp,
                                        if (isSelected) Color.White.copy(0.6f) else Color.Transparent,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable(enabled = !isAnswerLocked) {
                                        selectedOptionIndex = idx
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = option,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Strike Boss Button
                    Button(
                        onClick = {
                            if (!isAnswerLocked && selectedOptionIndex != null) {
                                isAnswerLocked = true
                                val isCorrect = selectedOptionIndex == currentQuestion.correctIndex
                                if (isCorrect) {
                                    juice.onCorrectAnswer()
                                    val damage = (maxBossHp / questions.size) + 10
                                    bossHp = (bossHp - damage).coerceAtLeast(0)
                                    attackFloatingText = "-$damage HP CRITICAL STRIKE! 💥"
                                    bossFlashRed = true

                                    scope.launch {
                                        bossShake.animateTo(12f, spring(stiffness = Spring.StiffnessHigh))
                                        bossShake.animateTo(-12f, spring(stiffness = Spring.StiffnessHigh))
                                        bossShake.animateTo(0f, spring(stiffness = Spring.StiffnessHigh))
                                        bossFlashRed = false
                                    }

                                    if (bossHp <= 0) {
                                        isVictory = true
                                        isBattleActive = false
                                        GameStateRepository.completeLesson(
                                            com.simats.duolingo.domain.model.DsaLesson(
                                                id = "boss_${boss.id}",
                                                unitId = "u1",
                                                index = 99,
                                                title = boss.bossName,
                                                subtitle = boss.subtitle,
                                                type = com.simats.duolingo.domain.model.LessonType.BOSS,
                                                exercises = emptyList(),
                                                xpReward = 60
                                            ),
                                            scoreAccuracy = 1f
                                        )
                                    } else {
                                        if (bossHp <= maxBossHp * 0.35f) currentPhase = 3
                                        else if (bossHp <= maxBossHp * 0.68f) currentPhase = 2
                                    }
                                } else {
                                    juice.onWrongAnswer()
                                    learnerHearts -= 1
                                    attackFloatingText = "MISSED! Boss countered! -1 Heart 💔"
                                    if (learnerHearts <= 0) {
                                        isDefeat = true
                                        isBattleActive = false
                                    }
                                }

                                scope.launch {
                                    delay(1200)
                                    if (isBattleActive) {
                                        selectedOptionIndex = null
                                        isAnswerLocked = false
                                        attackFloatingText = null
                                        questionIndex++
                                    }
                                }
                            }
                        },
                        enabled = selectedOptionIndex != null && !isAnswerLocked,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9600)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = if (isAnswerLocked) "EVALUATING..." else "STRIKE BOSS ⚔️",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// VICTORY VIEW
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun BossVictoryView(boss: BossBattle, onClaim: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Text(
            text = "👑 BOSS DEFEATED! 👑",
            color = Color(0xFFFFC800),
            fontSize = 24.sp,
            fontWeight = FontWeight.Black
        )

        Box(
            modifier = Modifier
                .size(130.dp)
                .clip(CircleShape)
                .background(Color(0xFFFF9600).copy(0.25f)),
            contentAlignment = Alignment.Center
        ) {
            Text("🎁", fontSize = 72.sp)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "${boss.bossName} Vanquished!",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "\"I have been defeated by algorithmic elegance!\"",
                color = Color(0xFFFFC800),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        // Rewards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF26204E))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("+60 XP", color = Color(0xFFFFC800), fontSize = 18.sp, fontWeight = FontWeight.Black)
                Text("Boss XP", color = DuolingoSubtext, fontSize = 11.sp)
            }
            Box(modifier = Modifier.width(1.dp).height(30.dp).background(Color.White.copy(0.2f)))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("+50 💎", color = Color(0xFF1CB0F6), fontSize = 18.sp, fontWeight = FontWeight.Black)
                Text("Gems Drop", color = DuolingoSubtext, fontSize = 11.sp)
            }
        }

        Button(
            onClick = onClaim,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF58CC02)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("CLAIM CHEST & ADVANCE", fontWeight = FontWeight.Black, fontSize = 15.sp)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// DEFEAT VIEW
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun BossDefeatView(onRetry: () -> Unit, onExit: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Text("💀 DEFEAT 💀", color = Color(0xFFFF4B4B), fontSize = 26.sp, fontWeight = FontWeight.Black)
        Text("The boss overpowered you this time!", color = DuolingoSubtext, fontSize = 14.sp)

        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9600)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("RETRY BATTLE", fontWeight = FontWeight.Black)
            }

            OutlinedButton(
                onClick = onExit,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("RETURN TO PATH", color = Color.White)
            }
        }
    }
}

private data class BossQuestion(val prompt: String, val options: List<String>, val correctIndex: Int, val explanation: String)

private fun getBossQuestions(bossId: String): List<BossQuestion> {
    return listOf(
        BossQuestion(
            prompt = "Which data structure guarantees O(1) direct element access by numerical index?",
            options = listOf("Linked List", "Array", "Binary Search Tree", "Queue"),
            correctIndex = 1,
            explanation = "Arrays provide direct address arithmetic base + i * size in O(1)."
        ),
        BossQuestion(
            prompt = "What is the time complexity of pushing an element onto an array-backed Stack with capacity?",
            options = listOf("O(1)", "O(n)", "O(log n)", "O(n²)"),
            correctIndex = 0,
            explanation = "Push increments top pointer and writes in constant O(1) time."
        ),
        BossQuestion(
            prompt = "In BFS traversal of a graph, what data structure manages the frontier of discovered vertices?",
            options = listOf("Stack", "Queue", "Priority Queue", "Hash Map"),
            correctIndex = 1,
            explanation = "BFS requires First-In, First-Out (FIFO) queue ordering."
        ),
        BossQuestion(
            prompt = "Which balanced search tree guarantees strictly O(log n) worst-case search, insert, and delete?",
            options = listOf("Binary Search Tree", "AVL Tree", "Array", "Trie"),
            correctIndex = 1,
            explanation = "AVL trees enforce height balance factor between -1 and +1."
        ),
        BossQuestion(
            prompt = "What is the space complexity of an Adjacency Matrix for a graph with V vertices?",
            options = listOf("O(V)", "O(E)", "O(V²)", "O(V + E)"),
            correctIndex = 2,
            explanation = "A V x V matrix takes V * V = O(V²) memory."
        ),
        BossQuestion(
            prompt = "In Union-Find with path compression and union by rank, what is the nearly-constant amortized time?",
            options = listOf("O(1)", "O(α(n))", "O(log n)", "O(n)"),
            correctIndex = 1,
            explanation = "Inverse Ackermann function α(n) grows so slowly it is practically <= 4."
        )
    )
}
