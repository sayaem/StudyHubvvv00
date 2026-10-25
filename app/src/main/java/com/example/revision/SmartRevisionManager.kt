package com.example.revision

import com.example.data.local.AdmissionTargetEntity
import com.example.data.local.ChapterEntity
import com.example.data.local.RevisionHistoryEntity
import com.example.data.local.RevisionItemEntity
import com.example.data.local.StudyTaskEntity
import com.example.data.local.SubjectEntity
import com.example.data.repository.StudyHubRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max

/**
 * Smart Revision Manager coordinating the Spaced Repetition Algorithm, duration estimation,
 * priority ranking, calendar proposals, and repository persistence.
 */
class SmartRevisionManager(
    private val repository: StudyHubRepository,
    val algorithm: SpacedRepetitionAlgorithm = SpacedRepetitionAlgorithm(),
    val durationEstimator: RevisionDurationEstimator = RevisionDurationEstimator(),
    val priorityEngine: RevisionPriorityEngine = RevisionPriorityEngine()
) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    /**
     * Flow of fully enriched UI models for tracked revision items.
     * Orders items by priority score (descending), next due date (ascending), and ID.
     */
    val trackedRevisionUiModels: Flow<List<TrackedRevisionUiModel>> = combine(
        repository.activeRevisionItems,
        repository.allSubjects,
        repository.allChapters,
        repository.allRevisionHistory,
        repository.allAdmissionTargets
    ) { items, subjects, chapters, history, targets ->
        val subjectsMap = subjects.associateBy { it.id }
        val chaptersMap = chapters.associateBy { it.id }
        val historyByItem = history.groupBy { it.revisionItemId }
        val currentDateStr = dateFormat.format(Date())

        // Find nearest future exam date across targets
        val nearestExamDateStr = targets
            .mapNotNull { it.examDate?.ifBlank { null } }
            .filter { it >= currentDateStr }
            .minOrNull()

        items.mapNotNull { item ->
            val subject = subjectsMap[item.subjectId] ?: return@mapNotNull null
            val chapter = chaptersMap[item.chapterId] ?: return@mapNotNull null
            val itemHistory = historyByItem[item.id] ?: emptyList()

            val lastRating = item.lastRecallRating?.let {
                try { RecallRating.valueOf(it) } catch (_: Exception) { null }
            }

            // Duration estimation
            val validDurations = itemHistory.map { it.durationSeconds }
            val (estimatedMins, confidence) = durationEstimator.estimateDuration(validDurations)

            // Priority evaluation
            val priority = priorityEngine.evaluatePriority(
                nextDueDateStr = item.nextDueDate,
                reviewCount = item.reviewCount,
                lastRecallRating = lastRating,
                nearestExamDateStr = nearestExamDateStr,
                currentDateStr = currentDateStr
            )

            TrackedRevisionUiModel(
                id = item.id,
                subjectId = item.subjectId,
                subjectName = subject.name,
                subjectColorHex = subject.colorHex,
                subjectIconKey = subject.iconKey,
                chapterId = item.chapterId,
                chapterTitle = chapter.title,
                status = RevisionTrackingStatus.ACTIVE,
                firstTrackedAt = item.firstTrackedAt,
                lastReviewedAt = item.lastReviewedAt,
                nextDueAt = item.nextDueAt,
                nextDueDate = item.nextDueDate,
                currentIntervalDays = item.currentIntervalDays,
                reviewCount = item.reviewCount,
                consecutiveSuccessCount = item.consecutiveSuccessCount,
                lastRecallRating = lastRating,
                estimatedDurationMinutes = estimatedMins,
                durationConfidence = confidence,
                isDueToday = priority.isDueToday,
                isOverdue = priority.isOverdue,
                daysOverdue = priority.daysOverdue,
                daysUntilDue = priority.daysUntilDue,
                priorityScore = priority.score,
                priorityReason = priority.reason,
                isNeverReviewed = item.reviewCount == 0
            )
        }.sortedWith(
            compareByDescending<TrackedRevisionUiModel> { it.priorityScore }
                .thenBy { it.nextDueDate }
                .thenBy { it.id }
        )
    }

    /**
     * Flow of calculated aggregate metrics for the Home card and Revision Hub.
     */
    val summaryMetrics: Flow<RevisionSummaryMetrics> = combine(
        trackedRevisionUiModels,
        repository.allRevisionHistory
    ) { models, history ->
        val dueToday = models.filter { it.isDueToday }
        val overdue = models.filter { it.isOverdue }
        val upcoming = models.filter { !it.isDueToday && !it.isOverdue }

        val totalEstimatedMins = (dueToday + overdue).sumOf { it.estimatedDurationMinutes }
        val totalReviews = history.size

        val onTimeCount = history.count { it.reviewTimingStatus == "ON_TIME" || it.reviewTimingStatus == "EARLY" }
        val onTimePercent = if (totalReviews > 0) ((onTimeCount.toFloat() / totalReviews) * 100).toInt() else 100

        val hardCount = history.count { it.recallRating == "HARD" }
        val okayCount = history.count { it.recallRating == "OKAY" }
        val easyCount = history.count { it.recallRating == "EASY" }

        val nextDueDate = models.filter { !it.isOverdue && !it.isDueToday }
            .minByOrNull { it.nextDueDate }
            ?.nextDueDate

        RevisionSummaryMetrics(
            totalTrackedCount = models.size,
            dueTodayCount = dueToday.size,
            overdueCount = overdue.size,
            upcomingCount = upcoming.size,
            totalEstimatedDurationMinutes = totalEstimatedMins,
            completedReviewsCount = totalReviews,
            onTimeCompletionRatePercent = onTimePercent,
            hardCount = hardCount,
            okayCount = okayCount,
            easyCount = easyCount,
            nextScheduledDueDate = nextDueDate
        )
    }

    /**
     * Tracks a syllabus chapter in the revision engine.
     */
    suspend fun trackChapter(subjectId: Long, chapterId: Long): Long {
        return repository.trackChapterInRevision(subjectId = subjectId, chapterId = chapterId)
    }

    /**
     * Removes a chapter from active revision tracking without deleting syllabus data.
     */
    suspend fun removeChapter(chapterId: Long) {
        repository.removeChapterFromRevision(chapterId)
    }

    /**
     * Postpones an item's due date safely without altering interval multiplier or review count.
     */
    suspend fun postponeItem(itemId: Long, days: Int = 1) {
        val item = repository.getRevisionItemByIdSync(itemId) ?: return
        val (newDueAt, newDueDate) = algorithm.postponeReview(item.nextDueAt, days)
        val updated = item.copy(
            nextDueAt = newDueAt,
            nextDueDate = newDueDate,
            updatedAt = System.currentTimeMillis()
        )
        repository.updateRevisionItem(updated)
    }

    /**
     * Completes a review, calculates the next interval and due date deterministically,
     * updates the item state, and logs the revision history atomically.
     */
    suspend fun completeReview(
        itemId: Long,
        rating: RecallRating,
        durationSeconds: Int = 0,
        focusSessionId: Long? = null,
        notes: String? = null
    ): ReviewCalculationResult? {
        val item = repository.getRevisionItemByIdSync(itemId) ?: return null
        val now = System.currentTimeMillis()
        val todayStr = dateFormat.format(Date(now))

        // Check if there is an upcoming exam for this subject
        val targets = repository.allAdmissionTargets.first()
        val nearestExamDateMillis = targets
            .mapNotNull { it.examDate?.ifBlank { null } }
            .mapNotNull {
                try { dateFormat.parse(it)?.time } catch (_: Exception) { null }
            }
            .filter { it > now }
            .minOrNull()

        val calculation = algorithm.processReview(
            currentIntervalDays = item.currentIntervalDays,
            rating = rating,
            scheduledDueMillis = item.nextDueAt,
            completionMillis = now,
            examDateMillis = nearestExamDateMillis
        )

        val newConsecutiveSuccess = if (rating == RecallRating.HARD) 0 else item.consecutiveSuccessCount + 1

        val updatedItem = item.copy(
            lastReviewedAt = now,
            nextDueAt = calculation.nextDueAt,
            nextDueDate = calculation.nextDueDate,
            currentIntervalDays = calculation.newIntervalDays,
            reviewCount = item.reviewCount + 1,
            consecutiveSuccessCount = newConsecutiveSuccess,
            lastRecallRating = rating.name,
            updatedAt = now
        )

        val historyEntry = RevisionHistoryEntity(
            revisionItemId = item.id,
            completedAt = now,
            completedDate = todayStr,
            recallRating = rating.name,
            previousIntervalDays = item.currentIntervalDays,
            newIntervalDays = calculation.newIntervalDays,
            reviewTimingStatus = calculation.timingStatus.name,
            durationSeconds = durationSeconds,
            focusSessionId = focusSessionId,
            notes = notes?.trim()?.ifBlank { null }
        )

        repository.recordCompletedReview(updatedItem, historyEntry)
        return calculation
    }

    /**
     * Generates a proposal for scheduling due revision items on the Calendar.
     * Respects user's daily revision budget and identifies already-scheduled sessions.
     */
    suspend fun generateCalendarScheduleProposal(
        dailyBudgetMinutes: Int = 60,
        targetDate: String? = null
    ): List<ProposedRevisionTask> {
        val scheduledDate = targetDate ?: dateFormat.format(Date())
        val models = trackedRevisionUiModels.first()
        val existingTasks = repository.getTasksForDate(scheduledDate).first()

        // Filter eligible items: due today or overdue
        val eligible = models.filter { it.isDueToday || it.isOverdue }

        var accumulatedMinutes = 0
        return eligible.map { model ->
            val isAlreadyScheduled = existingTasks.any { task ->
                task.chapterId == model.chapterId && task.date == scheduledDate && task.planType == "REVISION"
            }

            val willFit = (accumulatedMinutes + model.estimatedDurationMinutes) <= dailyBudgetMinutes
            if (!isAlreadyScheduled && willFit) {
                accumulatedMinutes += model.estimatedDurationMinutes
            }

            ProposedRevisionTask(
                revisionItemId = model.id,
                chapterId = model.chapterId,
                subjectId = model.subjectId,
                title = "Revise: ${model.chapterTitle}",
                subjectName = model.subjectName,
                date = scheduledDate,
                estimatedMinutes = model.estimatedDurationMinutes,
                priorityReason = model.priorityReason,
                isAlreadyScheduled = isAlreadyScheduled,
                isSelected = !isAlreadyScheduled && willFit
            )
        }
    }

    /**
     * Safely and idempotently commits approved proposed revision tasks to the Calendar.
     * Prevents duplicate tasks for the same chapter on the same day.
     */
    suspend fun commitProposedTasksToCalendar(proposedTasks: List<ProposedRevisionTask>): Int {
        var committedCount = 0
        for (prop in proposedTasks) {
            if (!prop.isSelected || prop.isAlreadyScheduled) continue

            // Double check existing tasks to guarantee idempotency
            val existing = repository.getTasksForDate(prop.date).first()
            val alreadyExists = existing.any { it.chapterId == prop.chapterId && it.date == prop.date && it.planType == "REVISION" }
            if (alreadyExists) continue

            val task = StudyTaskEntity(
                title = prop.title,
                date = prop.date,
                time = null,
                durationMinutes = prop.estimatedMinutes,
                subjectId = prop.subjectId,
                chapterId = prop.chapterId,
                priority = "HIGH",
                planType = "REVISION",
                notes = "Scheduled via Smart Revision Engine: ${prop.priorityReason}"
            )
            repository.createTask(task)
            committedCount++
        }
        return committedCount
    }
}
