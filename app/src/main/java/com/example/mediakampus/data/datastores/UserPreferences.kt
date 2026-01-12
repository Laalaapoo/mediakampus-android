package com.example.mediakampus.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {
    companion object {
        private val AUTH_TOKEN = stringPreferencesKey("auth_token")
        private val USER_ROLE = stringPreferencesKey("user_role")
        private val USER_NAME = stringPreferencesKey("user_name")
    }

    val authToken: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[AUTH_TOKEN]
    }

    val userRole: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[USER_ROLE]
    }

    val userName: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[USER_NAME]
    }

    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[AUTH_TOKEN] = token
        }
    }

    suspend fun saveUserProfile(name: String, role: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_NAME] = name
            prefs[USER_ROLE] = role
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}