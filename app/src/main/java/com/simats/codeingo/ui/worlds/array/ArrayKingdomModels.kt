package com.simats.codeingo.ui.worlds.array

import androidx.compose.ui.graphics.Color
import java.util.UUID

enum class ArrayBlockVisualState {
    NORMAL,
    HIGHLIGHTED,
    SELECTED,
    COMPARING,
    SWAPPING,
    SORTED_LOCKED,
    CORRECT_GLOW,
    WRONG_SHAKE
}

data class ArrayBlockItem(
    val id: String = UUID.randomUUID().toString(),
    val value: Int,
    val index: Int,
    val state: ArrayBlockVisualState = ArrayBlockVisualState.NORMAL,
    val isLocked: Boolean = false,
    val xOffset: Float = 0f,
    val yOffset: Float = 0f,
    val zIndex: Float = 0f
)

enum class ArrayKingdomLevel(
    val id: Int,
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val xpReward: Int,
    val coinReward: Int
) {
    LEVEL_1(1, "What is an Array?", "Positions, Elements & 0-Based Indices", "📦", 20, 10),
    LEVEL_2(2, "Index Hunt", "Tap blocks at target indices", "🎯", 30, 15),
    LEVEL_3(3, "Treasure Search", "Linear search step-by-step", "🔍", 40, 20),
    LEVEL_4(4, "Bubble Sort Intro", "Watch larger values bubble to the right", "🔄", 50, 25),
    LEVEL_5(5, "Control the Sort", "Select adjacent pairs & swap", "👆", 60, 30),
    LEVEL_6(6, "Speed Challenge", "Sort before the 20s timer expires!", "⏱️", 80, 40),
    BOSS_BATTLE(7, "The Chaos Array", "Defeat the Glitch Boss with Bubble Sort!", "🔥", 100, 50);

    companion object {
        fun fromId(id: Int): ArrayKingdomLevel = entries.find { it.id == id } ?: LEVEL_1
    }
}

enum class KingdomGameSpeed(val multiplier: Double, val label: String) {
    SLOW(0.5, "0.5x"),
    NORMAL(1.0, "1.0x"),
    FAST(2.0, "2.0x");

    fun next(): KingdomGameSpeed = when (this) {
        SLOW -> NORMAL
        NORMAL -> FAST
        FAST -> SLOW
    }
}

data class KingdomBoss(
    val name: String = "THE CHAOS ARRAY",
    val maxHP: Int = 100,
    val currentHP: Int = 100,
    val isAttacking: Boolean = false,
    val isDamaged: Boolean = false,
    val dialogue: String = "You cannot order my chaos!"
) {
    val hpFraction: Float
        get() = (currentHP.toFloat() / maxHP.toFloat()).coerceIn(0f, 1f)
}
