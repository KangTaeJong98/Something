package com.taetae98.something.repository

import androidx.datastore.core.DataStore
import com.taetae98.something.Account
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository @Inject constructor(
    private val accountDataStore: DataStore<Account>
){
    fun getEmailFlow(): Flow<String> {
        return accountDataStore.data.map {
            it.email
        }
    }

    fun getEmail(): String {
        return runBlocking(Dispatchers.IO) {
            accountDataStore.data.map { it.email }.first()
        }
    }

    suspend fun setEmail(email: String) {
        accountDataStore.updateData {
            it.toBuilder()
                .setEmail(email)
                .build()
        }
    }

    suspend fun logout() {
        accountDataStore.updateData {
            it.toBuilder()
                .setEmail("")
                .build()
        }
    }
}