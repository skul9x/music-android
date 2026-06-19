package com.musicdownloader.app.data.models

import org.junit.Assert.*
import org.junit.Test

class DownloadModelsTest {

    @Test
    fun `DownloadFormat enum has all expected values`() {
        val formats = DownloadFormat.values()
        assertEquals(4, formats.size)
        assertTrue(formats.contains(DownloadFormat.M4A_AUDIO))
        assertTrue(formats.contains(DownloadFormat.VIDEO_1080P))
        assertTrue(formats.contains(DownloadFormat.VIDEO_720P))
        assertTrue(formats.contains(DownloadFormat.VIDEO_BEST))
    }

    @Test
    fun `VideoInfo data class holds correct data`() {
        val info = VideoInfo(
            title = "My Song",
            thumbnailUrl = "https://img.youtube.com/vi/abc/maxresdefault.jpg",
            duration = 300,
            uploader = "Artist",
            url = "https://youtube.com/watch?v=abc"
        )
        assertEquals("My Song", info.title)
        assertEquals(300, info.duration)
        assertEquals("Artist", info.uploader)
    }

    @Test
    fun `DownloadProgress data class defaults`() {
        val progress = DownloadProgress(
            percent = 45.5f,
            etaSeconds = 30,
            speedStr = "1.2MiB/s",
            line = "[download] 45.5%"
        )
        assertEquals(45.5f, progress.percent, 0.01f)
        assertEquals(30, progress.etaSeconds)
    }

    @Test
    fun `DownloadUiState sealed class instances`() {
        val idle: DownloadUiState = DownloadUiState.Idle
        val error: DownloadUiState = DownloadUiState.Error("Network error")
        val success: DownloadUiState = DownloadUiState.Success("/path/to/file.m4a")

        assertTrue(idle is DownloadUiState.Idle)
        assertEquals("Network error", (error as DownloadUiState.Error).message)
        assertEquals("/path/to/file.m4a", (success as DownloadUiState.Success).filePath)
    }
}
