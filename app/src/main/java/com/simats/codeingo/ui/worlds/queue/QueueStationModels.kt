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
    LEVEL_1(1, "What is a Queue?", "Transit line: FRONT vs REAR pointers", "🎫", 10, 3),
    LEVEL_2(2, "The ENQUEUE Boarding", "Passengers join only at the REAR", "➡️", 10, 3),
    LEVEL_3(3, "The DEQUEUE Dispatch", "Depart strictly from the FRONT gate", "🚪", 12, 4),
    LEVEL_4(4, "The PEEK Passenger", "Inspect the FRONT without departure", "👁️", 12, 4),
    LEVEL_5(5, "FIFO Dispatch Prediction", "First-In, First-Out sequence check", "🔮", 14, 5),
    LEVEL_6(6, "Ticket Rush Gauntlet", "Fast-paced passenger boarding", "⚡", 15, 5),
    BOSS_BATTLE(7, "GRIDLOCK EXPRESS", "Clear the congested platform before gridlock!", "🚆", 25, 15);

    companion object {
        fun fromId(id: Int): QueueStationLevel = entries.find { it.id == id } ?: LEVEL_1
    }
}
