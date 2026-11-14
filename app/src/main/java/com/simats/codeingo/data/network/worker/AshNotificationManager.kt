package com.simats.codeingo.data.network.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.simats.codeingo.domain.StreakManager

/**
 * 🔔 AshCrypt Notification Manager
 *
 * Implements Phase 5.5:
 * - Creates 3 specialized channels: Streak Protection, Reforge Due, and League Ending
 * - Strictly respects Quiet Mode and user preferences
 * - Zero-guilt copy, zero manipulative dark patterns
 */
object AshNotificationManager {

    const val CHANNEL_STREAK = "ashcrypt_streak_channel"
    const val CHANNEL_REFORGE = "ashcrypt_reforge_channel"
    const val CHANNEL_LEAGUE = "ashcrypt_league_channel"

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val streakChannel = NotificationChannel(
                CHANNEL_STREAK,
                "Streak Protection",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Respectful daily practice reminders."
            }

            val reforgeChannel = NotificationChannel(
                CHANNEL_REFORGE,
                "Reforge (Spaced Repetition)",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Alerts when concepts are due for spaced review."
            }

            val leagueChannel = NotificationChannel(
                CHANNEL_LEAGUE,
                "Weekly League Results",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Weekly promotion and league recap updates."
            }

            notificationManager.createNotificationChannels(listOf(streakChannel, reforgeChannel, leagueChannel))
        }
    }

    fun sendStreakReminder(context: Context, streakDays: Int) {
        val streakManager = StreakManager.instance
        if (streakManager.isQuietModeEnabled(context)) return

        val builder = NotificationCompat.Builder(context, CHANNEL_STREAK)
            .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
            .setContentTitle("Your Cipher Sanctuary Awaits 🛡️")
            .setContentText("Complete 1 quick node today to advance your cryptographic intuition.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(101, builder.build())
        } catch (_: SecurityException) {
            // Handled when POST_NOTIFICATIONS is not yet granted
        }
    }
}

