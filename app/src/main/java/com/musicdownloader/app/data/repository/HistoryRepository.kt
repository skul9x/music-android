package com.musicdownloader.app.data.repository

import android.content.Context
import com.musicdownloader.app.data.models.DownloadHistoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HistoryRepository(private val persistence: HistoryPersistence) {
    private val _historyFlow = MutableStateFlow<List<DownloadHistoryItem>>(persistence.getHistory())
    val historyFlow: StateFlow<List<DownloadHistoryItem>> = _historyFlow.asStateFlow()

    companion object {
        @Volatile
        private var instance: HistoryRepository? = null
        fun getInstance(context: Context): HistoryRepository {
            return instance ?: synchronized(this) {
                instance ?: HistoryRepository(SQLiteHistoryPersistence(context)).also { instance = it }
            }
        }
    }

    fun getHistory(): List<DownloadHistoryItem> {
        return synchronized(this) {
            persistence.getHistory()
        }
    }

    fun addItem(item: DownloadHistoryItem) {
        synchronized(this) {
            persistence.addItem(item)
            _historyFlow.value = persistence.getHistory()
        }
    }

    fun clearHistory() {
        synchronized(this) {
            persistence.clearHistory()
            _historyFlow.value = emptyList()
        }
    }
}
