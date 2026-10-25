package com.example.planner

import com.example.data.local.AdmissionTargetEntity
import com.example.data.local.ChapterEntity
import com.example.data.local.StudySessionEntity
import com.example.data.local.StudyTaskEntity
import com.example.data.local.SubjectEntity
import com.example.data.local.UserProfile
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

object RuleBasedPlanningEngine {

    private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    data class CandidateTask(
        val title: String,
        val subject: SubjectEntity?,
        val chapter: ChapterEntity?,
        val target: AdmissionTargetEntity?,
        val existingTask: StudyTaskEntity?,
        val planType: String, // "STUDY", "REVISION", "EXAM"
        val examUrgency: Double,
        val remainingWorkload: Double,
        val deadlineUrgency: Double,
        val revisionNeed: Double,
        val priorityScore: Double,
        val priority: String,
        val recommendationReason: String
    )

    /**
     * Generates a realistic, deterministic proposed daily study plan draft
     * based on real student data, priority scoring, self-learned durations, and available calendar slots.
     */
    fun generatePlan(
        targetDate: String,
        subjects: List<SubjectEntity>,
        chapters: List<ChapterEntity>,
        tasks: List<StudyTaskEntity>,
        sessions: List<StudySessionEntity>,
        admissionTargets: List<AdmissionTargetEntity>,
        userProfile: UserProfile?,
        currentTimeMs: Long = System.currentTimeMillis()
    ): PlannerDraft {
        val draftId = "draft_" + UUID.randomUUID().toString().take(8)
        val todayDateStr = DATE_FORMAT.format(Date(currentTimeMs))
        val isTargetToday = targetDate == todayDateStr

        val dailyBudget = userProfile?.dailyGoalMinutes ?: 240
        val existingTasksForDay = tasks.filter { it.date == targetDate && it.status != "CANCELLED" }
        val committedMinutes = existingTasksForDay.sumOf { it.durationMinutes }
        val availableBudgetMinutes = (dailyBudget - committedMinutes).coerceAtLeast(0)

        // If no subjects exist at all, return INSUFFICIENT_DATA
        if (subjects.isEmpty() && tasks.none { it.status == "PENDING" } && admissionTargets.isEmpty()) {
            return PlannerDraft(
                draftId = draftId,
                targetDate = targetDate,
                tasks = emptyList(),
                totalDurationMinutes = 0,
                availableBudgetMinutes = availableBudgetMinutes,
                status = PlannerStatus.INSUFFICIENT_DATA,
                warnings = listOf("No subjects or syllabus chapters found. Add subjects in the Syllabus tab to enable smart planning.")
            )
        }

        // 1. Gather all candidates
        val candidates = mutableListOf<CandidateTask>()

        // A. Existing Pending Tasks with deadlines on or before targetDate
        val pendingTasks = tasks.filter { it.status == "PENDING" && it.date <= targetDate }
        for (task in pendingTasks) {
            val sub = subjects.find { it.id == task.subjectId }
            val ch = chapters.find { it.id == task.chapterId }
            val daysDiff = calculateDaysDifference(targetDate, task.date)
            val deadlineScore = when {
                daysDiff < 0 -> 90.0 // Overdue
                daysDiff == 0 -> 80.0 // Due today
                else -> 60.0
            }
            val score = 0.35 * 20.0 + 0.25 * 50.0 + 0.20 * deadlineScore + 0.20 * 30.0
            val prio = if (score >= 65.0) "HIGH" else if (score >= 40.0) "MEDIUM" else "LOW"
            candidates.add(
                CandidateTask(
                    title = task.title,
                    subject = sub,
                    chapter = ch,
                    target = null,
                    existingTask = task,
                    planType = task.planType,
                    examUrgency = 20.0,
                    remainingWorkload = 50.0,
                    deadlineUrgency = deadlineScore,
                    revisionNeed = 30.0,
                    priorityScore = score,
                    priority = prio,
                    recommendationReason = if (daysDiff < 0) "Overdue task from ${task.date}" else "Scheduled plan pending completion"
                )
            )
        }

        // B. Incomplete Syllabus Chapters linked to upcoming exams or core subjects
        val activeTargets = admissionTargets.filter { !it.isArchived }
        for (subject in subjects) {
            val subjectChapters = chapters.filter { it.subjectId == subject.id }
            val uncompleted = subjectChapters.filter { it.status != "COMPLETED" }
            val completed = subjectChapters.filter { it.status == "COMPLETED" }

            val remainingWorkloadScore = if (subjectChapters.isNotEmpty()) {
                (uncompleted.size.toDouble() / subjectChapters.size.toDouble()) * 100.0
            } else 50.0

            // Check if there is an upcoming exam for this subject or track
            val relevantTarget = activeTargets.minByOrNull {
                calculateDaysDifference(targetDate, it.examDate ?: it.applicationDeadline)
            }
            val daysToExam = relevantTarget?.let {
                calculateDaysDifference(targetDate, it.examDate ?: it.applicationDeadline)
            } ?: 999

            val examUrgencyScore = when {
                daysToExam <= 3 -> 100.0
                daysToExam <= 7 -> 85.0
                daysToExam <= 14 -> 65.0
                daysToExam <= 30 -> 40.0
                else -> 10.0
            }

            // Uncompleted Chapters
            for (chapter in uncompleted.take(3)) {
                // Avoid duplicating if an existing task already has this chapter scheduled on targetDate
                val alreadyScheduled = existingTasksForDay.any { it.chapterId == chapter.id }
                if (alreadyScheduled) continue

                val isInProgress = chapter.status == "IN_PROGRESS"
                val deadlineScore = if (isInProgress) 70.0 else 40.0
                val revisionScore = 20.0

                val score = 0.35 * examUrgencyScore + 0.25 * remainingWorkloadScore + 0.20 * deadlineScore + 0.20 * revisionScore
                val prio = if (score >= 65.0) "HIGH" else if (score >= 40.0) "MEDIUM" else "LOW"

                val reason = when {
                    daysToExam <= 7 && relevantTarget != null ->
                        "${relevantTarget.institutionName} exam in $daysToExam days. Incomplete syllabus chapter."
                    isInProgress ->
                        "Chapter currently in progress. Complete to maintain momentum."
                    else ->
                        "Unfinished chapter in ${subject.name} (${uncompleted.size} remaining)."
                }

                candidates.add(
                    CandidateTask(
                        title = chapter.title,
                        subject = subject,
                        chapter = chapter,
                        target = relevantTarget,
                        existingTask = null,
                        planType = "STUDY",
                        examUrgency = examUrgencyScore,
                        remainingWorkload = remainingWorkloadScore,
                        deadlineUrgency = deadlineScore,
                        revisionNeed = revisionScore,
                        priorityScore = score,
                        priority = prio,
                        recommendationReason = reason
                    )
                )
            }

            // C. Spaced Repetition Revision for Completed Chapters
            // If completed chapters haven't been studied in >= 3 days
            for (chapter in completed.take(2)) {
                val lastSession = sessions.filter { it.chapterId == chapter.id && it.status == "COMPLETED" }
                    .maxByOrNull { it.startedAt }
                val daysSinceSession = if (lastSession != null) {
                    ((currentTimeMs - lastSession.startedAt) / (1000 * 60 * 60 * 24)).toInt()
                } else 10

                if (daysSinceSession >= 3) {
                    val alreadyScheduled = existingTasksForDay.any { it.chapterId == chapter.id }
                    if (alreadyScheduled) continue

                    val revScore = (daysSinceSession * 15.0).coerceAtMost(90.0)
                    val score = 0.35 * examUrgencyScore + 0.25 * 30.0 + 0.20 * 40.0 + 0.20 * revScore
                    val prio = if (score >= 65.0) "HIGH" else if (score >= 40.0) "MEDIUM" else "LOW"

                    candidates.add(
                        CandidateTask(
                            title = "Revision: ${chapter.title}",
                            subject = subject,
                            chapter = chapter,
                            target = relevantTarget,
                            existingTask = null,
                            planType = "REVISION",
                            examUrgency = examUrgencyScore,
                            remainingWorkload = 30.0,
                            deadlineUrgency = 40.0,
                            revisionNeed = revScore,
                            priorityScore = score,
                            priority = prio,
                            recommendationReason = "Spaced repetition due ($daysSinceSession days since last review)."
                        )
                    )
                }
            }
        }

        // Deduplicate candidates by title and chapterId
        val uniqueCandidates = candidates.distinctBy { it.chapter?.id ?: it.title }

        if (uniqueCandidates.isEmpty()) {
            return PlannerDraft(
                draftId = draftId,
                targetDate = targetDate,
                tasks = emptyList(),
                totalDurationMinutes = 0,
                availableBudgetMinutes = availableBudgetMinutes,
                status = PlannerStatus.EMPTY,
                warnings = listOf("All scheduled tasks and syllabus chapters are up to date! Great job.")
            )
        }

        // 2. Sort by Priority Score descending
        val sortedCandidates = uniqueCandidates.sortedByDescending { it.priorityScore }

        // 3. Workload Balancing & Duration Estimation
        val selectedTasks = mutableListOf<ProposedTask>()
        val unscheduled = mutableListOf<UnscheduledTaskInfo>()
        var accumulatedMinutes = 0
        val effectiveBudget = if (availableBudgetMinutes > 0) availableBudgetMinutes else dailyBudget
        val subjectTaskCount = mutableMapOf<Long, Int>()

        for (cand in sortedCandidates) {
            val subId = cand.subject?.id
            val currentCountForSub = if (subId != null) subjectTaskCount.getOrDefault(subId, 0) else 0

            // Workload balancing: max 2 tasks per subject unless exam is <= 3 days away
            if (subId != null && currentCountForSub >= 2 && cand.examUrgency < 90.0) {
                unscheduled.add(
                    UnscheduledTaskInfo(
                        title = cand.title,
                        subjectName = cand.subject.name,
                        reason = "Subject quota reached to maintain balanced study."
                    )
                )
                continue
            }

            // Estimate duration using Self-Learning Engine
            val durationEstimate = SelfLearningDurationEstimator.estimateDuration(
                subjectId = cand.subject?.id,
                chapterId = cand.chapter?.id,
                taskId = cand.existingTask?.id,
                sessions = sessions,
                userDefaultMinutes = cand.existingTask?.durationMinutes ?: 30
            )

            val taskDuration = durationEstimate.durationMinutes

            // Check if task fits in budget
            if (accumulatedMinutes + taskDuration <= effectiveBudget || selectedTasks.isEmpty()) {
                selectedTasks.add(
                    ProposedTask(
                        id = "draft_task_${cand.chapter?.id ?: cand.existingTask?.id ?: cand.title.hashCode()}",
                        title = cand.title,
                        subjectId = cand.subject?.id,
                        subjectName = cand.subject?.name,
                        subjectColorHex = cand.subject?.colorHex,
                        chapterId = cand.chapter?.id,
                        chapterTitle = cand.chapter?.title,
                        targetId = cand.target?.id,
                        targetName = cand.target?.institutionName,
                        durationMinutes = taskDuration,
                        priority = cand.priority,
                        priorityScore = cand.priorityScore,
                        planType = cand.planType,
                        recommendationReason = cand.recommendationReason,
                        durationConfidence = durationEstimate.confidence,
                        durationSource = durationEstimate.sourceDescription
                    )
                )
                accumulatedMinutes += taskDuration
                if (subId != null) {
                    subjectTaskCount[subId] = currentCountForSub + 1
                }
            } else {
                unscheduled.add(
                    UnscheduledTaskInfo(
                        title = cand.title,
                        subjectName = cand.subject?.name,
                        reason = "Exceeds remaining available study budget (${effectiveBudget - accumulatedMinutes}m remaining)."
                    )
                )
            }

            // Max 5 tasks per daily plan to keep daily schedule realistic
            if (selectedTasks.size >= 5) break
        }

        // 4. Scheduling: Assign feasible, non-overlapping time slots
        val scheduledTasks = assignTimeSlots(
            proposed = selectedTasks,
            existingTasks = existingTasksForDay,
            preferredStudyTime = userProfile?.preferredStudyTime ?: "Morning",
            isTargetToday = isTargetToday,
            currentTimeMs = currentTimeMs
        )

        val warnings = mutableListOf<String>()
        if (availableBudgetMinutes < committedMinutes) {
            warnings.add("Existing calendar tasks already use $committedMinutes minutes on $targetDate.")
        }
        if (unscheduled.isNotEmpty()) {
            warnings.add("${unscheduled.size} task(s) left unscheduled to prevent overload.")
        }

        return PlannerDraft(
            draftId = draftId,
            targetDate = targetDate,
            tasks = scheduledTasks,
            totalDurationMinutes = scheduledTasks.sumOf { it.durationMinutes },
            availableBudgetMinutes = availableBudgetMinutes,
            status = PlannerStatus.DRAFT_READY,
            warnings = warnings,
            unscheduledTasks = unscheduled
        )
    }

    /**
     * Assigns feasible, non-overlapping time slots respecting existing calendar entries and user preferences.
     */
    fun assignTimeSlots(
        proposed: List<ProposedTask>,
        existingTasks: List<StudyTaskEntity>,
        preferredStudyTime: String,
        isTargetToday: Boolean,
        currentTimeMs: Long
    ): List<ProposedTask> {
        // Collect occupied intervals on the target date [startMin, endMin]
        val occupied = existingTasks.mapNotNull { task ->
            val time = task.time
            if (!time.isNullOrBlank()) {
                val start = parseTimeToMinutes(time)
                if (start != null) {
                    val end = start + task.durationMinutes
                    start to end
                } else null
            } else null
        }.sortedBy { it.first }.toMutableList()

        // Base start time based on user preferred time of day
        var startMin = when (preferredStudyTime) {
            "Morning" -> 9 * 60     // 09:00
            "Afternoon" -> 14 * 60  // 14:00
            "Evening" -> 17 * 60    // 17:00
            "Night" -> 20 * 60      // 20:00
            else -> 9 * 60
        }

        // If scheduling for today, ensure we never schedule in the past
        if (isTargetToday) {
            val cal = Calendar.getInstance().apply { timeInMillis = currentTimeMs }
            val nowMin = (cal.get(Calendar.HOUR_OF_DAY) * 60) + cal.get(Calendar.MINUTE)
            val minEarliest = nowMin + 15 // At least 15 mins in future
            if (startMin < minEarliest) {
                startMin = roundUpToNextQuarterHour(minEarliest)
            }
        }

        val result = mutableListOf<ProposedTask>()

        for (task in proposed) {
            var taskStart = startMin
            var taskEnd = taskStart + task.durationMinutes
            val breakMins = 10

            // Find next free non-overlapping window
            var conflictFound = true
            while (conflictFound && taskEnd <= 23 * 60) {
                val overlapping = occupied.find { occ ->
                    (taskStart < occ.second && taskEnd > occ.first)
                }
                if (overlapping != null) {
                    // Jump past conflicting event plus a short 10-minute transition break
                    taskStart = roundUpToNextQuarterHour(overlapping.second + breakMins)
                    taskEnd = taskStart + task.durationMinutes
                } else {
                    conflictFound = false
                }
            }

            val startTimeStr = formatMinutesToTime(taskStart)
            val endTimeStr = formatMinutesToTime(taskEnd)

            result.add(
                task.copy(
                    startTime = startTimeStr,
                    endTime = endTimeStr
                )
            )

            // Mark this slot as occupied and prepare start time for next task
            occupied.add(taskStart to taskEnd)
            startMin = taskEnd + breakMins
        }

        return result
    }

    private fun parseTimeToMinutes(timeStr: String): Int? {
        return try {
            val parts = timeStr.trim().split(":")
            if (parts.size >= 2) {
                val h = parts[0].toInt()
                val m = parts[1].toInt()
                (h * 60) + m
            } else null
        } catch (e: Exception) {
            null
        }
    }

    private fun formatMinutesToTime(minutes: Int): String {
        val h = (minutes / 60) % 24
        val m = minutes % 60
        return String.format(Locale.US, "%02d:%02d", h, m)
    }

    private fun roundUpToNextQuarterHour(minutes: Int): Int {
        val remainder = minutes % 15
        return if (remainder == 0) minutes else minutes + (15 - remainder)
    }

    fun calculateDaysDifference(baseDateStr: String, targetDateStr: String?): Int {
        if (targetDateStr.isNullOrBlank()) return 999
        return try {
            val base = DATE_FORMAT.parse(baseDateStr) ?: return 999
            val target = DATE_FORMAT.parse(targetDateStr) ?: return 999
            val diffMs = target.time - base.time
            (diffMs / (1000 * 60 * 60 * 24)).toInt()
        } catch (e: Exception) {
            999
        }
    }
}
