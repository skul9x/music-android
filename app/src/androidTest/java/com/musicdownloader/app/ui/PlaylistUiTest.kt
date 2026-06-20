package com.musicdownloader.app.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.musicdownloader.app.data.models.DownloadProgress
import com.musicdownloader.app.data.models.VideoInfo
import com.musicdownloader.app.ui.components.DownloadProgressSection
import com.musicdownloader.app.ui.components.VideoInfoCard
import com.musicdownloader.app.ui.theme.MusicDownloaderTheme
import org.junit.Rule
import org.junit.Test

class PlaylistUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun videoInfoCard_showsPlaylistBadgeAndCount_whenIsPlaylistTrue() {
        val playlistInfo = VideoInfo(
            title = "Awesome Playlist",
            thumbnailUrl = "https://example.com/thumb.jpg",
            duration = 0,
            uploader = "Playlist Creator",
            url = "https://youtube.com/playlist?list=PL123",
            isPlaylist = true,
            videoCount = 15
        )

        composeTestRule.setContent {
            MusicDownloaderTheme {
                VideoInfoCard(videoInfo = playlistInfo)
            }
        }

        // Verify elements inside the playlist view are displayed
        composeTestRule.onNodeWithTag("playlist_info_row").assertIsDisplayed()
        composeTestRule.onNodeWithTag("playlist_icon", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("video_count_text", useUnmergedTree = true).assertTextEquals("15 videos")
        composeTestRule.onNodeWithTag("playlist_badge", useUnmergedTree = true).assertIsDisplayed()
        
        // Duration should not be present
        composeTestRule.onNodeWithTag("video_duration").assertDoesNotExist()
    }

    @Test
    fun videoInfoCard_showsDuration_whenIsPlaylistFalse() {
        val videoInfo = VideoInfo(
            title = "Cool Music Video",
            thumbnailUrl = "https://example.com/thumb.jpg",
            duration = 245,
            uploader = "ArtistName",
            url = "https://youtube.com/watch?v=abc",
            isPlaylist = false
        )

        composeTestRule.setContent {
            MusicDownloaderTheme {
                VideoInfoCard(videoInfo = videoInfo)
            }
        }

        // Verify duration is displayed
        composeTestRule.onNodeWithTag("video_duration").assertIsDisplayed()
        
        // Playlist specific rows should not exist
        composeTestRule.onNodeWithTag("playlist_info_row").assertDoesNotExist()
    }

    @Test
    fun downloadProgressSection_showsItemProgress_whenTotalItemsGreaterThanZero() {
        val progress = DownloadProgress(
            percent = 45f,
            etaSeconds = 30,
            speedStr = "2.5 MB/s",
            line = "[download] Downloading item 3 of 10",
            currentItem = 3,
            totalItems = 10
        )

        composeTestRule.setContent {
            MusicDownloaderTheme {
                DownloadProgressSection(
                    progress = progress,
                    onCancelClick = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("progress_status").assertTextEquals("Downloading item 3 of 10")
    }

    @Test
    fun downloadProgressSection_showsDownloadingText_whenTotalItemsIsZero() {
        val progress = DownloadProgress(
            percent = 45f,
            etaSeconds = 30,
            speedStr = "2.5 MB/s",
            line = "[download] 45.0%",
            currentItem = 0,
            totalItems = 0
        )

        composeTestRule.setContent {
            MusicDownloaderTheme {
                DownloadProgressSection(
                    progress = progress,
                    onCancelClick = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("progress_status").assertTextEquals("Downloading...")
    }
}
