package com.simats.duolingo.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.duolingo.R
import com.simats.duolingo.data.*
import com.simats.duolingo.ui.components.*
import com.simats.duolingo.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ─── Tab definitions ───────────────────────────────────────────────────────────
enum class DashboardTab(val emoji: String, val label: String) {
    LEARN("🏡", "LEARN"),
    LETTERS("▶️", "VIDEOS"),
    LEADERBOARDS("🛡️", "LEADERBOARDS"),
    QUESTS("🎁", "QUESTS"),
    SHOP("💎", "SHOP"),
    PROFILE("👤", "PROFILE"),
    MORE("💬", "MORE")
}

// ─── Main Dashboard Screen ─────────────────────────────────────────────────────
@Composable
fun DashboardScreen() {
    var selectedTab by remember { mutableStateOf(DashboardTab.LEARN) }
    var showSideMenu by remember { mutableStateOf(false) }
    var showLoginSheet by remember { mutableStateOf(false) }
    var showSignupSheet by remember { mutableStateOf(false) }
    var showLangSheet by remember { mutableStateOf(false) }
    var showLoadingScreen by remember { mutableStateOf(false) }
    var showAssessment by remember { mutableStateOf(false) }
    var showMoreBottomDrawer by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    var showPhoenixSanctuary by remember { mutableStateOf(false) }

    var selectedLockedNodeId by remember { mutableStateOf<Int?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // 1-second timer for Heart Lives regeneration
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            AppState.tickHeartTimer()
        }
    }

    val activeUnitId = remember(AppState.activeLevelIndex) {
        allUnits.firstOrNull { unit ->
            unit.nodes.any { it.levelNumber == AppState.activeLevelIndex }
        }?.id ?: 1
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DuolingoDarkBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ── 1. Top Stats Header ───────────────────────────────────────────
            DashboardTopBar(
                onMenuClick = { showSideMenu = true },
                onOpenSanctuary = { showPhoenixSanctuary = true },
                onLanguageClick = { showLangSheet = true }
            )

            // ── Tab Content ───────────────────────────────────────────────────
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    DashboardTab.LEARN -> {
                        LazyColumn(
                            state = listState,
                            contentPadding = PaddingValues(vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(28.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(allUnits.size) { i ->
                                val unit = allUnits[i]
                                UnitSectionView(
                                    unit = unit,
                                    activeLevelIdx = AppState.activeLevelIndex,
                                    unlockedLevels = AppState.unlockedLevelIndices,
                                    selectedLockedNodeId = selectedLockedNodeId,
                                    onNodeClick = { node, isUnlocked ->
                                        if (isUnlocked) {
                                            AppState.activeLevelIndex = node.levelNumber
                                            selectedLockedNodeId = null
                                        } else {
                                            selectedLockedNodeId = if (selectedLockedNodeId == node.id) null else node.id
                                        }
                                    },
                                    onStartActiveLesson = {
                                        showLoadingScreen = true
                                    },
                                    onJumpHere = {
                                        unit.nodes.firstOrNull()?.let { firstNode ->
                                            AppState.activeLevelIndex = firstNode.levelNumber
                                            selectedLockedNodeId = null
                                            coroutineScope.launch {
                                                listState.animateScrollToItem(i)
                                            }
                                        }
                                    },
                                    onOpenSanctuary = { showPhoenixSanctuary = true }
                                )
                            }

                            // Footer Links
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        listOf("ABOUT", "•", "BLOG", "•", "STORE", "•", "TERMS", "•", "PRIVACY").forEach { link ->
                                            Text(
                                                text = link,
                                                color = DuolingoSubtext.copy(alpha = 0.7f),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    DashboardTab.LETTERS -> LettersScreen()
                    DashboardTab.LEADERBOARDS -> LeaderboardsScreen(onStartLesson = { showLoadingScreen = true })
                    DashboardTab.QUESTS -> QuestsScreen(onStartLesson = { showLoadingScreen = true })
                    DashboardTab.SHOP -> ShopScreen(onOpenCreateProfile = { showSignupSheet = true })
                    DashboardTab.PROFILE -> {
                        if (AppState.isLoggedIn) {
                            ProfileScreen(onOpenCreateProfile = { showSignupSheet = true })
                        } else {
                            ProfileCreationSheet(
                                onDismiss = { selectedTab = DashboardTab.LEARN },
                                onOpenLogin = { showLoginSheet = true }
                            )
                        }
                    }
                    DashboardTab.MORE -> {}
                }
            }
        }

        // ── Slide-Out Side Menu Drawer ─────────────────────────────────────────
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
            enter = slideInHorizontally(initialOffsetX = { -it }),
            exit = slideOutHorizontally(targetOffsetX = { -it })
        ) {
            SideMenuDrawer(
                selectedTab = selectedTab,
                onSelectTab = { tab ->
                    showSideMenu = false
                    if (tab == DashboardTab.MORE) {
                        showMoreBottomDrawer = true
                    } else {
                        selectedTab = tab
                    }
                }
            )
        }

        // ── More Bottom Drawer Sheet ──────────────────────────────────────────
        if (showMoreBottomDrawer) {
            MoreBottomDrawer(
                onDismiss = { showMoreBottomDrawer = false },
                onSettings = {
                    showMoreBottomDrawer = false
                    showSettingsSheet = true
                },
                onLogout = {
                    showMoreBottomDrawer = false
                    AppState.resetProfileData()
                    selectedTab = DashboardTab.PROFILE
                    AppState.toastMessage = "Logged out successfully"
                    coroutineScope.launch {
                        delay(2500)
                        AppState.toastMessage = null
                    }
                }
            )
        }

        // ── Modals & Sheets ───────────────────────────────────────────────────
        if (showPhoenixSanctuary) {
            PhoenixSanctuarySheet(onDismiss = { showPhoenixSanctuary = false })
        }

        if (showSettingsSheet) {
            SettingsSheet(onDismiss = { showSettingsSheet = false })
        }

        if (showLangSheet) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(0.6f))
                    .clickable(onClick = { showLangSheet = false }),
                contentAlignment = Alignment.BottomCenter
            ) {
                LanguagePickerSheet(
                    onDismiss = { showLangSheet = false },
                    onLanguageSelected = { showLangSheet = false }
                )
            }
        }

        if (showLoginSheet) {
            LoginSheet(onDismiss = { showLoginSheet = false })
        }

        if (showSignupSheet) {
            ProfileCreationSheet(
                onDismiss = { showSignupSheet = false },
                onOpenLogin = {
                    showSignupSheet = false
                    showLoginSheet = true
                }
            )
        }

        if (showLoadingScreen) {
            LoadingScreen(onFinished = {
                showLoadingScreen = false
                showAssessment = true
            })
        }

        if (showAssessment) {
            AssessmentScreen(
                unitId = activeUnitId,
                levelNumber = AppState.activeLevelIndex,
                heartsCount = AppState.heartsCount,
                onHeartLost = { AppState.loseHeart() },
                onComplete = {
                    showAssessment = false
                    AppState.unlockNextLevel()
                },
                onDismiss = { showAssessment = false }
            )
        }

        // ── Global Toast ──────────────────────────────────────────────────────
        AnimatedVisibility(
            visible = AppState.toastMessage != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        ) {
            AppState.toastMessage?.let { msg ->
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(DuolingoCardBg)
                        .border(1.5.dp, DuolingoGreen, RoundedCornerShape(16.dp))
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("✓", color = DuolingoGreen, fontWeight = FontWeight.Bold)
                    Text(msg, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ─── 1. Top Stats Header ───────────────────────────────────────────────────────
@Composable
private fun DashboardTopBar(
    onMenuClick: () -> Unit,
    onOpenSanctuary: () -> Unit,
    onLanguageClick: () -> Unit,
) {
    val streak = AppState.dayStreak
    val activePhoenix = AppState.activePhoenix

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DuolingoDarkBg)
            .border(BorderStroke(1.dp, DuolingoInputBorder.copy(0.6f)), RoundedCornerShape(0.dp))
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Hamburger Drawer Toggle
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(DuolingoCardBg)
                .border(1.dp, DuolingoInputBorder, CircleShape)
                .clickable(onClick = onMenuClick),
            contentAlignment = Alignment.Center
        ) {
            Text("☰", color = Color.White, fontSize = 16.sp)
        }

        Spacer(Modifier.weight(1f))

        // Phoenix Sanctuary Companion Button
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(DuolingoCardBg)
                .border(1.dp, Color(0xFFFF9600).copy(0.7f), RoundedCornerShape(10.dp))
                .clickable(onClick = onOpenSanctuary)
                .padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Image(
                painter = painterResource(id = activePhoenix.drawableResId),
                contentDescription = "Companion",
                modifier = Modifier.size(20.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = "Stage ${activePhoenix.id}",
                color = Color(0xFFFF9600),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black
            )
        }

        // Language Dropdown
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(DuolingoCardBg)
                .border(1.dp, DuolingoInputBorder, RoundedCornerShape(10.dp))
                .clickable(onClick = onLanguageClick)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(AppState.selectedLanguage?.flagEmoji ?: "🐍", fontSize = 18.sp)
            Text("▾", color = DuolingoSubtext, fontSize = 9.sp)
        }

        // Day Streak
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            Text("🔥", fontSize = 16.sp)
            Text(
                text = "$streak",
                color = if (streak > 0) Color(0xFFFF9600) else DuolingoSubtext,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black
            )
        }

        // Gems
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            Text("💎", fontSize = 16.sp)
            Text("500", color = DuolingoBlue, fontSize = 15.sp, fontWeight = FontWeight.Black)
        }

        // Hearts with 5-Minute Timer
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            Text("❤️", fontSize = 16.sp)
            if (AppState.heartsCount < 5 && AppState.isHeartTimerActive) {
                val m = AppState.heartTimerRemainingSeconds / 60
                val s = AppState.heartTimerRemainingSeconds % 60
                val timeStr = String.format("%02d:%02d", m, s)
                Text(
                    text = "${AppState.heartsCount} ($timeStr)",
                    color = Color.Red,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Black
                )
            } else {
                Text(
                    text = "${AppState.heartsCount}",
                    color = Color.Red,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

// ─── Unit Section View ─────────────────────────────────────────────────────────
@Composable
private fun UnitSectionView(
    unit: UnitModel,
    activeLevelIdx: Int,
    unlockedLevels: Set<Int>,
    selectedLockedNodeId: Int?,
    onNodeClick: (LessonNodeItem, Boolean) -> Unit,
    onStartActiveLesson: () -> Unit,
    onJumpHere: () -> Unit,
    onOpenSanctuary: () -> Unit,
) {
    val themeColor = Color(unit.themeColorHex)
    val darkColor = Color(unit.themeDarkColorHex)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Section Header Banner Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(themeColor)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SECTION ${unit.sectionNumber}, UNIT ${unit.unitNumber}".uppercase(),
                    color = Color.White.copy(0.9f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(0.25f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📖", fontSize = 12.sp)
                        Text("Guidebook", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Text(
                text = unit.titleDefault,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
        }

        // JUMP HERE? Pill Callout Banner on units > 1
        if (unit.id > 1) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(themeColor)
                    .border(1.5.dp, Color.White.copy(0.3f), RoundedCornerShape(20.dp))
                    .clickable(onClick = onJumpHere)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("⏩", fontSize = 12.sp)
                Text(
                    text = "JUMP HERE?",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        // Winding 3D Level Node Path
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            unit.nodes.forEach { node ->
                val isUnlocked = node.levelNumber in unlockedLevels
                val isActive = node.levelNumber == activeLevelIdx
                val isLockedSelected = selectedLockedNodeId == node.id

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(x = node.xOffsetDp.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // 1. ACTIVE UNLOCKED TARGET TOOLTIP (START +10 XP)
                        if (isActive && selectedLockedNodeId == null) {
                            Column(
                                modifier = Modifier
                                    .width(210.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(themeColor)
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = unit.titleDefault,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    textAlign = TextAlign.Center
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White)
                                        .clickable(onClick = onStartActiveLesson)
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "START +10 XP",
                                        color = themeColor,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }

                        // 2. LOCKED LEVEL POPOVER TOOLTIP
                        if (isLockedSelected) {
                            Column(
                                modifier = Modifier
                                    .width(220.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(DuolingoCardBg)
                                    .border(1.5.dp, DuolingoInputBorder, RoundedCornerShape(18.dp))
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = node.title,
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Complete all levels above to unlock this!",
                                    color = DuolingoSubtext,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(40.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(20, 32, 38))
                                        .border(1.dp, DuolingoInputBorder, RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "LOCKED",
                                        color = DuolingoSubtext,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }

                        // 3D Pushable Node Button
                        Pushable3DNodeButton(
                            node = node,
                            themeColor = themeColor,
                            themeDarkColor = darkColor,
                            isUnlocked = isUnlocked,
                            isActive = isActive,
                            onClick = { onNodeClick(node, isUnlocked) }
                        )
                    }

                    // STRICTLY ONE MASCOT ANIMATION VIEW AT THE ACTIVE TARGET NODE!
                    if (isActive) {
                        Spacer(modifier = Modifier.width(14.dp))
                        when (unit.characterType) {
                            UnitCharacterType.DUO_BACKPACK -> DuoBackpackMascotView(onOpenSanctuary = onOpenSanctuary)
                            UnitCharacterType.LILY_PURPLE  -> LilyPurpleMascotView()
                            UnitCharacterType.VIKRAM_BEES  -> VikramBeesMascotView()
                            UnitCharacterType.OSCAR_ARTIST -> OscarArtistMascotView()
                            UnitCharacterType.JUNIOR_PARTY -> JuniorPartyMascotView()
                        }
                    }
                }
            }
        }

        // Section Divider Line
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = DuolingoInputBorder)
            Text(
                text = "UNIT ${unit.unitNumber} COMPLETE",
                color = DuolingoSubtext,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = DuolingoInputBorder)
        }
    }
}

// ─── 3D Bevel Pushable Node Button Component ──────────────────────────────────
@Composable
private fun Pushable3DNodeButton(
    node: LessonNodeItem,
    themeColor: Color,
    themeDarkColor: Color,
    isUnlocked: Boolean,
    isActive: Boolean,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val faceColor = if (isUnlocked) themeColor else DuolingoCardBg
    val shadowColor = if (isUnlocked) themeDarkColor else Color(36, 56, 66)

    val iconText = when {
        !isUnlocked -> "🔒"
        node.icon == NodeIcon.STAR -> "⭐"
        node.icon == NodeIcon.BOOK -> "📖"
        node.icon == NodeIcon.DUMBBELL -> "🏋️"
        node.icon == NodeIcon.CHEST -> "📦"
        node.icon == NodeIcon.HEADPHONES -> "🎧"
        node.icon == NodeIcon.TROPHY -> "🏆"
        else -> "⭐"
    }

    Box(
        modifier = Modifier
            .size(76.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Active Pulsing Outer Ring
        if (isActive) {
            val infiniteTransition = rememberInfiniteTransition(label = "pulseRing")
            val ringScale by infiniteTransition.animateFloat(
                initialValue = 1f, targetValue = 1.15f,
                animationSpec = infiniteRepeatable(tween(800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
                label = "ringScale"
            )
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .scale(ringScale)
                    .border(4.dp, themeColor.copy(alpha = 0.5f), CircleShape)
            )
        }

        // 3D Depth Shadow Extrusion
        Box(
            modifier = Modifier
                .size(68.dp)
                .offset(y = if (isPressed) 2.dp else 7.dp)
                .clip(CircleShape)
                .background(shadowColor)
        )

        // 3D Top Surface Button Face
        Box(
            modifier = Modifier
                .size(68.dp)
                .offset(y = if (isPressed) 5.dp else 0.dp)
                .clip(CircleShape)
                .background(faceColor)
                .border(
                    width = 2.dp,
                    color = if (isUnlocked) Color.White.copy(0.25f) else DuolingoInputBorder,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(iconText, fontSize = 24.sp)
        }
    }
}

// ─── Side Menu Drawer ──────────────────────────────────────────────────────────
@Composable
private fun SideMenuDrawer(
    selectedTab: DashboardTab,
    onSelectTab: (DashboardTab) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(250.dp)
            .background(DuolingoDarkBg)
            .border(BorderStroke(1.dp, DuolingoInputBorder), RoundedCornerShape(0.dp))
            .statusBarsPadding()
            .padding(vertical = 16.dp, horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Code in Go",
            color = DuolingoGreen,
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )

        HorizontalDivider(color = DuolingoInputBorder)

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            DashboardTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) DuolingoBlue.copy(0.18f) else Color.Transparent)
                        .border(
                            width = 1.5.dp,
                            color = if (isSelected) DuolingoBlue.copy(0.4f) else Color.Transparent,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { onSelectTab(tab) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(tab.emoji, fontSize = 22.sp)
                    Text(
                        text = tab.label,
                        color = if (isSelected) DuolingoBlue else Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

// ─── More Bottom Drawer Sheet ─────────────────────────────────────────────────
@Composable
private fun MoreBottomDrawer(
    onDismiss: () -> Unit,
    onSettings: () -> Unit,
    onLogout: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(0.6f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(DuolingoDarkBg)
                .clickable(enabled = false) {}
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(DuolingoCardBg)
                    .border(1.5.dp, DuolingoInputBorder, RoundedCornerShape(20.dp))
            ) {
                // Settings Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onSettings)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(DuolingoBlue.copy(0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("⚙️", fontSize = 18.sp)
                    }
                    Text("SETTINGS", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.weight(1f))
                    Text("›", color = DuolingoSubtext, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }

                HorizontalDivider(color = DuolingoInputBorder.copy(0.6f))

                // Logout Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onLogout)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Red.copy(0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🚪", fontSize = 18.sp)
                    }
                    Text("LOGOUT", color = Color.Red, fontSize = 16.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.weight(1f))
                    Text("›", color = Color.Red.copy(0.6f), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
