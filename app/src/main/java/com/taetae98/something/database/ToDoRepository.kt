package com.taetae98.something.database

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
    fun insertToDo(todo: ToDo) {
        CoroutineScope(Dispatchers.IO).launch {
            todoDao.insert(todo)
        }
    }

    fun updateToDo(todo: ToDo) {
        CoroutineScope(Dispatchers.IO).launch {
            todoDao.update(todo)
        }
    }

    fun deleteToDo(todo: ToDo) {
        CoroutineScope(Dispatchers.IO).launch {
            todoDao.delete(todo)
        }
    }
}