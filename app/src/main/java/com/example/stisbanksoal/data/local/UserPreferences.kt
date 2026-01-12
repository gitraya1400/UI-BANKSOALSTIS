package com.example.stisbanksoal.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    private val ACCESS_TOKEN = stringPreferencesKey("access_token")
    private val USER_ROLE = stringPreferencesKey("user_role")
    private val USER_NAME = stringPreferencesKey("user_name")
    private val USER_ID = longPreferencesKey("user_id")
    private val USER_EMAIL = stringPreferencesKey("user_email")

    private val USER_NIP = stringPreferencesKey("user_nip")
    // Get Data
    val accessToken: Flow<String?>
        get() = context.dataStore.data.map { preferences -> preferences[ACCESS_TOKEN] }

    val userRole: Flow<String?>
        get() = context.dataStore.data.map { preferences -> preferences[USER_ROLE] }

    val userName: Flow<String?>
        get() = context.dataStore.data.map { preferences -> preferences[USER_NAME] }

    val userNip: Flow<String?>
        get() = context.dataStore.data.map { preferences -> preferences[USER_NIP] }
    // Save Login Sederhana
    suspend fun saveToken(token: String, role: String, name: String, email: String) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = token
            preferences[USER_ROLE] = role
            preferences[USER_NAME] = name
            preferences[USER_EMAIL] = email
        }
    }

    // Save Data Lengkap (INI YANG DIBUTUHKAN PROFILE VIEW MODEL)
    suspend fun saveUser(id: Long, name: String, email: String, nip: String, role: String, token: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = id
            preferences[USER_NAME] = name
            preferences[USER_EMAIL] = email // Penting!
            preferences[USER_NIP] = nip     // Penting!
            preferences[USER_ROLE] = role
            preferences[ACCESS_TOKEN] = token
        }
    }

    // Logout
    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}