package com.taetae98.something.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.taetae98.something.constant.DATABASE_NAME
import com.taetae98.something.dto.Drawer
import com.taetae98.something.dto.ToDo
import com.taetae98.something.utility.TimeTypeConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [ToDo::class, Drawer::class], version = 1, exportSchema = true)
@TypeConverters(TimeTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                        .addCallback(
                            object : RoomDatabase.Callback() {
                                override fun onCreate(db: SupportSQLiteDatabase) {
                                    super.onCreate(db)
                                    CoroutineScope(Dispatchers.IO).launch {
                                        getInstance(context).drawerDao().insert(Drawer())
                                        getInstance(context).todoDao().insert(
                                                ToDo(
                                                        title = "Hello!",
                                                        description = """
                                                            Description
                                                            1. Click to edit.
                                                            2. Long click to menu.
                                                            3. Swipe to set finished
                                                        """.trimIndent()
                                                )
                                        )
                                    }
                                }
                            }
                        )
                        .build()
                        .also {
                            instance = it
                        }
            }
        }
    }

    abstract fun todoDao(): ToDoDao
    abstract fun drawerDao(): DrawerDao
}