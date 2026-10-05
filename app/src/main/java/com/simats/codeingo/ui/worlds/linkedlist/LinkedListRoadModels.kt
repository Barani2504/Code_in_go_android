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
    LEVEL_1(1, "Meet the Nodes", "Value + Next pointer container", "🔗", 20, 10),
    LEVEL_2(2, "Follow the Chain", "Traverse from HEAD to TAIL", "➡️", 30, 15),
    LEVEL_3(3, "Build the Chain", "Connect pointers between nodes", "🧩", 40, 20),
    LEVEL_4(4, "Insert a Node", "Redirect next pointers seamlessly", "➕", 50, 25),
    LEVEL_5(5, "Delete a Node", "Bypass with prev.next = curr.next", "✂️", 60, 30),
    LEVEL_6(6, "Pointer Sprint", "Traverse long pointer chains", "⚡", 80, 40),
    BOSS_BATTLE(7, "THE BROKEN CHAIN", "Reconnect severed pointers to defeat the boss!", "🔥", 100, 50);

    companion object {
        fun fromId(id: Int): LinkedListRoadLevel = entries.find { it.id == id } ?: LEVEL_1
    }
}

data class LLNodeItem(
    val id: String = UUID.randomUUID().toString(),
    val value: Int,
    val nextId: String? = null
)
