package com.example

import com.example.data.local.AdmissionTargetEntity
import com.example.data.local.ChapterEntity
import com.example.data.local.FocusGamificationEntity
import com.example.data.local.StudySessionEntity
import com.example.data.local.StudyTaskEntity
import com.example.data.local.SubjectEntity
import com.example.data.local.UserProfile
import com.example.ui.screens.stats.DateRangeFilter
import com.example.ui.screens.stats.InsightType
import com.example.ui.screens.stats.StatsAnalyticsEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class StatsAnalyticsEngineTest {

    @Test
    fun testFormatDuration() {
        assertEquals("0m", StatsAnalyticsEngine.formatDuration(0))
        assertEquals("45m", StatsAnalyticsEngine.formatDuration(45))
        assertEquals("1h", StatsAnalyticsEngine.formatDuration(60))
        assertEquals("1h 30m", StatsAnalyticsEngine.formatDuration(90))
        assertEquals("14h 20m", StatsAnalyticsEngine.formatDuration(860))
    }

    @Test
    fun testPeriodOverviewWithSessions() {
        val now = System.currentTimeMillis()
        val sessions = listOf(
            StudySessionEntity(
                startedAt = now - 3600_000,
                endedAt = now,
                elapsedSeconds = 3600,
                status = "COMPLETED"
            ),
            StudySessionEntity(
                startedAt = now - 7200_000,
                endedAt = now - 3600_000,
                elapsedSeconds = 1800,
                status = "COMPLETED"
            ),
            StudySessionEntity(
                startedAt = now - 10000,
                endedAt = now,
                elapsedSeconds = 500,
                status = "ABANDONED"
            )
        )

        val profile = UserProfile(dailyGoalMinutes = 60)
        val overview = StatsAnalyticsEngine.calculatePeriodOverview(
            filter = DateRangeFilter.SEVEN_DAYS,
            sessions = sessions,
            userProfile = profile,
            referenceTimeMillis = now
        )

        assertEquals(90, overview.totalMinutes) // 3600s + 1800s = 90 mins
        assertEquals(2, overview.completedSessionsCount)
        assertEquals(45, overview.avgSessionDurationMinutes) // 90 / 2
        assertTrue(overview.goalsMetDays >= 1) // 90 mins meets 60 mins goal
    }

    @Test
    fun testConsistencyHeatmapGeneration() {
        val now = System.currentTimeMillis()
        val sessions = listOf(
            StudySessionEntity(
                startedAt = now - 1000,
                endedAt = now,
                elapsedSeconds = 7200, // 120 mins -> intensity 4
                status = "COMPLETED"
            )
        )

        val heatmap = StatsAnalyticsEngine.generateConsistencyHeatmap(
            sessions = sessions,
            daysCount = 28,
            referenceTimeMillis = now
        )

        assertEquals(28, heatmap.size)
        val todayCell = heatmap.last()
        assertTrue(todayCell.isToday)
        assertEquals(120, todayCell.minutes)
        assertEquals(4, todayCell.intensityLevel)
    }

    @Test
    fun testSubjectIntelligence() {
        val now = System.currentTimeMillis()
        val subject1 = SubjectEntity(id = 1L, name = "Physics", colorHex = "#2563EB")
        val subject2 = SubjectEntity(id = 2L, name = "Chemistry", colorHex = "#10B981")

        val chapters = listOf(
            ChapterEntity(id = 1L, subjectId = 1L, title = "Motion", status = "COMPLETED"),
            ChapterEntity(id = 2L, subjectId = 1L, title = "Forces", status = "NOT_STARTED"),
            ChapterEntity(id = 3L, subjectId = 2L, title = "Atomic Structure", status = "NOT_STARTED")
        )

        val sessions = listOf(
            StudySessionEntity(
                subjectId = 1L,
                startedAt = now - 3600_000,
                endedAt = now,
                elapsedSeconds = 3600,
                status = "COMPLETED"
            )
        )

        val tasks = listOf(
            StudyTaskEntity(
                id = 1L,
                title = "Solve Physics problems",
                date = "2026-10-25",
                durationMinutes = 60,
                subjectId = 1L
            )
        )

        val analytics = StatsAnalyticsEngine.calculateSubjectIntelligence(
            filter = DateRangeFilter.ALL_TIME,
            sessions = sessions,
            subjects = listOf(subject1, subject2),
            chapters = chapters,
            tasks = tasks,
            referenceTimeMillis = now
        )

        assertEquals(2, analytics.size)
        val physicsStats = analytics.first { it.subject.id == 1L }
        assertEquals(60, physicsStats.totalMinutes)
        assertEquals(1, physicsStats.completedChapters)
        assertEquals(2, physicsStats.totalChapters)
        assertEquals(50, physicsStats.chapterCompletionPercent)
        assertEquals(100f, physicsStats.sharePercent, 0.01f)

        val chemStats = analytics.first { it.subject.id == 2L }
        assertEquals(0, chemStats.totalMinutes)
        assertEquals(0, chemStats.completedChapters)
    }

    @Test
    fun testFocusSessionBreakdown() {
        val now = System.currentTimeMillis()
        val sessions = listOf(
            StudySessionEntity(
                startedAt = now - 3600_000,
                endedAt = now,
                elapsedSeconds = 3600,
                status = "COMPLETED",
                sessionType = "POMODORO_FOCUS",
                reflectionRating = 5,
                distractions = 2
            ),
            StudySessionEntity(
                startedAt = now - 7200_000,
                endedAt = now - 3600_000,
                elapsedSeconds = 600,
                status = "ABANDONED",
                sessionType = "DEEP_FOCUS",
                reflectionRating = 3,
                distractions = 1
            )
        )

        val breakdown = StatsAnalyticsEngine.calculateFocusSessionBreakdown(
            filter = DateRangeFilter.ALL_TIME,
            sessions = sessions,
            referenceTimeMillis = now
        )

        assertEquals(1, breakdown.completedCount)
        assertEquals(1, breakdown.abandonedCount)
        assertEquals(50, breakdown.completionRatePercent)
        assertEquals(3, breakdown.totalDistractions)
        assertEquals(4.0f, breakdown.avgRating, 0.01f)
    }

    @Test
    fun testPlannedVsActual() {
        val now = System.currentTimeMillis()
        val tasks = listOf(
            StudyTaskEntity(
                id = 1L,
                title = "Math Calculus",
                date = "2026-10-25",
                durationMinutes = 60,
                status = "COMPLETED"
            ),
            StudyTaskEntity(
                id = 2L,
                title = "Biology Review",
                date = "2026-10-25",
                durationMinutes = 60,
                status = "PENDING"
            )
        )

        val sessions = listOf(
            StudySessionEntity(
                startedAt = now - 3600_000,
                endedAt = now,
                elapsedSeconds = 5400, // 90 mins
                status = "COMPLETED"
            )
        )

        val plannedVsActual = StatsAnalyticsEngine.calculatePlannedVsActual(
            filter = DateRangeFilter.ALL_TIME,
            tasks = tasks,
            sessions = sessions,
            referenceTimeMillis = now
        )

        assertEquals(120, plannedVsActual.plannedMinutes)
        assertEquals(90, plannedVsActual.actualMinutes)
        assertEquals(2, plannedVsActual.plannedTasksCount)
        assertEquals(1, plannedVsActual.completedTasksCount)
        assertEquals(50, plannedVsActual.taskCompletionRatePercent)
        assertEquals(75, plannedVsActual.adherencePercent) // 90 / 120 = 75%
    }

    @Test
    fun testSmartInsightsGeneration() {
        val overview = StatsAnalyticsEngine.calculatePeriodOverview(
            filter = DateRangeFilter.SEVEN_DAYS,
            sessions = emptyList(),
            userProfile = UserProfile(dailyGoalMinutes = 120)
        )
        val breakdown = StatsAnalyticsEngine.calculateFocusSessionBreakdown(
            filter = DateRangeFilter.SEVEN_DAYS,
            sessions = emptyList()
        )
        val gamification = FocusGamificationEntity(currentStreakDays = 5)

        val target = AdmissionTargetEntity(
            id = 1L,
            institutionName = "BUET",
            applicationDeadline = "2026-12-01"
        )

        val insights = StatsAnalyticsEngine.generateInsights(
            overview = overview,
            breakdown = breakdown,
            subjects = emptyList(),
            gamification = gamification,
            admissionTargets = listOf(target),
            userProfile = UserProfile()
        )

        assertTrue(insights.any { it.type == InsightType.STREAK })
        assertTrue(insights.any { it.type == InsightType.READINESS })
    }
}
