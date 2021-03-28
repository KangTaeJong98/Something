package com.taetae98.something.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.taetae98.something.ActivityMainNavigationXmlDirections
import com.taetae98.something.R
import com.taetae98.something.adapter.ToDoAdapter
import com.taetae98.something.base.BaseFragment
import com.taetae98.something.databinding.FragmentTodoBinding
import com.taetae98.something.dto.ToDo
import com.taetae98.something.viewmodel.ToDoViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ToDoFragment : BaseFragment<FragmentTodoBinding>(R.layout.fragment_todo) {
    private val todoViewModel by activityViewModels<ToDoViewModel>()

    @Inject
    lateinit var todoAdapter: ToDoAdapter

    init {
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        todoViewModel.todoLiveData.observe(viewLifecycleOwner) {
            todoAdapter.submitList(it)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        onCreateSupportActionbar()
        onCreateRecyclerView()
        onCreateOnEdit()
        return view
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
            findNavController().navigate(ActivityMainNavigationXmlDirections.actionGlobalToDoEditFragment(ToDo()))
        }
    }
}