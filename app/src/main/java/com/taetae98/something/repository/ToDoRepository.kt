package com.taetae98.something.repository

import androidx.lifecycle.LiveData
import com.taetae98.something.database.ToDoDao
import com.taetae98.something.dto.ToDo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ToDoRepository @Inject constructor(
    private val todoDao: ToDoDao
) {
    fun insert(todo: ToDo) {
        CoroutineScope(Dispatchers.IO).launch {
            todoDao.insert(todo)
        }
    }

    fun update(todo: ToDo) {
        CoroutineScope(Dispatchers.IO).launch {
            todoDao.update(todo)
        }
    }

    fun delete(todo: ToDo) {
        CoroutineScope(Dispatchers.IO).launch {
            todoDao.delete(todo)
        }
    }

    suspend fun delete() {
        todoDao.select().forEach {
            todoDao.delete(it)
        }
    }

    fun selectLiveData(): LiveData<List<ToDo>> {
        return todoDao.selectLiveData()
    }

    suspend fun selectWithDrawer(drawerId: Long): List<ToDo> {
        return todoDao.selectWithDrawer(drawerId)
    }

    suspend fun select(): List<ToDo> {
        return todoDao.select()
    }
}