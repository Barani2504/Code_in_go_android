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
    LEVEL_1(1, "Meet the Nodes", "Value + Next pointer container", "🔗", 10, 3),
    LEVEL_2(2, "Follow the Chain", "Traverse from HEAD to TAIL", "➡️", 10, 3),
    LEVEL_3(3, "Build the Chain", "Connect pointers between nodes", "🧩", 12, 4),
    LEVEL_4(4, "Insert a Node", "Redirect next pointers seamlessly", "➕", 12, 4),
    LEVEL_5(5, "Delete a Node", "Bypass with prev.next = curr.next", "✂️", 14, 5),
    LEVEL_6(6, "Pointer Sprint", "Traverse long pointer chains", "⚡", 15, 5),
    BOSS_BATTLE(7, "THE BROKEN CHAIN", "Reconnect severed pointers to defeat the boss!", "🔥", 25, 15);

    companion object {
        fun fromId(id: Int): LinkedListRoadLevel = entries.find { it.id == id } ?: LEVEL_1
    }
}

data class LLNodeItem(
    val id: String = UUID.randomUUID().toString(),
    val value: Int,
    val nextId: String? = null
)
