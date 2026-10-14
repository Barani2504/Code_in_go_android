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
    LEVEL_1(1, "Modular Arithmetic", "Modular rings & coprimality tests", "🔢", 10, 3),
    LEVEL_2(2, "Diffie-Hellman Push", "Generate public keys g^a mod p", "⬇️", 10, 3),
    LEVEL_3(3, "Euler's Totient Pop", "Calculate phi(n) = (p-1)(q-1)", "⬆️", 12, 4),
    LEVEL_4(4, "RSA ModExp Peek", "Inspect public (e, n) & private d", "👁️", 12, 4),
    LEVEL_5(5, "Point Add Prediction", "Predict elliptic chord reflections", "🔮", 14, 5),
    LEVEL_6(6, "Key Generator Gauntlet", "Synthesize 2048-bit keys at speed", "⚡", 15, 5),
    BOSS_BATTLE(7, "THE QUANTUM CRYPTANALYST", "Defend asymmetric keys against quantum factoring!", "💥", 25, 15);

    companion object {
        fun fromId(id: Int): StackTowerLevel = entries.find { it.id == id } ?: LEVEL_1
    }
}
