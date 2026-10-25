package com.example

import com.example.buddy.BuddyActionType
import com.example.buddy.BuddyContextEnvelope
import com.example.buddy.BuddyQueryIntent
import com.example.buddy.StudyBuddyContextEngine
import com.example.buddy.StudyHubVerifiedFacts
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StudyBuddyContextEngineTest {

    @Test
    fun testIntentClassification() {
        assertEquals(BuddyQueryIntent.PLANNING, StudyBuddyContextEngine.classifyIntent("Help me plan my study schedule for today"))
        assertEquals(BuddyQueryIntent.PLANNING, StudyBuddyContextEngine.classifyIntent("I missed my study routine"))
        assertEquals(BuddyQueryIntent.REVISION, StudyBuddyContextEngine.classifyIntent("What topics should I revise today with active recall?"))
        assertEquals(BuddyQueryIntent.EXAM_PREPARATION, StudyBuddyContextEngine.classifyIntent("When is my next BUET admission test?"))
        assertEquals(BuddyQueryIntent.STATS_PROGRESS, StudyBuddyContextEngine.classifyIntent("How many hours have I logged and what is my progress?"))
        assertEquals(BuddyQueryIntent.FOCUS_HABITS, StudyBuddyContextEngine.classifyIntent("Start pomodoro timer and avoid distractions"))
        assertEquals(BuddyQueryIntent.TOPIC_SYLLABUS, StudyBuddyContextEngine.classifyIntent("Explain physics Chapter 4 thermodynamics"))
        assertEquals(BuddyQueryIntent.GENERAL, StudyBuddyContextEngine.classifyIntent("How do I stay motivated when feeling exhausted?"))
    }

    @Test
    fun testTruthfulnessInEmptyFacts() {
        val emptyFacts = StudyHubVerifiedFacts(
            studentName = "Rahim",
            academicLevel = "HSC",
            admissionTrack = "Engineering",
            dailyGoalHours = 4.0f,
            todayPlannedTasksCount = 0,
            todayPlannedTasksSummary = emptyList(),
            totalSubjectsCount = 6,
            totalChaptersCount = 42,
            completedChaptersCount = 10,
            incompleteChaptersCount = 32,
            dueRevisionsCount = 0,
            dueRevisionSubjects = emptyList(),
            todayStudyMinutesRecorded = 0,
            totalStudyHoursRecorded = 0f,
            nextExamTarget = null,
            specificTopicName = null
        )

        val envelope = BuddyContextEnvelope(
            queryIntent = BuddyQueryIntent.PLANNING,
            verifiedFacts = emptyFacts,
            timeReference = "Sunday, 25 October 2026, 05:00 PM"
        )

        val instruction = StudyBuddyContextEngine.buildSystemInstruction(envelope)

        // Verify truthful acknowledging of zero/empty state
        assertTrue(instruction.contains("(No tasks scheduled for today)"))
        assertTrue(instruction.contains("No upcoming exam date recorded."))
        assertTrue(instruction.contains("0 items due"))
        assertTrue(instruction.contains("10/42 chapters completed"))
        assertTrue(instruction.contains("Rahim"))
    }

    @Test
    fun testTruthfulnessWithPopulatedFacts() {
        val facts = StudyHubVerifiedFacts(
            studentName = "Anika",
            academicLevel = "HSC 2026",
            admissionTrack = "Medical",
            dailyGoalHours = 6.0f,
            todayPlannedTasksCount = 2,
            todayPlannedTasksSummary = listOf("Revise Botany Chapter 2 (PENDING)", "Solve Chemistry MCQ (COMPLETED)"),
            totalSubjectsCount = 5,
            totalChaptersCount = 35,
            completedChaptersCount = 20,
            incompleteChaptersCount = 15,
            dueRevisionsCount = 3,
            dueRevisionSubjects = listOf("Physics", "Biology"),
            todayStudyMinutesRecorded = 90,
            totalStudyHoursRecorded = 45.5f,
            nextExamTarget = "DMC (Medical) on 2026-11-20",
            specificTopicName = "Cell Division",
            specificSubjectName = "Biology"
        )

        val envelope = BuddyContextEnvelope(
            queryIntent = BuddyQueryIntent.REVISION,
            verifiedFacts = facts,
            timeReference = "Sunday, 25 October 2026, 05:00 PM"
        )

        val instruction = StudyBuddyContextEngine.buildSystemInstruction(envelope)

        assertTrue(instruction.contains("Anika"))
        assertTrue(instruction.contains("3 items due"))
        assertTrue(instruction.contains("Physics, Biology"))
        assertTrue(instruction.contains("DMC (Medical) on 2026-11-20"))
        assertTrue(instruction.contains("Revise Botany Chapter 2 (PENDING)"))
        assertTrue(instruction.contains("Cell Division"))
    }

    @Test
    fun testSuggestedActions() {
        val facts = StudyHubVerifiedFacts(dueRevisionsCount = 2)

        val planningActions = StudyBuddyContextEngine.suggestActions(BuddyQueryIntent.PLANNING, facts)
        assertTrue(planningActions.any { it.actionType == BuddyActionType.OPEN_CALENDAR })

        val revisionActions = StudyBuddyContextEngine.suggestActions(BuddyQueryIntent.REVISION, facts)
        assertTrue(revisionActions.any { it.actionType == BuddyActionType.OPEN_REVISION_HUB })
        assertTrue(revisionActions.any { it.actionType == BuddyActionType.START_FOCUS })

        val examActions = StudyBuddyContextEngine.suggestActions(BuddyQueryIntent.EXAM_PREPARATION, facts)
        assertTrue(examActions.any { it.actionType == BuddyActionType.OPEN_ADMISSION_CALENDAR })
    }
}
