package com.example.ui.screens.revision

import android.os.Build
import androidx.activity.compose.BackHandler
import kotlin.math.max
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.RevisionHistoryEntity
import com.example.revision.DurationConfidence
import com.example.revision.ProposedRevisionTask
import com.example.revision.RecallRating
import com.example.revision.RevisionSummaryMetrics
import com.example.revision.TrackedRevisionUiModel
import com.example.ui.components.RecallFeedbackDialog
import com.example.ui.components.getSubjectIcon
import com.example.ui.components.parseColorHex
import com.example.ui.theme.BrandTeal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class RevisionHubTab(val label: String) {
    DUE_TODAY("Due Today"),
    OVERDUE("Overdue"),
    UPCOMING("Upcoming"),
    ALL_TRACKED("All Tracked"),
    HISTORY("History"),
    ANALYTICS("Analytics")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RevisionHubScreen(
    items: List<TrackedRevisionUiModel>,
    metrics: RevisionSummaryMetrics,
    history: List<RevisionHistoryEntity>,
    proposedCalendarTasks: List<ProposedRevisionTask>,
    onCompleteReview: (itemId: Long, rating: RecallRating, durationSeconds: Int, notes: String?) -> Unit,
    onLaunchInFocus: (TrackedRevisionUiModel) -> Unit,
    onPostponeItem: (itemId: Long) -> Unit,
    onRemoveFromTracking: (chapterId: Long) -> Unit,
    onLoadCalendarProposal: () -> Unit,
    onToggleProposedTask: (chapterId: Long) -> Unit,
    onCommitCalendarTasks: (onComplete: (Int) -> Unit) -> Unit,
    onClearCalendarProposal: () -> Unit,
    onNavigateToSyllabus: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    var selectedTab by remember { mutableStateOf(RevisionHubTab.DUE_TODAY) }
    var selectedSubjectFilter by remember { mutableStateOf<String?>("All") }

    // Active review feedback dialog state
    var reviewDialogTarget by remember { mutableStateOf<TrackedRevisionUiModel?>(null) }
    var stopTrackingConfirmTarget by remember { mutableStateOf<TrackedRevisionUiModel?>(null) }
    var showCalendarProposalSheet by remember { mutableStateOf(false) }
    var calendarCommitNotice by remember { mutableStateOf<String?>(null) }

    val distinctSubjects = remember(items) {
        listOf("All") + items.map { it.subjectName }.distinct()
    }

    val filteredItems = remember(items, selectedTab, selectedSubjectFilter) {
        val tabFiltered = when (selectedTab) {
            RevisionHubTab.DUE_TODAY -> items.filter { it.isDueToday }
            RevisionHubTab.OVERDUE -> items.filter { it.isOverdue }
            RevisionHubTab.UPCOMING -> items.filter { !it.isDueToday && !it.isOverdue }
            RevisionHubTab.ALL_TRACKED -> items
            RevisionHubTab.HISTORY, RevisionHubTab.ANALYTICS -> emptyList()
        }
        if (selectedSubjectFilter == null || selectedSubjectFilter == "All") {
            tabFiltered
        } else {
            tabFiltered.filter { it.subjectName == selectedSubjectFilter }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Smart Revision Hub",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Adaptive Spaced Repetition",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("revision_hub_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (items.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                onLoadCalendarProposal()
                                showCalendarProposalSheet = true
                            },
                            modifier = Modifier.testTag("revision_hub_schedule_calendar_button")
                        ) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Schedule to Calendar", tint = BrandTeal)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            // Commit Notice banner
            if (calendarCommitNotice != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF10B981).copy(alpha = 0.15f))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = calendarCommitNotice!!,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF047857)
                        )
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { calendarCommitNotice = null },
                            tint = Color(0xFF047857)
                        )
                    }
                }
            }

            // Summary Metric Chips
            RevisionMetricsHeader(
                metrics = metrics,
                onStartTopReview = {
                    val top = items.firstOrNull { it.isOverdue || it.isDueToday } ?: items.firstOrNull()
                    if (top != null) reviewDialogTarget = top
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Primary Navigation Tabs
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(RevisionHubTab.entries) { tab ->
                    val isSelected = selectedTab == tab
                    val badgeCount = when (tab) {
                        RevisionHubTab.DUE_TODAY -> metrics.dueTodayCount
                        RevisionHubTab.OVERDUE -> metrics.overdueCount
                        RevisionHubTab.UPCOMING -> metrics.upcomingCount
                        RevisionHubTab.ALL_TRACKED -> metrics.totalTrackedCount
                        RevisionHubTab.HISTORY -> history.size
                        RevisionHubTab.ANALYTICS -> 0
                    }

                    Card(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedTab = tab }
                            .testTag("revision_tab_${tab.name.lowercase()}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) BrandTeal else MaterialTheme.colorScheme.surface
                        ),
                        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = tab.label,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                            if (badgeCount > 0 && tab != RevisionHubTab.ANALYTICS) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(if (isSelected) Color.White.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "$badgeCount",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Subject filter chip row (only when displaying item lists)
            if (selectedTab != RevisionHubTab.HISTORY && selectedTab != RevisionHubTab.ANALYTICS && distinctSubjects.size > 2) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(distinctSubjects) { subName ->
                        val isSelected = selectedSubjectFilter == subName
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { selectedSubjectFilter = subName }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = subName,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Tab Content
            when (selectedTab) {
                RevisionHubTab.HISTORY -> {
                    RevisionHistoryList(history = history)
                }
                RevisionHubTab.ANALYTICS -> {
                    RevisionAnalyticsView(metrics = metrics, history = history, trackedItems = items)
                }
                else -> {
                    if (filteredItems.isEmpty()) {
                        RevisionEmptyState(
                            tab = selectedTab,
                            onNavigateToSyllabus = onNavigateToSyllabus
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredItems, key = { it.id }) { item ->
                                RevisionItemCard(
                                    item = item,
                                    onReviewNow = { reviewDialogTarget = item },
                                    onStudyInFocus = { onLaunchInFocus(item) },
                                    onPostpone = { onPostponeItem(item.id) },
                                    onStopTracking = { stopTrackingConfirmTarget = item }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(24.dp)) }
                        }
                    }
                }
            }
        }
    }

    // Active Recall Feedback Dialog
    if (reviewDialogTarget != null) {
        val target = reviewDialogTarget!!
        RecallFeedbackDialog(
            chapterTitle = target.chapterTitle,
            onConfirmRating = { rating, notes ->
                onCompleteReview(target.id, rating, target.estimatedDurationMinutes * 60, notes)
                reviewDialogTarget = null
            },
            onDismiss = { reviewDialogTarget = null }
        )
    }

    // Stop tracking confirmation dialog
    if (stopTrackingConfirmTarget != null) {
        val target = stopTrackingConfirmTarget!!
        AlertDialog(
            onDismissRequest = { stopTrackingConfirmTarget = null },
            title = { Text("Stop Revision Tracking?") },
            text = {
                Text("This will remove '${target.chapterTitle}' from automated spaced repetition. Your chapter in the syllabus will remain unchanged.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRemoveFromTracking(target.chapterId)
                        stopTrackingConfirmTarget = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Remove Tracking", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { stopTrackingConfirmTarget = null }) {
                    Text("Keep Tracking")
                }
            }
        )
    }

    // Calendar Schedule Proposal Modal Bottom Sheet
    if (showCalendarProposalSheet) {
        CalendarScheduleProposalSheet(
            proposedTasks = proposedCalendarTasks,
            onToggleTask = onToggleProposedTask,
            onCommit = {
                onCommitCalendarTasks { count ->
                    showCalendarProposalSheet = false
                    calendarCommitNotice = "Successfully added $count revision sessions to your Calendar!"
                }
            },
            onDismiss = {
                showCalendarProposalSheet = false
                onClearCalendarProposal()
            }
        )
    }
}

@Composable
private fun RevisionMetricsHeader(
    metrics: RevisionSummaryMetrics,
    onStartTopReview: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Due / Overdue numbers
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(
                            text = "${metrics.dueTodayCount}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = BrandTeal
                            )
                        )
                        Text(
                            text = "Due Today",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (metrics.overdueCount > 0) {
                        Spacer(modifier = Modifier.width(20.dp))
                        Column {
                            Text(
                                text = "${metrics.overdueCount}",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFEF4444)
                                )
                            )
                            Text(
                                text = "Overdue",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF4444)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(20.dp))
                    Column {
                        Text(
                            text = "${metrics.totalEstimatedDurationMinutes}m",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "Est. Total Time",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // CTA Button if items due
                if (metrics.dueTodayCount > 0 || metrics.overdueCount > 0) {
                    Button(
                        onClick = onStartTopReview,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandTeal),
                        modifier = Modifier.testTag("start_top_priority_review_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Review", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun RevisionItemCard(
    item: TrackedRevisionUiModel,
    onReviewNow: () -> Unit,
    onStudyInFocus: () -> Unit,
    onPostpone: () -> Unit,
    onStopTracking: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val subjectColor = parseColorHex(item.subjectColorHex)

    val statusColor = when {
        item.isOverdue -> Color(0xFFEF4444)
        item.isDueToday -> BrandTeal
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val statusLabel = when {
        item.isOverdue -> "${item.daysOverdue}d Overdue"
        item.isDueToday -> "Due Today"
        else -> "In ${item.daysUntilDue}d"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
            .testTag("revision_item_${item.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Subject pill + Status badge + Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(subjectColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getSubjectIcon(item.subjectIconKey),
                            contentDescription = item.subjectName,
                            tint = subjectColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.subjectName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = subjectColor
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(statusColor.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = statusLabel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }

                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More", modifier = Modifier.size(16.dp))
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Postpone 1 Day") },
                                onClick = {
                                    showMenu = false
                                    onPostpone()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Remove from Revision", color = Color(0xFFEF4444)) },
                                onClick = {
                                    showMenu = false
                                    onStopTracking()
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Chapter Title
            Text(
                text = item.chapterTitle,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Explainable Reason
            Text(
                text = item.priorityReason,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Metadata Chips (Interval, Reviews Count, Last Recall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Interval chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "Interval: ${item.currentIntervalDays.toInt()}d",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Completed review count
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (item.reviewCount == 0) "Never reviewed" else "${item.reviewCount} ${if (item.reviewCount == 1) "review" else "reviews"}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Estimated time
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "~${item.estimatedDurationMinutes}m",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Actions Row: "Review Now", "Study in Focus", "Postpone"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onReviewNow,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandTeal),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("review_now_button_${item.id}")
                ) {
                    Text("Review Now", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                OutlinedButton(
                    onClick = onStudyInFocus,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("study_in_focus_button_${item.id}")
                ) {
                    Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("In Focus", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                IconButton(
                    onClick = onPostpone,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(Icons.Default.Schedule, contentDescription = "Postpone 1 Day", modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun RevisionHistoryList(history: List<RevisionHistoryEntity>) {
    if (history.isEmpty()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(36.dp))
                Spacer(modifier = Modifier.height(10.dp))
                Text("No revision history yet", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Completed spaced repetition sessions will appear here with your recall ratings and interval progression.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(history, key = { it.id }) { entry ->
                val ratingColor = when (entry.recallRating) {
                    "HARD" -> Color(0xFFEF4444)
                    "OKAY" -> Color(0xFFF59E0B)
                    else -> Color(0xFF10B981)
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(ratingColor.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = entry.recallRating,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ratingColor
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = entry.completedDate,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (entry.reviewTimingStatus == "ON_TIME") "On time" else entry.reviewTimingStatus.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() },
                                    fontSize = 11.sp,
                                    color = if (entry.reviewTimingStatus == "OVERDUE") Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Interval changed: ${entry.previousIntervalDays.toInt()}d → ${entry.newIntervalDays.toInt()}d",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Medium
                                )
                            )

                            if (!entry.notes.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Notes: ${entry.notes}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (entry.durationSeconds > 0) {
                            Text(
                                text = "${entry.durationSeconds / 60}m",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun RevisionAnalyticsView(
    metrics: RevisionSummaryMetrics,
    history: List<RevisionHistoryEntity>,
    trackedItems: List<TrackedRevisionUiModel>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Recall Rating Breakdown",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Self-reported review confidence distribution",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    val total = max(1, metrics.completedReviewsCount)
                    RatingDistributionBar("Easy", metrics.easyCount, total, Color(0xFF10B981))
                    Spacer(modifier = Modifier.height(8.dp))
                    RatingDistributionBar("Okay", metrics.okayCount, total, Color(0xFFF59E0B))
                    Spacer(modifier = Modifier.height(8.dp))
                    RatingDistributionBar("Hard", metrics.hardCount, total, Color(0xFFEF4444))
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Spaced Repetition Principles",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• Hard: Resets interval to 1 day so tricky concepts are reviewed immediately.\n• Okay: Expands interval by 1.5x to reinforce moderate recall.\n• Easy: Doubles interval (2.0x) to consolidate confident long-term retention.\n• 100% deterministic, local, and explainable heuristic.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )
                    )
                }
            }
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun RatingDistributionBar(label: String, count: Int, total: Int, color: Color) {
    val fraction = count.toFloat() / total
    val percent = (fraction * 100).toInt()
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(text = "$count ($percent%)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.15f)
        )
    }
}

@Composable
private fun RevisionEmptyState(
    tab: RevisionHubTab,
    onNavigateToSyllabus: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val (icon, title, desc) = when (tab) {
                RevisionHubTab.OVERDUE -> Triple(
                    Icons.Default.CheckCircle,
                    "No overdue revisions",
                    "All your tracked topics are up to date! Great job staying on top of your spaced repetition schedule."
                )
                RevisionHubTab.DUE_TODAY -> Triple(
                    Icons.Default.CheckCircle,
                    "No reviews due today",
                    "You've completed all scheduled revisions for today. Check upcoming topics or add new syllabus chapters."
                )
                RevisionHubTab.UPCOMING -> Triple(
                    Icons.Default.Schedule,
                    "No upcoming revisions",
                    "Track syllabus chapters to schedule future spaced repetition intervals."
                )
                else -> Triple(
                    Icons.Default.Autorenew,
                    "No tracked topics yet",
                    "Add syllabus chapters to Revision Tracking to let StudyHub automatically calculate optimal review intervals."
                )
            }

            Icon(imageVector = icon, contentDescription = null, tint = BrandTeal, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = desc, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = androidx.compose.ui.text.style.TextAlign.Center))
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onNavigateToSyllabus,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandTeal)
            ) {
                Text("Go to Syllabus", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarScheduleProposalSheet(
    proposedTasks: List<ProposedRevisionTask>,
    onToggleTask: (chapterId: Long) -> Unit,
    onCommit: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Schedule Revisions to Calendar",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Preview and approve due revision sessions before adding to your daily calendar.",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (proposedTasks.isEmpty()) {
                Text(
                    text = "No revisions are currently eligible to be scheduled for today.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Close")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(proposedTasks, key = { it.chapterId }) { task ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = task.isSelected,
                                    onCheckedChange = { if (!task.isAlreadyScheduled) onToggleTask(task.chapterId) },
                                    enabled = !task.isAlreadyScheduled,
                                    colors = CheckboxDefaults.colors(checkedColor = BrandTeal)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = task.title,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "${task.subjectName} • ~${task.estimatedMinutes} mins",
                                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    )
                                    if (task.isAlreadyScheduled) {
                                        Text(
                                            text = "Already scheduled on calendar",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = BrandTeal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val selectedCount = proposedTasks.count { it.isSelected && !it.isAlreadyScheduled }
                Button(
                    onClick = onCommit,
                    enabled = selectedCount > 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("commit_revision_calendar_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandTeal)
                ) {
                    Text("Add $selectedCount Revision Tasks to Calendar", fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
