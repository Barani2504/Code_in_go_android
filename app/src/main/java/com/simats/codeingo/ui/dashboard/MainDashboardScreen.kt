package com.simats.codeingo.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.data.model.DashboardTab
import com.simats.codeingo.data.model.LessonNodeItem
import com.simats.codeingo.data.model.UnitCharacterType
import com.simats.codeingo.data.model.UnitModel
import com.simats.codeingo.data.repository.CourseRepository
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.navigation.BottomNavBar
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DsaBlue
import com.simats.codeingo.ui.theme.DsaBlueDark
import com.simats.codeingo.ui.theme.DsaGreen
import com.simats.codeingo.ui.theme.DsaGreenDark
import com.simats.codeingo.ui.theme.DsaOrange
import com.simats.codeingo.ui.theme.DsaOrangeDark
import com.simats.codeingo.ui.theme.DsaPurple
import com.simats.codeingo.ui.theme.DsaPurpleDark
import com.simats.codeingo.ui.theme.DsaRed
import com.simats.codeingo.ui.theme.DsaRedDark
import com.simats.codeingo.ui.theme.InputBorder
import com.simats.codeingo.ui.theme.SubtextGray
import com.simats.codeingo.ui.leaderboards.LeaderboardsScreen
import com.simats.codeingo.ui.practice.PracticeHubScreen
import com.simats.codeingo.ui.profile.ProfileScreen
import com.simats.codeingo.ui.quests.QuestsScreen
import com.simats.codeingo.ui.shop.ShopScreen
import com.simats.codeingo.ui.visualizer.VisualizerScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboardScreen(
    onStartLesson: (lessonId: String) -> Unit,
    onStartBoss: (bossId: String) -> Unit,
    onOpenPhoenixSanctuary: () -> Unit,
    onOpenSettings: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameManager = GameManager.instance
    val totalXP by gameManager.totalXP.collectAsState()
    val streakDays by gameManager.streakDays.collectAsState()
    val heartsCount by gameManager.heartsCount.collectAsState()
    val activePhoenixStage by gameManager.activePhoenixStage.collectAsState()
    val unlockedLevels by gameManager.unlockedLevelIndices.collectAsState()

    var selectedTab by remember { mutableStateOf(DashboardTab.LEARN) }
    var showSideMenu by remember { mutableStateOf(false) }
    var showMoreDrawer by remember { mutableStateOf(false) }
    var activeLevelIndex by remember { mutableIntStateOf(1) }

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Curriculum units for the learning path
    val dashboardUnits = remember {
        listOf(
            UnitModel(
                id = 1, sectionNumber = 1, unitNumber = 1,
                titleKey = "unit_1_title", titleDefault = "🟢 Unit 1: Foundations",
                themeColor = DsaGreen, themeDarkColor = DsaGreenDark,
                nodes = listOf(
                    LessonNodeItem(1, 1, "x.squareroot", (-10).dp, "What is a DSA?"),
                    LessonNodeItem(2, 2, "grid", (-45).dp, "Types of Data Structures"),
                    LessonNodeItem(3, 3, "arrow.left.arrow.right", (-65).dp, "Linear vs Non-Linear"),
                    LessonNodeItem(4, 4, "bus", 0.dp, "Static vs Dynamic"),
                    LessonNodeItem(5, 5, "gauge", 50.dp, "Time & Space Complexity"),
                    LessonNodeItem(6, 6, "flame", 0.dp, "Boss: The Great Jumble", isBoss = true)
                ),
                characterType = UnitCharacterType.PHOENIX,
                worldTheme = "The Hatchery Warehouse"
            ),
            UnitModel(
                id = 2, sectionNumber = 1, unitNumber = 2,
                titleKey = "unit_2_title", titleDefault = "🟢 Unit 2: Arrays",
                themeColor = DsaGreen, themeDarkColor = DsaGreenDark,
                nodes = listOf(
                    LessonNodeItem(7, 7, "grid", 0.dp, "Array Basics & Address Math"),
                    LessonNodeItem(8, 8, "arrow.right", 45.dp, "Conveyor Sweep"),
                    LessonNodeItem(9, 9, "arrow.forward", 65.dp, "Shift Party (Insertion)"),
                    LessonNodeItem(10, 10, "arrow.back", 10.dp, "Close the Gap (Deletion)"),
                    LessonNodeItem(11, 11, "search", (-50).dp, "Hi-Lo Master (Binary Search)"),
                    LessonNodeItem(12, 12, "grid", (-20).dp, "2D Arrays & Battleship"),
                    LessonNodeItem(13, 13, "flame", 0.dp, "Boss: The Locker Thief", isBoss = true)
                ),
                characterType = UnitCharacterType.PHOENIX,
                worldTheme = "Locker Row Avenue"
            ),
            UnitModel(
                id = 3, sectionNumber = 1, unitNumber = 3,
                titleKey = "unit_3_title", titleDefault = "🟢 Unit 3: Strings",
                themeColor = DsaGreen, themeDarkColor = DsaGreenDark,
                nodes = listOf(
                    LessonNodeItem(14, 14, "textformat", 0.dp, "String Basics & Immutability"),
                    LessonNodeItem(15, 15, "edit", (-40).dp, "Highlighter Sweep"),
                    LessonNodeItem(16, 16, "build", (-60).dp, "Word Forge"),
                    LessonNodeItem(17, 17, "sync", 0.dp, "Mirror Chamber (Palindrome)"),
                    LessonNodeItem(18, 18, "sort", 45.dp, "Letter Sieve (Anagrams)"),
                    LessonNodeItem(19, 19, "flame", 0.dp, "Boss: The Scrambled Scroll", isBoss = true)
                ),
                characterType = UnitCharacterType.PHOENIX,
                worldTheme = "The Scroll Scriptorium"
            ),
            UnitModel(
                id = 4, sectionNumber = 2, unitNumber = 4,
                titleKey = "unit_4_title", titleDefault = "🟡 Unit 4: Linked Lists",
                themeColor = DsaOrange, themeDarkColor = DsaOrangeDark,
                nodes = listOf(
                    LessonNodeItem(20, 20, "link", 10.dp, "Train Builder (Singly Linked)"),
                    LessonNodeItem(21, 21, "cut", 50.dp, "Pointer Surgery"),
                    LessonNodeItem(22, 22, "swap", 60.dp, "Three-Pointer Dance (Reversal)"),
                    LessonNodeItem(23, 23, "sync", (-10).dp, "Doubly Linked List"),
                    LessonNodeItem(24, 24, "loop", (-45).dp, "Circular & Tortoise-Hare"),
                    LessonNodeItem(25, 25, "flame", 0.dp, "Boss: The Runaway Train", isBoss = true)
                ),
                characterType = UnitCharacterType.PHOENIX,
                worldTheme = "Chain Canyon"
            ),
            UnitModel(
                id = 5, sectionNumber = 2, unitNumber = 5,
                titleKey = "unit_5_title", titleDefault = "🟡 Unit 5: Stack",
                themeColor = DsaOrange, themeDarkColor = DsaOrangeDark,
                nodes = listOf(
                    LessonNodeItem(26, 26, "layers", (-10).dp, "Stack Basics & LIFO"),
                    LessonNodeItem(27, 27, "swap_vert", (-45).dp, "Push & Pop Visualizer"),
                    LessonNodeItem(28, 28, "data_object", 10.dp, "Bracket Bouncer"),
                    LessonNodeItem(29, 29, "calculate", 50.dp, "Railway Yard & Postfix"),
                    LessonNodeItem(30, 30, "flame", 0.dp, "Boss: Diner Rush", isBoss = true)
                ),
                characterType = UnitCharacterType.PHOENIX,
                worldTheme = "Pancake Tower Diner"
            ),
            UnitModel(
                id = 6, sectionNumber = 2, unitNumber = 6,
                titleKey = "unit_6_title", titleDefault = "🟡 Unit 6: Queue",
                themeColor = DsaOrange, themeDarkColor = DsaOrangeDark,
                nodes = listOf(
                    LessonNodeItem(31, 31, "queue", 0.dp, "Queue Basics & FIFO"),
                    LessonNodeItem(32, 32, "sync", 45.dp, "Circular Queue Ring"),
                    LessonNodeItem(33, 33, "swap_horiz", (-30).dp, "Deque (Double-Ended)"),
                    LessonNodeItem(34, 34, "priority_high", (-60).dp, "Priority Queue (ER Triage)"),
                    LessonNodeItem(35, 35, "flame", 0.dp, "Boss: The Café Stampede", isBoss = true)
                ),
                characterType = UnitCharacterType.PHOENIX,
                worldTheme = "Ember Café Rush"
            ),
            UnitModel(
                id = 7, sectionNumber = 2, unitNumber = 7,
                titleKey = "unit_7_title", titleDefault = "🟡 Unit 7: Hashing",
                themeColor = DsaOrange, themeDarkColor = DsaOrangeDark,
                nodes = listOf(
                    LessonNodeItem(36, 36, "lock", 10.dp, "Magic Locker Assigner"),
                    LessonNodeItem(37, 37, "function", 50.dp, "Craft Your Hash Function"),
                    LessonNodeItem(38, 38, "check", 0.dp, "HashMap & HashSet"),
                    LessonNodeItem(39, 39, "warning", (-45).dp, "Collision: Chaining vs Probing"),
                    LessonNodeItem(40, 40, "flame", 0.dp, "Boss: The Locker Storm", isBoss = true)
                ),
                characterType = UnitCharacterType.PHOENIX,
                worldTheme = "Hash Harbor"
            ),
            UnitModel(
                id = 8, sectionNumber = 3, unitNumber = 8,
                titleKey = "unit_8_title", titleDefault = "🔵 Unit 8: Trees",
                themeColor = DsaBlue, themeDarkColor = DsaBlueDark,
                nodes = listOf(
                    LessonNodeItem(41, 41, "park", 0.dp, "Tree Anatomy & Terminology"),
                    LessonNodeItem(42, 42, "fork_right", (-40).dp, "Forest Tour (Pre/In/Post DFS)"),
                    LessonNodeItem(43, 43, "waves", (-60).dp, "Level-Order (BFS Wave)"),
                    LessonNodeItem(44, 44, "alt_route", 0.dp, "BST Plinko Insert"),
                    LessonNodeItem(45, 45, "sync", 45.dp, "Balance Doctor (AVL Rotations)"),
                    LessonNodeItem(46, 46, "filter_hdr", 65.dp, "Binary Heap (Bubble-up)"),
                    LessonNodeItem(47, 47, "flame", 0.dp, "Boss: The Twisted Oak", isBoss = true)
                ),
                characterType = UnitCharacterType.PHOENIX,
                worldTheme = "Ember Forest"
            ),
            UnitModel(
                id = 9, sectionNumber = 3, unitNumber = 9,
                titleKey = "unit_9_title", titleDefault = "🔵 Unit 9: Trie",
                themeColor = DsaBlue, themeDarkColor = DsaBlueDark,
                nodes = listOf(
                    LessonNodeItem(48, 48, "text_fields", (-10).dp, "Trie & Shared Prefixes"),
                    LessonNodeItem(49, 49, "search", (-45).dp, "Insert & Search Crystals"),
                    LessonNodeItem(50, 50, "keyboard", 10.dp, "Live Autocomplete"),
                    LessonNodeItem(51, 51, "content_cut", 50.dp, "Safe Node Pruning"),
                    LessonNodeItem(52, 52, "flame", 0.dp, "Boss: The Whispering Wall", isBoss = true)
                ),
                characterType = UnitCharacterType.PHOENIX,
                worldTheme = "Crystal Cave of Words"
            ),
            UnitModel(
                id = 10, sectionNumber = 3, unitNumber = 10,
                titleKey = "unit_10_title", titleDefault = "🟣 Unit 10: Graphs",
                themeColor = DsaPurple, themeDarkColor = DsaPurpleDark,
                nodes = listOf(
                    LessonNodeItem(53, 53, "hub", 0.dp, "Vertices & Bridges"),
                    LessonNodeItem(54, 54, "north_east", 45.dp, "Directed & Weighted Bridges"),
                    LessonNodeItem(55, 55, "table_chart", (-30).dp, "Adjacency Matrix vs List"),
                    LessonNodeItem(56, 56, "water", (-60).dp, "BFS: Ripple Flood"),
                    LessonNodeItem(57, 57, "south", 0.dp, "DFS: Deep Diver"),
                    LessonNodeItem(58, 58, "flame", 0.dp, "Boss: The Storm Archipelago", isBoss = true)
                ),
                characterType = UnitCharacterType.PHOENIX,
                worldTheme = "Sky Islands"
            ),
            UnitModel(
                id = 11, sectionNumber = 4, unitNumber = 11,
                titleKey = "unit_11_title", titleDefault = "🔴 Unit 11: Advanced Structures",
                themeColor = DsaRed, themeDarkColor = DsaRedDark,
                nodes = listOf(
                    LessonNodeItem(59, 59, "groups", (-10).dp, "Union-Find (Kingdom Merge)"),
                    LessonNodeItem(60, 60, "bar_chart", (-45).dp, "Segment Tree: Range Query"),
                    LessonNodeItem(61, 61, "stairs", 0.dp, "Fenwick Tree (Lowbit Ladder)"),
                    LessonNodeItem(62, 62, "palette", 50.dp, "Red-Black Tree: Color Court"),
                    LessonNodeItem(63, 63, "menu_book", 20.dp, "B-Tree: Library Shelving"),
                    LessonNodeItem(64, 64, "crown", 0.dp, "Final Boss: Eternal Flame Guardian", isBoss = true)
                ),
                characterType = UnitCharacterType.PHOENIX,
                worldTheme = "The Sun Citadel"
            )
        )
    }

    Box(modifier = modifier.fillMaxSize().background(DarkBackground)) {
        Scaffold(
            topBar = {
                TopStatsHeader(
                    phoenixStage = activePhoenixStage,
                    streakDays = streakDays,
                    totalXP = totalXP,
                    heartsCount = heartsCount,
                    onMenuClick = { showSideMenu = true },
                    onPhoenixClick = onOpenPhoenixSanctuary,
                    onHeartsClick = { selectedTab = DashboardTab.SHOP }
                )
            },
            containerColor = DarkBackground
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (selectedTab) {
                    DashboardTab.LEARN -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(28.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 20.dp)
                        ) {
                            dashboardUnits.forEach { unit ->
                                item {
                                    val isUnitUnlocked = unit.id == 1 || unlockedLevels.contains(unit.nodes.first().levelNumber)
                                    UnitSectionBanner(
                                        unit = unit,
                                        isUnlocked = isUnitUnlocked,
                                        onGuidebookClick = { /* Show guidebook info */ }
                                    )
                                }

                                items(unit.nodes) { node ->
                                    val isUnlocked = unlockedLevels.contains(node.levelNumber)
                                    val isActive = node.levelNumber == activeLevelIndex

                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            modifier = Modifier.offset(x = node.xOffset),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                // Active Target Start Tooltip (matches iOS)
                                                if (isActive) {
                                                    Column(
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                        modifier = Modifier.padding(bottom = 6.dp)
                                                    ) {
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(RoundedCornerShape(12.dp))
                                                                .background(unit.themeColor)
                                                                .clickable {
                                                                    if (node.isBoss) {
                                                                        val bossId = if (unit.id < 10) "boss_unit_0${unit.id}" else "boss_unit_${unit.id}"
                                                                        onStartBoss(bossId)
                                                                    } else {
                                                                        val unitData = CourseRepository.allUnits.getOrNull(unit.id - 1)
                                                                        val nodeIndex = unit.nodes.indexOf(node)
                                                                        val lesson = unitData?.lessons?.getOrNull(nodeIndex) ?: unitData?.lessons?.firstOrNull()
                                                                        onStartLesson(lesson?.id ?: "u1_l1_what_is_ds")
                                                                    }
                                                                }
                                                                .padding(horizontal = 14.dp, vertical = 6.dp)
                                                        ) {
                                                            Text(
                                                                text = if (node.isBoss) "⚔️ BOSS BATTLE" else "START +10 XP ▶",
                                                                fontSize = 11.sp,
                                                                fontWeight = FontWeight.Black,
                                                                color = Color.White
                                                            )
                                                        }
                                                    }
                                                }

                                                LessonNodeButton(
                                                    levelNumber = node.levelNumber,
                                                    icon = node.icon,
                                                    title = node.title,
                                                    xOffset = 0.dp,
                                                    themeColor = unit.themeColor,
                                                    themeDarkColor = unit.themeDarkColor,
                                                    isUnlocked = isUnlocked,
                                                    isActive = isActive,
                                                    isBoss = node.isBoss,
                                                    onClick = {
                                                        if (isUnlocked) {
                                                            activeLevelIndex = node.levelNumber
                                                            if (node.isBoss) {
                                                                val bossId = if (unit.id < 10) "boss_unit_0${unit.id}" else "boss_unit_${unit.id}"
                                                                onStartBoss(bossId)
                                                            } else {
                                                                val unitData = CourseRepository.allUnits.getOrNull(unit.id - 1)
                                                                val nodeIndex = unit.nodes.indexOf(node)
                                                                val lesson = unitData?.lessons?.getOrNull(nodeIndex) ?: unitData?.lessons?.firstOrNull()
                                                                onStartLesson(lesson?.id ?: "u1_l1_what_is_ds")
                                                            }
                                                        }
                                                    }
                                                )

                                                Spacer(modifier = Modifier.height(6.dp))

                                                Text(
                                                    text = node.title,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isUnlocked) Color.White else SubtextGray
                                                )
                                            }

                                            // Mascot placed beside the active node
                                            if (isActive) {
                                                Spacer(modifier = Modifier.width(14.dp))
                                                PhoenixEggCompanionMascotView(
                                                    levelNumber = node.levelNumber,
                                                    isBoss = node.isBoss
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            item {
                                // End of path celebration card
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFFFFC800).copy(alpha = 0.12f))
                                        .padding(16.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "🏆", fontSize = 28.sp)
                                        Spacer(modifier = Modifier.padding(8.dp))
                                        Column {
                                            Text(
                                                text = "You're progressing through the path!",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White
                                            )
                                            Text(
                                                text = "Complete boss challenges to evolve your Phoenix!",
                                                fontSize = 12.sp,
                                                color = SubtextGray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    DashboardTab.VISUALIZER -> {
                        VisualizerScreen(modifier = Modifier.fillMaxSize())
                    }

                    DashboardTab.PRACTICE -> {
                        PracticeHubScreen(modifier = Modifier.fillMaxSize())
                    }

                    DashboardTab.LEADERBOARDS -> {
                        LeaderboardsScreen(modifier = Modifier.fillMaxSize())
                    }

                    DashboardTab.QUESTS -> {
                        QuestsScreen(modifier = Modifier.fillMaxSize())
                    }

                    DashboardTab.SHOP -> {
                        ShopScreen(modifier = Modifier.fillMaxSize())
                    }

                    DashboardTab.PROFILE -> {
                        ProfileScreen(modifier = Modifier.fillMaxSize())
                    }

                    DashboardTab.MORE -> {}
                }
            }
        }

        // Slide-Out Side Menu Drawer Overlay
        AnimatedVisibility(
            visible = showSideMenu,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable { showSideMenu = false }
            )
        }

        AnimatedVisibility(
            visible = showSideMenu,
            enter = slideInHorizontally { -it },
            exit = slideOutHorizontally { -it }
        ) {
            SideMenuDrawer(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    showSideMenu = false
                },
                onMoreClick = {
                    showSideMenu = false
                    showMoreDrawer = true
                }
            )
        }

        // More Bottom Drawer
        if (showMoreDrawer) {
            ModalBottomSheet(
                onDismissRequest = { showMoreDrawer = false },
                sheetState = bottomSheetState,
                containerColor = DarkBackground
            ) {
                MoreBottomDrawer(
                    onOpenSettings = {
                        showMoreDrawer = false
                        onOpenSettings()
                    },
                    onLogout = {
                        showMoreDrawer = false
                        onLogout()
                    }
                )
            }
        }
    }
}
