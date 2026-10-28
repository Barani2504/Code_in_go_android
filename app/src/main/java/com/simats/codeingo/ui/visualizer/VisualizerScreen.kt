package com.simats.codeingo.ui.visualizer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.data.model.DSA5ChapterData
import com.simats.codeingo.data.model.DSAChapterModel
import com.simats.codeingo.ui.gamification.DSAChapterStoryIntroSheet
import com.simats.codeingo.ui.theme.liquidGlassCard
import com.simats.codeingo.ui.theme.pressScale
import com.simats.codeingo.ui.theme.tilt3D
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.R
import com.simats.codeingo.data.model.DSAVisualizerType
import com.simats.codeingo.domain.LocalizationManager
import com.simats.codeingo.ui.components.DuolingoButton
import com.simats.codeingo.ui.phoenix.PhoenixAtmosphericBackgroundView
import com.simats.codeingo.ui.visualizer.CNSLabModeTabBar
import com.simats.codeingo.ui.visualizer.model.CNSLabMode
import com.simats.codeingo.ui.theme.AmberGold
import com.simats.codeingo.ui.theme.AppFontFamily
import com.simats.codeingo.ui.theme.CardBackground
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DsaBlue
import com.simats.codeingo.ui.theme.DsaGreen
import com.simats.codeingo.ui.theme.DsaOrange
import com.simats.codeingo.ui.theme.DsaPurple
import com.simats.codeingo.ui.theme.DsaRed
import com.simats.codeingo.ui.theme.DsaTeal
import com.simats.codeingo.ui.theme.DuolingoGreen
import com.simats.codeingo.ui.theme.DuolingoRed
import com.simats.codeingo.ui.theme.InputBorder
import com.simats.codeingo.ui.theme.PhoenixGreen
import com.simats.codeingo.ui.theme.SubtextGray
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors

enum class VisualizerDisplayMode {
    LIST,
    VISUALIZER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisualizerScreen(
    modifier: Modifier = Modifier,
    onOpenArrayKingdom: (() -> Unit)? = null,
    onOpenStackTower: (() -> Unit)? = null,
    onOpenQueueStation: (() -> Unit)? = null,
    onOpenLinkedListRoad: (() -> Unit)? = null,
    onOpenBinaryTreeForest: (() -> Unit)? = null
) {
    val localizationManager = LocalizationManager.instance
    val selectedLanguageCode by localizationManager.selectedLanguageCode.collectAsState()
    val selectedLangObj by localizationManager.selectedLanguage.collectAsState()

    val gameManager = GameManager.instance
    val maxUnlockedChapter by gameManager.maxUnlockedChapter.collectAsState()
    val completedChapters by gameManager.completedChapters.collectAsState()

    var activePopupChapter by remember { mutableStateOf<DSAChapterModel?>(null) }
    var selectedStoryChapter by remember { mutableStateOf<DSAChapterModel?>(null) }

    var displayMode by remember { mutableStateOf(VisualizerDisplayMode.LIST) }
    var selectedType by remember { mutableStateOf(DSAVisualizerType.STACK) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val currentLanguageName = selectedLangObj?.name ?: when (selectedLanguageCode) {
        "es" -> "Spanish"
        "fr" -> "French"
        "de" -> "German"
        "ja" -> "Japanese"
        else -> "Python"
    }

    val categories = listOf("All", "Classical", "Symmetric", "Asymmetric", "Hashing & Protocols")

    val filteredStructures = remember(searchQuery, selectedCategory) {
        DSAVisualizerType.PEDAGOGICAL_ORDER.filter { type ->
            val matchesCategory = selectedCategory == "All" || type.filterCategory == selectedCategory
            val query = searchQuery.trim().lowercase()
            val matchesSearch = query.isEmpty() ||
                type.title.lowercase().contains(query) ||
                type.tagline.lowercase().contains(query) ||
                type.category.lowercase().contains(query) ||
                type.shortDescription.lowercase().contains(query)
            matchesCategory && matchesSearch
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LocalDynamicThemeColors.current.background)
    ) {
        PhoenixAtmosphericBackgroundView()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header Bar
            VisualizerHeaderBar(
                displayMode = displayMode,
                onBackToList = { displayMode = VisualizerDisplayMode.LIST }
            )

            if (displayMode == VisualizerDisplayMode.LIST) {
                // Topic List View with 5-Chapter Progressive Roadmap
                TopicListView(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelect = { selectedCategory = it },
                    filteredStructures = filteredStructures,
                    maxUnlockedChapter = maxUnlockedChapter,
                    completedChapters = completedChapters,
                    onSelectTopic = { type ->
                        selectedType = type
                        displayMode = VisualizerDisplayMode.VISUALIZER
                    },
                    onChapterTap = { chapter ->
                        activePopupChapter = chapter
                    },
                    onUnlockNextChapter = { nextId ->
                        gameManager.unlockNextChapter(nextId)
                    },
                    onResetProgression = {
                        gameManager.resetChapterProgression()
                    },
                    onOpenArrayKingdom = onOpenArrayKingdom,
                    onOpenStackTower = onOpenStackTower,
                    onOpenQueueStation = onOpenQueueStation,
                    onOpenLinkedListRoad = onOpenLinkedListRoad,
                    onOpenBinaryTreeForest = onOpenBinaryTreeForest
                )
            } else {
                // Topic Detail View
                TopicDetailView(
                    selectedType = selectedType,
                    onSelectType = { selectedType = it },
                    onBackToList = { displayMode = VisualizerDisplayMode.LIST },
                    currentLanguageName = currentLanguageName
                )
            }
        }

        // Active Chapter Popup Modal
        activePopupChapter?.let { chapter ->
            Dialog(
                onDismissRequest = { activePopupChapter = null },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                DSAChapterPopupCard(
                    chapter = chapter,
                    isCompleted = completedChapters.contains(chapter.id),
                    onPlay3D = {
                        activePopupChapter = null
                        when (chapter.id) {
                            1 -> onOpenArrayKingdom?.invoke()
                            2 -> onOpenLinkedListRoad?.invoke()
                            3 -> onOpenStackTower?.invoke()
                            4 -> onOpenQueueStation?.invoke()
                            5 -> onOpenBinaryTreeForest?.invoke()
                        }
                    },
                    onLearnVisualizer = {
                        activePopupChapter = null
                        selectedType = when (chapter.id) {
                            1 -> DSAVisualizerType.ARRAY
                            2 -> DSAVisualizerType.LINKED_LIST
                            3 -> DSAVisualizerType.STACK
                            4 -> DSAVisualizerType.QUEUE
                            5 -> DSAVisualizerType.BINARY_TREE
                            else -> DSAVisualizerType.ARRAY
                        }
                        displayMode = VisualizerDisplayMode.VISUALIZER
                    },
                    onOpenStory = {
                        activePopupChapter = null
                        selectedStoryChapter = chapter
                    },
                    onCompleteLevel = {
                        gameManager.markChapterCompleted(chapter.id)
                        activePopupChapter = null
                    },
                    onDismiss = { activePopupChapter = null }
                )
            }
        }

        // Chapter Field Story Modal
        selectedStoryChapter?.let { chapter ->
            Dialog(
                onDismissRequest = { selectedStoryChapter = null },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                DSAChapterStoryIntroSheet(
                    chapter = chapter,
                    onStartFirstLevel = {
                        selectedStoryChapter = null
                        when (chapter.id) {
                            1 -> onOpenArrayKingdom?.invoke()
                            2 -> onOpenLinkedListRoad?.invoke()
                            3 -> onOpenStackTower?.invoke()
                            4 -> onOpenQueueStation?.invoke()
                            5 -> onOpenBinaryTreeForest?.invoke()
                        }
                    },
                    onDismiss = { selectedStoryChapter = null }
                )
            }
        }
    }
}

@Composable
private fun VisualizerHeaderBar(
    displayMode: VisualizerDisplayMode,
    onBackToList: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (displayMode == VisualizerDisplayMode.VISUALIZER) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(DsaBlue.copy(alpha = 0.20f))
                    .border(1.5.dp, DsaBlue.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .clickable { onBackToList() }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = LocalDynamicThemeColors.current.textPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "All Topics",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = AppFontFamily,
                        color = LocalDynamicThemeColors.current.textPrimary
                    )
                }
            }
        } else {
            Column {
                Text(
                    text = "CNS Visualizer",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = AppFontFamily,
                    color = LocalDynamicThemeColors.current.textPrimary
                )
                Text(
                    text = "Interactive Cryptography & Network Security Lab",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = AppFontFamily,
                    color = SubtextGray
                )
            }
        }

        // Active Lab Status Indicator Pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(DsaBlue.copy(alpha = 0.15f))
                .border(1.2.dp, DsaBlue.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 7.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(PhoenixGreen)
                )
                Text(
                    text = "ACTIVE LAB",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = AppFontFamily,
                    color = DsaBlue,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
private fun TopicListView(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    categories: List<String>,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit,
    filteredStructures: List<DSAVisualizerType>,
    maxUnlockedChapter: Int,
    completedChapters: Set<Int>,
    onSelectTopic: (DSAVisualizerType) -> Unit,
    onChapterTap: (DSAChapterModel) -> Unit,
    onUnlockNextChapter: (Int) -> Unit,
    onResetProgression: () -> Unit,
    onOpenArrayKingdom: (() -> Unit)?,
    onOpenStackTower: (() -> Unit)?,
    onOpenQueueStation: (() -> Unit)?,
    onOpenLinkedListRoad: (() -> Unit)?,
    onOpenBinaryTreeForest: (() -> Unit)?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        // Section Title: Interactive Visualizer Directory
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⚡ EXPLORE CRYPTOGRAPHY & NETWORK SECURITY",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = AmberGold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackground.copy(alpha = 0.75f))
                .border(1.2.dp, InputBorder, RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
                BasicTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.weight(1f),
                    textStyle = TextStyle(
                        color = LocalDynamicThemeColors.current.textPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    singleLine = true,
                    cursorBrush = SolidColor(AmberGold),
                    decorationBox = { innerTextField ->
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Search ciphers, hashing, keys, protocols...",
                                color = SubtextGray,
                                fontSize = 13.sp
                            )
                        }
                        innerTextField()
                    }
                )
                if (searchQuery.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = SubtextGray,
                        modifier = Modifier
                            .size(18.dp)
                            .clickable { onSearchQueryChange("") }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Category Filter Pills
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategory == cat
                val shape = RoundedCornerShape(20.dp)
                Box(
                    modifier = Modifier
                        .clip(shape)
                        .background(if (isSelected) DsaBlue else CardBackground.copy(alpha = 0.7f))
                        .border(1.2.dp, if (isSelected) DsaBlue else InputBorder, shape)
                        .clickable { onCategorySelect(cat) }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = cat,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isSelected) Color.White else SubtextGray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))



        // Directory Info Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(CardBackground.copy(alpha = 0.65f))
                .border(1.2.dp, DsaBlue.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(DsaBlue.copy(alpha = 0.22f))
                        .border(1.dp, LocalDynamicThemeColors.current.placeholder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "✨", fontSize = 18.sp)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Interactive CNS Directory",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = LocalDynamicThemeColors.current.textPrimary
                    )
                    Text(
                        text = "Tap any cryptographic topic below to open its interactive visualizer & live code",
                        fontSize = 12.sp,
                        color = SubtextGray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Cards list
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            filteredStructures.forEach { type ->
                DSATopicListCard(
                    type = type,
                    onSelect = { onSelectTopic(type) }
                )
            }
        }

        Spacer(modifier = Modifier.height(110.dp))
    }
}

@Composable
private fun VisualizerGameArenasRow(
    maxUnlockedChapter: Int,
    onOpenArrayKingdom: (() -> Unit)?,
    onOpenStackTower: (() -> Unit)?,
    onOpenQueueStation: (() -> Unit)?,
    onOpenLinkedListRoad: (() -> Unit)?,
    onOpenBinaryTreeForest: (() -> Unit)?
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Array Kingdom (Chapter 1)
        item {
            ArenaCard(
                chapterId = 1,
                maxUnlockedChapter = maxUnlockedChapter,
                title = "🏛️ CIPHER SANCTUM",
                subtitle = "Classical Ciphers & Radio",
                accentColor = AmberGold,
                emotionRes = R.drawable.phoenix_emotion_4,
                onClick = { onOpenArrayKingdom?.invoke() }
            )
        }
        // Block Cipher Forge (Chapter 2)
        item {
            ArenaCard(
                chapterId = 2,
                maxUnlockedChapter = maxUnlockedChapter,
                title = "⚙️ BLOCK CIPHER FORGE",
                subtitle = "FIPS-197 AES & S-Box",
                accentColor = Color(0xFFEF4444),
                emotionRes = R.drawable.phoenix_emotion_4,
                onClick = { onOpenLinkedListRoad?.invoke() }
            )
        }
        // Key Exchange Citadel (Chapter 3)
        item {
            ArenaCard(
                chapterId = 3,
                maxUnlockedChapter = maxUnlockedChapter,
                title = "🏰 KEY EXCHANGE CITADEL",
                subtitle = "Diffie-Hellman & RSA Curves",
                accentColor = Color(0xFFF59E0B),
                emotionRes = R.drawable.phoenix_emotion_28,
                onClick = { onOpenStackTower?.invoke() }
            )
        }
        // Integrity Foundry (Chapter 4)
        item {
            ArenaCard(
                chapterId = 4,
                maxUnlockedChapter = maxUnlockedChapter,
                title = "🛡️ INTEGRITY FOUNDRY",
                subtitle = "SHA-2/3 & Merkle Trees",
                accentColor = Color(0xFFA855F7),
                emotionRes = R.drawable.phoenix_emotion_25,
                onClick = { onOpenQueueStation?.invoke() }
            )
        }
        // Cyber Bastion (Chapter 5)
        item {
            ArenaCard(
                chapterId = 5,
                maxUnlockedChapter = maxUnlockedChapter,
                title = "⚡ CYBER BASTION",
                subtitle = "TLS 1.3, IPsec & Firewalls",
                accentColor = Color(0xFF10B981),
                emotionRes = R.drawable.phoenix_emotion_13,
                onClick = { onOpenBinaryTreeForest?.invoke() }
            )
        }
    }
}

@Composable
private fun ArenaCard(
    chapterId: Int,
    maxUnlockedChapter: Int,
    title: String,
    subtitle: String,
    accentColor: Color,
    emotionRes: Int,
    onClick: () -> Unit
) {
    val isUnlocked = true // All arenas unlocked for testing
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = Modifier
            .clip(shape)
            .background(CardBackground.copy(alpha = if (isUnlocked) 0.85f else 0.45f))
            .border(1.5.dp, accentColor.copy(alpha = if (isUnlocked) 0.45f else 0.20f), shape)
            .pressScale()
            .clickable { if (isUnlocked) onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Image(
            painter = painterResource(id = emotionRes),
            contentDescription = null,
            modifier = Modifier.size(38.dp)
        )
        Column {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = LocalDynamicThemeColors.current.textPrimary
            )
            Text(
                text = if (isUnlocked) subtitle else "Locked (Finish Ch. ${chapterId - 1})",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isUnlocked) accentColor else Color.Gray
            )
        }
        Icon(
            imageVector = if (isUnlocked) Icons.Default.ArrowForward else Icons.Default.Lock,
            contentDescription = null,
            tint = if (isUnlocked) accentColor else Color.Gray,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun DSATopicListCard(
    type: DSAVisualizerType,
    onSelect: () -> Unit
) {
    val dynamicColors = LocalDynamicThemeColors.current
    val isDark = dynamicColors.isDark
    val cardAccent = if (type.color == AmberGold && !isDark) Color(0xFFB45309) else type.color
    val diffAccent = if (type.difficultyColor == AmberGold && !isDark) Color(0xFFB45309) else type.difficultyColor

    val cardShape = RoundedCornerShape(22.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(CardBackground.copy(alpha = if (isDark) 0.85f else 0.98f))
            .border(
                1.5.dp,
                if (isDark) cardAccent.copy(alpha = 0.45f) else cardAccent.copy(alpha = 0.35f),
                cardShape
            )
            .pressScale()
            .clickable { onSelect() }
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Top Row: Icon on left, Title + Badges + Tagline on right (aligned together)
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(cardAccent.copy(alpha = if (isDark) 0.20f else 0.12f))
                    .border(1.2.dp, cardAccent.copy(alpha = if (isDark) 0.6f else 0.35f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = getDsaTypeIcon(type), fontSize = 24.sp)
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(cardAccent.copy(alpha = if (isDark) 0.15f else 0.10f))
                            .border(1.dp, cardAccent.copy(alpha = if (isDark) 0.30f else 0.20f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = type.category.uppercase(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = cardAccent
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(diffAccent.copy(alpha = if (isDark) 0.15f else 0.10f))
                            .border(1.dp, diffAccent.copy(alpha = if (isDark) 0.30f else 0.20f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = type.difficulty,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = diffAccent
                        )
                    }
                }

                Text(
                    text = type.title,
                    fontSize = 17.5.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = AppFontFamily,
                    color = dynamicColors.textPrimary
                )

                Text(
                    text = type.tagline,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = AppFontFamily,
                    color = cardAccent
                )
            }
        }

        Text(
            text = type.shortDescription,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = AppFontFamily,
            color = SubtextGray,
            maxLines = 2,
            lineHeight = 17.sp
        )

        // Operations & Time Complexity
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f, fill = false),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                type.keyOperations.take(2).forEach { op ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isDark) Color.White.copy(alpha = 0.08f)
                                else Color(0xFFF1F5F9)
                            )
                            .border(
                                1.dp,
                                if (isDark) Color.White.copy(alpha = 0.12f)
                                else Color(0xFFCBD5E1),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = op,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = dynamicColors.textPrimary.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(cardAccent.copy(alpha = if (isDark) 0.18f else 0.12f))
                    .border(1.dp, cardAccent.copy(alpha = if (isDark) 0.40f else 0.28f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 9.dp, vertical = 4.dp)
            ) {
                Text(
                    text = type.timeComplexity,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = if (isDark) dynamicColors.textPrimary else cardAccent,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }

        // Action Bar: "Open Interactive Lab →"
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Interactive Visualizer & Code",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Black,
                fontFamily = AppFontFamily,
                color = cardAccent
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(cardAccent)
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Open",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = AppFontFamily,
                        color = Color.White
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Open",
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TopicDetailView(
    selectedType: DSAVisualizerType,
    onSelectType: (DSAVisualizerType) -> Unit,
    onBackToList: () -> Unit,
    currentLanguageName: String
) {
    var activeLabMode by remember(selectedType) { mutableStateOf(CNSLabMode.EXPLORE) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Horizontal Quick Switcher Carousel
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(DSAVisualizerType.PEDAGOGICAL_ORDER) { type ->
                val isSelected = selectedType == type
                val shape = RoundedCornerShape(20.dp)
                Box(
                    modifier = Modifier
                        .clip(shape)
                        .background(if (isSelected) type.color else CardBackground.copy(alpha = 0.7f))
                        .border(1.5.dp, if (isSelected) type.color else InputBorder, shape)
                        .clickable {
                            activeLabMode = CNSLabMode.EXPLORE
                            onSelectType(type)
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = getDsaTypeIcon(type), fontSize = 12.sp)
                        Text(
                            text = type.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isSelected) Color.White else SubtextGray
                        )
                    }
                }
            }
        }

        // Scrollable Detail Content
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Spacer(modifier = Modifier.height(2.dp))

            // 1. Structure Deep Dive Header Card
            DSATopicDeepDiveHeaderCard(type = selectedType)

            // 1.5 Unified Four-Mode Lab Navigation (Explore · Attack / Break · Code & Test · Challenge)
            CNSLabModeTabBar(
                selectedMode = activeLabMode,
                onSelectMode = { activeLabMode = it },
                accentColor = selectedType.color
            )

            // 2. Interactive CNS Visualizer Engine (10 Flagship Cryptographic Labs)
            when (selectedType) {
                DSAVisualizerType.ARRAY -> CaesarCipherVisualizer(mode = activeLabMode)
                DSAVisualizerType.STACK -> DESFeistelVisualizer(mode = activeLabMode)
                DSAVisualizerType.QUEUE -> AESMatrixVisualizer(mode = activeLabMode)
                DSAVisualizerType.LINKED_LIST -> VigenereCipherVisualizer(mode = activeLabMode)
                DSAVisualizerType.BINARY_TREE -> DiffieHellmanVisualizer(mode = activeLabMode)
                DSAVisualizerType.AVL_TREE -> RSAPublicKeyVisualizer(mode = activeLabMode)
                DSAVisualizerType.TRIE -> ECCPointVisualizer(mode = activeLabMode)
                DSAVisualizerType.GRAPH -> SHA256Visualizer(mode = activeLabMode)
                DSAVisualizerType.SORTING -> HMACCollisionVisualizer(mode = activeLabMode)
                DSAVisualizerType.STEP_RECORDER -> TLS13HandshakeVisualizer(mode = activeLabMode)
            }

            // 3. Real World Applications Card (Contextual in Explore mode)
            if (activeLabMode == CNSLabMode.EXPLORE) {
                DSARealWorldApplicationsCard(type = selectedType)
            }

            // 4. Idiomatic Code Implementation Card in Chosen Language (Explore mode; Code & Test has integrated test vectors + code)
            if (activeLabMode == CNSLabMode.EXPLORE) {
                CNSLanguageImplementationCardView(
                    structureName = selectedType.title,
                    initialLanguage = currentLanguageName
                )
            }

            // 5. Navigation Buttons: Up Next & Back to Topics
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val next = selectedType.nextType
                val nextShape = RoundedCornerShape(18.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(nextShape)
                        .background(CardBackground)
                        .border(1.5.dp, next.color.copy(alpha = 0.45f), nextShape)
                        .clickable {
                            activeLabMode = CNSLabMode.EXPLORE
                            onSelectType(next)
                        }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "UP NEXT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = next.color
                        )
                        Text(
                            text = "Explore ${next.title}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = LocalDynamicThemeColors.current.textPrimary
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = next.color,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CardBackground.copy(alpha = 0.5f))
                        .border(1.dp, InputBorder, RoundedCornerShape(14.dp))
                        .clickable { onBackToList() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = SubtextGray,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Back to All Topics",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = SubtextGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun DSATopicDeepDiveHeaderCard(type: DSAVisualizerType) {
    val shape = RoundedCornerShape(22.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(CardBackground.copy(alpha = 0.85f))
            .border(1.5.dp, type.color.copy(alpha = 0.45f), shape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(type.color.copy(alpha = 0.18f))
                    .border(1.5.dp, type.color, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = getDsaTypeIcon(type), fontSize = 24.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(type.color.copy(alpha = 0.15f))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = type.category.uppercase(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = type.color
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(type.difficultyColor.copy(alpha = 0.15f))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = type.difficulty,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = type.difficultyColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = type.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = LocalDynamicThemeColors.current.textPrimary
                )
            }
        }

        Text(
            text = type.tagline,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = type.color
        )

        Text(
            text = type.shortDescription,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = SubtextGray,
            lineHeight = 18.sp
        )

        // Complexity Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(LocalDynamicThemeColors.current.inputBackground)
                    .border(1.dp, InputBorder, RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "⏱️ Time:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SubtextGray)
                    Text(text = type.timeComplexity, fontSize = 12.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = LocalDynamicThemeColors.current.textPrimary)
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(LocalDynamicThemeColors.current.inputBackground)
                    .border(1.dp, InputBorder, RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "💾 Space:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SubtextGray)
                    Text(text = type.spaceComplexity, fontSize = 12.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = LocalDynamicThemeColors.current.textPrimary)
                }
            }
        }
    }
}

@Composable
private fun DSARealWorldApplicationsCard(type: DSAVisualizerType) {
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(CardBackground.copy(alpha = 0.85f))
            .border(1.2.dp, InputBorder, shape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "🌐", fontSize = 18.sp)
            Text(
                text = "Real-World Systems & Applications",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = LocalDynamicThemeColors.current.textPrimary
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            type.realWorldApplications.forEach { app ->
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "✓", fontSize = 13.sp, fontWeight = FontWeight.Black, color = DuolingoGreen)
                    Text(
                        text = app,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.85f),
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}

private fun getDsaTypeIcon(type: DSAVisualizerType): String = when (type) {
    DSAVisualizerType.ARRAY -> "🔐"
    DSAVisualizerType.STACK -> "🧱"
    DSAVisualizerType.QUEUE -> "🔲"
    DSAVisualizerType.LINKED_LIST -> "📜"
    DSAVisualizerType.BINARY_TREE -> "🔑"
    DSAVisualizerType.AVL_TREE -> "🏛️"
    DSAVisualizerType.TRIE -> "📈"
    DSAVisualizerType.GRAPH -> "🧬"
    DSAVisualizerType.SORTING -> "🛡️"
    DSAVisualizerType.STEP_RECORDER -> "⚡"
}

// ──────────────────────────────────────────────────────────────────
// Note: The 10 Interactive CNS Visualizers are modularized in
// com.simats.codeingo.ui.visualizer.CNSVisualizers.kt
// ──────────────────────────────────────────────────────────────────

// ══════════════════════════════════════════════════════════════════
// 🗺️ Chapter Roadmap & Unlock Banner Composables (Parity with iOS)
// ══════════════════════════════════════════════════════════════════

@Composable
private fun ChapterRoadmapHeader(
    maxUnlockedChapter: Int,
    completedChapters: Set<Int>,
    onReset: () -> Unit
) {
    val isDark = LocalDynamicThemeColors.current.isDark
    val isAllMastered = maxUnlockedChapter >= 5 && completedChapters.contains(5)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .liquidGlassCard(accentGlow = AmberGold, cornerRadius = 18.dp)
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(AmberGold.copy(alpha = 0.35f), Color(0xFFFF9800).copy(alpha = 0.2f))
                                )
                            )
                            .border(1.2.dp, AmberGold.copy(alpha = 0.55f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = if (isDark) AmberGold else Color(0xFFD97706),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(
                                        (if (isDark) AmberGold else Color(0xFFC66900)).copy(alpha = 0.15f)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.5.dp)
                            ) {
                                Text(
                                    text = "CHAPTER ${minOf(maxUnlockedChapter, 5)} OF 5",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isDark) AmberGold else Color(0xFFC66900)
                                )
                            }

                            if (maxUnlockedChapter >= 5) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color(0xFFE91E63).copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.5.dp)
                                ) {
                                    Text(
                                        text = "FINAL ROUND",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFE91E63)
                                    )
                                }
                            }
                        }

                        Text(
                            text = if (isAllMastered) "All Arenas Mastered!" else "5 Flagship 3D CNS Arenas",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = LocalDynamicThemeColors.current.textPrimary
                        )
                    }
                }

                // Dev/Testing Reset Button
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f))
                        .clickable { onReset() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset",
                            tint = if (isDark) Color.White.copy(alpha = 0.6f) else Color(0xFF64748B),
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = "Reset",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White.copy(alpha = 0.6f) else Color(0xFF64748B)
                        )
                    }
                }
            }

            // Step Indicator Dots
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                (1..5).forEach { round ->
                    val color = when {
                        completedChapters.contains(round) -> DuolingoGreen
                        round == maxUnlockedChapter -> AmberGold
                        else -> if (isDark) Color.White.copy(alpha = 0.18f) else Color.Black.copy(alpha = 0.12f)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(5.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }
        }
    }
}

@Composable
private fun DSAChapterSummaryCard(
    chapter: DSAChapterModel,
    isUnlocked: Boolean,
    isCompleted: Boolean,
    isCurrentInProgress: Boolean,
    onTap: () -> Unit
) {
    val isDark = LocalDynamicThemeColors.current.isDark

    val cardBg = if (isUnlocked) {
        if (isDark) Color(0xFF182032) else Color.White
    } else {
        if (isDark) Color.White.copy(alpha = 0.03f) else Color.Black.copy(alpha = 0.02f)
    }

    val strokeColor = when {
        isCurrentInProgress -> chapter.primaryColor.copy(alpha = 0.85f)
        isUnlocked -> chapter.primaryColor.copy(alpha = 0.30f)
        else -> Color.Gray.copy(alpha = 0.20f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(cardBg)
            .border(1.2.dp, strokeColor, RoundedCornerShape(20.dp))
            .clickable { onTap() }
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Mascot Avatar / Lock Icon
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked) {
                            Brush.linearGradient(
                                listOf(chapter.primaryColor.copy(alpha = 0.32f), chapter.darkColor.copy(alpha = 0.15f))
                            )
                        } else {
                            Brush.linearGradient(
                                listOf(Color.Gray.copy(alpha = 0.2f), Color.Gray.copy(alpha = 0.1f))
                            )
                        }
                    )
                    .border(
                        1.4.dp,
                        if (isUnlocked) chapter.primaryColor.copy(alpha = 0.55f) else Color.Gray.copy(alpha = 0.3f),
                        CircleShape
                    )
            ) {
                if (isUnlocked) {
                    Text(text = chapter.characterEmoji, fontSize = 28.sp)
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = if (isDark) Color.White.copy(alpha = 0.4f) else Color(0xFF94A3B8),
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Mini Status Badge
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = DuolingoGreen,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(16.dp)
                            .background(Color.White, CircleShape)
                    )
                } else if (isCurrentInProgress) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(Color(0xFFFF9800))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "GO",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }

            // Middle Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                if (isUnlocked) chapter.primaryColor.copy(alpha = 0.14f)
                                else Color.Gray.copy(alpha = 0.12f)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "CHAPTER ${chapter.number}",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isUnlocked) chapter.primaryColor else Color.Gray
                        )
                    }

                    if (isCompleted) {
                        Text(
                            text = "MASTERED",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = DuolingoGreen
                        )
                    } else if (isCurrentInProgress) {
                        Text(
                            text = "IN PROGRESS",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = AmberGold
                        )
                    }
                }

                Text(
                    text = chapter.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isUnlocked) LocalDynamicThemeColors.current.textPrimary else LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.45f)
                )

                Text(
                    text = chapter.subtitle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isUnlocked) LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.70f) else LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.35f),
                    lineHeight = 14.sp
                )
            }

            // Right Chevron / Lock
            Icon(
                imageVector = if (isUnlocked) Icons.Default.ArrowForward else Icons.Default.Lock,
                contentDescription = null,
                tint = if (isUnlocked) chapter.primaryColor else Color.Gray.copy(alpha = 0.4f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun UnlockNextChapterButton(
    nextChapter: DSAChapterModel,
    onUnlock: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(nextChapter.primaryColor, nextChapter.darkColor)
                )
            )
            .border(1.5.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
            .pressScale()
            .clickable { onUnlock() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f))
                ) {
                    Icon(
                        imageVector = Icons.Default.LockOpen,
                        contentDescription = "Unlock",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "LEVEL COMPLETED! TAP TO UNLOCK",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Text(
                        text = "Unlock Chapter ${nextChapter.number}: ${nextChapter.title}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun GrandChampionBanner() {
    val isDark = LocalDynamicThemeColors.current.isDark
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .liquidGlassCard(accentGlow = AmberGold, cornerRadius = 18.dp)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = AmberGold,
                modifier = Modifier.size(28.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "ALL 5 CHAPTERS COMPLETED!",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isDark) AmberGold else Color(0xFFC66900)
                )
                Text(
                    text = "You have mastered all data structure arenas & visualizers.",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.75f)
                )
            }
        }
    }
}

@Composable
private fun DSAChapterPopupCard(
    chapter: DSAChapterModel,
    isCompleted: Boolean,
    onPlay3D: () -> Unit,
    onLearnVisualizer: () -> Unit,
    onOpenStory: () -> Unit,
    onCompleteLevel: () -> Unit,
    onDismiss: () -> Unit
) {
    val isDark = LocalDynamicThemeColors.current.isDark
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(if (isDark) Color(0xFF0F1523) else Color.White)
            .border(1.5.dp, chapter.primaryColor.copy(alpha = 0.45f), RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Top Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(chapter.primaryColor, chapter.darkColor)
                                )
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "${chapter.characterEmoji} CHAPTER ${chapter.number}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(chapter.primaryColor.copy(alpha = 0.12f))
                            .border(1.dp, chapter.primaryColor.copy(alpha = 0.35f), CircleShape)
                            .padding(horizontal = 9.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "${chapter.theme.emoji} ${chapter.theme.displayName}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = chapter.primaryColor
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.6f),
                    modifier = Modifier
                        .size(24.dp)
                        .pressScale()
                        .clickable { onDismiss() }
                )
            }

            // Title & Story
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = chapter.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = LocalDynamicThemeColors.current.textPrimary
                )
                Text(
                    text = chapter.story,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.75f),
                    lineHeight = 18.sp
                )
            }

            // Action Buttons
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Enter 3D Arena
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(chapter.primaryColor, chapter.darkColor)
                            )
                        )
                        .pressScale()
                        .clickable { onPlay3D() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Text(text = "ENTER 3D ARENA", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                }

                // Launch Visualizer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f))
                        .border(1.dp, chapter.primaryColor.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                        .pressScale()
                        .clickable { onLearnVisualizer() }
                        .padding(vertical = 11.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Visibility, contentDescription = null, tint = chapter.primaryColor, modifier = Modifier.size(16.dp))
                        Text(text = "LAUNCH VISUALIZER", fontSize = 12.5.sp, fontWeight = FontWeight.Black, color = chapter.primaryColor)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Read Story
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.04f))
                            .pressScale()
                            .clickable { onOpenStory() }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, tint = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.8f), modifier = Modifier.size(15.dp))
                            Text(text = "Field Story", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LocalDynamicThemeColors.current.textPrimary.copy(alpha = 0.8f))
                        }
                    }

                    // Complete Level
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isCompleted) DuolingoGreen.copy(alpha = 0.15f) else AmberGold.copy(alpha = 0.15f))
                            .pressScale()
                            .clickable { onCompleteLevel() }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isCompleted) "✓ Mastered" else "Mark Complete",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isCompleted) DuolingoGreen else AmberGold
                        )
                    }
                }
            }
        }
    }
}
