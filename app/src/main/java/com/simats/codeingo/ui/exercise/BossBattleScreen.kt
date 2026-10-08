package com.simats.codeingo.ui.exercise

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.data.model.DSABossSpec
import com.simats.codeingo.data.repository.CourseRepository
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.ui.components.AppButton
import com.simats.codeingo.ui.components.AppButtonStyle
import com.simats.codeingo.ui.components.ProgressBarAnimated
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DuolingoBlue
import com.simats.codeingo.ui.theme.DuolingoBlueDark
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoGreenDark
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.DuolingoRedDark
import com.simats.codeingo.ui.theme.InputBorder
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.liquidGlassCard
import com.simats.codeingo.ui.theme.pressScale
import com.simats.codeingo.ui.theme.tilt3D
import kotlinx.coroutines.delay

@Composable
fun BossBattleScreen(
    bossId: String,
    onVictory: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val boss = remember(bossId) {
        CourseRepository.getBossById(bossId) ?: DSABossSpec(
            id = bossId,
            name = "The Great Jumble",
            title = "Chaos Golem of the Warehouse",
            worldTheme = "The Hatchery Warehouse",
            avatarEmoji = "🥚👹",
            bossHp = 100,
            quote = "All your eggs will crack in my bottomless O(n) mire!",
            defeatQuote = "No! Your tidy O(1) structures have brought order!",
            targetPhoenixStageAwarded = 3
        )
    }

    var bossHp by remember { mutableIntStateOf(boss.bossHp) }
    var learnerHearts by remember { mutableIntStateOf(3) }
    var timeRemaining by remember { mutableIntStateOf(boss.timeLimitSeconds) }
    var currentPhase by remember { mutableIntStateOf(1) }
    var isVictory by remember { mutableStateOf(false) }
    var isDefeat by remember { mutableStateOf(false) }

    // Boss Battle Questions
    val questions = remember {
        listOf(
            Triple("What is the worst-case retrieval time in an unorganized heap?", listOf("O(1)", "O(log n)", "O(n)", "O(n²)"), 2),
            Triple("Which sorting algorithm achieves O(n log n) guaranteed worst-case?", listOf("Bubble Sort", "Merge Sort", "Insertion Sort", "Selection Sort"), 1),
            Triple("What data structure powers Dijkstra's shortest path algorithm efficiently?", listOf("Stack", "Min-Heap", "Queue", "Array"), 1)
        )
    }

    var questionIdx by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var isAnswerChecked by remember { mutableStateOf(false) }

    // Countdown Timer
    LaunchedEffect(isVictory, isDefeat) {
        if (!isVictory && !isDefeat) {
            while (timeRemaining > 0) {
                delay(1000)
                timeRemaining -= 1
            }
            if (bossHp > 0 && !isVictory) {
                isDefeat = true
            }
        }
    }

    val transition = rememberInfiniteTransition(label = "BossBreathing")
    val bossOffsetY by transition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BossY"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1E0A24), DarkBackground)
                )
            )
    ) {
        if (isVictory) {
            val unitId = remember(bossId) {
                bossId.removePrefix("boss_unit_").toIntOrNull() ?: 1
            }
            val starsEarned = remember(learnerHearts) {
                when (maxOf(0, 3 - learnerHearts)) {
                    0 -> 5
                    1 -> 3
                    2 -> 1
                    else -> 0
                }
            }
            val bossXP = unitId * 50

            // Full 3D Interactive Egg Cracking & Phoenix Ascension Celebration
            com.simats.codeingo.ui.phoenix.PhoenixEggHatch3DView(
                unitId = unitId,
                levelNumber = boss.targetPhoenixStageAwarded,
                totalLevelsInUnit = 6,
                isBoss = true,
                xpEarned = bossXP,
                starsEarned = starsEarned,
                accuracyPercentage = 100,
                onContinue = {
                    GameManager.instance.addStars(starsEarned)
                    GameManager.instance.awardBossVictory(boss, unitId)
                    GameManager.instance.completeLessonAndExtendStreak()
                    onVictory()
                },
                onFinish = {
                    GameManager.instance.addStars(starsEarned)
                    GameManager.instance.awardBossVictory(boss, unitId)
                    GameManager.instance.completeLessonAndExtendStreak()
                    onVictory()
                },
                onUpgradePhoenixNextUnit = {
                    GameManager.instance.addStars(starsEarned)
                    GameManager.instance.awardBossVictory(boss, unitId)
                    GameManager.instance.completeLessonAndExtendStreak()
                    onVictory()
                }
            )
        } else if (isDefeat) {
            // Defeat View
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "💔", fontSize = 72.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "DEFEATED",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = DuolingoRed
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = boss.quote,
                    fontSize = 14.sp,
                    color = SubtextGray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(40.dp))
                AppButton(
                    title = "TRY AGAIN",
                    style = AppButtonStyle.ACTION_BLUE,
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                // Battle HUD Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Exit", tint = SubtextGray)
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Timer
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "⏱️", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${timeRemaining}s",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = if (timeRemaining <= 15) DuolingoRed else LocalDynamicThemeColors.current.textPrimary
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // 3 Learner Hearts
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (i in 1..3) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = if (i <= learnerHearts) DuolingoRed else SubtextGray.copy(alpha = 0.3f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Scrollable Arena Section
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    // Boss Showcase
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .offset(y = bossOffsetY.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .background(DuolingoRed.copy(alpha = 0.2f))
                        )
                        Text(text = boss.avatarEmoji, fontSize = 64.sp)
                    }

                    Text(
                        text = boss.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = LocalDynamicThemeColors.current.textPrimary
                    )

                    Text(
                        text = boss.title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SubtextGray
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Boss HP Bar
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "BOSS HP", fontSize = 11.sp, fontWeight = FontWeight.Black, color = DuolingoRed)
                            Text(text = "$bossHp / ${boss.bossHp}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LocalDynamicThemeColors.current.textPrimary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        ProgressBarAnimated(
                            progress = bossHp.toFloat() / boss.bossHp.toFloat(),
                            height = 12.dp,
                            barColor = DuolingoRed,
                            trackColor = Color(0xFF281414)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Boss Question
                    val curQ = questions[questionIdx % questions.size]
                    Text(
                        text = curQ.first,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = LocalDynamicThemeColors.current.textPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Options
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        curQ.second.forEachIndexed { idx, opt ->
                            val isSelected = selectedOption == idx
                            val shape = RoundedCornerShape(14.dp)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .pressScale(0.96f)
                                    .clip(shape)
                                    .background(if (isSelected) DuolingoBlue.copy(alpha = 0.22f) else CardBackground)
                                    .border(if (isSelected) 2.dp else 1.2.dp, if (isSelected) DuolingoBlue else InputBorder, shape)
                                    .clickable { selectedOption = idx }
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = opt,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LocalDynamicThemeColors.current.textPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Attack Action 3D Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    val canAttack = selectedOption != null
                    AppButton(
                        title = "⚔️ ATTACK BOSS",
                        style = if (canAttack) AppButtonStyle.DANGER_CRIMSON else AppButtonStyle.DISABLED,
                        isEnabled = canAttack,
                        onClick = {
                            if (selectedOption != null) {
                                val curQ = questions[questionIdx % questions.size]
                                if (selectedOption == curQ.third) {
                                    // Hit boss for 35 HP
                                    val newHp = maxOf(0, bossHp - 35)
                                    bossHp = newHp
                                    if (newHp <= 0) {
                                        isVictory = true
                                    } else {
                                        questionIdx += 1
                                        selectedOption = null
                                    }
                                } else {
                                    // Learner loses a heart
                                    learnerHearts -= 1
                                    if (learnerHearts <= 0) {
                                        isDefeat = true
                                    } else {
                                        questionIdx += 1
                                        selectedOption = null
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    )
                }
            }
        }
    }
}
