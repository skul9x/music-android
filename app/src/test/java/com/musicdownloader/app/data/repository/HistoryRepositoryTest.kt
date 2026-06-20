package com.musicdownloader.app.data.repository

import com.musicdownloader.app.data.models.DownloadHistoryItem
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class HistoryRepositoryTest {

    private lateinit var persistence: HistoryPersistence
    private lateinit var repo: HistoryRepository

    @Before
    fun setup() {
        persistence = InMemoryHistoryPersistence()
        repo = HistoryRepository(persistence)
    }

    @Test
    fun `initially history is empty`() {
        assertTrue(repo.getHistory().isEmpty())
    }

    @Test
    fun `addItem increases history size and persists`() {
        val item = DownloadHistoryItem(
            title = "Test Song",
            filePath = "/downloads/test.m4a",
            format = "M4A",
            timestamp = System.currentTimeMillis(),
            thumbnailUrl = "https://example.com/thumb.jpg"
        )
        repo.addItem(item)
        
        assertEquals(1, repo.getHistory().size)
        assertEquals(1, persistence.getHistory().size)
        assertEquals("Test Song", persistence.getHistory()[0].title)
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
        assertTrue(persistence.getHistory().isEmpty())
    }

    @Test
    fun `isPlaylist flag is correctly persisted and retrieved`() {
        val playlistItem = DownloadHistoryItem(
            title = "My Playlist",
            filePath = "/downloads/playlist",
            format = "MP3",
            timestamp = 1000L,
            thumbnailUrl = "",
            isPlaylist = true
        )
        val singleItem = DownloadHistoryItem(
            title = "Single Song",
            filePath = "/downloads/song.mp3",
            format = "MP3",
            timestamp = 2000L,
            thumbnailUrl = "",
            isPlaylist = false
        )
        repo.addItem(playlistItem)
        repo.addItem(singleItem)

        val history = repo.getHistory()
        assertEquals(2, history.size)
        assertEquals("Single Song", history[0].title)
        assertFalse(history[0].isPlaylist)
        
        assertEquals("My Playlist", history[1].title)
        assertTrue(history[1].isPlaylist)
    }

    @Test
    fun `duplicate entries are both preserved`() {
        val item1 = DownloadHistoryItem("Song", "/a", "M4A", 1000, "")
        val item2 = DownloadHistoryItem("Song", "/a", "M4A", 1000, "")
        repo.addItem(item1)
        repo.addItem(item2)

        val history = repo.getHistory()
        assertEquals(2, history.size)
    }

    @Test
    fun `long file paths are preserved`() {
        val longPath = "/downloads/" + "a".repeat(1000) + ".mp3"
        val item = DownloadHistoryItem("Song", longPath, "M4A", 1000, "")
        repo.addItem(item)

        val history = repo.getHistory()
        assertEquals(1, history.size)
        assertEquals(longPath, history[0].filePath)
    }
}
