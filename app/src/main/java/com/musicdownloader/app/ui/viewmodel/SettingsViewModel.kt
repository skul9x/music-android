package com.musicdownloader.app.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.musicdownloader.app.data.models.DownloadFormat
import com.musicdownloader.app.data.models.Settings
import com.musicdownloader.app.data.repository.SettingsRepository
import com.musicdownloader.app.data.repository.UpdateManager
import com.musicdownloader.app.data.repository.UpdateStatus
import com.yausername.youtubedl_android.YoutubeDL
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface UpdateUiState {
    object Idle : UpdateUiState
    object Updating : UpdateUiState
    object AlreadyUpToDate : UpdateUiState
    data class Updated(val version: String) : UpdateUiState
    data class Error(val message: String) : UpdateUiState
}

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val updateManager: UpdateManager
) : ViewModel() {

    val settingsState: StateFlow<Settings> = settingsRepository.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = settingsRepository.getSettings()
        )

    private val _updateState = MutableStateFlow<UpdateUiState>(UpdateUiState.Idle)
    val updateState: StateFlow<UpdateUiState> = _updateState.asStateFlow()

    fun updateDefaultFormat(format: DownloadFormat) {
        val current = settingsRepository.getSettings()
        settingsRepository.updateSettings(current.copy(defaultFormat = format))
    }

    fun updateDefaultSavePath(path: String) {
        val current = settingsRepository.getSettings()
        settingsRepository.updateSettings(current.copy(defaultSavePath = path))
    }

    fun updateAutoPasteEnabled(enabled: Boolean) {
        val current = settingsRepository.getSettings()
        settingsRepository.updateSettings(current.copy(autoPasteEnabled = enabled))
    }

    fun triggerEngineUpdate(context: Context) {
        viewModelScope.launch {
            _updateState.value = UpdateUiState.Updating
            when (val result = updateManager.updateEngine(context)) {
                is UpdateStatus.AlreadyUpToDate -> {
                    _updateState.value = UpdateUiState.AlreadyUpToDate
                    val current = settingsRepository.getSettings()
                    settingsRepository.updateSettings(current.copy(engineLastUpdated = System.currentTimeMillis()))
                }
                is UpdateStatus.Updated -> {
                    _updateState.value = UpdateUiState.Updated(result.version)
                    val current = settingsRepository.getSettings()
                    settingsRepository.updateSettings(current.copy(engineLastUpdated = System.currentTimeMillis()))
                }
                is UpdateStatus.Error -> {
                    _updateState.value = UpdateUiState.Error(result.message)
                }
            }
        }
    }

    fun resetUpdateState() {
        _updateState.value = UpdateUiState.Idle
    }

    fun getEngineVersion(context: Context): String {
        return try {
            YoutubeDL.getInstance().versionName(context) ?: "unknown"
        } catch (e: Exception) {
            "unknown"
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                    ?: throw IllegalArgumentException("Application key is missing")
                return SettingsViewModel(
                    SettingsRepository(application.applicationContext),
                    UpdateManager()
                ) as T
            }
        }
    }
}
