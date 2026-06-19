package com.musicdownloader.app.service

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DownloadServiceTest {

    @Test
    fun service_intent_can_be_created() {
        val context = ApplicationProvider.getApplicationContext<android.app.Application>()
        val intent = Intent(context, DownloadService::class.java).apply {
            putExtra("url", "https://youtube.com/watch?v=test")
            putExtra("save_path", "/storage/emulated/0/Download")
            putExtra("format", "M4A_AUDIO")
        }
        assertNotNull("Intent should be non-null", intent)
        assertEquals("https://youtube.com/watch?v=test", intent.getStringExtra("url"))
    }
}
