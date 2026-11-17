package com.simats.codeingo

import com.simats.codeingo.data.model.CnsChapter
import com.simats.codeingo.data.repository.ChapterRegistry
import com.simats.codeingo.data.repository.CurriculumRepository
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class CurriculumValidationTest {

    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val assetsCurriculumDir = File("src/main/assets/curriculum").let {
        if (it.exists()) it else File("app/src/main/assets/curriculum")
    }

    @Test
    fun testAll25ChapterJsonFilesExist() {
        assertTrue("Curriculum directory must exist at: ${assetsCurriculumDir.absolutePath}", assetsCurriculumDir.exists())
        for (i in 1..25) {
            val fileName = "chapter_${String.format("%02d", i)}.json"
            val file = File(assetsCurriculumDir, fileName)
            assertTrue("Chapter JSON file must exist: $fileName", file.exists())
            assertTrue("Chapter JSON file must not be empty: $fileName", file.length() > 500)
        }
    }

    @Test
    fun testAll25ChaptersParseAndValidateSchema() {
        for (i in 1..25) {
            val fileName = "chapter_${String.format("%02d", i)}.json"
            val file = File(assetsCurriculumDir, fileName)
            val jsonContent = file.readText(Charsets.UTF_8)
            val chapter: CnsChapter = jsonParser.decodeFromString(jsonContent)

            // Basic Metadata
            assertEquals("Chapter id must match index", i, chapter.id)
            assertEquals("Chapter number must match index", i, chapter.number)
            val expectedRealmId = ((i - 1) / 5) + 1
            assertEquals("Chapter realmId must match 5-chapter partition", expectedRealmId, chapter.realmId)
            assertTrue("Chapter title must not be blank", chapter.title.isNotBlank())
            assertTrue("Syllabus unit must not be blank", chapter.syllabusUnit.isNotBlank())
            assertTrue("Visualizer id must not be blank", chapter.visualizerId.isNotBlank())
            assertTrue("Visualizer title must not be blank", chapter.visualizerTitle.isNotBlank())
            assertTrue("Ash recap must not be blank", chapter.ashRecap.isNotBlank())
            assertEquals("Estimated minutes must be 5", 5, chapter.estimatedMinutes)

            // Learning Goals: 3 goals starting with "You will be able to"
            assertEquals("Chapter $i must have exactly 3 learning goals", 3, chapter.learningGoals.size)
            for (goal in chapter.learningGoals) {
                assertTrue("Goal must start with 'You will be able to': $goal", goal.startsWith("You will be able to"))
            }

            // Lesson Cards: 3 to 4 cards, each with coreConcept <= 40 words
            assertTrue("Chapter $i must have 3-4 lesson cards", chapter.lessonCards.size in 3..4)
            for (card in chapter.lessonCards) {
                assertTrue("Lesson card title must not be blank", card.title.isNotBlank())
                assertTrue("Lesson card analogy must not be blank", card.analogy.isNotBlank())
                assertTrue("Lesson card coreConcept must not be blank", card.coreConcept.isNotBlank())
                assertTrue("Lesson card example must not be blank", card.example.isNotBlank())
                val wordCount = card.coreConcept.split(Regex("\\s+")).count { it.isNotBlank() }
                assertTrue("Lesson card coreConcept must be <= 40 words, was $wordCount in '${card.title}'", wordCount <= 40)
            }

            // Questions: 6 to 8 questions
            assertTrue("Chapter $i must have 6-8 questions, had ${chapter.questions.size}", chapter.questions.size in 6..8)
            for (q in chapter.questions) {
                assertTrue("Question id must not be blank", q.id.isNotBlank())
                assertTrue("Question prompt must not be blank", q.prompt.isNotBlank())
                assertTrue("Question explanation must not be blank", q.explanation.isNotBlank())
                assertTrue("Question realWorldExample must not be blank", q.realWorldExample.isNotBlank())
                assertTrue("Question options must have at least 2 choices", q.options.size >= 2)
                assertTrue("correctIndex must be valid", q.correctIndex in 0 until q.options.size)
                assertEquals("wrongExplanations count must match options count", q.options.size, q.wrongExplanations.size)
            }
        }
    }

    @Test
    fun testZeroDsaContentRemainsInCurriculum() {
        val forbiddenTerms = listOf("binary tree", "linked list", "stack plate", "queue person", "bubble sort", "merge sort")
        for (i in 1..25) {
            val file = File(assetsCurriculumDir, "chapter_${String.format("%02d", i)}.json")
            val content = file.readText(Charsets.UTF_8).lowercase()
            for (term in forbiddenTerms) {
                assertTrue("Chapter $i must not contain forbidden DSA term '$term'", !content.contains(term))
            }
        }
    }

    @Test
    fun testChapterRegistryExposesAll25ChaptersAcross5Realms() {
        val chapters = ChapterRegistry.all25Chapters
        assertEquals("ChapterRegistry must define exactly 25 core chapters", 25, chapters.size)

        // Verify IDs 1..25
        for (i in 1..25) {
            val ch = ChapterRegistry.getChapterById(i)
            assertNotNull("Chapter $i must be resolvable by id", ch)
            assertEquals("Chapter id must be $i", i, ch?.id)
            assertEquals("Chapter must have 5 stages", 5, ch?.nodes?.size)
        }

        // Verify Boss specifications at end of each realm (chapters 5, 10, 15, 20, 25)
        val bossChapterIds = listOf(5, 10, 15, 20, 25)
        for (bossId in bossChapterIds) {
            val ch = ChapterRegistry.getChapterById(bossId)
            assertNotNull("Boss chapter $bossId must have a bossSpec", ch?.bossSpec)
            assertTrue("Boss node must be marked isBoss", ch?.nodes?.last()?.isBoss == true)
        }

        // Verify Basecamp Chapter 0
        val basecamp = ChapterRegistry.getChapterById(0)
        assertNotNull("Basecamp must exist", basecamp)
        assertEquals(0, basecamp?.id)
        assertEquals(5, basecamp?.nodes?.size)
    }

    @Test
    fun testCurriculumRepositoryLoadsRealmsAndChapters() {
        val realms = CurriculumRepository.realms
        assertEquals("Must have 5 realms", 5, realms.size)
        assertEquals(listOf(1, 2, 3, 4, 5), realms[0].chapterIds)
        assertEquals(listOf(6, 7, 8, 9, 10), realms[1].chapterIds)
        assertEquals(listOf(11, 12, 13, 14, 15), realms[2].chapterIds)
        assertEquals(listOf(16, 17, 18, 19, 20), realms[3].chapterIds)
        assertEquals(listOf(21, 22, 23, 24, 25), realms[4].chapterIds)

        // Test loading chapter 1 and chapter 25 via CurriculumRepository
        CurriculumRepository.populateFromDirectory(assetsCurriculumDir)
        val ch1 = CurriculumRepository.getChapter(1)
        assertNotNull("Chapter 1 must load", ch1)
        assertEquals("Why do we need security?", ch1?.title)

        val ch25 = CurriculumRepository.getChapter(25)
        assertNotNull("Chapter 25 must load", ch25)
        assertEquals("The future of security", ch25?.title)
    }
}
