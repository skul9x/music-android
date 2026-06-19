package com.musicdownloader.app.data.repository

import com.musicdownloader.app.data.models.DownloadFormat
import com.musicdownloader.app.data.models.DownloadProgress
import com.musicdownloader.app.data.models.VideoInfo
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay

class FakeDownloadRepository : IDownloadRepository {

    var videoInfoToReturn: VideoInfo? = null
    var shouldThrowOnFetch: Boolean = false

    var downloadResultPath: String = ""
    var shouldThrowOnDownload: Boolean = false
    var simulateSlowDownload: Boolean = false
    var isCancelled: Boolean = false
        private set

    override suspend fun fetchVideoInfo(url: String): VideoInfo {
        if (shouldThrowOnFetch) {
            throw Exception("Fake fetch error")
        }
        return videoInfoToReturn ?: VideoInfo(
            title = "Fake Video",
            thumbnailUrl = "https://example.com/fake.jpg",
            duration = 120,
            uploader = "FakeUser",
            url = url
        )
    }

    override suspend fun download(
        url: String,
        savePath: String,
        format: DownloadFormat,
        onProgress: (DownloadProgress) -> Unit
    ): String {
        isCancelled = false
        if (shouldThrowOnDownload) {
            throw Exception("Fake download error")
        }
        if (simulateSlowDownload) {
            for (i in 1..10) {
                if (isCancelled) {
                    throw CancellationException("Download cancelled")
                }
                onProgress(DownloadProgress(i * 10f, 100L - i * 10, "1.0MiB/s", "[download] ${i * 10}%"))
                delay(50)
            }
        } else {
            onProgress(DownloadProgress(100f, 0, "1.0MiB/s", "[download] 100%"))
        }
        return downloadResultPath
    }

    override fun cancelDownload() {
        isCancelled = true
    }
}
