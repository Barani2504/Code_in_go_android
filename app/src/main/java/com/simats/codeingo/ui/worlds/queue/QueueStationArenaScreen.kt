package com.simats.codeingo.ui.worlds.queue

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.ui.components.Duolingo3DButton
import com.simats.codeingo.ui.components.Duolingo3DButtonStyle
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.worlds.array.KingdomGameSpeed
import kotlinx.coroutines.delay
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueStationArenaScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameManager = GameManager.instance
    val gemsCount by gameManager.gemsCount.collectAsState()

    var currentLevel by remember { mutableStateOf(QueueStationLevel.LEVEL_1) }
    var unlockedLevels by remember { mutableStateOf(setOf(1)) }
    var levelStars by remember { mutableStateOf(mutableMapOf(1 to 3)) }

    var hearts by remember { mutableIntStateOf(5) }
    var sessionXP by remember { mutableIntStateOf(0) }
    var speed by remember { mutableStateOf(KingdomGameSpeed.NORMAL) }

    var showTheoryCodex by remember { mutableStateOf(false) }
    var showHintSheet by remember { mutableStateOf(false) }
    var showVictoryModal by remember { mutableStateOf(false) }
    var showBossVictoryModal by remember { mutableStateOf(false) }

    fun handleLevelCompleted(lvl: QueueStationLevel) {
        val nextId = lvl.id + 1
        unlockedLevels = unlockedLevels + nextId
        levelStars[lvl.id] = 3
        sessionXP += lvl.xpReward
        gameManager.addXP(lvl.xpReward)
        gameManager.addGems(lvl.coinReward)

        if (lvl == QueueStationLevel.BOSS_BATTLE) {
            showBossVictoryModal = true
        } else {
            showVictoryModal = true
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Astral Railway Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF240E33), Color(0xFF13061C), Color(0xFF09020F))
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top HUD
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Exit", tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color.Black.copy(alpha = 0.45f)).border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp)).padding(horizontal = 6.dp, vertical = 3.dp)) {
                        Text(text = "❤️ $hearts", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color.Black.copy(alpha = 0.45f)).border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp)).padding(horizontal = 6.dp, vertical = 3.dp)) {
                        Text(text = "⭐ +$sessionXP", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color.Black.copy(alpha = 0.45f)).border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp)).padding(horizontal = 6.dp, vertical = 3.dp)) {
                        Text(text = "💎 $gemsCount", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFB946FA).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFFB946FA).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .clickable { speed = speed.next() }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(text = speed.label, fontSize = 10.5.sp, fontWeight = FontWeight.Black, color = Color(0xFFB946FA))
                    }

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFD700).copy(alpha = 0.2f))
                            .clickable { showHintSheet = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Lightbulb, contentDescription = "Hint", tint = Color(0xFFFFD700), modifier = Modifier.size(15.dp))
                    }
                }
            }

            // Level Track
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QueueStationLevel.entries.forEach { lvl ->
                    val isUnlocked = unlockedLevels.contains(lvl.id)
                    val isSelected = currentLevel == lvl

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                when {
                                    isSelected && lvl == QueueStationLevel.BOSS_BATTLE -> DuolingoRed
                                    isSelected -> Color(0xFFB946FA)
                                    isUnlocked -> Color(0xFF221133)
                                    else -> Color.Black.copy(alpha = 0.35f)
                                }
                            )
                            .border(1.dp, if (isSelected) Color.White else Color.White.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
                            .clickable(enabled = isUnlocked) { currentLevel = lvl }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = if (isUnlocked) lvl.iconEmoji else "🔒", fontSize = 12.sp)
                            Text(text = "L${lvl.id}", fontSize = 12.sp, fontWeight = FontWeight.Black, color = if (isSelected) Color.Black else Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Active Level Canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                when (currentLevel) {
                    QueueStationLevel.LEVEL_1 -> QueueLevel1Content(onComplete = { handleLevelCompleted(QueueStationLevel.LEVEL_1) })
                    QueueStationLevel.LEVEL_2 -> QueueLevel2Content(onComplete = { handleLevelCompleted(QueueStationLevel.LEVEL_2) })
                    QueueStationLevel.LEVEL_3 -> QueueLevel3Content(onComplete = { handleLevelCompleted(QueueStationLevel.LEVEL_3) })
                    QueueStationLevel.LEVEL_4 -> QueueLevel4Content(onComplete = { handleLevelCompleted(QueueStationLevel.LEVEL_4) })
                    QueueStationLevel.LEVEL_5 -> QueueLevel5Content(onComplete = { handleLevelCompleted(QueueStationLevel.LEVEL_5) }, onDeductHeart = { hearts = maxOf(0, hearts - 1) })
                    QueueStationLevel.LEVEL_6 -> QueueLevel6Content(onComplete = { handleLevelCompleted(QueueStationLevel.LEVEL_6) })
                    QueueStationLevel.BOSS_BATTLE -> QueueBossBattleContent(onDefeatBoss = { handleLevelCompleted(QueueStationLevel.BOSS_BATTLE) }, onDeductHeart = { hearts = maxOf(0, hearts - 1) })
                }
            }

            // Bottom Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 10.dp)
                    .padding(bottom = 40.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF221133))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                        .clickable { showTheoryCodex = true }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(text = "🎫 FIFO Station Codex", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.White)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF221133))
                            .clickable {
                                val current = currentLevel
                                currentLevel = QueueStationLevel.LEVEL_1
                                currentLevel = current
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Replay", tint = Color.White, modifier = Modifier.size(18.dp))
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .clickable {
                                val nextId = minOf(7, currentLevel.id + 1)
                                unlockedLevels = unlockedLevels + nextId
                                currentLevel = QueueStationLevel.fromId(nextId)
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Text(text = "Skip ⏭", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }
        }

        // Modals
        if (showVictoryModal) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.75f)), contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier.fillMaxWidth(0.85f).clip(RoundedCornerShape(24.dp)).background(Color(0xFF1E0E2B)).border(2.dp, Color(0xFFB946FA), RoundedCornerShape(24.dp)).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = "🎫 PASSENGERS DISPATCHED!", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFFB946FA))
                    Text(text = currentLevel.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Duolingo3DButton(
                        title = "NEXT TRAIN ➔",
                        style = Duolingo3DButtonStyle.GREEN,
                        onClick = {
                            showVictoryModal = false
                            val nextId = minOf(7, currentLevel.id + 1)
                            currentLevel = QueueStationLevel.fromId(nextId)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        if (showBossVictoryModal) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.75f)), contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier.fillMaxWidth(0.85f).clip(RoundedCornerShape(24.dp)).background(Color(0xFF1E0E2B)).border(2.dp, DuolingoGreen, RoundedCornerShape(24.dp)).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = "👑 GRIDLOCK BROKEN!", fontSize = 22.sp, fontWeight = FontWeight.Black, color = DuolingoGreen)
                    Text(text = "You cleared the rail platform and mastered FIFO Queues!", fontSize = 13.sp, color = SubtextGray, textAlign = TextAlign.Center)
                    Duolingo3DButton(
                        title = "COMPLETE STATION",
                        style = Duolingo3DButtonStyle.GREEN,
                        onClick = {
                            showBossVictoryModal = false
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        if (showTheoryCodex) {
            ModalBottomSheet(onDismissRequest = { showTheoryCodex = false }, containerColor = Color(0xFF1E0E2B)) {
                Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "🎫 Queue Station Codex (FIFO)", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFFB946FA))
                    Text(
                        text = "A Queue is a First-In, First-Out (FIFO) data structure. Like boarding a train, the first person to arrive at the FRONT is the first to leave, and new arrivals join at the REAR.\n\nKey Operations:\n- ENQUEUE(item): Adds to REAR in O(1)\n- DEQUEUE(): Removes from FRONT in O(1)\n- PEEK(): Inspects FRONT in O(1)\n\nQueues are essential for Breadth-First Search (BFS), asynchronous job processing, and printer spools.",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        if (showHintSheet) {
            ModalBottomSheet(onDismissRequest = { showHintSheet = false }, containerColor = Color(0xFF1E0E2B)) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "💡 Station Hint: ${currentLevel.title}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFFB946FA))
                    Text(
                        text = when (currentLevel) {
                            QueueStationLevel.LEVEL_1 -> "The FRONT person boards first! The REAR person arrived most recently."
                            QueueStationLevel.LEVEL_2 -> "ENQUEUE always adds to the REAR of the line."
                            QueueStationLevel.LEVEL_3 -> "DEQUEUE always takes from the FRONT of the line."
                            QueueStationLevel.LEVEL_4 -> "PEEK views the FRONT without removing anyone."
                            QueueStationLevel.LEVEL_5 -> "Whoever arrived first leaves first (FIFO)!"
                            QueueStationLevel.LEVEL_6 -> "Enqueue and Dequeue in order to fulfill the passenger boardings."
                            QueueStationLevel.BOSS_BATTLE -> "Spam DEQUEUE to release passengers before the platform overfills!"
                        },
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// 🚆 Levels Content
// ══════════════════════════════════════════════════════════════════
@Composable
private fun QueueLevel1Content(onComplete: () -> Unit) {
    val line = remember { mutableStateListOf("🧑 Alice", "👱 Bob", "🧔 Charlie") }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color(0xFF261036)).border(1.dp, Color(0xFFB946FA).copy(alpha = 0.4f), RoundedCornerShape(18.dp)).padding(16.dp)
        ) {
            Text(
                text = "🎫 Station Queue: Passengers wait in line. FRONT leaves first, new arrivals join REAR!",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }

        // Horizontal Queue Track
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            line.forEachIndexed { idx, name ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        modifier = Modifier
                            .size(76.dp, 56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (idx == 0) Color(0xFFB946FA) else Color(0xFF4A1A6D))
                            .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Text(
                        text = if (idx == 0) "FRONT 🚪" else if (idx == line.size - 1) "REAR ➡️" else "[$idx]",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = if (idx == 0) Color(0xFFB946FA) else SubtextGray
                    )
                }
            }
        }

        Duolingo3DButton(
            title = "🚪 DEQUEUE FRONT (Board Train)",
            style = Duolingo3DButtonStyle.GREEN,
            onClick = {
                if (line.isNotEmpty()) {
                    line.removeAt(0)
                    if (line.size == 1) onComplete()
                }
            },
            modifier = Modifier.fillMaxWidth(0.85f)
        )
    }
}

@Composable
private fun QueueLevel2Content(onComplete: () -> Unit) {
    val line = remember { mutableStateListOf("🧑 Alice") }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = "ENQUEUE adds new arrivals exclusively to the REAR.", fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Bold)

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            line.forEachIndexed { idx, name ->
                Box(
                    modifier = Modifier
                        .size(76.dp, 50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFB946FA)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        Duolingo3DButton(
            title = "➡️ ENQUEUE(\"Dave\")",
            style = Duolingo3DButtonStyle.AMBER,
            onClick = {
                line.add("👨 Dave")
                onComplete()
            },
            modifier = Modifier.fillMaxWidth(0.75f)
        )
    }
}

@Composable
private fun QueueLevel3Content(onComplete: () -> Unit) {
    val line = remember { mutableStateListOf("🧑 1st", "👱 2nd", "🧔 3rd") }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = "DEQUEUE dispatches the FRONT passenger.", fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Bold)

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            line.forEachIndexed { idx, name ->
                Box(
                    modifier = Modifier
                        .size(70.dp, 50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (idx == 0) DuolingoRed else Color(0xFF4A1A6D)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = name, fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.White)
                }
            }
        }

        Duolingo3DButton(
            title = "🚪 DEQUEUE()",
            style = Duolingo3DButtonStyle.WHITE,
            onClick = {
                if (line.isNotEmpty()) {
                    line.removeAt(0)
                    onComplete()
                }
            },
            modifier = Modifier.fillMaxWidth(0.7f)
        )
    }
}

@Composable
private fun QueueLevel4Content(onComplete: () -> Unit) {
    var isPeeked by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = "PEEK checks the FRONT passenger without dispatching them.", fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Bold)

        Box(
            modifier = Modifier
                .size(100.dp, 60.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (isPeeked) Color.Cyan else Color(0xFFB946FA)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🧑 VIP Guest", fontSize = 14.sp, fontWeight = FontWeight.Black, color = if (isPeeked) Color.Black else Color.White)
        }

        Duolingo3DButton(
            title = "👁️ PEEK FRONT",
            style = Duolingo3DButtonStyle.BLUE,
            onClick = {
                isPeeked = true
                onComplete()
            },
            modifier = Modifier.fillMaxWidth(0.7f)
        )
    }
}

@Composable
private fun QueueLevel5Content(onComplete: () -> Unit, onDeductHeart: () -> Unit) {
    var answered by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(
            text = "🔮 Arrival order: [A, B, C].\nWho will DEQUEUE first?",
            fontSize = 15.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            listOf("A", "B", "C").forEach { opt ->
                Box(
                    modifier = Modifier
                        .size(64.dp, 54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF261036))
                        .border(1.5.dp, Color(0xFFB946FA), RoundedCornerShape(14.dp))
                        .clickable(enabled = !answered) {
                            if (opt == "A") {
                                answered = true
                                onComplete()
                            } else {
                                onDeductHeart()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = opt, fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun QueueLevel6Content(onComplete: () -> Unit) {
    var step by remember { mutableIntStateOf(0) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = "⚡ Ticket Rush: Dispatch 2 passengers!", fontSize = 14.sp, color = Color(0xFFB946FA), fontWeight = FontWeight.Bold)

        Duolingo3DButton(
            title = "🚪 DEQUEUE PASSENGER (${step}/2)",
            style = Duolingo3DButtonStyle.GREEN,
            onClick = {
                step++
                if (step == 2) onComplete()
            },
            modifier = Modifier.fillMaxWidth(0.85f)
        )
    }
}

@Composable
private fun QueueBossBattleContent(onDefeatBoss: () -> Unit, onDeductHeart: () -> Unit) {
    val platform = remember { mutableStateListOf(1, 2, 3) }
    var bossHP by remember { mutableIntStateOf(100) }

    LaunchedEffect(Unit) {
        while (bossHP > 0 && platform.size < 6) {
            delay(1600)
            platform.add(Random.nextInt(4, 99))
            if (platform.size >= 6) {
                onDeductHeart()
                platform.clear()
                platform.addAll(listOf(1, 2, 3))
            }
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(text = "👾 GRIDLOCK EXPRESS BOSS! (${bossHP} HP)", fontSize = 16.sp, color = DuolingoRed, fontWeight = FontWeight.Black)

        Text(text = "Congestion: ${platform.size}/6 (Gridlock limit!)", fontSize = 12.sp, color = if (platform.size >= 5) DuolingoRed else Color(0xFFB946FA))

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            platform.forEach { _ ->
                Box(modifier = Modifier.size(36.dp, 44.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFB946FA)), contentAlignment = Alignment.Center) {
                    Text(text = "👤", fontSize = 16.sp)
                }
            }
        }

        Duolingo3DButton(
            title = "🚪 RAPID DEQUEUE DISPATCH!",
            style = Duolingo3DButtonStyle.GREEN,
            onClick = {
                if (platform.isNotEmpty()) {
                    platform.removeAt(0)
                    bossHP = maxOf(0, bossHP - 25)
                    if (bossHP <= 0) onDefeatBoss()
                }
            },
            modifier = Modifier.fillMaxWidth(0.85f)
        )
    }
}
