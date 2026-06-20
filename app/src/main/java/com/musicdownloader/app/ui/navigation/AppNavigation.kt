package com.musicdownloader.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.musicdownloader.app.ui.screens.MainScreen
import com.musicdownloader.app.ui.screens.SettingsScreen
import com.musicdownloader.app.ui.viewmodel.DownloadViewModel
import com.musicdownloader.app.ui.viewmodel.SettingsViewModel

enum class Screen {
    MAIN, SETTINGS
}

@Composable
fun AppNavigation(
    downloadViewModel: DownloadViewModel,
    settingsViewModel: SettingsViewModel
) {
    var currentScreen by remember { mutableStateOf(Screen.MAIN) }

    when (currentScreen) {
        Screen.MAIN -> {
            MainScreen(
                viewModel = downloadViewModel,
                settingsViewModel = settingsViewModel,
                onNavigateToSettings = { currentScreen = Screen.SETTINGS }
            )
        }
        Screen.SETTINGS -> {
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateBack = { currentScreen = Screen.MAIN }
            )
        }
    }
}
