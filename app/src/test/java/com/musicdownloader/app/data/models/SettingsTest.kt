package com.musicdownloader.app.data.models

import org.junit.Assert.*
import org.junit.Test

class SettingsTest {

    @Test
    fun `default settings have sensible values`() {
        val settings = Settings()
        assertTrue(settings.defaultSavePath.isNotBlank())
        assertEquals(DownloadFormat.M4A_AUDIO, settings.defaultFormat)
        assertTrue(settings.autoPasteEnabled)
    }

    @Test
    fun `settings can be copied with modifications`() {
        val original = Settings()
        val modified = original.copy(autoPasteEnabled = false)
        assertFalse(modified.autoPasteEnabled)
        assertTrue(original.autoPasteEnabled)
    }
}
