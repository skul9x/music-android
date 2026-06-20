package com.musicdownloader.app.service

import com.musicdownloader.app.data.models.DownloadProgress
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

    @Test
    fun `getProgressContentText for single download without speed`() {
        val progress = DownloadProgress(
            percent = 45f,
            etaSeconds = 0,
            speedStr = "",
            line = "",
            currentItem = 0,
            totalItems = 0
        )
        val text = NotificationHelper.getProgressContentText(progress)
        assertEquals("Downloading: 45%", text)
    }

    @Test
    fun `getProgressContentText for single download with speed`() {
        val progress = DownloadProgress(
            percent = 45f,
            etaSeconds = 0,
            speedStr = "1.5 MB/s",
            line = "",
            currentItem = 0,
            totalItems = 0
        )
        val text = NotificationHelper.getProgressContentText(progress)
        assertEquals("Downloading: 45% (1.5 MB/s)", text)
    }

    @Test
    fun `getProgressContentText for playlist download`() {
        val progress = DownloadProgress(
            percent = 30f,
            etaSeconds = 90,
            speedStr = "1.5 MB/s",
            line = "",
            currentItem = 3,
            totalItems = 10
        )
        val text = NotificationHelper.getProgressContentText(progress)
        assertEquals("Downloading item 3 of 10 (30%)", text)
    }

    @Test
    fun `getProgressSubText for single download is null`() {
        val progress = DownloadProgress(
            percent = 45f,
            etaSeconds = 90,
            speedStr = "1.5 MB/s",
            line = "",
            currentItem = 0,
            totalItems = 0
        )
        val text = NotificationHelper.getProgressSubText(progress)
        assertNull(text)
    }

    @Test
    fun `getProgressSubText for playlist download with empty speed is null`() {
        val progress = DownloadProgress(
            percent = 30f,
            etaSeconds = 90,
            speedStr = "",
            line = "",
            currentItem = 3,
            totalItems = 10
        )
        val text = NotificationHelper.getProgressSubText(progress)
        assertNull(text)
    }

    @Test
    fun `getProgressSubText for playlist download with speed and no ETA`() {
        val progress = DownloadProgress(
            percent = 30f,
            etaSeconds = 0,
            speedStr = "1.5 MB/s",
            line = "",
            currentItem = 3,
            totalItems = 10
        )
        val text = NotificationHelper.getProgressSubText(progress)
        assertEquals("1.5 MB/s", text)
    }

    @Test
    fun `getProgressSubText for playlist download with speed and ETA`() {
        val progress = DownloadProgress(
            percent = 30f,
            etaSeconds = 90, // 1 min 30 sec -> 1:30
            speedStr = "1.5 MB/s",
            line = "",
            currentItem = 3,
            totalItems = 10
        )
        val text = NotificationHelper.getProgressSubText(progress)
        assertEquals("1.5 MB/s • ETA: 1:30", text)
    }
}
