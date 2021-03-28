package com.taetae98.something.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.taetae98.something.base.BaseDao
import com.taetae98.something.dto.Drawer

@Dao
interface DrawerDao : BaseDao<Drawer> {
    @Query("SELECT * FROM Drawer")
    suspend fun select(): List<Drawer>

    @Query("SELECT * FROM Drawer")
    fun selectLiveData(): LiveData<List<Drawer>>
}