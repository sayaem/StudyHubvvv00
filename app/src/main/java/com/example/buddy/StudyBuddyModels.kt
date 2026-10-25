package com.example.buddy

/**
 * Supported intent classifications for Study Buddy context retrieval.
 */
enum class BuddyQueryIntent {
    TOPIC_SYLLABUS,
    PLANNING,
    STATS_PROGRESS,
    FOCUS_HABITS,
    REVISION,
    EXAM_PREPARATION,
    GENERAL
}

/**
 * Action type for safe in-app navigation and validated operations.
 */
enum class BuddyActionType {
    START_FOCUS,
    OPEN_CALENDAR,
    OPEN_SYLLABUS,
    OPEN_REVISION_HUB,
    OPEN_STATS,
    OPEN_ADMISSION_CALENDAR
}

/**
 * An actionable chip proposed by Study Buddy.
 */
data class BuddyAction(
    val actionType: BuddyActionType,
    val title: String,
    val description: String = "",
    val targetSubjectId: Long? = null,
    val targetChapterId: Long? = null
)

/**
 * Verified StudyHub facts retrieved from local Room database.
 */
data class StudyHubVerifiedFacts(
    val studentName: String? = null,
    val academicLevel: String? = null,
    val admissionTrack: String? = null,
    val dailyGoalHours: Float? = null,
    val preferredStudyTime: String? = null,
    val todayPlannedTasksCount: Int = 0,
    val todayPlannedTasksSummary: List<String> = emptyList(),
    val totalSubjectsCount: Int = 0,
    val totalChaptersCount: Int = 0,
    val completedChaptersCount: Int = 0,
    val incompleteChaptersCount: Int = 0,
    val dueRevisionsCount: Int = 0,
    val dueRevisionSubjects: List<String> = emptyList(),
    val todayStudyMinutesRecorded: Int = 0,
    val totalStudyHoursRecorded: Float = 0f,
    val nextExamTarget: String? = null,
    val specificTopicName: String? = null,
    val specificSubjectName: String? = null,
    val notesCount: Int = 0
)

/**
 * Contextual envelope attached to prompt requests.
 */
data class BuddyContextEnvelope(
    val queryIntent: BuddyQueryIntent,
    val verifiedFacts: StudyHubVerifiedFacts,
    val timeReference: String,
    val entryPointContext: String? = null,
    val allowedActions: List<BuddyActionType> = listOf(
        BuddyActionType.START_FOCUS,
        BuddyActionType.OPEN_CALENDAR,
        BuddyActionType.OPEN_SYLLABUS,
        BuddyActionType.OPEN_REVISION_HUB,
        BuddyActionType.OPEN_STATS,
        BuddyActionType.OPEN_ADMISSION_CALENDAR
    )
)

/**
 * Request payload sent to the AI Provider.
 */
data class BuddyGenerationRequest(
    val conversationId: Long,
    val userPrompt: String,
    val recentTurns: List<Pair<String, String>>, // sender (USER or ASSISTANT) to text
    val contextEnvelope: BuddyContextEnvelope
)

/**
 * Result returned by the AI Provider.
 */
sealed interface BuddyAiResult {
    data class Success(
        val responseText: String,
        val suggestedActions: List<BuddyAction> = emptyList()
    ) : BuddyAiResult

    data class Error(
        val errorMessage: String,
        val isQuotaExhausted: Boolean = false,
        val isNetworkError: Boolean = false,
        val canRetry: Boolean = true
    ) : BuddyAiResult
}
