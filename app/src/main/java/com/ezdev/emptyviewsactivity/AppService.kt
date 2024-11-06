package com.ezdev.emptyviewsactivity

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.ezdev.emptyviewsactivity.util.log

class AppService : Service() {
    private val binder = AppBinder()
    private val data = ServiceData()

    inner class AppBinder : Binder() {
        fun getService(): AppService = this@AppService
    }

    override fun onCreate() {
        log("onCreate")
    }

    override fun onBind(p0: Intent?): IBinder {
        log("onBind")

        return binder
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        log("onStartCommand")

        data.data = intent.getStringExtra(EXTRA_DATA)
        updateNotification(data)

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        log("onUnbind")

        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        log("onDestroy")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        log("onTaskRemoved")

        updateNotification(data)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun getNotification(serviceData: ServiceData): Notification {
        val currentTime = System.currentTimeMillis().toInt()
        val pendingIntentFlag = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val fullScreenIntent = getFullScreenIntent(applicationContext, serviceData)
        val fullScreenPendingIntent =
            PendingIntent.getActivity(this, currentTime, fullScreenIntent, pendingIntentFlag)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
            Notification.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(serviceData.data)
                .setContentText(serviceData.data)
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .build()
        } else {
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(serviceData.data)
                .setContentText(serviceData.data)
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .build()
        }
    }

    private fun updateNotification(serviceData: ServiceData) {
        this.data.set(serviceData)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                getNotification(serviceData),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_PHONE_CALL
            )
        } else {
            startForeground(NOTIFICATION_ID, getNotification(serviceData))
        }
    }

    private fun getFullScreenIntent(context: Context, serviceData: ServiceData) =
        Intent(context, MainActivity::class.java).apply {
            putExtra(EXTRA_DATA, serviceData.data)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        }

    companion object {
        private const val CHANNEL_ID = "CHANNEL_ID"
        private const val CHANNEL_NAME = "CHANNEL_NAME"
        private const val NOTIFICATION_ID = 8386

        fun startService(context: Context, data: String) {
            val intent = Intent(context, AppService::class.java).apply {
                putExtra(EXTRA_DATA, data)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, AppService::class.java)
            context.stopService(intent)
        }
    }

}

data class ServiceData(
    var data: String? = null
) {
    fun set(serviceData: ServiceData) {
        data = serviceData.data
    }
}

const val EXTRA_DATA = "EXTRA_DATA"