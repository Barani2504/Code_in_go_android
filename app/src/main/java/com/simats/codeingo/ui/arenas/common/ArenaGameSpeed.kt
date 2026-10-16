package com.simats.codeingo.ui.arenas.common

/**
 * ⚡ ArenaGameSpeed — Simulation Speed Multiplier for Codeingo CNS Battle Arenas
 */
enum class ArenaGameSpeed(val multiplier: Double, val label: String) {
    SLOW(0.5, "0.5x"),
    NORMAL(1.0, "1.0x"),
    FAST(2.0, "2.0x");

    fun next(): ArenaGameSpeed = when (this) {
        SLOW -> NORMAL
        NORMAL -> FAST
        FAST -> SLOW
    }
}
