package com.taetae98.something.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.taetae98.something.MainActivity

class StickyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        context.startActivity(Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP))
    }
}