package com.musicdownloader.app.util

import org.junit.Assert.*
import org.junit.Test

class StorageHelperTest {

    @Test
    fun `default download path is not empty`() {
        val path = StorageHelper.getDefaultDownloadPath()
        assertTrue("Default path should not be blank", path.isNotBlank())
    }

    @Test
    fun `sanitizeFileName removes illegal characters`() {
        val cleaned = StorageHelper.sanitizeFileName("My Video / Title: \"Test\" <file>")
        assertFalse("Should not contain /", cleaned.contains("/"))
        assertFalse("Should not contain :", cleaned.contains(":"))
        assertFalse("Should not contain \"", cleaned.contains("\""))
        assertFalse("Should not contain <", cleaned.contains("<"))
        assertFalse("Should not contain >", cleaned.contains(">"))
    }

    @Test
    fun `sanitizeFileName preserves valid characters`() {
        val cleaned = StorageHelper.sanitizeFileName("My Video Title 2024")
        assertEquals("My Video Title 2024", cleaned)
    }
}
