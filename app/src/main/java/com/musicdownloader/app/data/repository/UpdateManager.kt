package com.musicdownloader.app.data.repository

import android.content.Context
import com.yausername.youtubedl_android.YoutubeDL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed interface UpdateStatus {
    object AlreadyUpToDate : UpdateStatus
    data class Updated(val version: String) : UpdateStatus
    data class Error(val message: String) : UpdateStatus
}

class UpdateManager {
    suspend fun updateEngine(
        context: Context,
        updateChannel: YoutubeDL.UpdateChannel = YoutubeDL.UpdateChannel._STABLE
    ): UpdateStatus = withContext(Dispatchers.IO) {
        try {
            val status = YoutubeDL.getInstance().updateYoutubeDL(context, updateChannel)
            if (status == YoutubeDL.UpdateStatus.ALREADY_UP_TO_DATE) {
                UpdateStatus.AlreadyUpToDate
            } else {
                val newVersion = YoutubeDL.getInstance().versionName(context) ?: "unknown"
                UpdateStatus.Updated(newVersion)
            }
        } catch (e: Exception) {
            UpdateStatus.Error(e.message ?: "Unknown error")
        }
    }
}
