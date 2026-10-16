package com.simats.codeingo.ui.arenas.key_exchange_citadel

enum class KeyExchangeCitadelLevel(
    val id: Int,
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val xpReward: Int,
    val coinReward: Int
) {
    LEVEL_1(1, "Clock Arithmetic", "Modular rings & exponentiation", "🔢", 10, 3),
    LEVEL_2(2, "Paint-Mix Handshake", "Diffie-Hellman color mixing & shared secret", "🎨", 10, 3),
    LEVEL_3(3, "Trapdoor Lab", "Euler's totient phi(n) = (p-1)(q-1)", "🚪", 12, 4),
    LEVEL_4(4, "Key Pair Forge", "RSA public (e, n) & private d confidentiality", "🔑", 12, 4),
    LEVEL_5(5, "Curve Walker", "Elliptic curve points & digital signatures", "📈", 14, 5),
    LEVEL_6(6, "Key-Size Gauntlet", "Square-and-multiply exponentiation sprint", "⚡", 15, 5),
    BOSS_BATTLE(7, "The Quantum Cryptanalyst", "Defend asymmetric keys against Shor's algorithm!", "💥", 25, 15);

    companion object {
        fun fromId(id: Int): KeyExchangeCitadelLevel = entries.find { it.id == id } ?: LEVEL_1
    }
}
