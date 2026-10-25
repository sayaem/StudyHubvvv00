package com.example.notifications

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages fine-grained user preferences for notification categories.
 * Stored in SharedPreferences to guarantee persistent zero-loss migration
 * without altering database table schemas.
 */
object NotificationPreferences {

    const val KEY_SMART_REMINDERS = "pref_smart_study_reminders"
    const val KEY_CALENDAR_REMINDERS = "pref_calendar_deadline_reminders"
    const val KEY_SYLLABUS_REMINDERS = "pref_syllabus_revision_reminders"
    const val KEY_GOAL_REMINDERS = "pref_daily_goal_reminders"
    const val KEY_CONSISTENCY_REMINDERS = "pref_consistency_streak_reminders"
    const val KEY_NOTIFICATION_SET = "pref_notification_set_content"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(HourlyNotificationDeliveryEngine.PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isSmartRemindersEnabled(context: Context): Boolean =
        getPrefs(context).getBoolean(KEY_SMART_REMINDERS, true)

    fun setSmartRemindersEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_SMART_REMINDERS, enabled).apply()
    }

    fun isCalendarRemindersEnabled(context: Context): Boolean =
        getPrefs(context).getBoolean(KEY_CALENDAR_REMINDERS, true)

    fun setCalendarRemindersEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_CALENDAR_REMINDERS, enabled).apply()
    }

    fun isSyllabusRemindersEnabled(context: Context): Boolean =
        getPrefs(context).getBoolean(KEY_SYLLABUS_REMINDERS, true)

    fun setSyllabusRemindersEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_SYLLABUS_REMINDERS, enabled).apply()
    }

    fun isGoalRemindersEnabled(context: Context): Boolean =
        getPrefs(context).getBoolean(KEY_GOAL_REMINDERS, true)

    fun setGoalRemindersEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_GOAL_REMINDERS, enabled).apply()
    }

    fun isConsistencyRemindersEnabled(context: Context): Boolean =
        getPrefs(context).getBoolean(KEY_CONSISTENCY_REMINDERS, true)

    fun setConsistencyRemindersEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_CONSISTENCY_REMINDERS, enabled).apply()
    }

    fun isNotificationSetEnabled(context: Context): Boolean =
        getPrefs(context).getBoolean(KEY_NOTIFICATION_SET, true)

    fun setNotificationSetEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_NOTIFICATION_SET, enabled).apply()
    }
}
