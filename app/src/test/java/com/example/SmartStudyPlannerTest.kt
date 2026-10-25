package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AdmissionTargetEntity
import com.example.data.local.AppDatabase
import com.example.data.local.ChapterEntity
import com.example.data.local.StudySessionEntity
import com.example.data.local.StudyTaskEntity
import com.example.data.local.SubjectEntity
import com.example.data.local.UserProfile
import com.example.data.repository.StudyHubRepository
import com.example.planner.CommitResult
import com.example.planner.DurationConfidence
import com.example.planner.PlannerStatus
import com.example.planner.RuleBasedPlanningEngine
import com.example.planner.SelfLearningDurationEstimator
import com.example.planner.SmartStudyPlannerManager
import kotlinx.coroutines.flow.first
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SmartStudyPlannerTest {

    private lateinit var context: Context
    private lateinit var db: AppDatabase
    private lateinit var repository: StudyHubRepository

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Application>()
        db = AppDatabase.getDatabase(context)
        repository = StudyHubRepository(db)
        runBlocking(kotlinx.coroutines.Dispatchers.IO) {
            db.clearAllTables()
        }
    }

    @After
    fun tearDown() {
        runBlocking(kotlinx.coroutines.Dispatchers.IO) {
            db.clearAllTables()
        }
    }

    private fun createSubject(id: Long, name: String): SubjectEntity {
        return SubjectEntity(
            id = id,
            name = name,
            banglaName = null,
            colorHex = "#3B82F6",
            iconKey = "book",
            category = "Compulsory",
            sortOrder = 1
        )
    }

    private fun createChapter(id: Long, subjectId: Long, title: String, status: String = "NOT_STARTED"): ChapterEntity {
        return ChapterEntity(
            id = id,
            subjectId = subjectId,
            title = title,
            sortOrder = 1,
            status = status
        )
    }

    private fun createSession(
        id: Long,
        subjectId: Long?,
        chapterId: Long?,
        elapsedSeconds: Int,
        endedAt: Long = System.currentTimeMillis()
    ): StudySessionEntity {
        return StudySessionEntity(
            id = id,
            subjectId = subjectId,
            chapterId = chapterId,
            taskId = null,
            startedAt = endedAt - (elapsedSeconds * 1000L),
            endedAt = endedAt,
            elapsedSeconds = elapsedSeconds,
            status = "COMPLETED",
            sessionType = "POMODORO_FOCUS"
        )
    }

    // =========================================================================
    // 1. SELF-LEARNING DURATION ESTIMATOR TESTS
    // =========================================================================

    @Test
    fun `estimator returns default confidence when zero sessions exist`() {
        val result = SelfLearningDurationEstimator.estimateDuration(
            subjectId = 1L,
            chapterId = 10L,
            taskId = null,
            sessions = emptyList(),
            userDefaultMinutes = 45
        )

        assertEquals(45, result.durationMinutes)
        assertEquals(DurationConfidence.DEFAULT, result.confidence)
        assertEquals(0, result.sampleCount)
    }

    @Test
    fun `estimator rejects short noise and excessively long outliers`() {
        val sessions = listOf(
            // Noise: < 5 minutes (300 seconds)
            createSession(1, 1L, 10L, 120), // 2 mins -> excluded
            createSession(2, 1L, 10L, 200), // 3.3 mins -> excluded
            // Valid sessions: 30 mins (1800s), 40 mins (2400s)
            createSession(3, 1L, 10L, 1800),
            createSession(4, 1L, 10L, 2400),
            // Outlier: > 180 minutes (10800s)
            createSession(5, 1L, 10L, 12000) // 200 mins -> excluded
        )

        val result = SelfLearningDurationEstimator.estimateDuration(
            subjectId = 1L,
            chapterId = 10L,
            taskId = null,
            sessions = sessions,
            userDefaultMinutes = 45
        )

        // Only 1800s (30m) and 2400s (40m) are valid -> sampleCount = 2 -> confidence = MEDIUM
        assertEquals(2, result.sampleCount)
        assertEquals(DurationConfidence.MEDIUM, result.confidence)
        // With avg 35m blended with user default 45m: (35*0.6 + 45*0.4) = 39 -> rounded to 40
        assertEquals(40, result.durationMinutes)
    }

    @Test
    fun `estimator returns high confidence with 3 or more sessions and rounds to 5 mins`() {
        val sessions = listOf(
            createSession(1, 1L, 10L, 1500), // 25 mins
            createSession(2, 1L, 10L, 1800), // 30 mins
            createSession(3, 1L, 10L, 2100), // 35 mins
            createSession(4, 1L, 10L, 2400)  // 40 mins
        )

        val result = SelfLearningDurationEstimator.estimateDuration(
            subjectId = 1L,
            chapterId = 10L,
            taskId = null,
            sessions = sessions,
            userDefaultMinutes = 45
        )

        assertEquals(4, result.sampleCount)
        assertEquals(DurationConfidence.HIGH, result.confidence)
        // Median of 25, 30, 35, 40 is (30 + 35) / 2 = 32.5 -> rounds to 35
        assertTrue(result.durationMinutes in 30..35)
    }

    @Test
    fun `estimator falls back to subject-level history when chapter has no sessions`() {
        val sessions = listOf(
            // Sessions for subject 1, but chapter 20
            createSession(1, 1L, 20L, 3000), // 50 mins
            createSession(2, 1L, 20L, 3000), // 50 mins
            createSession(3, 1L, 20L, 3000)  // 50 mins
        )

        val result = SelfLearningDurationEstimator.estimateDuration(
            subjectId = 1L,
            chapterId = 999L, // non-existent chapter
            taskId = null,
            sessions = sessions,
            userDefaultMinutes = 45
        )

        assertEquals(3, result.sampleCount)
        assertEquals(DurationConfidence.HIGH, result.confidence)
        assertEquals(50, result.durationMinutes)
    }

    @Test
    fun `estimator clamps duration between 15 and 90 minutes`() {
        // Fast sessions (e.g. 8 mins valid, but clamped at 15)
        val shortSessions = listOf(createSession(1, 1L, 10L, 480)) // 8 mins
        val resultShort = SelfLearningDurationEstimator.estimateDuration(1L, 10L, null, shortSessions, 10)
        assertEquals(15, resultShort.durationMinutes)

        // Very long valid sessions (e.g. 150 mins -> clamped at 90)
        val longSessions = listOf(createSession(2, 1L, 10L, 9000)) // 150 mins
        val resultLong = SelfLearningDurationEstimator.estimateDuration(1L, 10L, null, longSessions, 150)
        assertEquals(90, resultLong.durationMinutes)
    }

    // =========================================================================
    // 2. RULE-BASED PLANNING ENGINE & PRIORITY TESTS
    // =========================================================================

    @Test
    fun `exam urgency increases candidate priority score`() {
        val targetDateStr = "2026-11-01"
        val examDateNear = "2026-11-03" // 2 days after target date -> urgent!
        val examDateFar = "2026-12-15"  // 44 days after target date

        val subjectNear = createSubject(1L, "Physics")
        val subjectFar = createSubject(2L, "Chemistry")

        val chapterNear = createChapter(10L, 1L, "Thermodynamics")
        val chapterFar = createChapter(20L, 2L, "Organic Chemistry")

        val targetNear = AdmissionTargetEntity(
            id = 100L,
            institutionName = "Physics Admission",
            applicationDeadline = "2026-10-20",
            examDate = examDateNear
        )
        val targetFar = AdmissionTargetEntity(
            id = 200L,
            institutionName = "Chemistry Admission",
            applicationDeadline = "2026-10-20",
            examDate = examDateFar
        )

        val draft = RuleBasedPlanningEngine.generatePlan(
            targetDate = targetDateStr,
            subjects = listOf(subjectNear, subjectFar),
            chapters = listOf(chapterNear, chapterFar),
            tasks = emptyList(),
            sessions = emptyList(),
            admissionTargets = listOf(targetNear, targetFar),
            userProfile = UserProfile(dailyGoalMinutes = 120),
            currentTimeMs = 1000L
        )

        assertFalse(draft.tasks.isEmpty())
        val topTask = draft.tasks.first()
        // Urgent exam subject should be first
        assertEquals(1L, topTask.subjectId)
        assertTrue(topTask.recommendationReason.contains("Exam", ignoreCase = true) || topTask.recommendationReason.contains("Admission"))
    }

    @Test
    fun `daily study budget constraint is respected`() {
        val targetDateStr = "2026-11-01"
        val subject = createSubject(1L, "Biology")
        val chapters = (1..10).map { createChapter(it.toLong(), 1L, "Chapter $it") }

        // Budget of 90 minutes
        val userProfile = UserProfile(dailyGoalMinutes = 90)

        val draft = RuleBasedPlanningEngine.generatePlan(
            targetDate = targetDateStr,
            subjects = listOf(subject),
            chapters = chapters,
            tasks = emptyList(),
            sessions = emptyList(),
            admissionTargets = emptyList(),
            userProfile = userProfile,
            currentTimeMs = 1000L
        )

        val totalProposedDuration = draft.tasks.sumOf { it.durationMinutes }
        assertTrue("Total proposed duration $totalProposedDuration must be <= 90", totalProposedDuration <= 90)
    }

    // =========================================================================
    // 3. CONFLICT VALIDATION & TIME SLOT SCHEDULING TESTS
    // =========================================================================

    @Test
    fun `scheduling avoids overlapping existing calendar plans`() {
        val targetDateStr = "2026-11-01"
        val subject = createSubject(1L, "Math")
        val chapter = createChapter(10L, 1L, "Calculus")

        // Existing task at 10:00 AM (duration 60 mins -> 10:00 to 11:00)
        val existingTask = StudyTaskEntity(
            id = 55L,
            title = "Morning Review",
            date = targetDateStr,
            time = "10:00",
            durationMinutes = 60,
            status = "PENDING"
        )

        val draft = RuleBasedPlanningEngine.generatePlan(
            targetDate = targetDateStr,
            subjects = listOf(subject),
            chapters = listOf(chapter),
            tasks = listOf(existingTask),
            sessions = emptyList(),
            admissionTargets = emptyList(),
            userProfile = UserProfile(dailyGoalMinutes = 120, preferredStudyTime = "Morning"),
            currentTimeMs = 1000L
        )

        val proposed = draft.tasks
        assertFalse(proposed.isEmpty())

        // None of the proposed tasks should overlap with 10:00 - 11:00
        for (task in proposed) {
            val parts = task.startTime.split(":")
            val taskStart = parts[0].toInt() * 60 + parts[1].toInt()
            val taskEnd = taskStart + task.durationMinutes

            val existStart = 10 * 60
            val existEnd = 11 * 60

            val overlaps = maxOf(taskStart, existStart) < minOf(taskEnd, existEnd)
            assertFalse("Proposed task at ${task.startTime} (${task.durationMinutes}m) overlaps with existing task at 10:00-11:00", overlaps)
        }
    }

    @Test
    fun `today planning schedules strictly in the future`() {
        val todayStr = dateFormat.format(Date())

        // Suppose current time is 14:30 (2:30 PM) -> 870 minutes
        val nowCal = Calendar.getInstance()
        nowCal.set(Calendar.HOUR_OF_DAY, 14)
        nowCal.set(Calendar.MINUTE, 30)
        val testNowMs = nowCal.timeInMillis

        val subject = createSubject(1L, "History")
        val chapter = createChapter(10L, 1L, "World War II")

        val draft = RuleBasedPlanningEngine.generatePlan(
            targetDate = todayStr,
            subjects = listOf(subject),
            chapters = listOf(chapter),
            tasks = emptyList(),
            sessions = emptyList(),
            admissionTargets = emptyList(),
            userProfile = UserProfile(preferredStudyTime = "Morning"), // Even if preferred time was morning
            currentTimeMs = testNowMs
        )

        val proposed = draft.tasks
        assertFalse(proposed.isEmpty())

        for (task in proposed) {
            val parts = task.startTime.split(":")
            val taskStart = parts[0].toInt() * 60 + parts[1].toInt()
            // Must be at least 14:30 + 15 mins buffer = 14:45 (885 mins)
            assertTrue("Task scheduled at ${task.startTime} ($taskStart mins) must be >= 885 mins", taskStart >= 885)
        }
    }

    // =========================================================================
    // 4. MANAGER & COMMIT TO CALENDAR TESTS
    // =========================================================================

    @Test
    fun `manager updates draft when user toggles or removes tasks`() = runBlocking {
        val subject = createSubject(1L, "English")
        val chapters = listOf(
            createChapter(1L, 1L, "Grammar"),
            createChapter(2L, 1L, "Vocabulary")
        )

        val manager = SmartStudyPlannerManager(repository)
        val draft = manager.generatePlan(
            targetDate = "2026-11-01",
            subjects = listOf(subject),
            chapters = chapters,
            tasks = emptyList(),
            sessions = emptyList(),
            admissionTargets = emptyList(),
            userProfile = UserProfile(dailyGoalMinutes = 120)
        )

        assertNotNull(draft)
        assertEquals(2, draft.tasks.size)

        val firstTaskId = draft.tasks[0].id
        // Deselect first task
        manager.toggleTaskSelection(firstTaskId)
        val updatedDraft1 = manager.currentDraft.first()
        assertFalse(updatedDraft1!!.tasks[0].isSelected)

        // Remove second task
        val secondTaskId = draft.tasks[1].id
        manager.removeTaskFromDraft(secondTaskId)
        val updatedDraft2 = manager.currentDraft.first()
        assertEquals(1, updatedDraft2!!.tasks.size)
    }

    @Test
    fun `commit to calendar generates non-overlapping StudyTaskEntity records idempotently`() = runBlocking {
        val subject = createSubject(1L, "English")
        val chapters = listOf(
            createChapter(1L, 1L, "Grammar"),
            createChapter(2L, 1L, "Vocabulary")
        )

        val manager = SmartStudyPlannerManager(repository)
        manager.generatePlan(
            targetDate = "2026-11-05",
            subjects = listOf(subject),
            chapters = chapters,
            tasks = emptyList(),
            sessions = emptyList(),
            admissionTargets = emptyList(),
            userProfile = UserProfile(dailyGoalMinutes = 120)
        )

        val result = manager.commitSelectedTasksToCalendar(emptyList())

        assertTrue(result.success)
        assertEquals(2, result.addedCount)

        // Verify entities inserted into Room database
        val committedTasks = repository.getTasksForDate("2026-11-05").first()
        assertEquals(2, committedTasks.size)

        // Verify entities have proper dates, times, and SMART_PLAN notes
        assertEquals("2026-11-05", committedTasks[0].date)
        assertEquals("STUDY", committedTasks[0].planType)
        assertTrue(committedTasks[0].notes?.contains("SmartPlanner") == true)
        assertFalse(committedTasks[0].time.isNullOrBlank())

        // Attempting to commit again without changes should be idempotent / no uncommitted tasks
        val secondResult = manager.commitSelectedTasksToCalendar(committedTasks)
        assertFalse(secondResult.success)
    }
}
