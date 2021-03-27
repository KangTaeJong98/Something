package com.taetae98.something.di

import android.content.Context
import com.taetae98.something.database.AppDatabase
import com.taetae98.something.database.DrawerDao
import com.taetae98.something.database.ToDoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideToDoDao(appDatabase: AppDatabase): ToDoDao {
        return appDatabase.todoDao()
    }

    @Provides
    fun provideDrawerDao(appDatabase: AppDatabase): DrawerDao {
        return appDatabase.drawerDao()
    }
}