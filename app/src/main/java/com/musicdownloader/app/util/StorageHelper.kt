package com.musicdownloader.app.util

import android.content.Context
import android.os.Environment
import java.io.File

object StorageHelper {
    private const val PREFS_NAME = "download_prefs"
    private const val KEY_SAVE_PATH = "save_path"

    fun getDefaultDownloadPath(): String {
        return try {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
        } catch (e: Throwable) {
            System.getProperty("java.io.tmpdir") ?: "/tmp"
        }
    }

    fun getSavePath(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_SAVE_PATH, null) ?: getDefaultDownloadPath()
    }

    fun setSavePath(context: Context, path: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_SAVE_PATH, path).apply()
    }

    fun sanitizeFileName(name: String): String {
        return name.replace(Regex("[\\\\/:*?\"<>|]"), "_")
    }

    fun getPhysicalPathFromUri(context: Context, uri: android.net.Uri): String? {
        if (uri.scheme == "file") {
            return uri.path
        }
        if (uri.scheme == "content") {
            val docId = try {
                android.provider.DocumentsContract.getTreeDocumentId(uri)
            } catch (e: Exception) {
                uri.path
            } ?: return null
            
            val split = docId.split(":")
            if (split.size >= 2 && "primary".equals(split[0], ignoreCase = true)) {
                return Environment.getExternalStorageDirectory().absolutePath + "/" + split[1]
            }
        }
        return null
    }
}
