package com.musicdownloader.app.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musicdownloader.app.data.models.DownloadFormat
import com.musicdownloader.app.data.models.DownloadHistoryItem
import com.musicdownloader.app.data.models.DownloadProgress
import com.musicdownloader.app.data.models.DownloadUiState
import com.musicdownloader.app.data.models.VideoInfo
import com.musicdownloader.app.data.repository.HistoryRepository
import com.musicdownloader.app.data.repository.IDownloadRepository
import com.musicdownloader.app.service.DownloadService
import com.musicdownloader.app.service.DownloadServiceBridge
import com.musicdownloader.app.util.LibraryInitializer
import com.musicdownloader.app.util.UrlValidator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DownloadViewModel(
    private val repository: IDownloadRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    val isLibraryReady: StateFlow<Boolean> = LibraryInitializer.isInitialized
    val libraryError: StateFlow<String?> = LibraryInitializer.initError

    private val _uiState = MutableStateFlow<DownloadUiState>(DownloadUiState.Idle)
    val uiState: StateFlow<DownloadUiState> = _uiState.asStateFlow()

    private var fetchJob: Job? = null
    private var downloadJob: Job? = null

    private var lastVideoInfo: VideoInfo? = null
    private var lastUrl: String? = null
    private var lastFormat: DownloadFormat? = null

    val historyFlow: StateFlow<List<DownloadHistoryItem>> = historyRepository.historyFlow

    init {
        viewModelScope.launch {
            DownloadServiceBridge.state.collect { serviceState ->
                if (serviceState !is DownloadUiState.Idle) {
                    _uiState.value = serviceState
                }
            }
        }
        viewModelScope.launch {
            _uiState.collect { state ->
                if (state is DownloadUiState.Success) {
                    val info = lastVideoInfo
                    val url = lastUrl ?: ""
                    val isPlaylist = info?.isPlaylist == true || com.musicdownloader.app.util.NetworkHelper.isPlaylistUrl(url)
                    val defaultTitle = if (isPlaylist) "Playlist Folder" else "Downloaded File"
                    val formatStr = lastFormat?.name?.replace("_", " ") ?: "M4A AUDIO"
                    historyRepository.addItem(
                        DownloadHistoryItem(
                            title = info?.title ?: defaultTitle,
                            filePath = state.filePath,
                            format = formatStr,
                            timestamp = System.currentTimeMillis(),
                            thumbnailUrl = info?.thumbnailUrl ?: "",
                            isPlaylist = isPlaylist
                        )
                    )
                }
            }
        }
    }

    fun fetchInfo(url: String) {
        if (!UrlValidator.isSupported(url)) {
            _uiState.value = DownloadUiState.Error("Invalid URL")
            return
        }
        _uiState.value = DownloadUiState.FetchingInfo
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            try {
                val info = repository.fetchVideoInfo(url)
                lastVideoInfo = info
                _uiState.value = DownloadUiState.InfoReady(info)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = DownloadUiState.Error(e.message ?: "Failed to fetch video info")
            }
        }
    }

    fun startDownload(url: String, savePath: String, format: DownloadFormat, context: Context? = null) {
        if (!UrlValidator.isSupported(url)) {
            _uiState.value = DownloadUiState.Error("Invalid URL")
            return
        }
        val info = (_uiState.value as? DownloadUiState.InfoReady)?.videoInfo
        if (info != null) {
            lastVideoInfo = info
        }
        lastUrl = url
        lastFormat = format
        _uiState.value = DownloadUiState.Downloading(DownloadProgress(0f, 0, "", ""))
        
        if (context != null) {
            val intent = Intent(context, DownloadService::class.java).apply {
                action = DownloadService.ACTION_START
                putExtra(DownloadService.EXTRA_URL, url)
                putExtra(DownloadService.EXTRA_SAVE_PATH, savePath)
                putExtra(DownloadService.EXTRA_FORMAT, format.name)
                putExtra(DownloadService.EXTRA_TITLE, info?.title)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        } else {
            downloadJob?.cancel()
            downloadJob = viewModelScope.launch {
                try {
                    val path = repository.download(url, savePath, format) { progress ->
                        _uiState.value = DownloadUiState.Downloading(progress)
                    }
                    _uiState.value = DownloadUiState.Success(path)
                } catch (e: CancellationException) {
                    _uiState.value = DownloadUiState.Cancelled
                    throw e
                } catch (e: Exception) {
                    _uiState.value = DownloadUiState.Error(e.message ?: "Download failed")
                }
            }
        }
    }

    fun cancelDownload(context: Context? = null) {
        if (context != null) {
            val intent = Intent(context, DownloadService::class.java).apply {
                action = DownloadService.ACTION_CANCEL
            }
            context.startService(intent)
        } else {
            downloadJob?.cancel()
            repository.cancelDownload()
            _uiState.value = DownloadUiState.Cancelled
        }
    }

    fun resetState() {
        fetchJob?.cancel()
        downloadJob?.cancel()
        DownloadServiceBridge.reset()
        _uiState.value = DownloadUiState.Idle
    }

    fun clearHistory() {
        historyRepository.clearHistory()
    }
}

