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
    LEVEL_1(1, "Caesar Shift Ring", "Alphabet ring & modulo 26 shifts", "🏛️", 10, 3),
    LEVEL_2(2, "Modular Offset Hunt", "Match plaintext to shifted ciphertext", "🎯", 10, 3),
    LEVEL_3(3, "Frequency Analysis", "Identify high-frequency English vowels", "🔍", 12, 4),
    LEVEL_4(4, "Polyalphabetic Stream", "Align Vigenere repeated keystream", "🔄", 12, 4),
    LEVEL_5(5, "Transposition Grids", "Reorder columnar rail fence blocks", "👆", 14, 5),
    LEVEL_6(6, "Cryptanalysis Sprint", "Crack the substitution cipher before 20s!", "⏱️", 15, 5),
    BOSS_BATTLE(7, "The Sphinx of Shadows", "Crack the ancient cipher to unlock the sanctum!", "🦅", 25, 15);

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
    val name: String = "THE SPHINX OF SHADOWS",
    val maxHP: Int = 100,
    val currentHP: Int = 100,
    val isAttacking: Boolean = false,
    val isDamaged: Boolean = false,
    val dialogue: String = "None can breach the Cipher Sanctum without the master key!"
) {
    val hpFraction: Float
        get() = (currentHP.toFloat() / maxHP.toFloat()).coerceIn(0f, 1f)
}
