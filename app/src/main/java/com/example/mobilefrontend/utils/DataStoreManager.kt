package com.example.mobilefrontend.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object DataStoreManager {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

    private val AUTH_TOKEN = stringPreferencesKey("token")

    suspend fun saveValue(context: Context, value: String) {
        context.dataStore.edit { preferences ->
            preferences[AUTH_TOKEN] = value
        }
    }

    fun getValue(context: Context): Flow<String> {
        return context.dataStore.data
            .map { preferences -> preferences[AUTH_TOKEN] ?: "" }
    }
}