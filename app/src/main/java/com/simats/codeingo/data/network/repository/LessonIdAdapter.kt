package com.simats.codeingo.data.network.repository

import com.simats.codeingo.data.repository.ChapterRegistry

/**
 * Pure programmatic bidirectional adapter between Android CNS level indices
 * and standardized backend lesson IDs.
 *
 * Guarantees zero drift with ChapterRegistry.
 * Strictly ignores foreign DSA IDs (e.g. u1_l1).
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
     * Returns null if the ID is foreign (e.g., DSA u1_l1) or invalid.
     */
    fun toLocalLevelIndex(backendId: String): Int? {
        val trimmed = backendId.trim().lowercase()
        if (!trimmed.startsWith("$PREFIX.c")) return null

        val parts = trimmed.removePrefix("$PREFIX.c").split(".l")
        if (parts.size != 2) return null

        val chapter = parts[0].toIntOrNull() ?: return null
        val stage = parts[1].toIntOrNull() ?: return null
        if (stage !in 1..5) return null

        return when (chapter) {
            0 -> 100 + stage
            in 1..5 -> ((chapter - 1) * 5) + stage
            in 6..10 -> 25 + ((chapter - 6) * 5) + stage
            else -> null
        }
    }

    /**
     * Filters a list of remote backend lesson IDs, keeping only valid CNS lessons
     * and translating them into Android level indices. Foreign DSA IDs are discarded.
     */
    fun extractLocalIndicesFromRemoteList(remoteIds: List<String>?): Set<Int> {
        if (remoteIds.isNullOrEmpty()) return emptySet()
        return remoteIds.mapNotNull { toLocalLevelIndex(it) }.toSet()
    }
}
