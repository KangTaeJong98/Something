package com.taetae98.something.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.taetae98.something.ActivityMainNavigationXmlDirections
import com.taetae98.something.R
import com.taetae98.something.adapter.ToDoAdapter
import com.taetae98.something.base.BaseFragment
import com.taetae98.something.databinding.FragmentTodoBinding
import com.taetae98.something.dto.Drawer
import com.taetae98.something.dto.ToDo
import com.taetae98.something.repository.DrawerRepository
import com.taetae98.something.repository.SettingRepository
import com.taetae98.something.utility.DataBinding
import com.taetae98.something.viewmodel.ToDoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import kotlin.properties.Delegates

@AndroidEntryPoint
class ToDoFragment : BaseFragment(), DataBinding<FragmentTodoBinding> {
    override val binding: FragmentTodoBinding by lazy { DataBindingUtil.inflate(layoutInflater, R.layout.fragment_todo, null, false) }

    private val todoViewModel by activityViewModels<ToDoViewModel>()
    private val args by navArgs<ToDoFragmentArgs>()

    @Inject
    lateinit var drawerRepository: DrawerRepository

    @Inject
    lateinit var settingRepository: SettingRepository

    @Inject
    lateinit var todoAdapter: ToDoAdapter

    private val showFinishedToDo by lazy { runBlocking { settingRepository.getToDoShowFinishedToDo().first() } }

    private var drawerId by Delegates.observable<Long?>(null) { _, _, _ ->
        notifyToDoListChanged()
    }

    private var todoList by Delegates.observable<List<ToDo>>(emptyList()) { _, _, _ ->
        notifyToDoListChanged()
    }

    init {
        setHasOptionsMenu(true)
    }

    private fun notifyToDoListChanged() {
        if (drawerId == null) {
            todoAdapter.submitList(todoList)
        } else {
            todoAdapter.submitList(todoList.filter { it.drawerId == drawerId })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCreateToDoList()
        onCreateDrawerID()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        onCreateViewDataBinding()
        onCreateSupportActionbar()
        onCreateRecyclerView()
        onCreateOnEdit()
        return binding.root
    }

    override fun onCreateViewDataBinding() {
        binding.lifecycleOwner = this
    }

    private fun onCreateToDoList() {
        todoViewModel.todoLiveData.observe(viewLifecycleOwner) {
            todoList = if (showFinishedToDo) {
                it
            } else {
                it.filter { todo ->
                    !todo.isFinished
                }
            }
        }
    }

    private fun onCreateDrawerID() {
        if (args.drawerId > 0L) {
            drawerId = args.drawerId
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                val drawer = settingRepository.getToDoDefaultDrawer().first()
                withContext(Dispatchers.Main) {
                    drawerId = drawer
                }
            }
        }
    }

    private fun onCreateSupportActionbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun onCreateRecyclerView() {
        with(binding.todoRecyclerView) {
            adapter = todoAdapter
        }
    }

    private fun onCreateOnEdit() {
        binding.setOnEdit {
            findNavController().navigate(ActivityMainNavigationXmlDirections.actionGlobalToDoEditFragment(ToDo(drawerId = drawerId)))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_todo_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.drawer -> {
                AlertDialog.Builder(requireContext()).apply {
                    setTitle(R.string.drawer)
                    val items = runBlocking(Dispatchers.IO) {
                        drawerRepository.select().toMutableList().apply {
                            add(0, Drawer(0L, getString(R.string.all)))
                        }
                    }

                    setItems(items.map { it.name }.toTypedArray()) { _, position ->
                        drawerId = if (position == 0) { null } else { items[position].id }
                    }
                }.show()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}