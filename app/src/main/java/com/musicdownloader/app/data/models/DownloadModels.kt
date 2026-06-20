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
    val url: String,
    val isPlaylist: Boolean = false,
    val videoCount: Int = 0
)

data class DownloadProgress(
    val percent: Float,
    val etaSeconds: Long,
    val speedStr: String,
    val line: String,
    val currentItem: Int = 0,
    val totalItems: Int = 0
) {
    fun getPostProcessingStatus(): String? {
        return when {
            line.contains("[ExtractAudio]", ignoreCase = true) -> "Converting to audio..."
            line.contains("[EmbedThumbnail]", ignoreCase = true) -> "Embedding Cover Art..."
            line.contains("[ffmpeg] Merging", ignoreCase = true) -> "Merging audio and video..."
            line.contains("[ffmpeg] Correcting container", ignoreCase = true) -> "Optimizing file format..."
            line.contains("[Metadata]", ignoreCase = true) -> "Embedding metadata..."
            else -> null
        }
    }
}

sealed class DownloadUiState {
    object Idle : DownloadUiState()
    object FetchingInfo : DownloadUiState()
    data class InfoReady(val videoInfo: VideoInfo) : DownloadUiState()
    data class Downloading(val progress: DownloadProgress) : DownloadUiState()
    data class Success(val filePath: String) : DownloadUiState()
    data class Error(val message: String) : DownloadUiState()
    object Cancelled : DownloadUiState()
}
