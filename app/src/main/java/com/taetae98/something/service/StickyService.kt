package com.taetae98.something.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import com.taetae98.something.receiver.StickyReceiver
import com.taetae98.something.singleton.NotificationManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StickyService : Service() {
    @Inject
    lateinit var notificationManager: NotificationManager

    companion object {
        private const val NOTIFICATION_FOREGROUND_ID = 1000
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel()
        }

        startForeground(NOTIFICATION_FOREGROUND_ID, notificationManager.createStickyNotification())
        registerReceiver(StickyReceiver(), IntentFilter(Intent.ACTION_SCREEN_OFF))
        return super.onStartCommand(intent, flags, startId)
    }
}