package com.musicdownloader.app.util

import java.util.Locale

fun formatDuration(seconds: Long): String {
    val secs = if (seconds < 0) 0L else seconds
    val h = secs / 3600
    val m = (secs % 3600) / 60
    val s = secs % 60
    return if (h > 0) {
        String.format(Locale.US, "%d:%02d:%02d", h, m, s)
    } else {
        String.format(Locale.US, "%d:%02d", m, s)
    }
}
