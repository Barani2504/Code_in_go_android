package com.simats.codeingo.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.data.model.DSA5ChapterCourse
import com.simats.codeingo.data.model.DashboardTab
import com.simats.codeingo.data.model.LessonNodeItem
import com.simats.codeingo.data.model.UnitCharacterType
import com.simats.codeingo.data.model.UnitModel
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.domain.LocalizationManager
import com.simats.codeingo.domain.PhoenixEmotionManager
import com.simats.codeingo.navigation.BottomNavBar
import com.simats.codeingo.ui.leaderboards.LeaderboardsScreen
import com.simats.codeingo.ui.phoenix.PhoenixAnimatedMascotView
import com.simats.codeingo.ui.phoenix.PhoenixAtmosphericBackgroundView
import com.simats.codeingo.ui.phoenix.PhoenixEmotionPickerSheet
import com.simats.codeingo.ui.phoenix.PhoenixEvolutionCelebrationScreen
import com.simats.codeingo.ui.phoenix.PhoenixMascotPose
import com.simats.codeingo.ui.profile.ProfileScreen
import com.simats.codeingo.ui.practice.PracticeHubScreen
import com.simats.codeingo.ui.quests.QuestsScreen
import com.simats.codeingo.ui.shop.ShopScreen
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.AmberGoldDark
import com.simats.codeingo.ui.visualizer.VisualizerScreen
import com.simats.codeingo.ui.worlds.array.ArrayKingdomArenaScreen
import com.simats.codeingo.ui.worlds.linkedlist.LinkedListRoadArenaScreen
import com.simats.codeingo.ui.worlds.queue.QueueStationArenaScreen
import com.simats.codeingo.ui.worlds.stack.StackTowerArenaScreen
import com.simats.codeingo.ui.worlds.tree.BinaryTreeForestArenaScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

/**
 * MainDashboardScreen faithfully synchronized with iOS MainDashboardView.swift (commit 2b2c2ab).
 * Features:
 * - Master 5-Chapter DSA Course (Array, Linked List, Stack, Queue, Tree) with 25 levels & 5 Boss Stages
 * - 3D Interactive Worlds Section (Array Kingdom, Stack Tower, Queue Station, LinkedList Road, Binary Tree Forest)
 * - Floating Liquid Glass Top Stats Header (Menu, PhoenixDynamicLogoView, Streak, Stars, XP, Hearts)
 * - 3D Level Nodes with animated active target aura, START tooltip, and locked level popover
 * - Dynamic Companion Mascot floating next to the active node
 * - Slide-out Side Drawer Menu
 * - FullScreen & Bottom Sheets for Emotion Picker, Streak, and Chapter Guidebook
 * - Docked 4-Tab Liquid Glass Bottom Navigation Bar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboardScreen(
    onStartLesson: (unitId: Int, levelNumber: Int, totalLevelsInUnit: Int, isBoss: Boolean) -> Unit,
    onStartBoss: (bossId: String) -> Unit,
    onOpenPhoenixSanctuary: () -> Unit,
    onOpenSettings: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameManager = GameManager.instance
    val localizationManager = LocalizationManager.instance
    val emotionManager = PhoenixEmotionManager.instance

    val totalXP by gameManager.totalXP.collectAsState()
    val totalStars by gameManager.totalStars.collectAsState()
    val streakDays by gameManager.streakDays.collectAsState()
    val isStreakLostPendingRestore by gameManager.isStreakLostPendingRestore.collectAsState()
    val savedStreakDays by gameManager.savedStreakDays.collectAsState()
    val heartsCount by gameManager.heartsCount.collectAsState()
    val unlockedLevels by gameManager.unlockedLevelIndices.collectAsState()
    val showEvolutionModal by gameManager.showEvolutionModal.collectAsState()
    val evolutionFromStage by gameManager.evolutionFromStage.collectAsState()
    val evolutionToStage by gameManager.evolutionToStage.collectAsState()

    var selectedTab by remember { mutableStateOf(DashboardTab.LEARN) }
    var showSideMenu by remember { mutableStateOf(false) }
    var showEmotionPickerSheet by remember { mutableStateOf(false) }
    var showStreakSheet by remember { mutableStateOf(false) }
    var showHeartsSheet by remember { mutableStateOf(false) }
    var guideSelectedUnit by remember { mutableStateOf<UnitModel?>(null) }
    var activeWorldArena by remember { mutableStateOf<String?>(null) }

    // Active Node & Locked Selection State
    var activeLevelIndex by remember { mutableIntStateOf(1) }
    var selectedLockedNodeId by remember { mutableStateOf<Int?>(null) }

    // Keep activeLevelIndex in sync whenever a stage is unlocked (mirrors iOS onComplete)
    LaunchedEffect(unlockedLevels) {
        val maxUnlocked = unlockedLevels.maxOrNull() ?: 1
        if (maxUnlocked > activeLevelIndex) {
            activeLevelIndex = maxUnlocked
            selectedLockedNodeId = null
        }
    }

    // 5-Minute Heart Regeneration Timer
    var heartTimerRemainingSeconds by remember { mutableIntStateOf(300) }
    LaunchedEffect(heartsCount) {
        if (heartsCount < 10) {
            while (isActive && heartsCount < 10) {
                delay(1000)
                if (heartTimerRemainingSeconds > 0) {
                    heartTimerRemainingSeconds--
                } else {
                    gameManager.regenerateHeart()
                    heartTimerRemainingSeconds = 300
                }
            }
        }
    }

    val heartTimerStr = if (heartsCount < 10) {
        val mins = heartTimerRemainingSeconds / 60
        val secs = heartTimerRemainingSeconds % 60
        String.format("%02d:%02d", mins, secs)
    } else null

    // 5 Master Chapters
    val masterUnits = remember {
        listOf(
            UnitModel(
                id = 1, sectionNumber = 1, unitNumber = 1,
                titleKey = "unit_1_title", titleDefault = "🔥 Chapter 1: Array — The Primordial Ember Highway",
                themeColor = Color(0xFFFF6E0F),
                themeDarkColor = Color(0xFFC84105),
                nodes = listOf(
                    LessonNodeItem(1, 1, "square.grid", 0.dp, "Stage 1: Meet the Array"),
                    LessonNodeItem(2, 2, "magnifyingglass", (-45).dp, "Stage 2: Find the Element"),
                    LessonNodeItem(3, 3, "arrow.right", 45.dp, "Stage 3: Build the Array"),
                    LessonNodeItem(4, 4, "gauge", (-20).dp, "Stage 4: Array Challenge"),
                    LessonNodeItem(5, 5, "flame", 0.dp, "Stage 5: Boss: The Highway Race", isBoss = true)
                ),
                characterType = UnitCharacterType.PHOENIX,
                worldTheme = "The Primordial Ember Highway"
            ),
            UnitModel(
                id = 2, sectionNumber = 2, unitNumber = 2,
                titleKey = "unit_2_title", titleDefault = "🔗 Chapter 2: Linked List — The Hatchling's Quest",
                themeColor = Color(0xFFF02D4B),
                themeDarkColor = Color(0xFFB41432),
                nodes = listOf(
                    LessonNodeItem(6, 6, "link", 0.dp, "Stage 1: Meet the Nodes"),
                    LessonNodeItem(7, 7, "arrow.right", 45.dp, "Stage 2: Follow the Chain"),
                    LessonNodeItem(8, 8, "puzzlepiece", (-45).dp, "Stage 3: Build the Chain"),
                    LessonNodeItem(9, 9, "plus", 20.dp, "Stage 4: Insert a Node"),
                    LessonNodeItem(10, 10, "flame", 0.dp, "Stage 5: Boss: The Broken Chain", isBoss = true)
                ),
                characterType = UnitCharacterType.PHOENIX,
                worldTheme = "The Hatchling's Quest"
            ),
            UnitModel(
                id = 3, sectionNumber = 3, unitNumber = 3,
                titleKey = "unit_3_title", titleDefault = "🥞 Chapter 3: Stack — The Solar Ascent Tower",
                themeColor = Color(0xFFFFA500),
                themeDarkColor = Color(0xFFD26E00),
                nodes = listOf(
                    LessonNodeItem(11, 11, "stack", 0.dp, "Stage 1: Stack Basics (LIFO)"),
                    LessonNodeItem(12, 12, "arrow.down", (-45).dp, "Stage 2: Push to Top"),
                    LessonNodeItem(13, 13, "arrow.up", 45.dp, "Stage 3: Pop from Top"),
                    LessonNodeItem(14, 14, "parentheses", (-20).dp, "Stage 4: Stack Sequences"),
                    LessonNodeItem(15, 15, "flame", 0.dp, "Stage 5: Boss: Escape the Tower", isBoss = true)
                ),
                characterType = UnitCharacterType.PHOENIX,
                worldTheme = "The Solar Ascent Tower"
            ),
            UnitModel(
                id = 4, sectionNumber = 4, unitNumber = 4,
                titleKey = "unit_4_title", titleDefault = "🎫 Chapter 4: Queue — The Astral Rebirth Line",
                themeColor = Color(0xFFB946FA),
                themeDarkColor = Color(0xFF8228BE),
                nodes = listOf(
                    LessonNodeItem(16, 16, "person.3", 0.dp, "Stage 1: Understand FIFO"),
                    LessonNodeItem(17, 17, "arrow.left", 45.dp, "Stage 2: Enqueue to Rear"),
                    LessonNodeItem(18, 18, "arrow.right", (-45).dp, "Stage 3: Dequeue from Front"),
                    LessonNodeItem(19, 19, "clock", 20.dp, "Stage 4: Queue Interleaving"),
                    LessonNodeItem(20, 20, "flame", 0.dp, "Stage 5: Boss: Ticket Rush", isBoss = true)
                ),
                characterType = UnitCharacterType.PHOENIX,
                worldTheme = "The Astral Rebirth Line"
            ),
            UnitModel(
                id = 5, sectionNumber = 5, unitNumber = 5,
                titleKey = "unit_5_title", titleDefault = "👑 Chapter 5: Tree — The Immortal Phoenix Kingdom",
                themeColor = Color(0xFFFF4623),
                themeDarkColor = Color(0xFFC31E0A),
                nodes = listOf(
                    LessonNodeItem(21, 21, "crown", 0.dp, "Stage 1: Find the Root"),
                    LessonNodeItem(22, 22, "leaf", (-45).dp, "Stage 2: Parent and Child"),
                    LessonNodeItem(23, 23, "wind", 45.dp, "Stage 3: Find the Leaves"),
                    LessonNodeItem(24, 24, "branch", (-20).dp, "Stage 4: Tree Traversals"),
                    LessonNodeItem(25, 25, "flame", 0.dp, "Stage 5: Boss: Save the Kingdom", isBoss = true)
                ),
                characterType = UnitCharacterType.PHOENIX,
                worldTheme = "The Immortal Phoenix Kingdom"
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070B12))
    ) {
        PhoenixAtmosphericBackgroundView()

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                BottomNavBar(
                    selectedTab = selectedTab,
                    onTabSelected = { tab ->
                        selectedTab = tab
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = paddingValues.calculateBottomPadding())
            ) {
                when (selectedTab) {
                    DashboardTab.LEARN -> {
                        // Learn Tab: Curriculum Path with Floating Header
                        Box(modifier = Modifier.fillMaxSize()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Spacing for floating top stats header
                                Spacer(modifier = Modifier.height(72.dp))

                                // 1. 3D INTERACTIVE WORLDS CAROUSEL
                                InteractiveGamesSection(
                                    onOpenWorld = { worldKey ->
                                        activeWorldArena = worldKey
                                    }
                                )

                                Spacer(modifier = Modifier.height(18.dp))

                                // 2. 5 CHAPTER UNITS PATH
                                masterUnits.forEach { unit ->
                                    val isUnitOpen = unit.id == 1 || (unit.nodes.firstOrNull()?.let { unlockedLevels.contains(it.levelNumber) } ?: false)

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        // Unit Section Banner
                                        UnitSectionBanner(
                                            unit = unit,
                                            isUnlocked = isUnitOpen,
                                            onGuidebookClick = { guideSelectedUnit = unit }
                                        )

                                        // Winding 3D Level Nodes
                                        unit.nodes.forEach { node ->
                                            val isNodeUnlocked = isUnitOpen && unlockedLevels.contains(node.levelNumber)
                                            val isActiveTarget = isUnitOpen && (node.levelNumber == activeLevelIndex)
                                            val isLockedSelected = selectedLockedNodeId == node.id

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                LessonNodeButton(
                                                    node = node,
                                                    unit = unit,
                                                    isUnlocked = isNodeUnlocked,
                                                    isActiveTarget = isActiveTarget,
                                                    isLockedSelected = isLockedSelected,
                                                    xOffset = if (isActiveTarget) 0.dp else node.xOffset * 0.45f,
                                                    onNodeClick = {
                                                        if (isNodeUnlocked) {
                                                            activeLevelIndex = node.levelNumber
                                                            selectedLockedNodeId = null
                                                        } else {
                                                            selectedLockedNodeId = node.id
                                                        }
                                                    },
                                                    onStartClick = {
                                                        if (node.isBoss) {
                                                            onStartBoss(node.bossSpec?.id ?: "boss_unit_${unit.id}")
                                                        } else {
                                                            onStartLesson(unit.id, node.levelNumber, 5, false)
                                                        }
                                                    }
                                                )

                                                // Companion Mascot Animation next to Active Target Node
                                                if (isActiveTarget) {
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    PhoenixAnimatedMascotView(
                                                        pose = PhoenixMascotPose.Walking,
                                                        modifier = Modifier.size(72.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                // Grand Master Journey Milestone Card
                                GrandMasterJourneyCard(
                                    completedCount = unlockedLevels.size,
                                    totalCount = 25,
                                    onOpenSanctuary = onOpenPhoenixSanctuary
                                )

                                Spacer(modifier = Modifier.height(40.dp))
                            }

                            // Floating Liquid Glass Top Stats Header (Docked to Top)
                            TopStatsHeader(
                                streakDays = streakDays,
                                isStreakPendingRestore = isStreakLostPendingRestore,
                                savedStreakDays = savedStreakDays,
                                totalStars = totalStars,
                                totalXP = totalXP,
                                heartsCount = heartsCount,
                                heartTimerString = heartTimerStr,
                                onMenuClick = { showSideMenu = true },
                                onPhoenixClick = { showEmotionPickerSheet = true },
                                onStreakClick = { showStreakSheet = true },
                                onHeartsClick = { showHeartsSheet = true }
                            )
                        }
                    }

                    DashboardTab.VISUALIZER -> {
                        VisualizerScreen(
                            onOpenArrayKingdom = { activeWorldArena = "array" },
                            onOpenStackTower = { activeWorldArena = "stack" },
                            onOpenQueueStation = { activeWorldArena = "queue" },
                            onOpenLinkedListRoad = { activeWorldArena = "linkedlist" },
                            onOpenBinaryTreeForest = { activeWorldArena = "tree" },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    DashboardTab.PRACTICE -> {
                        PracticeHubScreen(
                            onOpenArrayKingdom = { activeWorldArena = "array" },
                            onOpenStackTower = { activeWorldArena = "stack" },
                            onOpenQueueStation = { activeWorldArena = "queue" },
                            onOpenLinkedListRoad = { activeWorldArena = "linkedlist" },
                            onOpenBinaryTreeForest = { activeWorldArena = "tree" },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    DashboardTab.LEADERBOARDS -> {
                        LeaderboardsScreen(
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    DashboardTab.PROFILE -> {
                        ProfileScreen(
                            onOpenSettings = onOpenSettings,
                            onLogout = onLogout,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    DashboardTab.QUESTS -> {
                        QuestsScreen(modifier = Modifier.fillMaxSize())
                    }

                    DashboardTab.SHOP -> {
                        ShopScreen(modifier = Modifier.fillMaxSize())
                    }

                    else -> {
                        VisualizerScreen(
                            onOpenArrayKingdom = { onStartLesson(1, 1, 5, false) },
                            onOpenStackTower = { onStartLesson(3, 11, 5, false) },
                            onOpenQueueStation = { onStartLesson(4, 16, 5, false) },
                            onOpenLinkedListRoad = { onStartLesson(2, 6, 5, false) },
                            onOpenBinaryTreeForest = { onStartLesson(5, 21, 5, false) },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        // Side Drawer Menu
        AnimatedVisibility(
            visible = showSideMenu,
            enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { showSideMenu = false }
            ) {
                SideMenuDrawer(
                    selectedTab = selectedTab,
                    onTabSelected = { tab ->
                        selectedTab = tab
                        showSideMenu = false
                    },
                    onMoreClick = {
                        showSideMenu = false
                        onOpenSettings()
                    }
                )
            }
        }

        // Phoenix Emotion Picker Bottom Sheet
        if (showEmotionPickerSheet) {
            PhoenixEmotionPickerSheet(
                onDismiss = { showEmotionPickerSheet = false }
            )
        }

        // Chapter Guidebook Modal Sheet
        guideSelectedUnit?.let { unit ->
            ModalBottomSheet(
                onDismissRequest = { guideSelectedUnit = null },
                containerColor = Color(0xFF0F1420)
            ) {
                ChapterGuidebookSheet(
                    unit = unit,
                    onDismiss = { guideSelectedUnit = null }
                )
            }
        }

        // Streak Modal Sheet
        if (showStreakSheet) {
            ModalBottomSheet(
                onDismissRequest = { showStreakSheet = false },
                containerColor = Color(0xFF0F1420)
            ) {
                StreakInfoSheet(
                    streakDays = streakDays,
                    isStreakPendingRestore = isStreakLostPendingRestore,
                    savedStreakDays = savedStreakDays,
                    onRestoreStreak = {
                        gameManager.restoreStreak()
                        showStreakSheet = false
                    },
                    onDismiss = { showStreakSheet = false }
                )
            }
        }

        // Hearts Modal Sheet
        if (showHeartsSheet) {
            ModalBottomSheet(
                onDismissRequest = { showHeartsSheet = false },
                containerColor = Color(0xFF0F1420)
            ) {
                HeartsInfoSheet(
                    heartsCount = heartsCount,
                    timerString = heartTimerStr,
                    onRefillHearts = {
                        gameManager.refillHearts()
                        showHeartsSheet = false
                    },
                    onDismiss = { showHeartsSheet = false }
                )
            }
        }

        // Phoenix Evolution Fullscreen Celebration Modal
        if (showEvolutionModal) {
            Dialog(
                onDismissRequest = { gameManager.dismissEvolutionModal() },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                PhoenixEvolutionCelebrationScreen(
                    fromStageId = evolutionFromStage,
                    toStageId = evolutionToStage,
                    onDismiss = { gameManager.dismissEvolutionModal() }
                )
            }
        }

        // 3D Interactive World Full-Screen Arena
        activeWorldArena?.let { arenaKey ->
            Dialog(
                onDismissRequest = { activeWorldArena = null },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false,
                    decorFitsSystemWindows = false
                )
            ) {
                when (arenaKey) {
                    "array" -> ArrayKingdomArenaScreen(onDismiss = { activeWorldArena = null })
                    "stack" -> StackTowerArenaScreen(onDismiss = { activeWorldArena = null })
                    "queue" -> QueueStationArenaScreen(onDismiss = { activeWorldArena = null })
                    "linkedlist" -> LinkedListRoadArenaScreen(onDismiss = { activeWorldArena = null })
                    "tree" -> BinaryTreeForestArenaScreen(onDismiss = { activeWorldArena = null })
                    else -> ArrayKingdomArenaScreen(onDismiss = { activeWorldArena = null })
                }
            }
        }
    }
}

// MARK: - 3D Interactive Worlds Section
@Composable
private fun InteractiveGamesSection(
    onOpenWorld: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "🎮 3D INTERACTIVE WORLDS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = AmberGold
            )
            Text(
                text = "5 ARENAS UNLOCKED",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.6f)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WorldPortalCard("tree", "🌲 BINARY TREE FOREST", "Explore BST Canopies & Lost Forest", Color(0xFF10B981), onOpenWorld)
            WorldPortalCard("array", "🏰 ARRAY KINGDOM", "Traverse Contiguous Memory Mazes", AmberGold, onOpenWorld)
            WorldPortalCard("stack", "🥞 SOLAR STACK TOWER", "Ascend Solar Plates & Escape the Spire", Color(0xFFF59E0B), onOpenWorld)
            WorldPortalCard("queue", "🎫 ASTRAL QUEUE STATION", "Manage FIFO Rail Networks & Dispatch", Color(0xFFA855F7), onOpenWorld)
            WorldPortalCard("linkedlist", "🔗 LINKED LIST ROAD", "Follow Pointers Across Pointer Bridges", Color(0xFFEF4444), onOpenWorld)
        }
    }
}

@Composable
private fun WorldPortalCard(
    worldKey: String,
    title: String,
    subtitle: String,
    accentColor: Color,
    onOpenWorld: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .width(300.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF0F1523).copy(alpha = 0.85f))
            .border(
                1.2.dp,
                Brush.linearGradient(listOf(Color.White.copy(alpha = 0.25f), accentColor.copy(alpha = 0.45f))),
                RoundedCornerShape(18.dp)
            )
            .clickable { onOpenWorld(worldKey) }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.20f))
                .border(1.5.dp, accentColor, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.SportsEsports,
                contentDescription = title,
                tint = accentColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.70f),
                lineHeight = 15.sp
            )
        }
    }
}

// MARK: - Grand Master Journey Card
@Composable
private fun GrandMasterJourneyCard(
    completedCount: Int,
    totalCount: Int,
    onOpenSanctuary: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF1E1428), Color(0xFF0F0B18))
                )
            )
            .border(1.5.dp, AmberGold.copy(alpha = 0.45f), RoundedCornerShape(22.dp))
            .clickable { onOpenSanctuary() }
            .padding(20.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = AmberGold,
                modifier = Modifier.size(36.dp)
            )
            Text(
                text = "GRAND MASTER PHOENIX JOURNEY",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold
            )
            Text(
                text = "$completedCount / $totalCount Stages Conquered",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Text(
                text = "Tap to enter the Phoenix Sanctuary and view all 18 evolution forms.",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.70f),
                textAlign = TextAlign.Center
            )
        }
    }
}

// MARK: - Chapter Guidebook Sheet
@Composable
private fun ChapterGuidebookSheet(
    unit: UnitModel,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "📖 Chapter ${unit.unitNumber} Field Guide",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White.copy(alpha = 0.6f),
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onDismiss() }
            )
        }

        Text(
            text = unit.titleDefault,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = unit.themeColor
        )

        Text(
            text = "Master the architectural foundations of this data structure through 5 structured interactive stages. Complete each stage in sequence to challenge the Chapter Boss and unlock the next world.",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.8f),
            lineHeight = 18.sp
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "CURRICULUM STAGES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = AmberGold
            )
            unit.nodes.forEach { node ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = if (node.isBoss) "⚔️" else "🔹", fontSize = 12.sp)
                    Text(
                        text = node.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// MARK: - Streak Info Sheet
@Composable
private fun StreakInfoSheet(
    streakDays: Int,
    isStreakPendingRestore: Boolean,
    savedStreakDays: Int,
    onRestoreStreak: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = if (isStreakPendingRestore) "🧊" else "🔥", fontSize = 48.sp)
        Text(
            text = if (isStreakPendingRestore) "Streak Frozen!" else "$streakDays Day Streak!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
        Text(
            text = if (isStreakPendingRestore) {
                "Your $savedStreakDays-day streak is frozen. Ignite it now to restore your flame!"
            } else {
                "Complete a lesson every single day to fuel your Phoenix and multiply your XP."
            },
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.75f),
            textAlign = TextAlign.Center
        )

        if (isStreakPendingRestore) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF22D3EE))
                    .clickable { onRestoreStreak() }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "RESTORE MY STREAK 🔥",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }
        }
    }
}

// MARK: - Hearts Info Sheet
@Composable
private fun HeartsInfoSheet(
    heartsCount: Int,
    timerString: String?,
    onRefillHearts: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "❤️", fontSize = 48.sp)
        Text(
            text = "$heartsCount / 10 Hearts",
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
        Text(
            text = if (heartsCount < 10) {
                "Next heart regenerates in $timerString. Keep practicing or refill instantly!"
            } else {
                "You have full hearts! Incorrect answers during lessons will consume 1 heart."
            },
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.75f),
            textAlign = TextAlign.Center
        )

        if (heartsCount < 10) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFFF4D4D))
                    .clickable { onRefillHearts() }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "REFILL HEARTS (10 ❤️)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }
    }
}
