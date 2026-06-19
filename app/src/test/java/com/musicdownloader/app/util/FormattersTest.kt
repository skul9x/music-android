package com.musicdownloader.app.util

import org.junit.Assert.assertEquals
import org.junit.Test

class FormattersTest {

    @Test
    fun `formatDuration with seconds less than a minute`() {
        assertEquals("0:45", formatDuration(45))
    }

    @Test
    fun `formatDuration with minutes and seconds`() {
        assertEquals("3:25", formatDuration(205))
    }

    @Test
    fun `formatDuration with hours`() {
        assertEquals("1:05:30", formatDuration(3930))
    }

    @Test
    fun `formatDuration with zero`() {
        assertEquals("0:00", formatDuration(0))
    }

    @Test
    fun `formatDuration with exactly one minute`() {
        assertEquals("1:00", formatDuration(60))
    }
}
