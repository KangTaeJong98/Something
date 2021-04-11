package com.taetae98.something.fragment

import android.os.Bundle
import android.view.*
import androidx.navigation.fragment.findNavController
import com.taetae98.something.R
import com.taetae98.something.base.BaseFragment
import com.taetae98.something.databinding.FragmentCalendarBinding
import com.taetae98.something.repository.SettingRepository
import com.taetae98.something.repository.ToDoRepository
import com.taetae98.something.utility.DataBinding
import com.taetae98.something.utility.Time
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class CalendarFragment : BaseFragment(), DataBinding<FragmentCalendarBinding> {
    override val binding: FragmentCalendarBinding by lazy { DataBinding.get(this, R.layout.fragment_calendar) }

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

    private fun onCreateViewDataBinding() {
        binding.lifecycleOwner = this
    }

    private fun onCreateSupportActionBar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun onCreateCalendarView() {
        val showFinishedToDo = runBlocking(Dispatchers.IO) { settingRepository.getCalendarShowFinishedToDo().first() }
        toDoRepository.selectToDoLiveData().observe(viewLifecycleOwner) {
            if (showFinishedToDo) {
                binding.todoCalendar.todoList = it.filter { todo ->
                    todo.hasTerm
                }
            } else {
                binding.todoCalendar.todoList = it.filter { todo ->
                    todo.hasTerm && !todo.isFinished
                }
            }
        }

        binding.todoCalendar.onDateClickListener = {
            findNavController().navigate(CalendarFragmentDirections.actionCalendarFragmentToToDoDateDialog(it, showFinishedToDo))
        }
    }
}