package com.simats.codeingo.ui.worlds.queue

import java.util.UUID

enum class QueueStationLevel(
    val id: Int,
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val xpReward: Int,
    val coinReward: Int
) {
    LEVEL_1(1, "One-Way Hash Gates", "Irreversible forward digest transit", "🔑", 10, 3),
    LEVEL_2(2, "The Avalanche Enqueue", "1-bit flip transforms 50% of hash", "💥", 10, 3),
    LEVEL_3(3, "The Sponge Dequeue", "SHA-3 Keccak absorb & squeeze stream", "🚪", 12, 4),
    LEVEL_4(4, "The HMAC Peek", "Inspect secret key XOR padding tags", "👁️", 12, 4),
    LEVEL_5(5, "PKI Signature Pipeline", "Verify X.509 certificate trust chains", "🔮", 14, 5),
    LEVEL_6(6, "Birthday Paradox Rush", "Find hash collisions within 2^(n/2)", "⚡", 15, 5),
    BOSS_BATTLE(7, "THE COLLISION BEAST", "Defend certificate integrity from hash collisions!", "🐾", 25, 15);

    companion object {
        fun fromId(id: Int): QueueStationLevel = entries.find { it.id == id } ?: LEVEL_1
    }
}
