package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.revision.RevisionSummaryMetrics

/**
 * Compact, Premium Study Buddy Home Entry Card (StudyBuddy 2.0).
 * Features a distinctive custom AI emblem, real grounded context status line,
 * 1-line quick prompt pills, and a compact secondary action button (no oversized CTA).
 */
@Composable
fun StudyBuddyHomeCard(
    studentName: String?,
    revisionMetrics: RevisionSummaryMetrics?,
    todayTasksCount: Int,
    nextExamTarget: String?,
    onOpenStudyBuddy: (initialPrompt: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val dueRevisions = (revisionMetrics?.dueTodayCount ?: 0) + (revisionMetrics?.overdueCount ?: 0)
    val hasRevisions = dueRevisions > 0
    val hasTasks = todayTasksCount > 0
    val hasExam = !nextExamTarget.isNullOrBlank()

    // Contextual status message based on actual data
    val statusText = when {
        hasRevisions -> "⚡ $dueRevisions topics due for revision"
        hasTasks -> "📅 $todayTasksCount study tasks scheduled today"
        hasExam -> "🎯 $nextExamTarget target track"
        !studentName.isNullOrBlank() -> "Grounded AI partner for $studentName"
        else -> "Grounded in your syllabus & study data"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onOpenStudyBuddy(null) }
            .testTag("study_buddy_home_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            // Header Row: Emblem + Title & Status + Compact Arrow Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    StudyBuddyEmblem(size = EmblemSize.MEDIUM)

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Study Buddy",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                            ) {
                                Text(
                                    text = "AI Companion",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = statusText,
                            fontSize = 11.sp,
                            color = if (hasRevisions) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (hasRevisions) FontWeight.SemiBold else FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Compact Secondary Action Button (Arrow in circular background)
                IconButton(
                    onClick = { onOpenStudyBuddy(null) },
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                        .testTag("study_buddy_open_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open Study Buddy",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Single-Line Compact Quick Prompt Pills
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(end = 4.dp)
            ) {
                item {
                    CompactPromptPill(
                        label = if (hasRevisions) "Revise due topics" else "What to study next?",
                        onClick = {
                            onOpenStudyBuddy(if (hasRevisions) "Help me revise my due topics" else "What should I study next?")
                        }
                    )
                }
                item {
                    CompactPromptPill(
                        label = "Plan today's study",
                        onClick = { onOpenStudyBuddy("Help me plan my study schedule for today.") }
                    )
                }
                item {
                    CompactPromptPill(
                        label = "Explain syllabus progress",
                        onClick = { onOpenStudyBuddy("Explain my current syllabus and focus progress.") }
                    )
                }
            }
        }
    }
}

@Composable
private fun CompactPromptPill(
    label: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag("quick_prompt_$label")
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
        )
    }
}
