package com.simats.codeingo.macrobenchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.By
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 🎯 QuizFlowBenchmark — Measures frame timing, transition frame rate,
 * and input response latency during lesson launch and interactive assessment.
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
class QuizFlowBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun openAndInteractWithQuiz() = benchmarkRule.measureRepeated(
        packageName = "com.simats.codeingo",
        metrics = listOf(FrameTimingMetric()),
        compilationMode = CompilationMode.DEFAULT,
        iterations = 5,
        startupMode = StartupMode.WARM
    ) {
        pressHome()
        startActivityAndWait()
        device.waitForIdle()

        // Locate any clickable lesson node or start lesson action
        val lessonNode = device.findObject(By.clickable(true))
        lessonNode?.click()
        device.waitForIdle()

        // Interact with option card if in quiz
        val optionCard = device.findObject(By.clickable(true))
        optionCard?.click()
        device.waitForIdle()
    }
}
