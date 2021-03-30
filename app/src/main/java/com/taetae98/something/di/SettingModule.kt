package com.taetae98.something.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.taetae98.something.settingStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SettingModule {

    @Provides
    @Singleton
    @SettingDataStore
    fun providesSettingDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.settingStore
    }
}