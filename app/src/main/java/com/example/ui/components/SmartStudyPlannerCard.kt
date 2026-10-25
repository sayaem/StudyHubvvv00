package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.planner.CommitResult
import com.example.planner.DurationConfidence
import com.example.planner.PlannerDraft
import com.example.planner.PlannerStatus
import com.example.planner.ProposedTask
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandTeal
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

/**
 * Step 2: Confirmation-First Dialog shown when the Intelligence Icon is tapped.
 */
@Composable
fun SmartStudyPlannerConfirmationDialog(
    targetDate: String,
    isTargetToday: Boolean,
    availableBudgetMinutes: Int,
    onConfirmGenerate: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(BrandBlue.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = BrandBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Smart Study Planner",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Smart Study Planner will analyze your available study time (${availableBudgetMinutes}m budget), existing calendar tasks, syllabus priorities, and learning pace to prepare a proposed study schedule for ${if (isTargetToday) "today" else targetDate}.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Generating a proposal will NOT save tasks to your Calendar until you review and confirm.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmGenerate,
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("planner_confirm_continue_button")
            ) {
                Text("Generate Proposed Plan", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("planner_confirm_cancel_button")
            ) {
                Text("Cancel", fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

/**
 * Steps 3 & 4: Dedicated Scrollable Proposed Schedule Review Interface.
 * Resolves scrolling bug by placing tasks inside an independently scrollable LazyColumn with bottom content padding.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartStudyPlannerReviewSheet(
    targetDate: String,
    availableBudgetMinutes: Int,
    plannerDraft: PlannerDraft?,
    plannerStatus: PlannerStatus,
    onToggleTaskSelection: (String) -> Unit,
    onUpdateTaskDuration: (String, Int) -> Unit,
    onRemoveTask: (String) -> Unit,
    onDismissDraft: () -> Unit,
    onCommitToCalendar: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissDraft,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(BrandBlue.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = BrandBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Proposed Schedule",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "$targetDate • ${availableBudgetMinutes}m budget",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                IconButton(
                    onClick = onDismissDraft,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("planner_review_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close proposed schedule",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (plannerStatus == PlannerStatus.GENERATING) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = BrandBlue,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Analyzing syllabus & calculating optimal slots...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            } else if (plannerDraft != null && plannerDraft.tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No study tasks needed for $targetDate! All high priority topics are up to date.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else if (plannerDraft != null) {
                val selectedTasks = plannerDraft.tasks.filter { it.isSelected }
                val totalDuration = selectedTasks.sumOf { it.durationMinutes }

                // Metrics summary
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${selectedTasks.size} task(s) recommended",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Est. Total: ${totalDuration}m",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = BrandTeal
                        )
                    }
                }

                // Warnings if any
                if (plannerDraft.warnings.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    plannerDraft.warnings.forEach { warning ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = StatusWarning,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = warning,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Task List Region (INDEPENDENT SCROLL + Generous Bottom Padding)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("planner_proposed_tasks_list"),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(top = 4.dp, bottom = 28.dp) // Generous bottom padding!
                    ) {
                        items(plannerDraft.tasks, key = { it.id }) { task ->
                            ProposedTaskItem(
                                task = task,
                                onToggleSelection = { onToggleTaskSelection(task.id) },
                                onDurationChanged = { newDur -> onUpdateTaskDuration(task.id, newDur) },
                                onRemove = { onRemoveTask(task.id) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Persistent Bottom Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismissDraft,
                        modifier = Modifier
                            .weight(0.35f)
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Dismiss", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = onCommitToCalendar,
                        enabled = selectedTasks.isNotEmpty(),
                        modifier = Modifier
                            .weight(0.65f)
                            .height(46.dp)
                            .testTag("add_all_to_calendar_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandTeal),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (selectedTasks.size == plannerDraft.tasks.size) "Add All to Calendar" else "Add (${selectedTasks.size}) to Calendar",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Success feedback banner shown after tasks are successfully committed.
 */
@Composable
fun SmartStudyPlannerCommitResultBanner(
    commitResult: CommitResult,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = commitResult.success,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .border(1.dp, StatusSuccess.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = StatusSuccess.copy(alpha = 0.12f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = StatusSuccess,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Plan Added to Calendar!",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Added ${commitResult.addedCount} task(s) (${commitResult.totalDurationMinutes} mins). View them in your timeline below.",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ProposedTaskItem(
    task: ProposedTask,
    onToggleSelection: () -> Unit,
    onDurationChanged: (Int) -> Unit,
    onRemove: () -> Unit
) {
    val subColor = parseColorHex(task.subjectColorHex ?: "#0796A3")

    val priorityColor = when (task.priority) {
        "HIGH" -> StatusDanger
        "MEDIUM" -> BrandTeal
        else -> TextMuted
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (task.isSelected) subColor.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                RoundedCornerShape(14.dp)
            )
            .clip(RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Row 1: Checkbox, Title, Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = task.isSelected,
                    onCheckedChange = { onToggleSelection() },
                    colors = CheckboxDefaults.colors(checkedColor = subColor),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (task.isSelected) MaterialTheme.colorScheme.onSurface else TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    task.subjectName?.let { sub ->
                        Text(
                            text = sub,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = subColor
                        )
                    }
                }

                // Time slot chip
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${task.startTime} - ${task.endTime}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Row 2: Recommendation Reason box
            Surface(
                color = subColor.copy(alpha = 0.08f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = subColor,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = task.recommendationReason,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Row 3: Priority badge, Confidence label & Duration Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val confLabel = when (task.durationConfidence) {
                    DurationConfidence.HIGH -> "High Conf."
                    DurationConfidence.MEDIUM -> "Med Conf."
                    DurationConfidence.LOW -> "Est."
                    DurationConfidence.DEFAULT -> "Baseline"
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = priorityColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = task.priority,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = priorityColor,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$confLabel (${task.durationMinutes}m)",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onDurationChanged((task.durationMinutes - 5).coerceAtLeast(15)) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease duration",
                            modifier = Modifier.size(14.dp),
                            tint = TextSecondary
                        )
                    }

                    Text(
                        text = "${task.durationMinutes}m",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    IconButton(
                        onClick = { onDurationChanged((task.durationMinutes + 5).coerceAtMost(90)) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase duration",
                            modifier = Modifier.size(14.dp),
                            tint = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove from draft",
                            modifier = Modifier.size(14.dp),
                            tint = StatusDanger.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}
