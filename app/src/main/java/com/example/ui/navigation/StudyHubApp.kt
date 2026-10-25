package com.example.ui.navigation

import android.Manifest
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.AppSettingsEntity
import com.example.ui.StudyHubViewModel
import com.example.ui.components.NavDestination
import com.example.ui.components.StudyHubBottomNav
import com.example.ui.components.StudyHubTopBar
import com.example.ui.screens.AdmissionCalendarScreen
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.FocusTimerScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NotesScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ProgressAnalyticsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.SyllabusScreen
import com.example.ui.screens.ToolsScreen
import com.example.ui.screens.revision.RevisionHubScreen
import com.example.ui.theme.BrandTeal
import com.example.ui.theme.StudyHubTheme
import com.example.ui.theme.ThemeMode

import com.example.notifications.StudyHubNotificationManager
import com.example.ui.components.InAppNotificationBanner
import java.util.Locale

enum class AppScreen {
    SPLASH,
    ONBOARDING,
    MAIN,
    PROFILE,
    NOTIFICATIONS,
    ADMISSION_CALENDAR,
    PROGRESS_ANALYTICS,
    NOTES,
    SETTINGS,
    REVISION_HUB
}

sealed class NavigationDestination {
    data class MainTab(val destination: NavDestination) : NavigationDestination()
    object Profile : NavigationDestination()
    object Notifications : NavigationDestination()
    object AdmissionCalendar : NavigationDestination()
    object ProgressAnalytics : NavigationDestination()
    object Notes : NavigationDestination()
    object Settings : NavigationDestination()
    object Onboarding : NavigationDestination()
    object Splash : NavigationDestination()
    object RevisionHub : NavigationDestination()
}

@Composable
fun StudyHubApp(
    viewModel: StudyHubViewModel,
    initialTargetRoute: String? = null
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()
    val chapters by viewModel.chapters.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val sessions by viewModel.sessions.collectAsStateWithLifecycle()
    val admissionTargets by viewModel.admissionTargets.collectAsStateWithLifecycle()
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val unreadNotificationsCount by viewModel.unreadNotificationsCount.collectAsStateWithLifecycle()
    val appSettings by viewModel.appSettings.collectAsStateWithLifecycle()

    val todayStudySeconds by viewModel.todayStudySeconds.collectAsStateWithLifecycle()
    val overallSyllabusProgress by viewModel.overallSyllabusProgress.collectAsStateWithLifecycle()
    val smartStudyReminders by viewModel.smartStudyReminders.collectAsStateWithLifecycle()
    val calendarReminders by viewModel.calendarReminders.collectAsStateWithLifecycle()
    val syllabusReminders by viewModel.syllabusReminders.collectAsStateWithLifecycle()
    val goalReminders by viewModel.goalReminders.collectAsStateWithLifecycle()
    val notificationSetEnabled by viewModel.notificationSetEnabled.collectAsStateWithLifecycle()

    // Smart Study Planner state
    val plannerDraft by viewModel.plannerDraft.collectAsStateWithLifecycle()
    val plannerStatus by viewModel.plannerStatus.collectAsStateWithLifecycle()
    val plannerCommitResult by viewModel.plannerCommitResult.collectAsStateWithLifecycle()

    // Timer state
    val timerStatus by viewModel.timerStatus.collectAsStateWithLifecycle()
    val timerMode by viewModel.timerMode.collectAsStateWithLifecycle()
    val pomodoroPhase by viewModel.pomodoroPhase.collectAsStateWithLifecycle()
    val currentCycle by viewModel.currentCycle.collectAsStateWithLifecycle()
    val targetCycles by viewModel.targetCycles.collectAsStateWithLifecycle()
    val timeRemainingSeconds by viewModel.timeRemainingSeconds.collectAsStateWithLifecycle()
    val totalDurationSeconds by viewModel.totalDurationSeconds.collectAsStateWithLifecycle()
    val selectedSessionType by viewModel.selectedSessionType.collectAsStateWithLifecycle()
    val timerSubjectId by viewModel.timerSubjectId.collectAsStateWithLifecycle()
    val timerChapterId by viewModel.timerChapterId.collectAsStateWithLifecycle()
    val selectedGoalTask by viewModel.selectedGoalTask.collectAsStateWithLifecycle()
    val isStrictFocus by viewModel.isStrictFocus.collectAsStateWithLifecycle()
    val distractionCount by viewModel.distractionCount.collectAsStateWithLifecycle()
    val workMinutes by viewModel.workMinutes.collectAsStateWithLifecycle()
    val shortBreakMinutes by viewModel.shortBreakMinutes.collectAsStateWithLifecycle()
    val longBreakMinutes by viewModel.longBreakMinutes.collectAsStateWithLifecycle()
    val autoStartBreaks by viewModel.autoStartBreaks.collectAsStateWithLifecycle()
    val autoStartNextFocus by viewModel.autoStartNextFocus.collectAsStateWithLifecycle()
    val ambientSoundType by viewModel.ambientSoundType.collectAsStateWithLifecycle()
    val ambientVolume by viewModel.ambientVolume.collectAsStateWithLifecycle()
    val focusPresets by viewModel.focusPresets.collectAsStateWithLifecycle()
    val focusGamification by viewModel.focusGamification.collectAsStateWithLifecycle()

    // Smart Revision Engine 1.0 State
    val revisionUiModels by viewModel.revisionUiModels.collectAsStateWithLifecycle()
    val revisionSummaryMetrics by viewModel.revisionSummaryMetrics.collectAsStateWithLifecycle()
    val allRevisionHistory by viewModel.allRevisionHistory.collectAsStateWithLifecycle()
    val proposedRevisionTasks by viewModel.proposedRevisionTasks.collectAsStateWithLifecycle()
    val activeRevisionItemId by viewModel.activeRevisionItemId.collectAsStateWithLifecycle()

    val backStack = remember { androidx.compose.runtime.mutableStateListOf<NavigationDestination>(NavigationDestination.Splash) }

    fun navigateTo(dest: NavigationDestination) {
        val currentTop = backStack.lastOrNull()
        if (currentTop == dest) return

        if (dest is NavigationDestination.MainTab) {
            // When navigating between main bottom tabs, maintain tab history with HOME as root
            if (currentTop is NavigationDestination.MainTab) {
                if (dest.destination == NavDestination.HOME) {
                    // Return to root HOME tab
                    while (backStack.size > 1 && backStack.last() !is NavigationDestination.MainTab) {
                        backStack.removeAt(backStack.size - 1)
                    }
                    if (backStack.lastOrNull() != NavigationDestination.MainTab(NavDestination.HOME)) {
                        backStack.removeAt(backStack.size - 1)
                        backStack.add(NavigationDestination.MainTab(NavDestination.HOME))
                    }
                } else {
                    // Switch tab
                    if (backStack.contains(dest)) {
                        backStack.remove(dest)
                    }
                    backStack.add(dest)
                }
            } else {
                // Secondary screen -> Main tab: push tab destination
                if (backStack.contains(dest)) {
                    backStack.remove(dest)
                }
                backStack.add(dest)
            }
        } else {
            // Push secondary screen onto stack
            if (backStack.lastOrNull() == dest) return
            backStack.add(dest)
        }
    }

    fun goBack() {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.size - 1)
        }
    }

    fun navigateToRoute(route: String?) {
        if (route.isNullOrBlank()) return
        when (route.lowercase(Locale.ROOT)) {
            "focus" -> navigateTo(NavigationDestination.MainTab(NavDestination.FOCUS))
            "syllabus" -> navigateTo(NavigationDestination.MainTab(NavDestination.SYLLABUS))
            "calendar", "tasks" -> navigateTo(NavigationDestination.MainTab(NavDestination.CALENDAR))
            "admission" -> navigateTo(NavigationDestination.AdmissionCalendar)
            "notes" -> navigateTo(NavigationDestination.Notes)
            "tools" -> navigateTo(NavigationDestination.MainTab(NavDestination.TOOLS))
            "notifications" -> navigateTo(NavigationDestination.Notifications)
            "profile" -> navigateTo(NavigationDestination.Profile)
            "analytics", "goals", "stats", "progress" -> navigateTo(NavigationDestination.ProgressAnalytics)
            "settings" -> navigateTo(NavigationDestination.Settings)
            "revision", "revisionhub", "spaced_repetition" -> navigateTo(NavigationDestination.RevisionHub)
            else -> navigateTo(NavigationDestination.MainTab(NavDestination.HOME))
        }
    }

    val pendingRoute by viewModel.targetNavigationRoute.collectAsStateWithLifecycle()
    LaunchedEffect(pendingRoute) {
        pendingRoute?.let { route ->
            navigateToRoute(route)
            viewModel.clearTargetNavigationRoute()
        }
    }

    LaunchedEffect(initialTargetRoute) {
        if (!initialTargetRoute.isNullOrBlank()) {
            navigateToRoute(initialTargetRoute)
        }
    }

    // Quick action trigger flags
    var requestAddSubjectOnSyllabus by remember { mutableStateOf(false) }
    var requestAddAdmissionOnCalendar by remember { mutableStateOf(false) }

    // Notification Permission Rationale Dialog State
    var showNotificationRationaleDialog by remember { mutableStateOf(false) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.initializeNotificationsOnLaunch()
        }
    }

    // Determine current ThemeMode
    val currentThemeMode = when (appSettings?.themeMode) {
        "DARK" -> ThemeMode.DARK
        "SYSTEM" -> ThemeMode.SYSTEM
        else -> ThemeMode.LIGHT
    }

    // Total hours focused
    val totalHoursFocused = sessions.filter { it.status == "COMPLETED" }.sumOf { it.elapsedSeconds } / 3600f
    val completedChaptersCount = chapters.count { it.status == "COMPLETED" }

    val isProfileLoaded by viewModel.isProfileLoaded.collectAsStateWithLifecycle()

    var isSubjectDetailOpen by remember { mutableStateOf(false) }

    val currentDestination = backStack.lastOrNull() ?: NavigationDestination.MainTab(NavDestination.HOME)

    val currentScreen = when (currentDestination) {
        is NavigationDestination.Splash -> AppScreen.SPLASH
        is NavigationDestination.Onboarding -> AppScreen.ONBOARDING
        is NavigationDestination.MainTab -> AppScreen.MAIN
        is NavigationDestination.Profile -> AppScreen.PROFILE
        is NavigationDestination.Notifications -> AppScreen.NOTIFICATIONS
        is NavigationDestination.AdmissionCalendar -> AppScreen.ADMISSION_CALENDAR
        is NavigationDestination.ProgressAnalytics -> AppScreen.PROGRESS_ANALYTICS
        is NavigationDestination.Notes -> AppScreen.NOTES
        is NavigationDestination.Settings -> AppScreen.SETTINGS
        is NavigationDestination.RevisionHub -> AppScreen.REVISION_HUB
    }

    val currentNavDestination = when (currentDestination) {
        is NavigationDestination.MainTab -> currentDestination.destination
        else -> {
            (backStack.findLast { it is NavigationDestination.MainTab } as? NavigationDestination.MainTab)?.destination ?: NavDestination.HOME
        }
    }

    LaunchedEffect(currentNavDestination) {
        isSubjectDetailOpen = false
    }

    // System Back Handler for app navigation stack
    BackHandler(enabled = backStack.size > 1 && !showNotificationRationaleDialog) {
        goBack()
    }

    if (showNotificationRationaleDialog) {
        BackHandler {
            showNotificationRationaleDialog = false
        }
    }

    StudyHubTheme(themeMode = currentThemeMode) {
        Box(modifier = Modifier.fillMaxSize()) {
            Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
                when (screen) {
                AppScreen.SPLASH -> {
                    SplashScreen(
                        isReady = isProfileLoaded,
                        onSplashFinished = {
                            backStack.clear()
                            if (userProfile?.isOnboardingCompleted == true) {
                                backStack.add(NavigationDestination.MainTab(NavDestination.HOME))
                            } else {
                                backStack.add(NavigationDestination.Onboarding)
                            }
                        }
                    )
                }

                AppScreen.ONBOARDING -> {
                    OnboardingScreen(
                        onComplete = { name, avatarId, level, group, year, track, goalH, time ->
                            viewModel.saveOnboardingProfile(
                                name = name,
                                avatarId = avatarId,
                                academicLevel = level,
                                group = group,
                                targetYear = year,
                                admissionTrack = track,
                                dailyGoalHours = goalH,
                                preferredTime = time,
                                onSuccess = {
                                    backStack.clear()
                                    backStack.add(NavigationDestination.MainTab(NavDestination.HOME))
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                        !viewModel.notificationManager.hasNotificationPermission()
                                    ) {
                                        showNotificationRationaleDialog = true
                                    }
                                }
                            )
                        }
                    )
                }

                AppScreen.MAIN -> {
                    Scaffold(
                        topBar = {
                            if (!isSubjectDetailOpen || currentNavDestination != NavDestination.SYLLABUS) {
                                StudyHubTopBar(
                                    unreadNotificationCount = unreadNotificationsCount,
                                    onNotificationClick = { navigateTo(NavigationDestination.Notifications) },
                                    onSettingsClick = { navigateTo(NavigationDestination.Settings) }
                                )
                            }
                        },
                        bottomBar = {
                            StudyHubBottomNav(
                                currentDestination = currentNavDestination,
                                onNavigate = { destination ->
                                    navigateTo(NavigationDestination.MainTab(destination))
                                }
                            )
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (currentNavDestination) {
                                NavDestination.HOME -> {
                                    HomeScreen(
                                        userProfile = userProfile,
                                        todayStudySeconds = todayStudySeconds,
                                        overallSyllabusProgress = overallSyllabusProgress,
                                        subjects = subjects,
                                        chapters = chapters,
                                        tasks = tasks,
                                        admissionTargets = admissionTargets,
                                        revisionMetrics = revisionSummaryMetrics,
                                        onOpenRevisionHub = {
                                            navigateTo(NavigationDestination.RevisionHub)
                                        },
                                        onAddSubjectClick = {
                                            requestAddSubjectOnSyllabus = true
                                            navigateTo(NavigationDestination.MainTab(NavDestination.SYLLABUS))
                                        },
                                        onStartFocusClick = {
                                            navigateTo(NavigationDestination.MainTab(NavDestination.FOCUS))
                                        },
                                        onTodayPlanClick = {
                                            navigateTo(NavigationDestination.MainTab(NavDestination.CALENDAR))
                                        },
                                        onAddAdmissionClick = {
                                            requestAddAdmissionOnCalendar = true
                                            navigateTo(NavigationDestination.AdmissionCalendar)
                                        },
                                        onOpenStatsClick = {
                                            navigateTo(NavigationDestination.ProgressAnalytics)
                                        },
                                        onOpenAdmissionCalendar = {
                                            navigateTo(NavigationDestination.AdmissionCalendar)
                                        },
                                        onSubjectClick = {
                                            navigateTo(NavigationDestination.MainTab(NavDestination.SYLLABUS))
                                        },
                                        onAvatarClick = {
                                            navigateTo(NavigationDestination.Profile)
                                        }
                                    )
                                }

                                NavDestination.SYLLABUS -> {
                                    SyllabusScreen(
                                        subjects = subjects,
                                        chapters = chapters,
                                        onAddSubject = { name, bangla, color, icon, cat, count ->
                                            viewModel.createSubject(name, bangla, color, icon, cat, count) {}
                                        },
                                        onDeleteSubject = { viewModel.deleteSubject(it) },
                                        onUpdateChapterStatus = { chap, status ->
                                            viewModel.updateChapterStatus(chap, status)
                                        },
                                        onAddChapter = { subId, title ->
                                            viewModel.addChapter(subId, title)
                                        },
                                        onDeleteChapter = { viewModel.deleteChapter(it) },
                                        revisionItems = revisionUiModels,
                                        onTrackChapterInRevision = { subId, chapId ->
                                            viewModel.trackChapterInRevision(subId, chapId)
                                        },
                                        onRemoveChapterFromRevision = { chapId ->
                                            viewModel.removeChapterFromRevision(chapId)
                                        },
                                        onReviewNow = { itemId, rating, duration, notes ->
                                            viewModel.completeRevisionReview(itemId, rating, duration, notes = notes)
                                        },
                                        onOpenRevisionHub = {
                                            navigateTo(NavigationDestination.RevisionHub)
                                        },
                                        initialAddSubjectRequested = requestAddSubjectOnSyllabus,
                                        onAddSubjectDismissed = { requestAddSubjectOnSyllabus = false },
                                        onSubjectDetailStateChanged = { isOpen ->
                                            isSubjectDetailOpen = isOpen
                                        }
                                    )
                                }

                                NavDestination.FOCUS -> {
                                    FocusTimerScreen(
                                        timerStatus = timerStatus,
                                        timerMode = timerMode,
                                        pomodoroPhase = pomodoroPhase,
                                        currentCycle = currentCycle,
                                        targetCycles = targetCycles,
                                        timeRemainingSeconds = timeRemainingSeconds,
                                        totalDurationSeconds = totalDurationSeconds,
                                        selectedSessionType = selectedSessionType,
                                        selectedSubjectId = timerSubjectId,
                                        selectedChapterId = timerChapterId,
                                        selectedGoalTask = selectedGoalTask,
                                        isStrictFocus = isStrictFocus,
                                        distractionCount = distractionCount,
                                        workMinutes = workMinutes,
                                        shortBreakMinutes = shortBreakMinutes,
                                        longBreakMinutes = longBreakMinutes,
                                        autoStartBreaks = autoStartBreaks,
                                        autoStartNextFocus = autoStartNextFocus,
                                        ambientSoundType = ambientSoundType,
                                        ambientVolume = ambientVolume,
                                        subjects = subjects,
                                        chapters = chapters,
                                        sessions = sessions,
                                        presets = focusPresets,
                                        gamification = focusGamification,
                                        dailyGoalMinutes = userProfile?.dailyGoalMinutes ?: 240,
                                        onSelectMode = { viewModel.setTimerMode(it) },
                                        onSelectDurationMinutes = { viewModel.selectTimerDuration(it, "CUSTOM") },
                                        onApplyPreset = { viewModel.applyPreset(it) },
                                        onSaveCustomPreset = { title, mode, workMins, shortMins, longMins, cycles, isStrict, autoB, autoN ->
                                            viewModel.saveCustomPreset(title, mode, workMins, shortMins, longMins, cycles, isStrict, autoB, autoN)
                                        },
                                        onDeletePreset = { viewModel.deleteFocusPreset(it) },
                                        onUpdateSettings = { workMins, shortMins, longMins, cycles, autoB, autoN ->
                                            viewModel.updateFocusSettings(workMins, shortMins, longMins, cycles, autoB, autoN)
                                        },
                                        onSelectSubject = { viewModel.setTimerSubject(it) },
                                        onSelectChapter = { viewModel.setTimerChapter(it) },
                                        onSetGoalTask = { viewModel.setGoalTask(it) },
                                        onRecordDistraction = { viewModel.recordDistraction() },
                                        onToggleStrict = { viewModel.toggleStrictFocus(it) },
                                        onSetAmbientSound = { viewModel.setAmbientSound(it) },
                                        onSetAmbientVolume = { viewModel.setAmbientVolume(it) },
                                        onStartTimer = { viewModel.startTimer() },
                                        onPauseTimer = { viewModel.pauseTimer() },
                                        onResumeTimer = { viewModel.resumeTimer() },
                                        onResetTimer = { viewModel.resetTimer() },
                                        onSkipPhase = { viewModel.skipPhase() },
                                        onFinishTimer = { completed, rating, notesText, dist ->
                                            viewModel.finishTimer(completed, rating, notesText, dist)
                                        },
                                        activeRevisionItemId = activeRevisionItemId,
                                        onCompleteRevisionReview = { itemId, rating, duration, notes ->
                                            viewModel.completeRevisionReview(
                                                itemId = itemId,
                                                rating = rating,
                                                durationSeconds = duration,
                                                notes = notes
                                            )
                                        }
                                    )
                                }


                                NavDestination.CALENDAR -> {
                                    CalendarScreen(
                                        tasks = tasks,
                                        admissionTargets = admissionTargets,
                                        subjects = subjects,
                                        chapters = chapters,
                                        sessions = sessions,
                                        userProfile = userProfile,
                                        gamification = focusGamification,
                                        plannerDraft = plannerDraft,
                                        plannerStatus = plannerStatus,
                                        plannerCommitResult = plannerCommitResult,
                                        onGenerateSmartPlan = { targetDate -> viewModel.generateSmartPlan(targetDate) },
                                        onTogglePlannerTaskSelection = { taskId -> viewModel.togglePlannerTaskSelection(taskId) },
                                        onUpdatePlannerTaskDuration = { taskId, dur -> viewModel.updatePlannerTask(taskId, dur) },
                                        onRemovePlannerTask = { taskId -> viewModel.removePlannerTask(taskId) },
                                        onDismissPlannerDraft = { viewModel.dismissPlannerDraft() },
                                        onCommitSmartPlanToCalendar = { viewModel.commitSmartPlanToCalendar() },
                                        onClearPlannerCommitResult = { viewModel.clearPlannerCommitResult() },
                                        onCreateTask = { title, date, time, dur, subId, priority, notesText ->
                                            viewModel.createTask(title, date, time, dur, subId, null, priority, "STUDY", notesText)
                                        },
                                        onCreatePlan = { title, date, time, dur, subId, chapId, priority, planType, notesText ->
                                            viewModel.createTask(title, date, time, dur, subId, chapId, priority, planType, notesText)
                                        },
                                        onUpdatePlan = { updatedTask ->
                                            viewModel.updateTask(updatedTask)
                                        },
                                        onToggleTaskStatus = { viewModel.toggleTaskStatus(it) },
                                        onDeleteTask = { viewModel.deleteTask(it) },
                                        onLaunchInFocus = { subId, chapId, taskTitle, durationMins ->
                                            viewModel.setTimerSubject(subId)
                                            viewModel.setGoalTask(taskTitle)
                                            viewModel.selectTimerDuration(durationMins, "POMODORO_FOCUS")
                                            navigateTo(NavigationDestination.MainTab(NavDestination.FOCUS))
                                        },
                                        onNavigateToNotifications = { navigateTo(NavigationDestination.Notifications) },
                                        onNavigateToProfile = { navigateTo(NavigationDestination.Profile) }
                                    )
                                }

                                NavDestination.TOOLS -> {
                                    ToolsScreen(
                                        onOpenAdmissionCalendar = { navigateTo(NavigationDestination.AdmissionCalendar) },
                                        onOpenAnalytics = { navigateTo(NavigationDestination.ProgressAnalytics) },
                                        onOpenNotes = { navigateTo(NavigationDestination.Notes) },
                                        onOpenRevisionHub = { navigateTo(NavigationDestination.RevisionHub) }
                                    )
                                }
                            }
                        }
                    }
                }

                AppScreen.NOTIFICATIONS -> {
                    NotificationsScreen(
                        notifications = notifications,
                        onMarkAllRead = { viewModel.markAllNotificationsAsRead() },
                        onDeleteNotification = { viewModel.deleteNotification(it) },
                        onNavigateRoute = { route -> navigateToRoute(route) },
                        onBack = { goBack() }
                    )
                }

                AppScreen.ADMISSION_CALENDAR -> {
                    AdmissionCalendarScreen(
                        targets = admissionTargets,
                        tasks = tasks,
                        onSaveTarget = { viewModel.saveAdmissionTarget(it) {} },
                        onDeleteTarget = { viewModel.deleteAdmissionTarget(it) },
                        onCreateTask = { title, date, targetId, priority, notes ->
                            viewModel.createTargetTask(title, date, targetId, priority, notes)
                        },
                        onToggleTaskStatus = { viewModel.toggleTaskStatus(it) },
                        onDeleteTask = { viewModel.deleteTask(it) },
                        onBack = { goBack() },
                        initialAddRequested = requestAddAdmissionOnCalendar,
                        onAddDismissed = { requestAddAdmissionOnCalendar = false }
                    )
                }

                AppScreen.PROGRESS_ANALYTICS -> {
                    ProgressAnalyticsScreen(
                        userProfile = userProfile,
                        sessions = sessions,
                        subjects = subjects,
                        chapters = chapters,
                        tasks = tasks,
                        admissionTargets = admissionTargets,
                        gamification = focusGamification,
                        revisionMetrics = revisionSummaryMetrics,
                        onOpenRevisionHub = {
                            navigateTo(NavigationDestination.RevisionHub)
                        },
                        onBack = { goBack() },
                        onStartFocus = {
                            navigateTo(NavigationDestination.MainTab(NavDestination.FOCUS))
                        },
                        onNavigateToCalendar = {
                            navigateTo(NavigationDestination.MainTab(NavDestination.CALENDAR))
                        },
                        onNavigateToSyllabus = {
                            navigateTo(NavigationDestination.MainTab(NavDestination.SYLLABUS))
                        },
                        onNavigateToAdmission = {
                            navigateTo(NavigationDestination.AdmissionCalendar)
                        }
                    )
                }

                AppScreen.PROFILE -> {
                    ProfileScreen(
                        userProfile = userProfile,
                        completedChaptersCount = completedChaptersCount,
                        totalStudyHours = totalHoursFocused,
                        onOpenSettings = { navigateTo(NavigationDestination.Settings) },
                        onOpenAdmissionCalendar = { navigateTo(NavigationDestination.AdmissionCalendar) },
                        onOpenAnalytics = { navigateTo(NavigationDestination.ProgressAnalytics) },
                        onOpenNotes = { navigateTo(NavigationDestination.Notes) },
                        onEditProfile = { navigateTo(NavigationDestination.Onboarding) },
                        onBack = { goBack() }
                    )
                }

                AppScreen.NOTES -> {
                    NotesScreen(
                        notes = notes,
                        subjects = subjects,
                        onSaveNote = { viewModel.saveNote(it) {} },
                        onDeleteNote = { viewModel.deleteNote(it) },
                        onBack = { goBack() }
                    )
                }

                AppScreen.SETTINGS -> {
                    SettingsScreen(
                        appSettings = appSettings,
                        currentThemeMode = currentThemeMode,
                        onThemeModeChanged = { viewModel.updateThemeMode(it) },
                        onSettingsUpdated = { viewModel.updateSettings(it) },
                        onOpenNotifications = { navigateTo(NavigationDestination.Notifications) },
                        onMasterToggleChanged = { viewModel.updateMasterNotificationToggle(it) },
                        smartRemindersEnabled = smartStudyReminders,
                        calendarRemindersEnabled = calendarReminders,
                        syllabusRemindersEnabled = syllabusReminders,
                        goalRemindersEnabled = goalReminders,
                        notificationSetEnabled = notificationSetEnabled,
                        onCategoryToggleChanged = { cat, enabled ->
                            viewModel.updateSmartRemindersCategory(cat, enabled)
                        },
                        onResetOnboarding = {
                            navigateTo(NavigationDestination.Onboarding)
                        },
                        onResetAllData = {
                            viewModel.resetAllData {
                                backStack.clear()
                                backStack.add(NavigationDestination.Onboarding)
                            }
                        },
                        onBack = { goBack() }
                    )
                }

                AppScreen.REVISION_HUB -> {
                    RevisionHubScreen(
                        items = revisionUiModels,
                        metrics = revisionSummaryMetrics,
                        history = allRevisionHistory,
                        proposedCalendarTasks = proposedRevisionTasks,
                        onCompleteReview = { itemId, rating, durationSeconds, notes ->
                            viewModel.completeRevisionReview(itemId, rating, durationSeconds, notes = notes)
                        },
                        onLaunchInFocus = { model ->
                            viewModel.launchRevisionInFocus(model)
                            navigateTo(NavigationDestination.MainTab(NavDestination.FOCUS))
                        },
                        onPostponeItem = { itemId ->
                            viewModel.postponeRevisionItem(itemId)
                        },
                        onRemoveFromTracking = { chapterId ->
                            viewModel.removeChapterFromRevision(chapterId)
                        },
                        onLoadCalendarProposal = {
                            viewModel.loadCalendarRevisionProposal()
                        },
                        onToggleProposedTask = { chapterId ->
                            viewModel.toggleProposedRevisionTask(chapterId)
                        },
                        onCommitCalendarTasks = { onComplete ->
                            viewModel.commitProposedRevisionTasks(onComplete)
                        },
                        onClearCalendarProposal = {
                            viewModel.clearProposedRevisionTasks()
                        },
                        onNavigateToSyllabus = {
                            navigateTo(NavigationDestination.MainTab(NavDestination.SYLLABUS))
                        },
                        onBack = { goBack() }
                    )
                }
            }
            }

            InAppNotificationBanner(
                onNavigateRoute = { route ->
                    navigateToRoute(route)
                }
            )
        }

        // Notification rationale dialog per PRD Section 8.13 & Appendix B
        if (showNotificationRationaleDialog) {
            AlertDialog(
                onDismissRequest = { showNotificationRationaleDialog = false },
                title = { Text("Stay on Track", fontWeight = FontWeight.Bold) },
                text = {
                    Text("Enable reminders so important study tasks and admission exam dates don't slip by.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showNotificationRationaleDialog = false
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandTeal)
                    ) {
                        Text("Enable Reminders")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showNotificationRationaleDialog = false }) {
                        Text("Maybe Later")
                    }
                }
            )
        }
    }
}
