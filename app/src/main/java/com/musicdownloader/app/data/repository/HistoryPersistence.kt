package com.musicdownloader.app.data.repository

import com.musicdownloader.app.data.models.DownloadHistoryItem

interface HistoryPersistence {
    fun getHistory(): List<DownloadHistoryItem>
    fun addItem(item: DownloadHistoryItem)
    fun clearHistory()
}
