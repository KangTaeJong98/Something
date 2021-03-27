package com.taetae98.something.view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.taetae98.something.adapter.ToDoAdapter
import com.taetae98.something.database.ToDoRepository
import com.taetae98.something.toast.DeleteToDoSnackbar
import com.taetae98.something.toast.SetFinishToDoSnackbar
import com.taetae98.something.utility.GridSpacingItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ToDoRecyclerView : RecyclerView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @Inject
    lateinit var todoRepository: ToDoRepository

    init {
        addItemDecoration(GridSpacingItemDecoration(1, 10))

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: ViewHolder, target: ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
                if (viewHolder is ToDoAdapter.ToDoHolder) {
                    val todo = viewHolder.element
                    if (!todo.isFinished) {
                        todoRepository.updateToDo(todo.copy(isFinished = true))
                        SetFinishToDoSnackbar(this@ToDoRecyclerView, todo).show()
                    } else {
                        todoRepository.deleteToDo(todo)
                        DeleteToDoSnackbar(this@ToDoRecyclerView, todo).show()
                    }
                }
            }
        }).attachToRecyclerView(this)
    }
}