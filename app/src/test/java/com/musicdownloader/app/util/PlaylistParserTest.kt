package com.musicdownloader.app.util

import org.junit.Assert.*
import org.junit.Test

class PlaylistParserTest {

    @Test
    fun `test standard playlist parsing`() {
        val json = """
            {
                "title": "My Favorite Hits",
                "uploader": "Awesome Music Channel",
                "thumbnail": "https://example.com/playlist_thumb.jpg",
                "entries": [
                    {
                        "title": "Song 1",
                        "id": "abc123"
                    },
                    {
                        "title": "Song 2",
                        "id": "def456"
                    }
                ]
            }
        """.trimIndent()

        val url = "https://youtube.com/playlist?list=PLxyz"
        val videoInfo = PlaylistParser.parse(json, url)

        assertEquals("My Favorite Hits", videoInfo.title)
        assertEquals("Awesome Music Channel", videoInfo.uploader)
        assertEquals("https://example.com/playlist_thumb.jpg", videoInfo.thumbnailUrl)
        assertEquals(2, videoInfo.videoCount)
        assertEquals(0L, videoInfo.duration)
        assertTrue(videoInfo.isPlaylist)
        assertEquals(url, videoInfo.url)
    }

    @Test
    fun `test uploader fallback to uploader_id`() {
        val json = """
            {
                "title": "Mix Playlist",
                "uploader_id": "user123_id",
                "thumbnail": "https://example.com/thumb.jpg",
                "entries": []
            }
        """.trimIndent()

        val videoInfo = PlaylistParser.parse(json, "http://test.url")
        assertEquals("user123_id", videoInfo.uploader)
    }

    @Test
    fun `test uploader unknown fallback`() {
        val json = """
            {
                "title": "Anonymous Playlist",
                "entries": []
            }
        """.trimIndent()

        val videoInfo = PlaylistParser.parse(json, "http://test.url")
        assertEquals("Unknown Uploader", videoInfo.uploader)
    }

    @Test
    fun `test fallback to first entry thumbnail when top-level is missing`() {
        val json = """
            {
                "title": "No Thumb Playlist",
                "uploader": "Uploader",
                "entries": [
                    {
                        "title": "Song 1",
                        "thumbnail": "https://example.com/first_entry_thumb.jpg"
                    },
                    {
                        "title": "Song 2",
                        "thumbnail": "https://example.com/second_entry_thumb.jpg"
                    }
                ]
            }
        """.trimIndent()

        val videoInfo = PlaylistParser.parse(json, "http://test.url")
        assertEquals("https://example.com/first_entry_thumb.jpg", videoInfo.thumbnailUrl)
    }

    @Test
    fun `test thumbnail extraction from thumbnails array`() {
        val json = """
            {
                "title": "Playlist",
                "uploader": "Uploader",
                "thumbnails": [
                    { "url": "https://example.com/low.jpg", "width": 100 },
                    { "url": "https://example.com/high.jpg", "width": 480 }
                ],
                "entries": []
            }
        """.trimIndent()

        val videoInfo = PlaylistParser.parse(json, "http://test.url")
        assertEquals("https://example.com/high.jpg", videoInfo.thumbnailUrl)
    }

    @Test
    fun `test first entry thumbnails array fallback`() {
        val json = """
            {
                "title": "Playlist",
                "uploader": "Uploader",
                "entries": [
                    {
                        "title": "Song 1",
                        "thumbnails": [
                            { "url": "https://example.com/entry_low.jpg" },
                            { "url": "https://example.com/entry_high.jpg" }
                        ]
                    }
                ]
            }
        """.trimIndent()

        val videoInfo = PlaylistParser.parse(json, "http://test.url")
        assertEquals("https://example.com/entry_high.jpg", videoInfo.thumbnailUrl)
    }
}
