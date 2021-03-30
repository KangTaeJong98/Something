package com.taetae98.something.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.taetae98.something.ActivityMainNavigationXmlDirections
import com.taetae98.something.R
import com.taetae98.something.adapter.DrawerAdapter
import com.taetae98.something.base.BaseFragment
import com.taetae98.something.databinding.FragmentDrawerBinding
import com.taetae98.something.dto.Drawer
import com.taetae98.something.utility.DataBinding
import com.taetae98.something.viewmodel.DrawerViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DrawerFragment : BaseFragment(), DataBinding<FragmentDrawerBinding> {
    override val binding: FragmentDrawerBinding by lazy { DataBindingUtil.inflate(layoutInflater, R.layout.fragment_drawer, null, false) }

    private val drawerViewModel by activityViewModels<DrawerViewModel>()

    @Inject
    lateinit var drawerAdapter: DrawerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        drawerViewModel.drawerLiveData.observe(viewLifecycleOwner) {
            drawerAdapter.submitList(it)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        onCreateViewDataBinding()
        onCreateSupportActionbar()
        onCreateRecyclerView()
        onCreateOnEdit()
        return binding.root
    }

    override fun onCreateViewDataBinding() {
        binding.lifecycleOwner = this
    }

    private fun onCreateSupportActionbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun onCreateRecyclerView() {
        with(binding.drawerRecyclerView) {
            adapter = drawerAdapter
        }
    }

    private fun onCreateOnEdit() {
        binding.setOnEdit {
            findNavController().navigate(ActivityMainNavigationXmlDirections.actionGlobalDrawerEditFragment(Drawer()))
        }
    }
}