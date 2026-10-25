package com.example

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.ActiveFocusSessionEntity
import com.example.data.local.AdmissionTargetEntity
import com.example.data.local.AppDatabase
import com.example.data.local.AppSettingsEntity
import com.example.data.local.ChapterEntity
import com.example.data.local.FocusGamificationEntity
import com.example.data.local.StudySessionEntity
import com.example.data.local.StudyTaskEntity
import com.example.data.local.SubjectEntity
import com.example.data.local.UserProfile
import com.example.notifications.CandidateSourceType
import com.example.notifications.DeliveryResult
import com.example.notifications.HourlyNotificationDeliveryEngine
import com.example.notifications.NotificationActionRoute
import com.example.notifications.NotificationCategory
import com.example.notifications.NotificationContentLibrary
import com.example.notifications.NotificationPreferences
import com.example.notifications.NotificationPriority
import com.example.notifications.NotificationScheduler
import com.example.notifications.NotificationTemplate
import com.example.notifications.SmartNotificationDecisionEngine
import com.example.notifications.StudyHubNotificationReceiver
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SmartNotificationIntelligenceTest {

    private lateinit var context: Context
    private lateinit var db: AppDatabase

    @Before
    fun setUp() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        org.robolectric.Shadows.shadowOf(app).grantPermissions(android.Manifest.permission.POST_NOTIFICATIONS)
        context = app
        db = AppDatabase.getDatabase(context)
        HourlyNotificationDeliveryEngine.clearAllHistoryForTesting(context)
        runBlocking(kotlinx.coroutines.Dispatchers.IO) {
            db.clearAllTables()
            db.settingsDao().upsertSettings(
                AppSettingsEntity(
                    id = 1,
                    studyRemindersEnabled = true,
                    quietHoursStartHour = 22,
                    quietHoursEndHour = 7,
                    lastNotificationTime = 0L
                )
            )
        }
    }

    @After
    fun tearDown() {
        HourlyNotificationDeliveryEngine.clearAllHistoryForTesting(context)
        runBlocking(kotlinx.coroutines.Dispatchers.IO) {
            db.clearAllTables()
        }
    }

    @Test
    fun testHighPriorityExamAlertSelectedOverLowPriorityCandidates() = runBlocking {
        // Seed admission target with exam in 3 days
        val todayCal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 25, 10, 0, 0)
        }
        val target = AdmissionTargetEntity(
            id = 101L,
            institutionName = "BUET",
            programUnit = "Engineering",
            applicationDeadline = "2026-11-15",
            examDate = "2026-10-28" // 3 days away!
        )
        val user = UserProfile(id = 1L, dailyGoalMinutes = 240)

        val candidate = SmartNotificationDecisionEngine.evaluateCandidateForSlot(
            context = context,
            userProfile = user,
            subjects = emptyList(),
            chapters = emptyList(),
            tasks = emptyList(),
            sessions = emptyList(),
            admissionTargets = listOf(target),
            notes = emptyList(),
            gamification = null,
            activeSession = null,
            settings = AppSettingsEntity(studyRemindersEnabled = true),
            recentNotifications = emptyList(),
            deliveredDeduplicationKeys = emptySet(),
            deliveredSetIdsToday = emptySet(),
            currentHour = 10,
            currentTimeMs = todayCal.timeInMillis
        )

        assertNotNull(candidate)
        assertEquals(NotificationPriority.HIGH, candidate!!.priority)
        assertEquals(CandidateSourceType.CALENDAR_PLAN, candidate.sourceType)
        assertTrue(candidate.title.contains("BUET"))
        assertEquals("admission", candidate.actionRoute)
    }

    @Test
    fun testDailyStudyGoalRemindersAndSuppressionWhenCompleted() = runBlocking {
        val todayCal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 25, 17, 0, 0) // 5 PM
        }
        val user = UserProfile(id = 1L, dailyGoalMinutes = 120) // 2 hour goal

        // Case 1: In progress (60 mins logged out of 120 mins)
        val session1 = StudySessionEntity(
            id = 1L,
            startedAt = todayCal.timeInMillis - 3600_000L,
            endedAt = todayCal.timeInMillis,
            elapsedSeconds = 60 * 60,
            status = "COMPLETED"
        )

        val candidateProgress = SmartNotificationDecisionEngine.evaluateCandidateForSlot(
            context = context,
            userProfile = user,
            subjects = emptyList(),
            chapters = emptyList(),
            tasks = emptyList(),
            sessions = listOf(session1),
            admissionTargets = emptyList(),
            notes = emptyList(),
            gamification = null,
            activeSession = null,
            settings = AppSettingsEntity(studyRemindersEnabled = true),
            recentNotifications = emptyList(),
            deliveredDeduplicationKeys = emptySet(),
            deliveredSetIdsToday = emptySet(),
            currentHour = 17,
            currentTimeMs = todayCal.timeInMillis
        )

        assertNotNull(candidateProgress)
        assertEquals(CandidateSourceType.STUDY_GOAL, candidateProgress!!.sourceType)
        assertTrue("Message should mention 60 minutes remaining or logged", candidateProgress.message.contains("60"))

        // Case 2: Goal Completed (130 mins logged out of 120 mins)
        val session2 = StudySessionEntity(
            id = 2L,
            startedAt = todayCal.timeInMillis - 7800_000L,
            endedAt = todayCal.timeInMillis,
            elapsedSeconds = 130 * 60,
            status = "COMPLETED"
        )

        val candidateCompleted = SmartNotificationDecisionEngine.evaluateCandidateForSlot(
            context = context,
            userProfile = user,
            subjects = emptyList(),
            chapters = emptyList(),
            tasks = emptyList(),
            sessions = listOf(session2),
            admissionTargets = emptyList(),
            notes = emptyList(),
            gamification = null,
            activeSession = null,
            settings = AppSettingsEntity(studyRemindersEnabled = true),
            recentNotifications = emptyList(),
            deliveredDeduplicationKeys = emptySet(),
            deliveredSetIdsToday = emptySet(),
            currentHour = 17,
            currentTimeMs = todayCal.timeInMillis
        )

        assertNotNull(candidateCompleted)
        assertTrue(candidateCompleted!!.title.contains("Completed") || candidateCompleted.title.contains("Achieved"))

        // Case 3: Once goal celebration was delivered, it is suppressed
        val candidateSuppressed = SmartNotificationDecisionEngine.evaluateCandidateForSlot(
            context = context,
            userProfile = user,
            subjects = emptyList(),
            chapters = emptyList(),
            tasks = emptyList(),
            sessions = listOf(session2),
            admissionTargets = emptyList(),
            notes = emptyList(),
            gamification = null,
            activeSession = null,
            settings = AppSettingsEntity(studyRemindersEnabled = true),
            recentNotifications = emptyList(),
            deliveredDeduplicationKeys = setOf(candidateCompleted.deduplicationKey),
            deliveredSetIdsToday = emptySet(),
            currentHour = 17,
            currentTimeMs = todayCal.timeInMillis
        )

        // Does not repeatedly celebrate or claim incomplete
        assertTrue(candidateSuppressed == null || candidateSuppressed.sourceType != CandidateSourceType.STUDY_GOAL)
    }

    @Test
    fun testUpcomingCalendarTaskTakesPrecedence() = runBlocking {
        // Today is 2026-10-25 14:00. Task scheduled for 14:30
        val todayCal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 25, 14, 0, 0)
        }
        val task = StudyTaskEntity(
            id = 42L,
            title = "Organic Chemistry Reactions",
            date = "2026-10-25",
            time = "14:30",
            durationMinutes = 45,
            status = "PENDING"
        )

        val candidate = SmartNotificationDecisionEngine.evaluateCandidateForSlot(
            context = context,
            userProfile = null,
            subjects = emptyList(),
            chapters = emptyList(),
            tasks = listOf(task),
            sessions = emptyList(),
            admissionTargets = emptyList(),
            notes = emptyList(),
            gamification = null,
            activeSession = null,
            settings = AppSettingsEntity(studyRemindersEnabled = true),
            recentNotifications = emptyList(),
            deliveredDeduplicationKeys = emptySet(),
            deliveredSetIdsToday = emptySet(),
            currentHour = 14,
            currentTimeMs = todayCal.timeInMillis
        )

        assertNotNull(candidate)
        assertEquals(NotificationPriority.HIGH, candidate!!.priority)
        assertEquals(CandidateSourceType.CALENDAR_PLAN, candidate.sourceType)
        assertTrue(candidate.title.contains("Organic Chemistry"))
        assertEquals("calendar", candidate.actionRoute)
    }

    @Test
    fun testRevisionDueReminderGenerated() = runBlocking {
        val todayCal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 25, 11, 0, 0)
        }
        val revisionTask = StudyTaskEntity(
            id = 55L,
            title = "Optics Chapter 3",
            date = "2026-10-25",
            status = "PENDING",
            planType = "REVISION"
        )

        val candidate = SmartNotificationDecisionEngine.evaluateCandidateForSlot(
            context = context,
            userProfile = null,
            subjects = emptyList(),
            chapters = emptyList(),
            tasks = listOf(revisionTask),
            sessions = emptyList(),
            admissionTargets = emptyList(),
            notes = emptyList(),
            gamification = null,
            activeSession = null,
            settings = AppSettingsEntity(studyRemindersEnabled = true),
            recentNotifications = emptyList(),
            deliveredDeduplicationKeys = emptySet(),
            deliveredSetIdsToday = emptySet(),
            currentHour = 11,
            currentTimeMs = todayCal.timeInMillis
        )

        assertNotNull(candidate)
        assertEquals(CandidateSourceType.SYLLABUS_REVISION, candidate!!.sourceType)
        assertTrue(candidate.title.contains("Revision Due"))
        assertEquals("syllabus", candidate.actionRoute)
    }

    @Test
    fun testStreakProtectionReminderGeneratedWhenUnfinished() = runBlocking {
        val todayCal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 25, 18, 0, 0) // 6 PM
        }
        val gamification = FocusGamificationEntity(
            id = 1L,
            currentStreakDays = 5
        )

        val candidate = SmartNotificationDecisionEngine.evaluateCandidateForSlot(
            context = context,
            userProfile = null,
            subjects = emptyList(),
            chapters = emptyList(),
            tasks = emptyList(),
            sessions = emptyList(), // No study sessions today
            admissionTargets = emptyList(),
            notes = emptyList(),
            gamification = gamification,
            activeSession = null,
            settings = AppSettingsEntity(studyRemindersEnabled = true),
            recentNotifications = emptyList(),
            deliveredDeduplicationKeys = emptySet(),
            deliveredSetIdsToday = emptySet(),
            currentHour = 18,
            currentTimeMs = todayCal.timeInMillis
        )

        assertNotNull(candidate)
        assertEquals(CandidateSourceType.CONSISTENCY_INSIGHT, candidate!!.sourceType)
        assertTrue(candidate.title.contains("5-Day Streak"))
        assertEquals("focus", candidate.actionRoute)
    }

    @Test
    fun testSmartFallbackToNotificationSetWhenNoSmartCandidateQualifies() = runBlocking {
        val todayCal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 25, 10, 0, 0)
        }
        // No tasks, no admission targets, no user profile -> no smart candidate qualifies
        val candidate = SmartNotificationDecisionEngine.evaluateCandidateForSlot(
            context = context,
            userProfile = null,
            subjects = emptyList(),
            chapters = emptyList(),
            tasks = emptyList(),
            sessions = emptyList(),
            admissionTargets = emptyList(),
            notes = emptyList(),
            gamification = null,
            activeSession = null,
            settings = AppSettingsEntity(studyRemindersEnabled = true),
            recentNotifications = emptyList(),
            deliveredDeduplicationKeys = emptySet(),
            deliveredSetIdsToday = emptySet(),
            currentHour = 10,
            currentTimeMs = todayCal.timeInMillis
        )

        assertNotNull("Should fall back to Notification Set item", candidate)
        assertEquals(CandidateSourceType.NOTIFICATION_SET, candidate!!.sourceType)
        assertTrue(candidate.id.startsWith("N"))
    }

    @Test
    fun testConcurrentHourlyDeliveryExecutionSafety() = runBlocking {
        val testCal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 25, 12, 0, 0)
        }
        val slotKey = HourlyNotificationDeliveryEngine.getSlotKey(testCal.timeInMillis)

        // Seed an admission target
        db.admissionDao().insertTarget(
            AdmissionTargetEntity(
                institutionName = "Dhaka Medical College",
                applicationDeadline = "2026-11-01",
                examDate = "2026-10-27"
            )
        )

        // Launch 5 concurrent calls for the same hourly slot
        val results = coroutineScope {
            (1..5).map {
                async {
                    HourlyNotificationDeliveryEngine.executeHourlyDelivery(
                        context = context,
                        timeMs = testCal.timeInMillis,
                        forceSlotKey = slotKey
                    )
                }
            }.awaitAll()
        }

        val successCount = results.count { it is DeliveryResult.Success }
        val skippedCount = results.count { it is DeliveryResult.Skipped && it.reason == "ALREADY_DELIVERED_THIS_HOUR" }

        assertEquals("Exactly one concurrent delivery must succeed", 1, successCount)
        assertEquals("Remaining 4 concurrent attempts must be safely skipped", 4, skippedCount)
    }

    @Test
    fun testUserCategoryDisablingSuppressesCandidates() = runBlocking {
        val todayCal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 25, 10, 0, 0)
        }
        val target = AdmissionTargetEntity(
            id = 200L,
            institutionName = "CUET",
            applicationDeadline = "2026-11-20",
            examDate = "2026-10-27" // 2 days away (High priority)
        )

        // Disable Calendar & Deadline category
        NotificationPreferences.setCalendarRemindersEnabled(context, false)

        val candidate = SmartNotificationDecisionEngine.evaluateCandidateForSlot(
            context = context,
            userProfile = null,
            subjects = emptyList(),
            chapters = emptyList(),
            tasks = emptyList(),
            sessions = emptyList(),
            admissionTargets = listOf(target),
            notes = emptyList(),
            gamification = null,
            activeSession = null,
            settings = AppSettingsEntity(studyRemindersEnabled = true),
            recentNotifications = emptyList(),
            deliveredDeduplicationKeys = emptySet(),
            deliveredSetIdsToday = emptySet(),
            currentHour = 10,
            currentTimeMs = todayCal.timeInMillis
        )

        // Exam candidate must be suppressed since calendar category is disabled
        assertTrue(candidate == null || candidate.sourceType != CandidateSourceType.CALENDAR_PLAN)

        // Re-enable for subsequent tests
        NotificationPreferences.setCalendarRemindersEnabled(context, true)
    }

    @Test
    fun testActiveFocusTimerSuppression() = runBlocking {
        val todayCal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 25, 14, 0, 0)
        }
        val activeRunningSession = ActiveFocusSessionEntity(
            sessionState = "FOCUS_RUNNING",
            timeRemainingSeconds = 600
        )

        val candidate = SmartNotificationDecisionEngine.evaluateCandidateForSlot(
            context = context,
            userProfile = null,
            subjects = emptyList(),
            chapters = emptyList(),
            tasks = emptyList(),
            sessions = emptyList(),
            admissionTargets = emptyList(),
            notes = emptyList(),
            gamification = null,
            activeSession = activeRunningSession,
            settings = AppSettingsEntity(studyRemindersEnabled = true),
            recentNotifications = emptyList(),
            deliveredDeduplicationKeys = emptySet(),
            deliveredSetIdsToday = emptySet(),
            currentHour = 14,
            currentTimeMs = todayCal.timeInMillis
        )

        // Must be null while timer is active
        assertEquals(null, candidate)
    }

    @Test
    fun testRebootReconcilesScheduleWithoutBurst() {
        val receiver = StudyHubNotificationReceiver()
        val bootIntent = Intent(Intent.ACTION_BOOT_COMPLETED)

        receiver.onReceive(context, bootIntent)

        // Verify no notification delivery was recorded by boot
        val prefs = context.getSharedPreferences(HourlyNotificationDeliveryEngine.PREFS_NAME, Context.MODE_PRIVATE)
        val lastSlot = prefs.getString(HourlyNotificationDeliveryEngine.KEY_LAST_DELIVERED_SLOT, null)
        assertEquals(null, lastSlot)
    }
}
