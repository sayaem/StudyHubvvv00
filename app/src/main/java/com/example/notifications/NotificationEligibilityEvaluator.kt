package com.example.notifications

import com.example.data.local.AdmissionTargetEntity
import com.example.data.local.AppSettingsEntity
import com.example.data.local.ChapterEntity
import com.example.data.local.NotificationItemEntity
import com.example.data.local.StudyNoteEntity
import com.example.data.local.StudyTaskEntity
import com.example.data.local.SubjectEntity
import com.example.data.local.UserProfile
import java.util.Calendar

object NotificationEligibilityEvaluator {

    fun isEligible(
        template: NotificationTemplate,
        settings: AppSettingsEntity,
        userProfile: UserProfile?,
        subjects: List<SubjectEntity>,
        chapters: List<ChapterEntity>,
        tasks: List<StudyTaskEntity>,
        admissionTargets: List<AdmissionTargetEntity>,
        notes: List<StudyNoteEntity>,
        isFocusTimerActive: Boolean,
        recentNotifications: List<NotificationItemEntity>,
        todayNotificationCount: Int,
        currentHour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
        currentTimeMs: Long = System.currentTimeMillis()
    ): Boolean {
        // 1. Master global study reminders switch (Part 2 rule)
        if (!settings.studyRemindersEnabled) {
            return false
        }

        // 2. Quiet hours check (10:00 PM to 7:00 AM local time default)
        if (isQuietHour(currentHour, settings.quietHoursStartHour, settings.quietHoursEndHour)) {
            return false
        }

        // 3. Suppress during active focus session or concentration (Part 3E safeguard)
        if (isFocusTimerActive) {
            return false
        }

        // 4. Hourly Cooldown Check (45-minute cooldown to protect hourly slots and prevent rapid consecutive re-triggers)
        val isUrgentDeadline = template.category == NotificationCategory.EXAM_ALERTS ||
                template.category == NotificationCategory.ADMISSION_READINESS
        
        if (settings.lastNotificationTime > 0 && !isUrgentDeadline) {
            val minCooldownMs = 45 * 60_000L // 45 minutes
            val elapsedSinceLast = currentTimeMs - settings.lastNotificationTime
            if (elapsedSinceLast < minCooldownMs) {
                return false
            }
        }

        // 5. Category Tone Rotation: Avoid repeating exact same category consecutively within rapid re-triggers (<30 mins)
        val lastNotif = recentNotifications.firstOrNull()
        if (lastNotif != null && lastNotif.category == template.category.name) {
            val timeSinceLastMs = currentTimeMs - lastNotif.createdAt
            if (timeSinceLastMs < 30 * 60_000L) { // Within 30 mins
                return false
            }
        }

        // 6. Data prerequisites check
        if (template.requiresExamData && admissionTargets.none { !it.isArchived }) {
            return false
        }
        if (template.requiresTasks && tasks.none { it.status == "PENDING" }) {
            return false
        }
        if (template.requiresChapters && chapters.none { it.status != "COMPLETED" }) {
            return false
        }
        if (template.requiresNotes && notes.isEmpty()) {
            return false
        }

        // 7. Repeat avoidance: check if title was shown in recent notifications within the last 12 hours
        val cutoff12HoursMs = currentTimeMs - (12 * 3600_000L)
        val recentTitles = recentNotifications.filter { it.createdAt >= cutoff12HoursMs }.take(15).map { it.title }
        if (recentTitles.contains(template.title)) {
            return false
        }

        return true
    }

    fun isQuietHour(currentHour: Int, startHour: Int, endHour: Int): Boolean {
        if (startHour == endHour) return false
        return if (startHour > endHour) {
            currentHour >= startHour || currentHour < endHour
        } else {
            currentHour in startHour until endHour
        }
    }
}
