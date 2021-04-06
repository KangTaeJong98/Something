package com.taetae98.something.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.taetae98.something.base.BaseDao
import com.taetae98.something.dto.Drawer
import java.util.*

@Dao
interface DrawerDao : BaseDao<Drawer> {
    @Query("SELECT * FROM Drawer")
    suspend fun select(): List<Drawer>

    @Query("SELECT * FROM Drawer")
    fun selectLiveData(): LiveData<List<Drawer>>

    @Query("SELECT * FROM Drawer WHERE id=:id LIMIT 1")
    suspend fun selectWithId(id: Long): Optional<Drawer>
}