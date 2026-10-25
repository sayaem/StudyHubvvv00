package com.example.planner

enum class PlannerStatus {
    IDLE,
    GENERATING,
    DRAFT_READY,
    COMMITTED,
    CONFLICT,
    INSUFFICIENT_DATA,
    EMPTY
}

enum class DurationConfidence {
    HIGH,     // >= 3 verified completed sessions for this subject/task
    MEDIUM,   // 1-2 verified completed sessions
    LOW,      // General study history baseline
    DEFAULT   // Cold start standard default (no historical sessions)
}

data class ProposedTask(
    val id: String,
    val title: String,
    val subjectId: Long? = null,
    val subjectName: String? = null,
    val subjectColorHex: String? = null,
    val chapterId: Long? = null,
    val chapterTitle: String? = null,
    val targetId: Long? = null,
    val targetName: String? = null,
    val durationMinutes: Int = 30,
    val startTime: String = "09:00",
    val endTime: String = "09:30",
    val priority: String = "MEDIUM", // "HIGH", "MEDIUM", "LOW"
    val priorityScore: Double = 50.0,
    val planType: String = "STUDY",   // "STUDY", "REVISION", "EXAM"
    val recommendationReason: String,
    val durationConfidence: DurationConfidence = DurationConfidence.DEFAULT,
    val durationSource: String = "Baseline default (30m)",
    val isSelected: Boolean = true
)

data class UnscheduledTaskInfo(
    val title: String,
    val subjectName: String? = null,
    val reason: String
)

data class PlannerDraft(
    val draftId: String,
    val targetDate: String,
    val tasks: List<ProposedTask>,
    val totalDurationMinutes: Int,
    val availableBudgetMinutes: Int,
    val status: PlannerStatus = PlannerStatus.DRAFT_READY,
    val warnings: List<String> = emptyList(),
    val unscheduledTasks: List<UnscheduledTaskInfo> = emptyList(),
    val generatedAtMs: Long = System.currentTimeMillis()
)

data class CommitResult(
    val success: Boolean,
    val addedCount: Int,
    val totalDurationMinutes: Int,
    val errorMessage: String? = null
)
