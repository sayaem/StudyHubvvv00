package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.local.AdmissionTargetEntity
import com.example.data.local.ChapterEntity
import com.example.data.local.FocusGamificationEntity
import com.example.data.local.StudySessionEntity
import com.example.data.local.StudyTaskEntity
import com.example.data.local.SubjectEntity
import com.example.data.local.UserProfile
import com.example.ui.components.StudyHubDetailTopBar
import com.example.ui.screens.stats.DateRangeFilter
import com.example.ui.screens.stats.StatsAnalyticsEngine
import com.example.ui.screens.stats.StatsConsistencyHeatmapSection
import com.example.revision.RevisionSummaryMetrics
import com.example.ui.screens.stats.StatsExamReadinessSection
import com.example.ui.screens.stats.StatsFocusBreakdownSection
import com.example.ui.screens.stats.StatsOverviewSection
import com.example.ui.screens.stats.StatsPeriodSelector
import com.example.ui.screens.stats.StatsPlannedVsActualSection
import com.example.ui.screens.stats.StatsRevisionAnalyticsSection
import com.example.ui.screens.stats.StatsSmartInsightsSection
import com.example.ui.screens.stats.StatsSubjectIntelligenceSection
import com.example.ui.screens.stats.StatsTrendChartSection

@Composable
fun ProgressAnalyticsScreen(
    userProfile: UserProfile?,
    sessions: List<StudySessionEntity>,
    subjects: List<SubjectEntity>,
    chapters: List<ChapterEntity>,
    tasks: List<StudyTaskEntity> = emptyList(),
    admissionTargets: List<AdmissionTargetEntity> = emptyList(),
    gamification: FocusGamificationEntity? = null,
    revisionMetrics: RevisionSummaryMetrics = RevisionSummaryMetrics(),
    onOpenRevisionHub: () -> Unit = {},
    onBack: () -> Unit,
    onStartFocus: () -> Unit = {},
    onNavigateToCalendar: () -> Unit = {},
    onNavigateToSyllabus: () -> Unit = {},
    onNavigateToAdmission: () -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf(DateRangeFilter.SEVEN_DAYS) }

    // Analytics calculations derived deterministically from real Room data
    val overview = remember(selectedFilter, sessions, userProfile) {
        StatsAnalyticsEngine.calculatePeriodOverview(
            filter = selectedFilter,
            sessions = sessions,
            userProfile = userProfile
        )
    }

    val dailyStudyPoints = remember(selectedFilter, sessions) {
        StatsAnalyticsEngine.getDailyStudyPoints(
            filter = selectedFilter,
            sessions = sessions
        )
    }

    val heatmapDays = remember(sessions) {
        StatsAnalyticsEngine.generateConsistencyHeatmap(
            sessions = sessions,
            daysCount = 28
        )
    }

    val subjectIntelligence = remember(selectedFilter, sessions, subjects, chapters, tasks) {
        StatsAnalyticsEngine.calculateSubjectIntelligence(
            filter = selectedFilter,
            sessions = sessions,
            subjects = subjects,
            chapters = chapters,
            tasks = tasks
        )
    }

    val focusBreakdown = remember(selectedFilter, sessions) {
        StatsAnalyticsEngine.calculateFocusSessionBreakdown(
            filter = selectedFilter,
            sessions = sessions
        )
    }

    val plannedVsActual = remember(selectedFilter, tasks, sessions) {
        StatsAnalyticsEngine.calculatePlannedVsActual(
            filter = selectedFilter,
            tasks = tasks,
            sessions = sessions
        )
    }

    val smartInsights = remember(overview, focusBreakdown, subjectIntelligence, gamification, admissionTargets, userProfile) {
        StatsAnalyticsEngine.generateInsights(
            overview = overview,
            breakdown = focusBreakdown,
            subjects = subjectIntelligence,
            gamification = gamification,
            admissionTargets = admissionTargets,
            userProfile = userProfile
        )
    }

    val examReadinessList = remember(admissionTargets) {
        StatsAnalyticsEngine.getExamReadinessList(admissionTargets)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        StudyHubDetailTopBar(
            title = "Study Analytics",
            onBackClick = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Period Selector (Today, 7 days, 30 days, All time)
            StatsPeriodSelector(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Study Overview (Total time, comparisons, goal completion, sessions, averages)
            StatsOverviewSection(overview = overview)

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Study Time Trends (Interactive bar chart with inspectable bars)
            StatsTrendChartSection(points = dailyStudyPoints)

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Study Consistency & Heatmap (28-day activity grid, streak, goal met days)
            StatsConsistencyHeatmapSection(
                days = heatmapDays,
                gamification = gamification,
                dailyGoalMinutes = userProfile?.dailyGoalMinutes?.takeIf { it > 0 } ?: 240
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 5. Subject Intelligence (Time share, chapters completed, actionable recommendations)
            StatsSubjectIntelligenceSection(
                subjects = subjectIntelligence,
                onOpenSyllabus = onNavigateToSyllabus,
                onStartFocus = onStartFocus
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 6. Focus Session Analysis (Completion rate, morning/evening distribution, focus modes)
            StatsFocusBreakdownSection(breakdown = focusBreakdown)

            Spacer(modifier = Modifier.height(20.dp))

            // 7. Planned vs Actual Study (Calendar tasks vs actual focus sessions)
            StatsPlannedVsActualSection(
                analysis = plannedVsActual,
                onOpenCalendar = onNavigateToCalendar
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 8. Spaced Repetition & Revision Analytics
            StatsRevisionAnalyticsSection(
                metrics = revisionMetrics,
                onOpenRevisionHub = onOpenRevisionHub
            )

            if (smartInsights.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                // 8. Smart Study Insights (Deterministic recommendations)
                StatsSmartInsightsSection(
                    insights = smartInsights,
                    onActionRoute = { route ->
                        when (route) {
                            "focus" -> onStartFocus()
                            "syllabus" -> onNavigateToSyllabus()
                            "calendar" -> onNavigateToCalendar()
                            "admission" -> onNavigateToAdmission()
                        }
                    }
                )
            }

            if (examReadinessList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                // 9. Exam & Admission Readiness
                StatsExamReadinessSection(
                    readinessItems = examReadinessList,
                    onOpenAdmissionCalendar = onNavigateToAdmission
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
