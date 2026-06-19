package com.musicdownloader.app.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import com.musicdownloader.app.MainActivity
import com.musicdownloader.app.data.models.DownloadFormat
import com.musicdownloader.app.data.models.DownloadProgress
import com.musicdownloader.app.data.models.DownloadUiState
import com.musicdownloader.app.data.repository.DownloadRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class DownloadService : Service() {

    companion object {
        const val ACTION_START = "com.musicdownloader.app.action.START"
        const val ACTION_CANCEL = "com.musicdownloader.app.action.CANCEL"
        const val EXTRA_URL = "url"
        const val EXTRA_SAVE_PATH = "save_path"
        const val EXTRA_FORMAT = "format"
        const val EXTRA_TITLE = "title"
    }

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private var downloadJob: Job? = null
    private val repository = DownloadRepository()

    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        NotificationHelper.createNotificationChannel(this)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == ACTION_CANCEL) {
            cancelDownload()
            return START_NOT_STICKY
        }

        val url = intent?.getStringExtra(EXTRA_URL)
        val savePath = intent?.getStringExtra(EXTRA_SAVE_PATH)
        val formatStr = intent?.getStringExtra(EXTRA_FORMAT)
        val title = intent?.getStringExtra(EXTRA_TITLE) ?: "Downloading..."

        if (url.isNullOrBlank() || savePath.isNullOrBlank() || formatStr.isNullOrBlank()) {
            stopSelf()
            return START_NOT_STICKY
        }

        val format = try {
            DownloadFormat.valueOf(formatStr)
        } catch (e: Exception) {
            DownloadFormat.M4A_AUDIO
        }

        startDownload(url, savePath, format, title)

        return START_NOT_STICKY
    }

    private fun startDownload(url: String, savePath: String, format: DownloadFormat, title: String) {
        downloadJob?.cancel()
        
        val initialNotification = NotificationHelper.buildProgressNotification(
            this,
            title,
            DownloadProgress(0f, 0, "", ""),
            getCancelPendingIntent()
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NotificationHelper.NOTIFICATION_ID,
                initialNotification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(NotificationHelper.NOTIFICATION_ID, initialNotification)
        }

        DownloadServiceBridge.updateState(DownloadUiState.Downloading(DownloadProgress(0f, 0, "", "")))

        downloadJob = serviceScope.launch {
            try {
                val filePath = repository.download(url, savePath, format) { progress ->
                    DownloadServiceBridge.updateState(DownloadUiState.Downloading(progress))
                    val notification = NotificationHelper.buildProgressNotification(
                        this@DownloadService,
                        title,
                        progress,
                        getCancelPendingIntent()
                    )
                    notificationManager.notify(NotificationHelper.NOTIFICATION_ID, notification)
                }

                DownloadServiceBridge.updateState(DownloadUiState.Success(filePath))
                
                val openIntent = Intent(this@DownloadService, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                val pendingOpenIntent = PendingIntent.getActivity(
                    this@DownloadService,
                    0,
                    openIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val successNotification = NotificationHelper.buildSuccessNotification(
                    this@DownloadService,
                    "Download Complete",
                    filePath,
                    pendingOpenIntent
                )

                stopForegroundCompat()
                notificationManager.notify(NotificationHelper.NOTIFICATION_ID, successNotification)
                stopSelf()

            } catch (e: CancellationException) {
                DownloadServiceBridge.updateState(DownloadUiState.Cancelled)
                stopForegroundCompat()
                stopSelf()
            } catch (e: Exception) {
                DownloadServiceBridge.updateState(DownloadUiState.Error(e.message ?: "Download failed"))
                
                val retryIntent = Intent(this@DownloadService, DownloadService::class.java).apply {
                    action = ACTION_START
                    putExtra(EXTRA_URL, url)
                    putExtra(EXTRA_SAVE_PATH, savePath)
                    putExtra(EXTRA_FORMAT, format.name)
                    putExtra(EXTRA_TITLE, title)
                }
                val pendingRetryIntent = PendingIntent.getService(
                    this@DownloadService,
                    1,
                    retryIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val failedNotification = NotificationHelper.buildFailedNotification(
                    this@DownloadService,
                    "Download Failed",
                    e.message ?: "An error occurred",
                    pendingRetryIntent
                )

                stopForegroundCompat()
                notificationManager.notify(NotificationHelper.NOTIFICATION_ID, failedNotification)
                stopSelf()
            }
        }
    }

    private fun cancelDownload() {
        downloadJob?.cancel()
        repository.cancelDownload()
        DownloadServiceBridge.updateState(DownloadUiState.Cancelled)
        stopForegroundCompat()
        stopSelf()
    }

    private fun stopForegroundCompat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            stopForeground(STOP_FOREGROUND_DETACH)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(false)
        }
    }

    private fun getCancelPendingIntent(): PendingIntent {
        val intent = Intent(this, DownloadService::class.java).apply {
            action = ACTION_CANCEL
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getService(this, 0, intent, flags)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }

    override fun onTimeout(startId: Int) {
        super.onTimeout(startId)
        cancelDownload()
    }
}
