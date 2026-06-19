package com.musicdownloader.app.util

import android.content.ClipboardManager
import android.content.Context

object ClipboardHelper {
    fun getClipboardText(context: Context): String? {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = clipboard?.primaryClip
        if (clip != null && clip.itemCount > 0) {
            return clip.getItemAt(0).text?.toString()
        }
        return null
    }
}
