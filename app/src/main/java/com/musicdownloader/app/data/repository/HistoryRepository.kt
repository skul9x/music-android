package com.musicdownloader.app.data.repository

import com.musicdownloader.app.data.models.DownloadHistoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HistoryRepository {
    private val historyList = mutableListOf<DownloadHistoryItem>()
    private val _historyFlow = MutableStateFlow<List<DownloadHistoryItem>>(emptyList())
    val historyFlow: StateFlow<List<DownloadHistoryItem>> = _historyFlow.asStateFlow()

    companion object {
        @Volatile
        private var instance: HistoryRepository? = null
        fun getInstance(): HistoryRepository {
            return instance ?: synchronized(this) {
                instance ?: HistoryRepository().also { instance = it }
            }
        }
    }

    fun getHistory(): List<DownloadHistoryItem> {
        return synchronized(this) {
            historyList.toList().sortedByDescending { it.timestamp }
        }
    }

    fun addItem(item: DownloadHistoryItem) {
        synchronized(this) {
            historyList.add(item)
            _historyFlow.value = historyList.toList().sortedByDescending { it.timestamp }
        }
    }

    fun clearHistory() {
        synchronized(this) {
            historyList.clear()
            _historyFlow.value = emptyList()
        }
    }
}
