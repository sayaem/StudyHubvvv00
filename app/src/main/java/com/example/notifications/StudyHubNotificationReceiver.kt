package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.local.AppDatabase
import com.example.data.local.AppSettingsEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StudyHubNotificationReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_TRIGGER_NOTIFICATION = "com.example.studyhub.TRIGGER_NOTIFICATION"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return

        // 1. Recover schedule on device boot or app update without sending bursts of missed notifications
        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == "android.intent.action.QUICKBOOT_POWERON" ||
            action == "com.htc.intent.action.QUICKBOOT_POWERON"
        ) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getDatabase(context)
                    val settings = db.settingsDao().getSettingsSync() ?: AppSettingsEntity()
                    if (settings.studyRemindersEnabled) {
                        NotificationScheduler.scheduleNextHourlyCheck(
                            context = context,
                            quietHoursStart = settings.quietHoursStartHour,
                            quietHoursEnd = settings.quietHoursEndHour
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    pendingResult.finish()
                }
            }
            return
        }

        if (action != ACTION_TRIGGER_NOTIFICATION) return

        // 2. Process hourly notification check
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(context)
                val settings = db.settingsDao().getSettingsSync() ?: AppSettingsEntity()

                if (settings.studyRemindersEnabled) {
                    HourlyNotificationDeliveryEngine.executeHourlyDelivery(context)

                    // Schedule the next hourly check
                    NotificationScheduler.scheduleNextHourlyCheck(
                        context = context,
                        quietHoursStart = settings.quietHoursStartHour,
                        quietHoursEnd = settings.quietHoursEndHour
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
