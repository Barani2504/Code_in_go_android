package com.simats.codeingo.core.diagnostics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.ui.theme.LocalDynamicThemeColors
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Universal safe coroutine launch helper with automatic exception handling and crash recording.
 */
fun safeCoroutineScope(tag: String = "AshCryptSafeScope"): CoroutineScope {
    val handler = CoroutineExceptionHandler { _, throwable ->
        CrashLogStore.recordCrash(Thread.currentThread(), throwable)
    }
    return CoroutineScope(SupervisorJob() + Dispatchers.Default + handler)
}

/**
 * Extension for safely launching async tasks without risking process termination.
 */
fun CoroutineScope.safeLaunch(
    onError: ((Throwable) -> Unit)? = null,
    block: suspend CoroutineScope.() -> Unit
): Job {
    val handler = CoroutineExceptionHandler { _, throwable ->
        CrashLogStore.recordCrash(Thread.currentThread(), throwable)
        onError?.invoke(throwable)
    }
    return launch(handler) {
        block()
    }
}

/**
 * Reusable ErrorBoundary to prevent screen-level fatal crashes from state mutations or recomposition.
 */
@Composable
fun ScreenErrorBoundary(
    screenName: String = "This section",
    onRetry: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    if (hasError) {
        val themeColors = LocalDynamicThemeColors.current
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(themeColors.background)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(themeColors.cardBackground, RoundedCornerShape(20.dp))
                    .padding(24.dp)
            ) {
                Text(
                    text = "🐦",
                    fontSize = 48.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Ash ran into a bump in $screenName",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = themeColors.textPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage ?: "Something unexpected happened. Your progress is safe.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = themeColors.textSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                if (onRetry != null) {
                    Button(
                        onClick = {
                            hasError = false
                            errorMessage = null
                            onRetry()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF7A3D),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Let's Try Again")
                    }
                }
            }
        }
    } else {
        content()
    }
}
