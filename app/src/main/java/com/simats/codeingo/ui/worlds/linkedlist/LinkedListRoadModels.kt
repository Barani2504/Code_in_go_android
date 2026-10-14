package com.simats.codeingo.ui.worlds.linkedlist

import java.util.UUID

enum class LinkedListRoadLevel(
    val id: Int,
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val xpReward: Int,
    val coinReward: Int
) {
    LEVEL_1(1, "Meet the S-Box", "Non-linear byte substitution lookup", "🛡️", 10, 3),
    LEVEL_2(2, "Feistel Round Swap", "Cross-link Left and Right 32-bit halves", "🔄", 10, 3),
    LEVEL_3(3, "AES ShiftRows", "Cyclic permutation of matrix rows", "➡️", 12, 4),
    LEVEL_4(4, "Galois MixColumns", "Matrix multiplication over GF(2^8)", "➕", 12, 4),
    LEVEL_5(5, "Block Cipher Modes", "Chain cipher blocks (CBC & CTR)", "🔗", 14, 5),
    LEVEL_6(6, "GCM Tag Sprint", "Authenticate packet tag before timer ends", "⚡", 15, 5),
    BOSS_BATTLE(7, "THE BLOCK OVERLORD", "Shatter the S-Box fortress to defeat the boss!", "👹", 25, 15);

    companion object {
        fun fromId(id: Int): LinkedListRoadLevel = entries.find { it.id == id } ?: LEVEL_1
    }
}

data class LLNodeItem(
    val id: String = UUID.randomUUID().toString(),
    val value: Int,
    val nextId: String? = null
)
