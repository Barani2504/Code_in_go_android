package com.simats.codeingo.domain.guide

/**
 * Single cryptographic concept defined in plain 8th-grade English.
 */
data class GlossaryTerm(
    val id: String,
    val term: String,
    val shortDefinition: String,
    val example: String,
    val category: String
)

/**
 * Centralized plain-language cryptographic glossary for AshCrypt.
 * Rule: 8th grade reading level, one idea per sentence, concrete real-world example.
 */
object CryptoGlossary {

    val TERMS: List<GlossaryTerm> = listOf(
        GlossaryTerm(
            id = "cipher",
            term = "Cipher",
            shortDefinition = "A secret recipe of mathematical steps used to turn readable text into an unreadable code.",
            example = "The Caesar cipher shifts each letter forward by 3 spots (A becomes D).",
            category = "Basics"
        ),
        GlossaryTerm(
            id = "plaintext",
            term = "Plaintext",
            shortDefinition = "The original, clear message you want to protect before it gets scrambled.",
            example = "\"ATTACK AT DAWN\" before any cipher is applied.",
            category = "Basics"
        ),
        GlossaryTerm(
            id = "ciphertext",
            term = "Ciphertext",
            shortDefinition = "The scrambled, unreadable message produced after encryption.",
            example = "\"DWWDFN DW GDZQ\" looks like complete nonsense to an eavesdropper.",
            category = "Basics"
        ),
        GlossaryTerm(
            id = "key",
            term = "Key",
            shortDefinition = "A secret number or password needed to lock and unlock a cryptographic cipher.",
            example = "If the Caesar shift is +3, then 3 is the secret key.",
            category = "Basics"
        ),
        GlossaryTerm(
            id = "encryption",
            term = "Encryption",
            shortDefinition = "The process of scrambling your readable message so only authorized eyes can read it.",
            example = "Locking a diary page into a secure safe.",
            category = "Basics"
        ),
        GlossaryTerm(
            id = "decryption",
            term = "Decryption",
            shortDefinition = "The process of using your secret key to turn scrambled code back into readable words.",
            example = "Using your key to unlock the safe and read the diary.",
            category = "Basics"
        ),
        GlossaryTerm(
            id = "symmetric",
            term = "Symmetric Encryption",
            shortDefinition = "A security system where the exact same secret key is used to both lock and unlock data.",
            example = "AES uses one shared key that both Alice and Bob keep private.",
            category = "Ciphers"
        ),
        GlossaryTerm(
            id = "asymmetric",
            term = "Asymmetric Encryption",
            shortDefinition = "A system that uses two paired keys: a public key to lock messages, and a private key to unlock them.",
            example = "Anyone can drop a letter through your mailbox slot, but only you hold the key to open it.",
            category = "Public Key"
        ),
        GlossaryTerm(
            id = "hash",
            term = "Cryptographic Hash",
            shortDefinition = "A one-way digital fingerprint that turns any file or text into a unique, fixed-size code.",
            example = "SHA-256 converts a whole book or a single word into exactly 64 hexadecimal characters.",
            category = "Integrity"
        ),
        GlossaryTerm(
            id = "avalanche",
            term = "Avalanche Effect",
            shortDefinition = "A crucial security property where changing just one letter completely changes over half the resulting code.",
            example = "Hashing \"cat\" vs \"Cat\" produces two totally unrecognizable outputs.",
            category = "Integrity"
        ),
        GlossaryTerm(
            id = "salt",
            term = "Salt",
            shortDefinition = "Random bits added to a password before hashing so identical passwords produce totally different fingerprints.",
            example = "Adding random characters like \"9x#Q\" prevents hackers from using pre-computed password lookup tables.",
            category = "Authentication"
        ),
        GlossaryTerm(
            id = "nonce",
            term = "Nonce",
            shortDefinition = "A \"number used once\" to make sure an attacker cannot record and replay old messages.",
            example = "A unique ticket number stamped on every message that expires immediately.",
            category = "Protocols"
        ),
        GlossaryTerm(
            id = "iv",
            term = "Initialization Vector (IV)",
            shortDefinition = "A random starting block that ensures two identical messages encrypted with the same key look completely different.",
            example = "Used in AES-CBC mode so repeating sentences don't leave visible patterns.",
            category = "Ciphers"
        ),
        GlossaryTerm(
            id = "mac",
            term = "Message Authentication Code (MAC)",
            shortDefinition = "A tamper-evident digital seal that proves a message was sent by a friend and was not changed on the wire.",
            example = "HMAC mixes the message with a secret key to guarantee integrity.",
            category = "Integrity"
        ),
        GlossaryTerm(
            id = "digital_signature",
            term = "Digital Signature",
            shortDefinition = "A mathematical stamp created with a private key that anyone can verify using the matching public key.",
            example = "Proves an operating system update genuinely came from the real company.",
            category = "Trust"
        ),
        GlossaryTerm(
            id = "diffie_hellman",
            term = "Diffie-Hellman",
            shortDefinition = "A brilliant trick that lets two people agree on a shared secret across the public internet without an eavesdropper learning it.",
            example = "Mixing shared public paint colors with private secret paint colors.",
            category = "Key Exchange"
        ),
        GlossaryTerm(
            id = "rsa",
            term = "RSA Algorithm",
            shortDefinition = "A public-key cipher whose security relies on the fact that multiplying two huge prime numbers is easy, but factoring their product is nearly impossible.",
            example = "Multiplying 61 * 53 is quick (3233), but guessing the two primes from 3233 takes much longer.",
            category = "Public Key"
        ),
        GlossaryTerm(
            id = "mitm",
            term = "Man-in-the-Middle (MITM)",
            shortDefinition = "An attack where a hacker sits quietly between two parties, intercepting and altering their conversation.",
            example = "A malicious Wi-Fi hotspot pretending to be your bank.",
            category = "Attacks"
        ),
        GlossaryTerm(
            id = "steganography",
            term = "Steganography",
            shortDefinition = "The art of hiding a secret message inside ordinary data, like embedding text inside photo pixels.",
            example = "Tucking secret coordinates into the least significant color bits of a cat picture.",
            category = "Basics"
        ),
        GlossaryTerm(
            id = "zkp",
            term = "Zero-Knowledge Proof",
            shortDefinition = "A way to prove you know a secret without revealing even a single detail of the secret itself.",
            example = "Proving you have the key to a circular cave door by walking in one entrance and coming out the other.",
            category = "Advanced"
        )
    )

    fun findTerm(query: String): GlossaryTerm? {
        val q = query.trim().lowercase()
        return TERMS.firstOrNull { it.term.lowercase() == q || it.id.lowercase() == q }
            ?: TERMS.firstOrNull { it.term.lowercase().contains(q) }
    }

    fun searchTerms(query: String): List<GlossaryTerm> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return TERMS
        return TERMS.filter {
            it.term.lowercase().contains(q) ||
            it.shortDefinition.lowercase().contains(q) ||
            it.category.lowercase().contains(q)
        }
    }
}
