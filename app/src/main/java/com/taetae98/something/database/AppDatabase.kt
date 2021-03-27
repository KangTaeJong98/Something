package com.taetae98.something.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.taetae98.something.constant.DATABASE_NAME
import com.taetae98.something.dto.Drawer
import com.taetae98.something.dto.ToDo
import com.taetae98.something.utility.TimeTypeConverter

@Database(entities = [ToDo::class, Drawer::class], version = 1, exportSchema = true)
@TypeConverters(TimeTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build().also {
                    instance = it
                }
            }
        }
    }

    abstract fun todoDao(): ToDoDao
    abstract fun drawerDao(): DrawerDao
}