package com.musicdownloader.app.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.musicdownloader.app.ui.screens.SettingsScreen
import com.musicdownloader.app.ui.theme.MusicDownloaderTheme
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun settingsScreen_displaysUpdateEngineButton() {
        composeTestRule.setContent {
            MusicDownloaderTheme {
                SettingsScreen(onNavigateBack = {})
            }
        }
        composeTestRule.onNodeWithTag("update_engine_button").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysAutopasteToggle() {
        composeTestRule.setContent {
            MusicDownloaderTheme {
                SettingsScreen(onNavigateBack = {})
            }
        }
        composeTestRule.onNodeWithTag("autopaste_toggle").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysDefaultFormatSelector() {
        composeTestRule.setContent {
            MusicDownloaderTheme {
                SettingsScreen(onNavigateBack = {})
            }
        }
        composeTestRule.onNodeWithTag("default_format_selector").assertIsDisplayed()
    }
}
