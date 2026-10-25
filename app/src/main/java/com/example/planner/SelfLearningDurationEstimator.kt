package com.example.planner

import com.example.data.local.StudySessionEntity
import kotlin.math.roundToInt

data class LearnedDurationEstimate(
    val durationMinutes: Int,
    val confidence: DurationConfidence,
    val sourceDescription: String,
    val sampleCount: Int
)

object SelfLearningDurationEstimator {

    private const val MIN_ALLOWED_MINUTES = 15
    private const val MAX_ALLOWED_MINUTES = 90
    private const val DEFAULT_FALLBACK_MINUTES = 30

    /**
     * Estimates realistic task duration based on verified, completed historical study sessions.
     * Uses robust statistics (outlier filtering, median, and bounded adaptation) without any AI.
     */
    fun estimateDuration(
        subjectId: Long?,
        chapterId: Long?,
        taskId: Long?,
        sessions: List<StudySessionEntity>,
        userDefaultMinutes: Int = DEFAULT_FALLBACK_MINUTES
    ): LearnedDurationEstimate {
        // 1. Filter valid historical sessions
        // Must be COMPLETED with active elapsedSeconds between 5 minutes and 180 minutes
        val validSessions = sessions.filter { session ->
            session.status == "COMPLETED" &&
                    session.elapsedSeconds in (5 * 60)..(180 * 60)
        }

        // 2. Check for exact task-level historical match
        if (taskId != null) {
            val taskSessions = validSessions.filter { it.taskId == taskId }
            if (taskSessions.isNotEmpty()) {
                val durations = taskSessions.map { it.elapsedSeconds / 60 }.sorted()
                val median = calculateMedian(durations)
                val clamped = median.coerceIn(MIN_ALLOWED_MINUTES, MAX_ALLOWED_MINUTES)
                val rounded = roundToNearestFive(clamped)
                val confidence = if (taskSessions.size >= 3) DurationConfidence.HIGH else DurationConfidence.MEDIUM
                return LearnedDurationEstimate(
                    durationMinutes = rounded,
                    confidence = confidence,
                    sourceDescription = "Learned from ${taskSessions.size} past session(s) for this task",
                    sampleCount = taskSessions.size
                )
            }
        }

        // 3. Check for subject-level historical sessions
        if (subjectId != null) {
            val subjectSessions = validSessions.filter { it.subjectId == subjectId }
            if (subjectSessions.isNotEmpty()) {
                val durations = subjectSessions.map { it.elapsedSeconds / 60 }.sorted()

                return if (durations.size >= 3) {
                    // High Confidence: Trimmed median
                    val median = calculateMedian(durations)
                    val clamped = median.coerceIn(MIN_ALLOWED_MINUTES, MAX_ALLOWED_MINUTES)
                    val rounded = roundToNearestFive(clamped)
                    LearnedDurationEstimate(
                        durationMinutes = rounded,
                        confidence = DurationConfidence.HIGH,
                        sourceDescription = "Learned from ${durations.size} past sessions for this subject",
                        sampleCount = durations.size
                    )
                } else {
                    // Medium Confidence (1-2 sessions): blended with user default
                    val avg = durations.average().toInt()
                    val blended = (avg * 0.6 + userDefaultMinutes * 0.4).roundToInt()
                    val clamped = blended.coerceIn(MIN_ALLOWED_MINUTES, MAX_ALLOWED_MINUTES)
                    val rounded = roundToNearestFive(clamped)
                    LearnedDurationEstimate(
                        durationMinutes = rounded,
                        confidence = DurationConfidence.MEDIUM,
                        sourceDescription = "Adjusted from ${durations.size} past session(s)",
                        sampleCount = durations.size
                    )
                }
            }
        }

        // 4. Check global study session patterns if subject has no data
        if (validSessions.size >= 5) {
            val allDurations = validSessions.map { it.elapsedSeconds / 60 }.sorted()
            val median = calculateMedian(allDurations)
            val clamped = median.coerceIn(MIN_ALLOWED_MINUTES, MAX_ALLOWED_MINUTES)
            val rounded = roundToNearestFive(clamped)
            return LearnedDurationEstimate(
                durationMinutes = rounded,
                confidence = DurationConfidence.LOW,
                sourceDescription = "Based on your overall study session average (${validSessions.size} sessions)",
                sampleCount = validSessions.size
            )
        }

        // 5. Cold-Start Baseline Default
        val baseline = userDefaultMinutes.coerceIn(MIN_ALLOWED_MINUTES, 60)
        val roundedBaseline = roundToNearestFive(baseline)
        return LearnedDurationEstimate(
            durationMinutes = roundedBaseline,
            confidence = DurationConfidence.DEFAULT,
            sourceDescription = "Baseline default (No past sessions for this subject)",
            sampleCount = 0
        )
    }

    private fun calculateMedian(sortedList: List<Int>): Int {
        if (sortedList.isEmpty()) return DEFAULT_FALLBACK_MINUTES
        val size = sortedList.size
        return if (size % 2 == 1) {
            sortedList[size / 2]
        } else {
            (sortedList[size / 2 - 1] + sortedList[size / 2]) / 2
        }
    }

    private fun roundToNearestFive(minutes: Int): Int {
        return ((minutes + 2) / 5) * 5
    }
}
