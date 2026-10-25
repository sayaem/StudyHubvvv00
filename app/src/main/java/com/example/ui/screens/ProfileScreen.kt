package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserProfile
import com.example.ui.components.StudentAvatar
import com.example.ui.theme.BorderSubtleLight
import com.example.ui.theme.BrandPaleTeal
import com.example.ui.theme.BrandTeal
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

@Composable
fun ProfileScreen(
    userProfile: UserProfile?,
    completedChaptersCount: Int,
    totalStudyHours: Float,
    onOpenSettings: () -> Unit,
    onOpenAdmissionCalendar: () -> Unit,
    onOpenAnalytics: () -> Unit,
    onOpenNotes: () -> Unit,
    onEditProfile: () -> Unit,
    onBack: (() -> Unit)? = null
) {
    val name = userProfile?.displayName?.ifBlank { "Student" } ?: "Student"
    val avatarId = userProfile?.avatarId ?: "avatar_1"
    val level = userProfile?.academicLevel ?: "HSC"
    val group = userProfile?.academicGroup ?: "Science"
    val track = userProfile?.admissionTrack

    val subtitle = if (level == "Admission") {
        "Admission • ${track ?: "General"}"
    } else {
        "$level • $group"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onBack != null) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = "Profile",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
            }

            IconButton(onClick = onOpenSettings) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Profile Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(22.dp)),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                StudentAvatar(avatarId = avatarId, size = 88.dp)

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = onEditProfile,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp), tint = BrandTeal)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit Profile", color = BrandTeal, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Real Stats Row (No fake numbers)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp)),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format("%.1fh", totalStudyHours),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = BrandTeal
                    )
                    Text("Total Focus", fontSize = 11.sp, color = TextSecondary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$completedChaptersCount",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF2563EB)
                    )
                    Text("Chapters Done", fontSize = 11.sp, color = TextSecondary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val goalH = (userProfile?.dailyGoalMinutes ?: 240) / 60
                    Text(
                        text = "${goalH}h",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF10B981)
                    )
                    Text("Daily Target", fontSize = 11.sp, color = TextSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Study Tools & Management",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        var activeModal by remember { mutableStateOf<ProfileDialogSection?>(null) }

        // Clean, well-spaced list of navigation cards with subtle right-arrows
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ProfileMenuItem(
                icon = Icons.AutoMirrored.Filled.HelpOutline,
                iconTint = BrandTeal,
                title = "Help & Support",
                subtitle = "FAQs, contact support, bug reports",
                onClick = { activeModal = ProfileDialogSection.HELP_SUPPORT }
            )

            ProfileMenuItem(
                icon = Icons.Default.Info,
                iconTint = Color(0xFF2563EB),
                title = "About StudyHub",
                subtitle = "App version, mission, developer info",
                onClick = { activeModal = ProfileDialogSection.ABOUT }
            )

            ProfileMenuItem(
                icon = Icons.Default.Description,
                iconTint = Color(0xFFF59E0B),
                title = "Terms & Conditions",
                subtitle = "Legal usage terms and academic guidelines",
                onClick = { activeModal = ProfileDialogSection.TERMS }
            )

            ProfileMenuItem(
                icon = Icons.Default.Security,
                iconTint = Color(0xFF10B981),
                title = "Privacy Policy",
                subtitle = "Data handling and privacy guidelines",
                onClick = { activeModal = ProfileDialogSection.PRIVACY }
            )
        }

        // Section Modals with comprehensive student info
        activeModal?.let { modal ->
            when (modal) {
                ProfileDialogSection.HELP_SUPPORT -> {
                    AlertDialog(
                        onDismissRequest = { activeModal = null },
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null, tint = BrandTeal)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Help & Support", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                        },
                        text = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = "Frequently Asked Questions",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "• How does the Focus Timer work?\nStudyHub uses Pomodoro, Deep Focus, and Stopwatch modes to track your actual study sessions with built-in ambient sounds and distraction tracking.\n\n" +
                                            "• How is Syllabus Progress calculated?\nProgress is based on chapters completed vs. total chapters planned across your active academic subjects.\n\n" +
                                            "• How do I set study targets?\nUse the Admission Calendar or Planner to schedule targeted preparation for Board exams or university entrance.\n\n" +
                                            "• Need Direct Assistance?\nContact student support directly at support@studyhub.edu or report technical issues to our development team.",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 18.sp
                                )
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = { activeModal = null },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandTeal)
                            ) {
                                Text("Close")
                            }
                        }
                    )
                }

                ProfileDialogSection.ABOUT -> {
                    AlertDialog(
                        onDismissRequest = { activeModal = null },
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF2563EB))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("About StudyHub", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                        },
                        text = {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "StudyHub v2.0 (Student Edition)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Modern Student Productivity & Academic Planner",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "StudyHub is crafted to empower secondary and higher secondary students preparing for Board exams and competitive university admissions. Designed with high-performance time management, syllabus tracking, real-time analytics, and personalized goals.",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 18.sp
                                )
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = { activeModal = null },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                            ) {
                                Text("Done")
                            }
                        }
                    )
                }

                ProfileDialogSection.TERMS -> {
                    AlertDialog(
                        onDismissRequest = { activeModal = null },
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFFF59E0B))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Terms & Conditions", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                        },
                        text = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = "Academic Use License",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "1. Personal Education Use: StudyHub is provided free for individual student academic planning and progress tracking.\n\n" +
                                            "2. Academic Integrity: You are responsible for ensuring that notes, tasks, and study records maintained within the application adhere to your academic institution's code of conduct.\n\n" +
                                            "3. Data Availability: Your study data is stored securely on your device with options for local backup and cloud synchronization when configured.",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 18.sp
                                )
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = { activeModal = null },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B))
                            ) {
                                Text("I Understand")
                            }
                        }
                    )
                }

                ProfileDialogSection.PRIVACY -> {
                    AlertDialog(
                        onDismissRequest = { activeModal = null },
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF10B981))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Privacy Policy", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                        },
                        text = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = "Student Data Privacy Commitment",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "• Zero Third-Party Tracking: StudyHub does not track your private browsing or sell student data to advertising networks.\n\n" +
                                            "• Local-First Privacy: Your study notes, chapter checklists, timers, and personal routines reside on your device storage.\n\n" +
                                            "• Full User Control: You have complete freedom to export, back up, or reset all personal study history anytime via the Settings panel.",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 18.sp
                                )
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = { activeModal = null },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                            ) {
                                Text("Understood")
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

enum class ProfileDialogSection {
    HELP_SUPPORT,
    ABOUT,
    TERMS,
    PRIVACY
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
