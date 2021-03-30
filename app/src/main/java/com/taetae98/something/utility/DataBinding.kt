package com.taetae98.something.utility

import androidx.databinding.ViewDataBinding

interface DataBinding<VB: ViewDataBinding> {
    val binding: VB

    fun onCreateViewDataBinding()
}