package com.taetae98.something.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.taetae98.something.repository.SettingRepository
import com.taetae98.something.service.StickyService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var settingRepository: SettingRepository

    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context, "Hello" ,Toast.LENGTH_SHORT).show()
        CoroutineScope(Dispatchers.IO).launch {
            if (settingRepository.getIsSticky().first()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(Intent(context, StickyService::class.java))
                } else {
                    context.startService(Intent(context, StickyService::class.java))
                }
            }
        }
    }
}