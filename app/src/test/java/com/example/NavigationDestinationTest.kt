package com.example

import com.example.ui.components.NavDestination
import org.junit.Assert.assertEquals
import org.junit.Test

class NavigationDestinationTest {
    @Test
    fun navDestinations_hasExactlyFiveTabsInRequiredOrder() {
        val destinations = NavDestination.entries
        assertEquals(5, destinations.size)
        assertEquals("Home", destinations[0].label)
        assertEquals("Syllabus", destinations[1].label)
        assertEquals("Focus", destinations[2].label)
        assertEquals("Calendar", destinations[3].label)
        assertEquals("Tools", destinations[4].label)
    }

    @Test
    fun quickActions_hasExactlyFiveRequiredActionLabels() {
        val expectedActions = listOf("Add Subject", "Today's Plan", "Start Focus", "Stats", "Target")
        assertEquals(5, expectedActions.size)
        assertEquals("Add Subject", expectedActions[0])
        assertEquals("Today's Plan", expectedActions[1])
        assertEquals("Start Focus", expectedActions[2])
        assertEquals("Stats", expectedActions[3])
        assertEquals("Target", expectedActions[4])
    }
}
