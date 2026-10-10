package com.simats.codeingo.data.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class DashboardTab(val id: Int, val gameEmoji: String, val title: String) {
    LEARN(0, "🏡", "Learn"),
    VISUALIZER(1, "🔬", "Visualizer"),
    PRACTICE(2, "🛠️", "Practice"),
    LEADERBOARDS(3, "🛡️", "Leaderboards"),
    SHOP(4, "🦴", "Shop"),
    PROFILE(5, "👤", "Profile"),
    MORE(6, "💬", "More")
}

enum class UnitCharacterType {
    DUO_BACKPACK,
    LILY_PURPLE,
    VIKRAM_BEES,
    OSCAR_ARTIST,
    JUNIOR_PARTY,
    PHOENIX
}

data class LessonNodeItem(
    val id: Int,
    val levelNumber: Int,
    val icon: String,
    val xOffset: Dp = 0.dp,
    val title: String,
    val isBoss: Boolean = false,
    val bossSpec: DSABossSpec? = null
)

data class UnitModel(
    val id: Int,
    val sectionNumber: Int,
    val unitNumber: Int,
    val titleKey: String,
    val titleDefault: String,
    val themeColor: Color,
    val themeDarkColor: Color,
    val nodes: List<LessonNodeItem>,
    val characterType: UnitCharacterType,
    val worldTheme: String = "",
    val bossSpec: DSABossSpec? = null
)

data class LevelParticle(
    val id: String = java.util.UUID.randomUUID().toString(),
    val x: Float,
    val y: Float,
    val color: Color,
    val size: Float,
    val alpha: Float = 1f
)
