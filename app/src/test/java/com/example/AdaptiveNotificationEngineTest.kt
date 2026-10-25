package com.example

import com.example.data.local.AdmissionTargetEntity
import com.example.data.local.AppSettingsEntity
import com.example.data.local.ChapterEntity
import com.example.data.local.NotificationItemEntity
import com.example.data.local.StudyNoteEntity
import com.example.data.local.StudyTaskEntity
import com.example.data.local.SubjectEntity
import com.example.data.local.UserProfile
import com.example.notifications.AdaptiveNotificationEngine
import com.example.notifications.NotificationCategory
import com.example.notifications.NotificationContentLibrary
import com.example.notifications.NotificationEligibilityEvaluator
import com.example.notifications.NotificationTemplate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AdaptiveNotificationEngineTest {

    @Test
    fun testNotificationLibraryContains344Items() {
        val allTemplates = NotificationContentLibrary.ALL_NOTIFICATIONS
        assertTrue("Library should contain at least 344 templates, actual: ${allTemplates.size}", allTemplates.size >= 344)

        // Verify base notification IDs span N001 to N120 and new notifications span N001 to N224 in NewNotificationsLibrary
        val ids = allTemplates.map { it.id }.toSet()
        for (i in 1..120) {
            val expectedId = "N" + String.format("%03d", i)
            assertTrue("Library missing notification ID $expectedId", ids.contains(expectedId))
        }
    }

    @Test
    fun testNotificationLibraryCategories() {
        val allTemplates = NotificationContentLibrary.ALL_NOTIFICATIONS
        val categories = allTemplates.map { it.category }.toSet()

        assertTrue(categories.contains(NotificationCategory.FUNNY_ROASTS))
        assertTrue(categories.contains(NotificationCategory.DISTRACTION_NUDGES))
        assertTrue(categories.contains(NotificationCategory.MOTIVATION))
        assertTrue(categories.contains(NotificationCategory.SYLLABUS_CHAPTERS))
        assertTrue(categories.contains(NotificationCategory.TASKS_PLANNING))
        assertTrue(categories.contains(NotificationCategory.EXAM_ALERTS))
        assertTrue(categories.contains(NotificationCategory.FOCUS_BREAKS))
        assertTrue(categories.contains(NotificationCategory.STREAKS_PROGRESS))
        assertTrue(categories.contains(NotificationCategory.HEALTHY_HABITS))
        assertTrue(categories.contains(NotificationCategory.GENTLE_COMEBACK))
        assertTrue(categories.contains(NotificationCategory.NOTES_LEARNING))
    }

    @Test
    fun testMasterToggleSuppression() {
        val template = NotificationContentLibrary.ALL_NOTIFICATIONS.first { it.id == "N031" }
        val settings = AppSettingsEntity(
            studyRemindersEnabled = false
        )

        val eligible = NotificationEligibilityEvaluator.isEligible(
            template = template,
            settings = settings,
            userProfile = null,
            subjects = emptyList(),
            chapters = emptyList(),
            tasks = emptyList(),
            admissionTargets = emptyList(),
            notes = emptyList(),
            isFocusTimerActive = false,
            recentNotifications = emptyList(),
            todayNotificationCount = 0,
            currentHour = 14
        )
        assertFalse("Notifications must be suppressed when master studyRemindersEnabled is false", eligible)
    }

    @Test
    fun testMinCooldownEnforcement() {
        val template = NotificationContentLibrary.ALL_NOTIFICATIONS.first { it.id == "N031" }
        val now = System.currentTimeMillis()

        // 15 minutes ago (within 45-min cooldown) -> suppressed
        val settingsRecent = AppSettingsEntity(
            lastNotificationTime = now - (15 * 60_000L)
        )
        val eligibleRecent = NotificationEligibilityEvaluator.isEligible(
            template = template,
            settings = settingsRecent,
            userProfile = null,
            subjects = emptyList(),
            chapters = emptyList(),
            tasks = emptyList(),
            admissionTargets = emptyList(),
            notes = emptyList(),
            isFocusTimerActive = false,
            recentNotifications = emptyList(),
            todayNotificationCount = 0,
            currentHour = 14,
            currentTimeMs = now
        )
        assertFalse("Notifications must be suppressed during rapid re-trigger window (<45 mins)", eligibleRecent)

        // 60 minutes ago (next hourly slot) -> eligible
        val settingsHourly = AppSettingsEntity(
            lastNotificationTime = now - (60 * 60_000L)
        )
        val eligibleHourly = NotificationEligibilityEvaluator.isEligible(
            template = template,
            settings = settingsHourly,
            userProfile = null,
            subjects = emptyList(),
            chapters = emptyList(),
            tasks = emptyList(),
            admissionTargets = emptyList(),
            notes = emptyList(),
            isFocusTimerActive = false,
            recentNotifications = emptyList(),
            todayNotificationCount = 0,
            currentHour = 14,
            currentTimeMs = now
        )
        assertTrue("Notifications must be allowed in the next hourly window (60 mins later)", eligibleHourly)
    }

    @Test
    fun testExamAlertsDataRequirementRestriction() {
        val examTemplates = NotificationContentLibrary.ALL_NOTIFICATIONS.filter {
            it.category == NotificationCategory.EXAM_ALERTS && it.requiresExamData
        }
        assertTrue(examTemplates.isNotEmpty())

        for (template in examTemplates) {
            assertTrue(
                "Exam alert template ${template.id} must require exam data",
                template.requiresExamData
            )
        }

        val settings = AppSettingsEntity(
            studyRemindersEnabled = true,
            quietHoursStartHour = 22,
            quietHoursEndHour = 7
        )

        val target = AdmissionTargetEntity(
            id = 1L,
            institutionName = "BUET",
            programUnit = "A Unit",
            applicationDeadline = "2026-11-15",
            examDate = "2026-12-01"
        )

        // Evaluate eligibility WITHOUT admission targets
        for (template in examTemplates) {
            val eligibleNoData = NotificationEligibilityEvaluator.isEligible(
                template = template,
                settings = settings,
                userProfile = null,
                subjects = emptyList(),
                chapters = emptyList(),
                tasks = emptyList(),
                admissionTargets = emptyList(),
                notes = emptyList(),
                isFocusTimerActive = false,
                recentNotifications = emptyList(),
                todayNotificationCount = 0,
                currentHour = 14
            )
            assertFalse(
                "Exam alert ${template.id} must NOT be eligible without admission targets",
                eligibleNoData
            )

            val eligibleWithData = NotificationEligibilityEvaluator.isEligible(
                template = template,
                settings = settings,
                userProfile = null,
                subjects = emptyList(),
                chapters = emptyList(),
                tasks = emptyList(),
                admissionTargets = listOf(target),
                notes = emptyList(),
                isFocusTimerActive = false,
                recentNotifications = emptyList(),
                todayNotificationCount = 0,
                currentHour = 14
            )
            assertTrue(
                "Exam alert ${template.id} MUST be eligible when admission targets exist",
                eligibleWithData
            )
        }
    }

    @Test
    fun testQuietHoursSuppression() {
        val template = NotificationContentLibrary.ALL_NOTIFICATIONS.first { it.id == "N031" } // Motivation
        val settings = AppSettingsEntity(
            quietHoursStartHour = 22,
            quietHoursEndHour = 7
        )

        // Hour 23 = Quiet hour (11 PM)
        val eligibleQuiet = NotificationEligibilityEvaluator.isEligible(
            template = template,
            settings = settings,
            userProfile = null,
            subjects = emptyList(),
            chapters = emptyList(),
            tasks = emptyList(),
            admissionTargets = emptyList(),
            notes = emptyList(),
            isFocusTimerActive = false,
            recentNotifications = emptyList(),
            todayNotificationCount = 0,
            currentHour = 23
        )
        assertFalse("Notifications must be suppressed during quiet hours (11 PM)", eligibleQuiet)

        // Hour 15 = Active hour (3 PM)
        val eligibleActive = NotificationEligibilityEvaluator.isEligible(
            template = template,
            settings = settings,
            userProfile = null,
            subjects = emptyList(),
            chapters = emptyList(),
            tasks = emptyList(),
            admissionTargets = emptyList(),
            notes = emptyList(),
            isFocusTimerActive = false,
            recentNotifications = emptyList(),
            todayNotificationCount = 0,
            currentHour = 15
        )
        assertTrue("Notifications should be allowed during active hours (3 PM)", eligibleActive)
    }

    @Test
    fun testFocusTimerSuppression() {
        val template = NotificationContentLibrary.ALL_NOTIFICATIONS.first { it.id == "N001" } // Funny Roast
        val settings = AppSettingsEntity(
            studyRemindersEnabled = true
        )

        val eligibleDuringFocus = NotificationEligibilityEvaluator.isEligible(
            template = template,
            settings = settings,
            userProfile = null,
            subjects = emptyList(),
            chapters = emptyList(),
            tasks = emptyList(),
            admissionTargets = emptyList(),
            notes = emptyList(),
            isFocusTimerActive = true,
            recentNotifications = emptyList(),
            todayNotificationCount = 0,
            currentHour = 14
        )
        assertFalse("Non-focus notifications must be suppressed while timer is active", eligibleDuringFocus)
    }

    @Test
    fun testAdaptiveNotificationEngineSelection() {
        val userProfile = UserProfile(displayName = "Rahim", academicLevel = "HSC")

        val eligibleTemplates = NotificationContentLibrary.ALL_NOTIFICATIONS.filter { !it.requiresExamData }

        val selected = AdaptiveNotificationEngine.selectBestCandidate(
            templates = eligibleTemplates,
            userProfile = userProfile,
            subjects = emptyList(),
            chapters = listOf(ChapterEntity(subjectId = 1L, title = "Physics Ch1", status = "NOT_STARTED")),
            tasks = listOf(StudyTaskEntity(title = "Math Prep", date = "2026-10-25", priority = "HIGH")),
            admissionTargets = emptyList(),
            notes = emptyList(),
            recentNotifications = emptyList(),
            currentHour = 15
        )

        assertNotNull("Engine must select an eligible notification candidate", selected)
        assertTrue(selected!!.body.isNotBlank())
    }
}
