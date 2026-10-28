package com.simats.codeingo.ui.visualizer.engine

/**
 * 🔍 Kasiski Examination & Classical Cryptanalysis Engine
 *
 * Implements:
 * 1. 26-letter frequency distribution and Chi-Squared goodness-of-fit to English
 * 2. Caesar automatic solver (evaluates all 26 shifts by Chi-Squared)
 * 3. Kasiski examination: repeated 3-gram distance spacing and factor frequency analysis
 * 4. Index of Coincidence (IoC) & Friedman key length estimation
 * 5. Multi-column coset solver to break Vigenère cipher automatically
 */
object KasiskiEngine {

    // Standard English monogram frequencies (percentages)
    val ENGLISH_FREQ = doubleArrayOf(
        8.2, 1.5, 2.8, 4.3, 12.7, 2.2, 2.0, 6.1, 7.0, 0.15,
        0.77, 4.0, 2.4, 6.7, 7.5, 1.9, 0.095, 6.0, 6.3, 9.1,
        2.8, 0.98, 2.4, 0.15, 2.0, 0.074
    )

    data class LetterCount(
        val char: Char,
        val count: Int,
        val percentage: Float,
        val expectedPercentage: Float
    )

    data class CaesarShiftScore(
        val shift: Int,
        val decryptedPreview: String,
        val chiSquared: Double
    )

    data class RepeatedNgram(
        val ngram: String,
        val positions: List<Int>,
        val distances: List<Int>
    )

    data class FactorCount(
        val factor: Int,
        val count: Int
    )

    data class KasiskiResult(
        val repeatedNgrams: List<RepeatedNgram>,
        val factorCounts: List<FactorCount>,
        val mostLikelyKeyLength: Int
    )

    data class VigenereCrackResult(
        val estimatedKeyLength: Int,
        val ioc: Double,
        val recoveredKey: String,
        val decryptedText: String
    )

    /** Computes letter frequency for letters A-Z */
    fun computeLetterFrequencies(text: String): List<LetterCount> {
        val counts = IntArray(26)
        var totalLetters = 0

        for (c in text.uppercase()) {
            if (c in 'A'..'Z') {
                counts[c - 'A']++
                totalLetters++
            }
        }

        val result = mutableListOf<LetterCount>()
        for (i in 0 until 26) {
            val char = ('A'.code + i).toChar()
            val cnt = counts[i]
            val pct = if (totalLetters > 0) (cnt.toFloat() / totalLetters) * 100f else 0f
            result.add(LetterCount(char, cnt, pct, ENGLISH_FREQ[i].toFloat()))
        }
        return result
    }

    /** Chi-squared metric comparing text letter counts to English expected counts */
    fun chiSquared(text: String): Double {
        val counts = IntArray(26)
        var total = 0
        for (c in text.uppercase()) {
            if (c in 'A'..'Z') {
                counts[c - 'A']++
                total++
            }
        }
        if (total == 0) return Double.MAX_VALUE

        var chi2 = 0.0
        for (i in 0 until 26) {
            val observed = counts[i].toDouble()
            val expected = (ENGLISH_FREQ[i] / 100.0) * total
            if (expected > 0.0) {
                val diff = observed - expected
                chi2 += (diff * diff) / expected
            }
        }
        return chi2
    }

    /** Evaluates all 26 possible Caesar shifts and ranks by Chi-Squared */
    fun analyzeCaesarShifts(ciphertext: String): List<CaesarShiftScore> {
        val scores = mutableListOf<CaesarShiftScore>()

        for (shift in 0..25) {
            val sb = java.lang.StringBuilder()
            for (c in ciphertext) {
                if (c in 'A'..'Z') {
                    val p = ((c - 'A' - shift + 26) % 26) + 'A'.code
                    sb.append(p.toChar())
                } else if (c in 'a'..'z') {
                    val p = ((c - 'a' - shift + 26) % 26) + 'a'.code
                    sb.append(p.toChar())
                } else {
                    sb.append(c)
                }
            }
            val dec = sb.toString()
            val score = chiSquared(dec)
            scores.add(CaesarShiftScore(shift, dec.take(40), score))
        }

        return scores.sortedBy { it.chiSquared }
    }

    /** Kasiski Examination: finds repeated 3-grams and factors their distances */
    fun kasiskiExamination(ciphertext: String, ngramLen: Int = 3): KasiskiResult {
        val clean = ciphertext.filter { it in 'A'..'Z' || it in 'a'..'z' }.uppercase()
        val posMap = mutableMapOf<String, MutableList<Int>>()

        for (i in 0..clean.length - ngramLen) {
            val gram = clean.substring(i, i + ngramLen)
            posMap.getOrPut(gram) { mutableListOf() }.add(i)
        }

        val repeated = mutableListOf<RepeatedNgram>()
        val allDistances = mutableListOf<Int>()

        for ((gram, positions) in posMap) {
            if (positions.size >= 2) {
                val dists = mutableListOf<Int>()
                for (i in 0 until positions.size - 1) {
                    val d = positions[i + 1] - positions[i]
                    dists.add(d)
                    allDistances.add(d)
                }
                repeated.add(RepeatedNgram(gram, positions, dists))
            }
        }

        // Count factors of distances
        val factorMap = mutableMapOf<Int, Int>()
        for (dist in allDistances) {
            for (f in 2..minOf(16, dist)) {
                if (dist % f == 0) {
                    factorMap[f] = (factorMap[f] ?: 0) + 1
                }
            }
        }

        val factorCounts = factorMap.map { FactorCount(it.key, it.value) }.sortedByDescending { it.count }
        val bestLen = factorCounts.firstOrNull()?.factor ?: 3

        return KasiskiResult(repeated, factorCounts, bestLen)
    }

    /** Index of Coincidence (IoC) calculation */
    fun indexOfCoincidence(text: String): Double {
        val clean = text.filter { it in 'A'..'Z' || it in 'a'..'z' }.uppercase()
        val n = clean.length
        if (n <= 1) return 0.0

        val counts = IntArray(26)
        for (c in clean) {
            counts[c - 'A']++
        }

        var sum = 0L
        for (cnt in counts) {
            sum += cnt.toLong() * (cnt - 1)
        }

        return sum.toDouble() / (n.toDouble() * (n - 1))
    }

    /** Automatically breaks Vigenère cipher given a candidate key length */
    fun breakVigenereWithKeyLength(ciphertext: String, keyLength: Int): VigenereCrackResult {
        val clean = ciphertext.filter { it in 'A'..'Z' || it in 'a'..'z' }.uppercase()
        val keyChars = CharArray(keyLength)

        for (col in 0 until keyLength) {
            val colChars = StringBuilder()
            var i = col
            while (i < clean.length) {
                colChars.append(clean[i])
                i += keyLength
            }

            // Find best Caesar shift for this column
            val shiftScores = analyzeCaesarShifts(colChars.toString())
            val bestShift = shiftScores.firstOrNull()?.shift ?: 0
            keyChars[col] = ('A'.code + bestShift).toChar()
        }

        val recoveredKey = String(keyChars)
        val ioc = indexOfCoincidence(clean)

        // Decrypt text using recovered key
        val sb = StringBuilder()
        var keyIdx = 0
        for (c in ciphertext) {
            if (c in 'A'..'Z') {
                val shift = keyChars[keyIdx % keyLength] - 'A'
                val p = ((c - 'A' - shift + 26) % 26) + 'A'.code
                sb.append(p.toChar())
                keyIdx++
            } else if (c in 'a'..'z') {
                val shift = keyChars[keyIdx % keyLength] - 'A'
                val p = ((c - 'a' - shift + 26) % 26) + 'a'.code
                sb.append(p.toChar())
                keyIdx++
            } else {
                sb.append(c)
            }
        }

        return VigenereCrackResult(
            estimatedKeyLength = keyLength,
            ioc = ioc,
            recoveredKey = recoveredKey,
            decryptedText = sb.toString()
        )
    }
}
