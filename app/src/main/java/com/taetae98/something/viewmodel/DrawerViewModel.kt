package com.taetae98.something.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.taetae98.something.database.DrawerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DrawerViewModel @Inject constructor(
    private val drawerRepository: DrawerRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel()