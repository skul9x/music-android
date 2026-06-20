package com.musicdownloader.app.data.repository

import com.musicdownloader.app.data.models.DownloadHistoryItem
import java.util.Collections

class InMemoryHistoryPersistence : HistoryPersistence {
    private val historyList = Collections.synchronizedList(mutableListOf<DownloadHistoryItem>())

    override fun getHistory(): List<DownloadHistoryItem> {
        return historyList.toList().sortedByDescending { it.timestamp }
    }

    override fun addItem(item: DownloadHistoryItem) {
        historyList.add(item)
    }

    override fun clearHistory() {
        historyList.clear()
    }
}
