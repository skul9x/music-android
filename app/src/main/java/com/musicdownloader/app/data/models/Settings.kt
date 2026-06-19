package com.musicdownloader.app.data.models

import com.musicdownloader.app.util.StorageHelper

data class Settings(
    val defaultSavePath: String = StorageHelper.getDefaultDownloadPath(),
    val defaultFormat: DownloadFormat = DownloadFormat.M4A_AUDIO,
    val autoPasteEnabled: Boolean = true,
    val engineLastUpdated: Long = 0L
)
