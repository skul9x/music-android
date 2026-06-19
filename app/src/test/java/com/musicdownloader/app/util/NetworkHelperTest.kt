package com.musicdownloader.app.util

import org.junit.Assert.*
import org.junit.Test

class NetworkHelperTest {

    @Test
    fun `stripPlaylistParam removes list parameter from URL`() {
        val url = "https://www.youtube.com/watch?v=abc123&list=PLxyz"
        val cleaned = NetworkHelper.stripPlaylistParam(url)
        assertEquals("https://www.youtube.com/watch?v=abc123", cleaned)
    }

    @Test
    fun `stripPlaylistParam leaves clean URL unchanged`() {
        val url = "https://www.youtube.com/watch?v=abc123"
        val cleaned = NetworkHelper.stripPlaylistParam(url)
        assertEquals(url, cleaned)
    }

    @Test
    fun `isPlaylistUrl detects playlist URLs`() {
        assertTrue(NetworkHelper.isPlaylistUrl("https://youtube.com/playlist?list=PLxyz"))
        assertTrue(NetworkHelper.isPlaylistUrl("https://youtube.com/watch?v=abc&list=PLxyz"))
    }

    @Test
    fun `isPlaylistUrl returns false for single video URLs`() {
        assertFalse(NetworkHelper.isPlaylistUrl("https://youtube.com/watch?v=abc123"))
        assertFalse(NetworkHelper.isPlaylistUrl("https://youtu.be/abc123"))
    }
}
