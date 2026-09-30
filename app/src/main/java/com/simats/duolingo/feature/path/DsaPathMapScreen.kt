package com.simats.duolingo.feature.path

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.duolingo.core.theme.*
import com.simats.duolingo.data.AppState
import com.simats.duolingo.data.repository.CourseRepository
import com.simats.duolingo.data.repository.GameStateRepository
import com.simats.duolingo.domain.model.*
import com.simats.duolingo.ui.components.PhoenixCreature
import kotlin.math.sin

/**
 * Duolingo-style zig-zag Path Map for the complete Data Structures & Algorithms Course.
 * Displays 11 units across metaphor worlds with sticky banners, pulsing nodes,
 * walking Phoenix mascot, and bottom sheet launcher.
 */
@Composable
fun DsaPathMapScreen(
    onStartLesson: (DsaLesson) -> Unit,
    onStartBoss: (BossBattle) -> Unit = {},
    onOpenSanctuary: () -> Unit,
    onOpenLanguagePicker: () -> Unit,
    onMenuClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val course = CourseRepository.dsaCourse
    var selectedLessonForSheet by remember { mutableStateOf<DsaLesson?>(null) }
    var selectedUnitForSheet by remember { mutableStateOf<DsaUnit?>(null) }
    val listState = rememberLazyListState()

    val totalXp by GameStateRepository.totalXp.collectAsState()
    val gems by GameStateRepository.gems.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DsaCourseBackgroundGradient)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ── Top Stats & Course Bar ──────────────────────────────────────
            DsaTopBar(
                totalXp = totalXp,
                gems = gems,
                dayStreak = AppState.dayStreak,
                hearts = AppState.heartsCount,
                activeStageId = AppState.activePhoenixStage,
                onOpenSanctuary = onOpenSanctuary,
                onOpenCoursePicker = onOpenLanguagePicker,
                onMenuClick = onMenuClick
            )

            // ── Scrollable Zig-Zag Units Path ──────────────────────────────
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                course.units.forEach { unit ->
                    // Sticky Unit Header Banner
                    item(key = "unit_header_${unit.id}") {
                        DsaUnitHeaderBanner(
                            unit = unit,
                            isUnlocked = GameStateRepository.isLessonUnlocked(unit.index, 1)
                        )
                    }

                    // Zig-zag lesson nodes inside unit
                    itemsIndexed(unit.lessons, key = { _, lesson -> lesson.id }) { index, lesson ->
                        val nodeState = GameStateRepository.getNodeState(unit.index, lesson)
                        val xOffset = sin(index * 1.35f) * 62f // Playful sine zig-zag

                        DsaPathNodeRow(
                            lesson = lesson,
                            unit = unit,
                            nodeState = nodeState,
                            xOffsetDp = xOffset,
                            isFirstAvailable = nodeState == NodeState.AVAILABLE,
                            onNodeClick = {
                                if (nodeState != NodeState.LOCKED) {
                                    selectedLessonForSheet = lesson
                                    selectedUnitForSheet = unit
                                }
                            }
                        )
                    }

                    // Divider between units
                    item(key = "unit_divider_${unit.id}") {
                        Spacer(Modifier.height(36.dp))
                    }
                }
            }
        }

        // ── Lesson Launcher Bottom Sheet ────────────────────────────────────
        selectedLessonForSheet?.let { lesson ->
            val unit = selectedUnitForSheet ?: course.units.first()
            DsaLessonLauncherSheet(
                lesson = lesson,
                unit = unit,
                onDismiss = { selectedLessonForSheet = null },
                onStart = {
                    val l = lesson
                    val u = unit
                    selectedLessonForSheet = null
                    if (l.type == LessonType.BOSS) {
                        onStartBoss(u.boss)
                    } else {
                        onStartLesson(l)
                    }
                }
            )
        }
    }
}

/* ================================================================== */
/*  TOP STATS & PHOENIX COMPANION HEADER                              */
/* ================================================================== */

@Composable
private fun DsaTopBar(
    totalXp: Int,
    gems: Int,
    dayStreak: Int,
    hearts: Int,
    activeStageId: Int,
    onOpenSanctuary: () -> Unit,
    onOpenCoursePicker: () -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFF14122C).copy(alpha = 0.96f),
        shadowElevation = 6.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Hamburger Menu Button
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2A2050))
                    .clickable(onClick = onMenuClick),
                contentAlignment = Alignment.Center
            ) {
                Text("☰", color = Color.White, fontSize = 16.sp)
            }

            // Course Switcher Pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF2A2050))
                    .clickable(onClick = onOpenCoursePicker)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("🔥", fontSize = 16.sp)
                Text("DSA", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Black)
                Text("▼", color = Color.White.copy(0.6f), fontSize = 10.sp)
            }

            // Streak Flame
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("🔥", fontSize = 15.sp)
                Text(
                    text = "$dayStreak",
                    color = Color(0xFFFF9600),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black
                )
            }

            // Gems
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("💎", fontSize = 15.sp)
                Text(
                    text = "$gems",
                    color = Color(0xFF1CB0F6),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black
                )
            }

            // Hearts Lives
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("❤️", fontSize = 15.sp)
                Text(
                    text = "$hearts",
                    color = Color(0xFFFF4B4B),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black
                )
            }

            // Phoenix Companion Mini-Card
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFFF9600), Color(0xFFFF4B4B))
                        )
                    )
                    .clickable(onClick = onOpenSanctuary)
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text("🐣", fontSize = 14.sp)
                Text(
                    text = "ST $activeStageId",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

/* ================================================================== */
/*  UNIT STICKY HEADER BANNER                                         */
/* ================================================================== */

@Composable
private fun DsaUnitHeaderBanner(
    unit: DsaUnit,
    isUnlocked: Boolean,
    modifier: Modifier = Modifier
) {
    val bannerBg = if (isUnlocked) {
        Brush.horizontalGradient(listOf(Color(unit.themeColorHex), Color(unit.themeDarkColorHex)))
    } else {
        Brush.horizontalGradient(listOf(Color(0xFF35304C), Color(0xFF232034)))
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(bannerBg)
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(0.3f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "UNIT ${unit.index} • ${unit.difficulty.label.uppercase()}",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }

            Text(
                text = if (isUnlocked) "🔥 IN PROGRESS" else "🔒 LOCKED",
                color = Color.White.copy(0.85f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = unit.title,
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black
        )

        Text(
            text = "World: ${unit.worldTheme}",
            color = Color.White.copy(0.88f),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/* ================================================================== */
/*  ZIG-ZAG PATH NODE WITH COMPANION POSITIONING                      */
/* ================================================================== */

@Composable
private fun DsaPathNodeRow(
    lesson: DsaLesson,
    unit: DsaUnit,
    nodeState: NodeState,
    xOffsetDp: Float,
    isFirstAvailable: Boolean,
    onNodeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        // Node with horizontal sine wave offset
        Box(
            modifier = Modifier.offset(x = xOffsetDp.dp),
            contentAlignment = Alignment.Center
        ) {
            // Pulsing halo glow for available node
            if (nodeState == NodeState.AVAILABLE) {
                val inf = rememberInfiniteTransition(label = "pulse")
                val pulseScale by inf.animateFloat(
                    initialValue = 0.95f,
                    targetValue = 1.25f,
                    animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
                    label = "scale"
                )
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(Color(unit.themeColorHex).copy(alpha = 0.35f))
                        .blur(8.dp)
                )
            }

            // Main Circular Node Button
            val isBoss = lesson.type == LessonType.BOSS
            val nodeSize = if (isBoss) 82.dp else 68.dp

            val nodeBgColor = when (nodeState) {
                NodeState.LOCKED -> Color(0xFF38324F)
                NodeState.AVAILABLE -> Color(unit.themeColorHex)
                NodeState.COMPLETED -> Color(0xFFFFD700)
                NodeState.LEGENDARY -> Color(0xFFA855F7)
                NodeState.IN_PROGRESS -> Color(unit.themeColorHex)
            }

            val iconEmoji = when {
                isBoss -> "💀"
                lesson.type == LessonType.CHEST -> "🎁"
                lesson.type == LessonType.CHECKPOINT -> "🛡️"
                nodeState == NodeState.COMPLETED -> "👑"
                nodeState == NodeState.LEGENDARY -> "⭐"
                nodeState == NodeState.LOCKED -> "🔒"
                else -> "★"
            }

            Box(
                modifier = Modifier
                    .size(nodeSize)
                    .clip(CircleShape)
                    .background(nodeBgColor)
                    .border(
                        width = if (isBoss) 4.dp else 3.dp,
                        color = if (nodeState == NodeState.LOCKED) Color(0xFF4C456A) else Color.White,
                        shape = CircleShape
                    )
                    .shadow(elevation = 6.dp, shape = CircleShape)
                    .clickable(enabled = nodeState != NodeState.LOCKED, onClick = onNodeClick),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iconEmoji,
                    fontSize = if (isBoss) 30.sp else 24.sp,
                    color = Color.White
                )
            }

            // Walking Phoenix companion positioned beside the active node!
            if (isFirstAvailable) {
                val flipDirection = if (xOffsetDp > 0) -88.dp else 88.dp
                Box(
                    modifier = Modifier
                        .offset(x = flipDirection, y = (-12).dp)
                        .size(68.dp)
                ) {
                    PhoenixCreature(
                        stage = AppState.activePhoenixStage.toFloat(),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

/* ================================================================== */
/*  LESSON LAUNCHER BOTTOM SHEET                                      */
/* ================================================================== */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DsaLessonLauncherSheet(
    lesson: DsaLesson,
    unit: DsaUnit,
    onDismiss: () -> Unit,
    onStart: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1B1B3A),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                Modifier
                    .padding(vertical = 12.dp)
                    .width(42.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(0.3f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Unit & Lesson Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(unit.themeColorHex).copy(0.2f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        "UNIT ${unit.index} • LESSON ${lesson.index}",
                        color = Color(unit.themeColorHex),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("💎 +5", color = Color(0xFF1CB0F6), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("⚡ +${lesson.xpReward} XP", color = Color(0xFFFFD700), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Title & Metaphor Subtitle
            Text(
                text = lesson.title,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )

            Text(
                text = lesson.subtitle,
                color = Color.White.copy(0.75f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF26204E))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("EXERCISES", color = Color.White.copy(0.6f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("${lesson.exercises.size} Interactive", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Black)
                    }
                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color.White.copy(0.2f)))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("DIFFICULTY", color = Color.White.copy(0.6f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(unit.difficulty.label, color = Color(unit.themeColorHex), fontSize = 13.sp, fontWeight = FontWeight.Black)
                    }
                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color.White.copy(0.2f)))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("HEARTS", color = Color.White.copy(0.6f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("❤️ 5 Lives", color = Color(0xFFFF4B4B), fontSize = 13.sp, fontWeight = FontWeight.Black)
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            // Big Juicy START Button
            Button(
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(unit.themeColorHex)
                )
            ) {
                Text(
                    text = if (lesson.type == LessonType.BOSS) "FIGHT BOSS ⚔️" else "START LESSON",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }

            Spacer(Modifier.height(10.dp))
        }
    }
}
