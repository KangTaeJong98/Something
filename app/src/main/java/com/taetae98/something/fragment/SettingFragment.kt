package com.taetae98.something.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import com.taetae98.something.R
import com.taetae98.something.base.BaseFragment
import com.taetae98.something.databinding.FragmentSettingBinding
import com.taetae98.something.dto.Drawer
import com.taetae98.something.repository.DrawerRepository
import com.taetae98.something.repository.SettingRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding>(R.layout.fragment_setting) {
    @Inject
    lateinit var drawerRepository: DrawerRepository

    @Inject
    lateinit var settingRepository: SettingRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        onCreateSupportActionBar()
        onCreateSomethingDefaultTheme()
        onCreateToDoShowFinishedToDo()
        onCreateToDoDefaultDrawer()
        onCreateCalendarShowFinishedToDo()
        return view
    }

    private fun onCreateSupportActionBar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun onCreateSomethingDefaultTheme() {
        with(binding.somethingDefaultThemeTextInputLayout.editText as AutoCompleteTextView) {
            CoroutineScope(Dispatchers.IO).launch {
                val defaultTheme = settingRepository.getDefaultTheme().first()
                withContext(Dispatchers.Main) {
                    setText(settingRepository.themeList[defaultTheme].first, false)
                }
            }
            setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, settingRepository.themeList.map { it.first }))
            onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    settingRepository.setDefaultTheme(position)
                }
                Toast.makeText(requireContext(), R.string.applies_when_the_app_restart, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onCreateToDoShowFinishedToDo() {
        with(binding.todoShowFinishedSwitch) {
            CoroutineScope(Dispatchers.IO).launch {
                val todoShowFinishedToDo = settingRepository.getToDoShowFinishedToDo().first()
                withContext(Dispatchers.Main) {
                    isChecked = todoShowFinishedToDo
                }
            }

            setOnCheckedChangeListener { _, isChecked ->
                CoroutineScope(Dispatchers.IO).launch {
                    settingRepository.setToDoShowFinishedToDo(isChecked)
                }
            }
        }
    }

    private fun onCreateToDoDefaultDrawer() {
        with(binding.todoDefaultDrawerTextInputLayout.editText as AutoCompleteTextView) {
            CoroutineScope(Dispatchers.IO).launch {
                val drawers = drawerRepository.select().toMutableList().apply {
                    add(0, Drawer(0L, getString(R.string.all)))
                }
                val defaultDrawerId = settingRepository.getToDoDefaultDrawer().first()
                val defaultDrawer = drawers.find { it.id == defaultDrawerId }

                withContext(Dispatchers.Main) {
                    if (defaultDrawer == null) {
                        setText(getString(R.string.all), false)
                    } else {
                        setText(defaultDrawer.name, false)
                    }

                    setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, drawers.map { it.name }))
                }


                onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                    CoroutineScope(Dispatchers.IO).launch {
                        if (position == 0) {
                            settingRepository.setToDoDefaultDrawer(null)
                        } else {
                            settingRepository.setToDoDefaultDrawer(drawers[position].id)
                        }
                    }
                }
            }
        }
    }

    private fun onCreateCalendarShowFinishedToDo() {
        with(binding.calendarShowFinishedSwitch) {

        }
    }
}