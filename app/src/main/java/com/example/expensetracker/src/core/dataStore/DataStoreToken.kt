package com.example.expensetracker.src.core.dataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_settings")

class DataStoreToken private constructor(private val context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: DataStoreToken? = null

        fun getInstance(context: Context): DataStoreToken {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DataStoreToken(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.TOKEN] = token
        }
    }

    suspend fun getToken(): String? {
        return context.dataStore.data.first()[PreferenceKeys.TOKEN]
    }

    fun getTokenFlow(): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[PreferenceKeys.TOKEN]
        }
    }

    suspend fun clearToken() {
        context.dataStore.edit { prefs ->
            prefs.remove(PreferenceKeys.TOKEN)
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}