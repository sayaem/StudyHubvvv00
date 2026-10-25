package com.example.notifications

import com.example.data.local.AdmissionTargetEntity
import com.example.data.local.AppSettingsEntity
import com.example.data.local.ChapterEntity
import com.example.data.local.NotificationItemEntity
import com.example.data.local.StudyNoteEntity
import com.example.data.local.StudyTaskEntity
import com.example.data.local.SubjectEntity
import com.example.data.local.UserProfile
import java.util.Calendar
import kotlin.random.Random

object AdaptiveNotificationEngine {

    fun calculateScore(
        template: NotificationTemplate,
        userProfile: UserProfile?,
        subjects: List<SubjectEntity>,
        chapters: List<ChapterEntity>,
        tasks: List<StudyTaskEntity>,
        admissionTargets: List<AdmissionTargetEntity>,
        notes: List<StudyNoteEntity>,
        recentNotifications: List<NotificationItemEntity>,
        currentHour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    ): Double {
        var score = 50.0 // Base score

        // 1. Priority Weight
        score += template.priority * 10.0

        // 2. Admission & Exam Urgency Boost
        if (template.category in listOf(NotificationCategory.EXAM_ALERTS, NotificationCategory.ADMISSION_READINESS)) {
            val hasUpcomingDeadline = admissionTargets.any { target ->
                !target.isArchived && isWithinDays(target.applicationDeadline, 14)
            }
            if (hasUpcomingDeadline) {
                score += 40.0 // Highest priority boost for upcoming deadlines
            }
        }

        // 3. Task & Plan Relevance
        if (template.category in listOf(NotificationCategory.TASKS_PLANNING, NotificationCategory.EXAM_STRATEGY)) {
            val pendingTasksToday = tasks.count { it.status == "PENDING" }
            if (pendingTasksToday > 0) {
                score += 25.0
            }
        }

        // 4. Syllabus & Chapter Progress Boost
        if (template.category in listOf(NotificationCategory.SYLLABUS_CHAPTERS, NotificationCategory.CHAPTER_MASTERY, NotificationCategory.REVISION_SCIENCE)) {
            val uncompletedChapters = chapters.count { it.status != "COMPLETED" }
            if (uncompletedChapters > 0) {
                score += 20.0
            }
        }

        // 5. Time-of-day Alignment with User's Preferred Study Time
        val preferredTime = userProfile?.preferredStudyTime ?: "Morning"
        val timeMatches = when (preferredTime) {
            "Morning" -> currentHour in 6..11
            "Afternoon" -> currentHour in 12..16
            "Evening" -> currentHour in 17..20
            "Night" -> currentHour in 21..23 || currentHour in 0..2
            else -> false
        }
        if (timeMatches) {
            score += 15.0
        }

        // 6. Tone Rotation & Freshness Bonus
        val lastCategory = recentNotifications.firstOrNull()?.category
        if (lastCategory == template.category.name) {
            score -= 25.0 // Tone rotation penalty
        }

        val shownRecently = recentNotifications.take(30).any { it.title == template.title }
        if (!shownRecently) {
            score += 20.0
        } else {
            score -= 40.0 // Repetition penalty
        }

        return score.coerceAtLeast(1.0)
    }

    fun selectBestCandidate(
        templates: List<NotificationTemplate>,
        userProfile: UserProfile?,
        subjects: List<SubjectEntity>,
        chapters: List<ChapterEntity>,
        tasks: List<StudyTaskEntity>,
        admissionTargets: List<AdmissionTargetEntity>,
        notes: List<StudyNoteEntity>,
        recentNotifications: List<NotificationItemEntity>,
        currentHour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    ): NotificationTemplate? {
        if (templates.isEmpty()) return null

        val scoredList = templates.map { template ->
            val score = calculateScore(
                template = template,
                userProfile = userProfile,
                subjects = subjects,
                chapters = chapters,
                tasks = tasks,
                admissionTargets = admissionTargets,
                notes = notes,
                recentNotifications = recentNotifications,
                currentHour = currentHour
            )
            template to score
        }

        val totalScore = scoredList.sumOf { it.second }
        if (totalScore <= 0) return templates.randomOrNull()

        var randomVal = Random.nextDouble() * totalScore
        for ((template, score) in scoredList) {
            randomVal -= score
            if (randomVal <= 0) {
                return template
            }
        }

        return scoredList.maxByOrNull { it.second }?.first
    }

    /**
     * Generates a dynamic, context-aware notification evaluating student data.
     * Evaluated separately from Notification Set items when Notification Set items are exhausted.
     * If neither source has eligible content, returns null (no fabricated notifications).
     */
    fun generateContextualAlgorithmNotification(
        userProfile: UserProfile?,
        subjects: List<SubjectEntity>,
        chapters: List<ChapterEntity>,
        tasks: List<StudyTaskEntity>,
        admissionTargets: List<AdmissionTargetEntity>,
        notes: List<StudyNoteEntity>,
        currentHour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    ): NotificationTemplate? {
        // 1. Admission / Exam deadlines within 30 days
        val activeTargets = admissionTargets.filter { !it.isArchived && isWithinDays(it.applicationDeadline, 30) }
        val nearestTarget = activeTargets.minByOrNull { it.applicationDeadline }
        if (nearestTarget != null) {
            val label = nearestTarget.programUnit?.let { "${nearestTarget.institutionName} ($it)" } ?: nearestTarget.institutionName
            return NotificationTemplate(
                id = "ALGO_ADMISSION_${nearestTarget.id}_$currentHour",
                category = NotificationCategory.ADMISSION_READINESS,
                title = "Deadline Focus: $label 🎯",
                body = "Application deadline is approaching (${nearestTarget.applicationDeadline}). Keep your focus steady this hour!",
                tone = "FOCUSED",
                actionRoute = NotificationActionRoute.CALENDAR,
                priority = 3,
                requiresExamData = true
            )
        }

        // 2. Pending Study Tasks
        val pendingTask = tasks.firstOrNull { it.status == "PENDING" }
        if (pendingTask != null) {
            return NotificationTemplate(
                id = "ALGO_TASK_${pendingTask.id}_$currentHour",
                category = NotificationCategory.TASKS_PLANNING,
                title = "Study Mission: ${pendingTask.title} 📝",
                body = "You have scheduled tasks waiting today. Take 25 minutes this hour to complete '${pendingTask.title}'!",
                tone = "FOCUSED",
                actionRoute = NotificationActionRoute.TASKS,
                priority = 2,
                requiresTasks = true
            )
        }

        // 3. Uncompleted Syllabus Chapters
        val unfinishedChapter = chapters.firstOrNull { it.status != "COMPLETED" }
        if (unfinishedChapter != null) {
            val subjectName = subjects.find { it.id == unfinishedChapter.subjectId }?.name ?: "Subject"
            return NotificationTemplate(
                id = "ALGO_CHAPTER_${unfinishedChapter.id}_$currentHour",
                category = NotificationCategory.CHAPTER_MASTERY,
                title = "Syllabus Step: ${unfinishedChapter.title} 📖",
                body = "Advance your $subjectName preparation by reviewing '${unfinishedChapter.title}' during this hour's study window.",
                tone = "ACADEMIC",
                actionRoute = NotificationActionRoute.SYLLABUS,
                priority = 2,
                requiresChapters = true
            )
        }

        // 4. Study Notes Active Recall
        val studyNote = notes.firstOrNull()
        if (studyNote != null) {
            return NotificationTemplate(
                id = "ALGO_NOTE_${studyNote.id}_$currentHour",
                category = NotificationCategory.NOTES_LEARNING,
                title = "Active Recall: ${studyNote.title} 💡",
                body = "Reinforce what you learned earlier. A 5-minute review of '${studyNote.title}' solidifies long-term retention.",
                tone = "INFORMATIONAL",
                actionRoute = NotificationActionRoute.NOTES,
                priority = 2,
                requiresNotes = true
            )
        }

        // 5. Active Subject Streak & Habit Check-in
        val firstSubject = subjects.firstOrNull()
        if (firstSubject != null || userProfile != null) {
            val subjectHint = firstSubject?.name ?: "studies"
            return NotificationTemplate(
                id = "ALGO_STREAK_$currentHour",
                category = NotificationCategory.STREAKS_PROGRESS,
                title = "Hourly Study Check-in ⏱️",
                body = "Small, consistent study blocks in $subjectHint compound into top scores. Ready for this hour's session?",
                tone = "MOTIVATIONAL",
                actionRoute = NotificationActionRoute.FOCUS,
                priority = 2
            )
        }

        // If no relevant student context exists at all, return null
        return null
    }

    fun isWithinDays(dateStr: String?, daysThreshold: Int): Boolean {
        if (dateStr.isNullOrBlank()) return false
        return try {
            val parts = dateStr.split("-")
            if (parts.size == 3) {
                val cal = Calendar.getInstance()
                val targetCal = Calendar.getInstance().apply {
                    set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt(), 23, 59)
                }
                val diffMs = targetCal.timeInMillis - cal.timeInMillis
                val diffDays = diffMs / (1000 * 60 * 60 * 24)
                diffDays in 0..daysThreshold
            } else false
        } catch (e: Exception) {
            false
        }
    }
}
