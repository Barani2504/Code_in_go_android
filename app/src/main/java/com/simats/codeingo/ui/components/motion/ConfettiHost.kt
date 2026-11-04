package com.simats.codeingo.ui.components.motion

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Spread
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

/**
 * Supercharged Konfetti Host for AshCrypt.
 *
 * Emits festive cryptographic party showers (Amber Gold, Phoenix Green, Electric Blue, Violet)
 * honoring all strict game-feel rules:
 * - Single active confetti system guaranteed via [EffectBudgetManager].
 * - Particle cap (~60 max) to protect 60/120fps frame rendering.
 * - Auto-suppressed in Calm Mode and Reduce-Motion.
 */
@Composable
fun ConfettiHost(
    trigger: Boolean,
    modifier: Modifier = Modifier,
    onComplete: () -> Unit = {}
) {
    if (!trigger) return
    if (isCalmModeActive()) {
        LaunchedEffect(Unit) { onComplete() }
        return
    }

    val canShow = remember { EffectBudgetManager.acquireConfetti() }
    if (!canShow) {
        LaunchedEffect(Unit) { onComplete() }
        return
    }

    DisposableEffect(Unit) {
        onDispose {
            EffectBudgetManager.releaseConfetti()
        }
    }

    val parties = remember {
        listOf(
            Party(
                speed = 10f,
                maxSpeed = 30f,
                damping = 0.9f,
                spread = Spread.ROUND,
                colors = listOf(0xFFFFC800.toInt(), 0xFF33D966.toInt(), 0xFF1CB0F6.toInt(), 0xFFA855F7.toInt()),
                emitter = Emitter(duration = 200, TimeUnit.MILLISECONDS).max(60),
                position = Position.Relative(0.5, 0.35)
            )
        )
    }

    KonfettiView(
        modifier = modifier.fillMaxSize(),
        parties = parties
    )
}

