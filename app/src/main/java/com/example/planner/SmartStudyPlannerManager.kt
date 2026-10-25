package com.example.planner

import com.example.data.local.AdmissionTargetEntity
import com.example.data.local.ChapterEntity
import com.example.data.local.StudySessionEntity
import com.example.data.local.StudyTaskEntity
import com.example.data.local.SubjectEntity
import com.example.data.local.UserProfile
import com.example.data.repository.StudyHubRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SmartStudyPlannerManager(
    private val repository: StudyHubRepository
) {
    private val _currentDraft = MutableStateFlow<PlannerDraft?>(null)
    val currentDraft: StateFlow<PlannerDraft?> = _currentDraft.asStateFlow()

    private val _plannerStatus = MutableStateFlow(PlannerStatus.IDLE)
    val plannerStatus: StateFlow<PlannerStatus> = _plannerStatus.asStateFlow()

    private val _lastCommitResult = MutableStateFlow<CommitResult?>(null)
    val lastCommitResult: StateFlow<CommitResult?> = _lastCommitResult.asStateFlow()

    private val commitMutex = Mutex()

    /**
     * Generates a new draft plan for the specified calendar date.
     */
    fun generatePlan(
        targetDate: String,
        subjects: List<SubjectEntity>,
        chapters: List<ChapterEntity>,
        tasks: List<StudyTaskEntity>,
        sessions: List<StudySessionEntity>,
        admissionTargets: List<AdmissionTargetEntity>,
        userProfile: UserProfile?
    ): PlannerDraft {
        _plannerStatus.value = PlannerStatus.GENERATING
        _lastCommitResult.value = null

        val draft = RuleBasedPlanningEngine.generatePlan(
            targetDate = targetDate,
            subjects = subjects,
            chapters = chapters,
            tasks = tasks,
            sessions = sessions,
            admissionTargets = admissionTargets,
            userProfile = userProfile
        )

        _currentDraft.value = draft
        _plannerStatus.value = draft.status
        return draft
    }

    /**
     * Toggles the selection status of a proposed task within the active draft.
     */
    fun toggleTaskSelection(taskId: String) {
        val draft = _currentDraft.value ?: return
        val updated = draft.tasks.map { task ->
            if (task.id == taskId) task.copy(isSelected = !task.isSelected) else task
        }
        _currentDraft.value = draft.copy(
            tasks = updated,
            totalDurationMinutes = updated.filter { it.isSelected }.sumOf { it.durationMinutes }
        )
    }

    /**
     * Updates duration or time for a proposed task in the draft.
     */
    fun updateTask(taskId: String, newDurationMins: Int, newStartTime: String? = null) {
        val draft = _currentDraft.value ?: return
        val updated = draft.tasks.map { task ->
            if (task.id == taskId) {
                val duration = newDurationMins.coerceIn(15, 120)
                val start = newStartTime ?: task.startTime
                task.copy(
                    durationMinutes = duration,
                    startTime = start
                )
            } else task
        }
        _currentDraft.value = draft.copy(
            tasks = updated,
            totalDurationMinutes = updated.filter { it.isSelected }.sumOf { it.durationMinutes }
        )
    }

    /**
     * Removes a proposed task from the active draft.
     */
    fun removeTaskFromDraft(taskId: String) {
        val draft = _currentDraft.value ?: return
        val updated = draft.tasks.filterNot { it.id == taskId }
        _currentDraft.value = draft.copy(
            tasks = updated,
            totalDurationMinutes = updated.filter { it.isSelected }.sumOf { it.durationMinutes }
        )
    }

    /**
     * Dismisses the current draft and returns planner to IDLE.
     */
    fun dismissDraft() {
        _currentDraft.value = null
        _plannerStatus.value = PlannerStatus.IDLE
    }

    /**
     * Clears commit confirmation banner.
     */
    fun clearCommitResult() {
        _lastCommitResult.value = null
    }

    /**
     * Idempotent, atomic-style commit of all selected valid tasks to the existing Calendar.
     * Prevents duplicate insertions if clicked multiple times.
     */
    suspend fun commitSelectedTasksToCalendar(
        existingTasks: List<StudyTaskEntity>
    ): CommitResult = commitMutex.withLock {
        val draft = _currentDraft.value
        if (draft == null || draft.status == PlannerStatus.COMMITTED) {
            return CommitResult(false, 0, 0, "No active draft to commit.")
        }

        val selected = draft.tasks.filter { it.isSelected }
        if (selected.isEmpty()) {
            return CommitResult(false, 0, 0, "No tasks were selected.")
        }

        // Idempotency: filter out tasks that already exist in database on targetDate with matching title and time
        val tasksToInsert = selected.filterNot { prop ->
            existingTasks.any { ex ->
                ex.date == draft.targetDate &&
                        ex.title.equals(prop.title, ignoreCase = true) &&
                        ex.time == prop.startTime
            }
        }.map { prop ->
            StudyTaskEntity(
                title = prop.title,
                date = draft.targetDate,
                time = prop.startTime,
                durationMinutes = prop.durationMinutes,
                subjectId = prop.subjectId,
                chapterId = prop.chapterId,
                targetId = prop.targetId,
                status = "PENDING",
                priority = prop.priority,
                planType = prop.planType,
                notes = "[SmartPlanner] ${prop.recommendationReason}"
            )
        }

        if (tasksToInsert.isEmpty()) {
            // Already committed or all tasks already present
            _currentDraft.value = draft.copy(status = PlannerStatus.COMMITTED)
            _plannerStatus.value = PlannerStatus.COMMITTED
            val result = CommitResult(
                success = false,
                addedCount = 0,
                totalDurationMinutes = 0,
                errorMessage = "All selected tasks are already on your calendar."
            )
            _lastCommitResult.value = result
            return result
        }

        try {
            repository.createTasks(tasksToInsert)
            val totalMins = tasksToInsert.sumOf { it.durationMinutes }
            val result = CommitResult(
                success = true,
                addedCount = tasksToInsert.size,
                totalDurationMinutes = totalMins
            )
            _currentDraft.value = draft.copy(status = PlannerStatus.COMMITTED)
            _plannerStatus.value = PlannerStatus.COMMITTED
            _lastCommitResult.value = result
            return result
        } catch (e: Exception) {
            e.printStackTrace()
            val result = CommitResult(false, 0, 0, "Failed to commit plan: ${e.message}")
            _lastCommitResult.value = result
            return result
        }
    }
}
