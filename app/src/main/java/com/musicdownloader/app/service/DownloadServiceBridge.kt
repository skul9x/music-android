package com.musicdownloader.app.service

import com.musicdownloader.app.data.models.DownloadUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object DownloadServiceBridge {
    private val _state = MutableStateFlow<DownloadUiState>(DownloadUiState.Idle)
    val state: StateFlow<DownloadUiState> = _state.asStateFlow()

    fun updateState(newState: DownloadUiState) {
        _state.value = newState
    }

    fun reset() {
        _state.value = DownloadUiState.Idle
    }
}
