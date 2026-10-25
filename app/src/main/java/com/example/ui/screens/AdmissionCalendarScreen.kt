package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AdmissionTargetEntity
import com.example.data.local.StudyTaskEntity
import com.example.ui.components.StudyHubDetailTopBar
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandTeal
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// Application Status options per requirement 3C
val APPLICATION_STATUSES = listOf(
    "INTERESTED",
    "PLANNING",
    "APPLICATION_OPEN",
    "IN_PROGRESS",
    "APPLIED",
    "ADMIT_CARD_AVAILABLE",
    "EXAM_COMPLETED",
    "RESULT_PUBLISHED",
    "ADMITTED",
    "NOT_APPLYING"
)

fun formatStatusLabel(status: String): String {
    return when (status.uppercase()) {
        "INTERESTED" -> "Interested"
        "PLANNING" -> "Upcoming / Planning"
        "APPLICATION_OPEN" -> "Application Open"
        "IN_PROGRESS" -> "In Progress"
        "APPLIED" -> "Applied"
        "ADMIT_CARD_AVAILABLE" -> "Admit Card Available"
        "EXAM_COMPLETED" -> "Exam Completed"
        "RESULT_PUBLISHED" -> "Result Published"
        "ADMITTED" -> "Admitted 🎉"
        "NOT_APPLYING" -> "Not Applying"
        else -> status.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
    }
}

fun getStatusColor(status: String): Color {
    return when (status.uppercase()) {
        "APPLICATION_OPEN", "IN_PROGRESS" -> BrandBlue
        "APPLIED", "ADMITTED" -> StatusSuccess
        "ADMIT_CARD_AVAILABLE", "EXAM_COMPLETED" -> Color(0xFF8B5CF6)
        "RESULT_PUBLISHED" -> Color(0xFFEC4899)
        "NOT_APPLYING" -> TextMuted
        else -> BrandTeal
    }
}

data class NextActionItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val reason: String,
    val daysRemaining: Long?,
    val isOverdue: Boolean,
    val target: AdmissionTargetEntity?,
    val task: StudyTaskEntity?
)

data class CalendarEventItem(
    val id: String,
    val title: String,
    val dateStr: String,
    val eventType: String, // "DEADLINE", "EXAM", "ADMIT_CARD", "OPENING", "TASK"
    val targetName: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdmissionCalendarScreen(
    targets: List<AdmissionTargetEntity>,
    tasks: List<StudyTaskEntity> = emptyList(),
    onSaveTarget: (AdmissionTargetEntity) -> Unit,
    onDeleteTarget: (AdmissionTargetEntity) -> Unit,
    onCreateTask: (title: String, date: String, targetId: Long, priority: String, notes: String?) -> Unit = { _, _, _, _, _ -> },
    onToggleTaskStatus: (StudyTaskEntity) -> Unit = {},
    onDeleteTask: (StudyTaskEntity) -> Unit = {},
    onBack: () -> Unit,
    initialAddRequested: Boolean = false,
    onAddDismissed: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Targets & Overview, 1: Calendar & Agenda, 2: Target Tasks
    var selectedFilter by remember { mutableStateOf("All") } // "All", "General University", "Medical", "Engineering", "Other"
    var showAddSheet by remember { mutableStateOf(initialAddRequested) }
    var targetToEdit by remember { mutableStateOf<AdmissionTargetEntity?>(null) }
    var selectedTargetDetail by remember { mutableStateOf<AdmissionTargetEntity?>(null) }
    var targetToDelete by remember { mutableStateOf<AdmissionTargetEntity?>(null) }
    var showAddTaskModalForTarget by remember { mutableStateOf<AdmissionTargetEntity?>(null) }

    androidx.activity.compose.BackHandler(enabled = targetToDelete != null) {
        targetToDelete = null
    }

    androidx.activity.compose.BackHandler(enabled = showAddTaskModalForTarget != null) {
        showAddTaskModalForTarget = null
    }

    androidx.activity.compose.BackHandler(enabled = showAddSheet) {
        showAddSheet = false
        targetToEdit = null
        onAddDismissed()
    }

    androidx.activity.compose.BackHandler(enabled = selectedTargetDetail != null) {
        selectedTargetDetail = null
    }

    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    val todayDate = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }

    val todayStr = remember { dateFormat.format(todayDate) }

    fun getDaysLeftMs(dateStr: String?): Long? {
        if (dateStr.isNullOrBlank()) return null
        return try {
            val d = dateFormat.parse(dateStr) ?: return null
            val diffMs = d.time - todayDate.time
            diffMs / (1000 * 60 * 60 * 24)
        } catch (e: Exception) {
            null
        }
    }

    fun getDaysLeftLabel(dateStr: String?): String {
        val days = getDaysLeftMs(dateStr) ?: return "No date"
        return when {
            days < 0 -> "Passed"
            days == 0L -> "Today"
            days == 1L -> "1 day left"
            else -> "$days days left"
        }
    }

    // 3A Metrics calculation
    val totalTargetsCount = targets.size
    val upcomingDeadlinesCount = targets.count {
        val days = getDaysLeftMs(it.applicationDeadline)
        days != null && days >= 0
    }
    val inProgressApplicationsCount = targets.count {
        it.applicationStatus in listOf("APPLICATION_OPEN", "IN_PROGRESS", "APPLIED", "INTERESTED", "PLANNING", "ADMIT_CARD_AVAILABLE")
    }
    val completedApplicationsCount = targets.count {
        it.applicationStatus in listOf("EXAM_COMPLETED", "RESULT_PUBLISHED", "ADMITTED")
    }
    val overdueItemsCount = targets.count {
        val days = getDaysLeftMs(it.applicationDeadline)
        days != null && days < 0 && it.applicationStatus !in listOf("APPLIED", "EXAM_COMPLETED", "RESULT_PUBLISHED", "ADMITTED", "NOT_APPLYING")
    } + tasks.count {
        it.targetId != null && it.status == "PENDING" && it.date < todayStr
    }

    // 3F Priority and Next Actions calculation
    val nextActions = remember(targets, tasks) {
        val list = mutableListOf<NextActionItem>()

        // Check target deadlines
        targets.forEach { target ->
            val days = getDaysLeftMs(target.applicationDeadline)
            if (days != null) {
                if (days < 0 && target.applicationStatus !in listOf("APPLIED", "EXAM_COMPLETED", "RESULT_PUBLISHED", "ADMITTED", "NOT_APPLYING")) {
                    list.add(
                        NextActionItem(
                            id = "target_overdue_${target.id}",
                            title = target.institutionName,
                            subtitle = "Application deadline passed on ${target.applicationDeadline}",
                            reason = "Overdue Deadline",
                            daysRemaining = days,
                            isOverdue = true,
                            target = target,
                            task = null
                        )
                    )
                } else if (days in 0..7 && target.applicationStatus !in listOf("APPLIED", "EXAM_COMPLETED", "RESULT_PUBLISHED", "ADMITTED", "NOT_APPLYING")) {
                    list.add(
                        NextActionItem(
                            id = "target_urgent_${target.id}",
                            title = target.institutionName,
                            subtitle = "Application deadline on ${target.applicationDeadline}",
                            reason = if (days == 0L) "Deadline Today!" else "Deadline in $days days",
                            daysRemaining = days,
                            isOverdue = false,
                            target = target,
                            task = null
                        )
                    )
                }
            }

            // Unpaid fee task
            if (!target.applicationFee.isNullOrBlank() && target.applicationStatus in listOf("PLANNING", "APPLICATION_OPEN", "IN_PROGRESS")) {
                val hasFeePaidTask = tasks.any { it.targetId == target.id && it.title.contains("fee", ignoreCase = true) && it.status == "COMPLETED" }
                if (!hasFeePaidTask) {
                    list.add(
                        NextActionItem(
                            id = "target_fee_${target.id}",
                            title = "${target.institutionName} — Pay Fee",
                            subtitle = "Fee: ${target.applicationFee}",
                            reason = "Application fee pending",
                            daysRemaining = days,
                            isOverdue = false,
                            target = target,
                            task = null
                        )
                    )
                }
            }

            // Admit card alert
            if (!target.admitCardDate.isNullOrBlank()) {
                val admitDays = getDaysLeftMs(target.admitCardDate)
                if (admitDays != null && admitDays in 0..3) {
                    list.add(
                        NextActionItem(
                            id = "admit_card_${target.id}",
                            title = "${target.institutionName} — Download Admit Card",
                            subtitle = "Admit card date: ${target.admitCardDate}",
                            reason = if (admitDays == 0L) "Admit card today!" else "Admit card in $admitDays days",
                            daysRemaining = admitDays,
                            isOverdue = false,
                            target = target,
                            task = null
                        )
                    )
                }
            }
        }

        // Check target tasks
        tasks.filter { it.targetId != null && it.status == "PENDING" }.forEach { task ->
            val target = targets.find { it.id == task.targetId }
            val days = getDaysLeftMs(task.date)
            if (days != null) {
                if (days < 0) {
                    list.add(
                        NextActionItem(
                            id = "task_overdue_${task.id}",
                            title = task.title,
                            subtitle = "${target?.institutionName ?: "Admission"} • Due ${task.date}",
                            reason = "Task Overdue",
                            daysRemaining = days,
                            isOverdue = true,
                            target = target,
                            task = task
                        )
                    )
                } else if (days <= 3) {
                    list.add(
                        NextActionItem(
                            id = "task_urgent_${task.id}",
                            title = task.title,
                            subtitle = "${target?.institutionName ?: "Admission"} • Due ${task.date}",
                            reason = if (days == 0L) "Task Due Today" else "Task Due in $days days",
                            daysRemaining = days,
                            isOverdue = false,
                            target = target,
                            task = task
                        )
                    )
                }
            }
        }

        list.sortedBy { if (it.isOverdue) -100 else (it.daysRemaining ?: 999) }.take(6)
    }

    val filteredTargets = targets.filter {
        when (selectedFilter) {
            "General" -> it.category == "General University"
            "Medical" -> it.category == "Medical"
            "Engineering" -> it.category == "Engineering"
            "Other" -> it.category == "Other"
            else -> true
        }
    }

    if (selectedTargetDetail != null) {
        val detail = selectedTargetDetail!!
        ApplicationDetailsScreen(
            target = detail,
            tasks = tasks.filter { it.targetId == detail.id },
            daysLeft = getDaysLeftLabel(detail.applicationDeadline),
            onBack = { selectedTargetDetail = null },
            onEdit = {
                targetToEdit = detail
                showAddSheet = true
            },
            onDelete = {
                targetToDelete = detail
            },
            onSaveTarget = onSaveTarget,
            onCreateTask = { title, date, priority, notes ->
                onCreateTask(title, date, detail.id, priority, notes)
            },
            onToggleTaskStatus = onToggleTaskStatus,
            onDeleteTask = onDeleteTask
        )
    } else {
        Scaffold(
            topBar = {
                // Requirement 2: Clean header without "Add Target" text button
                StudyHubDetailTopBar(
                    title = "Admission Command Center",
                    onBackClick = onBack
                )
            },
            floatingActionButton = {
                // Requirement 2: Clean circular '+' Floating Action Button at bottom-right
                FloatingActionButton(
                    onClick = {
                        targetToEdit = null
                        showAddSheet = true
                    },
                    containerColor = BrandTeal,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier
                        .padding(bottom = 12.dp, end = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add admission target",
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Command Center Navigation Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = BrandTeal
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Command Center", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Calendar & Agenda", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Target Tasks", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.TaskAlt, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                }

                when (selectedTab) {
                    0 -> {
                        // Overview, Next Actions & Admission Target List
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 90.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // 3A Overview Metrics Section
                            item {
                                CommandCenterMetricsRow(
                                    totalTargets = totalTargetsCount,
                                    upcomingDeadlines = upcomingDeadlinesCount,
                                    inProgress = inProgressApplicationsCount,
                                    completed = completedApplicationsCount,
                                    overdue = overdueItemsCount
                                )
                            }

                            // 3F Priority and Next Actions
                            if (nextActions.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "Priority Next Actions ⚡",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        nextActions.forEach { action ->
                                            NextActionCard(
                                                item = action,
                                                onCardClick = {
                                                    if (action.target != null) {
                                                        selectedTargetDetail = action.target
                                                    }
                                                },
                                                onCompleteTask = {
                                                    if (action.task != null) {
                                                        onToggleTaskStatus(action.task)
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            // Category Filter Chips
                            item {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Admission Targets (${filteredTargets.size})",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        listOf("All", "General", "Medical", "Engineering", "Other").forEach { filter ->
                                            val isSelected = selectedFilter == filter
                                            Card(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .clickable { selectedFilter = filter },
                                                colors = CardDefaults.cardColors(
                                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                                                ),
                                                border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null
                                            ) {
                                                Text(
                                                    text = filter,
                                                    fontSize = 12.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Targets List or Empty State
                            if (filteredTargets.isEmpty()) {
                                item {
                                    EmptyTargetsCard(onAddClick = {
                                        targetToEdit = null
                                        showAddSheet = true
                                    })
                                }
                            } else {
                                items(filteredTargets, key = { it.id }) { target ->
                                    val daysLeftLabel = getDaysLeftLabel(target.applicationDeadline)
                                    val daysVal = getDaysLeftMs(target.applicationDeadline)
                                    val isUrgent = daysVal != null && daysVal in 0..7

                                    val targetTasks = tasks.filter { it.targetId == target.id }
                                    val completedTasksCount = targetTasks.count { it.status == "COMPLETED" }

                                    TargetCardItem(
                                        target = target,
                                        daysLeftLabel = daysLeftLabel,
                                        isUrgent = isUrgent,
                                        completedTasksCount = completedTasksCount,
                                        totalTasksCount = targetTasks.size,
                                        onClick = { selectedTargetDetail = target },
                                        onStatusChange = { newStatus ->
                                            onSaveTarget(target.copy(applicationStatus = newStatus))
                                        },
                                        onAddTask = {
                                            showAddTaskModalForTarget = target
                                        }
                                    )
                                }
                            }
                        }
                    }

                    1 -> {
                        // 3E Calendar & Agenda View
                        CalendarAndAgendaView(
                            targets = targets,
                            tasks = tasks,
                            onTargetClick = { selectedTargetDetail = it }
                        )
                    }

                    2 -> {
                        // 3D Target Task Checklist
                        TargetTasksView(
                            targets = targets,
                            tasks = tasks,
                            onCreateTask = onCreateTask,
                            onToggleTaskStatus = onToggleTaskStatus,
                            onDeleteTask = onDeleteTask
                        )
                    }
                }
            }
        }
    }

    // Add / Edit Target Sheet
    if (showAddSheet) {
        AddAdmissionTargetSheet(
            existingTarget = targetToEdit,
            existingTargets = targets,
            onDismiss = {
                showAddSheet = false
                targetToEdit = null
                onAddDismissed()
            },
            onSave = { target ->
                onSaveTarget(target)
                showAddSheet = false
                targetToEdit = null
                onAddDismissed()
            }
        )
    }

    // Add Task for Target Modal
    showAddTaskModalForTarget?.let { target ->
        QuickAddTaskModal(
            target = target,
            onDismiss = { showAddTaskModalForTarget = null },
            onSaveTask = { title, date, priority, notes ->
                onCreateTask(title, date, target.id, priority, notes)
                showAddTaskModalForTarget = null
            }
        )
    }

    // Delete Target Confirmation Dialog
    targetToDelete?.let { tgt ->
        AlertDialog(
            onDismissRequest = { targetToDelete = null },
            title = { Text("Delete ${tgt.institutionName}?") },
            text = { Text("This will permanently remove this admission target and its milestone tracking.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteTarget(tgt)
                        targetToDelete = null
                        selectedTargetDetail = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusDanger)
                ) {
                    Text("Delete Target")
                }
            },
            dismissButton = {
                TextButton(onClick = { targetToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CommandCenterMetricsRow(
    totalTargets: Int,
    upcomingDeadlines: Int,
    inProgress: Int,
    completed: Int,
    overdue: Int
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Admission Overview",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricBox(
                label = "Saved Targets",
                value = "$totalTargets",
                color = BrandTeal,
                modifier = Modifier.weight(1f)
            )
            MetricBox(
                label = "Upcoming",
                value = "$upcomingDeadlines",
                color = BrandBlue,
                modifier = Modifier.weight(1f)
            )
            MetricBox(
                label = "In Progress",
                value = "$inProgress",
                color = Color(0xFF8B5CF6),
                modifier = Modifier.weight(1f)
            )
            MetricBox(
                label = "Overdue",
                value = "$overdue",
                color = if (overdue > 0) StatusDanger else StatusSuccess,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MetricBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun NextActionCard(
    item: NextActionItem,
    onCardClick: () -> Unit,
    onCompleteTask: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onCardClick)
            .border(
                1.dp,
                if (item.isOverdue) StatusDanger.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline,
                RoundedCornerShape(14.dp)
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isOverdue) StatusDanger.copy(alpha = 0.06f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (item.isOverdue) Icons.Default.Warning else Icons.Default.Flag,
                contentDescription = null,
                tint = if (item.isOverdue) StatusDanger else StatusWarning,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .background(
                        if (item.isOverdue) StatusDanger.copy(alpha = 0.15f) else StatusWarning.copy(alpha = 0.15f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = item.reason,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (item.isOverdue) StatusDanger else StatusWarning
                )
            }
        }
    }
}

@Composable
fun TargetCardItem(
    target: AdmissionTargetEntity,
    daysLeftLabel: String,
    isUrgent: Boolean,
    completedTasksCount: Int,
    totalTasksCount: Int,
    onClick: () -> Unit,
    onStatusChange: (String) -> Unit,
    onAddTask: () -> Unit
) {
    var showStatusMenu by remember { mutableStateOf(false) }

    val categoryColor = when (target.category) {
        "Medical" -> Color(0xFFEC4899)
        "Engineering" -> BrandTeal
        "General University" -> Color(0xFFF97316)
        else -> BrandBlue
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(categoryColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (target.category) {
                            "Medical" -> Icons.Default.MedicalServices
                            "Engineering" -> Icons.Default.Engineering
                            else -> Icons.Default.School
                        },
                        contentDescription = target.category,
                        tint = categoryColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = target.institutionName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "${target.category}${target.programUnit?.let { " • $it" } ?: ""}",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }

                Box(
                    modifier = Modifier
                        .background(
                            if (isUrgent) StatusDanger.copy(alpha = 0.15f) else MaterialTheme.colorScheme.secondaryContainer,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = daysLeftLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUrgent) StatusDanger else MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dates summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Deadline", fontSize = 10.sp, color = TextMuted)
                    Text(target.applicationDeadline, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
                if (!target.examDate.isNullOrBlank()) {
                    Column {
                        Text("Exam Date", fontSize = 10.sp, color = TextMuted)
                        Text(target.examDate, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                if (!target.applicationFee.isNullOrBlank()) {
                    Column {
                        Text("Fee", fontSize = 10.sp, color = TextMuted)
                        Text(target.applicationFee, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Status chip & quick status changer dropdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(getStatusColor(target.applicationStatus).copy(alpha = 0.15f))
                            .clickable { showStatusMenu = true }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = formatStatusLabel(target.applicationStatus),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = getStatusColor(target.applicationStatus)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Change Status",
                                tint = getStatusColor(target.applicationStatus),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showStatusMenu,
                        onDismissRequest = { showStatusMenu = false }
                    ) {
                        APPLICATION_STATUSES.forEach { st ->
                            DropdownMenuItem(
                                text = { Text(formatStatusLabel(st), fontSize = 13.sp) },
                                onClick = {
                                    onStatusChange(st)
                                    showStatusMenu = false
                                }
                            )
                        }
                    }
                }

                // Add task for target action
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onAddTask)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Task",
                        tint = BrandTeal,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (totalTasksCount > 0) "$completedTasksCount/$totalTasksCount Tasks" else "+ Task",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandTeal
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarAndAgendaView(
    targets: List<AdmissionTargetEntity>,
    tasks: List<StudyTaskEntity>,
    onTargetClick: (AdmissionTargetEntity) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("All") } // "All", "Deadlines", "Exams", "Admit Cards", "Tasks"

    val events = remember(targets, tasks, selectedFilter) {
        val list = mutableListOf<CalendarEventItem>()

        targets.forEach { target ->
            if (selectedFilter in listOf("All", "Deadlines")) {
                list.add(
                    CalendarEventItem(
                        id = "dl_${target.id}",
                        title = "${target.institutionName} — Application Deadline",
                        dateStr = target.applicationDeadline,
                        eventType = "DEADLINE",
                        targetName = target.institutionName,
                        color = StatusDanger
                    )
                )
            }
            if (!target.examDate.isNullOrBlank() && selectedFilter in listOf("All", "Exams")) {
                list.add(
                    CalendarEventItem(
                        id = "exam_${target.id}",
                        title = "${target.institutionName} — Admission Test",
                        dateStr = target.examDate,
                        eventType = "EXAM",
                        targetName = target.institutionName,
                        color = Color(0xFF8B5CF6)
                    )
                )
            }
            if (!target.admitCardDate.isNullOrBlank() && selectedFilter in listOf("All", "Admit Cards")) {
                list.add(
                    CalendarEventItem(
                        id = "admit_${target.id}",
                        title = "${target.institutionName} — Admit Card Download",
                        dateStr = target.admitCardDate,
                        eventType = "ADMIT_CARD",
                        targetName = target.institutionName,
                        color = BrandBlue
                    )
                )
            }
        }

        if (selectedFilter in listOf("All", "Tasks")) {
            tasks.filter { it.targetId != null }.forEach { task ->
                val target = targets.find { it.id == task.targetId }
                list.add(
                    CalendarEventItem(
                        id = "task_${task.id}",
                        title = task.title,
                        dateStr = task.date,
                        eventType = "TASK",
                        targetName = target?.institutionName ?: "Admission",
                        color = BrandTeal
                    )
                )
            }
        }

        list.sortedBy { it.dateStr }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Legend & Filters
        item {
            Column {
                Text(
                    text = "Calendar Legend",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LegendItem(color = StatusDanger, label = "Deadline")
                    LegendItem(color = Color(0xFF8B5CF6), label = "Exam")
                    LegendItem(color = BrandBlue, label = "Admit Card")
                    LegendItem(color = BrandTeal, label = "Task")
                }
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All", "Deadlines", "Exams", "Admit Cards", "Tasks").forEach { filter ->
                        val isSelected = selectedFilter == filter
                        Card(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedFilter = filter },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                            ),
                            border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null
                        ) {
                            Text(
                                text = filter,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        if (events.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No events found for this filter.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted)
                        )
                    }
                }
            }
        } else {
            items(events, key = { it.id }) { event ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(event.color)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = event.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = "${event.targetName} • Date: ${event.dateStr}",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun TargetTasksView(
    targets: List<AdmissionTargetEntity>,
    tasks: List<StudyTaskEntity>,
    onCreateTask: (title: String, date: String, targetId: Long, priority: String, notes: String?) -> Unit,
    onToggleTaskStatus: (StudyTaskEntity) -> Unit,
    onDeleteTask: (StudyTaskEntity) -> Unit
) {
    val targetTasks = tasks.filter { it.targetId != null }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Target Action Tasks",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Keep track of document collection, fee payments, and form submissions.",
                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
            )
        }

        if (targetTasks.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.TaskAlt, contentDescription = null, tint = BrandTeal, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No admission tasks created yet", fontWeight = FontWeight.Bold)
                        Text(
                            "Add target tasks directly from the Command Center target cards.",
                            fontSize = 12.sp,
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(targetTasks, key = { it.id }) { task ->
                val target = targets.find { it.id == task.targetId }
                val isDone = task.status == "COMPLETED"

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { onToggleTaskStatus(task) }) {
                            Icon(
                                imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.Check,
                                contentDescription = "Toggle completion",
                                tint = if (isDone) StatusSuccess else TextMuted
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDone) TextMuted else MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = "${target?.institutionName ?: "Target"} • Due: ${task.date}",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                        IconButton(onClick = { onDeleteTask(task) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete task", tint = StatusDanger, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyTargetsCard(onAddClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.School, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No admission targets tracked yet",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Track application deadlines, admit cards, and exam dates in one place.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onAddClick,
                colors = ButtonDefaults.buttonColors(containerColor = BrandTeal),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Add University Target")
            }
        }
    }
}

@Composable
fun ApplicationDetailsScreen(
    target: AdmissionTargetEntity,
    tasks: List<StudyTaskEntity>,
    daysLeft: String,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSaveTarget: (AdmissionTargetEntity) -> Unit,
    onCreateTask: (String, String, String, String?) -> Unit,
    onToggleTaskStatus: (StudyTaskEntity) -> Unit,
    onDeleteTask: (StudyTaskEntity) -> Unit
) {
    var showAddTaskModal by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        StudyHubDetailTopBar(
            title = "Target Details",
            onBackClick = onBack,
            actions = {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Target", tint = BrandTeal)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Target", tint = StatusDanger)
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Main Target Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.School, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = target.institutionName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            )
                            Text(
                                text = "${target.category}${target.programUnit?.let { " • $it" } ?: ""}",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(getStatusColor(target.applicationStatus).copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = formatStatusLabel(target.applicationStatus),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = getStatusColor(target.applicationStatus)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Days remaining: $daysLeft",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Milestone Dates
            Text(
                text = "Milestone Dates",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DateRow(label = "Application Open", date = target.applicationOpenDate ?: "Not specified")
                    DateRow(label = "Application Deadline", date = target.applicationDeadline, isHighlight = true)
                    DateRow(label = "Admit Card Available", date = target.admitCardDate ?: "Not announced")
                    DateRow(label = "Admission Exam", date = target.examDate ?: "Not announced")
                    if (!target.resultDate.isNullOrBlank()) {
                        DateRow(label = "Result Publication", date = target.resultDate)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fees & Notes
            Text(
                text = "Application Information & Notes",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (!target.applicationFee.isNullOrBlank()) {
                        Text(
                            text = "Application Fee: ${target.applicationFee}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    if (!target.notes.isNullOrBlank()) {
                        Text(
                            text = "Notes: ${target.notes}",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    if (!target.officialUrl.isNullOrBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Link, contentDescription = null, tint = BrandTeal, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = target.officialUrl,
                                style = MaterialTheme.typography.bodySmall.copy(color = BrandTeal, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Target Tasks Checklist
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Action Checklist",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Button(
                    onClick = { showAddTaskModal = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandTeal),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Task", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (tasks.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                        Text("No tasks added for this target yet. Tap 'Add Task' to create checklist items.", fontSize = 12.sp, color = TextMuted)
                    }
                }
            } else {
                tasks.forEach { task ->
                    val isDone = task.status == "COMPLETED"
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp)),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { onToggleTaskStatus(task) }) {
                                Icon(
                                    imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.Check,
                                    contentDescription = null,
                                    tint = if (isDone) StatusSuccess else TextMuted
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = task.title,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = if (isDone) TextMuted else MaterialTheme.colorScheme.onSurface
                                )
                                Text("Due: ${task.date}", fontSize = 11.sp, color = TextMuted)
                            }
                            IconButton(onClick = { onDeleteTask(task) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete task", tint = StatusDanger, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    if (showAddTaskModal) {
        QuickAddTaskModal(
            target = target,
            onDismiss = { showAddTaskModal = false },
            onSaveTask = { title, date, priority, notes ->
                onCreateTask(title, date, priority, notes)
                showAddTaskModal = false
            }
        )
    }
}

@Composable
fun DateRow(label: String, date: String, isHighlight: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                tint = if (isHighlight) BrandTeal else TextMuted,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                color = if (isHighlight) MaterialTheme.colorScheme.onSurface else TextSecondary,
                fontWeight = if (isHighlight) FontWeight.SemiBold else FontWeight.Normal
            )
        }

        Text(
            text = date,
            fontSize = 13.sp,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium,
            color = if (isHighlight) BrandTeal else MaterialTheme.colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAddTaskModal(
    target: AdmissionTargetEntity,
    onDismiss: () -> Unit,
    onSaveTask: (title: String, date: String, priority: String, notes: String?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedPreset by remember { mutableStateOf("Collect required documents") }
    var customTitle by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf(target.applicationDeadline) }

    val presets = listOf(
        "Collect required documents",
        "Complete application form",
        "Pay application fee",
        "Download admit card",
        "Prepare for admission test",
        "Check exam results"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
        ) {
            Text(
                text = "Add Task for ${target.institutionName}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("Select Task Preset", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                presets.forEach { preset ->
                    val isSelected = selectedPreset == preset && customTitle.isBlank()
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                selectedPreset = preset
                                customTitle = ""
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) BrandTeal.copy(alpha = 0.15f) else Color(0xFFF1F5F9)
                        ),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, BrandTeal) else null
                    ) {
                        Text(
                            text = preset,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) BrandTeal else TextPrimary,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = customTitle,
                onValueChange = { customTitle = it },
                label = { Text("Or enter custom task title") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = dueDate,
                onValueChange = { dueDate = it },
                label = { Text("Due Date (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        val finalTitle = customTitle.ifBlank { selectedPreset }
                        if (finalTitle.isNotBlank() && dueDate.isNotBlank()) {
                            onSaveTask(finalTitle.trim(), dueDate.trim(), "MEDIUM", null)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandTeal)
                ) {
                    Text("Save Task")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAdmissionTargetSheet(
    existingTarget: AdmissionTargetEntity? = null,
    existingTargets: List<AdmissionTargetEntity> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (AdmissionTargetEntity) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var name by remember { mutableStateOf(existingTarget?.institutionName ?: "") }
    var category by remember { mutableStateOf(existingTarget?.category ?: "General University") }
    var programUnit by remember { mutableStateOf(existingTarget?.programUnit ?: "") }
    var openDate by remember { mutableStateOf(existingTarget?.applicationOpenDate ?: "") }
    var deadline by remember { mutableStateOf(existingTarget?.applicationDeadline ?: "2025-11-25") }
    var admitCardDate by remember { mutableStateOf(existingTarget?.admitCardDate ?: "") }
    var examDate by remember { mutableStateOf(existingTarget?.examDate ?: "") }
    var resultDate by remember { mutableStateOf(existingTarget?.resultDate ?: "") }
    var fee by remember { mutableStateOf(existingTarget?.applicationFee ?: "") }
    var status by remember { mutableStateOf(existingTarget?.applicationStatus ?: "PLANNING") }
    var officialUrl by remember { mutableStateOf(existingTarget?.officialUrl ?: "") }
    var notes by remember { mutableStateOf(existingTarget?.notes ?: "") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
        ) {
            Text(
                text = if (existingTarget != null) "Edit Admission Target" else "Add Admission Target",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = "Enter institution details, milestone dates, and status",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    color = StatusDanger,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    errorMessage = null
                },
                label = { Text("University / Institution Name *") },
                placeholder = { Text("e.g. Dhaka University, BUET, DMC") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text("Category", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("General University", "Medical", "Engineering", "Other").forEach { cat ->
                    val isSelected = category == cat
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { category = cat },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) BrandTeal else Color(0xFFF1F5F9)
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (cat == "General University") "General" else cat,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TextPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = programUnit,
                onValueChange = { programUnit = it },
                label = { Text("Program / Faculty / Unit (Optional)") },
                placeholder = { Text("e.g. A Unit, Science Faculty") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = openDate,
                onValueChange = { openDate = it },
                label = { Text("Application Open Date (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = deadline,
                onValueChange = {
                    deadline = it
                    errorMessage = null
                },
                label = { Text("Application Deadline (YYYY-MM-DD) *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = admitCardDate,
                onValueChange = { admitCardDate = it },
                label = { Text("Admit Card Available Date (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = examDate,
                onValueChange = { examDate = it },
                label = { Text("Admission Exam Date (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = fee,
                onValueChange = { fee = it },
                label = { Text("Application Fee") },
                placeholder = { Text("e.g. 1000 BDT") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = officialUrl,
                onValueChange = { officialUrl = it },
                label = { Text("Official Admission Website / Link") },
                placeholder = { Text("https://admission.du.ac.bd") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes / Requirements") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        val trimmedName = name.trim()
                        val trimmedDeadline = deadline.trim()

                        if (trimmedName.isBlank()) {
                            errorMessage = "Please enter institution name."
                            return@Button
                        }

                        if (trimmedDeadline.isBlank()) {
                            errorMessage = "Please enter application deadline."
                            return@Button
                        }

                        // Requirement 2: Prevent accidental duplicate targets from rapid taps
                        if (existingTarget == null && existingTargets.any {
                            it.institutionName.equals(trimmedName, ignoreCase = true) &&
                                    (it.programUnit ?: "").equals(programUnit.trim(), ignoreCase = true)
                        }) {
                            errorMessage = "Target for $trimmedName (${programUnit.ifBlank { "General" }}) already exists."
                            return@Button
                        }

                        if (!isSaving) {
                            isSaving = true
                            onSave(
                                AdmissionTargetEntity(
                                    id = existingTarget?.id ?: 0L,
                                    institutionName = trimmedName,
                                    category = category,
                                    programUnit = programUnit.trim().ifBlank { null },
                                    cycleLabel = existingTarget?.cycleLabel ?: "2025",
                                    applicationOpenDate = openDate.trim().ifBlank { null },
                                    applicationDeadline = trimmedDeadline,
                                    admitCardDate = admitCardDate.trim().ifBlank { null },
                                    examDate = examDate.trim().ifBlank { null },
                                    resultDate = resultDate.trim().ifBlank { null },
                                    applicationFee = fee.trim().ifBlank { null },
                                    applicationStatus = status,
                                    officialUrl = officialUrl.trim().ifBlank { null },
                                    notes = notes.trim().ifBlank { null },
                                    createdAt = existingTarget?.createdAt ?: System.currentTimeMillis()
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandTeal),
                    enabled = !isSaving
                ) {
                    Text(if (existingTarget != null) "Update Target" else "Save Target", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
