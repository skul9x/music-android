package com.musicdownloader.app.data.repository

import com.musicdownloader.app.data.models.DownloadFormat
import com.musicdownloader.app.data.models.DownloadProgress
import com.musicdownloader.app.data.models.VideoInfo

interface IDownloadRepository {
    suspend fun fetchVideoInfo(url: String): VideoInfo
    suspend fun download(
        url: String,
        savePath: String,
        format: DownloadFormat,
        onProgress: (DownloadProgress) -> Unit
    ): String
    fun cancelDownload()
}
