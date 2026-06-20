package com.musicdownloader.app.data.repository

import android.content.ContentValues
import android.content.Context
import com.musicdownloader.app.data.models.DownloadHistoryItem

class SQLiteHistoryPersistence(context: Context) : HistoryPersistence {
    private val dbHelper = HistoryDatabaseHelper(context)

    override fun getHistory(): List<DownloadHistoryItem> {
        val list = mutableListOf<DownloadHistoryItem>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            HistoryDatabaseHelper.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            "${HistoryDatabaseHelper.COLUMN_TIMESTAMP} DESC"
        )
        cursor.use { c ->
            val titleIdx = c.getColumnIndexOrThrow(HistoryDatabaseHelper.COLUMN_TITLE)
            val filePathIdx = c.getColumnIndexOrThrow(HistoryDatabaseHelper.COLUMN_FILE_PATH)
            val formatIdx = c.getColumnIndexOrThrow(HistoryDatabaseHelper.COLUMN_FORMAT)
            val timestampIdx = c.getColumnIndexOrThrow(HistoryDatabaseHelper.COLUMN_TIMESTAMP)
            val thumbnailUrlIdx = c.getColumnIndexOrThrow(HistoryDatabaseHelper.COLUMN_THUMBNAIL_URL)
            val isPlaylistIdx = c.getColumnIndexOrThrow(HistoryDatabaseHelper.COLUMN_IS_PLAYLIST)

            while (c.moveToNext()) {
                val title = c.getString(titleIdx)
                val filePath = c.getString(filePathIdx)
                val format = c.getString(formatIdx)
                val timestamp = c.getLong(timestampIdx)
                val thumbnailUrl = c.getString(thumbnailUrlIdx)
                val isPlaylist = c.getInt(isPlaylistIdx) == 1

                list.add(
                    DownloadHistoryItem(
                        title = title,
                        filePath = filePath,
                        format = format,
                        timestamp = timestamp,
                        thumbnailUrl = thumbnailUrl,
                        isPlaylist = isPlaylist
                    )
                )
            }
        }
        return list
    }

    override fun addItem(item: DownloadHistoryItem) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(HistoryDatabaseHelper.COLUMN_TITLE, item.title)
            put(HistoryDatabaseHelper.COLUMN_FILE_PATH, item.filePath)
            put(HistoryDatabaseHelper.COLUMN_FORMAT, item.format)
            put(HistoryDatabaseHelper.COLUMN_TIMESTAMP, item.timestamp)
            put(HistoryDatabaseHelper.COLUMN_THUMBNAIL_URL, item.thumbnailUrl)
            put(HistoryDatabaseHelper.COLUMN_IS_PLAYLIST, if (item.isPlaylist) 1 else 0)
        }
        db.insert(HistoryDatabaseHelper.TABLE_NAME, null, values)
    }

    override fun clearHistory() {
        val db = dbHelper.writableDatabase
        db.delete(HistoryDatabaseHelper.TABLE_NAME, null, null)
    }
}
