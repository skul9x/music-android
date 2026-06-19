package com.musicdownloader.app.data.repository

import com.musicdownloader.app.data.models.DownloadHistoryItem
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class HistoryRepositoryTest {

    private lateinit var repo: HistoryRepository

    @Before
    fun setup() {
        repo = HistoryRepository()
    }

    @Test
    fun `initially history is empty`() {
        assertTrue(repo.getHistory().isEmpty())
    }

    @Test
    fun `addItem increases history size`() {
        repo.addItem(
            DownloadHistoryItem(
                title = "Test Song",
                filePath = "/downloads/test.m4a",
                format = "M4A",
                timestamp = System.currentTimeMillis(),
                thumbnailUrl = "https://example.com/thumb.jpg"
            )
        )
        assertEquals(1, repo.getHistory().size)
    }

    @Test
    fun `history items are in reverse chronological order`() {
        repo.addItem(DownloadHistoryItem("First", "/a", "M4A", 1000, ""))
        repo.addItem(DownloadHistoryItem("Second", "/b", "M4A", 2000, ""))

        val history = repo.getHistory()
        assertEquals("Second", history[0].title)
        assertEquals("First", history[1].title)
    }

    @Test
    fun `clearHistory empties the list`() {
        repo.addItem(DownloadHistoryItem("Song", "/a", "M4A", 1000, ""))
        repo.clearHistory()
        assertTrue(repo.getHistory().isEmpty())
    }
}
