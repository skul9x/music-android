package com.musicdownloader.app.service

import org.junit.Assert.*
import org.junit.Test

class NotificationHelperTest {

    @Test
    fun `CHANNEL_ID is defined and non-empty`() {
        assertTrue(NotificationHelper.CHANNEL_ID.isNotBlank())
    }

    @Test
    fun `NOTIFICATION_ID is positive`() {
        assertTrue(NotificationHelper.NOTIFICATION_ID > 0)
    }
}
