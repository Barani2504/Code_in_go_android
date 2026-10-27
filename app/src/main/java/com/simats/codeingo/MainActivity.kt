package com.simats.codeingo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.simats.codeingo.navigation.AppNavHost
import com.simats.codeingo.ui.theme.CodeingoTheme
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.ProvideScreenMetrics

import android.view.View
import android.view.ViewTreeObserver
import com.simats.codeingo.domain.GameManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Keep splash screen up until the snapshot is ready (with a 1500ms safety timeout)
        val content: View = findViewById(android.R.id.content)
        val startTime = System.currentTimeMillis()
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    val isReady = GameManager.instance.isInitialized.value
                    val isTimedOut = (System.currentTimeMillis() - startTime) > 1500L
                    return if (isReady || isTimedOut) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        false
                    }
                }
            }
        )

        setContent {
            CodeingoTheme {
                ProvideScreenMetrics {
                    val colors = com.simats.codeingo.ui.theme.LocalDynamicThemeColors.current
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = colors.background
                    ) {
                        AppNavHost()
                    }
                }
            }
        }
    }
}