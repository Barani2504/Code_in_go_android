package com.simats.codeingo.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
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
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sin
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
import com.simats.codeingo.ui.gamification.DSAChapterStoryIntroSheet
import com.simats.codeingo.ui.gamification.DSAFinalMasterJourneyView
import com.simats.codeingo.ui.leaderboards.LeaderboardsScreen
import com.simats.codeingo.ui.phoenix.PhoenixAnimatedMascotView
import com.simats.codeingo.ui.phoenix.PhoenixAtmosphericBackgroundView
import com.simats.codeingo.ui.phoenix.PhoenixEmotionPickerSheet
import com.simats.codeingo.ui.phoenix.PhoenixEvolutionCelebrationScreen
import com.simats.codeingo.ui.phoenix.PhoenixMascotPose
import com.simats.codeingo.ui.profile.ProfileScreen
import com.simats.codeingo.ui.practice.PracticeHubScreen
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.AmberGoldDark
import com.simats.codeingo.ui.shop.GemShopSheet
import com.simats.codeingo.ui.visualizer.VisualizerScreen
import com.simats.codeingo.ui.worlds.array.ArrayKingdomArenaScreen
import com.simats.codeingo.ui.worlds.linkedlist.LinkedListRoadArenaScreen
import com.simats.codeingo.ui.worlds.queue.QueueStationArenaScreen
import com.simats.codeingo.ui.worlds.stack.StackTowerArenaScreen
import com.simats.codeingo.ui.worlds.tree.BinaryTreeForestArenaScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.liquidGlassCard

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
    val gemsCount by gameManager.gemsCount.collectAsState()
    val totalStars by gameManager.totalStars.collectAsState()
    val streakDays by gameManager.streakDays.collectAsState()
    val isStreakLostPendingRestore by gameManager.isStreakLostPendingRestore.collectAsState()
    val savedStreakDays by gameManager.savedStreakDays.collectAsState()
    val heartsCount by gameManager.heartsCount.collectAsState()
    val unlockedLevels by gameManager.unlockedLevelIndices.collectAsState()
    val showEvolutionModal by gameManager.showEvolutionModal.collectAsState()
    val evolutionFromStage by gameManager.evolutionFromStage.collectAsState()
    val evolutionToStage by gameManager.evolutionToStage.collectAsState()

    val maxUnlockedChapter by gameManager.maxUnlockedChapter.collectAsState()
    val completedChapters by gameManager.completedChapters.collectAsState()
    val completedLevelIndices by gameManager.completedLevelIndices.collectAsState()
    val activeLevelIndex by gameManager.activeLevelIndex.collectAsState()

    var selectedTab by remember { mutableStateOf(DashboardTab.LEARN) }
    var showSideMenu by remember { mutableStateOf(false) }
    var showEmotionPickerSheet by remember { mutableStateOf(false) }
    var showStreakSheet by remember { mutableStateOf(false) }
    var showHeartsSheet by remember { mutableStateOf(false) }
    var showGemShopSheet by remember { mutableStateOf(false) }
    var showFinalMasterJourney by remember { mutableStateOf(false) }
    var storySelectedChapter by remember { mutableStateOf<com.simats.codeingo.data.model.DSAChapterModel?>(null) }
    var guideSelectedUnit by remember { mutableStateOf<UnitModel?>(null) }
    var activeWorldArena by remember { mutableStateOf<String?>(null) }

    // Sequential Chapter Review & Replay Navigation State
    var reviewingPreviousUnitId by remember { mutableStateOf<Int?>(null) }
    var replayReturnLevelIndex by remember { mutableStateOf<Int?>(null) }
    var selectedLockedNodeId by remember { mutableStateOf<Int?>(null) }

    // Keep activeLevelIndex in sync whenever a stage is unlocked (mirrors iOS onComplete)
    LaunchedEffect(unlockedLevels) {
        val maxUnlocked = unlockedLevels.maxOrNull() ?: 1
        if (maxUnlocked > activeLevelIndex && reviewingPreviousUnitId == null) {
            gameManager.setActiveLevel(maxUnlocked)
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

    fun finishReplayAndReturnToCurrentLevel(): Boolean {
        val returnLevel = replayReturnLevelIndex ?: return false
        replayReturnLevelIndex = null
        reviewingPreviousUnitId = null
        gameManager.setActiveLevel(returnLevel)
        selectedLockedNodeId = null
        return true
    }

    fun showPreviousChapterPath() {
        if (maxUnlockedChapter <= 1) return
        replayReturnLevelIndex = activeLevelIndex
        val targetUnitId = maxUnlockedChapter - 1
        reviewingPreviousUnitId = targetUnitId
        val firstLevel = masterUnits.firstOrNull { it.id == targetUnitId }?.nodes?.firstOrNull()?.levelNumber ?: 1
        gameManager.setActiveLevel(firstLevel)
    }

    fun returnToCurrentChapterPath() {
        if (!finishReplayAndReturnToCurrentLevel()) {
            reviewingPreviousUnitId = null
            val currentUnit = masterUnits.firstOrNull { it.id == maxUnlockedChapter }
            if (currentUnit != null) {
                val unlocked = currentUnit.nodes.map { it.levelNumber }.filter { unlockedLevels.contains(it) }
                val target = unlocked.lastOrNull() ?: currentUnit.nodes.firstOrNull()?.levelNumber ?: 1
                gameManager.setActiveLevel(target)
            }
        }
    }

    val visibleUnits = remember(reviewingPreviousUnitId, maxUnlockedChapter, masterUnits) {
        val reviewId = reviewingPreviousUnitId
        if (reviewId != null) {
            masterUnits.filter { it.id == reviewId }
        } else {
            masterUnits.filter { unit ->
                unit.id >= maxUnlockedChapter && unit.id <= minOf(maxUnlockedChapter + 1, masterUnits.size)
            }
        }
    }

    val dashboardScrollState = rememberScrollState()

    // Auto-scroll smoothly to active target node
    LaunchedEffect(activeLevelIndex) {
        val targetScroll = ((activeLevelIndex - 1) * 260).coerceAtLeast(0)
        dashboardScrollState.animateScrollTo(targetScroll)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LocalDynamicThemeColors.current.background)
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
                            val topInsets = WindowInsets.statusBars.union(WindowInsets.displayCutout)
                            val topInsetPadding = topInsets.asPaddingValues().calculateTopPadding()

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(dashboardScrollState),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Spacing for floating top stats header (status bar + cutout insets + header height)
                                Spacer(modifier = Modifier.height(topInsetPadding + 96.dp))

                                // 1. 3D INTERACTIVE WORLDS CAROUSEL
                                InteractiveGamesSection(
                                    maxUnlockedChapter = maxUnlockedChapter,
                                    onOpenWorld = { worldKey ->
                                        activeWorldArena = worldKey
                                    }
                                )

                                Spacer(modifier = Modifier.height(18.dp))

                                // 2. CHAPTER UNITS PATH (Visible Current + 1 Next Preview)
                                visibleUnits.forEach { unit ->
                                    val isUnitOpen = unit.id <= maxUnlockedChapter

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
                                            onGuidebookClick = {
                                                val chapter = com.simats.codeingo.data.model.DSA5ChapterData.chapters.firstOrNull { it.id == unit.id }
                                                if (chapter != null) {
                                                    storySelectedChapter = chapter
                                                } else {
                                                    guideSelectedUnit = unit
                                                }
                                            }
                                        )

                                        // Review Navigation Banner (Return to Current / Review Previous)
                                        ReviewNavigationButton(
                                            unit = unit,
                                            maxUnlockedChapter = maxUnlockedChapter,
                                            reviewingPreviousUnitId = reviewingPreviousUnitId,
                                            onReturnToCurrent = { returnToCurrentChapterPath() },
                                            onShowPrevious = { showPreviousChapterPath() }
                                        )

                                        // Winding 3D Level Nodes with Stepping Stones Path & Smooth Flying Mascot
                                        val nodeCirclePositions = remember { mutableStateMapOf<Int, Offset>() }
                                        var unitCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
                                        val isUnitActive = unit.nodes.any { it.levelNumber == activeLevelIndex }

                                        val density = LocalDensity.current
                                        val mascotSizePx = with(density) { 72.dp.toPx() }
                                        val mascotHalfPx = mascotSizePx / 2f

                                        val coroutineScope = rememberCoroutineScope()
                                        val flightProgress = remember { Animatable(1f) }
                                        val landingSettle = remember { Animatable(0f) }
                                        var flightStartPos by remember { mutableStateOf<Offset?>(null) }
                                        var flightTargetPos by remember { mutableStateOf<Offset?>(null) }
                                        var isFlying by remember { mutableStateOf(false) }
                                        var lastTrackedActiveLevel by remember { mutableIntStateOf(activeLevelIndex) }
                                        var hasInitialFlightPlayed by remember { mutableStateOf(false) }

                                        val targetOffset = nodeCirclePositions[activeLevelIndex]

                                        // Initial appearance flight: triggers once when active level circle position is first laid out
                                        LaunchedEffect(nodeCirclePositions[activeLevelIndex]) {
                                            if (!hasInitialFlightPlayed && isUnitActive) {
                                                val target = nodeCirclePositions[activeLevelIndex]
                                                if (target != null && target != Offset.Zero) {
                                                    val prevLevel = activeLevelIndex - 1
                                                    val prevPos = nodeCirclePositions[prevLevel]
                                                    if (prevLevel >= 1 && prevPos != null && prevPos != Offset.Zero) {
                                                        flightStartPos = prevPos
                                                        flightTargetPos = target
                                                        hasInitialFlightPlayed = true
                                                        lastTrackedActiveLevel = activeLevelIndex
                                                        isFlying = true
                                                        flightProgress.snapTo(0f)
                                                        flightProgress.animateTo(
                                                            targetValue = 1f,
                                                            animationSpec = tween(durationMillis = 3000, easing = FastOutSlowInEasing)
                                                        )
                                                        isFlying = false
                                                        landingSettle.snapTo(1f)
                                                        landingSettle.animateTo(
                                                            0f,
                                                            animationSpec = spring(dampingRatio = 0.55f, stiffness = 400f)
                                                        )
                                                    } else {
                                                        flightStartPos = target
                                                        flightTargetPos = target
                                                        flightProgress.snapTo(1f)
                                                        hasInitialFlightPlayed = true
                                                        lastTrackedActiveLevel = activeLevelIndex
                                                    }
                                                }
                                            }
                                        }

                                        // Level click / level transition flight: keyed ONLY on activeLevelIndex!
                                        // This ensures clicking another level immediately starts the flight and is never cancelled by layout updates!
                                        LaunchedEffect(activeLevelIndex) {
                                            if (isUnitActive && hasInitialFlightPlayed && lastTrackedActiveLevel != activeLevelIndex) {
                                                val target = nodeCirclePositions[activeLevelIndex]
                                                val startPos = flightTargetPos ?: nodeCirclePositions[lastTrackedActiveLevel] ?: target ?: Offset.Zero
                                                val stepDiff = abs(activeLevelIndex - lastTrackedActiveLevel)
                                                val flightDuration = if (stepDiff <= 1) 3000 else minOf(4200, 3000 + (stepDiff - 1) * 350)

                                                lastTrackedActiveLevel = activeLevelIndex
                                                if (target != null && target != Offset.Zero) {
                                                    flightStartPos = startPos
                                                    flightTargetPos = target
                                                    isFlying = true
                                                    flightProgress.snapTo(0f)
                                                    flightProgress.animateTo(
                                                        targetValue = 1f,
                                                        animationSpec = tween(durationMillis = flightDuration, easing = FastOutSlowInEasing)
                                                    )
                                                    isFlying = false
                                                    landingSettle.snapTo(1f)
                                                    landingSettle.animateTo(
                                                        0f,
                                                        animationSpec = spring(dampingRatio = 0.55f, stiffness = 400f)
                                                    )
                                                }
                                            }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .onGloballyPositioned { unitCoordinates = it }
                                        ) {
                                            Column(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy(16.dp)
                                            ) {
                                                unit.nodes.forEachIndexed { index, node ->
                                                    val isNodeCompleted = completedLevelIndices.contains(node.levelNumber)
                                                    val isNodeUnlocked = isUnitOpen && (unlockedLevels.contains(node.levelNumber) || isNodeCompleted)
                                                    val isActiveTarget = isUnitOpen && (node.levelNumber == activeLevelIndex)
                                                    val isLockedSelected = selectedLockedNodeId == node.id

                                                    val safeXOffset = (node.xOffset * 0.45f).coerceIn((-22).dp, 22.dp)

                                                    val prevNode = if (index > 0) unit.nodes[index - 1] else null
                                                    val prevXOffset = if (prevNode != null) {
                                                        (prevNode.xOffset * 0.45f).coerceIn((-22).dp, 22.dp)
                                                    } else safeXOffset

                                                    // Stepping Stone Connector Dots from previous node
                                                    if (index > 0) {
                                                        ConnectorSteppingStones(
                                                            fromX = prevXOffset,
                                                            toX = safeXOffset,
                                                            isActive = isNodeUnlocked || isNodeCompleted,
                                                            unitColor = unit.themeColor
                                                        )
                                                    }

                                                    Box(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        LessonNodeButton(
                                                            node = node,
                                                            unit = unit,
                                                            isUnlocked = isNodeUnlocked,
                                                            isActiveTarget = isActiveTarget,
                                                            isLockedSelected = isLockedSelected,
                                                            xOffset = safeXOffset,
                                                            onCirclePositioned = { circleCoords ->
                                                                unitCoordinates?.let { parent ->
                                                                    if (circleCoords.isAttached && parent.isAttached) {
                                                                        val centerInParent = parent.localPositionOf(
                                                                            circleCoords,
                                                                            Offset(circleCoords.size.width / 2f, circleCoords.size.height / 2f)
                                                                        )
                                                                        nodeCirclePositions[node.levelNumber] = centerInParent
                                                                    }
                                                                }
                                                            },
                                                            onNodeClick = {
                                                                if (isNodeUnlocked) {
                                                                    gameManager.setActiveLevel(node.levelNumber)
                                                                    selectedLockedNodeId = null
                                                                } else {
                                                                    selectedLockedNodeId = node.id
                                                                }
                                                            },
                                                            onStartClick = {
                                                                if (node.levelNumber < activeLevelIndex && replayReturnLevelIndex == null) {
                                                                    replayReturnLevelIndex = activeLevelIndex
                                                                }
                                                                if (node.isBoss) {
                                                                    onStartBoss(node.bossSpec?.id ?: "boss_unit_${unit.id}")
                                                                } else {
                                                                    onStartLesson(unit.id, node.levelNumber, 5, false)
                                                                }
                                                            }
                                                        )
                                                    }
                                                }
                                            }

                                            // Smoothly flying Phoenix Mascot across level nodes (Independent Overlay Frame)
                                            if (isUnitActive && (targetOffset != null || flightTargetPos != null)) {
                                                val p = flightProgress.value
                                                val start = flightStartPos ?: targetOffset ?: Offset.Zero
                                                val target = flightTargetPos ?: targetOffset ?: Offset.Zero

                                                val dx = target.x - start.x
                                                val dy = target.y - start.y
                                                val distance = hypot(dx, dy).coerceAtLeast(1f)

                                                // 1. Normal perpendicular unit vector for lateral swing along the curve
                                                val normX = -dy / distance
                                                val normY = dx / distance

                                                // 2. Parabolic lateral swing factor: 4 * p * (1 - p), peaks at midpoint (p = 0.5)
                                                val parabolaFactor = 4f * p * (1f - p)

                                                // Outward lateral swing following the winding stepping stone curves
                                                val curveDir = when {
                                                    abs(dx) > 4f -> if (dx > 0) 1f else -1f
                                                    else -> if (dy >= 0) 1f else -1f
                                                }
                                                val maxLateralSwingPx = (max(38f, distance * 0.22f) * curveDir).coerceIn(-75f, 75f)
                                                val lateralSwingX = normX * maxLateralSwingPx * parabolaFactor
                                                val lateralSwingY = normY * maxLateralSwingPx * parabolaFactor

                                                // 3. Parabolic altitude lift & dive arc in 3D space
                                                val arcAltitudePx = with(density) { 62.dp.toPx() }
                                                val altitudeArcY = if (isFlying) -arcAltitudePx * parabolaFactor else 0f

                                                // 4. Lifelike wing-beat flapping harmonic oscillation
                                                val wingbeatHeave = if (isFlying) {
                                                    sin(p * 8f * Math.PI.toFloat()) * with(density) { 3.dp.toPx() }
                                                } else 0f

                                                // Total 2D coordinates on screen
                                                val curX = start.x + dx * p + lateralSwingX
                                                val curY = start.y + dy * p + lateralSwingY + altitudeArcY + wingbeatHeave

                                                // 5. Aerodynamic banking roll into the curve & turns
                                                val bankTilt = if (isFlying) {
                                                    val swingBank = (-normX * maxLateralSwingPx * 0.18f * cos(p * Math.PI.toFloat()))
                                                    val forwardBank = (dx * 0.12f)
                                                    (swingBank + forwardBank).coerceIn(-26f, 26f) * sin(p * Math.PI.toFloat())
                                                } else 0f

                                                // 6. Pitch angle (Climb vs dive glide)
                                                val pitchTilt = if (isFlying) {
                                                    val flightDirY = if (dy >= 0) 1f else -1f
                                                    cos(p * Math.PI.toFloat()) * -6f * flightDirY
                                                } else 0f

                                                // 7. Elevation Scale (Bird flies closer to camera during flight)
                                                val flightElevationScale = if (isFlying) {
                                                    1.0f + 0.20f * parabolaFactor
                                                } else 1.0f

                                                // 8. Landing settle spring (squash and stretch upon touchdown)
                                                val settleProgress = landingSettle.value
                                                val settleScaleX = 1.0f + settleProgress * 0.08f
                                                val settleScaleY = 1.0f - settleProgress * 0.08f
                                                val settleOffsetY = with(density) { settleProgress * 3.dp.toPx() }

                                                // 9. Perch upon circle alignment
                                                val perchOffsetPx = with(density) { 10.dp.toPx() }
                                                val posX = curX - mascotHalfPx
                                                val posY = curY - mascotHalfPx - perchOffsetPx + settleOffsetY

                                                Box(
                                                    modifier = Modifier
                                                        .offset {
                                                            IntOffset(posX.roundToInt(), posY.roundToInt())
                                                        }
                                                        .graphicsLayer {
                                                            rotationZ = bankTilt + pitchTilt
                                                            scaleX = flightElevationScale * settleScaleX
                                                            scaleY = flightElevationScale * settleScaleY
                                                        }
                                                        .clickable(
                                                            interactionSource = remember { MutableInteractionSource() },
                                                            indication = null
                                                        ) {
                                                            // Tapping the bird triggers 3-second celebratory flight from previous circle!
                                                            val prevPos = nodeCirclePositions[activeLevelIndex - 1] ?: (targetOffset ?: Offset.Zero)
                                                            flightStartPos = prevPos
                                                            flightTargetPos = targetOffset
                                                            coroutineScope.launch {
                                                                isFlying = true
                                                                flightProgress.snapTo(0f)
                                                                flightProgress.animateTo(
                                                                    targetValue = 1f,
                                                                    animationSpec = tween(durationMillis = 3000, easing = FastOutSlowInEasing)
                                                                )
                                                                isFlying = false
                                                                landingSettle.snapTo(1f)
                                                                landingSettle.animateTo(
                                                                    0f,
                                                                    animationSpec = spring(dampingRatio = 0.55f, stiffness = 400f)
                                                                )
                                                            }
                                                        }
                                                ) {
                                                    // Dynamic soft ground shadow under the bird during flight
                                                    if (isFlying) {
                                                        val groundShadowAlpha = (0.28f - 0.14f * parabolaFactor).coerceIn(0.08f, 0.35f)
                                                        val groundShadowScale = (1.0f - 0.35f * parabolaFactor)
                                                        Box(
                                                            modifier = Modifier
                                                                .size(width = 44.dp, height = 12.dp)
                                                                .align(Alignment.BottomCenter)
                                                                .offset(y = with(density) { (-altitudeArcY * 0.7f).toDp() + 6.dp })
                                                                .graphicsLayer {
                                                                    alpha = groundShadowAlpha
                                                                    scaleX = groundShadowScale
                                                                    scaleY = groundShadowScale
                                                                }
                                                                .clip(CircleShape)
                                                                .background(Color.Black.copy(alpha = 0.45f))
                                                        )
                                                    }

                                                    PhoenixEggCompanionMascotView(
                                                        levelNumber = activeLevelIndex,
                                                        isBoss = unit.nodes.firstOrNull { it.levelNumber == activeLevelIndex }?.isBoss ?: false,
                                                        isFlying = isFlying,
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
                                    onOpenSanctuary = {
                                        if (unlockedLevels.size >= 25) {
                                            showFinalMasterJourney = true
                                        } else {
                                            onOpenPhoenixSanctuary()
                                        }
                                    }
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
                                gemsCount = gemsCount,
                                heartTimerString = heartTimerStr,
                                onMenuClick = { showSideMenu = true },
                                onPhoenixClick = { showEmotionPickerSheet = true },
                                onStreakClick = { showStreakSheet = true },
                                onXpClick = { selectedTab = DashboardTab.PRACTICE },
                                onHeartsClick = { showHeartsSheet = true },
                                onGemsClick = { showGemShopSheet = true }
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
                        val completedLessonCount by GameManager.instance.completedLessonIds.collectAsState()
                        LeaderboardsScreen(
                            completedLessons = completedLessonCount.size,
                            onStartLesson = { selectedTab = DashboardTab.LEARN },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    DashboardTab.SHOP -> {
                        GemShopSheet(
                            onDismiss = { selectedTab = DashboardTab.LEARN },
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
                containerColor = LocalDynamicThemeColors.current.cardBackground
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
                containerColor = LocalDynamicThemeColors.current.cardBackground
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
                containerColor = LocalDynamicThemeColors.current.cardBackground
            ) {
                HeartsInfoSheet(
                    heartsCount = heartsCount,
                    timerString = heartTimerStr,
                    onRefillHearts = {
                        gameManager.refillHearts()
                        showHeartsSheet = false
                    },
                    onRefillWithGems = { amount, price ->
                        val result = gameManager.buyHearts(amount, price)
                        if (result.first) {
                            showHeartsSheet = false
                        }
                    },
                    onOpenShop = {
                        showHeartsSheet = false
                        showGemShopSheet = true
                    },
                    onDismiss = { showHeartsSheet = false }
                )
            }
        }

        // Gem Shop & Duolingo Cosmetics Modal Sheet
        if (showGemShopSheet) {
            GemShopSheet(
                onDismiss = { showGemShopSheet = false }
            )
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

        // DSA Final Master Journey Fullscreen Celebration Modal
        if (showFinalMasterJourney) {
            Dialog(
                onDismissRequest = { showFinalMasterJourney = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                DSAFinalMasterJourneyView(
                    totalXP = totalXP,
                    totalLessons = unlockedLevels.size,
                    correctAnswers = unlockedLevels.size * 5,
                    perfectLessons = maxOf(1, unlockedLevels.size / 2),
                    currentStreak = if (streakDays > 0) streakDays else 3,
                    onDismiss = { showFinalMasterJourney = false }
                )
            }
        }

        // DSA Chapter Story Intro Sheet Modal
        storySelectedChapter?.let { chapter ->
            Dialog(
                onDismissRequest = { storySelectedChapter = null },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                DSAChapterStoryIntroSheet(
                    chapter = chapter,
                    onStartFirstLevel = {
                        val firstLevel = chapter.levels.firstOrNull()?.levelNumber ?: 1
                        onStartLesson(chapter.id, firstLevel, chapter.levels.size, false)
                    },
                    onDismiss = { storySelectedChapter = null }
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
    maxUnlockedChapter: Int,
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
                text = "${minOf(maxUnlockedChapter, 5)} / 5 ARENAS UNLOCKED",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.6f)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WorldPortalCard("array", 1, "🏰 ARRAY KINGDOM", "Traverse Contiguous Memory Mazes", AmberGold, maxUnlockedChapter, onOpenWorld)
            WorldPortalCard("linkedlist", 2, "🔗 LINKED LIST ROAD", "Follow Pointers Across Pointer Bridges", Color(0xFFEF4444), maxUnlockedChapter, onOpenWorld)
            WorldPortalCard("stack", 3, "🥞 SOLAR STACK TOWER", "Ascend Solar Plates & Escape the Spire", Color(0xFFF59E0B), maxUnlockedChapter, onOpenWorld)
            WorldPortalCard("queue", 4, "🎫 ASTRAL QUEUE STATION", "Manage FIFO Rail Networks & Dispatch", Color(0xFFA855F7), maxUnlockedChapter, onOpenWorld)
            WorldPortalCard("tree", 5, "🌲 BINARY TREE FOREST", "Explore BST Canopies & Lost Forest", Color(0xFF10B981), maxUnlockedChapter, onOpenWorld)
        }
    }
}

@Composable
private fun WorldPortalCard(
    worldKey: String,
    chapterId: Int,
    title: String,
    subtitle: String,
    accentColor: Color,
    maxUnlockedChapter: Int,
    onOpenWorld: (String) -> Unit
) {
    val isDark = LocalDynamicThemeColors.current.isDark
    val isUnlocked = chapterId <= maxUnlockedChapter

    Box(
        modifier = Modifier
            .width(300.dp)
            .height(84.dp)
            .clip(RoundedCornerShape(18.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDark) Color(0xFF0F1523).copy(alpha = if (isUnlocked) 0.85f else 0.45f) else Color.White.copy(alpha = if (isUnlocked) 0.95f else 0.55f))
                .border(
                    1.2.dp,
                    Brush.linearGradient(listOf(LocalDynamicThemeColors.current.placeholder.copy(alpha = 0.25f), accentColor.copy(alpha = if (isUnlocked) 0.45f else 0.20f))),
                    RoundedCornerShape(18.dp)
                )
                .clickable {
                    if (isUnlocked) {
                        onOpenWorld(worldKey)
                    }
                }
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
                    color = LocalDynamicThemeColors.current.textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.70f),
                    lineHeight = 15.sp
                )
            }
        }

        if (!isUnlocked) {
            ArenaLockOverlay(
                requiredChapter = chapterId,
                accentColor = accentColor
            )
        }
    }
}

// MARK: - Arena Lock Overlay
@Composable
private fun ArenaLockOverlay(
    requiredChapter: Int,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val isDark = LocalDynamicThemeColors.current.isDark
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(18.dp))
            .background(
                if (isDark) Color(0xFF0A0F1D).copy(alpha = 0.88f)
                else Color.White.copy(alpha = 0.92f)
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.20f))
                    .border(1.2.dp, accentColor.copy(alpha = 0.45f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.18f))
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "LOCKED",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = accentColor
                    )
                }
                Text(
                    text = "Finish Chapter ${requiredChapter - 1} to unlock",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDark) Color.White.copy(alpha = 0.85f) else Color(0xFF192337)
                )
            }
        }
    }
}

// MARK: - Review Navigation Button
@Composable
private fun ReviewNavigationButton(
    unit: UnitModel,
    maxUnlockedChapter: Int,
    reviewingPreviousUnitId: Int?,
    onReturnToCurrent: () -> Unit,
    onShowPrevious: () -> Unit
) {
    if (reviewingPreviousUnitId == unit.id) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF58CC02), Color(0xFF26A640))
                    )
                )
                .clickable { onReturnToCurrent() }
                .padding(horizontal = 16.dp, vertical = 11.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "➡️", fontSize = 14.sp)
                    Text(
                        text = "Return to Current Chapter ($maxUnlockedChapter)",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
                Text(text = "✨", fontSize = 12.sp)
            }
        }
    } else if (maxUnlockedChapter > 1 && reviewingPreviousUnitId == null) {
        val isDark = LocalDynamicThemeColors.current.isDark
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (isDark) Color.White.copy(alpha = 0.06f) else Color.White)
                .border(
                    1.dp,
                    if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.06f),
                    RoundedCornerShape(14.dp)
                )
                .clickable { onShowPrevious() }
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "🔄", fontSize = 13.sp)
                    Text(
                        text = "Review Previous Chapter (${maxUnlockedChapter - 1})",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color(0xFF1E283C)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Review",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) Color.White.copy(alpha = 0.65f) else Color(0xFF414B5F)
                    )
                    Text(
                        text = "›",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White.copy(alpha = 0.65f) else Color(0xFF414B5F)
                    )
                }
            }
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
            .liquidGlassCard(accentGlow = AmberGold, cornerRadius = 22.dp)
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
                color = LocalDynamicThemeColors.current.textPrimary
            )
            Text(
                text = "Tap to enter the Phoenix Sanctuary and view all 18 evolution forms.",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.70f),
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
                color = LocalDynamicThemeColors.current.textPrimary
            )
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.6f),
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
            color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.8f),
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
                        color = LocalDynamicThemeColors.current.textPrimary
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
            color = LocalDynamicThemeColors.current.textPrimary
        )
        Text(
            text = if (isStreakPendingRestore) {
                "Your $savedStreakDays-day streak is frozen. Ignite it now to restore your flame!"
            } else {
                "Complete a lesson every single day to fuel your Phoenix and multiply your XP."
            },
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.75f),
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
    onRefillWithGems: (Int, Int) -> Unit = { _, _ -> },
    onOpenShop: () -> Unit = {},
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(text = "❤️", fontSize = 48.sp)
        Text(
            text = "$heartsCount / 10 Hearts",
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = LocalDynamicThemeColors.current.textPrimary
        )
        Text(
            text = if (heartsCount < 10) {
                "Next heart regenerates in ${timerString ?: "a moment"}. Keep practicing or refill instantly!"
            } else {
                "You have full hearts! Incorrect answers during lessons will consume 1 heart."
            },
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.75f),
            textAlign = TextAlign.Center
        )

        if (heartsCount < 10) {
            // Free / Instant full refill
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
                    text = "REFILL FULL HEARTS (10 ❤️)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            // Buy +5 hearts with gems
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF1CA6FF))
                    .clickable { onRefillWithGems(5, 20) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "BUY +5 HEARTS (💎 20)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }

        // Open Gem Shop button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White.copy(alpha = 0.08f))
                .clickable { onOpenShop() }
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🛍️ VISIT GEM SHOP",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = LocalDynamicThemeColors.current.textPrimary
            )
        }
    }
}

// MARK: - Stepping Stone Connector Trail
@Composable
private fun ConnectorSteppingStones(
    fromX: androidx.compose.ui.unit.Dp,
    toX: androidx.compose.ui.unit.Dp,
    isActive: Boolean,
    unitColor: Color,
    modifier: Modifier = Modifier
) {
    val isDark = LocalDynamicThemeColors.current.isDark
    Column(
        modifier = modifier.padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (dotIndex in 1..3) {
            val progress = dotIndex / 4f
            val dotX = fromX + (toX - fromX) * progress
            val dotSize = if (isActive) 7.5.dp else 6.dp
            Box(
                modifier = Modifier
                    .offset(x = dotX)
                    .size(dotSize)
                    .clip(CircleShape)
                    .background(
                        if (isActive) unitColor.copy(alpha = 0.85f)
                        else if (isDark) Color.White.copy(alpha = 0.20f)
                        else Color(0xFFC8D2E2)
                    )
            )
        }
    }
}
