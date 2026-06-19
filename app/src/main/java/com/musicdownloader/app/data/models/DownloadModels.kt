package com.musicdownloader.app.data.models

enum class DownloadFormat {
    M4A_AUDIO,
    VIDEO_1080P,
    VIDEO_720P,
    VIDEO_BEST
}

data class VideoInfo(
    val title: String,
    val thumbnailUrl: String,
    val duration: Long,
    val uploader: String,
    val url: String
)

data class DownloadProgress(
    val percent: Float,
    val etaSeconds: Long,
    val speedStr: String,
    val line: String
)

sealed class DownloadUiState {
    object Idle : DownloadUiState()
    object FetchingInfo : DownloadUiState()
    data class InfoReady(val videoInfo: VideoInfo) : DownloadUiState()
    data class Downloading(val progress: DownloadProgress) : DownloadUiState()
    data class Success(val filePath: String) : DownloadUiState()
    data class Error(val message: String) : DownloadUiState()
    object Cancelled : DownloadUiState()
}
