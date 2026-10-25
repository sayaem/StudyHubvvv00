package com.example.revision

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Self-reported recall rating after completing a spaced repetition revision session.
 * Strictly 3 accessible options per engineering specification:
 * - HARD: Struggled to recall key concepts (resets/shortens interval to 1 day).
 * - OKAY: Remembered some, but missed important details (moderate 1.5x multiplier).
 * - EASY: Recalled confidently and thoroughly (strong 2.0x multiplier).
 */
enum class RecallRating(val label: String, val description: String) {
    HARD("Hard", "Struggled to recall key concepts"),
    OKAY("Okay", "Remembered main ideas, missed details"),
    EASY("Easy", "Recalled concepts confidently and quickly")
}

/**
 * Timing status of a revision review relative to its scheduled due date.
 */
enum class ReviewTimingStatus {
    ON_TIME,
    OVERDUE,
    EARLY
}

/**
 * Tracking status of an item in the revision engine.
 */
enum class RevisionTrackingStatus {
    ACTIVE,
    PAUSED,
    ARCHIVED
}

/**
 * Confidence level of historical duration estimates.
 * Clearly differentiates fallback heuristics from data backed by genuine user history.
 */
enum class DurationConfidence(val label: String) {
    BASELINE("Estimated baseline (default)"),
    EARLY_ESTIMATE("Early estimate (1-2 sessions)"),
    HISTORY_BACKED("Based on your revision history (3+ sessions)")
}

/**
 * Configuration policy for the Spaced Repetition Algorithm.
 * All parameters are documented and configurable heuristics, not arbitrary magic numbers.
 */
data class SpacedRepetitionPolicy(
    val initialIntervalDays: Double = 1.0,
    val hardMultiplier: Double = 1.0, // Resets interval to 1 day
    val okayMultiplier: Double = 1.5, // Moderate expansion
    val easyMultiplier: Double = 2.0, // Strong expansion
    val minIntervalDays: Double = 1.0,
    val maxIntervalDays: Double = 180.0,
    val defaultEstimatedDurationMinutes: Int = 25,
    val minDurationMinutes: Int = 10,
    val maxDurationMinutes: Int = 90,
    val minObservationsForHistory: Int = 3
)

/**
 * UI representation of a tracked revision item combining entity data, syllabus details,
 * priority scoring, and transparent explanation.
 */
data class TrackedRevisionUiModel(
    val id: Long,
    val subjectId: Long,
    val subjectName: String,
    val subjectColorHex: String,
    val subjectIconKey: String,
    val chapterId: Long,
    val chapterTitle: String,
    val status: RevisionTrackingStatus,
    val firstTrackedAt: Long,
    val lastReviewedAt: Long?,
    val nextDueAt: Long,
    val nextDueDate: String, // YYYY-MM-DD
    val currentIntervalDays: Double,
    val reviewCount: Int,
    val consecutiveSuccessCount: Int,
    val lastRecallRating: RecallRating?,
    val estimatedDurationMinutes: Int,
    val durationConfidence: DurationConfidence,
    val isDueToday: Boolean,
    val isOverdue: Boolean,
    val daysOverdue: Int,
    val daysUntilDue: Int,
    val priorityScore: Int,
    val priorityReason: String,
    val isNeverReviewed: Boolean
)

/**
 * Summary metrics for the Revision Hub and Home card.
 */
data class RevisionSummaryMetrics(
    val totalTrackedCount: Int = 0,
    val dueTodayCount: Int = 0,
    val overdueCount: Int = 0,
    val upcomingCount: Int = 0,
    val totalEstimatedDurationMinutes: Int = 0,
    val completedReviewsCount: Int = 0,
    val onTimeCompletionRatePercent: Int = 100,
    val hardCount: Int = 0,
    val okayCount: Int = 0,
    val easyCount: Int = 0,
    val nextScheduledDueDate: String? = null
)

/**
 * Proposed revision task for Calendar preview and safe, idempotent commit.
 */
data class ProposedRevisionTask(
    val revisionItemId: Long,
    val chapterId: Long,
    val subjectId: Long,
    val title: String,
    val subjectName: String,
    val date: String, // YYYY-MM-DD
    val estimatedMinutes: Int,
    val priorityReason: String,
    val isAlreadyScheduled: Boolean = false,
    val isSelected: Boolean = true
)

/**
 * Result of a revision review calculation.
 */
data class ReviewCalculationResult(
    val newIntervalDays: Double,
    val nextDueAt: Long,
    val nextDueDate: String,
    val timingStatus: ReviewTimingStatus,
    val explanation: String
)
