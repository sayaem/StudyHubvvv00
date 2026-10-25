package com.example

import com.example.revision.RecallRating
import com.example.revision.ReviewTimingStatus
import com.example.revision.SpacedRepetitionAlgorithm
import com.example.revision.SpacedRepetitionPolicy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SpacedRepetitionAlgorithmTest {

    private lateinit var algorithm: SpacedRepetitionAlgorithm
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    @Before
    fun setup() {
        algorithm = SpacedRepetitionAlgorithm()
    }

    @Test
    fun `initializeFirstReview schedules for anchor date with 1 day interval`() {
        val now = System.currentTimeMillis()
        val expectedDateStr = dateFormat.format(Date(now))

        val (nextDueMillis, nextDueDateStr) = algorithm.initializeFirstReview(anchorDateMillis = now)
        assertEquals(expectedDateStr, nextDueDateStr)
        assertTrue(nextDueMillis >= now - 24 * 60 * 60 * 1000L)
    }

    @Test
    fun `processReview with Hard rating resets interval to 1 day`() {
        val now = System.currentTimeMillis()
        val result = algorithm.processReview(
            currentIntervalDays = 14.0,
            rating = RecallRating.HARD,
            scheduledDueMillis = now,
            completionMillis = now
        )

        assertEquals(1.0, result.newIntervalDays, 0.001)
        assertEquals(ReviewTimingStatus.ON_TIME, result.timingStatus)
        assertTrue(result.explanation.contains("Hard"))
        assertTrue(result.explanation.contains("1 day"))
    }

    @Test
    fun `processReview with Okay rating expands interval by 1_5x`() {
        val now = System.currentTimeMillis()
        val result = algorithm.processReview(
            currentIntervalDays = 4.0,
            rating = RecallRating.OKAY,
            scheduledDueMillis = now,
            completionMillis = now
        )

        // 4.0 * 1.5 = 6.0 days
        assertEquals(6.0, result.newIntervalDays, 0.001)
        assertEquals(ReviewTimingStatus.ON_TIME, result.timingStatus)
        assertTrue(result.explanation.contains("Okay"))
        assertTrue(result.explanation.contains("1.5x"))
    }

    @Test
    fun `processReview with Easy rating doubles interval to 2_0x`() {
        val now = System.currentTimeMillis()
        val result = algorithm.processReview(
            currentIntervalDays = 5.0,
            rating = RecallRating.EASY,
            scheduledDueMillis = now,
            completionMillis = now
        )

        // 5.0 * 2.0 = 10.0 days
        assertEquals(10.0, result.newIntervalDays, 0.001)
        assertEquals(ReviewTimingStatus.ON_TIME, result.timingStatus)
        assertTrue(result.explanation.contains("Easy"))
        assertTrue(result.explanation.contains("doubled"))
    }

    @Test
    fun `processReview clamps interval between min 1 day and max 180 days`() {
        val now = System.currentTimeMillis()
        // Below min fallback
        val resultMin = algorithm.processReview(
            currentIntervalDays = 0.2,
            rating = RecallRating.HARD,
            scheduledDueMillis = now,
            completionMillis = now
        )
        assertEquals(1.0, resultMin.newIntervalDays, 0.001)

        // Runaway max clamp
        val resultMax = algorithm.processReview(
            currentIntervalDays = 120.0,
            rating = RecallRating.EASY,
            scheduledDueMillis = now,
            completionMillis = now
        )
        // 120 * 2 = 240, clamped to 180.0
        assertEquals(180.0, resultMax.newIntervalDays, 0.001)
    }

    @Test
    fun `processReview correctly identifies overdue review and includes days late in explanation`() {
        val now = System.currentTimeMillis()
        val twoDaysAgo = now - (2 * 24 * 60 * 60 * 1000L)

        val result = algorithm.processReview(
            currentIntervalDays = 2.0,
            rating = RecallRating.OKAY,
            scheduledDueMillis = twoDaysAgo,
            completionMillis = now
        )

        assertEquals(ReviewTimingStatus.OVERDUE, result.timingStatus)
        assertTrue(result.explanation.contains("late") || result.explanation.contains("overdue"))
    }

    @Test
    fun `processReview caps interval when upcoming exam date is approaching`() {
        val now = System.currentTimeMillis()
        // Exam is 5 days from now
        val examDateMillis = now + (5 * 24 * 60 * 60 * 1000L)

        val result = algorithm.processReview(
            currentIntervalDays = 6.0,
            rating = RecallRating.EASY,
            scheduledDueMillis = now,
            completionMillis = now,
            examDateMillis = examDateMillis
        )

        // Normally 6.0 * 2.0 = 12.0 days, but exam is in 5 days, so interval must be capped to < 5 days
        assertTrue(result.newIntervalDays <= 4.0)
        assertTrue(result.explanation.contains("exam"))
    }

    @Test
    fun `postponeReview safely adds days without altering interval multiplier or review count`() {
        val now = System.currentTimeMillis()
        val (newDueMillis, newDueDateStr) = algorithm.postponeReview(currentDueMillis = now, postponeDays = 2)

        val expectedDate = dateFormat.format(Date(now + 2 * 24 * 60 * 60 * 1000L))
        assertEquals(expectedDate, newDueDateStr)
        assertTrue(newDueMillis > now)
    }

    @Test
    fun `evaluateDueStatus accurately detects due today and overdue`() {
        val (dueToday, overdueToday, overdueDaysToday) = algorithm.evaluateDueStatus("2026-10-25", "2026-10-25")
        assertTrue(dueToday)
        assertFalse(overdueToday)
        assertEquals(0, overdueDaysToday)

        val (dueOverdue, isOverdue, daysOverdue) = algorithm.evaluateDueStatus("2026-10-22", "2026-10-25")
        assertFalse(dueOverdue)
        assertTrue(isOverdue)
        assertEquals(3, daysOverdue)

        val (dueFuture, isOverdueFuture, daysFuture) = algorithm.evaluateDueStatus("2026-10-28", "2026-10-25")
        assertFalse(dueFuture)
        assertFalse(isOverdueFuture)
        assertEquals(0, daysFuture)
    }

    @Test
    fun `identical inputs produce strictly deterministic outputs`() {
        val now = 1792900000000L
        val res1 = algorithm.processReview(4.0, RecallRating.OKAY, now, now)
        val res2 = algorithm.processReview(4.0, RecallRating.OKAY, now, now)

        assertEquals(res1.newIntervalDays, res2.newIntervalDays, 0.001)
        assertEquals(res1.nextDueAt, res2.nextDueAt)
        assertEquals(res1.nextDueDate, res2.nextDueDate)
        assertEquals(res1.timingStatus, res2.timingStatus)
        assertEquals(res1.explanation, res2.explanation)
    }
}
