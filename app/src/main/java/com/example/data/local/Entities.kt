package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Long = 1L,
    val displayName: String = "",
    val avatarId: String = "avatar_1",
    val academicLevel: String = "HSC", // "SSC", "HSC", "Admission"
    val academicGroup: String = "Science", // "Science", "Humanities", "Business Studies", "Class 9-10"
    val targetYear: String = "2025",
    val admissionTrack: String? = null, // "General University", "Medical", "Engineering", "Other"
    val dailyGoalMinutes: Int = 240, // 4 hours default
    val preferredStudyTime: String = "Morning", // "Morning", "Afternoon", "Evening", "Night"
    val isOnboardingCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val banglaName: String? = null,
    val colorHex: String = "#0796A3",
    val iconKey: String = "calculator", // "calculator", "book", "atom", "flask", "stethoscope", "compass", "pen", "palette"
    val category: String = "Compulsory", // "Compulsory", "Optional"
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chapters")
data class ChapterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val subjectId: Long,
    val title: String,
    val sortOrder: Int = 0,
    val status: String = "NOT_STARTED", // "NOT_STARTED", "IN_PROGRESS", "COMPLETED"
    val notes: String? = null,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "study_tasks")
data class StudyTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val date: String, // YYYY-MM-DD
    val time: String? = null, // HH:mm
    val durationMinutes: Int = 30,
    val subjectId: Long? = null,
    val chapterId: Long? = null,
    val targetId: Long? = null,
    val status: String = "PENDING", // "PENDING", "COMPLETED", "CANCELLED"
    val priority: String = "MEDIUM", // "LOW", "MEDIUM", "HIGH"
    val notes: String? = null,
    val planType: String = "STUDY", // "STUDY", "REVISION", "EXAM", "ASSIGNMENT", "BREAK", "PERSONAL"
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "study_sessions")
data class StudySessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val subjectId: Long? = null,
    val chapterId: Long? = null,
    val taskId: Long? = null,
    val startedAt: Long,
    val endedAt: Long,
    val elapsedSeconds: Int,
    val status: String = "COMPLETED", // "COMPLETED", "ABANDONED", "ENDED_EARLY"
    val sessionType: String = "POMODORO_FOCUS", // "POMODORO_FOCUS", "SHORT_BREAK", "LONG_BREAK", "CUSTOM", "DEEP_FOCUS", "STOPWATCH"
    val reflectionRating: Int = 5,
    val reflectionNotes: String? = null,
    val distractions: Int = 0
)

@Entity(tableName = "admission_targets")
data class AdmissionTargetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val institutionName: String,
    val category: String = "General University", // "General University", "Medical", "Engineering", "Other"
    val programUnit: String? = null, // "A Unit", "Engineering & Architecture", "MBBS"
    val cycleLabel: String = "2025",
    val applicationOpenDate: String? = null, // YYYY-MM-DD
    val applicationDeadline: String, // YYYY-MM-DD
    val admitCardDate: String? = null,
    val examDate: String? = null,
    val resultDate: String? = null,
    val applicationFee: String? = null,
    val applicationStatus: String = "PLANNING", // "INTERESTED", "PLANNING", "APPLICATION_OPEN", "IN_PROGRESS", "APPLIED", "ADMIT_CARD_AVAILABLE", "EXAM_COMPLETED", "RESULT_PUBLISHED", "NOT_APPLYING"
    val prepProgress: Int = 0, // 0 - 100
    val notes: String? = null,
    val officialUrl: String? = null,
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "study_notes")
data class StudyNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val subjectId: Long? = null,
    val title: String,
    val body: String,
    val tags: String? = null,
    val isPinned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notification_items")
data class NotificationItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val body: String,
    val category: String = "STUDY", // "STUDY", "ADMISSION", "DAILY_GOAL", "SYSTEM"
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val actionRoute: String? = null
)

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val themeMode: String = "LIGHT", // "LIGHT", "DARK", "SYSTEM"
    val studyRemindersEnabled: Boolean = true, // Master Notifications ON/OFF
    val soundEnabled: Boolean = true,
    val quietHoursStartHour: Int = 22, // 10 PM
    val quietHoursEndHour: Int = 7,    // 7 AM
    val lastNotificationTime: Long = 0L,
    val consecutiveIgnoredCount: Int = 0
)

@Entity(tableName = "focus_presets")
data class FocusPresetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val mode: String, // "POMODORO", "DEEP_FOCUS", "SIMPLE_TIMER", "STOPWATCH"
    val workMinutes: Int,
    val shortBreakMinutes: Int = 5,
    val longBreakMinutes: Int = 15,
    val targetCycles: Int = 4,
    val isStrict: Boolean = false,
    val isDefault: Boolean = false,
    val autoStartBreaks: Boolean = false,
    val autoStartNextFocus: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "focus_gamification")
data class FocusGamificationEntity(
    @PrimaryKey val id: Long = 1L,
    val totalXp: Int = 0,
    val currentStreakDays: Int = 0,
    val longestStreakDays: Int = 0,
    val lastFocusDateStr: String = "",
    val totalSessionsCompleted: Int = 0,
    val totalFocusMinutes: Int = 0,
    val unlockedAchievementIds: String = "" // comma-separated e.g. "first_session,streak_3"
)

@Entity(tableName = "active_focus_session")
data class ActiveFocusSessionEntity(
    @PrimaryKey val id: Int = 1,
    val sessionState: String = "IDLE", // IDLE, FOCUS_RUNNING, FOCUS_PAUSED, SHORT_BREAK_RUNNING, SHORT_BREAK_PAUSED, LONG_BREAK_RUNNING, LONG_BREAK_PAUSED, COMPLETED, ENDED_EARLY
    val mode: String = "POMODORO", // POMODORO, DEEP_FOCUS, SIMPLE_TIMER, STOPWATCH
    val phase: String = "WORK", // WORK, SHORT_BREAK, LONG_BREAK
    val totalDurationSeconds: Int = 25 * 60,
    val timeRemainingSeconds: Int = 25 * 60,
    val startTimeMs: Long = 0L,
    val accumulatedPauseMs: Long = 0L,
    val lastPauseMs: Long = 0L,
    val lastTickMs: Long = System.currentTimeMillis(),
    val currentCycle: Int = 1,
    val targetCycles: Int = 4,
    val workMinutes: Int = 25,
    val shortBreakMinutes: Int = 5,
    val longBreakMinutes: Int = 15,
    val autoStartBreaks: Boolean = false,
    val autoStartNextFocus: Boolean = false,
    val subjectId: Long? = null,
    val chapterId: Long? = null,
    val goalTask: String? = null,
    val isStrict: Boolean = false,
    val distractions: Int = 0,
    val ambientSoundType: String = "OFF",
    val ambientVolume: Float = 0.5f,
    val reflectionNotes: String? = null,
    val rating: Int = 5
)


