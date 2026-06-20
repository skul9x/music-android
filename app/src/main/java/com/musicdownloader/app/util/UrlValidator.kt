package com.musicdownloader.app.util

object UrlValidator {
    private val SUPPORTED_PATTERNS = listOf(
        Regex("https?://(www\\.)?youtube\\.com/watch\\?v=.+"),
        Regex("https?://youtu\\.be/.+"),
        Regex("https?://m\\.youtube\\.com/watch\\?v=.+"),
        Regex("https?://(www\\.|vm\\.|vt\\.)?tiktok\\.com/.+"),
    )

    fun isSupported(url: String): Boolean {
        val trimmed = url.trim()
        if (trimmed.isEmpty()) return false
        return SUPPORTED_PATTERNS.any { it.matches(trimmed) }
    }
}
