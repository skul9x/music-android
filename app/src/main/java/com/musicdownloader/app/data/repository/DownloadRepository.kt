package com.musicdownloader.app.data.repository

import com.musicdownloader.app.data.models.DownloadFormat
import com.musicdownloader.app.data.models.DownloadProgress
import com.musicdownloader.app.data.models.VideoInfo
import com.musicdownloader.app.util.NetworkHelper
import com.musicdownloader.app.util.PlaylistParser
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DownloadRepository : IDownloadRepository {
    private val activeProcessIdKey = "downloader_process_id"

    override suspend fun fetchVideoInfo(url: String): VideoInfo = withContext(Dispatchers.IO) {
        if (NetworkHelper.isPlaylistUrl(url)) {
            val request = YoutubeDLRequest(url).apply {
                addOption("--flat-playlist")
                addOption("--dump-single-json")
            }
            val response = YoutubeDL.getInstance().execute(request)
            val jsonString = response.out
                ?: throw Exception("Failed to get response output from yt-dlp.")
            PlaylistParser.parse(jsonString, url)
        } else {
            val request = YoutubeDLRequest(url).apply {
                addOption("--no-playlist")
            }
            val ytInfo = YoutubeDL.getInstance().getInfo(request)
            VideoInfo(
                title = ytInfo.title ?: "Unknown Title",
                thumbnailUrl = ytInfo.thumbnail ?: "",
                duration = ytInfo.duration.toLong(),
                uploader = ytInfo.uploader ?: "Unknown Uploader",
                url = url,
                isPlaylist = false,
                videoCount = 0
            )
        }
    }


    override suspend fun download(
        url: String,
        savePath: String,
        format: DownloadFormat,
        onProgress: (DownloadProgress) -> Unit
    ): String = withContext(Dispatchers.IO) {
        val isPlaylist = NetworkHelper.isPlaylistUrl(url)
        val request = YoutubeDLRequest(url).apply {
            if (isPlaylist) {
                addOption("-o", "$savePath/%(playlist_title)s/%(title)s.%(ext)s")
            } else {
                addOption("-o", "$savePath/%(title)s.%(ext)s")
                addOption("--no-playlist")
            }
            addOption("--newline")
            
            when (format) {
                DownloadFormat.M4A_AUDIO -> {
                    addOption("-x")
                    addOption("--audio-format", "m4a")
                    addOption("--embed-thumbnail")
                    addOption("--convert-thumbnails", "jpg")
                }
                DownloadFormat.VIDEO_1080P -> {
                    addOption("-f", "bestvideo[height<=1080]+bestaudio/best[height<=1080]")
                }
                DownloadFormat.VIDEO_720P -> {
                    addOption("-f", "bestvideo[height<=720]+bestaudio/best[height<=720]")
                }
                DownloadFormat.VIDEO_BEST -> {
                    addOption("-f", "bestvideo+bestaudio/best")
                }
            }
        }

        var currentItem = 0
        var totalItems = 0
        val itemRegex = Regex("\\[download\\] Downloading item (\\d+) of (\\d+)")

        val response = YoutubeDL.getInstance().execute(request, activeProcessIdKey) { progress, eta, line ->
            val speedStr = extractSpeed(line)
            val match = itemRegex.find(line)
            if (match != null) {
                currentItem = match.groupValues[1].toIntOrNull() ?: currentItem
                totalItems = match.groupValues[2].toIntOrNull() ?: totalItems
            }
            onProgress(
                DownloadProgress(
                    percent = progress,
                    etaSeconds = eta,
                    speedStr = speedStr,
                    line = line,
                    currentItem = currentItem,
                    totalItems = totalItems
                )
            )
        }

        val filePath = if (isPlaylist) {
            extractPlaylistPath(response.out, savePath)
        } else {
            extractFilePath(response.out, savePath)
        }

        if (filePath.isEmpty()) {
            throw Exception("Failed to locate downloaded file path from output.")
        }
        filePath
    }

    override fun cancelDownload() {
        try {
            YoutubeDL.getInstance().destroyProcessById(activeProcessIdKey)
        } catch (e: Exception) {
            // Ignore
        }
    }

    internal fun extractSpeed(line: String): String {
        val regex = Regex("at\\s+([^\\s]+)")
        val match = regex.find(line)
        return match?.groupValues?.get(1) ?: ""
    }

    internal fun extractFilePath(output: String?, savePath: String): String {
        if (output == null) return ""

        val mergingRegex = Regex("\\[ffmpeg\\] Merging formats into \"([^\"]+)\"")
        mergingRegex.find(output)?.groupValues?.get(1)?.let { return it }

        val ffmpegDestRegex = Regex("\\[ffmpeg\\] Destination:\\s+(.+)")
        ffmpegDestRegex.find(output)?.groupValues?.get(1)?.let { return it.trim() }

        val downloadDestRegex = Regex("\\[download\\] Destination:\\s+(.+)")
        downloadDestRegex.find(output)?.groupValues?.get(1)?.let { return it.trim() }

        val alreadyDownloadedRegex = Regex("\\[download\\]\\s+(.+)\\s+has already been downloaded")
        alreadyDownloadedRegex.find(output)?.groupValues?.get(1)?.let { return it.trim() }

        return ""
    }

    internal fun extractPlaylistPath(output: String?, savePath: String): String {
        if (output == null) return ""
        val file = extractFilePath(output, savePath)
        if (file.isNotEmpty()) {
            val javaFile = java.io.File(file)
            return javaFile.parent ?: savePath
        }
        return ""
    }
}
