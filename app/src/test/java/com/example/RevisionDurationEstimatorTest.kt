package com.example

import com.example.revision.DurationConfidence
import com.example.revision.RevisionDurationEstimator
import com.example.revision.SpacedRepetitionPolicy
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RevisionDurationEstimatorTest {

    private lateinit var estimator: RevisionDurationEstimator

    @Before
    fun setup() {
        estimator = RevisionDurationEstimator(SpacedRepetitionPolicy())
    }

    @Test
    fun `empty history falls back to default 25 minutes with BASELINE confidence`() {
        val (estimatedMins, confidence) = estimator.estimateDuration(emptyList())

        assertEquals(25, estimatedMins)
        assertEquals(DurationConfidence.BASELINE, confidence)
    }

    @Test
    fun `one or two observations produce EARLY_ESTIMATE confidence and blend with baseline`() {
        // One session of 15 minutes (900 seconds)
        val (estimatedMins, confidence) = estimator.estimateDuration(listOf(900))

        assertEquals(DurationConfidence.EARLY_ESTIMATE, confidence)
        // (15 * 1 + 25 * 2) / 3 = 65 / 3 = 21.6 -> 22 mins
        assertEquals(22, estimatedMins)
    }

    @Test
    fun `three or more observations use robust statistical median with HISTORY_BACKED confidence`() {
        // Durations: 15 min, 20 min, 25 min, 40 min, 100 min
        val sessionsSec = listOf(15 * 60, 20 * 60, 25 * 60, 40 * 60, 100 * 60)

        val (estimatedMins, confidence) = estimator.estimateDuration(sessionsSec)

        assertEquals(DurationConfidence.HISTORY_BACKED, confidence)
        // Median of [15, 20, 25, 40, 100] is 25
        assertEquals(25, estimatedMins)
    }

    @Test
    fun `invalid or implausibly short and long sessions are filtered out`() {
        // 10s (too short), 30s (too short), 20 min (valid), 25 min (valid), 30 min (valid), 4 hours (too long)
        val sessionsSec = listOf(10, 30, 20 * 60, 25 * 60, 30 * 60, 14400)

        val (estimatedMins, confidence) = estimator.estimateDuration(sessionsSec)

        assertEquals(DurationConfidence.HISTORY_BACKED, confidence)
        // Only [20, 25, 30] mins are valid. Median = 25 min
        assertEquals(25, estimatedMins)
    }

    @Test
    fun `extreme estimates are clamped within min 10m and max 90m`() {
        // All sessions very short: 1 min each
        val shortSessions = listOf(60, 60, 60, 60)
        val (shortMins, _) = estimator.estimateDuration(shortSessions)
        assertEquals(10, shortMins) // Clamped to minDurationMinutes (10)

        // All sessions long: 110 min each
        val longSessions = listOf(110 * 60, 115 * 60, 120 * 60)
        val (longMins, _) = estimator.estimateDuration(longSessions)
        assertEquals(90, longMins) // Clamped to maxDurationMinutes (90)
    }
}
