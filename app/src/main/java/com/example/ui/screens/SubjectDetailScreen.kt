package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import com.example.revision.RecallRating
import com.example.revision.TrackedRevisionUiModel
import com.example.ui.components.RecallFeedbackDialog
import com.example.ui.theme.BrandDeepTeal
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChapterEntity
import com.example.data.local.SubjectEntity
import com.example.ui.components.StudyHubDetailTopBar
import com.example.ui.components.getSubjectIcon
import com.example.ui.components.parseColorHex
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectDetailScreen(
    subject: SubjectEntity,
    chapters: List<ChapterEntity>,
    onBack: () -> Unit,
    onUpdateChapterStatus: (ChapterEntity, String) -> Unit,
    onAddChapter: (String) -> Unit,
    onDeleteChapter: (ChapterEntity) -> Unit,
    onDeleteSubject: (SubjectEntity) -> Unit,
    revisionItems: List<TrackedRevisionUiModel> = emptyList(),
    onTrackChapterInRevision: (subjectId: Long, chapterId: Long) -> Unit = { _, _ -> },
    onRemoveChapterFromRevision: (chapterId: Long) -> Unit = {},
    onReviewNow: (itemId: Long, rating: RecallRating, durationSeconds: Int, notes: String?) -> Unit = { _, _, _, _ -> },
    onOpenRevisionHub: () -> Unit = {}
) {
    BackHandler { onBack() }

    var chapterFilter by remember { mutableStateOf("All") } // "All", "To-Do", "In Progress", "Completed"
    var showAddChapterSheet by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var stopTrackingTargetChapterId by remember { mutableStateOf<Long?>(null) }
    var reviewDialogTargetItem by remember { mutableStateOf<TrackedRevisionUiModel?>(null) }

    val subjectColor = parseColorHex(subject.colorHex)
    val completedCount = chapters.count { it.status == "COMPLETED" }
    val totalCount = chapters.size
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
    val percent = (progress * 100).toInt()

    val filteredChapters = chapters.filter { chapter ->
        when (chapterFilter) {
            "To-Do" -> chapter.status == "NOT_STARTED"
            "In Progress" -> chapter.status == "IN_PROGRESS"
            "Completed" -> chapter.status == "COMPLETED"
            else -> true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        StudyHubDetailTopBar(
            title = subject.name,
            onBackClick = onBack,
            actions = {
                IconButton(onClick = { showDeleteConfirmDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Subject",
                        tint = StatusDanger
                    )
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 6.dp)
        ) {
            // Subject Summary Hero Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                                .background(subjectColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getSubjectIcon(subject.iconKey),
                                contentDescription = subject.name,
                                tint = subjectColor,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = subject.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                if (!subject.banglaName.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "(${subject.banglaName})",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$completedCount of $totalCount completed ($percent%)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        Text(
                            text = "$percent%",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = subjectColor
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = subjectColor,
                        trackColor = subjectColor.copy(alpha = 0.15f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Filter Tabs: All, To-Do, In Progress, Completed
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("All", "To-Do", "In Progress", "Completed").forEach { tab ->
                    val isSelected = chapterFilter == tab
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { chapterFilter = tab },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        ),
                        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tab,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Chapters List & Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Chapters (${filteredChapters.size})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )

                Button(
                    onClick = { showAddChapterSheet = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Chapter", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (filteredChapters.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No chapters in this filter",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap '+ Add Chapter' above to add chapters to your syllabus.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredChapters, key = { it.id }) { chapter ->
                        val isDone = chapter.status == "COMPLETED"
                        val isInProgress = chapter.status == "IN_PROGRESS"
                        val revisionItem = revisionItems.find { it.chapterId == chapter.id }

                        val statusLabel = when (chapter.status) {
                            "COMPLETED" -> "Done"
                            "IN_PROGRESS" -> "In Progress"
                            else -> "To-Do"
                        }

                        val statusColor = when (chapter.status) {
                            "COMPLETED" -> StatusSuccess
                            "IN_PROGRESS" -> StatusWarning
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }

                        var showChapterRevisionMenu by remember { mutableStateOf(false) }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp)),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // 1-Tap Status Checkbox
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .clickable {
                                                val nextStatus = when (chapter.status) {
                                                    "NOT_STARTED" -> "IN_PROGRESS"
                                                    "IN_PROGRESS" -> "COMPLETED"
                                                    else -> "NOT_STARTED"
                                                }
                                                onUpdateChapterStatus(chapter, nextStatus)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = when {
                                                isDone -> Icons.Default.CheckCircle
                                                isInProgress -> Icons.Default.KeyboardArrowUp
                                                else -> Icons.Default.RadioButtonUnchecked
                                            },
                                            contentDescription = chapter.status,
                                            tint = statusColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = chapter.title,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isDone) FontWeight.Normal else FontWeight.SemiBold,
                                                color = if (isDone) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                            )
                                        )

                                        // Spaced Repetition topic-level metadata
                                        if (revisionItem != null) {
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = if (revisionItem.isDueToday) {
                                                    "🔄 Due Today • Interval: ${revisionItem.currentIntervalDays.toInt()}d"
                                                } else if (revisionItem.isOverdue) {
                                                    "🔄 ${revisionItem.daysOverdue}d overdue • Interval: ${revisionItem.currentIntervalDays.toInt()}d"
                                                } else {
                                                    "🔄 Next review: ${revisionItem.nextDueDate} (Interval: ${revisionItem.currentIntervalDays.toInt()}d)"
                                                },
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = if (revisionItem.isOverdue) Color(0xFFEF4444) else BrandDeepTeal
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    // Spaced Repetition Contextual Action Button
                                    Box {
                                        IconButton(
                                            onClick = {
                                                if (revisionItem == null) {
                                                    onTrackChapterInRevision(subject.id, chapter.id)
                                                } else {
                                                    showChapterRevisionMenu = true
                                                }
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Autorenew,
                                                contentDescription = if (revisionItem == null) "Track in Revision" else "Revision Status",
                                                tint = if (revisionItem != null) BrandDeepTeal else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        if (revisionItem != null) {
                                            DropdownMenu(
                                                expanded = showChapterRevisionMenu,
                                                onDismissRequest = { showChapterRevisionMenu = false }
                                            ) {
                                                DropdownMenuItem(
                                                    text = { Text("Review Now") },
                                                    onClick = {
                                                        showChapterRevisionMenu = false
                                                        reviewDialogTargetItem = revisionItem
                                                    }
                                                )
                                                DropdownMenuItem(
                                                    text = { Text("Open Revision Hub") },
                                                    onClick = {
                                                        showChapterRevisionMenu = false
                                                        onOpenRevisionHub()
                                                    }
                                                )
                                                DropdownMenuItem(
                                                    text = { Text("Remove from Revision", color = Color(0xFFEF4444)) },
                                                    onClick = {
                                                        showChapterRevisionMenu = false
                                                        stopTrackingTargetChapterId = chapter.id
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(4.dp))

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(statusColor.copy(alpha = 0.12f))
                                            .clickable {
                                                val nextStatus = when (chapter.status) {
                                                    "NOT_STARTED" -> "IN_PROGRESS"
                                                    "IN_PROGRESS" -> "COMPLETED"
                                                    else -> "NOT_STARTED"
                                                }
                                                onUpdateChapterStatus(chapter, nextStatus)
                                            }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = statusLabel,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = statusColor
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(4.dp))

                                    IconButton(
                                        onClick = { onDeleteChapter(chapter) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Delete chapter",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Active review recall feedback dialog
        if (reviewDialogTargetItem != null) {
            val item = reviewDialogTargetItem!!
            RecallFeedbackDialog(
                chapterTitle = item.chapterTitle,
                onConfirmRating = { rating, notes ->
                    onReviewNow(item.id, rating, item.estimatedDurationMinutes * 60, notes)
                    reviewDialogTargetItem = null
                },
                onDismiss = { reviewDialogTargetItem = null }
            )
        }

        // Stop tracking confirmation dialog
        if (stopTrackingTargetChapterId != null) {
            val chapId = stopTrackingTargetChapterId!!
            AlertDialog(
                onDismissRequest = { stopTrackingTargetChapterId = null },
                title = { Text("Remove from Revision?") },
                text = {
                    Text("This chapter will no longer be tracked in automated spaced repetition. The chapter will remain intact in your syllabus.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onRemoveChapterFromRevision(chapId)
                            stopTrackingTargetChapterId = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                    ) {
                        Text("Remove Tracking", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { stopTrackingTargetChapterId = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }

    if (showAddChapterSheet) {
        AddChapterBottomSheet(
            subjectName = subject.name,
            existingChapters = chapters,
            onDismiss = { showAddChapterSheet = false },
            onAddChapter = { title, initialStatus ->
                onAddChapter(title)
                showAddChapterSheet = false
            }
        )
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Delete ${subject.name}?", fontWeight = FontWeight.Bold) },
            text = { Text("This will permanently remove this subject and all its chapters. This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteSubject(subject)
                        showDeleteConfirmDialog = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusDanger)
                ) {
                    Text("Delete Subject")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddChapterBottomSheet(
    subjectName: String,
    existingChapters: List<ChapterEntity>,
    onDismiss: () -> Unit,
    onAddChapter: (title: String, initialStatus: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var chapterTitle by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("NOT_STARTED") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
                text = "Add Chapter",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Subject: $subjectName",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = chapterTitle,
                onValueChange = {
                    chapterTitle = it
                    errorMessage = null
                },
                label = { Text("Chapter Name *") },
                placeholder = { Text("e.g. Integration by Parts") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                isError = errorMessage != null,
                supportingText = errorMessage?.let { { Text(it, color = StatusDanger) } },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Initial Status",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val statuses = listOf(
                    Triple("NOT_STARTED", "To-Do", MaterialTheme.colorScheme.onSurfaceVariant),
                    Triple("IN_PROGRESS", "In Progress", StatusWarning),
                    Triple("COMPLETED", "Completed", StatusSuccess)
                )

                statuses.forEach { (code, label, color) ->
                    val isSelected = selectedStatus == code
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedStatus = code }
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) color else MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) color.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) color else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

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
                        val trimmed = chapterTitle.trim()
                        if (trimmed.isBlank()) {
                            errorMessage = "Chapter name cannot be empty"
                        } else if (existingChapters.any { it.title.equals(trimmed, ignoreCase = true) }) {
                            errorMessage = "A chapter with this name already exists in $subjectName"
                        } else {
                            onAddChapter(trimmed, selectedStatus)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Save Chapter", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
