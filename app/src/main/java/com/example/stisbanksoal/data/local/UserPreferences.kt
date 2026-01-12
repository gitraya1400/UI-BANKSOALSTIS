package com.example.stisbanksoal.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Membuat instansi DataStore (semacam database kecil key-value)
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val USER_ROLE = stringPreferencesKey("user_role") // "ADMIN" atau "DOSEN"
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
    }

    // Mengambil Token (Flow artinya datanya mengalir/live update)
    val accessToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN]
    }

    val userRole: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_ROLE]
    }

    val userName: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME]
    }

    // Simpan Data Login
    suspend fun saveAuthToken(token: String, role: String, name: String, email: String) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = token
            preferences[USER_ROLE] = role
            preferences[USER_NAME] = name
            preferences[USER_EMAIL] = email
        }
    }

    // Logout (Hapus data)
    suspend fun clearAuthToken() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}