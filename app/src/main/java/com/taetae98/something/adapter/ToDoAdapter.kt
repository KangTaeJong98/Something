package com.taetae98.something.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import com.taetae98.something.ActivityMainNavigationXmlDirections
import com.taetae98.something.base.BaseAdapter
import com.taetae98.something.base.BaseHolder
import com.taetae98.something.databinding.HolderTodoBinding
import com.taetae98.something.dto.ToDo

class ToDoAdapter : BaseAdapter<ToDo>(ToDoItemCallback()) {
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
                it.findNavController().navigate(ActivityMainNavigationXmlDirections.actionGlobalToDoEditFragment(element))
            }
        }

        override fun bind(element: ToDo) {
            super.bind(element)
            binding.todo = element
        }
    }

    class ToDoItemCallback() : DiffUtil.ItemCallback<ToDo>() {
        override fun areItemsTheSame(oldItem: ToDo, newItem: ToDo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ToDo, newItem: ToDo): Boolean {
            return oldItem == newItem
        }
    }
}