package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ui.components.getAvatarInfo
import com.example.ui.screens.formatMinutes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context matches StudyHub`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("StudyHub", appName)
    }

    @Test
    fun `formatMinutes converts accurately`() {
        assertEquals("0m", formatMinutes(0))
        assertEquals("45m", formatMinutes(45))
        assertEquals("1h 0m", formatMinutes(60))
        assertEquals("1h 45m", formatMinutes(105))
        assertEquals("4h 0m", formatMinutes(240))
    }

    @Test
    fun `avatar library returns valid avatars`() {
        val avatar1 = getAvatarInfo("avatar_1")
        assertNotNull(avatar1)
        assertEquals("Rafi Rahman", avatar1.name)

        val fallback = getAvatarInfo("non_existent_id")
        assertNotNull(fallback)
        assertEquals("Rafi Rahman", fallback.name)
    }
}
