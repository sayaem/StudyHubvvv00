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
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderSubtleLight
import com.example.ui.theme.BrandTeal
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

val SUBJECT_PALETTE = listOf(
    "#0796A3", // Teal
    "#2563EB", // Blue
    "#F97316", // Orange
    "#EC4899", // Pink
    "#8B5CF6", // Purple
    "#10B981", // Green
    "#1E293B", // Navy
    "#EF4444"  // Coral
)

val SUBJECT_ICONS = listOf(
    "calculator" to Icons.Default.Calculate,
    "book" to Icons.Default.MenuBook,
    "atom" to Icons.Default.Science,
    "flask" to Icons.Default.Biotech,
    "stethoscope" to Icons.Default.MedicalServices,
    "compass" to Icons.Default.Architecture,
    "pen" to Icons.Default.Create,
    "palette" to Icons.Default.Palette
)

fun getSubjectIcon(key: String): ImageVector {
    return SUBJECT_ICONS.find { it.first == key }?.second ?: Icons.Default.MenuBook
}

fun parseColorHex(hex: String, fallback: Color = BrandTeal): Color {
    return try {
        val clean = hex.removePrefix("#")
        val colorLong = clean.toLong(16)
        if (clean.length == 6) Color(colorLong or 0x00000000FF000000)
        else Color(colorLong)
    } catch (e: Exception) {
        fallback
    }
}

@Composable
fun LiveSubjectCardPreview(
    name: String,
    banglaName: String?,
    colorHex: String,
    iconKey: String,
    chapterCount: Int,
    completedChapters: Int = 0,
    modifier: Modifier = Modifier
) {
    val subjectColor = parseColorHex(colorHex)
    val icon = getSubjectIcon(iconKey)
    val displayName = name.ifBlank { "Subject Name" }
    val progress = if (chapterCount > 0) completedChapters.toFloat() / chapterCount else 0f
    val percentText = if (chapterCount > 0) "${(progress * 100).toInt()}%" else "No chapters"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon in colored container
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(subjectColor.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = displayName,
                    tint = subjectColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        maxLines = 1
                    )
                    if (!banglaName.isNullOrBlank()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "($banglaName)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (chapterCount > 0) "$completedChapters / $chapterCount chapters" else "0 chapters added",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                if (chapterCount > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = subjectColor,
                        trackColor = subjectColor.copy(alpha = 0.16f),
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = percentText,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (chapterCount > 0) subjectColor else MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
