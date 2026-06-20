package com.musicdownloader.app.util

import com.musicdownloader.app.data.models.VideoInfo
import org.json.JSONObject

object PlaylistParser {
    fun parse(jsonString: String, url: String): VideoInfo {
        val root = JSONObject(jsonString)
        val title = if (root.has("title")) root.optString("title") else "Unknown Title"
        
        val uploader = when {
            root.has("uploader") && root.optString("uploader").isNotEmpty() -> root.optString("uploader")
            root.has("uploader_id") && root.optString("uploader_id").isNotEmpty() -> root.optString("uploader_id")
            else -> "Unknown Uploader"
        }

        var thumbnailUrl = root.optString("thumbnail", "")
        if (thumbnailUrl.isEmpty() && root.has("thumbnails")) {
            val thumbnails = root.optJSONArray("thumbnails")
            if (thumbnails != null && thumbnails.length() > 0) {
                thumbnailUrl = thumbnails.optJSONObject(thumbnails.length() - 1)?.optString("url", "") ?: ""
            }
        }

        val entries = root.optJSONArray("entries")
        val videoCount = entries?.length() ?: 0

        if (thumbnailUrl.isEmpty() && videoCount > 0) {
            val firstEntry = entries?.optJSONObject(0)
            if (firstEntry != null) {
                thumbnailUrl = firstEntry.optString("thumbnail", "")
                if (thumbnailUrl.isEmpty() && firstEntry.has("thumbnails")) {
                    val entryThumbnails = firstEntry.optJSONArray("thumbnails")
                    if (entryThumbnails != null && entryThumbnails.length() > 0) {
                        thumbnailUrl = entryThumbnails.optJSONObject(entryThumbnails.length() - 1)?.optString("url", "") ?: ""
                    }
                }
            }
        }

        return VideoInfo(
            title = if (title.isEmpty()) "Unknown Title" else title,
            thumbnailUrl = thumbnailUrl,
            duration = 0L,
            uploader = uploader,
            url = url,
            isPlaylist = true,
            videoCount = videoCount
        )
    }
}
