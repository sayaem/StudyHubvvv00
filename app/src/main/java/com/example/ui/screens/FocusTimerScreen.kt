package com.example.ui.screens

import com.example.revision.RecallRating
import com.example.ui.components.RecallFeedbackDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.ChapterEntity
import com.example.data.local.FocusGamificationEntity
import com.example.data.local.FocusPresetEntity
import com.example.data.local.StudySessionEntity
import com.example.data.local.SubjectEntity
import com.example.ui.FocusSessionState
import com.example.ui.TimerStatus
import com.example.ui.components.getSubjectIcon
import com.example.ui.components.parseColorHex
import com.example.ui.theme.BrandDeepTeal
import com.example.ui.theme.BrandTeal
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusTimerScreen(
    timerStatus: TimerStatus,
    timerMode: String,
    pomodoroPhase: String,
    currentCycle: Int,
    targetCycles: Int,
    timeRemainingSeconds: Int,
    totalDurationSeconds: Int,
    selectedSessionType: String,
    selectedSubjectId: Long?,
    selectedChapterId: Long? = null,
    selectedGoalTask: String?,
    isStrictFocus: Boolean,
    distractionCount: Int = 0,
    workMinutes: Int = 25,
    shortBreakMinutes: Int = 5,
    longBreakMinutes: Int = 15,
    autoStartBreaks: Boolean = false,
    autoStartNextFocus: Boolean = false,
    ambientSoundType: String,
    ambientVolume: Float,
    subjects: List<SubjectEntity>,
    chapters: List<ChapterEntity>,
    sessions: List<StudySessionEntity>,
    presets: List<FocusPresetEntity>,
    gamification: FocusGamificationEntity?,
    dailyGoalMinutes: Int = 240,
    onSelectMode: (String) -> Unit,
    onSelectDurationMinutes: (Int) -> Unit = {},
    onApplyPreset: (FocusPresetEntity) -> Unit,
    onSaveCustomPreset: (String, String, Int, Int, Int, Int, Boolean, Boolean, Boolean) -> Unit,
    onDeletePreset: (FocusPresetEntity) -> Unit,
    onUpdateSettings: (Int, Int, Int, Int, Boolean, Boolean) -> Unit = { _, _, _, _, _, _ -> },
    onSelectSubject: (Long?) -> Unit,
    onSelectChapter: (Long?) -> Unit = {},
    onSetGoalTask: (String?) -> Unit,
    onRecordDistraction: () -> Unit = {},
    onToggleStrict: (Boolean) -> Unit,
    onSetAmbientSound: (String) -> Unit,
    onSetAmbientVolume: (Float) -> Unit,
    onStartTimer: () -> Unit,
    onPauseTimer: () -> Unit,
    onResumeTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onSkipPhase: () -> Unit,
    onFinishTimer: (Boolean, Int, String?, Int) -> Unit,
    onDeleteSession: (StudySessionEntity) -> Unit = {},
    activeRevisionItemId: Long? = null,
    onCompleteRevisionReview: ((itemId: Long, rating: RecallRating, durationSeconds: Int, notes: String?) -> Unit)? = null
) {
    var activeTab by remember { mutableStateOf("TIMER") } // "TIMER" or "ANALYTICS"
    var showAmbientSheet by remember { mutableStateOf(false) }
    var showGoalDialog by remember { mutableStateOf(false) }
    var showCustomPresetSheet by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    var showReflectionDialog by remember { mutableStateOf(false) }
    var showRecallFeedbackDialog by remember { mutableStateOf(false) }
    var pendingRevisionElapsedSeconds by remember { mutableIntStateOf(0) }
    var showEmergencyExitDialog by remember { mutableStateOf(false) }
    var showCustomDurationDialog by remember { mutableStateOf(false) }

    // Gamification level calculation
    val xp = gamification?.totalXp ?: 0
    val userLevel = 1 + (xp / 1000)
    val streakDays = gamification?.currentStreakDays ?: 0

    // Auto trigger reflection dialog when timer completes normally
    var reflectionTriggered by remember { mutableStateOf(false) }
    if (timerStatus == TimerStatus.COMPLETED && !reflectionTriggered) {
        showReflectionDialog = true
        reflectionTriggered = true
    } else if (timerStatus != TimerStatus.COMPLETED) {
        reflectionTriggered = false
    }

    val primaryModeColor = when (timerMode) {
        "POMODORO" -> when (pomodoroPhase) {
            "SHORT_BREAK" -> Color(0xFFF59E0B) // Amber
            "LONG_BREAK" -> Color(0xFF6366F1) // Indigo
            else -> BrandTeal // Emerald/Teal
        }
        "DEEP_FOCUS" -> Color(0xFF8B5CF6) // Purple
        "SIMPLE_TIMER" -> Color(0xFF2563EB) // Blue
        else -> Color(0xFF10B981) // Stopwatch Green
    }

    val activeSubject = subjects.find { it.id == selectedSubjectId }
    val activeChapter = chapters.find { it.id == selectedChapterId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(start = 16.dp, end = 16.dp, top = 2.dp, bottom = 8.dp)
    ) {
        // --- Top Bar Header ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = "Focus Command Center",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        letterSpacing = (-0.2).sp,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Level $userLevel Scholar • $streakDays Day Streak 🔥",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = BrandTeal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Ambient Sound Chip
                Surface(
                    onClick = { showAmbientSheet = true },
                    shape = RoundedCornerShape(16.dp),
                    color = if (ambientSoundType != "OFF") BrandTeal.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (ambientSoundType != "OFF") BrandTeal else MaterialTheme.colorScheme.outline
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (ambientSoundType != "OFF") Icons.Default.GraphicEq else Icons.Default.MusicNote,
                            contentDescription = "Ambient Sounds",
                            tint = if (ambientSoundType != "OFF") BrandTeal else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (ambientSoundType != "OFF") ambientSoundType else "Sound",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (ambientSoundType != "OFF") BrandTeal else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }

                // Focus Settings Cog
                Surface(
                    onClick = { showSettingsSheet = true },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Box(
                        modifier = Modifier.size(30.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Focus Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                // Stats / Timer Pill Button (Horizontal, compact, never wraps vertically)
                Surface(
                    onClick = { activeTab = if (activeTab == "TIMER") "ANALYTICS" else "TIMER" },
                    shape = RoundedCornerShape(16.dp),
                    color = if (activeTab == "ANALYTICS") BrandTeal else BrandTeal.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandTeal)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (activeTab == "TIMER") Icons.Default.Analytics else Icons.Default.Timer,
                            contentDescription = if (activeTab == "TIMER") "Stats" else "Timer",
                            tint = if (activeTab == "ANALYTICS") Color.White else BrandTeal,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (activeTab == "TIMER") "Stats" else "Timer",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeTab == "ANALYTICS") Color.White else BrandTeal,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (activeTab == "TIMER") {
            // ==========================================
            // ============ TIMER TAB VIEW =============
            // ==========================================

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Mode Switcher Tabs
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val modes = listOf(
                            Triple("POMODORO", "Pomodoro", "Cycles"),
                            Triple("DEEP_FOCUS", "Deep Focus", "50m Block"),
                            Triple("SIMPLE_TIMER", "Timer", "Custom"),
                            Triple("STOPWATCH", "Stopwatch", "Count-up")
                        )
                        modes.forEach { (modeKey, label, _) ->
                            val isSelected = timerMode == modeKey
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent)
                                    .border(
                                        width = if (isSelected) 1.dp else 0.dp,
                                        color = if (isSelected) primaryModeColor.copy(alpha = 0.5f) else Color.Transparent,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { onSelectMode(modeKey) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) primaryModeColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }
                }

                // 2. Hero Interactive Circular Timer Display Card
                item {
                    val progress = if (timerMode == "STOPWATCH") 1f else {
                        if (totalDurationSeconds > 0) {
                            1f - (timeRemainingSeconds.toFloat() / totalDurationSeconds)
                        } else 0f
                    }

                    val displayMinutes = timeRemainingSeconds / 60
                    val displaySeconds = timeRemainingSeconds % 60
                    val displayTime = if (displayMinutes >= 60) {
                        val hours = displayMinutes / 60
                        val mins = displayMinutes % 60
                        String.format("%02d:%02d:%02d", hours, mins, displaySeconds)
                    } else {
                        String.format("%02d:%02d", displayMinutes, displaySeconds)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp, horizontal = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Stable top indicator slot (fixed height 26.dp)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(26.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (timerMode == "POMODORO") {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Cycle $currentCycle of $targetCycles",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextSecondary
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        (1..targetCycles).forEach { idx ->
                                            Box(
                                                modifier = Modifier
                                                    .size(9.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        when {
                                                            idx < currentCycle -> StatusSuccess
                                                            idx == currentCycle -> primaryModeColor
                                                            else -> MaterialTheme.colorScheme.surfaceVariant
                                                        }
                                                    )
                                            )
                                        }
                                    }
                                } else {
                                    Text(
                                        text = when (timerMode) {
                                            "DEEP_FOCUS" -> "50 min Uninterrupted Block"
                                            "SIMPLE_TIMER" -> "Custom Countdown Timer"
                                            else -> "Open-ended Stopwatch Session"
                                        },
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            val trackColor = MaterialTheme.colorScheme.surfaceVariant

                            // Main Circular Ring Timer
                            Box(
                                modifier = Modifier.size(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val strokeWidth = 14.dp.toPx()
                                    val diameter = size.minDimension - strokeWidth
                                    val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                                    val arcSize = Size(diameter, diameter)

                                    // Track
                                    drawArc(
                                        color = trackColor,
                                        startAngle = -90f,
                                        sweepAngle = 360f,
                                        useCenter = false,
                                        topLeft = topLeft,
                                        size = arcSize,
                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                    )

                                    // Progress
                                    drawArc(
                                        color = primaryModeColor,
                                        startAngle = -90f,
                                        sweepAngle = progress * 360f,
                                        useCenter = false,
                                        topLeft = topLeft,
                                        size = arcSize,
                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                    )
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = displayTime,
                                        fontSize = if (displayMinutes >= 60) 34.sp else 42.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = (-1).sp,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )

                                    Text(
                                        text = when (timerMode) {
                                            "POMODORO" -> when (pomodoroPhase) {
                                                "SHORT_BREAK" -> "Short Break"
                                                "LONG_BREAK" -> "Long Break"
                                                else -> "Work Focus"
                                            }
                                            "DEEP_FOCUS" -> "Deep Focus Block"
                                            "SIMPLE_TIMER" -> "Countdown Timer"
                                            else -> "Stopwatch Elapsed"
                                        },
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = primaryModeColor
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Big Play/Pause Action Button inside Ring with crisp styling
                                    Surface(
                                        onClick = {
                                            when (timerStatus) {
                                                TimerStatus.IDLE -> onStartTimer()
                                                TimerStatus.RUNNING -> onPauseTimer()
                                                TimerStatus.PAUSED -> onResumeTimer()
                                                TimerStatus.COMPLETED -> onResetTimer()
                                            }
                                        },
                                        shape = CircleShape,
                                        color = primaryModeColor,
                                        border = androidx.compose.foundation.BorderStroke(2.dp, Color.White.copy(alpha = 0.4f)),
                                        shadowElevation = 3.dp,
                                        modifier = Modifier
                                            .size(56.dp)
                                            .testTag("focus_play_pause_button")
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = when (timerStatus) {
                                                    TimerStatus.RUNNING -> Icons.Default.Pause
                                                    else -> Icons.Default.PlayArrow
                                                },
                                                contentDescription = "Timer Action",
                                                tint = Color.White,
                                                modifier = Modifier.size(30.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Secondary Control Slot: Stable 40dp height container for both Idle & Active states
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (timerStatus == TimerStatus.RUNNING || timerStatus == TimerStatus.PAUSED) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Reset Button
                                        Surface(
                                            onClick = onResetTimer,
                                            shape = RoundedCornerShape(12.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(38.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 6.dp),
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    Icons.Default.Refresh,
                                                    contentDescription = "Reset",
                                                    modifier = Modifier.size(15.dp),
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(
                                                    "Reset",
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    maxLines = 1,
                                                    softWrap = false
                                                )
                                            }
                                        }

                                        // Skip Button (if Pomodoro)
                                        if (timerMode == "POMODORO") {
                                            Surface(
                                                onClick = onSkipPhase,
                                                shape = RoundedCornerShape(12.dp),
                                                color = MaterialTheme.colorScheme.surfaceVariant,
                                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(38.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 6.dp),
                                                    horizontalArrangement = Arrangement.Center,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        Icons.Default.SkipNext,
                                                        contentDescription = "Skip",
                                                        modifier = Modifier.size(15.dp),
                                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                    Text(
                                                        "Skip",
                                                        fontSize = 11.5.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        maxLines = 1,
                                                        softWrap = false
                                                    )
                                                }
                                            }
                                        }

                                        // Distraction Counter
                                        Surface(
                                            onClick = onRecordDistraction,
                                            shape = RoundedCornerShape(12.dp),
                                            color = if (distractionCount > 0) Color(0xFFEF4444).copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant,
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                if (distractionCount > 0) Color(0xFFEF4444) else MaterialTheme.colorScheme.outline
                                            ),
                                            modifier = Modifier
                                                .weight(if (timerMode == "POMODORO") 1.25f else 1.4f)
                                                .height(38.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 6.dp),
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.NotificationsActive,
                                                    contentDescription = "Distraction",
                                                    tint = if (distractionCount > 0) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(
                                                    text = if (distractionCount > 0) "+ Distract ($distractionCount)" else "+ Distract",
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (distractionCount > 0) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    maxLines = 1,
                                                    softWrap = false
                                                )
                                            }
                                        }

                                        // Finish Button
                                        Surface(
                                            onClick = { showReflectionDialog = true },
                                            shape = RoundedCornerShape(12.dp),
                                            color = primaryModeColor,
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(38.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 6.dp),
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Default.Check, contentDescription = "Finish", tint = Color.White, modifier = Modifier.size(15.dp))
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text("Finish", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.5.sp, maxLines = 1, softWrap = false)
                                            }
                                        }
                                    }
                                } else {
                                    // Idle Controls: Strict Mode Toggle & Set Custom Time (Identical 38.dp height)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Strict Focus Toggle Chip
                                        Surface(
                                            onClick = { onToggleStrict(!isStrictFocus) },
                                            shape = RoundedCornerShape(12.dp),
                                            color = if (isStrictFocus) Color(0xFFEF4444).copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant,
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                if (isStrictFocus) Color(0xFFEF4444) else MaterialTheme.colorScheme.outline
                                            ),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(38.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp),
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Lock,
                                                    contentDescription = "Strict Mode",
                                                    tint = if (isStrictFocus) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(5.dp))
                                                Text(
                                                    text = if (isStrictFocus) "Strict Focus ON" else "Strict Focus OFF",
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isStrictFocus) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    maxLines = 1,
                                                    softWrap = false
                                                )
                                            }
                                        }

                                        // Custom Duration Button
                                        Surface(
                                            onClick = { showCustomDurationDialog = true },
                                            shape = RoundedCornerShape(12.dp),
                                            color = BrandTeal.copy(alpha = 0.10f),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, BrandTeal.copy(alpha = 0.5f)),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(38.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp),
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Timer,
                                                    contentDescription = "Set Custom Time",
                                                    tint = BrandTeal,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(5.dp))
                                                Text(
                                                    text = "Set Custom Time",
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = BrandTeal,
                                                    maxLines = 1,
                                                    softWrap = false
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. Quick Duration Presets Chips Row (when not running stopwatch)
                if (timerStatus == TimerStatus.IDLE && timerMode != "STOPWATCH") {
                    item {
                        Column {
                            Text(
                                text = "Quick Durations",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val quickMins = listOf(15, 25, 45, 50, 60, 90)
                                items(quickMins) { mins ->
                                    val isCurrent = totalDurationSeconds == mins * 60
                                    Surface(
                                        onClick = { onSelectDurationMinutes(mins) },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isCurrent) primaryModeColor else MaterialTheme.colorScheme.surfaceVariant,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isCurrent) primaryModeColor else MaterialTheme.colorScheme.outline)
                                    ) {
                                        Text(
                                            text = "${mins}m",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. Saved Presets Carousel
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Saved Presets",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            )
                            TextButton(
                                onClick = { showCustomPresetSheet = true },
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = BrandTeal, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("+ New Preset", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandTeal)
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(presets, key = { it.id }) { preset ->
                                val isCurrentPreset = preset.mode == timerMode &&
                                    preset.workMinutes == (totalDurationSeconds / 60)

                                Card(
                                    modifier = Modifier
                                        .widthIn(min = 145.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { onApplyPreset(preset) }
                                        .border(
                                            width = if (isCurrentPreset) 1.5.dp else 1.dp,
                                            color = if (isCurrentPreset) BrandTeal else MaterialTheme.colorScheme.outline,
                                            shape = RoundedCornerShape(14.dp)
                                        ),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isCurrentPreset) BrandTeal.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f, fill = false)) {
                                            Text(
                                                text = preset.title,
                                                fontSize = 12.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            val modeDisplay = when (preset.mode) {
                                                "POMODORO" -> "Pomodoro"
                                                "DEEP_FOCUS" -> "Deep Focus"
                                                "SIMPLE_TIMER" -> "Timer"
                                                else -> "Stopwatch"
                                            }
                                            Text(
                                                text = "${preset.workMinutes}m • $modeDisplay",
                                                fontSize = 11.sp,
                                                color = TextSecondary,
                                                maxLines = 1
                                            )
                                        }
                                        if (!preset.isDefault) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            IconButton(
                                                onClick = { onDeletePreset(preset) },
                                                modifier = Modifier.size(22.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Delete Preset",
                                                    tint = TextMuted,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 5. Subject & Syllabus Chapter Integration Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Subject & Syllabus Chapter",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Subjects Pills
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                item {
                                    val isGeneral = selectedSubjectId == null
                                    Surface(
                                        onClick = { onSelectSubject(null) },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isGeneral) BrandTeal else MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = "General",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isGeneral) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                                items(subjects) { sub ->
                                    val isSel = selectedSubjectId == sub.id
                                    val subColor = parseColorHex(sub.colorHex)
                                    Surface(
                                        onClick = { onSelectSubject(sub.id) },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSel) subColor else MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = getSubjectIcon(sub.iconKey),
                                                contentDescription = null,
                                                tint = if (isSel) Color.White else subColor,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = sub.name,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }

                            // Chapters Pills if a subject is selected
                            if (selectedSubjectId != null) {
                                val subjectChapters = chapters.filter { it.subjectId == selectedSubjectId }
                                if (subjectChapters.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Select Chapter (Optional)",
                                        fontSize = 11.sp,
                                        color = TextSecondary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LazyRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        item {
                                            val isAll = selectedChapterId == null
                                            Surface(
                                                onClick = { onSelectChapter(null) },
                                                shape = RoundedCornerShape(8.dp),
                                                color = if (isAll) BrandTeal.copy(alpha = 0.8f) else MaterialTheme.colorScheme.surfaceVariant
                                            ) {
                                                Text(
                                                    text = "All Chapters",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isAll) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                        items(subjectChapters) { chap ->
                                            val isSel = selectedChapterId == chap.id
                                            Surface(
                                                onClick = { onSelectChapter(chap.id) },
                                                shape = RoundedCornerShape(8.dp),
                                                color = if (isSel) BrandTeal else MaterialTheme.colorScheme.surfaceVariant
                                            ) {
                                                Text(
                                                    text = chap.title,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Goal Task pill
                            Surface(
                                onClick = { showGoalDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Icon(
                                            imageVector = Icons.Default.Flag,
                                            contentDescription = "Session Goal",
                                            tint = BrandTeal,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = selectedGoalTask?.ifBlank { null } ?: "Set specific target (e.g. Solve 20 MCQ)",
                                            fontSize = 12.sp,
                                            color = if (!selectedGoalTask.isNullOrBlank()) MaterialTheme.colorScheme.onSurface else TextSecondary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Goal", tint = TextSecondary, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // ==========================================
            // ======== ANALYTICS & HISTORY VIEW ========
            // ==========================================
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Daily Target Card
                item {
                    val todayMins = sessions.filter {
                        val sDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it.startedAt))
                        sDate == SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) && it.status == "COMPLETED"
                    }.sumOf { it.elapsedSeconds } / 60

                    val targetPct = if (dailyGoalMinutes > 0) ((todayMins.toFloat() / dailyGoalMinutes) * 100).toInt().coerceAtMost(100) else 0

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp)),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "Today's Focus Goal", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                    Text(text = "$todayMins of $dailyGoalMinutes mins ($targetPct%)", fontSize = 12.sp, color = TextSecondary)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(BrandTeal.copy(alpha = 0.15f))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(text = "Level $userLevel Scholar", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = BrandTeal)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            LinearProgressIndicator(
                                progress = { targetPct / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = BrandTeal,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }

                // 2. Weekly Canvas Bar Chart
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp)),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Weekly Focus Minutes", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Spacer(modifier = Modifier.height(12.dp))

                            val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                            val dayMinutesMap = remember(sessions) {
                                val cal = Calendar.getInstance()
                                val map = mutableMapOf<Int, Int>()
                                (1..7).forEach { map[it] = 0 }
                                sessions.filter { it.status == "COMPLETED" }.forEach { sess ->
                                    cal.timeInMillis = sess.startedAt
                                    val dow = cal.get(Calendar.DAY_OF_WEEK)
                                    val mappedIdx = if (dow == Calendar.SUNDAY) 7 else (dow - 1)
                                    map[mappedIdx] = (map[mappedIdx] ?: 0) + (sess.elapsedSeconds / 60)
                                }
                                map
                            }

                            val maxMins = (dayMinutesMap.values.maxOrNull() ?: 120).coerceAtLeast(60)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                (1..7).forEach { dayIdx ->
                                    val mins = dayMinutesMap[dayIdx] ?: 0
                                    val barHeightRatio = (mins.toFloat() / maxMins).coerceIn(0.08f, 1f)

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(text = "${mins}m", fontSize = 9.sp, color = TextSecondary)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(0.5f)
                                                .fillMaxSize(barHeightRatio)
                                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                                .background(if (mins > 0) BrandTeal else MaterialTheme.colorScheme.surfaceVariant)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = days[dayIdx - 1], fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. Achievements Gallery
                item {
                    Text(
                        text = "Focus Achievements",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val unlockedIds = gamification?.unlockedAchievementIds?.split(",")?.filter { it.isNotBlank() } ?: emptyList()

                    val allBadges = listOf(
                        Triple("first_session", "First Step", "Complete 1 session"),
                        Triple("streak_3", "Streak 3", "3 day streak"),
                        Triple("streak_7", "Streak 7", "7 day streak"),
                        Triple("focus_100", "Centurion", "100 focus mins"),
                        Triple("focus_500", "Scholar", "500 focus mins"),
                        Triple("deep_diver", "Deep Diver", "60m single session")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        allBadges.take(3).forEach { (id, title, desc) ->
                            val isUnlocked = unlockedIds.contains(id)
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isUnlocked) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (isUnlocked) BrandTeal else TextMuted,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                    Text(text = desc, fontSize = 9.sp, color = TextSecondary, textAlign = TextAlign.Center, maxLines = 1)
                                }
                            }
                        }
                    }
                }

                // 4. Session History List
                item {
                    Text(
                        text = "Session History Log",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                if (sessions.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp)),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                                Text(text = "No focus sessions logged yet. Complete a session to see history!", fontSize = 12.sp, color = TextSecondary)
                            }
                        }
                    }
                } else {
                    items(sessions, key = { it.id }) { sess ->
                        val sub = subjects.find { it.id == sess.subjectId }
                        val mins = sess.elapsedSeconds / 60
                        val subColor = sub?.let { parseColorHex(it.colorHex) } ?: BrandTeal

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
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
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(subColor.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = getSubjectIcon(sub?.iconKey ?: "book"),
                                        contentDescription = null,
                                        tint = subColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = sub?.name ?: "General Focus",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()).format(Date(sess.startedAt)),
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                    if (sess.distractions > 0) {
                                        Text(
                                            text = "${sess.distractions} distraction(s) logged",
                                            fontSize = 10.sp,
                                            color = Color(0xFFEF4444)
                                        )
                                    }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "$mins mins",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.sp,
                                        color = BrandTeal
                                    )
                                    if (sess.reflectionRating > 0) {
                                        Row {
                                            (1..sess.reflectionRating).forEach { _ ->
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = null,
                                                    tint = Color(0xFFF59E0B),
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // ============= MODALS & SHEETS ============
    // ==========================================

    // Goal Task Dialog
    if (showGoalDialog) {
        var goalInput by remember { mutableStateOf(selectedGoalTask ?: "") }
        AlertDialog(
            onDismissRequest = { showGoalDialog = false },
            title = { Text("Set Session Target Goal", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = goalInput,
                    onValueChange = { goalInput = it },
                    label = { Text("What do you want to accomplish?") },
                    placeholder = { Text("e.g. Solve 20 Physics Numericals") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSetGoalTask(goalInput.ifBlank { null })
                        showGoalDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandTeal)
                ) {
                    Text("Save Goal")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGoalDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Custom Duration Setup Dialog
    if (showCustomDurationDialog) {
        var durationInputMins by remember { mutableIntStateOf(totalDurationSeconds / 60) }
        AlertDialog(
            onDismissRequest = { showCustomDurationDialog = false },
            title = { Text("Custom Focus Duration", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Choose duration in minutes (1 - 180):", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = durationInputMins.toString(),
                        onValueChange = { durationInputMins = it.toIntOrNull()?.coerceIn(1, 180) ?: 25 },
                        label = { Text("Minutes") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSelectDurationMinutes(durationInputMins)
                        showCustomDurationDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandTeal)
                ) {
                    Text("Apply")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomDurationDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Ambient Sound Bottom Sheet
    if (showAmbientSheet) {
        ModalBottomSheet(onDismissRequest = { showAmbientSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Ambient Soundscapes",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(14.dp))

                val sounds = listOf(
                    Triple("OFF", "Off", "Silence"),
                    Triple("RAIN", "Rainfall", "Calming rain noise"),
                    Triple("OCEAN", "Ocean Waves", "Soothing wave rhythm"),
                    Triple("LOFI", "Lo-Fi Beats", "Warm ambient tones"),
                    Triple("CAFE", "Cozy Cafe", "Soft focus texture"),
                    Triple("WHITE_NOISE", "White Noise", "Constant fan sound")
                )

                sounds.forEach { (typeKey, title, desc) ->
                    val isSel = ambientSoundType == typeKey
                    Surface(
                        onClick = { onSetAmbientSound(typeKey) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSel) BrandTeal.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) BrandTeal else MaterialTheme.colorScheme.outline),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(text = desc, fontSize = 11.sp, color = TextSecondary)
                            }
                            if (isSel) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = BrandTeal)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(text = "Volume Control", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Slider(
                    value = ambientVolume,
                    onValueChange = { onSetAmbientVolume(it) },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(thumbColor = BrandTeal, activeTrackColor = BrandTeal)
                )

                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }

    // Focus Settings Bottom Sheet (Work/Break times & Auto-Start toggles)
    if (showSettingsSheet) {
        var inputWork by remember { mutableIntStateOf(workMinutes) }
        var inputShortBreak by remember { mutableIntStateOf(shortBreakMinutes) }
        var inputLongBreak by remember { mutableIntStateOf(longBreakMinutes) }
        var inputCycles by remember { mutableIntStateOf(targetCycles) }
        var inputAutoBreaks by remember { mutableStateOf(autoStartBreaks) }
        var inputAutoNextFocus by remember { mutableStateOf(autoStartNextFocus) }

        ModalBottomSheet(onDismissRequest = { showSettingsSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Focus & Pomodoro Settings",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(14.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = inputWork.toString(),
                        onValueChange = { inputWork = it.toIntOrNull()?.coerceIn(1, 180) ?: 25 },
                        label = { Text("Work (m)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = inputShortBreak.toString(),
                        onValueChange = { inputShortBreak = it.toIntOrNull()?.coerceIn(1, 60) ?: 5 },
                        label = { Text("Short Break (m)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = inputLongBreak.toString(),
                        onValueChange = { inputLongBreak = it.toIntOrNull()?.coerceIn(1, 90) ?: 15 },
                        label = { Text("Long Break (m)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = inputCycles.toString(),
                        onValueChange = { inputCycles = it.toIntOrNull()?.coerceIn(1, 12) ?: 4 },
                        label = { Text("Cycles") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Auto-Start Breaks", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Automatically start break countdown", fontSize = 11.sp, color = TextSecondary)
                    }
                    Switch(
                        checked = inputAutoBreaks,
                        onCheckedChange = { inputAutoBreaks = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = BrandTeal, checkedTrackColor = BrandTeal.copy(alpha = 0.5f))
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Auto-Start Next Focus", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Automatically resume focus after break ends", fontSize = 11.sp, color = TextSecondary)
                    }
                    Switch(
                        checked = inputAutoNextFocus,
                        onCheckedChange = { inputAutoNextFocus = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = BrandTeal, checkedTrackColor = BrandTeal.copy(alpha = 0.5f))
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        onUpdateSettings(inputWork, inputShortBreak, inputLongBreak, inputCycles, inputAutoBreaks, inputAutoNextFocus)
                        showSettingsSheet = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandTeal),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Settings", color = Color.White)
                }
            }
        }
    }

    // Custom Preset Setup Sheet
    if (showCustomPresetSheet) {
        var presetTitle by remember { mutableStateOf("") }
        var workMinsInput by remember { mutableIntStateOf(25) }
        var shortBreakInput by remember { mutableIntStateOf(5) }
        var longBreakInput by remember { mutableIntStateOf(15) }
        var cyclesInput by remember { mutableIntStateOf(4) }
        var isStrictInput by remember { mutableStateOf(false) }
        var autoBreaksInput by remember { mutableStateOf(false) }
        var autoNextInput by remember { mutableStateOf(false) }

        ModalBottomSheet(onDismissRequest = { showCustomPresetSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(text = "Custom Preset Setup", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = presetTitle,
                    onValueChange = { presetTitle = it },
                    label = { Text("Preset Name") },
                    placeholder = { Text("e.g. Physics Sprint") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = workMinsInput.toString(),
                        onValueChange = { workMinsInput = it.toIntOrNull()?.coerceIn(1, 180) ?: 25 },
                        label = { Text("Work (m)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = shortBreakInput.toString(),
                        onValueChange = { shortBreakInput = it.toIntOrNull()?.coerceIn(1, 60) ?: 5 },
                        label = { Text("Short Break (m)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = longBreakInput.toString(),
                        onValueChange = { longBreakInput = it.toIntOrNull()?.coerceIn(1, 90) ?: 15 },
                        label = { Text("Long Break (m)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = cyclesInput.toString(),
                        onValueChange = { cyclesInput = it.toIntOrNull()?.coerceIn(1, 12) ?: 4 },
                        label = { Text("Cycles") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Strict Focus Enabled", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Switch(
                        checked = isStrictInput,
                        onCheckedChange = { isStrictInput = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = BrandTeal, checkedTrackColor = BrandTeal.copy(alpha = 0.5f))
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        onSaveCustomPreset(
                            presetTitle,
                            "POMODORO",
                            workMinsInput,
                            shortBreakInput,
                            longBreakInput,
                            cyclesInput,
                            isStrictInput,
                            autoBreaksInput,
                            autoNextInput
                        )
                        showCustomPresetSheet = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandTeal),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Preset", color = Color.White)
                }
            }
        }
    }

    // Strict Focus Mode Immersive Overlay
    if (isStrictFocus && timerStatus == TimerStatus.RUNNING) {
        Dialog(
            onDismissRequest = {
                // Intercept dismiss to confirm emergency exit
                showEmergencyExitDialog = true
            },
            properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnBackPress = true, dismissOnClickOutside = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0F172A))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Top Bar in Strict Mode
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("STRICT FOCUS ACTIVE", color = Color(0xFFEF4444), fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                        }

                        // Distraction Counter inside Strict Overlay
                        Surface(
                            onClick = onRecordDistraction,
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1E293B),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (distractionCount > 0) "+ Distraction ($distractionCount)" else "+ Distraction",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Center Focus Ring & Countdown
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val displayMins = timeRemainingSeconds / 60
                        val displaySecs = timeRemainingSeconds % 60
                        val displayString = String.format("%02d:%02d", displayMins, displaySecs)

                        Text(
                            text = activeSubject?.name ?: "Deep Study",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        if (!selectedGoalTask.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = selectedGoalTask,
                                color = BrandTeal,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        Box(
                            modifier = Modifier.size(220.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val strokeW = 12.dp.toPx()
                                val diam = size.minDimension - strokeW
                                val arcS = Size(diam, diam)
                                val tl = Offset(strokeW / 2, strokeW / 2)
                                val prog = if (totalDurationSeconds > 0) 1f - (timeRemainingSeconds.toFloat() / totalDurationSeconds) else 0f

                                drawArc(
                                    color = Color(0xFF334155),
                                    startAngle = -90f,
                                    sweepAngle = 360f,
                                    useCenter = false,
                                    topLeft = tl,
                                    size = arcS,
                                    style = Stroke(width = strokeW, cap = StrokeCap.Round)
                                )
                                drawArc(
                                    color = primaryModeColor,
                                    startAngle = -90f,
                                    sweepAngle = prog * 360f,
                                    useCenter = false,
                                    topLeft = tl,
                                    size = arcS,
                                    style = Stroke(width = strokeW, cap = StrokeCap.Round)
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = displayString,
                                    color = Color.White,
                                    fontSize = 44.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "Stay in the flow",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "\"Focus is saying no to 1,000 other good ideas.\"",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }

                    // Emergency Exit Button
                    TextButton(onClick = { showEmergencyExitDialog = true }) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Emergency Exit Session", color = Color(0xFFEF4444), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Emergency Exit Confirmation Dialog
    if (showEmergencyExitDialog) {
        AlertDialog(
            onDismissRequest = { showEmergencyExitDialog = false },
            title = { Text("Exit Strict Focus?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Exiting will end your strict focus session early. Your study time will still be recorded and XP awarded.",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showEmergencyExitDialog = false
                        onFinishTimer(false, 3, "Ended early via Emergency Exit", distractionCount)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Exit & Save", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEmergencyExitDialog = false }) { Text("Stay Focused") }
            }
        )
    }

    // Reflection & Completion Dialog Modal
    if (showReflectionDialog) {
        var rating by remember { mutableIntStateOf(5) }
        var notesText by remember { mutableStateOf("") }
        val elapsedMins = (totalDurationSeconds - timeRemainingSeconds) / 60
        val earnedXp = (elapsedMins * 10) + 50

        AlertDialog(
            onDismissRequest = { showReflectionDialog = false },
            title = {
                Column {
                    Text(text = "Session Complete! 🎉", fontWeight = FontWeight.ExtraBold)
                    Text(text = "+$earnedXp XP Earned • Level Progress", fontSize = 12.sp, color = BrandTeal, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(text = "How productive was this session?", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        (1..5).forEach { starIdx ->
                            IconButton(onClick = { rating = starIdx }) {
                                Icon(
                                    imageVector = if (starIdx <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "Star $starIdx",
                                    tint = if (starIdx <= rating) Color(0xFFF59E0B) else TextMuted
                                )
                            }
                        }
                    }

                    if (distractionCount > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Distractions recorded: $distractionCount",
                            fontSize = 11.sp,
                            color = Color(0xFFEF4444),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        label = { Text("Quick Reflection / Notes") },
                        placeholder = { Text("What did you accomplish?") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val secs = if (timerMode == "STOPWATCH") timeRemainingSeconds else (totalDurationSeconds - timeRemainingSeconds).coerceAtLeast(0)
                        onFinishTimer(true, rating, notesText, distractionCount)
                        showReflectionDialog = false
                        if (activeRevisionItemId != null && onCompleteRevisionReview != null) {
                            pendingRevisionElapsedSeconds = secs
                            showRecallFeedbackDialog = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandTeal)
                ) {
                    Text("Log Session & Claim XP", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    val secs = if (timerMode == "STOPWATCH") timeRemainingSeconds else (totalDurationSeconds - timeRemainingSeconds).coerceAtLeast(0)
                    onFinishTimer(true, 5, null, distractionCount)
                    showReflectionDialog = false
                    if (activeRevisionItemId != null && onCompleteRevisionReview != null) {
                        pendingRevisionElapsedSeconds = secs
                        showRecallFeedbackDialog = true
                    }
                }) { Text("Skip") }
            }
        )
    }

    // Contextual Spaced Repetition Recall Feedback Dialog after revision focus
    if (showRecallFeedbackDialog && activeRevisionItemId != null) {
        val chapterTitle = chapters.find { it.id == selectedChapterId }?.title
            ?: selectedGoalTask?.removePrefix("Revise: ")
            ?: "Chapter Revision"

        RecallFeedbackDialog(
            chapterTitle = chapterTitle,
            onConfirmRating = { recallRating, notes ->
                onCompleteRevisionReview?.invoke(activeRevisionItemId, recallRating, pendingRevisionElapsedSeconds, notes)
                showRecallFeedbackDialog = false
            },
            onDismiss = {
                showRecallFeedbackDialog = false
            }
        )
    }
}
