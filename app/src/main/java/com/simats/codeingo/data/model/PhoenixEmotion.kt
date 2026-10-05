package com.simats.codeingo.data.model

import androidx.compose.ui.graphics.Color

/**
 * Phoenix emotion category — mirrors iOS PhoenixEmotionCategory enum.
 */
enum class PhoenixEmotionCategory(
    val displayName: String,
    val icon: String,           // Material icon name (no leading "Icons.")
) {
    ALL      ("All",              "sparkles"),
    HAPPY    ("Happy & Proud",    "face.smiling.fill"),
    LEARNING ("Learning & Focus", "lightbulb"),
    DRAMA    ("Drama & Tears",    "cloud.rain"),
    FIERCE   ("Fierce & Battle",  "whatshot"),   // flame.fill → whatshot
    LOVE     ("Love & Charm",     "favorite"),   // heart.fill → favorite
}

/**
 * Single Phoenix emotion entry — exact parity with iOS PhoenixEmotion struct.
 *
 * @param id                 0-based emotion index (note: 1 is absent in iOS catalog).
 * @param key                Internal string key for DataStore persistence.
 * @param title              Display title ("Happy Phoenix").
 * @param feeling            Short feeling label.
 * @param quote              Inspirational quote shown in picker.
 * @param emoji              Emoji representing the emotion.
 * @param category           One of [PhoenixEmotionCategory].
 * @param glowR/G/B          RGB components (0-255) of the aura glow color.
 * @param triggerDescription Human-readable rule description shown in Settings.
 */
data class PhoenixEmotion(
    val id: Int,
    val key: String,
    val title: String,
    val feeling: String,
    val quote: String,
    val emoji: String,
    val category: PhoenixEmotionCategory,
    val glowR: Double,
    val glowG: Double,
    val glowB: Double,
    val triggerDescription: String,
) {
    /** Resource name for in-app transparent mascot image.  e.g. "phoenix_emotion_0" */
    val assetName: String get() = "phoenix_emotion_$id"

    /** The Android resource name (for use with painterResource / ImageBitmap). */
    val drawableResName: String get() = assetName

    /** Aura glow color derived from glowR/G/B components (0-255). */
    val auraColor: Color get() = Color(
        red   = (glowR / 255.0).toFloat(),
        green = (glowG / 255.0).toFloat(),
        blue  = (glowB / 255.0).toFloat(),
    )
}

// ══════════════════════════════════════════════════════════════════
// FULL CATALOG — 28 Phoenix mascot emotions (ID 0, 2-28; 1 absent)
// Mirrors iOS allPhoenixEmotions exactly — keys, quotes, categories,
// RGB glow values all preserved for full cross-platform parity.
// ══════════════════════════════════════════════════════════════════
val allPhoenixEmotions: List<PhoenixEmotion> = listOf(
    PhoenixEmotion(0, "neutral",     "Calm Phoenix",         "Neutral",      "Every great journey begins with a single breath of calm.",    "😐", PhoenixEmotionCategory.ALL,      255.0, 200.0, 100.0, "Default neutral state on app launch"),
    PhoenixEmotion(2, "celebrating", "Celebrating Phoenix",  "Excited",      "Yes! I knew you could do it! Let's celebrate every win!",    "🎉", PhoenixEmotionCategory.HAPPY,    255.0, 215.0,   0.0, "Triggered on lesson completion"),
    PhoenixEmotion(3, "proud",       "Proud Phoenix",        "Proud",        "Stand tall! Your hard work is paying off beautifully.",       "💪", PhoenixEmotionCategory.HAPPY,    255.0, 140.0,   0.0, "Triggered on XP milestone"),
    PhoenixEmotion(4, "curious",     "Curious Phoenix",      "Curious",      "Hmm… what if we looked at this from a different angle?",     "🤔", PhoenixEmotionCategory.LEARNING,  80.0, 180.0, 255.0, "Triggered when exploring visualizer"),
    PhoenixEmotion(5, "focused",     "Focused Phoenix",      "Focused",      "Eyes on the code. Distractions are for later.",               "🎯", PhoenixEmotionCategory.LEARNING, 100.0, 100.0, 255.0, "Triggered on long practice session"),
    PhoenixEmotion(6, "starry",      "Starry Prodigy",       "Amazed",       "The stars aligned just for this moment. You're legendary!",  "🤩", PhoenixEmotionCategory.HAPPY,    150.0, 100.0, 255.0, "7+ day streak or perfect score"),
    PhoenixEmotion(7, "smiling",     "Smiling Phoenix",      "Happy",        "Every bug you fix makes the world a little more beautiful.", "😊", PhoenixEmotionCategory.HAPPY,    255.0, 180.0,  80.0, "After a successful quiz"),
    PhoenixEmotion(8, "laughing",    "Laughing Phoenix",     "Playful",      "Ha! Even errors have their own kind of humour.",             "😄", PhoenixEmotionCategory.HAPPY,    255.0, 200.0,  50.0, "After a humorous wrong answer"),
    PhoenixEmotion(9, "winking",     "Winking Phoenix",      "Cheeky",       "Psst… I have a little secret trick for this one 😉",         "😉", PhoenixEmotionCategory.HAPPY,    255.0, 165.0,  60.0, "When hint is revealed"),
    PhoenixEmotion(10,"inspired",    "Inspired Phoenix",     "Inspired",     "Right now, genius is flowing through your fingertips!",      "✨", PhoenixEmotionCategory.LEARNING, 200.0, 150.0, 255.0, "After breakthrough moment"),
    PhoenixEmotion(11,"thinking",    "Thinking Phoenix",     "Thoughtful",   "Deep thoughts, shallow bugs — let's solve this together.",   "💭", PhoenixEmotionCategory.LEARNING, 120.0, 120.0, 220.0, "During a hard problem"),
    PhoenixEmotion(12,"reading",     "Reading Phoenix",      "Studious",     "Knowledge absorbed today becomes power wielded tomorrow.",   "📖", PhoenixEmotionCategory.LEARNING,  80.0, 160.0, 200.0, "Opening docs or theory codex"),
    PhoenixEmotion(13,"eureka",      "Eureka Spark",         "Brilliant",    "⚡ That lightbulb moment just lit up the entire room!",      "💡", PhoenixEmotionCategory.LEARNING, 255.0, 240.0,  60.0, "5x combo correct answers"),
    PhoenixEmotion(14,"sleepy",      "Sleepy Phoenix",       "Tired",        "Even coders need rest. Sleep encodes memories too 🌙",       "😴", PhoenixEmotionCategory.DRAMA,   130.0, 130.0, 200.0, "Late night practice session"),
    PhoenixEmotion(15,"worried",     "Worried Ember",        "Anxious",      "I believe in you more than you believe in yourself right now.","🥺", PhoenixEmotionCategory.DRAMA, 255.0, 160.0,  80.0, "5 PM–9 PM and no practice today"),
    PhoenixEmotion(16,"sad",         "Sad Phoenix",          "Sad",          "It's okay to feel this way. Tomorrow is a fresh compile.",   "😢", PhoenixEmotionCategory.DRAMA,  100.0, 150.0, 255.0, "Streak broken"),
    PhoenixEmotion(17,"heartbroken", "Heartbroken Cry",      "Devastated",   "Zero hearts… I felt that too. We\'ll restore them together.","😭", PhoenixEmotionCategory.DRAMA,  200.0,  80.0, 120.0, "Zero hearts remaining"),
    PhoenixEmotion(18,"pouting",     "Pouting Grudge",       "Grumpy",       "You promised to practice today. The code is waiting…",       "😒", PhoenixEmotionCategory.DRAMA,  180.0, 130.0,  60.0, "Daytime and no practice yet"),
    PhoenixEmotion(19,"relieved",    "Relieved Sigh",        "Stressed",     "One heart left. High stakes, but you\'ve got this.",         "😮‍💨", PhoenixEmotionCategory.DRAMA,200.0, 200.0, 100.0, "1 heart remaining"),
    PhoenixEmotion(20,"confused",    "Confused Phoenix",     "Puzzled",      "Wait… that shouldn't have compiled. Let\'s trace it again.", "😕", PhoenixEmotionCategory.DRAMA,  150.0, 150.0, 150.0, "Multiple wrong answers in a row"),
    PhoenixEmotion(21,"grateful",    "Grateful Tears",       "Touched",      "You came back. That means everything to me. Truly. 🥹",      "🥹", PhoenixEmotionCategory.LOVE,   200.0, 220.0, 255.0, "Hearts fully refilled"),
    PhoenixEmotion(22,"love",        "Love Phoenix",         "Adoring",      "Coding with passion is the highest form of love. 💕",        "💕", PhoenixEmotionCategory.LOVE,   255.0, 130.0, 180.0, "After a personal milestone"),
    PhoenixEmotion(23,"furious",     "Furious Warning",      "Blazing",      "🔥 It's 9 PM and your streak is on the line. MOVE. NOW.",    "💢", PhoenixEmotionCategory.FIERCE, 255.0,  50.0,  30.0, "After 9 PM with no practice"),
    PhoenixEmotion(24,"fierce",      "Fierce Phoenix",       "Warrior",      "No mercy in this arena. Focus. Execute. Win.",               "⚔️", PhoenixEmotionCategory.FIERCE, 220.0,  80.0,  40.0, "Boss battle initiated"),
    PhoenixEmotion(25,"determined",  "Determined Phoenix",   "Resolute",     "Setbacks are just level bosses. You always pass the level.", "🔥", PhoenixEmotionCategory.FIERCE, 255.0, 100.0,  20.0, "After failing a level once"),
    PhoenixEmotion(26,"crowned",     "Crown Champion",       "Royal",        "👑 Number one. Because you refused to quit.",                "👑", PhoenixEmotionCategory.HAPPY,   255.0, 210.0,  40.0, "Rank #1 on leaderboard"),
    PhoenixEmotion(27,"angelic",     "Angelic Phoenix",      "Pure",         "Perfect score. Flawless execution. Absolute coding divinity.","😇", PhoenixEmotionCategory.HAPPY,  220.0, 220.0, 255.0, "100% quiz accuracy"),
    PhoenixEmotion(28,"battle",      "Battle Standard",      "War-ready",    "🚩 Boss encounter detected. Engage maximum strategy.",       "🚩", PhoenixEmotionCategory.FIERCE, 255.0,  30.0,  30.0, "Active boss battle"),
)

/** Convenience lookup by emotion id. Falls back to emotion 0 (Calm Phoenix) if missing. */
fun phoenixEmotion(id: Int): PhoenixEmotion =
    allPhoenixEmotions.firstOrNull { it.id == id } ?: allPhoenixEmotions.first()

/** Emotions filtered by category (ALL returns everything). */
fun PhoenixEmotionCategory.filtered(): List<PhoenixEmotion> =
    if (this == PhoenixEmotionCategory.ALL) allPhoenixEmotions
    else allPhoenixEmotions.filter { it.category == this }
