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

    @Test
    fun `progress log parsing extracts currentItem and totalItems correctly`() {
        val itemRegex = Regex("\\[download\\] Downloading item (\\d+) of (\\d+)")
        
        val line1 = "[download] Downloading item 3 of 10"
        val match1 = itemRegex.find(line1)
        assertNotNull(match1)
        assertEquals("3", match1!!.groupValues[1])
        assertEquals("10", match1.groupValues[2])

        val line2 = "[download] Downloading item 10 of 10"
        val match2 = itemRegex.find(line2)
        assertNotNull(match2)
        assertEquals("10", match2!!.groupValues[1])
        assertEquals("10", match2.groupValues[2])
    }
}

