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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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