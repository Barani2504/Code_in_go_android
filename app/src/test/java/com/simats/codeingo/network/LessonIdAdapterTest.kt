package com.simats.codeingo.network

import com.simats.codeingo.data.network.repository.LessonIdAdapter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LessonIdAdapterTest {

    @Test
    fun testBasecampLevels_roundTrip() {
        // Basecamp Levels 101..105 -> cns.c0.l1..cns.c0.l5
        for (i in 101..105) {
            val backendId = LessonIdAdapter.toBackendLessonId(i)
            val expected = "cns.c0.l${i - 100}"
            assertEquals(expected, backendId)

            val decodedIndex = LessonIdAdapter.toLocalLevelIndex(backendId)
            assertEquals(i, decodedIndex)
        }
    }

    @Test
    fun testCoreChapters_roundTrip() {
        // Core Chapters 1..5 (Levels 1..25)
        for (i in 1..25) {
            val backendId = LessonIdAdapter.toBackendLessonId(i)
            val chapter = ((i - 1) / 5) + 1
            val stage = ((i - 1) % 5) + 1
            val expected = "cns.c$chapter.l$stage"
            assertEquals(expected, backendId)

            val decodedIndex = LessonIdAdapter.toLocalLevelIndex(backendId)
            assertEquals(i, decodedIndex)
        }
    }

    @Test
    fun testExpansionChapters_roundTrip() {
        // Expansion Chapters 6..10 (Levels 26..50)
        for (i in 26..50) {
            val backendId = LessonIdAdapter.toBackendLessonId(i)
            val chapter = ((i - 26) / 5) + 6
            val stage = ((i - 26) % 5) + 1
            val expected = "cns.c$chapter.l$stage"
            assertEquals(expected, backendId)

            val decodedIndex = LessonIdAdapter.toLocalLevelIndex(backendId)
            assertEquals(i, decodedIndex)
        }
    }

    @Test
    fun testForeignDsaIds_areRejected() {
        // iOS DSA curriculum IDs must map strictly to null
        assertNull(LessonIdAdapter.toLocalLevelIndex("u1_l1_what_is_ds"))
        assertNull(LessonIdAdapter.toLocalLevelIndex("u1_l2_array_intro"))
        assertNull(LessonIdAdapter.toLocalLevelIndex("u1_boss"))
        assertNull(LessonIdAdapter.toLocalLevelIndex("u5_boss"))
        assertNull(LessonIdAdapter.toLocalLevelIndex("random_id"))
        assertNull(LessonIdAdapter.toLocalLevelIndex("cns.invalid"))
        assertNull(LessonIdAdapter.toLocalLevelIndex("cns.c1.l99")) // invalid stage
    }

    @Test
    fun testExtractLocalIndicesFromRemoteList_filtersForeignIds() {
        val remoteList = listOf(
            "cns.c0.l1",
            "u1_l1_what_is_ds", // Foreign DSA ID
            "cns.c1.l1",
            "u2_boss",          // Foreign DSA ID
            "cns.c6.l1"
        )

        val localIndices = LessonIdAdapter.extractLocalIndicesFromRemoteList(remoteList)
        assertEquals(setOf(101, 1, 26), localIndices)
    }
}
