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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
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
import com.simats.codeingo.ui.onboarding.LanguagePickerSheet
import com.simats.codeingo.ui.theme.AmberGold
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
import com.simats.codeingo.ui.theme.SubtextGray
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    var displayMode by remember { mutableStateOf(VisualizerDisplayMode.LIST) }
    var selectedType by remember { mutableStateOf(DSAVisualizerType.STACK) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var showLanguagePicker by remember { mutableStateOf(false) }

    val currentLanguageName = selectedLangObj?.name ?: when (selectedLanguageCode) {
        "es" -> "Spanish"
        "fr" -> "French"
        "de" -> "German"
        "ja" -> "Japanese"
        else -> "Python"
    }

    val currentLanguageFlag = when (currentLanguageName.lowercase()) {
        "python" -> "🐍"
        "java" -> "☕"
        "c++", "cpp" -> "⚙️"
        "swift" -> "🦅"
        "go" -> "🐹"
        "javascript", "js" -> "📜"
        "kotlin" -> "📱"
        else -> "🐍"
    }

    val categories = listOf("All", "Linear", "Trees & Graphs", "Algorithms")

    val filteredStructures = remember(searchQuery, selectedCategory) {
        DSAVisualizerType.entries.filter { type ->
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
            .background(DarkBackground)
    ) {
        PhoenixAtmosphericBackgroundView()

        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            VisualizerHeaderBar(
                displayMode = displayMode,
                currentLanguageFlag = currentLanguageFlag,
                currentLanguageName = currentLanguageName,
                onBackToList = { displayMode = VisualizerDisplayMode.LIST },
                onOpenLanguagePicker = { showLanguagePicker = true }
            )

            if (displayMode == VisualizerDisplayMode.LIST) {
                // Topic List View
                TopicListView(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelect = { selectedCategory = it },
                    filteredStructures = filteredStructures,
                    onSelectTopic = { type ->
                        selectedType = type
                        displayMode = VisualizerDisplayMode.VISUALIZER
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

        if (showLanguagePicker) {
            ModalBottomSheet(
                onDismissRequest = { showLanguagePicker = false },
                containerColor = DarkBackground
            ) {
                LanguagePickerSheet(
                    selectedLanguageCode = selectedLanguageCode,
                    onLanguageSelected = { lang ->
                        localizationManager.setSelectedLanguage(lang)
                        showLanguagePicker = false
                    },
                    onDismiss = { showLanguagePicker = false }
                )
            }
        }
    }
}

@Composable
private fun VisualizerHeaderBar(
    displayMode: VisualizerDisplayMode,
    currentLanguageFlag: String,
    currentLanguageName: String,
    onBackToList: () -> Unit,
    onOpenLanguagePicker: () -> Unit
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
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "All Topics",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }
        } else {
            Column {
                Text(
                    text = "DSA Visualizer",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = "Watch structures & code come alive",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = SubtextGray
                )
            }
        }

        // Language Switcher Pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(CardBackground.copy(alpha = 0.85f))
                .border(1.5.dp, DsaBlue.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                .clickable { onOpenLanguagePicker() }
                .padding(horizontal = 12.dp, vertical = 7.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = currentLanguageFlag, fontSize = 15.sp)
                Text(
                    text = currentLanguageName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = SubtextGray,
                    modifier = Modifier.size(14.dp)
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
    onSelectTopic: (DSAVisualizerType) -> Unit,
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
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
                BasicTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.weight(1f),
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    singleLine = true,
                    cursorBrush = SolidColor(AmberGold),
                    decorationBox = { innerTextField ->
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Search topics, operations, complexity...",
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

        // 3D Game Arenas Quick Access Carousel
        VisualizerGameArenasRow(
            onOpenArrayKingdom = onOpenArrayKingdom,
            onOpenStackTower = onOpenStackTower,
            onOpenQueueStation = onOpenQueueStation,
            onOpenLinkedListRoad = onOpenLinkedListRoad,
            onOpenBinaryTreeForest = onOpenBinaryTreeForest
        )

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
                        .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "✨", fontSize = 18.sp)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Interactive DSA Directory",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Tap any structure below to open its visualizer & live code",
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

        Spacer(modifier = Modifier.height(50.dp))
    }
}

@Composable
private fun VisualizerGameArenasRow(
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
        // Array Kingdom
        item {
            ArenaCard(
                title = "👑 ARRAY KINGDOM",
                subtitle = "3D Sorting Arena",
                accentColor = AmberGold,
                emotionRes = R.drawable.phoenix_emotion_4,
                onClick = { onOpenArrayKingdom?.invoke() }
            )
        }
        // Stack Tower
        item {
            ArenaCard(
                title = "🗼 STACK TOWER",
                subtitle = "3D LIFO Tower & Boss",
                accentColor = Color(0xFF00E5FF),
                emotionRes = R.drawable.phoenix_emotion_28,
                onClick = { onOpenStackTower?.invoke() }
            )
        }
        // Queue Station
        item {
            ArenaCard(
                title = "🚋 QUEUE STATION",
                subtitle = "3D FIFO Station & Boss",
                accentColor = DuolingoGreen,
                emotionRes = R.drawable.phoenix_emotion_25,
                onClick = { onOpenQueueStation?.invoke() }
            )
        }
        // Linked List Road
        item {
            ArenaCard(
                title = "🛣️ LINKED LIST ROAD",
                subtitle = "3D Pointer Highway & Boss",
                accentColor = Color(0xFF00E5FF),
                emotionRes = R.drawable.phoenix_emotion_4,
                onClick = { onOpenLinkedListRoad?.invoke() }
            )
        }
        // Binary Tree Forest
        item {
            ArenaCard(
                title = "🌲 BINARY TREE FOREST",
                subtitle = "3D Tree Canopies & Boss",
                accentColor = Color(0xFF00CD9C),
                emotionRes = R.drawable.phoenix_emotion_13,
                onClick = { onOpenBinaryTreeForest?.invoke() }
            )
        }
    }
}

@Composable
private fun ArenaCard(
    title: String,
    subtitle: String,
    accentColor: Color,
    emotionRes: Int,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = Modifier
            .clip(shape)
            .background(CardBackground.copy(alpha = 0.85f))
            .border(1.5.dp, accentColor.copy(alpha = 0.45f), shape)
            .clickable { onClick() }
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
                color = Color.White
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
        }
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun DSATopicListCard(
    type: DSAVisualizerType,
    onSelect: () -> Unit
) {
    val cardShape = RoundedCornerShape(22.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(CardBackground.copy(alpha = 0.85f))
            .border(1.5.dp, type.color.copy(alpha = 0.45f), cardShape)
            .clickable { onSelect() }
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Top Row: Icon, Category & Difficulty Badge, Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(type.color.copy(alpha = 0.20f))
                    .border(1.2.dp, type.color.copy(alpha = 0.6f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = getDsaTypeIcon(type), fontSize = 22.sp)
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

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = type.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }

        Text(
            text = type.tagline,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = type.color
        )

        Text(
            text = type.shortDescription,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = SubtextGray,
            maxLines = 2,
            lineHeight = 16.sp
        )

        // Operations & Time Complexity
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                type.keyOperations.take(3).forEach { op ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = op,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(type.color.copy(alpha = 0.18f))
                    .border(1.dp, type.color.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = type.timeComplexity,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = Color.White
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
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = type.color
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(type.color)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Open",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
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
    Column(modifier = Modifier.fillMaxSize()) {
        // Horizontal Quick Switcher Carousel
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(DSAVisualizerType.entries) { type ->
                val isSelected = selectedType == type
                val shape = RoundedCornerShape(20.dp)
                Box(
                    modifier = Modifier
                        .clip(shape)
                        .background(if (isSelected) type.color else CardBackground.copy(alpha = 0.7f))
                        .border(1.5.dp, if (isSelected) type.color else InputBorder, shape)
                        .clickable { onSelectType(type) }
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

            // 2. Interactive Visualizer Engine
            when (selectedType) {
                DSAVisualizerType.STACK -> StackVisualizer()
                DSAVisualizerType.QUEUE -> QueueVisualizer()
                DSAVisualizerType.ARRAY -> ArrayVisualizer()
                DSAVisualizerType.LINKED_LIST -> LinkedListVisualizer()
                DSAVisualizerType.BINARY_TREE -> BinaryTreeVisualizer()
                DSAVisualizerType.AVL_TREE -> AVLTreeVisualizer()
                DSAVisualizerType.TRIE -> TrieVisualizer()
                DSAVisualizerType.GRAPH -> GraphVisualizer()
                DSAVisualizerType.SORTING -> SortingVisualizer()
                DSAVisualizerType.STEP_RECORDER -> StepRecorderVisualizer()
            }

            // 3. Real World Applications Card
            DSARealWorldApplicationsCard(type = selectedType)

            // 4. Idiomatic Code Implementation Card in Chosen Language
            if (selectedType != DSAVisualizerType.STEP_RECORDER) {
                DSALanguageImplementationCardView(
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
                        .clickable { onSelectType(next) }
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
                            color = Color.White
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
                    color = Color.White
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
                    .background(DarkBackground.copy(alpha = 0.6f))
                    .border(1.dp, InputBorder, RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "⏱️ Time:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SubtextGray)
                    Text(text = type.timeComplexity, fontSize = 12.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = Color.White)
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkBackground.copy(alpha = 0.6f))
                    .border(1.dp, InputBorder, RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "💾 Space:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SubtextGray)
                    Text(text = type.spaceComplexity, fontSize = 12.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = Color.White)
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
                color = Color.White
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
                        color = Color.White.copy(alpha = 0.85f),
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}

private fun getDsaTypeIcon(type: DSAVisualizerType): String = when (type) {
    DSAVisualizerType.STACK -> "📚"
    DSAVisualizerType.ARRAY -> "📊"
    DSAVisualizerType.QUEUE -> "🎫"
    DSAVisualizerType.LINKED_LIST -> "🔗"
    DSAVisualizerType.BINARY_TREE -> "🌲"
    DSAVisualizerType.AVL_TREE -> "⚖️"
    DSAVisualizerType.TRIE -> "🔤"
    DSAVisualizerType.GRAPH -> "🕸️"
    DSAVisualizerType.SORTING -> "🔄"
    DSAVisualizerType.STEP_RECORDER -> "🎬"
}

// ──────────────────────────────────────────────
// 1. Stack Visualizer
// ──────────────────────────────────────────────
@Composable
private fun StackVisualizer() {
    val items = remember { mutableStateListOf(10, 25, 42) }
    var nextVal by remember { mutableIntStateOf(50) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackground)
                .border(1.5.dp, InputBorder, RoundedCornerShape(16.dp))
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (items.isEmpty()) {
                    Text(text = "Stack is Empty", color = SubtextGray, fontSize = 14.sp)
                } else {
                    items.reversed().forEachIndexed { index, value ->
                        val isTop = index == 0
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isTop) DsaBlue else Color(0xFF1E3A5F))
                                .border(1.5.dp, if (isTop) Color.White else DsaBlue.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isTop) "$value (TOP)" else "$value",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DuolingoButton(
                text = "PUSH",
                faceColor = DsaBlue,
                shadowColor = Color(0xFF1899D6),
                onClick = {
                    if (items.size < 5) {
                        items.add(nextVal)
                        nextVal += 7
                    }
                },
                modifier = Modifier.weight(1f)
            )

            DuolingoButton(
                text = "POP",
                faceColor = DsaRed,
                shadowColor = Color(0xFFD23232),
                onClick = {
                    if (items.isNotEmpty()) {
                        items.removeAt(items.lastIndex)
                    }
                },
                modifier = Modifier.weight(1f)
            )

            DuolingoButton(
                text = "CLEAR",
                faceColor = CardBackground,
                shadowColor = Color(0xFF142028),
                textColor = SubtextGray,
                onClick = { items.clear() },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))
        ComplexityCard(time = "Push: O(1) • Pop: O(1) • Peek: O(1)", space = "O(n)")
    }
}

// ──────────────────────────────────────────────
// 2. Queue Visualizer
// ──────────────────────────────────────────────
@Composable
private fun QueueVisualizer() {
    val items = remember { mutableStateListOf(14, 28, 49) }
    var nextVal by remember { mutableIntStateOf(63) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackground)
                .border(1.5.dp, InputBorder, RoundedCornerShape(16.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (items.isEmpty()) {
                    Text(text = "Queue is Empty", color = SubtextGray, fontSize = 14.sp)
                } else {
                    items.forEachIndexed { index, value ->
                        val isFront = index == 0
                        val isRear = index == items.lastIndex

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = when {
                                    isFront && isRear -> "FRONT/REAR"
                                    isFront -> "FRONT"
                                    isRear -> "REAR"
                                    else -> ""
                                },
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isFront) DuolingoGreen else AmberGold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DsaPurple.copy(alpha = 0.25f))
                                    .border(1.5.dp, DsaPurple, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$value",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        if (index < items.lastIndex) {
                            Text(text = "→", fontSize = 18.sp, color = SubtextGray, modifier = Modifier.padding(horizontal = 6.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DuolingoButton(
                text = "ENQUEUE",
                faceColor = DsaPurple,
                shadowColor = Color(0xFF823CDC),
                onClick = {
                    if (items.size < 5) {
                        items.add(nextVal)
                        nextVal += 11
                    }
                },
                modifier = Modifier.weight(1f)
            )

            DuolingoButton(
                text = "DEQUEUE",
                faceColor = DsaRed,
                shadowColor = Color(0xFFD23232),
                onClick = {
                    if (items.isNotEmpty()) {
                        items.removeAt(0)
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))
        ComplexityCard(time = "Enqueue: O(1) • Dequeue: O(1)", space = "O(n)")
    }
}

// ──────────────────────────────────────────────
// 3. Array Visualizer (Faithful to iOS ArrayVisualizerView)
// ──────────────────────────────────────────────
@Composable
private fun ArrayVisualizer() {
    val arrayItems = remember { mutableStateListOf(10, 25, 8, 42, 17) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var lastAction by remember { mutableStateOf("Tap any cell to select it") }
    var highlightInsert by remember { mutableStateOf<Int?>(null) }
    var searchTargetText by remember { mutableStateOf("") }
    var scanningIndex by remember { mutableStateOf<Int?>(null) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackground)
                .border(1.5.dp, InputBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header inside card
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Array Visualizer",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "Sequential memory cells with index access",
                            fontSize = 12.sp,
                            color = SubtextGray
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DsaGreen.copy(alpha = 0.15f))
                            .border(1.dp, DsaGreen.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Size: ${arrayItems.size}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DsaGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Index Labels & Cells (Horizontally scrollable for safety)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.Center
                ) {
                    arrayItems.forEachIndexed { index, valNum ->
                        val isSelected = selectedIndex == index
                        val isInserted = highlightInsert == index
                        val isScanning = scanningIndex == index

                        val cellScale by animateFloatAsState(
                            targetValue = if (isInserted) 1.15f else if (isSelected || isScanning) 1.08f else 1.0f,
                            animationSpec = spring(dampingRatio = 0.55f),
                            label = "cellScale"
                        )

                        val cellBg = when {
                            isScanning -> AmberGold.copy(alpha = 0.35f)
                            isSelected -> DsaGreen.copy(alpha = 0.35f)
                            isInserted -> DsaGreen.copy(alpha = 0.45f)
                            else -> DsaGreen.copy(alpha = 0.12f)
                        }

                        val cellBorderColor = when {
                            isScanning -> AmberGold
                            isSelected -> DsaGreen
                            isInserted -> Color.White
                            else -> DsaGreen.copy(alpha = 0.4f)
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .scale(cellScale)
                        ) {
                            Text(
                                text = "[$index]",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected || isScanning) Color.White else SubtextGray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(width = 54.dp, height = 56.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(cellBg)
                                    .border(
                                        width = if (isSelected || isInserted || isScanning) 2.5.dp else 1.5.dp,
                                        color = cellBorderColor,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        if (selectedIndex == index) {
                                            selectedIndex = null
                                            lastAction = "Selection cleared"
                                        } else {
                                            selectedIndex = index
                                            lastAction = "Selected index [$index] = $valNum"
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$valNum",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isSelected || isScanning) Color.White else DsaGreen
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "0x%04X".format(0x1000 + index * 4),
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = SubtextGray.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Last Action status chip
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(DsaGreen.copy(alpha = 0.1f))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = lastAction,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DsaGreen
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons: Insert, Delete, Reset
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DuolingoButton(
                        text = "+ INSERT",
                        faceColor = DsaGreen,
                        shadowColor = Color(0xFF45A000),
                        onClick = {
                            val newVal = (1..99).random()
                            val insertIndex = if (selectedIndex != null) (selectedIndex!! + 1) else arrayItems.size
                            val clampedIndex = insertIndex.coerceIn(0, arrayItems.size)
                            arrayItems.add(clampedIndex, newVal)
                            highlightInsert = clampedIndex
                            lastAction = "Inserted $newVal at index [$clampedIndex]"
                            selectedIndex = null
                            scope.launch {
                                delay(600)
                                highlightInsert = null
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )

                    DuolingoButton(
                        text = "DELETE",
                        faceColor = if (selectedIndex != null) DsaRed else CardBackground,
                        shadowColor = if (selectedIndex != null) Color(0xFFD23232) else Color(0xFF142028),
                        textColor = if (selectedIndex != null) Color.White else SubtextGray,
                        enabled = selectedIndex != null,
                        onClick = {
                            selectedIndex?.let { idx ->
                                if (idx in 0 until arrayItems.size) {
                                    val removed = arrayItems.removeAt(idx)
                                    lastAction = "Deleted $removed from index [$idx]"
                                    selectedIndex = null
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )

                    DuolingoButton(
                        text = "RESET",
                        faceColor = DsaOrange,
                        shadowColor = Color(0xFFD06400),
                        onClick = {
                            arrayItems.clear()
                            arrayItems.addAll(listOf(10, 25, 8, 42, 17))
                            selectedIndex = null
                            lastAction = "Array reset to original"
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search by Value Row (Linear Scan O(n))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkBackground)
                        .border(1.dp, InputBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = SubtextGray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextField(
                        value = searchTargetText,
                        onValueChange = { searchTargetText = it },
                        singleLine = true,
                        textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                        cursorBrush = SolidColor(DsaGreen),
                        modifier = Modifier.weight(1f),
                        decorationBox = { innerTextField ->
                            if (searchTargetText.isEmpty()) {
                                Text(text = "Search value (e.g. 42)", color = SubtextGray, fontSize = 13.sp)
                            }
                            innerTextField()
                        }
                    )
                    if (searchTargetText.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                val target = searchTargetText.toIntOrNull()
                                if (target == null) {
                                    lastAction = "Enter a valid integer to search"
                                } else {
                                    scope.launch {
                                        var found = false
                                        for (i in arrayItems.indices) {
                                            scanningIndex = i
                                            lastAction = "Scanning index [$i]: checking ${arrayItems[i]} == $target..."
                                            delay(350)
                                            if (arrayItems[i] == target) {
                                                selectedIndex = i
                                                lastAction = "✓ Found $target at index [$i]! (Time: O(n))"
                                                found = true
                                                break
                                            }
                                        }
                                        scanningIndex = null
                                        if (!found) {
                                            selectedIndex = null
                                            lastAction = "✗ Value $target not found in array (Scanned ${arrayItems.size} cells)"
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Run Search",
                                tint = DsaGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Key Concepts Card matching iOS
        ComplexityCard(
            time = "Access: O(1) • Search: O(n) • Insert/Delete Mid: O(n) • Append: O(1)",
            space = "O(n) sequential memory"
        )
    }
}

// ──────────────────────────────────────────────
// 4. Linked List Visualizer (Interactive Pointer Nodes)
// ──────────────────────────────────────────────
@Composable
private fun LinkedListVisualizer() {
    val nodes = remember { mutableStateListOf(10, 20, 30) }
    var nextVal by remember { mutableIntStateOf(40) }
    var lastAction by remember { mutableStateOf("Tap Insert to add nodes with pointers") }
    var highlightIdx by remember { mutableStateOf<Int?>(null) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackground)
                .border(1.5.dp, InputBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    nodes.forEachIndexed { index, valNum ->
                        val isHead = index == 0
                        val isTail = index == nodes.lastIndex
                        val isHighlighted = highlightIdx == index

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (isHead) "HEAD" else if (isTail) "TAIL" else "",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isHead) DuolingoGreen else DsaOrange
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isHighlighted) DsaOrange.copy(alpha = 0.4f) else DsaOrange.copy(alpha = 0.15f))
                                        .border(1.5.dp, if (isHighlighted) Color.White else DsaOrange, RoundedCornerShape(8.dp))
                                ) {
                                    // Data box
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp, 40.dp)
                                            .padding(2.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "$valNum", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                    // Pointer box
                                    Box(
                                        modifier = Modifier
                                            .size(26.dp, 40.dp)
                                            .background(DsaOrange.copy(alpha = 0.1f))
                                            .border(1.dp, DsaOrange.copy(alpha = 0.3f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "•", fontSize = 16.sp, color = DsaOrange)
                                    }
                                }
                            }

                            // Pointer Arrow
                            Text(
                                text = " → ",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = DsaOrange,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                            )
                        }
                    }

                    // Terminal null
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "", fontSize = 9.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E2832))
                                .border(1.5.dp, InputBorder, RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Text(text = "null", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SubtextGray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = lastAction,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = DsaOrange
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DuolingoButton(
                text = "+ HEAD",
                faceColor = DsaGreen,
                shadowColor = Color(0xFF46A302),
                onClick = {
                    nodes.add(0, nextVal)
                    lastAction = "Inserted $nextVal at HEAD in O(1) time"
                    highlightIdx = 0
                    nextVal += 10
                    scope.launch {
                        delay(600)
                        highlightIdx = null
                    }
                },
                modifier = Modifier.weight(1f)
            )

            DuolingoButton(
                text = "+ TAIL",
                faceColor = DsaOrange,
                shadowColor = Color(0xFFD27800),
                onClick = {
                    nodes.add(nextVal)
                    lastAction = "Inserted $nextVal at TAIL in O(1) time"
                    highlightIdx = nodes.lastIndex
                    nextVal += 10
                    scope.launch {
                        delay(600)
                        highlightIdx = null
                    }
                },
                modifier = Modifier.weight(1f)
            )

            DuolingoButton(
                text = "- HEAD",
                faceColor = DsaRed,
                shadowColor = Color(0xFFD23232),
                onClick = {
                    if (nodes.isNotEmpty()) {
                        val removed = nodes.removeAt(0)
                        lastAction = "Deleted HEAD node ($removed) in O(1) time"
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))
        ComplexityCard(time = "Insert Head/Tail: O(1) • Search: O(n)", space = "O(n)")
    }
}

// ──────────────────────────────────────────────
// 5. Binary Tree Visualizer
// ──────────────────────────────────────────────
@Composable
private fun BinaryTreeVisualizer() {
    val treeValues = remember { listOf(50, 30, 70, 20, 40, 60, 80) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var narration by remember { mutableStateOf("Binary Search Tree: Tap any node to inspect BST properties") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackground)
                .border(1.5.dp, InputBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Root Level
                TreeNodeItem(treeValues[0], isSelected = selectedIndex == 0) {
                    selectedIndex = 0
                    narration = "Root Node: 50. Left subtree values < 50, Right subtree > 50."
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Level 1 Children
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    TreeNodeItem(treeValues[1], isSelected = selectedIndex == 1) {
                        selectedIndex = 1
                        narration = "Node 30: Left child of 50. Has children 20 and 40."
                    }
                    TreeNodeItem(treeValues[2], isSelected = selectedIndex == 2) {
                        selectedIndex = 2
                        narration = "Node 70: Right child of 50. Has children 60 and 80."
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Level 2 Leaves
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TreeNodeItem(treeValues[3], isSelected = selectedIndex == 3, size = 38) {
                        selectedIndex = 3
                        narration = "Leaf Node 20: Leftmost element (minimum value)."
                    }
                    TreeNodeItem(treeValues[4], isSelected = selectedIndex == 4, size = 38) {
                        selectedIndex = 4
                        narration = "Leaf Node 40: Right child of 30."
                    }
                    TreeNodeItem(treeValues[5], isSelected = selectedIndex == 5, size = 38) {
                        selectedIndex = 5
                        narration = "Leaf Node 60: Left child of 70."
                    }
                    TreeNodeItem(treeValues[6], isSelected = selectedIndex == 6, size = 38) {
                        selectedIndex = 6
                        narration = "Leaf Node 80: Rightmost element (maximum value)."
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = narration,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF00CD9C)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
        ComplexityCard(time = "Search / Insert: O(log n) • In-Order: Sorted", space = "O(n)")
    }
}

@Composable
private fun TreeNodeItem(
    value: Int,
    isSelected: Boolean,
    size: Int = 44,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(if (isSelected) Color(0xFF00CD9C) else Color(0xFF00CD9C).copy(alpha = 0.2f))
            .border(2.dp, Color(0xFF00CD9C), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$value",
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            color = if (isSelected) Color.White else Color(0xFF00CD9C)
        )
    }
}

// ──────────────────────────────────────────────
// 6. AVL Tree Visualizer (Balance Doctor Rotations)
// ──────────────────────────────────────────────
@Composable
private fun AVLTreeVisualizer() {
    var rootVal by remember { mutableIntStateOf(30) }
    var leftVal by remember { mutableIntStateOf(20) }
    var rightVal by remember { mutableIntStateOf(40) }
    var bfRoot by remember { mutableIntStateOf(1) }
    var narration by remember { mutableStateOf("AVL Balanced Tree: Balance Factors ∈ {-1, 0, 1}") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackground)
                .border(1.5.dp, InputBorder, RoundedCornerShape(16.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Root node with BF badge
                AVLNodeBadge(rootVal, bfRoot)

                Spacer(modifier = Modifier.height(20.dp))

                // Children
                Row(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AVLNodeBadge(leftVal, 0)
                    AVLNodeBadge(rightVal, 0)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = narration,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = DsaTeal
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DuolingoButton(
                text = "ROTATE LEFT (LL)",
                faceColor = DsaTeal,
                shadowColor = Color(0xFF008F6B),
                onClick = {
                    val temp = rootVal
                    rootVal = rightVal
                    rightVal += 10
                    leftVal = temp
                    bfRoot = 0
                    narration = "Rotated Left: New root $rootVal, Balance factor re-stabilized to 0."
                },
                modifier = Modifier.weight(1f)
            )

            DuolingoButton(
                text = "ROTATE RIGHT (RR)",
                faceColor = DsaBlue,
                shadowColor = Color(0xFF1899D6),
                onClick = {
                    val temp = rootVal
                    rootVal = leftVal
                    leftVal = (leftVal - 10).coerceAtLeast(10)
                    rightVal = temp
                    bfRoot = 0
                    narration = "Rotated Right: New root $rootVal, Balance factor re-stabilized to 0."
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))
        ComplexityCard(time = "Rotation: O(1) • Guaranteed Height: O(log n)", space = "O(n)")
    }
}

@Composable
private fun AVLNodeBadge(value: Int, bf: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(DsaTeal.copy(alpha = 0.25f))
                .border(2.dp, DsaTeal, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "$value", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.White)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color.Black.copy(alpha = 0.4f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "BF: ${if (bf > 0) "+$bf" else "$bf"}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (bf == 0) DuolingoGreen else AmberGold
            )
        }
    }
}

// ──────────────────────────────────────────────
// 7. Trie Visualizer (Crystal Cave of Words)
// ──────────────────────────────────────────────
@Composable
private fun TrieVisualizer() {
    val words = remember { listOf("CAT", "CAR", "CARD", "DO", "DOG") }
    var searchPrefix by remember { mutableStateOf("CA") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackground)
                .border(1.5.dp, InputBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Word Bank Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Stored Words: ", fontSize = 12.sp, color = SubtextGray)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        words.forEach { w ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.Cyan.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(text = w, fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.Cyan)
                            }
                        }
                    }
                }

                // Interactive Prefix Search Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkBackground)
                        .border(1.dp, InputBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color.Cyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextField(
                        value = searchPrefix,
                        onValueChange = { searchPrefix = it.uppercase() },
                        textStyle = TextStyle(color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold),
                        cursorBrush = SolidColor(Color.Cyan),
                        modifier = Modifier.weight(1f),
                        decorationBox = { innerTextField ->
                            if (searchPrefix.isEmpty()) {
                                Text(text = "Type prefix (e.g. CA, DO)...", color = SubtextGray, fontSize = 13.sp)
                            }
                            innerTextField()
                        }
                    )
                }

                // Trie Tree Diagram
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Root
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color.Cyan.copy(alpha = 0.25f))
                            .border(1.dp, Color.Cyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "ROOT", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Level 1: C and D
                    Row(modifier = Modifier.fillMaxWidth(0.7f), horizontalArrangement = Arrangement.SpaceAround) {
                        TrieNodeItem("C", isMatch = searchPrefix.startsWith("C"))
                        TrieNodeItem("D", isMatch = searchPrefix.startsWith("D"))
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Level 2: A and O
                    Row(modifier = Modifier.fillMaxWidth(0.7f), horizontalArrangement = Arrangement.SpaceAround) {
                        TrieNodeItem("A", isMatch = searchPrefix.startsWith("CA"))
                        TrieNodeItem("O", isEnd = true, isMatch = searchPrefix.startsWith("DO"))
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Level 3: T, R, G
                    Row(modifier = Modifier.fillMaxWidth(0.9f), horizontalArrangement = Arrangement.SpaceAround) {
                        TrieNodeItem("T", isEnd = true, isMatch = searchPrefix == "CAT")
                        TrieNodeItem("R", isEnd = true, isMatch = searchPrefix.startsWith("CAR"))
                        TrieNodeItem("G", isEnd = true, isMatch = searchPrefix == "DOG")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
        ComplexityCard(time = "Search / Insert: O(L) where L = word length", space = "O(ALPHABET_SIZE × L × N)")
    }
}

@Composable
private fun TrieNodeItem(char: String, isEnd: Boolean = false, isMatch: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isMatch) Color.Cyan else Color(0xFF1E2832))
                .border(1.5.dp, Color.Cyan, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = char,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = if (isMatch) Color.Black else Color.White
            )
        }
        if (isEnd) {
            Text(text = "★ WORD", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = AmberGold)
        }
    }
}

// ──────────────────────────────────────────────
// 8. Graph Visualizer (Sky Islands BFS / DFS)
// ──────────────────────────────────────────────
@Composable
private fun GraphVisualizer() {
    val islands = listOf("A", "B", "C", "D", "E")
    var visitedPath by remember { mutableStateOf<List<String>>(emptyList()) }
    var activeIsland by remember { mutableStateOf<String?>(null) }
    var traversalType by remember { mutableStateOf("Tap BFS or DFS to traverse Sky Islands") }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackground)
                .border(1.5.dp, InputBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            // Bridges
            Canvas(modifier = Modifier.fillMaxSize()) {
                val pA = Offset(size.width * 0.2f, size.height * 0.25f)
                val pB = Offset(size.width * 0.5f, size.height * 0.25f)
                val pC = Offset(size.width * 0.8f, size.height * 0.5f)
                val pD = Offset(size.width * 0.2f, size.height * 0.75f)
                val pE = Offset(size.width * 0.5f, size.height * 0.75f)

                drawLine(Color.Magenta.copy(alpha = 0.4f), pA, pB, strokeWidth = 3f)
                drawLine(Color.Magenta.copy(alpha = 0.4f), pB, pC, strokeWidth = 3f)
                drawLine(Color.Magenta.copy(alpha = 0.4f), pC, pE, strokeWidth = 3f)
                drawLine(Color.Magenta.copy(alpha = 0.4f), pA, pD, strokeWidth = 3f)
                drawLine(Color.Magenta.copy(alpha = 0.4f), pD, pE, strokeWidth = 3f)
            }

            // Islands
            Box(modifier = Modifier.fillMaxSize()) {
                IslandCircle("A", Alignment.TopStart, Offset(40f, 20f), activeIsland == "A", visitedPath.contains("A"))
                IslandCircle("B", Alignment.TopCenter, Offset(0f, 20f), activeIsland == "B", visitedPath.contains("B"))
                IslandCircle("C", Alignment.CenterEnd, Offset(-30f, 0f), activeIsland == "C", visitedPath.contains("C"))
                IslandCircle("D", Alignment.BottomStart, Offset(40f, -20f), activeIsland == "D", visitedPath.contains("D"))
                IslandCircle("E", Alignment.BottomCenter, Offset(0f, -20f), activeIsland == "E", visitedPath.contains("E"))
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = if (visitedPath.isEmpty()) traversalType else "Visited: ${visitedPath.joinToString(" → ")}",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = AmberGold
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DuolingoButton(
                text = "BFS FLOOD",
                faceColor = Color(0xFFA05AFF),
                shadowColor = Color(0xFF823CDC),
                onClick = {
                    scope.launch {
                        val order = listOf("A", "B", "D", "C", "E")
                        visitedPath = emptyList()
                        traversalType = "BFS Ripple: Exploring all neighbors level-by-level"
                        for (island in order) {
                            activeIsland = island
                            visitedPath = visitedPath + island
                            delay(450)
                        }
                        activeIsland = null
                    }
                },
                modifier = Modifier.weight(1f)
            )

            DuolingoButton(
                text = "DFS DIVER",
                faceColor = DsaBlue,
                shadowColor = Color(0xFF1899D6),
                onClick = {
                    scope.launch {
                        val order = listOf("A", "B", "C", "E", "D")
                        visitedPath = emptyList()
                        traversalType = "DFS Diver: Plunging deep down path before backtracking"
                        for (island in order) {
                            activeIsland = island
                            visitedPath = visitedPath + island
                            delay(450)
                        }
                        activeIsland = null
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))
        ComplexityCard(time = "Traversal: O(V + E) • Adjacency List: O(V + E)", space = "O(V)")
    }
}

@Composable
private fun IslandCircle(
    id: String,
    alignment: Alignment,
    offset: Offset,
    isActive: Boolean,
    isVisited: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        contentAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .offset(offset.x.dp, offset.y.dp)
                .size(42.dp)
                .clip(CircleShape)
                .background(if (isActive) AmberGold else if (isVisited) Color(0xFFA05AFF) else Color(0xFF261E38))
                .border(2.dp, if (isActive) Color.White else Color(0xFFA05AFF), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = id,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = if (isActive) Color.Black else Color.White
            )
        }
    }
}

// ──────────────────────────────────────────────
// 9. Sorting Visualizer (Bubble Sort Steps)
// ──────────────────────────────────────────────
@Composable
private fun SortingVisualizer() {
    val items = remember { mutableStateListOf(64, 34, 25, 12, 22, 11, 90) }
    var highlightedIdx by remember { mutableStateOf<Int?>(null) }
    var narration by remember { mutableStateOf("Tap Next Step to execute adjacent comparison & swap") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackground)
                .border(1.5.dp, InputBorder, RoundedCornerShape(16.dp))
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                items.forEachIndexed { index, num ->
                    val isCompared = highlightedIdx == index || highlightedIdx == index - 1
                    val barHeight = (num * 1.6f).dp
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "$num", fontSize = 11.sp, color = if (isCompared) AmberGold else Color.White, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(28.dp)
                                .height(barHeight)
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(if (isCompared) AmberGold else DsaRed)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = narration,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = DsaRed
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DuolingoButton(
                text = "NEXT STEP",
                faceColor = DsaRed,
                shadowColor = Color(0xFFD23232),
                onClick = {
                    var swapped = false
                    for (i in 0 until items.lastIndex) {
                        if (items[i] > items[i + 1]) {
                            val temp = items[i]
                            items[i] = items[i + 1]
                            items[i + 1] = temp
                            highlightedIdx = i + 1
                            narration = "Swapped ${items[i + 1]} and ${items[i]} (out of order)"
                            swapped = true
                            break
                        }
                    }
                    if (!swapped) {
                        narration = "🎉 Array is completely sorted!"
                        highlightedIdx = null
                    }
                },
                modifier = Modifier.weight(1f)
            )

            DuolingoButton(
                text = "RESET",
                faceColor = CardBackground,
                shadowColor = Color(0xFF142028),
                textColor = SubtextGray,
                onClick = {
                    items.clear()
                    items.addAll(listOf(64, 34, 25, 12, 22, 11, 90))
                    highlightedIdx = null
                    narration = "Array reset to unsorted state"
                },
                modifier = Modifier.weight(0.6f)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))
        ComplexityCard(time = "Worst: O(n²) • Best: O(n) • Comparisons: n(n-1)/2", space = "O(1)")
    }
}

// ──────────────────────────────────────────────
// 10. Step Recorder Engine Visualizer
// ──────────────────────────────────────────────
@Composable
private fun StepRecorderVisualizer() {
    val steps = listOf(
        Triple("Array initialized", "Array [10, 20, 30, 40] allocated contiguously in memory.", listOf(10, 20, 30, 40)),
        Triple("Shift element at index 3", "Element 40 shifts right to index 4 to make space.", listOf(10, 20, 30, 40, 40)),
        Triple("Shift element at index 2", "Element 30 shifts right to index 3.", listOf(10, 20, 30, 30, 40)),
        Triple("Insert 25 at index 2", "Value 25 written at index 2 in O(1) direct write time.", listOf(10, 20, 25, 30, 40))
    )
    var stepIdx by remember { mutableIntStateOf(0) }
    val current = steps[stepIdx]

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackground)
                .border(1.5.dp, InputBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Step Pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "STEP ${stepIdx + 1} OF ${steps.size}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = AmberGold
                    )
                    Text(text = current.first, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                // Array State Box
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    current.third.forEachIndexed { i, v ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (stepIdx == 3 && i == 2) DuolingoGreen else DsaBlue.copy(alpha = 0.3f))
                                .border(1.5.dp, if (stepIdx == 3 && i == 2) DuolingoGreen else DsaBlue, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "$v", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                Text(
                    text = current.second,
                    fontSize = 12.sp,
                    color = SubtextGray,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DuolingoButton(
                text = "PREVIOUS",
                faceColor = CardBackground,
                shadowColor = Color(0xFF142028),
                textColor = if (stepIdx > 0) Color.White else SubtextGray,
                onClick = { if (stepIdx > 0) stepIdx -= 1 },
                modifier = Modifier.weight(1f)
            )

            DuolingoButton(
                text = "NEXT STEP",
                faceColor = AmberGold,
                shadowColor = Color(0xFFD27800),
                textColor = Color.Black,
                onClick = { if (stepIdx < steps.lastIndex) stepIdx += 1 },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))
        ComplexityCard(time = "Step Cost: O(1) • Total Sequence: O(n)", space = "O(1) auxiliary")
    }
}

// ──────────────────────────────────────────────
// Reusable Complexity Card
// ──────────────────────────────────────────────
@Composable
private fun ComplexityCard(time: String, space: String) {
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(CardBackground)
            .border(1.dp, InputBorder, shape)
            .padding(14.dp)
    ) {
        Text(text = "TIME COMPLEXITY", fontSize = 10.sp, fontWeight = FontWeight.Black, color = SubtextGray)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = time, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "SPACE COMPLEXITY", fontSize = 10.sp, fontWeight = FontWeight.Black, color = SubtextGray)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = space, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White, fontFamily = FontFamily.Monospace)
    }
}
