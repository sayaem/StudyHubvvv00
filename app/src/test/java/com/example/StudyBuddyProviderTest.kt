package com.example

import com.example.buddy.BuddyAiResult
import com.example.buddy.BuddyContextEnvelope
import com.example.buddy.BuddyGenerationRequest
import com.example.buddy.BuddyQueryIntent
import com.example.buddy.FakeStudyBuddyAiProvider
import com.example.buddy.StudyHubVerifiedFacts
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StudyBuddyProviderTest {

    @Test
    fun testFakeProviderDeterministicReply() = runBlocking {
        val provider = FakeStudyBuddyAiProvider()

        val facts = StudyHubVerifiedFacts(
            studentName = "Tanvir",
            todayPlannedTasksCount = 3,
            dueRevisionsCount = 2,
            dueRevisionSubjects = listOf("Math", "Chemistry"),
            completedChaptersCount = 14,
            totalChaptersCount = 30
        )

        val request = BuddyGenerationRequest(
            conversationId = 1L,
            userPrompt = "What should I revise today?",
            recentTurns = emptyList(),
            contextEnvelope = BuddyContextEnvelope(
                queryIntent = BuddyQueryIntent.REVISION,
                verifiedFacts = facts,
                timeReference = "2026-10-25"
            )
        )

        val result = provider.generateReply(request)
        assertTrue(result is BuddyAiResult.Success)
        val success = result as BuddyAiResult.Success
        assertTrue(success.responseText.contains("2 revision items due"))
        assertTrue(success.responseText.contains("Math, Chemistry"))
        assertTrue(success.suggestedActions.isNotEmpty())
    }

    @Test
    fun testFakeProviderSimulatedError() = runBlocking {
        val provider = FakeStudyBuddyAiProvider(
            simulatedResult = BuddyAiResult.Error(
                errorMessage = "Quota exhausted",
                isQuotaExhausted = true,
                canRetry = true
            )
        )

        val request = BuddyGenerationRequest(
            conversationId = 2L,
            userPrompt = "Hello",
            recentTurns = emptyList(),
            contextEnvelope = BuddyContextEnvelope(
                queryIntent = BuddyQueryIntent.GENERAL,
                verifiedFacts = StudyHubVerifiedFacts(),
                timeReference = "2026-10-25"
            )
        )

        val result = provider.generateReply(request)
        assertTrue(result is BuddyAiResult.Error)
        val error = result as BuddyAiResult.Error
        assertEquals("Quota exhausted", error.errorMessage)
        assertTrue(error.isQuotaExhausted)
    }
}
