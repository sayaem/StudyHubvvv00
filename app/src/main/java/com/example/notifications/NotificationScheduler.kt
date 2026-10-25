package com.example.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

object NotificationScheduler {

    const val PROACTIVE_NOTIFICATION_ID = 8888

    /**
     * Calculates the trigger time for the next upcoming hourly notification window.
     * Aligns to the top of the next hour (:00) and advances past quiet hours.
     */
    fun calculateNextHourlyTriggerTimeMs(
        nowMs: Long = System.currentTimeMillis(),
        quietHoursStart: Int = 22,
        quietHoursEnd: Int = 7
    ): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = nowMs
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.HOUR_OF_DAY, 1)
        }

        // Avoid firing immediately if less than 60 seconds from the top of the hour
        if (cal.timeInMillis - nowMs < 60_000L) {
            cal.add(Calendar.HOUR_OF_DAY, 1)
        }

        // If next hour lands in quiet hours, fast-forward to the end of quiet hours (e.g., 7:00 AM)
        while (NotificationEligibilityEvaluator.isQuietHour(
                cal.get(Calendar.HOUR_OF_DAY),
                quietHoursStart,
                quietHoursEnd
            )
        ) {
            cal.add(Calendar.HOUR_OF_DAY, 1)
        }

        return cal.timeInMillis
    }

    /**
     * Schedules the next recurring hourly notification check using AlarmManager setAndAllowWhileIdle.
     * Prevents duplicate alarms by reusing PROACTIVE_NOTIFICATION_ID with FLAG_UPDATE_CURRENT.
     */
    fun scheduleNextHourlyCheck(
        context: Context,
        customDelayMs: Long? = null,
        quietHoursStart: Int = 22,
        quietHoursEnd: Int = 7
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val triggerTimeMs = customDelayMs?.let { System.currentTimeMillis() + it }
            ?: calculateNextHourlyTriggerTimeMs(
                nowMs = System.currentTimeMillis(),
                quietHoursStart = quietHoursStart,
                quietHoursEnd = quietHoursEnd
            )

        val intent = Intent(context, StudyHubNotificationReceiver::class.java).apply {
            action = StudyHubNotificationReceiver.ACTION_TRIGGER_NOTIFICATION
            putExtra(StudyHubNotificationReceiver.EXTRA_NOTIFICATION_ID, PROACTIVE_NOTIFICATION_ID)
        }

        val pendingFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            PROACTIVE_NOTIFICATION_ID,
            intent,
            pendingFlags
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMs,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMs,
                    pendingIntent
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Backwards compatibility alias for existing callers.
     */
    fun scheduleNextAdaptiveOpportunity(
        context: Context,
        delayMinutes: Long = 60L
    ) {
        scheduleNextHourlyCheck(context, customDelayMs = delayMinutes * 60_000L)
    }

    fun cancelPendingNotifications(context: Context) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
            val intent = Intent(context, StudyHubNotificationReceiver::class.java).apply {
                action = StudyHubNotificationReceiver.ACTION_TRIGGER_NOTIFICATION
            }
            val pendingFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                PROACTIVE_NOTIFICATION_ID,
                intent,
                pendingFlags
            )
            alarmManager.cancel(pendingIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
