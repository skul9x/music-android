package com.musicdownloader.app.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object NetworkHelper {

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun isPlaylistUrl(url: String): Boolean {
        return url.contains("list=") || url.contains("/playlist")
    }

    fun stripPlaylistParam(url: String): String {
        if (!url.contains("list=")) return url
        
        val index = url.indexOf("list=")
        if (index == -1) return url
        
        val separatorIndex = url.lastIndexOf('&', index)
        if (separatorIndex != -1) {
            val endOfParam = url.indexOf('&', index)
            return if (endOfParam != -1) {
                url.substring(0, separatorIndex) + url.substring(endOfParam)
            } else {
                url.substring(0, separatorIndex)
            }
        }
        
        val queryStartIndex = url.lastIndexOf('?', index)
        if (queryStartIndex != -1) {
            val endOfParam = url.indexOf('&', index)
            return if (endOfParam != -1) {
                url.substring(0, queryStartIndex + 1) + url.substring(endOfParam + 1)
            } else {
                url.substring(0, queryStartIndex)
            }
        }
        
        return url
    }
}
