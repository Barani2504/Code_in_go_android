package com.simats.codeingo.ui.worlds.tree

import java.util.UUID

enum class BinaryTreeForestLevel(
    val id: Int,
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val xpReward: Int,
    val coinReward: Int
) {
    LEVEL_1(1, "Find the Root", "The single ancestor at the top of the canopy", "👑", 20, 10),
    LEVEL_2(2, "Parent & Child", "Left and Right branching pathways", "🌿", 30, 15),
    LEVEL_3(3, "Find the Leaves", "Terminal nodes with no descendants", "🍃", 40, 20),
    LEVEL_4(4, "BST Property", "Left < Root < Right rule", "⚖️", 50, 25),
    LEVEL_5(5, "BST Insertion", "Traverse and insert into correct slot", "🌱", 60, 30),
    LEVEL_6(6, "In-Order Traversal", "Left ➔ Root ➔ Right = Sorted!", "📜", 80, 40),
    BOSS_BATTLE(7, "THE OVERGROWN ENT", "Balance the corrupted ancient tree!", "🌲", 100, 50);

    companion object {
        fun fromId(id: Int): BinaryTreeForestLevel = entries.find { it.id == id } ?: LEVEL_1
    }
}
