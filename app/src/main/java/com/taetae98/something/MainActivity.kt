package com.taetae98.something

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.taetae98.something.base.BaseActivity
import com.taetae98.something.databinding.ActivityMainBinding
import com.taetae98.something.repository.SettingRepository
import com.taetae98.something.repository.ToDoRepository
import com.taetae98.something.utility.DataBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity(), DataBinding<ActivityMainBinding> {
    override val binding: ActivityMainBinding by lazy { DataBindingUtil.setContentView(this, R.layout.activity_main) }

    @Inject
    lateinit var settingRepository: SettingRepository

    @Inject
    lateinit var todoRepository: ToDoRepository

    private val navController by lazy {
        (supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment).navController
    }

    private val appBarConfiguration by lazy {
        AppBarConfiguration(
                setOf(
                    R.id.todoFragment, R.id.calendarFragment, R.id.drawerFragment, R.id.finishedFragment, R.id.settingFragment
                ),
                binding.drawer
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreateTheme()
        onCreateViewDataBinding()
        onCreateNavigationView()
        onCreateNotification()
    }

    private fun onCreateTheme() {
        val defaultTheme = runBlocking { settingRepository.getDefaultTheme().first() }
        AppCompatDelegate.setDefaultNightMode(defaultTheme)
    }

    override fun onCreateViewDataBinding() {
        binding.lifecycleOwner = this
    }

    override fun setSupportActionBar(toolbar: Toolbar?) {
        super.setSupportActionBar(toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun onCreateNavigationView() {
        with(binding) {
            navigation.setupWithNavController(navController)
        }
    }

    private fun onCreateNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_MIN)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            manager.createNotificationChannel(channel)
        }

        val contentIntent = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), 0)
        with(NotificationManagerCompat.from(this)) {
            todoRepository.selectToDoLiveData().observe(this@MainActivity) {
                cancelAll()
                it.filter { todo ->
                    todo.isNotification && !todo.isFinished
                }.forEach { todo ->
                    val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Notification.Builder(this@MainActivity, NOTIFICATION_CHANNEL_ID)
                    } else {
                        Notification.Builder(this@MainActivity)
                    }

                    val term = if (todo.hasTerm) {
                        val format = SimpleDateFormat.getDateInstance()
                        if (todo.beginTime == todo.endTime) {
                            format.format(todo.beginTime.timeInMillis) + "\n"
                        } else {
                            val result = format.format(todo.beginTime.timeInMillis) + " ~ " + format.format(todo.endTime.timeInMillis)
                            result + "\n"
                        }
                    } else {
                        ""
                    }

                    with(builder) {
                        setSmallIcon(R.drawable.ic_finish)
                        setContentTitle(todo.title)
                        setContentText("$term${todo.description}")
                        setShowWhen(false)
                        setContentIntent(contentIntent)
                        setOngoing(true)
                        style = Notification.BigTextStyle()
                    }

                    notify(todo.id.toInt(), builder.build())
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        with(binding) {
            if (drawer.isDrawerOpen(navigation)) {
                drawer.closeDrawer(navigation)
            } else {
                super.onBackPressed()
            }
        }
    }
}