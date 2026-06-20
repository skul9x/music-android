package com.musicdownloader.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.musicdownloader.app.data.models.DownloadProgress
import com.musicdownloader.app.util.formatDuration

object NotificationHelper {
    const val CHANNEL_ID = "downloads"
    const val NOTIFICATION_ID = 1

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Downloads"
            val descriptionText = "Notifications for download progress"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableLights(false)
                enableVibration(false)
                setSound(null, null)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    internal fun getProgressContentText(progress: DownloadProgress): String {
        val progressPercent = progress.percent.toInt()
        val speedText = progress.speedStr
        val postProcessStatus = progress.getPostProcessingStatus()

        if (postProcessStatus != null) {
            return if (progress.totalItems > 0) {
                "Item ${progress.currentItem} of ${progress.totalItems}: $postProcessStatus"
            } else {
                postProcessStatus
            }
        }

        return if (progress.totalItems > 0) {
            "Downloading item ${progress.currentItem} of ${progress.totalItems} (${progressPercent}%)"
        } else {
            if (speedText.isNotEmpty()) {
                "Downloading: $progressPercent% (${speedText})"
            } else {
                "Downloading: $progressPercent%"
            }
        }
    }

    internal fun getProgressSubText(progress: DownloadProgress): String? {
        if (progress.getPostProcessingStatus() != null) {
            return "Post-processing..."
        }
        val speedText = progress.speedStr
        if (progress.totalItems > 0 && speedText.isNotEmpty()) {
            val etaStr = if (progress.etaSeconds > 0) {
                " • ETA: ${formatDuration(progress.etaSeconds)}"
            } else {
                ""
            }
            return "$speedText$etaStr"
        }
        return null
    }

    fun buildProgressNotification(
        context: Context,
        title: String,
        progress: DownloadProgress,
        cancelPendingIntent: PendingIntent
    ): Notification {
        val progressPercent = progress.percent.toInt()
        val contentText = getProgressContentText(progress)
        val subText = getProgressSubText(progress)
        val isIndeterminate = progress.percent < 0 || progress.getPostProcessingStatus() != null

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setProgress(100, progressPercent, isIndeterminate)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Cancel",
                cancelPendingIntent
            )

        if (subText != null) {
            builder.setSubText(subText)
        }

        return builder.build()
    }

    fun buildSuccessNotification(
        context: Context,
        title: String,
        filePath: String,
        openPendingIntent: PendingIntent? = null
    ): Notification {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText("Download complete")
            .setSubText(filePath)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setAutoCancel(true)
            .setOngoing(false)

        openPendingIntent?.let {
            builder.addAction(
                android.R.drawable.ic_menu_view,
                "Open",
                it
            )
        }
        return builder.build()
    }

    fun buildFailedNotification(
        context: Context,
        title: String,
        errorMsg: String,
        retryPendingIntent: PendingIntent? = null
    ): Notification {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(errorMsg)
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setAutoCancel(true)
            .setOngoing(false)

        retryPendingIntent?.let {
            builder.addAction(
                android.R.drawable.stat_notify_sync,
                "Retry",
                it
            )
        }
        return builder.build()
    }
}
