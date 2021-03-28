package com.taetae98.something.database

import androidx.lifecycle.LiveData
import com.taetae98.something.dto.Drawer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DrawerRepository @Inject constructor(
    private val drawerDao: DrawerDao
) {
    fun insertDrawer(drawer: Drawer) {
        CoroutineScope(Dispatchers.IO).launch {
            drawerDao.insert(drawer)
        }
    }

    fun updateDrawer(drawer: Drawer) {
        CoroutineScope(Dispatchers.IO).launch {
            drawerDao.update(drawer)
        }
    }

    fun deleteDrawer(drawer: Drawer) {
        CoroutineScope(Dispatchers.IO).launch {
            drawerDao.delete(drawer)
        }
    }

    suspend fun select(): List<Drawer> {
        return drawerDao.select()
    }

    fun selectLiveData(): LiveData<List<Drawer>> {
        return drawerDao.selectLiveData()
    }
}