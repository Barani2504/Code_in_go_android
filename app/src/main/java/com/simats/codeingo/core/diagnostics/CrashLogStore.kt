package com.simats.codeingo.core.diagnostics

import android.content.Context
import android.os.Build
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Lightweight, zero-dependency crash logging store for AshCrypt.
 * Captures uncaught exceptions, system diagnostics, and thread states into app-private storage.
 */
object CrashLogStore {
    private const val TAG = "CrashLogStore"
    private const val MAX_LOG_FILES = 10
    private const val CRASH_DIR = "ashcrypt_crash_logs"

    private var appContext: Context? = null

    fun initialize(context: Context) {
        appContext = context.applicationContext
        installUncaughtExceptionHandler()
    }

    private fun installUncaughtExceptionHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                recordCrash(thread, throwable)
            } catch (e: Throwable) {
                Log.e(TAG, "Failed to record uncaught exception", e)
            } finally {
                defaultHandler?.uncaughtException(thread, throwable)
            }
        }
    }

    @Synchronized
    fun recordCrash(thread: Thread, throwable: Throwable): File? {
        val context = appContext ?: return null
        return try {
            val dir = File(context.filesDir, CRASH_DIR).apply { if (!exists()) mkdirs() }
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val file = File(dir, "crash_${timestamp}_${System.currentTimeMillis()}.txt")

            val report = buildString {
                appendLine("=== ASHCRYPT UNCAUGHT CRASH REPORT ===")
                appendLine("Timestamp: ${Date()}")
                appendLine("Thread: ${thread.name} (id: ${thread.id}, priority: ${thread.priority})")
                appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL} (${Build.DEVICE})")
                appendLine("Android SDK: ${Build.VERSION.SDK_INT} (${Build.VERSION.RELEASE})")
                val rt = Runtime.getRuntime()
                val usedMemMb = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024)
                val maxMemMb = rt.maxMemory() / (1024 * 1024)
                appendLine("Memory Usage: ${usedMemMb}MB / ${maxMemMb}MB")
                appendLine("Exception: ${throwable.javaClass.name}: ${throwable.message}")
                appendLine("\n--- STACK TRACE ---")
                appendLine(throwable.stackTraceToString())

                val cause = throwable.cause
                if (cause != null) {
                    appendLine("\n--- CAUSED BY ---")
                    appendLine(cause.stackTraceToString())
                }
            }

            file.writeText(report)
            Log.e(TAG, "Crash written to: ${file.absolutePath}")
            pruneOldLogs(dir)
            file
        } catch (e: Exception) {
            Log.e(TAG, "Error writing crash report", e)
            null
        }
    }

    private fun pruneOldLogs(dir: File) {
        val files = dir.listFiles { f -> f.extension == "txt" }?.sortedByDescending { it.lastModified() } ?: return
        if (files.size > MAX_LOG_FILES) {
            files.drop(MAX_LOG_FILES).forEach { it.delete() }
        }
    }

    fun getRecentCrashLogs(): List<File> {
        val context = appContext ?: return emptyList()
        val dir = File(context.filesDir, CRASH_DIR)
        return dir.listFiles { f -> f.extension == "txt" }?.sortedByDescending { it.lastModified() } ?: emptyList()
    }
}
