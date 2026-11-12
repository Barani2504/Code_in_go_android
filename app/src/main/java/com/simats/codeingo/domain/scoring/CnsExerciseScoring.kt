package com.simats.codeingo.domain.scoring

/**
 * Pure Kotlin mathematical and algorithmic scoring engine for CNS exercises.
 * Decoupled from Android UI to enable rigorous unit testing and server reconciliation.
 */
object CnsExerciseScoring {

    /**
     * Scores a Caesar / Vigenère cipher wheel decryption.
     * Decrypts [ciphertext] with [userShift] (0..25) and verifies against [targetPlaintext].
     */
    fun scoreCipherWheel(
        ciphertext: String,
        userShift: Int,
        targetPlaintext: String
    ): Boolean {
        val normalizedShift = (userShift % 26 + 26) % 26
        val decrypted = ciphertext.uppercase().map { ch ->
            if (ch in 'A'..'Z') {
                val originalPos = ch - 'A'
                val newPos = (originalPos - normalizedShift + 26) % 26
                ('A' + newPos)
            } else {
                ch
            }
        }.joinToString("")

        return decrypted.trim().equals(targetPlaintext.uppercase().trim(), ignoreCase = true)
    }

    /**
     * Evaluates bit flipping and calculates the resulting Hamming distance & avalanche effect.
     * Returns true if user flipped at least [minFlipsRequired] and [flippedBits] differs from [originalBits].
     */
    fun calculateAvalancheHammingDistance(
        originalBits: String,
        flippedBits: String
    ): Int {
        val minLen = minOf(originalBits.length, flippedBits.length)
        var diffCount = 0
        for (i in 0 until minLen) {
            if (originalBits[i] != flippedBits[i]) {
                diffCount++
            }
        }
        diffCount += kotlin.math.abs(originalBits.length - flippedBits.length)
        return diffCount
    }

    fun scoreBitFlipper(
        originalBits: String,
        flippedBits: String,
        minFlipsRequired: Int = 1
    ): Boolean {
        val distance = calculateAvalancheHammingDistance(originalBits, flippedBits)
        return distance >= minFlipsRequired
    }

    /**
     * Evaluates round builder sequence (e.g. AES round: SubBytes -> ShiftRows -> MixColumns -> AddRoundKey).
     */
    fun scoreRoundBuilder(
        userOrder: List<String>,
        targetOrder: List<String>
    ): Boolean {
        if (userOrder.size != targetOrder.size) return false
        return userOrder == targetOrder
    }

    /**
     * Modular exponentiation: calculates (base^exponent) mod modulus efficiently.
     * Used in RSA and Diffie-Hellman questions.
     */
    fun modularExponentiation(base: Long, exponent: Long, modulus: Long): Long {
        if (modulus == 1L) return 0L
        var c = 1L
        var b = base % modulus
        var e = exponent
        while (e > 0L) {
            if (e % 2L == 1L) {
                c = (c * b) % modulus
            }
            e = e shr 1
            b = (b * b) % modulus
        }
        return c
    }

    fun scoreModularMath(
        base: Long,
        exponent: Long,
        modulus: Long,
        userAnswer: Long
    ): Boolean {
        val expected = modularExponentiation(base, exponent, modulus)
        return expected == userAnswer
    }

    /**
     * Evaluates packet triage classification (ALLOW, DROP, SUSPICIOUS).
     */
    fun scorePacketTriage(
        userClassifications: Map<String, String>,
        correctClassifications: Map<String, String>
    ): Boolean {
        if (userClassifications.isEmpty() || correctClassifications.isEmpty()) return false
        if (userClassifications.size < correctClassifications.size) return false
        return correctClassifications.all { (packetId, correctAction) ->
            userClassifications[packetId].equals(correctAction, ignoreCase = true)
        }
    }

    /**
     * Evaluates spot-the-flaw protocol/code analysis.
     */
    fun scoreSpotTheFlaw(
        selectedLineIndex: Int,
        vulnerableLineIndex: Int
    ): Boolean {
        return selectedLineIndex == vulnerableLineIndex
    }

    /**
     * Evaluates key size dial against required security tier.
     */
    fun scoreKeySizeDial(
        selectedKeySize: String,
        targetKeySize: String
    ): Boolean {
        return selectedKeySize.trim().equals(targetKeySize.trim(), ignoreCase = true)
    }
}

