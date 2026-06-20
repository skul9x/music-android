package com.musicdownloader.app.data.repository

import com.musicdownloader.app.data.models.DownloadHistoryItem
import org.junit.Assert.*
import org.junit.Test

class PlaylistHistoryTest {

    @Test
    fun `download history item holds isPlaylist flag`() {
        val item = DownloadHistoryItem(
            title = "My Playlist",
            filePath = "/storage/emulated/0/Download/My Playlist",
            format = "M4A AUDIO",
            timestamp = System.currentTimeMillis(),
            thumbnailUrl = "https://example.com/playlist.jpg",
            isPlaylist = true
        )

        assertTrue(item.isPlaylist)
        assertEquals("My Playlist", item.title)
        assertEquals("/storage/emulated/0/Download/My Playlist", item.filePath)
    }

    @Test
    fun `download history item defaults isPlaylist to false`() {
        val item = DownloadHistoryItem(
            title = "My Single Song",
            filePath = "/storage/emulated/0/Download/song.m4a",
            format = "M4A AUDIO",
            timestamp = System.currentTimeMillis(),
            thumbnailUrl = "https://example.com/song.jpg"
        )

        assertFalse(item.isPlaylist)
    }
}
