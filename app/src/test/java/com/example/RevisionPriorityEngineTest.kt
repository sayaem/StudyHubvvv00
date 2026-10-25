package com.example

import com.example.revision.RecallRating
import com.example.revision.RevisionPriorityEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RevisionPriorityEngineTest {

    private lateinit var priorityEngine: RevisionPriorityEngine

    @Before
    fun setup() {
        priorityEngine = RevisionPriorityEngine()
    }

    @Test
    fun `overdue topics receive highest priority score with days overdue boost`() {
        val result1DayOverdue = priorityEngine.evaluatePriority(
            nextDueDateStr = "2026-10-24",
            reviewCount = 3,
            lastRecallRating = RecallRating.OKAY,
            currentDateStr = "2026-10-25"
        )

        val result3DaysOverdue = priorityEngine.evaluatePriority(
            nextDueDateStr = "2026-10-22",
            reviewCount = 3,
            lastRecallRating = RecallRating.OKAY,
            currentDateStr = "2026-10-25"
        )

        assertTrue(result1DayOverdue.isOverdue)
        assertEquals(1, result1DayOverdue.daysOverdue)
        assertEquals(1050, result1DayOverdue.score) // 1000 + 1 * 50

        assertTrue(result3DaysOverdue.isOverdue)
        assertEquals(3, result3DaysOverdue.daysOverdue)
        assertEquals(1150, result3DaysOverdue.score) // 1000 + 3 * 50

        assertTrue(result3DaysOverdue.score > result1DayOverdue.score)
    }

    @Test
    fun `due today with previous Hard rating ranks above normal due today`() {
        val hardResult = priorityEngine.evaluatePriority(
            nextDueDateStr = "2026-10-25",
            reviewCount = 2,
            lastRecallRating = RecallRating.HARD,
            currentDateStr = "2026-10-25"
        )

        val normalResult = priorityEngine.evaluatePriority(
            nextDueDateStr = "2026-10-25",
            reviewCount = 2,
            lastRecallRating = RecallRating.OKAY,
            currentDateStr = "2026-10-25"
        )

        assertTrue(hardResult.isDueToday)
        assertEquals(800, hardResult.score)
        assertTrue(hardResult.reason.contains("Hard"))

        assertTrue(normalResult.isDueToday)
        assertEquals(500, normalResult.score)

        assertTrue(hardResult.score > normalResult.score)
    }

    @Test
    fun `due today with upcoming exam within 14 days receives exam priority boost`() {
        val examApproachingResult = priorityEngine.evaluatePriority(
            nextDueDateStr = "2026-10-25",
            reviewCount = 1,
            lastRecallRating = RecallRating.OKAY,
            nearestExamDateStr = "2026-11-02", // 8 days away
            currentDateStr = "2026-10-25"
        )

        assertEquals(700, examApproachingResult.score)
        assertTrue(examApproachingResult.reason.contains("Exam approaching"))
    }

    @Test
    fun `first review needed receives appropriate baseline attention score`() {
        val firstReviewResult = priorityEngine.evaluatePriority(
            nextDueDateStr = "2026-10-26",
            reviewCount = 0,
            lastRecallRating = null,
            currentDateStr = "2026-10-25"
        )

        assertEquals(400, firstReviewResult.score)
        assertTrue(firstReviewResult.reason.contains("First review needed"))
    }

    @Test
    fun `missing exam date or missing ratings do not fabricate reasons`() {
        val cleanResult = priorityEngine.evaluatePriority(
            nextDueDateStr = "2026-10-25",
            reviewCount = 1,
            lastRecallRating = null,
            nearestExamDateStr = null,
            currentDateStr = "2026-10-25"
        )

        assertEquals(500, cleanResult.score)
        assertFalse(cleanResult.reason.contains("Exam"))
        assertFalse(cleanResult.reason.contains("Hard"))
    }
}
