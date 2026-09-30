package com.simats.duolingo.data

import androidx.compose.ui.graphics.Color
import com.simats.duolingo.R

data class PhoenixStageData(
    val id: Int,
    val name: String,
    val subtitle: String,
    val act: Int, // 1, 2, 3
    val element: String,
    val auraColor: Color,
    val quote: String,
    val drawableResId: Int,
)

val allPhoenixStages: List<PhoenixStageData> = listOf(
    PhoenixStageData(1, "Sacred Egg Awakening", "The primordial ember stirs within the egg", 1, "🔥 Ember", Color(0xFFFF9600), "Life awakens!", R.drawable.phoenix_stage_1),
    PhoenixStageData(2, "First Hatching", "Shell cracks under living celestial fire", 1, "🔥 Flame", Color(0xFFFF8700), "I break free!", R.drawable.phoenix_stage_2),
    PhoenixStageData(3, "Baby Fledgling", "Curious golden eyes greeting the dawn", 1, "✨ Spark", Color(0xFFFFD900), "Chirp! Hello world!", R.drawable.phoenix_stage_3),
    PhoenixStageData(4, "First Feathers", "Plumage gleams with vibrant crimson & gold", 1, "🪶 Plumage", Color(0xFFFF7A00), "Feeling stronger!", R.drawable.phoenix_stage_4),
    PhoenixStageData(5, "Nestbound Leap", "Stretching wings at the brink of the cliff", 1, "💨 Breeze", Color(0xFFFFC800), "Ready to leap!", R.drawable.phoenix_stage_5),
    PhoenixStageData(6, "First Flight", "Soaring gracefully above the clouds", 1, "🌪 Flight", Color(0xFFFF6D00), "I can fly!", R.drawable.phoenix_stage_6),
    PhoenixStageData(7, "Ancient Perch", "Watching over the sacred forest canopy", 2, "🌿 Nature", Color(0xFF58CC02), "Guardian watch!", R.drawable.phoenix_stage_7),
    PhoenixStageData(8, "Valley Glider", "Riding warm thermal drafts across canyons", 2, "⛰ Valley", Color(0xFFFFC000), "Onward to adventure!", R.drawable.phoenix_stage_8),
    PhoenixStageData(9, "Storm Trial", "Facing torrential hurricane winds head on", 2, "⚡️ Lightning", Color(0xFF1CB0F6), "No storm can stop me!", R.drawable.phoenix_stage_9),
    PhoenixStageData(10, "Tempest Battle", "Striking through thunderous vortex storms", 2, "⚡️ Thunder", Color(0xFF5B5EA6), "Unleash the lightning!", R.drawable.phoenix_stage_10),
    PhoenixStageData(11, "Fiery Resilience", "Inner core burns hotter than any cold", 2, "🔥 Inferno", Color(0xFFFF4B4B), "Fire burns eternal!", R.drawable.phoenix_stage_11),
    PhoenixStageData(12, "Rebirth Dawn", "Ascending purified from the sacred ashes", 2, "✨ Rebirth", Color(0xFFFF9600), "Risen anew!", R.drawable.phoenix_stage_12),
    PhoenixStageData(13, "Arcane Awakening", "Mystical violet flames encircle the wings", 3, "🔮 Arcane", Color(0xFFCE82FF), "Mystic energy flows!", R.drawable.phoenix_stage_13),
    PhoenixStageData(14, "Radiant Crest", "Golden coronal crest bursts with sunlight", 3, "☀️ Radiance", Color(0xFFFFD000), "Shining bright!", R.drawable.phoenix_stage_14),
    PhoenixStageData(15, "Elemental Master", "Wielding the 4 fundamental primal forces", 3, "🌀 Elements", Color(0xFF00CD9C), "Elements align!", R.drawable.phoenix_stage_15),
    PhoenixStageData(16, "Golden Aegis", "Armored plumage forged in solar furnaces", 3, "🛡 Solar Aegis", Color(0xFFFFC000), "Invincible shield!", R.drawable.phoenix_stage_16),
    PhoenixStageData(17, "Solar Ascent", "Diving straight into the core of the Sun", 3, "☀️ Solar Core", Color(0xFFFF6400), "Touching the sun!", R.drawable.phoenix_stage_17),
    PhoenixStageData(18, "Eternal Cosmic Phoenix", "Transcendent celestial deity of the stars", 3, "🌌 Cosmos", Color(0xFFA855F7), "Eternal and boundless!", R.drawable.phoenix_stage_18),
)
