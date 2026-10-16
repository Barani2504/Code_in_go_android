package com.simats.codeingo.ui.arenas.integrity_foundry

enum class IntegrityFoundryLevel(
    val id: Int,
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val xpReward: Int,
    val coinReward: Int
) {
    LEVEL_1(1, "Digest Press", "Irreversible forward digest one-wayness", "🔨", 10, 3),
    LEVEL_2(2, "Bit-Flip Lab", "Strict avalanche criterion (1 bit flip cascades ~50%)", "💥", 10, 3),
    LEVEL_3(3, "Sponge Chamber", "SHA-3 Keccak absorb & squeeze state matrix", "🧽", 12, 4),
    LEVEL_4(4, "Wax Seal", "Merkle tree root validation & digest chaining", "🏷️", 12, 4),
    LEVEL_5(5, "Signature Seal", "Birthday bound collision search space 2^(n/2)", "📜", 14, 5),
    LEVEL_6(6, "Birthday Radar", "HMAC inner/outer pads & length extension defense", "⚡", 15, 5),
    BOSS_BATTLE(7, "The Collision Beast", "Defend certificate integrity from hash collisions!", "🐾", 25, 15);

    companion object {
        fun fromId(id: Int): IntegrityFoundryLevel = entries.find { it.id == id } ?: LEVEL_1
    }
}
