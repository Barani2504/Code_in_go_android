package com.simats.duolingo.core.sound

import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView

/**
 * Game Juice & Haptic feedback system:
 * Provides sensory tactile responses for correct, wrong, combos, and drag snaps.
 */
class JuiceFeedback(private val view: View) {

    fun onCorrectAnswer(combo: Int = 1) {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
    }

    fun onWrongAnswer() {
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
    }

    fun onSnap() {
        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
    }

    fun onEvolveCelebration() {
        view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
    }
}

@Composable
fun rememberJuiceFeedback(): JuiceFeedback {
    val view = LocalView.current
    return androidx.compose.runtime.remember(view) {
        JuiceFeedback(view)
    }
}
