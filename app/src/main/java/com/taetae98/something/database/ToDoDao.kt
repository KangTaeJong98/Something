package com.taetae98.something.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.taetae98.something.base.BaseDao
import com.taetae98.something.dto.ToDo

@Dao
interface ToDoDao : BaseDao<ToDo> {
    companion object {
        private const val ORDER = "ORDER BY isOnTop DESC, beginTime, endTime"
    }

    @Query("SELECT * FROM ToDo $ORDER")
    fun selectToDoLiveData(): LiveData<List<ToDo>>
}