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
    LEVEL_1(1, "What is a Queue?", "Transit line: FRONT vs REAR pointers", "🎫", 20, 10),
    LEVEL_2(2, "The ENQUEUE Boarding", "Passengers join only at the REAR", "➡️", 30, 15),
    LEVEL_3(3, "The DEQUEUE Dispatch", "Depart strictly from the FRONT gate", "🚪", 40, 20),
    LEVEL_4(4, "The PEEK Passenger", "Inspect the FRONT without departure", "👁️", 50, 25),
    LEVEL_5(5, "FIFO Dispatch Prediction", "First-In, First-Out sequence check", "🔮", 60, 30),
    LEVEL_6(6, "Ticket Rush Gauntlet", "Fast-paced passenger boarding", "⚡", 80, 40),
    BOSS_BATTLE(7, "GRIDLOCK EXPRESS", "Clear the congested platform before gridlock!", "🚆", 100, 50);

    companion object {
        fun fromId(id: Int): QueueStationLevel = entries.find { it.id == id } ?: LEVEL_1
    }
}
