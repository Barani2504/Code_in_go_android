package com.simats.codeingo.data.repository

import android.content.Context
import com.simats.codeingo.data.model.CnsChapter
import com.simats.codeingo.data.model.CnsRealm
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream
import java.util.concurrent.ConcurrentHashMap

/**
 * 📚 CurriculumRepository — Central Repository for the 25-Chapter CNS Curriculum.
 *
 * Provides safe, cached access to syllabus-complete chapters, lesson cards,
 * interactive questions, and visualizer mappings.
 */
object CurriculumRepository {

    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val chapterCache = ConcurrentHashMap<Int, CnsChapter>()

    val realms: List<CnsRealm> = listOf(
        CnsRealm(
            id = 1,
            title = "Secrets 101",
            syllabusUnit = "Unit I: Classical Ciphers & Policies",
            emoji = "🔥",
            bossName = "The Cipher Breaker",
            arenaName = "Cipher Sanctum",
            chapterIds = (1..5).toList()
        ),
        CnsRealm(
            id = 2,
            title = "Symmetric Forge",
            syllabusUnit = "Unit II: Symmetric Encryption & Math",
            emoji = "⚙️",
            bossName = "The Block Overlord",
            arenaName = "Block Cipher Forge",
            chapterIds = (6..10).toList()
        ),
        CnsRealm(
            id = 3,
            title = "Public-Key Citadel",
            syllabusUnit = "Unit III: Asymmetric Cryptography & Primes",
            emoji = "🔑",
            bossName = "The Quantum Cryptanalyst",
            arenaName = "Key Exchange Citadel",
            chapterIds = (11..15).toList()
        ),
        CnsRealm(
            id = 4,
            title = "Trust & Proof",
            syllabusUnit = "Unit IV: Integrity, Hashes & Signatures",
            emoji = "🛡️",
            bossName = "The Collision Beast",
            arenaName = "Integrity Foundry",
            chapterIds = (16..20).toList()
        ),
        CnsRealm(
            id = 5,
            title = "Real-World Defense",
            syllabusUnit = "Unit V: Network Security & Emerging Threats",
            emoji = "⚡",
            bossName = "The Cyber Titan",
            arenaName = "Cyber Bastion",
            chapterIds = (21..25).toList()
        )
    )

    fun getRealm(realmId: Int): CnsRealm? = realms.firstOrNull { it.id == realmId }

    fun getRealmForChapter(chapterId: Int): CnsRealm? = realms.firstOrNull { chapterId in it.chapterIds }

    fun getChapter(id: Int, context: Context? = null): CnsChapter? {
        chapterCache[id]?.let { return it }

        if (context != null) {
            loadChapterFromContext(context, id)?.let {
                chapterCache[id] = it
                return it
            }
        }

        // Fallback for JVM unit tests without Android Context
        val file = File("app/src/main/assets/curriculum/chapter_${String.format("%02d", id)}.json")
        if (file.exists()) {
            return try {
                val jsonString = file.readText(Charsets.UTF_8)
                val chapter = jsonParser.decodeFromString<CnsChapter>(jsonString)
                chapterCache[id] = chapter
                chapter
            } catch (e: Exception) {
                null
            }
        }
        return null
    }

    fun getAllChapters(context: Context? = null): List<CnsChapter> {
        val list = mutableListOf<CnsChapter>()
        for (i in 1..25) {
            getChapter(i, context)?.let { list.add(it) }
        }
        return list
    }

    fun getChaptersForRealm(realmId: Int, context: Context? = null): List<CnsChapter> {
        val realm = getRealm(realmId) ?: return emptyList()
        return realm.chapterIds.mapNotNull { getChapter(it, context) }
    }

    fun preloadAll(context: Context) {
        for (i in 1..25) {
            if (!chapterCache.containsKey(i)) {
                loadChapterFromContext(context, i)?.let { chapterCache[i] = it }
            }
        }
    }

    fun populateFromDirectory(dir: File): Int {
        var count = 0
        for (i in 1..25) {
            val file = File(dir, "chapter_${String.format("%02d", i)}.json")
            if (file.exists()) {
                try {
                    val chapter = jsonParser.decodeFromString<CnsChapter>(file.readText(Charsets.UTF_8))
                    chapterCache[i] = chapter
                    count++
                } catch (_: Exception) {}
            }
        }
        return count
    }

    private fun loadChapterFromContext(context: Context, id: Int): CnsChapter? {
        val fileName = "curriculum/chapter_${String.format("%02d", id)}.json"
        return try {
            val inputStream: InputStream = context.assets.open(fileName)
            val jsonString = inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            jsonParser.decodeFromString<CnsChapter>(jsonString)
        } catch (e: Exception) {
            null
        }
    }
}
