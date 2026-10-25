package com.example.revision

import kotlin.math.roundToInt

/**
 * Historical Revision Duration Estimator for StudyHub.
 *
 * Deterministic, robust statistics:
 * - Uses median of actual completed revision sessions for robust outlier resistance.
 * - Filters out invalid (< 60s) or implausibly long (> 2h) sessions.
 * - Distinguishes confidence tiers:
 *   - BASELINE (0 sessions): 25 mins fallback.
 *   - EARLY_ESTIMATE (1-2 sessions): blended early heuristic.
 *   - HISTORY_BACKED (3+ sessions): real statistical median.
 */
class RevisionDurationEstimator(
    private val policy: SpacedRepetitionPolicy = SpacedRepetitionPolicy()
) {

    /**
     * Estimates revision duration in minutes and assigns the appropriate confidence level.
     *
     * @param validDurationsSeconds List of completed session durations in seconds for this item or subject.
     */
    fun estimateDuration(validDurationsSeconds: List<Int>): Pair<Int, DurationConfidence> {
        // Filter plausible durations: between 1 minute (60s) and 2 hours (7200s)
        val filtered = validDurationsSeconds.filter { it in 60..7200 }

        return when {
            filtered.size >= policy.minObservationsForHistory -> {
                val medianSec = calculateMedian(filtered)
                val minutes = (medianSec / 60.0).roundToInt()
                val clampedMinutes = minutes.coerceIn(policy.minDurationMinutes, policy.maxDurationMinutes)
                Pair(clampedMinutes, DurationConfidence.HISTORY_BACKED)
            }
            filtered.isNotEmpty() -> {
                // 1 or 2 observations: blend with baseline (25 mins)
                val avgObservedMin = filtered.average() / 60.0
                val blended = (avgObservedMin * filtered.size + policy.defaultEstimatedDurationMinutes * (3 - filtered.size)) / 3.0
                val clampedMinutes = blended.roundToInt().coerceIn(policy.minDurationMinutes, policy.maxDurationMinutes)
                Pair(clampedMinutes, DurationConfidence.EARLY_ESTIMATE)
            }
            else -> {
                Pair(policy.defaultEstimatedDurationMinutes, DurationConfidence.BASELINE)
            }
        }
    }

    private fun calculateMedian(values: List<Int>): Double {
        if (values.isEmpty()) return policy.defaultEstimatedDurationMinutes * 60.0
        val sorted = values.sorted()
        val middle = sorted.size / 2
        return if (sorted.size % 2 == 1) {
            sorted[middle].toDouble()
        } else {
            (sorted[middle - 1] + sorted[middle]) / 2.0
        }
    }
}
