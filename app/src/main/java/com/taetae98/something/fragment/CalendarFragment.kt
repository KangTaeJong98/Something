package com.taetae98.something.fragment

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.taetae98.something.R
import com.taetae98.something.base.BaseFragment
import com.taetae98.something.databinding.FragmentCalendarBinding
import com.taetae98.something.repository.SettingRepository
import com.taetae98.something.repository.ToDoRepository
import com.taetae98.something.utility.DataBinding
import com.taetae98.something.utility.Time
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class CalendarFragment : BaseFragment(), DataBinding<FragmentCalendarBinding> {
    override val binding: FragmentCalendarBinding by lazy { DataBindingUtil.inflate(layoutInflater, R.layout.fragment_calendar, null, false) }

    @Inject
    lateinit var toDoRepository: ToDoRepository
    @Inject
    lateinit var settingRepository: SettingRepository

    init {
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_calendar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.today -> {
                binding.todoCalendar.setTime(Time(System.currentTimeMillis()))
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCreateCalendarView()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        onCreateViewDataBinding()
        onCreateSupportActionBar()
        return binding.root
    }

    override fun onCreateViewDataBinding() {
        binding.lifecycleOwner = this
    }

    private fun onCreateSupportActionBar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun onCreateCalendarView() {
        toDoRepository.selectToDoLiveData().observe(viewLifecycleOwner) {
            CoroutineScope(Dispatchers.IO).launch {
                val value = settingRepository.getCalendarShowFinishedToDo().first()
                withContext(Dispatchers.Main) {
                    if (value) {
                        binding.todoCalendar.todoList = it.filter { todo ->
                            todo.hasTerm
                        }
                    } else {
                        binding.todoCalendar.todoList = it.filter { todo ->
                            todo.hasTerm && !todo.isFinished
                        }
                    }
                }
            }
        }

        binding.todoCalendar.onDateClickListener = {
            val showFinishedToDo = runBlocking { settingRepository.getCalendarShowFinishedToDo().first() }
            findNavController().navigate(CalendarFragmentDirections.actionCalendarFragmentToToDoDateDialog(it, showFinishedToDo))
        }
    }
}