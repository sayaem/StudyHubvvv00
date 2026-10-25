package com.example.ui

import android.app.Application
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.buddy.BuddyAction
import com.example.buddy.BuddyAiResult
import com.example.buddy.BuddyContextEnvelope
import com.example.buddy.BuddyGenerationRequest
import com.example.buddy.GeminiRestAiProvider
import com.example.buddy.StudyBuddyAiProvider
import com.example.buddy.StudyBuddyContextEngine
import com.example.buddy.StudyHubVerifiedFacts
import com.example.data.local.BuddyConversationEntity
import com.example.data.local.BuddyMessageEntity
import com.example.data.local.AdmissionTargetEntity
import com.example.data.local.AppDatabase
import com.example.data.local.AppSettingsEntity
import com.example.data.local.ChapterEntity
import com.example.data.local.NotificationItemEntity
import com.example.data.local.StudyNoteEntity
import com.example.data.local.StudySessionEntity
import com.example.data.local.StudyTaskEntity
import com.example.data.local.SubjectEntity
import com.example.data.local.UserProfile
import com.example.data.repository.StudyHubRepository
import com.example.notifications.StudyHubNotificationManager
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class TimerStatus {
    IDLE, RUNNING, PAUSED, COMPLETED
}

enum class FocusSessionState {
    IDLE,
    FOCUS_RUNNING,
    FOCUS_PAUSED,
    SHORT_BREAK_RUNNING,
    SHORT_BREAK_PAUSED,
    LONG_BREAK_RUNNING,
    LONG_BREAK_PAUSED,
    COMPLETED,
    ENDED_EARLY
}

class StudyHubViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = StudyHubRepository(db)
    val notificationManager = StudyHubNotificationManager(application)

    private val _isProfileLoaded = MutableStateFlow(false)
    val isProfileLoaded: StateFlow<Boolean> = _isProfileLoaded.asStateFlow()

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        viewModelScope.launch {
            repository.userProfile.collect {
                _isProfileLoaded.value = true
            }
        }
    }

    val subjects: StateFlow<List<SubjectEntity>> = repository.allSubjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chapters: StateFlow<List<ChapterEntity>> = repository.allChapters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<StudyTaskEntity>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sessions: StateFlow<List<StudySessionEntity>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val admissionTargets: StateFlow<List<AdmissionTargetEntity>> = repository.allAdmissionTargets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes: StateFlow<List<StudyNoteEntity>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationItemEntity>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationsCount: StateFlow<Int> = repository.unreadNotificationCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val appSettings: StateFlow<AppSettingsEntity?> = repository.appSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val focusPresets: StateFlow<List<com.example.data.local.FocusPresetEntity>> = repository.allFocusPresets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val focusGamification: StateFlow<com.example.data.local.FocusGamificationEntity?> = repository.focusGamification
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _targetNavigationRoute = MutableStateFlow<String?>(null)
    val targetNavigationRoute: StateFlow<String?> = _targetNavigationRoute.asStateFlow()

    private val _smartStudyReminders = MutableStateFlow(com.example.notifications.NotificationPreferences.isSmartRemindersEnabled(application))
    val smartStudyReminders: StateFlow<Boolean> = _smartStudyReminders.asStateFlow()

    private val _calendarReminders = MutableStateFlow(com.example.notifications.NotificationPreferences.isCalendarRemindersEnabled(application))
    val calendarReminders: StateFlow<Boolean> = _calendarReminders.asStateFlow()

    private val _syllabusReminders = MutableStateFlow(com.example.notifications.NotificationPreferences.isSyllabusRemindersEnabled(application))
    val syllabusReminders: StateFlow<Boolean> = _syllabusReminders.asStateFlow()

    private val _goalReminders = MutableStateFlow(com.example.notifications.NotificationPreferences.isGoalRemindersEnabled(application))
    val goalReminders: StateFlow<Boolean> = _goalReminders.asStateFlow()

    private val _notificationSetEnabled = MutableStateFlow(com.example.notifications.NotificationPreferences.isNotificationSetEnabled(application))
    val notificationSetEnabled: StateFlow<Boolean> = _notificationSetEnabled.asStateFlow()

    // Smart Study Planner 1.0 State
    val plannerManager = com.example.planner.SmartStudyPlannerManager(repository)
    val plannerDraft: StateFlow<com.example.planner.PlannerDraft?> = plannerManager.currentDraft
    val plannerStatus: StateFlow<com.example.planner.PlannerStatus> = plannerManager.plannerStatus
    val plannerCommitResult: StateFlow<com.example.planner.CommitResult?> = plannerManager.lastCommitResult

    // Smart Revision Engine 1.0 State
    val revisionManager = com.example.revision.SmartRevisionManager(repository)
    val revisionUiModels: StateFlow<List<com.example.revision.TrackedRevisionUiModel>> = revisionManager.trackedRevisionUiModels
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val revisionSummaryMetrics: StateFlow<com.example.revision.RevisionSummaryMetrics> = revisionManager.summaryMetrics
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), com.example.revision.RevisionSummaryMetrics())
    val allRevisionHistory: StateFlow<List<com.example.data.local.RevisionHistoryEntity>> = repository.allRevisionHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeRevisionItemId = MutableStateFlow<Long?>(null)
    val activeRevisionItemId: StateFlow<Long?> = _activeRevisionItemId.asStateFlow()

    private val _proposedRevisionTasks = MutableStateFlow<List<com.example.revision.ProposedRevisionTask>>(emptyList())
    val proposedRevisionTasks: StateFlow<List<com.example.revision.ProposedRevisionTask>> = _proposedRevisionTasks.asStateFlow()

    // Focus 3.0 Timer State
    private val _focusSessionState = MutableStateFlow(FocusSessionState.IDLE)
    val focusSessionState: StateFlow<FocusSessionState> = _focusSessionState.asStateFlow()

    private val _timerStatus = MutableStateFlow(TimerStatus.IDLE)
    val timerStatus: StateFlow<TimerStatus> = _timerStatus.asStateFlow()

    private val _timerMode = MutableStateFlow("POMODORO") // "POMODORO", "DEEP_FOCUS", "SIMPLE_TIMER", "STOPWATCH"
    val timerMode: StateFlow<String> = _timerMode.asStateFlow()

    private val _pomodoroPhase = MutableStateFlow("WORK") // "WORK", "SHORT_BREAK", "LONG_BREAK"
    val pomodoroPhase: StateFlow<String> = _pomodoroPhase.asStateFlow()

    private val _currentCycle = MutableStateFlow(1)
    val currentCycle: StateFlow<Int> = _currentCycle.asStateFlow()

    private val _targetCycles = MutableStateFlow(4)
    val targetCycles: StateFlow<Int> = _targetCycles.asStateFlow()

    private val _isStrictFocus = MutableStateFlow(false)
    val isStrictFocus: StateFlow<Boolean> = _isStrictFocus.asStateFlow()

    private val _timeRemainingSeconds = MutableStateFlow(25 * 60)
    val timeRemainingSeconds: StateFlow<Int> = _timeRemainingSeconds.asStateFlow()

    private val _totalDurationSeconds = MutableStateFlow(25 * 60)
    val totalDurationSeconds: StateFlow<Int> = _totalDurationSeconds.asStateFlow()

    private val _selectedSessionType = MutableStateFlow("POMODORO_FOCUS")
    val selectedSessionType: StateFlow<String> = _selectedSessionType.asStateFlow()

    private val _timerSubjectId = MutableStateFlow<Long?>(null)
    val timerSubjectId: StateFlow<Long?> = _timerSubjectId.asStateFlow()

    private val _timerChapterId = MutableStateFlow<Long?>(null)
    val timerChapterId: StateFlow<Long?> = _timerChapterId.asStateFlow()

    private val _selectedGoalTask = MutableStateFlow<String?>(null)
    val selectedGoalTask: StateFlow<String?> = _selectedGoalTask.asStateFlow()

    private val _distractionCount = MutableStateFlow(0)
    val distractionCount: StateFlow<Int> = _distractionCount.asStateFlow()

    private val _workMinutes = MutableStateFlow(25)
    val workMinutes: StateFlow<Int> = _workMinutes.asStateFlow()

    private val _shortBreakMinutes = MutableStateFlow(5)
    val shortBreakMinutes: StateFlow<Int> = _shortBreakMinutes.asStateFlow()

    private val _longBreakMinutes = MutableStateFlow(15)
    val longBreakMinutes: StateFlow<Int> = _longBreakMinutes.asStateFlow()

    private val _autoStartBreaks = MutableStateFlow(false)
    val autoStartBreaks: StateFlow<Boolean> = _autoStartBreaks.asStateFlow()

    private val _autoStartNextFocus = MutableStateFlow(false)
    val autoStartNextFocus: StateFlow<Boolean> = _autoStartNextFocus.asStateFlow()

    private val _ambientSoundType = MutableStateFlow("OFF")
    val ambientSoundType: StateFlow<String> = _ambientSoundType.asStateFlow()

    private val _ambientVolume = MutableStateFlow(0.7f)
    val ambientVolume: StateFlow<Float> = _ambientVolume.asStateFlow()

    val ambientSoundManager = com.example.notifications.AmbientSoundManager()

    // Persistent Wall-Clock Timing Parameters
    private var startTimeMs: Long = 0L
    private var targetDurationMs: Long = 25 * 60 * 1000L
    private var accumulatedPauseMs: Long = 0L
    private var lastPauseMs: Long = 0L

    private var timerJob: Job? = null
    private var lastRecordedSessionId: Long? = null

    init {
        viewModelScope.launch {
            repository.seedDefaultFocusPresetsIfEmpty()
            restoreActiveSessionIfAny()
        }
    }

    // Date formatting helper
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun getTodayDateString(): String = dateFormat.format(Date())

    // Calculations per PRD Section 11
    val todayStudySeconds: StateFlow<Int> = sessions.combine(MutableStateFlow(Unit)) { sessionList, _ ->
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val todayEnd = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis

        sessionList.filter {
            it.status == "COMPLETED" && it.startedAt in todayStart..todayEnd
        }.sumOf { it.elapsedSeconds }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val overallSyllabusProgress: StateFlow<Int> = chapters.combine(subjects) { chapterList, subjectList ->
        if (chapterList.isEmpty() || subjectList.isEmpty()) 0
        else {
            val completed = chapterList.count { it.status == "COMPLETED" }
            ((completed.toFloat() / chapterList.size) * 100).toInt()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun calculateDaysRemaining(targetDateStr: String?): Long? {
        if (targetDateStr.isNullOrBlank()) return null
        return try {
            val targetDate = dateFormat.parse(targetDateStr) ?: return null
            val now = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
            val diffMs = targetDate.time - now.time
            diffMs / (1000 * 60 * 60 * 24)
        } catch (e: Exception) {
            null
        }
    }

    // Timer Actions & Focus 3.0 State Machine
    fun setTimerMode(mode: String) {
        if (_focusSessionState.value == FocusSessionState.FOCUS_RUNNING ||
            _focusSessionState.value == FocusSessionState.SHORT_BREAK_RUNNING ||
            _focusSessionState.value == FocusSessionState.LONG_BREAK_RUNNING
        ) return

        _timerMode.value = mode
        when (mode) {
            "POMODORO" -> {
                _pomodoroPhase.value = "WORK"
                _currentCycle.value = 1
                selectTimerDuration(_workMinutes.value, "POMODORO_FOCUS")
            }
            "DEEP_FOCUS" -> {
                selectTimerDuration(50, "POMODORO_FOCUS")
            }
            "SIMPLE_TIMER" -> {
                selectTimerDuration(15, "CUSTOM")
            }
            "STOPWATCH" -> {
                _timerJobCancel()
                _totalDurationSeconds.value = 0
                _timeRemainingSeconds.value = 0
                _selectedSessionType.value = "CUSTOM"
                _focusSessionState.value = FocusSessionState.IDLE
                _timerStatus.value = TimerStatus.IDLE
                persistActiveSession()
            }
        }
    }

    fun applyPreset(preset: com.example.data.local.FocusPresetEntity) {
        if (_focusSessionState.value == FocusSessionState.FOCUS_RUNNING ||
            _focusSessionState.value == FocusSessionState.SHORT_BREAK_RUNNING ||
            _focusSessionState.value == FocusSessionState.LONG_BREAK_RUNNING
        ) return

        _timerMode.value = preset.mode
        _isStrictFocus.value = preset.isStrict
        _targetCycles.value = preset.targetCycles
        _currentCycle.value = 1
        _pomodoroPhase.value = "WORK"
        _workMinutes.value = preset.workMinutes
        _shortBreakMinutes.value = preset.shortBreakMinutes
        _longBreakMinutes.value = preset.longBreakMinutes
        _autoStartBreaks.value = preset.autoStartBreaks
        _autoStartNextFocus.value = preset.autoStartNextFocus
        selectTimerDuration(preset.workMinutes, if (preset.mode == "POMODORO") "POMODORO_FOCUS" else "CUSTOM")
    }

    fun updateFocusSettings(
        workMins: Int,
        shortBreakMins: Int,
        longBreakMins: Int,
        cycles: Int,
        autoBreaks: Boolean,
        autoNextFocus: Boolean
    ) {
        _workMinutes.value = workMins.coerceIn(1, 180)
        _shortBreakMinutes.value = shortBreakMins.coerceIn(1, 60)
        _longBreakMinutes.value = longBreakMins.coerceIn(1, 90)
        _targetCycles.value = cycles.coerceIn(1, 12)
        _autoStartBreaks.value = autoBreaks
        _autoStartNextFocus.value = autoNextFocus

        if (_pomodoroPhase.value == "WORK" && (_focusSessionState.value == FocusSessionState.IDLE)) {
            selectTimerDuration(_workMinutes.value, "POMODORO_FOCUS")
        }
    }

    fun saveCustomPreset(
        title: String,
        mode: String,
        workMins: Int,
        shortBreakMins: Int,
        longBreakMins: Int,
        cycles: Int,
        isStrict: Boolean,
        autoStartBreaks: Boolean = false,
        autoStartNextFocus: Boolean = false
    ) {
        viewModelScope.launch {
            repository.saveFocusPreset(
                com.example.data.local.FocusPresetEntity(
                    title = title.ifBlank { "Custom ${workMins}m" },
                    mode = mode,
                    workMinutes = workMins,
                    shortBreakMinutes = shortBreakMins,
                    longBreakMinutes = longBreakMins,
                    targetCycles = cycles,
                    isStrict = isStrict,
                    isDefault = false,
                    autoStartBreaks = autoStartBreaks,
                    autoStartNextFocus = autoStartNextFocus
                )
            )
        }
    }

    fun deleteFocusPreset(preset: com.example.data.local.FocusPresetEntity) {
        viewModelScope.launch {
            repository.deleteFocusPreset(preset)
        }
    }

    fun setGoalTask(goal: String?) {
        _selectedGoalTask.value = goal
        persistActiveSession()
    }

    fun recordDistraction() {
        _distractionCount.value += 1
        persistActiveSession()
    }

    fun selectTimerDuration(minutes: Int, sessionType: String) {
        if (_focusSessionState.value == FocusSessionState.FOCUS_RUNNING ||
            _focusSessionState.value == FocusSessionState.SHORT_BREAK_RUNNING ||
            _focusSessionState.value == FocusSessionState.LONG_BREAK_RUNNING
        ) return

        _timerJobCancel()
        val secs = minutes * 60
        _totalDurationSeconds.value = secs
        _timeRemainingSeconds.value = secs
        targetDurationMs = secs * 1000L
        _selectedSessionType.value = sessionType
        _focusSessionState.value = FocusSessionState.IDLE
        _timerStatus.value = TimerStatus.IDLE
        persistActiveSession()
    }

    fun setTimerSubject(subjectId: Long?) {
        _timerSubjectId.value = subjectId
        // Reset chapter if subject changed
        _timerChapterId.value = null
        persistActiveSession()
    }

    fun setTimerChapter(chapterId: Long?) {
        _timerChapterId.value = chapterId
        persistActiveSession()
    }

    fun setTimerSubjectAndChapter(subjectId: Long?, chapterId: Long?) {
        _timerSubjectId.value = subjectId
        _timerChapterId.value = chapterId
        persistActiveSession()
    }

    fun setWorkDuration(minutes: Int) {
        _workMinutes.value = minutes.coerceIn(1, 180)
        selectTimerDuration(_workMinutes.value, "POMODORO_FOCUS")
    }

    fun toggleStrictFocus(enabled: Boolean) {
        _isStrictFocus.value = enabled
        persistActiveSession()
    }

    fun setAmbientSound(soundType: String) {
        _ambientSoundType.value = soundType
        ambientSoundManager.setSound(soundType)
    }

    fun setAmbientVolume(volume: Float) {
        _ambientVolume.value = volume
        ambientSoundManager.setVolume(volume)
    }

    fun startTimer(isRestoring: Boolean = false) {
        val now = System.currentTimeMillis()
        val currentState = _focusSessionState.value

        if (!isRestoring) {
            if (currentState == FocusSessionState.IDLE) {
                startTimeMs = now
                accumulatedPauseMs = 0L
                _distractionCount.value = 0
            } else if (isPausedState(currentState)) {
                accumulatedPauseMs += (now - lastPauseMs)
            }
        }

        val nextState = when (_timerMode.value) {
            "POMODORO" -> when (_pomodoroPhase.value) {
                "SHORT_BREAK" -> FocusSessionState.SHORT_BREAK_RUNNING
                "LONG_BREAK" -> FocusSessionState.LONG_BREAK_RUNNING
                else -> FocusSessionState.FOCUS_RUNNING
            }
            else -> FocusSessionState.FOCUS_RUNNING
        }

        _focusSessionState.value = nextState
        _timerStatus.value = TimerStatus.RUNNING

        if (_ambientSoundType.value != "OFF") {
            ambientSoundManager.setSound(_ambientSoundType.value)
        }

        persistActiveSession()

        _timerJobCancel()
        timerJob = viewModelScope.launch {
            while (_focusSessionState.value == FocusSessionState.FOCUS_RUNNING ||
                _focusSessionState.value == FocusSessionState.SHORT_BREAK_RUNNING ||
                _focusSessionState.value == FocusSessionState.LONG_BREAK_RUNNING
            ) {
                delay(500L)
                updateTimerTick()
            }
        }
    }

    private fun isPausedState(state: FocusSessionState): Boolean {
        return state == FocusSessionState.FOCUS_PAUSED ||
                state == FocusSessionState.SHORT_BREAK_PAUSED ||
                state == FocusSessionState.LONG_BREAK_PAUSED
    }

    private fun updateTimerTick() {
        val currentState = _focusSessionState.value
        if (!isRunningState(currentState)) return

        val now = System.currentTimeMillis()
        val elapsedMs = maxOf(0L, now - startTimeMs - accumulatedPauseMs)

        if (_timerMode.value == "STOPWATCH") {
            _timeRemainingSeconds.value = (elapsedMs / 1000L).toInt()
        } else {
            val remainMs = maxOf(0L, targetDurationMs - elapsedMs)
            val remainSecs = (remainMs / 1000L).toInt()
            _timeRemainingSeconds.value = remainSecs

            if (remainMs <= 0) {
                handleTimerFinishedNormally()
            }
        }
    }

    private fun isRunningState(state: FocusSessionState): Boolean {
        return state == FocusSessionState.FOCUS_RUNNING ||
                state == FocusSessionState.SHORT_BREAK_RUNNING ||
                state == FocusSessionState.LONG_BREAK_RUNNING
    }

    private fun handleTimerFinishedNormally() {
        if (_timerMode.value == "POMODORO") {
            if (_pomodoroPhase.value == "WORK") {
                // Focus work finished! Transition to break
                if (_autoStartBreaks.value) {
                    transitionToNextPhase(autoStart = true)
                } else {
                    transitionToNextPhase(autoStart = false)
                }
            } else {
                // Break finished!
                if (_autoStartNextFocus.value) {
                    transitionToNextPhase(autoStart = true)
                } else {
                    transitionToNextPhase(autoStart = false)
                }
            }
        } else {
            finishTimer(completedNormally = true)
        }
    }

    fun pauseTimer() {
        if (isRunningState(_focusSessionState.value)) {
            val pausedState = when (_focusSessionState.value) {
                FocusSessionState.SHORT_BREAK_RUNNING -> FocusSessionState.SHORT_BREAK_PAUSED
                FocusSessionState.LONG_BREAK_RUNNING -> FocusSessionState.LONG_BREAK_PAUSED
                else -> FocusSessionState.FOCUS_PAUSED
            }
            _focusSessionState.value = pausedState
            _timerStatus.value = TimerStatus.PAUSED
            lastPauseMs = System.currentTimeMillis()
            _timerJobCancel()
            ambientSoundManager.stop()
            persistActiveSession()
        }
    }

    fun resumeTimer() {
        if (isPausedState(_focusSessionState.value)) {
            startTimer()
        }
    }

    fun resetTimer() {
        _timerJobCancel()
        ambientSoundManager.stop()
        if (_timerMode.value == "STOPWATCH") {
            _timeRemainingSeconds.value = 0
        } else {
            _timeRemainingSeconds.value = _totalDurationSeconds.value
        }
        _focusSessionState.value = FocusSessionState.IDLE
        _timerStatus.value = TimerStatus.IDLE
        _distractionCount.value = 0
        clearActiveSession()
    }

    fun skipPhase() {
        _timerJobCancel()
        ambientSoundManager.stop()
        if (_timerMode.value == "POMODORO") {
            transitionToNextPhase(autoStart = false)
        } else {
            resetTimer()
        }
    }

    private fun transitionToNextPhase(autoStart: Boolean) {
        val now = System.currentTimeMillis()
        startTimeMs = now
        accumulatedPauseMs = 0L

        if (_pomodoroPhase.value == "WORK") {
            // Work finished -> Break
            val isLong = _currentCycle.value >= _targetCycles.value
            _pomodoroPhase.value = if (isLong) "LONG_BREAK" else "SHORT_BREAK"
            val breakMins = if (isLong) _longBreakMinutes.value else _shortBreakMinutes.value
            val breakSecs = breakMins * 60
            _totalDurationSeconds.value = breakSecs
            _timeRemainingSeconds.value = breakSecs
            targetDurationMs = breakSecs * 1000L
            _selectedSessionType.value = if (isLong) "LONG_BREAK" else "SHORT_BREAK"

            val nextState = if (isLong) {
                if (autoStart) FocusSessionState.LONG_BREAK_RUNNING else FocusSessionState.LONG_BREAK_PAUSED
            } else {
                if (autoStart) FocusSessionState.SHORT_BREAK_RUNNING else FocusSessionState.SHORT_BREAK_PAUSED
            }
            _focusSessionState.value = nextState
            _timerStatus.value = if (autoStart) TimerStatus.RUNNING else TimerStatus.PAUSED

            if (autoStart) {
                startTimer(isRestoring = true)
            } else {
                persistActiveSession()
            }
        } else {
            // Break finished -> Next Focus cycle
            if (_pomodoroPhase.value == "LONG_BREAK") {
                _currentCycle.value = 1
            } else {
                _currentCycle.value += 1
            }
            _pomodoroPhase.value = "WORK"
            val workSecs = _workMinutes.value * 60
            _totalDurationSeconds.value = workSecs
            _timeRemainingSeconds.value = workSecs
            targetDurationMs = workSecs * 1000L
            _selectedSessionType.value = "POMODORO_FOCUS"

            val nextState = if (autoStart) FocusSessionState.FOCUS_RUNNING else FocusSessionState.FOCUS_PAUSED
            _focusSessionState.value = nextState
            _timerStatus.value = if (autoStart) TimerStatus.RUNNING else TimerStatus.PAUSED

            if (autoStart) {
                startTimer(isRestoring = true)
            } else {
                persistActiveSession()
            }
        }
    }

    fun finishTimer(
        completedNormally: Boolean = false,
        rating: Int = 5,
        notes: String? = null,
        distractions: Int = 0,
        onGamificationUpdated: (com.example.data.local.FocusGamificationEntity) -> Unit = {}
    ) {
        val now = System.currentTimeMillis()
        val elapsedMs = if (_timerMode.value == "STOPWATCH") {
            _timeRemainingSeconds.value * 1000L
        } else {
            val totalMs = _totalDurationSeconds.value * 1000L
            if (completedNormally) totalMs else (totalMs - (_timeRemainingSeconds.value * 1000L))
        }

        val elapsedSecs = (elapsedMs / 1000L).toInt()

        _timerJobCancel()
        ambientSoundManager.stop()
        _focusSessionState.value = if (completedNormally) FocusSessionState.COMPLETED else FocusSessionState.ENDED_EARLY
        _timerStatus.value = TimerStatus.COMPLETED
        clearActiveSession()

        if (elapsedSecs >= 10) { // Log meaningful sessions
            viewModelScope.launch {
                val start = if (startTimeMs > 0) startTimeMs else (now - elapsedMs)
                val totalDistractions = if (distractions > 0) distractions else _distractionCount.value
                val id = repository.logStudySession(
                    subjectId = _timerSubjectId.value,
                    chapterId = _timerChapterId.value,
                    taskId = null,
                    startedAt = start,
                    endedAt = now,
                    elapsedSeconds = elapsedSecs,
                    status = if (completedNormally) "COMPLETED" else "ENDED_EARLY",
                    sessionType = _selectedSessionType.value,
                    reflectionRating = rating,
                    reflectionNotes = notes,
                    distractions = totalDistractions
                )
                lastRecordedSessionId = id

                val updatedGamification = repository.awardFocusSession(
                    elapsedSeconds = elapsedSecs,
                    isCompleted = completedNormally
                )
                onGamificationUpdated(updatedGamification)

                // Notify completion
                val mins = elapsedSecs / 60
                val subjectName = subjects.value.find { it.id == _timerSubjectId.value }?.name ?: "Study"
                val title = "Focus Session Completed!"
                val msg = "Awesome! You focused on $subjectName for $mins minutes and earned XP!"

                repository.postNotification(
                    title = title,
                    body = msg,
                    category = "STUDY",
                    actionRoute = "focus"
                )
                notificationManager.postNotification(
                    id = 1001,
                    channelId = StudyHubNotificationManager.CHANNEL_STUDY,
                    title = title,
                    message = msg
                )
            }
        }
    }

    private fun persistActiveSession() {
        viewModelScope.launch {
            val currentState = _focusSessionState.value
            if (currentState == FocusSessionState.IDLE ||
                currentState == FocusSessionState.COMPLETED ||
                currentState == FocusSessionState.ENDED_EARLY
            ) {
                repository.clearActiveFocusSession()
            } else {
                val entity = com.example.data.local.ActiveFocusSessionEntity(
                    sessionState = currentState.name,
                    mode = _timerMode.value,
                    phase = _pomodoroPhase.value,
                    currentCycle = _currentCycle.value,
                    targetCycles = _targetCycles.value,
                    subjectId = _timerSubjectId.value,
                    chapterId = _timerChapterId.value,
                    goalTask = _selectedGoalTask.value,
                    isStrict = _isStrictFocus.value,
                    totalDurationSeconds = _totalDurationSeconds.value,
                    timeRemainingSeconds = _timeRemainingSeconds.value,
                    startTimeMs = startTimeMs,
                    accumulatedPauseMs = accumulatedPauseMs,
                    lastPauseMs = lastPauseMs,
                    lastTickMs = System.currentTimeMillis(),
                    distractions = _distractionCount.value,
                    workMinutes = _workMinutes.value,
                    shortBreakMinutes = _shortBreakMinutes.value,
                    longBreakMinutes = _longBreakMinutes.value,
                    autoStartBreaks = _autoStartBreaks.value,
                    autoStartNextFocus = _autoStartNextFocus.value
                )
                repository.saveActiveFocusSession(entity)
            }
        }
    }

    private fun clearActiveSession() {
        viewModelScope.launch {
            repository.clearActiveFocusSession()
        }
    }

    private suspend fun restoreActiveSessionIfAny() {
        val active = repository.getActiveFocusSessionSync() ?: return
        val savedState = try {
            FocusSessionState.valueOf(active.sessionState)
        } catch (e: Exception) {
            FocusSessionState.IDLE
        }

        if (savedState == FocusSessionState.IDLE ||
            savedState == FocusSessionState.COMPLETED ||
            savedState == FocusSessionState.ENDED_EARLY
        ) {
            return
        }

        _timerMode.value = active.mode
        _pomodoroPhase.value = active.phase
        _currentCycle.value = active.currentCycle
        _targetCycles.value = active.targetCycles
        _timerSubjectId.value = active.subjectId
        _timerChapterId.value = active.chapterId
        _selectedGoalTask.value = active.goalTask
        _isStrictFocus.value = active.isStrict
        _totalDurationSeconds.value = active.totalDurationSeconds
        _distractionCount.value = active.distractions
        _workMinutes.value = active.workMinutes
        _shortBreakMinutes.value = active.shortBreakMinutes
        _longBreakMinutes.value = active.longBreakMinutes
        _autoStartBreaks.value = active.autoStartBreaks
        _autoStartNextFocus.value = active.autoStartNextFocus

        startTimeMs = active.startTimeMs
        accumulatedPauseMs = active.accumulatedPauseMs
        lastPauseMs = active.lastPauseMs
        targetDurationMs = active.totalDurationSeconds * 1000L

        val now = System.currentTimeMillis()
        val isPaused = savedState == FocusSessionState.FOCUS_PAUSED ||
                savedState == FocusSessionState.SHORT_BREAK_PAUSED ||
                savedState == FocusSessionState.LONG_BREAK_PAUSED

        if (isPaused) {
            _timeRemainingSeconds.value = active.timeRemainingSeconds
            _focusSessionState.value = savedState
            _timerStatus.value = TimerStatus.PAUSED
        } else {
            val elapsedMs = maxOf(0L, now - startTimeMs - accumulatedPauseMs)
            if (_timerMode.value == "STOPWATCH") {
                _timeRemainingSeconds.value = (elapsedMs / 1000L).toInt()
                _focusSessionState.value = savedState
                _timerStatus.value = TimerStatus.RUNNING
                startTimer(isRestoring = true)
            } else {
                val remainMs = targetDurationMs - elapsedMs
                if (remainMs <= 0) {
                    _timeRemainingSeconds.value = 0
                    if (active.phase == "WORK" && _autoStartBreaks.value) {
                        transitionToNextPhase(autoStart = true)
                    } else {
                        _focusSessionState.value = FocusSessionState.COMPLETED
                        _timerStatus.value = TimerStatus.COMPLETED
                        finishTimer(completedNormally = true)
                    }
                } else {
                    _timeRemainingSeconds.value = (remainMs / 1000L).toInt()
                    _focusSessionState.value = savedState
                    _timerStatus.value = TimerStatus.RUNNING
                    startTimer(isRestoring = true)
                }
            }
        }
    }

    private fun _timerJobCancel() {
        timerJob?.cancel()
        timerJob = null
    }

    override fun onCleared() {
        super.onCleared()
        ambientSoundManager.stop()
    }


    // Onboarding and User Profile
    fun saveOnboardingProfile(
        name: String,
        avatarId: String,
        academicLevel: String,
        group: String,
        targetYear: String,
        admissionTrack: String?,
        dailyGoalHours: Int,
        preferredTime: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val profile = UserProfile(
                displayName = name.trim().ifBlank { "Student" },
                avatarId = avatarId,
                academicLevel = academicLevel,
                academicGroup = group,
                targetYear = targetYear,
                admissionTrack = admissionTrack,
                dailyGoalMinutes = dailyGoalHours * 60,
                preferredStudyTime = preferredTime,
                isOnboardingCompleted = true,
                updatedAt = System.currentTimeMillis()
            )
            repository.saveUserProfile(profile)

            // Seed initial relevant subjects if user has none yet
            if (subjects.value.isEmpty()) {
                seedInitialSubjects(academicLevel, admissionTrack)
            }

            // Post welcome notification
            repository.postNotification(
                title = "Welcome to StudyHub!",
                body = "Your personal study companion is ready. Plan, focus, and achieve your goals!",
                category = "SYSTEM"
            )

            onSuccess()
        }
    }

    private suspend fun seedInitialSubjects(academicLevel: String, admissionTrack: String?) {
        when (academicLevel) {
            "SSC" -> {
                repository.createSubjectWithChapters("Mathematics", "গণিত", "#2563EB", "calculator", "Compulsory", 12)
                repository.createSubjectWithChapters("Physics", "পদার্থবিজ্ঞান", "#0796A3", "atom", "Compulsory", 10)
                repository.createSubjectWithChapters("Chemistry", "রসায়ন", "#F97316", "flask", "Compulsory", 10)
                repository.createSubjectWithChapters("English", "ইংরেজি", "#EC4899", "book", "Compulsory", 8)
                repository.createSubjectWithChapters("Bangla", "বাংলা", "#8B5CF6", "pen", "Compulsory", 6)
            }
            "HSC" -> {
                repository.createSubjectWithChapters("Higher Mathematics", "উচ্চতর গণিত", "#2563EB", "calculator", "Compulsory", 10)
                repository.createSubjectWithChapters("Physics", "পদার্থবিজ্ঞান", "#0796A3", "atom", "Compulsory", 10)
                repository.createSubjectWithChapters("Chemistry", "রসায়ন", "#F97316", "flask", "Compulsory", 10)
                repository.createSubjectWithChapters("Biology", "জীববিজ্ঞান", "#10B981", "biotech", "Compulsory", 8)
                repository.createSubjectWithChapters("English", "ইংরেজি", "#EC4899", "book", "Compulsory", 8)
            }
            "Admission" -> {
                when (admissionTrack) {
                    "Medical" -> {
                        repository.createSubjectWithChapters("Biology", "জীববিজ্ঞান", "#10B981", "biotech", "Compulsory", 12)
                        repository.createSubjectWithChapters("Chemistry", "রসায়ন", "#F97316", "flask", "Compulsory", 10)
                        repository.createSubjectWithChapters("Physics", "পদার্থবিজ্ঞান", "#0796A3", "atom", "Compulsory", 10)
                        repository.createSubjectWithChapters("English & GK", "ইংরেজি ও সাধারণ জ্ঞান", "#EC4899", "book", "Compulsory", 8)
                    }
                    "Engineering" -> {
                        repository.createSubjectWithChapters("Mathematics", "গণিত", "#2563EB", "calculator", "Compulsory", 12)
                        repository.createSubjectWithChapters("Physics", "পদার্থবিজ্ঞান", "#0796A3", "atom", "Compulsory", 12)
                        repository.createSubjectWithChapters("Chemistry", "রসায়ন", "#F97316", "flask", "Compulsory", 10)
                        repository.createSubjectWithChapters("English", "ইংরেজি", "#EC4899", "book", "Compulsory", 6)
                    }
                    else -> {
                        repository.createSubjectWithChapters("Bangla", "বাংলা", "#8B5CF6", "pen", "Compulsory", 10)
                        repository.createSubjectWithChapters("English", "ইংরেজি", "#EC4899", "book", "Compulsory", 10)
                        repository.createSubjectWithChapters("General Knowledge", "সাধারণ জ্ঞান", "#0796A3", "book", "Compulsory", 8)
                    }
                }
            }
        }
    }

    // Subject operations
    fun createSubject(
        name: String,
        banglaName: String?,
        colorHex: String,
        iconKey: String,
        category: String,
        chapterCount: Int,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.createSubjectWithChapters(
                name = name,
                banglaName = banglaName,
                colorHex = colorHex,
                iconKey = iconKey,
                category = category,
                initialChapterCount = chapterCount
            )
            onSuccess()
        }
    }

    fun deleteSubject(subject: SubjectEntity) {
        viewModelScope.launch {
            repository.deleteSubject(subject)
        }
    }

    // Chapter operations
    fun updateChapterStatus(chapter: ChapterEntity, newStatus: String) {
        viewModelScope.launch {
            repository.updateChapterStatus(chapter, newStatus)
        }
    }

    fun addChapter(subjectId: Long, title: String, count: Int = 1) {
        viewModelScope.launch {
            val existing = chapters.value.filter { it.subjectId == subjectId }
            val nextSort = existing.size + 1
            repository.addChapter(subjectId, title.ifBlank { "Chapter $nextSort" }, nextSort)
        }
    }

    fun deleteChapter(chapter: ChapterEntity) {
        viewModelScope.launch {
            repository.deleteChapter(chapter)
        }
    }

    // Task operations
    fun createTask(
        title: String,
        date: String,
        time: String?,
        duration: Int,
        subjectId: Long?,
        chapterId: Long? = null,
        priority: String = "MEDIUM",
        planType: String = "STUDY",
        notes: String? = null,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            repository.createTask(
                StudyTaskEntity(
                    title = title.trim(),
                    date = date,
                    time = time,
                    durationMinutes = duration,
                    subjectId = subjectId,
                    chapterId = chapterId,
                    status = "PENDING",
                    priority = priority,
                    planType = planType,
                    notes = notes
                )
            )
            onSuccess()
        }
    }

    fun updateTask(task: StudyTaskEntity, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.updateTask(task)
            onSuccess()
        }
    }

    fun createTargetTask(
        title: String,
        date: String,
        targetId: Long,
        priority: String = "MEDIUM",
        notes: String? = null,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            repository.createTask(
                StudyTaskEntity(
                    title = title.trim(),
                    date = date,
                    targetId = targetId,
                    status = "PENDING",
                    priority = priority,
                    notes = notes
                )
            )
            onSuccess()
        }
    }

    fun toggleTaskStatus(task: StudyTaskEntity) {
        viewModelScope.launch {
            val newStatus = if (task.status == "COMPLETED") "PENDING" else "COMPLETED"
            repository.updateTaskStatus(task, newStatus)
        }
    }

    fun deleteTask(task: StudyTaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    // Admission target operations
    fun saveAdmissionTarget(target: AdmissionTargetEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.saveAdmissionTarget(target)
            onSuccess()
        }
    }

    fun deleteAdmissionTarget(target: AdmissionTargetEntity) {
        viewModelScope.launch {
            repository.deleteAdmissionTarget(target)
        }
    }

    // Study notes operations
    fun saveNote(note: StudyNoteEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.saveNote(note)
            onSuccess()
        }
    }

    fun deleteNote(note: StudyNoteEntity) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    // Notifications operations
    fun initializeNotificationsOnLaunch() {
        viewModelScope.launch {
            try {
                val settings = repository.getAppSettingsSync() ?: AppSettingsEntity()
                if (settings.studyRemindersEnabled) {
                    com.example.notifications.NotificationScheduler.scheduleNextHourlyCheck(
                        context = getApplication(),
                        quietHoursStart = settings.quietHoursStartHour,
                        quietHoursEnd = settings.quietHoursEndHour
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateMasterNotificationToggle(enabled: Boolean) {
        viewModelScope.launch {
            val current = appSettings.value ?: AppSettingsEntity()
            val updated = current.copy(studyRemindersEnabled = enabled)
            repository.saveAppSettings(updated)

            if (enabled) {
                com.example.notifications.NotificationScheduler.scheduleNextHourlyCheck(
                    context = getApplication(),
                    quietHoursStart = updated.quietHoursStartHour,
                    quietHoursEnd = updated.quietHoursEndHour
                )
            } else {
                com.example.notifications.NotificationScheduler.cancelPendingNotifications(getApplication())
            }
        }
    }

    fun deleteNotification(id: Long) {
        viewModelScope.launch {
            repository.deleteNotification(id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }

    fun onNotificationRouteReceived(route: String) {
        _targetNavigationRoute.value = route
    }

    fun clearTargetNavigationRoute() {
        _targetNavigationRoute.value = null
    }

    fun updateSmartRemindersCategory(category: String, enabled: Boolean) {
        val app = getApplication<Application>()
        when (category) {
            "SMART" -> {
                com.example.notifications.NotificationPreferences.setSmartRemindersEnabled(app, enabled)
                _smartStudyReminders.value = enabled
            }
            "CALENDAR" -> {
                com.example.notifications.NotificationPreferences.setCalendarRemindersEnabled(app, enabled)
                _calendarReminders.value = enabled
            }
            "SYLLABUS" -> {
                com.example.notifications.NotificationPreferences.setSyllabusRemindersEnabled(app, enabled)
                _syllabusReminders.value = enabled
            }
            "GOAL" -> {
                com.example.notifications.NotificationPreferences.setGoalRemindersEnabled(app, enabled)
                _goalReminders.value = enabled
            }
            "NOTIFICATION_SET" -> {
                com.example.notifications.NotificationPreferences.setNotificationSetEnabled(app, enabled)
                _notificationSetEnabled.value = enabled
            }
        }
    }

    // Settings operations
    fun updateThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            val current = appSettings.value ?: AppSettingsEntity()
            repository.saveAppSettings(current.copy(themeMode = mode.name))
        }
    }

    fun updateSettings(updated: AppSettingsEntity) {
        viewModelScope.launch {
            repository.saveAppSettings(updated)
        }
    }

    fun resetAllData(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.resetAllData()
            onComplete()
        }
    }

    // Smart Study Planner operations
    fun generateSmartPlan(targetDate: String) {
        plannerManager.generatePlan(
            targetDate = targetDate,
            subjects = subjects.value,
            chapters = chapters.value,
            tasks = tasks.value,
            sessions = sessions.value,
            admissionTargets = admissionTargets.value,
            userProfile = userProfile.value
        )
    }

    fun togglePlannerTaskSelection(taskId: String) {
        plannerManager.toggleTaskSelection(taskId)
    }

    fun updatePlannerTask(taskId: String, newDurationMins: Int, newStartTime: String? = null) {
        plannerManager.updateTask(taskId, newDurationMins, newStartTime)
    }

    fun removePlannerTask(taskId: String) {
        plannerManager.removeTaskFromDraft(taskId)
    }

    fun dismissPlannerDraft() {
        plannerManager.dismissDraft()
    }

    fun commitSmartPlanToCalendar() {
        viewModelScope.launch {
            plannerManager.commitSelectedTasksToCalendar(tasks.value)
        }
    }

    fun clearPlannerCommitResult() {
        plannerManager.clearCommitResult()
    }

    // --- Smart Revision Engine 1.0 Actions ---

    fun setActiveRevisionItemId(id: Long?) {
        _activeRevisionItemId.value = id
    }

    fun trackChapterInRevision(subjectId: Long, chapterId: Long) {
        viewModelScope.launch {
            revisionManager.trackChapter(subjectId, chapterId)
        }
    }

    fun removeChapterFromRevision(chapterId: Long) {
        viewModelScope.launch {
            revisionManager.removeChapter(chapterId)
        }
    }

    fun postponeRevisionItem(itemId: Long, days: Int = 1) {
        viewModelScope.launch {
            revisionManager.postponeItem(itemId, days)
        }
    }

    fun completeRevisionReview(
        itemId: Long,
        rating: com.example.revision.RecallRating,
        durationSeconds: Int = 0,
        focusSessionId: Long? = null,
        notes: String? = null,
        onResult: ((com.example.revision.ReviewCalculationResult?) -> Unit)? = null
    ) {
        viewModelScope.launch {
            val sessionToLink = focusSessionId ?: lastRecordedSessionId
            val res = revisionManager.completeReview(
                itemId = itemId,
                rating = rating,
                durationSeconds = durationSeconds,
                focusSessionId = sessionToLink,
                notes = notes
            )
            _activeRevisionItemId.value = null
            onResult?.invoke(res)
        }
    }

    fun launchRevisionInFocus(revisionModel: com.example.revision.TrackedRevisionUiModel) {
        _activeRevisionItemId.value = revisionModel.id
        setTimerSubjectAndChapter(revisionModel.subjectId, revisionModel.chapterId)
        setGoalTask("Revise: ${revisionModel.chapterTitle}")
        setTimerMode("POMODORO")
        setWorkDuration(revisionModel.estimatedDurationMinutes)
    }

    fun loadCalendarRevisionProposal(dailyBudgetMinutes: Int = 60) {
        viewModelScope.launch {
            val proposal = revisionManager.generateCalendarScheduleProposal(dailyBudgetMinutes)
            _proposedRevisionTasks.value = proposal
        }
    }

    fun toggleProposedRevisionTask(chapterId: Long) {
        _proposedRevisionTasks.value = _proposedRevisionTasks.value.map {
            if (it.chapterId == chapterId && !it.isAlreadyScheduled) it.copy(isSelected = !it.isSelected) else it
        }
    }

    fun commitProposedRevisionTasks(onComplete: (Int) -> Unit) {
        viewModelScope.launch {
            val count = revisionManager.commitProposedTasksToCalendar(_proposedRevisionTasks.value)
            _proposedRevisionTasks.value = emptyList()
            onComplete(count)
        }
    }

    fun clearProposedRevisionTasks() {
        _proposedRevisionTasks.value = emptyList()
    }

    // --- Study Buddy State & Operations ---
    var buddyAiProvider: StudyBuddyAiProvider = GeminiRestAiProvider()

    val buddyConversations: StateFlow<List<BuddyConversationEntity>> = repository.allBuddyConversations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentBuddyConversationId = MutableStateFlow<Long?>(null)
    val currentBuddyConversationId: StateFlow<Long?> = _currentBuddyConversationId.asStateFlow()

    private val _currentBuddyMessages = MutableStateFlow<List<BuddyMessageEntity>>(emptyList())
    val currentBuddyMessages: StateFlow<List<BuddyMessageEntity>> = _currentBuddyMessages.asStateFlow()

    private val _isSendingBuddyMessage = MutableStateFlow(false)
    val isSendingBuddyMessage: StateFlow<Boolean> = _isSendingBuddyMessage.asStateFlow()

    private val _buddyErrorMessage = MutableStateFlow<String?>(null)
    val buddyErrorMessage: StateFlow<String?> = _buddyErrorMessage.asStateFlow()

    private val _lastSuggestedActions = MutableStateFlow<List<BuddyAction>>(emptyList())
    val lastSuggestedActions: StateFlow<List<BuddyAction>> = _lastSuggestedActions.asStateFlow()

    private var buddyMessageJob: Job? = null
    private var activeConversationCollectorJob: Job? = null

    // Track initial topic/subject if navigated with context
    private val _activeBuddyTopicContext = MutableStateFlow<Pair<String?, String?>?>(null)
    val activeBuddyTopicContext: StateFlow<Pair<String?, String?>?> = _activeBuddyTopicContext.asStateFlow()

    fun selectOrCreateConversation(
        conversationId: Long? = null,
        initialTopicId: Long? = null,
        initialSubjectId: Long? = null,
        initialTopicName: String? = null,
        initialSubjectName: String? = null
    ) {
        if (initialTopicName != null || initialSubjectName != null) {
            _activeBuddyTopicContext.value = Pair(initialTopicName, initialSubjectName)
        }

        viewModelScope.launch {
            val targetId = if (conversationId != null) {
                conversationId
            } else {
                val title = if (!initialTopicName.isNullOrBlank()) {
                    "Topic: $initialTopicName"
                } else {
                    "Study Session"
                }
                repository.createBuddyConversation(
                    title = title,
                    initialContextTopicId = initialTopicId,
                    initialContextSubjectId = initialSubjectId
                )
            }
            _currentBuddyConversationId.value = targetId
            listenToMessages(targetId)
        }
    }

    private fun listenToMessages(conversationId: Long) {
        activeConversationCollectorJob?.cancel()
        activeConversationCollectorJob = viewModelScope.launch {
            repository.getBuddyMessages(conversationId).collect { msgs ->
                _currentBuddyMessages.value = msgs
            }
        }
    }

    fun startNewBuddyConversation(
        initialTopicId: Long? = null,
        initialSubjectId: Long? = null,
        initialTopicName: String? = null,
        initialSubjectName: String? = null
    ) {
        _activeBuddyTopicContext.value = if (initialTopicName != null || initialSubjectName != null) {
            Pair(initialTopicName, initialSubjectName)
        } else null
        viewModelScope.launch {
            val title = if (!initialTopicName.isNullOrBlank()) "Topic: $initialTopicName" else "Study Session"
            val id = repository.createBuddyConversation(
                title = title,
                initialContextTopicId = initialTopicId,
                initialContextSubjectId = initialSubjectId
            )
            _currentBuddyConversationId.value = id
            listenToMessages(id)
        }
    }

    fun deleteBuddyConversation(conversationId: Long) {
        viewModelScope.launch {
            repository.deleteBuddyConversation(conversationId)
            if (_currentBuddyConversationId.value == conversationId) {
                _currentBuddyConversationId.value = null
                _currentBuddyMessages.value = emptyList()
            }
        }
    }

    fun clearBuddyError() {
        _buddyErrorMessage.value = null
    }

    fun sendBuddyMessage(prompt: String, entryPointContext: String? = null) {
        val trimmed = prompt.trim()
        if (trimmed.isEmpty() || _isSendingBuddyMessage.value) return

        val convId = _currentBuddyConversationId.value
        if (convId == null) {
            viewModelScope.launch {
                val id = repository.createBuddyConversation(
                    title = trimmed.take(30),
                    initialContextTopicId = null,
                    initialContextSubjectId = null
                )
                _currentBuddyConversationId.value = id
                listenToMessages(id)
                performSendMessage(id, trimmed, entryPointContext)
            }
        } else {
            performSendMessage(convId, trimmed, entryPointContext)
        }
    }

    private fun performSendMessage(conversationId: Long, promptText: String, entryPointContext: String?) {
        _isSendingBuddyMessage.value = true
        _buddyErrorMessage.value = null

        buddyMessageJob?.cancel()
        buddyMessageJob = viewModelScope.launch {
            val userMsg = BuddyMessageEntity(
                conversationId = conversationId,
                sender = "USER",
                content = promptText,
                status = "SENT"
            )
            repository.insertBuddyMessage(userMsg)

            val verifiedFacts = buildVerifiedFacts(
                specificTopic = _activeBuddyTopicContext.value?.first,
                specificSubject = _activeBuddyTopicContext.value?.second
            )
            val intent = StudyBuddyContextEngine.classifyIntent(promptText)
            val timeRef = StudyBuddyContextEngine.getCurrentTimeReference()
            val envelope = BuddyContextEnvelope(
                queryIntent = intent,
                verifiedFacts = verifiedFacts,
                timeReference = timeRef,
                entryPointContext = entryPointContext
            )

            val history = repository.getBuddyMessagesSync(conversationId)
                .filter { it.status == "SENT" }
                .map { Pair(it.sender, it.content) }

            val request = BuddyGenerationRequest(
                conversationId = conversationId,
                userPrompt = promptText,
                recentTurns = history,
                contextEnvelope = envelope
            )

            val result = buddyAiProvider.generateReply(request)

            when (result) {
                is BuddyAiResult.Success -> {
                    _lastSuggestedActions.value = result.suggestedActions
                    val assistantMsg = BuddyMessageEntity(
                        conversationId = conversationId,
                        sender = "ASSISTANT",
                        content = result.responseText,
                        status = "SENT"
                    )
                    repository.insertBuddyMessage(assistantMsg)
                    _isSendingBuddyMessage.value = false
                }
                is BuddyAiResult.Error -> {
                    _buddyErrorMessage.value = result.errorMessage
                    val failedMsg = BuddyMessageEntity(
                        conversationId = conversationId,
                        sender = "ASSISTANT",
                        content = result.errorMessage,
                        status = "FAILED"
                    )
                    repository.insertBuddyMessage(failedMsg)
                    _isSendingBuddyMessage.value = false
                }
            }
        }
    }

    fun retryLastBuddyMessage() {
        val convId = _currentBuddyConversationId.value ?: return
        val messages = _currentBuddyMessages.value
        val lastUserMsg = messages.lastOrNull { it.sender == "USER" } ?: return
        val lastMsg = messages.lastOrNull()
        if (lastMsg != null && lastMsg.status == "FAILED") {
            viewModelScope.launch {
                repository.deleteBuddyConversation(lastMsg.id)
                performSendMessage(convId, lastUserMsg.content, null)
            }
        }
    }

    private fun buildVerifiedFacts(specificTopic: String?, specificSubject: String?): StudyHubVerifiedFacts {
        val profile = userProfile.value
        val allSubs = subjects.value
        val allChaps = chapters.value
        val allTasks = tasks.value
        val allSessions = sessions.value
        val allTargets = admissionTargets.value
        val revMetrics = revisionSummaryMetrics.value

        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val todayTasks = allTasks.filter { it.date == todayDate }
        val todaySeconds = todayStudySeconds.value

        val completedChaps = allChaps.count { it.status == "COMPLETED" }
        val totalSecs = allSessions.filter { it.status == "COMPLETED" }.sumOf { it.elapsedSeconds }

        val nextExam = allTargets
            .filter { !it.examDate.isNullOrBlank() }
            .sortedBy { it.examDate }
            .firstOrNull()

        val nextExamStr = if (nextExam != null) {
            "${nextExam.institutionName} (${nextExam.programUnit ?: nextExam.category}) on ${nextExam.examDate}"
        } else null

        val revModels = revisionUiModels.value
        val dueCount = revMetrics.dueTodayCount + revMetrics.overdueCount
        val dueRevSubjects = revModels.filter { it.isDueToday || it.isOverdue }.map { it.subjectName }.distinct()

        return StudyHubVerifiedFacts(
            studentName = profile?.displayName?.ifBlank { "Student" } ?: "Student",
            academicLevel = profile?.academicLevel,
            admissionTrack = profile?.admissionTrack,
            dailyGoalHours = profile?.let { it.dailyGoalMinutes / 60f },
            preferredStudyTime = profile?.preferredStudyTime,
            todayPlannedTasksCount = todayTasks.size,
            todayPlannedTasksSummary = todayTasks.map { "${it.title} (${it.status})" },
            totalSubjectsCount = allSubs.size,
            totalChaptersCount = allChaps.size,
            completedChaptersCount = completedChaps,
            incompleteChaptersCount = allChaps.size - completedChaps,
            dueRevisionsCount = dueCount,
            dueRevisionSubjects = dueRevSubjects,
            todayStudyMinutesRecorded = (todaySeconds / 60).toInt(),
            totalStudyHoursRecorded = totalSecs / 3600f,
            nextExamTarget = nextExamStr,
            specificTopicName = specificTopic,
            specificSubjectName = specificSubject,
            notesCount = notes.value.size
        )
    }
}

