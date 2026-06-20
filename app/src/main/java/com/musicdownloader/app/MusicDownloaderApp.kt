package com.musicdownloader.app

import android.app.Application
import android.util.Log
import com.musicdownloader.app.util.LibraryInitializer

class MusicDownloaderApp : Application() {
    override fun onCreate() {
        super.onCreate()
        LibraryInitializer.initialize(this)
    }
}
