package com.taetae98.something.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.taetae98.something.R
import com.taetae98.something.base.BaseFragment
import com.taetae98.something.databinding.FragmentDrawerEditBinding
import com.taetae98.something.repository.DrawerRepository
import com.taetae98.something.utility.DataBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DrawerEditFragment : BaseFragment(), DataBinding<FragmentDrawerEditBinding> {
    override val binding: FragmentDrawerEditBinding by lazy { DataBindingUtil.inflate(layoutInflater, R.layout.fragment_drawer_edit, null, false) }

    private val args by navArgs<DrawerEditFragmentArgs>()
    private val drawer by lazy { args.drawer }

    @Inject
    lateinit var drawerRepository: DrawerRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        onCreateViewDataBinding()
        onCreateSupportActionBar()
        onCreateDrawer()
        onCreateOnFinish()
        onCreateTextInputLayout()
        return binding.root
    }

    override fun onCreateViewDataBinding() {
        binding.lifecycleOwner = this
    }

    private fun onCreateSupportActionBar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun onCreateDrawer() {
        binding.drawer = drawer
    }

    private fun onCreateOnFinish() {
        binding.setOnFinish {
            if (binding.nameInputLayout.editText!!.text.isEmpty()) {
                Toast.makeText(requireContext(), R.string.fill_the_name, Toast.LENGTH_SHORT).show()
                return@setOnFinish
            }

            drawer.name = binding.nameInputLayout.editText!!.text.toString()
            drawerRepository.insertDrawer(drawer)

            findNavController().navigateUp()
        }
    }

    private fun onCreateTextInputLayout() {
        binding.nameInputLayout.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                        .toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
            }
        }
    }
}