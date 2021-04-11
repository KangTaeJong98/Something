package com.taetae98.something.repository

import androidx.lifecycle.LiveData
import com.taetae98.something.database.DrawerDao
import com.taetae98.something.dto.Drawer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DrawerRepository @Inject constructor(
    private val drawerDao: DrawerDao
) {
    fun insert(drawer: Drawer) {
        CoroutineScope(Dispatchers.IO).launch {
            drawerDao.insert(drawer)
        }
    }

    fun update(drawer: Drawer) {
        CoroutineScope(Dispatchers.IO).launch {
            drawerDao.update(drawer)
        }
    }

    fun delete(drawer: Drawer) {
        CoroutineScope(Dispatchers.IO).launch {
            drawerDao.delete(drawer)
        }
    }

    suspend fun delete() {
        drawerDao.select().forEach {
            drawerDao.delete(it)
        }
    }

    suspend fun selectById(id: Long): Optional<Drawer> {
        return drawerDao.selectWithId(id)
    }

    suspend fun select(): List<Drawer> {
        return drawerDao.select()
    }

    fun selectLiveData(): LiveData<List<Drawer>> {
        return drawerDao.selectLiveData()
    }
}