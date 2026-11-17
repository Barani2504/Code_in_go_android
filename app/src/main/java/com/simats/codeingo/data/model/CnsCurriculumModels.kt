package com.simats.codeingo.data.model

import kotlinx.serialization.Serializable

/**
 * 🎓 CnsCurriculumModels — Syllabus-Complete CNS 25-Chapter Curriculum Models
 * Mapping Anna University CS8792 Units I–V & Stallings' Cryptography & Network Security.
 */

@Serializable
data class CnsLessonCard(
    val cardIndex: Int = 1,
    val title: String = "",
    val analogy: String = "",
    val coreConcept: String = "",
    val example: String = ""
)

@Serializable
data class CnsQuestion(
    val id: String = "",
    val type: String = "MULTIPLE_CHOICE",
    val prompt: String = "",
    val options: List<String> = emptyList(),
    val correctIndex: Int = 0,
    val explanation: String = "",
    val wrongExplanations: List<String> = emptyList(),
    val realWorldExample: String = ""
)

@Serializable
data class CnsChapter(
    val id: Int,
    val number: Int,
    val realmId: Int,
    val realmTitle: String,
    val title: String,
    val syllabusUnit: String,
    val covers: String,
    val visualizerId: String,
    val visualizerTitle: String,
    val prerequisites: String = "None",
    val estimatedMinutes: Int = 5,
    val learningGoals: List<String> = emptyList(),
    val lessonCards: List<CnsLessonCard> = emptyList(),
    val questions: List<CnsQuestion> = emptyList(),
    val ashRecap: String = ""
)

data class CnsRealm(
    val id: Int,
    val title: String,
    val syllabusUnit: String,
    val emoji: String,
    val bossName: String,
    val arenaName: String,
    val chapterIds: List<Int>
)
