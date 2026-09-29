package com.simats.duolingo.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.simats.duolingo.data.*
import com.simats.duolingo.ui.theme.*

// ─── Tab definitions ───────────────────────────────────────────────────────────
enum class DashboardTab(val emoji: String, val label: String) {
    LEARN("🏡", "Learn"),
    LETTERS("▶️", "Code"),
    LEADERBOARDS("🛡️", "Ranks"),
    QUESTS("🎁", "Quests"),
    SHOP("💎", "Shop"),
    PROFILE("👤", "Profile"),
}

// ─── Main Dashboard Wrapper ────────────────────────────────────────────────────
@Composable
fun DashboardScreen() {
    var selectedTab by remember { mutableStateOf(DashboardTab.LEARN) }
    var showLoginSheet     by remember { mutableStateOf(false) }
    var showLangSheet      by remember { mutableStateOf(false) }
    var showLoadingScreen  by remember { mutableStateOf(false) }
    var showAssessment     by remember { mutableStateOf(false) }
    var showSideMenu       by remember { mutableStateOf(false) }

    var unlockedLevels  by remember { mutableStateOf(setOf(1, 7, 13, 18, 23)) }
    var activeLevelIdx  by remember { mutableIntStateOf(1) }
    var heartsCount     by remember { mutableIntStateOf(5) }
    var activeUnitId    by remember { mutableIntStateOf(1) }

    // Timer for heart regeneration
    LaunchedEffect(heartsCount) {
        if (heartsCount < 5) {
            kotlinx.coroutines.delay(300_000L) // 5 minutes
            heartsCount = 5
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ── Top Stats Bar ─────────────────────────────────────────────────
            DashboardTopBar(
                heartsCount = heartsCount,
                selectedLanguage = com.simats.duolingo.data.AppState.selectedLanguage,
                onMenuClick = { showSideMenu = !showSideMenu },
                onLanguageClick = { showLangSheet = true },
            )

            // ── Tab content ────────────────────────────────────────────────────
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    DashboardTab.LEARN        -> LearnTab(
                        units = allUnits,
                        unlockedLevels = unlockedLevels,
                        activeLevelIdx = activeLevelIdx,
                        onLevelTap = { unit, level ->
                            activeUnitId   = unit.id
                            activeLevelIdx = level
                            showLoadingScreen = true
                        }
                    )
                    DashboardTab.LETTERS      -> LettersScreen()
                    DashboardTab.LEADERBOARDS -> LeaderboardsScreen()
                    DashboardTab.QUESTS       -> QuestsScreen(onStartLesson = { showLoadingScreen = true })
                    DashboardTab.SHOP         -> ShopScreen(onOpenCreateProfile = { showLoginSheet = true })
                    DashboardTab.PROFILE      -> ProfileScreen(onOpenCreateProfile = { showLoginSheet = true })
                }
            }

            // ── Bottom navigation bar ─────────────────────────────────────────
            BottomNavBar(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
        }

        // ── Side menu overlay ─────────────────────────────────────────────────
        if (showSideMenu) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable { showSideMenu = false }
            )
            SideMenuDrawer(
                onClose = { showSideMenu = false },
                onSettings = { showSideMenu = false },
                onLogout = {
                    com.simats.duolingo.data.AppState.isLoggedIn = false
                    showSideMenu = false
                },
            )
        }

        // ── Modals ─────────────────────────────────────────────────────────────
        if (showLangSheet) {
            Box(modifier = Modifier.fillMaxSize()) {
                LanguagePickerSheet(
                    onDismiss = { showLangSheet = false },
                    onLanguageSelected = { showLangSheet = false }
                )
            }
        }

        if (showLoginSheet) {
            Box(modifier = Modifier.fillMaxSize()) {
                LoginSheet(onDismiss = { showLoginSheet = false })
            }
        }

        if (showLoadingScreen) {
            Box(modifier = Modifier.fillMaxSize()) {
                LoadingScreen(onFinished = {
                    showLoadingScreen = false
                    showAssessment = true
                })
            }
        }

        if (showAssessment) {
            Box(modifier = Modifier.fillMaxSize()) {
                AssessmentScreen(
                    unitId = activeUnitId,
                    levelNumber = activeLevelIdx,
                    heartsCount = heartsCount,
                    onHeartLost = { if (heartsCount > 0) heartsCount-- },
                    onComplete = {
                        showAssessment = false
                        val next = activeLevelIdx + 1
                        unlockedLevels = unlockedLevels + next
                        activeLevelIdx = next
                    },
                    onDismiss = { showAssessment = false }
                )
            }
        }
    }
}

// ─── Top Stats Bar ─────────────────────────────────────────────────────────────
@Composable
private fun DashboardTopBar(
    heartsCount: Int,
    selectedLanguage: com.simats.duolingo.data.Language?,
    onMenuClick: () -> Unit,
    onLanguageClick: () -> Unit,
) {
    val streak = com.simats.duolingo.data.AppState.dayStreak

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DuolingoDarkBg)
            .border(BorderStroke(1.dp, DuolingoInputBorder.copy(0.6f)), RoundedCornerShape(0.dp))
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hamburger
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(DuolingoCardBg)
                .border(1.dp, DuolingoInputBorder, CircleShape)
                .clickable(onClick = onMenuClick),
            contentAlignment = Alignment.Center
        ) {
            Text("☰", color = Color.White, fontSize = 16.sp)
        }

        Spacer(Modifier.weight(1f))

        // Language selector
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
            Text(selectedLanguage?.flagEmoji ?: "🌐", fontSize = 20.sp)
            Text("▾", color = DuolingoSubtext, fontSize = 9.sp)
        }

        // Streak
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("🔥", fontSize = 16.sp)
            Text("$streak", color = if (streak > 0) DuolingoOrange else DuolingoSubtext,
                fontSize = 15.sp, fontWeight = FontWeight.Black)
        }

        // Gems
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("💎", fontSize = 16.sp)
            Text("500", color = DuolingoBlue, fontSize = 15.sp, fontWeight = FontWeight.Black)
        }

        // Hearts
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("❤️", fontSize = 16.sp)
            Text("$heartsCount", color = Color.Red, fontSize = 15.sp, fontWeight = FontWeight.Black)
        }
    }
}

// ─── Bottom Nav Bar ────────────────────────────────────────────────────────────
@Composable
private fun BottomNavBar(selectedTab: DashboardTab, onTabSelected: (DashboardTab) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DuolingoHeaderBg)
            .border(BorderStroke(1.dp, DuolingoInputBorder.copy(0.4f)), RoundedCornerShape(0.dp))
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        DashboardTab.entries.forEach { tab ->
            val isSelected = tab == selectedTab
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onTabSelected(tab) }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(tab.emoji, fontSize = if (isSelected) 22.sp else 20.sp)
                Text(
                    tab.label,
                    color = if (isSelected) DuolingoGreen else DuolingoSubtext,
                    fontSize = 10.sp,
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                )
            }
        }
    }
}

// ─── Learn Tab (scrollable unit path) ─────────────────────────────────────────
@Composable
private fun LearnTab(
    units: List<UnitModel>,
    unlockedLevels: Set<Int>,
    activeLevelIdx: Int,
    onLevelTap: (UnitModel, Int) -> Unit,
) {
    LazyColumn(
        state = rememberLazyListState(),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        items(units.size) { i ->
            UnitSection(
                unit = units[i],
                unlockedLevels = unlockedLevels,
                activeLevelIdx = activeLevelIdx,
                onLevelTap = { lvl -> onLevelTap(units[i], lvl) }
            )
        }
        item { Spacer(Modifier.height(20.dp)) }
    }
}

// ─── Unit Section ──────────────────────────────────────────────────────────────
@Composable
private fun UnitSection(
    unit: UnitModel,
    unlockedLevels: Set<Int>,
    activeLevelIdx: Int,
    onLevelTap: (Int) -> Unit,
) {
    val themeColor = Color(unit.themeColorHex)
    val darkColor  = Color(unit.themeDarkColorHex)

    Column(modifier = Modifier.fillMaxWidth()) {
        // Unit header banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.linearGradient(listOf(themeColor, darkColor)))
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Column {
                Text("SECTION ${unit.sectionNumber}, UNIT ${unit.unitNumber}",
                    color = Color.White.copy(0.8f), fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                Text(unit.titleDefault, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
            }
        }

        Spacer(Modifier.height(16.dp))

        // Lesson nodes path (zigzag)
        unit.nodes.forEach { node ->
            val isUnlocked = node.levelNumber in unlockedLevels
            val isActive   = node.levelNumber == activeLevelIdx

            LessonNode(
                node      = node,
                isUnlocked = isUnlocked,
                isActive  = isActive,
                themeColor = themeColor,
                onClick   = { if (isUnlocked) onLevelTap(node.levelNumber) }
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─── Individual Lesson Node ────────────────────────────────────────────────────
@Composable
private fun LessonNode(
    node: LessonNodeItem,
    isUnlocked: Boolean,
    isActive: Boolean,
    themeColor: Color,
    onClick: () -> Unit,
) {
    val nodeEmoji = when (node.icon) {
        NodeIcon.STAR      -> if (isUnlocked) "⭐" else "🔒"
        NodeIcon.BOOK      -> if (isUnlocked) "📖" else "🔒"
        NodeIcon.DUMBBELL  -> if (isUnlocked) "🏋️" else "🔒"
        NodeIcon.CHEST     -> if (isUnlocked) "📦" else "🔒"
        NodeIcon.HEADPHONES -> if (isUnlocked) "🎧" else "🔒"
        NodeIcon.TROPHY    -> if (isUnlocked) "🏆" else "🔒"
    }

    val pulse = rememberInfiniteTransition(label = "pulse")
    val pulsScale by pulse.animateFloat(
        initialValue = 1f, targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(700, easing = EaseInOut), RepeatMode.Reverse),
        label = "pulsScale"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .offset(x = node.xOffsetDp.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(if (isActive) 72.dp else 64.dp)
                .scale(if (isActive) pulsScale else 1f)
                .clip(CircleShape)
                .background(
                    when {
                        isActive   -> themeColor
                        isUnlocked -> themeColor.copy(alpha = 0.85f)
                        else       -> DuolingoInputBg
                    }
                )
                .border(
                    width = if (isActive) 4.dp else 2.dp,
                    color = when {
                        isActive   -> Color.White.copy(0.6f)
                        isUnlocked -> themeColor.copy(0.5f)
                        else       -> DuolingoInputBorder
                    },
                    shape = CircleShape
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(nodeEmoji, fontSize = if (isActive) 28.sp else 24.sp)
        }
    }
}

// ─── Side Menu Drawer ──────────────────────────────────────────────────────────
@Composable
private fun SideMenuDrawer(
    onClose: () -> Unit,
    onSettings: () -> Unit,
    onLogout: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(DuolingoCardBg)
            .padding(24.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Menu", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
                Text("✕", color = DuolingoSubtext, fontSize = 20.sp, modifier = Modifier.clickable(onClick = onClose))
            }

            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DuolingoInputBorder))

            listOf(
                "⚙️" to "Settings",
                "🌐" to "Change Language",
                "📢" to "Invite Friends",
                "🔒" to "Privacy Policy",
            ).forEach { (emoji, label) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onSettings)
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(emoji, fontSize = 20.sp)
                    Text(label, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.weight(1f))

            // Logout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onLogout)
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🚪", fontSize = 20.sp)
                Text("Log out", color = Color.Red, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
