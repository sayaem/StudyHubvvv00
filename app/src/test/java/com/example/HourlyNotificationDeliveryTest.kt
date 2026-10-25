package com.example

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AdmissionTargetEntity
import com.example.data.local.AppDatabase
import com.example.data.local.AppSettingsEntity
import com.example.data.local.ChapterEntity
import com.example.data.local.StudyTaskEntity
import com.example.data.local.SubjectEntity
import com.example.notifications.DeliveryResult
import com.example.notifications.HourlyNotificationDeliveryEngine
import com.example.notifications.NotificationActionRoute
import com.example.notifications.NotificationCategory
import com.example.notifications.NotificationContentLibrary
import com.example.notifications.NotificationScheduler
import com.example.notifications.NotificationTemplate
import com.example.notifications.StudyHubNotificationReceiver
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
class HourlyNotificationDeliveryTest {

    private lateinit var context: Context
    private lateinit var db: AppDatabase

    @Before
    fun setUp() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        org.robolectric.Shadows.shadowOf(app).grantPermissions(android.Manifest.permission.POST_NOTIFICATIONS)
        context = app
        db = AppDatabase.getDatabase(context)
        HourlyNotificationDeliveryEngine.clearAllHistoryForTesting(context)
        kotlinx.coroutines.runBlocking(kotlinx.coroutines.Dispatchers.IO) {
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
            // Seed a sample subject and uncompleted chapter
            val subId = db.subjectDao().insertSubject(SubjectEntity(name = "Physics", colorHex = "#0796A3"))
            db.chapterDao().insertChapter(ChapterEntity(subjectId = subId, title = "Thermodynamics", status = "NOT_STARTED"))
            db.taskDao().insertTask(StudyTaskEntity(title = "Practice Problems", date = "2026-10-25", priority = "HIGH"))
        }
    }

    @After
    fun tearDown() {
        HourlyNotificationDeliveryEngine.clearAllHistoryForTesting(context)
        kotlinx.coroutines.runBlocking(kotlinx.coroutines.Dispatchers.IO) {
            db.clearAllTables()
        }
    }

    @Test
    fun testHourlyTriggerCalculationAlignsToTopNextHour() {
        val cal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 25, 9, 15, 0)
        }
        val triggerTime = NotificationScheduler.calculateNextHourlyTriggerTimeMs(
            nowMs = cal.timeInMillis,
            quietHoursStart = 22,
            quietHoursEnd = 7
        )

        val expected = Calendar.getInstance().apply {
            timeInMillis = triggerTime
        }
        assertEquals(10, expected.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, expected.get(Calendar.MINUTE))
        assertEquals(0, expected.get(Calendar.SECOND))
    }

    @Test
    fun testHourlyTriggerCalculationAdvancesPastQuietHours() {
        // At 10:15 PM (22:15) quiet hours (22 to 7) are in effect
        val cal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 25, 22, 15, 0)
        }
        val triggerTime = NotificationScheduler.calculateNextHourlyTriggerTimeMs(
            nowMs = cal.timeInMillis,
            quietHoursStart = 22,
            quietHoursEnd = 7
        )

        val expected = Calendar.getInstance().apply {
            timeInMillis = triggerTime
        }
        // Must fast-forward past 10 PM - 7 AM to 7:00 AM next day
        assertEquals(7, expected.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, expected.get(Calendar.MINUTE))
    }

    @Test
    fun testHourlySlotProtectionPreventsDuplicateInSameHour() = runBlocking {
        val testCal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 25, 10, 15, 0)
        }
        val slotKey = HourlyNotificationDeliveryEngine.getSlotKey(testCal.timeInMillis)

        // First delivery attempt for hour 10
        val result1 = HourlyNotificationDeliveryEngine.executeHourlyDelivery(
            context = context,
            timeMs = testCal.timeInMillis,
            forceSlotKey = slotKey
        )
        assertTrue("First delivery in hour 10 slot must succeed", result1 is DeliveryResult.Success)

        // Second delivery attempt during same hour 10 (e.g. app opened or re-trigger)
        val result2 = HourlyNotificationDeliveryEngine.executeHourlyDelivery(
            context = context,
            timeMs = testCal.timeInMillis + (10 * 60_000L), // 10 minutes later in same hour
            forceSlotKey = slotKey
        )
        assertTrue("Second delivery in same slot must be skipped", result2 is DeliveryResult.Skipped)
        assertEquals("ALREADY_DELIVERED_THIS_HOUR", (result2 as DeliveryResult.Skipped).reason)
    }

    @Test
    fun testNextHourSlotAllowed() = runBlocking {
        val hour10Cal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 25, 10, 5, 0)
        }
        val hour11Cal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 25, 11, 5, 0)
        }

        val result1 = HourlyNotificationDeliveryEngine.executeHourlyDelivery(
            context = context,
            timeMs = hour10Cal.timeInMillis,
            forceSlotKey = HourlyNotificationDeliveryEngine.getSlotKey(hour10Cal.timeInMillis)
        )
        assertTrue("Hour 10 delivery must succeed", result1 is DeliveryResult.Success)

        val result2 = HourlyNotificationDeliveryEngine.executeHourlyDelivery(
            context = context,
            timeMs = hour11Cal.timeInMillis,
            forceSlotKey = HourlyNotificationDeliveryEngine.getSlotKey(hour11Cal.timeInMillis)
        )
        assertTrue("Hour 11 delivery must not be blocked by hour 10", result2 is DeliveryResult.Success)
    }

    @Test
    fun testNotificationSetSameDayDeduplication() = runBlocking {
        val testNotificationSet = listOf(
            NotificationTemplate(
                id = "SET_A",
                category = NotificationCategory.MOTIVATION,
                title = "Item A Title",
                body = "Item A Body",
                actionRoute = NotificationActionRoute.FOCUS
            ),
            NotificationTemplate(
                id = "SET_B",
                category = NotificationCategory.FUNNY_ROASTS,
                title = "Item B Title",
                body = "Item B Body",
                actionRoute = NotificationActionRoute.FOCUS
            )
        )

        val hour10Cal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 25, 10, 0, 0)
        }
        val hour11Cal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 25, 11, 0, 0)
        }

        // Hour 10 delivery
        val result1 = HourlyNotificationDeliveryEngine.executeHourlyDelivery(
            context = context,
            timeMs = hour10Cal.timeInMillis,
            customNotificationSet = testNotificationSet
        )
        assertTrue(result1 is DeliveryResult.Success)
        val firstDeliveredId = (result1 as DeliveryResult.Success).template.id

        // Hour 11 delivery
        val result2 = HourlyNotificationDeliveryEngine.executeHourlyDelivery(
            context = context,
            timeMs = hour11Cal.timeInMillis,
            customNotificationSet = testNotificationSet
        )
        assertTrue(result2 is DeliveryResult.Success)
        val secondDeliveredId = (result2 as DeliveryResult.Success).template.id

        // Must deliver a DIFFERENT item from the set on the same day
        assertTrue(firstDeliveredId != secondDeliveredId)
        val deliveredToday = HourlyNotificationDeliveryEngine.getDeliveredSetIdsToday(context, hour11Cal.timeInMillis)
        assertEquals(2, deliveredToday.size)
        assertTrue(deliveredToday.contains(firstDeliveredId))
        assertTrue(deliveredToday.contains(secondDeliveredId))
    }

    @Test
    fun testDateRolloverResetsUsedNotificationSetIds() = runBlocking {
        val testNotificationSet = listOf(
            NotificationTemplate(
                id = "DAILY_EXCLUSIVE",
                category = NotificationCategory.MOTIVATION,
                title = "Daily Exclusive Item",
                body = "Exclusive body text",
                actionRoute = NotificationActionRoute.FOCUS
            )
        )

        val day1Cal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 25, 14, 0, 0)
        }
        val day2Cal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 26, 14, 0, 0)
        }

        // Deliver on Day 1
        val resultDay1 = HourlyNotificationDeliveryEngine.executeHourlyDelivery(
            context = context,
            timeMs = day1Cal.timeInMillis,
            customNotificationSet = testNotificationSet
        )
        assertTrue(resultDay1 is DeliveryResult.Success)
        assertEquals("DAILY_EXCLUSIVE", (resultDay1 as DeliveryResult.Success).template.id)

        // On Day 1 hour 15, DAILY_EXCLUSIVE is now used
        val day1Hour15Cal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 25, 15, 0, 0)
        }
        val usedDay1 = HourlyNotificationDeliveryEngine.getDeliveredSetIdsToday(context, day1Hour15Cal.timeInMillis)
        assertTrue(usedDay1.contains("DAILY_EXCLUSIVE"))

        // Roll over to Day 2: history should reset and DAILY_EXCLUSIVE can be delivered again
        val resultDay2 = HourlyNotificationDeliveryEngine.executeHourlyDelivery(
            context = context,
            timeMs = day2Cal.timeInMillis,
            customNotificationSet = testNotificationSet
        )
        assertTrue("DAILY_EXCLUSIVE should be eligible again on Day 2", resultDay2 is DeliveryResult.Success)
        assertEquals("DAILY_EXCLUSIVE", (resultDay2 as DeliveryResult.Success).template.id)
    }

    @Test
    fun testAlgorithmFallbackWhenNotificationSetExhausted() = runBlocking {
        // Empty Notification Set (or all used for today)
        val emptyNotificationSet = emptyList<NotificationTemplate>()

        val hourCal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 25, 12, 0, 0)
        }

        val result = HourlyNotificationDeliveryEngine.executeHourlyDelivery(
            context = context,
            timeMs = hourCal.timeInMillis,
            customNotificationSet = emptyNotificationSet
        )

        assertTrue("Should successfully fall back to algorithm notification", result is DeliveryResult.Success)
        val success = result as DeliveryResult.Success
        assertEquals(HourlyNotificationDeliveryEngine.SOURCE_ALGORITHM, success.source)
        assertTrue("Algorithm notification ID should start with ALGO_", success.template.id.startsWith("ALGO_"))
        assertNotNull(success.template.title)
    }

    @Test
    fun testEmptyStateWhenNeitherSourceHasContent() = runBlocking {
        // Clear all data so algorithm has no context (no subjects, tasks, targets, chapters, notes, profile)
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            db.clearAllTables()
            db.settingsDao().upsertSettings(
                AppSettingsEntity(
                    id = 1,
                    studyRemindersEnabled = true,
                    quietHoursStartHour = 22,
                    quietHoursEndHour = 7
                )
            )
        }

        val hourCal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 25, 12, 0, 0)
        }

        val result = HourlyNotificationDeliveryEngine.executeHourlyDelivery(
            context = context,
            timeMs = hourCal.timeInMillis,
            customNotificationSet = emptyList() // No static items
        )

        assertTrue("Empty state must not fabricate notifications", result is DeliveryResult.Skipped)
        assertEquals("NO_ELIGIBLE_CONTENT", (result as DeliveryResult.Skipped).reason)
    }

    @Test
    fun testQuietHoursSuppression() = runBlocking {
        val quietCal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 25, 23, 30, 0) // 11:30 PM (quiet hours 22..7)
        }

        val result = HourlyNotificationDeliveryEngine.executeHourlyDelivery(
            context = context,
            timeMs = quietCal.timeInMillis
        )
        assertTrue(result is DeliveryResult.Skipped)
        assertEquals("QUIET_HOURS", (result as DeliveryResult.Skipped).reason)
    }

    @Test
    fun testMasterToggleDisabledSuppression() = runBlocking {
        db.settingsDao().upsertSettings(
            AppSettingsEntity(
                id = 1,
                studyRemindersEnabled = false
            )
        )

        val activeCal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 25, 14, 0, 0)
        }

        val result = HourlyNotificationDeliveryEngine.executeHourlyDelivery(
            context = context,
            timeMs = activeCal.timeInMillis
        )
        assertTrue(result is DeliveryResult.Skipped)
        assertEquals("DISABLED", (result as DeliveryResult.Skipped).reason)
    }

    @Test
    fun testBootReceiverRestoresHourlyScheduleWithoutBurst() {
        val receiver = StudyHubNotificationReceiver()
        val bootIntent = Intent(Intent.ACTION_BOOT_COMPLETED)

        // Trigger boot receiver
        receiver.onReceive(context, bootIntent)

        // Verify that boot receiver did not immediately fire a burst of notifications
        val prefs = context.getSharedPreferences(HourlyNotificationDeliveryEngine.PREFS_NAME, Context.MODE_PRIVATE)
        val lastDeliveredSlot = prefs.getString(HourlyNotificationDeliveryEngine.KEY_LAST_DELIVERED_SLOT, null)
        // No immediate notification should be marked delivered by boot alone
        assertTrue("Boot must not deliver an immediate burst of notifications", lastDeliveredSlot == null)
    }
}
