package com.musicdownloader.app.util

import android.content.Context
import android.util.Log
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object LibraryInitializer {
    private const val TAG = "LibraryInitializer"

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    private val _initError = MutableStateFlow<String?>(null)
    val initError: StateFlow<String?> = _initError.asStateFlow()

    fun initialize(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                YoutubeDL.getInstance().init(context)
                FFmpeg.getInstance().init(context)
                _isInitialized.value = true
                Log.d(TAG, "YoutubeDL and FFmpeg initialized successfully")
            } catch (e: Throwable) {
                val errorMsg = e.message ?: "Unknown initialization error"
                _initError.value = errorMsg
                Log.e(TAG, "Failed to initialize YoutubeDL or FFmpeg: $errorMsg", e)
            }
        }
    }

    fun reset() {
        _isInitialized.value = false
        _initError.value = null
    }
}
