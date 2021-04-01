 package com.taetae98.something.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.taetae98.something.R
import com.taetae98.something.adapter.ToDoAdapter
import com.taetae98.something.base.BaseDialog
import com.taetae98.something.databinding.DialogTodoDateBinding
import com.taetae98.something.dto.ToDo
import com.taetae98.something.repository.ToDoRepository
import com.taetae98.something.utility.DataBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ToDoDateDialog : BaseDialog(), DataBinding<DialogTodoDateBinding> {
    override val binding: DialogTodoDateBinding by lazy { DataBindingUtil.inflate(layoutInflater, R.layout.dialog_todo_date, null, false) }

    private val args by navArgs<ToDoDateDialogArgs>()
    private val time by lazy { args.time }
    private val showFinishedToDo by lazy { args.showFinishedToDo }

    @Inject
    lateinit var todoAdapter: ToDoAdapter

    @Inject
    lateinit var todoRepository: ToDoRepository

    override fun onResume() {
        super.onResume()
        setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.attributes?.windowAnimations = R.style.Theme_Something_ToDo_Date_Dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCreateToDoList()
    }

    private fun onCreateToDoList() {
        todoRepository.selectToDoLiveData().observe(this) {
            todoAdapter.submitList(
                it.filter { todo ->
                    if (showFinishedToDo) {
                        todo.beginTime <= time && time <= todo.endTime && todo.hasTerm
                    } else {
                        todo.beginTime <= time && time <= todo.endTime && todo.hasTerm && !todo.isFinished
                    }
                }
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        onCreateViewDataBinding()
        onCreateTime()
        onCreateToDoRecyclerView()
        onCreateOnAdd()
        return binding.root
    }

    override fun onCreateViewDataBinding() {
        binding.lifecycleOwner = this
    }

    private fun onCreateTime() {
        binding.time = time
    }

    private fun onCreateToDoRecyclerView() {
        with(todoAdapter) {
            onClickCallback = { _, todo ->
                findNavController().navigate(ToDoDateDialogDirections.actionToDoDateDialogToTodoEditFragment(todo))
            }
            onEditCallback = { _, todo ->
                findNavController().navigate(ToDoDateDialogDirections.actionToDoDateDialogToTodoEditFragment(todo))
            }
        }
        with(binding.todoRecyclerView) {
            adapter = todoAdapter
        }
    }

    private fun onCreateOnAdd() {
        binding.setOnAdd {
            findNavController().navigate(ToDoDateDialogDirections.actionToDoDateDialogToTodoEditFragment(ToDo(hasTerm = true, beginTime = time, endTime = time)))
        }
    }
}