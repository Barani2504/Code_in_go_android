package com.simats.codeingo

import com.simats.codeingo.data.db.MistakeVaultEntity
import com.simats.codeingo.data.model.DSAExerciseItem
import com.simats.codeingo.data.model.DSAExerciseType
import com.simats.codeingo.data.model.MistakeVaultItem
import com.simats.codeingo.data.repository.ChapterRegistry
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.ui.arenas.routing.ArenaRoute
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RoomAndParityHardeningTest {

    @Test
    fun testMistakeVaultEntity_roundTripMapping() {
        val item = MistakeVaultItem(
            id = "test_item_123",
            prompt = "What does AES MixColumns operate on?",
            conceptId = "aes_mix_columns",
            wrongAnswerGiven = "Bits individually",
            correctAnswer = "Four bytes in each state matrix column over GF(2^8)",
            explanation = "MixColumns treats each column of four bytes as a four-term polynomial.",
            timestamp = 1700000000000L,
            isRepaired = false,
            conceptTag = "Galois Field GF(2^8)",
            misconceptionTag = "Bit vs Byte Diffusion",
            chapterId = 2
        )

        val entity = MistakeVaultEntity.fromDomain(item, attemptCount = 3)
        assertEquals("test_item_123", entity.id)
        assertEquals(3, entity.attemptCount)
        assertEquals("aes_mix_columns", entity.conceptId)

        val domainRestored = entity.toDomain()
        assertEquals(item.id, domainRestored.id)
        assertEquals(item.prompt, domainRestored.prompt)
        assertEquals(item.conceptId, domainRestored.conceptId)
        assertEquals(item.wrongAnswerGiven, domainRestored.wrongAnswerGiven)
        assertEquals(item.correctAnswer, domainRestored.correctAnswer)
        assertEquals(item.explanation, domainRestored.explanation)
        assertEquals(item.timestamp, domainRestored.timestamp)
        assertEquals(item.isRepaired, domainRestored.isRepaired)
        assertEquals(item.conceptTag, domainRestored.conceptTag)
        assertEquals(item.misconceptionTag, domainRestored.misconceptionTag)
        assertEquals(item.chapterId, domainRestored.chapterId)
    }

    @Test
    fun testHeartDeductions_boundsAndSingleDeduction() {
        val gm = GameManager.instance
        gm.refillHearts()
        assertEquals(10, gm.heartsCount.value)

        // 1. Single loseHeart call deducts exactly 1
        gm.loseHeart()
        assertEquals(9, gm.heartsCount.value)

        // 2. Single recordWrongAnswer deducts exactly 1
        val mockExercise = DSAExerciseItem(
            id = "test_ex",
            type = DSAExerciseType.MULTIPLE_CHOICE,
            prompt = "Test prompt",
            conceptId = "test_concept",
            options = listOf("Wrong", "Right"),
            correctIndex = 1,
            explanation = "Explanation"
        )
        gm.recordWrongAnswer(mockExercise, "Wrong")
        assertEquals(8, gm.heartsCount.value)

        // 3. Lower boundary clamping at 0
        while (gm.heartsCount.value > 0) {
            gm.loseHeart()
        }
        assertEquals(0, gm.heartsCount.value)

        // Cannot drop below 0
        gm.loseHeart()
        assertEquals(0, gm.heartsCount.value)

        gm.recordWrongAnswer(mockExercise, "Wrong")
        assertEquals(0, gm.heartsCount.value)
    }

    @Test
    fun testFrozenParity_coreChaptersAndArenaRoutes() {
        // Assert exactly 5 core chapters exist for iOS parity
        val coreUnits = ChapterRegistry.getCoreUnits()
        assertEquals(5, coreUnits.size)
        assertEquals(1, coreUnits[0].id)
        assertEquals(2, coreUnits[1].id)
        assertEquals(3, coreUnits[2].id)
        assertEquals(4, coreUnits[3].id)
        assertEquals(5, coreUnits[4].id)

        // Assert expansion units are separate and do not contaminate core unit IDs
        val expansionUnits = ChapterRegistry.getExpansionUnits()
        assertTrue("Expansion units exist", expansionUnits.isNotEmpty())
        for (exp in expansionUnits) {
            assertTrue("Expansion unit ID must be > 5 or 0", exp.id > 5 || exp.id == 0)
        }

        // Assert all 5 ArenaRoute keys strictly match iOS parity contract
        assertEquals("array", ArenaRoute.CIPHER_SANCTUM)
        assertEquals("linkedlist", ArenaRoute.BLOCK_CIPHER_FORGE)
        assertEquals("stack", ArenaRoute.KEY_EXCHANGE_CITADEL)
        assertEquals("queue", ArenaRoute.INTEGRITY_FOUNDRY)
        assertEquals("tree", ArenaRoute.CYBER_BASTION)
    }
}
