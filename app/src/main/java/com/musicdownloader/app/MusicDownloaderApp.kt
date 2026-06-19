package com.musicdownloader.app

import android.app.Application
import android.util.Log
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLException

class MusicDownloaderApp : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            YoutubeDL.getInstance().init(this)
            FFmpeg.getInstance().init(this)
            Log.d("MusicDownloaderApp", "YoutubeDL and FFmpeg initialized successfully")
        } catch (e: YoutubeDLException) {
            Log.e("MusicDownloaderApp", "Failed to initialize YoutubeDL", e)
        } catch (e: Exception) {
            Log.e("MusicDownloaderApp", "Error during initialization", e)
        }
    }
}
