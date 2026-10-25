package com.example.notifications

import android.content.Context
import com.example.data.local.ActiveFocusSessionEntity
import com.example.data.local.AdmissionTargetEntity
import com.example.data.local.AppSettingsEntity
import com.example.data.local.ChapterEntity
import com.example.data.local.FocusGamificationEntity
import com.example.data.local.NotificationItemEntity
import com.example.data.local.StudyNoteEntity
import com.example.data.local.StudySessionEntity
import com.example.data.local.StudyTaskEntity
import com.example.data.local.SubjectEntity
import com.example.data.local.UserProfile
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class CandidateSourceType {
    STUDY_GOAL,
    CALENDAR_PLAN,
    SYLLABUS_REVISION,
    CONSISTENCY_INSIGHT,
    ALGORITHM,
    NOTIFICATION_SET
}

enum class NotificationPriority(val level: Int) {
    HIGH(3),
    MEDIUM(2),
    LOW(1)
}

data class NotificationCandidate(
    val id: String,
    val sourceType: CandidateSourceType,
    val title: String,
    val message: String,
    val category: NotificationCategory,
    val priority: NotificationPriority,
    val relevanceReason: String,
    val deduplicationKey: String,
    val actionRoute: String,
    val timeSensitivityScore: Int = 0,
    val template: NotificationTemplate
)

object SmartNotificationDecisionEngine {

    private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val TIME_FORMAT = SimpleDateFormat("HH:mm", Locale.US)

    /**
     * Centralized decision engine that evaluates all supported sources:
     * 1. Generates smart candidates (Goal, Calendar, Syllabus, Consistency, Algorithm)
     * 2. Ranks by Priority (HIGH > MEDIUM > LOW) and deterministic tie-breaking
     * 3. Selects the highest-priority eligible smart notification
     * 4. If none qualifies, falls back to an unused Notification Set item for today
     * 5. If neither source has eligible content, returns null (skip slot without fabricating)
     */
    fun evaluateCandidateForSlot(
        context: Context,
        userProfile: UserProfile?,
        subjects: List<SubjectEntity>,
        chapters: List<ChapterEntity>,
        tasks: List<StudyTaskEntity>,
        sessions: List<StudySessionEntity>,
        admissionTargets: List<AdmissionTargetEntity>,
        notes: List<StudyNoteEntity>,
        gamification: FocusGamificationEntity?,
        activeSession: ActiveFocusSessionEntity?,
        settings: AppSettingsEntity,
        recentNotifications: List<NotificationItemEntity>,
        deliveredDeduplicationKeys: Set<String>,
        deliveredSetIdsToday: Set<String>,
        currentHour: Int,
        currentTimeMs: Long = System.currentTimeMillis(),
        customNotificationSet: List<NotificationTemplate>? = null
    ): NotificationCandidate? {
        // Respect master switch
        if (!settings.studyRemindersEnabled) return null

        val isFocusActive = activeSession != null && activeSession.sessionState.contains("RUNNING")
        if (isFocusActive) return null

        val todayDateStr = DATE_FORMAT.format(Date(currentTimeMs))
        val dateKey = HourlyNotificationDeliveryEngine.getDateKey(currentTimeMs)

        // 1. Gather Smart Candidates if smart reminders are enabled
        val smartCandidates = mutableListOf<NotificationCandidate>()

        val smartRemindersEnabled = NotificationPreferences.isSmartRemindersEnabled(context)
        if (smartRemindersEnabled) {
            // A. Study Goal Reminders
            if (NotificationPreferences.isGoalRemindersEnabled(context)) {
                smartCandidates.addAll(
                    generateStudyGoalCandidates(
                        userProfile = userProfile,
                        sessions = sessions,
                        todayDateStr = todayDateStr,
                        dateKey = dateKey,
                        currentHour = currentHour,
                        currentTimeMs = currentTimeMs,
                        deliveredKeys = deliveredDeduplicationKeys
                    )
                )
            }

            // B. Calendar-Aware Reminders
            if (NotificationPreferences.isCalendarRemindersEnabled(context)) {
                smartCandidates.addAll(
                    generateCalendarCandidates(
                        tasks = tasks,
                        admissionTargets = admissionTargets,
                        todayDateStr = todayDateStr,
                        dateKey = dateKey,
                        currentHour = currentHour,
                        currentTimeMs = currentTimeMs,
                        deliveredKeys = deliveredDeduplicationKeys
                    )
                )
            }

            // C. Syllabus and Revision Reminders
            if (NotificationPreferences.isSyllabusRemindersEnabled(context)) {
                smartCandidates.addAll(
                    generateSyllabusCandidates(
                        subjects = subjects,
                        chapters = chapters,
                        tasks = tasks,
                        admissionTargets = admissionTargets,
                        todayDateStr = todayDateStr,
                        dateKey = dateKey,
                        deliveredKeys = deliveredDeduplicationKeys
                    )
                )
            }

            // D. Study Consistency Insights
            if (NotificationPreferences.isConsistencyRemindersEnabled(context)) {
                smartCandidates.addAll(
                    generateConsistencyCandidates(
                        gamification = gamification,
                        sessions = sessions,
                        todayDateStr = todayDateStr,
                        dateKey = dateKey,
                        currentHour = currentHour,
                        currentTimeMs = currentTimeMs,
                        deliveredKeys = deliveredDeduplicationKeys
                    )
                )
            }

            // E. Contextual Algorithm Reminders
            val algoTemplate = AdaptiveNotificationEngine.generateContextualAlgorithmNotification(
                userProfile = userProfile,
                subjects = subjects,
                chapters = chapters,
                tasks = tasks,
                admissionTargets = admissionTargets,
                notes = notes,
                currentHour = currentHour
            )
            if (algoTemplate != null) {
                val dedupKey = "ALGO_${algoTemplate.id}_$dateKey"
                if (dedupKey !in deliveredDeduplicationKeys) {
                    val candidate = NotificationCandidate(
                        id = algoTemplate.id,
                        sourceType = CandidateSourceType.ALGORITHM,
                        title = algoTemplate.title,
                        message = algoTemplate.body,
                        category = algoTemplate.category,
                        priority = NotificationPriority.LOW,
                        relevanceReason = "Contextual study nudge generated from student activity",
                        deduplicationKey = dedupKey,
                        actionRoute = algoTemplate.actionRoute.name.lowercase(Locale.ROOT),
                        template = algoTemplate
                    )
                    smartCandidates.add(candidate)
                }
            }
        }

        // 2. Filter smart candidates that are already delivered today or in recent notifications
        val eligibleSmart = smartCandidates.filter { candidate ->
            candidate.deduplicationKey !in deliveredDeduplicationKeys &&
                    !isTitleRecentlyDelivered(candidate.title, recentNotifications, currentTimeMs)
        }

        // 3. Prioritize smart candidates: High > Medium > Low with deterministic tie-breaking
        if (eligibleSmart.isNotEmpty()) {
            val bestSmart = eligibleSmart.sortedWith(
                compareByDescending<NotificationCandidate> { it.priority.level }
                    .thenByDescending { it.timeSensitivityScore }
                    .thenBy { (it.id + currentHour).hashCode() }
            ).first()
            return bestSmart
        }

        // 4. Fallback to Notification Set if smart notification did not qualify
        if (NotificationPreferences.isNotificationSetEnabled(context)) {
            val baseSet = customNotificationSet ?: NotificationContentLibrary.ALL_NOTIFICATIONS
            val unusedSet = baseSet.filter { it.id !in deliveredSetIdsToday }

            val startOfDay = Calendar.getInstance().apply {
                timeInMillis = currentTimeMs
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val todayCount = recentNotifications.count { it.createdAt >= startOfDay }

            val eligibleSet = unusedSet.filter { template ->
                NotificationEligibilityEvaluator.isEligible(
                    template = template,
                    settings = settings,
                    userProfile = userProfile,
                    subjects = subjects,
                    chapters = chapters,
                    tasks = tasks,
                    admissionTargets = admissionTargets,
                    notes = notes,
                    isFocusTimerActive = false,
                    recentNotifications = recentNotifications,
                    todayNotificationCount = todayCount,
                    currentHour = currentHour,
                    currentTimeMs = currentTimeMs
                )
            }

            if (eligibleSet.isNotEmpty()) {
                val selectedTemplate = AdaptiveNotificationEngine.selectBestCandidate(
                    templates = eligibleSet,
                    userProfile = userProfile,
                    subjects = subjects,
                    chapters = chapters,
                    tasks = tasks,
                    admissionTargets = admissionTargets,
                    notes = notes,
                    recentNotifications = recentNotifications,
                    currentHour = currentHour
                )

                if (selectedTemplate != null) {
                    return NotificationCandidate(
                        id = selectedTemplate.id,
                        sourceType = CandidateSourceType.NOTIFICATION_SET,
                        title = selectedTemplate.title,
                        message = selectedTemplate.body,
                        category = selectedTemplate.category,
                        priority = NotificationPriority.LOW,
                        relevanceReason = "Notification Set selection for daily study motivation",
                        deduplicationKey = "SET_${selectedTemplate.id}_$dateKey",
                        actionRoute = selectedTemplate.actionRoute.name.lowercase(Locale.ROOT),
                        template = selectedTemplate
                    )
                }
            }
        }

        // 5. Neither source has eligible content: return null
        return null
    }

    private fun generateStudyGoalCandidates(
        userProfile: UserProfile?,
        sessions: List<StudySessionEntity>,
        todayDateStr: String,
        dateKey: String,
        currentHour: Int,
        currentTimeMs: Long,
        deliveredKeys: Set<String>
    ): List<NotificationCandidate> {
        val candidates = mutableListOf<NotificationCandidate>()
        if (userProfile == null || userProfile.dailyGoalMinutes <= 0) return candidates

        val startOfDay = Calendar.getInstance().apply {
            timeInMillis = currentTimeMs
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val endOfDay = startOfDay + (24 * 3600_000L)

        val todaySessions = sessions.filter {
            it.status == "COMPLETED" && it.startedAt in startOfDay..endOfDay
        }
        val todayFocusMinutes = todaySessions.sumOf { it.elapsedSeconds } / 60
        val targetMinutes = userProfile.dailyGoalMinutes

        // Case 1: Goal Completed (Celebrate once per day)
        if (todayFocusMinutes >= targetMinutes) {
            val key = "GOAL_COMPLETED_$dateKey"
            if (key !in deliveredKeys) {
                candidates.add(
                    NotificationCandidate(
                        id = "GOAL_COMPLETED_${userProfile.id}",
                        sourceType = CandidateSourceType.STUDY_GOAL,
                        title = "Daily Goal Completed! 🎯",
                        message = "Outstanding focus! You reached today's target of ${targetMinutes}m with ${todayFocusMinutes}m logged.",
                        category = NotificationCategory.STREAKS_PROGRESS,
                        priority = NotificationPriority.MEDIUM,
                        relevanceReason = "User completed configured daily study goal",
                        deduplicationKey = key,
                        actionRoute = "analytics",
                        timeSensitivityScore = 60,
                        template = NotificationTemplate(
                            id = "GOAL_COMPLETED_${userProfile.id}",
                            category = NotificationCategory.STREAKS_PROGRESS,
                            title = "Daily Goal Completed! 🎯",
                            body = "Outstanding focus! You reached today's target of ${targetMinutes}m with ${todayFocusMinutes}m logged.",
                            actionRoute = NotificationActionRoute.PROGRESS
                        )
                    )
                )
            }
            return candidates // No incomplete goal nudges when goal is done!
        }

        val remainingMinutes = targetMinutes - todayFocusMinutes

        // Case 2: Almost completed (within 30 mins)
        if (todayFocusMinutes > 0 && remainingMinutes <= 30) {
            val key = "GOAL_CLOSE_$dateKey"
            if (key !in deliveredKeys) {
                candidates.add(
                    NotificationCandidate(
                        id = "GOAL_CLOSE_${userProfile.id}",
                        sourceType = CandidateSourceType.STUDY_GOAL,
                        title = "Almost at Your Daily Goal! ⏳",
                        message = "Only $remainingMinutes minutes left to reach your ${targetMinutes}m daily target. One short sprint will finish it!",
                        category = NotificationCategory.TASKS_PLANNING,
                        priority = NotificationPriority.MEDIUM,
                        relevanceReason = "Within 30 minutes of completing daily study goal",
                        deduplicationKey = key,
                        actionRoute = "focus",
                        timeSensitivityScore = 75,
                        template = NotificationTemplate(
                            id = "GOAL_CLOSE_${userProfile.id}",
                            category = NotificationCategory.TASKS_PLANNING,
                            title = "Almost at Your Daily Goal! ⏳",
                            body = "Only $remainingMinutes minutes left to reach your ${targetMinutes}m daily target. One short sprint will finish it!",
                            actionRoute = NotificationActionRoute.FOCUS
                        )
                    )
                )
            }
        }
        // Case 3: In progress during afternoon/evening
        else if (todayFocusMinutes > 0 && currentHour in 15..21) {
            val key = "GOAL_PROGRESS_$dateKey"
            if (key !in deliveredKeys) {
                candidates.add(
                    NotificationCandidate(
                        id = "GOAL_PROGRESS_${userProfile.id}",
                        sourceType = CandidateSourceType.STUDY_GOAL,
                        title = "Daily Goal in Progress ⏱️",
                        message = "You've studied ${todayFocusMinutes}m of your ${targetMinutes}m goal ($remainingMinutes min remaining). Jump in for a session this hour!",
                        category = NotificationCategory.TASKS_PLANNING,
                        priority = NotificationPriority.MEDIUM,
                        relevanceReason = "Afternoon progress check-in for daily study target",
                        deduplicationKey = key,
                        actionRoute = "focus",
                        timeSensitivityScore = 50,
                        template = NotificationTemplate(
                            id = "GOAL_PROGRESS_${userProfile.id}",
                            category = NotificationCategory.TASKS_PLANNING,
                            title = "Daily Goal in Progress ⏱️",
                            body = "You've studied ${todayFocusMinutes}m of your ${targetMinutes}m goal ($remainingMinutes min remaining). Jump in for a session this hour!",
                            actionRoute = NotificationActionRoute.FOCUS
                        )
                    )
                )
            }
        }
        // Case 4: Not started yet in afternoon (between 2 PM and 8 PM)
        else if (todayFocusMinutes == 0 && currentHour in 14..20) {
            val key = "GOAL_START_$dateKey"
            if (key !in deliveredKeys) {
                candidates.add(
                    NotificationCandidate(
                        id = "GOAL_START_${userProfile.id}",
                        sourceType = CandidateSourceType.STUDY_GOAL,
                        title = "Start Today's Study Goal 📘",
                        message = "Your daily goal is ${targetMinutes} minutes. Even a 25-minute Pomodoro session will give you positive momentum!",
                        category = NotificationCategory.MOTIVATION,
                        priority = NotificationPriority.MEDIUM,
                        relevanceReason = "Daily goal has 0 minutes completed by afternoon",
                        deduplicationKey = key,
                        actionRoute = "focus",
                        timeSensitivityScore = 40,
                        template = NotificationTemplate(
                            id = "GOAL_START_${userProfile.id}",
                            category = NotificationCategory.MOTIVATION,
                            title = "Start Today's Study Goal 📘",
                            body = "Your daily goal is ${targetMinutes} minutes. Even a 25-minute Pomodoro session will give you positive momentum!",
                            actionRoute = NotificationActionRoute.FOCUS
                        )
                    )
                )
            }
        }

        return candidates
    }

    private fun generateCalendarCandidates(
        tasks: List<StudyTaskEntity>,
        admissionTargets: List<AdmissionTargetEntity>,
        todayDateStr: String,
        dateKey: String,
        currentHour: Int,
        currentTimeMs: Long,
        deliveredKeys: Set<String>
    ): List<NotificationCandidate> {
        val candidates = mutableListOf<NotificationCandidate>()

        // 1. Exam Deadlines & Application Deadlines (High Priority)
        val activeTargets = admissionTargets.filter { !it.isArchived }
        for (target in activeTargets) {
            // Exam Date Alert (Within 7 days)
            target.examDate?.let { examDate ->
                val daysUntil = calculateDaysDifference(todayDateStr, examDate)
                if (daysUntil in 0..7) {
                    val key = "EXAM_${target.id}_d$daysUntil"
                    if (key !in deliveredKeys) {
                        val title = if (daysUntil == 0) {
                            "Exam Today: ${target.institutionName} 🚨"
                        } else {
                            "Exam in $daysUntil day${if (daysUntil > 1) "s" else ""}: ${target.institutionName} 🎯"
                        }
                        val unit = target.programUnit?.let { " ($it)" } ?: ""
                        val body = if (daysUntil == 0) {
                            "Today is exam day for ${target.institutionName}$unit. Stay confident and focused!"
                        } else {
                            "${target.institutionName}$unit exam is approaching on $examDate. Review your high-yield chapters today."
                        }
                        candidates.add(
                            NotificationCandidate(
                                id = "EXAM_${target.id}_$daysUntil",
                                sourceType = CandidateSourceType.CALENDAR_PLAN,
                                title = title,
                                message = body,
                                category = NotificationCategory.EXAM_ALERTS,
                                priority = NotificationPriority.HIGH,
                                relevanceReason = "Exam date is within 7 days",
                                deduplicationKey = key,
                                actionRoute = "admission",
                                timeSensitivityScore = 100 - (daysUntil * 5),
                                template = NotificationTemplate(
                                    id = "EXAM_${target.id}_$daysUntil",
                                    category = NotificationCategory.EXAM_ALERTS,
                                    title = title,
                                    body = body,
                                    actionRoute = NotificationActionRoute.CALENDAR
                                )
                            )
                        )
                    }
                }
            }

            // Application Deadline Alert (Within 3 days)
            if (target.applicationStatus != "APPLIED" && target.applicationStatus != "EXAM_COMPLETED") {
                val daysUntilApp = calculateDaysDifference(todayDateStr, target.applicationDeadline)
                if (daysUntilApp in 0..3) {
                    val key = "APP_DEADLINE_${target.id}_d$daysUntilApp"
                    if (key !in deliveredKeys) {
                        val title = "Application Deadline: ${target.institutionName} ⚠️"
                        val body = "Application deadline closes ${if (daysUntilApp == 0) "today" else "in $daysUntilApp days"} (${target.applicationDeadline}). Finalize your submission!"
                        candidates.add(
                            NotificationCandidate(
                                id = "APP_DEADLINE_${target.id}_$daysUntilApp",
                                sourceType = CandidateSourceType.CALENDAR_PLAN,
                                title = title,
                                message = body,
                                category = NotificationCategory.ADMISSION_READINESS,
                                priority = NotificationPriority.HIGH,
                                relevanceReason = "Application deadline is within 3 days",
                                deduplicationKey = key,
                                actionRoute = "admission",
                                timeSensitivityScore = 95 - (daysUntilApp * 5),
                                template = NotificationTemplate(
                                    id = "APP_DEADLINE_${target.id}_$daysUntilApp",
                                    category = NotificationCategory.ADMISSION_READINESS,
                                    title = title,
                                    body = body,
                                    actionRoute = NotificationActionRoute.CALENDAR
                                )
                            )
                        )
                    }
                }
            }
        }

        // 2. Upcoming Scheduled Task Today (Within next 90 minutes)
        val todayTasks = tasks.filter { it.status == "PENDING" && it.date == todayDateStr }
        val nowCal = Calendar.getInstance().apply { timeInMillis = currentTimeMs }
        val currentMinuteOfDay = (nowCal.get(Calendar.HOUR_OF_DAY) * 60) + nowCal.get(Calendar.MINUTE)

        for (task in todayTasks) {
            val taskTime = task.time
            if (!taskTime.isNullOrBlank()) {
                val parsedMinutes = parseTimeToMinutes(taskTime)
                if (parsedMinutes != null) {
                    val diffMinutes = parsedMinutes - currentMinuteOfDay
                    if (diffMinutes in 5..90) {
                        val key = "TASK_UPCOMING_${task.id}_$dateKey"
                        if (key !in deliveredKeys) {
                            val title = "Upcoming Session: ${task.title} 📅"
                            val body = "Scheduled for ${task.time} today (${task.durationMinutes}m). Prepare your study space!"
                            candidates.add(
                                NotificationCandidate(
                                    id = "TASK_UPCOMING_${task.id}",
                                    sourceType = CandidateSourceType.CALENDAR_PLAN,
                                    title = title,
                                    message = body,
                                    category = NotificationCategory.TASKS_PLANNING,
                                    priority = NotificationPriority.HIGH,
                                    relevanceReason = "Scheduled study plan starts within 90 minutes",
                                    deduplicationKey = key,
                                    actionRoute = "calendar",
                                    timeSensitivityScore = 90 - diffMinutes / 2,
                                    template = NotificationTemplate(
                                        id = "TASK_UPCOMING_${task.id}",
                                        category = NotificationCategory.TASKS_PLANNING,
                                        title = title,
                                        body = body,
                                        actionRoute = NotificationActionRoute.CALENDAR
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }

        // 3. Missed Task Needing Reschedule
        val missedTasks = tasks.filter { task ->
            task.status == "PENDING" && (
                task.date < todayDateStr || (
                    task.date == todayDateStr &&
                    task.time != null &&
                    (parseTimeToMinutes(task.time) ?: 0) < (currentMinuteOfDay - 60)
                )
            )
        }
        val firstMissed = missedTasks.maxByOrNull { it.date }
        if (firstMissed != null) {
            val key = "TASK_MISSED_${firstMissed.id}_$dateKey"
            if (key !in deliveredKeys) {
                val title = "Reschedule Missed Plan 📌"
                val body = "'${firstMissed.title}' was scheduled for ${firstMissed.date}. Open your planner to complete or reschedule it."
                candidates.add(
                    NotificationCandidate(
                        id = "TASK_MISSED_${firstMissed.id}",
                        sourceType = CandidateSourceType.CALENDAR_PLAN,
                        title = title,
                        message = body,
                        category = NotificationCategory.TASKS_PLANNING,
                        priority = NotificationPriority.MEDIUM,
                        relevanceReason = "Scheduled plan was missed and may need rescheduling",
                        deduplicationKey = key,
                        actionRoute = "calendar",
                        timeSensitivityScore = 45,
                        template = NotificationTemplate(
                            id = "TASK_MISSED_${firstMissed.id}",
                            category = NotificationCategory.TASKS_PLANNING,
                            title = title,
                            body = body,
                            actionRoute = NotificationActionRoute.CALENDAR
                        )
                    )
                )
            }
        }

        return candidates
    }

    private fun generateSyllabusCandidates(
        subjects: List<SubjectEntity>,
        chapters: List<ChapterEntity>,
        tasks: List<StudyTaskEntity>,
        admissionTargets: List<AdmissionTargetEntity>,
        todayDateStr: String,
        dateKey: String,
        deliveredKeys: Set<String>
    ): List<NotificationCandidate> {
        val candidates = mutableListOf<NotificationCandidate>()

        // 1. Due Revision Tasks
        val revisionTasks = tasks.filter {
            it.planType == "REVISION" && it.status == "PENDING" && it.date <= todayDateStr
        }
        val dueRevision = revisionTasks.firstOrNull()
        if (dueRevision != null) {
            val key = "REVISION_DUE_${dueRevision.id}_$dateKey"
            if (key !in deliveredKeys) {
                val title = "Revision Due: ${dueRevision.title} 🧠"
                val body = "Active recall strengthens long-term retention. Take 20 minutes to review this chapter today."
                candidates.add(
                    NotificationCandidate(
                        id = "REVISION_DUE_${dueRevision.id}",
                        sourceType = CandidateSourceType.SYLLABUS_REVISION,
                        title = title,
                        message = body,
                        category = NotificationCategory.REVISION_SCIENCE,
                        priority = NotificationPriority.MEDIUM,
                        relevanceReason = "Scheduled revision task due today",
                        deduplicationKey = key,
                        actionRoute = "syllabus",
                        timeSensitivityScore = 65,
                        template = NotificationTemplate(
                            id = "REVISION_DUE_${dueRevision.id}",
                            category = NotificationCategory.REVISION_SCIENCE,
                            title = title,
                            body = body,
                            actionRoute = NotificationActionRoute.SYLLABUS
                        )
                    )
                )
            }
        }

        // 2. In-Progress Chapter Continuity
        val inProgressChapter = chapters.firstOrNull { it.status == "IN_PROGRESS" }
        if (inProgressChapter != null) {
            val key = "CHAPTER_IN_PROGRESS_${inProgressChapter.id}_$dateKey"
            if (key !in deliveredKeys) {
                val subName = subjects.find { it.id == inProgressChapter.subjectId }?.name ?: "Syllabus"
                val title = "Continue Chapter: ${inProgressChapter.title} 🔖"
                val body = "You're actively studying '${inProgressChapter.title}' in $subName. Complete a 25-minute session to finish it!"
                candidates.add(
                    NotificationCandidate(
                        id = "CHAPTER_IN_PROGRESS_${inProgressChapter.id}",
                        sourceType = CandidateSourceType.SYLLABUS_REVISION,
                        title = title,
                        message = body,
                        category = NotificationCategory.CHAPTER_MASTERY,
                        priority = NotificationPriority.MEDIUM,
                        relevanceReason = "Chapter currently marked in progress",
                        deduplicationKey = key,
                        actionRoute = "syllabus",
                        timeSensitivityScore = 55,
                        template = NotificationTemplate(
                            id = "CHAPTER_IN_PROGRESS_${inProgressChapter.id}",
                            category = NotificationCategory.CHAPTER_MASTERY,
                            title = title,
                            body = body,
                            actionRoute = NotificationActionRoute.SYLLABUS
                        )
                    )
                )
            }
        }

        // 3. Incomplete Chapters with Upcoming Exam within 30 days
        val nearExam = admissionTargets.firstOrNull { !it.isArchived && calculateDaysDifference(todayDateStr, it.examDate) in 0..30 }
        if (nearExam != null) {
            val uncompletedChapter = chapters.firstOrNull { it.status != "COMPLETED" }
            if (uncompletedChapter != null) {
                val key = "CHAPTER_EXAM_${uncompletedChapter.id}_$dateKey"
                if (key !in deliveredKeys) {
                    val title = "Syllabus Milestone: ${uncompletedChapter.title} 📖"
                    val body = "Prepare for ${nearExam.institutionName} by completing '${uncompletedChapter.title}'. Every chapter boosts your rank!"
                    candidates.add(
                        NotificationCandidate(
                            id = "CHAPTER_EXAM_${uncompletedChapter.id}",
                            sourceType = CandidateSourceType.SYLLABUS_REVISION,
                            title = title,
                            message = body,
                            category = NotificationCategory.SYLLABUS_CHAPTERS,
                            priority = NotificationPriority.MEDIUM,
                            relevanceReason = "Uncompleted chapter with admission exam within 30 days",
                            deduplicationKey = key,
                            actionRoute = "syllabus",
                            timeSensitivityScore = 60,
                            template = NotificationTemplate(
                                id = "CHAPTER_EXAM_${uncompletedChapter.id}",
                                category = NotificationCategory.SYLLABUS_CHAPTERS,
                                title = title,
                                body = body,
                                actionRoute = NotificationActionRoute.SYLLABUS
                            )
                        )
                    )
                }
            }
        }

        return candidates
    }

    private fun generateConsistencyCandidates(
        gamification: FocusGamificationEntity?,
        sessions: List<StudySessionEntity>,
        todayDateStr: String,
        dateKey: String,
        currentHour: Int,
        currentTimeMs: Long,
        deliveredKeys: Set<String>
    ): List<NotificationCandidate> {
        val candidates = mutableListOf<NotificationCandidate>()
        if (gamification == null || gamification.currentStreakDays < 2) return candidates

        val streak = gamification.currentStreakDays

        val startOfDay = Calendar.getInstance().apply {
            timeInMillis = currentTimeMs
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val hasStudiedToday = sessions.any { it.status == "COMPLETED" && it.startedAt >= startOfDay }

        // Streak Protection: afternoon/evening with 0 study sessions today
        if (!hasStudiedToday && currentHour >= 16) {
            val key = "STREAK_PROTECT_${streak}_$dateKey"
            if (key !in deliveredKeys) {
                candidates.add(
                    NotificationCandidate(
                        id = "STREAK_PROTECT_$streak",
                        sourceType = CandidateSourceType.CONSISTENCY_INSIGHT,
                        title = "$streak-Day Streak at Risk! 🔥",
                        message = "You've built $streak consecutive days of study habit. Start a focus session to keep your streak unbroken!",
                        category = NotificationCategory.STREAKS_PROGRESS,
                        priority = NotificationPriority.MEDIUM,
                        relevanceReason = "User has active multi-day streak but has not studied today yet",
                        deduplicationKey = key,
                        actionRoute = "focus",
                        timeSensitivityScore = 70,
                        template = NotificationTemplate(
                            id = "STREAK_PROTECT_$streak",
                            category = NotificationCategory.STREAKS_PROGRESS,
                            title = "$streak-Day Streak at Risk! 🔥",
                            body = "You've built $streak consecutive days of study habit. Start a focus session to keep your streak unbroken!",
                            actionRoute = NotificationActionRoute.FOCUS
                        )
                    )
                )
            }
        } else if (hasStudiedToday) {
            // Streak Celebration: once per day
            val key = "STREAK_CELEBRATE_${streak}_$dateKey"
            if (key !in deliveredKeys) {
                candidates.add(
                    NotificationCandidate(
                        id = "STREAK_CELEBRATE_$streak",
                        sourceType = CandidateSourceType.CONSISTENCY_INSIGHT,
                        title = "$streak-Day Study Streak! 🔥",
                        message = "Great dedication! You've logged focus sessions for $streak consecutive days. Consistency creates mastery.",
                        category = NotificationCategory.STREAKS_PROGRESS,
                        priority = NotificationPriority.LOW,
                        relevanceReason = "User completed study session maintaining streak",
                        deduplicationKey = key,
                        actionRoute = "analytics",
                        timeSensitivityScore = 30,
                        template = NotificationTemplate(
                            id = "STREAK_CELEBRATE_$streak",
                            category = NotificationCategory.STREAKS_PROGRESS,
                            title = "$streak-Day Study Streak! 🔥",
                            body = "Great dedication! You've logged focus sessions for $streak consecutive days. Consistency creates mastery.",
                            actionRoute = NotificationActionRoute.PROGRESS
                        )
                    )
                )
            }
        }

        return candidates
    }

    private fun isTitleRecentlyDelivered(
        title: String,
        recentNotifications: List<NotificationItemEntity>,
        currentTimeMs: Long
    ): Boolean {
        val cutoff12Hours = currentTimeMs - (12 * 3600_000L)
        return recentNotifications.any { it.createdAt >= cutoff12Hours && it.title == title }
    }

    private fun parseTimeToMinutes(timeStr: String): Int? {
        return try {
            val parts = timeStr.trim().split(":")
            if (parts.size >= 2) {
                val hour = parts[0].toInt()
                val minute = parts[1].toInt()
                (hour * 60) + minute
            } else null
        } catch (e: Exception) {
            null
        }
    }

    fun calculateDaysDifference(todayDateStr: String, targetDateStr: String?): Int {
        if (targetDateStr.isNullOrBlank()) return 999
        return try {
            val today = DATE_FORMAT.parse(todayDateStr) ?: return 999
            val target = DATE_FORMAT.parse(targetDateStr) ?: return 999
            val diffMs = target.time - today.time
            (diffMs / (1000 * 60 * 60 * 24)).toInt()
        } catch (e: Exception) {
            999
        }
    }
}
