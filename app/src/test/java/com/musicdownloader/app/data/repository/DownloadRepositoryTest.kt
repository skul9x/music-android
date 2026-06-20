package com.musicdownloader.app.data.repository

import org.junit.Assert.*
import org.junit.Test

class DownloadRepositoryTest {

    @Test
    fun `test item regex matching`() {
        val itemRegex = Regex("\\[download\\] Downloading item (\\d+) of (\\d+)")
        
        val line1 = "[download] Downloading item 3 of 20"
        val match1 = itemRegex.find(line1)
        assertNotNull(match1)
        assertEquals("3", match1!!.groupValues[1])
        assertEquals("20", match1.groupValues[2])

        val line2 = "[download] Downloading item 15 of 15"
        val match2 = itemRegex.find(line2)
        assertNotNull(match2)
        assertEquals("15", match2!!.groupValues[1])
        assertEquals("15", match2.groupValues[2])

        val lineNonMatching = "[download] 45% of 100MB at 1.2MiB/s ETA 00:30"
        val matchNonMatching = itemRegex.find(lineNonMatching)
        assertNull(matchNonMatching)
    }

    @Test
    fun `test extractFilePath from various formats`() {
        val repo = DownloadRepository()
        
        // Merging format
        val mergingLog = "[ffmpeg] Merging formats into \"/downloads/song.m4a\""
        assertEquals("/downloads/song.m4a", repo.extractFilePath(mergingLog, "/downloads"))
        
        // ffmpeg destination
        val ffmpegDestLog = "[ffmpeg] Destination: /downloads/song.m4a"
        assertEquals("/downloads/song.m4a", repo.extractFilePath(ffmpegDestLog, "/downloads"))

        // download destination
        val downloadDestLog = "[download] Destination: /downloads/song.m4a"
        assertEquals("/downloads/song.m4a", repo.extractFilePath(downloadDestLog, "/downloads"))

        // already downloaded
        val alreadyDownloadedLog = "[download] /downloads/song.m4a has already been downloaded"
        assertEquals("/downloads/song.m4a", repo.extractFilePath(alreadyDownloadedLog, "/downloads"))
    }

    @Test
    fun `test extractPlaylistPath resolves parent folder`() {
        val repo = DownloadRepository()
        
        val output = "[download] Destination: /downloads/My Playlist/Song 1.m4a"
        val playlistPath = repo.extractPlaylistPath(output, "/downloads")
        
        // Normalize slashes for platform independence in assertion
        val normalizedPath = playlistPath.replace('\\', '/')
        assertEquals("/downloads/My Playlist", normalizedPath)
    }
}
