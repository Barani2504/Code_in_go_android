package com.simats.codeingo.ui.visualizer.scaffold

/**
 * 🎬 VisualizerBeat — The Four-Beat Learning Architecture:
 * 1. STORY (5-second hook & real-life picture)
 * 2. WATCH (Ash auto-plays or steps through demonstration, <= 5 steps)
 * 3. TRY (Learner interacts with one control and observes "What changed?")
 * 4. CHECK (One question testing intuition with XP reward)
 */
enum class VisualizerBeat(val title: String, val stepNumber: Int) {
    STORY("Story", 1),
    WATCH("Watch", 2),
    TRY("Try", 3),
    CHECK("Check", 4)
}

data class VisualizerStory(
    val title: String,
    val oneSentenceHook: String,
    val realLifeAnalogy: String,
    val conceptName: String,
    val iconEmoji: String
)

data class WatchStep(
    val stepIndex: Int,
    val narration: String, // strictly <= 15 words
    val whatChanged: String,
    val detailNotes: String = ""
)

data class CheckQuestion(
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val xpReward: Int = 20
)

data class LostHelp(
    val simpleHeadline: String,
    val simpleAnalogy: String,
    val takeawayBullet: String
)

data class VisualizerSpec(
    val id: String,
    val title: String,
    val realmNumber: Int,
    val realmTitle: String,
    val story: VisualizerStory,
    val watchSteps: List<WatchStep>,
    val checkQuestion: CheckQuestion,
    val lostHelp: LostHelp,
    val oneSentencePromise: String = ""
)
