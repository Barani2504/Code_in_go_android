package com.simats.codeingo.ui.arenas.routing

/**
 * 🔒 FROZEN SCHEMA CONTRACT: iOS Parity Route Keys
 *
 * These five route identifiers ("array", "linkedlist", "stack", "queue", "tree")
 * are frozen invariants under the Codeingo Dual-Skin Parity Contract (PARITY.md).
 * They map the frozen iOS DSA curriculum routes 1:1 to the active Android CNS Arenas.
 *
 * DO NOT RENAME OR MODIFY THESE STRING VALUES.
 */
object ArenaRoute {
    /** Unit 1: Classical Ciphers & Cryptanalysis (maps to iOS Unit 1: Arrays) */
    const val CIPHER_SANCTUM = "array"

    /** Unit 2: Symmetric Encryption & Block Ciphers (maps to iOS Unit 2: Linked Lists) */
    const val BLOCK_CIPHER_FORGE = "linkedlist"

    /** Unit 3: Asymmetric Cryptography & Public Keys (maps to iOS Unit 3: Stacks & Queues) */
    const val KEY_EXCHANGE_CITADEL = "stack"

    /** Unit 4: Cryptographic Hashes & Signatures (maps to iOS Unit 4: Hash Tables) */
    const val INTEGRITY_FOUNDRY = "queue"

    /** Unit 5: Network Security & Defense Protocols (maps to iOS Unit 5: Trees & Graphs) */
    const val CYBER_BASTION = "tree"

    val ALL_ROUTES: List<String> = listOf(
        CIPHER_SANCTUM,
        BLOCK_CIPHER_FORGE,
        KEY_EXCHANGE_CITADEL,
        INTEGRITY_FOUNDRY,
        CYBER_BASTION
    )
}
