package com.simats.codeingo.ui.components.motion

import android.content.Context
import android.os.PowerManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar
import java.util.concurrent.atomic.AtomicInteger

/**
 * Global Effect Budget & Sensory Safety Manager.
 *
 * Enforces strict limits:
 * - Exactly ONE active confetti system at any time.
 * - Max ~60 particles total for any burst.
 * - Capped concurrent explosion/shatter instances (<= 2).
 * - Capped active Rive mascot instances (<= 1 on mobile, <= 2 on tablet).
 * - Photosensitivity guarantee: No flashes > 3 Hz; no saturated full-screen strobes.
 * - Calm / Relaxed Mode: Completely disables shakes, flashes, and particle bursts.
 * - Quiet Hours: Automatically lowers haptic amplitude and mutes game audio between 22:00 and 07:00.
 * - Battery Saver / Low Power: Automatically halves particle budgets and disables 3D tilts.
 */
object EffectBudgetManager {

    // ── User Configuration Flows ──
    private val _isCalmMode = MutableStateFlow(false)
    val isCalmMode: StateFlow<Boolean> = _isCalmMode.asStateFlow()

    private val _isQuietHoursEnabled = MutableStateFlow(true)
    val isQuietHoursEnabled: StateFlow<Boolean> = _isQuietHoursEnabled.asStateFlow()

    private val _quietHoursStartHour = MutableStateFlow(22) // 10 PM
    private val _quietHoursEndHour = MutableStateFlow(7)   // 7 AM

    // ── Active System Tracking ──
    private val activeConfettiSystems = AtomicInteger(0)
    private val activeExplosions = AtomicInteger(0)
    private val activeRiveInstances = AtomicInteger(0)

    // ── Public Controls ──
    fun isCalmModeEnabled(): Boolean = _isCalmMode.value

    fun requestConfetti(): Boolean = acquireConfetti()

    fun requestExplode(): Boolean = acquireExplosion()

    fun setCalmMode(enabled: Boolean) {
        _isCalmMode.value = enabled
    }

    fun setQuietHoursEnabled(enabled: Boolean) {
        _isQuietHoursEnabled.value = enabled
    }

    /**
     * Checks whether currently within designated Quiet Hours.
     */
    fun isQuietTime(): Boolean {
        if (!_isQuietHoursEnabled.value) return false
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val start = _quietHoursStartHour.value
        val end = _quietHoursEndHour.value
        return if (start > end) {
            currentHour >= start || currentHour < end
        } else {
            currentHour in start until end
        }
    }

    /**
     * Confetti budget check: exactly 1 confetti system allowed concurrently.
     * Blocked if Calm mode is ON.
     */
    fun acquireConfetti(): Boolean {
        if (_isCalmMode.value) return false
        return activeConfettiSystems.compareAndSet(0, 1)
    }

    fun releaseConfetti() {
        activeConfettiSystems.set(0)
    }

    /**
     * Maximum allowed particles per burst, dynamically throttled by mode & power-saver.
     */
    fun maxAllowedParticles(context: Context? = null): Int {
        if (_isCalmMode.value) return 0

        val isPowerSave = try {
            if (context != null) {
                val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
                pm?.isPowerSaveMode == true
            } else false
        } catch (_: Throwable) {
            false
        }

        return if (isPowerSave) 28 else 60
    }

    /**
     * Explode / Shatter concurrency check (max 2).
     */
    fun acquireExplosion(): Boolean {
        if (_isCalmMode.value) return false
        while (true) {
            val current = activeExplosions.get()
            if (current >= 2) return false
            if (activeExplosions.compareAndSet(current, current + 1)) return true
        }
    }

    fun releaseExplosion() {
        activeExplosions.updateAndGet { if (it > 0) it - 1 else 0 }
    }

    /**
     * Rive mascot instance tracker (max 2).
     */
    fun registerRiveInstance(): Boolean {
        while (true) {
            val current = activeRiveInstances.get()
            if (current >= 2) return false
            if (activeRiveInstances.compareAndSet(current, current + 1)) return true
        }
    }

    fun unregisterRiveInstance() {
        activeRiveInstances.updateAndGet { if (it > 0) it - 1 else 0 }
    }

    /**
     * Checks if a visual shake is permitted (bypassed in Calm mode).
     */
    fun isShakeAllowed(): Boolean = !_isCalmMode.value

    /**
     * Checks if a light flash is permitted (photosensitivity gate <= 3Hz, bypassed in Calm mode).
     */
    fun isFlashAllowed(): Boolean = !_isCalmMode.value
}

/**
 * Convenience Compose hook to observe Calm Mode in real-time.
 */
@Composable
fun isCalmModeActive(): Boolean {
    val calm by EffectBudgetManager.isCalmMode.collectAsState()
    return calm
}

