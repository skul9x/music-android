package com.musicdownloader.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.musicdownloader.app.data.repository.DownloadRepository
import com.musicdownloader.app.data.repository.HistoryRepository
import com.musicdownloader.app.ui.navigation.AppNavigation
import com.musicdownloader.app.ui.theme.MusicDownloaderTheme
import com.musicdownloader.app.ui.viewmodel.DownloadViewModel
import com.musicdownloader.app.ui.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val downloadViewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DownloadViewModel(
                    DownloadRepository(),
                    HistoryRepository.getInstance(applicationContext)
                ) as T
            }
        }
        val downloadViewModel: DownloadViewModel by viewModels { downloadViewModelFactory }
        val settingsViewModel: SettingsViewModel by viewModels { SettingsViewModel.Factory }

        setContent {
            MusicDownloaderTheme {
                AppNavigation(
                    downloadViewModel = downloadViewModel,
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }
}


