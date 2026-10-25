package com.example.revision

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Transparent, deterministic Spaced Repetition Algorithm for StudyHub Revision Engine 1.0.
 *
 * This is an evidence-based scheduling heuristic:
 * - Adapts strictly to real recall ratings (Hard, Okay, Easy).
 * - Safe clamping [1 day .. 180 days] prevents zero, negative, or runaway intervals.
 * - Late/overdue reviews are handled conservatively (late days are never rewarded as free interval growth).
 * - Capable of date anchoring and deterministic date arithmetic.
 */
class SpacedRepetitionAlgorithm(
    val policy: SpacedRepetitionPolicy = SpacedRepetitionPolicy()
) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    /**
     * Initializes a new revision item schedule.
     * When first tracked, the topic is not assumed to have decayed memory.
     * The first review is scheduled for the anchor date (default today).
     */
    fun initializeFirstReview(
        anchorDateMillis: Long = System.currentTimeMillis(),
        initialIntervalDays: Double = policy.initialIntervalDays
    ): Pair<Long, String> {
        val safeInterval = safeClampInterval(initialIntervalDays)
        val cal = Calendar.getInstance().apply {
            timeInMillis = anchorDateMillis
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val nextDueMillis = cal.timeInMillis
        val nextDueDateString = dateFormat.format(Date(nextDueMillis))
        return Pair(nextDueMillis, nextDueDateString)
    }

    /**
     * Calculates the new interval, next due date, timing status, and transparent explanation
     * upon completion of a review.
     *
     * @param currentIntervalDays The previous scheduling interval in days (>= 1.0).
     * @param rating Self-reported recall rating (HARD, OKAY, EASY).
     * @param scheduledDueMillis The timestamp when this review was due.
     * @param completionMillis The actual timestamp when the review was completed.
     * @param examDateMillis Optional timestamp of an upcoming verified exam for this subject.
     */
    fun processReview(
        currentIntervalDays: Double,
        rating: RecallRating,
        scheduledDueMillis: Long,
        completionMillis: Long = System.currentTimeMillis(),
        examDateMillis: Long? = null
    ): ReviewCalculationResult {
        val safeCurrentInterval = if (currentIntervalDays < policy.minIntervalDays || currentIntervalDays.isNaN()) {
            policy.initialIntervalDays
        } else {
            currentIntervalDays
        }

        // Determine timing status
        val dueDayStart = getDayStartMillis(scheduledDueMillis)
        val completionDayStart = getDayStartMillis(completionMillis)
        val timingStatus = when {
            completionDayStart > dueDayStart -> ReviewTimingStatus.OVERDUE
            completionDayStart < dueDayStart -> ReviewTimingStatus.EARLY
            else -> ReviewTimingStatus.ON_TIME
        }

        val daysOverdue = max(0, ((completionDayStart - dueDayStart) / (24 * 60 * 60 * 1000L)).toInt())

        // Calculate raw next interval based on rating
        val rawNextInterval = when (rating) {
            RecallRating.HARD -> {
                // Hard reset: regardless of overdue status or prior interval, reset to 1 day
                policy.initialIntervalDays
            }
            RecallRating.OKAY -> {
                // Moderate increase: 1.5x previous interval.
                // Do not multiply based on elapsed overdue days.
                safeCurrentInterval * policy.okayMultiplier
            }
            RecallRating.EASY -> {
                // Solid recall: 2.0x previous interval.
                safeCurrentInterval * policy.easyMultiplier
            }
        }

        // Round to nearest integer day (minimum 1 day)
        var roundedInterval = max(policy.minIntervalDays, roundToWholeDays(rawNextInterval))

        // Exam-aware capping: if an exam is within the window, ensure a review occurs before the exam
        var isExamCapped = false
        if (examDateMillis != null && examDateMillis > completionMillis) {
            val daysUntilExam = ((examDateMillis - completionMillis) / (24 * 60 * 60 * 1000L)).toDouble()
            if (daysUntilExam > 1.0 && roundedInterval >= daysUntilExam) {
                // Cap interval to leave 1-2 days before exam for final review
                val cappedDays = max(1.0, daysUntilExam - 1.0)
                if (cappedDays < roundedInterval) {
                    roundedInterval = cappedDays
                    isExamCapped = true
                }
            }
        }

        val finalInterval = safeClampInterval(roundedInterval)

        // Calculate next due date by adding interval days to completion date
        val nextCal = Calendar.getInstance().apply {
            timeInMillis = completionMillis
            add(Calendar.DAY_OF_YEAR, finalInterval.toInt())
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val nextDueMillis = nextCal.timeInMillis
        val nextDueDateString = dateFormat.format(Date(nextDueMillis))

        // Generate explainable reason
        val explanation = buildString {
            when (rating) {
                RecallRating.HARD -> {
                    append("Rated Hard — interval reset to 1 day for prompt reinforcement.")
                }
                RecallRating.OKAY -> {
                    append("Rated Okay — interval expanded by 1.5x to ${finalInterval.toInt()} days.")
                }
                RecallRating.EASY -> {
                    append("Rated Easy — interval doubled to ${finalInterval.toInt()} days for long-term retention.")
                }
            }
            if (timingStatus == ReviewTimingStatus.OVERDUE) {
                append(" (Completed $daysOverdue ${if (daysOverdue == 1) "day" else "days"} late)")
            }
            if (isExamCapped) {
                append(" Adjusted to fit before upcoming exam.")
            }
        }

        return ReviewCalculationResult(
            newIntervalDays = finalInterval,
            nextDueAt = nextDueMillis,
            nextDueDate = nextDueDateString,
            timingStatus = timingStatus,
            explanation = explanation
        )
    }

    /**
     * Postpones a review by a specified number of days (e.g. 1 day).
     * Strictly does NOT count as a review or increase interval multiplier.
     */
    fun postponeReview(
        currentDueMillis: Long,
        postponeDays: Int = 1
    ): Pair<Long, String> {
        val daysToAdd = max(1, postponeDays)
        val cal = Calendar.getInstance().apply {
            timeInMillis = currentDueMillis
            add(Calendar.DAY_OF_YEAR, daysToAdd)
        }
        val newDueMillis = cal.timeInMillis
        val newDueDateString = dateFormat.format(Date(newDueMillis))
        return Pair(newDueMillis, newDueDateString)
    }

    /**
     * Evaluates whether an item is due today or overdue compared to reference date.
     */
    fun evaluateDueStatus(
        nextDueDateString: String,
        currentDateString: String = getCurrentDateString()
    ): Triple<Boolean, Boolean, Int> {
        val diffDays = calculateDayDifference(currentDateString, nextDueDateString)
        val isDueToday = diffDays == 0
        val isOverdue = diffDays > 0 // nextDueDate < currentDate
        val daysOverdue = if (isOverdue) diffDays else 0
        return Triple(isDueToday, isOverdue, daysOverdue)
    }

    fun getCurrentDateString(): String = dateFormat.format(Date())

    private fun safeClampInterval(interval: Double): Double {
        if (interval.isNaN() || interval.isInfinite()) return policy.initialIntervalDays
        return interval.coerceIn(policy.minIntervalDays, policy.maxIntervalDays)
    }

    private fun roundToWholeDays(value: Double): Double {
        return value.roundToInt().toDouble()
    }

    private fun getDayStartMillis(timestamp: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    private fun calculateDayDifference(currentDateStr: String, targetDateStr: String): Int {
        return try {
            val current = dateFormat.parse(currentDateStr)?.time ?: return 0
            val target = dateFormat.parse(targetDateStr)?.time ?: return 0
            ((current - target) / (24 * 60 * 60 * 1000L)).toInt()
        } catch (_: Exception) {
            0
        }
    }
}
