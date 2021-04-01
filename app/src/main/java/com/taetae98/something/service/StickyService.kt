package com.taetae98.something.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.taetae98.something.*
import com.taetae98.something.receiver.StickyReceiver

class StickyService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        startForeground(NOTIFICATION_FOREGROUND_ID, buildNotification())

        registerReceiver(StickyReceiver(), IntentFilter(Intent.ACTION_SCREEN_OFF))
        return super.onStartCommand(intent, flags, startId)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_MIN)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        val contentIntent = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), 0)

        return NotificationCompat.Builder(this@StickyService, NOTIFICATION_CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_finish)
            setShowWhen(false)
            setContentTitle(getString(R.string.show_when_unlock_the_phone))
            setContentIntent(contentIntent)
        }.build()
    }
}