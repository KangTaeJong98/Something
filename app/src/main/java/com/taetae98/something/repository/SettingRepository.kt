package com.taetae98.something.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.taetae98.something.R
import com.taetae98.something.di.SettingDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingRepository @Inject constructor() {
    private val isStickyKey by lazy { booleanPreferencesKey("Is Sticky") }
    private val defaultThemeKey by lazy { intPreferencesKey("Default Theme") }
    private val todoShowFinishedToDoKey by lazy { booleanPreferencesKey("ToDo Show Finished ToDo") }
    private val todoDefaultDrawerKey by lazy { longPreferencesKey("ToDo Default Drawer") }
    private val calendarShowFinishedToDoKey by lazy { booleanPreferencesKey("Calendar Show Finished ToDo") }


    val themeList = arrayOf("System" to R.style.Theme_Something, "Blue" to R.style.Theme_Something_Blue, "Dark" to R.style.Theme_Something_Dark)

    @Inject
    @SettingDataStore
    lateinit var settingStore: DataStore<Preferences>

    fun getDefaultTheme(): Flow<Int> {
        return settingStore.data.map {
            it[defaultThemeKey] ?: 0
        }
    }

    suspend fun setDefaultTheme(value: Int) {
        settingStore.edit {
            it[defaultThemeKey] = value
        }
    }

    fun getToDoShowFinishedToDo(): Flow<Boolean> {
        return settingStore.data.map {
            it[todoShowFinishedToDoKey] ?: false
        }
    }

    suspend fun setToDoShowFinishedToDo(value: Boolean) {
        settingStore.edit {
            it[todoShowFinishedToDoKey] = value
        }
    }

    fun getToDoDefaultDrawer(): Flow<Long?> {
        return settingStore.data.map { it[todoDefaultDrawerKey] }
    }

    suspend fun setToDoDefaultDrawer(value: Long?) {
        settingStore.edit {
            if (value == null) {
                it.remove(todoDefaultDrawerKey)
            } else {
                it[todoDefaultDrawerKey] = value
            }
        }
    }

    fun getCalendarShowFinishedToDo(): Flow<Boolean> {
        return settingStore.data.map { it[calendarShowFinishedToDoKey] ?: false }
    }

    suspend fun setCalendarShowFinishedToDo(value: Boolean) {
        settingStore.edit { it[calendarShowFinishedToDoKey] = value }
    }

    fun getIsSticky(): Flow<Boolean> {
        return settingStore.data.map { it[isStickyKey] ?: false }
    }

    suspend fun setIsSticky(value: Boolean) {
        settingStore.edit { it[isStickyKey] = value }
    }
}