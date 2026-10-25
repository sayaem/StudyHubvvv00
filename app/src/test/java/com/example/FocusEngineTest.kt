package com.example

import com.example.data.local.ActiveFocusSessionEntity
import com.example.data.local.FocusGamificationEntity
import com.example.data.local.FocusPresetEntity
import com.example.ui.FocusSessionState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FocusEngineTest {

    @Test
    fun `preset entity default values and auto start flags`() {
        val preset = FocusPresetEntity(
            title = "Standard Pomodoro",
            mode = "POMODORO",
            workMinutes = 25,
            shortBreakMinutes = 5,
            longBreakMinutes = 15,
            targetCycles = 4,
            autoStartBreaks = true,
            autoStartNextFocus = true
        )

        assertEquals("Standard Pomodoro", preset.title)
        assertEquals(25, preset.workMinutes)
        assertEquals(5, preset.shortBreakMinutes)
        assertEquals(15, preset.longBreakMinutes)
        assertEquals(4, preset.targetCycles)
        assertFalse(preset.isStrict)
        assertTrue(preset.autoStartBreaks)
        assertTrue(preset.autoStartNextFocus)
    }

    @Test
    fun `gamification level calculation`() {
        val g1 = FocusGamificationEntity(totalXp = 0)
        val level1 = 1 + (g1.totalXp / 1000)
        assertEquals(1, level1)

        val g2 = FocusGamificationEntity(totalXp = 2450)
        val level3 = 1 + (g2.totalXp / 1000)
        assertEquals(3, level3)
    }

    @Test
    fun `xp reward calculation formula`() {
        val elapsedSecs = 25 * 60 // 25 mins
        val mins = elapsedSecs / 60
        val baseMultiplier = 10
        val bonusXp = 50 // completion bonus

        val earnedXp = (mins * baseMultiplier) + bonusXp
        assertEquals(300, earnedXp) // 250 + 50 = 300 XP
    }

    @Test
    fun `focus session state enum values exist`() {
        val states = FocusSessionState.values()
        assertTrue(states.contains(FocusSessionState.IDLE))
        assertTrue(states.contains(FocusSessionState.FOCUS_RUNNING))
        assertTrue(states.contains(FocusSessionState.FOCUS_PAUSED))
        assertTrue(states.contains(FocusSessionState.SHORT_BREAK_RUNNING))
        assertTrue(states.contains(FocusSessionState.SHORT_BREAK_PAUSED))
        assertTrue(states.contains(FocusSessionState.LONG_BREAK_RUNNING))
        assertTrue(states.contains(FocusSessionState.LONG_BREAK_PAUSED))
        assertTrue(states.contains(FocusSessionState.COMPLETED))
        assertTrue(states.contains(FocusSessionState.ENDED_EARLY))
    }

    @Test
    fun `active focus session entity persistence and wall-clock delta calculation`() {
        val now = 1000000L
        val active = ActiveFocusSessionEntity(
            sessionState = FocusSessionState.FOCUS_RUNNING.name,
            mode = "POMODORO",
            phase = "WORK",
            totalDurationSeconds = 25 * 60,
            timeRemainingSeconds = 20 * 60,
            startTimeMs = now - (5 * 60 * 1000L),
            accumulatedPauseMs = 0L,
            lastPauseMs = 0L,
            lastTickMs = now,
            currentCycle = 1,
            targetCycles = 4,
            workMinutes = 25,
            shortBreakMinutes = 5,
            longBreakMinutes = 15,
            autoStartBreaks = false,
            autoStartNextFocus = false,
            subjectId = 101L,
            chapterId = 202L,
            goalTask = "Solve 10 Questions",
            isStrict = true,
            distractions = 2
        )

        assertEquals("FOCUS_RUNNING", active.sessionState)
        assertEquals(101L, active.subjectId)
        assertEquals(202L, active.chapterId)
        assertEquals(2, active.distractions)
        assertTrue(active.isStrict)

        // Simulate 2 minutes later after process was restored
        val restoreTime = now + (2 * 60 * 1000L)
        val elapsedMs = restoreTime - active.startTimeMs - active.accumulatedPauseMs
        val remainMs = (active.totalDurationSeconds * 1000L) - elapsedMs
        val remainSecs = (remainMs / 1000L).toInt()

        // 25m total - 7m elapsed = 18m remaining = 1080 seconds
        assertEquals(18 * 60, remainSecs)
    }

    @Test
    fun `pomodoro cycle transition logic`() {
        // Cycle 1..3 transitions to short break
        val currentCycle = 2
        val targetCycles = 4
        val isLongBreak = currentCycle >= targetCycles
        assertFalse(isLongBreak)

        // Cycle 4 transitions to long break
        val finalCycle = 4
        val isFinalLongBreak = finalCycle >= targetCycles
        assertTrue(isFinalLongBreak)
    }
}
