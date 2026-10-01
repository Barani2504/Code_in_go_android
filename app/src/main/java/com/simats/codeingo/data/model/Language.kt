package com.simats.codeingo.data.model

import java.util.UUID

data class Language(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val nativeName: String,
    val code: String,
    val flagEmoji: String,
    val learnersCount: String
) {
    companion object {
        val defaultLanguages: List<Language> = listOf(
            Language(name = "Python", nativeName = "Python", code = "python", flagEmoji = "🐍", learnersCount = "38.5M learners"),
            Language(name = "Java", nativeName = "Java", code = "java", flagEmoji = "☕️", learnersCount = "35.2M learners"),
            Language(name = "C++", nativeName = "C++", code = "cpp", flagEmoji = "⚙️", learnersCount = "25.4M learners"),
            Language(name = "JavaScript", nativeName = "JavaScript", code = "js", flagEmoji = "📜", learnersCount = "42.2M learners"),
            Language(name = "Swift", nativeName = "Swift", code = "swift", flagEmoji = "🦅", learnersCount = "19.5M learners"),
            Language(name = "Go", nativeName = "Go", code = "go", flagEmoji = "🐹", learnersCount = "13.8M learners"),
            Language(name = "Ruby", nativeName = "Ruby", code = "ruby", flagEmoji = "💎", learnersCount = "10.4M learners"),
            Language(name = "Kotlin", nativeName = "Kotlin", code = "kotlin", flagEmoji = "📱", learnersCount = "15.1M learners"),
            Language(name = "Rust", nativeName = "Rust", code = "rust", flagEmoji = "🦀", learnersCount = "8.7M learners"),
            Language(name = "C#", nativeName = "C#", code = "csharp", flagEmoji = "🪟", learnersCount = "22.3M learners"),
            Language(name = "PHP", nativeName = "PHP", code = "php", flagEmoji = "🐘", learnersCount = "18.2M learners"),
            Language(name = "HTML/CSS", nativeName = "HTML/CSS", code = "html", flagEmoji = "🌐", learnersCount = "50.1M learners")
        )
    }
}
