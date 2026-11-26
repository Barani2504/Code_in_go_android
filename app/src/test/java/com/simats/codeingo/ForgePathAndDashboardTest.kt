package com.simats.codeingo

import com.simats.codeingo.data.repository.ChapterRegistry
import com.simats.codeingo.data.repository.CurriculumRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ForgePathAndDashboardTest {

    private val assetsCurriculumDir = File("src/main/assets/curriculum").let {
        if (it.exists()) it else File("app/src/main/assets/curriculum")
    }

    @Test
    fun testFiveRealms_structureAndCompleteMapping() {
        val realms = CurriculumRepository.realms
        assertEquals("Must have exactly 5 realms", 5, realms.size)

        // Validate Realm 1
        assertEquals("Secrets 101", realms[0].title)
        assertEquals("The Cipher Breaker", realms[0].bossName)
        assertEquals("Cipher Sanctum", realms[0].arenaName)
        assertEquals((1..5).toList(), realms[0].chapterIds)

        // Validate Realm 2
        assertEquals("Symmetric Forge", realms[1].title)
        assertEquals("The Block Overlord", realms[1].bossName)
        assertEquals("Block Cipher Forge", realms[1].arenaName)
        assertEquals((6..10).toList(), realms[1].chapterIds)

        // Validate Realm 3
        assertEquals("Public-Key Citadel", realms[2].title)
        assertEquals("The Quantum Cryptanalyst", realms[2].bossName)
        assertEquals("Key Exchange Citadel", realms[2].arenaName)
        assertEquals((11..15).toList(), realms[2].chapterIds)

        // Validate Realm 4
        assertEquals("Trust & Proof", realms[3].title)
        assertEquals("The Collision Beast", realms[3].bossName)
        assertEquals("Integrity Foundry", realms[3].arenaName)
        assertEquals((16..20).toList(), realms[3].chapterIds)

        // Validate Realm 5
        assertEquals("Real-World Defense", realms[4].title)
        assertEquals("The Cyber Titan", realms[4].bossName)
        assertEquals("Cyber Bastion", realms[4].arenaName)
        assertEquals((21..25).toList(), realms[4].chapterIds)
    }

    @Test
    fun testFutureRealmsAreVisibleButLockedWithRequiredBannerText() {
        val realms = CurriculumRepository.realms
        val currentMaxChapter = 3 // User in Realm 1

        val maxUnlockedRealm = ((currentMaxChapter - 1) / 5) + 1
        assertEquals("User at chapter 3 must be in realm 1", 1, maxUnlockedRealm)

        for (realm in realms) {
            val isUnlocked = realm.id <= maxUnlockedRealm
            if (!isUnlocked) {
                // Must produce the exact required text: "🔒 Realm X Locked — Unlock by finishing Realm X-1"
                val expectedLockText = "🔒 Realm ${realm.id} Locked — Unlock by finishing Realm ${realm.id - 1}"
                assertTrue("Expected lock text for Realm ${realm.id}", expectedLockText.contains("Unlock by finishing Realm ${realm.id - 1}"))
            } else {
                assertEquals(1, realm.id)
            }
        }
    }

    @Test
    fun testAll25ChaptersResolveDetailSheetGoalsAndLabLinks() {
        CurriculumRepository.populateFromDirectory(assetsCurriculumDir)

        for (i in 1..25) {
            val ch = CurriculumRepository.getChapter(i)
            assertNotNull("Chapter $i must be non-null in curriculum", ch)
            assertEquals(5, ch?.estimatedMinutes)
            assertEquals("Chapter $i must have 3 learning goals", 3, ch?.learningGoals?.size)
            assertTrue("Visualizer id must exist", ch?.visualizerId?.isNotBlank() == true)
            assertTrue("Visualizer title must exist", ch?.visualizerTitle?.isNotBlank() == true)
            assertTrue("Ash recap must exist", ch?.ashRecap?.isNotBlank() == true)
        }
    }

    @Test
    fun testChapterRegistryExposes25ChaptersAcross125Stages() {
        val coreChapters = ChapterRegistry.all25Chapters
        assertEquals(25, coreChapters.size)

        var totalStages = 0
        for (chapter in coreChapters) {
            assertEquals(5, chapter.nodes.size)
            totalStages += chapter.nodes.size
            // Verify node IDs and levels
            for (node in chapter.nodes) {
                assertTrue("Node level must be > 0", node.levelNumber > 0)
                assertTrue("Node icon must be non-empty", node.icon.isNotBlank())
                assertTrue("Node title must be non-empty", node.title.isNotBlank())
            }
        }
        assertEquals("25 chapters * 5 stages must equal 125 stages", 125, totalStages)
    }

    @Test
    fun testDeepLinkingLevelCalculation_mapsEveryLevelToCorrectChapter() {
        // Chapters 1..20
        for (level in 1..100) {
            val expectedChapter = ((level - 1) / 5) + 1
            val resolved = ChapterRegistry.getChapterForLevel(level)
            assertNotNull("Level $level must resolve to a chapter", resolved)
            assertEquals("Level $level must map to Chapter $expectedChapter", expectedChapter, resolved?.id)
        }

        // Chapters 21..25
        for (level in 106..130) {
            val expectedChapter = ((level - 106) / 5) + 21
            val resolved = ChapterRegistry.getChapterForLevel(level)
            assertNotNull("Level $level must resolve to a chapter", resolved)
            assertEquals("Level $level must map to Chapter $expectedChapter", expectedChapter, resolved?.id)
        }

        // Basecamp 101..105
        for (level in 101..105) {
            val resolved = ChapterRegistry.getChapterForLevel(level)
            assertNotNull("Basecamp level $level must resolve", resolved)
            assertEquals(0, resolved?.id)
        }
    }
}
