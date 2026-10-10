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
    LEVEL_1(1, "What is a Stack?", "Vertical tower & The TOP pointer", "🥞", 10, 3),
    LEVEL_2(2, "The PUSH Power", "Drop elements onto the TOP", "⬇️", 10, 3),
    LEVEL_3(3, "The POP Eviction", "Remove only the uppermost block", "⬆️", 12, 4),
    LEVEL_4(4, "The PEEK Vision", "Inspect TOP without removing it", "👁️", 12, 4),
    LEVEL_5(5, "LIFO Prediction", "Predict which element pops next", "🔮", 14, 5),
    LEVEL_6(6, "The LIFO Gauntlet", "Execute speed push & pop sequences", "⚡", 15, 5),
    BOSS_BATTLE(7, "TOWER COLLAPSE", "Pop destabilized blocks to save the spire!", "💥", 25, 15);

    companion object {
        fun fromId(id: Int): StackTowerLevel = entries.find { it.id == id } ?: LEVEL_1
    }
}
