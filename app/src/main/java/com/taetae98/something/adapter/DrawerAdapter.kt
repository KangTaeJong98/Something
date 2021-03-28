package com.taetae98.something.adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.Menu
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import com.taetae98.something.ActivityMainNavigationXmlDirections
import com.taetae98.something.R
import com.taetae98.something.base.BaseAdapter
import com.taetae98.something.base.BaseHolder
import com.taetae98.something.database.DrawerRepository
import com.taetae98.something.database.ToDoRepository
import com.taetae98.something.databinding.HolderDrawerBinding
import com.taetae98.something.dto.Drawer
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@FragmentScoped
class DrawerAdapter @Inject constructor(
        private val todoRepository: ToDoRepository,
        private val drawerRepository: DrawerRepository
) : BaseAdapter<Drawer>(DrawerItemCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<out ViewDataBinding, Drawer> {
        return DrawerHolder(HolderDrawerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class DrawerHolder(binding: HolderDrawerBinding) : BaseHolder<HolderDrawerBinding, Drawer>(binding) {
        init {
            itemView.setOnClickListener {
                it.findNavController().navigate(ActivityMainNavigationXmlDirections.actionGlobalDrawerEditFragment(element))
            }

            itemView.setOnCreateContextMenuListener { menu, _, _ ->
                menu.add(Menu.NONE, Menu.NONE, Menu.NONE, context.getString(R.string.edit)).setOnMenuItemClickListener {
                    itemView.findNavController().navigate(ActivityMainNavigationXmlDirections.actionGlobalDrawerEditFragment(element))
                    true
                }

                menu.add(Menu.NONE, Menu.NONE, Menu.NONE, context.getString(R.string.delete)).setOnMenuItemClickListener {
                    AlertDialog.Builder(context).apply {
                        setTitle(R.string.delete)
                        setMessage(R.string.delete_with_todo)
                        setNegativeButton(R.string.no) { _, _ ->
                            drawerRepository.deleteDrawer(element)

                        }

                        setPositiveButton(R.string.yes) { _, _ ->
                            CoroutineScope(Dispatchers.IO).launch {
                                todoRepository.selectWithDrawer(element.id).forEach {
                                    todoRepository.deleteToDo(it)
                                }
                            }

                            drawerRepository.deleteDrawer(element)
                        }
                    }.show()
                    true
                }
            }
        }

        override fun bind(element: Drawer) {
            super.bind(element)
            binding.drawer = element
        }
    }

    class DrawerItemCallback : DiffUtil.ItemCallback<Drawer>() {
        override fun areItemsTheSame(oldItem: Drawer, newItem: Drawer): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Drawer, newItem: Drawer): Boolean {
            return oldItem == newItem
        }
    }
}