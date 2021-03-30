package com.taetae98.something

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

const val DATABASE_NAME = "something.db"

val Context.settingStore: DataStore<Preferences> by preferencesDataStore("settings")