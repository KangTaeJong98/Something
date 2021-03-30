package com.taetae98.something.fragment

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.taetae98.something.R
import com.taetae98.something.adapter.BindingAdapter
import com.taetae98.something.base.BaseFragment
import com.taetae98.something.databinding.FragmentTodoEditBinding
import com.taetae98.something.repository.DrawerRepository
import com.taetae98.something.repository.ToDoRepository
import com.taetae98.something.utility.Time
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class ToDoEditFragment : BaseFragment<FragmentTodoEditBinding>(R.layout.fragment_todo_edit) {
    private val args by navArgs<ToDoEditFragmentArgs>()
    private val todo by lazy { args.todo }

    @Inject
    lateinit var todoRepository: ToDoRepository

    @Inject
    lateinit var drawerRepository: DrawerRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        onCreateSupportActionBar()
        onCreateToDo()
        onCreateStickySwitch()
        onCreateTermSwitch()
        onCreateOnSetBegin()
        onCreateOnSetEnd()
        onCreateDrawerSwitch()
        onCreateDrawer()
        onCreateOnFinish()
        onCreateTextInputLayout()
        return view
    }

    private fun onCreateSupportActionBar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun onCreateToDo() {
        with(todo) {
            if (!hasTerm) {
                beginTime = Time()
                endTime = Time()
            }

            binding.todo = this
        }
    }

    private fun onCreateStickySwitch() {
        binding.stickySwitch.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Settings.canDrawOverlays(requireContext())) {
                AlertDialog.Builder(requireContext()).apply {
                    setTitle(R.string.permission_request)
                    setMessage(context.getString(R.string.require_permission))
                    setPositiveButton(context.getString(R.string.confirm)) { _, _ ->
                        startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${requireContext().packageName}")))
                    }

                    setNegativeButton(context.getString(R.string.cancel)) { _, _ ->

                    }
                }.show()

                binding.stickySwitch.isChecked = false
            }
        }
    }

    private fun onCreateTermSwitch() {
        fun refresh() {
            if (binding.termSwitch.isChecked) {
                binding.termBeginLayout.alpha = 1.0F
                binding.termEndLayout.alpha = 1.0F
            } else {
                binding.termBeginLayout.alpha = 0.3F
                binding.termEndLayout.alpha = 0.3F
            }
        }

        refresh()
        binding.termSwitch.setOnCheckedChangeListener { _, _ ->
            refresh()
        }
    }

    private fun onCreateOnSetBegin() {
        binding.setOnSetBegin {
            if (binding.termSwitch.isChecked) {
                with(todo) {
                    DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                        beginTime.year = year
                        beginTime.month = month
                        beginTime.dayOfMonth = dayOfMonth

                        BindingAdapter.setDate(binding.termBegin, beginTime)
                        if (beginTime > endTime) {
                            endTime = Time(beginTime.timeInMillis)
                            BindingAdapter.setDate(binding.termEnd, endTime)
                        }
                    }, beginTime.year, beginTime.month, beginTime.dayOfMonth).show()
                }
            }
        }
    }

    private fun onCreateOnSetEnd() {
        binding.setOnSetEnd {
            if (binding.termSwitch.isChecked) {
                with(todo) {
                    DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                        endTime.year = year
                        endTime.month = month
                        endTime.dayOfMonth = dayOfMonth

                        BindingAdapter.setDate(binding.termEnd, endTime)
                        if (endTime < beginTime) {
                            args.todo.beginTime = Time(endTime.timeInMillis)
                            BindingAdapter.setDate(binding.termBegin, args.todo.beginTime)
                        }
                    }, endTime.year, endTime.month, endTime.dayOfMonth).show()
                }
            }
        }
    }

    private fun onCreateDrawerSwitch() {
        fun refresh() {
            if (binding.drawerSwitch.isChecked) {
                binding.drawerTextInputLayout.alpha = 1.0F
            } else {
                binding.drawerTextInputLayout.alpha = 0.3F
            }
        }

        refresh()
        binding.drawerSwitch.setOnCheckedChangeListener { _, _ ->
            refresh()
        }
    }

    private fun onCreateDrawer() {
        CoroutineScope(Dispatchers.IO).launch {
            with(binding.drawerTextInputLayout.editText as AutoCompleteTextView) {
                val array = drawerRepository.select()

                withContext(Dispatchers.Main) {
                    setText(array.find { it.id == todo.drawerId }?.name, false)
                    setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, array.map { it.name }))
                    onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                        todo.drawerId = array[position].id
                    }
                }
            }
        }
    }

    private fun onCreateOnFinish() {
        binding.setOnFinish {
            if (binding.titleTextInputLayout.editText!!.text.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.fill_the_title), Toast.LENGTH_SHORT).show()
                return@setOnFinish
            }

            with(todo) {
                isOnTop = binding.onTopSwitch.isChecked
                isSticky = binding.stickySwitch.isChecked
                hasTerm = binding.termSwitch.isChecked
                title = binding.titleTextInputLayout.editText!!.text.toString()
                description = binding.descriptionTextInputLayout.editText!!.text.toString()
                if (!binding.drawerSwitch.isChecked) {
                    drawerId = null
                }
            }

            todoRepository.insertToDo(todo)
            findNavController().navigateUp()
        }
    }

    private fun onCreateTextInputLayout() {
        binding.titleTextInputLayout.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                        .toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
            }
        }

        binding.descriptionTextInputLayout.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                        .toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
            }
        }
    }
}