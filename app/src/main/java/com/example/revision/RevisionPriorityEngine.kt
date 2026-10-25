package com.example.revision

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max

/**
 * Predictable, deterministic priority scoring engine for revision topics.
 *
 * Rule Hierarchy:
 * 1. Overdue topics (Score: 1000 + daysOverdue * 50)
 * 2. Due today with previous Hard rating (Score: 800)
 * 3. Due today with verified upcoming exam within 14 days (Score: 700)
 * 4. Due today standard (Score: 500)
 * 5. First review needed (never reviewed) (Score: 400)
 * 6. Upcoming within 3 days (Score: 200 - daysUntilDue * 50)
 * 7. Long-term scheduled (Score: 50)
 */
class RevisionPriorityEngine {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    data class PriorityEvaluation(
        val score: Int,
        val reason: String,
        val isDueToday: Boolean,
        val isOverdue: Boolean,
        val daysOverdue: Int,
        val daysUntilDue: Int
    )

    /**
     * Evaluates the priority score and transparent explanation for a revision item.
     */
    fun evaluatePriority(
        nextDueDateStr: String,
        reviewCount: Int,
        lastRecallRating: RecallRating?,
        nearestExamDateStr: String? = null,
        currentDateStr: String = dateFormat.format(Date())
    ): PriorityEvaluation {
        val diffDays = calculateDayDifference(currentDateStr, nextDueDateStr)
        val isDueToday = diffDays == 0
        val isOverdue = diffDays > 0 // nextDueDate < currentDate
        val daysOverdue = if (isOverdue) diffDays else 0
        val daysUntilDue = if (diffDays < 0) -diffDays else 0

        val daysUntilExam = nearestExamDateStr?.let {
            val examDiff = calculateDayDifference(currentDateStr, it)
            if (examDiff <= 0) -examDiff else null // Future exam date
        }

        var score = 0
        val reason: String

        when {
            isOverdue -> {
                score = 1000 + (daysOverdue * 50)
                reason = if (daysOverdue == 1) "1 day overdue — priority catchup" else "$daysOverdue days overdue — priority catchup"
            }
            isDueToday && lastRecallRating == RecallRating.HARD -> {
                score = 800
                reason = "Due today • Previous review rated Hard (needs reinforcement)"
            }
            isDueToday && daysUntilExam != null && daysUntilExam <= 14 -> {
                score = 700
                reason = "Due today • Exam approaching in $daysUntilExam days"
            }
            isDueToday -> {
                score = 500
                reason = if (reviewCount == 0) "First review due today" else "Scheduled for review today"
            }
            reviewCount == 0 -> {
                score = 400
                reason = "First review needed (not yet reviewed)"
            }
            daysUntilDue in 1..3 -> {
                score = 200 - (daysUntilDue * 50)
                reason = "Upcoming in $daysUntilDue ${if (daysUntilDue == 1) "day" else "days"}"
            }
            else -> {
                score = 50
                reason = "Next review on $nextDueDateStr"
            }
        }

        return PriorityEvaluation(
            score = score,
            reason = reason,
            isDueToday = isDueToday,
            isOverdue = isOverdue,
            daysOverdue = daysOverdue,
            daysUntilDue = daysUntilDue
        )
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
