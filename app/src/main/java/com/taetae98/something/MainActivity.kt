package com.taetae98.something

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.taetae98.something.base.BaseActivity
import com.taetae98.something.databinding.ActivityMainBinding
import com.taetae98.something.repository.SettingRepository
import com.taetae98.something.repository.ToDoRepository
import com.taetae98.something.singleton.NotificationManager
import com.taetae98.something.utility.DataBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity(), DataBinding<ActivityMainBinding> {
    override val binding: ActivityMainBinding by lazy { DataBinding.get(this, R.layout.activity_main) }

    @Inject
    lateinit var settingRepository: SettingRepository

    @Inject
    lateinit var todoRepository: ToDoRepository

    @Inject
    lateinit var notificationManager: NotificationManager

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
        AppCompatDelegate.setDefaultNightMode(settingRepository.themeList[defaultTheme].second)
    }

    private fun onCreateViewDataBinding() {
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
            notificationManager.createNotificationChannel()
        }

        todoRepository.selectLiveData().observe(this) {
            notificationManager.notificationToDo(it.filter { todo ->
                todo.isNotification && !todo.isFinished
            })
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