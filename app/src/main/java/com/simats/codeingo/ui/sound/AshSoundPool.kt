package com.simats.codeingo.ui.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import com.simats.codeingo.R
import com.simats.codeingo.ui.components.motion.EffectBudgetManager
import com.simats.codeingo.ui.theme.MotionController

/**
 * High-performance SoundPool implementation for AshCrypt (Phase 1).
 * Replaces CDMA ToneGenerator placeholder beeps with authentic sound effects.
 */
object AshSoundPool {
    private const val TAG = "AshSoundPool"
    private var soundPool: SoundPool? = null

    private var soundTap: Int = 0
    private var soundCorrect: Int = 0
    private var soundWrong: Int = 0
    private var soundCombo: Int = 0
    private var soundLevelUp: Int = 0
    private var soundChest: Int = 0
    private var soundWhoosh: Int = 0

    private var masterVolume: Float = 1.0f
    private var isInitialized: Boolean = false

    fun initialize(context: Context) {
        if (isInitialized) return
        try {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(8)
                .setAudioAttributes(audioAttributes)
                .build()

            soundTap = soundPool?.load(context, R.raw.tap, 1) ?: 0
            soundCorrect = soundPool?.load(context, R.raw.correct, 1) ?: 0
            soundWrong = soundPool?.load(context, R.raw.wrong, 1) ?: 0
            soundCombo = soundPool?.load(context, R.raw.combo, 1) ?: 0
            soundLevelUp = soundPool?.load(context, R.raw.level_up, 1) ?: 0
            soundChest = soundPool?.load(context, R.raw.chest, 1) ?: 0
            soundWhoosh = soundPool?.load(context, R.raw.whoosh, 1) ?: 0

            isInitialized = true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize SoundPool: ${e.message}")
        }
    }

    fun setMasterVolume(volume: Float) {
        masterVolume = volume.coerceIn(0.0f, 1.0f)
    }

    private fun canPlaySound(): Boolean {
        if (!MotionController.soundEnabled.value) return false
        if (EffectBudgetManager.isQuietTime()) return false
        return isInitialized && masterVolume > 0.01f
    }

    private fun play(soundId: Int, rate: Float = 1.0f, volumeScale: Float = 1.0f) {
        if (!canPlaySound() || soundId == 0) return
        val vol = (masterVolume * volumeScale).coerceIn(0f, 1f)
        soundPool?.play(soundId, vol, vol, 1, 0, rate.coerceIn(0.5f, 2.0f))
    }

    fun playTap() = play(soundTap, volumeScale = 0.8f)
    fun playCorrect() = play(soundCorrect, volumeScale = 1.0f)
    fun playWrong() = play(soundWrong, volumeScale = 0.9f)
    fun playCombo(multiplier: Int = 1) {
        val rate = 1.0f + (multiplier.coerceIn(1, 8) - 1) * 0.05f
        play(soundCombo, rate = rate, volumeScale = 1.0f)
    }
    fun playLevelUp() = play(soundLevelUp, volumeScale = 1.0f)
    fun playChest() = play(soundChest, volumeScale = 1.0f)
    fun playWhoosh() = play(soundWhoosh, volumeScale = 0.8f)

    fun release() {
        soundPool?.release()
        soundPool = null
        isInitialized = false
    }
}
