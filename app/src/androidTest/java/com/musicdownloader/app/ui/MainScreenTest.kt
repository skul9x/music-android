package com.musicdownloader.app.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.musicdownloader.app.data.models.*
import com.musicdownloader.app.data.repository.FakeDownloadRepository
import com.musicdownloader.app.ui.screens.MainScreen
import com.musicdownloader.app.ui.theme.MusicDownloaderTheme
import com.musicdownloader.app.ui.viewmodel.DownloadViewModel
import org.junit.Rule
import org.junit.Test

class MainScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createViewModel(): DownloadViewModel {
        return DownloadViewModel(FakeDownloadRepository())
    }

    @Test
    fun mainScreen_displaysUrlInput() {
        composeTestRule.setContent {
            MusicDownloaderTheme {
                MainScreen(viewModel = createViewModel())
            }
        }
        composeTestRule.onNodeWithTag("url_input").assertIsDisplayed()
    }

    @Test
    fun mainScreen_displaysFormatSelector() {
        composeTestRule.setContent {
            MusicDownloaderTheme {
                MainScreen(viewModel = createViewModel())
            }
        }
        composeTestRule.onNodeWithTag("format_selector").assertIsDisplayed()
    }

    @Test
    fun mainScreen_fetchInfoButton_exists() {
        composeTestRule.setContent {
            MusicDownloaderTheme {
                MainScreen(viewModel = createViewModel())
            }
        }
        composeTestRule.onNodeWithTag("fetch_info_button").assertIsDisplayed()
    }

    @Test
    fun mainScreen_pasteButton_exists() {
        composeTestRule.setContent {
            MusicDownloaderTheme {
                MainScreen(viewModel = createViewModel())
            }
        }
        composeTestRule.onNodeWithTag("paste_button").assertIsDisplayed()
    }

    @Test
    fun mainScreen_downloadButton_disabledWhenUrlEmpty() {
        composeTestRule.setContent {
            MusicDownloaderTheme {
                MainScreen(viewModel = createViewModel())
            }
        }
        composeTestRule.onNodeWithTag("download_button").assertIsNotEnabled()
    }
}
