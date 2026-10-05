package com.simats.codeingo.ui.worlds.stack

import java.util.UUID

enum class StackBlockVisualState {
    NORMAL,
    TOP_HIGHLIGHTED,
    BEING_PUSHED,
    BEING_POPPED,
    PEEK_INSPECTED,
    UNSTABLE_SHAKING,
    LOCKED
}

data class StackBlockItem(
    val id: String = UUID.randomUUID().toString(),
    val value: Int,
    val state: StackBlockVisualState = StackBlockVisualState.NORMAL,
    val yOffset: Float = 0f,
    val opacity: Float = 1.0f,
    val scale: Float = 1.0f
)

enum class StackOperationType(val rawValue: String, val iconEmoji: String) {
    PUSH("PUSH", "⬇️"),
    POP("POP", "⬆️"),
    PEEK("PEEK", "👁️")
}

enum class StackTowerLevel(
    val id: Int,
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val xpReward: Int,
    val coinReward: Int
) {
    LEVEL_1(1, "What is a Stack?", "Vertical tower & The TOP pointer", "🥞", 20, 10),
    LEVEL_2(2, "The PUSH Power", "Drop elements onto the TOP", "⬇️", 30, 15),
    LEVEL_3(3, "The POP Eviction", "Remove only the uppermost block", "⬆️", 40, 20),
    LEVEL_4(4, "The PEEK Vision", "Inspect TOP without removing it", "👁️", 50, 25),
    LEVEL_5(5, "LIFO Prediction", "Predict which element pops next", "🔮", 60, 30),
    LEVEL_6(6, "The LIFO Gauntlet", "Execute speed push & pop sequences", "⚡", 80, 40),
    BOSS_BATTLE(7, "TOWER COLLAPSE", "Pop destabilized blocks to save the spire!", "💥", 100, 50);

    companion object {
        fun fromId(id: Int): StackTowerLevel = entries.find { it.id == id } ?: LEVEL_1
    }
}
