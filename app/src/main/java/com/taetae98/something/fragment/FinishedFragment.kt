package com.taetae98.something.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.taetae98.something.R
import com.taetae98.something.adapter.ToDoAdapter
import com.taetae98.something.base.BaseFragment
import com.taetae98.something.databinding.FragmentFinishedBinding
import com.taetae98.something.dto.ToDo
import com.taetae98.something.utility.DataBinding
import com.taetae98.something.viewmodel.ToDoViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FinishedFragment : BaseFragment(), DataBinding<FragmentFinishedBinding> {
    override val binding: FragmentFinishedBinding by lazy { DataBindingUtil.inflate(layoutInflater, R.layout.fragment_finished, null, false) }

    private val todoViewModel by activityViewModels<ToDoViewModel>()

    @Inject
    lateinit var todoAdapter: ToDoAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        todoViewModel.todoLiveData.observe(viewLifecycleOwner) {
            todoAdapter.submitList(it.filter { todo -> todo.isFinished })
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        onCreateViewDataBinding()
        onCreateSupportActionbar()
        onCreateRecyclerView()
        return binding.root
    }

    override fun onCreateViewDataBinding() {
        binding.lifecycleOwner = this
    }

    private fun onCreateSupportActionbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun onCreateRecyclerView() {
        with(todoAdapter) {
            val callback: (View, ToDo) -> Unit = { _, todo ->
                findNavController().navigate(FinishedFragmentDirections.actionFinishedFragmentToTodoEditFragment(todo))
            }

            onClickCallback = callback
            onEditCallback = callback
        }

        with(binding.todoRecyclerView) {
            adapter = todoAdapter
        }
    }
}