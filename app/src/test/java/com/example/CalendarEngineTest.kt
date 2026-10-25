package com.example

import com.example.data.local.StudyTaskEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class CalendarEngineTest {

    private fun parseTimeToMinutes(timeStr: String?): Int {
        if (timeStr.isNullOrBlank()) return 8 * 60
        val parts = timeStr.trim().split(":")
        val h = parts[0].toIntOrNull() ?: 8
        val m = parts.getOrNull(1)?.take(2)?.toIntOrNull() ?: 0
        return h * 60 + m
    }

    private fun checkOverlap(start1: Int, dur1: Int, start2: Int, dur2: Int): Boolean {
        val end1 = start1 + dur1
        val end2 = start2 + dur2
        return maxOf(start1, start2) < minOf(end1, end2)
    }

    private fun formatMinutesToAmPm(totalMinutes: Int): String {
        val norm = (totalMinutes % (24 * 60) + (24 * 60)) % (24 * 60)
        val hour24 = norm / 60
        val minute = norm % 60
        val isPm = hour24 >= 12
        val hour12 = if (hour24 == 0) 12 else if (hour24 > 12) hour24 - 12 else hour24
        val amPm = if (isPm) "PM" else "AM"
        return String.format("%02d:%02d %s", hour12, minute, amPm)
    }

    private fun formatPlanTimeRange(timeStr: String?, durationMinutes: Int): String {
        val startMins = parseTimeToMinutes(timeStr)
        val endMins = (startMins + durationMinutes) % (24 * 60)

        val startStr = formatMinutesToAmPm(startMins)
        val endStr = formatMinutesToAmPm(endMins)

        val durHours = durationMinutes / 60
        val durMins = durationMinutes % 60
        val durStr = when {
            durHours > 0 && durMins > 0 -> "${durHours}h ${durMins}m"
            durHours > 0 -> "${durHours}h 00m"
            else -> "${durMins}m"
        }

        return "$startStr - $endStr • $durStr"
    }

    @Test
    fun `time overlap detection correctly detects conflicting plans`() {
        // Plan 1: 08:00 AM to 09:30 AM (90 mins) -> 480 to 570
        val start1 = parseTimeToMinutes("08:00")
        val dur1 = 90

        // Plan 2: 09:00 AM to 10:00 AM (60 mins) -> 540 to 600 (Conflicts!)
        val start2 = parseTimeToMinutes("09:00")
        val dur2 = 60

        assertTrue(checkOverlap(start1, dur1, start2, dur2))

        // Plan 3: 09:30 AM to 11:00 AM (90 mins) -> 570 to 660 (Starts exactly when Plan 1 ends - No Conflict)
        val start3 = parseTimeToMinutes("09:30")
        val dur3 = 90

        assertFalse(checkOverlap(start1, dur1, start3, dur3))
    }

    @Test
    fun `format plan time range formats strictly on single continuous string`() {
        val formatted = formatPlanTimeRange("08:00", 60)
        assertEquals("08:00 AM - 09:00 AM • 1h 00m", formatted)

        val formatted2 = formatPlanTimeRange("14:30", 45)
        assertEquals("02:30 PM - 03:15 PM • 45m", formatted2)
    }

    @Test
    fun `auto suggest next available free time slot`() {
        val existingTasks = listOf(
            StudyTaskEntity(title = "Math", date = "2026-10-25", time = "08:00", durationMinutes = 60),
            StudyTaskEntity(title = "Physics", date = "2026-10-25", time = "09:00", durationMinutes = 45)
        )

        val latest = existingTasks.maxByOrNull { parseTimeToMinutes(it.time) + it.durationMinutes }
        assertNotNull(latest)
        val endMins = parseTimeToMinutes(latest!!.time) + latest.durationMinutes
        val nextStartH = endMins / 60
        val nextStartM = endMins % 60
        val nextFreeTimeStr = String.format("%02d:%02d", nextStartH, nextStartM)

        assertEquals("09:45", nextFreeTimeStr)
    }

    @Test
    fun `time parsing handles 24-hour strings accurately`() {
        assertEquals(8 * 60, parseTimeToMinutes("08:00"))
        assertEquals(13 * 60 + 30, parseTimeToMinutes("13:30"))
        assertEquals(0, parseTimeToMinutes("00:00"))
        assertEquals(23 * 60 + 59, parseTimeToMinutes("23:59"))
    }

    @Test
    fun `weekly seven-day sequence generates consecutive dates`() {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, 2025)
        cal.set(Calendar.MONTH, Calendar.APRIL)
        cal.set(Calendar.DAY_OF_MONTH, 14) // Monday, 14 April 2025

        val days = (0..6).map { offset ->
            val dCal = cal.clone() as Calendar
            dCal.add(Calendar.DAY_OF_MONTH, offset)
            dCal.get(Calendar.DAY_OF_MONTH)
        }

        assertEquals(listOf(14, 15, 16, 17, 18, 19, 20), days)
    }

    @Test
    fun `study task entity defaults to STUDY plan type`() {
        val task = StudyTaskEntity(
            title = "Trigonometry",
            date = "2025-04-17",
            time = "08:00",
            durationMinutes = 90
        )

        assertEquals("STUDY", task.planType)
        assertEquals("PENDING", task.status)
        assertEquals(90, task.durationMinutes)
    }
}
