package com.example.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity

class StudyHubNotificationManager(private val context: Context) {

    companion object {
        const val CHANNEL_STUDY = "studyhub_study"
        const val CHANNEL_ADMISSION = "studyhub_admission"
        const val CHANNEL_DAILY_GOAL = "studyhub_daily_goal"
        const val CHANNEL_CALENDAR = "studyhub_calendar"
        const val EXTRA_TARGET_ROUTE = "extra_target_route"
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createChannels()
    }

    private fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    CHANNEL_STUDY,
                    "Study Reminders & Roasts",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Reminders, roasts, and motivation for study sessions"
                    enableVibration(true)
                },
                NotificationChannel(
                    CHANNEL_ADMISSION,
                    "Admission Deadlines & Exam Alerts",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Milestone alerts for application deadlines and exam dates"
                    enableVibration(true)
                },
                NotificationChannel(
                    CHANNEL_DAILY_GOAL,
                    "Daily Study Goals & Progress",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Updates on daily study progress and goal achievements"
                },
                NotificationChannel(
                    CHANNEL_CALENDAR,
                    "Calendar Plans & Schedule",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Alerts for upcoming scheduled study plans and missed sessions"
                    enableVibration(true)
                }
            )
            channels.forEach { notificationManager.createNotificationChannel(it) }
        }
    }

    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun postNotification(
        id: Int,
        channelId: String,
        title: String,
        message: String,
        actionRoute: String? = null
    ): Boolean {
        if (!hasNotificationPermission()) {
            return false
        }

        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                if (!actionRoute.isNullOrBlank()) {
                    putExtra(EXTRA_TARGET_ROUTE, actionRoute)
                }
            }

            val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                id,
                intent,
                pendingIntentFlags
            )

            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            notificationManager.notify(id, builder.build())
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}
