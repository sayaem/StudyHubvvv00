package com.example.ui.screens.stats

import com.example.data.local.AdmissionTargetEntity
import com.example.data.local.ChapterEntity
import com.example.data.local.FocusGamificationEntity
import com.example.data.local.StudySessionEntity
import com.example.data.local.StudyTaskEntity
import com.example.data.local.SubjectEntity
import com.example.data.local.UserProfile
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

enum class DateRangeFilter(val label: String, val days: Int) {
    TODAY("Today", 1),
    SEVEN_DAYS("7 days", 7),
    THIRTY_DAYS("30 days", 30),
    ALL_TIME("All time", 0)
}

data class DailyStudyPoint(
    val dateLabel: String,
    val fullDateStr: String,
    val minutes: Int,
    val sessionCount: Int,
    val timestamp: Long
)

data class PeriodOverview(
    val totalMinutes: Int,
    val completedSessionsCount: Int,
    val dailyAverageMinutes: Int,
    val avgSessionDurationMinutes: Int,
    val goalCompletionPercent: Int,
    val goalsMetDays: Int,
    val totalDaysInPeriod: Int,
    val previousPeriodMinutes: Int,
    val deltaMinutesText: String?,
    val deltaPercentText: String?,
    val isPositiveDelta: Boolean
)

data class ConsistencyHeatmapDay(
    val dateStr: String,
    val dayOfWeek: Int, // Calendar.SUNDAY .. Calendar.SATURDAY
    val dayNumber: Int,
    val minutes: Int,
    val intensityLevel: Int, // 0 to 4
    val isToday: Boolean
)

data class SubjectAnalytics(
    val subject: SubjectEntity,
    val totalMinutes: Int,
    val sessionCount: Int,
    val avgDurationMinutes: Int,
    val sharePercent: Float,
    val completedChapters: Int,
    val totalChapters: Int,
    val chapterCompletionPercent: Int,
    val plannedMinutes: Int,
    val recommendation: String
)

data class FocusSessionBreakdown(
    val completedCount: Int,
    val abandonedCount: Int,
    val completionRatePercent: Int,
    val totalDistractions: Int,
    val avgRating: Float,
    val timeOfDayDistribution: Map<String, Int>, // "Morning", "Afternoon", "Evening", "Night" -> minutes
    val sessionTypeDistribution: Map<String, Int> // "Pomodoro", "Deep Focus", "Stopwatch", "Timer" -> minutes
)

data class PlannedVsActualAnalysis(
    val plannedMinutes: Int,
    val actualMinutes: Int,
    val plannedTasksCount: Int,
    val completedTasksCount: Int,
    val taskCompletionRatePercent: Int,
    val adherencePercent: Int,
    val summaryMessage: String
)

enum class InsightType {
    ACHIEVEMENT,
    STREAK,
    ATTENTION_NEEDED,
    TIME_PATTERN,
    READINESS
}

data class SmartStudyInsight(
    val id: String,
    val title: String,
    val description: String,
    val type: InsightType,
    val actionLabel: String? = null,
    val actionRoute: String? = null
)

data class ExamReadinessItem(
    val target: AdmissionTargetEntity,
    val daysRemaining: Long,
    val isUpcoming: Boolean,
    val statusText: String
)

object StatsAnalyticsEngine {

    fun formatDuration(totalMinutes: Int): String {
        if (totalMinutes <= 0) return "0m"
        val h = totalMinutes / 60
        val m = totalMinutes % 60
        return when {
            h > 0 && m > 0 -> "${h}h ${m}m"
            h > 0 -> "${h}h"
            else -> "${m}m"
        }
    }

    fun calculatePeriodOverview(
        filter: DateRangeFilter,
        sessions: List<StudySessionEntity>,
        userProfile: UserProfile?,
        referenceTimeMillis: Long = System.currentTimeMillis()
    ): PeriodOverview {
        val dailyGoalMinutes = userProfile?.dailyGoalMinutes?.takeIf { it > 0 } ?: 240
        val cal = Calendar.getInstance().apply { timeInMillis = referenceTimeMillis }

        // Start and end of current period
        val bounds = getPeriodBounds(filter, cal)
        val curStart = bounds.curStart
        val curEnd = bounds.curEnd
        val prevStart = bounds.prevStart
        val prevEnd = bounds.prevEnd
        val daysInPeriod = bounds.days

        val currentCompleted = sessions.filter {
            it.status == "COMPLETED" && it.startedAt in curStart..curEnd
        }
        val totalMinutes = currentCompleted.sumOf { it.elapsedSeconds } / 60
        val completedSessionsCount = currentCompleted.size
        val avgSessionDuration = if (completedSessionsCount > 0) totalMinutes / completedSessionsCount else 0

        val effectiveDays = if (filter == DateRangeFilter.ALL_TIME) {
            val earliest = sessions.minOfOrNull { it.startedAt } ?: curStart
            val diff = TimeUnit.MILLISECONDS.toDays(curEnd - earliest).toInt() + 1
            diff.coerceAtLeast(1)
        } else {
            daysInPeriod.coerceAtLeast(1)
        }

        val dailyAvg = totalMinutes / effectiveDays

        // Daily goals met in current period
        val dayFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val minutesPerDayMap = currentCompleted.groupBy {
            dayFormat.format(Date(it.startedAt))
        }.mapValues { (_, daySessions) ->
            daySessions.sumOf { it.elapsedSeconds } / 60
        }

        val goalsMetDays = minutesPerDayMap.count { it.value >= dailyGoalMinutes }
        val goalCompletionPercent = if (effectiveDays > 0) {
            ((goalsMetDays.toFloat() / effectiveDays.toFloat()) * 100).toInt().coerceIn(0, 100)
        } else 0

        // Previous period comparison
        var deltaMinutesText: String? = null
        var deltaPercentText: String? = null
        var isPositiveDelta = true
        var prevTotalMinutes = 0

        if (filter != DateRangeFilter.ALL_TIME && prevStart > 0) {
            val prevCompleted = sessions.filter {
                it.status == "COMPLETED" && it.startedAt in prevStart..prevEnd
            }
            prevTotalMinutes = prevCompleted.sumOf { it.elapsedSeconds } / 60
            val diffMinutes = totalMinutes - prevTotalMinutes
            isPositiveDelta = diffMinutes >= 0

            val absDiff = kotlin.math.abs(diffMinutes)
            val diffFormatted = formatDuration(absDiff)

            val periodLabel = when (filter) {
                DateRangeFilter.TODAY -> "yesterday"
                DateRangeFilter.SEVEN_DAYS -> "last week"
                DateRangeFilter.THIRTY_DAYS -> "previous 30 days"
                DateRangeFilter.ALL_TIME -> ""
            }

            deltaMinutesText = if (diffMinutes >= 0) {
                "$diffFormatted more than $periodLabel"
            } else {
                "$diffFormatted less than $periodLabel"
            }

            if (prevTotalMinutes > 0) {
                val pct = ((diffMinutes.toFloat() / prevTotalMinutes.toFloat()) * 100).toInt()
                deltaPercentText = if (pct >= 0) "+$pct%" else "$pct%"
            } else if (totalMinutes > 0) {
                deltaPercentText = "+100%"
            }
        }

        return PeriodOverview(
            totalMinutes = totalMinutes,
            completedSessionsCount = completedSessionsCount,
            dailyAverageMinutes = dailyAvg,
            avgSessionDurationMinutes = avgSessionDuration,
            goalCompletionPercent = goalCompletionPercent,
            goalsMetDays = goalsMetDays,
            totalDaysInPeriod = effectiveDays,
            previousPeriodMinutes = prevTotalMinutes,
            deltaMinutesText = deltaMinutesText,
            deltaPercentText = deltaPercentText,
            isPositiveDelta = isPositiveDelta
        )
    }

    private data class Bounds(
        val curStart: Long,
        val curEnd: Long,
        val prevStart: Long,
        val prevEnd: Long,
        val days: Int
    )

    private fun getPeriodBounds(filter: DateRangeFilter, refCal: Calendar): Bounds {
        val endCal = (refCal.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        val curEnd = endCal.timeInMillis

        return when (filter) {
            DateRangeFilter.TODAY -> {
                val startCal = (refCal.clone() as Calendar).apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val prevStartCal = (startCal.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -1) }
                val prevEndCal = (endCal.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -1) }
                Bounds(startCal.timeInMillis, curEnd, prevStartCal.timeInMillis, prevEndCal.timeInMillis, 1)
            }
            DateRangeFilter.SEVEN_DAYS -> {
                val startCal = (refCal.clone() as Calendar).apply {
                    add(Calendar.DAY_OF_YEAR, -6)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val prevStartCal = (startCal.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -7) }
                val prevEndCal = (startCal.clone() as Calendar).apply { add(Calendar.MILLISECOND, -1) }
                Bounds(startCal.timeInMillis, curEnd, prevStartCal.timeInMillis, prevEndCal.timeInMillis, 7)
            }
            DateRangeFilter.THIRTY_DAYS -> {
                val startCal = (refCal.clone() as Calendar).apply {
                    add(Calendar.DAY_OF_YEAR, -29)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val prevStartCal = (startCal.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -30) }
                val prevEndCal = (startCal.clone() as Calendar).apply { add(Calendar.MILLISECOND, -1) }
                Bounds(startCal.timeInMillis, curEnd, prevStartCal.timeInMillis, prevEndCal.timeInMillis, 30)
            }
            DateRangeFilter.ALL_TIME -> {
                Bounds(0L, curEnd, 0L, 0L, 0)
            }
        }
    }

    fun getDailyStudyPoints(
        filter: DateRangeFilter,
        sessions: List<StudySessionEntity>,
        referenceTimeMillis: Long = System.currentTimeMillis()
    ): List<DailyStudyPoint> {
        val completed = sessions.filter { it.status == "COMPLETED" }
        val dayLabelFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val fullDateFormat = SimpleDateFormat("MMM d", Locale.getDefault())

        val numDays = when (filter) {
            DateRangeFilter.TODAY -> 1
            DateRangeFilter.SEVEN_DAYS -> 7
            DateRangeFilter.THIRTY_DAYS -> 30
            DateRangeFilter.ALL_TIME -> 14
        }

        val result = mutableListOf<DailyStudyPoint>()
        val cal = Calendar.getInstance().apply {
            timeInMillis = referenceTimeMillis
            add(Calendar.DAY_OF_YEAR, -(numDays - 1))
        }

        for (i in 0 until numDays) {
            val startOfDay = (cal.clone() as Calendar).apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val endOfDay = (cal.clone() as Calendar).apply {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis

            val daySessions = completed.filter { it.startedAt in startOfDay..endOfDay }
            val dayMinutes = daySessions.sumOf { it.elapsedSeconds } / 60
            val label = if (numDays <= 7) dayLabelFormat.format(cal.time) else cal.get(Calendar.DAY_OF_MONTH).toString()

            result.add(
                DailyStudyPoint(
                    dateLabel = label,
                    fullDateStr = fullDateFormat.format(cal.time),
                    minutes = dayMinutes,
                    sessionCount = daySessions.size,
                    timestamp = cal.timeInMillis
                )
            )
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }

        return result
    }

    fun generateConsistencyHeatmap(
        sessions: List<StudySessionEntity>,
        daysCount: Int = 28,
        referenceTimeMillis: Long = System.currentTimeMillis()
    ): List<ConsistencyHeatmapDay> {
        val completed = sessions.filter { it.status == "COMPLETED" }
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance().apply {
            timeInMillis = referenceTimeMillis
            add(Calendar.DAY_OF_YEAR, -(daysCount - 1))
        }

        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(referenceTimeMillis))
        val list = mutableListOf<ConsistencyHeatmapDay>()

        for (i in 0 until daysCount) {
            val start = (cal.clone() as Calendar).apply {
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            val end = (cal.clone() as Calendar).apply {
                set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(Calendar.SECOND, 59); set(Calendar.MILLISECOND, 999)
            }.timeInMillis

            val dayMinutes = completed.filter { it.startedAt in start..end }.sumOf { it.elapsedSeconds } / 60
            val dateStr = dateFormat.format(cal.time)

            val intensity = when {
                dayMinutes == 0 -> 0
                dayMinutes < 30 -> 1
                dayMinutes < 60 -> 2
                dayMinutes < 120 -> 3
                else -> 4
            }

            list.add(
                ConsistencyHeatmapDay(
                    dateStr = dateStr,
                    dayOfWeek = cal.get(Calendar.DAY_OF_WEEK),
                    dayNumber = cal.get(Calendar.DAY_OF_MONTH),
                    minutes = dayMinutes,
                    intensityLevel = intensity,
                    isToday = dateStr == todayStr
                )
            )
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return list
    }

    fun calculateSubjectIntelligence(
        filter: DateRangeFilter,
        sessions: List<StudySessionEntity>,
        subjects: List<SubjectEntity>,
        chapters: List<ChapterEntity>,
        tasks: List<StudyTaskEntity>,
        referenceTimeMillis: Long = System.currentTimeMillis()
    ): List<SubjectAnalytics> {
        val cal = Calendar.getInstance().apply { timeInMillis = referenceTimeMillis }
        val bounds = getPeriodBounds(filter, cal)
        val curStart = bounds.curStart
        val curEnd = bounds.curEnd

        val relevantSessions = sessions.filter {
            it.status == "COMPLETED" && (filter == DateRangeFilter.ALL_TIME || it.startedAt in curStart..curEnd)
        }
        val totalPeriodMinutes = relevantSessions.sumOf { it.elapsedSeconds } / 60

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val curStartStr = dateFormat.format(Date(curStart))
        val curEndStr = dateFormat.format(Date(curEnd))

        return subjects.map { sub ->
            val subSessions = relevantSessions.filter { it.subjectId == sub.id }
            val totalMinutes = subSessions.sumOf { it.elapsedSeconds } / 60
            val sessionCount = subSessions.size
            val avgDuration = if (sessionCount > 0) totalMinutes / sessionCount else 0
            val sharePercent = if (totalPeriodMinutes > 0) (totalMinutes.toFloat() / totalPeriodMinutes.toFloat()) * 100f else 0f

            val subChapters = chapters.filter { it.subjectId == sub.id }
            val completedChapters = subChapters.count { it.status == "COMPLETED" }
            val totalChapters = subChapters.size
            val chapterPercent = if (totalChapters > 0) ((completedChapters.toFloat() / totalChapters.toFloat()) * 100).toInt() else 0

            val subTasks = tasks.filter {
                it.subjectId == sub.id && (filter == DateRangeFilter.ALL_TIME || (it.date in curStartStr..curEndStr))
            }
            val plannedMinutes = subTasks.sumOf { it.durationMinutes }

            val recommendation = when {
                totalMinutes == 0 && subTasks.isNotEmpty() -> "Scheduled tasks waiting: start a focus session to stay on track."
                totalMinutes == 0 -> "No time logged recently. Review chapters to maintain syllabus balance."
                chapterPercent == 100 -> "All chapters completed! Excellent work — practice revision & test papers."
                totalMinutes >= 180 && chapterPercent < 40 -> "High study time logged. Check off finished chapters in Syllabus."
                totalMinutes < plannedMinutes -> "Studied ${formatDuration(totalMinutes)} of ${formatDuration(plannedMinutes)} planned. Catch up on remaining tasks."
                else -> "Consistent progress! Keep reviewing upcoming chapters."
            }

            SubjectAnalytics(
                subject = sub,
                totalMinutes = totalMinutes,
                sessionCount = sessionCount,
                avgDurationMinutes = avgDuration,
                sharePercent = sharePercent,
                completedChapters = completedChapters,
                totalChapters = totalChapters,
                chapterCompletionPercent = chapterPercent,
                plannedMinutes = plannedMinutes,
                recommendation = recommendation
            )
        }.sortedByDescending { it.totalMinutes }
    }

    fun calculateFocusSessionBreakdown(
        filter: DateRangeFilter,
        sessions: List<StudySessionEntity>,
        referenceTimeMillis: Long = System.currentTimeMillis()
    ): FocusSessionBreakdown {
        val cal = Calendar.getInstance().apply { timeInMillis = referenceTimeMillis }
        val bounds = getPeriodBounds(filter, cal)
        val curStart = bounds.curStart
        val curEnd = bounds.curEnd

        val periodSessions = sessions.filter {
            filter == DateRangeFilter.ALL_TIME || it.startedAt in curStart..curEnd
        }

        val completedCount = periodSessions.count { it.status == "COMPLETED" }
        val abandonedCount = periodSessions.count { it.status == "ABANDONED" || it.status == "ENDED_EARLY" }
        val totalStarted = completedCount + abandonedCount
        val completionRate = if (totalStarted > 0) ((completedCount.toFloat() / totalStarted.toFloat()) * 100).toInt() else 100

        val totalDistractions = periodSessions.sumOf { it.distractions }
        val ratedSessions = periodSessions.filter { it.reflectionRating in 1..5 }
        val avgRating = if (ratedSessions.isNotEmpty()) {
            ratedSessions.map { it.reflectionRating }.average().toFloat()
        } else 5.0f

        val timeOfDayMap = mutableMapOf("Morning" to 0, "Afternoon" to 0, "Evening" to 0, "Night" to 0)
        val typeMap = mutableMapOf("Pomodoro" to 0, "Deep Focus" to 0, "Timer" to 0, "Stopwatch" to 0)

        val hourCal = Calendar.getInstance()
        periodSessions.filter { it.status == "COMPLETED" }.forEach { session ->
            hourCal.timeInMillis = session.startedAt
            val hour = hourCal.get(Calendar.HOUR_OF_DAY)
            val sessionMinutes = session.elapsedSeconds / 60

            val slot = when {
                hour in 5..11 -> "Morning"
                hour in 12..16 -> "Afternoon"
                hour in 17..20 -> "Evening"
                else -> "Night"
            }
            timeOfDayMap[slot] = (timeOfDayMap[slot] ?: 0) + sessionMinutes

            val friendlyType = when (session.sessionType) {
                "POMODORO_FOCUS" -> "Pomodoro"
                "DEEP_FOCUS" -> "Deep Focus"
                "STOPWATCH" -> "Stopwatch"
                else -> "Timer"
            }
            typeMap[friendlyType] = (typeMap[friendlyType] ?: 0) + sessionMinutes
        }

        return FocusSessionBreakdown(
            completedCount = completedCount,
            abandonedCount = abandonedCount,
            completionRatePercent = completionRate,
            totalDistractions = totalDistractions,
            avgRating = avgRating,
            timeOfDayDistribution = timeOfDayMap,
            sessionTypeDistribution = typeMap
        )
    }

    fun calculatePlannedVsActual(
        filter: DateRangeFilter,
        tasks: List<StudyTaskEntity>,
        sessions: List<StudySessionEntity>,
        referenceTimeMillis: Long = System.currentTimeMillis()
    ): PlannedVsActualAnalysis {
        val cal = Calendar.getInstance().apply { timeInMillis = referenceTimeMillis }
        val bounds = getPeriodBounds(filter, cal)
        val curStart = bounds.curStart
        val curEnd = bounds.curEnd
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val curStartStr = dateFormat.format(Date(curStart))
        val curEndStr = dateFormat.format(Date(curEnd))

        val periodTasks = tasks.filter {
            filter == DateRangeFilter.ALL_TIME || (it.date in curStartStr..curEndStr)
        }
        val plannedMinutes = periodTasks.sumOf { it.durationMinutes }
        val plannedTasksCount = periodTasks.size
        val completedTasksCount = periodTasks.count { it.status == "COMPLETED" }

        val taskCompletionRate = if (plannedTasksCount > 0) {
            ((completedTasksCount.toFloat() / plannedTasksCount.toFloat()) * 100).toInt()
        } else 0

        val completedSessions = sessions.filter {
            it.status == "COMPLETED" && (filter == DateRangeFilter.ALL_TIME || it.startedAt in curStart..curEnd)
        }
        val actualMinutes = completedSessions.sumOf { it.elapsedSeconds } / 60

        val adherencePercent = if (plannedMinutes > 0) {
            ((actualMinutes.toFloat() / plannedMinutes.toFloat()) * 100).toInt().coerceIn(0, 150)
        } else if (actualMinutes > 0) {
            100
        } else 0

        val summaryMessage = when {
            plannedTasksCount == 0 && actualMinutes == 0 -> "No planned tasks or sessions yet for this period."
            plannedTasksCount == 0 && actualMinutes > 0 -> "Studied ${formatDuration(actualMinutes)} without pre-planned calendar tasks."
            adherencePercent >= 100 -> "Target achieved! You completed $completedTasksCount of $plannedTasksCount tasks ($adherencePercent% of planned time)."
            adherencePercent >= 70 -> "Solid progress: completed $completedTasksCount of $plannedTasksCount tasks (${formatDuration(actualMinutes)} of ${formatDuration(plannedMinutes)})."
            else -> "Room to grow: completed $completedTasksCount of $plannedTasksCount tasks. Plan focused blocks to hit your goal."
        }

        return PlannedVsActualAnalysis(
            plannedMinutes = plannedMinutes,
            actualMinutes = actualMinutes,
            plannedTasksCount = plannedTasksCount,
            completedTasksCount = completedTasksCount,
            taskCompletionRatePercent = taskCompletionRate,
            adherencePercent = adherencePercent,
            summaryMessage = summaryMessage
        )
    }

    fun generateInsights(
        overview: PeriodOverview,
        breakdown: FocusSessionBreakdown,
        subjects: List<SubjectAnalytics>,
        gamification: FocusGamificationEntity?,
        admissionTargets: List<AdmissionTargetEntity>,
        userProfile: UserProfile?
    ): List<SmartStudyInsight> {
        val insights = mutableListOf<SmartStudyInsight>()

        // 1. Streak Insight
        val currentStreak = gamification?.currentStreakDays ?: 0
        if (currentStreak >= 3) {
            insights.add(
                SmartStudyInsight(
                    id = "streak_active",
                    title = "$currentStreak-Day Study Streak!",
                    description = "You have maintained an active study rhythm for $currentStreak consecutive days. Consistency builds deep mastery.",
                    type = InsightType.STREAK,
                    actionLabel = "Start Focus",
                    actionRoute = "focus"
                )
            )
        } else if (currentStreak in 1..2) {
            insights.add(
                SmartStudyInsight(
                    id = "streak_building",
                    title = "Building Momentum ($currentStreak Day)",
                    description = "Study today to keep your streak alive and unlock streak rewards in the Focus hub.",
                    type = InsightType.STREAK,
                    actionLabel = "Start Focus",
                    actionRoute = "focus"
                )
            )
        }

        // 2. Time-of-day pattern insight
        val topTimeOfDay = breakdown.timeOfDayDistribution.maxByOrNull { it.value }
        if (topTimeOfDay != null && topTimeOfDay.value > 0) {
            val pref = userProfile?.preferredStudyTime ?: "Morning"
            val matchText = if (topTimeOfDay.key.equals(pref, ignoreCase = true)) {
                "aligning perfectly with your preferred $pref schedule."
            } else {
                "differing from your selected preference ($pref)."
            }
            insights.add(
                SmartStudyInsight(
                    id = "peak_time",
                    title = "Peak Rhythm: ${topTimeOfDay.key}",
                    description = "You logged the most study time (${formatDuration(topTimeOfDay.value)}) in the ${topTimeOfDay.key}, $matchText",
                    type = InsightType.TIME_PATTERN
                )
            )
        }

        // 3. Subject balance insight
        if (subjects.size >= 2) {
            val topSubject = subjects.firstOrNull()
            val zeroSubject = subjects.lastOrNull { it.totalMinutes == 0 }
            if (topSubject != null && zeroSubject != null && topSubject.totalMinutes > 60) {
                insights.add(
                    SmartStudyInsight(
                        id = "subject_balance",
                        title = "Balance Study Load",
                        description = "${topSubject.subject.name} received ${topSubject.sharePercent.toInt()}% of your time, while ${zeroSubject.subject.name} has no logged sessions this period.",
                        type = InsightType.ATTENTION_NEEDED,
                        actionLabel = "View Syllabus",
                        actionRoute = "syllabus"
                    )
                )
            }
        }

        // 4. Goal Accomplishment
        if (overview.totalMinutes > 0 && overview.goalCompletionPercent >= 70) {
            insights.add(
                SmartStudyInsight(
                    id = "goal_strong",
                    title = "Goal Target Met: ${overview.goalCompletionPercent}%",
                    description = "You achieved your daily study target on ${overview.goalsMetDays} of ${overview.totalDaysInPeriod} days in this timeframe.",
                    type = InsightType.ACHIEVEMENT
                )
            )
        }

        // 5. Exam Readiness
        val upcomingTarget = admissionTargets.filter { !it.isArchived && it.applicationDeadline.isNotBlank() }
            .minByOrNull { it.applicationDeadline }
        if (upcomingTarget != null) {
            val days = calculateDaysRemaining(upcomingTarget.applicationDeadline)
            if (days in 0..60) {
                insights.add(
                    SmartStudyInsight(
                        id = "exam_countdown",
                        title = "${upcomingTarget.institutionName} Deadline in $days Days",
                        description = "Track preparation progress, complete remaining chapters, and manage admission tasks.",
                        type = InsightType.READINESS,
                        actionLabel = "Admission Calendar",
                        actionRoute = "admission"
                    )
                )
            }
        }

        return insights
    }

    fun calculateDaysRemaining(dateStr: String): Long {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(dateStr) ?: return -1
            val now = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            val diff = date.time - now
            TimeUnit.MILLISECONDS.toDays(diff)
        } catch (_: Exception) {
            -1
        }
    }

    fun getExamReadinessList(targets: List<AdmissionTargetEntity>): List<ExamReadinessItem> {
        return targets.filter { !it.isArchived }.map { target ->
            val days = calculateDaysRemaining(target.applicationDeadline)
            val isUpcoming = days >= 0
            val statusText = when {
                days < 0 -> "Deadline passed"
                days == 0L -> "Due today!"
                days == 1L -> "1 day remaining"
                else -> "$days days remaining"
            }
            ExamReadinessItem(
                target = target,
                daysRemaining = days,
                isUpcoming = isUpcoming,
                statusText = statusText
            )
        }.sortedWith(compareBy({ !it.isUpcoming }, { it.daysRemaining }))
    }
}
