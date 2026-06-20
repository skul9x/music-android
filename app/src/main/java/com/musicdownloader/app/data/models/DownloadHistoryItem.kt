package com.musicdownloader.app.data.models

data class DownloadHistoryItem(
    val title: String,
    val filePath: String,
    val format: String,
    val timestamp: Long,
    val thumbnailUrl: String,
    val isPlaylist: Boolean = false
)
