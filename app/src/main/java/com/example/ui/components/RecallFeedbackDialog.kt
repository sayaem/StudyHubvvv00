package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.revision.RecallRating
import com.example.ui.theme.BrandTeal

/**
 * Accessible, clear recall feedback dialog after completing a revision session.
 * Presents exactly 3 transparent options per engineering requirements:
 * - Hard (Reset to 1 day for reinforcement)
 * - Okay (1.5x moderate interval expansion)
 * - Easy (2.0x solid retention expansion)
 */
@Composable
fun RecallFeedbackDialog(
    chapterTitle: String,
    onConfirmRating: (rating: RecallRating, notes: String?) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedRating by remember { mutableStateOf<RecallRating>(RecallRating.OKAY) }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Recall Feedback",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = chapterTitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "How easily did you recall key concepts during this review?",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 3 Accessible options
                RecallOptionCard(
                    rating = RecallRating.HARD,
                    title = "Hard",
                    subtitle = "Struggled to recall — reset interval to 1 day",
                    icon = Icons.Default.SentimentDissatisfied,
                    accentColor = Color(0xFFEF4444),
                    isSelected = selectedRating == RecallRating.HARD,
                    onClick = { selectedRating = RecallRating.HARD }
                )

                Spacer(modifier = Modifier.height(8.dp))

                RecallOptionCard(
                    rating = RecallRating.OKAY,
                    title = "Okay",
                    subtitle = "Remembered main ideas — moderate 1.5x pacing",
                    icon = Icons.Default.SentimentNeutral,
                    accentColor = Color(0xFFF59E0B),
                    isSelected = selectedRating == RecallRating.OKAY,
                    onClick = { selectedRating = RecallRating.OKAY }
                )

                Spacer(modifier = Modifier.height(8.dp))

                RecallOptionCard(
                    rating = RecallRating.EASY,
                    title = "Easy",
                    subtitle = "Recalled confidently — double interval (2.0x)",
                    icon = Icons.Default.SentimentSatisfiedAlt,
                    accentColor = Color(0xFF10B981),
                    isSelected = selectedRating == RecallRating.EASY,
                    onClick = { selectedRating = RecallRating.EASY }
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Session Notes (Optional)") },
                    placeholder = { Text("Key formulas or tricky questions noted") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("recall_feedback_notes"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirmRating(selectedRating, notes.ifBlank { null })
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandTeal),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("submit_recall_feedback_button")
            ) {
                Text("Save Review & Update Interval", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dismiss_recall_feedback_button")
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun RecallOptionCard(
    rating: RecallRating,
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) accentColor else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(14.dp)
            )
            .testTag("recall_option_${rating.name.lowercase()}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) accentColor.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = if (isSelected) 0.2f else 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
