package com.taetae98.something.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.taetae98.something.R
import com.taetae98.something.adapter.ToDoAdapter
import com.taetae98.something.base.BaseFragment
import com.taetae98.something.databinding.FragmentFinishedBinding
import com.taetae98.something.viewmodel.ToDoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FinishedFragment : BaseFragment<FragmentFinishedBinding>(R.layout.fragment_finished) {
    private val todoViewModel by activityViewModels<ToDoViewModel>()
    private val todoAdapter by lazy { ToDoAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        todoViewModel.todoLiveData.observe(viewLifecycleOwner) {
            todoAdapter.submitList(it.filter { todo -> todo.isFinished })
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        onCreateSupportActionbar()
        onCreateRecyclerView()
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
}