package com.simats.codeingo.domain

import android.content.Context
import android.content.SharedPreferences
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import java.util.UUID

/**
 * 🔊 PhoenixVoiceGuide — Ash's Video-Game Voice Companion (Text-to-Speech)
 *
 * Gives Ash a friendly, encouraging guide voice like a video-game companion,
 * narrating user progress across chapters, victories, failures, and shop upgrades.
 * Respects user mute preference and safely no-ops in headless/test environments.
 */
class PhoenixVoiceGuide private constructor() {

    companion object {
        val instance: PhoenixVoiceGuide by lazy { PhoenixVoiceGuide() }
        private const val PREFS_NAME = "ash_voice_guide_prefs"
        private const val KEY_VOICE_ENABLED = "voice_enabled"
    }

    private var tts: TextToSpeech? = null
    private var isReady: Boolean = false
    private var prefs: SharedPreferences? = null
    private var pendingLine: String? = null

    private val _isVoiceEnabled = MutableStateFlow(true)
    val isVoiceEnabled: StateFlow<Boolean> = _isVoiceEnabled.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _lastSpokenLine = MutableStateFlow<String?>(null)
    val lastSpokenLine: StateFlow<String?> = _lastSpokenLine.asStateFlow()

    fun initialize(context: Context) {
        val appContext = context.applicationContext ?: return
        prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _isVoiceEnabled.value = prefs?.getBoolean(KEY_VOICE_ENABLED, true) ?: true

        if (tts != null) return
        runCatching {
            tts = TextToSpeech(appContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    val engine = tts
                    runCatching {
                        engine?.language = Locale.US
                        engine?.setPitch(1.10f)
                        engine?.setSpeechRate(1.03f)
                        engine?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                            override fun onStart(utteranceId: String?) {
                                _isSpeaking.value = true
                            }
                            override fun onDone(utteranceId: String?) {
                                _isSpeaking.value = false
                            }
                            @Deprecated("Deprecated in Java")
                            override fun onError(utteranceId: String?) {
                                _isSpeaking.value = false
                            }
                        })
                    }
                    isReady = true
                    pendingLine?.let { line ->
                        pendingLine = null
                        speakInternal(line)
                    }
                }
            }
        }
    }

    fun setVoiceEnabled(enabled: Boolean) {
        _isVoiceEnabled.value = enabled
        prefs?.edit()?.putBoolean(KEY_VOICE_ENABLED, enabled)?.apply()
        if (!enabled) {
            stop()
        } else {
            speak("Voice guide activated! I'll guide you through every cipher, Defender.")
        }
    }

    fun toggleVoice(): Boolean {
        val next = !_isVoiceEnabled.value
        setVoiceEnabled(next)
        return next
    }

    fun speak(text: String) {
        val clean = text.trim()
        if (clean.isEmpty()) return
        _lastSpokenLine.value = clean
        if (!_isVoiceEnabled.value) return
        if (!isReady) {
            pendingLine = clean
            return
        }
        speakInternal(clean)
    }

    private fun speakInternal(text: String) {
        runCatching {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, UUID.randomUUID().toString())
        }
    }

    fun stop() {
        pendingLine = null
        _isSpeaking.value = false
        runCatching { tts?.stop() }
    }

    // ── Contextual Video-Game Guide Lines ───────────────────────────────────

    fun speakChapterWelcome(chapterId: Int, chapterTitle: String, stageNumber: Int) {
        val shortTitle = chapterTitle.substringBefore("—").trim()
        speak("Welcome to Chapter $chapterId, $shortTitle! Let's conquer stage $stageNumber together.")
    }

    fun speakStageVictory(chapterId: Int, xpEarned: Int) {
        val lines = listOf(
            "Awesome victory! You cleared the stage and earned $xpEarned XP!",
            "Mission accomplished! Plus $xpEarned XP. Ready to fly to the next node!",
            "Great job, Defender! Chapter $chapterId defense complete. Keep the streak alive!"
        )
        speak(lines[(chapterId + xpEarned) % lines.size])
    }

    fun speakStageFailed(heartsLeft: Int) {
        if (heartsLeft <= 0) {
            speak("Out of hearts, Defender! Visit the Gem Shop to refill and strike right back.")
        } else {
            speak("Don't give up! You still have $heartsLeft hearts left. Check the hint and try again!")
        }
    }

    fun speakChapterComplete(chapterId: Int, nextChapterTitle: String?) {
        val next = nextChapterTitle?.substringBefore("—")?.trim()
        if (next != null) {
            speak("Chapter $chapterId mastered! Flying onward to $next!")
        } else {
            speak("Incredible! Chapter $chapterId mastered! You are a true cyber defender!")
        }
    }

    fun speakShopEquip(itemName: String) {
        speak("Looking sharp! Equipped $itemName. Let's take this style to the chapters!")
    }

    fun speakShopPurchase(itemName: String) {
        speak("Unlocked $itemName! Ready for action, Defender!")
    }

    fun speakEncouragement(chapterId: Int, stageNumber: Int) {
        val lines = listOf(
            "Hey Defender! Tap the glowing node to start Chapter $chapterId, stage $stageNumber.",
            "I'm Ash, your security guide! Complete nodes to evolve my Phoenix form and earn diamonds!",
            "Tip: Check the Visualizer tab anytime to play with live ciphers and hash engines!"
        )
        speak(lines[(chapterId + stageNumber) % lines.size])
    }
}
