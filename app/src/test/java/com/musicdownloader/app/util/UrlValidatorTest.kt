package com.musicdownloader.app.util

import org.junit.Assert.*
import org.junit.Test

class UrlValidatorTest {

    @Test
    fun `valid youtube URL returns true`() {
        assertTrue(UrlValidator.isSupported("https://www.youtube.com/watch?v=dQw4w9WgXcQ"))
        assertTrue(UrlValidator.isSupported("https://youtu.be/dQw4w9WgXcQ"))
        assertTrue(UrlValidator.isSupported("https://m.youtube.com/watch?v=abc123"))
    }

    @Test
    fun `valid tiktok URL returns true`() {
        assertTrue(UrlValidator.isSupported("https://www.tiktok.com/@user/video/1234567890"))
        assertTrue(UrlValidator.isSupported("https://vm.tiktok.com/ABC123/"))
    }

    @Test
    fun `invalid URLs return false`() {
        assertFalse(UrlValidator.isSupported(""))
        assertFalse(UrlValidator.isSupported("not a url"))
        assertFalse(UrlValidator.isSupported("http://example.com"))
        assertFalse(UrlValidator.isSupported("ftp://youtube.com/watch?v=test"))
    }

    @Test
    fun `null-like empty string returns false`() {
        assertFalse(UrlValidator.isSupported("   "))
    }
}
