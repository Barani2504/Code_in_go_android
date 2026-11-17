package com.simats.codeingo.ui.motion

import android.view.HapticFeedbackConstants
import android.view.View

/**
 * Unified Haptic Feedback Engine for AshCrypt (Phase 1).
 * Respects system accessibility and LocalReduceMotion settings.
 */
object AshHaptics {

    fun tap(view: View, reduceMotion: Boolean = false) {
        if (reduceMotion) return
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
    }

    fun success(view: View, reduceMotion: Boolean = false) {
        if (reduceMotion) return
        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
    }

    fun error(view: View, reduceMotion: Boolean = false) {
        if (reduceMotion) return
        view.performHapticFeedback(HapticFeedbackConstants.REJECT)
    }

    fun selection(view: View, reduceMotion: Boolean = false) {
        if (reduceMotion) return
        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
    }

    fun heavyImpact(view: View, reduceMotion: Boolean = false) {
        if (reduceMotion) return
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
    }
}
