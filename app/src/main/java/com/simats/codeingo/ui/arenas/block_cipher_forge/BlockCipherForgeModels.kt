package com.simats.codeingo.ui.arenas.block_cipher_forge

enum class BlockCipherForgeLevel(
    val id: Int,
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val xpReward: Int,
    val coinReward: Int
) {
    LEVEL_1(1, "S-Box Forge", "Non-linear byte substitution in GF(2^8)", "🛡️", 10, 3),
    LEVEL_2(2, "ShiftRows Slider", "Cyclic permutation of 4x4 matrix rows", "➡️", 10, 3),
    LEVEL_3(3, "MixColumns Mixer", "Matrix multiplication over GF(2^8)", "🧪", 12, 4),
    LEVEL_4(4, "AddRoundKey Lock", "Bitwise state key XOR alignment", "🔐", 12, 4),
    LEVEL_5(5, "Mode Detective", "Inspect ECB structural leak & IV reuse", "🕵️", 14, 5),
    LEVEL_6(6, "Avalanche Sprint", "Bit diffusion & avalanche containment", "⚡", 15, 5),
    BOSS_BATTLE(7, "THE BLOCK OVERLORD", "3-Phase AES Boss Battle: Conquer the Bastion", "👹", 25, 15);

    companion object {
        fun fromId(id: Int): BlockCipherForgeLevel = entries.find { it.id == id } ?: LEVEL_1
    }
}
