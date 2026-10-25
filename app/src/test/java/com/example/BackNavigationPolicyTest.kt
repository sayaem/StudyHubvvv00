package com.example

import com.example.ui.components.NavDestination
import com.example.ui.navigation.NavigationDestination
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BackNavigationPolicyTest {

    private val backStack = mutableListOf<NavigationDestination>(NavigationDestination.MainTab(NavDestination.HOME))

    private fun navigateTo(dest: NavigationDestination) {
        val currentTop = backStack.lastOrNull()
        if (currentTop == dest) return

        if (dest is NavigationDestination.MainTab) {
            if (currentTop is NavigationDestination.MainTab) {
                if (dest.destination == NavDestination.HOME) {
                    while (backStack.size > 1 && backStack.last() !is NavigationDestination.MainTab) {
                        backStack.removeAt(backStack.size - 1)
                    }
                    if (backStack.lastOrNull() != NavigationDestination.MainTab(NavDestination.HOME)) {
                        backStack.removeAt(backStack.size - 1)
                        backStack.add(NavigationDestination.MainTab(NavDestination.HOME))
                    }
                } else {
                    if (backStack.contains(dest)) {
                        backStack.remove(dest)
                    }
                    backStack.add(dest)
                }
            } else {
                if (backStack.contains(dest)) {
                    backStack.remove(dest)
                }
                backStack.add(dest)
            }
        } else {
            if (backStack.lastOrNull() == dest) return
            backStack.add(dest)
        }
    }

    private fun goBack(): Boolean {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.size - 1)
            return true
        }
        return false // At root destination
    }

    @Test
    fun testBasicScreenNavigation_ProfileAndBack() {
        assertEquals(1, backStack.size)
        assertEquals(NavigationDestination.MainTab(NavDestination.HOME), backStack.last())

        navigateTo(NavigationDestination.Profile)
        assertEquals(2, backStack.size)
        assertEquals(NavigationDestination.Profile, backStack.last())

        val handled = goBack()
        assertTrue(handled)
        assertEquals(1, backStack.size)
        assertEquals(NavigationDestination.MainTab(NavDestination.HOME), backStack.last())
    }

    @Test
    fun testDeepNavigation_HomeToSettingsToNotificationsAndBack() {
        navigateTo(NavigationDestination.Profile)
        navigateTo(NavigationDestination.Settings)
        navigateTo(NavigationDestination.Notifications)

        assertEquals(4, backStack.size)
        assertEquals(NavigationDestination.Notifications, backStack.last())

        goBack()
        assertEquals(NavigationDestination.Settings, backStack.last())

        goBack()
        assertEquals(NavigationDestination.Profile, backStack.last())

        goBack()
        assertEquals(NavigationDestination.MainTab(NavDestination.HOME), backStack.last())
    }

    @Test
    fun testBottomNavTabHistory_SwitchTabsAndBack() {
        navigateTo(NavigationDestination.MainTab(NavDestination.SYLLABUS))
        navigateTo(NavigationDestination.MainTab(NavDestination.FOCUS))
        navigateTo(NavigationDestination.MainTab(NavDestination.TOOLS))

        assertEquals(NavigationDestination.MainTab(NavDestination.TOOLS), backStack.last())

        goBack()
        assertEquals(NavigationDestination.MainTab(NavDestination.FOCUS), backStack.last())

        goBack()
        assertEquals(NavigationDestination.MainTab(NavDestination.SYLLABUS), backStack.last())

        goBack()
        assertEquals(NavigationDestination.MainTab(NavDestination.HOME), backStack.last())
    }

    @Test
    fun testRootDestinationExitPolicy() {
        assertEquals(1, backStack.size)
        val canGoBack = goBack()
        assertFalse("At root destination, back handler must yield to system app exit", canGoBack)
        assertEquals(1, backStack.size)
    }

    @Test
    fun testExternalEntryPoints_NotificationDeepLink() {
        // App opens from notification directly into Admission Calendar
        navigateTo(NavigationDestination.AdmissionCalendar)
        assertEquals(NavigationDestination.AdmissionCalendar, backStack.last())

        goBack()
        assertEquals(NavigationDestination.MainTab(NavDestination.HOME), backStack.last())
    }
}
