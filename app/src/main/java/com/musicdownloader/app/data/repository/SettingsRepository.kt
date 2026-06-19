package com.musicdownloader.app.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.musicdownloader.app.data.models.DownloadFormat
import com.musicdownloader.app.data.models.Settings
import com.musicdownloader.app.util.StorageHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(private val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("download_prefs", Context.MODE_PRIVATE)

    private val _settingsFlow = MutableStateFlow(loadSettings())
    val settingsFlow: Flow<Settings> = _settingsFlow.asStateFlow()

    private fun loadSettings(): Settings {
        val defaultSavePath = sharedPreferences.getString("save_path", null) ?: StorageHelper.getDefaultDownloadPath()
        val formatName = sharedPreferences.getString("default_format", DownloadFormat.M4A_AUDIO.name) ?: DownloadFormat.M4A_AUDIO.name
        val defaultFormat = try {
            DownloadFormat.valueOf(formatName)
        } catch (e: Exception) {
            DownloadFormat.M4A_AUDIO
        }
        val autoPasteEnabled = sharedPreferences.getBoolean("auto_paste_enabled", true)
        val engineLastUpdated = sharedPreferences.getLong("engine_last_updated", 0L)
        return Settings(defaultSavePath, defaultFormat, autoPasteEnabled, engineLastUpdated)
    }

    fun getSettings(): Settings {
        return loadSettings()
    }

    fun updateSettings(settings: Settings) {
        sharedPreferences.edit().apply {
            putString("save_path", settings.defaultSavePath)
            putString("default_format", settings.defaultFormat.name)
            putBoolean("auto_paste_enabled", settings.autoPasteEnabled)
            putLong("engine_last_updated", settings.engineLastUpdated)
            apply()
        }
        _settingsFlow.value = settings
    }
}
