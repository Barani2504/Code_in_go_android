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
    LEVEL_1(1, "TLS 1.3 1-RTT Handshake", "ClientHello KeyShare exchange", "🔐", 10, 3),
    LEVEL_2(2, "IPsec ESP Tunnel Mode", "Encapsulate entire IP packets", "🛡️", 10, 3),
    LEVEL_3(3, "Stateful Firewall 5-Tuple", "Track TCP SYN_SENT and ESTABLISHED", "🧱", 12, 4),
    LEVEL_4(4, "Cryptographic SYN Cookies", "Stateless TCP DoS flood defense", "🍪", 12, 4),
    LEVEL_5(5, "DNSSEC Trust Anchor Chain", "Traverse RRSIG digital signatures", "⛓️", 14, 5),
    LEVEL_6(6, "Zero Trust Microsegmentation", "Never trust, always verify perimeter", "⚡", 15, 5),
    BOSS_BATTLE(7, "THE CYBER TITAN", "Deploy multi-layer defense to vanquish the titan!", "🤖", 25, 15);

    companion object {
        fun fromId(id: Int): BinaryTreeForestLevel = entries.find { it.id == id } ?: LEVEL_1
    }
}
