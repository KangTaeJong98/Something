package com.taetae98.something.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.taetae98.something.repository.ToDoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ToDoViewModel @Inject constructor(
        private val todoRepository: ToDoRepository,
        private val savedStateHandle: SavedStateHandle
): ViewModel() {
    val todoLiveData = todoRepository.selectToDoLiveData()
}