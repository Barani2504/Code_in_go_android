package com.simats.duolingo.domain.simulator

/* ================================================================== */
/*  REUSABLE VISUALIZER STEP-RECORDER ENGINE                          */
/* ================================================================== */

/**
 * Generic simulation engine interface:
 * Given a structure state S and an operation Op, records a discrete
 * sequence of immutable animation steps.
 */
interface DsSimulator<S, Op> {
    fun initial(): S
    fun apply(state: S, op: Op): List<Step<S>>
}

/**
 * A single discrete animation frame in a data structure operation.
 */
data class Step<S>(
    val state: S,
    val narration: String,
    val highlight: Highlight = Highlight.None,
    val codeLine: Int? = null,
    val costDelta: Cost = Cost.ZERO,
    val pointers: Map<String, Int> = emptyMap() // e.g. "i" -> 2, "prev" -> 0
)

/**
 * Element or node highlight tags for visualizer drawing.
 */
sealed interface Highlight {
    object None : Highlight
    data class Index(val index: Int, val tag: HighlightTag = HighlightTag.ACTIVE) : Highlight
    data class Indices(val indices: Set<Int>, val tag: HighlightTag = HighlightTag.COMPARING) : Highlight
    data class Node(val nodeId: String, val tag: HighlightTag = HighlightTag.ACTIVE) : Highlight
    data class Edge(val fromId: String, val toId: String, val tag: HighlightTag = HighlightTag.ACTIVE) : Highlight
    data class Range(val start: Int, val end: Int, val tag: HighlightTag = HighlightTag.ACTIVE) : Highlight
}

enum class HighlightTag {
    ACTIVE,
    COMPARING,
    SWAPPING,
    INSERTED,
    DELETED,
    VISITED,
    FOUND,
    ERROR
}

/**
 * Big-O Cost meter tracker for live operations.
 */
data class Cost(
    val comparisons: Int = 0,
    val movesOrShifts: Int = 0,
    val pointerUpdates: Int = 0,
    val memoryAllocated: Int = 0,
    val bigONotation: String = "O(1)"
) {
    operator fun plus(other: Cost) = Cost(
        comparisons = comparisons + other.comparisons,
        movesOrShifts = movesOrShifts + other.movesOrShifts,
        pointerUpdates = pointerUpdates + other.pointerUpdates,
        memoryAllocated = memoryAllocated + other.memoryAllocated,
        bigONotation = if (other.bigONotation != "O(1)") other.bigONotation else bigONotation
    )

    companion object {
        val ZERO = Cost()
    }
}
