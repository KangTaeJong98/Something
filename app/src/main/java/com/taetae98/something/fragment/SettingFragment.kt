package com.taetae98.something.fragment

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import com.taetae98.something.R
import com.taetae98.something.base.BaseFragment
import com.taetae98.something.databinding.FragmentSettingBinding
import com.taetae98.something.dto.Drawer
import com.taetae98.something.dto.ToDo
import com.taetae98.something.repository.AccountRepository
import com.taetae98.something.repository.DrawerRepository
import com.taetae98.something.repository.SettingRepository
import com.taetae98.something.repository.ToDoRepository
import com.taetae98.something.service.StickyService
import com.taetae98.something.utility.DataBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : BaseFragment(), DataBinding<FragmentSettingBinding> {
    override val binding: FragmentSettingBinding by lazy { DataBinding.get(this, R.layout.fragment_setting) }

    @Inject
    lateinit var todoRepository: ToDoRepository

    @Inject
    lateinit var drawerRepository: DrawerRepository

    @Inject
    lateinit var settingRepository: SettingRepository

    @Inject
    lateinit var accountRepository: AccountRepository

    companion object {
        private const val BACKUP = 1000
        private const val RESTORE = 1001
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BACKUP || requestCode == RESTORE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
                runBlocking(Dispatchers.IO) {
                    accountRepository.setEmail(account.email!!)
                }
                when(requestCode) {
                    BACKUP -> {
                        onBackup()
                    }
                    RESTORE -> {
                        onRestore()
                    }
                }
            } catch (e: ApiException) {
                Toast.makeText(requireContext(), R.string.google_login_fail, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        onCreateViewDataBinding()
        onCreateSupportActionBar()
        onCreateSomethingDefaultTheme()
        onCreateToDoShowFinishedToDo()
        onCreateToDoDefaultDrawer()
        onCreateCalendarShowFinishedToDo()
        onCreateStickySwitch()
        onCreateOnBackup()
        onCreateOnRestore()
        return binding.root
    }

    private fun onCreateViewDataBinding() {
        binding.lifecycleOwner = this
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
            CoroutineScope(Dispatchers.IO).launch {
                val calendarShowFinishedToDo = settingRepository.getCalendarShowFinishedToDo().first()
                withContext(Dispatchers.Main) {
                    isChecked = calendarShowFinishedToDo
                }
            }

            setOnCheckedChangeListener { _, isChecked ->
                CoroutineScope(Dispatchers.IO).launch {
                    settingRepository.setCalendarShowFinishedToDo(isChecked)
                }
            }
        }
    }

    private fun onCreateStickySwitch() {
        with(binding.stickySwitch) {
            setOnClickListener {
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

            setOnCheckedChangeListener { _, isChecked ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Settings.canDrawOverlays(requireContext())) {
                    CoroutineScope(Dispatchers.IO).launch {
                        settingRepository.setIsSticky(false)
                    }
                    return@setOnCheckedChangeListener
                }

                if (isChecked) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        requireContext().startForegroundService(Intent(requireContext(), StickyService::class.java))
                    } else {
                        requireContext().startService(Intent(requireContext(), StickyService::class.java))
                    }
                } else {
                    requireContext().stopService(Intent(requireContext(), StickyService::class.java))
                }

                CoroutineScope(Dispatchers.IO).launch {
                    settingRepository.setIsSticky(isChecked)
                }
            }

            CoroutineScope(Dispatchers.IO).launch {
                val value = settingRepository.getIsSticky().first()
                withContext(Dispatchers.Main) {
                    isChecked = value
                }
            }
        }
    }

    private fun onCreateOnBackup() {
        binding.setOnBackup {
            if (accountRepository.getEmail().isNotBlank()) {
                onBackup()
            } else {
                onGoogleLogin(BACKUP)

            }
        }
    }

    private fun onCreateOnRestore() {
        binding.setOnRestore {
            if (accountRepository.getEmail().isNotBlank()) {
                onRestore()
            } else {
                onGoogleLogin(RESTORE)

            }
        }
    }

    private fun onBackup() {
        AlertDialog.Builder(requireContext()).apply {
            val array = resources.getStringArray(R.array.backup_type)
            val storePath = accountRepository.getEmail()
                .replace(".", "")
                .replace("#", "")
                .replace("$", "")
                .replace("[", "")
                .replace("]", "")

            setItems(array) { _, pos ->
                when(array[pos]) {
                    getString(R.string.replace) -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            FirebaseDatabase.getInstance().getReference(storePath).child("todo").setValue(todoRepository.select())
                            FirebaseDatabase.getInstance().getReference(storePath).child("drawer").setValue(drawerRepository.select())
                            withContext(Dispatchers.Main) {
                                Toast.makeText(requireContext(), R.string.finish, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    getString(R.string.add) -> {
                        FirebaseDatabase.getInstance().getReference(storePath).child("todo").addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    val localToDo = todoRepository.select()
                                    val maxToDoId = localToDo.maxOf { it.id }
                                    val firebaseToDo = snapshot.getValue(object : GenericTypeIndicator<List<ToDo>>(){})

                                    FirebaseDatabase.getInstance().getReference(storePath).child("drawer").addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            CoroutineScope(Dispatchers.IO).launch {
                                                val localDrawer = drawerRepository.select()
                                                val maxDrawerId = localDrawer.maxOf { it.id }
                                                val firebaseDrawer = snapshot.getValue(object : GenericTypeIndicator<List<Drawer>>(){})?.onEach {
                                                    it.id += maxDrawerId
                                                }
                                                firebaseToDo?.onEach {
                                                    it.id += maxToDoId
                                                    if (it.drawerId != null) {
                                                        it.drawerId = it.drawerId!! + maxDrawerId
                                                    }
                                                }

                                                val addToDo = ArrayList<ToDo>(localToDo.size + (firebaseToDo?.size ?: 0)).apply {
                                                    addAll(localToDo)
                                                    firebaseToDo?.let { addAll(it) }
                                                }

                                                val addDrawer = ArrayList<Drawer>(localDrawer.size + (firebaseDrawer?.size ?: 0)).apply {
                                                    addAll(localDrawer)
                                                    firebaseDrawer?.let { addAll(it) }
                                                }

                                                FirebaseDatabase.getInstance().getReference(storePath).child("todo").setValue(addToDo)
                                                FirebaseDatabase.getInstance().getReference(storePath).child("drawer").setValue(addDrawer)
                                                withContext(Dispatchers.Main) {
                                                    Toast.makeText(requireContext(), R.string.finish, Toast.LENGTH_LONG).show()
                                                }
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            Toast.makeText(requireContext(), R.string.database_connect_fail, Toast.LENGTH_LONG).show()
                                        }
                                    })
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(requireContext(), R.string.database_connect_fail, Toast.LENGTH_LONG).show()
                            }
                        })
                    }
                }
            }
        }.show()
    }

    private fun onRestore() {
        AlertDialog.Builder(requireContext()).apply {
            val array = resources.getStringArray(R.array.backup_type)
            val storePath = accountRepository.getEmail()
                .replace(".", "")
                .replace("#", "")
                .replace("$", "")
                .replace("[", "")
                .replace("]", "")

            setItems(array) { _, pos ->
                when(array[pos]) {
                    getString(R.string.replace) -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            todoRepository.delete()
                            drawerRepository.delete()

                            FirebaseDatabase.getInstance().getReference(storePath).child("drawer").addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        snapshot.getValue(object : GenericTypeIndicator<List<Drawer>>(){})?.let {
                                            it.forEach { drawer ->
                                                drawerRepository.insert(drawer)
                                            }
                                        }
                                        FirebaseDatabase.getInstance().getReference(storePath).child("todo").addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    snapshot.getValue(object : GenericTypeIndicator<List<ToDo>>(){})?.let {
                                                        it.forEach { todo ->
                                                            todoRepository.insert(todo)
                                                        }
                                                    }

                                                    withContext(Dispatchers.Main) {
                                                        Toast.makeText(requireContext(), R.string.finish, Toast.LENGTH_LONG).show()
                                                    }
                                                }
                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                                Toast.makeText(requireContext(), R.string.database_connect_fail, Toast.LENGTH_LONG).show()
                                            }
                                        })
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(requireContext(), R.string.database_connect_fail, Toast.LENGTH_LONG).show()
                                }
                            })
                        }
                    }
                    getString(R.string.add) -> {
                        FirebaseDatabase.getInstance().getReference(storePath).child("todo").addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val firebaseToDo = snapshot.getValue(object : GenericTypeIndicator<List<ToDo>>(){})
                                FirebaseDatabase.getInstance().getReference(storePath).child("drawer").addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            val localToDo = todoRepository.select()
                                            val maxToDoId = localToDo.maxOf { it.id }
                                            val localDrawer = drawerRepository.select()
                                            val maxDrawerId = localDrawer.maxOf { it.id }
                                            snapshot.getValue(object : GenericTypeIndicator<List<Drawer>>(){})?.onEach {
                                                it.id += maxDrawerId
                                                drawerRepository.insert(it)
                                            }
                                            firebaseToDo?.onEach {
                                                it.id += maxToDoId
                                                if (it.drawerId != null) {
                                                    it.drawerId = it.drawerId!! + maxDrawerId
                                                }
                                                todoRepository.insert(it)
                                            }

                                            withContext(Dispatchers.Main) {
                                                Toast.makeText(requireContext(), R.string.finish, Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Toast.makeText(requireContext(), R.string.database_connect_fail, Toast.LENGTH_LONG).show()
                                    }
                                })
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(requireContext(), R.string.database_connect_fail, Toast.LENGTH_LONG).show()
                            }
                        })
                    }
                }
            }
        }.show()
    }

    private fun onGoogleLogin(action: Int) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        val signInIntent = googleSignInClient.signInIntent

        startActivityForResult(signInIntent, action)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
    }
}