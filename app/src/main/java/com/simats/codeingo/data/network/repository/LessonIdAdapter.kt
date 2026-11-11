package com.simats.codeingo.data.network.repository

import com.simats.codeingo.data.repository.ChapterRegistry

/**
 * Universal bidirectional adapter between Android CNS level indices
 * and standardized backend lesson IDs.
 *
 * Guarantees zero drift with ChapterRegistry while fully supporting
 * all backend curriculum seeds (CNS, Crypto, and DSA unit mappings).
 */
object LessonIdAdapter {

    private const val PREFIX = "cns"

    /**
     * Converts an Android integer level index to a standardized backend lesson ID.
     *  - Basecamp (101..105) -> cns.c0.l1 .. cns.c0.l5
     *  - Core Chapters 1..5 (1..25) -> cns.c1.l1 .. cns.c5.l5
     *  - Expansion Chapters 6..10 (26..50) -> cns.c6.l1 .. cns.c10.l5
     */
    fun toBackendLessonId(levelIndex: Int): String {
        return when {
            ChapterRegistry.isBasecampLevel(levelIndex) -> {
                "$PREFIX.c0.l${levelIndex - 100}"
            }
            ChapterRegistry.isExpansionLevel(levelIndex) -> {
                val chapter = ((levelIndex - 26) / 5) + 6
                val stage = ((levelIndex - 26) % 5) + 1
                "$PREFIX.c$chapter.l$stage"
            }
            levelIndex in 1..25 -> {
                val chapter = ((levelIndex - 1) / 5) + 1
                val stage = ((levelIndex - 1) % 5) + 1
                "$PREFIX.c$chapter.l$stage"
            }
            else -> "$PREFIX.unknown.l$levelIndex"
        }
    }

    /**
     * Converts a backend lesson ID back to an Android integer level index.
     * Supports:
     *  - Standard CNS IDs: cns.c1.l1 .. cns.c10.l5, cns.1.1
     *  - Direct indices: "1" .. "50", "level_1" .. "level_50", "101" .. "105"
     *  - Crypto seeds: "crypto_l1_caesar_vigenere" .. "crypto_l16_boss_zkp"
     *  - Unit seeds: "unit_crypto_01", "unit_01_foundations", "unit_01" -> capstone boss level
     *  - Generic seeds: "u1_l1_what_is_ds" .. "u10_l4_boss_dsu"
     *  - Basecamp: "c0_l1" .. "c0_l5"
     */
    fun toLocalLevelIndex(backendId: String): Int? {
        val trimmed = backendId.trim().lowercase()

        // Direct integer: "1" .. "50", "101" .. "105"
        trimmed.toIntOrNull()?.let { num ->
            if (num in 1..50 || num in 101..105) return num
        }

        // Direct level prefix: "level_5", "level-5"
        if (trimmed.startsWith("level_") || trimmed.startsWith("level-")) {
            val num = trimmed.substring(6).toIntOrNull()
            if (num != null && (num in 1..50 || num in 101..105)) return num
        }

        // Basecamp prefix: "c0_l1", "basecamp_l1"
        if (trimmed.startsWith("c0_l") || trimmed.startsWith("c0.l")) {
            val stage = trimmed.substring(4).toIntOrNull()
            if (stage != null && stage in 1..5) return 100 + stage
        }

        // Normalized separators: replace '-' and '_' with '.'
        val normalized = trimmed.replace('_', '.').replace('-', '.')

        // Match cns.c{chapter}.l{stage}
        val matchDot = Regex("^cns\\.c(\\d+)\\.l(\\d+)$").find(normalized)
        if (matchDot != null) {
            val chapter = matchDot.groupValues[1].toIntOrNull() ?: return null
            val stage = matchDot.groupValues[2].toIntOrNull() ?: return null
            if (stage !in 1..5) return null
            return when (chapter) {
                0 -> 100 + stage
                in 1..5 -> ((chapter - 1) * 5) + stage
                in 6..10 -> 25 + ((chapter - 6) * 5) + stage
                else -> null
            }
        }

        // Match cns.{chapter}.{stage}
        val matchSimple = Regex("^cns\\.(\\d+)\\.(\\d+)$").find(normalized)
        if (matchSimple != null) {
            val chapter = matchSimple.groupValues[1].toIntOrNull() ?: return null
            val stage = matchSimple.groupValues[2].toIntOrNull() ?: return null
            val adjustedStage = if (stage == 0) 1 else stage
            if (adjustedStage !in 1..5) return null
            return when (chapter) {
                0 -> 100 + adjustedStage
                in 1..5 -> ((chapter - 1) * 5) + adjustedStage
                in 6..10 -> 25 + ((chapter - 6) * 5) + adjustedStage
                else -> null
            }
        }

        // Match Crypto Seed Lessons: crypto_l1 .. crypto_l16
        val matchCrypto = Regex("^crypto\\.l(\\d+)(?:\\..*)?$").find(normalized)
        if (matchCrypto != null) {
            val num = matchCrypto.groupValues[1].toIntOrNull() ?: return null
            return when (num) {
                in 1..4 -> if (num == 4) 5 else num
                in 5..8 -> if (num == 8) 10 else 5 + (num - 4)
                in 9..12 -> if (num == 12) 15 else 10 + (num - 8)
                in 13..16 -> if (num == 16) 20 else 15 + (num - 12)
                else -> null
            }
        }

        // Match Course Unit seeds: unit_crypto_01, unit_01_foundations, unit_01, unit_1
        val matchUnit = Regex("^unit(?:\\.crypto)?\\.(\\d+)(?:\\..*)?$").find(normalized)
        if (matchUnit != null) {
            val unitNum = matchUnit.groupValues[1].toIntOrNull() ?: return null
            if (unitNum in 1..10) {
                return if (unitNum <= 5) unitNum * 5 else 25 + (unitNum - 5) * 5
            }
        }

        // Match Generic backend lesson seeds: u1_l1_..., u1_l4_boss_...
        val matchUL = Regex("^u(\\d+)\\.l(\\d+)(?:\\..*)?$").find(normalized)
        if (matchUL != null) {
            val unit = matchUL.groupValues[1].toIntOrNull() ?: return null
            val lesson = matchUL.groupValues[2].toIntOrNull() ?: return null
            if (unit in 1..10) {
                val isBoss = trimmed.contains("boss") || lesson >= 4
                return if (isBoss) {
                    if (unit <= 5) unit * 5 else 25 + (unit - 5) * 5
                } else {
                    val base = if (unit <= 5) (unit - 1) * 5 else 25 + (unit - 6) * 5
                    base + lesson.coerceIn(1, 4)
                }
            }
        }

        return null
    }

    /**
     * Parses a unit string (e.g. "unit_01_foundations", "unit_crypto_02", "3")
     * into an integer unit/chapter index (1..10).
     */
    fun parseUnitNumber(unitStr: String?): Int? {
        if (unitStr.isNullOrBlank()) return null
        val trimmed = unitStr.trim().lowercase()
        trimmed.toIntOrNull()?.let { num ->
            if (num in 1..10) return num
        }
        val match = Regex("unit_(?:crypto_)?(\\d+)").find(trimmed)
        if (match != null) {
            val num = match.groupValues[1].toIntOrNull()
            if (num != null && num in 1..10) return num
        }
        return null
    }

    /**
     * Filters a list of remote backend lesson IDs, translating them into
     * Android level indices.
     */
    fun extractLocalIndicesFromRemoteList(remoteIds: List<String>?): Set<Int> {
        if (remoteIds.isNullOrEmpty()) return emptySet()
        return remoteIds.mapNotNull { toLocalLevelIndex(it) }.toSet()
    }
}
