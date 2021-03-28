package com.taetae98.something.toast

import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.taetae98.something.R
import com.taetae98.something.database.ToDoRepository
import com.taetae98.something.dto.ToDo
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewComponent

class DeleteToDoSnackbar(
        private val view: View,
        private val todo: ToDo
) {
    private val entryPoint by lazy { EntryPoints.get(view, DeleteToDoSnackbarInterface::class.java) }
    private val todoRepository by lazy { entryPoint.getToDoRepository() }

    private val snackbar by lazy {
        Snackbar.make(view, R.string.delete_success, Snackbar.LENGTH_LONG)
                .setActionTextColor(view.context.resources.getColor(R.color.yellow_green, null))
                .setAction(R.string.undo) {
                    todoRepository.insertToDo(todo)
                }
    }

    fun show() {
        snackbar.show()
    }

    @EntryPoint
    @InstallIn(ViewComponent::class)
    interface DeleteToDoSnackbarInterface {
        fun getToDoRepository(): ToDoRepository
    }
}