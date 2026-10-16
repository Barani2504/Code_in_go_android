package com.simats.codeingo.ui.arenas.cyber_bastion

enum class CyberBastionLevel(
    val id: Int,
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val xpReward: Int,
    val coinReward: Int
) {
    LEVEL_1(1, "TLS 1.3 Handshake", "1-RTT ephemeral ECDH KeyShare negotiation", "🔐", 10, 3),
    LEVEL_2(2, "Cipher Suite Lab", "Modern AEAD cipher suites (GCM/Poly1305)", "🛡️", 10, 3),
    LEVEL_3(3, "IPsec ESP Tunnel", "Encapsulating Security Payload & anti-replay", "🧱", 12, 4),
    LEVEL_4(4, "Stateful Firewall", "TCP state tracking & port inspection", "🚪", 12, 4),
    LEVEL_5(5, "PKI Trust Chain", "X.509 certificate hierarchy & chain validation", "📜", 14, 5),
    LEVEL_6(6, "SYN Cookie Defense", "Stateless TCP sequence number DoS mitigation", "🍪", 15, 5),
    BOSS_BATTLE(7, "The Cyber Titan", "Multi-vector cyber defense against the Titan!", "🤖", 25, 15);

    companion object {
        fun fromId(id: Int): CyberBastionLevel = entries.find { it.id == id } ?: LEVEL_1
    }
}
