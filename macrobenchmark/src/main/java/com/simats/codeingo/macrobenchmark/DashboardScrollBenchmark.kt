package com.simats.codeingo.macrobenchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 📊 DashboardScrollBenchmark — Measures frame timing, 90th/99th percentile frame duration,
 * and jank percentage during continuous scrolling on MainDashboardScreen.
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
class DashboardScrollBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun scrollDashboardJourney() = benchmarkRule.measureRepeated(
        packageName = "com.simats.codeingo",
        metrics = listOf(FrameTimingMetric()),
        compilationMode = CompilationMode.DEFAULT,
        iterations = 5,
        startupMode = StartupMode.WARM
    ) {
        pressHome()
        startActivityAndWait()

        // Wait for dashboard or dismiss splash/onboarding if needed
        device.waitForIdle()

        // Locate scrollable container or fling downwards
        val scrollable = device.findObject(By.scrollable(true))
        if (scrollable != null) {
            scrollable.setGestureMargin(device.displayWidth / 5)
            scrollable.fling(Direction.DOWN)
            device.waitForIdle()
            scrollable.fling(Direction.UP)
            device.waitForIdle()
        } else {
            // Fallback screen swipe
            device.swipe(
                device.displayWidth / 2,
                (device.displayHeight * 0.75).toInt(),
                device.displayWidth / 2,
                (device.displayHeight * 0.25).toInt(),
                12
            )
            device.waitForIdle()
        }
    }
}
