package com.taetae98.something.singleton

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.taetae98.something.MainActivity
import com.taetae98.something.R
import com.taetae98.something.dto.ToDo
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationManager @Inject constructor(@ApplicationContext
    private val context: Context
) {
    private val manager by lazy { NotificationManagerCompat.from(context) }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "Something"
        private const val NOTIFICATION_CHANNEL_NAME = "Something"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel() {
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_MIN)
        manager.createNotificationChannel(channel)
    }

    fun notificationToDo(list: List<ToDo>) {
        manager.cancelAll()
        list.forEach {
            manager.notify(it.id.toInt(), createToDoNotification(it))
        }
    }

    private fun createToDoNotification(todo: ToDo): Notification {
        val term = if (todo.hasTerm) {
            val format = SimpleDateFormat.getDateInstance()
            if (todo.beginTime == todo.endTime) {
                format.format(todo.beginTime.timeInMillis)
            } else {
                val result = format.format(todo.beginTime.timeInMillis) + " ~ " + format.format(todo.endTime.timeInMillis)
                result
            }
        } else {
            ""
        }

        val contentIntent = PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), 0)
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_finish)
            setContentTitle(todo.title)
            setContentText("$term${if (term.isNotBlank()) "\n" else "" }${todo.description}")
            setShowWhen(false)
            setContentIntent(contentIntent)
            setOngoing(true)
            setStyle(NotificationCompat.BigTextStyle())
            setGroup("ToDo")
            setGroupSummary(true)
        }.build()
    }

    fun createStickyNotification(): Notification {
        val contentIntent = PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), 0)
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_finish)
            setShowWhen(false)
            setContentTitle(context.getString(R.string.show_when_unlock_the_phone))
            setContentIntent(contentIntent)
        }.build()
    }
}