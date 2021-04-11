package com.taetae98.something.adapter

import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.taetae98.something.R
import com.taetae98.something.base.BaseAdapter
import com.taetae98.something.base.BaseHolder
import com.taetae98.something.databinding.HolderTodoBinding
import com.taetae98.something.dto.ToDo
import com.taetae98.something.repository.ToDoRepository
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class ToDoAdapter @Inject constructor(
    private val todoRepository: ToDoRepository,
) : BaseAdapter<ToDo>(ToDoItemCallback()) {

    var onClickCallback: ((View, ToDo) -> Unit)? = null
    var onEditCallback: ((View, ToDo) -> Unit)? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<out ViewDataBinding, ToDo> {
        return ToDoHolder(HolderTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).id
    }

    inner class ToDoHolder(binding: HolderTodoBinding) : BaseHolder<HolderTodoBinding, ToDo>(binding) {
        init {
            itemView.setOnClickListener {
                onClickCallback?.invoke(it, element)
            }

            itemView.setOnCreateContextMenuListener { menu, _, _ ->
                menu.add(Menu.NONE, Menu.NONE, Menu.NONE, context.getString(R.string.edit)).setOnMenuItemClickListener {
                    onEditCallback?.invoke(itemView, element)
                    true
                }

                if (element.isFinished) {
                    menu.add(Menu.NONE, Menu.NONE, Menu.NONE, context.getString(R.string.set_not_finished)).setOnMenuItemClickListener {
                        todoRepository.update(element.copy(isFinished = false))
                        true
                    }
                } else {
                    menu.add(Menu.NONE, Menu.NONE, Menu.NONE, context.getString(R.string.set_finished)).setOnMenuItemClickListener {
                        todoRepository.update(element.copy(isFinished = true))
                        true
                    }
                }
                menu.add(Menu.NONE, Menu.NONE, Menu.NONE, context.getString(R.string.delete)).setOnMenuItemClickListener {
                    todoRepository.delete(element)
                    true
                }
            }
        }

        override fun bind(element: ToDo) {
            super.bind(element)
            binding.todo = element
        }
    }

    class ToDoItemCallback : DiffUtil.ItemCallback<ToDo>() {
        override fun areItemsTheSame(oldItem: ToDo, newItem: ToDo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ToDo, newItem: ToDo): Boolean {
            return oldItem == newItem
        }
    }
}