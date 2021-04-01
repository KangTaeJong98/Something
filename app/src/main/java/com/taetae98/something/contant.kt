package com.taetae98.something

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

const val DATABASE_NAME = "something.db"

const val NOTIFICATION_FOREGROUND_ID = 1000
const val NOTIFICATION_CHANNEL_ID = "Something"
const val NOTIFICATION_CHANNEL_NAME = "Something"

val Context.settingStore: DataStore<Preferences> by preferencesDataStore("settings")